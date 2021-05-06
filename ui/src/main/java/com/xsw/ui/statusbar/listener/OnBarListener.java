package com.xsw.ui.statusbar.listener;

import com.xsw.ui.statusbar.BarProperties;

public interface OnBarListener {

    /**
     * On bar info change.
     *
     * @param barProperties the bar info
     */
    void onBarChange(BarProperties barProperties);
}
