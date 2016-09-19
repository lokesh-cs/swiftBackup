var _ = require('lodash');

var RequestMappingParser = (function(){

	var encodeParameter = function(oParameter){

	    _.forEach(oParameter, function(oVal, sKey){
	      oParameter[sKey] = encodeURIComponent(oVal);
	    });
	  };

	  return {

	    getRequestUrl: function (sUrl, oParameter) {
	      encodeParameter(oParameter);

	      var compiled = _.template(sUrl);
	      return compiled(oParameter);
	    }
    /*getRequestUrl: function (sUrl, aParameter) {
      var iIndex = 0;

      var sActualUrl = sUrl.replace(/{{.*?}}/g, function (sMatchedString, iOffset, sActualString) {
        return aParameter[iIndex++];
      });

      return sActualUrl;

    }*/

  }

})();

module.exports =  RequestMappingParser;
