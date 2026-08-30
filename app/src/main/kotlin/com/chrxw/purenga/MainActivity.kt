package com.chrxw.purenga

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chrxw.purenga.ui.ClickableItemView
import com.chrxw.purenga.ui.CopyrightWarnView
import com.chrxw.purenga.ui.DarkContainLayout
import com.chrxw.purenga.ui.FitImageView
import com.chrxw.purenga.utils.DialogUtils
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.StatusUtils
import com.github.kyuubiran.ezxhelper.AndroidLogger


/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {
    private fun openNga(openPluginSetting: Boolean) {
        try {
            val intent = Intent(Intent.ACTION_MAIN).setComponent(
                ComponentName(
                    Constant.NGA_PACKAGE_NAME, "com.donews.nga.activitys.MainActivity"
                )
            )

            if (openPluginSetting) {
                intent.putExtra("fromShortcut", true)
                intent.putExtra("gotoName", "pluginSetting")
            }

            startActivity(intent)
        } catch (e: Throwable) {
            AndroidLogger.e(e)
            toast(getString(R.string.open_nga_failed))
        }
    }

    private fun hideAppIcon() {
        val componentName = ComponentName(this, "com.chrxw.purenga.MainActivity")
        val packageManager = packageManager
        val state = packageManager.getComponentEnabledSetting(componentName)
        if (state != PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            packageManager.setComponentEnabledSetting(
                componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
            )
        }
    }

    private fun toast(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(this, text, duration).show()
    }

    private fun showPluginTutorial() {
        val root = ScrollView(this)
        val linearLayout = DarkContainLayout(this, false)
        linearLayout.addView(ClickableItemView(this, R.string.tutorials_first))
        linearLayout.addView(FitImageView(this, R.drawable.tutorials3))
        linearLayout.addView(ClickableItemView(this, R.string.tutorials_second))
        linearLayout.addView(FitImageView(this, R.drawable.tutorials4))

        root.addView(linearLayout)

        AlertDialog.Builder(this).apply {
            setView(root)
            setNegativeButton("关闭", null)
            show()
        }
    }

    private lateinit var runningStatusView: ClickableItemView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Helper.context = applicationContext

        val root = ScrollView(this)
        val container = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
        }

        container.addView(FitImageView(this, R.mipmap.ic_launcher).apply {
            val size = resources.displayMetrics.widthPixels / 3
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                topMargin = 16
                bottomMargin = 16
                gravity = android.view.Gravity.CENTER_HORIZONTAL
            }
        })

        container.addView(CopyrightWarnView(this))

        container.addView(ClickableItemView(this, R.string.about))
        container.addView(ClickableItemView(this, R.string.donate, R.string.donate_summary).apply {
            setOnClickListener {
                DialogUtils.popupDonateDialog(this@MainActivity)
            }
        })
        container.addView(ClickableItemView(this, R.string.author, R.string.author_summary).apply {
            setOnClickListener {
                DialogUtils.popupDonateDialog(this@MainActivity)
            }
        })
        container.addView(ClickableItemView(this, R.string.change_log, R.string.version).apply {
            subTitle = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            setOnClickListener {
                DialogUtils.popupChangeLogDialog(this@MainActivity)
            }
        })
        container.addView(ClickableItemView(this, R.string.check_update, R.string.check_update_summary).apply {
            setOnClickListener {
                DialogUtils.popupCheckUpdate(this@MainActivity)
            }
        })
        container.addView(
            ClickableItemView(
                this, R.string.get_latest_version, R.string.get_latest_version_summary
            ).apply {
                setOnClickListener {
                    DialogUtils.popupGotoReleasePage(this@MainActivity)
                }
            })

        container.addView(ClickableItemView(this, R.string.other))
        runningStatusView = ClickableItemView(this, R.string.running_status, R.string.module_disabled)
        container.addView(runningStatusView)
        container.addView(ClickableItemView(this, R.string.open_nga, R.string.open_nga_summary).apply {
            setOnClickListener {
                openNga(false)
            }
        })
        container.addView(ClickableItemView(this, R.string.open_purenga, R.string.open_purenga_summary).apply {
            setOnClickListener {
                openNga(true)
            }
        })

        container.addView(ClickableItemView(this, R.string.setting))
        container.addView(
            ClickableItemView(
                this, R.string.plugin_setting, R.string.plugin_setting_summary
            ).apply {
                setOnClickListener {
                    showPluginTutorial()
                }
            })
        container.addView(ClickableItemView(this, R.string.hide_icon, R.string.hide_icon_summary).apply {
            setOnClickListener {
                hideAppIcon()
            }
        })

        root.addView(container)
        setContentView(root)

        DialogUtils.popupEulaDialog(this)
    }

    override fun onResume() {
        super.onResume()
        runningStatusView.subTitle =
            getString(if (StatusUtils.modelEnabled) R.string.module_enabled else R.string.module_disabled)
    }

    override fun onDestroy() {
        super.onDestroy()
        Helper.clearStaticResource()
    }
}