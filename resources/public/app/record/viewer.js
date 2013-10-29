"use strict";

var viewer = angular.module("RecordsViewer", []);

viewer.controller('RecordsViewerCtrl', function ($rootScope, $scope, $log) {
    $rootScope.$on('update-records-viewer', function (event, record) {
        $log.debug('previewing record {}', record.id);
        $scope.title = record.title;
        $scope.text = record.text;
    });
});