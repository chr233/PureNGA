package com.chrxw.purenga.hook

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.view.children
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.R
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 设置页面钩子
 */
class PreferencesHook : IHook {

    companion object {
        lateinit var clsMainActivity: Class<*>
        lateinit var clsHomeDrawerLayout: Class<*>
        lateinit var clsSettingActivity: Class<*>
    }

    override fun hookName(): String {
        return "设置页面"
    }

    override fun init(classLoader: ClassLoader) {
        clsMainActivity = classLoader.loadClass("com.donews.nga.activitys.MainActivity")
        clsHomeDrawerLayout = classLoader.loadClass("com.donews.nga.widget.HomeDrawerLayout")
        clsSettingActivity = classLoader.loadClass("com.donews.nga.setting.SettingActivity")
    }

    override fun hook() {

        XposedHelpers.findAndHookMethod(
            clsHomeDrawerLayout,
            "initLayout",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val viewBinding = XposedHelpers.getObjectField(param.thisObject, "binding")
                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout

                    for (x in root.children) {
                        Log.i(x.javaClass)
                    }
                }
            })


        XposedHelpers.findAndHookMethod(
            clsSettingActivity,
            "initLayout",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val viewBinding = XposedHelpers.getObjectField(param.thisObject, "viewBinding")
                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout
                    val scrollView = root.getChildAt(1) as ScrollView
                    val linearLayout = scrollView.getChildAt(0) as LinearLayout
                    val context = root.context

                    val button = Button(context)
                    button.text = "PureNGA 设置"
                    button.setBackgroundColor(if (Helper.darkMode) 0x21211d else 0xfdfae2)
                    button.setOnClickListener {
                        val context = param.thisObject as Context
                        val dialog = AlertDialog.Builder(context).setTitle("PureNGA 设置")
//                            .setCancelable(false)
                            .setView(generateView(context))
                            .create()

                        dialog.show()

                        val lp = WindowManager.LayoutParams()
                        lp.copyFrom(dialog.window!!.attributes)
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
                        dialog.show()
                        dialog.window?.attributes = lp

                    }

                    button.setPadding(5, 5, 5, 5)

//                    val line = linearLayout.getChildAt(linearLayout.childCount-2)
//                    linearLayout.addView(line)
                    linearLayout.removeViewAt(linearLayout.childCount - 1)
                    linearLayout.addView(button)
                }
            })
    }

    fun generateView(context: Context): View {
        val ctx = context.createPackageContext(BuildConfig.APPLICATION_ID, Context.CONTEXT_IGNORE_SECURITY)
        val inflater = LayoutInflater.from(ctx)
        val view = inflater.inflate(R.layout.inapp_setting_activity, null)

//        val text = view.findViewById<TextView>(R.id.switch1)
//        text.text = "114514"

        return view
    }
}

