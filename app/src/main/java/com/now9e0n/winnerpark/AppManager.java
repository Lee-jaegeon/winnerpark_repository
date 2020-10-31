package com.now9e0n.winnerpark;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;

public class AppManager extends Application {

    @Getter @Setter
    private User user;
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
                user = (User) inputStream.readObject();
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

    public static void activityWindowSet(Activity activity) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    public static boolean isTextEqual(EditText editText1, EditText editText2) {
        return editText1.getEditableText().toString().equals(editText2.getEditableText().toString());
    }

    public static boolean isTextEqual(EditText target, String... texts) {
        for (String text : texts)
            if (target.getEditableText().toString().equals(text)) return true;

        return false;
    }

    public static boolean isTextEqual(String target, String... texts) {
        for (String text : texts)
            if (target.equals(text)) return true;

        return false;
    }

    public static <T> T getCurrentDate(Class<T> type) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            String date = format.format(new Date());

            if (type == String.class) return (T) date;
            if (type == Date.class) return (T) format.parse(date);
            return null;
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Drawable getMyDrawable(int drawable) {
        return ResourcesCompat.getDrawable(resources, drawable, theme);
    }

    public static Drawable getReSizedDrawable(int drawableId, int width, int height) {
        Drawable drawable = getMyDrawable(drawableId);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        width = (int) (width * densityRatio);
        height = (int) (height * densityRatio);
        return new BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, width, height, true));
    }

    public static int getMyColor(int color) {
        return ResourcesCompat.getColor(resources, color, theme);
    }
}