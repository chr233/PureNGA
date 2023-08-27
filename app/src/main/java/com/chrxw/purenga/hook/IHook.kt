package com.chrxw.purenga.hook

/**
 * 基础钩子
 */
interface IHook {
    /**
     * Hook名称
     */
    fun hookName(): String

    /**
     * 初始化Hook
     */
    fun init(classLoader: ClassLoader)

    /**
     * 执行Hook
     */
    fun hook()
}