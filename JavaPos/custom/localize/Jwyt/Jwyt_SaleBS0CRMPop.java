package custom.localize.Jwyt;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.Struct.CmPopGoodsDef;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SpareInfoDef;

import custom.localize.Bstd.Bstd_DataService;
import custom.localize.Bstd.Bstd_NetService;
import custom.localize.Bstd.Bstd_SaleBS;

public class Jwyt_SaleBS0CRMPop extends Bstd_SaleBS
{
	public Vector crmPop = null;
	public int vipzk1 = 0; // 刷商品时实时计算
	public int vipzk2 = 1; // 付款时计算

	public void findGoodsRuleFromCRM(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		findGoodsCRMPop(sg, goods, info);
	}

	public Vector findSameGroup(CmPopGoodsDef cmp, int group)
	{
		Vector grpvec = ((Bstd_NetService) NetService.getDefault()).findCMPOPGroup(cmp.dqid, cmp.ruleid, group, CmdDef.WYT_FINDEWMCMPOPGROUP);

		if (grpvec == null)
			grpvec = ((Bstd_DataService) DataService.getDefault()).findCMPOPGroup(cmp.dqid, cmp.ruleid, group);

		return grpvec;
	}

	public Vector findCmpopGift(String dqid, String ruleid, String ladderid)
	{
		Vector giftvec = ((Bstd_NetService) NetService.getDefault()).findCMPOPGift(dqid, ruleid, ladderid, CmdDef.WYT_FINDCMPOPGIFT);

		if (giftvec == null)
			giftvec = ((Bstd_DataService) DataService.getDefault()).findCMPOPGift(dqid, ruleid, ladderid);

		return giftvec;
	}

