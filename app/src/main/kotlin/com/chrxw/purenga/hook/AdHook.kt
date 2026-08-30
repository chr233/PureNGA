package com.chrxw.purenga.hook

import android.app.Activity
import android.view.View
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.findMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.MemberExtensions.isAbstract
import com.github.kyuubiran.ezxhelper.finders.FieldFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field


/**
 * 广告钩子
 */
class AdHook : IHook {
    /**
     *
     */
    companion object {
        private lateinit var clsDnFeedAd: Class<*>
        private lateinit var clsNativeExpressAD: Class<*>
        private lateinit var clsUtils_bp: Class<*>
        private lateinit var clsAdSize: Class<*>
        private lateinit var clsZkAdNativeImpl: Class<*>
        private lateinit var clsLoadingActivity_a: Class<*>
        private lateinit var clsKsAdSDK: Class<*>
        private lateinit var clsTTAdSdk: Class<*>
        private lateinit var clsDnAdNativeClass: Class<*>
        private lateinit var clsDnTapFeedAd: Class<*>
        lateinit var clsPostListFragment: Class<*>
        private lateinit var clsHomeRecommendFragment: Class<*>
        private lateinit var clsActivityEntity: Class<*>
        private lateinit var clsSubject: Class<*>
        private lateinit var clsBaseActivity: Class<*>
        private lateinit var clsBannerHolder: Class<*>
        lateinit var fldViewBinding: Field
        private var fldForumFoldBinding: Field? = null
        private lateinit var fldGameRecommendBindings: List<Field>
        private lateinit var clsGameRecommendBinder: Class<*>
        private var fldBannerHolderViewC: Field? = null
        lateinit var clsHomeFragment: Class<*>

        fun isClsZkAdNativeImplInit() = ::clsZkAdNativeImpl.isInitialized
        fun isClsKsAdSDKInit() = ::clsKsAdSDK.isInitialized
        fun isClsTTAdSdkInit() = ::clsTTAdSdk.isInitialized
        fun isClsDnAdNativeClassInit() = ::clsDnAdNativeClass.isInitialized
        fun isClsDnTapFeedAdInit() = ::clsDnTapFeedAd.isInitialized
    }

    override fun init(classLoader: ClassLoader) {
        clsDnFeedAd = classLoader.loadClass("com.donews.admediation.adimpl.feed.DnFeedAd")
        clsNativeExpressAD = classLoader.loadClass("com.qq.e.ads.nativ.NativeExpressAD")
        clsAdSize = classLoader.loadClass("com.qq.e.ads.nativ.ADSize")
        clsUtils_bp = classLoader.loadClass("com.kwad.sdk.utils.bp")
        try {
            clsZkAdNativeImpl = classLoader.loadClass("com.donews.zkad.api.ZkAdNativeImpl")
        } catch (e: Throwable) {
            AndroidLogger.e(e)
        }
        clsLoadingActivity_a = classLoader.loadClass("gov.pianzong.androidnga.activity.LoadingActivity\$a")

        try {
            clsKsAdSDK = classLoader.loadClass("com.kwad.sdk.api.KsAdSDK")
            clsTTAdSdk = classLoader.loadClass("com.bytedance.sdk.openadsdk.TTAdSdk")
            clsDnAdNativeClass = classLoader.loadClass("com.donews.b.start.DnAdNative")
            clsDnTapFeedAd = classLoader.loadClass("com.donews.admediation.adimpl.feed.DnTapFeedAd")
        } catch (e: Throwable) {
            AndroidLogger.e(e)
        }

        clsPostListFragment = classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.PostListFragment")
        clsHomeRecommendFragment = classLoader.loadClass("com.donews.nga.fragments.HomeRecommendFragment")
        clsActivityEntity = classLoader.loadClass("com.donews.nga.entity.ActivityEntity")
        clsSubject = classLoader.loadClass("gov.pianzong.androidnga.model.Subject")
        clsBaseActivity = classLoader.loadClass("com.donews.nga.common.base.BaseActivity")
        clsBannerHolder = classLoader.loadClass("com.donews.nga.adapters.HomeRecommendAdapter\$BannerHolder")
        clsHomeFragment = classLoader.loadClass("com.donews.nga.fragments.HomeFragment")
        fldViewBinding = FieldFinder.fromClass(clsBaseActivity).filterByName("viewBinding").first()

        try {
            fldForumFoldBinding =
                FieldFinder.fromClass("com.donews.nga.adapters.ForumFoldListAdapter\$GameRecommendBinder", classLoader)
                    .filterByName("binding").firstOrNull()
            fldGameRecommendBindings = FieldFinder.fromClass(
                "gov.pianzong.androidnga.databinding.LayoutVoteGameRecommendCommunityBinding", classLoader
            ).toList()
            clsGameRecommendBinder =
                classLoader.loadClass("com.donews.nga.adapters.ForumFoldListAdapter\$GameRecommendBinder")

            fldBannerHolderViewC = FieldFinder.fromClass(
                "gov.pianzong.androidnga.databinding.ItemHomeRecommendBannerLayoutBinding", classLoader
            ).filterByName("e").firstOrNull()
        } catch (ex: Throwable) {
            AndroidLogger.e(ex)
        }

    }

