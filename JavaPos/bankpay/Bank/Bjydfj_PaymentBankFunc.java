package bankpay.Bank;

import java.io.BufferedReader;
import java.io.IOException;
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

public class Bjydfj_PaymentBankFunc extends PaymentBankFunc
{
	public String[] getFuncItem()
	{
		String[] func = new String[4];
		func[0] = "[" + PaymentBank.XYKXF + "]" + "非接消费";
		func[1] = "[" + PaymentBank.XYKTH + "]" + "非接退货";
		func[2] = "[" + PaymentBank.XYKJZ + "]" + "非接结算";
		func[3] = "[" + PaymentBank.XKQT1 + "]" + "非接统计";

		return func;
	}

	public boolean getFuncLabel(int type, String[] grpLabelStr)
	{
		switch (type)
		{
		case PaymentBank.XYKXF: // 消费
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKTH:// 隔日退货
			grpLabelStr[0] = "原凭证号";
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "交易金额";
			break;
		case PaymentBank.XYKJZ: // 银联结账
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "非接结算";
			break;
		case PaymentBank.XKQT1: // 统计
			grpLabelStr[0] = null;
			grpLabelStr[1] = null;
			grpLabelStr[2] = null;
			grpLabelStr[3] = null;
			grpLabelStr[4] = "非接统计";
			break;
		}

		return true;
	}

	public boolean getFuncText(int type, String[] grpTextStr)
	{
		switch (type)
		{
		case PaymentBank.XYKXF: // 消费
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = null;
			break;
		case PaymentBank.XYKTH: // 隔日退货
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始隔日退货 ";
			break;
		case PaymentBank.XYKJZ: // 银联结账
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键开始银联结账";
			break;
		case PaymentBank.XKQT1: // 统计
			grpTextStr[0] = null;
			grpTextStr[1] = null;
			grpTextStr[2] = null;
			grpTextStr[3] = null;
			grpTextStr[4] = "按回车键统计";
			break;
		}

		return true;
	}

	public boolean XYKExecute(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			if ((type != PaymentBank.XYKXF) && (type != PaymentBank.XYKTH) && (type != PaymentBank.XYKJZ) && (type != PaymentBank.XKQT1))
			{
				errmsg = "银联接口不支持该交易";
				new MessageBox(errmsg);

				return false;
			}

			if (PathFile.fileExist(ConfigClass.BankPath + "\\PFACE.TXT"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\PFACE.TXT");

				if (PathFile.fileExist(ConfigClass.BankPath + "\\PFACE.TXT"))
				{
					errmsg = "交易请求文件PFACE.TXT无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			if (PathFile.fileExist(ConfigClass.BankPath + "\\print.txt"))
			{
				PathFile.deletePath(ConfigClass.BankPath + "\\print.txt");

				if (PathFile.fileExist(ConfigClass.BankPath + "\\print.txt"))
				{
					errmsg = "交易请求文件print.txt无法删除,请重试";
					XYKSetError("XX", errmsg);
					new MessageBox(errmsg);
					return false;
				}
			}

			// 写入请求数据
			if (!XYKWriteRequest(type, money, track1, track2, track3, oldseqno, oldauthno, olddate, memo)) { return false; }

			// 读取应答数据
			if (!XYKReadResult()) { return false; }

			// 检查交易是否成功
			XYKCheckRetCode();

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
			XYKSetError("XX", "金卡异常XX:" + ex.getMessage());
			new MessageBox("调用金卡工程处理模块异常!\n\n" + ex.getMessage(), null, false);
			return false;
		}
	}

	public boolean XYKNeedPrintDoc()
	{
		if (!checkBankSucceed()) { return false; }

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

	public boolean XYKWriteRequest(int type, double money, String track1, String track2, String track3, String oldseqno, String oldauthno, String olddate, Vector memo)
	{
		try
		{
			String line = "";
			String type1 = "";

			String jestr = String.valueOf((long) ManipulatePrecision.doubleConvert(money * 100, 2, 1));

			for (int i = jestr.length(); i < 12; i++)
			{
				jestr = "0" + jestr;
			}

			String syjh = Convert.increaseChar(ConfigClass.CashRegisterCode, ' ', 6);

			String syyh = Convert.increaseChar(GlobalInfo.posLogin.gh, ' ', 6);

			// 根据不同的类型生成文本结构
			switch (type)
			{
			case PaymentBank.XYKXF:
				type1 = "28";
				break;
			case PaymentBank.XYKTH:
				type1 = "30";
				break;
			case PaymentBank.XYKJZ:
				type1 = "33";
				break;
			case PaymentBank.XKQT1:
				type1 = "34";
				break;
			}

			line = syjh + " " + syyh + " " + type1 + " " + jestr;

			// 调用接口模块
			if (PathFile.fileExist(ConfigClass.BankPath + "\\Bankmis.exe"))
			{
				CommonMethod.waitForExec(ConfigClass.BankPath + "\\Bankmis.exe " + line);
			}
			else
			{
				new MessageBox("找不到金卡工程模块 Bankmis.exe");
				XYKSetError("XX", "找不到金卡工程模块 Bankmis.exe");
				return false;
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
			if (!PathFile.fileExist(ConfigClass.BankPath + "\\PFACE.TXT") || ((br = CommonMethod.readFileGBK(ConfigClass.BankPath + "\\PFACE.TXT")) == null))
			{
				XYKSetError("XX", "读取金卡工程应答数据失败!");
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}

			String line = br.readLine();

			bld.retcode = Convert.newSubString(line, 0, 2);
			bld.retmsg = Convert.newSubString(line, 2, 42).trim();

			if (bld.retcode.equals("00"))
			{
				bld.bankinfo = Convert.newSubString(line, 42, 44) + XYKReadBankName(Convert.newSubString(line, 42, 44));
				bld.cardno = Convert.newSubString(line, 54, 74).trim();

				String je = Convert.newSubString(line, 74, 86);
				double j = Double.parseDouble(je);
				j = ManipulatePrecision.mul(j, 0.01);
				bld.je = j;
			}
			if (Convert.newSubString(line, 86, 92).length() > 0)
			{
				bld.trace = Convert.toLong(Convert.newSubString(line, 82, 94).trim());
			}

			errmsg = bld.retmsg;
			return true;
		}
		catch (Exception ex)
		{
			XYKSetError("XX", "读取应答XX:" + ex.getMessage());
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
			if (!PathFile.fileExist(ConfigClass.BankPath + "\\PRINT.TXT"))
				return;

			printName = ConfigClass.BankPath + "\\PRINT.TXT";

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
						if (line.trim().equals("@"))
						{
							Printer.getDefault().cutPaper_Normal();
							continue;
						}

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

}
