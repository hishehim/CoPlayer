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

function createPlaylistContainer(playlist){
    var playlistContainer = document.createElement("div");
    playlistContainer.id = "playlist-" + playlist.id;
    playlistContainer.style.outline = "1px solid #333333";
    var jsr = jsRouter.controllers.Playlists.getByUID(playlist.id);

    playlistContainer.appendChild(
        createHyperLink(playlist.title, jsr.url));

    playlistContainer.appendChild(createTextDiv(playlist.size.toString().concat(" links")));
    return playlistContainer;
}

function loadPL(url, container){
    url.ajax({
        dataType: "json",
        success: function(data) {
             var listView = document.getElementById(container);
             // loop through json array and append it to the main container
             $.each(data, function(i, obj) {
                 if (obj !== undefined || obj !== null) {
                     listView.appendChild(createPlaylistContainer(obj));
                 }
             });
             console.log(data);
             }
        }).fail(function(data, status, err) {
            alert("Failed to fetch playlist data");
            console.error("getJson failed: " + status + " error: " + err);
        });
}