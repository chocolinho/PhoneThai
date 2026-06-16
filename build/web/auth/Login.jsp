<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Đăng nhập - PhoneThai</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <style>
    :root{
      --bg:#fafafa; --card:#ffffff; --text:#0F172A; --muted:#6b7280;
      --primary:#E63946; --primary-2:#FF6B6B;
      --border:#e5e7eb; --danger:#dc2626; --success:#16a34a;
    }
    *{box-sizing:border-box}
    html,body{height:100%}
    body{
      margin:0; font-family:'Inter',sans-serif;
      background:linear-gradient(120deg,#fff5f5,#fffafa);
      color:var(--text);
    }
    .page{min-height:100vh; display:grid; place-items:center; padding:24px}
    .auth{
      width:100%; max-width:960px; background:var(--card); border:1px solid var(--border);
      border-radius:20px; overflow:hidden; display:grid; grid-template-columns:1.2fr 1fr;
      box-shadow:0 10px 25px rgba(230,57,70,.2);
    }
    @media(max-width:900px){.auth{grid-template-columns:1fr}.visual{display:none}}
    .visual{
      padding:40px; background:
        radial-gradient(1000px 400px at -10% -20%, #ffe5e5, transparent 70%),
        linear-gradient(135deg,#fffafa 0%,#fff5f5 70%);
      border-right:1px solid var(--border);
      display:flex; flex-direction:column; justify-content:center; gap:18px;
    }
    .brand{display:flex;align-items:center;gap:10px;font-weight:700;color:var(--text)}
    .brand-badge{
      width:44px;height:44px;border-radius:12px;
      background:linear-gradient(135deg,var(--primary),var(--primary-2));
      color:#fff;display:grid;place-items:center;font-weight:800;font-size:18px;
    }
    .visual h2{margin:0;font-size:28px}
    .visual p{margin:0;color:var(--muted);line-height:1.6}
    .form{padding:38px 34px;display:flex;flex-direction:column;justify-content:center}
    .title{font-size:26px;font-weight:800;margin:0 0 8px}
    .subtitle{color:var(--muted);margin-bottom:22px}
    .alert{padding:12px 14px;border-radius:12px;font-size:14px;margin:10px 0;border:1px solid}
    .alert-danger{border-color:#fecaca;color:#991b1b;background:#fff7f7}
    .alert-success{border-color:#bbf7d0;color:#065f46;background:#f0fdf4}
    .group{margin-top:16px}
    label{display:block;margin-bottom:8px;font-weight:600;font-size:14px}
    .input{
      width:100%;padding:12px 14px;border:1px solid var(--border);border-radius:12px;
      font-size:15px;outline:none;background:#fff;transition:border-color .15s,box-shadow .15s;
    }
    .input:focus{border-color:var(--primary);box-shadow:0 0 0 4px rgba(230,57,70,.15)}
    .btn{
      width:100%;margin-top:18px;padding:12px;border:none;border-radius:12px;
      font-weight:700;font-size:15px;cursor:pointer;color:#fff;
      background:linear-gradient(135deg,var(--primary),var(--primary-2));
      box-shadow:0 6px 18px rgba(230,57,70,.25);transition:transform .05s;
    }
    .btn:active{transform:translateY(1px)}
    .row{display:flex;justify-content:space-between;align-items:center;margin-top:14px;font-size:14px}
    .link{color:var(--primary);text-decoration:none}
    .link:hover{text-decoration:underline}
    .footer{text-align:center;color:var(--muted);margin-top:16px;font-size:14px}
    .toggle{position:absolute;right:12px;top:50%;transform:translateY(-50%);border:none;background:transparent;cursor:pointer;color:var(--muted)}
    .input-wrap{position:relative}
  </style>
</head>
<body>
  <div class="page">
    <div class="auth">
      <div class="visual">
        <div class="brand">
          <div class="brand-badge">PT</div><div>PhoneThai</div>
        </div>
        <h2>Chào mừng trở lại ❤️</h2>
        <p>Đăng nhập để khám phá ưu đãi hấp dẫn và sản phẩm yêu thích.</p>
      </div>

      <div class="form">
        <h1 class="title">Đăng nhập</h1>
        <p class="subtitle">Nhập thông tin tài khoản để tiếp tục.</p>

        <c:if test="${not empty errorMessage}">
          <div class="alert alert-danger">${errorMessage}</div>
        </c:if>

        <form action="${ctx}/login" method="post">
          <div class="group">
            <label for="username">Tên đăng nhập</label>
            <input class="input" type="text" id="username" name="username" value="${cookie.userC.value}" required/>
          </div>

          <div class="group">
            <label for="password">Mật khẩu</label>
            <div class="input-wrap">
              <input class="input" type="password" id="password" name="password" value="${cookie.passC.value}" required/>
              <button type="button" class="toggle" onclick="togglePw()">👁</button>
            </div>
          </div>

          <div class="row">
            <label><input type="checkbox" name="remember" ${not empty cookie.userC ? 'checked' : ''}/> Ghi nhớ</label>
            <a href="${ctx}/forgot" class="link">Quên mật khẩu?</a>
          </div>

          <button class="btn" type="submit">Đăng nhập ngay</button>
        </form>

        <div class="footer">
          Chưa có tài khoản? <a class="link" href="${ctx}/auth/Register.jsp">Đăng ký</a>

        </div>
      </div>
    </div>
  </div>
  <script>
    function togglePw(){
      const i=document.getElementById('password');
      i.type=i.type==='password'?'text':'password';
    }
  </script>
</body>
</html>
