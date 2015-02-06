package org.thinkit.data.phoenix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class test {

	public static void main(String[] args) throws SQLException{
		Statement stmt = null;
		ResultSet rset = null;
		
		Connection con = DriverManager.getConnection("jdbc:phoenix:master:2181");
		stmt = con.createStatement();
		
		stmt.executeUpdate("create table test1 (mykey integer not null primary key, mycolumn varchar)");
		stmt.executeUpdate("upsert into test values (1,'Hello')");
		stmt.executeUpdate("upsert into test values (2,'World!')");
		con.commit();
		
		PreparedStatement statement = con.prepareStatement("select * from test1");
		rset = statement.executeQuery();
		while (rset.next()) {
			System.out.println(rset.getString("mycolumn"));
		}
		statement.close();
		con.close();

	}

}
