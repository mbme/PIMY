"use strict";

var editor = angular.module("RecordsEditor", ["RecordUtils"]);


editor.controller('RecordsEditorCtrl', function ($scope, $rootScope, $log, RecordsService) {

    var prepareTags = function (rec) {
        var resp = _.clone(rec);
        resp.tags = _.map(rec.tags.split(','), $.trim);
        return resp;
    };

    $scope.record = {
        id: '-1',
        title: 'asdf',
        text: 'asdf',
        tags: 'asdf1, 123'
    };

    $scope.$watch('record', function (newRec) {
        $rootScope.$broadcast('update-records-viewer', newRec);
    }, true);

    $scope.save = function () {
        //todo implement
        $log.debug('saving...');
        RecordsService.post(prepareTags($scope.record));
    };
});