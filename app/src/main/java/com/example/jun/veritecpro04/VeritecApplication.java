package com.example.jun.veritecpro04;

import android.app.Application;

import io.realm.Realm;

public class VeritecApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
