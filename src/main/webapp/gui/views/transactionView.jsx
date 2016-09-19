var React = require('react');
var _ = require('lodash');
var EventBus = require('../libraries/eventdispatcher/EventDispatcher');
var Pie = require('react-simple-pie-chart');
var Dialog = require('material-ui/lib/dialog');
require("react-tap-event-plugin")();

var oMock = require('../mock/mock');
var MockDataForTiles = require('../mock/mock-data-for-tiles');
var TransactionInfoDialogView = require('./transactionLogsInfoDialogView.jsx').view;

var Events = {
    HANDLE_PIE_WRAPPER_CLICKED: "handle_pie_wrapper_clicked",
    HANDLE_LABEL_CLICKED: "handle_label_clicked",
    HANDLE_LOAD_LABEL_CLICKED: "handle_load_label_clicked",
    HANDLE_LOG_DIALOG_CLOSE: "handle_log_dialog_close",
    SEARCH_BY_FIELD_CLICKED: "search_by_field_changed",
    HANDLE_SEARCH_STATUS: "handle_search_status"

};


var TransactionView = React.createClass({
    propTypes: {
        data: React.PropTypes.object,
        clickedPieChart: React.PropTypes.string,
        model: React.PropTypes.object
    },

    getPieChart: function () {
        var aData = this.props.data;

        var oMockColor = oMock.pieChartColor;
        var sPassColor = oMockColor.pass;
        var sFailColor = oMockColor.fail;
        var sInProgressColor = oMockColor.inProgress;
        var aPieDOM = [];
        var that = this;

        _.forEach(aData, function (oCurrentItem) {
            var oTransaction = oCurrentItem.transaction;
            var iPass = oTransaction.pass;
            var iFail = oTransaction.fail;
            var iInProgress = oTransaction.inProgress;

            var aSlice = [
                {
                    color: sPassColor,
                    value: iPass
                },
                {
                    color: sFailColor,
                    value: iFail
                },
                {
                    color: sInProgressColor,
                    value: iInProgress
                }
            ];

            var sItemName = oCurrentItem.label;
            var sPieContainerClassName = sItemName.replace(' ', '').toLowerCase() + "Pie pieWrapper";
            aPieDOM.push(
                <div className={sPieContainerClassName} onClick={that.handlePieWrapperClicked.bind(that, oCurrentItem)}>
                    <Pie slices={aSlice}/>
                    <div className="pieChartLabel">{sItemName}</div>
                </div>
            );
        });

        return aPieDOM;
    },

    handlePieWrapperClicked: function (oCurrentItem) {
        if (oCurrentItem) {
            EventBus.dispatch(Events.HANDLE_PIE_WRAPPER_CLICKED, this, oCurrentItem);

        }
    },

    getPieInfoContainerDOM: function () {
        var aData = this.props.model.getDataList();
        var sClickedPie = this.props.model.getClickedPie();

        var oCurrentPie = _.find(aData, {label: sClickedPie});

        var oTransaction = {};
        oTransaction = oCurrentPie.transaction;

        var iPass = oTransaction.pass;
        var iFail = oTransaction.fail;
        var iInProgress = oTransaction.inProgress;

        var oMockColor = oMock.pieChartColor;
        // var oPassStyle = {'background-color': oMockColor.pass};
        // var oFailStyle = {'background-color': oMockColor.fail};
        // var oInProgressStyle = {'background-color': oMockColor.inProgress};

        return (
            <div className="pieInfoBody">
                <div className="pieInfoBodyLabel">{sClickedPie}</div>
                <div className="passInfo" onClick={this.handleLabelClicked.bind(this, "pass", oCurrentPie)}>
                    <div className="infoLeftContainer">
                        <div className="passLabel infoLabel">Passed</div>
                    </div>
                    <div className="infoRightContainer">
                        <div className="passValue pieInfoChild">{iPass}</div>
                    </div>
                </div>
                <div className="inProgressInfo" onClick={this.handleLabelClicked.bind(this, "inprogress", oCurrentPie)}>
                    <div className="infoLeftContainer">
                        <div className="inProgressLabel infoLabel">In Progress</div>
                    </div>
                    <div className="infoRightContainer">
                        <div className="inProgressValue pieInfoChild">{iInProgress}</div>
                    </div>
                </div>
                <div className="failInfo" onClick={this.handleLabelClicked.bind(this, "fail", oCurrentPie)}>
                    <div className="infoLeftContainer">
                        <div className="inProgressLabel infoLabel">Failed</div>
                    </div>
                    <div className="infoRightContainer">
                        <div className="failValue pieInfoChild">{iFail}</div>
                    </div>
                </div>
            </div>
        );
    },


    handleLabelClicked: function (label, oCurrentPie) {
        EventBus.dispatch(Events.HANDLE_LABEL_CLICKED, this, label, oCurrentPie);
    },

    handleLoadLabelClicked: function (fromIndex, direction, label, oCurrentPie) {
        var field = "";
        var value = "";
        var value1 = "";

        if (this.props.model.getSearchStatus()) {

            if (this.refs.searchBox1.value != "") {
                value = this.refs.searchBox1.value;
                field = "transactionId";
            } else if (this.refs.searchBox2.value != "") {
                value = this.refs.searchBox2.value;
                field = "user";
            } else if (this.refs.searchBox3.value != "" || this.refs.searchBox4.value != "") {
                if (this.refs.searchBox3.value != "")
                    value = Date.parse(this.refs.searchBox3.value);
                if (this.refs.searchBox4.value != "")
                    value1 = Date.parse(this.refs.searchBox4.value);
                field = "timeRange"
            }
        }

        EventBus.dispatch(Events.HANDLE_LOAD_LABEL_CLICKED, this, fromIndex, direction, field, value, value1, label, oCurrentPie);
    },

    handleLogDialogClose: function () {
        EventBus.dispatch(Events.HANDLE_LOG_DIALOG_CLOSE, this);
    },

    searchByTransactionField: function (field, searchBox, label, oCurrentPie) {
        var value = "";
        var value1 = "";

        switch (searchBox) {
            case 'searchBox1' :
                value = this.refs.searchBox1.value;
                break;
            case 'searchBox2' :
                value = this.refs.searchBox2.value;
                break;
            case 'searchBox3' :
                if (this.refs.searchBox3.value != "")
                    value = Date.parse(this.refs.searchBox3.value);
                if (this.refs.searchBox4.value != "")
                    value1 = Date.parse(this.refs.searchBox4.value);
                break;
        }
        EventBus.dispatch(Events.SEARCH_BY_FIELD_CLICKED, this, field, value, value1, label, oCurrentPie);

    },

    handleSearchStatus: function (label, oCurrentPie) {
        this.refs.searchBox1.value = "";
        this.refs.searchBox2.value = "";
        this.refs.searchBox3.value = "";
        this.refs.searchBox4.value = "";

        EventBus.dispatch(Events.HANDLE_SEARCH_STATUS, this, label, oCurrentPie);
    },

    getPieChartView: function (oData) {

        var oTransactionData = oData.transaction;
        var iPass = oTransactionData.pass;
        var iFail = oTransactionData.fail;
        var iInProgress = oTransactionData.inProgress;
        var oMockColor = oMock.pieChartColor;

        var aSlice = [
            {
                color: oMockColor.inProgress,
                value: iInProgress
            },
            {
                color: oMockColor.fail,
                value: iFail
            },
            {
                color: oMockColor.pass,
                value: iPass
            }
        ];

        var sItemName = oData.label;
        var sPieContainerClassName = sItemName.replace(' ', '').toLowerCase() + "Pie pieWrapper";

        return (
            <div className={sPieContainerClassName}>
                <Pie slices={aSlice}/>
            </div>
        );
    },

    getTileViews: function () {

        var aTiles = MockDataForTiles;
        var aData = this.props.model.getDataList();
        var _this = this;
        var aBigTileViews = [];
        var aSmallTileViews = [];

        _.forEach(aTiles, function (oTile) {
            var oData = _.find(aData, {id: oTile.systemId});
            var oPieChartView = {};
            if (oData) {
                oPieChartView = (
                    <div className="pieChartWrapper" onClick={_this.handlePieWrapperClicked.bind(_this, oData)}>
                        {_this.getPieChartView(oData)}
                    </div>);
            } else {
                oPieChartView = <div className="pieChartInfoWrapper">{_this.getPieInfoContainerDOM()}</div>;
            }


            var sTileClassName = oTile.isBig ? "customTile customBig " : "customTile ";
            sTileClassName += oTile.className;

            if (oTile.isSelectedTile) {
                sTileClassName += ' selectedTile';
            }

            if (oTile.isBig) {
                aBigTileViews.push(<div key={oTile.id} className={sTileClassName}
                                        onClick={_this.handlePieWrapperClicked.bind(_this, oData)}>{oPieChartView}</div>);
            } else {
                aSmallTileViews.push(<div key={oTile.id} className={sTileClassName}
                                          onClick={_this.handlePieWrapperClicked.bind(_this, oData)}>{oPieChartView}</div>);
            }
        });

        return <div>
            <div className="bigTileContainer">{aBigTileViews}</div>
            <div className="smallTileContainer">{aSmallTileViews}</div>
        </div>;
    },


    render: function () {

        var aData = this.props.model.getDataList();
        var oModel = this.props.model;
        var aTileViews = this.getTileViews();
        var bDialogVisibility = oModel.getDialogVisibilityStatus();
        var aDialogDataList = oModel.getDialogTableRowList();
        var fromIndex = oModel.getFromIndex();
        var sizeOfResult = oModel.getSizeOfResult();
        var aCurrentPie = oModel.getClickedPie();
        var oCurrentPie = _.find(aData, {label: aCurrentPie});
        var aLabel = oModel.getLabelClicked();


        var oDialogContentStyle = {
            width: '80%',
            maxWidth: 'none'
        };

        var oDialogBodyStyle = {
            padding: '15px',
            paddingBottom: '5px'
        };

        return (
            <div className='transactionView'>
                {aTileViews}
                <Dialog
                    modal={false}
                    open={bDialogVisibility}
                    contentStyle={oDialogContentStyle}
                    bodyStyle={oDialogBodyStyle}
                    onRequestClose={this.handleLogDialogClose}>
                    <div className="searchViewContainer">
                        <div ref="searchBoxWrapper" className="searchBoxContainer">
                            <input
                                id="transactionIdSearch"
                                ref="searchBox1"
                                className="searchBox1"
                                type="text"
                                placeholder="Search by Transaction ID"
                            />
                            <button className="submitButton" type="submit"
                                    onClick={this.searchByTransactionField.bind(this, "transactionId", "searchBox1", aLabel, oCurrentPie)}></button>
                            <div className="searchBoxClear"></div>
                            <input
                                id="userSearch"
                                ref="searchBox2"
                                className="searchBox2"
                                type="text"
                                placeholder="Search by User "
                            />
                            <button className="submitButton" type="submit"
                                    onClick={this.searchByTransactionField.bind(this, "user", "searchBox2", aLabel, oCurrentPie)}></button>
                            <div className="searchBoxClear"></div>
                            <input
                                id="timeRangeSearch"
                                ref="searchBox3"
                                className="searchBox3"
                                type="text"
                                placeholder="Search by TimeRange (from)"
                            />
                            <input
                                id="timeRangeSearch"
                                ref="searchBox4"
                                className="searchBox3"
                                type="text"
                                placeholder="eg. mm/dd/yyyy 09:00:00 (to)"
                            />
                            <button className="submitButton" type="submit"
                                    onClick={this.searchByTransactionField.bind(this, "timeRange", "searchBox3", aLabel, oCurrentPie)}></button>
                            <div className="searchBoxClear"></div>
                            <button className="clearButton"
                                    onClick={this.handleSearchStatus.bind(this, aLabel, oCurrentPie)}>CLEAR
                            </button>

                        </div>
                    </div>
                    <TransactionInfoDialogView data={aDialogDataList}/>

                    <div className="load">
                        <div className="loadPrevious"
                             onClick={this.handleLoadLabelClicked.bind(this, fromIndex, "prev", aLabel, oCurrentPie)}></div>
                        <div className="noOfResults">Showing Results {fromIndex}-{(fromIndex+20) < sizeOfResult?fromIndex+20 :sizeOfResult}</div>
                        <div className="loadNext"
                             onClick={this.handleLoadLabelClicked.bind(this, fromIndex, "forward", aLabel, oCurrentPie)}></div>
                    </div>


                </Dialog>
            </div>
        );
    }


});

module.exports = {
    view: TransactionView,
    event: Events
};