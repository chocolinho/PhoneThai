<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Đơn hàng của tôi - PhoneThai</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<%@ include file="/Header.jsp" %>

<div class="container py-5">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1 class="h3 fw-bold mb-0">Đơn hàng của tôi</h1>
    <a class="btn btn-outline-secondary" href="${ctx}/products">Tiếp tục mua sắm</a>
  </div>

  <c:if test="${empty orders}">
    <div class="alert alert-info">Bạn chưa có đơn hàng nào. Hãy đặt mua sản phẩm để trải nghiệm PhoneThai nhé!</div>
  </c:if>

  <c:if test="${not empty orders}">
    <div class="table-responsive mb-4">
      <table class="table align-middle">
        <thead class="table-light">
          <tr>
            <th>Mã đơn</th>
            <th>Ngày đặt</th>
            <th class="text-center">Số lượng</th>
            <th class="text-end">Tổng tiền</th>
            <th>Trạng thái</th>
            <th class="text-end">Chi tiết</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="order" items="${orders}">
          <tr>
            <td>#${order.orderId}</td>
            <td>
              <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" />
            </td>
            <td class="text-center">${order.quantity}</td>
            <td class="text-end text-danger fw-semibold">
              <fmt:formatNumber value="${order.total}" type="number" groupingUsed="true" /> ₫
            </td>
            <td>
              <span class="badge text-bg-${order.status eq 'completed' ? 'success' : (order.status eq 'cancelled' ? 'danger' : 'warning')}">
                <c:out value="${order.status}" />
              </span>
            </td>
            <td class="text-end">
              <a class="btn btn-sm btn-outline-primary" href="${ctx}/orders?orderId=${order.orderId}">
                Xem
              </a>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </c:if>

  <c:if test="${not empty selectedOrder}">
    <div class="card shadow-sm">
      <div class="card-header d-flex justify-content-between align-items-center">
        <div>
          <h2 class="h5 mb-1">Chi tiết đơn #${selectedOrder.orderId}</h2>
          <div class="text-muted small">
            Đặt ngày <fmt:formatDate value="${selectedOrder.orderDate}" pattern="dd/MM/yyyy HH:mm" />
          </div>
        </div>
        <a class="btn btn-outline-secondary btn-sm" href="${ctx}/orders">Đóng</a>
      </div>
      <div class="card-body">
        <c:if test="${empty orderDetails}">
          <p class="text-muted mb-0">Không tìm thấy chi tiết cho đơn hàng này.</p>
        </c:if>
        <c:if test="${not empty orderDetails}">
          <div class="table-responsive">
            <table class="table align-middle">
              <thead class="table-light">
                <tr>
                  <th>Sản phẩm</th>
                  <th class="text-center">SL</th>
                  <th class="text-end">Giá</th>
                  <th class="text-end">Thành tiền</th>
                </tr>
              </thead>
              <tbody>
              <c:forEach var="detail" items="${orderDetails}">
                <tr>
                  <td>${detail.productName}</td>
                  <td class="text-center">${detail.quantity}</td>
                  <td class="text-end">
                    <fmt:formatNumber value="${detail.price}" type="number" groupingUsed="true" /> ₫
                  </td>
                  <td class="text-end fw-semibold text-danger">
                    <fmt:formatNumber value="${detail.subtotal}" type="number" groupingUsed="true" /> ₫
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </c:if>
      </div>
    </div>
  </c:if>
</div>

<%@ include file="/Footer.jsp" %>
</body>
</html>
