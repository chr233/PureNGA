package com.chrxw.purenga

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.lightColorScheme
import androidx.core.net.toUri
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.chrxw.purenga.ui.ClickableItemView
import com.chrxw.purenga.ui.FitImageView
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger


/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {
    companion object {
        /**
         * 检测模块启用状态
         */
        @JvmStatic
        @Keep
        fun isModuleActive(): Boolean {
            return false
        }
    }

    private fun isLightTheme(): Boolean {
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(android.R.attr.windowBackground, typedValue, true)
        val color = typedValue.data
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }

    fun transparentStatusBar() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT

        //设置状态栏文字颜色
        setStatusBarTextColor(isLightTheme())
    }

    private fun setStatusBarTextColor(light: Boolean) {
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = if (light) { //白色文字
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        } else { //黑色文字
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = systemUiVisibility
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        supportFragmentManager.beginTransaction().replace(R.id.settings, SettingsFragment()).commit()
    }

    /**
     * 设置页
     */
    class SettingsFragment : PreferenceFragmentCompat() {
        private var runningStatusPref: Preference? = null

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_settings, rootKey)
            findPreference<Preference>("version")?.summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            runningStatusPref = findPreference("running_status")
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {

            when (val prefKey = preference.key) {
                "hide_icon" -> {
                    val ctx = context
                    if (ctx != null) {
                        hideAppIcon(ctx)
                    }
                    return true
                }

                "plugin_setting" -> {
                    val ctx = requireContext()

                    val root = ScrollView(ctx)
                    val linearLayout = LinearLayout(ctx).apply {
                        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                        orientation = LinearLayout.VERTICAL
                    }
                    linearLayout.addView(ClickableItemView(ctx).apply {
                        title = "更新说明"
                        subTitle = resources.getString(R.string.chang_log).replace("|", "\n")
                    })
                    linearLayout.addView(ClickableItemView(ctx).apply {
                        title = "版本信息"
                        val pluginVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                        subTitle = "插件版本: $pluginVersion"
                    })
                    linearLayout.addView(FitImageView(ctx).apply {
                        setImageResource(R.drawable.tutorials3, null)
                    })
                    linearLayout.addView(FitImageView(ctx).apply {
                        setImageResource(R.drawable.tutorials4, null)
                    })

                    root.addView(linearLayout)

                    AlertDialog.Builder(activity).apply {
                        setView(root)
                        setNegativeButton("关闭", null)
                        show()
                    }
                    return true
                }

                "open_nga" -> {
                    try {
                        val intent = Intent(Intent.ACTION_MAIN).setComponent(
                            ComponentName(
                                Constant.NGA_PACKAGE_NAME, "com.donews.nga.activitys.MainActivity"
                            )
                        )
                        startActivity(intent)
                    } catch (e: Throwable) {
                        toast("打开 NGA 失败")
                    }
                    return true
                }

                "open_purenga" -> {
                    try {
                        val intent = Intent(Intent.ACTION_MAIN).setComponent(
                            ComponentName(
                                Constant.NGA_PACKAGE_NAME, "com.donews.nga.activitys.MainActivity"
                            )
                        )

                        intent.putExtra("fromShortcut", true)
                        intent.putExtra("gotoName", "pluginSetting")

                        startActivity(intent)
                    } catch (e: Throwable) {
                        AndroidLogger.e(e)
                        toast("打开 设置 失败")
                    }
                    return true
                }

                "version" -> {
                    Helper.checkForUpdates()

                    return true
                }

                else -> {
                    val uri = when (prefKey) {
                        "author" -> Constant.AUTHOR_URL
                        "donate" -> Constant.DONATE_URL
                        "repo" -> Constant.REPO_URL
                        else -> null
                    }

                    if (!uri.isNullOrEmpty()) {
                        val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
                        startActivity(intent)
                    }

                    return true
                }
            }
        }

        override fun onResume() {
            super.onResume()
            runningStatusPref?.setSummary(if (isModuleActive()) R.string.module_enabled else R.string.module_disabled)
        }

        private fun hideAppIcon(context: Context) {
            val componentName = ComponentName(context, "com.chrxw.purenga.MainActivity")
            val packageManager = context.packageManager
            val state = packageManager.getComponentEnabledSetting(componentName)
            if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                packageManager.setComponentEnabledSetting(
                    componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
                )
            }
        }

        private fun toast(text: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(this.context, text, duration).show()
        }
    }
}