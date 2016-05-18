
createPlaylistContainer = function (playlist){
    var playlistContainer = document.createElement("a");
    playlistContainer.className = "list-group-item";
    playlistContainer.href = jsRouter.controllers.Playlists.play(playlist.id).url;
    var header = document.createElement('h4');
    header.className = "list-group-item-heading";
    $(header).text(playlist.title);
    var body = document.createElement('p');
    $(body).html("size: " + playlist.size);
    playlistContainer.appendChild(header);
    playlistContainer.appendChild(body);
    return playlistContainer;
};


$(document).ready(function(){
    loadPL(
        jsRouter.controllers.json.PlaylistJSON.getUsrPublicList(getQueryVariable("username")).url,
        'public-pl');
});