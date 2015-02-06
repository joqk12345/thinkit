package org.thinkit.data.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.thinkit.data.vo.MCHEntity;

public class MysqlHelper {
	private static Connection conn = null;
	
	private static Statement st; 
	
	public static  Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");// 加载Mysql数据驱动
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://192.168.14.21:3306/MHRtable", "root", "");// 创建数据连接
		} catch (Exception e) {
			System.out.println("数据库连接失败" + e.getMessage());
		}
		return conn;	//返回所建立的数据库连接
	}
	
	
	public  void insertIdentifyInfo(MCHEntity mch){
//		conn = getConnection();	//同样先要获取连接，即连接到数据库
		
	     Connection con = null;
	     Statement st; 
	        try {
	            con = getConnection();
	            String sql = "INSERT INTO MCH(uuid, speechid)"
						+ " VALUES ('"+mch.getUUID()+"','"+mch.getSpeechid()+"')";	// 插入数据的sql语句
				System.out.println("sql"+sql);
				st = (Statement) con.createStatement();	// 创建用于执行静态sql语句的Statement对象
				int count = st.executeUpdate(sql);	// 执行插入操作的sql语句，并返回插入数据的个数
				System.out.println("向identifyinfo表中插入 " + count + " 条数据");	//输出插入操作的处理结果
	        } catch (Exception e) {
	        	e.printStackTrace();
	        } finally {
	            if (con != null) {
	                try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
						System.out.println("插入数据失败" + e.getMessage());
					}
	            }
	        }
	}
	
	public  String selectIdentifyInfo(String sid){
		
	     Connection con = null;
	     Statement st = null; 
	     ResultSet rs = null;
	     String rest = null;
	        try {
	            con = getConnection();
//	            String sql = "INSERT INTO MCH(uuid, speechid)" + " VALUES ('"+mch.getUUID()+"','"+mch.getSpeechid()+"')";	// 插入数据的sql语句
	            
	            String sql = "select uuid from  MCH where speechid="+sid+" limit 1";	// 插入数据的sql语句
				System.out.println("sql"+sql);
				st = (Statement) con.createStatement();	// 创建用于执行静态sql语句的Statement对象
				rs  = st.executeQuery(sql);
				
				while (rs.next()) {
					System.out.println(rs.getString("uuid"));
					rest = rs.getString("uuid");
				}
//				System.out.println("向identifyinfo表中插入 " + count + " 条数据");	//输出插入操作的处理结果
	        } catch (Exception e) {
	        	e.printStackTrace();
	        } finally {
	        	
	        	if (st != null) {
	        		try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
	        		st = null;
				}
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					rs = null;
				}
	            if (con != null) {
	                try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
						System.out.println("查询数据失败" + e.getMessage());
					}
	            }
	        }
	        return rest;
	}
	
	
	
	public static void main(String args[]){
		
		MysqlHelper mh = new MysqlHelper();
//		MCHEntity mch = new MCHEntity("jdkaljfa;","15014.v3");
//		mh.insertIdentifyInfo(mch);
		mh.selectIdentifyInfo("0001");
//		System.out.println(mh.getConnection());
		
	}
}
