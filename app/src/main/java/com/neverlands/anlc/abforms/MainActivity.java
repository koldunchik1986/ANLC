// MainActivity.java
package com.neverlands.anlc.abforms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.neverlands.anlc.ANLCApplication;
import com.neverlands.anlc.AppConsts;
import com.neverlands.anlc.AppVars;
import com.neverlands.anlc.AutoboiState;
import com.neverlands.anlc.ChatUsersManager;
import com.neverlands.anlc.R;
import com.neverlands.anlc.abproxy.Proxy;
import com.neverlands.anlc.helpers.ExplorerHelper;
import com.neverlands.anlc.helpers.FeatureBrowserEmulation;
import com.neverlands.anlc.myprofile.UserConfig;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final ReentrantReadWriteLock lockStat = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock lockAddressStatus = new ReentrantReadWriteLock();
    public static final ReentrantReadWriteLock lockOb = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock lockBaloon = new ReentrantReadWriteLock();

    // UI компоненты
    private WebView browserGame;
    private TabHost tabHost;
    private DrawerLayout drawerLayout;
    private TextView textboxTexLog;
    private TextView statuslabelClock;
    private ProgressBar progressBar;
    private Button buttonAutoboi;
    private Button buttonNavigator;
    private CheckBox buttonDrink;
    private CheckBox buttonAutoAnswer;
    private CheckBox buttonAutoFish;
    private CheckBox buttonAutoSkin;
    private CheckBox buttonAutoRefresh;
    private CheckBox buttonWaitOpen;
    private CheckBox buttonOpenNevid;
    private CheckBox buttonSelfNevid;
    private CheckBox buttonPerenap;
    private CheckBox buttonWalkers;
    private CheckBox buttonFury;
    private CheckBox buttonDoTexLog;
    private CheckBox buttonShowPerformance;
    private TextView statuslabelAutoAdv;
    private TextView statuslabelTorgAdv;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Кнопка для ручной авторизации
        Button buttonLogin = new Button(this);
        buttonLogin.setText("Войти");
        buttonLogin.setTextSize(18);
        buttonLogin.setPadding(32, 32, 32, 32);
        ((android.widget.LinearLayout) findViewById(R.id.tabGame)).addView(buttonLogin);

        buttonLogin.setOnClickListener(v -> {
            if (AppVars.Profile == null) {
                Toast.makeText(this, "Профиль не выбран", Toast.LENGTH_SHORT).show();
                return;
            }
            buttonLogin.setEnabled(false);
            Toast.makeText(this, "Авторизация...", Toast.LENGTH_SHORT).show();
            com.neverlands.anlc.auth.AuthManager.authorizeAsync(AppVars.Profile, getApplicationContext(), new com.neverlands.anlc.auth.AuthManager.AuthCallback() {
                @Override
                public void onSuccess(java.util.List<okhttp3.Cookie> cookies) {
                    // Формируем строку куки
                    StringBuilder sb = new StringBuilder();
                    for (okhttp3.Cookie c : cookies) {
                        sb.append(c.name()).append("=").append(c.value()).append("; ");
                    }
                    String cookieHeader = sb.toString();
                    // Передаем куки в WebView
                    android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
                    cookieManager.setAcceptCookie(true);
                    cookieManager.removeAllCookies(null);
                    cookieManager.setCookie("http://neverlands.ru/main.php", cookieHeader);
                    // Загружаем main.php
                    runOnUiThread(() -> {
                        browserGame.loadUrl("http://neverlands.ru/main.php");
                        buttonLogin.setEnabled(true);
                    });
                }
                @Override
                public void onFailure(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                        buttonLogin.setEnabled(true);
                    });
                }
            });
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeUI();
        setupWebView();
        setupEventHandlers();

        AppVars.MainForm = this;

        // Если профиль не создан, сразу открываем окно создания профиля
        if (AppVars.Profile == null) {
            Intent intent = new Intent(this, com.neverlands.anlc.forms.ProfilesActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        initForm();
        formLoad();
    }

    private void initializeUI() {
        browserGame = findViewById(R.id.browserGame);
        tabHost = findViewById(R.id.tabHost);
        drawerLayout = findViewById(R.id.drawerLayout);
        textboxTexLog = findViewById(R.id.textboxTexLog);
        statuslabelClock = findViewById(R.id.statuslabelClock);
        progressBar = findViewById(R.id.progressBar);
        buttonAutoboi = findViewById(R.id.buttonAutoboi);
        buttonNavigator = findViewById(R.id.buttonNavigator);
        buttonDrink = findViewById(R.id.buttonDrink);
        buttonAutoAnswer = findViewById(R.id.buttonAutoAnswer);
        buttonAutoFish = findViewById(R.id.buttonAutoFish);
        buttonAutoSkin = findViewById(R.id.buttonAutoSkin);
        buttonAutoRefresh = findViewById(R.id.buttonAutoRefresh);
        buttonWaitOpen = findViewById(R.id.buttonWaitOpen);
        buttonOpenNevid = findViewById(R.id.buttonOpenNevid);
        buttonSelfNevid = findViewById(R.id.buttonSelfNevid);
        buttonPerenap = findViewById(R.id.buttonPerenap);
        buttonWalkers = findViewById(R.id.buttonWalkers);
        buttonFury = findViewById(R.id.buttonFury);
        buttonDoTexLog = findViewById(R.id.buttonDoTexLog);
        buttonShowPerformance = findViewById(R.id.buttonShowPerformance);
        statuslabelAutoAdv = findViewById(R.id.statuslabelAutoAdv);
        statuslabelTorgAdv = findViewById(R.id.statuslabelTorgAdv);

        tabHost.setup();

        TabHost.TabSpec tabGame = tabHost.newTabSpec("tabGame");
        tabGame.setIndicator("Игра");
        tabGame.setContent(R.id.tabGame);
        tabHost.addTab(tabGame);

        TabHost.TabSpec tabContacts = tabHost.newTabSpec("tabContacts");
        tabContacts.setIndicator("Контакты");
        tabContacts.setContent(R.id.tabContacts);
        tabHost.addTab(tabContacts);

        TabHost.TabSpec tabLogs = tabHost.newTabSpec("tabLogs");
        tabLogs.setIndicator("Логи");
        tabLogs.setContent(R.id.tabLogs);
        tabHost.addTab(tabLogs);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        FeatureBrowserEmulation.applyToWebView(browserGame);
        browserGame.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return gameBeforeNavigate(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                documentCompleted();
            }
        });

        browserGame.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        var settings = browserGame.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setUseWideViewPort(true);
        CookieManager.getInstance().setAcceptCookie(true);
    }

    private void setupEventHandlers() {
        buttonDoTexLog.setOnClickListener(v -> {
            if (AppVars.Profile != null) AppVars.Profile.setDoTexLog(buttonDoTexLog.isChecked());
        });
        buttonShowPerformance.setOnClickListener(v -> {
            if (AppVars.Profile != null) AppVars.Profile.setShowPerformance(buttonShowPerformance.isChecked());
        });

        buttonAutoboi.setOnClickListener(v -> changeButtonAutoboiState());
        buttonNavigator.setOnClickListener(v -> {
            if (AppVars.Profile != null) moveToDialog(AppVars.Profile.getMapLocation());
        });
        buttonDrink.setOnClickListener(v -> {
            AppVars.AutoDrink = buttonDrink.isChecked();
            if (AppVars.AutoDrink) reloadMainPhpInvoke();
        });

        buttonAutoAnswer.setOnClickListener(v -> {
            if (AppVars.Profile != null) AppVars.Profile.setDoAutoAnswer(buttonAutoAnswer.isChecked());
        });
        buttonAutoFish.setOnClickListener(v -> {
            if (AppVars.Profile != null) {
                AppVars.Profile.setFishAuto(buttonAutoFish.isChecked());
                AppVars.Profile.save();
                if (AppVars.Profile.isFishAuto()) {
                    AppVars.AutoFishCheckUd = true;
                    AppVars.AutoFishWearUd = false;
                    AppVars.AutoFishCheckUm = AppVars.Profile.getFishUm() == 0;
                    AppVars.Profile.setLezDoAutoboi(true);
                    AppVars.AutoFishHand1 = "";
                    AppVars.AutoFishHand1D = "";
                    AppVars.AutoFishHand2 = "";
                    AppVars.AutoFishHand2D = "";
                    AppVars.AutoFishMassa = "";
                    AppVars.AutoFishNV = 0;
                    AppVars.AutoFishDrink = false;
                    updateNavigatorOff();
                    reloadMainPhpInvoke();
                }
            }
        });

        buttonAutoSkin.setOnClickListener(v -> {
            if (AppVars.Profile != null) {
                AppVars.Profile.setSkinAuto(buttonAutoSkin.isChecked());
                AppVars.Profile.save();
                if (AppVars.Profile.isSkinAuto()) {
                    AppVars.AutoSkinCheckUm = true;
                    AppVars.AutoSkinCheckRes = true;
                    AppVars.SkinUm = false;
                    AppVars.AutoSkinCheckKnife = true;
                    AppVars.AutoSkinArmedKnife = false;
                    reloadMainPhpInvoke();
                }
            }
        });

        buttonAutoRefresh.setOnClickListener(v -> {
            AppVars.AutoRefresh = buttonAutoRefresh.isChecked();
            reloadMainPhpInvoke();
        });

        buttonWaitOpen.setOnClickListener(v -> {
            AppVars.WaitOpen = buttonWaitOpen.isChecked();
            reloadMainFrame();
        });

        buttonOpenNevid.setOnClickListener(v -> {
            AppVars.AutoOpenNevid = buttonOpenNevid.isChecked();
            if (buttonOpenNevid.isChecked()) {
                buttonWalkers.setChecked(true);
                buttonWalkers(true);
            }
        });

        buttonSelfNevid.setOnClickListener(v -> {
            AppVars.DoSelfNevid = buttonSelfNevid.isChecked();
            if (buttonSelfNevid.isChecked()) {
                buttonWalkers.setChecked(true);
                buttonWalkers(true);
            }
            reloadMainFrame();
        });

        buttonPerenap.setOnClickListener(v -> {
            AppVars.DoPerenap = buttonPerenap.isChecked();
            writeChatMsgSafe(AppVars.DoPerenap ?
                "Включен <b>режим перенападения</b>. В этом режиме любое совершенное нападение будет повторяться..." :
                "<b>Режим перенападения</b> отключен.");
        });

        buttonWalkers.setOnClickListener(v -> buttonWalkers(buttonWalkers.isChecked()));
        buttonFury.setOnClickListener(v -> {
            AppVars.DoFury = buttonFury.isChecked();
            writeChatMsgSafe(AppVars.DoFury ?
                "Включен <b>режим свитка осады</b>..." :
                "<b>Режим свитка осады</b> отключен.");
        });

        statuslabelAutoAdv.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Автореклама")
            .setMessage("Бросить рекламу в чат досрочно?")
            .setPositiveButton(android.R.string.yes, (d, w) -> AppVars.LastAdv = new Date())
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show());

        statuslabelTorgAdv.setOnClickListener(v -> new AlertDialog.Builder(this)
            .setTitle("Автореклама торговли")
            .setMessage("Бросить рекламу торговли в чат досрочно?")
            .setPositiveButton(android.R.string.yes, (d, w) -> AppVars.LastTorgAdv = new Date())
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show());

        tabHost.setOnTabChangedListener(tabId -> tabChanged());
    }

    private void initForm() {
        setTitle(AppVars.AppVersion.getProductFullVersion());
    buttonDoTexLog.setChecked(AppVars.Profile != null && AppVars.Profile.isDoTexLog());
    buttonShowPerformance.setChecked(AppVars.Profile != null && AppVars.Profile.isShowPerformance());
        buttonAutoAnswer.setChecked(AppVars.Profile != null && AppVars.Profile.isDoAutoAnswer());
        buttonAutoFish.setChecked(AppVars.Profile != null && AppVars.Profile.isFishAuto());
        buttonAutoSkin.setChecked(AppVars.Profile != null && AppVars.Profile.isSkinAuto());
        buttonAutoRefresh.setChecked(AppVars.AutoRefresh);
        buttonWaitOpen.setChecked(AppVars.WaitOpen);
        buttonOpenNevid.setChecked(AppVars.AutoOpenNevid);
        buttonSelfNevid.setChecked(AppVars.DoSelfNevid);
        buttonPerenap.setChecked(AppVars.DoPerenap);
        buttonWalkers.setChecked(AppVars.DoShowWalkers);
        buttonFury.setChecked(AppVars.DoFury);
        changeAutoboiState(AutoboiState.AutoboiOff);
        startTimers();
        AppVars.LastInitForm = new Date();
    }

    private void formLoad() {
        startProxy();
        logOn();
    }

    private void startProxy() {
        Proxy proxy = new Proxy();
        if (!proxy.start()) {
            Toast.makeText(this, R.string.message_proxy_init_error, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void startTimers() {
        handler.postDelayed(() -> {
            timerClock();
            handler.postDelayed(this::startTimers, AppConsts.CLOCK_UPDATE_INTERVAL);
        }, AppConsts.CLOCK_UPDATE_INTERVAL);
    }

    private void logOn() {
        browserGame.loadUrl(AppConsts.GAME_MAIN_URL);
    }

    private boolean gameBeforeNavigate(String url) { return false; }
    private void documentCompleted() {}
    private void tabChanged() {}

    public void timerClock() {
        statuslabelClock.setText(timeFormat.format(new Date()));
    }

    public void timerCrap() {}
    public void checkInfo() {}
    public void trayIconTick() {}

    private void changeButtonAutoboiState() {
        changeAutoboiState(AppVars.Autoboi == AutoboiState.AutoboiOff ? AutoboiState.AutoboiOn : AutoboiState.AutoboiOff);
    }

    private void changeAutoboiState(AutoboiState state) {
        AppVars.Autoboi = state;
        switch (state) {
            case AutoboiOff -> buttonAutoboi.setText("Автобой выкл");
            case AutoboiOn -> buttonAutoboi.setText("Автобой вкл");
            case AutoboiWait -> buttonAutoboi.setText("Автобой ждет");
            case AutoboiWaitForTurn -> buttonAutoboi.setText("Автобой ждет хода");
            case AutoboiWaitEndOfBoi -> buttonAutoboi.setText("Автобой ждет конца боя");
            case AutoboiWaitEndOfBoiCancel -> buttonAutoboi.setText("Автобой ждет конца боя (отмена)");
        }
        buttonAutoboi.setEnabled(state == AutoboiState.AutoboiOff || state == AutoboiState.AutoboiOn);
    }

    private void moveToDialog(String location) {}
    private void updateNavigatorOff() {}

    public void reloadMainFrame() { browserGame.reload(); }
    public void reloadMainPhpInvoke() { browserGame.loadUrl(AppConsts.GAME_MAIN_URL); }

    private void buttonWalkers(boolean check) {
        AppVars.DoShowWalkers = check;
        AppVars.MyCoordOld = "";
        AppVars.MyLocOld = "";
    }

    public void writeChatMsgSafe(String message) {}
    public void writeChatTip(String message) {}
    public void selfNevidOffSafe() {
        buttonSelfNevid.setChecked(false);
        AppVars.DoSelfNevid = false;
    }

    public void formMainClose(String message) {
        browserGame.loadUrl(AppConsts.GAME_EXIT_URL);
        AppVars.AccountError = message;
        AppVars.DoPromptExit = false;
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (AppVars.DoPromptExit) {
            new AlertDialog.Builder(this)
                .setTitle("Выход")
                .setMessage("Вы действительно хотите выйти из игры?")
                .setPositiveButton(android.R.string.yes, (d, w) -> super.onBackPressed())
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AppVars.MainForm == this) AppVars.MainForm = null;
        ChatUsersManager.save();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode >= AppConsts.REQUEST_CREATE_PROFILE && requestCode <= AppConsts.REQUEST_AUTO_LOGON) {
            if (resultCode == RESULT_OK && data != null) {
                UserConfig userConfig = (UserConfig) data.getSerializableExtra("userConfig");
                if (userConfig != null) {
                    ANLCApplication.initWithConfig(userConfig);
                    initForm();
                    formLoad();
                }
            } else {
                finish();
            }
        }
    }

    public String getServerTime() {
        return statuslabelClock.getText().toString();
    }

    public static void showFishTip() {}
}