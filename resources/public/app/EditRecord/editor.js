"use strict";

var editor = angular.module("EditRecord", []);


editor.controller('EditorCtrl', function ($scope, $log) {
    $scope.record = {
        id: '',
        title: '',
        text: '',
        tags: ''
    };

    $scope.save = function () {
        //todo implement
        $log.debug('saving...');
    };
});