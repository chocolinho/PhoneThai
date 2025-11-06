<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
  <title>Thông tin cá nhân</title>
 
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="../css/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<jsp:include page="/Header.jsp"/>

<div class="container py-4" style="max-width:900px">
  <div class="row g-4">
    <div class="col-md-6">
      <div class="card p-4 shadow-sm">
        <h5 class="mb-3">Thông tin tài khoản</h5>
        <div class="mb-2"><b>Họ tên:</b> ${sessionScope.user.fullName}</div>
        <div class="mb-2"><b>Email:</b> ${sessionScope.user.email}</div>
        <div class="mb-2"><b>SĐT:</b> ${sessionScope.user.phone}</div>
        <a href="${ctx}/auth/profile/edit" class="btn btn-danger mt-3">Chỉnh sửa</a>

        <c:if test="${param.ok == '1'}">
          <div class="alert alert-success mt-3">Đã lưu thông tin.</div>
        </c:if>
      </div>
    </div>

    <div class="col-md-6">
      <div class="card p-4 shadow-sm">
        <h5 class="mb-3">Đổi mật khẩu</h5>
        <c:if test="${param.perr == 'mismatch'}"><div class="alert alert-warning">Mật khẩu nhập lại không khớp.</div></c:if>
        <c:if test="${param.perr == 'old'}"><div class="alert alert-danger">Mật khẩu hiện tại không đúng.</div></c:if>
        <c:if test="${param.perr == 'save'}"><div class="alert alert-danger">Không lưu được mật khẩu. Thử lại.</div></c:if>
        <c:if test="${param.pok == '1'}"><div class="alert alert-success">Đổi mật khẩu thành công.</div></c:if>

        <form action="${ctx}/auth/profile/password" method="post" class="row g-3">
          <div class="col-12">
            <input type="password" name="oldPassword" class="form-control" placeholder="Mật khẩu hiện tại" required>
          </div>
          <div class="col-md-6">
            <input type="password" name="newPassword" class="form-control" placeholder="Mật khẩu mới" required>
          </div>
          <div class="col-md-6">
            <input type="password" name="rePassword" class="form-control" placeholder="Nhập lại mật khẩu" required>
          </div>
          <div class="col-12">
            <button class="btn btn-danger">Cập nhật mật khẩu</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>
</body>
</html>
