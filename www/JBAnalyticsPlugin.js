var exec = require('cordova/exec');

function  JBAnalyticsPlugin() {
};


JBAnalyticsPlugin.prototype.onEventWithData = function (eventId,eventData) {
    exec(null, null, 'JBAnalyticsPlugin', 'onEventWithData',[eventId, eventData]);
};

var analyticsModel = new JBAnalyticsPlugin();
module.exports = analyticsModel;