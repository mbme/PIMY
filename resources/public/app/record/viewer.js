"use strict";

var viewer = angular.module("RecordsViewer", []);

viewer.controller('RecordsViewerCtrl', function ($rootScope, $scope, $log) {
    $scope.isNew = function () {
        return !$scope.record || $scope.record.id < 0;
    };
    $rootScope.$on('update-records-viewer', function (event, record) {
        $log.debug('previewing record {}', record.id);
        $scope.record = record;
        $scope.text = record.text;
    });
});