package com.tmstudio.goon;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewConfiguration;
import android.widget.Scroller;

/*
 * Flip different views. Taken from http://android-projects.de/2011/01/04/android-homescreen-view-flipper/
 */

public class ViewFlipper extends ViewGroup {
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mScrollX = 0;
    private int mCurrentScreen = 0;

    private float mLastMotionX;

    private static final String LOG_TAG = "DragableSpace";

    private static final int SNAP_VELOCITY = 1000;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    private int mTouchState = TOUCH_STATE_REST;

    private int mTouchSlop = 0;

    public ViewFlipper(Context context) {
        super(context);
        mScroller = new Scroller(context);

        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        this.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.FILL_PARENT));

       setWillNotDraw(false);
       requestDisallowInterceptTouchEvent(true);
    }


    public ViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DragableSpace);
        mCurrentScreen = a.getInteger(R.styleable.DragableSpace_default_screen, 0);

        mScroller = new Scroller(context);
         
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        this.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT ,
                    ViewGroup.LayoutParams.FILL_PARENT));

        setWillNotDraw(false);
        requestDisallowInterceptTouchEvent(true);
    }


    @Override 
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         */

        /*
         * Shortcut the most recurring case: the user is in the dragging state
         * and he is moving his finger. We want to intercept this motion.
         */
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE)
                && (mTouchState != TOUCH_STATE_REST)) {
            return true;
                }

        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                 * Locally do absolute value. mLastMotionX is set to the y value
                 * of the down event.
                 */
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                boolean xMoved = xDiff > mTouchSlop + 50;

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = TOUCH_STATE_SCROLLING;
                }
                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;
                /*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 */
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Release the drag
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mTouchState != TOUCH_STATE_REST;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        final int action = event.getAction();
        final float x = event.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.i(LOG_TAG, "event : down");
                /*
                 * If being flinged and user touches, stop the fling. isFinished
                 * will be false if being flinged.
                 */
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionX = x;

                break;
            case MotionEvent.ACTION_MOVE:
                // Scroll to follow the motion event
                final int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;

                if (deltaX < 0) {
                    if (mScrollX > 0) {
                        BadScrollHelp.setScrollX(deltaX);
                        scrollBy(Math.max(-mScrollX, deltaX), 0);
                    }
                } else if (deltaX > 0) {
                    final int availableToScroll = getChildAt(getChildCount() - 1)
                        .getRight()
                        - mScrollX - getWidth();
                    if (availableToScroll > 0) {
                        BadScrollHelp.setScrollX(deltaX);
                        scrollBy(Math.min(availableToScroll, deltaX), 0);
                    }
                }

                // Request a redraw
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                Log.i(LOG_TAG, "event : up");
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int velocityX = (int) velocityTracker.getXVelocity();

                if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
                    // Fling hard enough to move left
                    snapToScreen(mCurrentScreen - 1);
                } else if (velocityX < -SNAP_VELOCITY
                        && mCurrentScreen < getChildCount() - 1) {
                    // Fling hard enough to move right
                    snapToScreen(mCurrentScreen + 1);
                } else {
                    snapToDestination();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                mTouchState = TOUCH_STATE_REST;
                //neu unten
                invalidate();  // Request redraw
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(LOG_TAG, "event : cancel");
                mTouchState = TOUCH_STATE_REST;
        }
        mScrollX = this.getScrollX();

        return true;
    }

    private void snapToDestination() {
        final int screenWidth = getWidth();
        final int whichScreen = (mScrollX + (screenWidth / 2)) / screenWidth;
        Log.i(LOG_TAG, "from des");
        snapToScreen(whichScreen);
    }

    public void snapToScreen(int whichScreen) {         
        Log.i(LOG_TAG, "snap To Screen " + whichScreen);
        mCurrentScreen = whichScreen;
        BadScrollHelp.setCurrentScreen(mCurrentScreen);
        final int newX = whichScreen * getWidth();
        final int delta = newX - mScrollX;
        mScroller.startScroll(mScrollX, 0, delta, 0, Math.abs(delta) * 2);

        invalidate();
    }

    public void setToScreen(int whichScreen) {
        Log.i(LOG_TAG, "set To Screen " + whichScreen);
        mCurrentScreen = whichScreen;
        BadScrollHelp.setCurrentScreen(mCurrentScreen);
        final int newX = whichScreen * getWidth();
        mScroller.startScroll(newX, 0, 0, 0, 10);             
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childLeft = 0;

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child
                        .getMeasuredHeight());
                childLeft += childWidth;
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            //throw new IllegalStateException("error mode.");
        }

        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            //throw new IllegalStateException("error mode.");
        }

        // The children are given the same width and height as the workspace
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        Log.i(LOG_TAG, "moving to screen "+mCurrentScreen);
        scrollTo(mCurrentScreen * width, 0);    
    }  

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mScrollX = mScroller.getCurrX();
            scrollTo(mScrollX, 0);
            postInvalidate();
        }
    }

    /**
     * Return the parceable instance to be saved
     */
    @Override
    protected Parcelable onSaveInstanceState() {
      final SavedState state = new SavedState(super.onSaveInstanceState());
      state.currentScreen = mCurrentScreen;
      return state;
    }


    /**
     * Restore the previous saved current screen
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
      SavedState savedState = (SavedState) state;
      super.onRestoreInstanceState(savedState.getSuperState());
      if (savedState.currentScreen != -1) {
        mCurrentScreen = savedState.currentScreen;
        BadScrollHelp.setCurrentScreen(mCurrentScreen);
      }
    }

    // ========================= INNER CLASSES ==============================

    public interface onViewChangedEvent{      
      void onViewChange (int currentViewIndex);
    }

    /**
     * A SavedState which save and load the current screen
     */
    public static class SavedState extends BaseSavedState {
      int currentScreen = -1;

      /**
       * Internal constructor
       * 
       * @param superState
       */
      SavedState(Parcelable superState) {
        super(superState);
      }

      /**
       * Private constructor
       * 
       * @param in
       */
      private SavedState(Parcel in) {
        super(in);
        currentScreen = in.readInt();
      }

      /**
       * Save the current screen
       */
      @Override
      public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(currentScreen);
      }

      /**
       * Return a Parcelable creator
       */
      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public SavedState createFromParcel(Parcel in) {
          return new SavedState(in);
        }

        public SavedState[] newArray(int size) {
          return new SavedState[size];
        }
      };
    }
}