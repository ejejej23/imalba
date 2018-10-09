package com.partTimeShort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBCPConn;

public class PartTimeSDAO {

	// 게시물 조회수 증가
	public void updateHitCount(int num) {
		PreparedStatement pstmt = null;
		Connection conn = DBCPConn.getConnection();
		String sql;

		try {
			sql = "UPDATE PARTTIMESHORT SET HITCOUNT = HITCOUNT+1 WHERE PTSNUM=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeQuery();

		} catch (Exception e) {
			System.out.println("updateHitCount_PTS::::" + e.toString());
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

	public PartTimeSDTO readPartTimeShort(int num) {
		PartTimeSDTO dto = null;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT PTSNUM, SUBJECT, CONTENT, ");
			sb.append(" HITCOUNT, CREATED, C.COMID, ");
			sb.append(" COMNAME, PHONENUM, DONE ");
			sb.append("FROM PARTTIMESHORT P ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON P.COMID=C.COMID ");
			sb.append("WHERE PTSNUM=?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new PartTimeSDTO();
				dto.setPtsNum(rs.getInt("ptsNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));
				dto.setDone(rs.getInt("done"));
			}

		} catch (Exception e) {
			System.out.println("listPartTimeShort전체::::" + e.toString());
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

	// 전체 데이터 개수 구하기 함수
	public int dataCount() {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*),0) FROM PARTTIMESHORT";
			pstmt = conn.prepareStatement(sql);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCount전체PTS::::" + e.toString());
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

	// 검색 데이터 개수 구하기 함수
	public int dataCount(String searchKey, String searchValue) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(COUNT(*),0)";
			sql += " FROM PARTTIMESHORT P ";
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
			System.out.println("dataCount검색PTS::::" + e.toString());
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

	// 전체 리스트
	public List<PartTimeSDTO> listPartTimeShort(int start, int end) {
		List<PartTimeSDTO> list = new ArrayList<>();

		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT PTSNUM, SUBJECT, CONTENT, DONE, ");
			sb.append("		HITCOUNT, CREATED, C.COMID, COMNAME, PHONENUM ");
			sb.append("		FROM PARTTIMESHORT P ");
			sb.append("		JOIN COMPANY C ");
			sb.append("		ON P.COMID = C.COMID ");
			sb.append("		ORDER BY PTSNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				PartTimeSDTO dto = new PartTimeSDTO();
				dto.setPtsNum(rs.getInt("ptsNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));
				dto.setDone(rs.getInt("done"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listPartTimeShort전체::::" + e.toString());
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

	// 검색 리스트
	public List<PartTimeSDTO> listPartTimeShort(int start, int end, String searchKey, String searchValue) {
		List<PartTimeSDTO> list = new ArrayList<>();

		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT PTSNUM, SUBJECT, CONTENT, DONE, ");
			sb.append("		HITCOUNT, CREATED, C.COMID, COMNAME, PHONENUM ");
			sb.append("		FROM PARTTIMESHORT P ");
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
			sb.append("		ORDER BY PTSNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ?");
			sb.append(") WHERE RNUM >= ?");
			pstmt = conn.prepareStatement(sb.toString());

			pstmt.setString(1, searchValue);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				PartTimeSDTO dto = new PartTimeSDTO();
				dto.setPtsNum(rs.getInt("ptsNum"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setComId(rs.getString("comId"));
				dto.setComName(rs.getString("comName"));
				dto.setComTel(rs.getString("phoneNum"));
				dto.setDone(rs.getInt("done"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listPartTimeShort검색::::" + e.toString());
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
	public void insertPartTimeShort(PartTimeSDTO dto) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("INSERT INTO PARTTIMESHORT ( ");
			sb.append(" PTSNUM, SUBJECT, CONTENT, ");
			sb.append(" COMID )");
			sb.append("VALUES( PTS_SEQ.NEXTVAL, ?,?,? )");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getComId());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("insertPartTimeShort::::" + e.toString());
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

	// update
	public void updatePartTimeS(PartTimeSDTO dto) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("UPDATE PARTTIMESHORT  ");
			sb.append(" SET COMID=?, SUBJECT=?, CONTENT=? ");
			sb.append("WHERE PTSNUM=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getComId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setInt(4, dto.getPtsNum());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("updatePartTimeShort::::" + e.toString());
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

	// 댓글삭제
	public void deletePartTimeShortReply(int num) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("DELETE FROM REPLY ");
			sb.append("   WHERE PTSNUM = ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("deletePartTimeShortReply::::" + e.toString());
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

	public void deletePartTimeShort(int num) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("DELETE FROM PARTTIMESHORT  ");
			sb.append("WHERE PTSNUM=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("deletePartTimeShort::::" + e.toString());
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

	// 마감여부 체크
	public void updateDone(int pstNum, int done) {
		PreparedStatement pstmt = null;
		Connection conn = DBCPConn.getConnection();
		String sql;

		try {
			sql = "UPDATE PARTTIMESHORT SET DONE = ? WHERE PTSNUM=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, done);
			pstmt.setInt(2, pstNum);

			pstmt.executeQuery();

		} catch (Exception e) {
			System.out.println("updateDone::::" + e.toString());
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

	public void insertReply(ReplyDTO dto) {
		PreparedStatement pstmt = null;
		Connection conn = DBCPConn.getConnection();
		String sql;

		try {
			sql = "INSERT INTO REPLY(REPLYNUM, PTSNUM, USERID, CONTENT)  VALUES(REPLY_SEQ.NEXTVAL, ?,?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getContent());

			pstmt.executeQuery();

		} catch (Exception e) {
			System.out.println("insertReply::::" + e.toString());
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

	public int dataCountReply(int num) {
		int result = 0;
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT COUNT(*) FROM REPLY ";
			sql += " WHERE PTSNUM = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				result = rs.getInt(1);
			}

		} catch (Exception e) {
			System.out.println("dataCountReply::::" + e.toString());
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

	public List<ReplyDTO> listReply(int num, int start, int end) {
		Connection conn = DBCPConn.getConnection();
		List<ReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {

			sb.append("SELECT * FROM ( ");
			sb.append("	SELECT ROWNUM RNUM, TB.* FROM ( ");
			sb.append("		SELECT R.REPLYNUM, R.USERID, USERNAME, PTSNUM, ");
			sb.append("		 	CONTENT, R.CREATED ");
			sb.append("		FROM REPLY R ");
			sb.append("		JOIN MEMBER M  ");
			sb.append("		ON R.USERID = M.USERID ");
			sb.append("		WHERE PTSNUM= ? ");
			sb.append("		ORDER BY R.REPLYNUM DESC ");
			sb.append("	) TB WHERE ROWNUM <= ? ");
			sb.append(") WHERE RNUM >= ?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();
			while (rs.next()) {
				ReplyDTO dto = new ReplyDTO();
				dto.setReplyNum(rs.getInt("replyNum"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setNum(rs.getInt("ptsNum"));
				dto.setContent(rs.getString("content"));
				dto.setCreated(rs.getString("created"));

				list.add(dto);
			}

		} catch (Exception e) {
			System.out.println("listReply::::" + e.toString());
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

	public void deleteReply(int replyNum) {
		Connection conn = DBCPConn.getConnection();
		PreparedStatement pstmt = null;
		String sql;

		try {

			sql = "DELETE FROM REPLY ";
			sql += " WHERE REPLYNUM =? ";

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, replyNum);

			pstmt.executeQuery();

		} catch (Exception e) {
			System.out.println("deleteReply::::" + e.toString());
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
