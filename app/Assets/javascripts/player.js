

var playlist = [
    {type: 'youtube', id: 'XsTjI75uEUQ'},
    {type: 'soundcloud', id: 'relaxdaily/relaxing-piano-music-work_study_meditation' },
    {type: 'youtube', id: 'mOO5qRjVFLw'},
    {type: 'soundcloud', id: 'relaxdaily/relaxing-music-calm-studying-yoga' },
    {type: 'youtube', id: 'WZjFMj7OHTw'},
    {type: 'soundcloud', id: 'sai-ram-49/charlie-puth-see-you-again-piano-demo-version-without-wiz-khalifafurious-7-soundtrack' },
    {type: 'youtube', id: 'l1Q-cI4RE5s'},
    {type: 'soundcloud', id: 'didlybom/ryuichi-sakamoto-merry-christmas-mr-lawrence'},
];


var curIndex = 0;
var repeatAll = false;
/**
Wrapper player object used to control the current playing player
*/

var playingPlayer = {
    player: "",
    play: function() { return; },
    pause: function() { return; },
    stop: function() { return; },
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
        /* Callback for widget has initialized
         * this part will only be called once per page load
         */
        scReady = true;

        /* On audio ended, play next track */
        scplayer.bind(SC.Widget.Events.FINISH, function() {
            playNext();
        });
        /* On error, play next track */
        scplayer.bind(SC.Widget.Events.ERROR, function() {
            playNext();
        });

        scplayer.unbind(SC.Widget.Events.READY);
    });
}

function loadSC(url) {
    scplayer.load(url,{
              auto_play: true,
              liking: false,
              buying: false,
              sharing: true,
              download: false,
              show_artwork: false,
              show_comments: false,
              show_playcount: false,
              show_user: false,
              visual: true,
              hide_related: true,
        });
    playingPlayer.player = "sc";
    playingPlayer.play = function(){scplayer.play();};
    playingPlayer.pause = function(){scplayer.pause();};
    playingPlayer.stop = function(){scplayer.pause();};
    /*
    * When widget start playing (due to autoplay), check to ensure
    * user has not switch to another player.
    */
    scplayer.bind(SC.Widget.Events.PLAY, function() {
        if (playingPlayer.player != "sc") {
            /* if current player is not soundcloud, stop player */
            scplayer.pause();
        }
        scplayer.unbind(SC.Widget.Events.PLAY);
    });
}

function scPlay(url) {
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
      playerVars: {
        'autoplay': 1,
        'enablejsapi':1,
        'origin': document.domain,
        'rel': 0,
        'fs': 0
      },
      events: {
        'onReady': onPlayerInitialized,
        'onError': onPlayerError,
        'onStateChange': onPlayerStateChange
      }
    });
    $("#yt-player").hide();
}

function onPlayerError(event) {
    playNext();
}

function onPlayerReady(event) {
    if (playingPlayer.player == "yt") {
        event.target.playVideo();
    }
    //event.target.playVideo();
}

// play is ready to take api calls
function onPlayerInitialized(event) {
    ytReady = true;
    event.target.removeEventListener("onReady", "onPlayerInitialized");
    event.target.addEventListener("onReady", "onPlayerReady");
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
    ytPlayer.loadVideoById(id);
    playingPlayer.player = "yt";
    playingPlayer.play = function(){ytPlayer.playVideo();};
    playingPlayer.pause = function(){ytPlayer.pauseVideo();};
    playingPlayer.stop = function(){ytPlayer.stopVideo();};
    showPlayer($('#yt-player'));
}

function play(index) {
    if (repeatAll) {
        index = (index + playlist.length) % playlist.length;
    }
    if (index < playlist.length && index >= 0) {

        playingPlayer.stop();
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

function toggleRepeatAll() {
    repeatAll = !(repeatAll);
}

function beginPlaylist() {
    if (ytReady === false || scReady === false) {
        /* wait for all players to be ready */
        setTimeout(beginPlaylist, 100);
    } else {
        $("#player-nav").show();
        curIndex = 0;
        play(0);
    }
}

function populate() {
    var trackList = document.getElementById("track-list");
    $(playlist).each(function(i,item) {
        var w = document.createElement('li');
        w.className = "playlist-item-wrapper";
        var a = document.createElement('a');
        $(a).click(function(){play(i);});
        a.className = "playlist-item";
        a.href = "#";
        a.appendChild(document.createTextNode(item.type));
        a.appendChild(document.createElement('br'));
        a.appendChild(document.createTextNode(item.id));
        w.appendChild(a);
        trackList.appendChild(w);
    });
}


$(document).ready(function(){
    $("#player-nav").hide();
    populate();
    initializeYT();
    initializeSC();
    beginPlaylist();
});