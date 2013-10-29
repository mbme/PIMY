"use strict";

var editor = angular.module("RecordEditor", ["RecordUtils"]);


editor.controller('RecordEditorCtrl', function ($scope, $log, RecordsService) {

    var prepareTags = function (rec) {
        var resp = _.clone(rec);
        resp.tags = _.map(rec.tags.split(','), $.trim);
        return resp;
    };

    $scope.record = {
        id: '',
        title: 'asdf',
        text: 'asdf',
        tags: 'asdf1, 123'
    };

    $scope.save = function () {
        //todo implement
        $log.debug('saving...');
        RecordsService.post(prepareTags($scope.record));
    };
});