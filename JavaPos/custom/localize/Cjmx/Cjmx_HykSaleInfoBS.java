package custom.localize.Cjmx;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.swt.custom.StyledText;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Communication.CmdDef;
import com.efuture.javaPos.Communication.NetService;
import com.efuture.javaPos.Device.Printer;
import com.efuture.javaPos.Global.CustomLocalize;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.HykInfoQueryBS;
import com.efuture.javaPos.Payment.CreatePayment;
import com.efuture.javaPos.Payment.PaymentCoupon;
import com.efuture.javaPos.PrintTemplate.GiftBillMode;
import com.efuture.javaPos.Struct.CustFilterDef;
import com.efuture.javaPos.Struct.CustomerDef;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.UI.CouponQueryInfoEvent;
import com.efuture.javaPos.UI.Design.MutiSelectForm;

public class Cjmx_HykSaleInfoBS
{
	protected CustomerDef cust = null;
	protected PaymentCoupon coupon = null;
	protected Vector couponlist = null;
	protected ArrayList coupondetail = null;
	protected HykInfoQueryBS hyk = null;
	protected int fjktypeChoice = -1;
	protected Vector rulelist;
	private boolean ch;

	public boolean findHYK(String track1, String track2, String track3)
	{
		hyk = CustomLocalize.getDefault().createHykInfoQueryBS();
		String[] s = parseFjkTrack(track1, track2, track3);
		cust = hyk.findMemberCard(s[1]);

		if (cust == null) { return false; }

		coupon = CreatePayment.getDefault().getPaymentCoupon();

		if (coupon.findFjk(track1, s[1], track3) && coupon.initList())
		{
			couponlist = coupon.couponList;
		}

		double zje = 0;
		if (couponlist != null)
		{
			for (int i = 0; i < couponlist.size(); i++)
			{
				String[] row = (String[]) couponlist.elementAt(i);
				zje += Convert.toDouble(row[2]);
			}
		}

		if (zje > 0)
		{
			ArrayList list = new ArrayList();

			if (coupon.findFjkInfo(track1, coupon.mzkret.cardno, track3, list))
			{
				if (list.size() > 0)
				{
					coupondetail = list;
				}
			}
		}

		return true;
	}

	public void displayBaseInfo(StyledText text,CustomerDef cust)
	{
		StringBuffer info = new StringBuffer();
		info.append(Language.apply("卡号:")+ Convert.appendStringSize(" ", cust.code, 1, 16, 16, 0));
		info.append(Language.apply("手机号:") + Convert.appendStringSize(" ", cust.valstr1.trim(),1, 13, 13, 0));
		info.append(Language.apply("卡类别:") + Convert.appendStringSize(" ", cust.type, 1, 8, 8, 0));
		info.append(Language.apply("积分功能:") + Convert.appendStringSize(" ", getFuncText(cust.isjf), 1, 6, 6, 0));
		info.append(Language.apply("卡积分:") + Convert.appendStringSize(" ",ManipulatePrecision.doubleToString(cust.valuememo), 1, 10, 10, 0));
		info.append(Language.apply("未销天数:") + Convert.appendStringSize(" ", cust.valstr8, 1, 8, 8, 0) + "\n\n");
		
		info.append(Language.apply("姓名:") + Convert.appendStringSize(" ",cust.name , 1, 16, 16, 0));
		info.append(Language.apply("性  别:") + Convert.appendStringSize(" ", cust.valstr4, 1, 13, 13, 0) );
		info.append(Language.apply("折扣率:") + Convert.appendStringSize(" ", cust.valstr5, 1, 8, 8, 0));
		info.append(Language.apply("折扣功能:") + Convert.appendStringSize(" ", getFuncText(cust.iszk), 1, 6, 6, 0));
		info.append(Language.apply("有效期:") + Convert.appendStringSize(" ", cust.maxdate, 1, 16, 16, 0)+ "\n");
		
//      测试数据
//		info.append(Language.apply("卡号:")+ Convert.appendStringSize("", cust.code, 1, 16, 16, 0));
//		info.append(Language.apply("手机号:") + Convert.appendStringSize("", "13031938231", 1, 13, 13, 0));
//		info.append(Language.apply("卡类别:") + Convert.appendStringSize("", cust.type, 1, 8, 8, 0));
//		info.append(Language.apply("积分功能:") + Convert.appendStringSize("", getFuncText(cust.isjf), 1, 6, 6, 0));
//		info.append(Language.apply("卡积分:") + Convert.appendStringSize("",ManipulatePrecision.doubleToString(cust.valuememo), 1, 10, 10, 0));
//		info.append(Language.apply("未销天数:") + Convert.appendStringSize("", "32天", 1, 8, 8, 0) + "\n\n");
//		
//		info.append(Language.apply("姓名:") + Convert.appendStringSize("",cust.name , 1, 16, 16, 0));
//		info.append(Language.apply("性  别:") + Convert.appendStringSize("", "女", 1, 13, 13, 0) );
//		info.append(Language.apply("折扣率:") + Convert.appendStringSize("", "0.712", 1, 8, 8, 0));
//		info.append(Language.apply("折扣功能:") + Convert.appendStringSize("", getFuncText(cust.iszk), 1, 6, 6, 0));
//		info.append(Language.apply("有效期:") + Convert.appendStringSize("", cust.maxdate, 1, 16, 16, 0)+ "\n");
		text.setText(info.toString());
	}

	protected String getFuncText(char bz)
	{
		if (bz == 'Y')
		{
			return Language.apply("是");
		}
		else
		{
			return Language.apply("否");
		}
	}

