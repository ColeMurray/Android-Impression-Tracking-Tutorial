package com.example.impressiontutorialstepthrough;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class VisibilityTracker {
    private static final long VISIBILITY_CHECK_DELAY_MILLIS = 100;
    private WeakHashMap<View, TrackingInfo> mTrackedViews = new WeakHashMap<>();
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private VisibilityTrackerListener mVisibilityTrackerListener;
    private boolean mIsVisibilityCheckScheduled;
    private VisibilityChecker mVisibilityChecker;
    private Handler mVisibilityHandler;
    private Runnable mVisibilityRunnable;


    public interface VisibilityTrackerListener {
        void onVisibilityChanged(List<View> visibleViews, List<View> invisibleViews);
    }

    static class TrackingInfo {
        View mRootView;
        int mMinVisiblePercent;
    }

    public VisibilityTracker(Activity activity) {
        View rootView = activity.getWindow().getDecorView();
        ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();

        mVisibilityHandler = new Handler();
        mVisibilityChecker = new VisibilityChecker();
        mVisibilityRunnable = new VisibilityRunnable();

        if (viewTreeObserver.isAlive()) {
            mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    scheduleVisibilityCheck();
                    return true;
                }
            };
            viewTreeObserver.addOnPreDrawListener(mOnPreDrawListener);
        } else {
            Log.d(VisibilityTracker.class.getSimpleName(), "Visibility tracker root view is not alive");
        }
    }

    public void addView(@NonNull View view, int minVisiblePercentageViewed) {

        TrackingInfo trackingInfo = mTrackedViews.get(view);
        if (trackingInfo == null) {
            // view is not yet being tracked
            trackingInfo = new TrackingInfo();
            mTrackedViews.put(view, trackingInfo);
            scheduleVisibilityCheck();
        }


        trackingInfo.mRootView = view;
        trackingInfo.mMinVisiblePercent = minVisiblePercentageViewed;
    }

    public void setVisibilityTrackerListener(VisibilityTrackerListener listener) {
        mVisibilityTrackerListener = listener;
    }

    public void removeVisibilityTrackerListener() {
        mVisibilityTrackerListener = null;
    }

    private void scheduleVisibilityCheck() {
        if (mIsVisibilityCheckScheduled) {
            return;
        }
        mIsVisibilityCheckScheduled = true;
        mVisibilityHandler.postDelayed(mVisibilityRunnable, VISIBILITY_CHECK_DELAY_MILLIS);
    }


    static class VisibilityChecker {
        private final Rect mClipRect = new Rect();


        boolean isVisible(@Nullable final View view, final int minPercentageViewed) {
            if (view == null || view.getVisibility() != View.VISIBLE || view.getParent() == null) {
                return false;
            }

            if (!view.getGlobalVisibleRect(mClipRect)) {
                return false;
            }

            final long visibleArea = (long) mClipRect.height() * mClipRect.width();
            final long totalViewArea = (long) view.getHeight() * view.getWidth();

            return totalViewArea > 0 && 100 * visibleArea >= minPercentageViewed * totalViewArea;

        }


    }

    class VisibilityRunnable implements Runnable {
        private final List<View> mVisibleViews;
        private final List<View> mInvisibleViews;


        VisibilityRunnable() {
            mVisibleViews = new ArrayList<>();
            mInvisibleViews = new ArrayList<>();
        }

        @Override
        public void run() {
            mIsVisibilityCheckScheduled = false;
            for (final Map.Entry<View, TrackingInfo> entry : mTrackedViews.entrySet()) {
                final View view = entry.getKey();
                final int minPercentageViewed = entry.getValue().mMinVisiblePercent;

                if (mVisibilityChecker.isVisible(view, minPercentageViewed)) {
                    mVisibleViews.add(view);
                } else
                    mInvisibleViews.add(view);
            }

            if (mVisibilityTrackerListener != null) {
                mVisibilityTrackerListener.onVisibilityChanged(mVisibleViews, mInvisibleViews);
            }

            mVisibleViews.clear();
            mInvisibleViews.clear();
        }
    }

}