declare var baseURL;
declare var contextPath;

declare var Stomp;
declare var SockJS;



$(document).ready(() => {
    var key = $("meta[name=player_key]").attr("content");
    var username = $("meta[name=player_username]").attr("content");

    var webSocket = new SockJS(baseURL + "players_matcher");
    webSocket.onclose = () => {
        stomp.send("/app/quit", {}, key);
    };
    var stomp = Stomp.over(webSocket);

    stomp.connect({}, () => {
        stomp.subscribe("/queue/matching/" + key, (el) => {
            var response = JSON.parse(el.body);

            if ("gameId" in response || "gameid" in response) {
                window.location.replace(baseURL + "play/" + response.gameId);
            }
            else if (response.content == "is_connected") {
                stomp.send("/app/" + key + "/connected");
            }
        });


        stomp.send("/app/" + key + "/join", {}, username);
    });


});