	protected boolean isLczcFunc(CustomerDef cust)
	{
		if ((cust.func != null) && (cust.func.length() >= 1) && (cust.func.charAt(0) == 'Y')) { return true; }

		return false;
	}

	public void displayCouponValue(PosTable table)
	{
		if ((couponlist == null) || (couponlist.size() <= 0)) { return; }

		String[] title = { Language.apply("券名称"), Language.apply("券余额") };
		int[] width = { 100, 100 };
		table.setTitle(title);
		table.setWidth(width);

		table.initialize();

		Vector v = new Vector();

		for (int i = 0; i < couponlist.size(); i++)
		{
			String[] row = (String[]) couponlist.elementAt(i);
			v.add(new String[] { row[1], row[2] });
		}

		table.exchangeContent(v);
	}

	public void displayCouponDetail(PosTable table,Vector saleinfo)
	{
		if (saleinfo != null || saleinfo.size()>0)
		{
			String[] title = { Language.apply("序"), Language.apply("柜组"), Language.apply("商品编码"), Language.apply("商品名称"), Language.apply("品牌"), Language.apply("尺码"), Language.apply("颜色"),Language.apply("季节"), Language.apply("数量"), Language.apply("售价"), Language.apply("成交价"), Language.apply("折扣"), Language.apply("销售日期")};
			int[] width = { 40, 110, 100, 100, 90, 65, 50, 50, 50, 80, 80, 80, 125 };
			table.setTitle(title);
			table.setWidth(width);

			table.initialize();

			Vector v = new Vector();

			for (int i = 0; i < saleinfo.size(); i++)
			{
				String[] infotemp = (String[]) saleinfo.get(i);
				String[] info = new String[] {String.valueOf(i+1),infotemp[0],infotemp[1],infotemp[2],infotemp[3],infotemp[4],infotemp[5],infotemp[6],infotemp[7],infotemp[8],infotemp[9],infotemp[10],infotemp[11]};;
				v.add(info);
			}

			table.exchangeContent(v);
		}
	}

	public int choicFjkType()
	{
		fjktypeChoice = -1;
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 获取自定义的解析规则
		rulelist = bs.showRule();
		if (rulelist != null && rulelist.size() <= 0) rulelist = null;

		// 先选择规则后刷会员卡
		if (GlobalInfo.sysPara.unionVIPMode == 'A')
		{
			if (rulelist != null && rulelist.size() > 1)
			{
				Vector con = new Vector();
				for (int i = 0; i < rulelist.size(); i++)
				{
					CustFilterDef filterDef = (CustFilterDef) rulelist.elementAt(i);
					con.add(new String[] { filterDef.desc });
				}
				String[] title = { Language.apply("会员卡类型") };
				int[] width = { 500 };

				int choice = new MutiSelectForm().open(Language.apply("请选择卡类型"), title, width, con);
				fjktypeChoice = choice;

				if (choice != -1)
				{
					CustFilterDef rule = ((CustFilterDef) rulelist.elementAt(choice));
					rulelist.removeAllElements();
					rulelist.add(rule);
				}

				if (rulelist != null) ch = true;
			}
		}

		return fjktypeChoice;
	}

	public String[] parseFjkTrack(String track1, String track2, String track3)
	{
		String[] s = new String[3];
		HykInfoQueryBS bs = CustomLocalize.getDefault().createHykInfoQueryBS();
		// 检查磁道是否和规则相匹配
		if (rulelist != null && rulelist.size() > 0)
		{
			rulelist = bs.chkTrack(track1, track2, track3, rulelist,false);
		}

		// 如果匹配的规则有多个,再次让客户选择(B模式先刷卡后选择)
		if (rulelist != null && rulelist.size() > 1)
		{
			rulelist = bs.chooseRule(rulelist,false);
		}

		// 解析有效规则下的磁道号
		if (rulelist != null && rulelist.size() > 0)
		{
			track2 = bs.getTrackByDefine(track1, track2, track3, rulelist);
		}
		else
		{
			if (ch)
			{
				new MessageBox(Language.apply("刷卡与联名卡规则不匹配，该卡无效"));
				return null;
			}
		}

		s[0] = track1;
		s[1] = track2;
		s[2] = track3;

		return s;
	}

	public void print(boolean reprint)
	{
		// 通过java_getsalems打印券
		if (cust == null) return ;
		/**
		StringBuffer buff = new StringBuffer();
		if (!new TextBox().open("请输入打印的张数", "张数", "请输入打印的张数", buff, 0.01, 100, true, TextBox.IntegerInput))
		{
			return ;
		}
		*/
		String flg = "N";
		if (reprint)
		{
			flg = "Y";
		}
		try{
			Vector v = new Vector();
	        if (NetService.getDefault().getSaleTicketMSInfo(v, GlobalInfo.sysPara.mktcode, GlobalInfo.syjDef.syjh, "0000", cust.code+",1,"+flg.toString(), NetService.getDefault().getMemCardHttp(CmdDef.GETMSINFO)))
	        {
	        	for (int i = 0; i < v.size(); i++)
	        	{
	        		SaleHeadDef salehead = new SaleHeadDef();
	        		if (reprint) salehead.printnum = 2;
	        		GiftGoodsDef def = (GiftGoodsDef) v.elementAt(i);
	        		GiftBillMode.getDefault(def.type).setTemplateObject(salehead, def);
	            	GiftBillMode.getDefault(def.type).PrintGiftBill();
	            	Printer.getDefault().cutPaper_Journal();
	        	}
	        	
	        	new MessageBox(Language.apply("重新打印完毕"));
	        }
		}catch(Exception er)
		{
			er.printStackTrace();
		}
		
	}
	
	public void specialDeal(CouponQueryInfoEvent event)
	{
	}
}
