var MicroEvent = require("./../libraries/microEvent/MicroEvent");
var _ = require('lodash');
var alertify = require('alertifyjs');

var RequestMapping = require('./../libraries/requestmappingparser/request-mapping-parser.js');
var MockRequestMapping = require('./../mock/dashboard-request-mapping').requestMapping;
var UrlResolution = require('./../mock/UrlResolution').urlResolution;
var AjaxPromise = require('./../libraries/ajaxpromise/ajax-promise');
var BackupData = require('../views/model/backupViewProps');

var BackupStore = (function () {

    var triggerChange = function () {
        return BackupStore.trigger('change');
    };

    var backupSuccess = function (oResponse) {

        BackupData.setBackupResponseData(oResponse);
        BackupData.allRestoreEnableDisable();
        BackupData.setDialogVisibilityStatus(true);
        BackupData.setDialogData("Backup Successful");
        triggerChange();
        document.getElementById("spinner").style.display = 'none';
        document.getElementById("overlay").style.display = 'none';
    };
    var backupFail = function (oResponse) {
        console.log(JSON.stringify(oResponse));
        BackupData.setDialogVisibilityStatus(true);
        BackupData.setDialogData("Backup Fail");
        triggerChange();
        document.getElementById("spinner").style.display = 'none';
        document.getElementById("overlay").style.display = 'none';
    };
    var restoreSuccess = function (oResponse) {
        BackupData.setDialogVisibilityStatus(true);
        BackupData.setDialogData("Restore Successful");
        triggerChange();
        document.getElementById("spinner").style.display = 'none';
        document.getElementById("overlay").style.display = 'none';
    };

    var restoreFail = function (oResponse) {
        console.log(JSON.stringify(oResponse));
        BackupData.setDialogVisibilityStatus(true);
        BackupData.setDialogData("Restore Fail");
        triggerChange();
        document.getElementById("spinner").style.display = 'none';
        document.getElementById("overlay").style.display = 'none';
    };
    var _startBackup = function (str) {

        document.getElementById("spinner").style.display = 'initial';
        document.getElementById("overlay").style.display = 'initial';
        if (str == "backup") {
            AjaxPromise
                .get(RequestMapping.getRequestUrl(UrlResolution.Backup + MockRequestMapping.GetBackup, null), null, null)
                .then(backupSuccess, backupFail)
                .catch(function (err) {
                    console.error('errors in response', err);
                    document.getElementById("spinner").style.display = 'none';
                    document.getElementById("overlay").style.display = 'none';
                });
        }
        else {
            if (BackupData.getBackupResponseData().length > 0) {
                if (BackupData.getIndividualItemData(0).status == "Success" || BackupData.getIndividualItemData(0).status == "Warning") {
                    AjaxPromise
                        .get(RequestMapping.getRequestUrl(UrlResolution.Backup + MockRequestMapping.GetRestore, null), null, null)
                        .then(restoreSuccess, restoreFail)
                        .catch(function (err) {
                            console.error('errors in response', err);
                            document.getElementById("spinner").style.display = 'none';
                            document.getElementById("overlay").style.display = 'none';
                        });
                }
            }
            else {
                document.getElementById("spinner").style.display = 'none';
                document.getElementById("overlay").style.display = 'none';
                BackupData.setDialogVisibilityStatus(true);
                BackupData.setDialogData("Restore Fail.No data to restore. or Last Backup is not succeed");
                triggerChange();
            }
        }
    };

    var _startRestore = function (id) {

        document.getElementById("spinner").style.display = 'initial';
        document.getElementById("overlay").style.display = 'initial';
        AjaxPromise
            .get(RequestMapping.getRequestUrl(UrlResolution.Backup + MockRequestMapping.GetRestore + "?backup=" + id, null), null, null)
            .then(restoreSuccess, restoreFail)
            .catch(function (err) {
                console.error('errors in response', err);
                document.getElementById("spinner").style.display = 'none';
                document.getElementById("overlay").style.display = 'none';
                //res.push(response);
            });
    };

    var initSuccess = function (oResponse) {
        BackupData.setBackupResponseData(oResponse);
        BackupData.allRestoreEnableDisable();
        triggerChange();
    };

    var initFail = function (oResponse) {

        triggerChange();
    };
    var _fetchBackupData = function () {

        AjaxPromise
            .get(RequestMapping.getRequestUrl(UrlResolution.Backup + MockRequestMapping.GetInitialBckupData, null), null, null)
            .then(initSuccess, initFail)
            .catch(function (err) {
                console.error('errors in response', err);
            });
    };
    var _backupDBView = function (oItem) {
        if (BackupData.getIndividualItemData(oItem) == null) {
            document.getElementById("plus" + oItem).style.display = "none";
            document.getElementById("minus" + oItem).style.display = "none";
            document.getElementById("end" + oItem).style.display = "inline";
        }
        else {
            if (document.getElementById(oItem).style.display == 'none') {
                document.getElementById(oItem).style.display = 'inline';
                document.getElementById("plus" + oItem).style.display = "none";
                document.getElementById("minus" + oItem).style.display = "inline";
                document.getElementById("end" + oItem).style.display = "none";
            }
            else {
                document.getElementById(oItem).style.display = 'none';
                document.getElementById("plus" + oItem).style.display = "inline";
                document.getElementById("minus" + oItem).style.display = "none";
                document.getElementById("end" + oItem).style.display = "none";
            }
            BackupData.restoreEnableDisable(oItem);
        }

        triggerChange();
    }

    return {
        fetchBackupData: function () {
            _fetchBackupData();
        },
        getBackupData: function () {
            return BackupData.getBackupResponseData();
        },
        initialize: function () {
            return {
                backupData: BackupData.getBackupResponseData()
            };
        },
        startBackup: function (str) {
            _startBackup(str);
        },
        startRestore: function (id) {
            _startRestore(id);
        },
        backupDBView: function (oItem) {
            _backupDBView(oItem);
        },
        closeDialog: function () {
            BackupData.setDialogVisibilityStatus(false);
            triggerChange();
        }

    };
})();

MicroEvent.mixin(BackupStore);

module.exports = BackupStore;