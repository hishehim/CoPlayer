function extractYouTubeID(url) {
    var regEx = /^.*(?:(?:youtu\.be\/|v\/|vi\/|u\/\w\/|embed\/)|(?:(?:watch)?\?v(?:i)?=|\&v(?:i)?=))([^#\&\?]*).*/;
    var matches = url.match(regEx);
    return matches[1];
}

function setItemVal(data, id) {
    /* input fields */
    $("#src-url").val(id);
    $("#src-type").val(data.provider_name);
    $("#src-title").val(data.title);
    $("#src-author").val(data.author_name);
    $("#src-img-url").val(data.thumbnail_url);
    /* visible */
    $("#src-img").attr('src', data.thumbnail_url);
    $("#src-provider-lb").text(data.provider_name);
    $("#src-title-lb").text(data.title);
    $("#src-author-lb").text(data.author_name);
}

function clearItemVal() {
    /* visible */
    $("#src-title-lb").text('');
    $("#src-author").text('');
    $("#src-provider-lb").text('');
    $("#src-img").attr('src', '');

    /* input fields */
    $("#src-url").val('');
    $("#src-type").val('');
    $("#src-title").val('');
    $("#src-author").val('');
    $("#src-img-url").val('');
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
                        setItemVal(data, scId);
                        $("#preview-container").show();
                    } else {
                        showError("Provided url does not link to single item");
                    }
                    break;
                case "youtube":
                    setItemVal(data, extractYouTubeID(data.url));
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