	public void findGoodsCRMPop(SaleGoodsDef sg, GoodsDef goods, SpareInfoDef info)
	{
		String newyhsp = "90000000";
		String cardno = null;
		String cardtype = null;

		if ((curCustomer != null && curCustomer.iszk == 'Y'))
		{
			cardno = curCustomer.code;
			cardtype = curCustomer.type;
		}

		GoodsPopDef popDef = new GoodsPopDef();

		// 非促销商品 或者在退货时，不查找促销信息
		((Jwyt_DataService) DataService.getDefault()).findPopRuleCRM(popDef, sg.code, sg.gz, sg.uid, goods.specinfo, sg.catid, sg.ppcode, saleHead.rqsj, cardno, cardtype, saletype);

		// 换货状态下，不使用任何促销
		if (popDef.yhspace == 0 || hhflag == 'Y')
		{
			popDef.yhspace = 0;
			popDef.memo = "";
			popDef.poppfjzkfd = 1;
		}

		// 将收券规则放入GOODSDEF 列表
		goods.memo = popDef.str2;
		goods.num1 = popDef.num1;
		goods.num2 = popDef.num2;
		goods.str4 = popDef.mode;
		info.char3 = popDef.type;

		// 促销联比例
		sg.xxtax = Convert.toDouble(popDef.ksrq); // 促销联比例
		goods.xxtax = Convert.toDouble(popDef.ksrq);
		if (goods.memo == null)
			goods.memo = "";

		// 增加CRM促销信息
		crmPop.add(popDef);

		// 标志是否为9开头扩展的控制
		//boolean append = false;
		// 无促销,此会员不允许促销
		if (popDef.yhspace == 0)
		{
			//append = false;
			info.str1 = "0000";
		}
		else if (popDef.yhspace == Integer.parseInt(newyhsp))
		{
			//append = true;
			info.str1 = newyhsp;
		}
		else
		{

			if (String.valueOf(popDef.yhspace).charAt(0) != '9')
			{
				if (GlobalInfo.sysPara.iscrmtjprice == 'Y')
					info.str1 = Convert.increaseInt(popDef.yhspace, 5).substring(0, 4);
				else
					info.str1 = Convert.increaseInt(popDef.yhspace, 4);

				//append = false;
			}
			else
			{
				info.str1 = String.valueOf(popDef.yhspace);

				// append = true;
			}

		}

		/*
		 * // 询问参加活动类型 满减或者满增 String yh = info.str1;
		 * 
		 * if (append) yh = yh.substring(1);
		 * 
		 * StringBuffer buff = new StringBuffer(yh); Vector contents = new
		 * Vector();
		 * 
		 * for (int i = 0; i < buff.length(); i++) { // 2-任选促销/1-存在促销/0-无促销 if
		 * (buff.charAt(i) == '2') { if (i == 0) { contents.add(new String[] {
		 * "D", "参与打折促销活动", "0" }); } else if (i == 1) { contents.add(new
		 * String[] { "J", "参与减现促销活动", "1" }); } else if (i == 2) {
		 * contents.add(new String[] { "Q", "参与返券促销活动", "2" }); } else if (i ==
		 * 3) { contents.add(new String[] { "Z", "参与赠品促销活动", "3" }); } else if
		 * (i == 5) { contents.add(new String[] { "F", "参与积分活动", "5" }); } } }
		 * 
		 * if (contents.size() <= 1) { if (contents.size() > 0) { String[] row =
		 * (String[]) contents.elementAt(0); int i = Integer.parseInt(row[2]);
		 * buff.setCharAt(i, '1'); } } else { String[] title = { "代码", "描述" };
		 * int[] width = { 60, 400 }; int choice = new
		 * MutiSelectForm().open("请选择参与满减满赠活动的规则", title, width, contents);
		 * 
		 * for (int i = 0; i < contents.size(); i++) { if (i != choice) {
		 * String[] row = (String[]) contents.elementAt(i); int j =
		 * Integer.parseInt(row[2]); buff.setCharAt(j, '0'); } else { String[]
		 * row = (String[]) contents.elementAt(i); int j =
		 * Integer.parseInt(row[2]); buff.setCharAt(j, '1'); } } }
		 * 
		 * if (append) info.str1 = "9" + buff.toString(); else info.str1 =
		 * buff.toString(); }
		 * 
		 * String line = "";
		 * 
		 * String yh = info.str1; if (append) yh = info.str1.substring(1);
		 * 
		 * if (yh.charAt(0) != '0') { line += "D"; }
		 * 
		 * if (yh.charAt(1) != '0') { line += "J"; }
		 * 
		 * if (yh.charAt(2) != '0') { line += "Q"; }
		 * 
		 * if (yh.charAt(3) != '0') { line += "Z"; }
		 * 
		 * if (yh.length() > 5 && yh.charAt(5) != '0') { line += "F"; }
		 * 
		 * if (line.length() > 0) { sg.name = "(" + line + ")" + sg.name; }
		 * 
		 * if (!append) { // str3记录促销组合码 if (GlobalInfo.sysPara.iscrmtjprice ==
		 * 'Y') sg.str3 = info.str1 +
		 * String.valueOf(Convert.increaseInt(popDef.yhspace, 5).substring(4));
		 * else sg.str3 = info.str1; } else { sg.str3 = info.str1; } //
		 * 将商品属性码,促销规则加入SaleGoodsDef里 sg.str3 += (";" + goods.specinfo); sg.str3
		 * += (";" + popDef.memo); sg.str3 += (";" + popDef.poppfjzkl); sg.str3
		 * += (";" + popDef.poppfjzkfd); sg.str3 += (";" + popDef.poppfj);
		 */

		// 只有找到了规则促销单，就记录到小票
		if (!info.str1.equals("0000") || !info.str1.equals(newyhsp))
		{
			sg.zsdjbh = popDef.djbh;
			sg.zszkfd = popDef.poplsjzkfd;
		}
	}

