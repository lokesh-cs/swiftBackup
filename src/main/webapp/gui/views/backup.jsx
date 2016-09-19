var React = require('react');
var EventBus = require('../libraries/eventdispatcher/EventDispatcher');
var _ = require('lodash');

var Dialog = require('material-ui/lib/dialog');
var BackupViewProps = require('./model/backupViewProps');

//var BackupDetailDialog = require('./BackupDetailDialog');

var Events = {
    BACKUP_CLICKED: "start_backup",
    BACKUP_DB_VIEW: "backup_db_view",
    RESTORE_CLICKED: "start_restore",
    CLOSE_DIALOG: "close_dialog"
};


var BackupView = React.createClass({
    propTypes: {
        data: React.PropTypes.object
    },
    getIndividualElementRow: function (data, oIndex) {
        var Views = [];
        if (data != null) {
            var dbData = data[oIndex];
            if (dbData == null) {
                Views.push(<div className="BackupViewItems">
                    <div className="Name">{oIndex}</div>
                    null</div>);
            }
            else {
                var dbtimestamp = dbData.timestamp;
                var dbstatus = dbData.status;
                var dbtimeTaken = dbData.timetaken;
                var dblocation = dbData.location;
                var dbresponseCode = dbData.responseCode;
                Views.push(
                    <div className="BackupViewItems">
                        <div className="Name">{oIndex}</div>
                        <div className="Timestamp">{dbtimestamp}</div>
                        <div className="Status">{dbstatus}</div>
                        <div className="Timetaken">{dbtimeTaken}</div>
                        <div className="ResponseCode">{dbresponseCode}</div>
                        <div className="Location">{dblocation}</div>
                    </div>
                );

            }
        }
        return (<div>{Views}</div>);
    },
    getBackupView: function (dbIndex) {
        var List = this.props.data;
        var Views = [];
        var data = List[dbIndex];

        if (data != null) {


            Views.push(
                <div className="BackupViewItems">
                    <div className="Name"></div>
                    <div className="Timestamp">Timestamp</div>
                    <div className="Status">Status</div>
                    <div className="Timetaken">Timetaken</div>
                    <div className="ResponseCode">R-Code</div>
                    <div className="Location">Location</div>
                </div>
            );
        }

        divStyle = {
            display: 'none'
        };
        return (<div id={(dbIndex).toString()} style={divStyle}>
            {Views}
            {this.getIndividualElementRow(data, "elasticsearch")}
            {this.getIndividualElementRow(data, "orientDb")}
            {this.getIndividualElementRow(data, "swift")}
        </div>);
    },

    startBackupRestore: function () {
        var that = this;
        return (
            <div>
                <button onClick={that.startBackup.bind(that, "backup")} className="backupButton">Backup</button>
                <button onClick={that.startBackup.bind(that, "restore")} className="backupButton">Restore</button>

            </div>
        );
    },

    startBackup: function (str) {
        EventBus.dispatch(Events.BACKUP_CLICKED, this, str);

    },
    startRestore: function (id) {

        EventBus.dispatch(Events.RESTORE_CLICKED, this, id);

    },
    showDetails: function (id) {
        //BackupDetails.showDetailsBackup(id);

        var aData = BackupViewProps.getIndividualItemData(id);

        if (aData == null) {
            aData = "No Data";
        }
        else {
            var sLogData = JSON.stringify(aData, null, '\t');

            sLogData = sLogData.replace(/\s+/g, "");
            sLogData = sLogData.replace(/"\//g, " \"/");
            sLogData = sLogData.replace(/,"/g, ",<Br/>\"");
            sLogData = sLogData.replace(/\\"/g, "\"");
            sLogData = sLogData.replace(/},{/g, "},<Br/>{");
            sLogData = sLogData.replace(/},"/g, "},<Br/>\"");
            sLogData = sLogData.replace(/":{/g, ":{<Br/>");

            var sDataDom = "<p>" + sLogData + "</p>";
            var win = window.open("data:text/html," + encodeURIComponent(sDataDom), "_blank");
            win.focus();
        }

    },

    handleCollapseExpandClick: function (str) {
        EventBus.dispatch(Events.BACKUP_DB_VIEW, this, str);
    },
    closeDialog(){
        EventBus.dispatch(Events.CLOSE_DIALOG, this);
    },

    render: function () {
        var backupRestoreButton = this.startBackupRestore();
        var oDialogContentStyle = {
            width: '300px',
        };
        var oDialogBodyStyle = {
            padding: '15px'
        };
        var displayImage = {
            display: 'none'
        };
        return (
            <div>
                {backupRestoreButton}
                <div className="backup">
                    <div className="DBbackup">
                        <div className="backupDetailTitlebar">
                            <img onClick={this.handleCollapseExpandClick.bind(this, "0")} id="minus0"
                                 style={displayImage} src="gui/images/minus.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "0")} id="end0" style={displayImage}
                                 src="gui/images/end.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "0")} id="plus0"
                                 src="gui/images/plus.gif"/>
                            <b>1</b>
                            <button onClick={this.startRestore.bind(this, 0)} className="restoreButton" id="restore0">
                                Restore
                            </button>
                            <button onClick={this.showDetails.bind(this, 0)} className="detailButton">Detail</button>
                        </div>
                        <div className="backupDetails">{this.getBackupView(0)}</div>
                    </div>
                    <div className="DBbackup">
                        <div className="backupDetailTitlebar">
                            <img onClick={this.handleCollapseExpandClick.bind(this, "1")} id="minus1"
                                 style={displayImage} src="gui/images/minus.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "1")} id="end1" style={displayImage}
                                 src="gui/images/end.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "1")} id="plus1"
                                 src="gui/images/plus.gif"/>
                            <b>2</b>
                            <button onClick={this.startRestore.bind(this, 1)} className="restoreButton" id="restore1">
                                Restore
                            </button>
                            <button onClick={this.showDetails.bind(this, 1)} className="detailButton">Detail</button>
                        </div>
                        <div className="backupDetails">{this.getBackupView(1)}</div>
                    </div>
                    <div className="DBbackup">
                        <div className="backupDetailTitlebar">
                            <img onClick={this.handleCollapseExpandClick.bind(this, "2")} id="minus2"
                                 style={displayImage} src="gui/images/minus.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "2")} id="end2" style={displayImage}
                                 src="gui/images/end.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "2")} id="plus2"
                                 src="gui/images/plus.gif"/>
                            <b>3</b>
                            <button onClick={this.startRestore.bind(this, 2)} className="restoreButton" id="restore2">
                                Restore
                            </button>
                            <button onClick={this.showDetails.bind(this, 2)} className="detailButton">Detail</button>
                        </div>
                        <div className="backupDetails">{this.getBackupView(2)}</div>
                    </div>
                    <div className="DBbackup">
                        <div className="backupDetailTitlebar">
                            <img onClick={this.handleCollapseExpandClick.bind(this, "3")} id="minus3"
                                 style={displayImage} src="gui/images/minus.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "3")} id="end3" style={displayImage}
                                 src="gui/images/end.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "3")} id="plus3"
                                 src="gui/images/plus.gif"/>
                            <b>4</b>
                            <button onClick={this.startRestore.bind(this, 3)} className="restoreButton" id="restore3">
                                Restore
                            </button>
                            <button onClick={this.showDetails.bind(this, 3)} className="detailButton">Detail</button>
                        </div>
                        <div className="backupDetails">{this.getBackupView(3)}</div>
                    </div>
                    <div className="DBbackup">
                        <div className="backupDetailTitlebar">
                            <img onClick={this.handleCollapseExpandClick.bind(this, "4")} id="minus4"
                                 style={displayImage} src="gui/images/minus.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "4")} id="end4" style={displayImage}
                                 src="gui/images/end.gif"/>
                            <img onClick={this.handleCollapseExpandClick.bind(this, "4")} id="plus4"
                                 src="gui/images/plus.gif"/>
                            <b>5</b>
                            <button onClick={this.startRestore.bind(this, 4)} className="restoreButton" id="restore4">
                                Restore
                            </button>
                            <button onClick={this.showDetails.bind(this, 4)} className="detailButton">Detail</button>
                        </div>
                        <div className="backupDetails">{this.getBackupView(4)}</div>
                    </div>

                </div>
                <div>
                    <Dialog
                        modal={false}
                        open={BackupViewProps.getDialogVisibilityStatus()}
                        contentStyle={oDialogContentStyle}
                        bodyStyle={oDialogBodyStyle}
                        onRequestClose={this.closeDialog}>
                        {BackupViewProps.getDialogData()}
                    </Dialog>
                </div>
                <div id="overlay"></div>
                <div id="spinner" class="spinner">
                    <img id="img-spinner" src="gui/images/spinner.gif" alt="Loading"/>
                </div>

            </div>
        )
    }
});

module.exports = {
    view: BackupView,
    event: Events
};
