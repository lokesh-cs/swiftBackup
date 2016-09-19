var TransactionViewModel = function (aData, sClickedPieChart, bDialogVisibilityStatus, aDialogDataList, oProperties, aLabelClicked, aSearchStatus, aFromIndex, aSizeOfResult) {
    this.getDataList = function () {
        return aData;
    };

    this.getClickedPie = function () {
        return sClickedPieChart;
    };

    this.getDialogVisibilityStatus = function () {
        return bDialogVisibilityStatus;
    };

    this.getDialogTableRowList = function () {
        return aDialogDataList;
    };

    this.getPropertyByName = function (sPropertyName) {
        return oProperties[sPropertyName];
    };

    this.getLabelClicked = function () {
        return aLabelClicked;
    };

    this.getSearchStatus = function () {
        return aSearchStatus;
    };

    this.getFromIndex = function () {
        return aFromIndex;
    };

    this.getSizeOfResult = function () {
        return aSizeOfResult;
    };

    this.toJSON = function () {
        return {
            'dataList': aData,
            'clickedPieChart': sClickedPieChart,
            'dialogVisibility': bDialogVisibilityStatus,
            'dialogTableRowList': aDialogDataList,
            'properties': oProperties
        }
    };
};

module.exports = TransactionViewModel;