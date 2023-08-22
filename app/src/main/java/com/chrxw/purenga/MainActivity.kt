package com.chrxw.purenga

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {

    companion object {
        fun isModuleActive(): Boolean {
            return false
        }
    }

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
            if (preference.key == "hide_icon") {
                val ctx = context
                if (ctx != null) {
                    hideAppIcon(ctx)
                }
                return true
            }

            val intent = when (preference.key) {
                "version" -> {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.github_url))
                    )
                }

                "author" -> {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.author_url))
                    )
                }

                "donate" -> {
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.donate_url))
                    )
                }

                "reopen_nga" -> {
                    Intent(Intent.ACTION_MAIN)
                        .setComponent(
                            ComponentName(
                                Constant.NGA_PACKAGE_NAME,
                                Constant.NGA_MAIN_ACTIVITY_NAME
                            )
                        )
                        .setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                }

                else -> null
            }

            if (intent != null) {
                startActivity(intent)
            }

            return true
        }

        override fun onResume() {
            super.onResume()
            val resId = if (isModuleActive()) R.string.module_enabled else R.string.module_disabled
            runningStatusPref?.summary = resources.getString(resId)
        }

        private fun hideAppIcon(context: Context) {
            val componentName = ComponentName(context, "com.chrxw.purenga.MainActivity")
            val packageManager = context.packageManager
            val state = packageManager.getComponentEnabledSetting(componentName)
            if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                packageManager.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}