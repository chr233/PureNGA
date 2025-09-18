package com.chrxw.purenga.hook

import android.app.Activity
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.ScrollView
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.ui.ClickableItemXpView
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import de.robv.android.xposed.XposedHelpers


class DebugHook : IHook {
    override fun init(classLoader: ClassLoader) {
    }

    override fun hook() {
        findFirstMethodByName(OptimizeHook.clsHomeDrawerLayout, "initLayout")?.createHook {
            after {
                it.log()

                val viewBinding = XposedHelpers.getObjectField(it.thisObject, "binding")
                val root = XposedHelpers.callMethod(viewBinding, "getRoot") as LinearLayout

                //净化侧拉菜单
                val scrollView = root.getChildAt(1) as ScrollView
                val linearLayout = scrollView.getChildAt(0) as LinearLayout

                val view = ClickableItemXpView(root.context, "重启 NGA", "调试用").apply {
                    setBackgroundColor(Color.LTGRAY)
                    setOnClickListener { _ ->
                        Helper.toast("正在重启")
                        val activity = XposedHelpers.callMethod(it.thisObject, "getActivity") as Activity
                        Helper.restartApplication(activity)
                    }
                }

                linearLayout.addView(view, linearLayout.childCount - 1)
            }
        }
    }

    override var name = "DebugHook"
}