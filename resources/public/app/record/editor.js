"use strict";

var editor = angular.module("RecordsEditor", ["RecordUtils"]);


editor.controller('RecordsEditorCtrl', function ($scope, $rootScope, $log, $element, RecordsService) {
    $scope.record = {
        id: '-1',
        title: 'asdf',
        text: 'asdf',
        tags: 'asdf1, 123'
    };

    var notifyViewer = function (rec) {
        $rootScope.$broadcast('rec-viewer:update', rec);
    };

    var textarea = $element.find('textarea')[0];
    var codeMirror = window.CodeMirror.fromTextArea(textarea, {
        mode: 'markdown',
        lineWrapping: true,
        lineNumbers: false,
        viewportMargin: Infinity,
        styleActiveLine: true
    });
    codeMirror.on('change', function () {
        $scope.$apply(function () {
            $scope.record.text = codeMirror.getValue();
        });
    });

    $scope.$watch('record', notifyViewer, true);

    $rootScope.$on('rec-viewer:ready', function () {
        notifyViewer($scope.record);
    });

    var prepareTags = function (rec) {
        var resp = _.clone(rec);
        resp.tags = _.map(rec.tags.split(','), $.trim);
        return resp;
    };
    $scope.save = function () {
        $log.debug('saving...');
        RecordsService.post(prepareTags($scope.record));
    };
});