	public void calcGoodsVIPRebate(int index)
	{
		getVIPZK(index, 0);
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

		if (curCustomer == null || (curCustomer != null && curCustomer.iszk != 'Y'))
			return;

		// 批发销售不计算
		if (SellType.ISBATCH(saletype)) { return; }

		if (SellType.ISEARNEST(saletype)) { return; }

		if (SellType.ISCOUPON(saletype)) { return; }

		// 削价商品和赠品不计算
		if ((saleGoodsDef.flag == '3') || (saleGoodsDef.flag == '1')) { return; }

		// 不为VIP折扣的商品不重新计算会员折扣额
		if (goodsDef.isvipzk == 'N')
			return;

		// 折扣门槛
		if (saleGoodsDef.hjje == 0 || ManipulatePrecision.doubleConvert((saleGoodsDef.hjje - saleGoodsDef.lszke - saleGoodsDef.lszre - saleGoodsDef.lszzk - saleGoodsDef.lszzr) / saleGoodsDef.hjje) < GlobalInfo.sysPara.vipzklimit)
			return;

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

					if (i == index)
						continue;

					if (saleGoodsDef1.code.equals(saleGoodsDef.code) && info1.char1 == 'Y')
					{
						q += saleGoodsDef1.sl;
					}
				}

				if (ManipulatePrecision.doubleConvert(max - used - q) > 0)
				{
					if (ManipulatePrecision.doubleConvert(saleGoodsDef.sl) > ManipulatePrecision.doubleConvert(max - used - q))
					{
						new MessageBox("此商品存在促销价，但是商品数量[" + saleGoodsDef.sl + "]超出数量限额【" + ManipulatePrecision.doubleConvert(max - used - q) + "】\n 强制将商品数量修改为【" + ManipulatePrecision.doubleConvert(max - used - q) + "】参与促销价");
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
		if (info.char1 == 'Y')
			return;

		if (goodsDef.isvipzk == 'Y')
		{
			// 开始计算VIP折扣
			saleGoodsDef.hyzke = 0;
			saleGoodsDef.hyzkfd = goodsDef.hyjzkfd;
		}

		// 判断促销单是否允许折上折
		/*
		 * if (goodsDef.pophyjzkl % 10 >= 1) zszflag = zszflag && true; else
		 * zszflag = zszflag && false;
		 */

		// 是否进行VIP打折,通过CRM促销控制
		boolean vipzk = false;

		// 无CRM促销，以分期促销折上折标志为准
		if (popDef.yhspace == 0)
		{
			vipzk = true;
		}
		else
		// 存在CRM促销
		{
			// 不享用VIP折扣,不进行VIP打折
			if (popDef.pophyjzkl == 0)
			{
				vipzk = false;
			}
			else
			// 享用VIP折扣，进行VIP折上折
			{
				vipzk = true;
				zszflag = zszflag && true;
			}
		}

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
				if (hyj == 0)
					hyj = goodsDef.hyj;
				else
					hyj = Math.min(hyj, goodsDef.hyj);
			}

			if (hyj != 0 && je > ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl))
			{
				saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert(je - ManipulatePrecision.doubleConvert(hyj * saleGoodsDef.sl));
				saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
				saleGoodsDef.hydjbh = goodsDef.str1;
			}

		}
		// 存在会员卡， 商品允许VIP折扣， CRM促销单允许享用VIP折扣
		else if (checkMemberSale() && curCustomer != null && goodsDef.isvipzk == 'Y' && vipzk && curCustomer.iszk == 'Y')
		{
			// 获取VIP折扣率定义
			calcVIPZK(index);

			// 折上折标志
			// zszflag = zszflag && (goodsDef.num4 == 1);
			zszflag = false;
			// 不计算会员卡折扣
			if (goodsDef.hyj == 1)
				return;

			// vipzk1 = 输入商品时计算商品VIP折扣,原VIP折上折模式
			if (type == vipzk1 && (GlobalInfo.sysPara.vipPromotionCrm == null || GlobalInfo.sysPara.vipPromotionCrm.equals("1")))
			{
				// 有折扣,进行折上折
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
								saleGoodsDef.hydjbh = goodsDef.str1;
							}
						}
					}
				}
				else
				{
					// 无折扣,按商品缺省会员折扣打折
					saleGoodsDef.hyzke = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * saleGoodsDef.hjje, 2, 1);
				}
			}
			else // vipzk2 = 按下付款键时计算商品VIP折扣,起点折扣计算模式
			if (type == vipzk2 && GlobalInfo.sysPara.vipPromotionCrm != null && GlobalInfo.sysPara.vipPromotionCrm.equals("2"))
			{
				// VIP折扣要除券付款
				double fte = 0;
				if (GlobalInfo.sysPara.vipPayExcp == 'Y')
					fte = getGoodsftje(index);

				double vipzsz = 0;

				// 直接在以以后折扣的基础上打商品定义的VIP会员折扣率
				if (GlobalInfo.sysPara.vipCalcType.equals("2"))
				{
					vipzsz = ManipulatePrecision.doubleConvert((1 - goodsDef.hyj) * (saleGoodsDef.hjje - getZZK(saleGoodsDef) - fte), 2, 1);
				}
				else if (GlobalInfo.sysPara.vipCalcType.equals("1"))
				{
					// 当前折扣如果高于门槛则还可以进行VIP折上折,否则VIP不能折上折
					if (getZZK(saleGoodsDef) > 0 && zszflag && ManipulatePrecision.doubleCompare(saleGoodsDef.hjje - getZZK(saleGoodsDef), saleGoodsDef.hjje * curCustomer.value3, 2) >= 0)
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
				saleGoodsDef.hydjbh = goodsDef.str1;
			}

			// 按价格精度计算折扣
			saleGoodsDef.hyzke = getConvertRebate(index, saleGoodsDef.hyzke);
		}

		getZZK(saleGoodsDef);

		if (saleGoodsDef.hyzke > 0)
		{

		}
	}

	public double getGoodsftje(int index)
	{
		if (goodsSpare == null || goodsSpare.size() <= index)
			return 0;
		SpareInfoDef spinfo = (SpareInfoDef) goodsSpare.elementAt(index);
		if (spinfo == null || spinfo.payft == null)
			return 0;

		return getftje(spinfo);
	}

	public double getftje(SpareInfoDef spinfo)
	{
		double ftje = 0;
		if (spinfo.payft != null)
		{
			for (int j = 0; j < spinfo.payft.size(); j++)
			{
				String[] s = (String[]) spinfo.payft.elementAt(j);
				if (s.length > 3)
					ftje += Convert.toDouble(s[3]);
			}
		}
		return ftje;
	}

	public void calcVIPZK(int index)
	{
		SaleGoodsDef saleGoodsDef = (SaleGoodsDef) saleGoods.elementAt(index);
		GoodsDef goodsDef = (GoodsDef) goodsAssistant.elementAt(index);

		// 未刷卡
		if (!checkMemberSale() || curCustomer == null)
			return;

		// 非零售开票
		if (!saletype.equals(SellType.RETAIL_SALE) && !saletype.equals(SellType.PREPARE_SALE))
		{
			goodsDef.hyj = 1;
			return;
		}

		// 查询商品VIP折上折定义
		GoodsPopDef popDef = new GoodsPopDef();
		if (((Jwyt_DataService) DataService.getDefault()).findHYZK(popDef, saleGoodsDef.code, curCustomer.type, saleGoodsDef.gz, saleGoodsDef.catid, saleGoodsDef.ppcode, goodsDef.specinfo))
		{
			// 有柜组和商品的VIP折扣定义
			goodsDef.hyj = popDef.pophyj;
			goodsDef.num4 = popDef.num2;
			goodsDef.str1 = popDef.str1;
		}
		else
		{
			// 无柜组和商品的VIP折扣定义,以卡类别的折扣率为VIP打折标准
			goodsDef.hyj = curCustomer.zkl;
			goodsDef.num4 = 1;
		}
	}

	public void writeSellObjectToStream(ObjectOutputStream s) throws Exception
	{
		super.writeSellObjectToStream(s);
		s.writeObject(crmPop);
	}

	public void readStreamToSellObject(ObjectInputStream s) throws Exception
	{
		super.readStreamToSellObject(s);
		crmPop = (Vector) s.readObject();
	}
}
