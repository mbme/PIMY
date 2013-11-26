// Because sometimes you need to style the cursor's line.

(function () {
    "use strict";
    var activeLineClass = "CodeMirror-activeline";

    CodeMirror.defineOption("styleActiveLine", false, function (cm, val, old) {
        var prev = old && old !== CodeMirror.Init;
        if (val && !prev) {
            updateActiveLine(cm);
            cm.on("cursorActivity", updateActiveLine);
        } else if (!val && prev) {
            cm.off("cursorActivity", updateActiveLine);
            clearActiveLine(cm);
            delete cm.state.activeLine;
        }
    });

    var clearActiveLine = function (cm) {
        if ("activeLine" in cm.state) {
            cm.removeLineClass(cm.state.activeLine, "text", activeLineClass);
        }
    };

    var updateActiveLine = function (cm) {
        var line = cm.getLineHandleVisualStart(cm.getCursor().line);
        if (cm.state.activeLine === line) {
            return;
        }
        clearActiveLine(cm);
        cm.addLineClass(line, "text", activeLineClass);
        cm.state.activeLine = line;
    };
})();
