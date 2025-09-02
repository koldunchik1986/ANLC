package com.neverlands.anlc.forms;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.neverlands.anlc.AppVars;
import com.neverlands.anlc.R;
import com.neverlands.anlc.myprofile.ConfigSelector;
import com.neverlands.anlc.myprofile.UserConfig;

/**
 * Активность для редактирования профиля пользователя.
 */
public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";

    private EditText profileNameEditText;
    private EditText loginEditText;
    private EditText passwordEditText;
    private CheckBox savePasswordCheckBox;
    private CheckBox autoLoginCheckBox;
    private EditText proxyHostEditText;
    private EditText proxyPortEditText;
    private EditText proxyUserEditText;
    private EditText proxyPasswordEditText;
    private CheckBox useProxyCheckBox;
    private Button saveButton;
    private Button cancelButton;

    private UserConfig currentProfile;
    private boolean isNewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Настройка ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Профиль");
        }

        // Инициализация UI элементов
        initializeViews();

        // Получаем данные из Intent
        String profileName = getIntent().getStringExtra("profile_name");
        isNewProfile = getIntent().getBooleanExtra("is_new", false);

        if (isNewProfile) {
            // Создаем новый профиль
            currentProfile = new UserConfig();
            if (actionBar != null) {
                actionBar.setTitle("Новый профиль");
            }
        } else {
            // Загружаем существующий профиль
            currentProfile = ConfigSelector.getProfile(profileName);
            if (currentProfile == null) {
                Log.e(TAG, "Profile not found: " + profileName);
                Toast.makeText(this, "Профиль не найден", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            if (actionBar != null) {
                actionBar.setTitle("Редактирование профиля");
            }
        }

        // Заполняем поля данными профиля
        populateFields();

        // Настраиваем обработчики событий
        setupEventHandlers();
    }

    /**
     * Инициализация UI элементов.
     */
    private void initializeViews() {
        profileNameEditText = findViewById(R.id.profileNameEditText);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        savePasswordCheckBox = findViewById(R.id.savePasswordCheckBox);
        autoLoginCheckBox = findViewById(R.id.autoLoginCheckBox);
        proxyHostEditText = findViewById(R.id.proxyHostEditText);
        proxyPortEditText = findViewById(R.id.proxyPortEditText);
        proxyUserEditText = findViewById(R.id.proxyUserEditText);
        proxyPasswordEditText = findViewById(R.id.proxyPasswordEditText);
        useProxyCheckBox = findViewById(R.id.useProxyCheckBox);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    /**
     * Заполнение полей данными профиля.
     */
    private void populateFields() {
        if (currentProfile != null) {
            profileNameEditText.setText(currentProfile.getProfileName());
            loginEditText.setText(currentProfile.getLogin());
            
            if (currentProfile.isSavePassword()) {
                passwordEditText.setText(currentProfile.getPassword());
            }
            
            savePasswordCheckBox.setChecked(currentProfile.isSavePassword());
            autoLoginCheckBox.setChecked(currentProfile.isAutoLogin());
            
            useProxyCheckBox.setChecked(currentProfile.isUseProxy());
            proxyHostEditText.setText(currentProfile.getProxyHost());
            proxyPortEditText.setText(String.valueOf(currentProfile.getProxyPort()));
            proxyUserEditText.setText(currentProfile.getProxyUser());
            proxyPasswordEditText.setText(currentProfile.getProxyPassword());
            
            updateProxyFieldsState();
        }
    }

    /**
     * Настройка обработчиков событий.
     */
    private void setupEventHandlers() {
        useProxyCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateProxyFieldsState());
        
        saveButton.setOnClickListener(v -> saveProfile());
        
        cancelButton.setOnClickListener(v -> finish());
    }

    /**
     * Обновление состояния полей прокси в зависимости от чекбокса.
     */
    private void updateProxyFieldsState() {
        boolean useProxy = useProxyCheckBox.isChecked();
        
        proxyHostEditText.setEnabled(useProxy);
        proxyPortEditText.setEnabled(useProxy);
        proxyUserEditText.setEnabled(useProxy);
        proxyPasswordEditText.setEnabled(useProxy);
    }

    /**
     * Сохранение профиля.
     */
    private void saveProfile() {
        String profileName = profileNameEditText.getText().toString().trim();
        String login = loginEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();
        boolean savePassword = savePasswordCheckBox.isChecked();
        boolean autoLogin = autoLoginCheckBox.isChecked();
        boolean useProxy = useProxyCheckBox.isChecked();
        String proxyHost = proxyHostEditText.getText().toString().trim();
        String proxyPortStr = proxyPortEditText.getText().toString().trim();
        String proxyUser = proxyUserEditText.getText().toString().trim();
        String proxyPassword = proxyPasswordEditText.getText().toString();

        // Проверка обязательных полей
        if (TextUtils.isEmpty(profileName)) {
            Toast.makeText(this, "Введите имя профиля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(login)) {
            Toast.makeText(this, "Введите логин", Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверка прокси
        int proxyPort = 0;
        if (useProxy) {
            if (TextUtils.isEmpty(proxyHost)) {
                Toast.makeText(this, "Введите адрес прокси", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(proxyPortStr)) {
                Toast.makeText(this, "Введите порт прокси", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                proxyPort = Integer.parseInt(proxyPortStr);
                if (proxyPort <= 0 || proxyPort > 65535) {
                    Toast.makeText(this, "Некорректный порт прокси", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Некорректный порт прокси", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Проверка на существование профиля с таким именем
        if (isNewProfile && ConfigSelector.profileExists(profileName)) {
            Toast.makeText(this, "Профиль с таким именем уже существует", Toast.LENGTH_SHORT).show();
            return;
        }

        // Обновляем данные профиля
        currentProfile.setProfileName(profileName);
        currentProfile.setUserNick(profileName); // ВАЖНО: для корректного сохранения и загрузки
        currentProfile.setLogin(login);

        if (savePassword) {
            currentProfile.setPassword(password);
            currentProfile.setUserPassword(password); // ВАЖНО: для корректной загрузки
        } else {
            currentProfile.setPassword("");
            currentProfile.setUserPassword("");
        }

        currentProfile.setSavePassword(savePassword);
        currentProfile.setAutoLogin(autoLogin);
        currentProfile.setUseProxy(useProxy);
        currentProfile.setProxyHost(proxyHost);
        currentProfile.setProxyPort(proxyPort);
        currentProfile.setProxyUser(proxyUser);
        currentProfile.setProxyPassword(proxyPassword);

        // Сохраняем профиль
        if (isNewProfile) {
            ConfigSelector.addProfile(currentProfile);
        } else {
            ConfigSelector.saveProfile(currentProfile);
        }

        // Если это активный профиль, обновляем его в AppVars
        if (AppVars.Profile != null && AppVars.Profile.getProfileName().equals(profileName)) {
            AppVars.Profile = currentProfile;
        }

    Toast.makeText(this, "Профиль сохранен", Toast.LENGTH_SHORT).show();

    // После сохранения возвращаемся к списку профилей
    android.content.Intent intent = new android.content.Intent(this, com.neverlands.anlc.forms.ProfilesActivity.class);
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