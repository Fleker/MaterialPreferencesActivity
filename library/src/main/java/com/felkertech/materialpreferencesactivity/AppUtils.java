package com.felkertech.materialpreferencesactivity;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

/**
 * Created by guest1 on 4/30/2016.
 */
public class AppUtils {
    public static boolean isTV(Context mContext) {
        UiModeManager uiModeManager = (UiModeManager) mContext.getSystemService(mContext.UI_MODE_SERVICE);
        return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
    }
    public static boolean isWatch(Context mContext) {
        UiModeManager uiModeManager = (UiModeManager) mContext.getSystemService(mContext.UI_MODE_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_WATCH;
        }
        return false;
    }
}
