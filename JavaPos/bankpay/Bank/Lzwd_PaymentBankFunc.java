package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import org.eclipse.swt.widgets.Text;

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

//兰州万达金卡工程接口
//调用动态库（模块名：JXNX；动态库(dll文件）：MisPos.dll ；函数：int BankTrans(char *  InputData, char* OutPutData)）
public class Lzwd_PaymentBankFunc extends PaymentBankFunc
{
	String path = null;
	public String[] getFuncItem()
    {
        String[] func = new String[7];
        
        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKYE + "]" + "查询余额";
        func[4] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[5] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[6] = "[" + PaymentBank.XYKJZ + "]" + "交易结算";       
        
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
                grpLabelStr[0] = null;
                grpLabelStr[1] = "流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易金额";
            break;
        	case PaymentBank.XYKTH://隔日退货   
				grpLabelStr[0] = "原授权号";
				grpLabelStr[1] = "原流水号";
				grpLabelStr[2] = "原日期时间";
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
        	case PaymentBank.XYKCD: //重打签购单    
                grpLabelStr[0] = null;
                grpLabelStr[1] = "流水号";
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打签购单";
            break;
        	case PaymentBank.XYKQD: //签到   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";
            break;
        	case PaymentBank.XYKJZ: //结算  
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易结算";
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
				grpTextStr[4] = null;
			break;
		 	case PaymentBank.XYKYE: 	//余额查询    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始余额查询";
            break;
		 	case PaymentBank.XYKCD: 	//重打签购单    
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始重打印";
            break;
		 	case PaymentBank.XYKQD: 	//交易签到   
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始签到";
            break;
		 	case PaymentBank.XYKJZ: 	//交易结算
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始结算";
            break;
		}
		
