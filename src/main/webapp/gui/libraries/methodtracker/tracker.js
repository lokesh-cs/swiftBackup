/**
 * Created by DEV on 10-07-2015.
 */
var Tracker = (function () {
  var callTrace = [];

  return {
    getTrace: function () {
      return callTrace;
    },

    emptyCallTrace: function () {
      callTrace = [];
    },

    trackMe: function (sClassName, sMethodName) {
      callTrace.push(sClassName + ": " + sMethodName);
    }
  }
})();


module.exports = Tracker;