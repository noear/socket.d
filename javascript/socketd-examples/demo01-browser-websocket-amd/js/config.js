requirejs.config({
    baseUrl : "js",
    paths : {
        "socketd" : "socketd",
    },
    callback() {
        require(['socketd'], () => {
            main();
        });
    }
})


