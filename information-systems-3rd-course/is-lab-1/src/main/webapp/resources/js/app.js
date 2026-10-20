(function () {
    const path = window.location.pathname.replace(/\/[^/]*$/, "");
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    let socket;
    let reconnectTimer;

    function connect() {
        socket = new WebSocket(protocol + "//" + window.location.host + path + "/ws/routes");
        socket.onmessage = function () {
            window.clearTimeout(window.routeRefreshTimer);
            window.routeRefreshTimer = window.setTimeout(function () {
                if (!document.querySelector(".modal-layer")) window.location.reload();
            }, 700);
        };
        socket.onclose = function () {
            reconnectTimer = window.setTimeout(connect, 2500);
        };
        socket.onopen = function () { window.clearTimeout(reconnectTimer); };
    }

    if ("WebSocket" in window) connect();
})();
