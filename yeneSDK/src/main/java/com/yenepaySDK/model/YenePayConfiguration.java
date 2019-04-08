package com.yenepaySDK.model;

import android.app.PendingIntent;
import android.content.Context;

public class YenePayConfiguration {
    private PendingIntent globalCompletionIntent;
    private PendingIntent globalCancelIntent;
    private static YenePayConfiguration _INSTANCE;
    private YenePayConfiguration(PendingIntent completionIntent, PendingIntent globalCancelIntent) {
        this.globalCompletionIntent = completionIntent;
        this.globalCancelIntent = globalCancelIntent;
    }

    public static synchronized YenePayConfiguration getDefaultInstance(){
        if(_INSTANCE == null){
            _INSTANCE = new YenePayConfiguration(null, null);
        }
        return _INSTANCE;
    }

    public static synchronized YenePayConfiguration setDefaultInstance(YenePayConfiguration configuration){
        _INSTANCE = configuration;
        return getDefaultInstance();
    }

    public PendingIntent getGlobalCompletionIntent() {
        return globalCompletionIntent;
    }

    public PendingIntent getGlobalCancelIntent() {
        return globalCancelIntent;
    }

    public static class Builder{
        private Context context;
        private PendingIntent mGlobalCompletionIntent;
        private PendingIntent mGlobalCancelIntent;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setGlobalCompletionIntent(PendingIntent completionIntent){
            this.mGlobalCompletionIntent = completionIntent;
            return this;
        }
        public Builder setGlobalCancelIntent(PendingIntent cancelIntent){
            this.mGlobalCancelIntent = cancelIntent;
            return this;
        }

        public YenePayConfiguration build(){
            return new YenePayConfiguration(mGlobalCompletionIntent, mGlobalCancelIntent);
        }
    }
}
