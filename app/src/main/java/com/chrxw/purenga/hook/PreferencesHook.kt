package com.chrxw.purenga.hook

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import com.chrxw.purenga.Constant
import com.chrxw.purenga.R
import com.chrxw.purenga.XposedInit
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import com.github.kyuubiran.ezxhelper.EzXHelper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import kotlin.system.exitProcess

/**
 * 设置页面钩子
 */
class PreferencesHook : IHook {

    companion object {
        lateinit var clsMainActivity: Class<*>
        lateinit var clsSettingActivity: Class<*>

        fun restartApplication(activity: Activity) {
            val pm = activity.packageManager
            val intent = pm.getLaunchIntentForPackage(activity.packageName)
            activity.finishAffinity()
            activity.startActivity(intent)
            exitProcess(0)
        }
    }

    override fun hookName(): String {
        return "设置页面"
    }

    override fun init(classLoader: ClassLoader) {
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsSettingActivity = classLoader.loadClass("com.donews.nga.setting.SettingActivity")
    }

    override fun hook() {

        var btnPureNGASetting: Button? = null

        XposedHelpers.findAndHookMethod(clsSettingActivity, "initLayout", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val activity = param.thisObject as Activity

                val viewBinding = XposedHelpers.getObjectField(activity, "viewBinding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                val scrollView = root.getChildAt(1) as ScrollView
                val linearLayout = scrollView.getChildAt(0) as LinearLayout

                EzXHelper.addModuleAssetPath(XposedInit.moduleRes)

                btnPureNGASetting = Button(activity).also { btn ->
                    btn.text = Constant.BTN_TITLE
                    btn.setOnClickListener {
                        val view = generateView()
                        loadSetting(view)
                        AlertDialog.Builder(activity).run {
                            setTitle(Constant.BTN_TITLE)
                            setCancelable(false)
                            setView(view)
                            setNegativeButton("取消") { _, _ ->
                                Helper.toast("设置未保存")
                            }
                            setPositiveButton("保存并重启 NGA") { _, _ ->
                                saveSetting(view)
                                Helper.toast("设置已保存")
                                restartApplication(activity)
                            }
                            create().also { dialog ->
                                val params = dialog.window?.attributes
                                val metrics = android.util.DisplayMetrics()
                                dialog.window!!.windowManager.defaultDisplay.getMetrics(metrics)
                                params?.width = metrics.widthPixels
                                params?.height = metrics.heightPixels
                                dialog.window!!.attributes = params
                                dialog.show()
                            }
                        }
                    }

                    btn.setTextColor(Color.parseColor(if (Helper.isDarkModel()) "#f8fae3" else "#3c3b39"))
                    btn.setBackgroundColor(0)
                    btn.setPadding(5, 5, 5, 5)
                    linearLayout.removeViewAt(linearLayout.childCount - 1)
                    linearLayout.addView(btn)
                }
            }
        })

        XposedHelpers.findAndHookMethod(OptimizeHook.clsAppConfig,
            "setDarkModel",
            Boolean::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    btnPureNGASetting?.setTextColor(Color.parseColor(if (Helper.isDarkModel()) "#f8fae3" else "#3c3b39"))
                }
            })
    }

    /**
     * 生成设置界面
     */
    fun generateView(): View {
        val inflater = LayoutInflater.from(EzXHelper.appContext)
        return try {
            inflater.inflate(R.layout.inapp_setting_activity, null)
        } catch (e: Throwable) {
            Log.e(e)
            Helper.toast("渲染界面异常, 请重启APP")
            inflater.inflate(XposedInit.moduleRes.getLayout(R.layout.inapp_setting_activity), null)
        }
    }

    private fun loadSetting(view: View) {
        Helper.spPlugin.run {
            view.findViewById<Switch>(R.id.pure_splash_ad).isChecked = getBoolean(Constant.PURE_SPLASH_AD, false)
            view.findViewById<Switch>(R.id.pure_post_ad).isChecked = getBoolean(Constant.PURE_POST_AD, false)
            view.findViewById<Switch>(R.id.crack_ad_task).isChecked = getBoolean(Constant.CRACK_AD_TASK, false)
            view.findViewById<Switch>(R.id.remove_store_icon).isChecked = getBoolean(Constant.REMOVE_STORE_ICON, false)
            view.findViewById<Switch>(R.id.remove_activity_icon).isChecked =
                getBoolean(Constant.REMOVE_ACTIVITY_ICON, false)
            view.findViewById<Switch>(R.id.use_external_browser).isChecked =
                getBoolean(Constant.USE_EXTERNAL_BROWSER, false)
            view.findViewById<Switch>(R.id.kill_update_check).isChecked = getBoolean(Constant.KILL_UPDATE_CHECK, false)
            view.findViewById<Switch>(R.id.kill_popup_dialog).isChecked = getBoolean(Constant.KILL_UPDATE_CHECK, false)
            view.findViewById<Switch>(R.id.hide_hook_info).isChecked = getBoolean(Constant.HIDE_HOOK_INFO, false)
        }
    }

    private fun saveSetting(view: View) {
        Helper.spPlugin.edit().run {
            putBoolean(Constant.PURE_SPLASH_AD, view.findViewById<Switch>(R.id.pure_splash_ad).isChecked)
            putBoolean(Constant.PURE_POST_AD, view.findViewById<Switch>(R.id.pure_post_ad).isChecked)
            putBoolean(Constant.CRACK_AD_TASK, view.findViewById<Switch>(R.id.crack_ad_task).isChecked)
            putBoolean(Constant.REMOVE_STORE_ICON, view.findViewById<Switch>(R.id.remove_store_icon).isChecked)
            putBoolean(Constant.REMOVE_ACTIVITY_ICON, view.findViewById<Switch>(R.id.remove_activity_icon).isChecked)
            putBoolean(Constant.USE_EXTERNAL_BROWSER, view.findViewById<Switch>(R.id.use_external_browser).isChecked)
            putBoolean(Constant.KILL_UPDATE_CHECK, view.findViewById<Switch>(R.id.kill_update_check).isChecked)
            putBoolean(Constant.KILL_UPDATE_CHECK, view.findViewById<Switch>(R.id.kill_popup_dialog).isChecked)
            putBoolean(Constant.HIDE_HOOK_INFO, view.findViewById<Switch>(R.id.hide_hook_info).isChecked)
            commit()
        }
    }
}

