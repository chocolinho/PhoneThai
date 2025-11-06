<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh toán - PhoneThai</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<jsp:include page="/Header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <h1 class="h3 fw-bold mb-4 text-center">Xác nhận đặt hàng</h1>

            <c:if test="${param.success == '1'}">
                <div class="alert alert-success">Đặt hàng thành công! Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.</div>
            </c:if>
            <c:if test="${param.error == '1'}">
                <div class="alert alert-danger">Đã xảy ra lỗi khi tạo đơn hàng. Vui lòng thử lại.</div>
            </c:if>

            <c:if test="${empty cartItems}">
                <div class="alert alert-info">Giỏ hàng của bạn đang trống. <a href="${ctx}/">Quay lại mua sắm</a>.</div>
            </c:if>

            <c:if test="${not empty cartItems}">
                <div class="card shadow-sm mb-4">
                    <div class="card-body">
                        <h2 class="h5 mb-3">Thông tin đơn hàng</h2>
                        <ul class="list-group list-group-flush">
                            <c:forEach var="item" items="${cartItems}">
                                <li class="list-group-item d-flex justify-content-between align-items-center">
                                    <div>
                                        <div class="fw-semibold">${item.productName}</div>
                                        <small class="text-muted">Số lượng: ${item.quantity}</small>
                                    </div>
                                    <div class="text-end">
                                        <div class="fw-semibold text-danger">
                                            <fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/> ₫
                                        </div>
                                        <small class="text-muted">Đơn giá: <fmt:formatNumber value="${item.price}" type="number" groupingUsed="true"/> ₫</small>
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <span class="fw-semibold">Tổng số lượng:</span>
                            <span>${totalQuantity}</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mt-2">
                            <span class="fw-semibold">Tổng thanh toán:</span>
                            <span class="h5 text-danger mb-0">
                                <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/> ₫
                            </span>
                        </div>
                    </div>
                </div>

                <form method="post" action="${ctx}/checkout" class="text-end">
                    <button type="submit" class="btn btn-success px-5">Đặt hàng ngay</button>
                    <a href="${ctx}/cart" class="btn btn-outline-secondary ms-2">Quay lại giỏ hàng</a>
                </form>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/Footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
