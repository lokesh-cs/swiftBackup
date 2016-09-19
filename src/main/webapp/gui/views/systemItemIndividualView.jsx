var React = require('react');
var EventBus = require('../libraries/eventdispatcher/EventDispatcher');
var _ = require('lodash');
var SystemIndividualItemRowView = require('./systemItemIndividualRowView.jsx').view;

var Events = {};

var systemItemIndividualView = React.createClass({

    propTypes: {
        item: React.PropTypes.object
    },
    // make it recurssion
    getChildNodeDom: function (oItem) {

        var oChildItems = oItem.childNodes;
        var aChildDom = [];
        var _this = this;

        _.forEach(oChildItems, function (oChildItem) {

            aChildDom.push(
                <SystemIndividualItemRowView item={oChildItem}/>
            );

            if (oChildItem.childNodes != undefined) {
                var visible = (oChildItem.isChildVisible) ? 'childDomNodes' : 'childDomNodes hideChildNodes';
                aChildDom = _.concat(aChildDom, <div className={visible}>
                        {_this.getChildNodeDom(oChildItem)}
                    </div>
                );
            }

        });

        return aChildDom;
    },


    render: function () {

        var oItem = this.props.item;
        var aChildNodesDom = this.getChildNodeDom(oItem);
        var sChildNodeClassName = (oItem.isChildVisible) ? 'childDomNodes' : 'childDomNodes hideChildNodes';

        return (
            <div className='systemItemIndividualViewWrapper'>
                <SystemIndividualItemRowView item={oItem}/>
                <div className={sChildNodeClassName}>
                    {aChildNodesDom}
                </div>
            </div>
        );
    }

});

module.exports = {
    view: systemItemIndividualView,
    event: Events
};