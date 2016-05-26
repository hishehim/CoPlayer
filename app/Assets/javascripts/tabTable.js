function openTab(e, tabID) {
    var i, tabs, tabLinks;

    $(".tabContainer").each(function(i,tc) {
        $(tc).hide();
    });
    tabs = document.getElementsByClassName("tabContainer");

    for (i = 0; i < tabs.length; i++) {
        tabs[i].style.display = "none";
    }

    tabLinks = document.getElementsByClassName("tabLink");

    for (i = 0; i < tabLinks.length; i++) {
        tabLinks[i].className = tabLinks[i].className.replace(" active", "");
    }

    document.getElementById(tabID).style.display="block";
    e.currentTarget.className += " active";
}