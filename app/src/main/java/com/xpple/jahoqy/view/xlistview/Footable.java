package com.xpple.jahoqy.view.xlistview;

import android.graphics.Canvas;

public interface Footable {
    boolean draw(Canvas canvas, int left, int top, int right, int bottom);

    int getHeight();
}