		return true;
    }
	public boolean XYKExecute(int type, double money, String track1,String track2, String track3, String oldseqno,String oldauthno, String olddate, Vector memo)
	{	
		try
		{
			if (!(type == PaymentBank.XYKXF || 
				  type == PaymentBank.XYKCX || 
				  type == PaymentBank.XYKTH || 
				  type == PaymentBank.XYKQD ||
				  type == PaymentBank.XYKJZ || 
				  type == PaymentBank.XYKYE ||
				  type == PaymentBank.XYKCD ))
				{			
					  new MessageBox("银联接口不支持此交易类型！！！");
					  
					  return false;
			    }
			
			path = getBankPath(paycode);
			if (PathFile.fileExist(path + "\\request.txt"))
			{
				PathFile.deletePath(path + "\\request.txt");
				if (PathFile.fileExist(path + "\\request.txt"))
				{
					errmsg = "交易“request.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(path + "\\result.txt"))
			{
				PathFile.deletePath(path + "\\result.txt");
				if (PathFile.fileExist(path + "\\result.txt"))
				{
					errmsg = "交易“result.txt”文件删除失败，请重试！！！";
					XYKSetError("XX",errmsg);
					new MessageBox(errmsg);
					
					return false;
				}				
			}
			if (PathFile.fileExist(path + "\\P_TackSingle.txt"))
            {
                PathFile.deletePath(path + "\\P_TackSingle.txt");
            }
            // 写入请求数据
            if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo))
            {
                return false;
            }
            //  调用接口模块
            if (PathFile.fileExist(path+"\\javaposbank.exe"))
            {
//            	CommonMethod.waitForExec(path+"\\javaposbank.exe JXNX","javaposbank");
            	CommonMethod.waitForExec(path+"\\javaposbank.exe JXNX");
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
			
            //打印签购单
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
		 String line = "";
		 String type1 = "";
		 PrintWriter pw = null;
		 
		 try
		 {
			 switch (type)
			 {
			 	case PaymentBank.XYKXF: 	// 消费
                    type1 = "00";
                break;
			 	case PaymentBank.XYKCX: 	// 消费撤销
			 		type1 = "01";
			 	break;	
                case PaymentBank.XYKTH:		// 隔日退货
                	type1 = "02";
                break;
                case  PaymentBank.XYKYE: 	// 余额查询
                	type1 = "03";
                break;	
                case  PaymentBank.XYKCD: 	// 重打签购单
                	type1 = "04";
                break;
                case  PaymentBank.XYKQD: 	// 交易签到
                	type1 = "05";
                break;
                case  PaymentBank.XYKJZ:
                    type1 = "06";			// 交易结算
                break;
			 }
			 
//				char posid[8];    /*1～8位 收银机号（最多8字节，左对齐，不足部分补空格*/
//				char operid[8];   /*9～16位	操作员号（最多8字节，左对齐，不足部分补空格）*/
//				char trans[2];    /*17～18位	  交易类型:*//*（'00'-消费  '01'-撤销  '02'-退货  '03'-查余额  '04'重打指定流水  '05'签到 '06'结算）*/
//				char amount[12];  /*19～30位 金额（12字节，无小数点，左补0，单位：分）*/
//				char old_date[4];	/*31～34位	原交易日期（4字节,mmdd格式，退货时用*/
//				char old_time[6];	/*35～40 原交易时间hhmmss*/
//			    char old_authno[6];  /*41～46原交易授权号*/
//				char old_reference[12]; /*47～58位	原交易参考号 (12字节，右对齐，左补0，退货时用)*/
//				char old_trace[6];   /*59～64位	流水号（6字节，右对齐，左补0，撤销或重打印），‘000000’为重打印上一笔*/
//				char trk2[37];	 	/*65～101位	二磁道数据（37字节，左对齐，不足部分补空格）*/
//				char trk3[104]; 	/*102～205位  三磁道数据（104字节, 左对齐，不足部分补空格*/
//				char lrc[3];    	/*206～208位 交易校验数据（3位从0～9的随机字符）*/

	         String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh,' ', 8);
	         
	         String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode,' ', 8);
	         
			 String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));
	         jestr = Convert.increaseCharForward(jestr,'0',12);
	         
	         String date="";
	         String time="";
	         
	         if(olddate.length() < 10){
	        	 
	        	 date = Convert.increaseChar(olddate, '0', 4); //交易日期
		         
		         time = Convert.increaseChar(olddate, '0', 6); //交易时间
	        	 
	         }else{
	        	 
	        	 date = Convert.increaseChar(olddate.substring(0, 4), '0', 4); //交易日期
		         
		         time = Convert.increaseChar(olddate.substring(4, 10), '0', 6); //交易时间
	         }
	         
	         
	         
	         String authno = Convert.increaseCharForward(oldseqno, '0', 6); //系统授权号
	         
	         if(type == PaymentBank.XYKTH){
	        	 
	        	 if("000000".equals(authno)){
	        		authno = "      ";
	        	 }
	         }
	         
	         String seqno = Convert.increaseCharForward("", '0', 12); //参考号
	         	         
	         String trace = Convert.increaseCharForward(oldauthno, '0', 6); //流水号   
	         
	         
	         
	         String strack2 = Convert.increaseChar(track2, ' ', 37); //二磁道数据
	         
	         String strack3 = Convert.increaseChar(" ", ' ', 104); //三磁道数据

	         bld.crc = XYKGetCRC();
	         
	        
	         //收银机号+操作员号+交易类型+金额+原交易日期+原交易时间+原交易授权号+原交易参考号+流水号+二磁道数据+三磁道数据+交易校验数据
	         line = syjh + syyh + type1 + jestr + date + time + authno + seqno + trace + strack2 + strack3 + bld.crc;
	     
	         try
	         {
	            pw = CommonMethod.writeFile(path+"\\request.txt");
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

	
	
	
	
	//读取result文件
	public boolean XYKReadResult()
	{
		BufferedReader br = null;
		
		try
        {
			if (!PathFile.fileExist(path+"\\result.txt") || ((br = CommonMethod.readFileGBK(path+"\\result.txt")) == null))
            {
            	XYKSetError("XX","读取金卡工程应答数据失败!");
                new MessageBox("读取金卡工程应答数据失败!", null, false);
                
                return false;
            }
			
			String line = br.readLine();

            if (line == null || line.length() <= 0)
            {
                return false;
            }
            
            String result[] = line.split(",");
            if (result == null) return false;
            
            bld.memo		= result[0];
            
            int type = Integer.parseInt(bld.type.trim());
             
            if (result.length >= 2)
            {
            	bld.retcode  =  Convert.newSubString(result[1], 0, 2);  //返回码2
            	if(!(bld.retcode.trim().equals("00")))
            	{
            		
//            		char resp_code[2];		/*1～2位 返回码	( 2字节， "00"成功)*/
//            		char bank_code[20];		/*3～22位 发卡行（20字节）*/
//            		char card_no[20];		/*23～42 卡号	(20字节，左对齐，不足部分补空格)*/
//            		char expr[4];			/*43～46位 有效期	(4字节) */
//            		char trace[6];			/*47～52位 流水号  (6字节，左对齐)*/
//            	    char reference[12]; /*53～64交易参考号*/
//            		char Date[4];		/*65～68交易日期mmdd*/
//            		char Time[6];		/*69～74交易时间hhmmss*/ 
//            	    char Authno[6];  /*75～80交易授权号*/
//            		char amount[12];		/*81～92金额（12字节，无小数点，左补0，单位：分）*/
//            		char resp_chin[40];		/*93～132 错误说明(40字节，左对齐，不足部分补空格)*/
//            	    char lrc[3];		/*133～135交易数据校验码*/
            		
//            		bld.retmsg = result[1].substring(48,88);   //错误说明
            		bld.retmsg = Convert.newSubString(result[1],92,132);
            		return false;
            	}
            	if(type == PaymentBank.XYKXF || type == PaymentBank.XYKCX ||  type == PaymentBank.XYKTH )
            	{
                	bld.cardno = Convert.newSubString(result[1], 22, 42);   //卡号20
                	bld.trace = Long.parseLong(Convert.newSubString(result[1], 46, 52));   //流水号6
                	bld.je = ManipulatePrecision.doubleConvert(ManipulatePrecision.div(Double.parseDouble(Convert.newSubString(result[1], 80, 92) ),100),2,1);   //交易金额		
            	}  	
  	
//            	String lrc = result[1].substring(88,91);   //交易数据校验码
//            	String lrc = Convert.newSubString(result[1],132,135);
            	
//    			if(!lrc.equals(bld.crc))
//    			{
//    				errmsg = "返回效验码" + lrc + "同原效验码" + bld.crc + "不一致";
//    				XYKSetError("XX", errmsg);
//    				new MessageBox(errmsg);
//    				
//    				return false;
//    			}
            	      
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
                    if (PathFile.fileExist(path + "\\request.txt"))
					{
						PathFile.deletePath(path + "\\request.txt");
					}

					if (PathFile.fileExist(path + "\\result.txt"))
					{
						PathFile.deletePath(path + "\\result.txt");
					}
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
	}
	public boolean XYKCheckRetCode()
	{
		if (bld.memo.trim().equals("0") && bld.retcode.trim().equals("00"))
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
	
	public boolean XYKNeedPrintDoc(int type)
	{
		if (!checkBankSucceed())
	    {
	        return false;
	    }
		if (  type == PaymentBank.XYKXF || type == PaymentBank.XYKCX || 
			  type == PaymentBank.XYKTH || type == PaymentBank.XYKJZ || 
			  type == PaymentBank.XYKCD)
		{
			return true;
		}
		else
			return false;
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
	
	
	public boolean checkDate(Text date)
	{
		String d = date.getText();
		if (d.length() < 10)
		{
			new MessageBox("日期格式错误\n日期格式《MMDDhhmmss》");
			return false;
		}
		
		return true;
	}

	
	public void XYKPrintDoc()
	{
		ProgressBox pb = null;
		String name =path + "\\P_TackSingle.txt";
		try
		{
			if (!PathFile.fileExist(name))
			{
				new MessageBox("找不到签购单打印文件！！！");
				
				return ;
			}
			pb = new ProgressBox();
			
//			pb.setText("OY+状态 : "+GlobalInfo.sysPara.issetprinter +"...."+ Printer.getDefault().getStatus());
			pb.setText("正在打印银联签购单,请等待...");
			for (int i = 0; i < GlobalInfo.sysPara.bankprint; i ++)
			{
				BufferedReader br = null;
				XYKPrintDoc_Start();
				try
				{
					br = CommonMethod.readFileGB2312(name);
					if (br == null)
					{
						new MessageBox("打开签购单文件失败");
						
						return ;
					}
					
					String line = null;
					while ((line = br.readLine()) != null)
					{
						if (line.length() <= 0)
							continue;
						if (line.equals("CUTPAPER"))
						{
							XYKPrintDoc_End();
							new MessageBox("请撕下客户签购单！！！");
							continue;
						}
						XYKPrintDoc_Print(line);
					}					
				}
				catch(Exception e)
				{
					new MessageBox(e.getMessage());
				}
				finally
				{
					if (br != null)
					try
					{
						br.close();
					}
					catch(IOException ie)
					{
						ie.printStackTrace();
					}					
				}
				XYKPrintDoc_End();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			new MessageBox("打印签购单异常!!!\n" + e.getMessage());
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
			if (PathFile.fileExist(name))
			{
				PathFile.deletePath(name);
			}
		}
	}
	
//	public boolean callBankFunc(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
//	{
//		boolean doClosePrint = true;
//
//		try
//		{
//			// 银联接口执行完重新连接打印机
//			if (Printer.getDefault() != null && !Printer.getDefault().getStatus() && doClosePrint)
//			{
//				Printer.getDefault().open();
//				Printer.getDefault().setEnable(true);
//			}
//
//			// 规范数据
//			if (track1 == null)
//				track1 = "";
//			if (track2 == null)
//				track2 = "";
//			if (track3 == null)
//				track3 = "";
//			if (oldseqno == null)
//				oldseqno = "";
//			if (oldauthno == null)
//				oldauthno = "";
//			if (olddate == null)
//				olddate = "";
//
//			// 写入请求数据日志
//			if (!this.WriteRequestLog(type, money, oldseqno, oldauthno, olddate)) { return false; }
//
//			// 调用金卡模块处理
//			this.XYKExecute(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo);
//
//			// 写入应答数据日志
//			this.WriteResultLog();
//
//			// 写入单独进行的银联消费数据
//			this.WriteSelfSaleData(memo);
//
//			// 将交易日志发送网上
//			this.BankLogSend();
//
//			// 判断交易是否成功
//			return checkBankSucceed();
//		}
//		catch (Exception ex)
//		{
//			new MessageBox("执行第三方支付接口异常!\n\n" + ex.getMessage());
//			ex.printStackTrace();
//			return false;
//		}
//		finally
//		{
//			// 银联接口需要自己进行打印则释放打印机
//			if (Printer.getDefault() != null && Printer.getDefault().getStatus())
//			{
//				Printer.getDefault().close();
//				doClosePrint = false;
//			}
//		}
//	}
	
}
