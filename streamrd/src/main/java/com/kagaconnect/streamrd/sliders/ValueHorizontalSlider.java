package com.kagaconnect.streamrd.sliders;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ValueHorizontalSlider  extends HorizontalScrollView {
    private static final String TAG = "Camera";
    private final Context context;
    private String[] values;
    private String currentValue;
    private float scrollEndedAt = 0;

    public ValueHorizontalSlider(final Context context, String[] values) {
        super(context);
        this.values = values;
        this.context = context;
        this.currentValue = values[0];

        setHorizontalScrollBarEnabled(false);

        LinearLayout l = new LinearLayout(context, null);
        l.setOrientation(LinearLayout.VERTICAL);

        l.addView(getLinearLayout());

        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,1));
        view.setBackgroundColor(Color.WHITE);
        view.setPadding(0, 0, 0, 0);
        l.addView(view);

        addView(l);

        View newView = getSelectedView(0);
        setSelectedColor(newView, Color.parseColor("#4285f4"));
    }

    public ValueHorizontalSlider(final Context context) {
        super(context);
        this.context = context;
        setHorizontalScrollBarEnabled(false);
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        final float scrollPosition_new = getScrollPosition(scrollX);
        final float scrollPosition_old = getScrollPosition(oldScrollX);

        final View newView = getSelectedView(scrollPosition_new);
        final View oldView = getSelectedView(scrollPosition_old);


        String newValue = getSelectedValue(scrollPosition_new);
        if (!Objects.equals(newValue, currentValue)) {
            onValueChange(newValue, currentValue);
            currentValue = newValue;

            setSelectedColor(oldView, Color.WHITE);
            setSelectedColor(newView,  Color.parseColor("#4285f4"));
        }

        onScroll(scrollPosition_new);

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public synchronized void run() {
                float newScrollPosition = getScrollPosition(getScrollX());
                if (scrollPosition_new == newScrollPosition && newScrollPosition != scrollEndedAt) {
                    scrollEndedAt = newScrollPosition;
                    onScrollEnd(newScrollPosition);
                }
            }
        }, 100);
    }

    private void setSelectedColor(View layout, int color){
        if(layout != null && ((LinearLayout)layout).getChildCount() > 1){
            TextView textView = (TextView)((LinearLayout)layout).getChildAt(0);
            TextView lineView = (TextView)((LinearLayout)layout).getChildAt(1);

            if(textView != null){
                textView.setTextColor(color);
            }
            if(lineView != null){
                lineView.setTextColor(color);
            }
        }
    }

    public float stringToValue(String string) {
        return Float.parseFloat(string);
    }

    public void onScrollEnd(float scrollPosition) {
    }

    public void onScroll(float scrollPosition) {
    }

    public void onValueChange(String newValue, String oldValue) {
        Log.d(TAG, newValue);
    }

    public String getSelectedValue() {
        return getSelectedValue(getScrollPosition());
    }

    String getSelectedValue(float scrollPosition) {
        int index;
        if (scrollPosition >= 1)
            index = values.length - 1;
        else
            index = (int) (values.length * scrollPosition);
        return values[index];
    }

    View getSelectedView(float scrollPosition) {
        int index;
        if (scrollPosition >= 1)
            index = values.length - 1;
        else
            index = (int) (values.length * scrollPosition);


        LinearLayout l = (LinearLayout)getChildAt(0);
        LinearLayout layout = (LinearLayout)l.getChildAt(0);

        return  layout.getChildAt(index);
    }

    public float getScrollPosition() {
        return getScrollPosition(getScrollX());
    }

    float getScrollPosition(int scrollX) {
        float maxScrollPosition = getChildAt(0).getWidth() - getWidth();
        float scrollPosition = scrollX / maxScrollPosition;
        return scrollPosition > 1 ? 1 : scrollPosition < 0 ? 0 : scrollPosition;
    }

    private LinearLayout getLinearLayout() {
        int textPadding = 30;
        int textSize = 12;

        LinearLayout layout = new LinearLayout(context, null);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        for (String value : values) {
            LinearLayout l = new LinearLayout(context, null);
            l.setOrientation(LinearLayout.VERTICAL);

            TextView textView = new TextView(context);
            textView.setText(value);
            textView.setPadding(textPadding, 0, textPadding, 0);
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(textSize);
            textView.setShadowLayer(3, 1, 1, Color.BLACK);

            l.addView(textView);

            TextView lineView = new TextView(context);
            lineView.setText("|");
            lineView.setGravity(Gravity.CENTER_HORIZONTAL);
            lineView.setPadding(textPadding, -14, textPadding, 0);
            lineView.setTextColor(Color.WHITE);
            lineView.setTextSize(8);
            lineView.setShadowLayer(3, 1, 1, Color.BLACK);

            l.addView(lineView);

            layout.addView(l);
        }

        return layout;
    }
}
