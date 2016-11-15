window.jsbridgeInited = false;


function onBridgeInitCompleteCallback(){
/*
    window.enabled = true;
    window.onscroll=function(){
        var top =  document.documentElement.scrollTop || document.body.scrollTop;
        if(!window.enabled && 0 == top){
            window.enabled = true;
            window.WebViewJavascriptBridge.callHandler("pageListener",
                    {message:"top", result:'true'},function(response){
                });
        }else if(window.enabled
            && 0 != top){
            window.enabled = false;
            window.WebViewJavascriptBridge.callHandler("pageListener",
                {message:"top", result:'false'},function(response){
            });
        }
    }
    */
    if(window.location.href.indexOf("uploadPage")>0)
    	uploadMenu('.work-ul', 1);
}

function connectWebViewJavascriptBridge(callback) {
    if (window.WebViewJavascriptBridge) {
        callback(WebViewJavascriptBridge)
        if(!window.jsbridgeInited) {
            window.jsbridgeInited = true;
            onBridgeInitCompleteCallback();
        }
    } else {
        document.addEventListener(
            'WebViewJavascriptBridgeReady'
            , function() {
                callback(WebViewJavascriptBridge)
                if(!window.jsbridgeInited) {
                    window.jsbridgeInited = true;
                    onBridgeInitCompleteCallback();
                }
            },
            false
        );
    }
}

function onKeyBoardShow(bottom) {
/*
    var diff = ($('input[type=text]:focus').offset().top - bottom) + 50;
    if(diff > 0) {
    */
    var diff = 300;
    $('body').css("top", (diff * -1) + "px");

   // }
};

function onKeyBoardHide() {
    $('body').css("top", "0px");
};


connectWebViewJavascriptBridge(function(bridge) {
    bridge.init(function(message, responseCallback) {
        console.log('JS got a message', message);
        //TODO upgrade
        var data = {
            'Javascript Responds': 'Wee!'
        };
        console.log('JS responding with', data);
        responseCallback(data);
    });

    bridge.registerHandler("functionInJs", function(data, responseCallback) {
        document.getElementById("show").innerHTML = ("data from Java: = " + data);
        var responseData = "Javascript Says Right back aka!";
        responseCallback(responseData);
    });
})
