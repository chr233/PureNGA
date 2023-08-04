package com.chrxw.purenga.hook

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.chrxw.purenga.BuildConfig
import com.chrxw.purenga.R
import com.chrxw.purenga.utils.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


/**
 * 关于页面钩子
 */
class AboutHook(classLoader: ClassLoader) : BaseHook(classLoader) {

    override fun startHook() {
        try {
            XposedHelpers.findAndHookMethod(
                "com.donews.nga.setting.AboutUsActivity",
                mClassLoader,
                "initLayout",
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val viewBinding =
                            XposedHelpers.callMethod(param.thisObject, "getViewBinding")
                        val textView = XposedHelpers.getObjectField(viewBinding, "h") as TextView
                        val newText =
                            textView.text.toString() + " + PureNGA:" + BuildConfig.VERSION_NAME + " (点此检查插件更新)"
                        textView.text = newText
                        textView.setOnClickListener(MyButton())
                    }
                })
        } catch (e: Exception) {
            Log.e(e)
        }
    }

    private class MyButton : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/chr233/PureNGA")
            )
            v?.context?.let { startActivity(it, intent, null) }
        }
    }
}
