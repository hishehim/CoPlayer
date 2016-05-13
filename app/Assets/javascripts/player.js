

var playlist = [
    {type: 'youtube', id: 'yVnHLn1Uapc'},
    {type: 'soundcloud', id: 'relaxdaily/relaxing-piano-music-work_study_meditation' },
    {type: 'youtube', id: '0Bmhjf0rKe8'},
    {type: 'soundcloud', id: 'relaxdaily/relaxing-music-calm-studying-yoga' },
    {type: 'youtube', id: 'YjA8ENHSmxY'},
    {type: 'soundcloud', id: 'sai-ram-49/charlie-puth-see-you-again-piano-demo-version-without-wiz-khalifafurious-7-soundtrack' },
];


var curIndex = 0;
var playingPlayer = {
    play: function(){ return; },
    pause: function(){ return; },
    stop: function(){ return; }
};
//var currFrame = null;

function showPlayer(player) {
    $(".player").each(function(i, p){
        $(p).hide();
    });
    player.show();
}

/* soundcloud widget control */
var scplayer;
var scReady = false;
function genSCLink(id) { return "https://soundcloud.com/".concat(id); }

function initializeSC(){
    var widgetIframe = document.getElementById('sc-player');
    scplayer = SC.Widget(widgetIframe);
    $('#sc-player').hide();
    scplayer.bind(SC.Widget.Events.READY, function() {
        scReady = true;
        scplayer.bind(SC.Widget.Events.FINISH, function() {
            //scplayer.pause();
            playNext();
        });
        scplayer.bind(SC.Widget.Events.ERROR, function() {
            playNext();
        });
    });
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
    playingPlayer.play = function(){scplayer.play();};
    playingPlayer.pause = function(){scplayer.pause();};
    playingPlayer.stop = function(){scplayer.pause();};
}

function scPlay(url) {
    playingPlayer.stop();
    showPlayer($('#sc-player'));
    loadSC(genSCLink(url));
}


/* youtube player control */
function initializeYT() {
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}

var ytPlayer;
var ytReady = false;

function onYouTubeIframeAPIReady() {
    ytPlayer = new YT.Player('yt-player', {
      height: '390',
      width: '640',
      playerVars: {
        'autoplay': 0,
        'enablejsapi':1,
        'origin': document.domain,
        'rel': 0,
        'fs': 0
      },
      events: {
        'onReady': onPlayerReady,
        'onError': onPlayerError,
        'onStateChange': onPlayerStateChange
      }
    });
    $("#yt-player").hide();
}

function onPlayerError(event) {
    alert("error");
}

// play is ready to take api calls
function onPlayerReady(event) {
    ytReady = true;
    event.target.removeEventListener("onReady", "onPlayerReady");
}

// when video ends
function onPlayerStateChange(event) {
    switch(event.data) {
        case 0:
            //event.target.stopVideo();
            playNext();
            break;
    }
}

function ytPlay(id) {
    playingPlayer.stop();
    showPlayer($('#yt-player'));
    playingPlayer.play = function(){ytPlayer.playVideo();};
    playingPlayer.pause = function(){ytPlayer.pauseVideo();};
    playingPlayer.stop = function(){ytPlayer.stopVideo();};
    ytPlayer.loadVideoById(id);
}

function play(index) {
    if (index < playlist.length) {
        switch (playlist[index].type) {
            case 'youtube':
                ytPlay(playlist[index].id);
                break;
            case 'soundcloud':
                scPlay(playlist[index].id);
                break;
            default:
                console.log(playlist[index]);
                return;
        }
        curIndex = index;
    }
}

function playPrev() {
    play(curIndex - 1);
}

function playNext() {
    play(curIndex + 1);
}

function beginPlaylist() {
    if (ytReady === false || scReady === false) {
        setTimeout(beginPlaylist, 100);
    } else {
        $("#player-nav").show();
        curIndex = 0;
        play(0);
    }
}

$(document).ready(function(){

        $("#player-nav").hide();
    initializeYT();
    initializeSC();
    beginPlaylist();
});