package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.commonKit.ProgressBox;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
/**
 * 睢宁百盛
 */
public class SnYl1_PaymentBankFunc extends PaymentBankFunc
{
	public String getbankfunc()
	{
		return ConfigClass.BankPath + "\\";
	}
	
	public String[] getFuncItem()
    {
        String[] func = new String[11];
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "银联结算";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "签购单重打上一笔";
        func[7] = "[" + PaymentBank.XKQT1+ "]" + "签购单重打任意一笔";
        func[8] = "[" + PaymentBank.XKQT2 + "]" + "QPBOC 查询余额";
        func[9] = "[" + PaymentBank.XKQT3 + "]" + "EMV参数下载";
        func[10] = "[" + PaymentBank.XKQT4+ "]" + "公钥下载";

        return func;
    }
	
	public boolean getFuncLabel(int type, String[] grpLabelStr)
    {
		switch (type)
        {
        	case PaymentBank.XYKXF: //	消费
        		grpLabelStr[0] = null;
        		grpLabelStr[1] = null;
        		grpLabelStr[2] = null;
        		grpLabelStr[3] = null;
        		grpLabelStr[4] = "交易金额";
        	break;
        	case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = "原凭证号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = null;
				grpLabelStr[1] = null;
				grpLabelStr[2] = null;
				grpLabelStr[3] = null;
				grpLabelStr[4] = "交易金额";
			break;
        	case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //银联结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "银联结账";
            break;
        	case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "余额查询";
            break;
        	case PaymentBank.XYKCD: //重打上笔签购单
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
            break;
        	case PaymentBank.XKQT1:		// 重打任意一笔
        	    grpLabelStr[0] = "原凭证号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打任意一笔";
            break;    
        	case PaymentBank.XKQT2:		// QPBOC 查询余额
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "QPBOC 查询余额";
            break;    
        	case PaymentBank.XKQT3:		// EMV参数下载
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "EMV参数下载";
            break;   
        	case PaymentBank.XKQT4:		// 公钥下载
        		grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "公钥下载";
            break;   
        
        }
		
		return true;
    }
	
