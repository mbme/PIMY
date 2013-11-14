"use strict";

define([
    'angular'
], function (angular) {
    var viewer = angular.module("RecordsViewer", []);

    viewer.controller('RecordsViewerCtrl', function ($rootScope, $scope, $log, $timeout, $element) {
        $scope.isNew = function () {
            return !$scope.record || $scope.record.id < 0;
        };

        $scope.isEditMode = function () {
            return $rootScope.pimy_options.viewer_edit_mode;
        };

        var initialized = false;

        $scope.isInitialized = function () {
            return initialized;
        };

        $scope.delete = function () {
            $log.debug('Deleting record {}', $scope.record.id);
        };

        var cleanUp = $rootScope.$on('rec-viewer:update', function (event, record) {
            $log.debug('previewing record {}', record.id);
            $scope.record = record;
            $scope.text = record.text;

            initialized = true;

            //DOM will be updated after next $digest, so we should trigger
            //scrollable update event after it too
            $timeout(function () {
                $rootScope.$emit('scrollable:update', $element, true);
            });
        });

        $scope.$on('$destroy', function () {
            $log.debug('destroying record viewer');
            cleanUp();
        });

        $rootScope.$emit('rec-viewer:ready');
    });

    return viewer;
});