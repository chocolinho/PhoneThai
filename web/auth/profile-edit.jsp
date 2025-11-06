<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
  <title>Chỉnh sửa thông tin</title>
  <link href="${ctx}/css/style.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="../css/style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<jsp:include page="/Header.jsp"/>

<div class="container py-4" style="max-width:700px">
  <div class="card p-4 shadow-sm">
    <h4 class="mb-3">Chỉnh sửa thông tin</h4>
    <c:if test="${not empty error}">
      <div class="alert alert-danger">${error}</div>
    </c:if>

    <form action="${ctx}/auth/profile/edit" method="post" class="row g-3">
      <div class="col-12">
        <label class="form-label">Họ tên</label>
        <input name="fullName" class="form-control" value="${user.fullName}" required>
      </div>
      <div class="col-md-6">
        <label class="form-label">Email</label>
        <input name="email" type="email" class="form-control" value="${user.email}">
      </div>
      <div class="col-md-6">
        <label class="form-label">Số điện thoại</label>
        <input name="phone" class="form-control" value="${user.phone}">
      </div>
      <div class="col-12">
        <button class="btn btn-danger">Lưu thay đổi</button>
        <a href="${ctx}/auth/profile" class="btn btn-outline-secondary ms-2">Hủy</a>
      </div>
    </form>
  </div>
</div>
</body>
</html>
