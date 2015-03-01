function sendSyncRequest() {
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200) {
            document.getElementById("loader").className = "";
            document.getElementById("sync-result").innerHTML = "Sync result: " + xmlHttp.responseText;
        }
    }
    xmlHttp.open("GET", "/sync", true);
    xmlHttp.send();
    document.getElementById("loader").className = "loader";
}