package com.garrisonthomas.junkapp;

import android.app.Application;

import com.firebase.client.Firebase;

public class App extends Application {

    public static final String FIREBASE_URL = "https://junkapp-43226.firebaseio.com/";

    @Override
    public void onCreate() {

        super.onCreate();

        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

    }
}
