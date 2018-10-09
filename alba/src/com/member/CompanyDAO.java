package com.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.util.DBCPConn;

public class CompanyDAO {
	public CompanyDTO readCompany(String comId) {
		CompanyDTO dto = null;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT COMID, PASSWORD, COMNAME, ");
			sb.append(" PHONENUM, ZIP, ADDRESS1, ADDRESS2, ");
			sb.append(" EMAIL, LOGOIMAGE, JOINED, ENABLED ");
			sb.append("FROM COMPANY ");
			sb.append("WHERE COMID=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, comId);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				dto = new CompanyDTO();
				dto.setComId(rs.getString("comId"));
				dto.setPassword(rs.getString("password"));
				dto.setComName(rs.getString("comname"));
				dto.setPhoneNum(rs.getString("phoneNum"));
				dto.setZip(rs.getString("zip"));
				dto.setAddress1(rs.getString("address1"));
				dto.setAddress2(rs.getString("address2"));
				dto.setEmail(rs.getString("email"));
				dto.setLogoImage(rs.getString("logoImage"));
				dto.setJoined(rs.getString("joined"));
				dto.setEnabled(rs.getInt("enabled"));
			}

		} catch (Exception e) {
			System.out.println("readCompany ::: "+e.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DBCPConn.close(conn);
		}

		return dto;
	}

	public int insertCompany(CompanyDTO dto) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("INSERT INTO COMPANY( COMID, PASSWORD, ");
			sb.append(" COMNAME, PHONENUM, ZIP, ADDRESS1, ADDRESS2,");
			sb.append(" EMAIL, LOGOIMAGE ");
			sb.append(") VALUES( ?,?,?,?,?,?,?,?,?) ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getComId());
			pstmt.setString(2, dto.getPassword());
			pstmt.setString(3, dto.getComName());
			pstmt.setString(4, dto.getPhoneNum());
			pstmt.setString(5, dto.getZip());
			pstmt.setString(6, dto.getAddress1());
			pstmt.setString(7, dto.getAddress2());
			pstmt.setString(8, dto.getEmail());
			pstmt.setString(9, dto.getLogoImage());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("insertCompany ::: "+e.toString());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DBCPConn.close(conn);
		}
		return result;
	}

	public void updateCompany(CompanyDTO dto) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("UPDATE COMPANY SET PASSWORD=?, ");
			sb.append(" COMNAME=?, PHONENUM=?, ZIP=?, ");
			sb.append(" ADDRESS1=?, ADDRESS2=?, EMAIL=?, ");
			sb.append(" LOGOIMAGE=?");
			sb.append("WHERE COMID=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getPassword());
			pstmt.setString(2, dto.getComName());
			pstmt.setString(3, dto.getPhoneNum());
			pstmt.setString(4, dto.getZip());
			pstmt.setString(5, dto.getAddress1());
			pstmt.setString(6, dto.getAddress2());
			pstmt.setString(7, dto.getEmail());
			pstmt.setString(8, dto.getLogoImage());
			pstmt.setString(9, dto.getComId());

			pstmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("updateCompany::::"+e.toString());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DBCPConn.close(conn);
		}
	}

	public void deleteCompany(String comId) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE COMPANY SET ENABLED=0 WHERE COMID=?";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, comId);

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("deleteCompany::::"+e.toString());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DBCPConn.close(conn);
		}
	}
}
