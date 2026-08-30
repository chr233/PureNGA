package com.chrxw.purenga.hook

import android.app.Activity
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.core.graphics.toColorInt
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.ui.ClickableItemView
import com.chrxw.purenga.utils.DialogUtils
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers


class DebugHook : IHook {
    override fun init(classLoader: ClassLoader) {
        MethodFinder.fromClass("com.donews.nga.entity.HomeTabParam", classLoader).filterByName("isFullscreen")
            .firstOrNull()?.createHook {
                after {
                    it.result = false
                }
            }

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

                val color = "#fff0cd".toColorInt()

                linearLayout.addView(ClickableItemView(root.context, "PureNGA 设置", "调试用").apply {
                    setBackgroundColor(color)
                    setOnClickListener { _ ->
                        val activity = XposedHelpers.callMethod(it.thisObject, "getActivity") as Activity
                        DialogUtils.popupSettingDialog(activity)
                    }
                }, linearLayout.childCount - 1)

                linearLayout.addView(ClickableItemView(root.context, "检查更新", "调试用").apply {
                    setBackgroundColor(color)
                    setOnClickListener { _ ->
                        val activity = XposedHelpers.callMethod(it.thisObject, "getActivity") as Activity
                        DialogUtils.popupCheckUpdate(activity)
                    }
                }, linearLayout.childCount - 1)

                linearLayout.addView(ClickableItemView(root.context, "更新日志", "调试用").apply {
                    setBackgroundColor(color)
                    setOnClickListener { _ ->
                        val activity = XposedHelpers.callMethod(it.thisObject, "getActivity") as Activity
                        DialogUtils.popupChangeLogDialog(activity)
                    }
                }, linearLayout.childCount - 1)

                linearLayout.addView(ClickableItemView(root.context, "重启 NGA", "调试用").apply {
                    setBackgroundColor(color)
                    setOnClickListener { _ ->
                        Helper.toast("正在重启")
                        val activity = XposedHelpers.callMethod(it.thisObject, "getActivity") as Activity
                        Helper.restartApplication(activity)
                    }
                }, linearLayout.childCount - 1)
            }
        }
    }

    override var name = "DebugHook"
}