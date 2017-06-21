package com.example.tm.videocut;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by odmen on 19.06.2017.
 */

public class ActivityUtil {
    public static float dpToPx(float dp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
