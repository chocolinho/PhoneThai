<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý Sản phẩm</title>
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
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/users">Quản lý User</a></li>
                    <li class="nav-item"><a class="nav-link active" href="${ctx}/admin/products">Quản lý Sản phẩm</a></li>
                    <li class="nav-item"><a class="nav-link" href="${ctx}/admin/orders">Quản lý Đơn hàng</a></li>
                </ul>
                <a href="${ctx}/logout" class="nav-link logout-link mt-auto">Đăng xuất</a>
            </nav>

            <!-- Main -->
            <div class="admin-main-content flex-grow-1">
                <!-- Header -->
                <header class="admin-header">
                    <div class="search-bar">
                        <form method="get" action="${ctx}/admin/products" class="w-100 d-flex align-items-center gap-2">
                            <input type="hidden" name="action" value="list"/>
                            <span class="text-muted small">Tìm:</span>

                            <input type="text"   name="q"   value="${param.q}"
                                   class="form-control search-input search-input--text"
                                   placeholder="Tên sản phẩm..."/>

                            <input type="number" name="pid" value="${param.pid}"
                                   class="form-control search-input search-input--id"
                                   placeholder="Mã"/>

                            <div class="search-actions d-flex align-items-center gap-2">
                                <button class="btn btn-primary search-btn" type="submit">Tìm</button>
                                <a class="btn btn-outline-secondary search-reset" href="${ctx}/admin/products">Xoá</a>
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

                <!-- Content -->
                <main>
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h1 class="h4 mb-0">Sản phẩm</h1>
                        <a class="btn btn-primary btn-sm" href="${ctx}/admin/products?action=new">+ Thêm Sản phẩm</a>
                    </div>

                    <div class="admin-card">
                        <div class="admin-table card-body p-0">
                            <table class="table table-hover mb-0">
                                <thead>
                                    <tr>
                                        <th>#ID</th>
                                        <th style="width:32%">Tên</th>
                                        <th class="text-end">Giá</th>
                                        <th class="text-end">Tồn</th>
                                        <th>Danh mục</th>
                                        <th>Ảnh</th>
                                        <th class="text-end">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${requestScope.list}" var="p">
                                        <tr>
                                            <td>${p.productId}</td>
                                            <td class="text-truncate">${p.name}</td>
                                            <td class="text-end">
                                                <fmt:formatNumber value="${p.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                            </td>
                                            <td class="text-end">${p.stock}</td>
                                            <td>${p.category}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty p.image}">
                                                        <img src="${ctx}/images/${fn:replace(p.image, 'images/', '')}"
                                                             width="40" height="40"
                                                             style="object-fit:cover;border-radius:6px;">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${ctx}/images/no-image.png"
                                                             width="40" height="40"
                                                             style="object-fit:cover;border-radius:6px;opacity:0.6;">
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <td class="text-end">
                                                <a class="btn btn-sm btn-outline-secondary" href="${ctx}/admin/products?action=edit&id=${p.productId}">Sửa</a>
                                                <form class="d-inline" action="${ctx}/admin/products" method="post"
                                                      onsubmit="return confirm('Xoá sản phẩm #${p.productId}?');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${p.productId}">
                                                    <button class="btn btn-sm btn-outline-danger">Xoá</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty requestScope.list}">
                                        <tr><td colspan="7" class="text-center text-muted py-4">Chưa có sản phẩm</td></tr>
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
                                           href="${ctx}/admin/products?page=${pno}&q=${fn:escapeXml(param.q)}&pid=${fn:escapeXml(param.pid)}">
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
