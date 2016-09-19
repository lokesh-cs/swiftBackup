var AjaxPromiseOnly = require('./ajax-promise-only');

var AjaxPromise = {

  getUrl: function (url) {
    var iUrl = url.indexOf('?');
    var sUrl = '';
    if(iUrl != -1) {
      sUrl = url + '&sessionId=' + sessionStorage.sessionId + '&requestId='+ localStorage.requestId;
    } else {
      sUrl = url + '?sessionId=' + sessionStorage.sessionId + '&requestId='+ localStorage.requestId;
    }
    return sUrl;
  },

  get: function (url, data, contentType) {
    url = this.getUrl(url);
    return AjaxPromiseOnly.get(url, data, contentType);
  },

  post: function (url, data, contentType) {
    url = this.getUrl(url);
    return AjaxPromiseOnly.post(url, data, contentType);
  },

  put: function (url, data, contentType) {
    url = this.getUrl(url);
    return AjaxPromiseOnly.put(url, data, contentType);
  },

  patch: function (url, data, contentType) {
    url = this.getUrl(url);
    return AjaxPromiseOnly.patch(url, data, contentType);
  },

  delete: function (url, data, contentType) {
    url = this.getUrl(url);
    return AjaxPromiseOnly.delete(url, data, contentType);
  },

  postCORS: function (url, data, contentType) {
    url = this.getUrl(url);
    return AjaxPromiseOnly.postCORS(url, data, contentType);
  },

  deleteCORS: function (url, data) {
    url = this.getUrl(url);
    return AjaxPromiseOnly.deleteCORS(url, data);
  }

};

module.exports = AjaxPromise;
