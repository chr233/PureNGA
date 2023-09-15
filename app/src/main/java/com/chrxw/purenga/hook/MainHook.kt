package com.chrxw.purenga.hook

import android.content.Context
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.EzXHelper


/**
 * 插件初始化钩子
 */
class MainHook : IHook {
    companion object {
        lateinit var clsNGAApplication: Class<*>
        lateinit var clsAppConfig: Class<*>
        lateinit var clsLoadingActivity: Class<*>
        lateinit var clsSPUtil: Class<*>
    }

    override fun init(classLoader: ClassLoader) {
        clsNGAApplication = classLoader.loadClass("gov.pianzong.androidnga.activity.NGAApplication")
        clsAppConfig = classLoader.loadClass("com.donews.nga.common.utils.AppConfig")
        clsLoadingActivity = classLoader.loadClass("gov.pianzong.androidnga.activity.LoadingActivity")

        clsSPUtil = classLoader.loadClass("com.donews.nga.common.utils.SPUtil")

        Helper.clsRId = classLoader.loadClass("gov.pianzong.androidnga.R\$id")

        EzXHelper.appContext.run {
            Helper.spDoinfo = getSharedPreferences(Constant.DNINFO, Context.MODE_PRIVATE)
            Helper.spPlugin = getSharedPreferences(Constant.PLUGIN_PREFERENCE_NAME, Context.MODE_PRIVATE)
        }

    }

    override fun hook() {}
}