declare var baseURL;
declare var contextPath;

declare var Stomp;
declare var SockJS;

const SOCKET_URL: string = baseURL + "playsocket";
var arena: Arena;
const CLASS_CROSS = "cross";
const CLASS_CIRCLE = "circle";
const CLASS_ACTIVE = "active";
const BOARD_ID: string = "board";
const TURN_TIME_VIEW = "time_left";

const JSON_TURN_TIMEOUT = "turn_timeout";
const JSON_GET_FIELD = "get_field";
const JSON_REQUEST_TYPE = "request";
const JSON_SHOW_BOARD = "show_board";
const JSON_INFO_TYPE = "info";
const JSON_GAME_END = "game_end";
const JSON_RESPONSE_TYPE = "response";
const JSON_REQUEST_IS_READY = "is_ready";
const JSON_REQUEST_IS_CONNECTED = "is_connected";
enum Events {
    OnMessageSend, OnMessageReceive
}

var clientId;
$(document).ready(el => {
    this.arena = new Arena();
    this.arena.start();
});

namespace Views {
    export class Point {
        constructor(public x: number, public y: number) { }
    }
}

namespace Control {

    var moveAllowed: boolean;

    export function isClientTurn() {
        return moveAllowed;
    }
    export function clientTurn() {
        moveAllowed = true;
    }

    export function notClientTurn() {
        moveAllowed = false;
    }
    export function isFieldLocked(BOARD_ID: string, x: number, y: number): boolean {
        var field = $("#" + BOARD_ID + " td[data-x=" + x + "][data-y=" + y + "]");
        return $(field).is("." + CLASS_ACTIVE);

    }


}

namespace Time {



    export enum TimeUnit {
        Minutes = 6000, Seconds = 1000, Milis = 1
    }


    abstract class Timer {
        protected actual: number;
        protected limit: number;
        protected ticker;
        private isStopped: boolean;
        protected speedInMilis;
        private unit: TimeUnit;
        private move;
        private onEnd: () => any;
        protected onUpdateEvent;

        start(speedInMilis: number, limit: number, from: number, unit: TimeUnit) {
            this.unit = unit;
            this.speedInMilis = speedInMilis * unit;
            this.actual = from * unit;
            this.limit = limit * unit;
            this.cycle();
            this.ticker = setInterval(() => this.cycle(), this.speedInMilis);
        }

        stop() {
            this.isStopped = true;

            if (this.ticker != null && this.ticker != undefined) {
                clearInterval(this.ticker);
            }
            this.onEnd();
        }

        abstract onStop();

        cycle() {
            if (this.isDone() && !this.isStopped) {
                this.update();
                this.onUpdateEvent(this.actual / this.unit);
            }
            else {
                this.stop();
            }
        }

        setOnUpdate(func: (time: number) => any) {
            this.onUpdateEvent = func;
        }

        setOnEnd(func: () => any) {
            this.onEnd = func;
        }

        abstract isDone(): boolean;
        abstract update();

    }

    class DeincrementingTimer extends Timer {

        onStop() {

        }
        isDone(): boolean {
            return this.actual > this.limit;
        }

        update() {
            this.actual -= this.speedInMilis;
        }
    }

    var turnTimer: Timer;

    export function startTurnTimer(timerID) {
        var max = $("meta[name=turn_time]").attr("content");
        turnTimer = new DeincrementingTimer();
        turnTimer.setOnEnd(() => $("#" + TURN_TIME_VIEW).html(""));
        turnTimer.setOnUpdate((time) => {
            var element = $("#" + TURN_TIME_VIEW);
            element.html(time.toString() + " sekund");
        });


        turnTimer.start(1, 0, parseInt(max) / TimeUnit.Seconds, TimeUnit.Seconds);
    }

    export function stopTurnTimer() {
        if (turnTimer != null && turnTimer != undefined) {
            turnTimer.stop();
        }
    }
}

namespace ViewGenerators {
    export const CLASS_CROSS_LINE = "cross-line";
    export const CLASS_CIRCLE = "circle";

    export namespace SVGGenerator {
        export function generateCross(size) {
            var svg = createCanvas("100%");
            var startX = 100 - parseInt(size) / 2 + "%";
            var startY = 100 - parseInt(size) / 2 + "%";
            var endX = parseInt(size) / 2 + "%";
            var endY = parseInt(size) / 2 + "%";
            var line1 = generateLine(endX, endY, startX, startY);
            line1.classList.add("cross-line");

            var line2 = generateLine(endX, startY, startX, endY);
            line2.classList.add(CLASS_CROSS_LINE);
            svg.appendChild(line1);
            svg.appendChild(line2);
            return svg;
        }

