<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>

<html>

<head>

<meta charset="UTF-8">

<title>유휴공간 목록</title>

<!-- =========================================================
     카카오맵 JavaScript SDK
     ========================================================= -->

<script type="text/javascript"
	src="//dapi.kakao.com/v2/maps/sdk.js?appkey=621670cf124241c7373264f0855aedbe">
</script>

<style>
* {
	box-sizing: border-box;
}

body {
	margin: 0;
	font-family: Arial, "Noto Sans KR", sans-serif;
	background: #f5f6f8;
	color: #222;
}

/* =========================
   전체 페이지
   ========================= */
.page {
	width: 100%;
	height: 100vh;
	display: flex;
	flex-direction: column;
}

/* =========================
   상단 헤더
   ========================= */
.header {
	height: 72px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 35px;
	background-color: #ffffff;
	border-bottom: 1px solid #e5e5e5;
	box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
	position: relative;
	z-index: 10;
}

/* =========================
   로고
   ========================= */
.header-logo {
	display: flex;
	align-items: center;
	gap: 15px;
}

.logo-main {
	font-size: 22px;
	font-weight: 700;
	letter-spacing: -0.5px;
	color: #222;
}

.logo-sub {
	padding-left: 15px;
	border-left: 1px solid #ddd;
	font-size: 12px;
	color: #888;
}

/* =========================
   상단 메뉴
   ========================= */
.header-nav {
	display: flex;
	align-items: center;
	gap: 35px;
}

.header-nav a {
	position: relative;
	text-decoration: none;
	color: #666;
	font-size: 14px;
	font-weight: 500;
	padding: 25px 0;
	transition: color 0.2s;
}

.header-nav a:hover {
	color: #222;
}

/* 현재 메뉴 */
.header-nav a.active {
	color: #222;
	font-weight: 700;
}

/* 현재 메뉴 아래 선 */
.header-nav a.active::after {
	content: "";
	position: absolute;
	left: 0;
	right: 0;
	bottom: 0;
	height: 2px;
	background-color: #222;
}

/* =================================================
   전체 화면
   ================================================= */
.container {
	display: flex;
	width: 100%;
	flex: 1;
	min-height: 0;
}

/* =================================================
   왼쪽 사이드바
   ================================================= */
.sidebar {
	width: 370px;
	padding: 28px 24px;
	background-color: #ffffff;
	border-right: 1px solid #e5e7eb;
	overflow-y: auto;
	box-shadow: 2px 0 12px rgba(0, 0, 0, 0.04);
	z-index: 10;
}

/* =================================================
   프로젝트 제목
   ================================================= */
.sidebar h1 {
	margin: 0;
	font-size: 26px;
	font-weight: 800;
	letter-spacing: -1px;
	color: #222;
}

/* 프로젝트 설명 */
.sidebar::before {
	content: "유휴공간 최적용도 추천 서비스";
	display: block;
	margin-top: 7px;
	margin-bottom: 28px;
	font-size: 13px;
	color: #888;
	letter-spacing: -0.3px;
}

/* =================================================
   검색 영역
   ================================================= */
.search-group {
	margin-bottom: 18px;
}

/* 검색 영역 제목 */
.search-group label {
	display: block;
	margin-bottom: 8px;
	font-size: 13px;
	font-weight: 700;
	color: #444;
}

/* 입력창 */
.search-group select, .search-group input {
	width: 100%;
	height: 44px;
	padding: 0 13px;
	border: 1px solid #dfe2e7;
	border-radius: 8px;
	background-color: #fafbfc;
	font-size: 13px;
	color: #333;
	outline: none;
	transition: all 0.2s;
}

/* 입력창 클릭 */
.search-group select:focus, .search-group input:focus {
	border-color: #555;
	background-color: #ffffff;
	box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.05);
}

/* placeholder */
.search-group input::placeholder {
	color: #aaa;
}

/* =================================================
   버튼
   ================================================= */
.button-group {
	display: flex;
	gap: 8px;
	margin-top: 22px;
	margin-bottom: 22px;
}

.button-group button {
	height: 44px;
	border: none;
	border-radius: 8px;
	cursor: pointer;
	font-size: 13px;
	font-weight: 600;
	transition: all 0.2s;
}

/* 검색 */
.search-button {
	flex: 1;
	background-color: #222;
	color: white;
}

.search-button:hover {
	background-color: #000;
	transform: translateY(-1px);
}

