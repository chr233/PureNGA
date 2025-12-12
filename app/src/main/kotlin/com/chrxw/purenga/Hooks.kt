package com.chrxw.purenga

import com.chrxw.purenga.hook.AdHook
import com.chrxw.purenga.hook.MainHook
import com.chrxw.purenga.hook.OptimizeHook
import com.chrxw.purenga.hook.PreferencesHook
import com.chrxw.purenga.hook.ShareHook
import com.chrxw.purenga.hook.ShortcutHook
import com.chrxw.purenga.hook.SpUtilsHook
import com.chrxw.purenga.hook.WebViewHook
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger

/**
 * 钩子
 */
object Hooks {
    private val hooks = arrayOf(
        AdHook(),
        OptimizeHook(),
        SpUtilsHook(),
        PreferencesHook(),
        WebViewHook(),
        ShareHook(),
        ShortcutHook(),
    )

    /**
     * 初始化钩子
     */
    fun initHooks(classLoader: ClassLoader): Int {
        val mainHook = MainHook()
        try {
            mainHook.init(classLoader)
            mainHook.hook()
        } catch (e: Throwable) {
            Helper.toast("插件核心无法初始化, 可能不适配当前版本")
            AndroidLogger.e(e)
            return 1
        }

        if(Helper.getSpBool(Constant.FORBID_LOAD,false)){
            return -1
        }


        val hideError = Helper.getSpBool(Constant.HIDE_ERROR_INFO, false)

        var error = 0
        for (hook in hooks) {
            val name = hook.name
            try {
                AndroidLogger.i("加载 $name 模块")
                hook.init(classLoader)
                hook.hook()
            } catch (e: NoSuchMethodError) {
                if (!hideError) {
                    Helper.toast("模块 $name 加载失败, 可能不支持当前版本的NGA")
                }
                error++
                AndroidLogger.e(e)
            } catch (e: Throwable) {
                if (!hideError) {
                    Helper.toast("模块 $name 加载遇到未知错误")
                }
                error++
                AndroidLogger.e(e)
            }
        }
        return error
    }
}