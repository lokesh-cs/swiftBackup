/**
 * Created by DEV on 10-07-2015.
 */
var Tracker = require('./tracker');

var MethodTracker = (function () {
  return {
    getTracker: function (sClassName) {
      return Tracker.trackMe.bind(null, sClassName);
    },

    getTrace: function () {
      return Tracker.getTrace();
    },

    emptyCallTrace: function () {
      Tracker.emptyCallTrace();
    }
  }
})();

module.exports = MethodTracker;