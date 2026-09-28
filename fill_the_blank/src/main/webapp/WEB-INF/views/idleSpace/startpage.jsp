<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>Fill in the blank</title>

<style>

/* =========================
   전체
   ========================= */
* {
	box-sizing: border-box;
}

html, body {
	margin: 0;
	padding: 0;
	width: 100%;
	height: 100%;
	font-family: Arial, "Noto Sans KR", sans-serif;
	color: #222;
	background-color: #fff;
}

body {
	min-height: 100vh;
}


/* =========================
   헤더
   ========================= */
.header {
	height: 90px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 70px;
	border-bottom: 1px solid #eee;
	background-color: #fff;
}

.header-logo {
	display: flex;
	align-items: baseline;
	gap: 12px;
}

.logo-main {
	font-size: 22px;
	font-weight: 700;
	letter-spacing: -0.5px;
}

.logo-sub {
	font-size: 12px;
	color: #999;
}


/* =========================
   상단 메뉴
   ========================= */
.header-nav {
	display: flex;
	align-items: center;
	gap: 35px;
}

.header-nav a {
	position: relative;
	text-decoration: none;
	color: #666;
	font-size: 14px;
	font-weight: 500;
	padding: 25px 0;
	transition: color 0.2s;
}

.header-nav a:hover {
	color: #222;
}

/* 현재 메뉴 */
.header-nav a.active {
	color: #222;
	font-weight: 700;
}

/* 현재 메뉴 아래 선 */
.header-nav a.active::after {
	content: "";
	position: absolute;
	left: 0;
	right: 0;
	bottom: 0;
	height: 2px;
	background-color: #222;
}


/* =========================
   메인
   ========================= */
.main {
	height: calc(100vh - 90px);
	min-height: 600px;
}


/* =========================
   서비스 소개
   60%
   ========================= */
.intro-section {
	height: 60%;
	display: flex;
	align-items: center;
	justify-content: center;
	text-align: center;
	background-color: #fff;
}

.intro-content {
	margin-top: -20px;
}

.intro-title {
	font-size: 42px;
	font-weight: 700;
	letter-spacing: -2px;
	margin: 0 0 20px;
}

.intro-title span {
	font-weight: 400;
}

.intro-description {
	font-size: 16px;
	line-height: 1.8;
	color: #777;
	margin: 0;
}

.intro-line {
	width: 40px;
	height: 2px;
	background-color: #222;
	margin: 28px auto 0;
}


/* =========================
   하단 메뉴
   40%
   ========================= */
.menu-section {
	height: 40%;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20px 70px 50px;
}

.menu-container {
	width: 100%;
	max-width: 1100px;
	height: 100%;
	display: flex;
	gap: 20px;
}


/* =========================
   메뉴 카드
   ========================= */
.menu-card {
	flex: 1;
	height: 100%;
	border: 1px solid #e5e5e5;
	background-color: #fff;

	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	text-decoration: none;
	color: #222;

	transition: all 0.25s ease;
}

.menu-card:hover {
	transform: translateY(-5px);
	border-color: #222;
	box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
}


/* 카드 아이콘 */
.menu-icon {
	font-size: 30px;
	margin-bottom: 18px;
}


/* 카드 제목 */
.menu-title {
	font-size: 18px;
	font-weight: 700;
	margin-bottom: 10px;
}


/* 카드 설명 */
.menu-description {
	font-size: 13px;
	color: #888;
	text-align: center;
	line-height: 1.6;
}


/* =========================
   반응형
   ========================= */
@media (max-width: 768px) {

	.header {
		padding: 0 25px;
	}

	.logo-sub {
		display: none;
	}

	.header-nav {
		gap: 15px;
	}

	.header-nav a {
		font-size: 12px;
	}

	.intro-title {
		font-size: 32px;
	}

	.intro-description {
		font-size: 14px;
	}

	.menu-section {
		padding: 15px 25px 30px;
	}

	.menu-container {
		gap: 10px;
	}

	.menu-icon {
		font-size: 24px;
	}

	.menu-title {
		font-size: 15px;
	}

	.menu-description {
		font-size: 11px;
	}
}

</style>
</head>

<body>

<!-- =========================
     상단 헤더
     ========================= -->
<header class="header">

	<div class="header-logo">
		<div class="logo-main">fill in the blank</div>
		<div class="logo-sub">빈 공간을 새로운 가치로 채우다</div>
	</div>

	<nav class="header-nav">
		<a href="#" class="active">메인 페이지</a>
		<a href="#">로그인</a>
		<a href="/signup">회원가입</a>
	</nav>

</header>


<!-- =========================
     메인
     ========================= -->
<main class="main">


	<!-- =========================
	     서비스 소개 영역 60%
	     ========================= -->
	<section class="intro-section">

		<div class="intro-content">

			<h1 class="intro-title">
				빈 공간을 <span>새로운 가치로</span> 채워보세요.
			</h1>

			<p class="intro-description">
				서울 곳곳의 유휴공간을 찾아보고<br>
				공간의 특성과 주변 환경을 바탕으로 새로운 활용 방법을 확인해보세요.
			</p>

			<div class="intro-line"></div>

		</div>

	</section>


	<!-- =========================
	     하단 메뉴 영역 40%
	     ========================= -->
	<section class="menu-section">

		<div class="menu-container">


			<!-- 유휴공간 리스트 -->
			<a href="/idle-space-list" class="menu-card">
				<div class="menu-icon">⌂</div>
				<div class="menu-title">
					유휴공간 찾기
				</div>
				<div class="menu-description">
					서울의 유휴공간을<br>
					찾아보고 확인해보세요.
				</div>

			</a>


			<!-- 공간 채팅 -->
			<a href="#" class="menu-card">
				<div class="menu-icon">○</div>
				<div class="menu-title">
					공간 이야기
				</div>
				<div class="menu-description">
					관심 있는 공간에 대해<br>
					사람들과 이야기를 나눠보세요.
				</div>

			</a>


			<!-- 마이페이지 -->
			<a href="#" class="menu-card">
				<div class="menu-icon">♙</div>
				<div class="menu-title">
					마이페이지
				</div>
				<div class="menu-description">
					나의 활동과 관심 공간을<br>
					확인해보세요.
				</div>

			</a>


		</div>

	</section>

</main>

</body>
</html>