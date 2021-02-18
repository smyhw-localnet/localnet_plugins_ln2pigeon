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
		message.info("ln2pigeon�������...");
		//������Զ�ȡ�����ļ�
		//���黹�Ƿ���configs�ļ�����û��ҵ�ʱ��᷽���
		CLconfig = DataManager.LoadConfig("./configs/ln2pigeon.config");
		message.info("��ʼ��localnet�ͻ���");
		List tmp1 = new ArrayList();
		localnetClient = new Client_sl("online.smyhw.localnet.plugins.ln2pigeon.protocol", tmp1);
		try {localnetClient.CLmsg(new DataPack("{\"type\":\"auth\",\"ID\":\""+ lnp.CLconfig.get_String("lnID","pigeon")+"\"}"));} catch (Json_Parse_Exception e) {e.printStackTrace();}//�ⲻ�ó����쳣
		message.info("��ʼ����ѯ�߳�localnet�ͻ���");
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
			{//�������Ϣ
				((protocol)lnp.localnetClient.protocolClass).SendTo_localnet(re);
				continue;//���ͬ���ˣ��������ӳ٣�������ʼ��һ����ѯ
			}
			try {Thread.sleep(1000);} catch (InterruptedException e) {message.warning("[ln2pigeon]��ѯPigeon���ݿ�ʱ�ӳٳ���", e);}
		}
	}
}
