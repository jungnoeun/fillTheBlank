<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>

<style>
    * {
        box-sizing: border-box;
    }

    body {
        margin: 0;
        background-color: #fff;
        font-family: Arial, sans-serif;
        color: #222;
    }

    .login-container {
        width: 400px;
        margin: 100px auto;
    }

    .login-title {
        text-align: center;
        margin-bottom: 40px;
    }

    .login-title h1 {
        margin: 0 0 10px;
        font-size: 28px;
    }

    .login-title p {
        margin: 0;
        color: #888;
        font-size: 14px;
    }

    .login-form {
        display: flex;
        flex-direction: column;
        gap: 15px;
    }

    .login-form input {
        width: 100%;
        height: 50px;
        padding: 0 15px;
        border: 1px solid #ddd;
        border-radius: 6px;
        font-size: 14px;
    }

    .login-form input:focus {
        outline: none;
        border-color: #222;
    }

    .login-button {
        height: 50px;
        border: none;
        border-radius: 6px;
        background-color: #222;
        color: white;
        font-size: 15px;
        cursor: pointer;
    }

    .login-button:hover {
        background-color: #444;
    }

    .signup-link {
        margin-top: 20px;
        text-align: center;
        font-size: 14px;
        color: #888;
    }

    .signup-link a {
        color: #222;
        text-decoration: none;
        font-weight: 600;
    }

    .error-message {
        margin-bottom: 15px;
        text-align: center;
        color: #d33;
        font-size: 14px;
    }
</style>

</head>

<body>

<div class="login-container">

    <div class="login-title">
        <h1>로그인</h1>
        <p>Fill the blank에 로그인하세요.</p>
    </div>

    <% if ("true".equals(request.getParameter("error"))) { %>
        <div class="error-message">
            아이디 또는 비밀번호가 올바르지 않습니다.
        </div>
    <% } %>

    <form class="login-form" action="${pageContext.request.contextPath}/login" method="post">

        <input
            type="text"
            name="username"
            placeholder="아이디"
            required>

        <input
            type="password"
            name="password"
            placeholder="비밀번호"
            required>

        <button type="submit" class="login-button">
            로그인
        </button>

    </form>

    <div class="signup-link">
        아직 회원이 아니신가요?
        <a href="${pageContext.request.contextPath}/signup">회원가입</a>
    </div>

</div>

</body>
</html>