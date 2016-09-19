var React = require("react");
var EventBus = require('./libraries/eventdispatcher/EventDispatcher');

var _ = require('lodash');

var SystemView = require('./views/systemView.jsx').view;
var TransactionView = require('./views/transactionView.jsx').view;
var oTransactionProps = require('./store/model/transaction-props');
var TransactionViewModel = require('./views/model/transaction-view-model');
var BackupView = require('./views/backup.jsx').view;
var BackupData = require('./views/model/backupViewProps');

var Events = {};

var ApplicationController = React.createClass({
    propTypes: {
        store: React.PropTypes.object,
        store1: React.PropTypes.object,
        store2: React.PropTypes.object
    },


    componentWillMount: function () {
        this.setState({
            data: this.getSystemStore().initialize().data,
            backupData: this.getBackupStore().initialize().backupData,
            clickedPieChart: this.getStore().initialize().clickedPieChart
        });
    },

    getStore: function () {
        return this.props.store;
    },
    getBackupStore: function () {
        return this.props.store1;
    },
    getSystemStore: function () {
        return this.props.store2;
    },

    //@UnBind: store with state
    componentWillUnmount: function () {
        this.getStore().unbind('change', this.stateChanged);
        this.getBackupStore().unbind('change', this.stateChanged);
        this.getSystemStore().unbind('change', this.stateChanged);
        this.props.action.deRegisterEvent();
    },

    //@Bind: Store with state
    componentDidMount: function () {
        this.getStore().bind('change', this.stateChanged);
        this.getBackupStore().bind('change', this.stateChanged);
        this.getSystemStore().bind('change', this.stateChanged);
        this.props.action.registerEvent();
    },

    stateChanged: function () {

        this.setState({

            data: this.getSystemStore().getSystemItemList(),
            backupData: this.getBackupStore().getBackupData(),
            clickedPieChart: this.getStore().getClickedPieChart()
        });
    },


    getTransactionModel: function () {
        var aTransactionData = this.state.data;
        var sClickedPieChart = this.state.clickedPieChart;
        var bDialogVisibility = oTransactionProps.getTransactionDialogVisibilityStatus();
        var aDialogDataList = oTransactionProps.getTransactionLogData();
        var aLabelClicked = oTransactionProps.getLabelClicked();
        var aSearchStatus = oTransactionProps.getSearchStatus();
        var aFromIndex = oTransactionProps.getFromIndex();
        var aSizeOfResult = oTransactionProps.getSizeOfResult();
        var oProperties = {};

        return new TransactionViewModel(
            aTransactionData,
            sClickedPieChart,
            bDialogVisibility,
            aDialogDataList,
            oProperties,
            aLabelClicked,
            aSearchStatus,
            aFromIndex,
            aSizeOfResult
        );
    },

    render: function () {
        var aData = this.state.data;
        var oTransactionModel = this.getTransactionModel();
        var bData = this.state.backupData;

        return (
            <div className="wrapperMain">
                <div className="dashboardHeader">
                    <div className="dashboardTitle">Monitoring Dashboard</div>
                </div>
                <div className="dashboardBody">
                    <div className="dashboardBodyContainer">
                        <div className="systemViewContainer">
                            <div className="systemLabel">System</div>
                            <SystemView data={aData}/>
                        </div>
                        <div className="transactionViewContainer">
                            <div className="transactionLabel">Transaction</div>
                            <TransactionView model={oTransactionModel}/>
                        </div>
                        <div className="BackupViewContainer">
                            <div className="transactionLabel">Backup</div>
                            <BackupView data={bData}/>
                        </div>
                    </div>
                </div>
            </div>);
    }

});

module.exports = {
    view: ApplicationController,
    events: Events
};

//<TransactionView />

