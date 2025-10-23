<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title><c:out value="${empty order ? 'Tạo đơn hàng' : 'Sửa đơn hàng #'}"/><c:out value="${empty order ? '' : order.orderId}"/></title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="${ctx}/css/admin.css" rel="stylesheet">
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
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h1 class="h4 mb-0">
                            <c:out value="${empty order ? 'Tạo đơn hàng' : 'Sửa đơn hàng #'}"/><c:out value="${empty order ? '' : order.orderId}"/>
                        </h1>
                        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/orders">← Quay lại</a>
                    </div>

                    <form action="${ctx}/admin/orders" method="post" id="orderForm">
                        <input type="hidden" name="action" value="${empty order ? 'create' : 'update'}" />
                        <c:if test="${not empty order}">
                            <input type="hidden" name="order_id" value="${order.orderId}" />
                        </c:if>

                        <div class="row g-3">
                            <div class="col-lg-4">
                                <div class="admin-card p-3">
                                    <div class="mb-3">
                                        <label class="form-label">Khách hàng (user)</label>
                                        <select name="user_id" class="form-select" required>
                                            <c:forEach items="${users}" var="u">
                                                <option value="${u.userId}" ${not empty order && order.userId==u.userId ? 'selected' : ''}>
                                                    <c:out value="${empty u.fullName ? u.username : u.fullName}"/>
                                                    <c:if test="${not empty u.email}"> — <c:out value="${u.email}"/></c:if>
                                                    </option>
                                            </c:forEach>
                                        </select>

                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Ngày đặt</label>
                                        <input type="datetime-local" name="order_date" class="form-control"
                                               value="${not empty order ? order.orderDateLocal : ''}">
                                        <div class="form-text">Để trống sẽ lấy thời điểm hiện tại.</div>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label">Trạng thái</label>
                                        <select name="status" class="form-select">
                                            <c:forEach items="${statusOptions}" var="opt">
                                                <option value="${opt}" ${not empty order && order.status==opt ? 'selected':''}>
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
                                    </div>

                                    <div class="border-top pt-3">
                                        <div class="d-flex justify-content-between">
                                            <span class="fw-medium">Số lượng</span>
                                            <span id="sumQty" class="fw-bold">0</span>
                                        </div>
                                        <div class="d-flex justify-content-between">
                                            <span class="fw-medium">Tổng tiền</span>
                                            <span id="sumTotal" class="fw-bold">₫0</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-lg-8">
                                <div class="admin-card p-3">
                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                        <h2 class="h6 mb-0">Chi tiết đơn</h2>
                                        <button class="btn btn-sm btn-primary" type="button" id="btnAddRow">+ Thêm dòng</button>
                                    </div>

                                    <div class="table-responsive">
                                        <table class="table align-middle" id="detailsTable">
                                            <thead>
                                                <tr>
                                                    <th style="width:45%">Sản phẩm</th>
                                                    <th class="text-end" style="width:15%">Giá (₫)</th>
                                                    <th class="text-end" style="width:15%">SL</th>
                                                    <th class="text-end" style="width:15%">Tạm tính</th>
                                                    <th class="text-end" style="width:10%"></th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:choose>
                                                    <c:when test="${not empty orderDetails}">
                                                        <c:forEach items="${orderDetails}" var="d">
                                                            <tr>
                                                                <td>
                                                                    <select name="product_id" class="form-select product-select" required>
                                                                        <option value="">-- Chọn --</option>
                                                                        <c:forEach items="${products}" var="p">
                                                                            <option value="${p.productId}" data-price="${p.price}" ${d.productId==p.productId?'selected':''}>
                                                                                <c:out value="${p.name}"/>
                                                                            </option>
                                                                        </c:forEach>
                                                                    </select>
                                                                </td>
                                                                <td class="text-end">
                                                                    <input type="number" step="1000" min="0" class="form-control text-end price-input" name="price" value="${d.price}" required>
                                                                </td>
                                                                <td class="text-end">
                                                                    <input type="number" min="1" class="form-control text-end qty-input" name="quantity" value="${d.quantity}" required>
                                                                </td>
                                                                <td class="text-end">
                                                                    <input type="text" class="form-control text-end subtotal-input" name="subtotal" value="${d.subtotal}" readonly>
                                                                </td>
                                                                <td class="text-end">
                                                                    <button class="btn btn-sm btn-outline-danger btnRemoveRow" type="button">Xoá</button>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <tr>
                                                            <td>
                                                                <select name="product_id" class="form-select product-select" required>
                                                                    <option value="">-- Chọn --</option>
                                                                    <c:forEach items="${products}" var="p">
                                                                        <option value="${p.productId}" data-price="${p.price}">
                                                                            <c:out value="${p.name}"/>
                                                                        </option>
                                                                    </c:forEach>
                                                                </select>
                                                            </td>
                                                            <td class="text-end">
                                                                <input type="number" step="1000" min="0" class="form-control text-end price-input" name="price" required>
                                                            </td>
                                                            <td class="text-end">
                                                                <input type="number" min="1" class="form-control text-end qty-input" name="quantity" value="1" required>
                                                            </td>
                                                            <td class="text-end">
                                                                <input type="text" class="form-control text-end subtotal-input" name="subtotal" value="0" readonly>
                                                            </td>
                                                            <td class="text-end">
                                                                <button class="btn btn-sm btn-outline-danger btnRemoveRow" type="button">Xoá</button>
                                                            </td>
                                                        </tr>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>

                                <div class="mt-3 text-end">
                                    <button class="btn btn-secondary" type="button" onclick="history.back()">Huỷ</button>
                                    <button class="btn btn-primary" type="submit">Lưu</button>
                                </div>
                            </div>
                        </div>
                    </form>
                </main>
            </div>
        </div>

        <script>
            (function () {
                const fmtVND = v => new Intl.NumberFormat('vi-VN').format(v || 0);
                const tbody = document.querySelector('#detailsTable tbody');
                const btnAdd = document.getElementById('btnAddRow');
                const sumQtyEl = document.getElementById('sumQty');
                const sumTotalEl = document.getElementById('sumTotal');

                function recalc() {
                    let total = 0, qty = 0;
                    tbody.querySelectorAll('tr').forEach(tr => {
                        const price = parseFloat(tr.querySelector('.price-input').value || 0);
                        const q = parseInt(tr.querySelector('.qty-input').value || 0);
                        const sub = price * q;
                        tr.querySelector('.subtotal-input').value = Math.round(sub);
                        total += sub;
                        qty += q;
                    });
                    sumQtyEl.textContent = qty;
                    sumTotalEl.textContent = '₫' + fmtVND(Math.round(total));
                }

                tbody.addEventListener('input', e => {
                    if (e.target.matches('.price-input,.qty-input'))
                        recalc();
                });
                tbody.addEventListener('change', e => {
                    if (e.target.matches('.product-select')) {
                        const opt = e.target.selectedOptions[0];
                        const price = opt ? (opt.getAttribute('data-price') || 0) : 0;
                        e.target.closest('tr').querySelector('.price-input').value = Math.round(price);
                        recalc();
                    }
                });
                tbody.addEventListener('click', e => {
                    if (e.target.matches('.btnRemoveRow')) {
                        if (tbody.querySelectorAll('tr').length > 1)
                            e.target.closest('tr').remove();
                        recalc();
                    }
                });
                btnAdd.addEventListener('click', () => {
                    const first = tbody.querySelector('tr');
                    const clone = first.cloneNode(true);
                    clone.querySelector('.product-select').selectedIndex = 0;
                    clone.querySelector('.price-input').value = '';
                    clone.querySelector('.qty-input').value = 1;
                    clone.querySelector('.subtotal-input').value = 0;
                    tbody.appendChild(clone);
                });

                recalc();
            })();
        </script>

    </body>
</html>
