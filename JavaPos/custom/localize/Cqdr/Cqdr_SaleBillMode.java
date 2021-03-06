package custom.localize.Cqdr;

import java.io.File;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulateDateTime;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Global.AccessLocalDB;
import com.efuture.javaPos.Global.DataService;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.SellType;
import com.efuture.javaPos.PrintTemplate.PrintTemplateItem;
import com.efuture.javaPos.PrintTemplate.SaleBillMode;
import com.efuture.javaPos.Struct.GiftGoodsDef;
import com.efuture.javaPos.Struct.OperUserDef;
import com.efuture.javaPos.Struct.SaleGoodsDef;
import com.efuture.javaPos.Struct.SalePayDef;

public class Cqdr_SaleBillMode extends SaleBillMode {

	protected String getItemDataString(PrintTemplateItem item, int index)
	{

		String line = null;

		line = extendCase(item, index);

		String text = item.text;

		if (line == null)
		{
			switch (Integer.parseInt(item.code))
			{
				case SBM_text: // 文本

					if (text == null)
					{
						line = "";
					}
					else
					{
						if (text.trim().indexOf("calc|") == 0)
						{
							line = super.calString(text, index);
						}
						else
						{
							line = text;
						}
					}

					break;

				case SBM_mktname: // 商场名称

					if (GlobalInfo.sysPara.mktname != null)
					{
						line = GlobalInfo.sysPara.mktname;
					}
					else
					{
						line = "";
					}

					break;

				case SBM_syjh: // 收银机号
					line = GlobalInfo.syjStatus.syjh;

					break;

				case SBM_gh: // 收银员号
					line = salehead.syyh;

					break;

				case SBM_name: // 收银员名称

					if (salehead.syyh.trim().equals(GlobalInfo.posLogin.gh.trim()))
					{
						line = GlobalInfo.posLogin.name;
					}
					else
					{
						OperUserDef staff = new OperUserDef();

						if (!DataService.getDefault().getOperUser(staff, salehead.syyh.trim()))
						{
							line = "";
						}
						else
						{
							line = staff.name;
						}
					}

					break;

				case SBM_fphm: // 小票号码
					line = Convert.increaseLong(salehead.fphm, 8);

					break;

				case SBM_rq: // 交易日期
					line = salehead.rqsj.split(" ")[0];

					break;

				case SBM_sj: // 交易时间
					line = salehead.rqsj.split(" ")[1];

					break;

				case SBM_printrq: // 打印日期
					line = ManipulateDateTime.getCurrentDate();

					break;

				case SBM_printsj: // 打印时间
					line = ManipulateDateTime.getCurrentTime();

					break;

				case SBM_index: // 商品序号
					line = String.valueOf(index + 1);

					break;

				case SBM_code: // 商品代码
					line = ((SaleGoodsDef) salegoods.elementAt(index)).code;

					break;

				case SBM_goodname: // 商品名称
					line = ((SaleGoodsDef) salegoods.elementAt(index)).name;
					// 记录商品所能打印的最大长度
					goodnamemaxlength = item.length;

					break;

				case SBM_goodnamebreak: // 需要换行打印的商品名称

					String goodname = ((SaleGoodsDef) salegoods.elementAt(index)).name;

					// 商品行不够打印商品名称的时候
					if (goodnamemaxlength < goodname.length())
					{
						// 将打不出来的部分赋值给商品名称换行打印项
						line = Convert.newSubString(goodname, goodnamemaxlength, goodname.getBytes().length);
					}
					else
					{
						line = "";
					}

					break;

				case SBM_sl: // 数量
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).sl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);

					break;

				case SBM_jg: // 售价
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).jg);

					break;

				case SBM_sjje: // 售价金额（数量*售价）
					line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_dphjzk: // 单品合计折扣

					if (((SaleGoodsDef) salegoods.elementAt(index)).hjzk == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(((SaleGoodsDef) salegoods.elementAt(index)).hjzk * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_hjzsl: // 总件数
					line = ManipulatePrecision.doubleToString(salehead.hjzsl * SellType.SELLSIGN(salehead.djlb), 4, 1, true);

					break;

				case SBM_hjzke: // 总折扣

					if (salehead.hjzke == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.hjzke * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_ysje: // 应收金额
					line = ManipulatePrecision.doubleToString(salehead.ysje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_yfje: // 应付金额
					line = ManipulatePrecision.doubleToString((salehead.ysje + salehead.sswr_sysy) * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_sjfk: // 实收金额
					line = ManipulatePrecision.doubleToString(salehead.sjfk * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_zl: // 找零金额
					line = ManipulatePrecision.doubleToString(salehead.zl);

					break;

				case SBM_hykh: // 会员卡号

					if ((salehead.hykh == null) || (salehead.hykh.length() <= 0))
					{
						line = null;
					}
					else
					{
						line = salehead.hykh;
					}

					break;

				case SBM_sqkh: // 授权卡号

					if ((salehead.sqkh == null) || (salehead.sqkh.length() <= 0))
					{
						line = null;
					}
					else
					{
						line = salehead.sqkh;
					}

					break;

				case SBM_payname: // 付款方式名称
					line = ((SalePayDef) salepay.elementAt(index)).payname;

					break;

				case SBM_ybje: // 付款方式金额
					line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).ybje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_payno: // 付款方式帐号
					String payno = ((SalePayDef) salepay.elementAt(index)).payno;
					
					String code = ((SalePayDef) salepay.elementAt(index)).paycode;
					

					if (new File(GlobalVar.ConfigPath + "//HidePaycode.ini").exists())
					{
						if (hidePayCode == null) readHidePayCode();
					    line = hidePayNo(code,payno);
					}
					else
					{
						line = payno;
					}
							
					if ((line == null) || (line.length() <= 0))
					{
						line = null;
					}
																							
					break;

				case SBM_djlb: // 交易类型
					line = String.valueOf(SellType.getDefault().typeExchange(salehead.djlb, salehead.hhflag, salehead));

					break;

				case SBM_sysy: // 收银损溢金额

					if ((salehead.sswr_sysy + salehead.fk_sysy) == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.sswr_sysy + salehead.fk_sysy);
					}

					break;

				case SBM_printnum: // 重打小票标志及重打次数

					if (salehead.printnum == 0)
					{
						line = null;
					}
					else
					{
						line = "**重印" + salehead.printnum + "**";
					}

					break;

				case SBM_inputbarcode: // 打印输入商品编码

					if (((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode != null && ((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode.trim().length() > 0)
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).inputbarcode;
					}
					else if (GlobalInfo.syjDef.issryyy == 'N')
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).barcode;
					}
					else
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).code;
					}

					break;

				case SBM_barcode: // 打印输入商品编码
					line = ((SaleGoodsDef) salegoods.elementAt(index)).barcode;

					break;

				case SBM_unit: // 商品单位
					line = String.valueOf(((SaleGoodsDef) salegoods.elementAt(index)).unit);

					break;

				case SBM_cjje: // 成交金额
					line = ManipulatePrecision.doubleToString((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk) * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_cjdj: // 成交单价
					line = ManipulatePrecision.doubleToString(ManipulatePrecision.div((((SaleGoodsDef) salegoods.elementAt(index)).hjje - ((SaleGoodsDef) salegoods.elementAt(index)).hjzk), ((SaleGoodsDef) salegoods.elementAt(index)).sl));

					break;

				case SBM_jfkh: // 积分卡号

					if (salehead.jfkh.length() <= 0)
					{
						line = null;
					}
					else
					{
						line = String.valueOf(salehead.jfkh);
					}

					break;

				case SBM_bcjf: // 本次积分

					if (salehead.bcjf == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.bcjf);
					}

					break;

				case SBM_yyyh: // 营业员号
					line = String.valueOf(((SaleGoodsDef) salegoods.elementAt(index)).yyyh);

					break;

				case SBM_ysjedx: // 人民币大写应收金额
					line = ManipulatePrecision.getFloatConverChinese(salehead.ysje);

					break;

				case SBM_mktcode: // 卖场代码
					line = GlobalInfo.sysPara.mktcode;

					break;

				case SBM_hyzke: // 会员折扣合计

					if (salehead.hyzke == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.hyzke * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_sqzkhj: // 授权折扣合计

					if (salehead.lszke == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.lszke * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_ljjf: // 累计积分

					if (salehead.ljjf == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.ljjf);
					}

					break;

				case SBM_gz: // 商品柜组
					line = ((SaleGoodsDef) salegoods.elementAt(0)).gz;

					Object obj1  = GlobalInfo.localDB.selectOneData("select NAME from MANAFRAME where GZ='" +line + "'" );
					if (obj1 != null && !String.valueOf(obj1).equals(""))
					{
						line = line+"/"+String.valueOf(obj1).trim();
					}
					
					break;
				case SBM_gzname: //商品柜组名称
					line = ((SaleGoodsDef) salegoods.elementAt(index)).gz;
					
					Object obj  = GlobalInfo.localDB.selectOneData("select NAME from MANAFRAME where GZ='" +line + "'" );
					if (obj != null && !String.valueOf(obj).equals(""))
					{
						line = String.valueOf(obj).trim();
					}
					else
					{
						line = "";
					}
					break;
				case SBM_ye: // 付款余额

					if (((SalePayDef) salepay.elementAt(index)).kye <= 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).kye);
					}

					break;

				case SBM_spzkbfb: // 总折扣百分比

					SaleGoodsDef saleGoodsDef = (SaleGoodsDef) salegoods.elementAt(index);

					// double je = saleGoodsDef.hjje - saleGoodsDef.hjzk;
					String zkbfb = ManipulatePrecision.doubleToString((saleGoodsDef.hjzk * 100) / saleGoodsDef.hjje, 1, 1, true);
					line = zkbfb + "%";

					break;

				case SBM_Aqje: // A券金额

					if ((salehead.memo != null) && (salehead.memo.split(",").length > 1))
					{
						String[] row = salehead.memo.split(",");
						double aje = Convert.toDouble(row[0]);

						if (aje > 0)
						{
							line = ManipulatePrecision.doubleToString(aje);
						}
					}

					break;

				case SBM_Bqje: // B券金额

					if ((salehead.memo != null) && (salehead.memo.split(",").length > 1))
					{
						String[] row = salehead.memo.split(",");
						double bje = Convert.toDouble(row[1]);

						if (bje > 0)
						{
							line = ManipulatePrecision.doubleToString(bje);
						}
					}

					break;

				case SBM_printinfo1: // 自定义打印信息
				case SBM_printinfo2: // 自定义打印信息
				{
					String printInfo = null;

					if (Integer.parseInt(item.code) == SBM_printinfo1)
					{
						printInfo = GlobalInfo.sysPara.printInfo1;
					}
					else
					{
						printInfo = GlobalInfo.sysPara.printInfo2;
					}

					if ((printInfo == null) || printInfo.trim().equals(""))
					{
						line = null;
					}
					else
					{
						line = null;

						String dt = new ManipulateDateTime().getDateByEmpty();
						String[] l = printInfo.split(";");

						for (int i = 0; i < l.length; i++)
						{
							String[] s = l[i].split(",");

							if (s.length < 3)
							{
								continue;
							}

							if ((dt.compareTo(s[0]) >= 0) && (dt.compareTo(s[1]) <= 0) && !s[2].trim().equals(""))
							{
								if (line == null)
								{
									line = "";
								}

								line += (s[2].trim() + "\n");
							}
						}

						if (line != null)
						{
							line = line.substring(0, line.length() - 1);
						}
					}

					break;
				}

				case SBM_Jfmemo:

					if ((salehead.str5 != null) && (salehead.str5.length() > 0))
					{
						line = salehead.str5;
					}

					break;

				case SBM_hjzje:

					if (salehead.hjzje == 0)
					{
						line = null;
					}
					else
					{
						line = ManipulatePrecision.doubleToString(salehead.hjzje * SellType.SELLSIGN(salehead.djlb));
					}

					break;

				case SBM_fpje:

					String[] paycodes = text.split("\\|");
					SalePayDef payDef = null;
					StringBuffer payInfo = new StringBuffer("发票金额:\n ");

					for (int i = 0; i < paycodes.length; i++)
					{
						for (int j = 0; j < salepay.size(); j++)
						{
							payDef = (SalePayDef) salepay.elementAt(j);

							if ((payDef.flag == '1') && payDef.paycode.equals(paycodes[i]))
							{
								payInfo.append(payDef.payname.trim() + ":" + ManipulatePrecision.doubleToString(payDef.ybje * SellType.SELLSIGN(salehead.djlb)) + "\n ");
							}
						}
					}

					text = "";
					line = payInfo.toString().trim();

					break;

				case SBM_payfkje:
					line = ManipulatePrecision.doubleToString(((SalePayDef) salepay.elementAt(index)).ybje * SellType.SELLSIGN(salehead.djlb));

					break;

				case SBM_paycode: // 付款方式代码
					line = ((SalePayDef) salepay.elementAt(index)).paycode;
					
					
					break;

				case SBM_changebillname: // 商品发票名称
					line = ((SaleGoodsDef) salegoods.elementAt(index)).str9;

					if (line == null || line.trim().length() < 1)
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).name;
					}

					break;

				case SBM_sjfkfpje:
					line = ManipulatePrecision.doubleToString(this.calcPayFPMoney() * SellType.SELLSIGN(salehead.djlb));
					break;
				case SBM_sjfkfpjedx:
					line = ManipulatePrecision.getFloatConverChinese(this.calcPayFPMoney() * SellType.SELLSIGN(salehead.djlb));
					break;

				case SBM_salefphm:// 打印收银员的发票编号
					line = Convert.increaseLong(this.salefph, item.length);
					break;
				case SBM_Memo:
					if (salemsgift != null)
					{
						for (int i = 0; i < salemsgift.size(); i++)
						{
							GiftGoodsDef def = (GiftGoodsDef) this.salemsgift.elementAt(i);
							if (def.type.equals("119"))
							{
								line = def.memo;
								break;
							}
						}
					}
					break;
				// 会员升级信息
				case SBM_hysjinfo:
					line = salehead.str4;
					break;
				// 收银机组
				case SBM_SyjGroup:
					line = GlobalInfo.syjDef.priv;
				//移动(在线)充值手机号码
				case SBM_phone: 
					if (AccessLocalDB.getDefault().checkMobileCharge(((SaleGoodsDef)salegoods.elementAt(0)).barcode) != null)
					{
						line = ((SaleGoodsDef) salegoods.elementAt(index)).batch;
					}
					else
					{
						line = null;
					}

					break;
			}
		}

		if ((line != null) && line.equals("&!"))
		{
			line = null;
		}

		// if (line != null && Integer.parseInt(item.code) != 0 && item.text !=
		// null && !item.text.trim().equals(""))
		if ((line != null) && (Integer.parseInt(item.code) != 0) && (text != null) && !text.trim().equals(""))
		{
			// line = item.text + line;
			int maxline = item.length - Convert.countLength(text);
			
			line = text + Convert.appendStringSize("", line, 0, maxline, maxline, item.alignment);
		}

		return line;
		
	}
}
