
createPlaylistContainer = function (playlist){
    var playlistContainer = document.createElement("a");
    playlistContainer.className = "list-group-item";
    playlistContainer.href = jsRouter.controllers.Playlists.play(playlist.id).url;
    var header = document.createElement('h4');
    header.className = "list-group-item-heading";
    $(header).text(playlist.title);
    var body = document.createElement('p');
    $(body).html("size: " + playlist.size);
    var editBtn = document.createElement('a');
    editBtn.href = jsRouter.controllers.Playlists.edit(playlist.id).url;
    editBtn.className = "btn btn-default pull-right";
    $(editBtn).text("edit");
    body.appendChild(editBtn);
    playlistContainer.appendChild(header);
    playlistContainer.appendChild(body);
    return playlistContainer;
};

$(document).ready(function(){
    loadPL(
        jsRouter.controllers.json.PlaylistJSON.getUsrPublicList(getQueryVariable("username")).url,
        'public-pl');
/*    loadPL(
        jsRouter.controllers.json.PlaylistJSON.getUsrPrivateList().url,
        'private-pl');*/
    document.getElementById('public-pl').style.display="block";
});