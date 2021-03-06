package custom.localize.Nnmk;

import java.util.Vector;

import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.CmdHead;
import com.efuture.javaPos.Communication.Http;
import com.efuture.javaPos.Communication.Transition;
import com.efuture.javaPos.Communication.XmlParse;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Struct.CustomerDef;

import custom.localize.Cmls.Cmls_NetService;

public class Nnmk_NetService extends Cmls_NetService
{
	public boolean checkCustomer(Http h, CustomerDef cust, String track,String type)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, track, GlobalInfo.sysPara.jygs ,type};
		String[] args = { "mktcode", "track", "jygs","type"};

		try
		{
			head = new CmdHead(CmdDef.FINDCUSTOMER_SMZD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(h, line, "找不到该顾客卡信息!");

			if (result == 0)
			{

				Vector v = new XmlParse(line.toString()).parseMeth(0, CustomerDef.ref);

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);

					if (Transition.ConvertToObject(cust, row)) { return true; }
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}
	
	public boolean changeCard(CustomerDef oldc,CustomerDef newc,String[] infos)
	{
		if (!GlobalInfo.isOnline) { return false; }

		CmdHead head = null;
		StringBuffer line = new StringBuffer();
		int result = -1;
		String[] values = { GlobalInfo.sysPara.mktcode, GlobalInfo.sysPara.jygs,ConfigClass.CashRegisterCode,GlobalInfo.posLogin.gh,oldc.code,newc.code ,""};
		String[] args = { "mktcode","jygs","syjh","syyh","oldcard","newcard","type"};

		try
		{
			head = new CmdHead(CmdDef.CHANGECUSTOMER_SMZD);
			line.append(head.headToString() + Transition.SimpleXML(values, args));

			result = HttpCall(getMemCardHttp(CmdDef.FINDCUSTOMER), line, "找不到该顾客卡信息!");

			if (result == 0)
			{

				Vector v = new XmlParse(line.toString()).parseMeth(0, new String[]{"value1","value2","value3","value11","value12","value13"});

				if (v.size() > 0)
				{
					String[] row = (String[]) v.elementAt(0);
					infos[0] = row[0];
					infos[1] = row[1];
					infos[2] = row[2];
					infos[3] = row[3];
					infos[4] = row[4];
					infos[5] = row[5];
					return true; 
				}
			}
		}
		catch (Exception er)
		{
			er.printStackTrace();
		}

		return false;
	}
}
