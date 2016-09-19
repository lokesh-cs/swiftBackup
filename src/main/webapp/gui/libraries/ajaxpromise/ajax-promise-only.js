/**
 * Created by DEV on 17-06-2015.
 */
var BluebirdPromise = require('bluebird');
var $ = require('jquery');
var _ = require('lodash');
var AjaxPromiseOnly = {
  ajax: function (type, url, data, contentType) {
    var _this = this;
    return new BluebirdPromise(function (resolve, reject) {
      data = getEncodedData(data);
      var successResponse = successCallback.bind(this, resolve);
      var failureResponse = failureCallback.bind(this, reject);
      var opts = {
        type: type,
        dataType: 'JSON',
        data: data,
        headers: { 'userId':  sessionStorage.userId},
        contentType: contentType || "application/json; charset=utf-8",
        beforeSend: function(){
          var loaderContainer = document.getElementById('loaderContainer');
          if(loaderContainer) {
            loaderContainer.classList.remove('loaderInVisible');
          }
        },
        success: successResponse,
        error: failureResponse
      };
      return $.ajax(url, opts);
    });
  },
  ajaxCORS: function (type, url, data, contentType) {
    return new BluebirdPromise(function (resolve, reject) {

      var successResponse = successCallback.bind(this, resolve);
      var failureResponse = failureCallback.bind(this, reject);
      var opts = {
        type: type,
        data: data,
        contentType: contentType || false,
        processData: false,
        crossDomain: true,
        xhrFields: {
          withCredentials: true
        },
        beforeSend: function(){
          var loaderContainer = document.getElementById('loaderContainer');
          if(loaderContainer) {
            loaderContainer.classList.remove('loaderInVisible');
          }
        },
        success: successResponse,
        error: failureResponse
      };
      if(contentType){
        opts.dataType = 'JSON'
      }
      return $.ajax(url, opts);
    });
  },
  get: function (url, data, contentType) {
    return this.ajax('GET', url, data, contentType);
  },
  post: function (url, data, contentType) {
    return this.ajax('POST', url, data, contentType);
  },
  put: function (url, data, contentType) {
    return this.ajax('PUT', url, data, contentType);
  },
  patch: function (url, data, contentType) {
    return this.ajax('PATCH', url, data, contentType);
  },
  delete: function (url, data, contentType) {
    return this.ajax('DELETE', url, data, contentType);
  },
  postCORS: function(url, data, contentType) {
    return this.ajaxCORS('POST', url, data, contentType);
  },
  deleteCORS: function(url, data) {
    return this.ajaxCORS('DELETE', url, data);
  },
};

var successCallback = function (oCallback, oResponse) {
  if(oCallback) {
    oResponse = getDecodedData(oResponse);
    oCallback(oResponse);
  }
  var loaderContainer = document.getElementById('loaderContainer');
  if(loaderContainer) {
    loaderContainer.classList.add('loaderInVisible');
  }
};

var failureCallback = function (oCallback, oResponse) {
  console.log(oResponse);
  if(oResponse.status == 401){
    window.location = window.location.pathname;
  }
  if(oCallback) {
    if(oResponse.responseJSON) {
      oCallback(oResponse.responseJSON);
    }else {
      var responseObject = {
        errorCode: "999999",
        response: "Server Not Responded",
        status: "FAILURE"
      };
      oCallback(responseObject);
    }
  }
  var loaderContainer = document.getElementById('loaderContainer');
  if(loaderContainer) {
    loaderContainer.classList.add('loaderInVisible');
  }
};

var getDecodedData = function (oData) {
  if (oData && !_.isEmpty(oData)) {
    var sData = JSON.stringify(oData);
    var txt = document.createElement("textarea");
    txt.innerHTML = sData;
    sData = txt.value;
    oData = JSON.parse(sData);
  }

  return oData;
};

var getEncodedData = function (oData) {
  if (oData && !_.isEmpty(oData)) {
    var sData = JSON.stringify(oData);
    sData = sData.replace(/[\u00A0-\u9999<>\&]/gim, function(i) {
      return '&#'+i.charCodeAt(0)+';';
    });
    oData = JSON.parse(sData);

  }

  return oData;
};

module.exports = AjaxPromiseOnly;
