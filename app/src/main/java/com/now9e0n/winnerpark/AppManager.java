package com.now9e0n.winnerpark;

import android.app.Application;

import lombok.Getter;
import lombok.Setter;

public class AppManager extends Application {

    @Getter @Setter
    private UserModel user;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}