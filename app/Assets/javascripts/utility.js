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

function getQueryVariable(variable)
{
       var query = window.location.search.substring(1);
       var vars = query.split("&");
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return pair[1];}
       }
       return(false);
}

var createPlaylistContainer = function(){ return; };

function loadPL(url, listContainer){
    $.ajax({
        dataType: "json",
        url: url,
        success: function(data) {
             var listView = document.getElementById(listContainer);
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