    override fun hook() {
        //屏蔽广告
        if (Helper.getSpBool(Constant.PURE_POST_AD, false)) {
            val hook1 = findFirstMethodByName(clsDnFeedAd, "requestServerSuccess")?.createHook {
                replace {
                    it.log()
                }
            }
            if (hook1 == null) {
                AndroidLogger.e("Donews 广告过滤失败")
            }

            val hook2 = findMethodByName(clsNativeExpressAD, "a").filterByAssignableParamTypes(clsAdSize).firstOrNull()
                ?.createHook {
                    replace {
                        it.log()
                        return@replace true
                    }
                }
            if (hook2 == null) {
                AndroidLogger.e("qq 广告过滤失败")
            }

            findMethodByName(clsUtils_bp, "runOnUiThread").forEach { method ->
                method.createHook {
                    replace {
                        it.log()
                        return@replace true
                    }
                }
            }

            if (isClsZkAdNativeImplInit()) {
                MethodFinder.fromClass(clsZkAdNativeImpl).forEach { mtd ->
                    val name = mtd.name
                    if (name.startsWith("load") && name.endsWith("Ad") && !mtd.isAbstract) {
                        mtd.createHook {
                            replace {
                                it.log()
                                AndroidLogger.i(mtd.name)
                            }
                        }
                    }
                }
            }

            //9.9.3 preThirdParty 改为 initThirdParty
            findFirstMethodByName(MainHook.clsNGAApplication, "preThirdParty")
                ?: findFirstMethodByName(MainHook.clsNGAApplication, "initThirdParty")?.createHook {
                    replace {
                        it.log()
                    }
                }

            findFirstMethodByName(MainHook.clsLoadingActivity, "loadAD")?.createHook {
                replace {
                    it.log()
                }
            } ?: {
                AndroidLogger.d("clsLoadingActivity loadAD 匹配失败")
            }

            if (isClsKsAdSDKInit()) {
                val hook3 = findFirstMethodByName(clsKsAdSDK, "init")?.createHook {
                    replace {
                        it.log()
                        return@replace false
                    }
                }
                if (hook3 == null) {
                    AndroidLogger.d("快手广告过滤失败")
                }
            }

            if (isClsTTAdSdkInit()) {
                val hook4 = findFirstMethodByName(clsTTAdSdk, "init")?.createHook {
                    replace {
                        it.log()
                        return@replace false
                    }
                }
                if (hook4 == null) {
                    AndroidLogger.d("穿山甲广告过滤失败")
                }
            }

            if (isClsDnAdNativeClassInit()) {
                MethodFinder.fromClass(clsDnAdNativeClass).forEach { method ->
                    val mtdName = method.name
                    if (!method.isAbstract) {
                        if (mtdName.startsWith("load") && mtdName.endsWith("Ad")) {
                            method.createHook {
                                replace {
                                    it.log()

                                    if (BuildConfig.DEBUG) {
                                        AndroidLogger.i("clsDnAdNativeClass $mtdName")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isClsDnTapFeedAdInit()) {
                MethodFinder.fromClass(clsDnTapFeedAd).forEach { method ->
                    val mtdName = method.name
                    if (!method.isAbstract) {
                        if (mtdName.startsWith("load") && mtdName.endsWith("Ad")) {
                            method.createHook {
                                replace {
                                    it.log()
                                    if (BuildConfig.DEBUG) {
                                        AndroidLogger.i("clsDnTapFeedAd $mtdName")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //屏蔽开屏广告
        if (Helper.getSpBool(Constant.PURE_SPLASH_AD, false)) {
            // 跳过开屏Logo页面
            val hook1 = findFirstMethodByName(MainHook.clsActivityLifecycle, "toForeGround")?.createHook {
                replace {
                    it.log()

                    val activity = it.args[0] as Activity
                    if (activity.javaClass == MainHook.clsLoadingActivity) {
                        AndroidLogger.d("跳过启动页")
                        XposedHelpers.setBooleanField(activity, "canJump", true)
                        XposedHelpers.setBooleanField(activity, "isADShow", true)
                        XposedHelpers.callMethod(activity, "goHome")
                    }
                }
            }

            val hook3 = findFirstMethodByName(clsLoadingActivity_a, "callBack")?.createHook {
                replace {
                    it.log()
                }
            }

            if (hook1 == null || hook3 == null) {
                AndroidLogger.w("过滤开屏广告功能部分加载失败")
            }
        }

        //屏蔽广告帖子
        if (Helper.getSpBool(Constant.ENABLE_PURE_POST, false)) {

            val purePost = Helper.getSpStr(Constant.PURE_POST, null)
            val pureAuthor = Helper.getSpStr(Constant.PURE_AUTHOR, null)

            val postKeywords = purePost?.split("|")?.filter { it.isNotEmpty() } ?: listOf()
            val authorKeywords = pureAuthor?.split("|")?.filter { it.isNotEmpty() } ?: listOf()

            val fldAuthor = FieldFinder.fromClass(clsSubject).filterByName("author").first()
            val fldSubject = FieldFinder.fromClass(clsSubject).filterByName("subject").first()

            if (postKeywords.isNotEmpty() || authorKeywords.isNotEmpty()) {
                findFirstMethodByName(clsPostListFragment, "addToList")?.createHook {
                    before {
                        it.log()

                        val posts = it.args[0] as List<*>
                        val puredPosts = mutableListOf<Any?>()

                        for (post in posts) {
                            if (post == null) {
                                continue
                            }

                            val author = fldAuthor.get(post) as String
                            val subject = fldSubject.get(post) as String

                            if (Helper.getSpBool(Constant.ENABLE_POST_LOG, false)) {
                                AndroidLogger.w("$author: $subject")
                            }

                            var pure = false
                            for (key in postKeywords) {
                                if (subject.contains(key)) {
                                    pure = true
                                    break
                                }
                            }
                            for (key in authorKeywords) {
                                if (author == key) {
                                    pure = true
                                    break
                                }
                            }

                            if (!pure) {
                                puredPosts.add(post)
                            }
                        }

                        it.args[0] = puredPosts
                    }
                }
            }
        }

        // 屏蔽首页浮窗广告
        if (Helper.getSpBool(Constant.PURE_POPUP_AD, false)) {
            findFirstMethodByName(clsHomeRecommendFragment, "showActivityMenu\$lambda$10")?.createHook {
                replace {
                    it.log()

                    AndroidLogger.w("去你妈的广告")
                }
            }

            findFirstMethodByName(clsActivityEntity, "getImageIcon")?.createHook {
                after {
                    it.log()

                    it.result = ""
                }
            }
        }

        // 屏蔽游戏推荐
        if (Helper.getSpBool(
                Constant.PURE_GAME_RECOMMEND, false
            ) && fldBannerHolderViewC != null && fldForumFoldBinding != null
        ) {
            findFirstMethodByName(clsBannerHolder, "setupBanners")?.createHook {
                replace {
                    it.log()

                    val binding = XposedHelpers.getObjectField(
                        it.thisObject, "binding"
                    ) //fldBannerHolderBinding.get(it.thisObject)
                    if (binding != null) {
                        val view = fldBannerHolderViewC!!.get(binding) as View
                        view.visibility = View.GONE
                    } else {
                        AndroidLogger.e("binding is null")
                    }
                }
            }

            findFirstMethodByName(clsGameRecommendBinder, "getItemView")?.createHook {
                after {
                    it.log()

                    val binding = fldForumFoldBinding!!.get(it.thisObject)
                    if (binding != null) {
                        for (fid in fldGameRecommendBindings) {
                            AndroidLogger.w("$fid")
                            val value = fid.get(binding)
                            if (value is View) {
                                value.visibility = View.GONE
                            }
                        }
                    } else {
                        AndroidLogger.e("binding is null")
                    }
                }
            }
        }

        if (Helper.getSpBool(Constant.PURE_VIDEO, false)) {
            MethodFinder.fromClass(clsHomeFragment).filterByName("updateTabs")
                .firstOrNull()?.createHook {
                    before {
                        it.log()

                        val list = it.args[0] as ArrayList<*>
                        for (i in list.size - 1 downTo 0) {
                            val ele = list[i] ?: continue
                            val name = XposedHelpers.getObjectField(ele, "name") as? String ?: continue
                            if (name == "短剧") {
                                list.removeAt(i)
                            }
                        }
                    }
                }
        }


    }

    override var name = "AdHook"
}
