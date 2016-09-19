var MicroEvent = require("./../libraries/microEvent/MicroEvent");
var _ = require('lodash');
var alertify = require('alertifyjs');

var AjaxPromise = require('./../libraries/ajaxpromise/ajax-promise');
var RequestMapping = require('./../libraries/requestmappingparser/request-mapping-parser.js');
var MockRequestMapping = require('./../mock/dashboard-request-mapping').requestMapping;

var MockDataForDashboard = require('./../mock/mock-data-for-dashboard');
var MockDataForTiles = require('./../mock/mock-data-for-tiles');
var oTransactionProps = require('./model/transaction-props');
var aSystemViewList = MockDataForDashboard.transactionData;
var sClickedPieChart = "Application Health";

var UrlResolution = require('./../mock/UrlResolution').urlResolution;


var Mystore = (function () {


    var _reload = function () {
        _fetchTransactionData();
        // _fetchOmdData();

        setTimeout(function () {
            _reload();
        }, 5000);
    };

    var _fetchTransactionData = function () {

        AjaxPromise
            .get(RequestMapping.getRequestUrl(UrlResolution.Elastic + MockRequestMapping.GetTransactions))
            .then(_successFetchTransactionCallBack, _failureFetchTransactionCallBack)
            .catch(_failureFetchTransactionCallBack);

    };

    var _successFetchTransactionCallBack = function (oResponse) {
        console.log('success _successFetchTransactionCallBack');

        var oData = {};
        oData = JSON.stringify(oResponse);

        //  if ((oData.orientCount != null) && (oData.elasticCount != null) && (oData.appHealthCount != null))
        _extractTransactionData(oData);
    };


    var _failureFetchTransactionCallBack = function () {
        console.log('in the _failureFetchTransactionCallBack() ');
    };

    var _extractTransactionData = function (oData1) {
        oData = JSON.parse(oData1);

        aSystemViewList[0].transaction.pass = oData.appHealthCount.success;
        aSystemViewList[0].transaction.fail = oData.appHealthCount.fail;
        aSystemViewList[0].transaction.inProgress = oData.appHealthCount.inProgress;

        aSystemViewList[1].transaction.pass = oData.orientCount.success;
        aSystemViewList[1].transaction.fail = oData.orientCount.fail;
        aSystemViewList[1].transaction.inProgress = oData.orientCount.inProgress;

        aSystemViewList[2].transaction.pass = oData.elasticCount.success;
        aSystemViewList[2].transaction.fail = oData.elasticCount.fail;
        aSystemViewList[2].transaction.inProgress = oData.elasticCount.inProgress;

        var oAssetTransaction = {pass: 0, fail: 0, inProgress: 0};
        /*
         _.forEach(aOperationalHitsData, function (oHit, iIndex) {
         var sAppHealthStatus = oHit.executionStatus;
         this.updateTransactionData(sAppHealthStatus, oApplicationHealthTransaction);

         var oInteractor = oHit.interactor;
         if (oInteractor.elasticStrategy) {
         var sElasticStatus = oInteractor.elasticStrategy.executionStatus;
         updateTransactionData(sElasticStatus, oElasticTransaction);
         }

         if (oInteractor.neo4jStrategy) {
         var sNeo4jStatus = oInteractor.neo4jStrategy.executionStatus;
         updateTransactionData(sNeo4jStatus, oNeo4jTransaction);
         }

         if (oInteractor.assetStrategy) {
         var sAssetStatus = oInteractor.assetStrategy.executionStatus;
         updateTransactionData(sAssetStatus, oAssetTransaction);
         }
         });
         */
        /*var aTransactionData = oTransactionProps.getTransactionDataList();
         _.forEach(aTransactionData, function (oDashboardItem, iIndexDashboard) {
         if (oDashboardItem.id == 1) {// Application Health
         oDashboardItem.transaction = oApplicationHealthTransaction;
         } else if (oDashboardItem.id == 2) { // Neo4j Cluster
         oDashboardItem.transaction = oNeo4jTransaction;
         } else if (oDashboardItem.id == 3) { // Elastic Cluster
         oDashboardItem.transaction = oElasticTransaction;
         } else if (oDashboardItem.id == 7) { // Asset === Swift
         oDashboardItem.transaction = oAssetTransaction;
         }
         });*/

        triggerChange();
    };

    var triggerChange = function () {
        return Mystore.trigger('change');
    };


    var _getTransactionLogData = function (fromIndex, direction, label1, oCurrentPie) {

        var logCount;
        if (label1 == "pass")
            logCount = oCurrentPie.transaction.pass;
        else if (label1 == "inprogress")
            logCount = oCurrentPie.transaction.inProgress;
        else if (label1 == "fail")
            logCount = oCurrentPie.transaction.fail;


        var iSize = 20;
        if (direction == "prev")
            var iFrom = fromIndex - 20;
        else if (direction == "forward")
            var iFrom = fromIndex + 20;
        else var iFrom = 0;
        if (iFrom < 0)
            iFrom = 0;

        if (iFrom > logCount)
            iFrom = iFrom - 20;

        var iCurrentId = oCurrentPie.id;

        var sCurrentContext = '';
        if (iCurrentId == 1) {
            sCurrentContext = 'apphealth' + label1;
        } else if (iCurrentId == 2) {
            sCurrentContext = 'orientdb' + label1;
        } else if (iCurrentId == 3) {
            sCurrentContext = 'elastic' + label1;
        } else if (iCurrentId == 7) {
            sCurrentContext = 'asset' + label1;
        }

        AjaxPromise
            .get(RequestMapping.getRequestUrl(UrlResolution.Elastic + MockRequestMapping.GetTransactionLogById, {
                id: sCurrentContext,
                size: iSize,
                from: iFrom
            }))
            .then(_successFetchTransactionLogsCallBack.bind(this, iFrom, label1), _failureFetchTransactionLogsCallBack)
            .catch(_failureFetchTransactionLogsCallBack);

    };

    var _getSearchedTransactionLogData = function (fromIndex, direction, field, value, value1, label, oCurrentPie) {
        var iCurrentId = oCurrentPie.id;

        var sCurrentContext = '';
        if (iCurrentId == 1) {
            sCurrentContext = '_source.executionStatus';
        } else if (iCurrentId == 2) {
            sCurrentContext = '_source.orientStrategy.executionStatus';
        } else if (iCurrentId == 3) {
            sCurrentContext = '_source.elasticStrategy.executionStatus';
        } else if (iCurrentId == 7) {
            sCurrentContext = 'asset' + label;
        }
        var label1 = '';
        if (label == "pass") {
            label1 = 'SUCCESS';
        } else if (label == "fail") {
            label1 = 'FAILURE';
        } else if (label == "inprogress") {
            label1 = 'INPROGRESS';
        }

        if (field == "transactionId")
            var iField = "id";
        else if (field == "user")
            var iField = "userName";
        else if (field == "timeRange")
            var iField = "startTime";

        var iSize = 20;
        if (direction == "prev")
            var iFrom = fromIndex - 20;
        else if (direction == "forward")
            var iFrom = fromIndex + 20;
        else var iFrom = 0;
        if (iFrom < 0)
            iFrom = 0;

        if (iFrom > oTransactionProps.getSizeOfResult())
            iFrom = iFrom - 20;

        var iValue = value;
        if (field == "timeRange") {

            var iValue1 = value1;

            AjaxPromise
                .get(RequestMapping.getRequestUrl(UrlResolution.Elastic + MockRequestMapping.GetSearchedTransactionLogByStartTime, {
                    id: sCurrentContext,
                    label: label1,
                    field: iField,
                    value: iValue,
                    value1: iValue1,
                    from: iFrom,
                    size: iSize
                }))
                .then(_successFetchTransactionLogsCallBack.bind(this, iFrom, label), _failureFetchTransactionLogsCallBack)
                .catch(_failureFetchTransactionLogsCallBack);
        }
        else {
            AjaxPromise
                .get(RequestMapping.getRequestUrl(UrlResolution.Elastic + MockRequestMapping.GetSearchedTransactionLog, {
                    id: sCurrentContext,
                    label: label1,
                    field: iField,
                    value: iValue,
                    from: iFrom,
                    size: iSize
                }))
                .then(_successSearchFetchTransactionLogsCallBack.bind(this, iFrom, label), _failureFetchTransactionLogsCallBack)
                .catch(_failureFetchTransactionLogsCallBack);
        }

    };

    var _successFetchTransactionLogsCallBack = function (fromIndex, label1, aResponse) {
        console.log('in fetchLogSuccess');

        /*  var oData = {};
         oData = JSON.stringify(aResponse);*/
        var aTransactionLogData = [];
        _.forEach(aResponse.result, function (oRes) {
            var oLog = {};
            oLog.id = oRes.id;
            oLog.user = oRes.userId;
            oLog.useCase = oRes.useCase;
            oLog.startTime = oRes.startTime;
            if (oRes.turnAroundTime != null) {
                oLog.turnAroundTime = oRes.turnAroundTime + " ms";
            }
            oLog.log = oRes;
            aTransactionLogData.push(oLog);
        });
        oTransactionProps.setTransactionLogData(aTransactionLogData);
        oTransactionProps.setLabelClicked(label1);
        oTransactionProps.setFromIndex(fromIndex);
        oTransactionProps.setSizeOfResult(aResponse.size);


        triggerChange();
    };


    var _failureFetchTransactionLogsCallBack = function (oResponse) {
        console.log('in fetchlogfailure');
    };

    var _successSearchFetchTransactionLogsCallBack = function (fromIndex, label1, aResponse) {
        console.log('in fetchLogSuccess');

        oData = {};
        oData = JSON.stringify(aResponse);

        var aTransactionLogData = [];
        _.forEach(aResponse.hits.hits, function (oRes) {
            var oLog = {};
            oLog.id = oRes._source.id;
            oLog.user = oRes._source.userId;
            oLog.useCase = oRes._source.useCase;
            oLog.startTime = oRes._source.startTime;
            if (oRes.turnAroundTime != null) {
                oLog.turnAroundTime = oRes._source.turnAroundTime + " ms";
            }
            oLog.log = oRes;
            aTransactionLogData.push(oLog);
        });
        oTransactionProps.setTransactionLogData(aTransactionLogData);
        oTransactionProps.setLabelClicked(label1);
        oTransactionProps.setFromIndex(fromIndex);
        oTransactionProps.setSizeOfResult(aResponse.hits.total)

        triggerChange();
    };


    var _toggleTransactionInfoDialogVisibility = function () {

        var bVisibility = oTransactionProps.getTransactionDialogVisibilityStatus();
        oTransactionProps.setTransactionDialogVisibilityStatus(!bVisibility);
        triggerChange();
    };

    var _turnSearchStatusOn = function () {

        oTransactionProps.setSearchStatus(true);
        triggerChange();
    };

    var _turnSearchStatusOff = function () {

        oTransactionProps.setSearchStatus(false);
        triggerChange();
    };

    return {
        fetchTransactionData: function () {
            AjaxPromise
                .get(RequestMapping.getRequestUrl(UrlResolution.Elastic + MockRequestMapping.GetTransactions))
                .then(_successFetchTransactionCallBack, _failureFetchTransactionCallBack)
                .catch(_failureFetchTransactionCallBack);

        },
        initialize: function () {
            return {
                data: aSystemViewList,
                clickedPieChart: sClickedPieChart
            };
        },

        getClickedPieChart: function () {
            return sClickedPieChart;
        },

        reloadData: function () {
            _reload();
        },

        handlePieWrapperClicked: function (oPieChartItem) {
            sClickedPieChart = oPieChartItem.label;
            var iIdSelected = oPieChartItem.id;
            _.forEach(MockDataForTiles, function (oTileItem) {
                oTileItem.isSelectedTile = (oTileItem.id == iIdSelected);
            });

            triggerChange();
        },

        handleLogDialogClose: function () {
            _toggleTransactionInfoDialogVisibility();
            _turnSearchStatusOff();
            oTransactionProps.setSizeOfResult(0);
            triggerChange();
        },

        handleSearchStatus: function (label, oCurrentPie) {
            _turnSearchStatusOff();
            _getTransactionLogData(0, null, label, oCurrentPie);
            triggerChange();
        },

        handleLabelClicked: function (label1, oCurrentPie) {
            if (label1 == "pass")
                logCount = oCurrentPie.transaction.pass;
            else if (label1 == "inprogress")
                logCount = oCurrentPie.transaction.inProgress;
            else if (label1 == "fail")
                logCount = oCurrentPie.transaction.fail;
            var fromIndex = 0;
            var direction = null;
            if (logCount > 0) {
                _getTransactionLogData(fromIndex, direction, label1, oCurrentPie);
                _toggleTransactionInfoDialogVisibility();
            } else {
                alertify.error("No any failed process...!");

            }
        },


        handleLoadLabelClicked: function (fromIndex, direction, field, value, value1, label1, oCurrentPie) {
            if (!oTransactionProps.getSearchStatus())

                _getTransactionLogData(fromIndex, direction, label1, oCurrentPie);
            else {
                if (field == "transactionId" || field == "user")
                {
                    if (value != "")
                        _getSearchedTransactionLogData(fromIndex, direction, field, value, value1, label1, oCurrentPie);
                    else _getTransactionLogData(fromIndex, direction, label1, oCurrentPie);
                }
                else  _getSearchedTransactionLogData(fromIndex, direction, field, value, value1, label1, oCurrentPie);
            }
        },

        handleSearchByFieldClicked: function (field, value, value1, label, oCurrentPie) {
            var fromIndex = 0;
            var direction = null;
            _turnSearchStatusOn();
            if (field == "transactionId" || field == "user")
            {
                if (value != "")
                    _getSearchedTransactionLogData(fromIndex, direction, field, value, value1, label, oCurrentPie);

                else _getTransactionLogData(fromIndex, direction, label, oCurrentPie);
            }
            else  _getSearchedTransactionLogData(fromIndex, direction, field, value, value1, label, oCurrentPie);

        },

        updateTransactionData: function (sCount, oTransaction) {
            var sLowerStatus = sStatus.toLowerCase();
            if (sLowerStatus == 'success') {
                oTransaction.pass = sCount;
            } else if (sLowerStatus == 'failure') {
                oTransaction.fail++;
            } else if (sLowerStatus == 'inprogress') {
                oTransaction.inProgress++;
            }
        }

        /*oshoJSONToMyJSONConverter: function () {

         var aOperationalHitsData = MockPerformanceData.hits.hits;

         var oApplicationHealthTransaction = {
         pass:0,
         fail:0,
         inProgress:0
         };

         var oElasticTransaction = {
         pass:0,
         fail:0,
         inProgress:0
         };

         var oNeo4jTransaction = {
         pass:0,
         fail:0,
         inProgress:0
         };

         var oAssetTransaction = {
         pass:0,
         fail:0,
         inProgress:0
         };

         var _this = this;
         _.forEach(aOperationalHitsData, function (oHit, iIndex) {
         var sAppHealthStatus = oHit['_source'].executionStatus;
         _this.updateTransactionData(sAppHealthStatus, oApplicationHealthTransaction);

         var oInteractor = oHit['_source'].interactor;
         if(oInteractor.elasticStrategy){
         var sElasticStatus = oInteractor.elasticStrategy.executionStatus;
         _this.updateTransactionData(sElasticStatus, oElasticTransaction);
         }

         if(oInteractor.neo4jStrategy){
         var sNeo4jStatus = oInteractor.neo4jStrategy.executionStatus;
         _this.updateTransactionData(sNeo4jStatus, oNeo4jTransaction);
         }

         if(oInteractor.assetStrategy){
         var sAssetStatus = oInteractor.assetStrategy.executionStatus;
         _this.updateTransactionData(sAssetStatus, oAssetTransaction);
         }
         });

         var aDashboardData = this.aSystemViewList;
         _.forEach(aDashboardData, function (oDashboardItem, iIndexDashboard) {
         if(oDashboardItem.id == 1){// Application Health
         oDashboardItem.transaction = oApplicationHealthTransaction;
         }else if(oDashboardItem.id == 2){ // Neo4j Cluster
         oDashboardItem.transaction = oNeo4jTransaction;
         }else if(oDashboardItem.id == 3){ // Elastic Cluster
         oDashboardItem.transaction = oElasticTransaction;
         }else if(oDashboardItem.id == 7){ // Asset === Swift
         oDashboardItem.transaction = oAssetTransaction;
         }
         });

         }*/


    };
})();

MicroEvent.mixin(Mystore);

module.exports = Mystore;