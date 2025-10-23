<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Dashboard - PhoneThai</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="${ctx}/css/admin.css" rel="stylesheet">
</head>
<body class="admin-body">

<div class="d-flex">
  <!-- Sidebar -->
  <nav class="admin-sidebar">
    <a href="${ctx}/" class="sidebar-brand">PhoneThai</a>
    <ul class="nav flex-column mt-3">
      <li class="nav-item">
        <a class="nav-link active" href="${ctx}/admin/dashboard">Tổng quan</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${ctx}/admin/users">Quản lý User</a>
      </li>
      <li class="nav-item">
        <!-- Đảm bảo route này khớp mapping của ProductController -->
        <a class="nav-link" href="${ctx}/admin/products">Quản lý Sản phẩm</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" href="${ctx}/admin/orders">Quản lý Đơn hàng</a>
      </li>
    </ul>
    <a href="${ctx}/logout" class="nav-link logout-link mt-auto">Đăng xuất</a>
  </nav>

  <!-- Main -->
  <div class="admin-main-content flex-grow-1">
    <!-- Header -->
    <header class="admin-header">
      <div class="search-bar">
        <form method="get" action="${ctx}/admin/search" class="w-100 d-flex align-items-center gap-2">
          <span class="text-muted small">Tìm:</span>
          <input type="text" name="q" class="form-control" placeholder="người dùng, đơn hàng, sản phẩm...">
        </form>
      </div>
      <div class="d-flex align-items-center gap-2">
        <img class="profile-avatar" src="${ctx}/images/profile.png" alt="">
        <div>
          <div class="admin-name">
            <c:out value="${empty sessionScope.user.fullName ? 'Admin' : sessionScope.user.fullName}"/>
          </div>
          <div class="admin-role">Administrator</div>
        </div>
      </div>
    </header>

    <main>
      <h1 class="h4 mb-3">Tổng quan</h1>

      <!-- KPIs -->
      <div class="row g-3">
        <div class="col-6 col-md-3">
          <div class="stat-card">
            <div class="icon">U</div>
            <div>
              <div class="stat-number">
                <c:out value="${empty requestScope.totalUsers ? 0 : requestScope.totalUsers}"/>
              </div>
              <div class="stat-label">Người dùng</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-md-3">
          <div class="stat-card">
            <div class="icon">P</div>
            <div>
              <div class="stat-number">
                <c:out value="${empty requestScope.totalProducts ? 0 : requestScope.totalProducts}"/>
              </div>
              <div class="stat-label">Sản phẩm</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-md-3">
          <div class="stat-card">
            <div class="icon">O</div>
            <div>
              <div class="stat-number">
                <c:out value="${empty requestScope.totalOrders ? 0 : requestScope.totalOrders}"/>
              </div>
              <div class="stat-label">Đơn hàng</div>
            </div>
          </div>
        </div>
        <div class="col-6 col-md-3">
          <div class="stat-card">
            <div class="icon">₫</div>
            <div>
              <div class="stat-number">
                <fmt:formatNumber value="${empty requestScope.totalRevenue ? 0 : requestScope.totalRevenue}"
                                  type="currency" currencySymbol="₫" maxFractionDigits="0"/>
              </div>
              <div class="stat-label">Doanh thu</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Bảng đơn hàng gần đây -->
      <div class="admin-card mt-3">
        <div class="card-header">Đơn hàng gần đây</div>
        <div class="admin-table card-body p-0">
          <table class="table table-hover mb-0">
            <thead>
            <tr>
              <th style="width:90px">#Đơn</th>
              <th>Khách hàng</th>
              <th class="text-end" style="width:160px">Tổng tiền</th>
              <th style="width:140px">Trạng thái</th>
              <th style="width:180px">Ngày tạo</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${requestScope.recentOrders}" var="o">
              <tr>
                <!-- Link sang trang sửa đơn cho nhanh -->
                <td>
                  <a href="${ctx}/admin/orders?action=edit&id=${o.orderId}" class="text-decoration-none">
                    ${o.orderId}
                  </a>
                </td>

                <!-- Tên khách: ưu tiên fullName/email nếu đã join; fallback: User #userId -->
                <td class="text-truncate">
                  <div class="fw-medium">
                    <c:choose>
                      <c:when test="${not empty o.userFullName}">
                        <c:out value="${o.userFullName}"/>
                      </c:when>
                      <c:otherwise>
                        User #${o.userId}
                      </c:otherwise>
                    </c:choose>
                  </div>
                  <c:if test="${not empty o.userEmail}">
                    <div class="text-muted small"><c:out value="${o.userEmail}"/></div>
                  </c:if>
                </td>

                <td class="text-end">
                  <fmt:formatNumber value="${o.total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                </td>

                <td>
                  <c:set var="s" value="${o.status}" />
                  <span class="badge
                    ${s=='pending'?'bg-secondary':
                      (s=='processing'?'bg-info':
                      (s=='shipped'?'bg-primary':
                      (s=='completed'?'bg-success':'bg-danger')))}">
                    <c:choose>
                      <c:when test="${s=='pending'}">Chờ xử lý</c:when>
                      <c:when test="${s=='processing'}">Đang xử lý</c:when>
                      <c:when test="${s=='shipped'}">Đã gửi</c:when>
                      <c:when test="${s=='completed'}">Hoàn thành</c:when>
                      <c:otherwise>Đã huỷ</c:otherwise>
                    </c:choose>
                  </span>
                </td>

                <td>
                  <fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                </td>
              </tr>
            </c:forEach>

            <c:if test="${empty requestScope.recentOrders}">
              <tr><td colspan="5" class="text-center text-muted py-4">Chưa có đơn hàng</td></tr>
            </c:if>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </div>
</div>

</body>
</html>
