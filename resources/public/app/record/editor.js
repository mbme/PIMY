"use strict";

var editor = angular.module("RecordEditor", ["restangular"]);


editor.controller('RecordEditorCtrl', function ($scope, $log, Restangular) {
    var records = Restangular.all('records');

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
        records.post(prepareTags($scope.record));
    };
});