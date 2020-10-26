package com.now9e0n.winnerpark;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatImageView;

import static com.now9e0n.winnerpark.AppManager.getMyColor;

public class EditTextIndicator extends AppCompatImageView {

    private int viewId;

    public EditTextIndicator(Context context) {
        this(context, null, 0);
    }

    public EditTextIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setBackgroundColor(getMyColor(R.color.light_gray));

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextIndicator);
        viewId = typedArray.getResourceId(R.styleable.EditTextIndicator_edit_text, 0);
        typedArray.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        EditText editText = ((View) getParent()).findViewById(viewId);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) colorAnimate(getMyColor(R.color.light_blue));
                else colorAnimate(getMyColor(R.color.light_gray));
            }
        });
    }

    private void colorAnimate(int colorTo) {
        int colorFrom = ((ColorDrawable) getBackground()).getColor();
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(animator -> setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.setDuration(750);
        colorAnimation.start();
    }
}
