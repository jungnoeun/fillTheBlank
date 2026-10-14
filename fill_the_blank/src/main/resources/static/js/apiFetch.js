/**
 * 
 */
async function apiFetch(url, options = {}) {
	// 원래 요청 실행
	let response = await fetch(url, {...options, credentials:"same-origin"});

	// access token 만료 등으로 401이 발생한 경우 재발급 시도
	if(response.status !== 401 || url.includes("/token/refresh")) {
		return response;
	}
	
	const refreshResponse = await fetch("/token/refresh", {
		method: "POST",
		credentials: "same-origin"
	});
	
	// refresh token도 사용할 수 없다면 로그인 페이지로 이동
	if(!refreshResponse.ok) {
		window.location.href = "/login";
		return refreshResponse;
	}
	
	// 새 access token 쿠키가 설정된 후 원래 요청을 한번 재시도
	return fetch(url, {...options, credentials: "same-origin"});
}