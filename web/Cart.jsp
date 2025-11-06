<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Giỏ hàng của bạn - PhoneThai</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<%@ include file="/Header.jsp" %>

<div class="container py-5">
  <h1 class="h3 mb-4 text-center fw-bold">Giỏ hàng của bạn</h1>

  <c:if test="${mustLogin}">
    <div class="alert alert-warning text-center" role="alert">
      Vui lòng <a href="${ctx}/auth/Login.jsp" class="alert-link">đăng nhập</a> để xem giỏ hàng của bạn.
    </div>
  </c:if>

  <c:if test="${not mustLogin && empty cartItems}">
    <div class="text-center text-muted py-5">
      <p>Giỏ hàng của bạn đang trống.</p>
      <a href="${ctx}/" class="btn btn-primary">Tiếp tục mua sắm</a>
    </div>
  </c:if>

  <c:if test="${not empty cartItems}">
    <div class="table-responsive">
      <table class="table align-middle">
        <thead class="table-light">
          <tr>
            <th>Sản phẩm</th>
            <th class="text-center">Số lượng</th>
            <th class="text-end">Giá</th>
            <th class="text-end">Tổng</th>
          </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${cartItems}">
          <tr>
            <td>
              <div class="d-flex align-items-center gap-3">
                <img src="${ctx}/images/${item.image}" width="60" height="60" style="object-fit:cover;border-radius:8px;">
                <div>
                  <div class="fw-semibold">${item.productName}</div>
                </div>
              </div>
            </td>
            <td class="text-center">${item.quantity}</td>
            <td class="text-end"><fmt:formatNumber value="${item.price}" type="number" groupingUsed="true"/> ₫</td>
            <td class="text-end text-danger fw-bold">
              <fmt:formatNumber value="${item.subtotal}" type="number" groupingUsed="true"/> ₫
            </td>
          </tr>
        </c:forEach>
        </tbody>
        <tfoot>
          <tr>
            <th colspan="3" class="text-end">Tổng cộng:</th>
            <th class="text-end text-danger h5">
              <fmt:formatNumber value="${total}" type="number" groupingUsed="true"/> ₫
            </th>
          </tr>
        </tfoot>
      </table>
    </div>

    <div class="text-end">
      <a href="${ctx}/checkout" class="btn btn-success px-4">Thanh toán</a>
      <a href="${ctx}/" class="btn btn-outline-secondary">Tiếp tục mua sắm</a>
    </div>
  </c:if>
</div>

<%@ include file="/Footer.jsp" %>
</body>
</html>
