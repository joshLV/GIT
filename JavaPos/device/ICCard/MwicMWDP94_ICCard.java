package device.ICCard;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Vector;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Device.DeviceName;

public class MwicMWDP94_ICCard extends Mwic_ICCard
{
	public boolean open()
	{
		return true;
	}
	
	public boolean close()
	{
		return true;
	}

	public String getDiscription()
	{
		return "明华IC卡MWDP94设备";
	}

	public Vector getPara()
	{
		Vector v = new Vector();
		v.add(new String[]{"端口号","0","1","2","3","0x378"});
		v.add(new String[]{"波特率","9600","110","300","600","1200","2400","4800","19200"});
		v.add(new String[]{"卡号偏移位"});
		v.add(new String[]{"卡号的长度"});
		v.add(new String[]{"读卡成功蜂鸣时长(ms)"});
		v.add(new String[]{"读卡失败蜂鸣时长(ms)"});
		v.add(new String[]{"javaposbank.exe参数行"});
		return v;
	}

	public String findCard()
	{
    	if (DeviceName.deviceICCard.length() <= 0) return null;
    	
        try
        {    	
	        String[] arg = DeviceName.deviceICCard.split(",");
	
        	StringBuffer line = new StringBuffer();
	        	
        	if (arg.length > 0) line.append(arg[0]);
        	else line.append("3");
	        line.append(",");
	        
            if (arg.length > 1) line.append(arg[1]);
            else line.append("9600");
	        line.append(",");

            if (arg.length > 2) line.append(arg[2]);
            else line.append("33");
	        line.append(",");
	        
            if (arg.length > 3) line.append(arg[3]);
            else line.append("9");
	        line.append(",");
	        

            String cmd = "";
            
            if (arg.length > 6 && arg[6].trim().length() > 0) cmd = arg[6];
            else cmd = "MWICRDEB2";
            
			//	先删除上次交易数据文件
			if (PathFile.fileExist("request.txt"))
			{
				PathFile.deletePath("request.txt");
			   
				if (PathFile.fileExist("request.txt"))
				{
					new MessageBox("读卡请求文件request.txt无法删除,请重试");
					return null;   	
				}
			}
			if (PathFile.fileExist("result.txt"))
			{
				PathFile.deletePath("result.txt");
			   
				if (PathFile.fileExist("result.txt"))
				{
					new MessageBox("读卡结果文件result.txt无法删除,请重试");
					return null;   	
				}
			}
			
			// 写入请求
			PrintWriter pw = CommonMethod.writeFile("request.txt");
			pw.write(line.toString());
			pw.close();
			
            // 调用接口模块
            if (PathFile.fileExist("javaposbank.exe"))
            {
            	CommonMethod.waitForExec("javaposbank.exe " + cmd);
            }
            else
            {
                new MessageBox("找不到IC卡模块 javaposbank.exe");
                return null;
            }

            // 读取应答
            BufferedReader br = null;
            if (!PathFile.fileExist("result.txt") || ((br = CommonMethod.readFileGBK("result.txt")) == null))
            {
                new MessageBox("读取卡号应答数据失败!");
                return null;
            }
            String cardno = br.readLine();
            if(cardno != null){
            	 String result[] = cardno.split(",");
            	if(result[0].equals("00"))cardno=result[1];else cardno=result[1];
            }
            br.close();
            
            return cardno;
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	return null;
        }
	}
	
	public String updateCardMoney(String cardno, String operator, double ye)
	{
		return "error:该设备不支持本功能";
	}
}