/* 초기화 */
.reset-button {
	width: 80px;
	background-color: #f1f2f4;
	color: #555;
}

.reset-button:hover {
	background-color: #e5e6e8;
}

/* =================================================
   검색 결과 영역
   ================================================= */
.result-count {
	display: flex;
	align-items: center;
	padding: 13px 14px;
	margin-bottom: 12px;
	background-color: #f7f8fa;
	border: 1px solid #eceef1;
	border-radius: 8px;
	font-size: 12px;
	color: #777;
}

.result-count strong {
	margin: 0 4px;
	font-size: 14px;
	color: #222;
}

/* =================================================
   검색 결과 목록
   ================================================= */
.result-list {
	border-top: 1px solid #eeeeee;
}

.result-item {
	padding: 16px 8px;
	border-bottom: 1px solid #eeeeee;
	cursor: pointer;
	transition: all 0.2s;
}

.result-item:hover {
	padding-left: 12px;
	background-color: #f8f9fa;
}

/* 시설명 */
.result-item .facility-name {
	margin-bottom: 6px;
	font-size: 15px;
	font-weight: 700;
	color: #222;
}

/* 면적 / 행정동 */
.result-item .facility-info {
	margin-bottom: 5px;
	font-size: 12px;
	color: #666;
}

/* 주소 */
.result-item .facility-address {
	font-size: 11px;
	color: #999;
	line-height: 1.5;
}

/* =================================================
   검색 결과 없음
   ================================================= */
.no-result {
	padding: 40px 10px;
	text-align: center;
	color: #999;
	font-size: 13px;
}

/* =================================================
   지도
   ================================================= */
#map {
	flex: 1;
	height: 100%;
	background-color: #e9ecef;
}

/* =================================================
   InfoWindow
   ================================================= */
.info-window {
	padding: 12px;
	width: 320px;
	font-size: 13px;
	line-height: 1.6;
}

.info-window .title {
	margin-bottom: 5px;
	font-size: 15px;
	font-weight: bold;
}

.info-window .basic-info {
	margin-bottom: 10px;
}

/* =================================================
   추천 영역
   ================================================= */
.recommendation-box {
	margin-top: 10px;
	padding-top: 10px;
	border-top: 1px solid #ddd;
}

.recommendation-header {
	margin-bottom: 10px;
	font-size: 15px;
	font-weight: bold;
}

.recommendation-item {
	margin-bottom: 12px;
	padding: 9px;
	background-color: #f7f7f7;
	border-radius: 5px;
}

.recommendation-title {
	display: flex;
	justify-content: space-between;
	margin-bottom: 5px;
	font-weight: bold;
}

.recommendation-score {
	font-weight: bold;
}

.recommendation-item ul {
	margin: 5px 0 0 0;
	padding-left: 18px;
}

.recommendation-item li {
	margin-bottom: 2px;
	font-size: 12px;
}

.no-recommendation {
	color: #888;
	font-size: 12px;
}

.loading {
	color: #888;
	font-size: 12px;
}

/* =========================================================
   InfoWindow 글씨 크기 강제 적용
   ========================================================= */

/* 유휴공간 이름 */
.info-window .info-title {
	font-size: 17px !important;
	font-weight: 700 !important;
	margin-bottom: 8px !important;
}

/* 행정동 */
.info-window .info-area {
	font-size: 13px !important;
	font-weight: 400 !important;
	margin-bottom: 8px !important;
}

/* 주소 / 면적 라벨 */
.info-window .info-label {
	font-size: 15px !important;
	font-weight: 700 !important;
}

/* 실제 주소 / 면적 */
.info-window .info-value {
	font-size: 15px !important;
	font-weight: 400 !important;
}
</style>

</head>