        function createCanvas(size) {
            var svg = document.createElement("svg");
            svg.setAttribute("width", size);
            svg.setAttribute("height", size);
            return svg;
        }
        function generateLine(x1, y1, x2, y2) {
            var line = document.createElement("line");
            line.setAttribute("x1", x1);
            line.setAttribute("y1", y1);
            line.setAttribute("x2", x2);
            line.setAttribute("y2", y2);
            return line;
        }


        export function generateCircle(size) {
            var sizeNum = parseInt(size);
            var svg = createCanvas("100%");
            var circle = document.createElement("circle");
            circle.setAttribute("cx", "50%");
            circle.setAttribute("cy", "50%");
            circle.setAttribute("r", sizeNum / 2 + "%");
            circle.classList.add(CLASS_CIRCLE);
            svg.appendChild(circle);
            return svg;
        }
    }

    export namespace BoardGenerator {
        var enableHoverable = true;

        export function disableHoverable() {
            $("#" + this.BOARD_ID + " td").removeClass("hoverable");
            enableHoverable = false;
        }
        export function createBoard(maxx: number, maxy: number, BOARD_ID: string, fieldOnClickEvent: (el) => any) {
            var table = document.createElement("table");

            for (let i = 0; i <= maxy; i++) {
                var row = this.createRow(i, maxx, BOARD_ID, fieldOnClickEvent);
                table.appendChild(row);
            }
            table.id = BOARD_ID;
            return table;
        }

        export function createRow(rowid: number, max: number, BOARD_ID: string, event: (el) => any): HTMLElement {
            var row = document.createElement("tr");

            for (let i = 0; i <= max; i++) {
                var column = document.createElement("td");
                var wrapper = document.createElement("div");

                if (enableHoverable) {
                    wrapper.classList.add("hoverable");
                }

                var gridType = new BoardDecorator.GridDrawer().getGridLayerCssClass(i, rowid, max);

                if (gridType.length > 0) {
                    wrapper.classList.add(gridType);
                }

                var anotherWrapper = document.createElement("div");
                anotherWrapper.className = "symbol-holder";
                wrapper.appendChild(anotherWrapper);

                column.appendChild(wrapper);
                column.setAttribute("data-x", i.toString());
                column.setAttribute("data-y", rowid.toString());
                column.onclick = event;
                row.appendChild(column);
            }
            return row;
        }
    }

    export namespace BoardDecorator {

        export class GridDrawer {


            getGridLayerCssClass(x, y, size) {
                var center: Views.Point = new Views.Point(size / 2, size / 2);
                var cssClass = "";

                if (x == center.x && y == center.y) {
                    cssClass = "tile_center";
                }
                else if (x == center.x) {
                    cssClass = "tile_center_vertical";
                }
                else if (y == center.y) {
                    cssClass = "tile_center_horizontal";
                }
                return cssClass;
            }
        }

        export function markWinningCombination(BOARD_ID, fields: Array<Views.Point>) {
            //set winning combinations by pink background;

            fields.forEach((el) => {
                var field = $("#" + BOARD_ID + " td[data-x=" + el.x + "][data-y=" + el.y + "]");
                field.addClass("win-combination-field");
            });


        }




    }

    export class BoardSelector {
        getFieldAt(x: number, y: number, BOARD_ID) {

            var field = $("#" + BOARD_ID + " td").filter((id, el) => el.getAttribute("data-x") == x.toString() && el.getAttribute("data-y") == y.toString());
            return field;
        }
    }

    export class StatusUpdater {
        viewHolder = document.getElementById("status");
        updateStatus(model) {
            this.viewHolder.innerHTML = model;
        }
    }
    export class BoardUpdater {

