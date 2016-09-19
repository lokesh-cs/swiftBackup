exports.requestMapping = {
    'GetTransactions': 'elastic/systemdashboard/getlogcount/',
    "GetTransactionLogById": 'elastic/systemdashboard/gettransactions?id=<%=id%>&size=<%=size%>&from=<%=from%>',
    "GetSearchedTransactionLogByStartTime": 'elastic/systemdashboard/getsearchedtransactions?id=<%=id%>&label=<%=label%>&field=<%=field%>&value=<%=value%>&value1=<%=value1%>&size=20&from=<%=from%>',
    'GetSearchedTransactionLog': 'logging/log/_search?q=<%=field%>="<%=value%>"&size=20&from=<%=from%>',

    'GetOmdData': 'smartdashboard/omdData',

    'GetBackup': 'smartdashboard/backup',
    'GetRestore': 'smartdashboard/restore',
    'GetInitialBckupData': 'smartdashboard/readBackupFile',

};