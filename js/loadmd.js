function loadmd(path, divId) {
    console.log(path);
    console.log(divId);
    console.log($('#' + divId));
    var md_content;
    $("#" + divId).load(path);
    $.get(path, function (result) {
        var html_content = markdown.toHTML(result);
        console.log(html_content);
        $('#' + divId).html(unescapeHtml(html_content), 'gruber');
    });
}

function unescapeHtml(safe) {
    return safe.replace(/&amp;/g, '&')
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&quot;/g, '"')
        .replace(/&#039;/g, "'");
}
var _getAllFilesFromFolder = function(dir) {

    var filesystem = require("fs");
    var results = [];

    filesystem.readdirSync(dir).forEach(function(file) {

        file = dir+'/'+file;
        var stat = filesystem.statSync(file);

        if (stat && stat.isDirectory()) {
            results = results.concat(_getAllFilesFromFolder(file))
        } else results.push(file);

    });

    return results;
};