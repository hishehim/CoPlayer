/* Create simple div element with text in it */
function createTextDiv(text) {
    var textBox = document.createElement("div");
    textBox.appendChild(document.createTextNode(text));
    return textBox;
}

/* Create simple div with anchor embedded inside
* May be changed to simple anchor element in the future
*/
function createHyperLink(text, link, type) {
    var rootDiv = document.createElement("div");
    var anchor = document.createElement("A");
    anchor.appendChild(document.createTextNode(text));
    anchor.href = link;
    rootDiv.appendChild(anchor);
    return rootDiv;
}

/* Function to create the container that holds each playlist data in index page */
function createListContainer(playlist) {
    var playlistContainer = document.createElement("div");
    playlistContainer.id = "playlist-" + playlist.id;
    playlistContainer.style.outline = "1px solid #333333";
    var jsr = jsRouter.controllers.Playlists.getById(playlist.id);

    playlistContainer.appendChild(
        createHyperLink(playlist.title, jsr.url));

    jsr = jsRouter.controllers.UserProfile.showProfile(playlist.owner);
    playlistContainer.appendChild(
        createHyperLink(playlist.owner, jsr.url));

    playlistContainer.appendChild(createTextDiv(playlist.size.toString().concat(" links")));
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
            console.log(data);
            }
       }).fail(function(data, status, err) {
               alert("Unable to fetch playlist data");
               console.error("getJson failed: " + status + " error: " + err);
       });
}

$(document).ready(function() {
   loadPlaylist();
});