package com.capstone.petros.cmsc436msdetector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class ReactionView extends View {
    Random generator = new Random();
    Paint bubblePaint = new Paint();
    boolean destroy = false;
    int count = 10;
    ArrayList<Long> reactTime = new ArrayList<Long>();
    long startTime = System.currentTimeMillis();
    long endTime;
    float x = -1;
    float y = -1;
    int adjustWidth;
    int adjustHeight;

    double totalScore = 0;
    double leftHandScore = -1, rightHandScore = -1;
    boolean testActive = false, first = true;

    public ReactionView(Context context) {
        super(context);
        init(null, 0);
    }

    public ReactionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ReactionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }

    public void toggleTestActive(boolean active){
        testActive = active;
    }

    // Call when resetting the test.
    public void resetTest() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bubblePaint.setColor(Color.RED);
        adjustWidth = getWidth() - getWidth()/10;
        adjustHeight = getHeight() - getHeight()/10;
        if (x == -1) {
            x = getWidth()/2;
            y = getHeight()/2;
        }

        if (destroy) {
            endTime = System.currentTimeMillis();
            reactTime.add(endTime-startTime);
            startTime = System.currentTimeMillis();
            float oldX = x;
            float oldY = y;
            while (x==oldX && y==oldY) {
                x = generator.nextInt(adjustWidth - getWidth()/10) + getWidth()/10;
                y = generator.nextInt(adjustHeight - getHeight()/10) + getHeight()/10;
            }
        }
        canvas.drawCircle(x, y, getWidth()/12, bubblePaint);
    }

    private boolean isInCircle(float x, float y, float circleX, float circleY, float radius) {
        double dx = Math.pow(x - circleX, 2);
        double dy = Math.pow(y - circleY, 2);

        if ((dx + dy) < Math.pow(radius, 2)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (isInCircle(event.getX(), event.getY(), x, y, getWidth()/12)) {
                    if (count == 0) {
                        long sum = 0;
                        for (long i : reactTime) {
                            sum += i;
                        }
                        double average = (double) (sum/reactTime.size()/((double) 1000));
                        AlertDialog builder;
                        builder = new AlertDialog.Builder((ReactionActivity)getContext()).create();
                        builder.setTitle("Test Ended");
                        builder.setMessage("Average reaction time: " + average + " seconds");
                        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "Reset", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                resetReactionTest();
                            }
                        });

                        builder.show();
                    }
                    destroy = true;
                    //we only do 10 times.
                    count--;
                }
                break;

            case MotionEvent.ACTION_MOVE: case MotionEvent.ACTION_UP:
                destroy = false;
                break;
        }
        invalidate();
        return true;
    }

    private void resetReactionTest() {
        reactTime = new ArrayList<Long>();
        count = 10;
        x = -1;
        y = -1;
        destroy = false;
        startTime = System.currentTimeMillis();
        invalidate();
    }
}