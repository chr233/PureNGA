package com.chrxw.purenga

import com.chrxw.purenga.hook.AboutHook
import com.chrxw.purenga.hook.AdHook
import com.chrxw.purenga.hook.MainHook
import com.chrxw.purenga.hook.OptimizeHook
import com.chrxw.purenga.hook.PreferencesHook
import com.chrxw.purenga.hook.RewardHook
import com.chrxw.purenga.hook.SplashHook
import com.chrxw.purenga.hook.WebViewHook
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log

class Hooks {
    companion object {
        private val hooks = arrayOf(
            MainHook(),
            OptimizeHook(),
            SplashHook(),
            RewardHook(),
            AdHook(),
            PreferencesHook(),
            AboutHook(),
            WebViewHook(),
        )

        fun initHooks(classLoader: ClassLoader) {
            for (hook in hooks) {
                val name = hook.hookName()
                try {
                    Log.i("加载 $name 模块")
                    hook.init(classLoader)
                    hook.hook()
                } catch (e: NoSuchMethodError) {
                    Helper.toast("模块 $name 加载失败, 可能不支持当前版本的NGA")
                    Log.e(e)
                } catch (e: Exception) {
                    Helper.toast("模块 $name 加载遇到未知错误")
                    Log.e(e)
                }
            }
        }
    }
}