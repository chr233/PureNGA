package com.chrxw.purenga.hook

import android.view.View
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.forceLog
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder
import com.github.kyuubiran.ezxhelper.finders.FieldFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder


class DebugHook : IHook {
    companion object {

    }

    override fun init(classLoader: ClassLoader) {

        MethodFinder.fromClass(
            "com.donews.nga.adapters.ForumFoldListAdapter\$GameRecommendBinder\$getItemView$\$inlined\$onClick$1",
            classLoader
        ).filterByName("onClick").firstOrNull()?.createHook {
            before {
                it.forceLog()
            }
        }

        val fidBinding =
            FieldFinder.fromClass("com.donews.nga.adapters.ForumFoldListAdapter\$GameRecommendBinder", classLoader)
                .filterByName("binding").first()

        MethodFinder.fromClass("com.donews.nga.adapters.ForumFoldListAdapter\$GameRecommendBinder", classLoader)
            .filterByName("getItemView").firstOrNull()?.createHook {
                after {
                    it.forceLog()

                    val binding = fidBinding.get(it.thisObject)
                    if (binding != null) {
                        FieldFinder.fromClass(
                            "gov.pianzong.androidnga.databinding.LayoutVoteGameRecommendCommunityBinding", classLoader
                        ).forEach { fid ->
                            {
                                val value = fid.get(binding)
                                if (value is View) {
                                    value.visibility = View.INVISIBLE
                                }
                            }
                        }
                    }else{
                        AndroidLogger.e("binding is null")
                    }
                }
            }

        MethodFinder.fromClass("com.donews.nga.activitys.MainActivity\$setupCenterMenu$1", classLoader)
            .filterByName("emit").filterByParamCount(2).firstOrNull()?.createHook {
                after {
                    it.forceLog()

                }
            }

        ConstructorFinder.fromClass("com.donews.nga.activitys.MainActivity\$setupCenterMenu$1", classLoader)
            .firstOrNull()?.createHook {
                after {
                    it.forceLog()

//                    val activity = it.args[0] as Activity
//                    val binding = XposedHelpers.getObjectField(activity, "viewBinding")
//
//                    val viewB = XposedHelpers.getObjectField(binding, "b") as View
//                    viewB.visibility = View.INVISIBLE
//
//                    val viewE = XposedHelpers.getObjectField(binding, "e") as View
//                    viewE.visibility = View.INVISIBLE
                }
            }

        val fidTabs = FieldFinder.fromClass("com.donews.nga.activitys.presenters.MainActivityPresenter", classLoader)
            .filterByName("tabParams").first()

        MethodFinder.fromClass("com.donews.nga.activitys.presenters.MainActivityPresenter", classLoader)
            .filterByName("initOtherData").firstOrNull()?.createHook {
                replace {
                    it.forceLog()

                    val tabs = fidTabs.get(it.thisObject) as ArrayList<*>
                    Helper.toast(tabs.size.toString())
                }
            }

        MethodFinder.fromClass(
            "com.donews.nga.activitys.presenters.MainActivityPresenter\$checkBindGamePlatformStatus$1", classLoader
        ).filterByName("complete").firstOrNull()?.createHook {
            after {
                it.forceLog()
            }
        }

    }

    override fun hook() {
        //显示首次运行提示

    }

    override var name = "DebugHook"
}