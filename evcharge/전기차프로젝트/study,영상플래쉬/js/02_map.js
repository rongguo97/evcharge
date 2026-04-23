const map = L.map("map").setView([35.1796, 129.0756], 12);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

// 초기 마커 (부산 중심)
let marker = L.marker([35.1796, 129.0756]).addTo(map)
    .bindPopup("부산 중심")
    .openPopup();

// 지도 클릭 이벤트: 클릭한 위치 주소 가져와서 마커 표시
map.on('click', function(e) {
    const { lat, lng } = e.latlng;

    // Nominatim API로 역지오코딩
    fetch(`https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}`)
      .then(res => res.json())
      .then(data => {
          const address = data.display_name;

         // 마커위치+주소표시
          marker.setLatLng([lat, lng])
                .bindPopup(`<b>클릭 위치</b><br>${address}`)
                .openPopup();
      })
      .catch(err => {
          console.error(err);
          marker.setLatLng([lat, lng])
                .bindPopup(`<b>클릭 위치</b><br>주소를 가져오지 못했습니다.`)
                .openPopup();
      });
});