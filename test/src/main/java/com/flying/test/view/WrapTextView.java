package com.flying.test.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by liwu.shu on 2017/5/23.
 */

public class WrapTextView extends TextView {
    public WrapTextView(Context context) {
        this(context,null);
    }

    public WrapTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public WrapTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


}
