<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Sổ địa chỉ - PhoneThai</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>
<%@ include file="/Header.jsp" %>

<div class="container py-5">
  <div class="row g-4">
    <div class="col-lg-5 order-lg-2">
      <div class="card shadow-sm">
        <div class="card-body">
          <h2 class="h5 fw-bold mb-3">${empty editing.addressId ? 'Thêm địa chỉ mới' : 'Cập nhật địa chỉ'}</h2>
          <c:if test="${not empty errors}">
            <div class="alert alert-danger">
              <ul class="mb-0">
                <c:forEach var="err" items="${errors}">
                  <li>${err}</li>
                </c:forEach>
              </ul>
            </div>
          </c:if>
          <form action="${ctx}/addresses" method="post" class="row g-3">
            <input type="hidden" name="addressId" value="${editing.addressId}" />
            <div class="col-12">
              <label class="form-label" for="fullName">Họ và tên</label>
              <input class="form-control" type="text" id="fullName" name="fullName" value="${editing.fullName}" required />
            </div>
            <div class="col-12">
              <label class="form-label" for="phone">Số điện thoại</label>
              <input class="form-control" type="text" id="phone" name="phone" value="${editing.phone}" required />
            </div>
            <div class="col-12">
              <label class="form-label" for="addressLine">Địa chỉ</label>
              <input class="form-control" type="text" id="addressLine" name="addressLine" value="${editing.addressLine}" placeholder="Số nhà, tên đường" required />
            </div>
            <div class="col-md-4">
              <label class="form-label" for="ward">Phường/Xã</label>
              <input class="form-control" type="text" id="ward" name="ward" value="${editing.ward}" />
            </div>
            <div class="col-md-4">
              <label class="form-label" for="district">Quận/Huyện</label>
              <input class="form-control" type="text" id="district" name="district" value="${editing.district}" required />
            </div>
            <div class="col-md-4">
              <label class="form-label" for="province">Tỉnh/Thành phố</label>
              <input class="form-control" type="text" id="province" name="province" value="${editing.province}" required />
            </div>
            <div class="col-12">
              <div class="form-check">
                <input class="form-check-input" type="checkbox" id="isDefault" name="isDefault" value="1" ${editing.default ? 'checked' : ''} />
                <label class="form-check-label" for="isDefault">Đặt làm địa chỉ mặc định</label>
              </div>
            </div>
            <div class="col-12 d-flex gap-2">
              <button class="btn btn-primary" type="submit" name="action" value="save">${empty editing.addressId ? 'Thêm mới' : 'Cập nhật'}</button>
              <a class="btn btn-outline-secondary" href="${ctx}/addresses">Hủy</a>
            </div>
          </form>
        </div>
      </div>
    </div>

    <div class="col-lg-7">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="h4 fw-bold mb-0">Sổ địa chỉ</h1>
        <c:if test="${not empty param.saved}"><span class="badge text-bg-success">Đã lưu địa chỉ</span></c:if>
        <c:if test="${not empty param.deleted}"><span class="badge text-bg-warning">Đã xóa địa chỉ</span></c:if>
        <c:if test="${not empty param.defaulted}"><span class="badge text-bg-info">Đã đặt địa chỉ mặc định</span></c:if>
      </div>

      <c:if test="${empty addresses}">
        <div class="alert alert-info">Bạn chưa có địa chỉ nào. Hãy thêm địa chỉ giao hàng để thanh toán nhanh hơn.</div>
      </c:if>

      <div class="row row-cols-1 g-3">
        <c:forEach var="address" items="${addresses}">
          <div class="col">
            <div class="card h-100 shadow-sm ${address.default ? 'border-primary' : ''}">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start">
                  <div>
                    <div class="fw-semibold">${address.fullName}</div>
                    <div class="text-muted">${address.phone}</div>
                  </div>
                  <c:if test="${address.default}">
                    <span class="badge text-bg-primary">Mặc định</span>
                  </c:if>
                </div>
                <div class="mt-2">
                  ${address.addressLine},
                  <c:if test="${not empty address.ward}">${address.ward}, </c:if>
                  ${address.district}, ${address.province}
                </div>
              </div>
              <div class="card-footer bg-transparent d-flex justify-content-between">
                <div class="btn-group btn-group-sm" role="group">
                  <a class="btn btn-outline-secondary" href="${ctx}/addresses?id=${address.addressId}">Sửa</a>
                  <form action="${ctx}/addresses" method="post" class="d-inline">
                    <input type="hidden" name="action" value="delete" />
                    <input type="hidden" name="id" value="${address.addressId}" />
                    <button class="btn btn-outline-danger" type="submit" onclick="return confirm('Xóa địa chỉ này?');">Xóa</button>
                  </form>
                </div>
                <c:if test="${not address.default}">
                  <form action="${ctx}/addresses" method="post" class="d-inline">
                    <input type="hidden" name="action" value="default" />
                    <input type="hidden" name="id" value="${address.addressId}" />
                    <button class="btn btn-link" type="submit">Đặt mặc định</button>
                  </form>
                </c:if>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>
  </div>
</div>

<%@ include file="/Footer.jsp" %>
</body>
</html>
