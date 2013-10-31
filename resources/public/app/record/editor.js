"use strict";

var editor = angular.module("RecordsEditor", ["RecordUtils"]);


editor.controller('RecordsEditorCtrl', function ($scope, $rootScope, $log, $element, RecordsService) {

    var prepareTags = function (rec) {
        var resp = _.clone(rec);
        resp.tags = _.map(rec.tags.split(','), $.trim);
        return resp;
    };

    $scope.record = {
        id: '-1',
        title: 'asdf',
        text: 'asdf',
        tags: 'asdf1, 123'
    };

    $scope.$watch('record', function (newRec) {
        $rootScope.$broadcast('update-records-viewer', newRec);
    }, true);

    $scope.save = function () {
        //todo implement
        $log.debug('saving...');
        RecordsService.post(prepareTags($scope.record));
    };

    var el = $($element);

    var buttonBar = $('.button-bar', el);
    buttonBar.css('left', el.width() - 20);

    var mousePos = { y: -1, x: -1 };
    var buttonBarTimer;

    el.on('mousemove', function (e) {
        //from EpicEditor
        // Here we check if the mouse has moves more than 5px in any direction before triggering the mousemove code
        // we do this for 2 reasons:
        // 1. On Mac OS X lion when you scroll and it does the iOS like "jump" when it hits the top/bottom of the page itll fire off
        //    a mousemove of a few pixels depending on how hard you scroll
        // 2. We give a slight buffer to the user in case he barely touches his touchpad or mouse and not trigger the UI
        if (Math.abs(mousePos.y - e.pageY) >= 5 || Math.abs(mousePos.x - e.pageX) >= 5) {
            buttonBar.show();
            if (buttonBarTimer) {
                clearTimeout(buttonBarTimer);
            }

            buttonBarTimer = window.setTimeout(function () {
                buttonBar.hide();
            }, 1000);
        }

        mousePos = { y: e.pageY, x: e.pageX };
    });

    buttonBar.on('mouseover', function () {
        if (buttonBarTimer) {
            clearTimeout(buttonBarTimer);
        }
    });

});