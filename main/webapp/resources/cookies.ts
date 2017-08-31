declare var baseURL;


$.getScript(baseURL + "static/js.cookie.js").done(() => {
    loadId();
    loadUsername();
});

var Cookies = {
    id: null,
    username: null,

    getUsername: function () {
        return this.username;
    },
    getId: function () {
        return this.id;
    }
};

function loadId() {
    var id = Cookie.get("id");

    if (id == undefined) {
        id = Math.random.toString().substring(2, 7);
    }
    Cookies.id = id;
}

function loadUsername() {
    var username = Cookie.get("username");

    if (username == undefined) {
        username = "Anonymous";
    }
    Cookies.username = username;
}