	public boolean getFuncText(int type, String[] grpTextStr)
    {
		switch (type)
		{
		 	case PaymentBank.XYKXF: 	// 消费
		        grpTextStr[0] = null;
		        grpTextStr[1] = null;
		        grpTextStr[2] = null;
		        grpTextStr[3] = null;
		        grpTextStr[4] = null;
		    break;
		 	case PaymentBank.XYKCX: 	// 消费撤销
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = null;
            break;
		 	case PaymentBank.XYKTH:		//隔日退货   
				grpTextStr[0] = null;
				grpTextStr[1] = null;
				grpTextStr[2] = null;
				grpTextStr[3] = null;
				grpTextStr[4] = "按回车键开始隔日退货 ";
			break;
		 	case PaymentBank.XYKQD: 	//交易签到
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始交易签到";
            break;
		 	case PaymentBank.XYKJZ: 	//银联结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始银联结账";
            break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键签购单重打";
            break;
		 	case PaymentBank.XKQT1:		// QPBOC 查询余额
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键重打任意一笔";
		 	break;	
		 	case PaymentBank.XKQT2:		// QPBOC 查询余额
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键QPBOC 查询余额";
		 	break;	
		 	case PaymentBank.XKQT3:		// EMV参数下载
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "EMV参数下载";
		 	break;	
		 	case PaymentBank.XKQT4:		// 公钥下载
		 		grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键公钥下载";
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
				(type != PaymentBank.XYKJZ) && (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) && (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2) && (type != PaymentBank.XKQT3) && (type != PaymentBank.XKQT4))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist(getbankfunc() + "request.txt"))
            {
                PathFile.deletePath(getbankfunc() + "request.txt");
                
                if (PathFile.fileExist(getbankfunc() + "request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(getbankfunc() + "result.txt"))
            {
                PathFile.deletePath(getbankfunc() + "result.txt");
                
                if (PathFile.fileExist(getbankfunc() + "result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist(getbankfunc() + "print.txt"))
            {
                PathFile.deletePath(getbankfunc() + "print.txt");
                
                if (PathFile.fileExist(getbankfunc() + "print.txt"))
                {
            		errmsg = "交易请求文件print.txt无法删除,请重试";
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
                if (PathFile.fileExist(getbankfunc() + "javaposbank.exe"))
                {
                	CommonMethod.waitForExec(getbankfunc() + "javaposbank.exe SNYL");
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
            if (XYKNeedPrintDoc())
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
	
	 public boolean XYKNeedPrintDoc()
	 {
        if (!checkBankSucceed())
        {
            return false;
        }
        
        return true;
	 }
	 
	 public boolean XYKCheckRetCode()
	 {
        if (bld.retcode.trim().equals("00"))
        {
            bld.retbz = 'Y';

            return true;
        }
        else
        {
            bld.retbz = 'N';

            return false;
        }
	 }
	 
	 public boolean checkBankSucceed()
	 {
        if (bld.retbz == 'N')
        {
            errmsg = bld.retmsg;

            return false;
        }
        else
        {
            errmsg = "交易成功";

            return true;
        }
	 }
	  
	 public boolean XYKWriteRequest(int type, double money, String track1,String track2, String track3,String oldseqno, String oldauthno,String olddate, Vector memo)
	 {
		 try
	     {
			 String line = "";
			 String type1 = "";
			 		 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         
	     	//流水号
	         String strseqno="";
				if (oldseqno != null)
				{
					strseqno = Convert.increaseChar(oldseqno,'0', 6);
				}
				else
				{
					strseqno = Convert.increaseChar("0", 6);
				}
	        
//				终端号
				String stroldterm="";
				if (oldauthno != null)
				{
					stroldterm = Convert.increaseChar(oldauthno,'0', 12);
				}
				else
				{
					stroldterm = Convert.increaseChar("0", 12);
				}
				
//				 原交易日期
				String strolddate="";
				if (olddate != null)
				{
					strolddate = Convert.increaseChar(olddate, 4);
				}
				else
				{
					strolddate = Convert.increaseChar("", 4);
				}
	         
		         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
		         
			    String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
			    
			    String qt = Convert.increaseChar("", 100);
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		type1 = "22";
	         	break;
	         	case PaymentBank.XYKCX:
	         		type1 = "23";
	         	break;	
	         	case PaymentBank.XYKTH:
	         		type1 = "25";
	         	break;	
	         	case PaymentBank.XYKQD:	
	         		type1 = "00";	
	         	break;	
	         	case PaymentBank.XYKJZ:
	         		type1 = "02";	
	         	break;	
	         	case PaymentBank.XYKYE:
	         		type1 = "03";	
	         	break;
	         	case PaymentBank.XYKCD:
	         		type1 = "A0";	
	         	break;	
	         	case PaymentBank.XKQT1:
	         		type1 = "A1";	
	         	break;	
	        	case PaymentBank.XKQT2:
	         		type1 = "30";	
	         	break;	
	        	case PaymentBank.XKQT3:
	         		type1 = "91";	
	         	break;	
	        	case PaymentBank.XKQT4:
	         		type1 = "92";	
	         	break;	
	         	
	         }
	         
	         line = type1+","+jestr+","+strolddate+","+strseqno+","+stroldterm+","+syjh+","+syyh+","+qt+",";
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile(getbankfunc() + "request.txt");
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
	 
	 public boolean XYKReadResult()
	 {
        BufferedReader br = null;
        
        try
        {
        	if (!PathFile.fileExist(getbankfunc() + "result.txt") || ((br = CommonMethod.readFileGBK(getbankfunc() + "result.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);

                return false;
            }
        	
        	String line = br.readLine();

            if (line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            
            if (result == null) return false;
             
            bld.retcode 	= result[0];
            if(bld.retcode.equals("00")){
            	bld.je = ManipulatePrecision.doubleConvert(Double.parseDouble(result[2])/100, 2, 1);
            	bld.cardno = result[6];
                bld.retmsg = result[1];
            }
            else{
            	bld.retmsg = result[1];
            	return false;
            }
        	return true;
        }
        catch (Exception ex)
        {
        	XYKSetError("XX","读取应答XX:"+ex.getMessage());
            new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
            ex.printStackTrace();

            return false;
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                    
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
	 }
	 
	 public void XYKPrintDoc()
		{
			ProgressBox pb = null;
			
				String printName = "";
			try
			{
				if (!PathFile.fileExist(getbankfunc() + "trans.rpt"))
				{	
					new MessageBox("签购单文本不存在无法打印!", null, false);
				     return;
				 }
				else
				 {
				       printName = getbankfunc() + "trans.rpt";
				 }
				
				pb = new ProgressBox();
		        pb.setText("正在打印银联签购单,请等待...");
		        
		        for (int i = 0; i < GlobalInfo.sysPara.bankprint; i++)
		        {
		        	XYKPrintDoc_Start();

		            BufferedReader br = null;
		            
		        try
		        {
		            	 br = CommonMethod.readFileGBK(printName);

		            	 if (br == null)
		            	 {
	                        new MessageBox("打开" + printName + "打印文件失败!");

	                        return;
		            	 }
		            	 
		            	 String line = null;
		             
		            	 while ((line = br.readLine()) != null)
		            	 {
		            		 
		            		 if (line.indexOf("?") >= 0) continue;
		            			 
		            		 XYKPrintDoc_Print(line);
		            	 }
		            }
		            catch (Exception ex)
		            {
		            	new MessageBox(ex.getMessage());
		            }
		            finally
		            {
		            	if (br != null)
		            	{
		            		br.close();
		            	}
		            }

		            XYKPrintDoc_End();
		        }
				
			}
			catch (Exception ex)
			{
				new MessageBox("打印签购单发生异常\n\n" + ex.getMessage());
				ex.printStackTrace();
			}
			finally
			{
				if (pb != null)
				{
					pb.close();
					pb = null;
				}
			}
		}
	 
		public void XYKPrintDoc_Start()
		{
				Printer.getDefault().startPrint_Normal();
		}
	 
	 public void XYKPrintDoc_Print(String printStr)
		{
			 Printer.getDefault().printLine_Normal(printStr);
		}
	 
		public void XYKPrintDoc_End()
		{
			Printer.getDefault().cutPaper_Normal();
		}
}
