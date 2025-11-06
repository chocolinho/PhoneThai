<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- CSS & Icons -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>
<link href="${ctx}/css/style.css?v=<%=System.currentTimeMillis()%>" rel="stylesheet" type="text/css"/>

<header class="cps-header sticky-top">
    <div class="cps-nav">
        <div class="container d-flex align-items-center gap-2">

            <!-- Logo -->
            <a class="cps-logo-text fw-bold text-white text-decoration-none me-3" href="${ctx}/">PhoneThai</a>

            <!-- Danh mục sản phẩm -->
            <div class="hover-root js-cat-root">
                <button id="btnCategories" type="button"
                        class="btn btn-light btn-sm cps-btn-rounded d-flex align-items-center gap-2 hover-trigger"
                        aria-controls="panelCategories" aria-expanded="false">
                    <i class="bi bi-list"></i><span class="d-none d-md-inline">Danh mục</span>
                </button>

                <div id="panelCategories" class="hover-panel shadow-sm" role="region" aria-label="Danh mục sản phẩm" aria-hidden="true">
                    <div class="container py-3">

                        <div class="row g-4 small">

                            <div class="col-12 col-md-6">
                                <h6 class="mb-2 text-uppercase fw-bold text-danger">Điện thoại</h6>
                                <ul class="list-unstyled mb-0">
                                    <li><a class="item-link" href="${ctx}/products?cat=phone">Tất cả điện thoại</a></li>
                                </ul>
                            </div>

                            <div class="col-12 col-md-6">
                                <h6 class="mb-2 text-uppercase fw-bold text-danger">Thương hiệu</h6>
                                <ul class="list-unstyled mb-0">
                                    <li><a class="item-link" href="${ctx}/products?brand=Apple">Apple</a></li>
                                    <li><a class="item-link" href="${ctx}/products?brand=Samsung">Samsung</a></li>
                                    <li><a class="item-link" href="${ctx}/products?brand=Xiaomi">Xiaomi</a></li>
                                    <li><a class="item-link" href="${ctx}/products?brand=OPPO">OPPO</a></li>
                                    <li><a class="item-link" href="${ctx}/products?brand=Sony">Sony</a></li>
                                    <li><a class="item-link" href="${ctx}/products?brand=Nokia">Nokia</a></li>
                                </ul>
                            </div>

                           

                        </div>
                    </div>
                </div>
            </div>

            <!-- Search (desktop inline) -->
            <form class="cps-search d-none d-md-flex" action="${ctx}/search" method="get" role="search">
                <i class="bi bi-search" aria-hidden="true"></i>
                <input class="form-control" type="search" name="q"
                       placeholder="Bạn muốn mua gì hôm nay?" aria-label="Tìm kiếm">
                <button class="btn btn-light ms-2 d-flex align-items-center gap-1" type="submit" title="Tìm kiếm">
                    <i class="bi bi-search"></i><span class="d-none d-lg-inline">Tìm</span>
                </button>
            </form>

            <!-- Cart -->
            <a class="btn btn-light btn-sm cps-btn-rounded position-relative" href="${ctx}/cart" title="Giỏ hàng">
                <i class="bi bi-bag"></i>
                <c:if test="${not empty sessionScope.cartCount}">
                    <span class="badge position-absolute top-0 start-100 translate-middle"
                          style="background: linear-gradient(135deg,#6C5CE7,#A78BFA); color:white;">
                        ${sessionScope.cartCount}
                    </span>
                </c:if>
            </a>

            <!-- Tài khoản -->
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <div class="dropdown">
                        <c:set var="displayName"
                               value="${empty sessionScope.user.fullName ? sessionScope.user.username : sessionScope.user.fullName}" />
                        <button class="btn btn-light btn-sm cps-btn-rounded d-flex align-items-center gap-2 dropdown-toggle"
                                id="dropdownUser" data-bs-toggle="dropdown" aria-expanded="false">
                            <span class="cps-avatar">
                                <c:choose>
                                    <c:when test="${not empty displayName}">${fn:toUpperCase(fn:substring(displayName,0,1))}</c:when>
                                    <c:otherwise>U</c:otherwise>
                                </c:choose>
                            </span>
                            <span class="d-none d-md-inline"><c:out value="${displayName}"/></span>
                        </button>

                        <ul class="dropdown-menu dropdown-menu-end shadow-sm small" aria-labelledby="dropdownUser">
                            <li class="dropdown-header">
                                <div class="fw-semibold"><c:out value="${displayName}"/></div>
                                <div class="text-muted"><c:out value="${sessionScope.user.email}"/></div>
                            </li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${ctx}/orders"><i class="bi bi-receipt me-2"></i>Đơn hàng</a></li>
                            <li><a class="dropdown-item" href="${ctx}/auth/profile"><i class="bi bi-person me-2"></i>Tài khoản</a></li>
                            <li><a class="dropdown-item" href="${ctx}/billing"><i class="bi bi-credit-card me-2"></i>Thanh toán</a></li>
                            <li><a class="dropdown-item" href="${ctx}/addresses"><i class="bi bi-geo-alt me-2"></i>Địa chỉ</a></li>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item text-danger" href="${ctx}/logout"><i class="bi bi-box-arrow-right me-2"></i>Đăng xuất</a></li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-light btn-sm cps-btn-rounded" href="${ctx}/auth/Login.jsp">
                        <i class="bi bi-person"></i> <span class="d-none d-md-inline">Đăng nhập</span>
                    </a>
                </c:otherwise>
            </c:choose>

        </div>  <!-- /.container -->
    </div>  <!-- /.cps-nav -->
</header>

<!-- JS: Toggle panel Danh mục -->
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const root = document.querySelector('.js-cat-root');
        const btn = document.getElementById('btnCategories');
        const panel = document.getElementById('panelCategories');

        if (!root || !btn || !panel)
            return;

        const isMobile = () => window.matchMedia('(max-width: 767.98px)').matches;
        const lockScroll = () => {
            if (isMobile())
                document.body.style.overflow = 'hidden';
        };
        const unlockScroll = () => {
            document.body.style.overflow = '';
        };

        const open = () => {
            root.classList.add('open');
            btn.setAttribute('aria-expanded', 'true');
            panel.setAttribute('aria-hidden', 'false');
            lockScroll();
        };

        const close = () => {
            root.classList.remove('open');
            btn.setAttribute('aria-expanded', 'false');
            panel.setAttribute('aria-hidden', 'true');
            unlockScroll();
        };

        btn.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            root.classList.contains('open') ? close() : open();
        });

        // Click ra ngoài thì đóng (không ảnh hưởng dropdown tài khoản)
        document.addEventListener('click', (e) => {
            const inCat = root.contains(e.target);
            const inUserDropdown = e.target.closest && e.target.closest('.dropdown');
            if (!inCat && !inUserDropdown)
                close();
        });

        // ESC để đóng
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape')
                close();
        });

        // Khi đổi kích thước, nếu đang mở mà về desktop thì bỏ lock scroll
        window.addEventListener('resize', () => {
            if (root.classList.contains('open') && !isMobile())
                unlockScroll();
        });

        // ARIA init
        btn.setAttribute('aria-controls', 'panelCategories');
        btn.setAttribute('aria-expanded', 'false');
        panel.setAttribute('aria-hidden', 'true');
    });
</script>

<!-- (Khuyến nghị) Thêm Bootstrap JS trong layout chung, trước </body>:
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
-->
