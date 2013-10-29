"use strict";

var list = angular.module("RecordsList", ["RecordUtils"]);


list.controller('RecordsListCtrl', function ($rootScope, $scope, $log, RecordsService) {
    var setActive = function (record) {
        $scope.activeId = record.id;
        $rootScope.$broadcast('update-records-viewer', record);
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
                    scope.$emit('update-scrollable');
                }
            });
        }
    };
});