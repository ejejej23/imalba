package com.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.util.DBCPConn;

public class MemberDAO {
	public MemberDTO readMember(String userId) {
		MemberDTO dto = null;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT USERID, PASSWORD, ");
			sb.append("		USERNAME, TO_CHAR(BIRTH, 'YYYY-MM-DD') BIRTH, PHONENUM, ");
			sb.append("		ZIP, ADDRESS1, ADDRESS2, ");
			sb.append("		EMAIL, JOINED, ENABLED ");
			sb.append("FROM MEMBER ");
			sb.append("WHERE USERID = ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, userId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new MemberDTO();
				dto.setUserId(rs.getString("userid"));
				dto.setPassword(rs.getString("password"));
				dto.setUserName(rs.getString("username"));
				dto.setBirth(rs.getString("birth"));
				dto.setPhoneNum(rs.getString("phonenum"));
				dto.setZip(rs.getString("zip"));
				dto.setAddress1(rs.getString("address1"));
				dto.setAddress2(rs.getString("address2"));
				dto.setEmail(rs.getString("email"));
				dto.setJoined(rs.getString("joined"));
				dto.setEnabled(rs.getInt("enabled"));

				if (dto.getPhoneNum() != null) {
					String[] ss = dto.getPhoneNum().split("-");
					if (ss.length == 3) {
						dto.setTel1(ss[0]);
						dto.setTel2(ss[1]);
						dto.setTel3(ss[2]);
					}
				}

				if (dto.getEmail() != null) {
					String[] ss = dto.getEmail().split("@");
					if (ss.length == 2) {
						dto.setEmail1(ss[0]);
						dto.setEmail2(ss[1]);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("readMember::::"+e.toString());
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

	public int insertMember(MemberDTO dto) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("INSERT INTO MEMBER( USERID, PASSWORD, USERNAME, ");
			sb.append(" BIRTH, PHONENUM, ZIP, ADDRESS1, ADDRESS2, EMAIL) ");
			sb.append(" VALUES( ?,?,?,?,?, ?,?,?,?) ");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getPassword());
			pstmt.setString(3, dto.getUserName());
			pstmt.setString(4, dto.getBirth());
			pstmt.setString(5, dto.getPhoneNum());
			pstmt.setString(6, dto.getZip());
			pstmt.setString(7, dto.getAddress1());
			pstmt.setString(8, dto.getAddress2());
			pstmt.setString(9, dto.getEmail());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("insertMember::::"+e.toString());
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

	public int updateMember(MemberDTO dto) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("UPDATE MEMBER SET PASSWORD=?, ");
			sb.append(" USERNAME=?, BIRTH=?, PHONENUM=?, ");
			sb.append(" ZIP=?, ADDRESS1=?, ADDRESS2=?, ");
			sb.append(" EMAIL=? ");
			sb.append(" WHERE USERID=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getPassword());
			pstmt.setString(2, dto.getUserName());
			pstmt.setString(3, dto.getBirth());
			pstmt.setString(4, dto.getPhoneNum());
			pstmt.setString(5, dto.getZip());
			pstmt.setString(6, dto.getAddress1());
			pstmt.setString(7, dto.getAddress2());
			pstmt.setString(8, dto.getEmail());
			pstmt.setString(9, dto.getUserId());

			result = pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("updateMember::::"+e.toString());
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

	public void deleteMember(String userId) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		String sql;

		try {
			sql = "UPDATE MEMBER SET ENABLED=0 WHERE USERID=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("deleteMember::::"+e.toString());
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