        updateBoard(boardModel, board): HTMLElement {
            for (let y = 0; y < boardModel.length; y++) {
                var row = boardModel[y];

                for (let x = 0; x < row.length; x++) {
                    let column = row[x];
                    let symbol;
                    let className;
                    if (column == "X") {
                        className = CLASS_ACTIVE + " " + CLASS_CROSS;
                        symbol = ViewGenerators.SVGGenerator.generateCross("50%");
                    }
                    else if (column == "O") {
                        className = CLASS_ACTIVE + " " + CLASS_CIRCLE;
                        symbol = ViewGenerators.SVGGenerator.generateCircle("50%");
                    }
                    else if (column == "?") {
                        className = "";
                    }
                    else {
                        return;
                    }
                    var viewField = new BoardSelector().getFieldAt(x, y, board.id);

                    if (symbol != undefined && symbol != null) {
                        viewField[0].firstElementChild.innerHTML = symbol.outerHTML;
                    }
                    viewField[0].className = className;

                }

            }

            return board;
        }
    }

}
class Message {
    title;
    content;
    appendix;

    constructor(title: string, content: string, appendix: {}) {
        this.title = title;
        this.content = content;
        this.appendix = appendix;
    }
}

interface Deliverer {
    send(message: Message);

}
class Arena implements Deliverer {

    webSocket;
    socket;
    allowedMove: boolean = false;
    observers: Array<Utils.EventObserver> = [];
    gameid = $('meta[name=gameid]').attr("content");
    boardCanvasId: string = "board_container";

    start() {
        this.adaptToMobile();
        clientId = $("meta[name=player_key").attr("content");

        var board = ViewGenerators.BoardGenerator.createBoard(2, 2, BOARD_ID, (el) => {
            var x = el.currentTarget.getAttribute("data-x");
            var y = el.currentTarget.getAttribute("data-y");
            if (!Control.isFieldLocked(BOARD_ID, x, y) && Control.isClientTurn()) {
                this.send(new Message(JSON_RESPONSE_TYPE, JSON_GET_FIELD, { x: x, y: y }));
            }

        });

        document.getElementById(this.boardCanvasId).replaceChild(board, document.getElementById(BOARD_ID));

        this.webSocket = new SockJS(SOCKET_URL);
        this.socket = Stomp.over(this.webSocket);
        this.socket.heartbeat.incoming = 0;
        var self = this;
        this.socket.connect({}, () => {
            $("#resign_button").off().on("click", () => {
                this.socket.send("/app/play/" + this.gameid + "/" + clientId + "/resign")
                document.location.replace(baseURL);
            });

            this.socket.subscribe("/queue/play/" + this.gameid + "/" + clientId, (el) => {
                var message: Message = JSON.parse(el.body);
                this.notifyObservers(Events.OnMessageReceive, message);
            });
            this.socket.send("/app/play/" + this.gameid + "/join/" + clientId, {}, "Adam");

        });


        this.addObserver(new Handlers.RequestsHandler(this));
        this.addObserver(new Handlers.InfoHandler(this));
        this.addObserver(new Handlers.StatusUpdater(this));
        this.addObserver(new Handlers.TurnTimeLeft(this));
        this.addObserver(new Handlers.ControlHandler(this));
    }

    private adaptToMobile() {
        if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
            ViewGenerators.BoardGenerator.disableHoverable();
        }
    }
    send(message: Message) {
        this.socket.send("/app/play/" + this.gameid + "/" + clientId + "/response", {}, JSON.stringify(message));
        this.notifyObservers(Events.OnMessageSend, message);
    }

    notifyObservers(event, data) {
        this.observers.forEach(el => { el.update(event, data) });
    }

    addObserver(observer: Utils.EventObserver) {
        this.observers.push(observer);
    }

    join() {
        this.createMessage(JSON_REQUEST_TYPE, "join", { username: $('meta[name=player_username]').attr("content"), gameid: $('meta[name=gameid]').attr("content") });
    }

    createMessage(type: string, content: string, appendix: {}) {
        this.webSocket.send(JSON.stringify({ type: type, content: content, appendix: appendix }));
    }
}

namespace Utils {
    export interface Observer {
        update();
    }

    export interface EventObserver {
        update(event: Events, data);
    }
    export interface DataObserver {
        update(data);
    }
}
namespace Handlers {
    export abstract class Handler implements Utils.EventObserver {
        deliverer: Deliverer;
        listeners: Array<Utils.DataObserver> = [];

        addListener(ob: Utils.DataObserver) {
            this.listeners.push(ob);
        }

        removeListener(ob) {
            this.listeners.splice(ob);
        }

        notifyAll(data) {
            this.listeners.forEach((el) => {
                el.update(data);
            });
        }

        handle(event: Events, message: Message) {
            this.work(event, message);
            this.notifyAll(message);
        }

        abstract work(event, message: Message);

        abstract accepts(message: Message): boolean;
        update(event, message: Message) {
            if (this.accepts(message)) {
                this.handle(event, message);
            }
        }

