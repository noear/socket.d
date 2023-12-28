requirejs.config({
    baseUrl : "/socket.d/javascript/",
    paths : {
        "sd" : "socketd/dist/socketd",
    },
    callback() {
        require(['sd'], () => main());
    }
})
