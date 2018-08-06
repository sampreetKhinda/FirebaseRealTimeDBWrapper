package com.yapapp.firebaserealtimedbwrapper.firebaseRealTimeDB

interface DataBaseCrudOperationsInterface {
    fun runQuery()
    fun deleteData()
    fun postData()
    fun putData()
    fun getDataOnce()
    fun getDataContinuous()
}