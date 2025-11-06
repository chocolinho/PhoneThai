<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Thông tin thanh toán - PhoneThai</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<%@ include file="/Header.jsp" %>

<div class="container py-5" style="max-width:720px;">
  <h1 class="h3 fw-bold mb-3">Thông tin thanh toán</h1>
  <p class="text-muted">Lưu thông tin thẻ để thanh toán nhanh hơn cho những lần mua sắm sau.</p>

  <c:if test="${not empty param.saved}">
    <div class="alert alert-success">Đã lưu thông tin thanh toán.</div>
  </c:if>
  <c:if test="${not empty param.deleted}">
    <div class="alert alert-warning">Đã xóa thông tin thanh toán đã lưu.</div>
  </c:if>
  <c:if test="${not empty errors}">
    <div class="alert alert-danger">
      <ul class="mb-0">
        <c:forEach var="err" items="${errors}">
          <li>${err}</li>
        </c:forEach>
      </ul>
    </div>
  </c:if>

  <div class="card shadow-sm">
    <div class="card-body">
      <form action="${ctx}/billing" method="post" class="row g-3">
        <div class="col-12">
          <label class="form-label" for="cardName">Tên in trên thẻ</label>
          <input class="form-control" type="text" id="cardName" name="cardName" value="${billingInfo.cardName}" required>
        </div>
        <div class="col-12">
          <label class="form-label" for="cardNumber">Số thẻ</label>
          <input class="form-control" type="text" id="cardNumber" name="cardNumber" value="${billingInfo.cardNumber}" placeholder="VD: 4111 1111 1111 1111" required>
        </div>
        <div class="col-md-6">
          <label class="form-label" for="bankName">Ngân hàng</label>
          <input class="form-control" type="text" id="bankName" name="bankName" value="${billingInfo.bankName}" required>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="expiryMonth">Tháng hết hạn</label>
          <input class="form-control" type="number" id="expiryMonth" name="expiryMonth" min="1" max="12" value="${billingInfo.expiryMonth}" required>
        </div>
        <div class="col-md-3">
          <label class="form-label" for="expiryYear">Năm hết hạn</label>
          <input class="form-control" type="number" id="expiryYear" name="expiryYear" min="${currentYear}" value="${billingInfo.expiryYear}" required>
        </div>
        <div class="col-12 d-flex justify-content-between align-items-center">
          <button class="btn btn-primary" type="submit">Lưu thông tin</button>
          <c:if test="${not empty billingInfo && not empty billingInfo.cardNumber}">
            <button class="btn btn-outline-danger" type="submit" name="action" value="delete" onclick="return confirm('Bạn có chắc muốn xóa thông tin thẻ đã lưu?');">Xóa</button>
          </c:if>
        </div>
      </form>
    </div>
  </div>

  <c:if test="${not empty billingInfo && not empty billingInfo.cardNumber}">
    <div class="mt-4">
      <h2 class="h6 text-uppercase text-muted">Thông tin đã lưu</h2>
      <div class="border rounded-3 p-3 bg-light">
        <div class="fw-semibold">${billingInfo.cardName}</div>
        <div>
          <c:set var="masked" value="**** **** **** ${fn:substring(billingInfo.cardNumber, fn:length(billingInfo.cardNumber) - 4, fn:length(billingInfo.cardNumber))}" />
          <span class="font-monospace">${masked}</span>
        </div>
        <div>Ngân hàng: ${billingInfo.bankName}</div>
        <div>Hết hạn: ${billingInfo.expiryMonth}/${billingInfo.expiryYear}</div>
      </div>
    </div>
  </c:if>
</div>

<%@ include file="/Footer.jsp" %>
</body>
</html>
