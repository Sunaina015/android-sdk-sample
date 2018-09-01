package fr.voxeet.sdk.sample.main_screen;

import android.support.annotation.DrawableRes;

import voxeet.com.sdk.json.UserInfo;

/**
 * Describe the state of a un/-selected user
 */

public class UserItem {

    @DrawableRes
    private int _drawable;

    private UserInfo _user_info;
    private boolean _selected;

    private UserItem() {
    }

    public UserItem(@DrawableRes int drawable, UserInfo user_info) {
        this();

        _drawable = drawable;
        _user_info = user_info;
        _selected = false;
    }

    public int getDrawable() {
        return _drawable;
    }

    public UserInfo getUserInfo() {
        return _user_info;
    }

    public boolean isSelected() {
        return _selected;
    }

    public void setSelected(boolean selected) {
        _selected = selected;
    }
}
