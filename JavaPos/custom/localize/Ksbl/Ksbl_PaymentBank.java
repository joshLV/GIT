/**
 * 
 */
package custom.localize.Ksbl;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.javaPos.Logic.SaleBS;
import com.efuture.javaPos.Payment.PaymentBank;
import com.efuture.javaPos.Payment.Bank.PaymentBankFunc;
import com.efuture.javaPos.Struct.BankLogDef;
import com.efuture.javaPos.Struct.PayModeDef;
import com.efuture.javaPos.Struct.SaleHeadDef;
import com.efuture.javaPos.Struct.SalePayDef;

/**
 * @author tujie
 *
 */
public class Ksbl_PaymentBank extends PaymentBank
{
	public Ksbl_PaymentBank()
	{		
		super();
	}
	
	public Ksbl_PaymentBank(PayModeDef mode,SaleBS sale)
	{
		super(mode,sale);
	}
	
	public Ksbl_PaymentBank(SalePayDef pay,SaleHeadDef head)
	{
		super(pay,head);
	}
	
	public void accountPay(boolean ret, BankLogDef bld, PaymentBankFunc pbf)
	{
		if (!ret)
		{
			// 交易失败，放弃付款对象
			salepay = null;
		}
		else
		{
			// 交易成功，记录交易数据到付款对象，batch必须不为空,标记付款已记账
			salepay.payno = bld.cardno;
			salepay.batch = String.valueOf(bld.trace);
			salepay.str1 = salepay.batch;
			salepay.idno = salepay.batch;
			salepay.ybje = bld.je;
			salepay.je = ManipulatePrecision.doubleConvert(salepay.ybje * ((paymode != null) ? paymode.hl : 1));
			salepay.kye = bld.kye;
			salepay.memo = bld.memo;
			salepay.num3 = bld.ylzk;
			// 替换付款明细名称为银行名称
			if (bld.bankinfo != null)
			{
				String bankinfo = bld.bankinfo.trim();
				if (bankinfo.length() > 2 && pbf != null && pbf.getReplaceBankNameMode())
				{
					int p = bankinfo.indexOf("-");
					if (p > 0)
						salepay.payname = bankinfo.substring(p + 1);
					else
						salepay.payname = bankinfo;
				}
			}
		}

		// 更新付款断点数据，标记为已付款状态,否则在记账以后如果掉电,断点读入的还是未记账状态
		if (this.saleBS != null)
			this.saleBS.writeBrokenData();
	}
	
}
