package com.chrxw.purenga

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private var runningStatusPref: Preference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_settings, rootKey)
            findPreference<Preference>("version")?.summary = BuildConfig.VERSION_NAME
            runningStatusPref = findPreference("running_status")
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {

            val intent: Intent?

            when (preference.key) {
                "version" -> {
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.github_url))
                    )
                }
                "author" -> {
                    intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.author_url))
                    )
                }
                "reopen_nga" -> {
                    intent = Intent(Intent.ACTION_MAIN)
                    intent.component =
                        ComponentName(Constant.NGA_PACKAGE_NAME, Constant.NGA_MAIN_ACTIVITY_NAME)
                    intent.flags = Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                }
                else -> {
                    intent = null
                }
            }

            if (intent != null) {
                startActivity(intent)
            }

            return true
        }

        override fun onResume() {
            super.onResume()
            runningStatusPref?.setSummary(if (isModuleActive()) R.string.module_enabled else R.string.module_disabled)
        }
    }

    companion object {
        fun isModuleActive(): Boolean {
            return false
        }
    }
}