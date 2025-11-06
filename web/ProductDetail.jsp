<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title><c:out value="${productDetail.name}" /> - PhoneThai</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700&family=Lora:wght@400;700&family=Poppins:wght@700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"/>
        <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>

        <jsp:include page="Header.jsp" />

        <div class="container mt-4">
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">${errorMessage}</div>
            </c:if>
            <c:if test="${empty errorMessage}">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="${ctx}/">Trang chủ</a></li>
                        <li class="breadcrumb-item"><a href="${ctx}/products?cat=${productDetail.category}">${productDetail.category}</a></li>
                        <li class="breadcrumb-item active" aria-current="page">${productDetail.name}</li>
                    </ol>
                </nav>
            </c:if>
        </div>

        <c:if test="${empty errorMessage}">
        <main class="product-detail-section section">
            <div class="container">
                <div class="row g-5">
                    <div class="col-lg-5 product-detail-image">
                        <img src="${ctx}/images/${productDetail.image}" alt="${productDetail.name}"
                             class="img-fluid rounded-4 shadow-sm">
                    </div>

                    <div class="col-lg-7 product-detail-content">
                        <h1 class="product-title">${productDetail.name}</h1>
                        <p class="product-stock">Tình trạng: <span class="fw-bold text-success">Còn hàng (${productDetail.stock} sản phẩm)</span></p>

                        <div class="price-box my-4">
                            <span class="detail-price">
                                <fmt:formatNumber value="${productDetail.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ
                            </span>
                        </div>

                        <p class="product-description">${productDetail.description}</p>

                        <hr class="my-4">

                        <div class="d-flex align-items-center gap-3 mb-4">
                            <label for="quantity" class="form-label mb-0">Số lượng:</label>
                            <input type="number" id="quantity" class="form-control quantity-input" value="1" min="1" max="${productDetail.stock}">
                        </div>

                        <button class="btn btn-primary-custom btn-lg w-100 js-add-main" data-id="${productDetail.productId}" type="button">
                            <i class="bi bi-bag-plus-fill me-2"></i> Thêm vào giỏ hàng
                        </button>
                    </div>
                </div>
            </div>
        </main>
        <section class="related-products-section section">
            <div class="container">
                <h2 class="section-title">Sản phẩm liên quan</h2>
                <div class="row g-4">
                    <c:if test="${empty relatedProducts}">
                        <p class="text-muted">Chưa có sản phẩm tương tự để đề xuất.</p>
                    </c:if>
                    <c:forEach items="${relatedProducts}" var="p">
                        <div class="col-lg-3 col-md-6">
                            <div class="product-card">
                                <div class="product-card__image-container">
                                    <a href="detail?pid=${p.productId}">
                                        <img class="product-card__image" loading="lazy"
                                             src="${ctx}/images/${p.image}"
                                             alt="${p.name}">
                                    </a>
                                </div>
                                <a class="product-card__title" href="detail?pid=${p.productId}">${p.name}</a>
                                <p class="product-card__price">
                                    <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ
                                </p>
                                <div class="product-card__actions">
                                    <a class="btn-detail" href="detail?pid=${p.productId}">
                                        <i class="bi bi-info-circle"></i> Chi tiết
                                    </a>
                                    <button class="btn-cart js-add-related" type="button" data-id="${p.productId}">
                                        <i class="bi bi-bag-plus"></i> Thêm
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>
        </c:if>
        <jsp:include page="Footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <c:if test="${empty errorMessage}">
        <script>
        document.addEventListener('DOMContentLoaded', function () {
          const mainBtn = document.querySelector('.js-add-main');
          const quantityInput = document.getElementById('quantity');

          async function addToCart(id, qty) {
            const payload = new URLSearchParams({id, quantity: qty});
            const res = await fetch('${ctx}/cart', {
              method: 'POST',
              headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
              body: payload.toString()
            });
            return res.json();
          }

          function handleResponse(data) {
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
          }

          if (mainBtn) {
            mainBtn.addEventListener('click', async () => {
              const id = mainBtn.dataset.id;
              const qty = Math.max(1, parseInt(quantityInput.value || '1', 10));
              try {
                const data = await addToCart(id, qty);
                handleResponse(data);
              } catch (err) {
                console.error(err);
                alert('Đã xảy ra lỗi khi thêm giỏ hàng!');
              }
            });
          }

          document.querySelectorAll('.js-add-related').forEach(btn => {
            btn.addEventListener('click', async () => {
              try {
                const data = await addToCart(btn.dataset.id, 1);
                handleResponse(data);
              } catch (err) {
                console.error(err);
                alert('Đã xảy ra lỗi khi thêm giỏ hàng!');
              }
            });
          });
        });
        </script>
        </c:if>

    </body>
</html>
