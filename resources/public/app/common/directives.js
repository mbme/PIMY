"use strict";

define([
    '../pimy'
], function (app) {

    //opens specified address after click on element; attribute only
    var addrName = 'addr';
    app.directive(addrName, function ($location) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var clickListener = function () {
                    scope.$apply(function () {
                        $location.url(attrs[addrName]);
                    });
                };
                element.on('click', clickListener);

                scope.$on('$destroy', function () {
                    element.off('click', null, clickListener);
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