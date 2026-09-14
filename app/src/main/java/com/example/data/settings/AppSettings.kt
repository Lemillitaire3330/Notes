package com.example.data.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    FRENCH("fr", "Français", "Français"),
    CHINESE("zh", "Chinese", "简体中文")
}

enum class ThemeMode(val code: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark")
}

enum class AppFontFamily(val code: String) {
    DEFAULT("default"),
    SERIF("serif"),
    MONOSPACE("monospace")
}

enum class AppFontSize(val code: String, val scaleFactor: Float) {
    SMALL("small", 0.88f),
    NORMAL("normal", 1.0f),
    LARGE("large", 1.15f)
}

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("secure_notes_app_settings", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(
        when (prefs.getString(KEY_LANGUAGE, "en")) {
            "fr" -> AppLanguage.FRENCH
            "zh" -> AppLanguage.CHINESE
            else -> AppLanguage.ENGLISH
        }
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _themeMode = MutableStateFlow(
        when (prefs.getString(KEY_THEME, "system")) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    private val _fontFamily = MutableStateFlow(
        when (prefs.getString(KEY_FONT, "default")) {
            "serif" -> AppFontFamily.SERIF
            "monospace" -> AppFontFamily.MONOSPACE
            else -> AppFontFamily.DEFAULT
        }
    )
    val fontFamily: StateFlow<AppFontFamily> = _fontFamily.asStateFlow()

    private val _fontSize = MutableStateFlow(
        when (prefs.getString(KEY_FONT_SIZE, "normal")) {
            "small" -> AppFontSize.SMALL
            "large" -> AppFontSize.LARGE
            else -> AppFontSize.NORMAL
        }
    )
    val fontSize: StateFlow<AppFontSize> = _fontSize.asStateFlow()

    private val _hasSeenLanguageNotice = MutableStateFlow(
        prefs.getBoolean(KEY_HAS_SEEN_LANGUAGE_NOTICE, false)
    )
    val hasSeenLanguageNotice: StateFlow<Boolean> = _hasSeenLanguageNotice.asStateFlow()

    fun setLanguage(newLanguage: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, newLanguage.code).apply()
        _language.value = newLanguage
    }

    fun setThemeMode(newTheme: ThemeMode) {
        prefs.edit().putString(KEY_THEME, newTheme.code).apply()
        _themeMode.value = newTheme
    }

    fun setFontFamily(newFont: AppFontFamily) {
        prefs.edit().putString(KEY_FONT, newFont.code).apply()
        _fontFamily.value = newFont
    }

    fun setFontSize(newSize: AppFontSize) {
        prefs.edit().putString(KEY_FONT_SIZE, newSize.code).apply()
        _fontSize.value = newSize
    }

    fun setHasSeenLanguageNotice(seen: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_SEEN_LANGUAGE_NOTICE, seen).apply()
        _hasSeenLanguageNotice.value = seen
    }

    companion object {
        private const val KEY_LANGUAGE = "key_language"
        private const val KEY_THEME = "key_theme"
        private const val KEY_FONT = "key_font"
        private const val KEY_FONT_SIZE = "key_font_size"
        private const val KEY_HAS_SEEN_LANGUAGE_NOTICE = "key_has_seen_language_notice"
    }
}
