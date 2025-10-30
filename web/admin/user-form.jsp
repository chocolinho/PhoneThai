<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>
    <c:choose>
      <c:when test="${not empty requestScope.formUser}">Sửa User</c:when>
      <c:otherwise>Thêm User</c:otherwise>
    </c:choose> | Admin PhoneThai
  </title>

  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="../css/admin.css" rel="stylesheet" type="text/css"/>
  <%-- --- REFINEMENT: Đã xoá thẻ <style> nội tuyến (đã chuyển vào admin.css) --- --%>
</head>
<body class="admin-body">

<%-- --- REFINEMENT: Thêm layout admin chuẩn --- --%>
<div class="d-flex">
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

  <div class="admin-main-content flex-grow-1">
    <header class="admin-header">
      <div class="search-bar"></div>
      <div class="d-flex align-items-center gap-2">
        <img class="profile-avatar" src="${ctx}/images/profile.png" alt="">
        <div>
          <div class="admin-name"><c:out value="${sessionScope.user.fullName}"/></div>
          <div class="admin-role">Administrator</div>
        </div>
      </div>
    </header>

    <main>
      <div class="d-flex align-items-center justify-content-between mb-3">
        <h3 class="mb-0 h4">
          <c:choose>
            <c:when test="${not empty requestScope.formUser}">Sửa User</c:when>
            <c:otherwise>Thêm User</c:otherwise>
          </c:choose>
        </h3>
        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/users">← Danh sách</a>
      </div>

      <div class="card admin-card">
        <div class="card-body p-3 p-md-4">

          <c:if test="${not empty requestScope.errorMsg}">
            <div class="alert alert-danger py-2 mb-3">${requestScope.errorMsg}</div>
          </c:if>

          <c:if test="${not empty sessionScope.flash_pw}">
            <div class="alert alert-warning py-2 mb-3">
              ${sessionScope.flash_pw}
            </div>
            <c:remove var="flash_pw" scope="session"/>
          </c:if>

          <form id="userForm" action="${ctx}/admin/users" method="post" class="row g-3" novalidate>
            <input type="hidden" name="action" value="save">
            <c:if test="${not empty requestScope.formUser}">
              <input type="hidden" name="id" value="${requestScope.formUser.userId}">
            </c:if>

            <div class="col-md-6">
              <label class="form-label">Username <span class="text-danger">*</span></label>
              <input name="username" class="form-control" required
                     placeholder="vd: dinhthai"
                     value="${requestScope.formUser.username}">
              <div class="invalid-feedback">Vui lòng nhập username.</div>
            </div>

            <c:if test="${empty requestScope.formUser}">
              <div class="col-md-6">
                <label class="form-label">
                  Mật khẩu <span class="text-danger">*</span>
                </label>
                <div class="input-group">
                  <input id="password" name="password" type="password" class="form-control" placeholder="Ít nhất 6 ký tự" required>
                  <button class="btn btn-outline-secondary" type="button" id="togglePw">Hiện</button>
                </div>
                <div class="form-text muted">Mật khẩu sẽ được băm trước khi lưu (không lưu plain text).</div>
              </div>

              <div class="col-md-6">
                <label class="form-label">Xác nhận mật khẩu <span class="text-danger">*</span></label>
                <input id="confirmPassword" type="password" class="form-control" placeholder="Nhập lại mật khẩu" required>
                <div class="invalid-feedback">Xác nhận mật khẩu chưa khớp.</div>
              </div>
            </c:if>

            <div class="col-md-6">
              <label class="form-label">Họ tên</label>
              <input name="full_name" class="form-control" placeholder="vd: Nguyễn Đình Thái"
                     value="${requestScope.formUser.fullName}">
            </div>

            <div class="col-md-6">
              <label class="form-label">Email</label>
              <input name="email" type="email" class="form-control" placeholder="name@example.com"
                     value="${requestScope.formUser.email}">
              <div class="invalid-feedback">Email không hợp lệ.</div>
            </div>

            <div class="col-md-3">
              <label class="form-label">Role</label>
              <select name="role" class="form-select">
                <option value="0" ${requestScope.formUser.role==0?'selected':''}>User</option>
                <option value="1" ${requestScope.formUser.role==1?'selected':''}>Admin</option>
              </select>
              <div class="form-text muted">0 = User, 1 = Admin.</div>
            </div>

            <div class="col-12 d-flex gap-2">
              <button class="btn btn-primary" type="submit">Lưu</button>
              <a class="btn btn-secondary" href="${ctx}/admin/users">Huỷ</a>

              <c:if test="${not empty requestScope.formUser}">
                <form action="${ctx}/admin/users" method="post" class="ms-auto d-inline"
                      onsubmit="return confirm('Reset mật khẩu cho ${requestScope.formUser.username}?');">
                  <input type="hidden" name="action" value="resetpw">
                  <input type="hidden" name="id" value="${requestScope.formUser.userId}">
                  <button class="btn btn-outline-warning">Reset mật khẩu</button>
                </form>
                <button type="button" class="btn btn-outline-danger" data-bs-toggle="modal" data-bs-target="#confirmDelete">
                  Xoá
                </button>
              </c:if>
            </div>
          </form>

          <small class="text-muted d-block mt-2">
            <c:choose>
              <c:when test="${not empty requestScope.formUser}">
                Khi sửa: mật khẩu <strong>không thể thay đổi tại đây</strong>. Dùng nút <em>Reset mật khẩu</em> để tạo mật khẩu tạm.
              </c:when>
              <c:otherwise>
                Khi thêm mới: yêu cầu mật khẩu và xác nhận (≥ 6 ký tự). Mật khẩu sẽ được băm (hash) trước khi lưu.
              </c:otherwise>
            </c:choose>
          </small>
        </div>
      </div>
    </main>
  </div>
