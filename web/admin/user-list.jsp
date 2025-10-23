<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Quản lý Người dùng</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${ctx}/css/admin.css" rel="stylesheet">
</head>
<body class="admin-body">

<div class="d-flex">
  <!-- Sidebar -->
  <nav class="admin-sidebar">
    <a href="${ctx}/" class="sidebar-brand">PhoneThai</a>
    <ul class="nav flex-column mt-3">
      <li class="nav-item"><a class="nav-link" href="${ctx}/admin/dashboard">Tổng quan</a></li>
      <li class="nav-item"><a class="nav-link active" href="${ctx}/admin/users">Quản lý User</a></li>
      <li class="nav-item"><a class="nav-link" href="${ctx}/admin/products">Quản lý Sản phẩm</a></li>
      <li class="nav-item"><a class="nav-link" href="${ctx}/admin/orders">Quản lý Đơn hàng</a></li>
    </ul>
    <a href="${ctx}/logout" class="nav-link logout-link mt-auto">Đăng xuất</a>
  </nav>

  <!-- Main -->
  <div class="admin-main-content flex-grow-1">
    <!-- Header -->
    <header class="admin-header">
      <div class="search-bar">
        <form method="get" action="${ctx}/admin/users" class="w-100 d-flex align-items-center gap-2">
          <input type="hidden" name="action" value="list"/>
          <span class="text-muted small">Tìm:</span>

          <!-- ID -->
          <input type="number"
                 name="uid"
                 value="${param.uid}"
                 class="form-control search-input search-input--id"
                 placeholder="ID">

          <!-- Họ tên -->
          <input type="text"
                 name="q"
                 value="${param.q}"
                 class="form-control search-input search-input--text"
                 placeholder="Họ tên...">

          <!-- Username -->
          <input type="text"
                 name="login"
                 value="${param.login}"
                 class="form-control search-input"
                 placeholder="Username...">

          <div class="search-actions d-flex align-items-center gap-2">
            <button class="btn btn-primary search-btn" type="submit">Tìm</button>
            <a class="btn btn-outline-secondary search-reset" href="${ctx}/admin/users">Xoá</a>
          </div>
        </form>
      </div>

      <div class="d-flex align-items-center gap-2">
        <img class="profile-avatar" src="${ctx}/images/profile.png" alt="">
        <div>
          <div class="admin-name"><c:out value="${sessionScope.user.fullName}"/></div>
          <div class="admin-role">Administrator</div>
        </div>
      </div>
    </header>

    <!-- Flash mật khẩu tạm sau khi reset (hiện 1 lần) -->
    <c:if test="${not empty sessionScope.flash_pw}">
      <div class="alert alert-warning d-flex align-items-center" role="alert">
        <div class="me-2">🔐</div>
        <div><strong>${sessionScope.flash_pw}</strong></div>
      </div>
      <c:remove var="flash_pw" scope="session"/>
    </c:if>

    <!-- Content -->
    <main>
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h4 mb-0">Người dùng</h1>
        <a class="btn btn-primary btn-sm" href="${ctx}/admin/users?action=new">+ Thêm User</a>
      </div>

      <div class="admin-card">
        <div class="admin-table card-body p-0">
          <table class="table table-hover mb-0">
            <thead>
              <tr>
                <th class="user-col--id">#ID</th>
                <th class="user-col--uname">Username</th>
                <th class="user-col--fname">Họ tên</th>
                <th class="user-col--email">Email</th>
                <th class="user-col--role">Role</th>
                <th class="user-col--created">Ngày tạo</th>
                <th class="text-end user-col--act">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <c:forEach items="${requestScope.list}" var="u">
                <tr>
                  <td>${u.userId}</td>

                  <td class="text-truncate">${u.username}</td>

                  <td class="text-truncate">
                    <c:out value="${empty u.fullName ? '-' : u.fullName}"/>
                  </td>

                  <td class="text-truncate">
                    <c:out value="${u.email}"/>
                  </td>

                  <td>
                    <span class="badge-role ${u.role == 1 ? 'admin' : 'user'}">
                      ${u.role == 1 ? 'Admin' : 'User'}
                    </span>
                  </td>

                  <td><fmt:formatDate value="${u.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>

                  <td class="text-end">
                    <div class="user-actions d-inline-flex align-items-center">
                      <a class="btn btn-sm btn-outline-secondary me-2"
                         href="${ctx}/admin/users?action=edit&id=${u.userId}">
                        Sửa
                      </a>

                      <form class="d-inline-block me-2" action="${ctx}/admin/users" method="post"
                            onsubmit="return confirm('Reset mật khẩu cho user #${u.userId}?');">
                        <input type="hidden" name="action" value="resetpw">
                        <input type="hidden" name="id" value="${u.userId}">
                        <button type="submit" class="btn btn-sm btn-warning">Reset</button>
                      </form>

                      <form class="d-inline-block" action="${ctx}/admin/users" method="post"
                            onsubmit="return confirm('Xoá user #${u.userId}?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${u.userId}">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Xoá</button>
                      </form>
                    </div>
                  </td>
                </tr>
              </c:forEach>

              <c:if test="${empty requestScope.list}">
                <tr><td colspan="7" class="text-center text-muted py-4">Chưa có người dùng</td></tr>
              </c:if>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Phân trang (tuỳ Controller truyền vào) -->
      <c:if test="${not empty requestScope.pageCount}">
        <nav class="mt-3">
          <ul class="pagination pagination-sm mb-0">
            <c:forEach begin="1" end="${requestScope.pageCount}" var="pno">
              <li class="page-item ${pno == requestScope.currentPage ? 'active' : ''}">
                <a class="page-link"
                   href="${ctx}/admin/users?page=${pno}
                         &uid=${fn:escapeXml(param.uid)}
                         &q=${fn:escapeXml(param.q)}
                         &login=${fn:escapeXml(param.login)}">
                  ${pno}
                </a>
              </li>
            </c:forEach>
          </ul>
        </nav>
      </c:if>

    </main>
  </div>
</div>

</body>
</html>
