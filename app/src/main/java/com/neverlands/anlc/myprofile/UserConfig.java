// UserConfig.java
package com.neverlands.anlc.myprofile;

import android.util.Log;
import com.neverlands.anlc.AppConsts;
import com.neverlands.anlc.helpers.DataManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс для хранения настроек пользователя, аналог UserConfig.cs
 */
public class UserConfig implements Serializable, Comparable<UserConfig> {
    /**
     * Статический метод для загрузки профиля
     * @param filePath путь к файлу профиля
     * @return загруженный UserConfig или null, если не удалось
     */
    public static UserConfig load(String filePath) {
        UserConfig config = new UserConfig();
        File file = new File(filePath);
        if (!file.exists()) return null;

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        } catch (IOException e) {
            Log.e("UserConfig", "Ошибка загрузки", e);
            return null;
        }

        config.UserNick = props.getProperty("UserNick", "");
        config.ConfigHash = props.getProperty("ConfigHash", "");
        config.UserAutoLogon = Boolean.parseBoolean(props.getProperty("UserAutoLogon", "false"));
        config.MapLocation = props.getProperty("MapLocation", "");
        config.DoHttpLog = Boolean.parseBoolean(props.getProperty("DoHttpLog", "true"));
        config.DoTexLog = Boolean.parseBoolean(props.getProperty("DoTexLog", "false"));
        config.ShowPerformance = Boolean.parseBoolean(props.getProperty("ShowPerformance", "false"));
        config.DoPromptExit = Boolean.parseBoolean(props.getProperty("DoPromptExit", "true"));
        config.DoGuamod = Boolean.parseBoolean(props.getProperty("DoGuamod", "false"));
        config.DoAutoAnswer = Boolean.parseBoolean(props.getProperty("DoAutoAnswer", "false"));
        config.FishAuto = Boolean.parseBoolean(props.getProperty("FishAuto", "false"));
        config.FishUm = Integer.parseInt(props.getProperty("FishUm", "0"));
        config.SkinAuto = Boolean.parseBoolean(props.getProperty("SkinAuto", "false"));
        config.LezDoAutoboi = Boolean.parseBoolean(props.getProperty("LezDoAutoboi", "false"));
        config.SoundEnabled = Boolean.parseBoolean(props.getProperty("SoundEnabled", "true"));

        config.AppConfigTimers.clear();
        int timerCount = Integer.parseInt(props.getProperty("TimerCount", "0"));
        for (int i = 0; i < timerCount; i++) {
            String timer = props.getProperty("Timer" + i);
            if (timer != null && !timer.isEmpty()) {
                config.AppConfigTimers.add(timer);
            }
        }

        config.CureAsk.clear();
        int askCount = Integer.parseInt(props.getProperty("CureAskCount", "0"));
        for (int i = 0; i < askCount; i++) {
            String ask = props.getProperty("CureAsk" + i);
            if (ask != null && !ask.isEmpty()) {
                config.CureAsk.add(ask);
            }
        }

        config.CureConvert = props.getProperty("CureConvert", "");
        config.CureAdv = props.getProperty("CureAdv", "");

