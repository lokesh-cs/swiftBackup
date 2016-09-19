var _ = require('lodash');

var BackupViewProps = (function () {

    var backupResponseData = [];
    var dialogData = "";
    var dialogVisibility = false;

    return {
        setBackupResponseData: function (data) {
            backupResponseData.length = 0;
            backupResponseData = data;
        },

        getBackupResponseData: function () {

            return backupResponseData;
        },

        getIndividualItemData: function (id) {

            return backupResponseData[id];
        },

        restoreEnableDisable: function (id) {
            if (backupResponseData[id] != null) {
                if (backupResponseData[id].status == "Success" || backupResponseData[id].status == "Warning") {
                    document.getElementById("restore" + id).style.pointerEvents = "visible";
                    document.getElementById("restore" + id).style.backgroundColor = "#4CC417";
                }
                else {
                    document.getElementById("restore" + id).style.pointerEvents = "none";
                    document.getElementById("restore" + id).style.backgroundColor = "lightgray";
                }
            }
        },

        allRestoreEnableDisable: function () {
            for (var id = 0; id < 5; id++) {
                if (backupResponseData[id] != null) {
                    if (backupResponseData[id].status == "Success" || backupResponseData[id].status == "Warning") {
                        document.getElementById("restore" + id).style.pointerEvents = "visible";
                        document.getElementById("restore" + id).style.backgroundColor = "#4CC417";
                    }
                    else {
                        document.getElementById("restore" + id).style.pointerEvents = "none";
                        document.getElementById("restore" + id).style.backgroundColor = "lightgray";
                    }
                }
            }
        },

        setDialogVisibilityStatus: function (visibility) {
            dialogVisibility = visibility;
        },

        getDialogVisibilityStatus: function () {
            return dialogVisibility;
        },

        setDialogData: function (data) {
            dialogData = data;
        },

        getDialogData: function () {
            return dialogData
        },

    };

})();

module.exports = BackupViewProps;
