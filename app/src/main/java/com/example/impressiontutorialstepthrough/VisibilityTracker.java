package com.example.impressiontutorialstepthrough;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.List;
import java.util.WeakHashMap;

public class VisibilityTracker {
    private WeakHashMap<View, TrackingInfo> mTrackedViews = new WeakHashMap<>();
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private VisibilityTrackerListener mVisibilityTrackerListener;

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

    private void setVisibilityTrackerListener(VisibilityTrackerListener listener) {
        mVisibilityTrackerListener = listener;
    }

    private void removeVisibilityTrackerListener() {
        mVisibilityTrackerListener = null;
    }

    private void scheduleVisibilityCheck() {
        // TODO:
    }

}