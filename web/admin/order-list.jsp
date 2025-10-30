<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Đơn hàng | Admin PhoneThai</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="../css/admin.css" rel="stylesheet" type="text/css"/>
    </head>
    <body class="admin-body">

        <div class="d-flex">
        
            <nav class="admin-sidebar">
                <a href="${ctx}/" class="sidebar-brand">PhoneThai</a>
                <ul class="nav flex-column mt-3">
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/dashboard">Tổng quan</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/users">Quản lý User</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/products">Quản lý Sản phẩm</a></li>
                    <li class="nav-item"><a class="nav-link active" href="${ctx}/admin/orders">Quản lý Đơn hàng</a></li>
                </ul>
                <a href="${ctx}/logout" class="nav-link logout-link mt-auto">Đăng xuất</a>
            </nav>

            <div class="admin-main-content flex-grow-1">
                <header class="admin-header">
                    <div class="search-bar">
                        <form method="get" action="${ctx}/admin/orders" class="w-100 d-flex align-items-center gap-2">
                            <span class="text-muted small">Tìm:</span>
                            <input type="text" name="q" value="${param.q}" class="form-control search-input search-input--text" placeholder="mã đơn / họ tên / email...">
                            <select name="status" class="form-select" style="max-width:180px">
                                <option value="">Tất cả trạng thái</option>
                                <c:set var="st" value="${param.status}" />
                                <c:forEach items="${requestScope.statusOptions}" var="opt">
                                    <option value="${opt}" ${st==opt?'selected':''}>
                                        <c:choose>
                                            <c:when test="${opt=='pending'}">Chờ xử lý</c:when>
                                            <c:when test="${opt=='processing'}">Đang xử lý</c:when>
                                            <c:when test="${opt=='shipped'}">Đã gửi</c:when>
                                            <c:when test="${opt=='completed'}">Hoàn thành</c:when>
                                            <c:otherwise>Đã huỷ</c:otherwise>
                                        </c:choose>
                                    </option>
                                </c:forEach>
                            </select>
                            <button class="btn btn-primary search-btn" type="submit">Lọc</button>
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

                <main>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h1 class="h4 mb-0">Đơn hàng</h1>
                        <a class="btn btn-primary btn-sm" href="${ctx}/admin/orders?action=new">+ Tạo đơn</a>
                    </div>

                    <div class="admin-card">
                        <div class="admin-table card-body p-0">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th style="width:80px">#Đơn</th>
                                        <th style="width:28%">Khách hàng</th>
                                        <th class="text-end" style="width:12%">SL</th>
                                        <th class="text-end" style="width:14%">Tổng (₫)</th>
                                        <th style="width:14%">Trạng thái</th>
                                        <th style="width:18%">Ngày tạo</th>
                                        <th class="text-end" style="width:14%">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${requestScope.list}" var="o">
                                        <tr>
                                            <td>${o.orderId}</td>

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

                                            <td class="text-end">${o.quantity}</td>

                                            <td class="text-end">
                                                <fmt:formatNumber value="${o.total}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </td>

                                            <td>
                                                <%-- --- REFINEMENT: Sử dụng badge-status --- --%>
                                                <c:set var="s" value="${o.status}"/>
                                                <span class="badge-status ${s}">
                                                    <c:choose>
                                                        <c:when test="${s=='pending'}">Chờ xử lý</c:when>
                                                        <c:when test="${s=='processing'}">Đang xử lý</c:when>
                                                        <c:when test="${s=='shipped'}">Đã gửi</c:when>
                                                        <c:when test="${s=='completed'}">Hoàn thành</c:when>
                                                        <c:otherwise>Đã huỷ</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>

                                            <td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm"/></td>

                                            <td class="text-end">
                                                <a class="btn btn-sm btn-outline-secondary"
                                                   href="${ctx}/admin/orders?action=edit&id=${o.orderId}">Sửa</a>
                                                <form class="d-inline" action="${ctx}/admin/orders" method="post"
                                                      onsubmit="return confirm('Xoá đơn #${o.orderId}?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${o.orderId}">
                                                    <button class="btn btn-sm btn-outline-danger">Xoá</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty requestScope.list}">
                                        <tr><td colspan="7" class="text-center text-muted py-4">Chưa có đơn hàng</td></tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <c:if test="${not empty requestScope.pageCount}">
                        <nav class="mt-3">
                            <ul class="pagination pagination-sm mb-0">
                                <c:forEach begin="1" end="${requestScope.pageCount}" var="pno">
                                    <li class="page-item ${pno == requestScope.currentPage ? 'active' : ''}">
                                        <a class="page-link" href="${ctx}/admin/orders?page=${pno}&q=${param.q}&status=${param.status}">${pno}</a>
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