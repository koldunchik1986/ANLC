package com.abclient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.abclient.R
import com.abclient.data.ProfileManager

// Окно добавления/редактирования профиля
class ProfileEditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_edit_activity)

        val editName = findViewById<EditText>(R.id.edit_profile_name)
        val editPassword = findViewById<EditText>(R.id.edit_profile_password)
        val checkboxAutologin = findViewById<android.widget.CheckBox>(R.id.checkbox_autologin)
        val checkboxProxy = findViewById<android.widget.CheckBox>(R.id.checkbox_proxy)
            val checkboxAutoClearCookies = findViewById<android.widget.CheckBox>(R.id.checkbox_auto_clear_cookies)
        val btnSave = findViewById<Button>(R.id.btn_save_profile)

        btnSave.setOnClickListener {
            val name = editName.text.toString().trim()
            val password = editPassword.text.toString()
            val autologin = checkboxAutologin.isChecked
            val useProxy = checkboxProxy.isChecked
                val autoClearCookies = checkboxAutoClearCookies.isChecked
            if (name.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите имя и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
                ProfileManager.addProfile(name, password, autologin, useProxy, autoClearCookies, this)
            Toast.makeText(this, "Профиль сохранён", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
