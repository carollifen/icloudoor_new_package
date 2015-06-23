package com.icloudoor.cloudoor.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Chronometer;

public class MyChronometer extends Chronometer{

    public MyChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyChronometer(Context context) {
        super(context);
    }
    
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        visibility = View.VISIBLE;
        super.onWindowVisibilityChanged(visibility);
    }
}
