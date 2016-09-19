var MockDataForTransaction = require('../../mock/mock-data-for-dashboard').transactionData;

var TransactionProps = (function () {

    var aTransactionDataList = MockDataForTransaction;
    var bTransactionDialogVisibilityStatus = false;
    var searchStatus = false;
    var aTransactionLogData = [];
    var sClickedPie = "Application Health";
    var aLabelClicked = " ";
    var aFromIndex = 0;
    var aSizeOfResult = 0;

    return {

        getLabelClicked: function () {
            return aLabelClicked;
        },

        setLabelClicked: function (label) {
            aLabelClicked = label;
        },

        getFromIndex: function () {
            return aFromIndex;
        },

        setFromIndex: function (fromIndex) {
            aFromIndex = fromIndex;
        },


        getSizeOfResult: function () {
            return aSizeOfResult;
        },

        setSizeOfResult: function (sizeOfResult) {
            aSizeOfResult = sizeOfResult;
        },

        getTransactionDataList: function () {
            return aTransactionDataList;
        },

        setTransactionDataList: function (_aTransactionDataList) {
            aTransactionDataList = _aTransactionDataList;
        },

        getTransactionDialogVisibilityStatus: function () {
            return bTransactionDialogVisibilityStatus;
        },

        setTransactionDialogVisibilityStatus: function (_bTransactionDialogVisibilityStatus) {
            bTransactionDialogVisibilityStatus = _bTransactionDialogVisibilityStatus;
        },

        getSearchStatus: function () {
            return searchStatus;
        },

        setSearchStatus: function (_searchStatus) {
            searchStatus = _searchStatus;
        },

        setTransactionLogData: function (aLogData) {
            aTransactionLogData = aLogData;
        },

        getTransactionLogData: function () {
            return aTransactionLogData;
        },

        setClickedPie: function (_sClickedPie) {
            sClickedPie = _sClickedPie;
        },

        getClickedPie: function () {
            return sClickedPie;
        }


    };

})();

module.exports = TransactionProps;
