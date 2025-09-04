package com.neverlands.anlc.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.neverlands.anlc.R
import com.neverlands.anlc.data.local.ProfileManager
import com.neverlands.anlc.data.local.model.Profile
import com.neverlands.anlc.data.remote.AuthResult
import com.neverlands.anlc.databinding.ActivityProfilesBinding // Assuming ViewBinding will be used
import com.neverlands.anlc.ui.base.BaseActivity
import com.neverlands.anlc.ui.main.MainActivity // Assuming MainActivity will be in ui.main

/**
 * Activity для выбора профиля и входа в игру.
 * Является основным экраном для взаимодействия с профилями пользователей.
 */
class ProfilesActivity : BaseActivity() {

    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var binding: ActivityProfilesBinding
    private var selectedProfile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupObservers()
        viewModel.loadProfiles()
    }

    private fun setupViews() {
        binding.loginButton.setOnClickListener {
            selectedProfile?.let { profile ->
                if (profile.configPasswordHash.isNullOrEmpty()) {
                    // Если пароль конфигурации не установлен, запрашиваем его
                    PasswordInputDialog { enteredPassword ->
                        viewModel.authorize(profile, enteredPassword)
                    }.show(supportFragmentManager, "password_dialog")
                } else {
                    // Если пароль конфигурации есть, используем его для авторизации
                    // TODO: В будущем здесь нужно будет запросить пароль от пользователя,
                    // если он не хочет использовать автологин или если пароль не совпадает с хешем
                    val password = "your_password_here" // Временно используем заглушку для теста
                    viewModel.authorize(profile, password)
                }
            } ?: showToast("Пожалуйста, выберите профиль")
        }

        binding.createButton.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            intent.putExtra("is_new_profile", true)
            startActivity(intent)
        }

        binding.editButton.setOnClickListener {
            selectedProfile?.let { profile ->
                val intent = Intent(this, ProfileEditActivity::class.java)
                intent.putExtra("is_new_profile", false)
                intent.putExtra("profile_to_edit", profile)
                startActivity(intent)
            } ?: showToast("Пожалуйста, выберите профиль для редактирования")
        }

        binding.deleteButton.setOnClickListener {
            selectedProfile?.let { profile ->
                viewModel.removeProfile(profile)
                showToast("Профиль ${profile.userNick} удален.")
                selectedProfile = null // Сбрасываем выбранный профиль
            } ?: showToast("Пожалуйста, выберите профиль для удаления")
        }
    }

    private fun setupObservers() {
        viewModel.profiles.observe(this) { profiles ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_single_choice,
                profiles.map { it.userNick }
            )
            binding.profilesListView.adapter = adapter
            binding.profilesListView.choiceMode = android.widget.ListView.CHOICE_MODE_SINGLE
            binding.profilesListView.setOnItemClickListener { _, _, position, _ ->
                selectedProfile = profiles[position]
            }
        }

        viewModel.authResult.observe(this) { result ->
            when (result) {
                AuthResult.Success -> {
                    showToast("Авторизация успешна!")
                    selectedProfile?.let { ProfileManager.setCurrentProfile(it) } // Устанавливаем текущий профиль
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is AuthResult.Failure -> showToast("Ошибка авторизации: ${result.error}")
                AuthResult.CaptchaRequired -> showToast("Требуется капча!")
            }
        }
    }
}
