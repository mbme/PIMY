"use strict";

define([
    '../pimy',
    'angular'
], function (app, angular) {

    //improved logger with interpolation
    app.config(function ($provide) {
        $provide.decorator('$log', function ($delegate) {
            var groupFinder = /(.)?\{\}/g;

            //creates logger with string interpolation
            var logInterpolation = function (log) {
                return function () {
                    if (arguments.length === 0) {
                        throw 'Zero log arguments for ' + log;
                    }
                    if (arguments.length === 1) {
                        log(arguments[0]);
                        return;
                    }
                    var paramsCount = arguments.length - 1;

                    var paramPos = 1;
                    var args = [].slice.call(arguments);
                    log(arguments[0].replace(groupFinder, function (str, firstChar) {
                        if (firstChar === '\\') {
                            return '{}';
                        }
                        if (paramPos > paramsCount) {
                            throw "To many params in template " + args[0];
                        }

                        var result = args[paramPos];
                        if (angular.isObject(result)) {
                            result = JSON.stringify(result);
                        }

                        paramPos += 1;

                        return firstChar ? firstChar + result : result;
                    }));
                };
            };

            $delegate.log = logInterpolation($delegate.log);
            $delegate.debug = $delegate.log;
            $delegate.info = logInterpolation($delegate.info);
            $delegate.warn = logInterpolation($delegate.warn);

            return $delegate;
        });
    });

    return app;
});