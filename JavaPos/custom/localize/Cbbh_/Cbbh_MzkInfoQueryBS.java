package custom.localize.Cbbh;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Logic.MzkInfoQueryBS;
import com.efuture.javaPos.Payment.PaymentMzk;

public class Cbbh_MzkInfoQueryBS extends MzkInfoQueryBS
{

	protected void mzkDisplayInfo(PaymentMzk mzk)
	{
		StringBuffer info = new StringBuffer();

		// 组织提示信息
		//info.append(Language.apply("卡  号: ") + Convert.appendStringSize("", mzk.getDisplayCardno(), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("卡  号: ") + Convert.appendStringSize("", "**********", 1, 20, 20, 0) + "\n");
		//info.append(Language.apply("持卡人: ") + Convert.appendStringSize("", mzk.mzkret.cardname, 1, 20, 20, 0) + "\n");
		//info.append(Language.apply("卡状态: ") + Convert.appendStringSize("", mzk.mzkret.status, 1, 20, 20, 0) + "\n");
		//info.append(Language.apply("面  值: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.money), 1, 20, 20, 0) + "\n");
		info.append(Language.apply("余  额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye), 1, 20, 20, 0) + "\n");

		/*if (mzk.mzkret.str1 != null && mzk.mzkret.str1.trim().length() > 0)
		{
			info.append(Language.apply("有效期: ") + Convert.appendStringSize("", mzk.mzkret.str1, 1, 20, 20, 0) + "\n");
		}

		if (mzk.isRecycleType(mzk.mzkret.func))
		{
			info.append(Language.apply("工本费: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.value3), 1, 20, 20, 0) + "\n");
			info.append(Language.apply("有效额: ") + Convert.appendStringSize("", ManipulatePrecision.doubleToString(mzk.mzkret.ye - mzk.mzkret.value3), 1, 20, 20, 0)
					+ "\n");
		}*/

		// 弹出显示
		new MessageBox(info.toString());
	}
}
