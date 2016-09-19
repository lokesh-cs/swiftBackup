var React = require('react');
var EventBus = require('../libraries/eventdispatcher/EventDispatcher');
var _ = require('lodash');

var SystemItemIndividualView = require('./systemItemIndividualView.jsx').view;
var MockDataForLeftTableHeader = require('../mock/mock-data-for-left-table-header');
var MockDataForDashBoard = require('../mock/mock-data-for-dashboard');
var MockColorData = require('../mock/mock');

var Events = {};

var SystemView = React.createClass({

    propTypes: {
        data: React.PropTypes.object
    },

    getInnerSystemView: function () {
        var aSystemItemList = this.props.data;
        var aDOM = [];
        _.forEach(aSystemItemList, function (oSystemItem, iIndex) {

            if (iIndex) {
                aDOM.push(
                    <SystemItemIndividualView item={oSystemItem}/>
                );
            }
        });

        return aDOM;
    },

    getHeaderView: function () {

        var aHeaderViews = [];
        var aHeaderList = MockDataForLeftTableHeader;

        _.forEach(aHeaderList, function (oHeader) {
            var className = oHeader.className + " headerLabel";
            aHeaderViews.push(<div className={className}>{oHeader.label}</div>);
        });

        return (<div className="headerViewContainer">{aHeaderViews}</div>);
    },

    getApplicationHealthView: function () {
        var oApplicationHealthData = _.find(MockDataForDashBoard.transactionData, {id: "1"});
        var sLabel = oApplicationHealthData.label;
        var sPass = oApplicationHealthData.transaction.pass;
        var sFail = oApplicationHealthData.transaction.fail;
        var sInPrg = oApplicationHealthData.transaction.inProgress;
        var sCPU = oApplicationHealthData.cpu;
        var sMemory = oApplicationHealthData.memory;
        var sDisk = oApplicationHealthData.disk;
        var sPing = oApplicationHealthData.ping;

        var sPlayClassName = 'playIcon rightIconContainerChild';
        var sRestartClassName = 'restartIcon rightIconContainerChild';
        var sShutDownClassName = 'shutDownIcon rightIconContainerChild';

        var sBackgroundColor = MockColorData.pieChartColor.pass;
        var oStatusStyle = {
            'background-color': sBackgroundColor
        };

        var oHeaderView = this.getHeaderView();

        return <div className="applicationHealthViewContainer">
            <div className="appHealthHeader">
                <div className="appHealthHeaderText">Application Health</div>
                <div className="appHealthHeaderBody">
                    <div className="statusIcon rightIconContainerChild">
                        <div className="statusIconInner" style={oStatusStyle}>Ok</div>
                    </div>
                    <div className="thumbUpIcon rightIconContainerChild"></div>
                    <div className={sPlayClassName}></div>
                    <div className={sRestartClassName}></div>
                    <div className={sShutDownClassName}></div>
                    <div className="exitIcon rightIconContainerChild"></div>
                </div>
            </div>
        </div>

    },

    render: function () {

        var oInnerSystemView = this.getInnerSystemView();
        var oHeaderView = this.getHeaderView();
        var oApplicationHealthView = this.getApplicationHealthView();

        return (
            <div>
                {oApplicationHealthView}
                <div className='systemView'>
                    {oHeaderView}
                    {oInnerSystemView}
                </div>
            </div>
        )
    }
});

module.exports = {
    view: SystemView,
    event: Events
};