        constructor(deliverer: Deliverer) {
            this.deliverer = deliverer;
        }
    }


    export class TurnTimeLeft extends Handler {
        accepts(message: Message): boolean {
            return true;
        }

        work(event, message: Message) {
            // starts timer and show remaining time.

            if (message.title == JSON_REQUEST_TYPE && message.content == JSON_GET_FIELD) {
                Time.stopTurnTimer();
                Time.startTurnTimer(TURN_TIME_VIEW);
            }
            else if (message.content == JSON_TURN_TIMEOUT || event == Events.OnMessageSend) {
                Time.stopTurnTimer();
            }
        }
    }

    export class ControlHandler extends Handler {
        accepts(message: Message) {
            return message != null;
        }

        work(event, message: Message) {
            if (message.content == JSON_GET_FIELD) {
                Control.clientTurn();
            }
            else if (message.content == JSON_GAME_END || message.content == JSON_TURN_TIMEOUT || (event == Events.OnMessageSend && message.title == JSON_RESPONSE_TYPE)) {
                Control.notClientTurn();
            }

        }
    }
    export class StatusUpdater extends Handler {
        accepts(message: Message): boolean {
            return message != null;
        }
        work(event, message: Message) {

            var content = message.content;

            if (event == Events.OnMessageSend && message.content == JSON_GET_FIELD) {
                new ViewGenerators.StatusUpdater().updateStatus("Your opponnent's move...");
                return;
            }
            switch (content) {
                case JSON_GET_FIELD:
                    new ViewGenerators.StatusUpdater().updateStatus("Your move!");
                    break;

                case JSON_TURN_TIMEOUT:
                    new ViewGenerators.StatusUpdater().updateStatus("Your turn ended!<br>Your opponnent's move...");
                    break;

                case JSON_GAME_END:

                    var appendix;
                    if (message.appendix.winnerId == null || message.appendix.winnerId == "") {
                        appendix = "Remis!";
                    }
                    else if (message.appendix.winnerId != null && message.appendix.winnerId != "" && message.appendix.winnerId == clientId && message.appendix.winningLine == null) {
                        appendix = "Haha! Your opponent gave up!<br>You won!";
                    }
                    else {
                        ViewGenerators.BoardDecorator.markWinningCombination(BOARD_ID, message.appendix.winningLine);
                        appendix = message.appendix.winnerId == clientId ? "You won!" : "You lose...";
                    }

                    new ViewGenerators.StatusUpdater().updateStatus(appendix);
                    break;
            }
        }
    }
    export class RequestsHandler extends Handler {

        work(event, message: Message) {
            if (message.content == JSON_GET_FIELD) {
                this.handleGetMove(message);
                new ViewGenerators.StatusUpdater().updateStatus(message.content);
            }
            else if (message.content == JSON_REQUEST_IS_READY) {
                this.handleIsReady(message);
            }
            else if (message.content == JSON_REQUEST_IS_CONNECTED) {
                this.handleIsConnected(message);
            }
        }

        handleGetMove(message: Message) {
            arena.allowedMove = true;
        }

        handleIsReady(message: Message) {
            var msg: Message = new Message(JSON_RESPONSE_TYPE, JSON_REQUEST_IS_READY, null);
            this.deliverer.send(msg);
        }


        handleIsConnected(message: Message) {
            var msg: Message = new Message(JSON_RESPONSE_TYPE, JSON_REQUEST_IS_CONNECTED, null);
            this.deliverer.send(msg);
        }
        accepts(message: Message): boolean {
            return message.title == JSON_REQUEST_TYPE;
        }


    }

    export class InfoHandler extends Handler {
        work(event, message: Message) {

            if (message.content == JSON_TURN_TIMEOUT) {
                this.handleTurnTimeout(message);
            }
            else if (message.content == JSON_SHOW_BOARD) {
                this.handleShowBoard(message.appendix);
            }
        }


        accepts(message: Message): boolean {
            return message.title == JSON_INFO_TYPE;
        }

        handleShowBoard(board) {
            var boardView = document.getElementById(BOARD_ID);
            document.getElementById(arena.boardCanvasId).replaceChild(new ViewGenerators.BoardUpdater().updateBoard(board, boardView), boardView);
        }
        handleTurnTimeout(message: Message) {
            new ViewGenerators.StatusUpdater().updateStatus("Ops. Your turn ended!");
        }
    }
}
