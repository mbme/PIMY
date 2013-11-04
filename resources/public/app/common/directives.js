"use strict";

define([
    '../pimy'
], function (app) {

    //opens specified address after click on element; attribute only
    var addrName = 'addr';
    app.directive(addrName, function () {
        return {
            restrict: 'A',
            controller: function ($element, $attrs, $location, $scope) {
                $element.on('click', function () {
                    $scope.$apply(function () {
                        $location.url($attrs[addrName]);
                    });
                });
            }
        };
    });

    //bootstrap icon
    app.directive('icon', function () {
        return {
            restrict: 'E',
            template: '<i class="glyphicon glyphicon-{{ type }}"></i>',
            replace: true,
            scope: {
                type: '@'
            }
        };
    });

    return app;
});