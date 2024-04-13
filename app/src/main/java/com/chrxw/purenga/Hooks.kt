package com.chrxw.purenga

import com.chrxw.purenga.hook.AboutHook
import com.chrxw.purenga.hook.AdHook
import com.chrxw.purenga.hook.MainHook
import com.chrxw.purenga.hook.OptimizeHook
import com.chrxw.purenga.hook.PreferencesHook
import com.chrxw.purenga.hook.RewardHook
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
        MainHook(),
        OptimizeHook(),
        AdHook(),
        SpUtilsHook(),
        RewardHook(),
        PreferencesHook(),
        AboutHook(),
        WebViewHook(),
        ShareHook(),
        ShortcutHook(),
    )

    /**
     * 初始化钩子
     */
    fun initHooks(classLoader: ClassLoader): Int {
        var error = 0
        for (hook in hooks) {
            val name = hook.name
            try {
                AndroidLogger.i("加载 $name 模块")
                hook.init(classLoader)
                hook.hook()
            } catch (e: NoSuchMethodError) {
                Helper.toast("模块 $name 加载失败, 可能不支持当前版本的NGA")
                error++
                AndroidLogger.e(e)
            } catch (e: Throwable) {
                Helper.toast("模块 $name 加载遇到未知错误")
                error++
                AndroidLogger.e(e)
            }
        }
        return error
    }

}