<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"  %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>
    <c:choose>
      <c:when test="${not empty product}">Sửa sản phẩm</c:when>
      <c:otherwise>Thêm sản phẩm</c:otherwise>
    </c:choose>
  </title>
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <style>
    :root{--bg:#f7f8fa;--card:#fff;--border:#e5e7eb;--muted:#64748b}
    body{background:var(--bg)}
    .admin-card{border-radius:14px;border:1px solid var(--border);box-shadow:0 12px 32px rgba(15,23,42,.08)}
    .img-prev{width:160px;height:160px;object-fit:contain;border:1px dashed var(--border);background:#fff}
    .form-text.muted{color:var(--muted)}
    .badge-sale{font-weight:600}
  </style>
</head>
<body class="admin-body">
<div class="container py-4">
  <div class="d-flex align-items-center justify-content-between mb-3">
    <h3 class="mb-0">
      <c:choose>
        <c:when test="${not empty product}">Sửa sản phẩm</c:when>
        <c:otherwise>Thêm sản phẩm</c:otherwise>
      </c:choose>
    </h3>
    <a class="btn btn-outline-secondary btn-sm" href="${ctx}/admin/products">← Danh sách</a>
  </div>

  <div class="card admin-card">
    <div class="card-body">
      <!-- enctype để upload file -->
      <form action="${ctx}/admin/products" method="post" class="row g-3" enctype="multipart/form-data">
        <input type="hidden" name="action" value="save"/>
        <c:if test="${not empty product}">
          <input type="hidden" name="id" value="${product.productId}"/>
        </c:if>

        <!-- Cột trái -->
        <div class="col-12 col-lg-8">
          <div class="row g-3">
            <div class="col-md-8">
              <label class="form-label">Tên sản phẩm <span class="text-danger">*</span></label>
              <input name="name" class="form-control" required value="${product.name}">
            </div>

            <div class="col-md-4">
              <label class="form-label">Brand</label>
              <!-- Gợi ý từ dao.getAllBrands() nếu controller setAttribute("brands") -->
              <input name="brand" class="form-control" list="brandList" value="${product.brand}">
              <datalist id="brandList">
                <c:forEach var="b" items="${brands}"><option value="${b}"/></c:forEach>
              </datalist>
              <div class="form-text muted">Có sẵn DAO: getAllBrands().</div>
            </div>

            <div class="col-md-4">
              <label class="form-label">Giá (₫) <span class="text-danger">*</span></label>
              <!-- decimal(10,2) -->
              <input name="price" type="number" step="0.01" min="0" class="form-control" required value="${product.price}">
            </div>

            <div class="col-md-4">
              <label class="form-label">Giá cũ (old_price)</label>
              <!-- decimal(12,2) nullable -->
              <input name="oldPrice" type="number" step="0.01" min="0" class="form-control" value="${product.oldPrice}">
              <div class="form-text muted">Để trống nếu không hiển thị giảm giá.</div>
            </div>

            <div class="col-md-4">
              <label class="form-label">Rating</label>
              <!-- decimal(2,1) 0..5 -->
              <input name="rating" type="number" step="0.1" min="0" max="5" class="form-control" value="${product.rating}">
            </div>

            <div class="col-md-4">
              <label class="form-label">Tồn kho</label>
              <input name="stock" type="number" min="0" class="form-control" required value="${product.stock}">
            </div>

            <div class="col-md-8">
              <label class="form-label">Danh mục</label>
              <!-- Nếu chưa có bảng categories, có thể feed từ distinct products -->
              <input name="category" class="form-control" list="catList" value="${product.category}">
              <datalist id="catList">
                <c:forEach var="cname" items="${categories}"><option value="${cname}"/></c:forEach>
              </datalist>
            </div>

            <div class="col-12">
              <label class="form-label">Mô tả</label>
              <textarea name="description" class="form-control" rows="4">${product.description}</textarea>
            </div>

            <!-- Thông tin giảm giá (nếu có) -->
            <c:if test="${not empty product and product.discountPercent > 0}">
              <div class="col-12">
                <span class="badge bg-danger-subtle text-danger border badge-sale">
                  Giảm ~ ${product.discountPercent}% so với giá cũ
                </span>
              </div>
            </c:if>
          </div>
        </div>

        <!-- Cột phải: Ảnh -->
        <div class="col-12 col-lg-4">
          <label class="form-label">Ảnh sản phẩm</label>
          <div class="d-flex gap-3 align-items-start">
            <img id="preview" class="img-prev rounded"
                 src="<c:out value='${empty product.image ? "" : (ctx.concat("/images/").concat(product.image))}'/>"
                 alt="preview"
                 onerror="this.src='${ctx}/images/placeholder.png'">
            <div class="flex-grow-1">
              <div class="mb-2">
                <input class="form-control" type="file" name="imageFile" accept="image/*" id="imageFile">
              </div>
              <div class="form-text">
                • Chọn file từ máy để tải lên<br>
                • Hoặc nhập tên file sẵn trong thư mục <code>/images</code>:
              </div>
              <input name="image" class="form-control mt-2" placeholder="VD: iphone_15_pro.jpg" value="${product.image}">
            </div>
          </div>
          <div class="form-text mt-2">
            Nếu upload file mới, servlet lưu vào <code>/images/</code> (tuỳ bạn cấu hình).
          </div>
        </div>

        <div class="col-12 d-flex gap-2">
          <button class="btn btn-primary">Lưu</button>
          <a class="btn btn-secondary" href="${ctx}/admin/products">Huỷ</a>

          <!-- Nút xoá chỉ hiện khi sửa -->
          <c:if test="${not empty product}">
            <button type="button" class="btn btn-outline-danger ms-auto" data-bs-toggle="modal" data-bs-target="#confirmDelete">Xoá</button>
          </c:if>
        </div>
      </form>
    </div>
  </div>
</div>

<!-- Modal xác nhận xoá -->
<c:if test="${not empty product}">
<div class="modal fade" id="confirmDelete" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content" action="${ctx}/admin/products" method="post">
      <input type="hidden" name="action" value="delete">
      <input type="hidden" name="id" value="${product.productId}">
      <div class="modal-header">
        <h5 class="modal-title">Xoá sản phẩm</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-body">
        Bạn có chắc chắn muốn xoá <strong><c:out value="${product.name}"/></strong>? Thao tác này không thể hoàn tác.
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
  // Preview ảnh & auto điền tên file vào input "image"
  const fileInput = document.getElementById('imageFile');
  const preview   = document.getElementById('preview');
  fileInput?.addEventListener('change', (e) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (evt) => { preview.src = evt.target.result; };
    reader.readAsDataURL(file);
    const nameField = document.querySelector('input[name="image"]');
    if (nameField && file.name) nameField.value = file.name;
  });
</script>
</body>
</html>