<body>


	<div class="page">

		<!-- =========================
         상단 헤더
         ========================= -->
		<header class="header">

			<div class="header-logo">
				<div class="logo-main">fill in the blank</div>

				<div class="logo-sub">빈 공간을 새로운 가치로 채우다</div>
			</div>

			<nav class="header-nav">
				<a href="#" class="active"> 유휴공간 찾기 </a> <a href="#"> 서비스 소개 </a> <a
					href="#"> 추천 시스템 </a>
			</nav>

		</header>


		<!-- =========================
         지도 + 검색 영역
         ========================= -->
		<div class="container">

			<div class="sidebar">

				<!-- =================================================
             프로젝트 제목
             ================================================= -->

				<h1>유휴공간 찾기</h1>


				<!-- =================================================
             행정동 검색
             ================================================= -->

				<div class="search-group">

					<label for="areaName"> 행정동 </label> <select id="areaName">

						<option value="">전체 행정동</option>

					</select>

				</div>


				<!-- =================================================
             시설명 검색
             ================================================= -->

				<div class="search-group">

					<label for="facilityName"> 시설명 </label> <input type="text"
						id="facilityName" placeholder="시설명을 입력하세요">

				</div>


				<!-- =================================================
             최소 면적 검색
             ================================================= -->

				<div class="search-group">

					<label for="facilitySize"> 최소 면적(㎡) </label> <input type="number"
						id="facilitySize" placeholder="예: 100" min="0">

				</div>


				<!-- =================================================
             검색 / 초기화 버튼
             ================================================= -->

				<div class="button-group">

					<button type="button" class="search-button"
						onclick="searchIdleSpace()">검색</button>


					<button type="button" class="reset-button" onclick="resetSearch()">

						초기화</button>

				</div>


				<!-- =================================================
             검색 결과 개수
             ================================================= -->

				<div class="result-count">
					검색 결과: <strong id="resultCount"> ${idleSpaces.size()} </strong> 개
				</div>


				<!-- =================================================
             검색 결과 목록
             ================================================= -->
				<div id="resultList" class="result-list"></div>
			</div>


		<!-- =================================================
         카카오 지도
         ================================================= -->
			<div id="map"></div>

		</div>

	</div>



	<script>


/* =========================================================
   1. 지도 생성
   ========================================================= */

var container =
    document.getElementById('map');


var options = {

    center:
        new kakao.maps.LatLng(
            37.5665,
            126.9780
        ),

    level: 8

};


var map =
    new kakao.maps.Map(
        container,
        options
    );



/* =========================================================
   2. 마커 배열
   ========================================================= */

var markers = [];



/* =========================================================
   3. InfoWindow 하나만 사용
   ========================================================= */

var infowindow = new kakao.maps.InfoWindow({
        zIndex: 1
    });



/* =========================================================
   4. 현재 선택된 마커
   ========================================================= */

/*
 * 추천 API 응답이 늦게 도착했을 때
 *
 * 이전 마커의 추천 정보가
 * 현재 마커의 InfoWindow를 덮어쓰는 문제를 방지한다.
 */

var currentMarker = null;



/* =========================================================
   5. 행정동 목록
   ========================================================= */

var areaNames = new Set();



/* =========================================================
   6. DB 데이터 → 마커 생성
   ========================================================= */

<c:forEach var="idleSpace" items="${idleSpaces}">


    var idleId =
        ${idleSpace.idleId};


    var latitude =
        ${idleSpace.latitude};


    var longitude =
        ${idleSpace.longtitude};


    var facilityName =
        '${idleSpace.facilityName}';


    var address =
        '${idleSpace.address}';


    var areaName =
        '${idleSpace.areaName}';


    var facilitySize =
        ${idleSpace.facilitySize};



    /* 행정동 목록 */

    if (areaName) {
        areaNames.add(areaName);
    }


    /* 마커 위치 */

    var position =
        new kakao.maps.LatLng(
            latitude,
            longitude
        );


    /* 마커 생성 */

    var marker =
        new kakao.maps.Marker({
            position: position,
            map: map
        });



    /* 마커에 DB 데이터 저장 */

    marker.idleSpace = {
        idleId: idleId,
        facilityName: facilityName,
        address: address,
        areaName: areaName,
        facilitySize: facilitySize,
        latitude: latitude,
        longitude: longitude

    };


    /* 마커 배열에 저장 */

    markers.push(marker);



    /* =================================================
       마커 클릭 이벤트
       ================================================= */

    kakao.maps.event.addListener(
        marker,
        'click',
        function() {
            showInfoWindow(this);
        }

    );


</c:forEach>



/* =========================================================
   7. 기본 정보 HTML
   ========================================================= */

function createBasicInfoHtml(data) {

    return (

        '<div class="info-window">' +

            '<div class="info-title">' +

                escapeHtml(
                    data.facilityName
                ) +

            '</div>' +


            '<div class="info-area">' +

                '📍 ' +

                escapeHtml(
                    data.areaName
                ) +

            '</div>' +


            '<div class="basic-info">' +

                '<div class="info-row">' +

                    '<div class="info-label">' +
                        '주소' +
                    '</div>' +

                    '<div class="info-value">' +
                        escapeHtml(
                            data.address
                        ) +
                    '</div>' +

                '</div>' +


                '<div class="info-row">' +

                    '<div class="info-label">' +
                        '면적' +
                    '</div>' +

                    '<div class="info-value">' +

                        data.facilitySize +

                        '㎡' +

                    '</div>' +

                '</div>' +


            '</div>' +

        '</div>'

    );

}



