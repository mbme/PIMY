"use strict";

define([
    'angular'
], function (angular) {
    var viewer = angular.module("RecordsViewer", []);

    viewer.controller('RecordsViewerCtrl', function ($rootScope, $scope, $log, $timeout) {
        $scope.isNew = function () {
            return !$scope.record || $scope.record.id < 0;
        };

        $rootScope.$on('rec-viewer:update', function (event, record) {
            $log.debug('previewing record {}', record.id);
            $scope.record = record;
            $scope.text = record.text;

            //DOM will be updated after next $digest, so we should trigger
            //scrollable update event after it too
            $timeout(function () {
                $scope.$emit('scrollable:update');
            });
        });

        $rootScope.$broadcast('rec-viewer:ready');
    });

    return viewer;
});