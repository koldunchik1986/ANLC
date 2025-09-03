package com.neverlands.anlc.forms;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.neverlands.anlc.R;
import com.neverlands.anlc.auth.ProfileManager;

public class ProfileActivity extends AppCompatActivity {

    private EditText profileNameEditText;
    private EditText loginEditText;
    private EditText passwordEditText;
    private CheckBox autoLoginCheckBox;
    private CheckBox autoClearCookiesCheckBox;
    private Button saveButton;
    private Button cancelButton;

    private ProfileManager.Profile currentProfile;
    private boolean isNewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();

        String profileName = getIntent().getStringExtra("profile_name");
        isNewProfile = getIntent().getBooleanExtra("is_new", false);

        if (isNewProfile) {
            currentProfile = null;
            if (actionBar != null) {
                actionBar.setTitle("Новый профиль");
            }
        } else {
            currentProfile = ProfileManager.INSTANCE.getProfiles().stream().filter(p -> p.getLogin().equals(profileName)).findFirst().orElse(null);
            if (currentProfile == null) {
                Toast.makeText(this, "Профиль не найден", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            if (actionBar != null) {
                actionBar.setTitle("Редактирование профиля");
            }
        }

        populateFields();
        setupEventHandlers();
    }

    private void initializeViews() {
        profileNameEditText = findViewById(R.id.profileNameEditText);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        autoLoginCheckBox = findViewById(R.id.autoLoginCheckBox);
        autoClearCookiesCheckBox = findViewById(R.id.autoClearCookiesCheckBox);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void populateFields() {
        if (currentProfile != null) {
            profileNameEditText.setText(currentProfile.getLogin());
            loginEditText.setText(currentProfile.getLogin());
            passwordEditText.setText(currentProfile.getPassword());
            autoLoginCheckBox.setChecked(currentProfile.getAutologin());
            autoClearCookiesCheckBox.setChecked(currentProfile.getAutoClearCookies());
        }
    }

    private void setupEventHandlers() {
        saveButton.setOnClickListener(v -> saveProfile());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveProfile() {
        String profileName = profileNameEditText.getText().toString().trim();
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        boolean autoLogin = autoLoginCheckBox.isChecked();
        boolean autoClearCookies = autoClearCookiesCheckBox.isChecked();

        if (TextUtils.isEmpty(profileName)) {
            Toast.makeText(this, "Введите имя профиля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(login)) {
            Toast.makeText(this, "Введите логин", Toast.LENGTH_SHORT).show();
            return;
        }

        ProfileManager.Profile newProfile = new ProfileManager.Profile(login, password, autoLogin, false, autoClearCookies);

        if (isNewProfile) {
            ProfileManager.INSTANCE.addProfile(newProfile);
        } else {
            ProfileManager.INSTANCE.updateProfile(currentProfile, newProfile);
        }

        Toast.makeText(this, "Профиль сохранен", Toast.LENGTH_SHORT).show();

        android.content.Intent intent = new android.content.Intent(this, ProfilesActivity.class);
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP | android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
