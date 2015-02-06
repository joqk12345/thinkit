package org.thinkit.data.phoenix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

public class HBaseSQLDriverTest {
	static final String SQL_END_TOKEN = ";";

	static final String JDBC_DRIVER_CLASS = "com.salesforce.phoenix.jdbc.PhoenixDriver";
	static final String JDBC_URL = "jdbc:phoenix:master,thinkit-4,compute1:2181";
	static final String USER = null;
	static final String PASSWORD = null;

	public static Connection getConnection(){
			Connection con ;
	 		try {
				con = DriverManager.getConnection(JDBC_URL);
				return con;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
 	}
	
	public void query(String sql) throws Exception{
		
		
		Connection con =getConnection();
		Statement stmt =con.createStatement();
		ResultSet rs =stmt.executeQuery(sql);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount =rsmd.getColumnCount();
		StringBuilder sb =new StringBuilder();
		for(int i=1;i<=columnCount;i++){
			String columnName = rsmd.getCatalogName(i);
			sb.append(columnName +"\t");
		}
		if(sb.length()>0)
			sb.setLength(sb.length()-1);
		System.out.println(sb.toString());
		
		//获取结果
		while(rs.next()){
			sb = new StringBuilder();
			for(int i=1;i<=columnCount;i++){
				sb.append(rs.getString(i)+"\t");
			}
			if(sb.length()>0)
				sb.setLength(sb.length()-1);
			System.out.println(sb.toString());
		}
		con.close();
	}
	
	
	public static void main(String[] args) throws Exception {
		HBaseSQLDriverTest test =new HBaseSQLDriverTest();
		test.query("Select * from web_stat where ACTIVE_VISITOR >200");
		
	}
}
