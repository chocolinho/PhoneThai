<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<footer class="cps-footer mt-4" role="contentinfo" aria-label="Thông tin & hỗ trợ PhoneThai">
  <div class="container py-4">
    <div class="row g-4 align-items-start">

      <!-- Brand / Social -->
      <section class="col-12 col-md-4" aria-labelledby="ft-brand">
        <h6 id="ft-brand" class="visually-hidden">Giới thiệu thương hiệu</h6>
        <a href="${ctx}/" class="d-inline-flex align-items-center mb-2 text-decoration-none">
         
          <strong class="text-light">PhoneThai</strong>
        </a>
        <p class="mb-3 small text-light">
          Điện thoại chính hãng, giao nhanh trong ngày, đổi trả dễ dàng.
        </p>

        <nav class="d-flex gap-3 fs-5" aria-label="Mạng xã hội">
          <a href="#" class="text-decoration-none" aria-label="Facebook PhoneThai">
            <i class="bi bi-facebook"></i>
          </a>
          <a href="#" class="text-decoration-none" aria-label="YouTube PhoneThai">
            <i class="bi bi-youtube"></i>
          </a>
          <a href="#" class="text-decoration-none" aria-label="Instagram PhoneThai">
            <i class="bi bi-instagram"></i>
          </a>
        </nav>
      </section>

      <!-- Sản phẩm -->
      <nav class="col-6 col-md-2" aria-labelledby="ft-products">
        <h6 id="ft-products" class="mb-2">Sản phẩm</h6>
        <ul class="list-unstyled small mb-0">
          <li><a href="${ctx}/?brand=Apple">iPhone</a></li>
          <li><a href="${ctx}/?brand=Samsung">Samsung</a></li>
          <li><a href="${ctx}/?brand=Xiaomi">Xiaomi</a></li>
          <li><a href="${ctx}/?cat=accessory">Phụ kiện</a></li>
        </ul>
      </nav>

      <!-- Hỗ trợ -->
      <nav class="col-6 col-md-2" aria-labelledby="ft-support">
        <h6 id="ft-support" class="mb-2">Hỗ trợ</h6>
        <ul class="list-unstyled small mb-0">
          <li><a href="${ctx}/warranty">Bảo hành</a></li>
          <li><a href="${ctx}/returns">Đổi trả &amp; Hoàn tiền</a></li>
          <li><a href="${ctx}/shipping">Giao hàng</a></li>
          <li><a href="${ctx}/installments">Trả góp 0%</a></li>
        </ul>
      </nav>

      <!-- Liên hệ / Newsletter -->
      <section class="col-12 col-md-4" aria-labelledby="ft-contact">
        <h6 id="ft-contact" class="mb-2">Liên hệ &amp; nhận tin</h6>

        <ul class="list-unstyled small mb-3">
          <li class="mb-1">
            <i class="bi bi-telephone me-2"></i>
            Hotline: <a href="tel:19001234">1900 1234</a> (8:00–21:00)
          </li>
          <li class="mb-1">
            <i class="bi bi-envelope me-2"></i>
            Email: <a href="mailto:support@phonethai.vn">support@phonethai.vn</a>
          </li>
          <li class="mb-1">
            <i class="bi bi-geo-alt me-2"></i>
            123 Nguyễn Trãi, Nam Định
          </li>
        </ul>

        <form class="d-flex gap-2 mt-2" action="${ctx}/newsletter/subscribe" method="post" novalidate>
          <label for="ft-email" class="visually-hidden">Email nhận khuyến mãi</label>
          <input id="ft-email" name="email" type="email" class="form-control form-control-sm"
                 placeholder="Email của bạn" required />
          <button class="btn btn-sm btn-warning fw-bold" type="submit">Đăng ký</button>
        </form>
      </section>
    </div>

    <hr class="border-light-subtle my-3" />

    <div class="d-flex flex-column flex-md-row justify-content-between align-items-center gap-2 small">
      <div>© <span id="ft-year"><script>document.getElementById('ft-year').innerText = new Date().getFullYear();</script></span> PhoneThai. All rights reserved.</div>
      <div class="text-muted">
        <a href="${ctx}/terms" class="me-3">Điều khoản</a>
        <a href="${ctx}/privacy">Chính sách bảo mật</a>
      </div>
    </div>
  </div>
</footer>
