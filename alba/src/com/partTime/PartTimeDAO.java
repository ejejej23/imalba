package com.partTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBCPConn;

public class PartTimeDAO {
	// 글번호로 글 불러오기
	public PartTimeDTO readPartTime(int ptNum) {
		PartTimeDTO dto = null;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT PTNUM, SUBJECT, CONTENT, ");
			sb.append(" HITCOUNT, CREATED, APPLYSTART, ");
			sb.append(" APPLYEND, C.COMID, COMNAME, ");
			sb.append(" PHONENUM, LOGOIMAGE, JOBIMAGE, ");
			sb.append(" ZIP, ADDRESS1, ADDRESS2, EMAIL ");
			sb.append("FROM PARTTIME P ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON P.COMID = C.COMID ");
			sb.append("WHERE PTNUM=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, ptNum);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PartTimeDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setHitCount(rs.getInt("hitCount"));

				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setCreated(rs.getString("created"));
				dto.setApplyStart(rs.getString("applyStart"));
				dto.setApplyEnd(rs.getString("applyEnd"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));
				dto.setLogoImage(rs.getString("logoImage"));
				dto.setJobImage(rs.getString("jobImage"));
				dto.setComZip(rs.getString("zip"));
				dto.setComAddress1(rs.getString("address1"));
				dto.setComAddress2(rs.getString("address2"));
				dto.setComEmail(rs.getString("email"));
			}

		} catch (Exception e) {
			System.out.println("readPartTime::::" + e.toString());
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

	// list : 전체
	public List<PartTimeDTO> listPartTime(int start, int end) {
		List<PartTimeDTO> list = new ArrayList<>();

		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT PTNUM, SUBJECT, CONTENT, ");
			sb.append("		HITCOUNT, CREATED, APPLYSTART, ");
			sb.append("		APPLYEND, P.COMID, COMNAME, JOBIMAGE, LOGOIMAGE ");
			sb.append("		FROM PARTTIME P ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON P.COMID = C.COMID ");
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				PartTimeDTO dto = new PartTimeDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setApplyStart(rs.getString("applyStart"));
				dto.setApplyEnd(rs.getString("applyEnd"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setJobImage(rs.getString("jobImage"));
				dto.setLogoImage(rs.getString("logoImage"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listPartTime전체::::" + e.toString());
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

	// list : 검색
	public List<PartTimeDTO> listPartTime(int start, int end, String searchKey, String searchValue) {
		List<PartTimeDTO> list = new ArrayList<>();

		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT PTNUM, SUBJECT, CONTENT, ");
			sb.append("		HITCOUNT, CREATED, APPLYSTART, ");
			sb.append("		APPLYEND, P.COMID, COMNAME, JOBIMAGE, LOGOIMAGE ");
			sb.append("		FROM PARTTIME P ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON P.COMID = C.COMID ");
			if (searchKey.equalsIgnoreCase("COMNAME")) {
				// 회사명 검사
				sb.append("		WHERE INSTR(COMNAME, ?) >= 1");
			} else if (searchKey.equalsIgnoreCase("created")) {
				// 작성일 검사
				sb.append("		WHERE TO_CHAR(CREATED, 'YYYY-MM-DD') = ?");
			} else {
				sb.append("		WHERE INSTR(" + searchKey + ", ?) >= 1");
			}
			sb.append("		ORDER BY PTNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				PartTimeDTO dto = new PartTimeDTO();
				dto.setPtNum(rs.getInt("ptNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setApplyStart(rs.getString("applyStart"));
				dto.setApplyEnd(rs.getString("applyEnd"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setJobImage(rs.getString("jobImage"));
				dto.setLogoImage(rs.getString("logoImage"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listPartTime검색::::" + e.toString());
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

	// insert
	public void insertPartTime(PartTimeDTO dto) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("INSERT INTO PARTTIME ( ");
			sb.append(" PTNUM, SUBJECT, CONTENT, ");
			sb.append(" APPLYSTART, APPLYEND, ");
			sb.append(" COMID, JOBIMAGE )");
			sb.append("VALUES( PT_SEQ.NEXTVAL, ?,?,?,?,?,? )");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getApplyStart());
			pstmt.setString(4, dto.getApplyEnd());
			pstmt.setString(5, dto.getComId());
			pstmt.setString(6, dto.getJobImage());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("insertPartTime::::" + e.toString());
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
			sql = "SELECT NVL(COUNT(*),0) FROM PARTTIME";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCount전체::::" + e.toString());
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
			sql = "SELECT NVL(COUNT(*),0)";
			sql += " FROM PARTTIME P ";
			sql += " JOIN COMPANY C ";
			sql += " ON C.COMID = P.COMID ";
			sql += " WHERE ";
			if (searchKey.equals("comName")) {
				sql += "INSTR(" + searchKey + ",?) = 1";
			} else if (searchKey.equals("created")) {
				sql += "TO_CHAR(CREATED,'YYYY-MM-DD') = ?";
			} else { // 검색조건이 subject,content일 때
				sql += "INSTR(" + searchKey + ",?) >= 1";
			}

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, searchValue);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCount검색::::" + e.toString());
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

	// 조회수 증가
	public void updateHitCount(int ptNum) {
		PreparedStatement pstmt = null;
		Connection conn = DBCPConn.getConnection();
		String sql;

		try {
			sql = "UPDATE PARTTIME SET HITCOUNT = HITCOUNT+1 WHERE PTNUM=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, ptNum);
			pstmt.executeQuery();

		} catch (Exception e) {
			System.out.println("updateHitCount::::" + e.toString());
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

	public PartTimeDTO preReadPartTime(int ptNum, String searchKey, String searchValue) {
		PartTimeDTO dto = null;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			if (searchValue != null && searchValue.length() != 0) {
				sb.append("SELECT ROWNUM, tb.* FROM ( ");
				sb.append("  SELECT PTNUM, SUBJECT FROM PARTTIME ");
				if (searchKey.equals("COMNAME"))
					sb.append("	 WHERE ( INSTR(COMNAME, ?) = 1)  ");
				else if (searchKey.equals("created"))
					sb.append("	 WHERE (TO_CHAR(created, 'YYYY-MM-DD') = ?) ");
				else
					sb.append("	 WHERE ( INSTR(" + searchKey + ", ?) > 0) ");
				sb.append("		 AND (PTNUM > ? ) ");
				sb.append("		 ORDER BY PTNUM ASC ");
				sb.append("	  ) tb WHERE ROWNUM=1 ");

				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, searchValue);
				pstmt.setInt(2, ptNum);
			} else {
				sb.append("SELECT ROWNUM, tb.* FROM ( ");
				sb.append("  SELECT PTNUM, SUBJECT FROM PARTTIME ");
				sb.append("	 WHERE PTNUM > ? ");
				sb.append("		 ORDER BY PTNUM ASC ");
				sb.append("	  ) tb WHERE ROWNUM=1 ");

				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setInt(1, ptNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PartTimeDTO();
				dto.setPtNum(rs.getInt("ptnum"));
				dto.setSubject(rs.getString("subject"));
			}
		} catch (Exception e) {
			System.out.println("preReadPartTime::::" + e.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			DBCPConn.close(conn);
		}

		return dto;
	}

	public PartTimeDTO nextReadPartTime(int ptNum, String searchKey, String searchValue) {
		PartTimeDTO dto = null;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			if (searchValue != null && searchValue.length() != 0) {
				sb.append("SELECT ROWNUM, tb.* FROM ( ");
				sb.append("  SELECT PTNUM, SUBJECT FROM PARTTIME ");
				if (searchKey.equals("COMNAME"))
					sb.append("	 WHERE ( INSTR(COMNAME, ?) = 1)  ");
				else if (searchKey.equals("created"))
					sb.append("	 WHERE (TO_CHAR(created, 'YYYY-MM-DD') = ?) ");
				else
					sb.append("	 WHERE ( INSTR(" + searchKey + ", ?) > 0) ");
				sb.append("		 AND (PTNUM < ? ) ");
				sb.append("		 ORDER BY PTNUM DESC ");
				sb.append("	  ) tb WHERE ROWNUM=1 ");

				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setString(1, searchValue);
				pstmt.setInt(2, ptNum);
			} else {
				sb.append("SELECT ROWNUM, tb.* FROM ( ");
				sb.append("  SELECT PTNUM, SUBJECT FROM PARTTIME ");
				sb.append("	 WHERE PTNUM < ? ");
				sb.append("		 ORDER BY PTNUM DESC ");
				sb.append("	  ) tb WHERE ROWNUM=1 ");

				pstmt = conn.prepareStatement(sb.toString());
				pstmt.setInt(1, ptNum);
			}

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PartTimeDTO();
				dto.setPtNum(rs.getInt("ptnum"));
				dto.setSubject(rs.getString("subject"));
			}
		} catch (Exception e) {
			System.out.println("nextReadPartTime::::" + e.toString());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}

			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			DBCPConn.close(conn);
		}

		return dto;
	}

	// update
	public void updatePartTime(PartTimeDTO dto) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("UPDATE PARTTIME  ");
			sb.append(" SET SUBJECT=?, CONTENT=?, ");
			sb.append(" APPLYSTART=?, APPLYEND=?, ");
			sb.append(" JOBIMAGE=? ");
			sb.append("WHERE PTNUM =?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getApplyStart());
			pstmt.setString(4, dto.getApplyEnd());
			pstmt.setString(5, dto.getJobImage());
			pstmt.setInt(6, dto.getPtNum());

			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("updatePartTime::::" + e.toString());
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

	// delete
	public void deletePartTime(int num) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("DELETE FROM PARTTIME  ");
			sb.append("WHERE PTNUM =?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);

			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("deletePartTime::::" + e.toString());
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
