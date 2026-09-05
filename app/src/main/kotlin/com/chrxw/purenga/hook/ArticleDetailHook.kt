package com.chrxw.purenga.hook

import android.webkit.WebView
import com.chrxw.purenga.Constant
import com.chrxw.purenga.hook.base.IHook
import com.chrxw.purenga.utils.ExtensionUtils.findFirstMethodByName
import com.chrxw.purenga.utils.ExtensionUtils.forceLog
import com.chrxw.purenga.utils.ExtensionUtils.log
import com.chrxw.purenga.utils.ExtensionUtils.printObject
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

        lateinit var clsPost: Class<*>
        lateinit var fidPostAuthorBean: Field

        lateinit var clsUserInfoBean: Class<*>
        lateinit var fidUserInfoUid: Field
        lateinit var fidUserInfoUserName: Field

        lateinit var clsWebAppInterface: Class<*>


    }

    override fun init(classLoader: ClassLoader) {
        clsArticleDetailActivity =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailActivity")

        clsArticleDetailFragment =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailFragment")
        fidWebView = FieldFinder.fromClass(clsArticleDetailFragment).filterByName("mWebView").first()

        clsPost = classLoader.loadClass("gov.pianzong.androidnga.model.Post")
        fidPostAuthorBean = FieldFinder.fromClass(clsPost).filterByName("authorBean").first()

        clsUserInfoBean = classLoader.loadClass("gov.pianzong.androidnga.model.UserInfoDataBean")
        fidUserInfoUid = FieldFinder.fromClass(clsUserInfoBean).filterByName("mUID").first()
        fidUserInfoUserName = FieldFinder.fromClass(clsUserInfoBean).filterByName("mUserName").first()

        clsWebAppInterface =
            classLoader.loadClass("gov.pianzong.androidnga.activity.forumdetail.ArticleDetailFragment\$WebAppInterface")
    }

    override fun hook() {
        // 楼主高亮
        if (Helper.getSpBool(Constant.HIGHLIGHT_AUTHOR, false)) {
            findFirstMethodByName(clsArticleDetailFragment, "finishLoad")?.createHook {
                before {
                    it.log()

                    val postList = it.args[0] as MutableList<*>

                    if (postList.isEmpty()) {
                        return@before
                    }

                    val mainPost = postList.first()

                    val postAuthor = fidPostAuthorBean.get(mainPost)
                    val postAuthorId = fidUserInfoUid.get(postAuthor) as String
                    val postAuthorName = fidUserInfoUserName.get(postAuthor) as String

                    AndroidLogger.i("主楼: $postAuthorName #$postAuthorId")

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
            }
        }

//        findFirstMethodByName(clsArticleDetailFragment, "fillWebViewData")?.createHook {
//            after {
//                it.forceLog()
//
//                val webView = fidWebView.get(it.thisObject) as WebView
//
//                val js = "setTimeout(()=>{window.ngaObj.doAction(999,[document.body.innerHTML]);},2000);"
//
//                webView.evaluateJavascript(js) { value ->
//                    AndroidLogger.w("aaa")
//                }
//            }
//        }
//
//        findFirstMethodByName(clsWebAppInterface, "doAction")?.createHook {
//            before {
//                it.forceLog()
//
//                val code = it.args[0] as Int
//
//                if (code == 999) {
//                    it.setResult(null)
//
//                    val list = it.args[1] as Array<*>
//
//                    if (list.isNotEmpty()) {
//
//                        val text = list.first() as String
//
//                        // 1. 获取 ClipboardManager 实例
//                        val clipboard: ClipboardManager? =
//                            EzXHelper.appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
//
//                        // 2. 创建 ClipData 对象，第一个参数是标签（可为 null），第二个是纯文本内容
//                        val clip = ClipData.newPlainText("label", text)
//
//
//                        // 3. 将数据设置到系统剪贴板
//                        if (clipboard != null) {
//                            clipboard.setPrimaryClip(clip)
//                            // 可选：提示用户复制成功
//                            Helper.toast("已复制到剪贴板", Toast.LENGTH_SHORT)
//                        }
//                    }
//
//                }
//            }
//        }

        findFirstMethodByName(WebView::class.java, "loadDataWithBaseURL")?.createHook {
            before {
                it.forceLog()

                val webview = it.thisObject as WebView

                val root = webview.rootView

                if (root::class.simpleName == "ArticleDetailActivity") {
                    AndroidLogger.e("11")
                    root.printObject()
                }

                root::class.printObject()
            }
        }
    }

//    private fun isArticlePage(wv: WebView): Boolean {
//        var ctx: Context? = wv.context
//        var depth = 0
//        while (ctx != null && depth++ < 8) {
//            val name = ctx.javaClass.name
//            if (name == TARGET_ACTIVITY) return true
//            if (name.startsWith("android.app.")) return false
//            ctx = if (ctx is ContextWrapper) ctx.baseContext else null
//        }
//        return false
//    }

    private fun buildInject(): String {
        val prefix = jsEscape("回复: ")   // 想改前缀只改这里
        return """
              <script>
              (function(){
                var P = '$prefix';
                function run(){
                  var boxes = document.querySelectorAll('.columnItem .headBlock .innerBox');
                  for (var i = 0; i < boxes.length; i++) {
                    var floor = boxes[i].querySelector('.floor');
                    if (floor && /楼主/.test(floor.textContent)) continue; // 主楼不加
                    var a = boxes[i].querySelector('.nameLine a.uname');
                    if (!a) continue;
                    var first = a.firstChild;
                    if (first && first.nodeType === 3) {
                      if (first.nodeValue.indexOf(P) === 0) continue; // 防重复
                      first.nodeValue = P + first.nodeValue;
                    } else {
                      a.insertBefore(document.createTextNode(P), a.firstChild);
                    }
                  }
                }
                run();
                document.addEventListener('DOMContentLoaded', run);
              })();
              </script>
          """.trimIndent()
    }

    private fun jsEscape(s: String) =
        s.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n")

    override var name = "ArticleDetailHook"
}
