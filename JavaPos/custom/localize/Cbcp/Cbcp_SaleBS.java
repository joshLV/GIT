package custom.localize.Cbcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Vector;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.ProgressBox;
import com.efuture.commonKit.Sqldb;
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
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.Payment;
import com.efuture.javaPos.PrintTemplate.CheckGoodsMode;
import com.efuture.javaPos.PrintTemplate.YyySaleBillMode;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;
import com.efuture.javaPos.Struct.SpareInfoDef;
import com.efuture.javaPos.UI.Design.MutiSelectForm;
import com.efuture.javaPos.UI.Design.RetSYJForm;

import custom.localize.Bcrm.Bcrm_DataService;

public class Cbcp_SaleBS extends Cbcp_THHNew_SaleBS//Cbbh_THH_SaleBS
{
	private boolean isCard = true;//控制一张单只允许有一种返劵规则，是否先刷会员卡


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

			NewKeyListener.sendKey(GlobalVar.Pay);
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
			//根据返回值提示 for cbbh
			if(cust!=null && cust.memo!=null && cust.memo.length()>2 && cust.memo.charAt(1)=='Y')
			{
				new MessageBox(cust.memo.substring(2,cust.memo.length()));
			}


			//遍历查找已录入商品的CRM促销
			calcMenberCrmPop();
			return true;
		}
		return false;
	}

	public void getVIPZK(int index, int type)
	{
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


	public boolean clearSell(int index)
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
		/*if(SellType.ISSALE(saleHead.djlb) && (goodsDef.str2 != null && goodsDef.str2.length()>=2 && goodsDef.str2.charAt(2) != 'Y'))
		{
			new MessageBox("该商品只允许退货，不允许销售！");
			return false;
		}*/

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
			((Cbcp_YyySaleBillMode)YyySaleBillMode.getDefault()).printfp();
		}
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
		
		return super.payAccount(mode, money);
	}
	
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
