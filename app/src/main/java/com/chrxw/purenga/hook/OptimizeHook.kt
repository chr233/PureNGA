package com.chrxw.purenga.hook

import android.R.attr.classLoader
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
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

                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as View
                    val layoutDrawSetting = Helper.getRId("about_us_title")
                    val linearLayout = root.findViewById<TextView>(layoutDrawSetting).parent as LinearLayout

                    val context = root.context

                    val button = Button(context)
                    button.text = "PureNGA +设置"
                    button.setOnClickListener {
                        val context = param.thisObject as Context
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Custom UI")
                            .setMessage("This is a custom UI.")
//                            .setView(R.xml.pref_settings)
//                            .setView()
                            .show()
                    }


                    var x = Helper.prefs
                    if (x != null) {
                        Log.i(x.getInt("12345", 0))
                        x.edit().putInt("12345", 666).commit()
                        Log.i(x.getInt("12345", 0))
                    } else {
                        Log.i("x is null")
                    }


                    linearLayout.addView(button)
                }
            })
    }

    fun generateView() {

    }
}

