package com.chrxw.purenga.utils

import com.chrxw.purenga.Constant
import io.github.kyuubiran.ezxhelper.android.logging.Logger
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper

/**
 * 模块运行状态
 *
 * api102: 现代 API 只注入 scope 内声明的作用域(gov.pianzong.androidnga), 模块 App 自身进程不会被注入,
 * 因此改用框架的 service 查询运行目标
 */
object StatusUtils {
    private var registered = false

    var modelEnabled = false

    fun start(onUpdate: () -> Unit) {
        if (registered) {
            return
        }
        registered = true

        try {
            XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
                override fun onServiceBind(service: XposedService) {
                    // 回调运行在 Binder 线程, 调用方负责切回主线程
                    StatusUtils.modelEnabled = queryRunning(service)
                    onUpdate()
                }

                override fun onServiceDied(service: XposedService) {
                    StatusUtils.modelEnabled = false
                    onUpdate()
                }
            })
        } catch (e: Throwable) {
            // 框架不支持 service 时仅影响状态显示, 不影响模块功能
            Logger.e("模块状态服务不可用", e)
        }
    }

    private fun queryRunning(service: XposedService): Boolean {
        return try {
            service.runningTargets.any { it.processName.startsWith(Constant.NGA_PACKAGE_NAME) }
        } catch (e: Throwable) {
            Logger.e("查询模块运行状态失败", e)
            false
        }
    }
}
