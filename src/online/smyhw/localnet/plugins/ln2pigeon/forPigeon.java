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
			message.warning("[ln2pigeon]向pigeon发送消息出错<"+msg+">", e);
		}
	}
	
	/**
	 * 从pigeon取回一条消息<br>
	 * 返回null表示没有新消息<br>
	 * 第一次调用一定会返回null
	 * @return 取回的消息
	 */
	static long lasteMsg = -1;
	public static String getMsg() {
		try 
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection  conn = DriverManager.getConnection(lnp.CLconfig.get_String("JDBC", "jdbc:mysql://localhost:3306/pigeon?useSSL=false"),lnp.CLconfig.get_String("mysqlUserName", "root"),lnp.CLconfig.get_String("mysqlPasswd", "root"));
			Statement stmt = conn.createStatement();
			if(lasteMsg==-1) 
			{//如果是第一次调用，检查最新消息ID
				ResultSet rs = stmt.executeQuery("select `id` from `posts` ORDER BY `id` DESC LIMIT 1;");
				rs.next();
				lasteMsg =rs.getInt("id");
				conn.close();
				return null;//无论如何，都会返回null
			}//因为上面无论如何都返回了null,所以这边不需要else
			ResultSet rs = stmt.executeQuery("SELECT `id`,`content`,`author`,`public` FROM `posts` WHERE `id`>"+lasteMsg+" ORDER BY `id` DESC;");
			while(rs.next()) 
			{
				lasteMsg = rs.getInt("id");
				if(!rs.getString("public").equals("0")) {continue;}//非公开的消息不转发
				if(rs.getString("author").equals(lnp.CLconfig.get_String("pnID","localnet"))){continue;}//如果是机器人自己发的消息，不转发
				String msg = rs.getString("author");
				msg = msg+":"+rs.getString("content");
				conn.close();
				return msg;
			}
			conn.close();
			return null;//这里表示没有新消息
		}
		catch (ClassNotFoundException e) 
		{
			message.warning("[ln2pigeon]找不到数据库驱动，请向开发者反馈！", e);
			return null;
		}
		catch (SQLException e) {
			message.warning("[ln2pigeon]处理JDBC错误", e);
			return null;
		}
	}

}