/* =========================================================
   8. 추천 HTML 생성
   ========================================================= */

function createRecommendationHtml(
    recommendations
) {


    /* 추천 정보가 없는 경우 */

    if (!recommendations || recommendations.length === 0) {

        return (

            '<div class="recommendation-box">' +

                '<div class="recommendation-header">' +

                    '<div class="recommendation-header-title">' +
                        '⭐ 추천 정보' +
                    '</div>' +

                '</div>' +

                '<div class="no-recommendation">' +

                    '등록된 추천 정보가 없습니다.' +

                '</div>' +

            '</div>'

        );

    }



    /* 추천 영역 시작 */

    var html =

        '<div class="recommendation-box">' +

            '<div class="recommendation-header">' +

                '<div class="recommendation-header-title">' +

                    '⭐ 추천 TOP 3' +

                '</div>' +

                '<div class="recommendation-header-sub">' +

                    '공간 적합도 순위' +

                '</div>' +

            '</div>';



    /* 최대 3개 */

    var top3 =
        recommendations.slice(0, 3);



    top3.forEach(
        function(recommendation) {

            var score = Number(
                    recommendation.score
                );



            /* 추천 이유 */

            var reasonText =
                recommendation.recommendationReason;

            var reasons = [];

            if (reasonText) {
                reasons = reasonText.split(', ');
            }



            /* 추천 카드 */
            html +=

                '<div class="recommendation-item">' +


                    '<div class="recommendation-title">' +

                        '<div class="recommendation-type">' +

                            '<span class="recommendation-rank">' +

                                recommendation.rank +

                                '위 ' +

                            '</span>' +

                            '<span class="recommendation-badge">' +

                                escapeHtml(
                                    recommendation.recommendationType
                                ) +

                            '</span>' +

                        '</div>' +

                        '<div class="recommendation-score">' +
                            score.toFixed(2) +
                            '<span>점</span>' +

                        '</div>' +


                    '</div>';



            /* 추천 이유 */

            if (reasons.length > 0) {

                html +=
                    '<div class="recommendation-reasons">';

                reasons.forEach(

                    function(reason) {

                        html +=

                            '<div class="reason">' +

                                '<span class="reason-check">✓ </span>' +

                                escapeHtml(reason) +

                            '</div>';
                    }

                );

                html +=

                    '</div>';

            }

            html +=

                '</div>';

        }

    );


    html += '</div>';

    return html;

}



/* =========================================================
   9. HTML 특수문자 처리
   ========================================================= */

function escapeHtml(value) {

    if (value === null || value === undefined) {
        return '';
    }


    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');

}



/* =========================================================
   10. InfoWindow 표시
   ========================================================= */

