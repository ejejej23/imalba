package com.applyAdmin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBCPConn;

public class ApplyAdDAO {

	public void insertApply(ApplyAdDTO dto) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("INSERT INTO APPLY ( ");
			sb.append(" APPLYNUM, PTNUM, USERID ) ");
			sb.append("VALUES( APPLY_SEQ.NEXTVAL, ?,? )");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, dto.getPtNum());
			pstmt.setString(2, dto.getUserId());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("insertApplyAdmin::::" + e.toString());
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

	public int dataCount() {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*),0) FROM APPLY";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCountApplyAdmin��ü::::" + e.toString());
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

		return result;
	}

	public int dataCount(String searchKey, String searchValue) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*),0) ";
			sql += " FROM APPLY A ";
			sql += " JOIN PARTTIME P ";
			sql += " ON A.PTNUM = P.PTNUM ";
			sql += " JOIN COMPANY C ";
			sql += " ON C.COMID = P.COMID ";
			sql += "WHERE ";
			if (searchKey.equals("created")) {
				sql += "TO_CHAR(APPLYDATE,'YYYY-MM-DD') = ?";
			} else { // �˻������� subject,content�� ��
				sql += "INSTR(" + searchKey + ",?) >= 1";
			}

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, searchValue);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCountAdmin�˻�::::" + e.toString());
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

		return result;
	}

	// �����ڿ�
	public List<ApplyAdDTO> listApplyA(int start, int end) {
		List<ApplyAdDTO> list = new ArrayList<>();
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT A.PTNUM, P.SUBJECT,  ");
			sb.append("		P.CREATED, A.APPLYDATE, ");
			sb.append("		P.COMID, C.COMNAME, C.PHONENUM, ");
			sb.append("		M.USERID, M.USERNAME ");
			sb.append("		FROM APPLY A ");
			sb.append("		JOIN PARTTIME P ");
			sb.append("		ON A.PTNUM = P.PTNUM ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON C.COMID = P.COMID ");
			sb.append("		JOIN MEMBER M ");
			sb.append("		ON M.USERID = A.USERID ");
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ApplyAdDTO dto = new ApplyAdDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setApplyDate(rs.getString("applyDate"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listApplyA������::::" + e.toString());
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

		return list;
	}

	public List<ApplyAdDTO> listApplyA(int start, int end, String searchKey, String searchValue) {
		List<ApplyAdDTO> list = new ArrayList<>();
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT A.PTNUM, P.SUBJECT,  ");
			sb.append("		P.CREATED, A.APPLYDATE, ");
			sb.append("		P.COMID, C.COMNAME, C.PHONENUM, ");
			sb.append("		M.USERID, M.USERNAME ");
			sb.append("		FROM APPLY A ");
			sb.append("		JOIN PARTTIME P ");
			sb.append("		ON A.PTNUM = P.PTNUM ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON C.COMID = P.COMID ");
			sb.append("		JOIN MEMBER M ");
			sb.append("		ON M.USERID = A.USERID ");
			sb.append(" WHERE ");
			if (searchKey.equals("created")) {
				sb.append("TO_CHAR(APPLYDATE,'YYYY-MM-DD') = ? ");
			} else if (searchKey.equals("userId")) {
				sb.append("INSTR(M.USERID,?) >= 1 ");
			} else {
				sb.append("INSTR(" + searchKey + ",?) >= 1 ");
			}
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ? ");
			sb.append(") WHERE RNUM >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ApplyAdDTO dto = new ApplyAdDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setApplyDate(rs.getString("applyDate"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listApplyA�����ڰ˻�::::" + e.toString());
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

		return list;
	}

}
