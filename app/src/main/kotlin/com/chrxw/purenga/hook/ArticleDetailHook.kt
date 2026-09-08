package com.chrxw.purenga.hook

import android.webkit.WebView
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.forceLog
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.FieldFinder
import java.lang.reflect.Field


/**
 * 开屏广告钩子
 */
class ArticleDetailHook : IHook {

    companion object {
        lateinit var clsArticleDetailActivity: Class<*>

        lateinit var clsArticleDetailFragment: Class<*>
        lateinit var fidWebView: Field

        var clsArticleDetailFragmentQ: Class<*>? = null

        lateinit var clsBaseFragment: Class<*>
        lateinit var fidThreadAuthor: Field
        lateinit var fidThreadAuthorId: Field

        lateinit var clsPost: Class<*>
        lateinit var fidPostAuthorBean: Field

        lateinit var clsUserInfoBean: Class<*>
        lateinit var fidUserInfoUid: Field
        lateinit var fidUserInfoUserName: Field
    }

    override fun init(classLoader: ClassLoader) {
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")

        clsArticleDetailFragment =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailFragment")
        fidWebView =
            FieldFinder.fromClass(clsArticleDetailFragment).filterByName("mWebView").first()

        try {
            clsArticleDetailFragmentQ =
                classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailFragment\$q")
        } catch (e: Throwable) {
            AndroidLogger.e(e)
        }

        clsBaseFragment = classLoader.loadClass("gov.pianzong.androidnga.activity.BaseFragment")
        fidThreadAuthor =
            FieldFinder.fromClass(clsBaseFragment).filterByName("mThreadAuthor").first()
        fidThreadAuthorId =
            FieldFinder.fromClass(clsBaseFragment).filterByName("mThreadAuthorId").first()

        clsPost = classLoader.loadClass("gov.pianzong.androidnga.model.Post")
        fidPostAuthorBean = FieldFinder.fromClass(clsPost).filterByName("authorBean").first()

        clsUserInfoBean = classLoader.loadClass("gov.pianzong.androidnga.model.UserInfoDataBean")
        fidUserInfoUid = FieldFinder.fromClass(clsUserInfoBean).filterByName("mUID").first()
        fidUserInfoUserName =
            FieldFinder.fromClass(clsUserInfoBean).filterByName("mUserName").first()
    }

    override fun hook() {
        // 楼主高亮
        if (Helper.getSpBool(Constant.ENABLE_HIGHLIGHT_AUTHOR, false)) {
            var webView: WebView? = null
            var authorName: String? = null

            findFirstMethodByName(clsArticleDetailFragment, "finishLoad")?.createHook {
                before {
                    it.log()

                    val postList = it.args[0] as MutableList<*>

                    if (postList.isEmpty()) {
                        return@before
                    }

                    val postAuthorId = fidThreadAuthorId.get(it.thisObject) as String

                    for (post in postList) {
                        val author = fidPostAuthorBean.get(post)
                        val authorId = fidUserInfoUid.get(author) as String
                        val authorName = fidUserInfoUserName.get(author) as String

                        if (postAuthorId == authorId) {
                            AndroidLogger.w("楼主: $authorName #$authorId")
                        } else {
                            AndroidLogger.d("其他: $authorName #$authorId")
                        }
                    }
                }

                after {
                    it.log()

                    webView = fidWebView.get(it.thisObject) as WebView
                    authorName = fidThreadAuthor.get(it.thisObject) as String
                }
            }

            findFirstMethodByName(clsArticleDetailFragment, "onDetach")?.createHook {
                after {
                    it.log()

                    webView = null
                    authorName = null
                }
            }

            clsArticleDetailFragmentQ?.let { clazz ->
                findFirstMethodByName(clazz, "onPageFinished")?.createHook {
                    after {
                        it.forceLog()

                        val author = authorName
                        val wv = webView
                        if (wv == null || author.isNullOrEmpty()) {
                            AndroidLogger.w("webView is null")
                            return@after
                        }

                        AndroidLogger.w("高亮楼主: $author")
                        val customJs = Helper.getSpStr(Constant.CUSTOM_POST_JS, null) ?: ""
                        val js = Constant.JS_HIGHLIGHT.replace("[AUTHOR]", author)
                            .replace("[CUSTOM_HS]", customJs)

                        wv.loadUrl("javascript:$js")
                        AndroidLogger.i("loaded JS")
                        AndroidLogger.i(js)

                    }
                }
            } ?: AndroidLogger.w("onPageFinished hook 失败")
        }
    }

    override var name = "ArticleDetailHook"
}
