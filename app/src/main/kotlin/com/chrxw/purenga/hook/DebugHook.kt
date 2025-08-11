package com.chrxw.purenga.hook

import com.chrxw.purenga.hook.base.IHook


class DebugHook : IHook {
    override fun init(classLoader: ClassLoader) {


    }

    override fun hook() {
        //显示首次运行提示

    }

    override var name = "DebugHook"
}