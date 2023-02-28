package com.chrxw.purenga.hook

/**
 * 基础钩子
 */
abstract class BaseHook(val mClassLoader: ClassLoader) {
    abstract fun startHook()
}