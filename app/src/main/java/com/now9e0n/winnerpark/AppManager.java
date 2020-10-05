package com.now9e0n.winnerpark;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import lombok.Getter;
import lombok.Setter;

public class AppManager extends Application {

    @Getter @Setter
    private UserModel user;
    private String userFilePath;

    @Override
    public void onCreate() {
        super.onCreate();

        userFilePath = getFilesDir() + "/user";
        readUser();
    }

    private void readUser() {
        ObjectInputStream inputStream;
        try {
            File file = new File(userFilePath);
            if (file.exists()) {
                inputStream = new ObjectInputStream(new FileInputStream(file));
                user = (UserModel) inputStream.readObject();
                inputStream.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.e("File", "Read Failed", e);
        }
    }

    public void saveUser() {
        ObjectOutputStream outStream;
        try {
            outStream = new ObjectOutputStream(new FileOutputStream(userFilePath));
            outStream.writeObject(user);
            outStream.close();
        } catch (IOException e) {
            Log.e("File", "Write Failed", e);
        }
    }

    public static float getDensityRatio(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        return (float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
    }
}