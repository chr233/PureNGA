package com.chrxw.purenga.hook

abstract class BaseHook(val mClassLoader: ClassLoader) {
    abstract fun startHook()
    open fun lateInitHook() {}
}