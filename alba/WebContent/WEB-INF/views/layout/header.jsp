<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<script type="text/javascript">
//엔터 처리
$(function(){
	   $("input").not($(":button")).keypress(function (evt) {
	        if (evt.keyCode == 13) {
	            var fields = $(this).parents('form,body').find('button,input,textarea,select');
	            var index = fields.index(this);
	            if ( index > -1 && ( index + 1 ) < fields.length ) {
	                fields.eq( index + 1 ).focus();
	            }
	            return false;
	        }
	     });
});
</script>

<div class="header-top">
    <div class="header-left">
        <p style="margin: 0px;">
            <a href="<%=cp%>/" style="text-decoration: none;">
                <span style="width: 200px; height: 70; position: relative; left: 0; top:15px; color: forestgreen; filter: mask(color=red) shadow(direction=135) chroma(color=red); font-family: arial black; font-size: 30px; font-weight: bold;">
                	<img alt="logo" src="<%=cp%>/resource/images/albaimg3.png" width="142px" height="50px">
                </span>
            </a>
        </p>
    </div>
    <div class="header-right">
        <div style="padding-top: 20px;  float: right;">
            <c:if test="${empty sessionScope.member}">
                <a href="<%=cp%>/member/login.do">로그인</a>
            </c:if>
            <c:if test="${not empty sessionScope.member}">
                <span style="color:limegreen; font-weight: bold;">${sessionScope.member.userName}</span>님
                    &nbsp;|&nbsp;
                    <a href="<%=cp%>/member/logout.do">로그아웃</a>
            </c:if>
        </div>
    </div>
</div>

<div class="menu">
    <ul class="nav">
        <li>
            <a href="#">소개</a>
            <ul>
                <li><a href="<%=cp%>/info/intro.do">사이트소개</a></li>
                <li><a href="<%=cp%>/info/map.do">찾아오시는길</a></li>
            </ul>
        </li>

        <li>
            <a href="#">알바게시판</a>
            <ul>
                <li><a href="<%=cp%>/partTime/list.do"  style="margin-left:75px; ">알바</a></li>
                <li><a href="<%=cp%>/partTimeShort/list.do">[급구]단기알바</a></li>
            </ul>
        </li>
        
		<c:if test="${sessionScope.member.userRoll==0 }">
        <li>
            <a href="#">관리자메뉴</a>
            <ul>
                <li><a href="<%=cp%>/applyAdmin/list.do" style="margin-left:210px; ">지원자/회사 조회</a></li>
            </ul>
        </li>
        </c:if>
        
        <c:if test="${sessionScope.member.userRoll==1}">
        <li>
            <a href="#">마이페이지</a>
            <ul>
               	<li><a href="<%=cp%>/member/pwd.do?mode=update&roll=M" style="margin-left:180px; ">정보확인</a></li>
                <li><a href="<%=cp%>/applyList/list.do">지원회사보기</a></li>
            </ul>
        </li>
        </c:if>
        
        <c:if test="${sessionScope.member.userRoll==2}">
        <li>
            <a href="#">마이컴퍼니</a>
            <ul>
               	<li><a href="<%=cp%>/member/pwd.do?mode=update&roll=C" style="margin-left:180px; ">정보확인</a></li>
                <li><a href="<%=cp%>/applyerList/list.do">지원자보기</a></li>
            </ul>
        </li>
        </c:if>
        
        <li style="float: right;"><a href="#">전체보기</a></li>

    </ul>      
</div>

<div class="navigation">
	<div class="nav-bar"></div>
</div>