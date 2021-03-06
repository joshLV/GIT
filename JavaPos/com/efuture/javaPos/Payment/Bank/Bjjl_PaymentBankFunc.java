package com.efuture.javaPos.Payment.Bank;

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
import com.efuture.javaPos.Payment.PaymentBank;


public class Bjjl_PaymentBankFunc extends PaymentBankFunc
{
    public String[] getFuncItem()
    {
        String[] func = new String[10];

        func[0] = "[" + PaymentBank.XYKXF + "]" + "消费";
        func[1] = "[" + PaymentBank.XYKCX + "]" + "消费撤销";
        func[2] = "[" + PaymentBank.XYKTH + "]" + "隔日退货";
        func[3] = "[" + PaymentBank.XYKQD + "]" + "交易签到";
        func[4] = "[" + PaymentBank.XYKJZ + "]" + "内卡结账";
        func[5] = "[" + PaymentBank.XYKYE + "]" + "余额查询";
        func[6] = "[" + PaymentBank.XYKCD + "]" + "重打签购单";
        func[7] = "[" + PaymentBank.XKQT1 + "]" + "外卡结账";
        func[8] = "[" + PaymentBank.XKQT2 + "]" + "重印内卡结账单";
        func[9] = "[" + PaymentBank.XKQT3 + "]" + "重印外卡结账单";

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
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "交易金额";

                break;

            case PaymentBank.XYKCX: //消费撤销
                grpLabelStr[0] = "原流水号";
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "交易金额";

                break;

            case PaymentBank.XYKYE: //余额查询    
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = "请 刷 卡";
                grpLabelStr[4] = "余额查询";

                break;

            case PaymentBank.XYKQD: //交易签到
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "交易签到";

                break;

            case PaymentBank.XYKJZ: //内卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "内卡结账";

                break;

            case PaymentBank.XYKTH: //隔日退货   
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "无效";

                break;

            case PaymentBank.XKQT1: //外卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "外卡结账";

                break;

            case PaymentBank.XYKCD: //签购单重打
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重打上笔签购单";

                break;

            case PaymentBank.XKQT2: //重印内卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重印内卡结账";

                break;

            case PaymentBank.XKQT3: //重印外卡结账
                grpLabelStr[0] = null;
                grpLabelStr[1] = null;
                grpLabelStr[2] = null;
                grpLabelStr[3] = null;
                grpLabelStr[4] = "重印外卡结账";

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
                grpTextStr[4] = "银联不支持该功能";

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
                grpTextStr[4] = "按回车键开始内卡结账";

                break;

            case PaymentBank.XKQT1: //外卡结账
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "按回车键开始外卡结账";

                break;

            case PaymentBank.XYKCD: //签购单重打
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "开始签购单重打";

                break;

            case PaymentBank.XKQT2: //重印内卡结账单
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "重印内卡结账单";

                break;

            case PaymentBank.XKQT3: //重印外卡结账单
                grpTextStr[0] = null;
                grpTextStr[1] = null;
                grpTextStr[2] = null;
                grpTextStr[3] = null;
                grpTextStr[4] = "重印外卡结账单";

                break;
        }

