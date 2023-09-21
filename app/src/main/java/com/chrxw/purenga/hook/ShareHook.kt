package com.chrxw.purenga.hook

import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method


/**
 * 假装分享钩子
 */
class ShareHook : IHook {
    companion object {
        private lateinit var clsActionsInfo: Class<*>
        private lateinit var clsCreateListener_1: Class<*>
        private lateinit var clsUMShareAPI: Class<*>
        private lateinit var clsBottomMenuDialog: Class<*>
        private lateinit var clsArticleDetailActivity_x: Class<*>
        private lateinit var clsArticleDetailActivity: Class<*>
        private lateinit var clsNetRequestWrapper: Class<*>
        private lateinit var clsNetRequestCallback: Class<*>
        private lateinit var clsActionType: Class<*>
        private lateinit var clsEvt: Class<*>
        private lateinit var eShareSuccess: Any
        private lateinit var MtdOnEvent: Method

        /**
         * 假装分享
         */
        private fun fakeShare(obj: Any, num: Int) {
            val evt = clsEvt.getDeclaredConstructor(clsActionType, Any::class.java)
                .newInstance(eShareSuccess, num)
            MtdOnEvent.invoke(obj, evt)
        }
    }

    override fun init(classLoader: ClassLoader) {
        clsActionsInfo = classLoader.loadClass("gov.pianzong.androidnga.model.ActionsInfo")
        clsCreateListener_1 =
            classLoader.loadClass("com.donews.nga.fragments.CommonWebFragment\$JsInterface\$createListener$1")
        clsUMShareAPI = classLoader.loadClass("com.umeng.socialize.UMShareAPI")
        clsBottomMenuDialog = classLoader.loadClass("gov.pianzong.androidnga.view.BottomMenuDialog")
        clsArticleDetailActivity_x =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity\$x")
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")
        clsNetRequestWrapper = classLoader.loadClass("gov.pianzong.androidnga.activity.NetRequestWrapper")
        clsNetRequestCallback = classLoader.loadClass("gov.pianzong.androidnga.activity.NetRequestCallback")
        clsActionType = classLoader.loadClass("gov.pianzong.androidnga.event.ActionType")

        MtdOnEvent = MethodFinder.fromClass(clsArticleDetailActivity).filterByName("onEvent").first()
        clsEvt = MtdOnEvent.parameterTypes[0]
    }

    override fun hook() {
        //假装分享
        if (Helper.getSpBool(Constant.FAKE_SHARE, false)) {
            //获取枚举值
            for (enum in clsActionType.enumConstants) {
                if (enum.toString() == "SHARE_SUCCESS") {
                    eShareSuccess = enum
                    break
                }
            }

            //维护 objArticleDetailActivity 对象
            var objArticleDetailActivity: Any? = null
            MethodFinder.fromClass(clsArticleDetailActivity).filterByName("onCreate").first().createHook {
                before {
                    it.log()
                    objArticleDetailActivity = it.thisObject
                }
            }
            MethodFinder.fromClass(clsArticleDetailActivity).filterByName("onDestroy").first().createHook {
                before {
                    it.log()
                    objArticleDetailActivity = null
                }
            }

            var tid: String

            //获取帖子信息
            MethodFinder.fromClass(clsArticleDetailActivity).filterByName("setThreadInfo").first().createHook {
                before {
                    it.log()

                    objArticleDetailActivity = it.thisObject

                    val post = it.args[0]
                    tid = XposedHelpers.callMethod(post, "getTid") as String
                    val fid = XposedHelpers.callMethod(post, "getFid") as String

                    AndroidLogger.i("tid $tid fid $fid")
                }
            }

            //添加按钮
            MethodFinder.fromClass(clsBottomMenuDialog).filterByName("initMenus").first().createHook {
                before {
                    it.log()

                    val activity = it.thisObject
                    val menus = XposedHelpers.getObjectField(activity, "menus") as MutableList<*>

                    val newMenu = menus.filterIsInstance<Any>() as MutableList<Any>

                    var imgId = Helper.getDrawerId("icon_vip_all_function_gift_user_dark")
                    if (imgId == -1) {
                        imgId = Helper.getDrawerId("drawer_setting_icon")
                    }

                    val fakeShare = clsActionsInfo.getConstructor(String::class.java, Int::class.java)
                        .newInstance(Constant.STR_FAKE_SHARE, imgId)
                    newMenu.add(fakeShare)

                    val fakeShare3 = clsActionsInfo.getConstructor(String::class.java, Int::class.java)
                        .newInstance(Constant.STR_FAKE_SHARE_TRIPLE, imgId)
                    newMenu.add(fakeShare3)


                    XposedHelpers.setObjectField(activity, "menus", newMenu)
                }
            }

            //内置浏览器分享点击事件
            MethodFinder.fromClass(clsCreateListener_1).filterByName("clickItem").first().createHook {
                before {
                    it.log()

                    val i = it.args[0] as Int
                    val btnName = it.args[1] as String
                    AndroidLogger.i("clickItem: i10 $i str4 $btnName")

                    when (btnName) {
                        Constant.STR_FAKE_SHARE, Constant.STR_FAKE_SHARE_TRIPLE -> Helper.toast("假装分享成功")
                    }
                }
            }

            //帖子分享点击事件
            MethodFinder.fromClass(clsArticleDetailActivity_x).filterByName("clickItem").first().createHook {
                before {
                    it.log()

                    when (val btnName = it.args[1] as String) {
                        Constant.STR_FAKE_SHARE, Constant.STR_FAKE_SHARE_TRIPLE -> {
                            if (objArticleDetailActivity != null) {
                                val num = (1..4)

                                fakeShare(objArticleDetailActivity!!, num.random())

                                if (btnName == Constant.STR_FAKE_SHARE_TRIPLE) {
                                    Thread.sleep(100)
                                    fakeShare(objArticleDetailActivity!!, num.random())
                                    Thread.sleep(100)
                                    fakeShare(objArticleDetailActivity!!, num.random())
                                }

                                Helper.toast("假装分享成功")
                            } else {
                                Helper.toast("假装分享失败")
                            }
                        }
                    }
                }
            }

            if (BuildConfig.DEBUG) {
                //Event回调
                MtdOnEvent.createHook {
                    before {
                        it.log()

                        val arg = it.args[0]
                        val actionType = XposedHelpers.callMethod(arg, "c")
                        val data = XposedHelpers.callMethod(arg, "d")
                        val list = XposedHelpers.callMethod(arg, "e") as List<*>?
                        val index = XposedHelpers.callMethod(actionType, "ordinal")

                        AndroidLogger.i("onEvent $arg $actionType[$index] $data")

                        if (list != null) {
                            val str = list.joinToString { x -> x.toString() }
                            AndroidLogger.i("list $str")
                        }
                    }
                }
            }
        }

        //绕过分享前验证是否安装App
        if (Helper.getSpBool(Constant.BYPASS_INSTALL_CHECK, false)) {
            MethodFinder.fromClass(clsUMShareAPI).filterByName("isInstall").first().createHook {
                replace {
                    it.log()
                    return@replace true
                }
            }
        }
    }

}
