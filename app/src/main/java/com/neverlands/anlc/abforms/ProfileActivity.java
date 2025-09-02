package com.neverlands.anlc.abforms;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Создание профиля игрока");
        setContentView(tv);
        // Здесь будет форма для ввода данных профиля
    }
}
