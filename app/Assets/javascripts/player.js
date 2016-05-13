

var playlist = [
    {type: 'youtube', id: 'yVnHLn1Uapc'},
    {type: 'soundcloud', id: 'relaxdaily/relaxing-piano-music-work_study_meditation' },
    {type: 'youtube', id: '0Bmhjf0rKe8'},
    {type: 'soundcloud', id: 'relaxdaily/relaxing-music-calm-studying-yoga' },
    {type: 'youtube', id: 'YjA8ENHSmxY'},
    {type: 'soundcloud', id: 'sai-ram-49/charlie-puth-see-you-again-piano-demo-version-without-wiz-khalifafurious-7-soundtrack' },
];


var curIndex;


/* soundcloud widget control */
var scplayer;
function genSCLink(id) { return "https://soundcloud.com/".concat(id); }

function initalizeSC(){
    var widgetIframe = document.getElementById('sc-player');
    scplayer = SC.Widget(widgetIframe);
    $('#sc-player').hide();
}

function loadSC(url) {
    console.log(url);
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
/*        playingPlayer.stop = scplayer.pause();
        playingPlayer.play = scplayer.play();
        playingPlayer.pause = scplayer.pause();*/
        scplayer.pause();
        playNext();
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

function onYouTubeIframeAPIReady() {
    ytPlayer = new YT.Player('yt-player', {
      height: '390',
      width: '640',
      playerVars: {
        'autoplay': 0,
        'enablejsapi':1,
        'origin': document.domain,
        'rel':0
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
            //ytPlayer.removeEventListener('onStateChange', onPlayerStateChange);
            event.target.stopVideo();
            //ytPlayer.stopVideo();
            //ytPlayer.addEventListener('onStateChange', onPlayerStateChange);
            $('#yt-player').hide();
            playNext();
            break;
    }
}

function ytPlay(id) {
    $('#yt-player').show();
    //playingPlayer.play = ytPlayer.playVideo();
    //playingPlayer.stop = ytPlayer.stopVideo();
    //playingPlayer.pause = ytPlayer.pause();
    //ytPlayer.addEventListener('onStateChange', onPlayerStateChange);
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

function playNext() {
    play(curIndex + 1);
}

function beginPlaylist() {
    if (ytReady === false) {
        setTimeout(beginPlaylist, 100);
    } else {
        curIndex = 0;
        play(0);
    }
}

$(document).ready(function(){
    initalizeYT();
    initalizeSC();
    beginPlaylist();
    //readyYouTubePlayer();
    //ytPlay('0Bmhjf0rKe8');
    //scPlay("sai-ram-49/charlie-puth-see-you-again-piano-demo-version-without-wiz-khalifafurious-7-soundtrack");
});