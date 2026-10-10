package com.chrxw.purenga

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.widget.Toast
import androidx.annotation.Keep
import com.chrxw.purenga.hook.DebugHook
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import io.github.kyuubiran.ezxhelper.android.logging.Logger
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface
import java.util.concurrent.atomic.AtomicBoolean


/**
 * 初始化Xposed
 */
@Keep
class XposedInit : XposedModule() {
    private val initialized = AtomicBoolean(false)

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        EzXposed.initOnModuleLoaded(this, param)

        Logger.i("模块已载入")
    }

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        if (param.packageName != Constant.NGA_PACKAGE_NAME) {
            return
        }

        Logger.tag = Constant.LOG_TAG
        EzXposed.initOnPackageLoaded(param)

        Logger.d("NGA内运行 onPackageLoaded")

        Helper.isXposed = true
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        if (param.packageName != Constant.NGA_PACKAGE_NAME) {
            return
        }

        Logger.i("a")

        if (!param.isFirstPackage) {
            return
        }

        EzXposed.initOnPackageReady(param)

        Logger.d("NGA内运行 onPackageReady")

        try {
            // 2. 反射创建 AssetManager 并添加模块 APK 路径
            val moduleInfo = getModuleApplicationInfo()

            val assets = AssetManager::class.java.newInstance()
            val addAssetPath =
                AssetManager::class.java.getMethod("addAssetPath", String::class.java)
            addAssetPath.invoke(assets, moduleInfo.sourceDir)

            // 3. 使用系统资源配置创建模块独立的 Resources 对象
            val systemRes = Resources.getSystem()
            Helper.moduleResources = Resources(
                assets,
                systemRes.displayMetrics,
                systemRes.configuration
            )

        } catch (t: Throwable) {
            // 处理异常
        }


        MethodFinder.fromClass(Instrumentation::class.java).filterByName("callApplicationOnCreate")
            .filterByAssignableParamTypes(Application::class.java).first().createHook {
                after {
                    it.log()

                    val app = it.args[0] as? Application
                    if (app == null) {
                        Logger.d("无法初始化")
                        return@after
                    }

                    if (initialized.compareAndSet(false, true)) {
                        installHooks(app.applicationContext)
                    } else {
                        Logger.d("跳过初始化")
                    }
                }
            }
    }

    private fun installHooks(context: Context) {
        EzXposed.initAppContext(context, false)
        Helper.context = EzXposed.appContext

        Logger.i("d")

        val error = Hooks.initHooks(context.classLoader)

        if (error == -1) {
            Logger.w("init hook 失败")
            return
        }

        if (BuildConfig.DEBUG) {
            Logger.w("!!! Debug 模式 !!!")
            val hook = DebugHook()
            try {
                hook.init(context.classLoader)
            } catch (e: Exception) {
                error + 1
                Logger.e("DebugHook 初始化失败", e)
            }

            try {
                hook.hook()
            } catch (e: Exception) {
                Logger.e("DebugHook Hook失败", e)
            }
        }

        if (error == 0) {
            if (!Helper.getSpBool(Constant.HIDE_HOOK_INFO, false)) {
                Helper.toast(
                    buildString {
                        appendLine("PureNGA 加载成功")
                        appendLine("【可以在设置中禁用】")
                    }, Toast.LENGTH_LONG
                )
            }
        } else {
            Helper.toast(
                buildString {
                    appendLine("PureNGA $error 个模块加载失败")
                    appendLine("可能不支持当前版本")
                    appendLine("NGA 版本: ${Helper.getNgaVersion()}")
                    appendLine("插件版本: ${BuildConfig.VERSION_NAME}")
                }, Toast.LENGTH_LONG
            )
        }
    }
}



