<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">

<head>
    <meta charset="UTF-8">

    <title>실시간 채팅</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            background-color: #f5f5f5;
            font-family: Arial, sans-serif;
        }
        

		.chat-page {
    		min-height: calc(100vh - 72px);
    		display: flex;
    		justify-content: center;
    		align-items: center;
    		padding: 30px;
		}

		.chat-container {
    		width: 100%;
    		max-width: 600px;
    		height: calc(100vh - 132px);
    		max-height: 750px;
    		min-height: 550px;
    		margin: 0;
    		background-color: white;
    		border: 1px solid #ddd;
    		border-radius: 12px;
    		display: flex;
    		flex-direction: column;
    		overflow: hidden;
		}

        .chat-header {
            padding: 18px 20px;
            border-bottom: 1px solid #ddd;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .chat-header h2 {
            margin: 0;
            font-size: 20px;
        }
        
        .chat-header-left {
    		display: flex;
    		align-items: center;
    		gap: 10px;
		}

		.chat-back-button {
    		border: none;
    		background: none;
    		padding: 0;
    		font-size: 22px;
    		color: #333;
    		cursor: pointer;
		}

		.chat-back-button:hover {
    		color: #000;
		}
		
		.chat-title-area {
    		display: flex;
    		flex-direction: column;
    		gap: 3px;
		}

		.chat-title-area h2 {
    		margin: 0;
    		font-size: 18px;
    		font-weight: 700;
    		color: #222;
		}

		.chat-subtitle {
    		font-size: 11px;
    		color: #888;
		}

        #connectionStatus {
            font-size: 13px;
            color: #777;
        }

        .chat-messages {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            background-color: #fafafa;
        }

        .message-row {
            display: flex;
            flex-direction: column;
            margin-bottom: 16px;
        }

        .message-row.mine {
            align-items: flex-end;
        }

        .message-row.other {
            align-items: flex-start;
        }

        .sender-name {
            margin-bottom: 5px;
            font-size: 12px;
            color: #666;
        }

        .message-bubble {
            max-width: 70%;
            padding: 10px 14px;
            border-radius: 14px;
            word-break: break-word;
            font-size: 14px;
            line-height: 1.4;
        }

        .message-row.mine .message-bubble {
            background-color: #333;
            color: white;
        }

        .message-row.other .message-bubble {
            background-color: white;
            border: 1px solid #ddd;
            color: #222;
        }

        .message-time {
            margin-top: 4px;
            font-size: 11px;
            color: #999;
        }

        .chat-input-area {
            padding: 14px;
            border-top: 1px solid #ddd;
            display: flex;
            gap: 8px;
            background-color: white;
        }

        #messageInput {
            flex: 1;
            height: 42px;
            padding: 0 12px;
            border: 1px solid #ccc;
            border-radius: 8px;
            outline: none;
        }

        #sendButton {
            width: 80px;
            border: none;
            border-radius: 8px;
            background-color: #333;
            color: white;
            cursor: pointer;
        }

        #sendButton:disabled {
            background-color: #aaa;
            cursor: default;
        }

        .leave-area {
            padding: 10px 14px;
            border-top: 1px solid #eee;
            text-align: right;
        }

        #leaveButton {
            border: none;
            background: none;
            color: #777;
            cursor: pointer;
        }
    </style>
</head>

<body
    data-idle-id="${idleId}"
    data-current-member-id="${sessionScope.loginMember.memberId}">
    
    <jsp:include page="/WEB-INF/views/common/header.jsp">
    	<jsp:param name="activeMenu" value="idle" />
	</jsp:include>

<main class="chat-page">
<div class="chat-container">

    <div class="chat-header">
    
    	<button type="button" class="back-button" onclick="history.back()">←</button>

        <div class="chat-title-area">
    		<h2>${facilityName}</h2>
    		<span class="chat-subtitle">공간 이용자 채팅</span>
		</div>
        <span id="connectionStatus">연결 준비 중...</span>

    </div>

    <div id="chatMessages" class="chat-messages"></div>

    <form id="chatForm" class="chat-input-area">
        
        <input type="text" id="messageInput" maxlength="2000" placeholder="메시지를 입력하세요." autocomplete="off">
        <button type="submit" id="sendButton" disabled>전송</button>

    </form>

    <div class="leave-area">
        <button type="button" id="leaveButton"> 채팅방 나가기</button>
    </div>

</div>


