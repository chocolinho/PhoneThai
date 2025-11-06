<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${empty pageTitle ? "Danh sách sản phẩm" : pageTitle}</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/css/style.css">
    <link href="https://cdn.jsdelivr.net/npm/remixicon@4.3.0/fonts/remixicon.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/Header.jsp" />

<main class="container py-4">
    <div class="d-flex flex-column flex-lg-row justify-content-between align-items-lg-center gap-3 mb-4">
        <div>
            <h1 class="h3 mb-1">${empty pageTitle ? "Danh sách sản phẩm" : pageTitle}</h1>
            <p class="text-muted mb-0">Tìm kiếm và lọc sản phẩm theo nhu cầu của bạn.</p>
        </div>
        <form class="row gx-2 gy-2 align-items-center" method="get" action="${ctx}/products">
            <div class="col-auto">
                <select class="form-select" name="cat">
                    <option value="">Tất cả danh mục</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat}" ${cat == selectedCategory ? 'selected' : ''}>${cat}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-auto">
                <select class="form-select" name="brand">
                    <option value="">Tất cả thương hiệu</option>
                    <c:forEach var="b" items="${brands}">
                        <option value="${b}" ${b == selectedBrand ? 'selected' : ''}>${b}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-auto">
                <input type="search" class="form-control" name="q" placeholder="Tìm kiếm..." value="${fn:escapeXml(query)}">
            </div>
            <div class="col-auto">
                <button class="btn btn-primary" type="submit">Lọc</button>
            </div>
        </form>
    </div>

    <c:if test="${empty products}">
        <div class="alert alert-info">Không tìm thấy sản phẩm nào phù hợp với tiêu chí của bạn.</div>
    </c:if>

    <c:if test="${not empty products}">
        <div class="row g-3 g-lg-4">
            <c:forEach var="p" items="${products}">
                <div class="col-6 col-md-4 col-lg-3">
                    <article class="cps-card h-100">
                        <c:if test="${p.discountPercent > 0}">
                            <div class="cps-badge-discount">Giảm ${p.discountPercent}%</div>
                        </c:if>
                        <a href="${ctx}/detail?pid=${p.productId}" class="cps-card-thumb">
                            <img src="${ctx}/images/${p.image}" alt="${p.name}" loading="lazy">
                        </a>
                        <div class="cps-card-body">
                            <h3 class="cps-card-title">
                                <a href="${ctx}/detail?pid=${p.productId}">${p.name}</a>
                            </h3>
                            <div class="cps-price">
                                <span class="price">
                                    <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>đ
                                </span>
                                <c:if test="${p.oldPrice > p.price}">
                                    <span class="price-old">
                                        <fmt:formatNumber value="${p.oldPrice}" type="number" groupingUsed="true"/>đ
                                    </span>
                                </c:if>
                            </div>
                            <div class="d-flex align-items-center justify-content-between small">
                                <div class="text-warning">
                                    <i class="ri-star-fill"></i> ${empty p.rating ? 5 : p.rating}
                                </div>
                                <button class="btn btn-sm btn-warning w-auto cps-addcart" data-id="${p.productId}">
                                    <i class="ri-shopping-cart-2-fill"></i>
                                </button>
                            </div>
                        </div>
                    </article>
                </div>
            </c:forEach>
        </div>
    </c:if>
</main>

<jsp:include page="/Footer.jsp" />

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('.cps-addcart').forEach(btn => {
    btn.addEventListener('click', async e => {
      e.preventDefault();
      const id = btn.dataset.id;
      const payload = new URLSearchParams({id, quantity: 1});

      try {
        const res = await fetch('${ctx}/cart', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: payload.toString()
        });
        const data = await res.json();
        if (data.count !== undefined) {
          let badge = document.querySelector('.bi-bag + .badge');
          if (!badge) {
            const cartBtn = document.querySelector('a[href$="/cart"]');
            badge = document.createElement('span');
            badge.className = 'badge position-absolute top-0 start-100 translate-middle';
            badge.style.background = 'linear-gradient(135deg,#6C5CE7,#A78BFA)';
            badge.style.color = 'white';
            cartBtn.appendChild(badge);
          }
          badge.textContent = data.count;
          badge.classList.add('animate__animated', 'animate__tada');
          setTimeout(() => badge.classList.remove('animate__animated', 'animate__tada'), 800);
        } else if (data.error) {
          alert(data.error);
          if (data.error.includes('đăng nhập')) {
            window.location.href = '${ctx}/auth/Login.jsp';
          }
        }
      } catch (err) {
        console.error(err);
        alert('Đã xảy ra lỗi khi thêm giỏ hàng!');
      }
    });
  });
});
</script>
</body>
</html>
