"use strict";

define([
    'angular',
    './utils'
], function (angular) {
    var list = angular.module("RecordsList", ["RecordUtils"]);

    list.controller('RecordsListCtrl', function ($rootScope, $scope, $log, RecordsService) {
        var setActive = function (record) {
            //if record is already selected then do nothing
            if ($scope.activeId === record.id) {
                return;
            }

            $scope.activeId = record.id;
            $rootScope.$broadcast('rec-viewer:update', record);
        };
        $scope.setActive = setActive;

        RecordsService.getList().then(function (records) {
            $log.debug('Loaded {} records', records.length);
            $scope.records = records;

            //select first item
            if (records.length > 0) {
                setActive(records[0]);
            }
        });
    });

    list.directive('sentinel', function () {
        return {
            restrict: 'A',
            link: function (scope) {
                scope.$watch('$last', function (val) {
                    if (val) {
                        scope.$emit('scrollable:update');
                    }
                });
            }
        };
    });

    return list;
});