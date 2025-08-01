package com.chrxw.purenga.hook

import android.app.Activity
import android.widget.ImageView
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


class DebugHook : IHook {
    companion object {

    }

    override fun init(classLoader: ClassLoader) {
        ConstructorFinder.fromClass("gov.pianzong.androidnga.databinding.FragmentHomeActivityBinding", classLoader)
            .firstOrNull()?.createHook {
                after {
                    it.log()

                    AndroidLogger.i("111111")

                    val menuBtn = it.args[2] as ImageView
                    AndroidLogger.i(menuBtn.toString())

                    menuBtn.setOnLongClickListener {

                        AndroidLogger.i("222")

                        true
                    }
                }
            } ?: AndroidLogger.e("FragmentHomeActivityBinding not found")

        MethodFinder.fromClass("com.donews.nga.fragments.HomeActivityFragment", classLoader).filterByName("initLayout")
            .firstOrNull()?.createHook {
                after {
                    it.log()
                    AndroidLogger.i("333")

                }
            }

        MethodFinder.fromClass(
            "com.donews.nga.activitys.MainActivity",
            classLoader
        ).filterByName(
            "setActivityFragment"
        ).firstOrNull()?.createHook {
            after {
                it.log()

                AndroidLogger.w("setActivityFragment called")
            }
        }
    }

    override fun hook() {
        //显示首次运行提示

    }

    override var name = "DebugHook"
}