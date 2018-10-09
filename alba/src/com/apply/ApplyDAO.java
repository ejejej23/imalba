package com.apply;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBCPConn;

public class ApplyDAO {

	public void insertApply(ApplyDTO dto) {
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
			System.out.println("insertApply::::" + e.toString());
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
			System.out.println("dataCountApply전체::::" + e.toString());
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

	public int dataCount(String userId) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*),0) FROM APPLY WHERE USERID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCountApply개인별::::" + e.toString());
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

	public int dataCount(String searchKey, String searchValue, String userId) {
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
			sql += "WHERE USERID = ? AND ";
			if (searchKey.equals("created")) {
				sql += "TO_CHAR(APPLYDATE,'YYYY-MM-DD') = ?";
			} else { // 검색조건이 subject,content일 때
				sql += "INSTR(" + searchKey + ",?) >= 1";
			}

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId);
			pstmt.setString(2, searchValue);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCountApply개인검색::::" + e.toString());
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

	// 개인회원용 : 내가 지원한 내역 보기
	public List<ApplyDTO> listApplyM(int start, int end, String userId) {
		List<ApplyDTO> list = new ArrayList<>();
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT A.PTNUM, P.SUBJECT,  ");
			sb.append("		P.CREATED, A.APPLYDATE, ");
			sb.append("		P.COMID, C.COMNAME, C.PHONENUM ");
			sb.append("		FROM APPLY A ");
			sb.append("		JOIN PARTTIME P ");
			sb.append("		ON A.PTNUM = P.PTNUM ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON C.COMID = P.COMID ");
			sb.append("		WHERE USERID = ? ");
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, userId);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ApplyDTO dto = new ApplyDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setApplyDate(rs.getString("applyDate"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listApplyM개인::::" + e.toString());
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

	public List<ApplyDTO> listApplyM(int start, int end, String searchKey, String searchValue, String userId) {
		List<ApplyDTO> list = new ArrayList<>();
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT A.PTNUM, P.SUBJECT,  ");
			sb.append("		P.CREATED, A.APPLYDATE, ");
			sb.append("		P.COMID, C.COMNAME, C.PHONENUM ");
			sb.append("		FROM APPLY A ");
			sb.append("		JOIN PARTTIME P ");
			sb.append("		ON A.PTNUM = P.PTNUM ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON C.COMID = P.COMID ");
			sb.append("		WHERE USERID = ? AND ");
			if (searchKey.equals("created")) {
				sb.append("TO_CHAR(APPLYDATE,'YYYY-MM-DD') = ? ");
			} else {
				sb.append("INSTR(" + searchKey + ",?) >= 1 ");
			}
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ? ");
			sb.append(") WHERE RNUM >= ? ");
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setString(1, userId);
			pstmt.setString(2, searchValue);
			pstmt.setInt(3, end);
			pstmt.setInt(4, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				ApplyDTO dto = new ApplyDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setApplyDate(rs.getString("applyDate"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listApplyM검색::::" + e.toString());
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

	public int readMyApply(String userId, int ptNum) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT COUNT(*) ");
			sb.append(" FROM APPLY A ");
			sb.append(" JOIN PARTTIME P ");
			sb.append(" ON A.PTNUM = P.PTNUM ");
			sb.append("	WHERE USERID=? AND P.PTNUM=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, userId);
			pstmt.setInt(2, ptNum);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("readApply::::" + e.toString());
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

}