        return true;
    }

    public boolean XYKExecute(int type, double money, String track1,
                              String track2, String track3, String oldseqno,
                              String oldauthno, String olddate, Vector memo)
    {
        try
        {
            if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKCX) &&
                    (type != PaymentBank.XYKQD) && (type != PaymentBank.XYKJZ) &&
                    (type != PaymentBank.XYKYE) && (type != PaymentBank.XYKCD) &&
                    (type != PaymentBank.XKQT1) && (type != PaymentBank.XKQT2) &&
                    (type != PaymentBank.XKQT3))
            {
                errmsg = "银联接口不支持该交易";
                new MessageBox(errmsg);

                return false;
            }

            // 先删除上次交易数据文件
            if (PathFile.fileExist("c:\\request.txt"))
            {
                PathFile.deletePath("c:\\request.txt");
                
                if (PathFile.fileExist("c:\\request.txt"))
                {
            		errmsg = "交易请求文件request.txt无法删除,请重试";
            		XYKSetError("XX",errmsg);
            		new MessageBox(errmsg);
            		return false;   	
                }
            }

            if (PathFile.fileExist("c:\\result.txt"))
            {
                PathFile.deletePath("c:\\result.txt");
                
                if (PathFile.fileExist("c:\\result.txt"))
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
                if (PathFile.fileExist("c:\\javaposbank.exe"))
                {
                	CommonMethod.waitForExec("c:\\javaposbank.exe BJJL");
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
            
           
            // 打印签购单
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

        int type = Integer.parseInt(bld.type.trim());
        
        // 消费，消费撤销，重打签购单
        if ((type == PaymentBank.XYKXF) || (type == PaymentBank.XYKCX) ||
                (type == PaymentBank.XYKTH) || (type == PaymentBank.XYKCD) ||
                (type == PaymentBank.XYKJZ) || (type == PaymentBank.XKQT1) ||
                (type == PaymentBank.XKQT2) || (type == PaymentBank.XKQT3))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean XYKCheckRetCode()
    {
        if (bld.retcode.trim().equals("0"))
        {
            bld.retbz  = 'Y';
            bld.retmsg = "金卡工程调用成功";

            return true;
        }
        else
        {
        	BufferedReader br = null;
        	
        	if (PathFile.fileExist("c:\\U\\LOG\\err.txt") && (br = CommonMethod.readFileGBK("c:\\U\\LOG\\err.txt")) != null)
            {
        		try
        		{
        			bld.retmsg = br.readLine();
        		}
        		catch (Exception ex)
        		{
        			ex.printStackTrace();
        		}
        		finally
        		{
        			try
        			{
        				if (br != null)
        				{
        					br.close();
        					br = null;
        				}
        				
        				 PathFile.deletePath("c:\\U\\LOG\\err.txt");
        			}
        			catch (Exception ex)
        			{
        				ex.printStackTrace();
        			}
        		}
            }
        	
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

    public boolean XYKWriteRequest(int type, double money, String track1,
                                      String track2, String track3,
                                      String oldseqno, String oldauthno,
                                      String olddate, Vector memo)
    {
        try
        {
            String line = "";

            String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100,2,1));

            //流水号
            String seq = "";

            if ((oldseqno == null) || (oldseqno.length() <= 0))
            {
                seq = Convert.increaseInt(0, 6);
            }
            else
            {
                try
                {
                    int num_seq = Integer.parseInt(oldseqno);
                    seq = Convert.increaseInt(num_seq, 6);
                }
                catch (Exception er)
                {
                    seq = Convert.increaseInt(0, 6);
                }
            }

            //根据不同的类型生成文本结构
            switch (type)
            {
                case PaymentBank.XYKXF:
                    line = String.valueOf(type) + "," + jestr + "," + track2 +
                           "," + track3 + ",0";

                    break;

                case PaymentBank.XYKCX:
                    line = String.valueOf(type) + "," + seq + "," + track2 +
                           "," + track3 + ",0";

                    break;

                case PaymentBank.XYKYE:
                    line = String.valueOf(type) + "," + track2 + "," + track3 +
                           ",0";

                    break;

                case PaymentBank.XYKQD:
                case PaymentBank.XKQT1:
                case PaymentBank.XYKJZ:
                    line = String.valueOf(type);

                    break;

                default:
                    bld.retbz = 'Y';

                    return true;
            }

            PrintWriter pw = null;
            
            try
            {
	            pw = CommonMethod.writeFile("c:\\request.txt");
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
            if (!PathFile.fileExist("c:\\result.txt") ||
                    ((br = CommonMethod.readFileGBK("c:\\result.txt")) == null))
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

            if (line.indexOf(",") != -1)
            {
                bld.retcode = line.trim().substring(0, line.indexOf(","));
                bld.retmsg  = line.trim().substring(line.indexOf(',') + 1);
            }
            else
            {
                bld.retcode = line;
            }

            if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF)) ||
                    bld.type.equals(String.valueOf(PaymentBank.XYKCX))) &&
                    bld.retcode.equals("0"))
            {
                if (!PathFile.fileExist("c:\\u\\print\\print.txt"))
                {
                    new MessageBox("找不到签购单打印文件!");

                    return false;
                }

                BufferedReader br1 = null;

                try
                {
                    br1 = CommonMethod.readFileGBK("c:\\u\\print\\print.txt");

                    if (br1 == null)
                    {
                        new MessageBox("打开c:\\u\\print\\print.txt打印文件失败!");

                        return false;
                    }

                    //
                    String line1 = null;

                    while ((line1 = br1.readLine()) != null)
                    {
                        if (line1.indexOf("卡号:") != -1)
                        {
                            bld.cardno = line1.substring(line1.indexOf("卡号:") +
                                                         3);
                        }
                        else if (line1.indexOf("流水号:") != -1)
                        {
                            try
                            {
                                bld.trace = Long.parseLong(line1.substring(line1.indexOf("流水号:") +
                                                                           4));
                            }
                            catch (Exception er)
                            {
                                new MessageBox(er.getMessage());
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    new MessageBox(e.getMessage());
                }
                finally
                {
                    if (br1 != null)
                    {
                        br1.close();
                    }
                }
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

        try
        {
            String printName = "";
             
            if ((bld.type.equals(String.valueOf(PaymentBank.XYKXF)) || bld.type.equals(String.valueOf(PaymentBank.XYKCX))))
            {
                if (!PathFile.fileExist("C:\\u\\print\\print.txt"))
                {
                    new MessageBox("找不到签购单打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\print.txt";
                }
            }
            else if ((bld.type.equals(String.valueOf(PaymentBank.XYKCD))))
            {
                if (!PathFile.fileExist("C:\\u\\print\\reprint.txt"))
                {
                    new MessageBox("找不到签购单重打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\reprint.txt";
                }
            }
            else if ((bld.type.equals(String.valueOf(PaymentBank.XYKJZ))))
            { 
                if (!PathFile.fileExist("C:\\u\\print\\settzx.txt"))
                {
                    new MessageBox("找不到内卡结账打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\settzx.txt";
                }
            }
            else if ((bld.type.equals(String.valueOf(PaymentBank.XKQT2))))
            {
                if (!PathFile.fileExist("C:\\u\\print\\resettzx.txt"))
                {
                    new MessageBox("找不到内卡结账重打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\resettzx.txt";
                }
            }
            else if ((bld.type.equals(String.valueOf(PaymentBank.XKQT1))))
            {
                if (!PathFile.fileExist("C:\\u\\print\\settvm.txt"))
                {
                    new MessageBox("找不到外卡结账打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\settvm.txt";
                }
            }
            else if ((bld.type.equals(String.valueOf(PaymentBank.XKQT3))))
            {
                if (!PathFile.fileExist("C:\\u\\print\\resettvm.txt"))
                {
                    new MessageBox("找不到外卡结账重打印文件!");

                    return;
                }
                else
                {
                    printName = "C:\\u\\print\\resettvm.txt";
                }
            }
            else
            {
                new MessageBox("此金卡工程操作没有打印文件");

                return;
            }

            pb = new ProgressBox();
            pb.setText("正在打印银联签购单,请等待...");
           
            for (int i = 0; i < 1; i++)
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

                    //
                    String line = null;
                   
                    while ((line = br.readLine()) != null)
                    {
                        if (line.trim().equals("CUT"))
                        {
                            break;
                        }

                        XYKPrintDoc_Print(line);
                    }
                }
                catch (Exception e)
                {
                    new MessageBox(e.getMessage());
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

    public void XYKPrintDoc_Print(String printStr)
    {
        if (onceprint)
        {
            Printer.getDefault().printLine_Normal(printStr);
        }
        else
        {
            printdoc.println(printStr);
        }
    }

    public void XYKPrintDoc_End()
    {
        if (onceprint)
        {
            Printer.getDefault().cutPaper_Normal();
        }
        else
        {
            printdoc.flush();
            printdoc.close();
            printdoc = null;
        }
    }
}
