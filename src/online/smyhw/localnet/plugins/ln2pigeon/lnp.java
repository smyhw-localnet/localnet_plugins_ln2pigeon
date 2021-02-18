package online.smyhw.localnet.plugins.ln2pigeon;

import java.util.ArrayList;
import java.util.List;

import online.smyhw.localnet.message;
import online.smyhw.localnet.command.cmdManager;
import online.smyhw.localnet.data.DataManager;
import online.smyhw.localnet.data.DataPack;
import online.smyhw.localnet.data.config;
import online.smyhw.localnet.event.ChatINFO_Event;
import online.smyhw.localnet.event.EventManager;
import online.smyhw.localnet.lib.Exception.Json_Parse_Exception;
import online.smyhw.localnet.network.Client_sl;

public class lnp 
{
	public static config CLconfig;
	public static Client_sl localnetClient;
	public static void plugin_loaded()
	{
		message.info("ln2pigeon插件加载...");
		//这里可以读取配置文件
		//建议还是放在configs文件夹里，用户找得时候会方便点
		CLconfig = DataManager.LoadConfig("./configs/ln2pigeon.config");
		message.info("初始化localnet客户端");
		List tmp1 = new ArrayList();
		localnetClient = new Client_sl("online.smyhw.localnet.plugins.ln2pigeon.protocol", tmp1);
		try {localnetClient.CLmsg(new DataPack("{\"type\":\"auth\",\"ID\":\""+ lnp.CLconfig.get_String("lnID","pigeon")+"\"}"));} catch (Json_Parse_Exception e) {e.printStackTrace();}//这不该出现异常
		message.info("初始化轮询线程localnet客户端");
		new getMsgFromPigeonThread();
	}
}

class getMsgFromPigeonThread extends Thread
{
	public getMsgFromPigeonThread() 
	{
		this.start();
	}
	
	public void run()
	{
		while(true)
		{
			String re = forPigeon.getMsg();
			if(re!=null) 
			{//如果有消息
				((protocol)lnp.localnetClient.protocolClass).SendTo_localnet(re);
				continue;//如果同步了，就跳过延迟，立即开始下一次轮询
			}
			try {Thread.sleep(1000);} catch (InterruptedException e) {message.warning("[ln2pigeon]轮询Pigeon数据库时延迟出错", e);}
		}
	}
}
