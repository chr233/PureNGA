package com.chrxw.purenga.hook

import android.content.Context
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.Helper


/**
 * 插件初始化钩子
 */
class MainHook : IHook {

    override fun hookName(): String {
        return "插件初始化"
    }

    override fun init(classLoader: ClassLoader) {
//        Helper.clsR = classLoader.loadClass("gov.pianzong.androidnga.R")
//        Helper.clsRId = classLoader.loadClass("gov.pianzong.androidnga.R\$id")
//        Helper.clsRColor = classLoader.loadClass("gov.pianzong.androidnga.R\$color")
//        Helper.clsRDimen = classLoader.loadClass("gov.pianzong.androidnga.R\$dimen")
//        Helper.clsRDrawable = classLoader.loadClass("gov.pianzong.androidnga.R\$drawable")
//        Helper.clsRLayout = classLoader.loadClass("gov.pianzong.androidnga.R\$layout")

        Helper.clsSPUtil = classLoader.loadClass("com.donews.nga.common.utils.SPUtil")
    }


    override fun hook() {
        //设置SharedPreferences
        Helper.spDoinfo = Helper.context.getSharedPreferences("dninfo", Context.MODE_PRIVATE)
        Helper.spPlugin = Helper.context.getSharedPreferences(Constant.PLUGIN_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
}