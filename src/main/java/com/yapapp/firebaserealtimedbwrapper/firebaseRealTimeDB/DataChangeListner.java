package com.yapapp.firebaserealtimedbwrapper.firebaseRealTimeDB;

import com.yapapp.firebaserealtimedbwrapper.model.ErrorModel;

/**
 * Created by anuj on 27/3/18.
 */

public interface DataChangeListner {
    <T extends Object> void onDataChange(T object, int taskId);

    void onError(ErrorModel error, String errMsg, int taskId, int statusCode);
}
