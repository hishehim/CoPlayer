$(document).ready(function() {
    // loads the playlist
    function loadPlaylist() {
        jsRouter.controllers.api.PlaylistAPI.getPublicPlaylist().ajax({
            dataType: "json",
            success: function(data) {
                             //$.getJSON("/api/playlist/public", function(data) {
                                 /*$.each(data, function(i, list) {
                                     $("#testloading").append(list.title);
                                 });*/
                                 $("#playlist-main-list").html(JSON.stringify(data));
                                 console.log(data);
                             }
        }).fail(function(data, status, err) {
                      alert("Unable to fetch playlist data");
                      console.error("getJson failed: " + status + " error: " + err);
                  });
    }
    loadPlaylist();
});