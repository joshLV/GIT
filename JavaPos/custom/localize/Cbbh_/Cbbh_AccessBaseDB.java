package custom.localize.Cbbh;

import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.PublicMethod;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Bcrm.Bcrm_AccessBaseDB;

public class Cbbh_AccessBaseDB extends Bcrm_AccessBaseDB
{
	public boolean findWCCRules(String billno,Vector rules)
	{
		String sql = "select seqno,billno,isyq,ruletype,gdid,catid,ppid,rulevalue,sdate,edate from pop_wechatticketrule where billno = " + billno + " and trunc(edate) >= trunc(sysdate) and trunc(sdate) <= trunc(sysdate) order by billno,ruletype,seqno";
		
		ResultSet rs = null;
        
        try
        {
        	PublicMethod.timeStart(Language.apply("正在查询本地微信收券规则库,请等待......"));
        	
            rs = GlobalInfo.baseDB.selectData(sql);
        	
            if (rs == null)
            {
            	return false;
            }

            if (rs.next())
            {
            	Cbbh_WCCRuleDef rule = new Cbbh_WCCRuleDef();

                if (!GlobalInfo.localDB.getResultSetToObject(rule))
                {
                    return false;
                }
                
                if(rules == null) rules = new Vector();
                rules.add(rule);
            }
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return false;
        }
        finally
        {
            GlobalInfo.baseDB.resultSetClose();
            GlobalInfo.localDB.resultSetClose();
            //
            PublicMethod.timeEnd(Language.apply("查询本地微信收券规则库耗时: ")); 
        }
	}

	public boolean getCustomer(CustomerDef cust, String track)
	{
		try
		{
			/**
			 * 脱网时刷会员卡：（不截取二轨前的字母）
				//track2中如果包含’=’，截取‘=’前的20位，不足20位取实际位数.
				  去掉前面的所有字母
				//track2中如果不包含‘=’，取track2的前20位，不足20位取实际位数.
				  track2中如果不包含‘=’，且是680开头的（新世纪，没有＝符号）	，直接取前10位  for donghm 2014.5.19 add

			 */
			String track2="";
			if(track!=null && track.indexOf("=")>=0)
			{
				track2=strFormat(track);
				track2 = track2.substring(0, track2.indexOf("="));
			}
			else
			{
				track2 = track;
				if(track2!=null && track2.startsWith("680"))
				{
					track2 = Convert.increaseChar(track2, 500).substring(0,10).trim();
				}
			}
			if(track2.length()>20)
			{
				track2 = track2.substring(0,20);
			}
			
			/*袁俊
			脱机卡号的处理请参考
			        //世纪联名储蓄卡取19位 世纪联名信用卡取16位 新世纪会员卡 取10位 其它不变
			        if (h.vhykh.substr(0, 8) == '62270037') {   //世纪联名储蓄卡
			            h.vhykh = h.vhykh.substr(0, 18).Trim();
			        }
			        else if (h.vhykh.substr(0, 8) == '62216600') {   //世纪联名信用卡
			            h.vhykh = h.vhykh.substr(0, 16).Trim();
			        }
			        else if (h.vhykh.substr(0, 4) == '6222' ) {   //重百联名卡
			            h.vhykh = h.vhykh.substr(0, 16).Trim();
			        }
			        else if (h.vhykh.length == 16 && h.vhykh.substr(0, 3) == '680') { //重百会员卡	 有=号
			            h.vhykh = h.vhykh.substr(0, 10).Trim();
			        }
			        else if (h.vhykh.length == 16 && h.vhykh.substr(0, 4) >= '7000' && h.vhykh.substr(0, 4) <= '9999') { //世纪礼品卡 无=号
			            h.vhykh = h.vhykh.substr(0, 10).Trim();
			        }
			        else if (h.vhykh.length > 16 ) {
			            h.vhykh = h.vhykh.substr(0, 16).Trim();
			        }
			        else {
			            h.vhykh = h.vhykh.Trim();
			        }

			*/
			/*String trackTmp = Convert.increaseChar(track, 500);
			if (trackTmp.substring(0, 8).equalsIgnoreCase("62270037")) {   //世纪联名储蓄卡
				track2 = trackTmp.substring(0, 18).trim();
	        }
	        else if (trackTmp.substring(0, 8).equalsIgnoreCase("62216600")) {   //世纪联名信用卡
	        	track2 = trackTmp.substring(0, 16).trim();
	        }
	        else if (trackTmp.substring(0, 4).equalsIgnoreCase("6222")) {   //重百联名卡
	        	track2 = trackTmp.substring(0, 16).trim();
	        }
	        else if (trackTmp.trim().length() == 16 && trackTmp.substring(0, 3).equalsIgnoreCase("680")) { //重百会员卡	 有=号
	        	track2 = trackTmp.substring(0, 10).trim();
	        }
	        else if (trackTmp.trim().length() == 16 && Convert.toInt(trackTmp.substring(0, 4)) >= 7000 && Convert.toInt(trackTmp.substring(0, 4)) <= 9999) { //世纪礼品卡 无=号
	        	track2 = trackTmp.substring(0, 10).trim();
	        }
	        else if (trackTmp.trim().length() > 16 ) {
	        	track2 = trackTmp.substring(0, 16).trim();
	        }
	        else {
	        	track2 = trackTmp.trim();
	        }*/
			
			cust.code 	= track2;
			cust.type   = "XX";
			cust.status = "Y";
			cust.track  = track2;
			cust.name	= "脱机会员";
			cust.ishy   = 'Y';
			cust.iszk   = 'N';
			cust.isjf   = 'Y';
			cust.func	= "N";
			cust.zkl    = 1;		
			cust.value1 = 0;
			cust.value2 = 0;
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	//去除最前面的字母
	private String strFormat(String str)
	{
		if(str==null || str.length()<=0) return "";
		char chr;
		int letterCount=0;
		for(int i=0; i<str.length(); i++)
		{
			chr=str.charAt(i);
			if(Convert.isLetter(chr))
			{			
				letterCount++;
			}
			else
			{
				if(i==0 || i==letterCount)
				{
					return  str.substring(i,str.length());
				}
			}
			
		}
		return str;
	}
}
