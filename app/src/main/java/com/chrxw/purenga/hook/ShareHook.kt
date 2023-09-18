package com.chrxw.purenga.hook

import android.app.Activity
import com.chrxw.purenga.Constant
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

        private lateinit var MtdOnEvent: Method

        private fun fakeShare(tid: String) {
            if (::MtdOnEvent.isInitialized) {
                AndroidLogger.i("fake share $tid")
            } else {
                AndroidLogger.e("mtdOnEvent not inited")
            }
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

//        MethodFinder.fromClass("vg.a\$a", classLoader).filterByName("onError").first().createHook {

//        MethodFinder.fromClass("rg.a\$a", classLoader).filterByName("onError").first().createHook {
//            replace { param ->
//                val obj = param.thisObject
//
//                val arg0 = param.args[0]
//
//                XposedHelpers.callMethod(obj, "onResult", arg0)
//
//                return@replace null
//            }
//        }
    }

    override fun hook() {
        //假装分享
        if (Helper.getSpBool(Constant.FAKE_SHARE, false)) {

            var tid = "114514"
            var fid = ""

            //获取帖子信息
            MethodFinder.fromClass(clsArticleDetailActivity).filterByName("setThreadInfo").first().createHook {
                before { param ->
                    val post = param.args[0]
                    tid = XposedHelpers.callMethod(post, "getTid") as String
                    fid = XposedHelpers.callMethod(post, "getFid") as String
                }
            }

            //添加按钮
            MethodFinder.fromClass(clsBottomMenuDialog).filterByName("initMenus").first().createHook {
                before { param ->
                    val activity = param.thisObject
                    val menus = XposedHelpers.getObjectField(activity, "menus") as MutableList<*>

                    val newMenu = menus.filterIsInstance<Any>() as MutableList<Any>

                    var imgId = Helper.getDrawerId("icon_vip_all_function_gift_user_dark")
                    if (imgId == -1) {
                        imgId = Helper.getDrawerId("drawer_setting_icon")
                    }

                    val newItem2 = clsActionsInfo.getConstructor(String::class.java, Int::class.java)
                        .newInstance("假装分享", imgId)
                    newMenu.add(newItem2)

                    XposedHelpers.setObjectField(activity, "menus", newMenu)
                }
            }

            //内置浏览器分享点击事件
            MethodFinder.fromClass(clsCreateListener_1).filterByName("clickItem").first().createHook {
                before { param ->
                    val i = param.args[0] as Int
                    val btnName = param.args[1] as String
                    AndroidLogger.i("clickItem: i10 $i str4 $btnName")

                    if (btnName == "假装分享") {
                        fakeShare(tid)
                    }
                }
            }

            //帖子分享点击事件
            MethodFinder.fromClass(clsArticleDetailActivity_x).filterByName("clickItem").first().createHook {
                before { param ->
                    val i = param.args[0] as Int
                    val btnName = param.args[1] as String
                    AndroidLogger.i("clickItem $i $btnName")

                    if (btnName == "假装分享") {
                        fakeShare(tid)
                    }
                }
            }

            //Event回调
            MtdOnEvent = MethodFinder.fromClass(clsArticleDetailActivity).filterByName("onEvent").first()

            val clsEvt = MtdOnEvent.parameterTypes[0]

            AndroidLogger.i(clsEvt.name)


            MtdOnEvent.createHook {
                before { param ->
                    val arg = param.args[0]
                    val actionType = XposedHelpers.callMethod(arg, "c")
                    val data = XposedHelpers.callMethod(arg, "d")
                    val index = XposedHelpers.callMethod(actionType, "ordinal")
                    AndroidLogger.i("onEvent MainActivity $arg $actionType [ $index ] $data")
                }
            }

            //分享后网络请求
            MethodFinder.fromClass(clsNetRequestWrapper).filterByAssignableParamTypes(
                String::class.java, String::class.java, String::class.java, clsNetRequestCallback
            ).last().createHook {
                before { param ->
                    val str1 = param.args[0]
                    val str2 = param.args[1]
                    val str3 = param.args[2]

                    AndroidLogger.i("$str1 $str2 $str3")
                }
            }


        }

        //绕过分享前验证是否安装App
        if (Helper.getSpBool(Constant.FORCE_INSTALLED, false)) {
            MethodFinder.fromClass(clsUMShareAPI).filterByName("isInstall").first().createHook {
                replace { param ->
                    val activity = param.args[0] as Activity
                    val mode = param.args[1]
                    AndroidLogger.i("isInstall $activity mode $mode")
                    return@replace true
                }
            }
        }
    }

}
