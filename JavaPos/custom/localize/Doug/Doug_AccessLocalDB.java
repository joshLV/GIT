package custom.localize.Doug;

import com.efuture.javaPos.Global.GlobalInfo;

import custom.localize.Bhls.Bhls_AccessLocalDB;

public class Doug_AccessLocalDB extends Bhls_AccessLocalDB
{

	public void paraConvertByCode(String code, String value)
	{
		super.paraConvertByCode(code, value);
		
		try
		{
			if (code.equals("SH"))
			{
				GlobalInfo.sysPara.mername = value.trim();
				return;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
