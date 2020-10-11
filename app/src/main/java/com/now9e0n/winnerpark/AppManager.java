package com.now9e0n.winnerpark;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.core.content.res.ResourcesCompat;

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

    private static Resources resources;
    private static Resources.Theme theme;
    @Getter static float densityRatio;

    @Override
    public void onCreate() {
        super.onCreate();

        userFilePath = getFilesDir() + "/user";
        readUser();

        resources = getResources();
        theme = getTheme();

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getApplicationContext().getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        densityRatio = (float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT;
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

    public static Drawable getMyDrawable(int drawable) {
        return ResourcesCompat.getDrawable(resources, drawable, theme);
    }

    public static int getMyColor(int color) {
        return ResourcesCompat.getColor(resources, color, theme);
    }
}