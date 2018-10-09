<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String cp=request.getContextPath();
%>

<c:if test="${dataCount != 0 }">
<table style='width: 100%; margin: 10px auto 30px; border-spacing: 0px;'>
<tr height="35">
    <td>
       <div style="clear: both;">
           <div style="float: left;"><span style="color: #3EA9CD; font-weight: bold;">댓글 ${dataCount }</span> <span>[댓글 목록, ${pageNo }/${total_page } 페이지]</span></div>
           <div style="float: right; text-align: right;"></div>
       </div>
    </td>
</tr>

<c:forEach var="vo" items="${list }">
    <tr height='35' style='background: #eee;'>
       <td width='50%' style='padding:5px 5px; border:1px solid #cccccc; border-right:none;'>
           <span><b>${vo.userName }</b></span>
        </td>
       <td width='50%' style='padding:5px 5px; border:1px solid #cccccc; border-left:none;' align='right'>
          <span>${vo.created }</span> | 
          	<c:if test="${sessionScope.member.userId == vo.userId || sessionScope.member.userId == 'admin' }">
				<span class="deleteReply" style="cursor: pointer;" data-replyNum='${vo.replyNum }' data-pageNo='${pageNo}'>삭제</span>
			</c:if>
			<c:if test="${sessionScope.member.userId != vo.userId && sessionScope.member.userId != 'admin' }">
				<span class="notyfyReply" style="cursor: pointer;">신고</span>
			</c:if>
        </td>
    </tr>
    <tr style=" height: 50px;">
        <td colspan='2' valign='top' style='padding:5px 5px;'>
                ${vo.content }
        </td>
    </tr>

</c:forEach>

     <tr height="40">
         <td colspan='2'>
              ${paging }
         </td>
     </tr>
</table>
</c:if>