        return config;
    }

    @Override
    public int compareTo(UserConfig other) {
        if (other == null) return 1;
        return this.UserNick.compareToIgnoreCase(other.UserNick);
    }
    // Стандартные поля профиля
    public String ProfileName = "";
    public String Login = "";
    public String Password = "";
    public boolean SavePassword = false;
    public boolean UseProxy = false;
    public String ProxyHost = "";
    public int ProxyPort = 0;
    public String ProxyUser = "";
    public String ProxyPassword = "";

    // Геттеры и сеттеры для стандартных полей
    public String getProfileName() { return ProfileName; }
    public void setProfileName(String profileName) { this.ProfileName = profileName; }

    public String getLogin() { return Login; }
    public void setLogin(String login) { this.Login = login; }

    public String getPassword() { return Password; }
    public void setPassword(String password) { this.Password = password; }

    public boolean isSavePassword() { return SavePassword; }
    public void setSavePassword(boolean savePassword) { this.SavePassword = savePassword; }

    public boolean isUseProxy() { return UseProxy; }
    public void setUseProxy(boolean useProxy) { this.UseProxy = useProxy; }

    public String getProxyHost() { return ProxyHost; }
    public void setProxyHost(String proxyHost) { this.ProxyHost = proxyHost; }

    public int getProxyPort() { return ProxyPort; }
    public void setProxyPort(int proxyPort) { this.ProxyPort = proxyPort; }

    public String getProxyUser() { return ProxyUser; }
    public void setProxyUser(String proxyUser) { this.ProxyUser = proxyUser; }

    public String getProxyPassword() { return ProxyPassword; }
    public void setProxyPassword(String proxyPassword) { this.ProxyPassword = proxyPassword; }
    // Геттеры и сеттеры для используемых свойств
    public String getUserNick() { return UserNick; }
    public void setUserNick(String userNick) { this.UserNick = userNick; }

    public String getUserPassword() { return UserPassword; }
    public void setUserPassword(String userPassword) { this.UserPassword = userPassword; }

    public String getConfigHash() { return ConfigHash; }
    public void setConfigHash(String configHash) { this.ConfigHash = configHash; }

    public boolean isAutoLogin() { return UserAutoLogon; }
    public void setAutoLogin(boolean autoLogin) { this.UserAutoLogon = autoLogin; }

    public String getMapLocation() { return MapLocation; }
    public void setMapLocation(String mapLocation) { this.MapLocation = mapLocation; }

    public boolean isDoHttpLog() { return DoHttpLog; }
    public void setDoHttpLog(boolean doHttpLog) { this.DoHttpLog = doHttpLog; }

    public boolean isDoTexLog() { return DoTexLog; }
    public void setDoTexLog(boolean doTexLog) { this.DoTexLog = doTexLog; }

    public boolean isShowPerformance() { return ShowPerformance; }
    public void setShowPerformance(boolean showPerformance) { this.ShowPerformance = showPerformance; }

    public boolean isDoPromptExit() { return DoPromptExit; }
    public void setDoPromptExit(boolean doPromptExit) { this.DoPromptExit = doPromptExit; }

    public boolean isDoGuamod() { return DoGuamod; }
    public void setDoGuamod(boolean doGuamod) { this.DoGuamod = doGuamod; }

    public boolean isDoAutoAnswer() { return DoAutoAnswer; }
    public void setDoAutoAnswer(boolean doAutoAnswer) { this.DoAutoAnswer = doAutoAnswer; }

    public boolean isFishAuto() { return FishAuto; }
    public void setFishAuto(boolean fishAuto) { this.FishAuto = fishAuto; }

    public int getFishUm() { return FishUm; }
    public void setFishUm(int fishUm) { this.FishUm = fishUm; }

    public boolean isSkinAuto() { return SkinAuto; }
    public void setSkinAuto(boolean skinAuto) { this.SkinAuto = skinAuto; }

    public boolean isLezDoAutoboi() { return LezDoAutoboi; }
    public void setLezDoAutoboi(boolean lezDoAutoboi) { this.LezDoAutoboi = lezDoAutoboi; }

    public boolean isSoundEnabled() { return SoundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) { this.SoundEnabled = soundEnabled; }
    private static final long serialVersionUID = 1L;

    public String UserNick = "";
    public String UserPassword = "";
    public String ConfigHash = "";
    public boolean UserAutoLogon = false;
    public String MapLocation = "";
    public boolean DoHttpLog = true;
    public boolean DoTexLog = false;
    public boolean ShowPerformance = false;
    public boolean DoPromptExit = true;
    public boolean DoGuamod = false;
    public boolean DoAutoAnswer = false;
    public boolean FishAuto = false;
    public int FishUm = 0;
    public boolean SkinAuto = false;
    public boolean LezDoAutoboi = false;
    public boolean SoundEnabled = true;

    public List<String> AppConfigTimers = new ArrayList<>();
    public List<String> CureAsk = new ArrayList<>();

    public String CureConvert = "";
    public String CureAdv = "";


    public void save() {
        Properties props = new Properties();
        props.setProperty("UserNick", UserNick);
        props.setProperty("ConfigHash", ConfigHash);
        props.setProperty("UserAutoLogon", String.valueOf(UserAutoLogon));
        props.setProperty("MapLocation", MapLocation);
        props.setProperty("DoHttpLog", String.valueOf(DoHttpLog));
        props.setProperty("DoTexLog", String.valueOf(DoTexLog));
        props.setProperty("ShowPerformance", String.valueOf(ShowPerformance));
        props.setProperty("DoPromptExit", String.valueOf(DoPromptExit));
        props.setProperty("DoGuamod", String.valueOf(DoGuamod));
        props.setProperty("DoAutoAnswer", String.valueOf(DoAutoAnswer));
        props.setProperty("FishAuto", String.valueOf(FishAuto));
        props.setProperty("FishUm", String.valueOf(FishUm));
        props.setProperty("SkinAuto", String.valueOf(SkinAuto));
        props.setProperty("LezDoAutoboi", String.valueOf(LezDoAutoboi));
        props.setProperty("SoundEnabled", String.valueOf(SoundEnabled));

        props.setProperty("TimerCount", String.valueOf(AppConfigTimers.size()));
        for (int i = 0; i < AppConfigTimers.size(); i++) {
            props.setProperty("Timer" + i, AppConfigTimers.get(i));
        }

        props.setProperty("CureAskCount", String.valueOf(CureAsk.size()));
        for (int i = 0; i < CureAsk.size(); i++) {
            props.setProperty("CureAsk" + i, CureAsk.get(i));
        }

        props.setProperty("CureConvert", CureConvert);
        props.setProperty("CureAdv", CureAdv);

        try {
            File dir = new File(com.neverlands.anlc.ANLCApplication.getAppContext().getFilesDir(), "profiles");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, UserNick + ".anlc");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                props.store(fos, "User Config");
            }
        } catch (IOException e) {
            Log.e("UserConfig", "Ошибка сохранения", e);
        }
    }
}