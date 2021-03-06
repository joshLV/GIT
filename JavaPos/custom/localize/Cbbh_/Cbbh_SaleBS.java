package custom.localize.Cbbh;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
import com.efuture.commonKit.TextBox;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.CashBox;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.AccessDayDB;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Global.LoadSysInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Global.StatusType;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.Plugin.EBill.EBill;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperRoleDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;
import com.swtdesigner.SWTResourceManager;

import custom.localize.Bcrm.Bcrm_DataService;

public class Cbbh_SaleBS extends Cbbh_THHNew_SaleBS//Cbbh_THH_SaleBS
{
	private boolean isCard = true;//控制一张单只允许有一种返劵规则，是否先刷会员卡

	private boolean isKdBack;//判断是普通退货，还是开单退货
	
//	public static boolean sendBack = true;;//判断此次退货是否调用付款促销
	
	public static Vector crmpopgoodsdetail = new Vector();//新促销商品明细
	
	public static Vector poplist = new Vector();//整单可选促销列表
	
	public static Vector addSalePay = new Vector();//家电退货需要自动添加的付款方式

	public static String jfjcode="1001";//积分劵id
	public static String cxjcode="1002";//促销劵id
	public static String dzkcode="1004";//店长卡id(家电)
	public static String lbkcode="1005";//品类卡id(家电)
	public static String ppkcode="1006";//品牌卡id(家电)
	
	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
    {
    	try
    	{
    		brokenAssistant.insertElementAt(String.valueOf(salePayUnique), 0);
        	
            s.writeObject(saleHead);
            s.writeObject(saleGoods);
            s.writeObject(goodsAssistant);
            s.writeObject(goodsSpare);
            s.writeObject(brokenAssistant);
            s.writeObject(curGrant);
            s.writeObject(curCustomer);
            s.writeObject(curyyygz);
            s.writeObject(cursqkh);
            //s.writeObject(memoPayment);
    		s.writeObject(new Character(cursqktype));
    		s.writeObject(new Double(cursqkzkfd));
    		s.writeObject(thSyjh);
    		s.writeObject(new Long(thFphm));
    		s.writeObject(new Boolean (isbackticket));
    		s.writeObject(checkdjbh);
    		s.writeObject(salePayment);
    		s.writeObject(crmpopgoodsdetail);//新增新促销
    		s.writeObject(crmPop);
    		s.writeObject(poplist);
    		s.writeObject(addSalePay);
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    	
    }
	
	public void readStreamToSellObject(ObjectInputStream s) throws Exception
    {
    	try
    	{
        	// 读取
            SaleHeadDef saleHead1 = (SaleHeadDef) s.readObject();
            Vector saleGoods1 = (Vector) s.readObject();
            Vector goods1 = (Vector) s.readObject();
            Vector spare1 = (Vector) s.readObject();
            Vector brokenAssistant1 = (Vector) s.readObject();
            OperRoleDef curGrant1 = (OperRoleDef) s.readObject();
            CustomerDef curCustomer1 = (CustomerDef) s.readObject();
            String curyyygz1 = (String) s.readObject();
            String cursqkh1 = (String) s.readObject();
            //Vector memoPayment1 = (Vector)s.readObject();
    		Character cursqktype1 = (Character) s.readObject();
    		Double cursqkzkfd1 = (Double) s.readObject();
    		String thSyjh1 = (String)s.readObject();
    		Long thFphm1 = (Long)s.readObject();  
    		Boolean isbackticket1 = (Boolean)s.readObject();
    		String checkdjbh1 = (String)s.readObject();
    		Vector salePayment1 = (Vector) s.readObject();
    		
    		Vector cpd = (Vector)s.readObject();
    		Vector crmPop1 =(Vector)s.readObject();
    		Vector poplist1 =(Vector)s.readObject();
    		Vector addSalePay1 =(Vector)s.readObject();
    		
    		// 赋对象
        	saleHead = saleHead1;
        	saleGoods = saleGoods1;
        	goodsAssistant = goods1;
        	goodsSpare = spare1;
        	brokenAssistant = brokenAssistant1;
        	curGrant = curGrant1;
        	curCustomer = curCustomer1;
        	curyyygz = curyyygz1;
        	cursqkh = cursqkh1;
        	//memoPayment = memoPayment1;
        	cursqktype = cursqktype1.charValue();
        	cursqkzkfd = cursqkzkfd1.doubleValue();
        	thSyjh = thSyjh1;
        	thFphm  = thFphm1.longValue();	
        	isbackticket = isbackticket1.booleanValue();
        	checkdjbh = checkdjbh1;
        	salePayment = salePayment1;
        	
        	salePayUnique = Convert.toInt(brokenAssistant.remove(0));
        	
//        	crmpopgoodsdetail.clear();//新增新促销
//        	crmpopgoodsdetail = cpd;
        	saveinoutgoods(cpd);
        	
        	crmPop = crmPop1;
        	poplist = poplist1;
        	addSalePay =addSalePay1;
    	}
    	catch(Exception ex)
    	{
    		PosLog.getLog(this.getClass().getSimpleName()).error(ex);
    	}
    }
	
	public void rePrint()
	{
		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;

		// 盘点
		if (SellType.ISCHECKINPUT(saletype))
		{
			if (saleGoods == null || saleGoods.size() <= 0) return;

			if (!CheckGoodsMode.getDefault().isLoad()) return;

			MessageBox me = new MessageBox(Language.apply("你确实要打印盘点小票吗?"), null, true);

			if (me.verify() != GlobalVar.Key1) return;

			CheckGoodsMode.getDefault().setTemplateObject(saleHead, saleGoods, salePayment);

			CheckGoodsMode.getDefault().printBill();

			return;
		}

		if (GlobalInfo.syjDef.printfs == '1' && saleGoods != null && saleGoods.size() > 0)
		{
			new MessageBox(Language.apply("当前打印为即扫即打并且已有商品交易,不能重打!"), null, false);

			return;
		}

		// 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }

		//MessageBox me = new MessageBox("你确实要重印上一张小票吗?", null, true);
		try
		{
			if (getReprintAuth())//(me.verify() == GlobalVar.Key1 && getReprintAuth())
			{
				//Object obj = null;
				String fphm = null;
				String syjh = ConfigClass.CashRegisterCode;

				if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
				{
					OperUserDef user = null;
					if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
					{
						if (user.privdy != 'Y' && user.privdy != 'L')
						{
							new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

							return;
						}

						String log = "授权重打印上一笔小票,授权工号:" + user.gh;
						AccessDayDB.getDefault().writeWorkLog(log);
					}
					else
					{
						return;
					}
				}

				//新增默认上一笔的小票号、收银机号 wangyong by 2013.9.3
				RetSYJForm frm = new RetSYJForm();
				int done = frm.open(null, -1, Language.apply("请输入【重印】收银机号和小票号"));
				if (done == frm.Done)
				{
					syjh = RetSYJForm.syj;
					fphm = String.valueOf(RetSYJForm.fph);
				}
				else
				{
					// 放弃重打印
					return;
				}

				//if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + syjh + "'")) != null)
				//{
				Sqldb db = getPrintSqlDB(syjh, fphm);
				if (db==null)
				{
					new MessageBox(Language.apply("没有查询到小票,不能打印!"));
					return;
				}
				try
				{
					//fphm = String.valueOf(obj);

					if ((rs = db.selectData("select * from salehead where syjh = '" + syjh + "' and  fphm = " + fphm)) != null)
					{

						if (!rs.next())
						{
							new MessageBox(Language.apply("没有查询到小票头,不能打印!"));
							return;
						}

						saleheadprint = new SaleHeadDef();

						if (!db.getResultSetToObject(saleheadprint)) { return; }
					}
					else
					{
						new MessageBox(Language.apply("查询小票头失败!"), null, false);
						return;
					}
				}
				catch (Exception ex)
				{
					new MessageBox(Language.apply("查询小票头出现异常!"), null, false);
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
					return;
				}
				finally
				{
					GlobalInfo.dayDB.resultSetClose();
				}

				try
				{
					if ((rs = db.selectData("select * from SALEGOODS where syjh = '" + syjh + "' and fphm = " + fphm
							+ " order by rowno")) != null)
					{
						boolean ret = false;
						salegoodsprint = new Vector();
						while (rs.next())
						{
							SaleGoodsDef sg = new SaleGoodsDef();

							if (!db.getResultSetToObject(sg)) { return; }

							salegoodsprint.add(sg);
							saleheadprint.yfphm = String.valueOf(sg.yfphm);//wangyong add by 2013.10.16 记录原小票信息,否则打印不了
							saleheadprint.ysyjh = sg.ysyjh;

							ret = true;
						}

						if (!ret)
						{
							new MessageBox(Language.apply("没有查询到小票明细,不能打印!"));
							return;
						}
					}
					else
					{
						new MessageBox(Language.apply("查询小票明细失败!"), null, false);
						return;
					}
				}
				catch (Exception ex)
				{
					new MessageBox(Language.apply("查询小票明细出现异常!"), null, false);
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
					return;
				}
				finally
				{
					db.resultSetClose();
				}

				try
				{
					if ((rs = db.selectData("select * from SALEPAY where syjh = '" + syjh + "' and fphm = " + fphm + " order by rowno")) != null)
					{
						boolean ret = false;
						salepayprint = new Vector();
						while (rs.next())
						{
							SalePayDef sp = new SalePayDef();

							if (!db.getResultSetToObject(sp)) { return; }

							salepayprint.add(sp);

							ret = true;
						}
						if (!ret)
						{
							new MessageBox(Language.apply("没有查询到付款明细,不能打印!"));
							return;
						}
					}
					else
					{
						new MessageBox(Language.apply("查询付款明细失败!"), null, false);
						return;
					}
				}
				catch (Exception ex)
				{
					new MessageBox(Language.apply("查询付款明细出现异常!"), null, false);
					PosLog.getLog(this.getClass().getSimpleName()).error(ex);
					return;
				}
				finally
				{
					db.resultSetClose();
				}

				saleheadprint.printnum++;
				AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm),
						String.valueOf(saleheadprint.printnum));
				ProgressBox pb = new ProgressBox();
				pb.setText(Language.apply("现在正在重打印小票,请等待....."));
				try
				{
					printSaleTicket(saleheadprint, salegoodsprint, salepayprint, false);
				}
				finally
				{
					pb.close();
				}
				//}
				//else
				//{
				//new MessageBox("当前没有销售数据,不能打印!");
				//}
			}
		}
		finally
		{
			saleheadprint = null;

			if (salegoodsprint != null)
			{
				salegoodsprint.clear();
				salegoodsprint = null;
			}

			if (salepayprint != null)
			{
				salepayprint.clear();
				salepayprint = null;
			}
		}
	}

	protected Sqldb getPrintSqlDB(String syjh, String fphm)
	{
		Sqldb db = null;
		ProgressBox pb = null;
		try
		{
			pb = new ProgressBox();
			pb.setText(Language.apply("正在查找小票, 请稍等..."));

			String path = ConfigClass.LocalDBPath + "Invoice";//"C:\\JavaPOS\\javaPos.Database\\Invoice";

			File dir = new File(path);
			File[] fileList = dir.listFiles();
			String dbpath;
			File f;
			for (int j = fileList.length - 1; j >= 0; j--)
			{
				if (!fileList[j].isDirectory()) continue;

				//检查文件是否存在
				dbpath = ConfigClass.LocalDBPath + "Invoice/" + fileList[j].getName().trim() + "/" + LoadSysInfo.getDefault().getDayDBName();
				f = new File(dbpath);
				if (!f.exists()) continue;

				//获取数据源
				db = LoadSysInfo.getDefault().loadDayDB(ManipulateDateTime.getConversionDate(fileList[j].getName().trim()));

				//检查是否存在小票
				if (isExistsSale(db, syjh, fphm))
				{
					//存在,退出查找
					db.resultSetClose();
					PosLog.getLog(this.getClass().getSimpleName()).info("找到小票: syjh=[" + syjh + "],fphm=[" + fphm + "],date=[" +  fileList[j].getName().trim()+ "].");
					break;
				}
				else
				{
					//不存在,继续查找
					db = null;
					PosLog.getLog(this.getClass().getSimpleName()).info("未找到小票: syjh=[" + syjh + "],fphm=[" + fphm + "].");
					continue;
				}
			}
		}
		catch (Exception ex)
		{
			db = null;
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		finally
		{
			if (pb != null)
			{
				pb.close();
				pb = null;
			}
		}
		return db;
	}


	//检查是否存在小票号
	protected boolean isExistsSale(Sqldb db, String syjh, String fphm)
	{
		boolean blnRet = false;
		try
		{
			if (db == null) return false;

			ResultSet rs = null;
			if ((rs = db.selectData("select * from salehead where syjh = '" + syjh + "' and  fphm = " + fphm)) != null)
			{

				if (!rs.next())
				{
					//new MessageBox("没有查询到小票头,不能打印!");
					return false;
				}
				return true;
			}
			else
			{
				//new MessageBox("查询小票头失败!", null, false);
				return false;
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(this.getClass().getSimpleName()).error(ex);
		}
		return blnRet;
	}

	public void enterInputCODE()
	{
		if (saleEvent.code.getText().trim().length() <= 0)
		{
			NewKeyListener.sendKey(GlobalVar.Pay);
		}
		else
		{
			super.enterInputCODE();
		}

	}

	// 是否已经使用了新的指定小票退货
	public boolean isNewUseSpecifyTicketBack(boolean display)
	{
		if (((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && SellType.ISBACK(saletype) && isbackticket) && saleEvent.code.getText().trim().length() <= 0)
		{
			//不知道什么用处，先屏蔽--maxun 2014年12月29日16:57:39
			//NewKeyListener.sendKey(GlobalVar.Pay);
			return false;
		}

		return super.isNewUseSpecifyTicketBack(display);
	}

	public void enterInputYYY()
	{
		if(((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && SellType.ISBACK(saletype) && isbackticket) && saleEvent.code.getText().trim().length() <= 0 && saleEvent.yyyh.getText().trim().length() <= 0)
		{
			NewKeyListener.sendKey(GlobalVar.Pay);
		}
		else
		{
			super.enterInputYYY();
		}
	}
	public boolean memberGrant()
	{

		boolean blnRet = false;

		if (isPreTakeStatus())
		{
			new MessageBox(Language.apply("预售提货状态下不允许重新刷卡"));
			return false;
		}

		// 会员卡必须在商品输入前,则输入了商品以后不能刷卡,指定小票除外
		if (GlobalInfo.sysPara.customvsgoods == 'A' && saleGoods.size() > 0 && !isNewUseSpecifyTicketBack(false))
		{
			new MessageBox(Language.apply("必须在输入商品前进行刷会员卡\n\n请把商品清除后再重刷卡"));
			return false;
		}

		// 读取会员卡
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		String track2;// = bs.readMemberCard();
		/*if(GlobalInfo.sysPara.isUseBankReadTrack == 'Y')
		{
			track2 = getTrackByBank();
		}
		else
		{
			track2 = bs.readMemberCard();
		}*/
		track2 = bs.readMemberCard();
		if (track2 == null || track2.equals(""))
			return false;

		// 查找会员卡
		CustomerDef cust = bs.findMemberCard(track2);

		if (cust == null)
			return false;

		wirteHyJoint(bs.getCustTrack());//处理联名卡

		// 调出原交易的指定小票退货模式允许重新刷卡改变当前会员卡(原卡可能失效、换卡等情况)
		if (isNewUseSpecifyTicketBack(false))
		{
			// 指定小票退仅记录卡号,不执行商品重算等处理
			curCustomer = cust;
			saleHead.hykh = cust.code;
			saleHead.hytype = cust.type;
			saleHead.str4 = cust.valstr2;
			saleHead.hymaxdate = cust.maxdate;
			blnRet =  true;
		}
		else
		{
			// 记录会员卡
			blnRet =  memberGrantFinish(cust);
		}



		if(blnRet)
		{
			//查询会员电子劵信息
			Vector cardinfo = ((Cbbh_NetService)NetService.getDefault()).getCardInfo(saleHead.hykh);
			StringBuffer cardinfomsg = new StringBuffer();
			if(cardinfo != null && cardinfo.size() > 0)
			{
				for(int i =0;i<cardinfo.size();i++)
				{
					String[] row = (String[]) cardinfo.elementAt(i);
					cardinfomsg.append("劵名称："+row[8]+" 金额："+String.valueOf(Convert.toDouble(row[9])*Convert.toDouble(row[10]))+" "+row[6]+"\n");
				}
				
				if(cust==null)
				{
					cust.memo = "YY ";
				}
			}
			
			//根据返回值提示 for cbbh
			if(cust!=null && cust.memo!=null && cust.memo.length()>2 && cust.memo.charAt(1)=='Y')
			{
				//new MessageBox(cust.memo.substring(2,cust.memo.length())+"\n----------卡电子劵信息----------\n"+cardinfomsg.toString());				
			}
			if(cust.memo==null) cust.memo="";
			if(cust.memo.length() > 2)
			{
				new MessageBox(cust.memo.substring(2,cust.memo.length())+"\n----------卡电子劵信息----------\n"+cardinfomsg.toString());
			}
			

			if(!GlobalInfo.sysPara.isnewpop.equals("Y"))
			{
				//遍历查找已录入商品的CRM促销
				calcMenberCrmPop();
			}
			return true;
		}
		return false;
	}

	public void getVIPZK(int index, int type)
	{
		if(GlobalInfo.sysPara.isnewpop.equals("Y"))return;
		
		boolean zszflag = true;
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		GoodsPopDef popDef = (GoodsPopDef) crmPop.elementAt(index);
		SpareInfoDef info = (SpareInfoDef) goodsSpare.elementAt(index);

		// 指定小票退货时不重算优惠价和会员价
		if (isSpecifyBack(saleGoodsDef)) { return; }

		// 积分换购商品不计算会员打折
		if (info.char2 == 'Y') { return; }

		if (curCustomer == null || (curCustomer != null && curCustomer.iszk != 'Y')) return;

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }

		if (SellType.ISCOUPON(saletype)) { return; }

		if (SellType.ISJFSALE(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) { return; }

		// 不为VIP折扣的商品不重新计算会员折扣额
		if (goodsDef.isvipzk == 'N') return;

		// 折扣门槛
		if (saleGoodsDef.hjje == 0
				|| ManipulatePrecision
				.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr)
						/ saleGoodsDef.hjje) < GlobalInfo.sysPara.vipzklimit) return;

		// 商品会员促销价
		if (popDef.jsrq != null && popDef.jsrq.length() > 0 && popDef.jsrq.split(",").length >= 5 && type == vipzk1)
		{
			// 商品会员价促销单号,商品促销价，限量数量 ，已享受数量，积分方式（0:正常积分 ,1:不积分 2:特价积分） 
			String[] arg = popDef.jsrq.split(",");

			double price = Convert.toDouble(arg[1]);
			double max = Convert.toDouble(arg[2]);
			double used = Convert.toDouble(arg[3]);

			// 限量
			boolean isprice = false;
			if (max > 0)
			{
				double q = 0;
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef saleGoodsDef1 = (SaleGoodsDef) saleGoods.elementAt(i);
					SpareInfoDef info1 = (SpareInfoDef) goodsSpare.elementAt(i);

					if (i == index) continue;

					if (saleGoodsDef1.code.equals(saleGoodsDef.code) && info1.char1 == 'Y')
					{
						q += saleGoodsDef1.sl;
					}
				}

				if (ManipulatePrecision.doubleConvert(max - used - q) > 0)
				{
					if (ManipulatePrecision.doubleConvert(saleGoodsDef.sl) > ManipulatePrecision.doubleConvert(max - used - q))
					{
//						new MessageBox("此商品存在促销价，但是商品数量[" + saleGoodsDef.sl + "]超出数量限额【" + ManipulatePrecision.doubleConvert(max - used - q)
//						+ "】\n 强制将商品数量修改为【" + ManipulatePrecision.doubleConvert(max - used - q) + "】参与促销价");
						new MessageBox(Language.apply("此商品存在促销价，但是商品数量[{0}]超出数量限额【{1}】\n 强制将商品数量修改为【{2}】参与促销价" ,new Object[]{saleGoodsDef.sl+"" ,ManipulatePrecision.doubleConvert(max - used - q)+"" ,ManipulatePrecision.doubleConvert(max - used - q)+""}));
						saleGoodsDef.sl = ManipulatePrecision.doubleConvert(max - used - q);
						saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.sl * saleGoodsDef.jg, 2, 1);
						calcGoodsYsje(index);
					}
					isprice = true;
				}
			}
			else
			{
				isprice = true;
			}

			if (isprice == true)
			{
				saleGoodsDef.hyzke = 0;
				saleGoodsDef.yhzke = 0;
				saleGoodsDef.lszke = 0;
				saleGoodsDef.lszre = 0;
				saleGoodsDef.lszzk = 0;
				saleGoodsDef.lszzr = 0;

				if (info.str1.length() > 1 && info.str1.charAt(0) == '9')
				{
					StringBuffer buff = new StringBuffer(info.str1);
					for (int z = 1; z < buff.length(); z++)
					{
						buff.setCharAt(z, '0');
					}
					info.str1 = buff.toString();
				}
				else
				{
					info.str1 = "0000";
				}
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((saleGoodsDef.jg - price) * saleGoodsDef.sl);
				saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
				saleGoodsDef.str1 = popDef.jsrq;
				info.char1 = 'Y';
			}
		}

		// 已计算了商品会员促销价，不再继续VIP折扣
		if (info.char1 == 'Y') return;

		if (goodsDef.isvipzk == 'Y')
		{
			// 开始计算VIP折扣
			saleGoodsDef.hyzke = 0;
			saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		}

		// 判断促销单是否允许折上折
		if (goodsDef.pophyjzkl % 10 >= 1) zszflag = zszflag && true;
		else zszflag = zszflag && false;

		//是否进行VIP打折,通过CRM促销控制
		boolean vipzk = false;

		/*//无CRM促销，以分期促销折上折标志为准
		if (popDef.yhspace == 0)
		{
			vipzk = true;
		}
		else
		//存在CRM促销
		{
			//不享用VIP折扣,不进行VIP打折
			if (popDef.pophyjzkl == 0)
			{
				vipzk = false;
			}
			else
			//享用VIP折扣，进行VIP折上折
			{
				vipzk = true;
				zszflag = zszflag && true;
			}
		}*/
		vipzk = true;//重百：只要有折扣率，则就打折（（单品折后价*折扣率），且是折上折
		zszflag=true;//wangyong add by 2014.3.19 for cbbh yuanjun
		GlobalInfo.sysPara.vipPromotionCrm = "1";//重百为实时体现

		if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'H' && type == vipzk1)
		{
			double je = ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - getZZK(saleGoodsDef)));
			double hyj = 0;
			if (goodsDef.pophyj != 0)
			{
				hyj = goodsDef.pophyj;
			}

			if (goodsDef.hyj != 0)
			{
				if (hyj == 0) hyj = goodsDef.hyj;
				else hyj = Math.min(hyj, goodsDef.hyj);
			}

			if (hyj != 0 && je > ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl))
			{
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(je - ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl));
				saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
			}

		}
		//存在会员卡， 商品允许VIP折扣， CRM促销单允许享用VIP折扣
		else if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'Y' && vipzk && curCustomer.iszk == 'Y')
		{
			// 获取VIP折扣率定义
			calcVIPZK(index);

			// 折上折标志
			zszflag = zszflag && (goodsDef.num4 == 1);

			// 不计算会员卡折扣
			if (goodsDef.hyj == 1 || goodsDef.hyj == 0) return;//wangyong add hyj=0 2014.4.15 否则退货时为价格0了

			// vipzk1 = 输入商品时计算商品VIP折扣,原VIP折上折模式
			if (type == vipzk1 && (GlobalInfo.sysPara.vipPromotionCrm == null || GlobalInfo.sysPara.vipPromotionCrm.equals("1")))
			{
				//有折扣,进行折上折
				if (getZZK(saleGoodsDef) >= 0.01 && goodsDef.hyj < 1.00)
				{
					// 需要折上折
					if (zszflag)
					{
						saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef)), 2, 1);
					}
					else
					{
						// 商品不折上折时，取商品的hyj和综合折扣较低者
						if (ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), goodsDef.hyj * saleGoodsDef.hjje, 2) > 0)
						{
							double zke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
							if (zke > getZZK(saleGoodsDef))
							{
								saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(zke - getZZK(saleGoodsDef), 2, 1);
							}
						}
					}
				}
				else
				{
					//无折扣,按商品缺省会员折扣打折
					saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
				}
			}
			else // vipzk2 = 按下付款键时计算商品VIP折扣,起点折扣计算模式 
				if (type == vipzk2 && GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
				{
					// VIP折扣要除券付款
					double fte = 0;
					if (GlobalInfo.sysPara.vipPayExcp == 'Y') fte = getGoodsftje(index);

					double vipzsz = 0;

					// 直接在以以后折扣的基础上打商品定义的VIP会员折扣率
					if (GlobalInfo.sysPara.vipCalcType.equals("2"))
					{
						vipzsz = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
					}
					else if (GlobalInfo.sysPara.vipCalcType.equals("1"))
					{
						// 当前折扣如果高于门槛则还可以进行VIP折上折,否则VIP不能折上折
						if (getZZK(saleGoodsDef) > 0
								&& zszflag
								&& ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), saleGoodsDef.hjje * curCustomer.value3, 2) >= 0)
						{
							vipzsz = ManipulatePrecision.doubleConvert((1 - curCustomer.zkl) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
						}

						// 如果VIP折上折以后的成交价 高于 该商品定义的VIP会员折扣率，则商品以商品定义的折扣执行VIP折
						double spvipcjj = ManipulatePrecision.doubleConvert(goodsDef.hyj * (saleGoodsDef.hjje - fte), 2, 1);
						if (ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - vipzsz, 2, 1) > spvipcjj)
						{
							vipzsz = ManipulatePrecision.doubleConvert(saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte - spvipcjj);
						}
					}

					saleGoodsDef.hyzke = vipzsz;
				}

			// 按价格精度计算折扣
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}

		getZZK(saleGoodsDef);
	}

	public void calcVIPZK(int index)
	{
		//不作任何处理

		/*// 未刷卡
		if (!checkMemberSale() || curCustomer == null) return;

		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);
		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}

		goodsDef.hyj = 0.9;//test
		goodsDef.num4 = 1;*/
	}

	/*public void backSell()
	{
		super.backSell();
	}*/

	protected void wirteHyJoint(String[] track)
	{
		try
		{
			/**
			 * track2前16位不包含'=',并且track3有值，则表示是联名会员卡号；
				对于联名会员卡，则要将将二轨信息前的16位（去除二轨前面的字母之后的二轨）记到salehead.jdfhdd，传到业务系统（其它会员卡不作此处理）
				查询会员卡时，传整个二轨道信息（不截取前面的字母）
			 */
			saleHead.jdfhdd = "";
			if(track.length>=3 && track[2].length()>0)
			{
				String track2New="";
				int track2Len = 0;
				char chr=' ';
				//存在三轨，则表示是联名卡
				for(int i=0; i<track[1].length(); i++)
				{
					chr=track[1].charAt(i);
					if(!Convert.isLetter(chr))
					{
						track2New = track2New + String.valueOf(chr);
						track2Len++;
					}
					if(track2Len>=16) break;
				}
				saleHead.jdfhdd = track2New;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}	

	protected void calcMenberCrmPop()
	{
		if (saleGoods.size() > 0)
		{
			ProgressBox pb = null;
			try
			{

				isCard = false;

				pb = new ProgressBox();
				pb.setText("正在查找CRM促销信息,请等待...");
				if(crmPop!=null) crmPop.removeAllElements();

				for (int i = 0; i < saleGoods.size(); i++)
				{	
					// 查找CRM促销
					findGoodsCRMPop((SaleGoodsDef)saleGoods.elementAt(i), 
							(GoodsDef)goodsAssistant.elementAt(i), 
							(SpareInfoDef)goodsSpare.elementAt(i));
					calcGoodsYsje(i);
				}

				//控制一张单只允许有一种返劵规则，后刷卡
				if(!isCard)
				{
					isCard = true;
					for (int i = 0; i < saleGoods.size(); i++) {
						SaleGoodsDef gds = (SaleGoodsDef) saleGoods.elementAt(i);

						String gdsmemo = gds.str3.split(";")[2];
						if(gdsmemo.length() > 0)
						{
							gdsmemo = gdsmemo.split(",")[1];
						}
						else
						{
							continue;
						}
						if(saleHead.str10.length() <= 0)
						{
							saleHead.str10 = gdsmemo;//记录商品有效返劵规则
						}
						else if(saleHead.str10.length() > 0 && !saleHead.str10.equals(gdsmemo) && gdsmemo.length() > 0)
						{
							new MessageBox("本比小票有不同返劵的商品，请取消本小票，分单处理！");
							saleHead.ismemo = false;
							return;
						}
					}

					for (int i = 0; i < saleGoods.size(); i++) {
						SaleGoodsDef gds = (SaleGoodsDef) saleGoods.elementAt(i);

						String gdsmemo = gds.str3.split(";")[2];
						if(gdsmemo.length() > 3)
						{
							gdsmemo = gdsmemo.split(",")[2];
						}
						else
						{
							continue;
						}
						if( (saleHead.str9 == null) || saleHead.str9.length() <= 3)
						{
							saleHead.str9 = gdsmemo;//记录商品有效返劵规则
						}
						else if(saleHead.str9.length() > 0 && !saleHead.str9.equals(gdsmemo) && gdsmemo.length() > 3)
						{
							new MessageBox("本比小票有不同满赠的商品，请取消本小票，分单处理！");
							saleHead.ismemo = false;
							return;
						}
					}
				}

				// 计算小票应收
				calcHeadYsje();
			}
			catch(Exception ex)
			{
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

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		try
		{
			if(GlobalInfo.sysPara.isnewpop.equals("Y")) return ;
			GlobalInfo.sysPara.iscrmtjprice = 'N';
			if ((curCustomer != null && curCustomer.func!=null && curCustomer.func.length()>=2))
			{
				//func.charAt(1)!=N表示找商品后则查找促销规则，否则不取
				if(curCustomer.func!=null && curCustomer.func.length()>1 && curCustomer.func.charAt(1) != 'N')
				{				
				}
				else
				{
					crmPop.add(new GoodsPopDef());
					info.str1 = "0000";
					return;
				}			
			}

			if(curCustomer == null && GlobalInfo.sysPara.noCustFindPop=='N')
			{
				//未刷会员卡时是否查找规则促销CRM
				crmPop.add(new GoodsPopDef());
				info.str1 = "0000";
				return;
			}

			//super.findGoodsCRMPop(sg, goods, info);

			String newyhsp = "90000000";
			String cardno = null;
			String cardtype = null;

			if ((curCustomer != null && curCustomer.iszk == 'Y'))
			{
				cardno = curCustomer.code;
				cardtype = curCustomer.type;
			}

			GoodsPopDef popDef = new GoodsPopDef();

			// 非促销商品 或者在换、退货时，不查找促销信息
			if(SellType.ISBACK(saletype) || SellType.ISHH(saletype))
			{
				crmPop.add(new GoodsPopDef());
				info.str1 = "0000";
				return;
			}
			String rqsj = saleHead.rqsj;
			//换销时，rqsj为退货原始小票的成交时间
			if(SellType.ISSALE(saletype) && hhflag == 'Y')
			{
				//取原小票交易时间
				//rqsj = "";
			}
			((Bcrm_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.barcode + "|" + sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode,
					rqsj, cardno, cardtype, saletype);

			if (popDef.yhspace == 0)// || hhflag == 'Y'
			{
				popDef.yhspace = 0;
				popDef.memo = "";
				popDef.poppfjzkfd = 1;
			}

			//将收券规则放入GOODSDEF 列表
			goods.memo = popDef.str2;
			goods.num1 = popDef.num1;
			goods.num2 = popDef.num2;
			goods.str4 = popDef.mode;
			info.char3 = popDef.type;

			//重百会员折扣率（折上折） add by 2014.3.19
			goods.hyj = popDef.num7;
			goods.num4 = 1;

			// 促销联比例
			sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
			goods.xxtax = Convert.toDouble(popDef.ksrq);
			if (goods.memo == null) goods.memo = "";

			// 增加CRM促销信息
			crmPop.add(popDef);

			// 标志是否为9开头扩展的控制
			boolean append = false;
			// 无促销,此会员不允许促销
			if (popDef.yhspace == 0)
			{
				append = false;
				info.str1 = "0000";
			}
			else if (popDef.yhspace == Integer.parseInt(newyhsp))
			{
				append = true;
				info.str1 = newyhsp;
			}
			else
			{

				if (String.valueOf(popDef.yhspace).charAt(0) != '9')
				{
					if (GlobalInfo.sysPara.iscrmtjprice == 'Y') info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
					else {
						//info.str1 = Convert.increaseInt(popDef.yhspace, 4);//old code
						//wangyong modify by 2014.3.13
						//1是，0否
						//重百CRM为：满减，返券，返礼，是否特价
						//而百货CRM为：打折，满减，返券，返礼
						//所以，转换为百货CRM模式处理
						info.str1 = Convert.increaseInt(popDef.yhspace, 4).substring(0, 3);//重百的最后一位为'是否特价'，且没有用，所以取前三位
						info.str1 = "0" + info.str1;//然后左补0（百货CRM处理模式：打折，满减，返券，返礼）					
					}

					append = false;
				}
				else
				{
					info.str1 = String.valueOf(popDef.yhspace);

					append = true;
				}
				//询问参加活动类型 满减或者满增
				String yh = info.str1;

				if (append) yh = yh.substring(1);

				StringBuffer buff = new StringBuffer(yh);
				Vector contents = new Vector();

				for (int i = 0; i < buff.length(); i++)
				{
					// 2-任选促销/1-存在促销/0-无促销
					if (buff.charAt(i) == '2')
					{
						if (i == 0)
						{
							contents.add(new String[] { "D", Language.apply("参与打折促销活动"), "0" });
						}
						else if (i == 1)
						{
							contents.add(new String[] { "J", Language.apply("参与减现促销活动"), "1" });
						}
						else if (i == 2)
						{
							contents.add(new String[] { "Q", Language.apply("参与返券促销活动"), "2" });
						}
						else if (i == 3)
						{
							contents.add(new String[] { "Z", Language.apply("参与赠品促销活动"), "3" });
						}
						else if (i == 5)
						{
							contents.add(new String[] { "F", Language.apply("参与积分活动"), "5" });
						}
					}
				}

				if (contents.size() <= 1)
				{
					if (contents.size() > 0)
					{
						String[] row = (String[]) contents.elementAt(0);
						int i = Integer.parseInt(row[2]);
						buff.setCharAt(i, '1');
					}
				}
				else
				{
					String[] title = { Language.apply("代码"), Language.apply("描述") };
					int[] width = { 60, 400 };
					int choice = new MutiSelectForm().open(Language.apply("请选择参与满减满赠活动的规则"), title, width, contents);

					for (int i = 0; i < contents.size(); i++)
					{
						if (i != choice)
						{
							String[] row = (String[]) contents.elementAt(i);
							int j = Integer.parseInt(row[2]);
							buff.setCharAt(j, '0');
						}
						else
						{
							String[] row = (String[]) contents.elementAt(i);
							int j = Integer.parseInt(row[2]);
							buff.setCharAt(j, '1');
						}
					}
				}

				if (append) info.str1 = "9" + buff.toString();
				else info.str1 = buff.toString();
			}

			String line = "";

			String yh = info.str1;
			if (append) yh = info.str1.substring(1);

			if (yh.charAt(0) != '0')
			{
				line += "D";
			}

			if (yh.charAt(1) != '0')
			{
				line += "J";
			}

			if (yh.charAt(2) != '0')
			{
				line += "Q";
			}

			if (yh.charAt(3) != '0')
			{
				line += "Z";
			}

			if (yh.length() > 5 && yh.charAt(5) != '0')
			{
				line += "F";
			}

			if (line.length() > 0)
			{
				sg.name = "(" + line + ")" + sg.name;
			}

			if (!append)
			{
				// str3记录促销组合码
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y') sg.str3 = info.str1 + String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
				else sg.str3 = info.str1;
			}
			else
			{
				sg.str3 = info.str1;
			}
			// 将商品属性码,促销规则加入SaleGoodsDef里
			sg.str3 += (";" + goods.specinfo);
			sg.str3 += (";" + popDef.memo);
			sg.str3 += (";" + popDef.poppfjzkl);
			sg.str3 += (";" + popDef.poppfjzkfd);
			sg.str3 += (";" + popDef.poppfj);

			// 只有找到了规则促销单，就记录到小票
			if (!info.str1.equals("0000") || !info.str1.equals(newyhsp))
			{
				sg.zsdjbh = popDef.djbh;
				sg.zszkfd = popDef.poplsjzkfd;
			}


			if(saleHead.hykh.length() > 0 && isCard)
			{	
				//控制一张单只允许有一种返劵规则，先刷卡			
				if(saleHead.str10.length() <= 0 && popDef.memo.split(",")[1].length() >0)
				{
					saleHead.str10 = popDef.memo.split(",")[1];//记录商品有效返劵规则
				}
				else if(saleHead.str10.length() > 0 && !saleHead.str10.equals(popDef.memo.split(",")[1]) && popDef.memo.split(",")[1].length() >0)
				{
					//删掉规则不一样的商品
					delSaleGoodsObject(saleGoods.size()-1);
					new MessageBox("一张小票只允许有同一种返劵规则！");
				}

				//控制一张单只允许有一种满赠规则，先刷卡
				if((saleHead.str9 == null) || saleHead.str9.length() <= 0 && (popDef.memo.split(",")[2].length() > 3 && !popDef.memo.split(",")[2].equals("#-#")))
				{
					saleHead.str9 = popDef.memo.split(",")[2];//记录商品有效返劵规则
				}
				else if(saleHead.str9.length() > 0 && !saleHead.str9.equals(popDef.memo.split(",")[2]) && (popDef.memo.split(",")[2].length() > 3 && !popDef.memo.split(",")[2].equals("#-#")))
				{
					//删掉规则不一样的商品
					delSaleGoodsObject(saleGoods.size()-1);
					new MessageBox("一张小票只允许有同一种满赠规则！");
				}
			}

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	/*public boolean doCrmPop()
	{
		return false;
	}*/

	/**
	 * 自定义快捷键
	 */
	public void execCustomKey1(boolean keydownonsale)
	{		
		String path = GlobalVar.ConfigPath+"\\SmallKeyDef.ini";
		String keydef[];
		Vector contents = new Vector();

		BufferedReader br = null;
		try
		{

			if ((br = CommonMethod.readFileGB2312(path)) == null)
			{
				new MessageBox("找不到小键盘键值数据文件",null, false);
				return;
			}

			String line = null;
			while((line = br.readLine()) != null)
			{
				if (line.trim().length() <= 0)
				{
					continue;
				}

				if (line.trim().charAt(0) == ';') //判断是否为备注
				{
					continue;
				}
				keydef = line.split("=");
				if(keydef.length<2) continue;

				keydef[0] = keydef[0].trim();
				keydef[1] = keydef[1].trim();
				contents.add(keydef);
			}


		}
		catch(Exception e)
		{
			PosLog.getLog(this.getClass()).error(e);
			new MessageBox("读取小键盘键值数据异常" + e.getMessage());			
			return;
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



		String[] title = { "代码", "功能信息描述" };
		int[] width = { 60, 300 };

		int choice = new MutiSelectForm().open("请选择功能", title, width, contents, true,310,520,280,400,false,false,-1,false);//窗口宽,窗口高,表宽,表高,是否是多选
		if (choice == -1)
		{
//			new MessageBox("没有选择功能");
			return;
		}
		else {
			String[] row = (String[]) (contents.elementAt(choice));
			if(1==1) NewKeyListener.sendKey(Convert.toInt(row[0]));
			/*if (row[0].equals("27"))//退货
			{
				saleEvent.backInput();
			}
			else if (row[0].equals("31"))//折让
			{
				saleEvent.inputRebatePrice();
			}
			else if (row[0].equals("33"))//总折让
			{
				saleEvent.inputAllRebatePrice();
			}
			else if (row[0].equals("30"))//折扣
			{
				saleEvent.inputRebate();
			}
			else if (row[0].equals("32"))//总折扣
			{
				saleEvent.inputAllRebate();
			}
			else if (row[0].equals("35"))//挂单
			{
				saleEvent.writeHnag();
			}
			else if (row[0].equals("36"))//解挂
			{
				saleEvent.readHang();
			}
			else if (row[0].equals("28"))//删除
			{
				saleEvent.deleteCurrentGoods();
			}
			else if (row[0].equals("29"))//取消
			{
				saleEvent.clearSell();
			}
			else if (row[0].equals("34"))//价格
			{
				saleEvent.inputPrice();
			}
			else if (row[0].equals("45"))//离开
			{
				if (new MessageBox(Language.apply("你确定要离开并锁定收银机吗?"), null, true).verify() == GlobalVar.Key1)
				{
					new PersonnelGoForm();
				}
			}
			else if (row[0].equals("17"))//确认
			{
				saleEvent.rebateDetail();
			}
			else if (row[0].equals("18"))//回车
			{
				saleEvent.enterInput();
			}
			else if (row[0].equals("26"))//数量
			{
				saleEvent.inputQuantity();
			}
			else if (row[0].equals("37"))//营业员
			{
				saleEvent.inputStaff();
			}
			else if (row[0].equals("39"))//会员授权
			{
				saleEvent.memberGrant();
			}
			else if (row[0].equals("41"))//付款键
			{
				saleEvent.payInput();
			}
			else if (row[0].equals("47"))//打印键
			{
				rePrint();
			}*/

		}
	}

	public void paySell()
	{
		if(saleHead.ismemo)
			super.paySell();
		else
			new MessageBox("一张小票只允许有同一种返劵和满赠规则，本比小票有不同返劵和满赠的商品，请取消本小票，分单处理！");return;		

	}


/*	public boolean clearSell(int index)
	{
		if(super.clearSell(index))
		{
			saleHead.ismemo = true;
			saleHead.str10 = "";
			saleHead.str9 = null;
			isCard = true;
			return true;
		}

		return false;
	}*/
	
	public boolean clearSell(int index)
	{
		if (cancelMemberOrGoodsRebate(index))
		{
			//取消新规则促销
			clearPopGoodsSell();
			
			
			return true;
		}
		else
		{
			if(super.clearSell(index))
			{
				saleHead.ismemo = true;
				saleHead.str10 = "";
				saleHead.str9 = null;
				isCard = true;
				
//				清除新促销临时数据
				clearpopdata();
				
				return true;
			}
		}
		
		return false;
	}

//	取消新规则促销
	private void clearPopGoodsSell() {
		for(int i=0;i<crmpopgoodsdetail.size();i++)
		{
			((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).rulepopzk = 0;
			((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).rulepopzkfd = 0;

			((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).zdrulepopzk = 0;
			((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).zdrulepopzkfd = 0;
			
			((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).lszk =0;
			

			getZZK((SaleGoodsDef)saleGoods.elementAt(i));
		}
		
		saveGoodsReplacePopGoodsList();
	}

	public SaleGoodsDef goodsDef2SaleGoods(GoodsDef goodsDef, String yyyh, double quantity, double price, double allprice, boolean dzcm)
	{
		SaleGoodsDef saleGoodsDef = new SaleGoodsDef();

		saleGoodsDef.syjh = saleHead.syjh; // 收银机号,主键
		saleGoodsDef.fphm = saleHead.fphm; // 小票号,主键
		saleGoodsDef.rowno = saleGoods.size() + 1; // 行号,主键
		saleGoodsDef.yyyh = yyyh; // 营业员
		saleGoodsDef.fph = curyyyfph; // 营业员手工单发票号
		saleGoodsDef.barcode = goodsDef.barcode; // 商品条码
		saleGoodsDef.code = goodsDef.code; // 商品编码
		saleGoodsDef.type = goodsDef.type; // 编码类别
		saleGoodsDef.gz = goodsDef.gz; // 商品柜组
		saleGoodsDef.catid = goodsDef.catid; // 商品品类
		saleGoodsDef.ppcode = goodsDef.ppcode; // 商品品牌
		saleGoodsDef.uid = goodsDef.uid; // 多单位码
		saleGoodsDef.batch = curBatch; // 批号
		saleGoodsDef.yhdjbh = goodsDef.popdjbh; // 优惠单据编号
		saleGoodsDef.name = ((goodsDef.name == null) ? "" : goodsDef.name.trim()); // 名称
		saleGoodsDef.unit = goodsDef.unit; // 单位
		saleGoodsDef.bzhl = goodsDef.bzhl; // 包装含量
		saleGoodsDef.sl = ManipulatePrecision.doubleConvert(quantity, 4, 1); // 销售数量
		saleGoodsDef.lsj = goodsDef.lsj; // 零售价
		saleGoodsDef.jg = ManipulatePrecision.doubleConvert(price, 2, 1); // 销售价格
		saleGoodsDef.hjzk = 0; // 合计折扣,等于各种折扣之和
		saleGoodsDef.hyzke = 0; // 会员折扣额(来自会员优惠)
		saleGoodsDef.hyzkfd = goodsDef.hyjzkfd; // 会员折扣分担
		saleGoodsDef.yhzke = 0; // 优惠折扣额(来自营销优惠)
		saleGoodsDef.yhzkfd = 0; // 优惠折扣分担
		saleGoodsDef.lszke = 0; // 零时折扣额(来自手工打折)
		saleGoodsDef.lszre = 0; // 零时折让额(来自手工打折)
		saleGoodsDef.lszzk = 0; // 零时总品折扣
		saleGoodsDef.lszzr = 0; // 零时总品折让
		saleGoodsDef.plzke = 0; // 批量折扣
		saleGoodsDef.zszke = 0; // 赠送折扣
		saleGoodsDef.lszkfd = curPfzkfd; // 临时折扣分担
		saleGoodsDef.sqkh = ""; // 单品授权卡号
		saleGoodsDef.sqktype = '\0'; // 单品授权卡类别
		saleGoodsDef.sqkzkfd = 0; // 单品授权卡授权折扣分担
		saleGoodsDef.isvipzk = goodsDef.isvipzk; // 是否允许VIP折扣（Y/N）
		saleGoodsDef.xxtax = goodsDef.xxtax; // 税率
		saleGoodsDef.flag = '4'; // 商品标志，1-赠品,2-电子秤条码，3-削价，4-一般

		// 合计金额
		if (dzcm)
		{
			saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(allprice, 2, 1);
			saleGoodsDef.flag = '2';
			if (SellType.ISBACK(saletype))
				saleGoodsDef.yhzke = dzcmjgzk;
			else
				saleGoodsDef.lszke = dzcmjgzk;
		}
		else
		{ // 良品铺子当ISDZC='A'时，当做电子秤处理
			if (goodsDef.isdzc == 'A')
				saleGoodsDef.flag = '2';
			saleGoodsDef.hjje = ManipulatePrecision.doubleConvert(saleGoodsDef.jg * saleGoodsDef.sl, 2, 1);
		}

		// 以旧换新码
		if (goodsDef.type == '8')
		{
			saleGoodsDef.yjhxcode = goodsDef.fxm;
			((SaleGoodsDef) (saleGoods.lastElement())).yjhxcode = goodsDef.code;
		}

		saleGoodsDef.inputbarcode = goodsDef.inputbarcode;
		saleGoodsDef.ysyjh = ""; // 原收银机号
		saleGoodsDef.yfphm = 0; // 原小票号
		saleGoodsDef.fhdd = ""; // 发货地点
		saleGoodsDef.memo = ""; // 备注
		saleGoodsDef.str1 = goodsDef.str1; // 备用字段
		saleGoodsDef.str2 = ""; // 备用字段
		saleGoodsDef.num1 = 0; // 备用字段
		saleGoodsDef.num2 = 0; // 备用字段

		if(SellType.ISHH(saletype))
		{
			if(saleGoodsDef.str13==null || (saleGoodsDef.str13!=null && !saleGoodsDef.str13.equalsIgnoreCase("T")))
			{
				saleGoodsDef.str13="S";
			}
			saleGoodsDef.ysyjh = this.thSyjh;
			saleGoodsDef.yfphm = this.thFphm;
		}

		// 家电下乡返款交易,记录退货原小票号
		if (SellType.ISJDXXFK(saletype) && GlobalInfo.sysPara.jdxxfkflag == 'Y')
		{
			saleGoodsDef.yfphm = thFphm;
			saleGoodsDef.ysyjh = thSyjh;
		}

		return saleGoodsDef;
	}

	public boolean checkFindGoodsAllowSale(GoodsDef goodsDef, double quantity, boolean isdzcm, double dzcmsl, double dzcmjg)
	{
		if(!GlobalInfo.sysPara.isnewpop.equals("Y"))
		{
			if(SellType.ISSALE(saleHead.djlb) && (goodsDef.str2 != null && goodsDef.str2.length()>=2 && goodsDef.str2.charAt(2) != 'Y'))
			{
				new MessageBox("该商品只允许退货，不允许销售！");
				return false;
			}
		}

		return super.checkFindGoodsAllowSale(goodsDef,quantity,isdzcm,dzcmsl,dzcmjg);
	}

	public void execCustomKey2(boolean keydownonsale)
	{
		if(GlobalInfo.sysPara.isfp == 'Y')
		{
			printFp();
		}
	}

	//打印发票
	public void printFp()
	{

		ResultSet rs = null;
		SaleHeadDef saleheadprint = null;
		Vector salegoodsprint = null;
		Vector salepayprint = null;

		//		 检查发票是否打印完,打印完未设置新发票号则不能交易
		if (Printer.getDefault().getSaleFphmComplate()) { return; }


		if (getReprintAuth())//(me.verify() == GlobalVar.Key1 && getReprintAuth())
		{
			//Object obj = null;
			String fphm = null;
			String syjh = ConfigClass.CashRegisterCode;

			if (curGrant.privdy != 'Y' && curGrant.privdy != 'L')
			{
				OperUserDef user = null;
				if ((user = DataService.getDefault().personGrant(Language.apply("授权重打印小票"))) != null)
				{
					if (user.privdy != 'Y' && user.privdy != 'L')
					{
						new MessageBox(Language.apply("当前工号没有重打上笔小票权限!"));

						return;
					}

					String log = "授权重打印上一笔小票,授权工号:" + user.gh;
					AccessDayDB.getDefault().writeWorkLog(log);
				}
				else
				{
					return;
				}
			}

			//新增默认上一笔的小票号、收银机号 wangyong by 2013.9.3
			RetSYJForm frm = new RetSYJForm();
			int done = frm.open(null, -1, Language.apply("请输入【重印】收银机号和小票号"));
			if (done == frm.Done)
			{
				syjh = RetSYJForm.syj;
				fphm = String.valueOf(RetSYJForm.fph);
			}
			else
			{
				// 放弃重打印
				return;
			}

			//if ((obj = GlobalInfo.dayDB.selectOneData("select max(fphm) from salehead where syjh = '" + syjh + "'")) != null)
			//{
			Sqldb db = getPrintSqlDB(syjh, fphm);
			if (db==null)
			{
				new MessageBox(Language.apply("没有查询到小票,不能打印!"));
				return;
			}
			try
			{
				//fphm = String.valueOf(obj);

				if ((rs = db.selectData("select * from salehead where syjh = '" + syjh + "' and  fphm = " + fphm)) != null)
				{

					if (!rs.next())
					{
						new MessageBox(Language.apply("没有查询到小票头,不能打印!"));
						return;
					}

					saleheadprint = new SaleHeadDef();

					if (!db.getResultSetToObject(saleheadprint)) { return; }
				}
				else
				{
					new MessageBox(Language.apply("查询小票头失败!"), null, false);
					return;
				}
			}
			catch (Exception ex)
			{
				new MessageBox(Language.apply("查询小票头出现异常!"), null, false);
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
				return;
			}
			finally
			{
				GlobalInfo.dayDB.resultSetClose();
			}

			try
			{
				if ((rs = db.selectData("select * from SALEGOODS where syjh = '" + syjh + "' and fphm = " + fphm
						+ " order by rowno")) != null)
				{
					boolean ret = false;
					salegoodsprint = new Vector();
					while (rs.next())
					{
						SaleGoodsDef sg = new SaleGoodsDef();

						if (!db.getResultSetToObject(sg)) { return; }

						salegoodsprint.add(sg);
						saleheadprint.yfphm = String.valueOf(sg.yfphm);//wangyong add by 2013.10.16 记录原小票信息,否则打印不了
						saleheadprint.ysyjh = sg.ysyjh;

						ret = true;
					}

					if (!ret)
					{
						new MessageBox(Language.apply("没有查询到小票明细,不能打印!"));
						return;
					}
				}
				else
				{
					new MessageBox(Language.apply("查询小票明细失败!"), null, false);
					return;
				}
			}
			catch (Exception ex)
			{
				new MessageBox(Language.apply("查询小票明细出现异常!"), null, false);
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
				return;
			}
			finally
			{
				db.resultSetClose();
			}

			try
			{
				if ((rs = db.selectData("select * from SALEPAY where syjh = '" + syjh + "' and fphm = " + fphm + " order by rowno")) != null)
				{
					boolean ret = false;
					salepayprint = new Vector();
					while (rs.next())
					{
						SalePayDef sp = new SalePayDef();

						if (!db.getResultSetToObject(sp)) { return; }

						salepayprint.add(sp);

						ret = true;
					}
					if (!ret)
					{
						new MessageBox(Language.apply("没有查询到付款明细,不能打印!"));
						return;
					}
				}
				else
				{
					new MessageBox(Language.apply("查询付款明细失败!"), null, false);
					return;
				}
			}
			catch (Exception ex)
			{
				new MessageBox(Language.apply("查询付款明细出现异常!"), null, false);
				PosLog.getLog(this.getClass().getSimpleName()).error(ex);
				return;
			}
			finally
			{
				db.resultSetClose();
			}

			saleheadprint.printnum++;
			AccessDayDB.getDefault().updatePrintNum(saleheadprint.syjh, String.valueOf(saleheadprint.fphm),
					String.valueOf(saleheadprint.printnum));
//			ProgressBox pb = new ProgressBox();
//			pb.setText(Language.apply("现在正在重打印小票,请等待....."));

			YyySaleBillMode.getDefault().setTemplateObject(saleheadprint, salegoodsprint , salepayprint);
			((Cbbh_YyySaleBillMode)YyySaleBillMode.getDefault()).printfp();
		}
	}
	
	
	//常规促销
	public void execCustomKey3(boolean keydownonsale)
	{
		int index = saleEvent.table.getSelectionIndex();
		findCgPop(index);
	}

	//规则促销
	public void execCustomKey4(boolean keydownonsale)
	{
		int index = saleEvent.table.getSelectionIndex();
		findGzPop(index);
	}
	
	
	//新促销查找常规促销
	private double findCgPop(int index)
	{

		Vector contents = new Vector();
		
		//常规促销列表
		Vector cglist = new Vector();
		
		if(crmpopgoodsdetail.size() <= 0){new MessageBox("无促销选择！");return 0;}
		
		CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(index);
		
		if(cpd != null)
		{
						
			for(int i =-1;i<poplist.size();i++)
			{
				if(i<0)
				{
					contents.add(new String[]{"00","无促销"});
					cglist.add(new String[]{"00","","","",""});
					continue;
				}
				
	//			调用后台接口，获取对应商品折扣
				String[] value = (String[]) poplist.elementAt(i);
				//1=常规促销，2=规则促销
				if(value[0].equals("2") || !value[1].equals(String.valueOf(index+1)))
				{	
					continue;
				}
				else
				{
					String[] value1 = new String[2];
					value1[0] = String.valueOf(i);
					value1[1] = value[4];
					contents.add(value1);
					cglist.add(value);
				}
			}
		}
		
		String[] title = { "序号", "功能信息描述" };
		int[] width = { 60,410 };

		int choice = new MutiSelectForm().open("请选择功能", title, width, contents, true,510,520,480,400,false,false,-1,false);//窗口宽,窗口高,表宽,表高,是否是多选
		if (choice == -1)
		{
//			new MessageBox("没有选择功能");
			return 0;
		}
		else {
			
			if(cglist != null && cglist.size() > 0)
			{
				String[] value = (String[])cglist.elementAt(Convert.toInt(choice));
				 
				if(value[0].equals("00"))
				{
						((CrmPopDetailDef) crmpopgoodsdetail.elementAt(index)).gdpopno = "";
						((CrmPopDetailDef) crmpopgoodsdetail.elementAt(index)).gdpopzk = 0;
				}
				else
				{
					 Vector gooddeflist = ((Cbbh_NetService)NetService.getDefault()).getChoosePop(cpd,String.valueOf(saleHead.fphm),new Vector(),value[0],value[1],value[3],"111",saletype);
						
					/* if(gooddeflist != null && gooddeflist.size()>0)
					 {
//						 crmpopgoodsdetail.clear();
//						 crmpopgoodsdetail = gooddeflist;
						 saveinoutgoods(gooddeflist);
					 }*/
					 
					 if(gooddeflist != null && gooddeflist.size()>0)
					 {
						 CrmPopDetailDef cpd1 = (CrmPopDetailDef) gooddeflist.elementAt(0);
						 for (int i = 0; i < crmpopgoodsdetail.size(); i++) {
							 CrmPopDetailDef cpdarray = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(i);
							if(cpd1.rowno == cpdarray.rowno)
							{
								crmpopgoodsdetail.set(i, cpd1);
								break;
							}
						}
					 }
				}
				 
				 saveGoodsReplacePopGoodsList();
				
				/*for(int i =0;i<gooddef.size();i++)
				{
					String[] cpdef  = (String[])gooddef.elementAt(i);
					for(int j=0;j<saleGoods.size();j++)
					{
						if(String.valueOf(((SaleGoodsDef)saleGoods.elementAt(j)).rowno).equals(cpdef[1]))
						{
							((SaleGoodsDef)saleGoods.elementAt(j)).jg = Convert.toDouble(cpdef[9]);
							((SaleGoodsDef)saleGoods.elementAt(j)).sl = Convert.toDouble(cpdef[8]);
//							((SaleGoodsDef)saleGoods.elementAt(j)).hjzk = Convert.toDouble(cpdef[11])+Convert.toDouble(cpdef[14]);
							((SaleGoodsDef)saleGoods.elementAt(j)).lszke = Convert.toDouble(cpdef[11]+Convert.toDouble(cpdef[14]));
							((SaleGoodsDef)saleGoods.elementAt(j)).lszkfd = Convert.toDouble(cpdef[12]+Convert.toDouble(cpdef[15]));
							
//							((SaleGoodsDef)saleGoods.elementAt(j)).num4 = Convert.toDouble(cpdef[11]);
//							((SaleGoodsDef)saleGoods.elementAt(j)).num5 = Convert.toDouble(cpdef[14]);

							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopsj = Convert.toDouble(cpdef[10]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopzk = Convert.toDouble(cpdef[11]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopzkfd = Convert.toDouble(cpdef[12]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopno = cpdef[13];
							
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopzk = Convert.toDouble(cpdef[14]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopzkfd = Convert.toDouble(cpdef[15]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopno = cpdef[16];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopmemo = cpdef[17];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopyhfs = cpdef[18];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulesupzkfd = Convert.toDouble(cpdef[19]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).ruleticketno = Convert.toDouble(cpdef[20]);
							
							
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopzk = Convert.toDouble(cpdef[21]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopzkfd = Convert.toDouble(cpdef[22]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopno = cpdef[23];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopmome = cpdef[24];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopyhfs = cpdef[25];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulesupzkfd = Convert.toDouble(cpdef[26]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdruleticketno = Convert.toDouble(cpdef[27]);
						}
					}
				}*/
				
			}
		}
		
		return 0;
	}
	
	//新促销查找规则促销
	private double findGzPop(int index)
	{

		Vector contents = new Vector();
		
		if(crmpopgoodsdetail.size() <= 0){new MessageBox("无促销选择！");return 0;}
		
		CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(index);
		
		//可选规则促销列表
		Vector gzlist = new Vector();
		
		
		if(cpd != null)
		{
			
			for(int i =-1;i<poplist.size();i++)
			{
				if(i<0)
				{
					contents.add(new String[]{"00","无促销"});
					gzlist.add(new String[]{"00","","","",""});
					continue;
				}
	//			调用后台接口，获取对应商品折扣
				String[] value = (String[]) poplist.elementAt(i);
				//1=常规促销，2=规则促销
				if(value[0].equals("1") || !value[1].equals(String.valueOf(index+1)))
				{	
					continue;
				}
				else
				{
					String[] value1 = new String[2];
					value1[0] = String.valueOf(i);
					value1[1] = value[4];
					contents.add(value1);
					gzlist.add(value);
				}
			}
		}
		
		String[] title = { "序号", "功能信息描述" };
		int[] width = { 60,410 };

		int choice = new MutiSelectForm().open("请选择功能", title, width, contents, true,510,520,480,400,false,false,-1,false);//窗口宽,窗口高,表宽,表高,是否是多选
		if (choice == -1)
		{
//			new MessageBox("没有选择功能");
			return 0;
		}
		else {
			
			if(gzlist != null && gzlist.size() > 0)
			{
				String[] value = (String[])gzlist.elementAt(Convert.toInt(choice));
				
//				Vector gooddeflist = ((Cbbh_NetService)NetService.getDefault()).getChoosePop(crmpopgoodsdetail,value[0],value[1],value[3],"123123");
//				
//				 if(gooddeflist != null && gooddeflist.size()>0)
//				 {
//					 crmpopgoodsdetail = new Vector();
//					 crmpopgoodsdetail = gooddeflist;
//				 }
				if(value[0].equals("00"))
				{
					((CrmPopDetailDef) crmpopgoodsdetail.elementAt(index)).rulepopno = "";
					((CrmPopDetailDef) crmpopgoodsdetail.elementAt(index)).rulepopmemo = "";
				}
				else
				{
					for(int i =0;i<crmpopgoodsdetail.size();i++)
					{
						CrmPopDetailDef cpddef = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(i);
						if(value[1].equals(String.valueOf(cpddef.rowno)))
						{
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).rulepopno = value[3];
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).rulepopmemo = value[4];
						}
					}
				}
				 
				 saveGoodsReplacePopGoodsList();
				
				/*for(int i =0;i<gooddef.size();i++)
				{
					String[] cpdef  = (String[])gooddef.elementAt(i);
					for(int j=0;j<saleGoods.size();j++)
					{
						if(String.valueOf(((SaleGoodsDef)saleGoods.elementAt(j)).rowno).equals(cpdef[1]))
						{
							((SaleGoodsDef)saleGoods.elementAt(j)).jg = Convert.toDouble(cpdef[9]);
							((SaleGoodsDef)saleGoods.elementAt(j)).sl = Convert.toDouble(cpdef[8]);
//							((SaleGoodsDef)saleGoods.elementAt(j)).hjzk = Convert.toDouble(cpdef[11])+Convert.toDouble(cpdef[14]);
							((SaleGoodsDef)saleGoods.elementAt(j)).lszke = Convert.toDouble(cpdef[11]+Convert.toDouble(cpdef[14]));
							((SaleGoodsDef)saleGoods.elementAt(j)).lszkfd = Convert.toDouble(cpdef[12]+Convert.toDouble(cpdef[15]));
							
							if(((SaleGoodsDef)saleGoods.elementAt(j)).name.indexOf("[M]") < 0)
							{
								((SaleGoodsDef)saleGoods.elementAt(j)).name = "[M]"+((SaleGoodsDef)saleGoods.elementAt(j)).name;
							}
							
//							((SaleGoodsDef)saleGoods.elementAt(j)).num4 = Convert.toDouble(cpdef[11]);
//							((SaleGoodsDef)saleGoods.elementAt(j)).num5 = Convert.toDouble(cpdef[14]);
							
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopsj = Convert.toDouble(cpdef[10]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopzk = Convert.toDouble(cpdef[11]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopzkfd = Convert.toDouble(cpdef[12]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).gdpopno = cpdef[13];
							
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopzk = Convert.toDouble(cpdef[14]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopzkfd = Convert.toDouble(cpdef[15]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopno = cpdef[16];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopmemo = cpdef[17];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulepopyhfs = cpdef[18];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).rulesupzkfd = Convert.toDouble(cpdef[19]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).ruleticketno = Convert.toDouble(cpdef[20]);
							
							
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopzk = Convert.toDouble(cpdef[21]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopzkfd = Convert.toDouble(cpdef[22]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopno = cpdef[23];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopmome = cpdef[24];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulepopyhfs = cpdef[25];
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdrulesupzkfd = Convert.toDouble(cpdef[26]);
							((CrmPopDetailDef)crmpopgoodsdetail.elementAt(j)).zdruleticketno = Convert.toDouble(cpdef[27]);
						}
					}
				}*/
				
			}
		}
		
		return 0;
	}
	
	
	//查找全部单品商品明细
	private void findCrmPopDetail(String type,Vector popgoods)
	{		
		//调用过程获取临时表数据
		 Vector cpdlist =  ((Cbbh_NetService)NetService.getDefault()).getTotalPop(String.valueOf(saleHead.fphm),type,popgoods,saleHead.hykh,saletype);
		 		
		/* if(cpdlist != null && cpdlist.size()>0)
		 {
			 saveinoutgoods(cpdlist);
		 }*/
		 
		 if(cpdlist != null && cpdlist.size()>0)
		 {
			 CrmPopDetailDef cpd = (CrmPopDetailDef) cpdlist.elementAt(0);
			 for (int i = 0; i < crmpopgoodsdetail.size(); i++) {
				 CrmPopDetailDef cpdarray = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(i);
				if(cpd.rowno == cpdarray.rowno)
				{
					crmpopgoodsdetail.set(i, cpd);
					break;
				}
			}
		 }
		 
		 saveGoodsReplacePopGoodsList();

		 
	}
	
	public boolean payAccount(PayModeDef mode, String money)
	{
		//退货不允许使用的付款方式
		if(SellType.ISBACK(saletype))
		{
			if(GlobalInfo.sysPara.isbackpay.length() > 0 && (","+GlobalInfo.sysPara.isbackpay+",").indexOf(","+mode.code+",") != -1)
			{
				new MessageBox("退货不允许使用"+mode.name); 
				return false;
			}
		}
		else	
		{
			if(GlobalInfo.sysPara.issalepay.length() > 0 && (","+GlobalInfo.sysPara.issalepay+",").indexOf(","+mode.code+",") != -1)
			{
				new MessageBox("销售不允许使用"+mode.name); 
				return false;
			}
		}
		
		//家电三种卡，每种卡一张单只允许使用一张
		for(int i =0;i<salePayment.size();i++)
		{
			SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
			if((sp.paycode.equals(dzkcode) && sp.paycode.equals(mode.code)) || (sp.paycode.equals(lbkcode) && sp.paycode.equals(mode.code)) || (sp.paycode.equals(ppkcode) && sp.paycode.equals(mode.code)))
			{
				new MessageBox("一张单只允许使用一张"+mode.name);
				return false;
			}
		}
		
		return super.payAccount(mode, money);
	}
		
	public void addSaleGoodsObject(SaleGoodsDef sg,GoodsDef goods,SpareInfoDef info)
	{
			
		if(saleHead.num1 > 0)
		{
			new MessageBox("家电开票不允许增加商品！");
			return;
		}
		
		super.addSaleGoodsObject(sg, goods, info);
		
		//家电开单商品销售退货不找促销
		//百货退货商品,已退商品不找促销
		if(sg.num7 > 0 || (SellType.ISBACK(saletype) && sg.num1 == 1) || sg.sl == 0)return;		
		
		//不指定小票退货不找促销
		if(SellType.ISBACK(saletype) && !((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
		{
			//保存临时促销明细表
			savepopgoods(saleGoods);
			return;
		}
		
		//退货往界面添加商品时不找促销，"#"是家电预销售，需要往下继续
		if((sg.yfphm > 0 && (sg.ysyjh != null || sg.ysyjh.length() > 0)) && (!sg.ysyjh.equals("#")))return;
		

		//保存临时促销明细表
		savepopgoods(saleGoods);
		
		//新增商品只传最后一个商品到后台
		CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(crmpopgoodsdetail.size()-1);
		Vector goodslist = new Vector();
		goodslist.add(cpd);
		
		//==2 家电
		if(saleHead.num5 == 2)
		{
//			findCrmPopDetail(String.valueOf(saleHead.num5),crmpopgoodsdetail); 
			findCrmPopDetail(String.valueOf(saleHead.num5),goodslist);
		}
		else
		{
//			findCrmPopDetail("1",crmpopgoodsdetail); 
			findCrmPopDetail("1",goodslist); 
		}

		inigoodsui();
		
		
		writeBrokenData();
		 
	}
	
	public double getSlOverFlow(double sl, char type1)
	{
		double result = sl;

		try
		{
			char type = type1;
			// 收银截断方式，1-四舍五入到3位、2-截断到3位
			switch (type)
			{
				case '1':
					result = ManipulatePrecision.doubleConvert(sl, 3, 1);

					break;

				case '2':
					result = ManipulatePrecision.doubleConvert(sl, 3, 0);

					break;

				default:
					result = ManipulatePrecision.doubleConvert(sl, 4, 1);
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	
	public boolean findGoods(String code, String yyyh, String gz, String memo)
	{
		if(super.findGoods(code, yyyh, gz, memo))
		{
			inigoodsui();
			return true;
		}
		return false;
	}
	
	 public boolean delSaleGoodsObject(int index)
	    {
	    	saleGoods.removeElementAt(index);
	    	goodsAssistant.removeElementAt(index);
	        goodsSpare.removeElementAt(index);
	        
	        if(crmpopgoodsdetail.size() > 0)
	        {
	        	crmpopgoodsdetail.removeElementAt(index);
	        }
	        
	        //整理行号
			if ((saleGoods != null || saleGoods.size() > 0) && (crmpopgoodsdetail != null || crmpopgoodsdetail.size() > 0))
			{
				SaleGoodsDef saleGoodsDef = null;
				CrmPopDetailDef cpd = null;
				
		        for(int i =0;i<saleGoods.size();i++)
		        {
		        	saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);
		        	cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(i);
		        	
					saleGoodsDef.rowno = i + 1;
					cpd.rowno = saleGoodsDef.rowno;
		        }
		        
		        Vector cheoosePopList = new Vector();
		        
		        //遍历删除可选促销列表
		        for(int j=0;j<poplist.size();j++)
		        {
		        	String[] line = (String[]) poplist.elementAt(j);
		        	if(!String.valueOf(index+1).equals(line[1]))
		        	{
		        		cheoosePopList.add(line);
		        		continue;
		        	}
		        }
		        poplist.clear();
		        poplist = cheoosePopList;
		        
			}
	        
	        return true;
	    }
	 
	 public void delBHLSPop()
	 {
		 if(!SellType.ISBACK(saletype))super.delBHLSPop();
	 }
	 
	 public boolean paySellPop()
	 {
		 if(GlobalInfo.sysPara.isnewpop.equals("Y"))
			 return true;
		 else
			 return super.paySellPop();
	 }
	 
	 public boolean comfirmPay()
		{
			// 不允许0金额交易成交
			if (GlobalInfo.sysPara.issaleby0 != 'Y' && calcHeadYfje() <= 0)
			{
				new MessageBox(Language.apply("交易金额不足,不能付款!"));
				return false;
			}

			// 商品数大于0
			if (saleGoods.size() <= 0)
			{
				new MessageBox(Language.apply("商品数小于1,不能付款"));
				return false;
			}

			//退货数量为0，为不退商品，需要传给后台计算促销
			if(!SellType.ISBACK(saletype))
			{
				// 商品数量小于1
				for (int i = 0; i < saleGoods.size(); i++)
				{
					SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.get(i);
	
					if (goodsDef.sl <= 0)
					{
						new MessageBox(Language.apply("第{0}行商品 [{1}] 数量不合法，不能付款\n请修改此行商品数量或者删除此商品后重新录入", new Object[] { " " + (i + 1) + " ", goodsDef.code }));
						return false;
					}
				}
			}

			if (GlobalInfo.payMode == null || GlobalInfo.payMode.size() <= 0)
			{
				new MessageBox(Language.apply("当前收银机没有定义付款方式,请通知信息部!"));
				return false;
			}

			return true;
		}
	 
	 //保存临时促销商品
	public void savepopgoods(Vector salegoods)
	{
//		crmpopgoodsdetail = new Vector();
		
		if(salegoods.size() <= 0)return;
			
		//新增商品
			if(crmpopgoodsdetail.size() <= 0 || salegoods.size() > crmpopgoodsdetail.size())
			{
				SaleGoodsDef cgd = (SaleGoodsDef) salegoods.elementAt(salegoods.size()-1);
				
				CrmPopDetailDef cpd = new CrmPopDetailDef();
				
				cpd.gbname = cgd.name;
				cpd.barcode = cgd.barcode;
				cpd.rowno = cgd.rowno;
				cpd.gdid = cgd.code;
				cpd.catid = cgd.catid;
				cpd.ppid = cgd.ppcode;
				cpd.sj = cgd.jg;
				cpd.minsj = cgd.jg;
				cpd.sl = cgd.sl;
				cpd.sjje = cgd.sl*cgd.jg;
				cpd.gdpopsj = 0;//常规促销售价
				cpd.gdpopzk =cgd.num3;//常规促销折扣
				cpd.gdpopzkfd = 0;//常规促销折扣供应商分担
				cpd.gdpopno = "";//常规促销单号
				cpd.rulepopzk =cgd.num4;//规则促销折扣
				cpd.rulepopzkfd = 0;//规则促销折扣分担
				cpd.rulepopno = "";//规则促销单号
				cpd.rulepopmemo = "";//规则促销描述
				cpd.rulepopyhfs = "";//规则促销优惠方式
				cpd.rulesupzkfd = 0;//规则促销供应商分担
				cpd.ruleticketno = 0;//规则促销返券类型
				cpd.zdrulepopzk = cgd.num5;//整单规则促销折扣
				cpd.zdrulepopzkfd = 0;//整单规则促销折扣分担
				cpd.zdrulepopno = "";//整单规则促销单号
				cpd.zdrulepopmome = "";//整单规则促销描述
				cpd.zdrulepopyhfs = "";//整单规则促销优惠方式
				cpd.zdrulesupzkfd = 0;//整单规则促销供应商分担
				cpd.zdruleticketno = 0;//整单规则促销返券类型
				cpd.ticketno = 0;//所用电子券编号
				cpd.ticketname = "";//所用电子券名称
				cpd.ticketzk = 0;//所用电子券抵扣金额
				cpd.jdsl = 0;//积点数量
				cpd.lszk = cgd.num6;//临时折扣
				//cpd.cjje = cpd.sjje-(cpd.gdpopzk-cpd.rulepopzk-cpd.zdrulepopzk-cpd.lszk);//成交金额
				//cpd.cjj =cpd.cjje / cpd.sl;//成交价
				cpd.cjje=0;
				cpd.cjj=0;
				
				if(saleHead != null)
				{
					if(SellType.ISBACK(saletype) && (saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0))
					{
						//2=换货的商品
						cgd.num1 = 2;
						cpd.num1 =cgd.num1;
					}
				}
				cpd.str1 = cgd.ysyjh;//原收银机
				cpd.str2 = String.valueOf(cgd.yfphm);//原小票
				
				cpd.gbname = cgd.name;//品名
				cpd.vpec = "";//规格
				cpd.guid ="";//单位ID
				cpd.unit=cgd.unit;//单位
				cpd.unitname=cgd.str2;//单位名称
				
				crmpopgoodsdetail.add(cpd);
			}
			else
			{
				//修改商品
				for (int j = 0; j < salegoods.size(); j++)
				{
					SaleGoodsDef cgd = (SaleGoodsDef) salegoods.elementAt(j);
					for(int i =0;i<crmpopgoodsdetail.size();i++)
					{
						CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(i);
						if(cpd.rowno == cgd.rowno)
						{
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).gbname = cgd.name;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).barcode = cgd.barcode;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).rowno = cgd.rowno;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).gdid = cgd.code;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).catid = cgd.catid;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).ppid = cgd.ppcode;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).sj = cgd.jg;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).minsj = cgd.jg;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).sl = cgd.sl;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).sjje = cgd.jg;
							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(i)).lszk = cgd.lszre+cgd.lszzr;//抹零功能
							
						}
					}
				}
			}
			saveGoodsReplacePopGoodsList();
	}
	
//	 输入数量
	public boolean inputQuantity(int index)
	{
		if(saleHead.num1 > 0){new MessageBox("开票商品不允许修改数量！");return false;}
		
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(index);
		if(SellType.ISBACK(saletype) && sgd.num1 < 2)
		{
			//指定小票退货
			if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
			{
				new MessageBox("指定退货商品不允许修改数量！");
				return false;
			}
		}
		
		if(super.inputQuantity(index, -1))
		{
			//处理输入的数量
			sgd.sl = getSlOverFlow(sgd.sl,GlobalInfo.sysPara.sljd);
			
			savepopgoods(saleGoods);
//			指定小票退货
			if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
			{
//				修改商品数量只传当前商品到后台
				CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(index);
				Vector goodslist = new Vector();
				goodslist.add(cpd);
				
			 //找商品促销
//			 findCrmPopDetail("1",crmpopgoodsdetail); 
				findCrmPopDetail("1",goodslist); 
			}
			 
			 inigoodsui();
			 
			 return true;
		}
		return false;
	}
	
//	 删除商品
	public boolean deleteGoods(int index)
	{
		if(saleHead.num1 > 0){new MessageBox("开票商品不允许删除！");return false;}
		
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(index);
		if(SellType.ISBACK(saletype) && sgd.num1 < 2)
		{
//			指定小票退货不找促销
			if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
			{
				new MessageBox("指定退货商品不允许删除！");
				return false;
			}
		}
		
		if(super.deleteGoods(index))
		{
			 savepopgoods(saleGoods);
//			指定小票退货
			if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
			{
			 //找商品促销
			 findCrmPopDetail("1",crmpopgoodsdetail); 
			}
			 
			 inigoodsui();
			 
			 return true;
		}
		return false;
	}
	
//	 输入金额
	public boolean inputPrice(int index)
	{
		if(saleHead.num1 > 0){new MessageBox("开票商品不允许修改金额！");return false;}
		
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(index);
		if(SellType.ISBACK(saletype) && sgd.num1 < 2)
		{
			//指定小票退货不允许修改金额
			if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
			{
				new MessageBox("指定退货商品不允许修改金额！");
				return false;
			}
		}
		
		if(super.inputPrice(index))
		{
			savepopgoods(saleGoods);
//			指定小票退货
			if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
			{
				//修改商品金额只传当前商品到后台
				CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(index);
				Vector goodslist = new Vector();
				goodslist.add(cpd);
				
				//找商品促销
//				findCrmPopDetail("1",crmpopgoodsdetail); 
				findCrmPopDetail("1",goodslist);

			}

			inigoodsui();
		}
		return false;
	}
	
//	 输入折让金额
	public boolean inputRebatePrice(int index)
	{
		SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(index);
		if(SellType.ISBACK(saletype) && sgd.num1 < 2)
		{
			//不指定小票退货不允许折让金额
			if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
			{
				new MessageBox("指定退货商品不允许折让金额！");
				return false;
			}
		}
		
		if(GlobalInfo.isOnline)
		{
			if(SellType.ISBACK(saletype))
			{
				String type="2";//2退货按付款键
				getTicketPop(crmpopgoodsdetail,type,saleHead.hykh,"","");
			}
			else
			{
				String type="0";//按付款键
				getTicketPop(crmpopgoodsdetail,type,saleHead.hykh,"","");
			}
		}

		
		saveGoodsReplacePopGoodsList();
		
		boolean flag = super.inputRebatePrice(index);
				
		savepopgoods(saleGoods);
		
		return flag;
	}
	
//	 输入总折让
	public boolean inputAllRebatePrice()
	{
		if(GlobalInfo.isOnline)
		{
			if(SellType.ISBACK(saletype))
			{
				String type="2";//2退货按付款键
				getTicketPop(crmpopgoodsdetail,type,saleHead.hykh,"","");
			}
			else
			{
				String type="0";//按付款键
				getTicketPop(crmpopgoodsdetail,type,saleHead.hykh,"","");
			}
		}
		
		saveGoodsReplacePopGoodsList();
		
		boolean flag = super.inputAllRebatePrice();
		
		savepopgoods(saleGoods);
		
		return flag;
		
	}
	
	
	//刷新商品界面
	public void inigoodsui()
	{
		/*for(int i =0;i<crmpopgoodsdetail.size();i++)
		{
			CrmPopDetailDef cpdef  = (CrmPopDetailDef)crmpopgoodsdetail.elementAt(i);
			for(int j=0;j<saleGoods.size();j++)
			{
				if(String.valueOf(((SaleGoodsDef)saleGoods.elementAt(j)).rowno).equals(String.valueOf(cpdef.rowno)))
				{
					((SaleGoodsDef)saleGoods.elementAt(j)).jg = Convert.toDouble(cpdef.sj);
					((SaleGoodsDef)saleGoods.elementAt(j)).sl = Convert.toDouble(cpdef.sl);
					((SaleGoodsDef)saleGoods.elementAt(j)).hjzk = Convert.toDouble(cpdef.gdpopzk)+Convert.toDouble(cpdef.rulepopzk)+Convert.toDouble(cpdef.lszk);
					((SaleGoodsDef)saleGoods.elementAt(j)).yhzke = Convert.toDouble(cpdef.gdpopzk)+Convert.toDouble(cpdef.rulepopzk)+Convert.toDouble(cpdef.lszk);
					
					
				}
			}
		}*/
		
		saveGoodsReplacePopGoodsList();
		
		//重新计算小票
		calcHeadYsje();
//		 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		
		saleEvent.setCurGoodsBigInfo();
		saleEvent.setTotalInfo();
		
		writeBrokenData();
	}
	
	
	 public boolean isSpecifyBack()
	 {
		 //指定小票退货可以在添加商品（换货操作）
		 return false;
	  }
	
	public static void setpoplist(Vector poplist1)
	{
		for(int i =0;i<poplist1.size();i++)
		{
			String[] line1 = (String[]) poplist1.elementAt(i);
			if(poplist.size() <= 0)
			{
				poplist = poplist1;
				break;
			}
			else
			{
				poplist.add(line1);
			}
		}
//		poplist = poplist1;
	}
	
	
	public static Vector getpoplist()
	{
		return poplist;
	}
	
	public void payInput()
	{
		if(saleGoods.size() > 0 && crmpopgoodsdetail.size() > 0)
		{			
			if(salePayment.size() > 0)
			{	
				for(int i=0;i<salePayment.size();i++)
				{
					SalePayDef sp = (SalePayDef) salePayment.elementAt(i);
					
					if(sp.paycode.equals(cxjcode))
					{
						deleteSalePay(i);
					}
					
				}
			}
			
			if (GlobalInfo.isOnline){
			
				//得到付款方式
				PayModeDef paymode = DataService.getDefault().searchPayMode(cxjcode);
				if(paymode == null){new MessageBox("找不到"+cxjcode+"付款方式！"); return;}
				//创建一个付款方式对象
				Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
				
				if(SellType.ISBACK(saletype))
				{
					String type="2";//2退货按付款键
					if(saleHead.num1 > 0)
					{
						type = "5";//5家电退货按付款键
					}
					
					((Cbbh_PaymentCouponNew)pay).poplist = getTicketPop(crmpopgoodsdetail,type,saleHead.hykh,"","");
	
					if(((Cbbh_PaymentCouponNew)pay).poplist == null)return;
				}
				else
				{
					String type="0";//0输完商品后自动计算最大用券金额
					if(saleHead.num1 > 0)
					{
						type = "3";//3家电按付款键
					}
					((Cbbh_PaymentCouponNew)pay).poplist = getTicketPop(crmpopgoodsdetail,type,saleHead.hykh,"","");
					if(((Cbbh_PaymentCouponNew)pay).poplist == null)return;
				}
					
				setYeShowNew(pay);
				
				((Cbbh_PaymentCouponNew)pay).getValidJe(0);
					
				pay.inputPay("");
				
					
				//赋值促销商品信息
				saveGoodsReplacePopGoodsList();			
				
			}
		}
		
		for(int i =0;i<addSalePay.size();i++)
		{

			SalePayDef spay = (SalePayDef) addSalePay.elementAt(i);
			
			PayModeDef paymode = DataService.getDefault().searchPayMode(spay.paycode);
			
			if(paymode == null){new MessageBox("找不到"+spay.paycode+"付款方式！"); return;}
			
			//创建一个付款方式对象
			Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
			pay.salepay = spay;
			
			addSalePayObject(spay, pay);
			
//			 计算剩余付款
			calcPayBalance();
		}
		
		super.payInput();
	}
	

	
	//获取整单促销，接劵，送劵列表
	public Vector getTicketPop(Vector popsalegoods,String type,String custno,String ticketno,String yqje)
	{
//		不指定小票退货不找促销
		if(SellType.ISBACK(saletype) && !((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)) && saleHead.num1 <= 0)
		{
			return new Vector();
		}
		return ((Cbbh_NetService)NetService.getDefault()).getTicketPop(String.valueOf(saleHead.fphm),popsalegoods,type,custno,ticketno,yqje,saletype);
	}
	
	
	//设置劵金额
	public void setYeShowNew(Payment pay)
	{
//		poplist = getTicketPop(crmpopgoodsdetail,"0","123123","","");
				
		if(((Cbbh_PaymentCouponNew)pay).poplist.size() > 0)
		{

			// 显示券类型
			for (int i = 0; i < ((Cbbh_PaymentCouponNew)pay).poplist.size(); i++)
			{

				String[] row = (String[]) ((Cbbh_PaymentCouponNew)pay).poplist.elementAt(i);
				
//				//积点
//				if(row[2].equals("3"))
//				{
//					for(int j=0;j<crmpopgoodsdetail.size();j++)
//					{
//						CrmPopDetailDef cpd = (CrmPopDetailDef)crmpopgoodsdetail.elementAt(j);
//						if(String.valueOf(cpd.rowno).equals(row[1]))
//						{
//							((CrmPopDetailDef) crmpopgoodsdetail.elementAt(j)).jdsl = Convert.toDouble(row[11]);
//						}
//					}
//				}
				
				//满减
				if(row[2].equals("5"))
				{						
					for(int j=0;j<crmpopgoodsdetail.size();j++)
					{
						CrmPopDetailDef cpd = (CrmPopDetailDef)crmpopgoodsdetail.elementAt(j);
						
						((SaleGoodsDef)saleGoods.elementAt(j)).yhzke = cpd.zdrulepopzk;
						((SaleGoodsDef)saleGoods.elementAt(j)).yhzkfd = cpd.zdrulepopzkfd;
						
						
//						 计算商品的合计
						getZZK(((SaleGoodsDef)saleGoods.elementAt(j)));
						
						calcHeadYsje();
					}
					
				}
				
				if(!row[2].equals("4"))continue;
				
				String[] lines = {row[7],row[8],String.valueOf(Convert.toDouble(row[9])*Convert.toDouble(row[10])),"1","-1","1"};
				((Cbbh_PaymentCouponNew)pay).couponList.add(lines);
			}
			
		}
	}
//	
////	发送退换货事务号
//	public boolean sendTHHTransID(String syjh, long fphm, String ishh, double transid)
//	{
//		if(GlobalInfo.sysPara.isnewpop.equals("Y")){return true;}
//		
//		return false;
//	}
//	
	
	/*	//不检查退换货
	public boolean checkTHH()
	{
		return true;
	}*/
	
	//将临时促销商品信息保存到交易商品明细
	public void saveGoodsReplacePopGoodsList()
	{
		
		for(int i =0;i<crmpopgoodsdetail.size();i++)
		{
			CrmPopDetailDef cpdef  = (CrmPopDetailDef)crmpopgoodsdetail.elementAt(i);
			
			for(int j=0;j<saleGoods.size();j++)
			{
				SaleGoodsDef sgd = ((SaleGoodsDef)saleGoods.elementAt(j));
				
				if(String.valueOf(sgd.rowno).equals(String.valueOf(cpdef.rowno)))
				{
					
					sgd.jg = Convert.toDouble(cpdef.sj);
					sgd.sl = Convert.toDouble(cpdef.sl);
					
					sgd.str2 = cpdef.unitname;//单位名称
					sgd.str3 = cpdef.guid;//单位ID
					

					//不定价商品
					if(sgd.lsj <= 0)
					{

						//定价常规折扣
						sgd.hyzke = cpdef.gdpopzk;
						
						//常规促销
						sgd.str9=cpdef.gdpopsj+","+cpdef.gdpopzk+","+cpdef.gdpopzkfd+","+cpdef.gdpopno;
						
						
						//规则促销
						sgd.rulezke = cpdef.rulepopzk;
						
						//规则促销
						sgd.str10=cpdef.rulepopzk+","+cpdef.rulepopzkfd+","+cpdef.rulepopno+","+cpdef.rulepopmemo+","+cpdef.rulepopyhfs+","+cpdef.rulesupzkfd+","+cpdef.ruleticketno;
					}
					//定价商品
					else
					{
						//常规促销
						sgd.hyzke = cpdef.gdpopzk;
						
						//常规促销
						sgd.str9=cpdef.gdpopsj+","+cpdef.gdpopzk+","+cpdef.gdpopzkfd+","+cpdef.gdpopno;
						
						//规则促销记到临时折扣（手工选择促销）
						sgd.rulezke = cpdef.rulepopzk;
						
						//规则促销
						sgd.str10=cpdef.rulepopzk+","+cpdef.rulepopzkfd+","+cpdef.rulepopno+","+cpdef.rulepopmemo+","+cpdef.rulepopyhfs+","+cpdef.rulesupzkfd+","+cpdef.ruleticketno;
					}
					
					
					//记录整单促销
					sgd.yhzke = Convert.toDouble(cpdef.zdrulepopzk);
					sgd.yhzkfd = Convert.toDouble(cpdef.zdrulepopzkfd);
					
					//整单规则
					sgd.str11=cpdef.zdrulepopzk+","+cpdef.zdrulepopzkfd+","+cpdef.zdrulepopno+","+cpdef.zdrulepopmome+","+cpdef.zdrulepopyhfs+","+cpdef.zdrulesupzkfd+","+cpdef.zdruleticketno;
					
					if(SellType.ISBACK(saletype))
					{
						//指定小票退货
						if(((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)))
						{
							sgd.lszre = cpdef.lszk;
						}
						else
						{
							//抹零功能（单品折让）
							if(sgd.lszre > 0)
							{
								sgd.lszre = cpdef.lszk;
							}
		//					抹零功能（总折让）
							if(sgd.lszzr > 0)
							{
								sgd.lszzr = cpdef.lszk;
							}
						}
					}
					else
					{
						//抹零功能（单品折让）
						if(sgd.lszre > 0)
						{
							sgd.lszre = cpdef.lszk;
						}
	//					抹零功能（总折让）
						if(sgd.lszzr > 0)
						{
							sgd.lszzr = cpdef.lszk;
						}
					}
					sgd.num14 = cpdef.lszk;//临时折扣
					sgd.num15 = cpdef.jdsl;//积点数量
					
//					店长卡
					sgd.str12=cpdef.shopcard+","+cpdef.shopcardzk;
//					品类卡
					sgd.str13=cpdef.catcard+","+cpdef.catcardzk;
//					品牌
					sgd.str14=String.valueOf(cpdef.ppcardzk);
//					所用电子券
					sgd.str15=cpdef.ticketno+","+cpdef.ticketname+","+cpdef.ticketzk;
					
					if(cpdef.ysyjid != null && cpdef.ysyjid.length() > 0)
					{
						sgd.ysyjh = cpdef.ysyjid;
						sgd.yfphm = Convert.toLong(cpdef.yinvno);
					}
					else if(cpdef.str1 != null && cpdef.str1.length() > 0)
					{
						sgd.ysyjh = cpdef.str1;
						sgd.yfphm = Convert.toLong(cpdef.str2);
					}
					
					
								
					getZZK(sgd);
				}
			}
		}
		
		//重新计算小票
		calcHeadYsje();
//		 刷新商品列表
		saleEvent.updateTable(getSaleGoodsDisplay());
		
		saleEvent.setCurGoodsBigInfo();
		
		saleEvent.setTotalInfo();
		
		writeBrokenData();
	}
	
	// 获取退货小票信息
	public boolean findBackTicketInfo()
	{
		if(!GlobalInfo.sysPara.isnewpop.equals("Y"))
		{
			super.findBackTicketInfo();
		}
		
		SaleHeadDef thsaleHead = null;
		Vector thsaleGoods = null;
		Vector thsalePayment = null;

		try
		{
			if (GlobalInfo.sysPara.inputydoc == 'D')
			{
				// 只记录原单小票号和款机号,但不按原单找商品
				return false;
			}

			// 如果是新指定小票进入
			if (SellType.ISHH(saletype) || saletype.equals(SellType.JDXX_BACK) || ((GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'C') && ((saleGoods.size() > 0 && isbackticket) || saleGoods.size() < 1)))
			{
				thsaleHead = new SaleHeadDef();
				thsaleGoods = new Vector();
				thsalePayment = new Vector();

				// 联网查询原小票信息
				ProgressBox pb = new ProgressBox();
				pb.setText(Language.apply("开始查找退货小票操作....."));
				if (!DataService.getDefault().getBackSaleInfo(thSyjh, String.valueOf(thFphm), thsaleHead, thsaleGoods, thsalePayment))
				{
					pb.close();
					pb = null;

					thSyjh = null;
					thFphm = 0;

					return false;
				}

				pb.close();
				pb = null;
				// 检查小票是否有满赠礼品，顾客退货，需要先退回礼品，再到收银台办理退货
				// Y为已在后台退回礼品 津乐会赠品退货
				if ((thsaleHead.str2.trim().equals("Y")))
				{
					new MessageBox(Language.apply("此小票有满赠礼品，请先到后台退回礼品再办理退货！"));
					return false;
				}
				// 检查此小票是否已经退货过，给出提示ADD by lwj
				if (thsaleHead.str1.trim().length() > 0)
				{
					if (new MessageBox(thsaleHead.str1 + Language.apply("\n是否继续退货？"), null, true).verify() != GlobalVar.Key1) { return false; }
				}
				// 原交易类型和当前退货类型不对应，不能退货
				// 如果原交易为预售提货，不判断
				// 如果当前交易类型为家电退货,那么可以支持零售销售的退货
				if (!thsaleHead.djlb.equals(SellType.PREPARE_TAKE) && !SellType.ISHH(saletype))
				{
					if (!SellType.getDjlbSaleToBack(thsaleHead.djlb).equals(this.saletype))
					{
						new MessageBox(Language.apply("原小票是[{0}]交易\n\n与当前退货交易类型不匹配", new Object[] { SellType.getDefault().typeExchange(thsaleHead.djlb, thsaleHead.hhflag, thsaleHead) }));
						// new MessageBox("原小票是[" +
						// SellType.getDefault().typeExchange(thsaleHead.djlb,
						// thsaleHead.hhflag, thsaleHead) +
						// "]交易\n\n与当前退货交易类型不匹配");

						// 清空原收银机号和原小票号
						thSyjh = null;
						thFphm = 0;
						return false;
					}
				}

				// 显示原小票商品明细
				Vector choice = new Vector();
				String[] title = { Language.apply("序"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("原数量"), Language.apply("原折扣"), Language.apply("原成交价"), Language.apply("退货"), Language.apply("退货数量") };
				int[] width = { 30, 100, 170, 80, 80, 100, 60, 100, 55 };
				String[] row = null;
				for (int i = 0; i < thsaleGoods.size(); i++)
				{
					CrmPopDetailDef cpd = (CrmPopDetailDef) thsaleGoods.get(i);
					row = new String[8];
					row[0] = String.valueOf(cpd.rowno);
					if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A"))
						row[1] = cpd.barcode;
					else if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
						row[1] = cpd.gdid;
					else 
						row[1] = cpd.barcode;

					/*if (sgd.inputbarcode.equals(""))
					{
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("A"))
							sgd.inputbarcode = sgd.barcode;
						row[1] = sgd.barcode;
						if (GlobalInfo.sysPara.backgoodscodestyle.equalsIgnoreCase("B"))
							sgd.inputbarcode = sgd.code;
						row[1] = sgd.code;
					}
					else
					{
						row[1] = sgd.inputbarcode;
					}*/

					row[2] = cpd.gbname;
					row[3] = ManipulatePrecision.doubleToString(cpd.sl, 4, 1, true);
					row[4] = ManipulatePrecision.doubleToString(cpd.gdpopzk+cpd.rulepopzk);
//					row[5] = ManipulatePrecision.doubleToString(cpd.sjje - (cpd.gdpopzk+cpd.rulepopzk));
					row[5] = ManipulatePrecision.doubleToString(cpd.cjje);
					row[6] = "";
					row[7] = "";
					choice.add(row);
				}

				String[] title1 = { Language.apply("序"), Language.apply("付款名称"), Language.apply("账号"), Language.apply("付款金额") };
				int[] width1 = { 30, 100, 250, 180 };
				String[] row1 = null;
				Vector content2 = new Vector();
				int j = 0;
				for (int i = 0; i < thsalePayment.size(); i++)
				{
					SalePayDef spd1 = (SalePayDef) thsalePayment.get(i);
					row1 = new String[4];
					row1[0] = String.valueOf(++j);
					row1[1] = String.valueOf(spd1.payname);
					row1[2] = String.valueOf(spd1.payno);
					row1[3] = ManipulatePrecision.doubleToString(spd1.je);
					content2.add(row1);
				}

				int cho = -1;
				if (EBill.getDefault().isEnable() && EBill.getDefault().isBack())
				{
					cho = EBill.getDefault().getChoice(choice);
				}
				else
				{
					// 选择要退货的商品
					cho = new MutiSelectForm().open(Language.apply("在以下窗口输入单品退货数量(回车键选择商品,付款键全选,确认键保存退出)"), title, width, choice, true, 780, 480, 750, 220, true, true, 7, true, 750, 130, title1, width1, content2, 0);
				}

				StringBuffer backYyyh = new StringBuffer();
				if (GlobalInfo.sysPara.backyyyh == 'Y')
				{
					new TextBox().open(Language.apply("开单营业员号："), "", Language.apply("请输入有效开单营业员号"), backYyyh, 0);
					// 查找营业员
					OperUserDef staff = null;
					if (backYyyh.length() != 0)
					{
						if ((staff = findYYYH(backYyyh.toString())) != null)
						{
							if (staff.type != '2')
							{
								new MessageBox(Language.apply("该工号不是营业员!"), null, false);
								return false;
							}
						}
						else
						{
							return false;
						}
					}
					else
					{
						return false;
					}

				}

				// 如果cho小于0且已经选择过退货小票
				if (cho < 0 && isbackticket)
					return true;
				if (cho < 0)
				{
					thSyjh = null;
					thFphm = 0;
					return false;
				}

				// 清除已有商品明细,重新初始化交易变量

				// 将退货授权保存下来
				String thsq = saleHead.thsq;
				initSellData();

				// 生成退货商品明细
				for (int i = 0; i < choice.size(); i++)
				{
					row = (String[]) choice.get(i);
//					if (!row[6].trim().equals("Y"))
//						continue;
					SaleGoodsDef sgd = new SaleGoodsDef();
					CrmPopDetailDef cpd = (CrmPopDetailDef) thsaleGoods.get(i);
					double thsl = ManipulatePrecision.doubleConvert(Convert.toDouble(row[7]), 4, 1);
					
					if (!row[6].trim().equals("Y") || cpd.sl <= 0)
					{
						sgd.jg = 0;
						sgd.sl =0;
						sgd.hjje = 0;
						sgd.num1 =0;
						cpd.num1 =0;//非退货的商品(做换货标记)
					}
					else
					{
						sgd.sl = cpd.sl;
						sgd.jg = Convert.toDouble(row[5]);
						sgd.hjje = cpd.sl*cpd.sj;
						sgd.num1 =1;
						cpd.num1=1;//退货的商品(做换货标记)
					}
					
					sgd.ppcode=cpd.ppid;
					sgd.catid=cpd.catid;
					//sgd.gz=cpd.gdid
					sgd.unit=cpd.unit;
					sgd.uid =cpd.vpec;
					
					sgd.str2="";
					sgd.barcode = cpd.barcode;
					sgd.code = cpd.gdid;
					sgd.name = cpd.gbname;
					sgd.yyyh = saleEvent.yyyh.getText();
					sgd.gz = saleEvent.gz.getText();
					sgd.unit=cpd.unit;

					sgd.yfphm = Convert.toLong(cpd.yinvno);
					sgd.ysyjh = cpd.ysyjid;
					
					cpd.str1 = cpd.ysyjid;
					cpd.str2 = String.valueOf(cpd.yinvno);
					
					sgd.flag='4';//crm需要
					sgd.type='1';//crm需要
					
					sgd.yrowno = cpd.rowno;
					sgd.memonum1 = cpd.sl;
					sgd.syjh = ConfigClass.CashRegisterCode;
					sgd.fphm = GlobalInfo.syjStatus.fphm;
					sgd.rowno = saleGoods.size() + 1;
					sgd.str4 = backYyyh.toString();
					sgd.ysl = cpd.sl;
					sgd.str13 = "";
					if(SellType.ISHH(saletype)) sgd.str13 = "T";

					// 重算商品行折扣
					if (ManipulatePrecision.doubleCompare(sgd.sl, thsl, 4) > 0)
					{
						sgd.hjje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hjje, sgd.sl), thsl), 2, 1); // 合计金额
						sgd.hyzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzke, sgd.sl), thsl), 2, 1); // 会员折扣额(来自会员优惠)
						sgd.yhzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.yhzke, sgd.sl), thsl), 2, 1); // 优惠折扣额(来自营销优惠)
						sgd.lszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszke, sgd.sl), thsl), 2, 1); // 零时折扣额(来自手工打折)
						sgd.lszre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszre, sgd.sl), thsl), 2, 1); // 零时折让额(来自手工打折)
						sgd.lszzk = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzk, sgd.sl), thsl), 2, 1); // 零时总品折扣
						sgd.lszzr = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.lszzr, sgd.sl), thsl), 2, 1); // 零时总品折让
						sgd.plzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.plzke, sgd.sl), thsl), 2, 1); // 批量折扣
						sgd.zszke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.zszke, sgd.sl), thsl), 2, 1); // 赠送折扣
						sgd.cjzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.cjzke, sgd.sl), thsl), 2, 1); // 厂家折扣
						sgd.ltzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.ltzke, sgd.sl), thsl), 2, 1);
						sgd.hyzklje = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.hyzklje, sgd.sl), thsl), 2, 1);
						sgd.qtzke = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzke, sgd.sl), thsl), 2, 1);
						sgd.qtzre = ManipulatePrecision.doubleConvert(ManipulatePrecision.mul(ManipulatePrecision.div(sgd.qtzre, sgd.sl), thsl), 2, 1);
						sgd.hjzk = getZZK(sgd);
						sgd.sl = thsl;
						cpd.sl = thsl;
					}

					// 加入商品列表
					addSaleGoodsObject(sgd, null, new SpareInfoDef());
					
					crmpopgoodsdetail.add(cpd);
				}
				inigoodsui();
				
			/*	//添加付款方式
				for(int i=0; i<thsalePayment.size(); i++)
	    		{
	    			if (!addPayment((SalePayDef) thsalePayment.elementAt(i)))  return false; 
	    		}
	    		addRefundToSalePay();//添加扣回到付款
*/
				// 查找原交易会员卡资料
				if (thsaleHead.hykh != null && !thsaleHead.hykh.trim().equals(""))
				{
					curCustomer = new CustomerDef();
					curCustomer.code = thsaleHead.hykh;
					curCustomer.name = thsaleHead.hykh;
					curCustomer.ishy = 'Y';

					/*
					 * 业务过程只支持磁道查询,不支持卡号查询,因此无法检查原交易会员卡是否有效 if
					 * (!DataService.getDefault().getCustomer(curCustomer,
					 * thsaleHead.hykh)) { curCustomer.code = thsaleHead.hykh;
					 * curCustomer.name = "无效卡"; curCustomer.ishy = 'Y';
					 * 
					 * new MessageBox("原交易的会员卡可能已失效!\n请重新刷卡后进行退货"); }
					 */
				}

				// 设置原小票头信息
				saleHead.hykh = thsaleHead.hykh;
				saleHead.hytype = thsaleHead.hytype;
				saleHead.jfkh = thsaleHead.jfkh;

				saleHead.thsq = thsq;
				saleHead.ghsq = thsaleHead.ghsq;
				saleHead.hysq = thsaleHead.hysq;
				saleHead.sqkh = thsaleHead.sqkh;
				saleHead.sqktype = thsaleHead.sqktype;
				saleHead.sqkzkfd = thsaleHead.sqkzkfd;
				saleHead.hhflag = hhflag;
				saleHead.jdfhdd = thsaleHead.jdfhdd;
				saleHead.salefphm = thsaleHead.salefphm;
				
				saleHead.hyzke = thsaleHead.hyzke;
				saleHead.yhzke = thsaleHead.yhzke;
				saleHead.lszke = thsaleHead.lszke;

				// 退货小票辅助处理
				takeBackTicketInfo(thsaleHead, thsaleGoods, thsalePayment);

				// 重算小票头
				calcHeadYsje();

				// 为了写入断点,要在刷新界面之前置为true
				isbackticket = true;

				// 检查是否超出退货限额
				if (curGrant.thxe > 0 && saleHead.ysje > curGrant.thxe)
				{
					OperUserDef staff = backSellGrant();
					if (staff == null)
					{
						initSellData();
						isbackticket = false;
					}
					else
					{
						if (staff.thxe > 0 && saleHead.ysje > staff.thxe)
						{
							new MessageBox(Language.apply("超出退货的最大限额，不能退货"));

							initSellData();
							isbackticket = false;
						}
						else
						{
							// 记录日志
							saleHead.thsq = staff.gh;
							curGrant.privth = staff.privth;
							curGrant.thxe = staff.thxe;

							String log = "授权退货,小票号:" + saleHead.fphm + ",最大退货限额:" + curGrant.thxe + ",授权:" + staff.gh;
							AccessDayDB.getDefault().writeWorkLog(log);

							//
							new MessageBox(Language.apply("授权退货,限额为 {0} 元", new Object[] { ManipulatePrecision.doubleToString(curGrant.thxe) }));
						}
					}
				}

				backPayment.removeAllElements();
				backPayment.addAll(thsalePayment);

				// 刷新界面显示
				saleEvent.clearTableItem();
				saleEvent.updateSaleGUI();

				return isbackticket;
			}

			return false;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (thsaleHead != null)
			{
				thsaleHead = null;
			}

			if (thsaleGoods != null)
			{
				thsaleGoods.clear();
				thsaleGoods = null;
			}

			if (thsalePayment != null)
			{
				thsalePayment.clear();
				thsalePayment = null;
			}
		}
	}
	
	//上下键不显示商品名称，显示规则促销信息
	public String[] convertColumnValue(String[] srcValue, int index)
	{
		String[] converttxt = super.convertColumnValue(srcValue, index);
				
		SaleGoodsDef goodsDef = (SaleGoodsDef) saleGoods.get(index);
		
		for (int i = 0; i < crmpopgoodsdetail.size(); i++) {
			CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(i);
			if(cpd.rowno == goodsDef.rowno)
			{
				converttxt[2] = "促销:"+cpd.rulepopmemo;
			}
		}
		
		saleEvent.zhongwenStyledText.setFont(SWTResourceManager.getFont("宋体", 14, SWT.BOLD));
		return converttxt;
	}
	
	//先下账
	/*public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox(Language.apply("小票附加信息输入失败,不能完成交易!"));
				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在汇总交易数据,请等待....."));
			if (!saleSummary())
			{
				new MessageBox(Language.apply("交易数据汇总失败!"));

				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在校验数据平衡,请等待....."));
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, Language.apply("正在输入客户信息,请等待......"));
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, Language.apply("正在打开钱箱,请等待....."));
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, Language.apply("正在记账付款数据,请等待....."));
				if (!saleCollectAccountPay())
				{
					new MessageBox(Language.apply("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!"));

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}
				if(GlobalInfo.isOnline)
				{
					//不指定小票退货不调用下账,saleHead.num1=家电开单号
					if(SellType.ISSALE(saletype) || saleHead.num1 > 0 || (SellType.ISBACK(saletype) && ((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0))))
					{
						if(!((Cbbh_NetService)NetService.getDefault()).accountPay(Cbbh_SaleBS.crmpopgoodsdetail,salePayment,saleHead))
						{
							new MessageBox(Language.apply("付款数据记账失败\n\n保存本地数据失败！"));
							return false;
						}
					}
				}
				
				setSaleFinishHint(status, Language.apply("正在写入交易数据,请等待......"));
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox(Language.apply("交易数据写盘失败!"));
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, Language.apply("正在清除断点保护数据,请等待......"));
				clearBrokenData();

				//
				setSaleFinishHint(status, Language.apply("正在清除付款冲正数据,请等待......"));
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!"));
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);
				//校验商品明细平等
				checkgoodsList();
				
				if(SellType.ISBACK(saletype))
				{
					if(GlobalInfo.sysPara.isprintback.equals("Y"))
					{
						//记录发票号码发送到后台
						saleHead.str1 = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
					}
				}
				else
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(0);
					//家电预销售不打小票 "#" = 预销售
					if(sgd.ysyjh != null && !sgd.ysyjh.equals("#"))
					{
						//记录发票号码发送到后台
						saleHead.str1 = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
					}
				}
					
//				 上传当前小票
				setSaleFinishHint(status, Language.apply("正在上传交易小票数据,请等待......"));
				boolean bsend = GlobalInfo.isOnline;
				

				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment) && bsend && GlobalInfo.isOnline)
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
					
					if(GlobalInfo.sysPara.sendsaleissuccess != 'Y')
					{
						saleFinish = false;
					}
					
					return false;
				}


				// 发送当前收银状态
				setSaleFinishHint(status, Language.apply("正在上传收银机交易汇总,请等待......"));
				DataService.getDefault().sendSyjStatus();

				doEvaluation(this.saleHead, this.saleGoods, this.salePayment);
				
				if(SellType.ISBACK(saletype))
				{
					if(GlobalInfo.sysPara.isprintback.equals("Y"))
					{
						// 打印小票
						setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
						printSaleBill();
					}
				}
				else
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(0);
					//家电预销售不打小票 "#" = 预销售
					if(sgd.ysyjh != null && !sgd.ysyjh.equals("#"))
					{
						//打印小票
						setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
						printSaleBill();
					}
				}
				
				
			}
			else
			{
				if(SellType.ISBACK(saletype))
				{
					if(GlobalInfo.sysPara.isprintback.equals("N"))
					{
						// 打印小票
						setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
						printSaleBill();
					}
				}
				else
				{
					//打印小票
					setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
					printSaleBill();
				}
			}


			// 标记本次交易已完成
			saleFinish = true;
			
			//还原家电退货状态
			isKdBack = false;
			
			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}

			// 清除本次交易数据
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, Language.apply("正在等待关闭钱箱,请等待......"));
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, Language.apply("本笔交易结束,开始新交易"));

			// 标记本次交易已完成
			saleFinish = true;
			
//			//清除新促销临时数据
//			clearpopdata();

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox(Language.apply("完成交易时发生异常:\n\n") + ex.getMessage());

			return saleFinish;
		}
	}*/
	
	public boolean saleFinishDone(Label status, StringBuffer waitKeyCloseForm)
	{
		try
		{
			// 如果没有连接打印机则连接
			if (GlobalInfo.sysPara.issetprinter == 'Y' && GlobalInfo.syjDef.isprint == 'Y' && Printer.getDefault() != null && !Printer.getDefault().getStatus())
			{
				Printer.getDefault().open();
				Printer.getDefault().setEnable(true);
			}

			// 标记最后交易完成方法已开始，避免重复触发
			if (!waitlab)
				waitlab = true;
			else
				return false;

			// 输入小票附加信息
			if (!inputSaleAppendInfo())
			{
				new MessageBox(Language.apply("小票附加信息输入失败,不能完成交易!"));
				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在汇总交易数据,请等待....."));
			if (!saleSummary())
			{
				new MessageBox(Language.apply("交易数据汇总失败!"));

				return false;
			}

			//
			setSaleFinishHint(status, Language.apply("正在校验数据平衡,请等待....."));
			if (!AccessDayDB.getDefault().checkSaleData(saleHead, saleGoods, salePayment))
			{
				new MessageBox(Language.apply("交易数据校验错误!"));

				return false;
			}

			// 最终效验
			if (!checkFinalStatus()) { return false; }

			// 不是练习交易数据写盘
			if (!SellType.ISEXERCISE(saletype))
			{
				// 输入顾客信息
				setSaleFinishHint(status, Language.apply("正在输入客户信息,请等待......"));
				selectAllCustomerInfo();

				//
				setSaleFinishHint(status, Language.apply("正在打开钱箱,请等待....."));
				CashBox.getDefault().openCashBox();

				//
				setSaleFinishHint(status, Language.apply("正在记账付款数据,请等待....."));
				if (!saleCollectAccountPay())
				{
					new MessageBox(Language.apply("付款数据记账失败\n\n稍后将自动发起已记账付款的冲正!"));

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}
				
				//校验商品明细平等
				checkgoodsList();
				
				if(SellType.ISBACK(saletype))
				{
					if(GlobalInfo.sysPara.isprintback.equals("Y"))
					{
						//记录发票号码发送到后台
						saleHead.str1 = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
					}
				}
				else
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(0);
					//家电预销售不打小票 "#" = 预销售
					if(sgd.ysyjh != null && !sgd.ysyjh.equals("#"))
					{
						//记录发票号码发送到后台
						saleHead.str1 = String.valueOf(Printer.getDefault().getCurrentSaleFphm());
					}
				}
					
//				 上传当前小票
				setSaleFinishHint(status, Language.apply("正在上传交易小票数据,请等待......"));
				
				boolean bsend = GlobalInfo.isOnline;
				

				if (!DataService.getDefault().sendSaleData(saleHead, saleGoods, salePayment) && bsend)
				{
					// 联网时发送小票却失败才记录日志
					if (bsend)
					{
						AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",联网销售时小票送网失败", StatusType.WORK_SENDERROR);
					}
					
					if(GlobalInfo.sysPara.sendsaleissuccess != 'Y')
					{
						saleFinish = false;
					}
					
					return false;
				}
				

				if(bsend)
				{
					saleHead.netbz ='Y';
				}
				setSaleFinishHint(status, Language.apply("正在写入交易数据,请等待......"));
				if (!AccessDayDB.getDefault().writeSale(saleHead, saleGoods, salePayment))
				{
					new MessageBox(Language.apply("交易数据写盘失败!"));
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票,金额:" + saleHead.ysje + ",发生数据写盘失败", StatusType.WORK_SENDERROR);

					// 记账失败,及时把冲正发送出去
					setSaleFinishHint(status, Language.apply("正在发送冲正数据,请等待....."));
					CreatePayment.getDefault().sendAllPaymentCz();

					return false;
				}

				// 小票已写盘,本次交易就要认为完成,即使后续处理异常也要返回成功
//				saleFinish = true;

				// 小票保存成功以后，及时清除断点
				setSaleFinishHint(status, Language.apply("正在清除断点保护数据,请等待......"));
				clearBrokenData();

				//
				setSaleFinishHint(status, Language.apply("正在清除付款冲正数据,请等待......"));
				if (!saleCollectAccountClear())
				{
					AccessDayDB.getDefault().writeWorkLog(saleHead.fphm + "小票清除冲正数据失败,但小票已成交保存", StatusType.WORK_SENDERROR);

					new MessageBox(Language.apply("小票已成交保存,但清除冲正数据失败\n\n请完成本笔交易后重启款机尝试删除记账冲正数据!"));
				}

				// 处理交易完成后一些后续动作
				doSaleFinshed(saleHead, saleGoods, salePayment);

				

				// 发送当前收银状态
				setSaleFinishHint(status, Language.apply("正在上传收银机交易汇总,请等待......"));
				DataService.getDefault().sendSyjStatus();

				doEvaluation(this.saleHead, this.saleGoods, this.salePayment);
				
				if(SellType.ISBACK(saletype))
				{
					if(GlobalInfo.sysPara.isprintback.equals("Y"))
					{
						// 打印小票
						setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
						printSaleBill();
					}
				}
				else
				{
					SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(0);
					//家电预销售不打小票 "#" = 预销售
					if(sgd.ysyjh != null && !sgd.ysyjh.equals("#"))
					{
						//打印小票
						setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
						printSaleBill();
					}
				}
				
				
			}
			else
			{
				if(SellType.ISBACK(saletype))
				{
					if(GlobalInfo.sysPara.isprintback.equals("N"))
					{
						// 打印小票
						setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
						printSaleBill();
					}
				}
				else
				{
					//打印小票
					setSaleFinishHint(status, Language.apply("正在打印交易小票,请等待......"));
					printSaleBill();
				}
			}

			// 标记本次交易已完成
			saleFinish = true;
			
			//还原家电退货状态
			isKdBack = false;
			
			// 返回到正常销售界面
			backToSaleStatus();

			// 保存本次的小票头
			if (saleFinish && saleHead != null)
			{
				lastsaleHead = saleHead;
			}

			// 清除本次交易数据
			this.initNewSale();

			// 关闭钱箱
			setSaleFinishHint(status, Language.apply("正在等待关闭钱箱,请等待......"));
			if (GlobalInfo.sysPara.closedrawer == 'Y')
			{
				// 如果钱箱能返回状态，采用等待钱箱关闭的方式来关闭找零窗口
				if (CashBox.getDefault().canCheckStatus())
				{
					// 等待钱箱关闭,最多等待一分钟
					int cnt = 0;
					while (CashBox.getDefault().getOpenStatus() && cnt < 30)
					{
						Thread.sleep(2000);

						cnt++;
					}

					// 等待一分钟后,钱箱还未关闭，标记为要等待按键才关闭找零窗口
					if (CashBox.getDefault().getOpenStatus() && cnt >= 30)
					{
						waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
						waitKeyCloseForm.append("Y");
					}
				}
				else
				{
					// 标记为要等待按键才关闭找零窗口
					waitKeyCloseForm.delete(0, waitKeyCloseForm.length());
					waitKeyCloseForm.append("Y");
				}
			}

			// 交易完成
			setSaleFinishHint(status, Language.apply("本笔交易结束,开始新交易"));

			// 标记本次交易已完成
			saleFinish = true;
			
//			//清除新促销临时数据
//			clearpopdata();

			return saleFinish;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();

			new MessageBox(Language.apply("完成交易时发生异常:\n\n") + ex.getMessage());

			return saleFinish;
		}
	}
	
	
	 public void initNewSale()
	 {
	    	super.initNewSale();
//	    	清除新促销临时数据
			clearpopdata();
//			sendBack = true;
	 }
	
	//检查小票平衡
	private void checkgoodsList()
	{
		if(!GlobalInfo.isOnline) return;
		if(saleGoods.size() != crmpopgoodsdetail.size())
		{
			new MessageBox("交易失败！\n本地商品明细条数与促销商品明细条数不相等\n本地明细="+saleGoods.size()+" 条\n促销明细="+crmpopgoodsdetail.size()+" 条");
//			return false;
		}
		
		//不指定小票退货
		if(!(SellType.ISBACK(saletype) && !((saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0)) && saleHead.num1 <= 0))
		{
			double headje =  getDetailOverFlow(saleHead.ysje,'3');//小票头应收金额
			double goodsje = 0;//本地小票明细总金额
			double popgoodsje = 0;//促销临时表商品总金额
			double payje = 0;//付款金额
			double zlje = 0;//找零金额
			double paysum = 0;//应付金额
		
			for(int i =0;i<saleGoods.size();i++)
			{
				SaleGoodsDef sgd = (SaleGoodsDef) saleGoods.elementAt(i);
				goodsje = goodsje+(sgd.hjje-sgd.hjzk);
			}
//			goodsje = getDetailOverFlow(goodsje);
			goodsje = getDetailOverFlow(goodsje,'3');
			
			
			for(int j =0;j<crmpopgoodsdetail.size();j++)
			{
				CrmPopDetailDef cpd = (CrmPopDetailDef) crmpopgoodsdetail.elementAt(j);
				popgoodsje = popgoodsje+cpd.cjje;
			}
//			popgoodsje = getDetailOverFlow(popgoodsje);
			popgoodsje = getDetailOverFlow(popgoodsje,'3');
			
			for(int k =0;k<salePayment.size();k++)
			{
				SalePayDef spd = (SalePayDef) salePayment.elementAt(k);
				if(spd.flag == '1')
				{
					payje = payje + (spd.je - spd.num1);
				}
				else if(spd.flag == '2')
				{
					zlje = zlje + spd.je;
				}
				
			}
			paysum = payje - zlje;
			
			//检查本地小票明细总金额与促销小票明细总金额
			if(getDetailOverFlow(goodsje,'3') != getDetailOverFlow(popgoodsje,'3'))
			{
				new MessageBox("交易失败！\n本地商品明细总金额与促销商品明细总金额不相等\n本地明细金额="+goodsje+" 元\n促销明细金额="+popgoodsje+" 元");
//				return false;
			}
			
			//比较本地小票头与促销小票明细总金额
			if(getDetailOverFlow(headje,'3') != getDetailOverFlow(goodsje,'3'))
			{
				new MessageBox("交易失败！\n本地小票头总金额与本地商品总金额不相等\n小票头金额="+headje+" 元\n本地明细金额="+goodsje+" 元");
//				return false;
			}
			
			//比较本地小票头与促销小票明细金额
			if(getDetailOverFlow(headje,'3') != getDetailOverFlow(popgoodsje,'3'))
			{
				new MessageBox("交易失败！\n本地小票头总金额与促销商品总金额不相等\n小票头金额="+headje+" 元\n促销明细金额="+popgoodsje+" 元");
//				return false;
			}
			
			//比较本地付款明细与促销小票明细金额
			if(getDetailOverFlow(paysum,'3') != getDetailOverFlow(popgoodsje,'3'))
			{
				new MessageBox("交易失败！\n本地付款明细总金额与促销商品总金额不相等\n付款金额="+paysum+" 元\n促销明细金额="+popgoodsje+" 元");
//				return false;
			}
		}
		
	}

