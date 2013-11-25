"use strict";

define([
    '../pimy',
    'text!./modal.tpl.html',
    'lodash',
    'jquery'
], function (app, modalTpl, _, $) {
    var buttons = {
        ok: {
            text: 'Ok'
        },
        cancel: {
            text: 'Cancel'
        }
    };
    app.service('ModalService', function ($log, $q) {
        this.config = {
            visible: false,
            title: '',
            text: '',
            buttons: [],

            cleanUp: function () {
                this.visible = false;
                this.title = '';
                this.text = '';
                this.buttons.length = 0;
            }
        };

        var self = this;
        this.dialog = function (options) {
            $log.debug('showing dialog {}', options.title);
            var deferred = $q.defer();

            self.config.cleanUp();

            var doc = $(document);
            var escHandler = function (evt) {
                if (evt.keyCode === 27) { // if ESC
                    self.config.close();
                }
            };
            doc.on('keyup', escHandler);

            //add buttons
            _.each(options.buttons || ['ok', 'cancel'], function (item) {
                var newButton = buttons[item];

                //if we passed wrong button name
                if (!newButton) {
                    $log.error('Wrong button name passed: "{}"', item);
                    throw "wrong button name: " + item;
                }

                newButton.action = function () {
                    doc.off('keyup', escHandler);

                    var btn = 'btn:' + item;
                    $log.debug('Dialog button "{}"', btn);

                    deferred.resolve(btn);

                    self.config.cleanUp();
                };

                self.config.buttons.push(newButton);
            });

            self.config.title = options.title;
            self.config.text = options.text;
            self.config.close = function () {
                if (options.closeable === false) {
                    return;
                }
                $log.debug('Dialog canceled');

                doc.off('keyup', escHandler);

                deferred.reject('closed');

                this.cleanUp();
            };
            self.config.visible = true;

            return deferred.promise;
        };
    });

    app.directive('modal', function (ModalService) {
        return {
            restrict: 'E',
            replace: true,
            template: modalTpl,
            scope: true,
            link: function (scope) {
                scope.config = ModalService.config;
            }
        };
    });

    return app;
});