function showInfoWindow(marker) {

    /* 현재 마커 저장 */
    currentMarker = marker;

    /* 마커 데이터 */
    var data =
        marker.idleSpace;


    /* =====================================================
       로딩 화면
       ===================================================== */

    var loadingContent =

        '<div class="info-window">' +

            '<div class="info-title">' +

                escapeHtml(
                    data.facilityName
                ) +

            '</div>' +

            '<div class="info-area">' +

                '📍 ' +

                escapeHtml(
                    data.areaName
                ) +

            '</div>' +

            '<div class="basic-info">' +

                '<div class="info-row">' +

                    '<div class="info-label">' +
                        '주소' +
                    '</div>' +

                    '<div class="info-value">' +

                        escapeHtml(
                            data.address
                        ) +

                    '</div>' +

                '</div>' +

                '<div class="info-row">' +

                    '<div class="info-label">' +
                        '면적' +
                    '</div>' +

                    '<div class="info-value">' +

                        data.facilitySize +

                        '㎡' +

                    '</div>' +

                '</div>' +

            '</div>' +

            '<div class="recommendation-box">' +

                '<div class="recommendation-header">' +

                    '<div class="recommendation-header-title">' +

                        '⭐ 추천 정보' +

                    '</div>' +

                '</div>' +

                '<div class="loading">' +

                    '추천 정보를 불러오는 중...' +

                '</div>' +

            '</div>' +

        '</div>';



    /* 로딩 화면 표시 */
    infowindow.setContent(loadingContent);


    infowindow.open(
        map,
        marker
    );



    /* =====================================================
       추천 API 호출
       ===================================================== */

    fetch(
        '${pageContext.request.contextPath}/recommendation/' +
        data.idleId
    )

    .then(

        function(response) {

            if (!response.ok) {
                throw new Error(
                    '추천 API 호출 실패: ' +
                    response.status
                );
            }

            return response.json();

        }

    )

    .then(

        function(recommendations) {


            /*
             * 다른 마커를 클릭했다면
             * 이전 요청 결과를 무시한다.
             */

            if (currentMarker !== marker) {
                return;
            }



            /* 추천 HTML 생성 */

            var recommendationHtml = createRecommendationHtml(recommendations);


            /* =================================================
               최종 InfoWindow
               ================================================= */

            var content =

                '<div class="info-window">' +

                    '<div class="info-title">' +

                        escapeHtml(
                            data.facilityName
                        ) +

                    '</div>' +


                    '<div class="info-area">' +

                        '📍 ' +

                        escapeHtml(
                            data.areaName
                        ) +

                    '</div>' +


                    '<div class="basic-info">' +


                        '<div class="info-row">' +

                            '<div class="info-label">' +
                                '주소' +
                            '</div>' +

                            '<div class="info-value">' +

                                escapeHtml(
                                    data.address
                                ) +

                            '</div>' +

                        '</div>' +


                        '<div class="info-row">' +

                            '<div class="info-label">' +
                                '면적' +
                            '</div>' +

                            '<div class="info-value">' +

                                data.facilitySize +

                                '㎡' +

                            '</div>' +

                        '</div>' +


                    '</div>' +


                    recommendationHtml +


                '</div>';



            /* InfoWindow 갱신 */

            infowindow.setContent(
                content
            );


            infowindow.open(
                map,
                marker
            );

        }

    )

    .catch(
        function(error) {
            console.error(
                '추천 정보 조회 오류:',
                error
            );



            /*
             * 다른 마커를 클릭했다면
             * 오류 메시지도 표시하지 않는다.
             */

            if (currentMarker !== marker) {
                return;
            }



            /* 오류 화면 */

            var content =

                '<div class="info-window">' +


                    '<div class="info-title">' +

                        escapeHtml(
                            data.facilityName
                        ) +

                    '</div>' +


                    '<div class="info-area">' +

                        '📍 ' +

                        escapeHtml(
                            data.areaName
                        ) +

                    '</div>' +


                    '<div class="basic-info">' +

                        '<div class="info-row">' +

                            '<div class="info-label">' +
                                '주소' +
                            '</div>' +

                            '<div class="info-value">' +

                                escapeHtml(
                                    data.address
                                ) +

                            '</div>' +

                        '</div>' +


                        '<div class="info-row">' +

                            '<div class="info-label">' +
                                '면적' +
                            '</div>' +

                            '<div class="info-value">' +

                                data.facilitySize +

                                '㎡' +

                            '</div>' +

                        '</div>' +

                    '</div>' +


                    '<div class="recommendation-box">' +

                        '<div class="recommendation-header">' +

                            '<div class="recommendation-header-title">' +

                                '⭐ 추천 정보' +

                            '</div>' +

                        '</div>' +


                        '<div class="no-recommendation">' +

                            '추천 정보를 불러오지 못했습니다.' +

                        '</div>' +

                    '</div>' +


                '</div>';



            infowindow.setContent(
                content
            );


            infowindow.open(
                map,
                marker
            );

        }

    );

}



/* =========================================================
   11. 행정동 SELECT 생성
   ========================================================= */

var areaSelect =
    document.getElementById(
        'areaName'
    );


Array.from(areaNames)

    .sort()

    .forEach(

        function(area) {


            var option =
                document.createElement(
                    'option'
                );


            option.value =
                area;


            option.textContent =
                area;


            areaSelect.appendChild(
                option
            );

        }

    );



/* =========================================================
   12. 검색
   ========================================================= */