//	清除新促销临时数据
	public void clearpopdata()
	{
		crmpopgoodsdetail.clear();
		poplist.clear();
	}
	

	//家电开单销售
	public void execCustomKey5(boolean keydownonsale)
	{
		if(saleGoods.size() > 0){new MessageBox("请先取消本比交易！");return;}
		StringBuffer thcode = new StringBuffer();
		if (new TextBox().open(Language.apply("请输入开票单号"),Language.apply("开票单号输入"), Language.apply("提示:请输入开票单号"),thcode, -1, -1));
		{
			if(thcode.toString().trim().equals(""))return;
			getSaleGoodsBill(thcode.toString(),saletype);
		}
		
	}
	
//	家电转正打印
	public void execCustomKey6(boolean keydownonsale)
	{
		jdZzPrint();
	}
	
	//获取开票单
	private boolean getSaleGoodsBill(String code,String saletype)
	{
		try{
			
			addSalePay = new Vector();
			
//			根据序号得到商品
			Vector v1 = new Vector();
			boolean done = ((Cbbh_NetService)NetService.getDefault()).getSaleGoodsBill(code,v1,saletype);
			
			if(done)
			{
				saleHead.num5=2;//2=家电
				
//				店长卡
				String shopcard = "";
				double shopcardzk = 0;
//				品类卡
				String catcard = "";
				double catcardzk = 0;
//				品牌卡
				String ppid="";
				double ppcardzk = 0;
					for (int i = 0; i < v1.size(); i++)
					{
						CrmPopDetailDef cpd=(CrmPopDetailDef)v1.elementAt(i);
						
						SaleGoodsDef sgd=new SaleGoodsDef();
						
							sgd.syjh = GlobalInfo.syjDef.syjh;
							sgd.rowno = cpd.rowno;
							sgd.barcode=cpd.barcode;
							sgd.name = cpd.gbname;
							sgd.code=cpd.gdid;
							sgd.yyyh=saleEvent.yyyh.getText();
							sgd.name=cpd.gbname;
							sgd.gz=saleEvent.gz.getText();
							sgd.unit=cpd.unit;
							sgd.jg=cpd.sj;
							sgd.sl=cpd.sl;
							sgd.catid = cpd.catid;
							sgd.ppcode = cpd.ppid;
							sgd.hjje=sgd.jg*sgd.sl;
							sgd.num3 = cpd.gdpopzk;
							sgd.num4 = cpd.rulepopzk;
							sgd.num5 = cpd.zdrulepopzk;
							sgd.num6 = cpd.lszk;
							sgd.lszre = cpd.lszk;
							
							sgd.num7 = Convert.toDouble(code);//家电开单号
							
							sgd.ysyjh = cpd.str1;//家电退货原收银机号
							sgd.yfphm = Convert.toLong(cpd.str2);//家电退货原小票号
							
							sgd.flag='4';//crm需要
							sgd.type='1';//crm需要
							
//							sgd.yfphm = Convert.toLong(cpd.str2);
//							sgd.ysyjh = cpd.str1;
							
							
							//转换单位名称
							sgd.str2 = cpd.unit;
							sgd.unit = cpd.unitname;
//							// 重算折扣
							getZZK(sgd);
							

							crmpopgoodsdetail.add(cpd);
							
							//加入商品列表
							addSaleGoodsObject(sgd, new GoodsDef(), new SpareInfoDef());
							
							
							//添加店长卡
							if(cpd.shopcardzk > 0)
							{
								shopcard = cpd.shopcard;
								shopcardzk += cpd.shopcardzk;
							}
							//添加品类卡
							if(cpd.catcardzk > 0)
							{
								catcard = cpd.catcard;
								catcardzk += cpd.catcardzk;
							}
							//添加品牌卡
							if(cpd.ppcardzk > 0)
							{
								ppid = cpd.ppid;
								ppcardzk += cpd.ppcardzk;
							}

					}
					
					if(shopcardzk > 0)
					{
						PayModeDef paymode = DataService.getDefault().searchPayMode(dzkcode);
						if(paymode == null){new MessageBox("找不到"+lbkcode+"付款方式！"); return false;}

//						保存付款方式
						SalePayDef sp = savesalepay(paymode,shopcard,shopcardzk);
						
						addSalePay.add(sp);
					}
					
					if(catcardzk > 0)
					{
						PayModeDef paymode = DataService.getDefault().searchPayMode(lbkcode);
						if(paymode == null){new MessageBox("找不到"+lbkcode+"付款方式！"); return false;}						

//						保存付款方式
						SalePayDef sp = savesalepay(paymode,catcard,catcardzk);
						
						addSalePay.add(sp);
					}
					
					if(ppcardzk > 0)
					{
						PayModeDef paymode = DataService.getDefault().searchPayMode(ppkcode);
						if(paymode == null){new MessageBox("找不到"+ppkcode+"付款方式！"); return false;}						

						//保存付款方式
						SalePayDef sp = savesalepay(paymode,ppid,ppcardzk);
						
						addSalePay.add(sp);
					}
					
					
					//记录开单序号
					saleHead.num1 = Convert.toDouble(code);
			}
			
			saveGoodsReplacePopGoodsList();
			
//			 计算小票应收
			calcHeadYsje();
			
			refreshSaleForm();
			
			
			return true;
			}
		catch(Exception er)
			{
				er.printStackTrace();
				return false;
			}
	}
	
	//保存家电三种卡信息
	public SalePayDef savesalepay(PayModeDef paymode,String payno,double money)
	{
		SalePayDef sp = new SalePayDef();
		sp.syjh = saleHead.syjh;
		sp.fphm = saleHead.fphm;
		sp.paycode = paymode.code;
		sp.payname = paymode.name;
		sp.payno = payno;
		sp.flag = '1';
		sp.ybje = Double.parseDouble(getPayMoneyByPrecision(money, paymode));
		sp.hl = paymode.hl;
		sp.je = ManipulatePrecision.doubleConvert(sp.ybje * sp.hl, 2, 1);
		
		return sp;
	}

	public void backSell()
	{
		//判断是否家电退货
		String path = GlobalVar.ConfigPath+"\\JDKP.ini";

		BufferedReader br = null;
		try
		{
			String line = null;
			if(((br = CommonMethod.readFileGB2312(path)) != null) && (line = br.readLine()) != null)
			{
				isKdBack = true;
				// 初始化交易
				initOneSale("4");
				
				StringBuffer thcode = new StringBuffer();
				if (new TextBox().open(Language.apply("请输入开票单号"),Language.apply("开票单号输入"), Language.apply("提示:请输入开票单号"),thcode, -1, -1));
				{
					if(thcode.toString().trim().equals(""))return;
					getSaleGoodsBill(thcode.toString(),saletype);
				}
			}
			else
			{
				isKdBack = false;
				super.backSell();
			}
		}
		catch(Exception e)
		{
			return;
		}
		
		/*Vector contents = new Vector();
		String[] title = { "序号", "功能信息描述" };
		int[] width = { 60, 200 };
		contents.add(new String[]{"0","普通退货"});
		contents.add(new String[]{"1","开单退货"});

		int choice = new MutiSelectForm().open("请选择功能", title, width, contents, true,305,320,280,200,false,false,-1,false);//窗口宽,窗口高,表宽,表高,是否是多选
		if (choice == -1)
		{
//			new MessageBox("没有选择功能");
			return;
		}
		else if (choice == 0){
			isKdBack = false;
			super.backSell();
		}
		else if(choice ==1){
			isKdBack = true;
			// 初始化交易
			initOneSale("4");
			
			StringBuffer thcode = new StringBuffer();
			if (new TextBox().open(Language.apply("请输入开票单号"),Language.apply("开票单号输入"), Language.apply("提示:请输入开票单号"),thcode, -1, -1));
			{
				if(thcode.toString().trim().equals(""))return;
				getSaleGoodsBill(thcode.toString(),saletype);
			}
		}
		else
		{
			return;
		}*/
	}
	
