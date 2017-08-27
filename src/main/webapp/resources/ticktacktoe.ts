declare var angular;
 var socket: WebSocket;
$(document).ready(() => {
   socket = new WebSocket("ws://localhost:8100/TickTackToeWebClient/connection");
    socket.onmessage = (data) => {
        window.alert(data.data);
    };
    socket.send({
        "command":"join",
        "username":"Przegladarka"
    });
});

var APP = angular.module('aplikacja', []);
APP.controller("BoardEvents", function ($scope) {

    $scope.getElement = function (element) {
       socket.send({
           "field":element.target.getAttribute("data-id"),
       });
    }
});