package com.clock.systemui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by shuliwu on 2017/9/14.
 */

public class ContentPop {
    Context mc;

    public ContentPop(Context mc) {

    }

    public void showPopWindow(View viewParent) {
        PopupWindow popupWindow = new PopupWindow(mc);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(viewParent, Gravity.BOTTOM,0,0);
    }
}
