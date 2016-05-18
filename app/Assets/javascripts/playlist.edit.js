function extractYouTubeID(url) {
    var regEx = /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/;
    var matches = url.match(regEx);
    return matches[1];
}

function setItemVal(id, src, title, author) {
    $("#src-url").val(id);
    $("#src-type").val(src);
    $("#src-title-lb").text(title);
    $("#src-author").text(author);
}

function clearItemVal() {
    $("#src-title-lb").text('');
    $("#src-author").text('');
    $("#src-url").val('');
    $("#src-type").val('');
}

function showError(msg) {
    $("#url-error").text(msg);
    $("#url-error").show();
}

function checkUrl() {
    clearItemVal();
    $("#preview-container").hide();
    $("#url-error").hide();
    $("#url-error").text('');
    var url = $("#input_url").val();
    var src;

    $.ajax({
        dataType: "json",
        url: "https://noembed.com/embed?url=" + url,
        success: function(data) {
            if ('error' in data) {
                showError("Supported providers are YOUTUBE and SOUNDCLOUD. Ensure url references only single item");
                return false;
            }
            console.log(data);
            switch(data.provider_name.toLowerCase()) {
                case "soundcloud":
                    var scRegex = /.com\/[a-zA-Z0-9-_]+\/[a-zA-Z0-9-_]+(\?|$)/;
                    var scId = scRegex.exec(data.url);
                    if (scId) {
                        scId = scId[0];
                        if (scId[scId.length-1] == '?') {
                            scId = scId.substring(5, scId.length - 1);
                        } else {
                            scId = scId.substring(5);
                        }
                        setItemVal(scId, data.provider_name, data.title, data.author_name);
                        $("#preview-container").show();
                    } else {
                        showError("Provided url does not link to single item");
                    }
                    break;
                case "youtube":
                    setItemVal(extractYouTubeID(data.url), data.provider_name, data.title, data.author_name);
                    $("#preview-container").show();
                    break;
                default:
                    $("#url-error").text(data.provider_name + " is currently not supported");
                    $("#url-error").show();
            }
        }
    }).fail(function(data, status, err) {
        alert("failed");
        console.error("getJson failed: " + status + " error: " + err);
        console.log(data);
    });
}