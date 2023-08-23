package com.chrxw.purenga.hook

import android.R.attr
import android.app.AndroidAppHelper
import android.app.Application
import android.app.Instrumentation
import android.widget.Toast
import com.chrxw.purenga.utils.Helper
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 插件初始化钩子
 */
class MainHook : IHook {

    companion object {
    }

    override fun hookName(): String {
        return "插件初始化"
    }

    override fun init(classLoader: ClassLoader) {
        Helper.clsR = classLoader.loadClass("gov.pianzong.androidnga.R")
        Helper.clsRId = classLoader.loadClass("gov.pianzong.androidnga.R\$id")
        Helper.clsRColor = classLoader.loadClass("gov.pianzong.androidnga.R\$color")
        Helper.clsRDimen = classLoader.loadClass("gov.pianzong.androidnga.R\$dimen")
        Helper.clsRDrawable = classLoader.loadClass("gov.pianzong.androidnga.R\$drawable")
        Helper.clsRLayout = classLoader.loadClass("gov.pianzong.androidnga.R\$layout")
    }


    override fun hook() {
    }
}