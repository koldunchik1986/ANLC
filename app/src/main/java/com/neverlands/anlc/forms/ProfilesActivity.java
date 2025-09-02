package com.neverlands.anlc.forms;

import android.app.Activity;
import android.os.Bundle;
    // import com.neverlands.anlc.auth.AuthManager;
import com.neverlands.anlc.myprofile.UserConfig;

public class ProfilesActivity extends Activity {
    private android.widget.LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        setContentView(layout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout.removeAllViews();

        java.util.List<String> profiles = com.neverlands.anlc.myprofile.ConfigSelector.getProfileNames();

        if (profiles.isEmpty()) {
            android.widget.TextView emptyText = new android.widget.TextView(this);
            emptyText.setText("Нет профилей. Создайте новый.");
            emptyText.setTextSize(18);
            emptyText.setPadding(0, 0, 0, 32);
            layout.addView(emptyText);
        } else {
            android.widget.ListView listView = new android.widget.ListView(this);
            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profiles);
            listView.setAdapter(adapter);
            layout.addView(listView);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                String profileName = profiles.get(position);
                com.neverlands.anlc.myprofile.UserConfig profile = com.neverlands.anlc.myprofile.ConfigSelector.getProfile(profileName);
                if (profile != null) {
                    com.neverlands.anlc.AppVars.Profile = profile;
                    android.widget.Toast.makeText(this, "Авторизация...", android.widget.Toast.LENGTH_SHORT).show();
                        // Авторизация отключена: реализуйте вызов AuthManager при наличии
                } else {
                    android.widget.Toast.makeText(this, "Профиль не найден", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }

        android.widget.Button createButton = new android.widget.Button(this);
        createButton.setText("Создать профиль");
        createButton.setTextSize(16);
        layout.addView(createButton);

        createButton.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.neverlands.anlc.forms.ProfileActivity.class);
            intent.putExtra("is_new", true);
            startActivity(intent);
        });
    }
}
