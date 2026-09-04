package com.chrxw.purenga.hook

import android.graphics.Color
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.Helper
import com.github.kyuubiran.ezxhelper.AndroidLogger
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.FieldFinder
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field


/**
 * 开屏广告钩子
 */
class ArticleViewHook : IHook {

    companion object {
        private lateinit var clsArticleDetailFragment: Class<*>

        private lateinit var clsArticleDetailActivity: Class<*>

        private lateinit var clsPost: Class<*>
        private lateinit var fidPostAuthor: Field
        private lateinit var fidPostIsAdminPost: Field
        private lateinit var fidPostIsAdminColor: Field

        private lateinit var clsUserInfoDataBean: Class<*>
        private lateinit var fidUserDataUid: Field
        private lateinit var fidUserDataUserName: Field
    }


    override fun init(classLoader: ClassLoader) {
        clsArticleDetailFragment =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailFragment")

        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")

        clsPost = classLoader.loadClass("gov.pianzong.androidnga.model.Post")
        fidPostAuthor = FieldFinder.fromClass(clsPost).filterByName("authorBean").first()
        fidPostIsAdminPost = FieldFinder.fromClass(clsPost).filterByName("isAdminPost").first()
        fidPostIsAdminColor = FieldFinder.fromClass(clsPost).filterByName("isAdminColor").first()

        clsUserInfoDataBean = classLoader.loadClass("gov.pianzong.androidnga.model.UserInfoDataBean")
        fidUserDataUid = FieldFinder.fromClass(clsUserInfoDataBean).filterByName("mUID").first()
        fidUserDataUserName = FieldFinder.fromClass(clsUserInfoDataBean).filterByName("mUserName").first()
    }

    override fun hook() {
        // 高亮楼主
        if (Helper.getSpBool(Constant.HIGHLIGHT_AUTHOR, true)) {
            findFirstMethodByName(clsArticleDetailFragment, "finishLoad")?.createHook {
                before {
                    it.log()

                    val postList = it.args[0] as MutableList<*>

                    if (postList.isEmpty()) {
                        return@before
                    }

                    val mainPost = postList.first()
                    val postAuthorUser = fidPostAuthor.get(mainPost)

//                    mainPost?.printObject()
//                    postAuthorUser?.printObject()

                    val postAuthorUid = fidUserDataUid.get(postAuthorUser) as String
                    val postAuthorUserName = fidUserDataUserName.get(postAuthorUser) as String

                    AndroidLogger.d("主楼 $postAuthorUserName #$postAuthorUid")

                    for (post in postList) {
                        val authorUser = fidPostAuthor.get(post)
                        val authorUid = fidUserDataUid.get(authorUser) as String
                        val authorUserName = fidUserDataUserName.get(authorUser) as String

                        if (authorUid == postAuthorUid) {
                            fidPostIsAdminPost.setInt(post, 1)
                            fidPostIsAdminColor.setInt(post, Color.RED)
                            AndroidLogger.w("楼层 $authorUserName #$authorUid")
                        } else {
                            AndroidLogger.i("楼层 $authorUserName #$authorUid")
                        }
                    }

                }

                after {
                    it.log()

                    try {
                        val frag = it.thisObject

                        val subject = XposedHelpers.getObjectField(frag, "responseSubject") as? String ?: ""
                        val author = XposedHelpers.getObjectField(frag, "mThreadAuthor") as? String ?: ""
                        val authorId = XposedHelpers.getObjectField(frag, "mThreadAuthorId") as? String ?: ""
                        val tid = XposedHelpers.getObjectField(frag, "mTid") as? String ?: ""
                        val page = XposedHelpers.getIntField(frag, "mPage")

                        AndroidLogger.e("标题: $subject | 作者: $author (uid=$authorId) | tid=$tid page=$page")

                    } catch (t: Throwable) {
                        AndroidLogger.w("hookTitleAndAuthor error: $t")
                    }
                }
            }
        }

    }

    override var name = "SplashHook"
}
