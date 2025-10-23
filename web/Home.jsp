<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>PhoneThai — Trang chủ</title>

        <!-- Bootstrap -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

        <!-- Font Awesome 6 (thay cho bootstrap-icons & FA4.7) -->
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css"
              integrity="sha512-Kc8EJ3sZ1gZyFhGJ6YVzX3XDr5gqT8UuYw4+1pIu4Y2RFAzvH6F5F8hSBZpX3uhm8Z+z5whU1kI4XgB4p2Bz7Q=="
              crossorigin="anonymous" referrerpolicy="no-referrer" />

        <!-- Google Fonts (khớp style.css) -->
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700;800&family=Lora:wght@700&family=Poppins:wght@700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/remixicon@4.3.0/fonts/remixicon.css" rel="stylesheet">

        <!-- CSS của bạn -->
        <link rel="stylesheet" href="${ctx}/css/style.css" />
    </head>
    <body>


        <%@ include file="Header.jsp" %>

        <main class="cps-home">

            <!-- ========== HOT SALE ========== -->
            <section class="container my-3">
                <div class="cps-promo">
                    <!-- Ribbon -->
                    <div class="promo-ribbon">
                        <span class="icon-left">🎁</span>
                        <strong>HOT SALE TODAY</strong>
                        <span class="icon-right">🎁</span>
                    </div>

                    <!-- Tabs + Countdown (đơn giản) -->
                    <div class="promo-tabs">
                        <button class="tab active" type="button">Hot</button>
                        <button class="tab active" type="button">Sale</button>

                        <div class="promo-countdown ms-auto">
                            <span class="label d-none d-sm-inline">THỜI GIAN:</span>
                            <span id="promoCountdown">23 : 59 : 59</span>
                        </div>
                    </div>

                    <!-- Carousel ngang -->
                    <div class="promo-carousel">
                        <button class="nav prev" type="button" aria-label="Prev">
                            <i class="fa-solid fa-chevron-left"><</i>
                        </button>

                        <div class="promo-track" id="promoTrack">
                            <c:forEach items="${products}" var="p" varStatus="st">
                                <c:if test="${st.index < 10}">
                                    <article class="promo-item"
                                             data-discount="${empty p.discountPercent ? 0 : p.discountPercent}">

                                        <c:if test="${p.discountPercent > 0}">
                                            <span class="promo-badge">Giảm ${p.discountPercent}%</span>
                                        </c:if>

                                        <a class="thumb" href="${ctx}/detail?pid=${p.productId}">
                                            <img src="${ctx}/images/${p.image}" alt="${p.name}" loading="lazy">
                                        </a>

                                        <h3 class="title">
                                            <a href="${ctx}/detail?pid=${p.productId}">${p.name}</a>
                                        </h3>

                                        <div class="prices">
                                            <span class="price">
                                                <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/>đ
                                            </span>
                                            <c:if test="${p.oldPrice > p.price}">
                                                <span class="old">
                                                    <fmt:formatNumber value="${p.oldPrice}" type="number" groupingUsed="true"/>đ
                                                </span>
                                            </c:if>
                                        </div>

                                        <div class="meta">
                                            <span class="rating">
                                                <i class="fa-solid fa-star"></i> ${empty p.rating ? 5 : p.rating}
                                            </span>
                                            <a class="wish" href="#"><i class="ri-heart-line"></i>Yêu thích</a>
                                        </div>
                                    </article>
                                </c:if>
                            </c:forEach>
                        </div>

                        <button class="nav next" type="button" aria-label="Next">
                            <i class="fa-solid fa-chevron-right">></i>
                        </button>
                    </div>
                </div>
            </section>
            <!-- ========== /HOT SALE ========== -->

            <!-- Tiêu đề chuyên mục -->
            <div class="container">
                <div class="text-center my-3 my-md-4">
                    <h2 class="cps-block-title">Featured Products</h2>
                </div>
            </div>

            <c:set var="initialLimit" value="12"/>
            <!-- Lưới sản phẩm: 4 cột/dòng từ md trở lên -->
            <section class="container mt-2">
                <div class="row g-3 g-lg-4" id="productGrid">
                    <c:forEach items="${products}" var="p" varStatus="st">
                        <div class="col-6 col-md-3 js-prod-item ${st.index >= initialLimit ? 'd-none' : ''}">
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

                <!-- Nút xem thêm -->
                <c:set var="remain" value="${fn:length(products) - initialLimit}" />
                <c:if test="${remain > 0}">
                    <div class="see-more text-center mt-2">
                        <button id="btnLoadMore" class="btn btn-outline-primary btn-see-more">
                            Xem thêm <span id="remainCount">${remain}</span> sản phẩm
                            <i class="ri-arrow-down-s-line ms-1"></i>
                        </button>
                    </div>
                </c:if>
            </section>


        </main>

        <%@ include file="/Footer.jsp" %>

        <!-- JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>

            document.addEventListener('DOMContentLoaded', function () {
                var trigger = document.getElementById('dropdownUser');
                if (trigger)
                    new bootstrap.Dropdown(trigger);
            });

            document.addEventListener('DOMContentLoaded', function () {
                // Promo carousel
                (function () {
                    var track = document.getElementById('promoTrack');
                    if (!track)
                        return;
                    var prev = track.parentElement.querySelector('.prev');
                    var next = track.parentElement.querySelector('.next');
                    function step() {
                        return Math.floor(track.clientWidth * 0.9);
                    }
                    if (prev)
                        prev.addEventListener('click', function () {
                            track.scrollBy({left: -step(), behavior: 'smooth'});
                        });
                    if (next)
                        next.addEventListener('click', function () {
                            track.scrollBy({left: step(), behavior: 'smooth'});
                        });
                })();

                // Demo countdown 1 giờ
                (function () {
                    var el = document.getElementById('promoCountdown');
                    if (!el)
                        return;
                    var end = Date.now() + 60 * 60 * 1000;
                    function pad(n) {
                        n = String(n);
                        return n.length < 2 ? ('0' + n) : n;
                    }
                    function tick() {
                        var left = Math.max(0, end - Date.now());
                        var h = Math.floor(left / 3600000);
                        var m = Math.floor((left % 3600000) / 60000);
                        var s = Math.floor((left % 60000) / 1000);
                        el.textContent = pad(h) + ' : ' + pad(m) + ' : ' + pad(s);
                        if (left > 0)
                            setTimeout(tick, 250);
                    }
                    tick();
                })();
            });
        </script>

        <script>
            // Load-more cho lưới sản phẩm
            (function () {
                const BATCH = 12; // mỗi lần mở thêm 12 sp
                const items = Array.from(document.querySelectorAll('.js-prod-item'));
                const btn = document.getElementById('btnLoadMore');
                const rc = document.getElementById('remainCount');
                if (!btn || items.length === 0)
                    return;

                let shown = items.filter(el => !el.classList.contains('d-none')).length;

                function updateRemain() {
                    const remain = Math.max(0, items.length - shown);
                    if (rc)
                        rc.textContent = remain;
                    if (remain <= 0) {
                        btn.closest('.see-more')?.remove(); // ẩn vùng nút khi hết
                    }
                }

                btn.addEventListener('click', function () {
                    const next = items.slice(shown, shown + BATCH);
                    next.forEach(el => el.classList.remove('d-none'));
                    shown += next.length;
                    updateRemain();
                });

                // cập nhật lần đầu (phòng trường hợp dữ liệu ít hơn initialLimit)
                updateRemain();
            })();
        </script>

        <script>
            /* ==== HOT SALE: Chỉ lấy sản phẩm giảm giá nhiều nhất ==== */
            (function () {
                const track = document.getElementById('promoTrack');
                if (!track)
                    return;

                const TOP_N = 10; // số item giữ lại
                // Lấy tất cả item, lọc discount > 0, sort desc theo % giảm
                const items = Array.from(track.children);
                const hot = items
                        .map(el => ({el, d: parseFloat(el.dataset.discount || '0')}))
                        .filter(x => x.d > 0)
                        .sort((a, b) => b.d - a.d)
                        .slice(0, TOP_N)
                        .map(x => x.el);

                // Nếu không có item đủ điều kiện thì giữ nguyên
                if (hot.length > 0) {
                    track.innerHTML = '';
                    hot.forEach(el => track.appendChild(el));
                }
            })();
        </script>

<script>
document.addEventListener('DOMContentLoaded', function () {
  // Xử lý thêm giỏ hàng
  document.querySelectorAll('.cps-addcart').forEach(btn => {
    btn.addEventListener('click', async e => {
      e.preventDefault();
      const id = btn.dataset.id;

      try {
        const res = await fetch('${ctx}/cart', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: 'id=' + encodeURIComponent(id)
        });

        const data = await res.json();

        if (data.count !== undefined) {
          // ✅ Cập nhật số lượng badge
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

          // ✅ Hiệu ứng vui mắt khi thêm
          badge.classList.add('animate__animated', 'animate__tada');
          setTimeout(() => badge.classList.remove('animate__animated', 'animate__tada'), 800);
        }
        else if (data.error) {
          // ❌ Nếu chưa đăng nhập
          alert(data.error);
          if (data.error.includes('đăng nhập')) {
            window.location.href = '${ctx}/auth/Login.jsp';
          }
        }
      } catch (err) {
        console.error(err);
        alert("Đã xảy ra lỗi khi thêm giỏ hàng!");
      }
    });
  });
});
</script>


        


    </body>
</html>
