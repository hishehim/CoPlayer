

/* soundcloud widget control */
var scplayer;
function genSCLink(id) { return "https://soundcloud.com/".concat(id); }

function initalizeSC(){
    var widgetIframe = document.getElementById('sc-player');
    scplayer = SC.Widget(widgetIframe);
    $('#sc-player').hide();
}

function loadSC(url) {
    scplayer.load(url,{
          show_artwork: false,
          auto_play: true,
          liking: false,
          buying: false,
          download: false,
          show_comments: false,
          show_playcount: false,
    });
    scplayer.bind(SC.Widget.Events.FINISH, function() {
        $('#sc-player').hide();
        scplayer.pause();
        ytPlay('0Bmhjf0rKe8');
    });
}

function scPlay(url) {
    $('#sc-player').show();
    loadSC(genSCLink(url));
}


/* youtube player control */
function initalizeYT() {
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}

var ytPlayer;
var ytReady = false;
function onYouTubePlayerAPIReady() {
    ytPlayer = new YT.Player('yt-player', {
        width: '100%',
        height: '100%',
        playerVars: {
            //fs: 0,
            rel: 0,
        },
        event: {
            "onReady": onPlayerReady
        }
     });
    $('#yt-player').hide();
}

// play is ready to take api calls
function onPlayerReady(event) {
    ytReady = true;
}

// when video ends
function onPlayerStateChange(event) {
    switch(event.data) {
        case 0:
            ytPlayer.stopVideo();
            $('#yt-player').hide();
            scPlay("sai-ram-49/charlie-puth-see-you-again-piano-demo-version-without-wiz-khalifafurious-7-soundtrack");
            break;
    }
}

function ytPlay(id) {
    if (ytReady === false) {
        setTimeout(function(){ytPlay(id);}, 100);
    }
    $('#yt-player').show();
    ytPlayer.addEventListener('onReady', onPlayerReady);
    ytPlayer.addEventListener('onStateChange', onPlayerStateChange);
    ytPlayer.loadVideoById(id);
}

function next() {

}

function prev() {

}

$(document).ready(function(){
    initalizeYT();
    initalizeSC();
    ytPlay('0Bmhjf0rKe8');
    //scPlay("sai-ram-49/charlie-puth-see-you-again-piano-demo-version-without-wiz-khalifafurious-7-soundtrack");
});