package com.applyer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBCPConn;

public class ApplyerDAO {

	public void insertApply(ApplyerDTO dto) {
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
			System.out.println("insertApplyer::::" + e.toString());
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
			System.out.println("dataCountApplyer전체::::" + e.toString());
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

	public int dataCount(String comId) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*),0) ";
			sql += " FROM APPLY A JOIN PARTTIME P ON A.PTNUM = P.PTNUM ";
			sql += " WHERE COMID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, comId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCountApplyer::::" + e.toString());
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
	
	//검색용
	public int dataCount(String searchKey, String searchValue, String comId) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*),0) ";
			sql += " FROM APPLY A JOIN PARTTIME P ON A.PTNUM = P.PTNUM ";
			sql += " WHERE COMID = ? AND ";
			if (searchKey.equals("created")) {
				sql += "TO_CHAR(APPLYDATE,'YYYY-MM-DD') = ?";
			} else { // 검색조건이 subject,content일 때
				sql += "INSTR(" + searchKey + ",?) >= 1";
			}
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, comId);
			pstmt.setString(2, searchValue);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCountApplyer검색::::" + e.toString());
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

	// 기업회원용
	public List<ApplyerDTO> listApplyer(int start, int end, String comId) {
		List<ApplyerDTO> list = new ArrayList<>();
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT A.PTNUM, P.SUBJECT,  ");
			sb.append("		P.CREATED, A.APPLYDATE, ");
			sb.append("		M.USERID, M.USERNAME, M.PHONENUM, M.BIRTH ");
			sb.append("		FROM APPLY A ");
			sb.append("		JOIN PARTTIME P ");
			sb.append("		ON A.PTNUM = P.PTNUM ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON C.COMID = P.COMID ");
			sb.append("		JOIN MEMBER M ");
			sb.append("		ON M.USERID = A.USERID ");
			sb.append("		WHERE C.COMID = ? ");
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, comId);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ApplyerDTO dto = new ApplyerDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setApplyDate(rs.getString("applyDate"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserTel(rs.getString("phoneNum"));
				dto.setBirth(rs.getString("birth"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listApplyer기업::::" + e.toString());
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

	public List<ApplyerDTO> listApplyer(int start, int end, String searchKey, String searchValue, String comId) {
		List<ApplyerDTO> list = new ArrayList<>();
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT A.PTNUM, P.SUBJECT,  ");
			sb.append("		P.CREATED, A.APPLYDATE, ");
			sb.append("		M.USERID, M.USERNAME, M.PHONENUM, M.BIRTH ");
			sb.append("		FROM APPLY A ");
			sb.append("		JOIN PARTTIME P ");
			sb.append("		ON A.PTNUM = P.PTNUM ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON C.COMID = P.COMID ");
			sb.append("		JOIN MEMBER M ");
			sb.append("		ON M.USERID = A.USERID ");
			sb.append("		WHERE C.COMID = ? AND ");
			if (searchKey.equals("created")) {
				sb.append("TO_CHAR(APPLYDATE,'YYYY-MM-DD') = ? ");
			} else { 
				sb.append("INSTR(" + searchKey + ",?) >= 1 ");
			}
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, comId);
			pstmt.setString(2, searchValue);
			pstmt.setInt(3, end);
			pstmt.setInt(4, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ApplyerDTO dto = new ApplyerDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setApplyDate(rs.getString("applyDate"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserTel(rs.getString("phoneNum"));
				dto.setBirth(rs.getString("birth"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listApplyer기업검색::::" + e.toString());
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
