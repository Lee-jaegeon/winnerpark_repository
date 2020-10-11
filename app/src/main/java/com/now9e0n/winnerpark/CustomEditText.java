package com.now9e0n.winnerpark;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;

import static com.now9e0n.winnerpark.AppManager.getMyDrawable;

public class CustomEditText extends AppCompatEditText implements TextWatcher, View.OnTouchListener, View.OnFocusChangeListener {
    private Drawable clearDrawable;
    private int length;

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        clearDrawable = getMyDrawable(R.drawable.btn_clear);
        length = (int) (18 * AppManager.getDensityRatio());
        clearDrawable.setBounds(0, 0, length, length);
        clearDrawable.setTint(Color.BLACK);
        setClearIconVisible(false);

        setOnFocusChangeListener(this);
        setOnTouchListener(this);
        addTextChangedListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus) setClearIconVisible(getEditableText().length() > 0);
        else setClearIconVisible(false);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();

        if (clearDrawable.isVisible() && x > getWidth() - getPaddingRight() - length) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                getEditableText().clear();

            return true;
        }

        else return false;
    }

    @Override
    public final void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isFocused()) setClearIconVisible(s.length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    private void setClearIconVisible(boolean visible) {
        clearDrawable.setVisible(visible, false);
        setCompoundDrawables(null, null, visible ? clearDrawable : null, null);
    }
}