package com.example.amministratore.library_maps;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

public class CustomMapsFragment extends SupportMapFragment {

    private View mOriginalContentView;
    private TouchableWrapper mTouchView;
    public void setTouchListener(TouchableWrapper.OnTouchListener onTouchListener) {
        mTouchView.setTouchListener(onTouchListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        mOriginalContentView = super.onCreateView(inflater, parent,
                savedInstanceState);
        mTouchView = new TouchableWrapper(getActivity());
        mTouchView.addView(mOriginalContentView);
        return mTouchView;
    }
    @Override
    public View getView() {
        return mOriginalContentView;
    }



    public static class TouchableWrapper extends FrameLayout {
        public TouchableWrapper(Context context) {
            super(context);
        }
        public void setTouchListener(OnTouchListener onTouchListener) {
            this.onTouchListener = onTouchListener;
        }
        private OnTouchListener onTouchListener;
        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    onTouchListener.onRelease();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
        public interface OnTouchListener {
            public void onTouch();
            public void onRelease();
        }
    }

}