var ReactDOM = require('react-dom');
var React = require('react');
var MyController = require('./controller.jsx').view;
var TransactionStore = require("./store/transactionStore");
var BackupStore = require("./store/backupStore");
var MyAction = require('./action');
var SystemStore = require('./store/systemStore');

//MyStore.fetchTransactionData();
BackupStore.fetchBackupData();
SystemStore.reloadData();

ReactDOM.render(<MyController store={TransactionStore} store1={BackupStore} store2={SystemStore}
                              action={MyAction}/>, document.querySelector('body'));

