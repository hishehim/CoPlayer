function createTextDiv(text) {
    var textBox = document.createElement("div");
    textBox.appendChild(document.createTextNode(text));
    return textBox;
}

function createHyperLink(text, link, type) {
    var rootDiv = document.createElement("div");
    var anchor = document.createElement("A");
    anchor.appendChild(document.createTextNode(text));
    anchor.href = link;
    rootDiv.appendChild(anchor);
    return rootDiv;
}

function createListContainer(playlist) {
    var playlistContainer = document.createElement("div");
    playlistContainer.id = "playlist-" + playlist.id;
    playlistContainer.style.outline = "1px solid #333333";
    var jsr = jsRouter.controllers.Playlists.getByUID(playlist.id);

    playlistContainer.appendChild(
        createHyperLink(playlist.title, jsr.url));

    jsr = jsRouter.controllers.UserProfile.showProfile(playlist.ownerid);
    playlistContainer.appendChild(
        createHyperLink(playlist.owner, jsr.url));

    playlistContainer.appendChild(createTextDiv(playlist.size.toString().concat(" links")));
/*    for (var key in playlist) {
        var para = document.createElement("P");
        var text = document.createTextNode(key + ': ' + playlist[key]);
        para.appendChild(text);
        playlistContainer.appendChild(para);
    }*/
    return playlistContainer;
}

function loadPlaylist() {
    /* For AJAX call documentation:
    * http://api.jquery.com/jquery.ajax/
    * See controllers.Application.javascriptRoutes
    */
    jsRouter.controllers.json.PlaylistJSON.getPublicPlaylist().ajax(
        {
        dataType: "json",
        success: function(data) {
             // get target container to populate the list
             var listView = document.getElementById("playlist-main-list");
            // loop through json array and append it to the main container
            $.each(data, function(i, obj) {
               listView.appendChild(createListContainer(obj));
            });
            console.log(data);
            }
       }).fail(function(data, status, err) {
               alert("Unable to fetch playlist data");
               console.error("getJson failed: " + status + " error: " + err);
       });
}

$(document).ready(function() {
   // loads the playlist
   loadPlaylist();
});