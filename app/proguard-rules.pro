# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

## 保留Xposed入口
#-keep class com.chrxw.purenga.XposedInit {*;}
#
## 保留R下面的资源
#-keep class **.R$* {*;}
#
## 抛出异常时保留代码行号
#-keepattributes SourceFile,LineNumberTable
#
## 基本混淆
#-keep class * extends android.app.Activity
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
## 因为这些子类都有可能被外部调用
#-keep public class * extends android.app.Activity
#-keep public class * extends android.preference.Preference
#-keep public class * extends android.view.View
## 保留在Activity中的方法参数是view的方法，
## 这样以来我们在layout中写的onClick就不会被影响
#-keepclassmembers class * extends android.app.Activity{
#    public void *(android.view.View);
#}
#
## 保留枚举类不被混淆
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
## 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
#-keepclassmembers class * {
#    void *(**On*Event);
#    void *(**On*Listener);
#}
