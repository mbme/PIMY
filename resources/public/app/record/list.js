"use strict";

var list = angular.module("RecordsList", ["RecordUtils"]);


list.controller('RecordsListCtrl', function ($scope, $log, RecordsService) {
    RecordsService.getList().then(function (records) {
        $log.debug('Loaded {} records', records.length);
        $scope.records = records;
    });
});

list.directive('sentinel', function () {
    return {
        restrict: 'A',
        link: function (scope) {
            scope.$watch('$last', function (val) {
                if (val) {
                    scope.$emit('update-scrollable');
                }
            });
        }
    };
});