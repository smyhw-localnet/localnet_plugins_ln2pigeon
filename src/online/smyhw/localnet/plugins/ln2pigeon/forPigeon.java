package online.smyhw.localnet.plugins.ln2pigeon;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import online.smyhw.localnet.message;
import online.smyhw.localnet.lib.*;

public class forPigeon 
{
	public static void sendMsg(String msg) {
		try 
		{
			WebAPI.simplePost(lnp.CLconfig.get_String("pigeonURL", "https://127.0.0.1/")+"?s=newpost&token="+lnp.CLconfig.get_String("token","01234567890123456789012345678901"),"ispublic=0&content="+msg);
		} catch (Exception e) {
			message.warning("[ln2pigeon]��pigeon������Ϣ����<"+msg+">", e);
		}
	}
	
	/**
	 * ��pigeonȡ��һ����Ϣ<br>
	 * ����null��ʾû������Ϣ<br>
	 * ��һ�ε���һ���᷵��null
	 * @return ȡ�ص���Ϣ
	 */
	static long lasteMsg = -1;
	public static String getMsg() {
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection  conn = DriverManager.getConnection(lnp.CLconfig.get_String("JDBC", "jdbc:mysql://localhost:3306/pigeon?useSSL=false"),lnp.CLconfig.get_String("mysqlUserName", "root"),lnp.CLconfig.get_String("mysqlPasswd", "root"));
			Statement stmt = conn.createStatement();
			if(lasteMsg==-1) 
			{//����ǵ�һ�ε��ã����������ϢID
				ResultSet rs = stmt.executeQuery("select `id` from `posts` ORDER BY `id` DESC LIMIT 1;");
				rs.next();
				lasteMsg =rs.getInt("id");
				conn.close();
				return null;//������Σ����᷵��null
			}//��Ϊ����������ζ�������null,������߲���Ҫelse
			ResultSet rs = stmt.executeQuery("SELECT `id`,`content`,`author`,`public` FROM `posts` WHERE `id`>"+lasteMsg+" ORDER BY `id` DESC;");
			while(rs.next()) 
			{
				lasteMsg = rs.getInt("id");
				if(!rs.getString("public").equals("0")) {continue;}//�ǹ�������Ϣ��ת��
				if(rs.getString("author").equals(lnp.CLconfig.get_String("pnID","localnet"))){continue;}//����ǻ������Լ�������Ϣ����ת��
				String msg = rs.getString("author");
				msg = msg+":"+rs.getString("content");
				conn.close();
				return msg;
			}
			conn.close();
			return null;//�����ʾû������Ϣ
		}
		catch (ClassNotFoundException e) 
		{
			message.warning("[ln2pigeon]�Ҳ������ݿ����������򿪷��߷�����", e);
			return null;
		}
		catch (SQLException e) {
			message.warning("[ln2pigeon]����JDBC����", e);
			return null;
		}
	}

}
