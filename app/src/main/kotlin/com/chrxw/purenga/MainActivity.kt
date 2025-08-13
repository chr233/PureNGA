package com.chrxw.purenga

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableRow.LayoutParams
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
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

    private fun openUrl(uri: String) {
        if (uri.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_VIEW, uri.toUri())
            startActivity(intent)
        }
    }

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
            toast("打开 NGA 失败")
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
        val linearLayout = LinearLayout(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            orientation = LinearLayout.VERTICAL
        }
        linearLayout.addView(ClickableItemView(this, false, R.string.tutorials_first))
        linearLayout.addView(FitImageView(this, R.drawable.tutorials3))
        linearLayout.addView(ClickableItemView(this, false, R.string.tutorials_second))
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

        val root = ScrollView(this)
        val container = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            orientation = LinearLayout.VERTICAL
        }

        container.addView(ClickableItemView(this, false, R.string.about))
        container.addView(ClickableItemView(this, false, R.string.donate, R.string.donate_summary).apply {
            setOnClickListener {
                openUrl(Constant.DONATE_URL)
            }
        })
        container.addView(ClickableItemView(this, false, R.string.author, R.string.author_summary).apply {
            setOnClickListener {
                Helper.checkForUpdates()
            }
        })
        container.addView(ClickableItemView(this, false, R.string.version, R.string.version).apply {
            subTitle = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            setOnClickListener {
                openUrl(Constant.REPO_URL)
            }
        })

        container.addView(ClickableItemView(this, false, R.string.other))
        runningStatusView = ClickableItemView(this, false, R.string.running_status, R.string.module_disabled)
        container.addView(runningStatusView)
        container.addView(ClickableItemView(this, false, R.string.open_nga, R.string.open_nga_summary).apply {
            setOnClickListener {
                openNga(false)
            }
        })
        container.addView(ClickableItemView(this, false, R.string.open_purenga, R.string.open_purenga_summary).apply {
            setOnClickListener {
                openNga(true)
            }
        })

        container.addView(ClickableItemView(this, false, R.string.setting))
        container.addView(
            ClickableItemView(
                this,
                false,
                R.string.plugin_setting,
                R.string.plugin_setting_summary
            ).apply {
                setOnClickListener {
                    showPluginTutorial()
                }
            })
        container.addView(ClickableItemView(this, false, R.string.hide_icon, R.string.hide_icon_summary).apply {
            setOnClickListener {
                hideAppIcon()
            }
        })


        root.addView(container)
        setContentView(root)
    }

    override fun onResume() {
        super.onResume()
        runningStatusView.subTitle =
            getString(if (isModuleActive()) R.string.module_enabled else R.string.module_disabled)
    }
}