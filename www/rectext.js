/*global cordova, module*/

module.exports = {
    recText: function (sourceType, imageSource, successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "Textocr", "recText", [sourceType, imageSource]);
    }
};