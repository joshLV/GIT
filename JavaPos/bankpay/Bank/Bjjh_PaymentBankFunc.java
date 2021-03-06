package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;

public class Bjjh_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
    {
        String[] func = new String[6];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[3] = "[" + PaymentBank.XYKJZ + "]" + "银联结算";
        func[4] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[5] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
     
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
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = "原交易日";
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
        	case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";
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
		}
		
		return true;
    }
	
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) && 
				 (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ) && 
				 (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) )
	            {
	                errmsg = "银联接口不支持该交易";
	                new MessageBox(errmsg);

	                return false;
	            }
			
			 // 先删除上次交易数据文件
            if (PathFile.fileExist("c:\\bjjh\\request.txt"))
            {
                PathFile.deletePath("c:\\bjjh\\request.txt");
                
                if (PathFile.fileExist("c:\\bjjh\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            if (PathFile.fileExist("c:\\bjjh\\result.txt"))
            {
                PathFile.deletePath("c:\\bjjh\\result.txt");
                
                if (PathFile.fileExist("c:\\bjjh\\result.txt"))
                {
            		errmsg = "交易请求文件result.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }
            
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            
            if (bld.retbz != 'Y')
            {
            	
                // 调用接口模块
                if (PathFile.fileExist("c:\\bjjh\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("c:\\bjjh\\javaposbank.exe BJJH");
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
                if (!XYKCheckRetCode()) return false;
            }
            
            
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
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
	 		 
	         String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         
	         for (int i = jestr.length(); i < 12; i++)
             {
                 jestr = "0" + jestr;
             }
	         
	         //	根据不同的类型生成文本结构
	         switch (type)
	         {
	         	case PaymentBank.XYKXF:
	         		line = PaymentBank.XYKXF + "," + jestr;
	         	break;
	         	case PaymentBank.XYKCX:
	         		line = PaymentBank.XYKCX+ "," + jestr + "," + oldseqno + "," + olddate;
	         	break;
	         	case PaymentBank.XYKQD:
	         		line = String.valueOf(PaymentBank.XYKQD);
	         	break;
	         	case PaymentBank.XYKJZ:
	         		line = String.valueOf(PaymentBank.XYKJZ);
	         	break;
	         	case PaymentBank.XYKYE:
	         		line = String.valueOf(PaymentBank.XYKYE);
	         	break;
	         	case PaymentBank.XYKCD:
	         		line = String.valueOf(PaymentBank.XYKCD);
	         	break;	
	         	
	         }
	         
	         PrintWriter pw = null;
	            
	         try
	         {
	            pw = CommonMethod.writeFile("c:\\bjjh\\request.txt");
	            
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
    	   if (!PathFile.fileExist("c:\\bjjh\\result.txt") || ((br = CommonMethod.readFileGBK("c:\\bjjh\\result.txt")) == null))
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
           
           if (Integer.parseInt(result[0]) != 0)
           {
        	   bld.retcode 		= result[0];
        	   bld.retmsg		= "调用金卡函数发生异常!";
        	   
        	   return false;
           }
           
           
           bld.retcode 		= result[1];
           
           if (!bld.retcode.equals("00"))
           {
        	   bld.retmsg 		= result[2];
        	   return false;
           }
           
           int row =  1;
           
           while ((line = br.readLine()) != null)
           {
        	   row = row + 1;
        	   
        	   switch (row)
        	   {
        		   case 4:   //上品 为 3
        			   bld.bankinfo = line.trim().substring(line.indexOf("：") + 1);
        		   break;
        		   case 6:   //上品 为 5
        			   bld.cardno = line.trim().substring(line.indexOf("：") + 1);
        		   break;
        		   case 9:
        			   bld.trace = Long.parseLong(line.trim().substring(line.indexOf("：") + 1));
        		   break;
        	   }
        	   
        	   if (row >= 9) break;
           }
           
    	   return true;
       }
       catch (Exception ex)
       {
    	   ex.printStackTrace();
    	   
    	   XYKSetError("XX","读取应答XX:"+ex.getMessage());
           new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
           
    	   
    	   return false;
       }
       finally
       {
           if (br != null)
           {
               try
               {
                   br.close();
                   
                   
                   if (PathFile.fileExist("c:\\bjjh\\request.txt"))
		           {
		                PathFile.deletePath("c:\\bjjh\\request.txt");
		           }
					
				   if (PathFile.fileExist("c:\\bjjh\\result.txt"))
		           {
		                PathFile.deletePath("c:\\bjjh\\result.txt");
		           }
               }
               catch (IOException e)
               {
                   e.printStackTrace();
               }
           }
       }
	 }
}