//	 是否指定小票退货
	public boolean isSpecifyTicketBack()
	{
		//新增开单退货
		if(isKdBack)return false;
		
		if (SellType.ISBACK(saletype) && ((GlobalInfo.sysPara.inputydoc == 'Y') || GlobalInfo.sysPara.inputydoc == 'A' || GlobalInfo.sysPara.inputydoc == 'B' || GlobalInfo.sysPara.inputydoc == 'C' || GlobalInfo.sysPara.inputydoc == 'D'))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/*public boolean payAccount(Payment pay, SalePayDef sp)
	{
		// 增加到付款集合
		if (sp != null || pay.alreadyAddSalePay)
		{
			if (sp != null)
			{
				// 付款覆盖模式,删除已有的付款p
				if (GlobalInfo.sysPara.payover == 'Y')
				{
					int i = existPayment(sp.paycode, sp.payno, true);
					if (i >= 0)
					{
						// 不管已有的付款是否取消成功,都要把当前付款增加到已付款中
						deleteSalePay(i);
					}
				}
				
				
				// 增加已付款
				addSalePayObject(sp, pay);
			}

			// 计算剩余付款
			calcPayBalance();

			// 如果是需要循环输入的付款方式,则自动发送ENTER键再次进入付款
			loopInputPay(pay);

			return true;
		}

		return false;
	}*/
	

   /* public void addSalePayObject(SalePayDef spay,Payment payobj)
    {
    	if(GlobalInfo.isOnline && !SellType.ISBACK(saletype) || (saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0))
//    	if(!SellType.ISBACK(saletype) && GlobalInfo.isOnline)
		{
			//检查受限的付款方式
    		spay.rowno = salePayment.size()+1;
    		if(spay.je >= saleEvent.saleBS.calcPayBalance())
    		{
    			if(!checkpay(spay)) return;
    		}
		}
		super.addSalePayObject(spay, payobj);
    }*/
    
    public boolean payComplete()
	{
    	if(GlobalInfo.isOnline && !SellType.ISBACK(saletype) || (saleHead.yfphm != null && saleHead.yfphm.length() > 0) && (saleHead.ysyjh != null && saleHead.ysyjh.length() > 0))
    	{
    		if(checkpay(new SalePayDef()))
        	{
        		return super.payComplete();
        	}
    	}
    	else
    	{
    		return super.payComplete();
    	}
    	
    	return false;
	}
	
	//检查受限的付款方式
	public boolean checkpay(SalePayDef sp)
	{
		String type = "1";//1百货修改付款方式的金额
		if(saleHead.num1 > 0)
		{
			type = "2";//2家电修改付款方式的金额
		}
		Vector popgoodslist = new Vector();
		Vector poplist = new Vector();
		//调用后台接口
		boolean flag = ((Cbbh_NetService)NetService.getDefault()).sendCheckPayMent(String.valueOf(saleHead.fphm),crmpopgoodsdetail, salePayment, type, saleHead.hykh , sp.paycode, String.valueOf(sp.je),sp.payno, popgoodslist,poplist,saletype);
		
		
		
		if(flag)
		{
			if(popgoodslist.size() > 0)
			{
//				crmpopgoodsdetail.clear();
//				crmpopgoodsdetail = poplist;
				saveinoutgoods(popgoodslist);
				
				saveGoodsReplacePopGoodsList();
	
			}
			
			//得到付款方式
			PayModeDef paymode = DataService.getDefault().searchPayMode(cxjcode);
			if(paymode == null){new MessageBox("找不到"+cxjcode+"付款方式！"); return false;}
			//创建一个付款方式对象
			Payment pay = CreatePayment.getDefault().createPaymentByPayMode(paymode, saleEvent.saleBS);
			
			if(poplist.size() > 0)
			{
				//重新获取整单参与的促销
				((Cbbh_PaymentCouponNew)pay).poplist.clear();
				((Cbbh_PaymentCouponNew)pay).poplist = poplist;
				
				((Cbbh_PaymentCouponNew)pay).getValidJe(0);
			}
		}
		else
		{
			return false;
		}
		
		return true;
	}
	
	public void calcHeadYsje()
	{
		SaleGoodsDef saleGoodsDef = null;
		int sign = 1;

		saleHead.hjzje = 0;
		saleHead.hjzsl = 0;
		saleHead.hjzke = 0;
		saleHead.hyzke = 0;
		saleHead.yhzke = 0;
		saleHead.lszke = 0;

		for (int i = 0; i < saleGoods.size(); i++)
		{
			saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(i);

			if (!statusCond(saleGoodsDef))
			{
				continue;
			}

			// 合计商品件数(电子秤商品总是按1件记数)
			int spjs = (int) saleGoodsDef.sl;
			if (saleGoodsDef.flag == '2')
				spjs = 1;
			saleHead.hjzsl += spjs;

			// 以旧换新商品,合计要减
			if (saleGoodsDef.type == '8' || this.isHHGoods(saleGoodsDef))
			{
				sign = -1;
			}
			else
			{
				sign = 1;
			}

			// 计算小票头汇总
			if(SellType.ISBACK(saletype) && saleGoodsDef.num1 == 2)
			{
				//换货商品要减去换货商品价格
				saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.hjzje - (saleGoodsDef.hjje * sign), 2, 1); // 合计总金额
				saleHead.hjzke = ManipulatePrecision.doubleConvert(saleHead.hjzke - (saleGoodsDef.hjzk * sign), 2, 1); // 合计折扣额
			}
			else
			{
			saleHead.hjzje = ManipulatePrecision.doubleConvert(saleHead.hjzje + (saleGoodsDef.hjje * sign), 2, 1); // 合计总金额
			saleHead.hjzke = ManipulatePrecision.doubleConvert(saleHead.hjzke + (saleGoodsDef.hjzk * sign), 2, 1); // 合计折扣额
			}

			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzke * sign), 2, 1); // 会员折扣额(来自会员优惠)
			saleHead.hyzke = ManipulatePrecision.doubleConvert(saleHead.hyzke + (saleGoodsDef.hyzklje * sign), 2, 1); // 会员折扣率金额(来自会员优惠)

			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.yhzke * sign), 2, 1); // 优惠折扣额(来自营销优惠)
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.zszke * sign), 2, 1); // 赠送折扣
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.rulezke * sign), 2, 1); // 超市规则促销折扣（非整单折扣）
			saleHead.yhzke = ManipulatePrecision.doubleConvert(saleHead.yhzke + (saleGoodsDef.mjzke * sign), 2, 1); // 超市规则促销折扣（整单折扣）

			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszke * sign), 2, 1); // 零时折扣额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszre * sign), 2, 1); // 零时折让额(来自手工打折)
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzk * sign), 2, 1); // 零时总品折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.lszzr * sign), 2, 1); // 零时总品折让
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.plzke * sign), 2, 1); // 批量折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.cjzke * sign), 2, 1); // 厂家折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.ltzke * sign), 2, 1); // 零头折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzke * sign), 2, 1); // 其他折扣
			saleHead.lszke = ManipulatePrecision.doubleConvert(saleHead.lszke + (saleGoodsDef.qtzre * sign), 2, 1); // 其他折扣
		}

		saleHead.ysje = ManipulatePrecision.doubleConvert(saleHead.hjzje - saleHead.hjzke, 2, 1);

		// 计算应付
		calcHeadYfje();
	}
	
	
	public void initSetYYYGZ(String type, boolean iscsinput)
	{
		if (GlobalInfo.syjDef.issryyy == 'C') // 家电模式
		{
			saleEvent.yyyh.setText("家电");
			saleEvent.gz.setText("0000");//任意柜
			saleEvent.saleform.setFocus(saleEvent.code);
		}
		else if (GlobalInfo.syjDef.issryyy == 'B')
		{
			saleEvent.yyyh.setText(Language.apply("百货"));
			saleEvent.gz.setText(Language.apply("0000"));//任意柜
			saleEvent.saleform.setFocus(saleEvent.code);
		}
		else 
		{
			super.initSetYYYGZ(type, iscsinput);
		}
			
	}
	
	public GoodsDef findGoodsInfo(String code, String yyyh, String gz, String dzcmscsj, boolean isdzcm, StringBuffer slbuf)
	{
		GoodsDef gd = findGoodsInfo(code, yyyh, gz, dzcmscsj, isdzcm, slbuf, false);
		
		Vector contents = new Vector();
		
		//找子母码，1=母码
		if(GlobalInfo.isOnline && gd != null && gd.str1.equals("1"))
		{
			Vector goodslist =  ((Cbbh_NetService)NetService.getDefault()).getGoodsList(gd.code);
			if(goodslist != null)
			{
				for(int i =0;i<goodslist.size();i++)
				{
					String[] goods = (String[]) goodslist.elementAt(i);
					contents.add(new String[]{goods[0],goods[2]+" | "+goods[3]});
				}
				
				String[] title = { "子码编码", "尺码|颜色" };
				int[] width = { 160,210 };
	
				int choice = new MutiSelectForm().open("请选择", title, width, contents, true,410,640,380,620,false,false,-1,false);//窗口宽,窗口高,表宽,表高,是否是多选
				
				if (choice == -1)
				{
	//				new MessageBox("没有选择功能");
					return null;
				}
				else 
				{
					String [] goods = (String[]) goodslist.elementAt(choice);
					gd = findGoodsInfo(goods[0], yyyh, gz, dzcmscsj, isdzcm, slbuf, false);
				}
			}
			else
			{
				new MessageBox("该母码下没有子码！");
				return null;
			}
		}
		else
		{
			return gd;
		}
		
		return gd;
	}
	
	public boolean checkDeleteSalePay(String string, boolean isDelete)
	{
		//店长，品类，品牌 卡付款方式退货时不允许被删除
		if((string.indexOf(dzkcode) != -1 || string.indexOf(lbkcode) != -1 || string.indexOf(ppkcode) != -1) && SellType.ISBACK(saletype)) 
			return true;
		else
			return false;
	}
	
	//家电转正打印
	public void jdZzPrint()
	{
		SaleHeadDef saleheadprint = new SaleHeadDef();
		Vector salegoodsprint = new Vector();
		Vector salepayprint = new Vector();
		
		String fphm = null;
		String syjh = ConfigClass.CashRegisterCode;
		
		RetSYJForm frm = new RetSYJForm();
		int done = frm.open(null, -1, Language.apply("请输入【转正】收银机号和小票号"));
		if (done == frm.Done)
		{
			syjh = RetSYJForm.syj;
			fphm = String.valueOf(RetSYJForm.fph);
			
			Vector popgoods = new Vector();
			
			if(((Cbbh_NetService)NetService.getDefault()).getZzFpInfo(syjh,fphm,saleheadprint,popgoods,salepayprint))
			{
				saleheadprint.syjh = syjh;
				saleheadprint.fphm = Convert.toLong(fphm);
				for(int i =0;i<popgoods.size();i++)
				{
					CrmPopDetailDef cpd = (CrmPopDetailDef) popgoods.elementAt(i);
					SaleGoodsDef sgd = new SaleGoodsDef();
					sgd.jg = cpd.sj;
					sgd.sl = cpd.sl;
					sgd.name = cpd.gbname;
					sgd.barcode = cpd.barcode;
					sgd.name = cpd.gbname;
					sgd.code = cpd.gdid;
					sgd.yyyh = saleEvent.yyyh.getText();
					sgd.gz = saleEvent.gz.getText();
					sgd.unit=cpd.unit;
					
					sgd.hyzke = cpd.gdpopzk;
					sgd.rulezke = cpd.rulepopzk;
					sgd.yhzke = cpd.zdrulepopzk;
					sgd.lszre = cpd.lszk;
				
					sgd.hjje = cpd.sj*cpd.sl;
					sgd.hjzk = ManipulatePrecision.doubleConvert(sgd.hyzke + sgd.yhzke + sgd.lszke + sgd.lszre + sgd.lszzk + sgd.lszzr + sgd.plzke + sgd.zszke + sgd.cjzke + sgd.ltzke + sgd.hyzklje + sgd.qtzke + sgd.qtzre + sgd.rulezke + sgd.mjzke, 2, 1);
					
					salegoodsprint.add(sgd);
				}
			}
			else
			{
				return;
			}
			
		}
		else
		{
			// 放弃重打印
			return;
		}
		
		printSaleTicket(saleheadprint, salegoodsprint, salepayprint, false);
	}
	
	//重新克隆促销商品明细
	public void saveinoutgoods(Vector popgoodslist)
	{
		crmpopgoodsdetail.clear(); 
		Vector popgoods = (Vector) popgoodslist.clone();
		crmpopgoodsdetail = popgoods;
		/*for (int i = 0; i < popgoodslist.size(); i++) {
			CrmPopDetailDef cpd1 = (CrmPopDetailDef) popgoodslist.elementAt(i);
			
			CrmPopDetailDef cpd = new CrmPopDetailDef();
			
			cpd.gbname = cpd1.gbname;
			cpd.barcode = cpd1.barcode;
			cpd.rowno = cpd1.rowno;
			cpd.gdid = cpd1.gdid;
			cpd.catid = cpd1.catid;
			cpd.ppid = cpd1.ppid;
			cpd.sj = cpd1.sj;
			cpd.minsj = cpd1.minsj;
			cpd.sl = cpd1.sl;
			cpd.sjje = cpd1.sjje;
			cpd.gdpopsj = cpd1.gdpopsj;//常规促销售价
			cpd.gdpopzk =cpd1.gdpopzk;//常规促销折扣
			cpd.gdpopzkfd = cpd1.gdpopzkfd;//常规促销折扣供应商分担
			cpd.gdpopno = cpd1.gdpopno;//常规促销单号
			cpd.rulepopzk =cpd1.rulepopzk;//规则促销折扣
			cpd.rulepopzkfd = cpd1.rulepopzkfd;//规则促销折扣分担
			cpd.rulepopno = cpd1.rulepopno;//规则促销单号
			cpd.rulepopmemo = cpd1.rulepopmemo;//规则促销描述
			cpd.rulepopyhfs = cpd1.rulepopyhfs;//规则促销优惠方式
			cpd.rulesupzkfd = cpd1.rulesupzkfd;//规则促销供应商分担
			cpd.ruleticketno = cpd1.ruleticketno;//规则促销返券类型
			cpd.zdrulepopzk = cpd1.zdrulepopzk;//整单规则促销折扣
			cpd.zdrulepopzkfd = cpd1.zdrulepopzkfd;//整单规则促销折扣分担
			cpd.zdrulepopno = cpd1.zdrulepopno;//整单规则促销单号
			cpd.zdrulepopmome = cpd1.zdrulepopmome;//整单规则促销描述
			cpd.zdrulepopyhfs = cpd1.zdrulepopyhfs;//整单规则促销优惠方式
			cpd.zdrulesupzkfd = cpd1.zdrulesupzkfd;//整单规则促销供应商分担
			cpd.zdruleticketno = cpd1.zdruleticketno;//整单规则促销返券类型
			cpd.ticketno = cpd1.ticketno;//所用电子券编号
			cpd.ticketname = cpd1.ticketname;//所用电子券名称
			cpd.ticketzk = cpd1.ticketzk;//所用电子券抵扣金额
			cpd.jdsl = cpd1.jdsl;//积点数量
			cpd.lszk = cpd1.lszk;//临时折扣
			cpd.cjje=cpd1.cjje;
			cpd.cjj=cpd1.cjj;
			cpd.num1 =cpd1.num1;
			cpd.str1 = cpd1.str1;//原收银机
			cpd.str2 = cpd1.str2;//原小票
			
			cpd.gbname = cpd1.gbname;//品名
			cpd.vpec = cpd1.vpec;//规格
			cpd.guid =cpd1.guid;//单位ID
			cpd.unit=cpd1.unit;//单位
			cpd.unitname=cpd1.unitname;//单位名称
			
			crmpopgoodsdetail.add(cpd);
		}*/
	}
	
	/*public boolean exitPaySell()
	{
		if(super.exitPaySell())
		{
			if(SellType.ISBACK(saletype))
			{
				sendBack = false;
			}
			return true;
		}
		
		return false;
		
	}*/

	
	public boolean readBrokenData()
	{
		if(super.readBrokenData())
		{
			int i;
			for (i = 0; i < payAssistant.size(); ++ i)
			{
				Payment p = (Payment) payAssistant.elementAt(i);
				
				if(p == null) return true;
				
				p.saleBS = this;
			}
			return true;
		}
		
		return false;
	}
}

