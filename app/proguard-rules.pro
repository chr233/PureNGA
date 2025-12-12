# 保留Xposed入口
-keep class com.chrxw.purenga.XposedInit {*;}

# 保留R下面的资源
-keep class **.R$* {*;}

# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 基本混淆
-keep class * extends android.app.Activity
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 保留 Activity 中在 layout 中通过 onClick 调用的方法（取消注释）
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
    void *(**On*Event);
    void *(**On*Listener);
}
#这段混淆规则用于保护使用了@Keep注解的类和成员不被混淆，以及忽略特定的冗余类。
-keep class androidx.annotation.Keep
#保留使用了@Keep注解的类和接口的所有成员
-keep @androidx.annotation.Keep class * {*;}
#保留使用了@Keep注解的类的方法
-keepclasseswithmembers class * {
@androidx.annotation.Keep <methods>;
}
#保留使用了@Keep注解的类的字段
-keepclasseswithmembers class * {
@androidx.annotation.Keep <fields>;
}
#保留使用了@Keep注解的类的构造方法
-keepclasseswithmembers class * {
@androidx.annotation.Keep <init>(...);
}