package bankpay.Bank;

import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
/**
 * 金盛银川店
 * 
 */
public class Jsyc_PaymentBankFunc extends ICBCKPC_PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[9];

		func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
		func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
		func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
		func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
		func[4] = "[" + PaymentBank.XYKJZ + "]" + "交易结账";
		func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
		func[6] = "[" + PaymentBank.XYKCD + "]" + "重打上笔签购单";
		func[7] = "[" + PaymentBank.XKQT1 + "]" + "按流水号重打";
		func[8] = "[" + PaymentBank.XKQT2+ "]" + "分期付款";
		return func;
	}
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该不用输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "余额查询";

				break;

			case PaymentBank.XYKJZ: //结账
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "结账";

				break;

			case PaymentBank.XYKTH: //隔日退货   
				grpLabelStr[0] = "原参考号";
				grpLabelStr[1] = "原终端号";
				grpLabelStr[2] = "原交易日";
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";

				break;

			case PaymentBank.XYKCD: //签购单重打
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打上笔签购单";
				break;
				
			case PaymentBank.XKQT1: //签购单重打
				grpLabelStr[0] = "原凭证号";
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "重打笔签购单";
				break;
			case PaymentBank.XKQT2://分期付款
				grpLabelStr[0] = null;
				grpLabelStr[1] = "分期编号";
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
				break;
				
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		//0-4对应FORM中的5个输入框
		//null表示该需要用户输入,不为null用户不输入
		switch (type)
		{
			case PaymentBank.XYKXF: //消费
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKCX: //消费撤销
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKTH: //退货
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;

				break;

			case PaymentBank.XYKYE: //余额查询    
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始余额查询";

				break;

			case PaymentBank.XYKQD: //交易签到
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始交易签到";

				break;

			case PaymentBank.XYKJZ: //内卡结账
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始结账";

				break;

			case PaymentBank.XYKCD: //签购单重打上笔
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始重打上笔签购单";

				break;
				
			case PaymentBank.XKQT1: //签购单重打指定
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "开始签购单重打";
				break;
			case PaymentBank.XKQT2://分期付款
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = null;
				break;
		}

		return true;
	}
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				(type != PaymentBank.XYKTH) && (type != PaymentBank.XYKQD) && 
				(type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath+"\\request.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath+"\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath+"\\result.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath+"\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(ConfigClass.BankPath+"\\ICBCPRTTKT.txt"))
            {
                PathFile.deletePath(ConfigClass.BankPath+"\\ICBCPRTTKT.txt");
                
                if (PathFile.fileExist(ConfigClass.BankPath+"\\ICBCPRTTKT.txt"))
                {
            		errmsg = "交易请求文件ICBCPRTTKT.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            //  写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	
                // 调用接口模块
                if (PathFile.fileExist(ConfigClass.BankPath+"\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec(ConfigClass.BankPath+"\\javaposbank.exe ICBCKPCLIENT2");
                }
                else
                {
                    new MessageBox("找不到金卡工程模块 javaposbank.exe");
                    XYKSetError("XX","找不到金卡工程模块 javaposbank.exe");
                    return false;
                }
                
                // 读取应答数据
                if (!XYKReadResult())
                {
                    return false;
                }
                
                // 检查交易是否成功
                XYKCheckRetCode();
            }
            
            // 	打印签购单
        	if (XYKNeedPrintDoc(type))
            {
                XYKPrintDoc();
            }
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
            XYKSetError("XX","金卡异常XX:"+ex.getMessage());
            new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
			return false;
		}
	}
	
	  
	 public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	 {
		 try
	     {
				String line = "";

				String type1 = "";
		
				switch (type)
				{
					case PaymentBank.XYKQD:
						type1 = "09";
						break;
					case PaymentBank.XYKXF://消费
						type1 = "05";
						break;
					case PaymentBank.XYKCX:
						type1 = "04";
						break;
					case PaymentBank.XYKTH:
						type1 = "04";
						break;
					case PaymentBank.XYKYE:		// 查询余额
						type1 = "10";
						break;
					case PaymentBank.XYKCD:		// 重打上笔票据
						type1 = "13";
						break;
					case PaymentBank.XYKJZ:		// 交易结账
						type1 = "14";
						break;
					case PaymentBank.XKQT1:		// 重打指定票据
						type1 = "13";
						break;
					case PaymentBank.XKQT2:		// 分期付款
						type1 = "12";
						break;
					default:
						return false;
				}
				
				/**
typedef struct
{
   	char TransType[2];		//交易类型（收银机MIS系统传入），消费:"05"  消费撤销:"04" 查询余额"10"
	char CardNo[19];		//交易卡号：MISPOS系统返回需记录到数据库中作为对账内容之一
	char Amount[12];		//交易金额（收银机MIS系统传入，需记录到数据库中作为对账内容之一）
	char Tip[12];			//小费金额（暂时不用，空格补齐）
	char MisBatchNo[6];		//MIS批次号（暂时不用，空格补齐）
	char MisTraceNo[6];		//MIS流水号（暂时不用，空格补齐）
	char TransTime[6];		//交易时间（MISPOS系统返回）
	char TransDate[8];		//交易日期（MISPOS系统返回）
    	char ExpDate[4];		//卡片有效期（MISPOS系统返回）
	char Track2[37];		//二磁道信息（预留，暂时不用）
	char Track3[104];		//三磁道信息（预留，暂时不用）
	char ReferNo[8];		//系统检索号（MISPOS系统返回，收银机MIS系统在撤销交易时需传给MISPOS系统，需记录到数据库中作为对账内容之一）
	char AuthNo[6];			//MISPOS系统返回 暂时不用，空格补齐
	char ReturnCode[2];		//返回码（MISPOS系统返回，返回码为“00”表示交易成功，否则表示交易失败）
	char TerminalId[15];		//MISPOS系统返回交易终端号
	char MerchantId[12];		//MISPOS系统返回商户号
	char InstallmentTimes[2];	//MISPOS系统返回 暂时不用，空格补齐
	char TC[16];			//MISPOS系统返回 暂时不用，空格补齐
	char OldAuthDate[8];		//原交易日期，撤销交易时候传送给MISPOS系统
	char MerchantNameEng[50];	//商户名称（英文）MISPOS系统返回
	char MerchantNameChs[40];	//商户中文名称MISPOS系统返回
	char TerminalTraceNo[6];	//终端流水号MISPOS系统返回
	char TerminalBatchNo[6];	//终端批次号MISPOS系统返回
	char IcCardId[4];		//MISPOS系统返回，暂时无需处理
	char CardType[20];		//MISPOS系统返回
	char TransName[20];		//MISPOS交易中文名称
	char DeviceInitFlag[1];		//MISPOS系统返回，暂时无需处理
	char Message[100];		//交易失败时，MISPOS系统返回中文错误描述信息
	char Remark[300];		//MISPOS系统返回，暂时无需处理
	char ForeignCardTraceNo[24];	//MISPOS系统返回，暂时无需处理
	char Ttotal[800];		//交易信息汇总，为交易总账信息
	char PlatId[10];		//收银台号
	char OperId[10];		//操作员号
} ST_ICBC_MIS;
				 */
				// 交易卡号【19】
				String cardno = Convert.increaseChar("", 19);
				// 交易金额【12】
				String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));
				jestr = Convert.increaseCharForward(jestr, '0', 12);
				// 消费金额【12】
				String Tip = Convert.increaseChar("", 12);
				// MIS批次号【6】
				String MisBatchNo = Convert.increaseChar("", 6);
				// MIS流水号【6】
				String MisTraceNo = Convert.increaseChar("", 6);
				// 交易时间【6】
				String TransTime = Convert.increaseChar("", 6);
				// 交易日期【8】
				String TransDate = Convert.increaseChar(olddate , 8);
				// 卡片有效期【4】
				String ExpDate = Convert.increaseChar("", 4);
				// 二磁道信息【37】
				String Track2 = Convert.increaseChar("", 37);
				// 三磁道信息【104】
				String Track3 = Convert.increaseChar("", 104);
				// 系统检索号【8】
				String ReferNo = Convert.increaseChar(oldseqno , 8);
				// MISPOS系统返回【6】
				String AuthNo = Convert.increaseChar("", 6);
				// 返回
				String retmsg = ","+Convert.increaseChar("", 2)+
								","+Convert.increaseChar(oldauthno,' ', 15)+
								","+Convert.increaseChar("", 12)+
								","+Convert.increaseChar("", 15)+
								","+Convert.increaseChar("", 2)+
								","+Convert.increaseChar("",16)+
								","+TransDate+
								"," +Convert.increaseChar("", 50)+
								","+Convert.increaseChar("", 40)+
								","+Convert.increaseChar("", 6)+
								","+Convert.increaseChar("", 6)+
								","+Convert.increaseChar("", 4)+
								","+Convert.increaseChar("", 20)+
								","+Convert.increaseChar("", 20)+
								","+Convert.increaseChar("", 800)+
								","+Convert.increaseChar("", 1)+
								","+Convert.increaseChar("", 100)+
								","+Convert.increaseChar("", 300)+
								","+Convert.increaseChar("", 24)+
								","+Convert.increaseChar(ConfigClass.CashRegisterCode, 10)+
								","+Convert.increaseChar(GlobalInfo.posLogin.gh, 10);
				bld.type = type1;
				line = type1 +","+ cardno +","+ jestr +","+ Tip +","+ MisBatchNo +","+ MisTraceNo +","+ TransTime +","+ Convert.increaseChar("" , 8) +","+ ExpDate +","+ Track2 +","+ Track3 +","+ ReferNo +","+ AuthNo+retmsg;

	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(ConfigClass.BankPath+"\\request.txt");
	            if (pw != null)
	            {
	                pw.println(line);
	                pw.flush();
	            }
	         }
	         finally
	         {
	        	if (pw != null)
	        	{
	        		pw.close();
	        	}
	         }
		        
			 return true;
	     }
		 catch (Exception ex)
		 {
			 new MessageBox("写入金卡工程请求数据异常!\n\n" + ex.getMessage(), null, false);
	         ex.printStackTrace();

	         return false;
		 }
	 }
	
}
