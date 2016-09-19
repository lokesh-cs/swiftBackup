var React = require('react');
var _ = require('lodash');
var $ = require('jquery');

var EventBus = require('../libraries/eventdispatcher/EventDispatcher');

var MockDialogHeader = require('./../mock/mock-data-for-dialog-table-header');

var Events = {};

var DashboardTransactionInfoDialogView = React.createClass({

    propTypes: {
        data: React.PropTypes.array
    },

    getDialogRows: function () {
        var aData = this.props.data;

        var aDataDOM = [];
        var that = this;
        _.forEach(aData, function (oData, iIndex) {
            var sTransactionId = oData.id;
            var sUser = oData.user;
            var sLog = "Log link...";
            var sUseCase = oData.useCase;
            var sStartTime = oData.startTime;
            var sTurnAroundTime = oData.turnAroundTime;
            var sLogLink = oData.log;

            aDataDOM.push(
                <div className='logItemRow'>
                    <div className="transactionId logItemRowChild">{sTransactionId}</div>
                    <div className="user logItemRowChild">{sUser}</div>
                    <div className="useCase logItemRowChild" title={sUseCase}>{sUseCase}</div>
                    <div className="startTime logItemRowChild">{sStartTime}</div>
                    <div className="turnAroundTime logItemRowChild">{sTurnAroundTime}</div>

                    <div className="log logItemRowChild"
                         onClick={that.handleLogClicked.bind(this, sLogLink)}>{sLog}</div>
                </div>
            )
        });
        return aDataDOM;
    },

    handleLogClicked: function (oLogData) {
        var sLogData = JSON.stringify(oLogData, null, '\t');
        sLogData = sLogData.replace(/,/g, ',<Br/>');
        sLogData = sLogData.replace(/\\r\\n\\t/g, ',<Br/>');
        var sDataDom = "<p>" + sLogData + "</p>";
        var win = window.open("data:text/html," + encodeURIComponent(sDataDom), "_blank");
        win.focus();
    },

    getHeaderDOM: function () {
        var aHeaderViews = [];

        _.forEach(MockDialogHeader, function (oHeader) {
            var sClassName = oHeader.className + " dialogHeaderLabel";
            aHeaderViews.push(<div className={sClassName}>{oHeader.label}</div>);
        });

        return (<div className="dialogHeaderContainer">{aHeaderViews}</div>);
    },


    render: function () {

        var aDialogRow = this.getDialogRows();
        var oHeader = this.getHeaderDOM();
        return (
            <div className='dialogContainer'>
                {oHeader}
                {aDialogRow}
            </div>
        );
    }
});

module.exports = {
    view: DashboardTransactionInfoDialogView,
    events: Events
};