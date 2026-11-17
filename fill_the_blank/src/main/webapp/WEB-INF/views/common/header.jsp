<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<header class="header">

    <div class="header-logo" onclick="location.href='${pageContext.request.contextPath}/introduction'">
        <div class="logo-main">fill in the blank</div>
        <div class="logo-sub">빈 공간을 새로운 가치로 채우다</div>
    </div>

    <nav class="header-nav">
        <a href="${pageContext.request.contextPath}/idle-space-list"
           class="${param.activeMenu eq 'idle' ? 'active' : ''}">유휴공간 찾기</a>

        <a href="${pageContext.request.contextPath}/introduction"
           class="${param.activeMenu eq 'intro' ? 'active' : ''}">서비스 소개</a>

        <a href="#"
           class="${param.activeMenu eq 'recommend' ? 'active' : ''}">마이페이지</a>
    </nav>
    
</header>