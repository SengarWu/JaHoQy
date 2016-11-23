package com.xpple.jahoqy.view.xlistview;

import android.content.Context;
import android.graphics.Canvas;

public interface Headable {
    int STATE_REST = 0;
    int STATE_LOADING = 1;
    int STATE_SUCCESS = 2;
    int STATE_FAIL = 3;
    int STATE_PULL = 4;
    int STATE_RELEASE = 5;

    void stateChange(int state, String msg);

    int getState();

    boolean draw(Canvas canvas, int left, int top, int right, int bottom);

    void toastResultInOtherWay(Context context, int state);

    int getHeight();
}
