var MicroEvent = require("./../libraries/microEvent/MicroEvent");
var _ = require('lodash');

var AjaxPromise = require('./../libraries/ajaxpromise/ajax-promise');
var RequestMapping = require('./../libraries/requestmappingparser/request-mapping-parser.js');
var MockRequestMapping = require('./../mock/dashboard-request-mapping').requestMapping;
var UrlResolution = require('./../mock/UrlResolution').urlResolution;
var MockDataForDashboard = require('./../mock/mock-data-for-dashboard');
var DataOfIps = require('./../mock/monitoring-configuration');


var aSystemViewList = MockDataForDashboard.transactionData;
var TransactionStore = require('./transactionStore');
var ipDataOfSever = DataOfIps.ipDataOfSever;

var SystemStore = (function () {


    var _reload = function () {
        TransactionStore.fetchTransactionData();
        _fetchOmdData();

        setTimeout(function () {
            _reload();
        }, 5000);
    };


    var _fetchOmdData = function () {
        AjaxPromise
            .get(RequestMapping.getRequestUrl(UrlResolution.Omd + MockRequestMapping.GetOmdData, null), null, null)
            .then(_successFetchOmdData, _failureFetchOmdData)
            .catch(_failureFetchOmdData);

    };


    var triggerChange = function () {
        return SystemStore.trigger('change');
    };


    var _successFetchOmdData = function (oResponse) {
        preProcessData(oResponse);
    };

    var _failureFetchOmdData = function (x, t, r) {
        console.log("Data not get here " + x + " " + t + " " + r);
    };

    var preProcessData = function (oResponse) {
        var jsonData = JSON.stringify(oResponse);
        //    console.log(oResponse.tomcat);
        dataForUpdate(1, oResponse.orientDb);
        dataForUpdate(2, oResponse.elastic);
        dataForUpdate(3, oResponse.tomcat);
        dataForUpdate(4, oResponse.swift);
        dataForUpdate(5, oResponse.pythonserver);
        triggerChange();
    };

    var dataForUpdate = function (i, data) {
        aSystemViewList[i].status = data.status;
        aSystemViewList[i].label = data.label;
        //  aSystemViewList[i].isChildVisible    = data.isChildVisible;

        if (data.isChildVisible == true) {
            aSystemViewList[i].childNodes = data.childNodes;
        }
        else {
            aSystemViewList[i].childNodes = [];
        }

        // aSystemViewList[i].transaction  = data.transaction;
        aSystemViewList[i].cpu = data.cpuUsed;
        aSystemViewList[i].memory = data.memoryUsed;
        aSystemViewList[i].disk = data.diskUsed;

        aSystemViewList[i].memoryUsedDescription = data.memoryUsedDescription;
        aSystemViewList[i].diskUsedDescription = data.diskUsedDescription;
        aSystemViewList[i].cpuUsedDescription = data.cpuUsedDescription;
        //aSystemViewList[i].ping              = data.ping;

    };

    return {

        initialize: function () {
            return {
                data: aSystemViewList,
                ipData: ipDataOfSever
            };
        },

        getSystemItemList: function () {
            return aSystemViewList;
        },


        reloadData: function () {
            _reload();
        },

        handleCollapseExpandIconClicked: function (sItemName, iId) {
            var aData = aSystemViewList;


            if (iId == "2" || iId == "3" || iId == "5" || iId == "7" || iId == "8") {
                var oItem = _.find(aData, {label: sItemName});

                if (oItem.isChildVisible == null) {
                    oItem.isChildVisible = true;
                }
                else {
                    oItem.isChildVisible = !oItem.isChildVisible;
                }
            }
            else {
                var oItem;
                _.forEach(aData, function (sItemData) {

                    _.forEach(sItemData.childNodes, function (childData) {
                        if (childData.label == sItemName) {
                            oItem = childData;
                        }
                    });

                });

                if (oItem.isChildVisible == null) {
                    oItem.isChildVisible = true;
                }
                else {
                    oItem.isChildVisible = !oItem.isChildVisible;
                }
            }

            triggerChange();
        },


        handleLogDialogClose: function () {
            _toggleFailInfoDialogVisibility();
            triggerChange();
        },


    };
})();

MicroEvent.mixin(SystemStore);

module.exports = SystemStore;