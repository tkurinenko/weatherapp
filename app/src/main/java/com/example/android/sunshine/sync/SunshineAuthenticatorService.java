package com.example.android.sunshine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class SunshineAuthenticatorService extends Service {
    public SunshineAuthenticatorService() {
    }

    private SunshineAuthenticator mAuthenticator;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
      return mAuthenticator.getIBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuthenticator = new SunshineAuthenticator(this);
    }
}
