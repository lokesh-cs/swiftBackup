var eventBus = require('./libraries/eventdispatcher/EventDispatcher');

var Store = require('./store/transactionStore');
var StoreBackup = require('./store/backupStore');
var StoreSystem = require('./store/systemStore');
var SystemItemIndividualRowViewEvents = require('./views/systemItemIndividualRowView.jsx').events;
var TransactionViewEvents = require('./views/transactionView.jsx').event;
var BackupViewEvents = require('./views/backup.jsx').event;
//var trackMe = MethodTracker.getTracker('AppAction');

var AppAction = (function () {

    var handleCollapseExpandIconClicked = function (oContext, sItemName, iId) {
        StoreSystem.handleCollapseExpandIconClicked(sItemName, iId);
    };

    var handlePieWrapperClicked = function (oContext, oPieChartItem) {
        Store.handlePieWrapperClicked(oPieChartItem);
    };

    var handleLabelClicked = function (oContext, label, oCurrentPie) {
        Store.handleLabelClicked(label, oCurrentPie);
    };

    var handleLoadLabelClicked = function (oContext, fromIndex, direction, field, value, value1, label, oCurrentPie) {
        Store.handleLoadLabelClicked(fromIndex, direction, field, value, value1, label, oCurrentPie);
    };

    var handleLogDialogClose = function (oContext) {
        Store.handleLogDialogClose();
    };

    var handleSearchByFieldClicked = function (oContext, field, value, value1, label, oCurrentPie) {
        Store.handleSearchByFieldClicked(field, value, value1, label, oCurrentPie);
    };

    var handleSearchStatus = function (oContext, label, oCurrentPie) {
        Store.handleSearchStatus(label, oCurrentPie);
    };


    /*Backup Action*/
    var startBackupHandler = function (oContext, str) {
        StoreBackup.startBackup(str);
    };
    var backupViewCollapseExpand = function (oContext, str) {
        StoreBackup.backupDBView(str);
    };
    var startRestoreHandler = function (oContext, id) {
        StoreBackup.startRestore(id);
    };
    var closeDialogHandler = function () {
        StoreBackup.closeDialog();
    };

    return {
        registerEvent: function () {
            eventBus.addEventListener(SystemItemIndividualRowViewEvents.HANDLE_COLLAPSE_EXPAND_ICON_CLICKED, handleCollapseExpandIconClicked);
            eventBus.addEventListener(TransactionViewEvents.HANDLE_PIE_WRAPPER_CLICKED, handlePieWrapperClicked);
            eventBus.addEventListener(TransactionViewEvents.HANDLE_LABEL_CLICKED, handleLabelClicked);
            eventBus.addEventListener(TransactionViewEvents.HANDLE_LOAD_LABEL_CLICKED, handleLoadLabelClicked);
            eventBus.addEventListener(TransactionViewEvents.HANDLE_LOG_DIALOG_CLOSE, handleLogDialogClose);
            eventBus.addEventListener(TransactionViewEvents.SEARCH_BY_FIELD_CLICKED, handleSearchByFieldClicked);
            eventBus.addEventListener(TransactionViewEvents.HANDLE_SEARCH_STATUS, handleSearchStatus);
            eventBus.addEventListener(BackupViewEvents.BACKUP_CLICKED, startBackupHandler)
            eventBus.addEventListener(BackupViewEvents.BACKUP_DB_VIEW, backupViewCollapseExpand);
            eventBus.addEventListener(BackupViewEvents.RESTORE_CLICKED, startRestoreHandler)
            eventBus.addEventListener(BackupViewEvents.CLOSE_DIALOG, closeDialogHandler);
        },

        deRegisterEvent: function () {
            eventBus.removeEventListener(SystemItemIndividualRowViewEvents.HANDLE_COLLAPSE_EXPAND_ICON_CLICKED, handleCollapseExpandIconClicked);
            eventBus.removeEventListener(TransactionViewEvents.HANDLE_PIE_WRAPPER_CLICKED, handlePieWrapperClicked);
            eventBus.removeEventListener(TransactionViewEvents.HANDLE_LABEL_CLICKED, handleLabelClicked);
            eventBus.removeEventListener(TransactionViewEvents.HANDLE_LOAD_LABEL_CLICKED, handleLoadLabelClicked);
            eventBus.removeEventListener(TransactionViewEvents.HANDLE_LOG_DIALOG_CLOSE, handleLogDialogClose);
            eventBus.removeEventListener(TransactionViewEvents.SEARCH_BY_FIELD_CLICKED, handleSearchByFieldClicked);
            eventBus.removeEventListener(TransactionViewEvents.HANDLE_SEARCH_STATUS, handleSearchStatus);
            eventBus.removeEventListener(BackupViewEvents.BACKUP_CLICKED, startBackupHandler);
            eventBus.removeEventListener(BackupViewEvents.BACKUP_DB_VIEW, backupViewCollapseExpand);
            eventBus.removeEventListener(BackupViewEvents.RESTORE_CLICKED, startRestoreHandler)
            eventBus.removeEventListener(BackupViewEvents.CLOSE_DIALOG, closeDialogHandler);

        }
    }

})();

module.exports = AppAction;