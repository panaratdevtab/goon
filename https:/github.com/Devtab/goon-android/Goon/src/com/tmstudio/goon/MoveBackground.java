package com.tmstudio.goon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/*
 * LinearLayout which moves the background image by touchEvents. 
 * Taken from: http://eagle.phys.utk.edu/guidry/android/DraggableSymbols.html
 * and changed a little bit
*/

public class MoveBackground extends LinearLayout{

    private Drawable background;     // background picture
    private float X = -300;             // Current x coordinate, upper left corner - 300
    private int scroll;

    public MoveBackground(Context context) {
        super(context);
    }

    public MoveBackground(Context context, AttributeSet attrs) {
        super(context);

        background = context.getResources().getDrawable(R.drawable.baby);
//just for tests, not really optimized yet :) 
    background.setBounds(0,0,1000,getResources().getDisplayMetrics().heightPixels);

        setWillNotDraw(false);
    }

    /* 
     * Don't need these methods, maybe later for gesture improvements
     */
    /*
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        switch (action) {
            // MotionEvent class constant signifying a finger-drag event  
            case MotionEvent.ACTION_MOVE: {
                // Request a redraw
                invalidate();
                break;
            }
            // MotionEvent class constant signifying a finger-up event
            case MotionEvent.ACTION_UP:
                invalidate();  // Request redraw
                break;
        }
        return true;
    }
    */


    // This method will be called each time the screen is redrawn. 
    // When to redraw is under Android control, but we can request a redraw 
    // using the method invalidate() inherited from the View superclass.

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);    

     // get the object movement
        if (BadScrollHelp.getScrollX() != scroll){
            //reduce the scrolling
            X -= scroll / 5;
            scroll = BadScrollHelp.getScrollX();
        }

        // Draw background image at its current locations
        canvas.save();
        canvas.translate(X,0);
        background.draw(canvas);
        canvas.restore();
    }
}