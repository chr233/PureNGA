package com.chrxw.purenga.hook

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Switch
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.R
import com.chrxw.purenga.XposedInit
import com.chrxw.purenga.utils.Helper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

/**
 * 设置页面钩子
 */
class PreferencesHook : IHook {

    companion object {
        lateinit var clsMainActivity: Class<*>
        lateinit var clsSettingActivity: Class<*>
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
                val viewBinding = XposedHelpers.getObjectField(param.thisObject, "viewBinding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                val scrollView = root.getChildAt(1) as ScrollView
                val linearLayout = scrollView.getChildAt(0) as LinearLayout

                val context = param.thisObject as Context

                btnPureNGASetting = Button(context).also { btn ->
                    btn.text = "PureNGA 设置"
                    btn.setOnClickListener {
                        val view = generateView(Helper.context)
//                        loadSetting(view)
                        AlertDialog.Builder(view.context).run {
                            setTitle("PureNGA 设置")
                            setCancelable(false)
                            setView(view)
                            setNegativeButton("取消") { _, _ ->
                                Helper.toast("设置未保存")
                            }
                            setPositiveButton("确定") { _, _ ->
                                saveSetting(view)
                                Helper.toast("设置已保存, 重启APP生效")
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
    fun generateView(context: Context): View {
        return try {
            throw Exception()
            val ctx = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
            val inflater = LayoutInflater.from(ctx)
            inflater.inflate(R.layout.inapp_setting_activity, null)
        } catch (e: Throwable) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(XposedInit.moduleRes.getLayout(R.layout.inapp_setting_activity), null)
        }
    }

    private fun loadSetting(view: View) {
        val mRes = XposedInit.moduleRes
        Helper.spPlugin.run {
            view.findViewById<Switch>(R.id.pure_splash_ad).run {
                text = mRes.getString(R.string.pure_splash_ad)
                isChecked = getBoolean(Constant.PURE_SPLASH_AD, false)
            }
            view.findViewById<Switch>(R.id.pure_post_ad).run {
                text = mRes.getString(R.string.pure_post_ad)
                isChecked = getBoolean(Constant.PURE_POST_AD, false)
            }
            view.findViewById<Switch>(R.id.crack_ad_task).run {
                text = mRes.getString(R.string.crack_ad_task)
                isChecked = getBoolean(Constant.CRACK_AD_TASK, false)
            }
            view.findViewById<Switch>(R.id.use_external_browser).run {
                text = mRes.getString(R.string.use_external_browser)
                isChecked = getBoolean(Constant.USE_EXTERNAL_BROWSER, false)
            }
            view.findViewById<Switch>(R.id.kill_update_check).run {
                text = mRes.getString(R.string.kill_update_check)
                isChecked = getBoolean(Constant.KILL_UPDATE_CHECK, false)
            }
            view.findViewById<Switch>(R.id.kill_popup_dialog).run {
                text = mRes.getString(R.string.kill_popup_dialog)
                isChecked = getBoolean(Constant.KILL_UPDATE_CHECK, false)
            }
            view.findViewById<Switch>(R.id.hide_hook_info).run {
                text = mRes.getString(R.string.hide_hook_info)
                isChecked = getBoolean(Constant.HIDE_HOOK_INFO, false)
            }
        }
    }

    private fun saveSetting(view: View) {
        Helper.spPlugin.edit().run {
            putBoolean(Constant.PURE_SPLASH_AD, view.findViewById<Switch>(R.id.pure_splash_ad).isChecked)
            putBoolean(Constant.PURE_POST_AD, view.findViewById<Switch>(R.id.pure_post_ad).isChecked)
            putBoolean(Constant.CRACK_AD_TASK, view.findViewById<Switch>(R.id.crack_ad_task).isChecked)
            putBoolean(Constant.USE_EXTERNAL_BROWSER, view.findViewById<Switch>(R.id.use_external_browser).isChecked)
            putBoolean(Constant.KILL_UPDATE_CHECK, view.findViewById<Switch>(R.id.kill_update_check).isChecked)
            putBoolean(Constant.KILL_UPDATE_CHECK, view.findViewById<Switch>(R.id.kill_popup_dialog).isChecked)
            putBoolean(Constant.HIDE_HOOK_INFO, view.findViewById<Switch>(R.id.hide_hook_info).isChecked)
            commit()
        }
    }
}

