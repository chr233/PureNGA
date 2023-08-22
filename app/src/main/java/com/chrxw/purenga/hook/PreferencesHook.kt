package com.chrxw.purenga.hook

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
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
        var clsPreference: Class<*>? = null
        var clsMainActivity: Class<*>? = null
        var clsHomeDrawerLayout: Class<*>? = null
        var clsSettingActivity: Class<*>? = null
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
//        XposedHelpers.findAndHookMethod(
//            clsHomeDrawerLayout,
//            "initLayout",
//            object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    val viewBinding = XposedHelpers.getObjectField(param.thisObject, "binding")
//                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as View
//                    val layoutDrawSetting = Helper.getRId("layout_drawer_setting")
//                    val linearLayout = root.findViewById<RelativeLayout>(layoutDrawSetting).parent as LinearLayout
//
//                    val context = root.context
//
//                    val relativeLayout = RelativeLayout(context)
////                    relativeLayout.setBackgroundResource(Helper.getRColor("nga_theme_page_color"))
//
////                    relativeLayout.setPadding( Helper.getRDimen("dp_15"))
//
//                    val imageView = ImageView(context)
//                    imageView.setImageResource(Helper.getRDrawable("drawer_setting_icon"))
//
//                    val textView = TextView(context)
//                    textView.text = "PureNGA 设置"
//                    textView.setTextColor(Color.CYAN)
//
//                    relativeLayout.addView(imageView)
//                    relativeLayout.addView(textView)
//
//                    linearLayout.addView(relativeLayout)
//                }
//            })


//        XposedHelpers.findAndHookMethod(
//            clsSettingActivity,
//            "initLayout",
//            object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    val viewBinding = XposedHelpers.getObjectField(param.thisObject, "viewBinding")
//
//                    val root = XposedHelpers.callMethod(viewBinding, "getRoot") as View
//
//                    val layoutDrawSetting = Helper.getRId("about_us_title")
//
//                    val linearLayout = root.findViewById<TextView>(layoutDrawSetting).parent as LinearLayout
//
//                    val context = root.context
//
//
//                    val textView = TextView(context)
//                    textView.text = "PureNGA 设置"
//                    textView.setTextColor(Color.CYAN)
//
//                    linearLayout.addView(textView)
////                    linearLayout.inser
//                }
//            })


        val settingXmlId = Helper.getRLayout("activity_setting")

        XposedHelpers.findAndHookMethod(
            LayoutInflater::class.java, "inflate",
            Int::class.javaPrimitiveType,
            ViewGroup::class.java,
            Boolean::class.javaPrimitiveType, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val id = param.args[0] as Int
                    val vg = param.args[1] as ViewGroup?
                    val attachToRoot = param.args[2] as Boolean

                    if (id == settingXmlId) {

                        Log.i(id)
                        Log.i(vg)
                        Log.i(attachToRoot)

                        val viewGroup = param.result as ViewGroup

                        val childViewGroup = viewGroup.getChildAt(1) as ScrollView

                        Log.i(childViewGroup.javaClass)

//                        for (x: View in childViewGroup.children) {
//                            Log.i("id:" + x.id)
//                        }


//                        val title = childViewGroup.getChildAt(0) as TextView
//
//                        title.setTextColor(if (Helper.darkMode) -0x2c2c2d else -0xbbbbbc)
//
//                        val summary = childViewGroup.getChildAt(1) as TextView
//                        summary.setTextColor(if (Helper.darkMode) -0x666667 else -0xededee)
//                        summary.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
//                        val seekbarValue = (childViewGroup.getChildAt(2) as ViewGroup).getChildAt(1) as TextView
//                        seekbarValue.setTextColor(if (Helper.darkMode) -0x2c2c2d else -0xbbbbbc)
//
//                        val root = LinearLayout(Helper.context)
//                        run {
//                            val layoutParams =
//                                ViewGroup.LayoutParams(
//                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                    ViewGroup.LayoutParams.WRAP_CONTENT
//                                )
//                            root.orientation = LinearLayout.VERTICAL
//                            root.layoutParams = layoutParams
//                            root.addView(viewGroup)
//                        }
//                        param.result = root
                    }
                }
            })


        //        XposedHelpers.findAndHookMethod(
//            clsMainActivity,
//            "initLayout",
//            object : XC_MethodHook() {
//                @Throws(Throwable::class)
//                override fun afterHookedMethod(param: MethodHookParam) {
//
//                    val viewBinding = XposedHelpers.getObjectField(param.thisObject, "viewBinding")
//
////                    val drawerLayout = XposedHelpers.getObjectField(viewBinding,"b" ) as DrawerLayout
//
////                    val context = drawerLayout.context
//
////                    Helper.toast(drawerLayout.toString())
//
//                    val linearLayout = XposedHelpers.getObjectField(viewBinding, "h") as LinearLayout
//
//                    val context = linearLayout.context
//
//                    val tv1 = XposedHelpers.getObjectField(viewBinding, "k") as TextView
//                    val tv2 = XposedHelpers.getObjectField(viewBinding, "l") as TextView
//                    val tv3 = XposedHelpers.getObjectField(viewBinding, "m") as TextView
//
//                    Log.i(tv1.text)
//                    Log.i(tv2.text)
//                    Log.i(tv3.text)
//
//                    tv1.text = "2333"
//                    tv2.text = "444"
//                    tv3.text = "555"
//
//                    Log.i(tv1.text)
//                    Log.i(tv2.text)
//                    Log.i(tv3.text)
//                }
//            })
    }

}