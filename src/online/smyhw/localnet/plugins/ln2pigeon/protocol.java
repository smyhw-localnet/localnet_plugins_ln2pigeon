package online.smyhw.localnet.plugins.ln2pigeon;

import java.util.List;

import online.smyhw.localnet.LN;
import online.smyhw.localnet.message;
import online.smyhw.localnet.data.DataPack;
import online.smyhw.localnet.lib.Json;
import online.smyhw.localnet.lib.Exception.Json_Parse_Exception;
import online.smyhw.localnet.network.Client_sl;
import online.smyhw.localnet.network.protocol.StandardProtocol;

public class protocol implements StandardProtocol {
	Client_sl upClient;
	public protocol(List input,Client_sl sy)
	{
		this.upClient = sy;
	}

	/**
	 * ֻʵ��message��������ӡ��console�ͳ�
	 * ���Ǵ�localnet��ȡ��Ϣ�ķ���
	 */
	@Override
	public void SendData(DataPack data) {
		if(data.getValue("type").equals("message"))
		{
			String msg = "<"+LN.ID+">:"+data.getValue("message");
			forPigeon.sendMsg(msg);
			return;
		}
		if(data.getValue("type").equals("forward_message"))
		{
			String msg = "<"+data.getValue("From")+">:"+data.getValue("message");
			forPigeon.sendMsg(msg);
			return;
		}
		message.warning("[ln2pigeon][Э��]:��֧�ֵ���Ϣ<"+data.getStr()+">(�ⲻ�Ǵ���ֻ���ҹ������˶���)");
		return;
		
	}
	
	public boolean SendTo_localnet(String msg)
	{
		DataPack dp;
		try 
		{
			msg = Json.Encoded(msg);
			dp = new DataPack("{\"type\":\"message\",\"message\":\""+msg+"\"}");
		} catch (Json_Parse_Exception e) {
			message.warning("[ln2pigeon]:��localnet������Ϣ����<"+msg+">", e);
			return false;
		}
		this.upClient.CLmsg(dp);
		return true;
	}
	
	/**
	 * ���ֱ�ӷ���JSON������Դ����ȷ��
	 * @param msg
	 * @return
	 */
	public boolean ALL_SendTo_localnet(String msg)
	{
		DataPack dp;
		try 
		{
			dp = new DataPack(msg);
		} catch (Json_Parse_Exception e) {
			message.warning("[ln2pigeon]:��localnet��������Ϣ����<"+msg+">", e);
			return false;
		}
		this.upClient.CLmsg(dp);
		return true;
	}
	

	@Override
	public void Disconnect() {
		message.warning("[ln2pigeon]:����ͻ��˱�localnetҪ��Ͽ�����");
	}

}