function searchIdleSpace() {


    var selectedArea =
        document.getElementById(
            'areaName'
        ).value;



    var facilityKeyword =
        document.getElementById(
            'facilityName'
        ).value
        .trim()
        .toLowerCase();



    var minSizeValue =
        document.getElementById(
            'facilitySize'
        ).value;



    var minSize = 0;



    if (minSizeValue !== '') {

        minSize =
            Number(
                minSizeValue
            );

    }



    var resultCount = 0;



    /* 검색 결과 */

    var searchResults = [];



    /* 모든 마커 검사 */

    markers.forEach(

        function(marker) {


            var data =
                marker.idleSpace;



            var matchArea = true;

            var matchFacility = true;

            var matchSize = true;



            /* 행정동 */

            if (selectedArea !== '') {

                matchArea =
                    data.areaName ===
                    selectedArea;

            }



            /* 시설명 */

            if (facilityKeyword !== '') {

                matchFacility =

                    data.facilityName
                        .toLowerCase()
                        .includes(
                            facilityKeyword
                        );

            }



            /* 면적 */

            if (minSize > 0) {

                matchSize =

                    Number(
                        data.facilitySize
                    ) >= minSize;

            }



            /* 모든 조건 만족 */

            if (

                matchArea &&

                matchFacility &&

                matchSize

            ) {


                marker.setMap(map);


                searchResults.push(
                    marker
                );


                resultCount++;

            }

            else {

                marker.setMap(null);

            }

        }

    );



    /* 결과 개수 */

    document.getElementById(
        'resultCount'
    ).textContent =
        resultCount;



    /* 결과 목록 */

    renderResultList(
        searchResults
    );



    /* InfoWindow 닫기 */

    infowindow.close();


    currentMarker = null;

}



/* =========================================================
   13. 검색 결과 목록
   ========================================================= */

function renderResultList(
    searchResults
) {


    var resultList =
        document.getElementById(
            'resultList'
        );


    /* 기존 목록 삭제 */

    resultList.innerHTML = '';



    /* 검색 결과 없음 */

    if (searchResults.length === 0) {

        var noResult = document.createElement('div');

        noResult.className = 'no-result';
        noResult.textContent = '검색 결과가 없습니다.';

        resultList.appendChild(noResult);

        return;

    }



    /* 결과 생성 */
    searchResults.forEach(

        function(marker) {
            var data = marker.idleSpace;

            /* 목록 아이템 */
            var item = document.createElement('div');
            item.className = 'result-item';

            /* 시설명 */
            var name = document.createElement('div');

            name.className = 'facility-name';
            name.textContent = data.facilityName;


            /* 면적 / 행정동 */
            var info = document.createElement('div');

            info.className = 'facility-info';
            info.textContent = data.areaName + ' · ' + data.facilitySize + '㎡';


            /* 주소 */
            var address = document.createElement('div');

            address.className = 'facility-address';
            address.textContent = data.address;


            /* 아이템에 추가 */
            item.appendChild(name);
            item.appendChild(info);
            item.appendChild(address);


            /* 목록 클릭 */

            item.addEventListener(
                'click',
                function() {
                    moveToMarker(
                        marker
                    );
                }
            );

            /* 결과 목록 추가 */
            resultList.appendChild(item);

        }

    );

}



/* =========================================================
   14. 목록 클릭 → 해당 마커 이동
   ========================================================= */

function moveToMarker(marker) {

    var data = marker.idleSpace;

    /* 해당 좌표 */

    var position = new kakao.maps.LatLng(
            data.latitude,
            data.longitude
        );


    /* 지도 이동 */
    map.setCenter(position);


    /* 지도 확대 */
    map.setLevel(3);



    /* InfoWindow 표시 */
    showInfoWindow(marker);

}



/* =========================================================
   15. 초기화
   ========================================================= */

function resetSearch() {


    /* 검색창 초기화 */

    document.getElementById(
        'areaName'
    ).value = '';


    document.getElementById(
        'facilityName'
    ).value = '';


    document.getElementById(
        'facilitySize'
    ).value = '';



    /* 모든 마커 표시 */

    markers.forEach(
        function(marker) {
            marker.setMap(map);
        }
    );


    /* 결과 개수 */

    document.getElementById(
        'resultCount'
    ).textContent =
        markers.length;


    /* 전체 결과 목록 */
    renderResultList(
        markers
    );

    /* InfoWindow 닫기 */

    infowindow.close();
    currentMarker = null;


    /* 서울 전체 */

    map.setCenter(
        new kakao.maps.LatLng(
            37.5665,
            126.9780
        )
    );


    map.setLevel(8);

}



/* =========================================================
   16. 페이지 처음 열었을 때
   ========================================================= */

renderResultList(
    markers
);


</script>

</body>

</html>
