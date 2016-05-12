
// Click run to begin the lesson


function getDomainName(d) {
    if (d.match(/youtube/i) || d.match(/youtu.be/i)) {
        return "youtube";
    }
    else if (d.match(/soundcloud/i)) {
        return "soundcloud";
    }
    else {
        return null;
    }
}


function extractDomain(url) {
    var domain;
    //find & remove protocol (http, ftp, etc.) and get domain
    if (url.indexOf("://") > -1) {
        domain = url.split('/')[2];
    }
    else {
        domain = url.split('/')[0];
    }

    //find & remove port number
    domain = domain.split(':')[0];

    return getDomainName(domain);
}

function extractYouTubeID(url) {
    var regEx = /.*(?:youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=)([^#\&\?]*).*/;
    var matches = url.match(regEx);
    return matches[1];
}

tyurl = "https://www.youtube.com/watch?v=9DW8oyfqZSc";
scurl = "https://soundcloud.com/blevebrown/payroll-feat-jimmy-2shoes";

tyurls = [
    "http://www.youtube.com/watch?v=0zM3nApSvMg&feature=feedrec_grec_index",
    "http://www.youtube.com/user/IngridMichaelsonVEVO#p/a/u/1/0zM3nApSvMg",
    "http://www.youtube.com/v/0zM3nApSvMg?fs=1&amp;hl=en_US&amp;rel=0",
    "http://www.youtube.com/watch?v=0zM3nApSvMg#t=0m10s",
    "http://www.youtube.com/embed/0zM3nApSvMg?rel=0",
    "http://www.youtube.com/watch?v=0zM3nApSvMg",
    "http://youtu.be/0zM3nApSvMg"
    ];

for (i = 0; i < tyurls.length; i++) {
    console.log(extractYouTubeID(tyurls[i]));
}

