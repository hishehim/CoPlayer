/* Function to create the container that holds each playlist data in index page */
function createListContainer(playlist) {
    var playlistContainer = document.createElement("a");
    playlistContainer.className = "list-group-item";
    playlistContainer.href = jsRouter.controllers.Playlists.play(playlist.id).url;
    var header = document.createElement('h4');
    header.className = "list-group-item-heading";
    $(header).text(playlist.title);
    var body = document.createElement('p');
    $(body).html(playlist.owner + "<br />size: " + playlist.size);
    playlistContainer.appendChild(header);
    playlistContainer.appendChild(body);
    return playlistContainer;
}

/* Function to populate the list of playlists */
function loadPlaylist() {
    /* For AJAX call documentation:
    * http://api.jquery.com/jquery.ajax/
    * See controllers.Application.javascriptRoutes
    */
    $.ajax({
        dataType: "json",
        url: jsRouter.controllers.json.PlaylistJSON.getPublicPlaylist().url,
        // success is the callback function for successful ajax call
        success: function(data) {
                // get target container to populate the list
                var listView = document.getElementById("playlist-main-list");
                // loop through json array and append it to the main container
                $.each(data, function(i, obj) {
                if (obj !== undefined || obj !== null) {
                    listView.appendChild(createListContainer(obj));
                }
                });
            }
       }).fail(function(data, status, err) {
               alert("Unable to fetch playlist data");
               console.error("getJson failed: " + status + " error: " + err);
       });
}

$(document).ready(function() {
    loadPlaylist();
});