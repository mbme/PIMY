"use strict";

define([
    'angular',
    'jquery',
    'lodash',
    './utils',
    '../common/fixed'
], function (angular, $, _) {
    var editor = angular.module("RecordsEditor", ["RecordUtils"]);

    editor.controller('RecordsEditorCtrl', function ($scope, $rootScope, $log, $element, RecordsService) {
        $scope.record = {
            id: '-1',
            title: 'title asdf',
            text: 'some text asdf',
            tags: 'asdf1, 123'
        };

        var notifyViewer = function (rec) {
            $rootScope.$emit('rec-viewer:update', rec);
        };

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

    return editor;
});