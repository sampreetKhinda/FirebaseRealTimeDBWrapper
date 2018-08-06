package com.yapapp.firebaserealtimedbwrapper.firebaseRealTimeDB;

import android.app.Activity;
import android.content.Context;

import com.yapapp.firebaserealtimedbwrapper.util.Log;

public class Builder {
    public DataChangeListner dataChangeListener;
    public Activity activity;
    public Params params;
    public int limitToFirst = -1;
    public int limitToLast = -1;
    public int startAt = -1;
    public int endAt = -1;
    public int equalTo = -1;
    public String baseUrl;

    public Builder(Query query, String path) {
        params = new Params();
        params.setQuery(query);
        params.setPath(path);
    }

    public Builder setValue(String value) {
        params.setValue(value);
        return this;
    }

    public Builder setData(Object data) {
        params.setData(data);
        return this;
    }

    public Builder with(Activity activity) {
        this.activity = activity;
        return this;
    }

    public Builder taskId(int taskId) {
        params.setTaskId(taskId);
        return this;
    }
    public Builder setClass(Class<?> className) {
        params.setClassName(className);
        return this;
    }

    public Builder sortAccording(SortType sortType) {
        params.setSortType(sortType);
        return this;
    }

    public Builder enableLog(Boolean enableLog) {
        Log.setLogs(enableLog);
        return this;
    }

    public Builder email(String email) {
        params.setEmail(email);
        return this;
    }

    public Builder displayName(String displayName){
        params.setDisplayName(displayName);
        return this;
    }
    public Builder password(String password) {
        params.setPassword(password);
        return this;
    }

    public Builder setCallback(DataChangeListner dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
        return this;
    }

    public Builder baseUrl(String baseUrl) {
        this.params.setBaseUrl(baseUrl);
        return this;
    }

    public RealTimeDataBaseClient build() {
        return new RealTimeDataBaseClient(this);
    }
}