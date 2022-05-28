package com.qurbani.app;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefClass {
    protected Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    public SharedPrefClass(Context context) {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences("qurbani22", Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }

    public void setUserId(String id) {
        mSharedPreferencesEditor.putString("userId", id);
        mSharedPreferencesEditor.apply();
        mSharedPreferencesEditor.commit();
    }

    public String getReferralId() {
        return mSharedPreferences.getString("userId", "");
    }
}
