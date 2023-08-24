package com.chrxw.purenga.hook

import com.chrxw.purenga.utils.Helper


/**
 * 插件初始化钩子
 */
class MainHook : IHook {

    override fun hookName(): String {
        return "插件初始化"
    }

    override fun init(classLoader: ClassLoader) {
        Helper.clsSPUtil = classLoader.loadClass("com.donews.nga.common.utils.SPUtil")
        Helper.clsR = classLoader.loadClass("gov.pianzong.androidnga.R")
        Helper.clsRId = classLoader.loadClass("gov.pianzong.androidnga.R\$id")
    }

    override fun hook() {
    }
}