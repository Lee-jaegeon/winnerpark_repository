package com.now9e0n.winnerpark;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import lombok.Setter;

public class CircleIndicator extends LinearLayout {

    private Context context;

    private @Setter int itemMargin;
    private @Setter int animDuration;

    private int defaultCircle;
    private int selectCircle;

    private ImageView[] imageDot;

    public CircleIndicator(Context context) {
        super(context);

        this.context = context;
    }

    public CircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
    }

    public void createDot(int count, int defaultCircle, int selectCircle) {
        this.defaultCircle = defaultCircle;
        this.selectCircle = selectCircle;

        imageDot = new ImageView[count];

        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);
            params.gravity = Gravity.CENTER;

            imageDot[i] = new ImageView(context);
            imageDot[i].setLayoutParams(params);
            imageDot[i].setImageResource(defaultCircle);
            imageDot[i].setTag(imageDot[i].getId(), false);
            addView(imageDot[i]);
        }

        selectDot(0);
    }

    public void selectDot(int position) {
        for (int i = 0; i < imageDot.length; i++) {
            if (i == position) {
                imageDot[i].setImageResource(selectCircle);
                selectScaleAnim(imageDot[i],1f,1.5f);
            }
            else {
                if((boolean) imageDot[i].getTag(imageDot[i].getId())) {
                    imageDot[i].setImageResource(defaultCircle);
                    defaultScaleAnim(imageDot[i], 1.5f, 1f);
                }
            }
        }
    }

    public void selectScaleAnim(View view, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(animDuration);
        view.setTag(view.getId(),true);
        view.startAnimation(anim);
    }

    public void defaultScaleAnim(View view, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        anim.setFillAfter(true);
        anim.setDuration(animDuration);
        view.setTag(view.getId(),false);
        view.startAnimation(anim);
    }
}