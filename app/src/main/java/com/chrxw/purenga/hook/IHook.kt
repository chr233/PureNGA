package com.chrxw.purenga.hook

/**
 * 基础钩子
 */
interface IHook {
    /**
     * 初始化Hook
     */
    fun init(classLoader: ClassLoader)

    /**
     * 执行Hook
     */
    fun hook()

    /**
     * 模块名
     */
    var name: String
}