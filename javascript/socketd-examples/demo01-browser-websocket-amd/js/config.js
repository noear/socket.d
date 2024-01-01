requirejs.config({
    baseUrl : "js",
    paths : {
        "socketd" : "socketd",
    },
    callback() {
        requirejs(['socketd'], () => {
            main();
        });
    }
})


