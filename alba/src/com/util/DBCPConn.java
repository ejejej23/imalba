package com.util;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBCPConn {
	private static DataSource ds = null;

	private DBCPConn() {

	}

	public static Connection getConnection() {
		Connection conn = null;

		try {
			if (ds == null) {
				// context.xml파일을 읽어 Context객체를 생성
				Context ctx = new InitialContext();
				// 자바 환경설정값을 이용하여 객체 검색
				Context context = (Context) ctx.lookup("java:/comp/env");
				// jdbc/myoracle이라는 이름의 JNDI객체를 찾아 반환
				ds = (DataSource) context.lookup("jdbc/myoracle");
			}

			conn = ds.getConnection();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return conn;
	}

	public static void close(Connection conn) {
		if (conn == null)
			return;

		try {
			if (!conn.isClosed())
				conn.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		conn = null;
	}
}
