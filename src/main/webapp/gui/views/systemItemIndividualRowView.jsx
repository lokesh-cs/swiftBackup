var React = require('react');
var EventBus = require('../libraries/eventdispatcher/EventDispatcher');
var _ = require('lodash');
var $ = require('jquery');

var MockColorData = require('../mock/mock');

var Events = {
    HANDLE_COLLAPSE_EXPAND_ICON_CLICKED: "handle_collapse_expand_icon_clicked"
};

var systemItemIndividualRowView = React.createClass({

    propTypes: {
        store: React.PropTypes.object,
        item: React.PropTypes.object
    },


    handleCollapseExpandClick: function (iId, oEvent) {
        var oItem = this.props.item;
        var sItemName = oItem.label;
        // if(iId!=1 && iId!=4 && iId!=5 && iId!=6 && iId!=7 && iId!=8) {
        //   EventBus.dispatch(Events.HANDLE_COLLAPSE_EXPAND_ICON_CLICKED, this, sItemName);
        //  }
        if (oItem.childNodes.length > 0) {
            EventBus.dispatch(Events.HANDLE_COLLAPSE_EXPAND_ICON_CLICKED, this, sItemName, iId);
        }
    },

    render: function () {
        var oItem = this.props.item;

        // console.log("label for port " + oItem.label);
        var sItemName = oItem.label;
        var iId = oItem.id;
        var sCPU = oItem.cpu;
        var sCPUUsed = oItem.cpuUsedDescription;

        var sMemory = oItem.memory;
        var sMemoryDescription = oItem.memoryUsedDescription;

        var sDisk = oItem.disk;
        var sDiskDescription = oItem.diskUsedDescription;
        var sPing = oItem.ping;
        var oStyle = ((iId != 1 && iId != 2 && iId != 3 && iId != 4 && iId != 5 && iId != 6 && iId != 7 && iId != 8) && (!oItem.childNodes || oItem.childNodes.length < 1)) ? {"border-top": "none"} : null;

        var sPlayClassName = 'playIcon rightIconContainerChild';
        var sRestartClassName = 'restartIcon rightIconContainerChild';
        var sShutDownClassName = 'shutDownIcon rightIconContainerChild';
        /*  if(oItem.id == 7 || oItem.id == 8){
         sPlayClassName += ' visibilityN';
         sRestartClassName += ' visibilityN';
         sShutDownClassName += ' visibilityN';
         }*/

        var sBackgroundColor = '';
        var sStatusTitle = '';
        var sStatus = oItem.status;
        if (sStatus == 'InProgress') {
            sStatusTitle = 'Warning';
            sBackgroundColor = MockColorData.pieChartColor.inProgress;
        } else if (sStatus == 'Pass') {
            sStatusTitle = 'Ok';
            sBackgroundColor = MockColorData.pieChartColor.pass;
        } else if (sStatus == 'Fail') {
            sStatusTitle = 'Down';
            sBackgroundColor = MockColorData.pieChartColor.fail;
        }

        var oStatusStyle = {
            'background-color': sBackgroundColor
        };

        return (
            <div className='systemItemIndividualView' ref='systemItemIndividualView' style={oStyle}>
                <div className="labelContainer systemItemIndividualViewChild"
                     onClick={this.handleCollapseExpandClick.bind(this, iId)}>{sItemName}</div>
                <div className="rightIconContainer systemItemIndividualViewChild">
                    <div className="statusIcon rightIconContainerChild">
                        <div className="statusIconInner" style={oStatusStyle}>{sStatusTitle}</div>
                    </div>
                    <div className="thumbUpIcon rightIconContainerChild"></div>
                    <div className={sPlayClassName}></div>
                    <div className={sRestartClassName}></div>
                    <div className={sShutDownClassName}></div>
                    <div className="exitIcon rightIconContainerChild"></div>
                    <div className="sCPU rightIconContainerChild" title={sCPUUsed}>{sCPU}</div>
                    <div className="sMemory rightIconContainerChild" title={sMemoryDescription}>{sMemory}</div>
                    <div className="sDisk rightIconContainerChild" title={sDiskDescription}>{sDisk}</div>
                    <div className="sPing rightIconContainerChild">{sPing}</div>
                </div>

            </div>
        );
    }
});

module.exports = {
    view: systemItemIndividualRowView,
    events: Events
};