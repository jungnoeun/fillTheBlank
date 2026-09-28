<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">

<head>
<meta charset="UTF-8">
<title>회원가입 | Fill in the blank</title>

<style>

* {
	box-sizing: border-box;
}

body {
	margin: 0;
	font-family: Arial, "Noto Sans KR", sans-serif;
	background-color: #fff;
	color: #222;
}

.signup-container {
	width: 420px;
	margin: 100px auto;
}

.signup-title {
	text-align: center;
	margin-bottom: 40px;
}

.signup-title h1 {
	font-size: 28px;
	margin-bottom: 10px;
}

.signup-title p {
	font-size: 13px;
	color: #888;
}

.form-group {
	margin-bottom: 20px;
}

.form-group label {
	display: block;
	font-size: 14px;
	font-weight: 600;
	margin-bottom: 8px;
}

.form-group input {
	width: 100%;
	height: 46px;
	padding: 0 12px;
	border: 1px solid #ddd;
	border-radius: 3px;
	font-size: 14px;
	outline: none;
}

.form-group input:focus {
	border-color: #222;
}

.signup-button {
	width: 100%;
	height: 48px;
	margin-top: 10px;
	border: none;
	background-color: #222;
	color: #fff;
	font-size: 15px;
	font-weight: 600;
	cursor: pointer;
	border-radius: 3px;
}

.signup-button:hover {
	background-color: #444;
}

.login-link {
	text-align: center;
	margin-top: 20px;
	font-size: 13px;
	color: #888;
}

.login-link a {
	color: #222;
	text-decoration: none;
}

</style>

</head>

<body>

<div class="signup-container">

	<div class="signup-title">
		<h1>회원가입</h1>
		<p>Fill in the blank과 함께 빈 공간을 채워보세요.</p>
	</div>


	<form action="/signup" method="post">

		<div class="form-group">
			<label for="username">아이디</label>
			<input type="text"
				   id="username"
				   name="username"
				   placeholder="아이디를 입력해주세요"
				   required>
		</div>


		<div class="form-group">
			<label for="password">비밀번호</label>
			<input type="password"
				   id="password"
				   name="password"
				   placeholder="비밀번호를 입력해주세요"
				   required>
		</div>


		<div class="form-group">
			<label for="name">이름</label>
			<input type="text"
				   id="name"
				   name="name"
				   placeholder="이름을 입력해주세요"
				   required>
		</div>


		<div class="form-group">
			<label for="email">이메일</label>
			<input type="email"
				   id="email"
				   name="email"
				   placeholder="이메일을 입력해주세요"
				   required>
		</div>


		<button type="submit" class="signup-button">
			회원가입
		</button>

	</form>


	<div class="login-link">
		이미 회원이신가요?
		<a href="#">로그인</a>
	</div>

</div>

</body>

</html>