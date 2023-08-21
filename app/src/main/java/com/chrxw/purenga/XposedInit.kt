package com.chrxw.purenga

import android.content.res.Resources
import android.content.res.XModuleResources
import android.widget.Toast
import com.chrxw.purenga.hook.AboutHook
import com.chrxw.purenga.hook.AdHook
import com.chrxw.purenga.hook.IHook
import com.chrxw.purenga.hook.MainHook
import com.chrxw.purenga.hook.PreferencesHook
import com.chrxw.purenga.hook.RewardHook
import com.chrxw.purenga.hook.SplashHook
import com.chrxw.purenga.hook.VipHook
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.*
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.text.MessageFormat

/**
 * 初始化Xposed
 */
class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        modulePath = startupParam.modulePath
        moduleRes = getModuleRes(modulePath)
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == BuildConfig.APPLICATION_ID) {
            Log.d("模块内运行")

            XposedHelpers.findAndHookMethod(
                MainActivity.Companion::class.java.name,
                lpparam.classLoader,
                "isModuleActive",
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        return true
                    }
                }
            )
        } else if (lpparam.packageName == Constant.NGA_PACKAGE_NAME) {
            Log.d("NGA内运行")

            initHooks(
                lpparam.classLoader,
                MainHook(),
            )

            initHooks(
                lpparam.classLoader,
                SplashHook(),
                RewardHook(),
                AdHook(),
                PreferencesHook(),
                AboutHook(),
            )
        }
    }

    companion object {
        lateinit var modulePath: String
        lateinit var moduleRes: Resources

        @JvmStatic
        fun getModuleRes(path: String): Resources {
            return XModuleResources.createInstance(path, null)
        }

        fun initHooks(classLoader: ClassLoader, vararg hooks: IHook) {
            for (hook in hooks) {
                try {
                    Log.i(MessageFormat.format("Hook {0} 加载", hook.hookName()))
                    hook.init(classLoader)
                    hook.hook()
                } catch (e: NoSuchMethodError) {
                    Helper.showToast(
                        MessageFormat.format(
                            "Hook {0} 加载失败, 可能不支持当前版本的NGA",
                            hook.hookName()
                        )
                    )
                    Log.e(e)
                } catch (e: Exception) {
                    Helper.showToast(
                        MessageFormat.format(
                            "Hook {0} 遇到未知错误",
                            hook.hookName()
                        )
                    )
                    Log.e(e)
                }
            }
        }
    }
}
