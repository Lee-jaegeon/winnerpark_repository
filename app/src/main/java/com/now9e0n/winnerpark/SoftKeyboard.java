package com.now9e0n.winnerpark;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.concurrent.atomic.AtomicBoolean;

public class SoftKeyboard implements View.OnFocusChangeListener {
    private static final int CLEAR_FOCUS = 0;

    private ViewGroup layout;
    private int layoutBottom;
    private InputMethodManager inputMethodManager;
    private int[] coordinates;
    private boolean isKeyboardShow;
    private SoftKeyboardChangesThread softKeyboardThread;

    private View view;
    private int viewId;

    public SoftKeyboard(int viewId, ViewGroup layout, InputMethodManager inputMethodManager) {
        this.layout = layout;
        keyboardHideByDefault();
        initEditTexts(layout);
        this.inputMethodManager = inputMethodManager;
        this.coordinates = new int[2];
        this.isKeyboardShow = false;
        this.softKeyboardThread = new SoftKeyboardChangesThread();
        this.softKeyboardThread.start();
        this.viewId = viewId;
    }


    public void openSoftKeyboard() {
        if(!isKeyboardShow) {
            layoutBottom = getLayoutCoordinates();
            inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
            softKeyboardThread.keyboardOpened();
            isKeyboardShow = true;
        }
    }

    public void closeSoftKeyboard() {
        if(isKeyboardShow) {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            isKeyboardShow = false;
        }
    }

    public void setSoftKeyboardCallback(SoftKeyboardChanged callback) {
        softKeyboardThread.setCallback(callback);
    }

    public void unRegisterSoftKeyboardCallback() {
        softKeyboardThread.stopThread();
    }

    public interface SoftKeyboardChanged {
        void onSoftKeyboardHide();
        void onSoftKeyboardShow();
    }

    private int getLayoutCoordinates() {
        layout.getLocationOnScreen(coordinates);
        return coordinates[1] + layout.getHeight();
    }

    private void keyboardHideByDefault() {
        layout.setFocusable(true);
        layout.setFocusableInTouchMode(true);
    }

    private void initEditTexts(ViewGroup viewgroup) {
        int childCount = viewgroup.getChildCount();

        for(int i = 0; i <= childCount - 1; i++) {
            View view = viewgroup.getChildAt(i);

            if(view instanceof ViewGroup)
                initEditTexts((ViewGroup) view);

            if(view instanceof EditText) {
                EditText editText = (EditText) view;
                editText.setOnFocusChangeListener(this);
                editText.setCursorVisible(true);
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if(hasFocus && view.getId() == viewId) {
            this.view = view;

            if(!isKeyboardShow) {
                layoutBottom = getLayoutCoordinates();
                softKeyboardThread.keyboardOpened();
                isKeyboardShow = true;
            }
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            if (m.what == CLEAR_FOCUS) {
                if(view != null) {
                    view.clearFocus();
                    view = null;
                }
            }
        }
    };

    private class SoftKeyboardChangesThread extends Thread {
        private AtomicBoolean started;
        private SoftKeyboardChanged callback;

        public SoftKeyboardChangesThread() {
            started = new AtomicBoolean(true);
        }

        public void setCallback(SoftKeyboardChanged callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            while(started.get()) {
                // Wait until keyboard is requested to open
                synchronized(this) {
                    try { wait(); }
                    catch (InterruptedException e) {
                        Log.e("SoftKeyboard", "Wait Open Request Failed", e);
                    }
                }

                int currentBottomLocation = getLayoutCoordinates();

                // There is some lag between open soft-keyboard function and when it really appears.
                while(currentBottomLocation == layoutBottom && started.get())
                    currentBottomLocation = getLayoutCoordinates();

                if(started.get())
                    callback.onSoftKeyboardShow();

                // When keyboard is opened from EditText, initial bottom location is greater than layoutBottom
                // and at some moment equals layoutBottom.
                // That broke the previous logic, so I added this new loop to handle this.
                while(currentBottomLocation >= layoutBottom && started.get())
                    currentBottomLocation = getLayoutCoordinates();

                // Now Keyboard is shown, keep checking layout dimensions until keyboard is gone
                while(currentBottomLocation != layoutBottom && started.get()) {
                    synchronized(this) {
                        try { wait(500); }
                        catch (InterruptedException e) {
                            Log.e("SoftKeyboard", "Check Layout Dimensions Failed", e);
                        }
                    }
                    currentBottomLocation = getLayoutCoordinates();
                }

                if(started.get())
                    callback.onSoftKeyboardHide();

                // if keyboard has been opened clicking and EditText.
                if(isKeyboardShow && started.get())
                    isKeyboardShow = false;

                // if an EditText is focused, remove its focus (on UI thread)
                if(started.get())
                    handler.obtainMessage(CLEAR_FOCUS).sendToTarget();
            }
        }

        public void keyboardOpened() {
            synchronized(this) { notify(); }
        }

        public void stopThread() {
            synchronized(this) {
                started.set(false);
                notify();
            }
        }

    }
}