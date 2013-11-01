"use strict";

var editor = angular.module("RecordsEditor", ["RecordUtils"]);


editor.controller('RecordsEditorCtrl', function ($scope, $rootScope, $log, $element, RecordsService) {

    var prepareTags = function (rec) {
        var resp = _.clone(rec);
        resp.tags = _.map(rec.tags.split(','), $.trim);
        return resp;
    };

    var notifyViewer = function (rec) {
        $rootScope.$broadcast('rec-viewer:update', rec);
    };

    $scope.record = {
        id: '-1',
        title: 'asdf',
        text: 'asdf',
        tags: 'asdf1, 123'
    };

    $scope.$watch('record', notifyViewer, true);

    $rootScope.$on('rec-viewer:ready', function () {
        notifyViewer($scope.record);
    });

    $scope.save = function () {
        //todo implement
        $log.debug('saving...');
        RecordsService.post(prepareTags($scope.record));
    };
});