</div>

<c:if test="${not empty requestScope.formUser}">
<div class="modal fade" id="confirmDelete" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" action="${ctx}/admin/users" method="post">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="id" value="${requestScope.formUser.userId}">
      <div class="modal-header">
        <h5 class="modal-title">Xoá User</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        Bạn có chắc chắn muốn xoá người dùng
        <strong><c:out value="${requestScope.formUser.username}"/></strong>?
        Thao tác này không thể hoàn tác.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Huỷ</button>
        <button type="submit" class="btn btn-danger">Xoá</button>
      </div>
    </form>
  </div>
</div>
</c:if>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
  // Toggle mật khẩu (chỉ tồn tại khi THÊM MỚI)
  const pw = document.getElementById('password');
  const cfpw = document.getElementById('confirmPassword');
  const toggle = document.getElementById('togglePw');
  toggle?.addEventListener('click', () => {
    const type = pw.type === 'password' ? 'text' : 'password';
    pw.type = type;
    if (cfpw) cfpw.type = type;
    toggle.textContent = type === 'password' ? 'Hiện' : 'Ẩn';
  });
  // Validate form
  document.getElementById('userForm')?.addEventListener('submit', (e) => {
    const form = e.target;

    // Required username
    const username = form.querySelector('input[name="username"]');
    if (!username.value.trim()) {
      username.classList.add('is-invalid');
      e.preventDefault(); return;
    } else {
      username.classList.remove('is-invalid');
    }

    // Nếu là THÊM MỚI: phải có password + confirm hợp lệ
    if (pw) {
      if (!pw.value.trim() || pw.value.length < 6) {
        pw.classList.add('is-invalid');
        alert('Mật khẩu phải có ít nhất 6 ký tự.');
        e.preventDefault(); return;
      } else {
        pw.classList.remove('is-invalid');
      }

      if (!cfpw.value.trim() || cfpw.value !== pw.value) {
        cfpw.classList.add('is-invalid');
        e.preventDefault(); return;
      } else {
        cfpw.classList.remove('is-invalid');
      }
    }
  });
</script>
</body>
</html>