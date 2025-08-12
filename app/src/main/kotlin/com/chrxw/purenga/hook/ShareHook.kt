package com.chrxw.purenga.hook

import com.chrxw.purenga.Constant
import com.chrxw.purenga.R
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.getStringFromMod
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
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
        private lateinit var clsArticleDetailActivity: Class<*>
        private lateinit var clsArticleDetailActivity_x: Class<*>
        private lateinit var clsArticleDetailActivity_u: Class<*>
        private lateinit var clsActionType: Class<*>
        private lateinit var clsEvt: Class<*>

        private lateinit var eShareSuccess: Any
        private lateinit var MtdOnEvent: Method
        private lateinit var strFakeShare: String
        private lateinit var strFakeShareThree: String

        /**
         * 假装分享
         */
        private fun fakeShare(obj: Any, num: Int) {
            val evt = clsEvt.getDeclaredConstructor(clsActionType, Any::class.java).newInstance(eShareSuccess, num)
            MtdOnEvent.invoke(obj, evt)
        }
    }

    override fun init(classLoader: ClassLoader) {
        clsActionsInfo = classLoader.loadClass("gov.pianzong.androidnga.model.ActionsInfo")
        clsCreateListener_1 =
            classLoader.loadClass("com.donews.nga.fragments.CommonWebFragment\$JsInterface\$createListener$1")
        clsUMShareAPI = classLoader.loadClass("com.umeng.socialize.UMShareAPI")
        clsBottomMenuDialog = classLoader.loadClass("gov.pianzong.androidnga.view.BottomMenuDialog")
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")
        clsArticleDetailActivity_x =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity\$x")
        clsArticleDetailActivity_u =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity\$u")
        clsActionType = classLoader.loadClass("gov.pianzong.androidnga.event.ActionType")

        MtdOnEvent = findFirstMethodByName(clsArticleDetailActivity, "onEvent")!!
        clsEvt = MtdOnEvent.parameterTypes[0]

        strFakeShare = R.string.fake_share.getStringFromMod()
        strFakeShareThree = R.string.fake_share_three.getStringFromMod()
    }

    override fun hook() {
        //假装分享
        if (Helper.getSpBool(Constant.FAKE_SHARE, false)) {
            //获取枚举值
            for (enum in clsActionType.enumConstants!!) {
                if (enum.toString() == "SHARE_SUCCESS") {
                    eShareSuccess = enum
                    break
                }
            }

            //维护 objArticleDetailActivity 对象
            var objArticleDetailActivity: Any? = null
            findFirstMethodByName(clsArticleDetailActivity, "onCreate")?.createHook {
                before {
                    it.log()
                    objArticleDetailActivity = it.thisObject
                }
            }
            findFirstMethodByName(clsArticleDetailActivity, "onDestroy")?.createHook {
                before {
                    it.log()
                    objArticleDetailActivity = null
                }
            }

            var tid: String

            //获取帖子信息
            findFirstMethodByName(clsArticleDetailActivity, "setThreadInfo")?.createHook {
                before {
                    it.log()

                    objArticleDetailActivity = it.thisObject

                    val post = it.args[0]
                    tid = XposedHelpers.callMethod(post, "getTid") as String
                    val fid = XposedHelpers.callMethod(post, "getFid") as String

                    if (Helper.getSpBool(Constant.ENABLE_POST_LOG, false)) {
                        AndroidLogger.i("tid $tid fid $fid")
                    }
                }
            }

            //添加按钮
            findFirstMethodByName(clsBottomMenuDialog, "initMenus")?.createHook {
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
                        .newInstance(strFakeShare, imgId)
                    newMenu.add(fakeShare)

                    val fakeShare3 = clsActionsInfo.getConstructor(String::class.java, Int::class.java)
                        .newInstance(strFakeShareThree, imgId)
                    newMenu.add(fakeShare3)

                    XposedHelpers.setObjectField(activity, "menus", newMenu)
                }
            }

            //内置浏览器分享点击事件
            findFirstMethodByName(clsCreateListener_1, "clickItem")?.createHook {
                before {
                    it.log()

                    val i = it.args[0] as Int
                    val btnName = it.args[1] as String
                    AndroidLogger.i("clickItem: i10 $i str4 $btnName")

                    if (btnName == strFakeShare || btnName == strFakeShareThree) {
                        Helper.toast("假装分享成功")
                    }
                }
            }

            //帖子分享点击事件
            val mtdClickItem = findFirstMethodByName(clsArticleDetailActivity_u, "clickItem") ?: findFirstMethodByName(
                clsArticleDetailActivity_x,
                "clickItem"
            )

            mtdClickItem?.createHook {
                before {
                    it.log()

                    val btnName = it.args[1] as String

                    if (btnName == strFakeShare || btnName == strFakeShareThree) {
                        if (objArticleDetailActivity != null) {
                            val num = (1..4)

                            fakeShare(objArticleDetailActivity!!, num.random())

                            if (btnName == strFakeShareThree) {
                                Thread.sleep(100)
                                fakeShare(objArticleDetailActivity!!, num.random())
                                Thread.sleep(100)
                                fakeShare(objArticleDetailActivity!!, num.random())
                            }

                            Helper.toast(R.string.fake_share_succes.getStringFromMod())
                        } else {
                            Helper.toast(R.string.fake_share_failed.getStringFromMod())
                        }
                    }
                }
            } ?: {
                Helper.toast(R.string.fake_share_failed_not_support.getStringFromMod())
            }
        }

        //绕过分享前验证是否安装App
        if (Helper.getSpBool(Constant.BYPASS_INSTALL_CHECK, false)) {
            findFirstMethodByName(clsUMShareAPI, "isInstall")?.createHook {
                replace {
                    it.log()
                    return@replace true
                }
            }
        }
    }

    override var name = "ShareHook"
}
