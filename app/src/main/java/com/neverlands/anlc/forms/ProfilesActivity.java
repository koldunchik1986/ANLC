package com.neverlands.anlc.forms;

import android.app.Activity;
import android.os.Bundle;
import com.neverlands.anlc.auth.AuthRepository;
import com.neverlands.anlc.auth.ProfileManager;

import java.util.List;
import java.util.stream.Collectors;

public class ProfilesActivity extends Activity {
    private android.widget.LinearLayout layout;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            layout = new android.widget.LinearLayout(this);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);
            setContentView(layout);
            AuthRepository.INSTANCE.init(getApplicationContext());
        }

        @Override
        protected void onResume() {
            super.onResume();
            layout.removeAllViews();

            // Принудительно обновляем список профилей
            List<ProfileManager.Profile> profiles = ProfileManager.INSTANCE.getProfiles();

            if (profiles.isEmpty()) {
                android.widget.TextView emptyText = new android.widget.TextView(this);
                emptyText.setText("Нет профилей. Создайте новый.");
                emptyText.setTextSize(18);
                emptyText.setPadding(0, 0, 0, 32);
                layout.addView(emptyText);
            } else {
                android.widget.TextView listLabel = new android.widget.TextView(this);
                listLabel.setText("Выберите профиль:");
                listLabel.setTextSize(16);
                listLabel.setPadding(0, 0, 0, 16);
                layout.addView(listLabel);

                android.widget.ListView listView = new android.widget.ListView(this);
                android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, profiles.stream().map(p -> p.getLogin()).collect(Collectors.toList()));
                listView.setAdapter(adapter);
                layout.addView(listView);

                // Кнопка входа
                android.widget.Button loginButton = new android.widget.Button(this);
                loginButton.setText("Войти в выбранный профиль");
                loginButton.setTextSize(16);
                layout.addView(loginButton);

                final int[] selectedIndex = {-1};
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    selectedIndex[0] = position;
                });

                loginButton.setOnClickListener(v -> {
                    if (selectedIndex[0] >= 0 && selectedIndex[0] < profiles.size()) {
                        ProfileManager.Profile profile = profiles.get(selectedIndex[0]);
                        if (profile != null) {
                            android.widget.Toast.makeText(this, "Авторизация...", android.widget.Toast.LENGTH_SHORT).show();
                            AuthRepository.INSTANCE.authorizeAsync(profile, (result, body, error) -> {
                                handleAuthResult(result, body, error, profile);
                                return null;
                            });
                        } else {
                            android.widget.Toast.makeText(this, "Профиль не найден", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        android.widget.Toast.makeText(this, "Выберите профиль", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Кнопка создания профиля всегда внизу
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

    private void handleAuthResult(int result, String body, String error, ProfileManager.Profile profile) {
        if (result == 0) {
            android.widget.Toast.makeText(this, "Авторизация успешна", android.widget.Toast.LENGTH_SHORT).show();
            AuthRepository.INSTANCE.startSessionHeartbeat(profile);
            android.content.Intent intent = new android.content.Intent(this, com.neverlands.anlc.abforms.MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            android.widget.Toast.makeText(this, "Ошибка авторизации: " + error, android.widget.Toast.LENGTH_LONG).show();
        }
    }
}
