document.addEventListener('DOMContentLoaded', () => {
    // 1. Leaflet 지도 초기화
    const initialLocation = [37.4979, 127.0276];
    const map = L.map('map').setView(initialLocation, 14);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);

    const chargerIcon = L.divIcon({
        className: 'custom-div-icon',
        html: `<div style="background-color:#6b5be2; width:12px; height:12px; border-radius:50%; border:2px solid white; box-shadow:0 0 10px rgba(107,91,226,0.8);"></div>`,
        iconSize: [12, 12],
        iconAnchor: [6, 6]
    });

    L.marker(initialLocation, { icon: chargerIcon }).addTo(map).bindPopup("강남역 충전소");

    // 2. 메가 메뉴 동적 제어 로직 (구조적 수정 반영)
    const navbar = document.querySelector('.navbar');
    const navItems = document.querySelectorAll('.nav-item-wrapper');
    const megaPanel = document.querySelector('.mega-menu-panel');
    const megaContents = document.querySelectorAll('.mega-content');

    // 메뉴 항목에 마우스 올렸을 때
    navItems.forEach(item => {
        item.addEventListener('mouseenter', () => {
            const menuId = item.getAttribute('data-menu');
            
            // 패널 열기
            megaPanel.classList.add('open');
            
            // 모든 컨텐츠 숨기기
            megaContents.forEach(content => {
                content.classList.remove('active');
            });
            
            // 해당하는 컨텐츠만 보이기
            const targetContent = document.getElementById(`menu-${menuId}`);
            if (targetContent) {
                targetContent.classList.add('active');
            }
        });
    });

    // 네비바 또는 메가 메뉴 패널을 벗어날 때 닫기
    const closeMenu = (e) => {
        // 마우스가 네비바나 메가 메뉴 패널 어디에도 있지 않을 때만 닫음
        if (!navbar.contains(e.relatedTarget) && !megaPanel.contains(e.relatedTarget)) {
            megaPanel.classList.remove('open');
        }
    };

    navbar.addEventListener('mouseleave', closeMenu);
    megaPanel.addEventListener('mouseleave', closeMenu);

    // 3. 검색 및 퀵 메뉴 이벤트
    const searchForm = document.querySelector('.search');
    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const query = document.querySelector('.search-box').value;
            if (query) alert(`'${query}' 위치를 검색합니다.`);
        });
    }

    // 4. 스크롤 시 내비게이션 스타일
    window.addEventListener('scroll', () => {
        const navbar = document.querySelector('.navbar');
        const megaMenu = document.querySelector('.mega-menu-panel');
        if (window.scrollY > 50) {
            navbar.style.background = 'rgba(11, 13, 26, 0.98)';
            navbar.style.height = '70px';
            megaMenu.style.top = '70px';
        } else {
            navbar.style.background = 'rgba(11, 13, 26, 0.95)';
            navbar.style.height = '80px';
            megaMenu.style.top = '80px';
        }
    });
});