<script>

    /*
     * ==========================
     * 기본 값
     * ==========================
     */

    const idleId = document.body.dataset.idleId;
    const currentMemberId = Number(document.body.dataset.currentMemberId);

    let roomId = null;
    let socket = null;
    let stompConnected = false;

    const chatMessages = document.getElementById("chatMessages");
    const messageInput = document.getElementById("messageInput");
    const sendButton = document.getElementById("sendButton");
    const connectionStatus = document.getElementById("connectionStatus");

    const contextPath = '${pageContext.request.contextPath}';

    /*
     * ==========================
     * 초기화
     * ==========================
     */

    document.addEventListener("DOMContentLoaded", initializeChat);

    async function initializeChat() {
        try {
            connectionStatus.textContent = "채팅방 확인 중...";
            
            // 1. idleId에 해당하는 채팅방 조회/생성
            const roomResponse =
                await fetch(
                	"/api/chat/rooms/space/" + idleId,
                    {
                        method: "POST",
                        credentials: "same-origin"
                    }
                );


            if (!roomResponse.ok) {
                throw new Error("채팅방을 불러오지 못했습니다.");
            }

            const room = await roomResponse.json();
            roomId = room.roomId;

            console.log("[CHAT] roomId:",roomId);


            // 2. 채팅방 참여 
            await joinRoom();

            // 3. 기존 메시지 조회
            await loadRecentMessages();

            // 4. WebSocket 연결
            connectWebSocket();


        } catch (error) {
            console.error(error);
            connectionStatus.textContent = "채팅 연결 실패";

            alert("채팅방을 불러오는 중 문제가 발생했습니다.");
        }
    }


    /*
     * ==========================
     * 채팅방 참여
     * ==========================
     */

    async function joinRoom() {

        const response =
            await fetch(
            	"/api/chat/rooms/" + roomId + "/join",
                {
                    method: "POST",
                    credentials: "same-origin"
                }
            );

        if (!response.ok) {
            throw new Error("채팅방 참여 실패");
        }

    }


    /*
     * ==========================
     * 최근 메시지 조회
     * ==========================
     */

    async function loadRecentMessages() {

        const response =
            await fetch(
            	"/api/chat/rooms/" + roomId + "/messages",
                {
                    method: "GET",
                    credentials: "same-origin"
                }
            );


        if (!response.ok) {
            throw new Error("메시지 조회 실패");
        }

        const messages = await response.json();
        chatMessages.innerHTML = "";

        messages.forEach(message => {
            appendMessage(message);
        });

        scrollToBottom();

    }


    /*
     * ==========================
     * WebSocket 연결
     * ==========================
     */

    function connectWebSocket() {

        const protocol =
            location.protocol === "https:"
                ? "wss"
                : "ws";


        const socketUrl = protocol + "://" + location.host + "/ws/chat";
        socket = new WebSocket(socketUrl);

        socket.onopen = () => {
            console.log(
                "[CHAT] WebSocket 연결"
            );

            const connectFrame =
                "CONNECT\n" +
                "accept-version:1.2\n" +
                "host:" + location.host + "\n" +
                "heart-beat:0,0\n" +
                "\n" +
                "\0";

            socket.send(connectFrame);
        };


        socket.onmessage = (event) => {
            handleStompFrame(
                event.data
            );
        };


        socket.onerror = (error) => {
            console.error(
                "[CHAT] WebSocket 오류",
                error
            );

            connectionStatus.textContent = "연결 오류";
        };


        socket.onclose = () => {
            stompConnected = false;
            sendButton.disabled = true;
            console.log("[CHAT] WebSocket 종료", event.code, event.reason);
            
            // JWT 만료
            if(event.code === 4001) {
            	connectionStatus.textContent = "로그인 만료";
            	alert("로그인 시간이 만료되었습니다. 다시 로그인해주세요.");
            	window.location.href = contextPath + "/login";
            	
            	return;
            }
            
            // 다른 탭에서 로그아웃
            if(event.code === 4002) {
            	connectionStatus.textContent = "로그아웃";
            	alert("로그아웃되었습니다. 다시 로그인해주세요.");
            	window.location.href = contextPath + "/login";
            	
            	return;
            }
            
            // 인터넷 끊김 등 기타 WebSocket 종료
            connectionStatus.textContent = "연결 종료";
        };

    }


    /*
     * ==========================
     * STOMP Frame 처리
     * ==========================
     */

    function handleStompFrame(rawData) {

        
        // 한 번에 여러 STOMP Frame이 들어올 가능성을 고려
        const frames = rawData.split("\0");

        frames.forEach(frame => {
            if (!frame.trim()) {
                return;
            }

            // heartbeat
            if (frame === "\n") {
                return;
            }

            const separatorIndex = frame.indexOf("\n\n");

            let headerText;
            let body;

            if (separatorIndex >= 0) {
                headerText = frame.substring(0, separatorIndex);
                body = frame.substring(separatorIndex + 2);
            } else {
                headerText = frame;
                body = "";
            }

            const headerLines = headerText.split("\n");
            const command = headerLines.shift();


            
            // CONNECT 성공
            
            if (command === "CONNECTED") {
                console.log("[CHAT] STOMP 연결 성공");

                stompConnected = true;
                connectionStatus.textContent = "연결됨";

                sendButton.disabled = false;
                subscribeRoom();

                return;
            }


            
            // 실시간 메시지 수신
            if (command === "MESSAGE") {
                try {
                    const message = JSON.parse(body);
                    appendMessage(message);
                    scrollToBottom();

                } catch (error) {
                    console.error("메시지 JSON 변환 실패", error);
                }

                return;
            }


            
            // STOMP 오류
            if (command === "ERROR") {
                console.error( "[CHAT] STOMP ERROR", frame);
                connectionStatus.textContent ="채팅 오류";
            }

        });

    }


    /*
     * ==========================
     * 채팅방 구독
     * ==========================
     */

    function subscribeRoom() {

        const subscribeFrame =
            "SUBSCRIBE\n" +
            "id:chat-subscription\n" +
            "destination:/topic/chat/rooms/"
                + roomId + "\n" +
            "\n" +
            "\0";


        socket.send(subscribeFrame);
        console.log("[CHAT] 채팅방 구독 완료");

    }


    /*
     * ==========================
     * 메시지 전송
     * ==========================
     */

    document
        .getElementById("chatForm")
        .addEventListener(
            "submit",
            function(event) {
                event.preventDefault();
                const content = messageInput.value.trim();

                if (!content) {
                    return;
                }

                if (!stompConnected) {
                    alert("채팅 서버에 연결되어 있지 않습니다.");
                    return;
                }

                const body = JSON.stringify({content: content});
                const sendFrame =
                    "SEND\n" +
                    "destination:/app/chat/rooms/"
                        + roomId + "\n" +
                    "content-type:application/json\n" +
                    "\n" +
                    body +
                    "\0";


                socket.send(sendFrame);


                /*
                 * 화면에 직접 append 하지 않음.
                 *
                 * 서버 Broadcast MESSAGE를
                 * 다시 받아서 화면에 추가한다.
                 *
                 * 그래야 중복 출력되지 않는다.
                 */

                messageInput.value = "";
                messageInput.focus();

            }
        );


    /*
     * ==========================
     * 화면에 메시지 출력
     * ==========================
     */

    function appendMessage(message) {

        const row = document.createElement("div");


        const isMine =
            Number(message.senderId)
            === currentMemberId;


        row.className = "message-row " + (isMine ? "mine" : "other");


        
        // 상대방 메시지는 이름 표시
		if (!isMine) {
            const sender = document.createElement("div");
            sender.className = "sender-name";
            sender.textContent = message.senderName ?? "사용자";
            row.appendChild(sender);
        }

        const bubble = document.createElement("div");
        bubble.className = "message-bubble";


        /*
         * innerHTML 사용하지 않음.
         *
         * 사용자가 입력한 HTML/Script 실행 방지
         */

        bubble.textContent = message.content;
        row.appendChild(bubble);


        const time = document.createElement("div");
        time.className = "message-time";
        time.textContent = formatTime(message.createdAt);

        row.appendChild(time);
        chatMessages.appendChild(row);

    }


    /*
     * ==========================
     * 시간 출력
     * ==========================
     */

    function formatTime(createdAt) {

        if (!createdAt) {
            return "";
        }


        const date = new Date(createdAt);


        return date.toLocaleTimeString(
            "ko-KR",
            {
                hour: "2-digit",
                minute: "2-digit"
            }
        );

    }


    /*
     * ==========================
     * 스크롤
     * ==========================
     */

    function scrollToBottom() {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }


    /*
     * ==========================
     * 채팅방 나가기
     * ==========================
     */

    document.getElementById("leaveButton")
        .addEventListener(
            "click",
            async function() {
                const confirmed = confirm("채팅방에서 나가시겠습니까?");

                if (!confirmed) {
                    return;
                }


                try {
                    const response =
                        await fetch(
                        	"/api/chat/rooms/" + roomId + "/leave",
                            {
                                method: "POST",
                                credentials: "same-origin"
                            }
                        );

                    if (!response.ok) {
                        throw new Error("채팅방 나가기 실패");
                    }

                    disconnectWebSocket();
                    alert("채팅방에서 나갔습니다.");

                    history.back();

                } catch (error) {
                    console.error(error);
                    alert("채팅방 나가기 중 오류가 발생했습니다.");
                }

            }
        );


    /*
     * ==========================
     * WebSocket 연결 종료
     * ==========================
     */

    function disconnectWebSocket() {

        if (socket && socket.readyState === WebSocket.OPEN) {

            const disconnectFrame =
                "DISCONNECT\n" +
                "\n" +
                "\0";

            socket.send(disconnectFrame);
            socket.close();
        }
    }


    /*
     * 페이지를 닫는 것은
     * '채팅방 나가기'가 아님.
     *
     * WebSocket만 종료한다.
     */

    window.addEventListener("beforeunload",
        function() {
            if (socket) {
                socket.close();
            }
        }
    );

</script>

</main>
</body>
</html>