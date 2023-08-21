package com.chrxw.purenga.hook

import android.app.Application
import android.app.Instrumentation
import android.widget.Toast
import com.chrxw.purenga.utils.Helper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * 设置页面钩子
 */
class PreferencesHook : IHook {

    companion object{
        var SettingsFragment: Class<*>? = null
        var DebugFragment: Class<*>? = null
        var Preference: Class<*>? = null
        var SwitchPreference: Class<*>? = null
        var OnPreferenceChangeListener: Class<*>? = null
        var OnPreferenceClickListener: Class<*>? = null
        var PreferenceFragmentCompat: Class<*>? = null
        var PreferenceManager: Class<*>? = null
        var PreferenceInflater: Class<*>? = null
        var PageInfoType: Class<*>? = null
        var ZHIntent: Class<*>? = null
        var MainActivity: Class<*>? = null
        var BasePreferenceFragment: Class<*>? = null
        var PreferenceGroup: Class<*>? = null
        var EditTextPreference: Class<*>? = null
        var SeekBarPreference: Class<*>? = null
        var OnSeekBarChangeListener: Class<*>? = null
        var ListPreference: Class<*>? = null
        var TooltipCompat: Class<*>? = null

        var findPreference: Method? = null
        var setSummary: Method? = null
        var setIcon: Method? = null
        var setVisible: Method? = null
        var getKey: Method? = null
        var setChecked: Method? = null
        var setOnPreferenceChangeListener: Method? = null
        var setOnPreferenceClickListener: Method? = null
        var setSharedPreferencesName: Method? = null
        var getContext: Method? = null
        var getText: Method? = null
        var addPreferencesFromResource: Method? = null
        var inflate: Method? = null
        var setTooltipText: Method? = null

        var SeekBarPreference_mMin: Field? = null
        var SeekBarPreference_mSeekBarValueTextView: Field? = null
        var OnSeekBarChangeListener_seekBarPreferenceInstance: Field? = null
        var ListPreference_mEntries: Field? = null
        var ListPreference_mEntryValues: Field? = null
    }

    override fun hookName(): String {
        return "设置页面"
    }

    override fun init(classLoader: ClassLoader) {
//       SettingsFragment =
//            classLoader.loadClass("com.zhihu.android.app.ui.fragment.preference.SettingsFragment")
//       DebugFragment =
//            classLoader.loadClass("com.zhihu.android.app.ui.fragment.DebugFragment")
       Preference =
            classLoader.loadClass("androidx.preference.Preference")
//       SwitchPreference =
//            classLoader.loadClass("com.zhihu.android.app.ui.widget.SwitchPreference")
       OnPreferenceChangeListener =
            classLoader.loadClass("androidx.preference.Preference\$c")
       OnPreferenceClickListener =
            classLoader.loadClass("androidx.preference.Preference\$d")
       PreferenceFragmentCompat =
            classLoader.loadClass("androidx.preference.g")
       PreferenceManager =
            classLoader.loadClass("androidx.preference.j")
       PreferenceInflater =
            classLoader.loadClass("androidx.preference.i")
//       PageInfoType =
//            classLoader.loadClass("com.zhihu.android.data.analytics.PageInfoType")
//       ZHIntent =
//            classLoader.loadClass("com.zhihu.android.answer.entrance.AnswerPagerEntance").getMethod(
//                "buildIntent",
//                Long::class.javaPrimitiveType
//            ).returnType
//       MainActivity =
//            classLoader.loadClass("com.zhihu.android.app.ui.activity.MainActivity")
//       BasePreferenceFragment =
//            classLoader.loadClass("com.zhihu.android.app.ui.fragment.BasePreferenceFragment")
       PreferenceGroup =
            classLoader.loadClass("androidx.preference.PreferenceGroup")
       EditTextPreference =
            classLoader.loadClass("androidx.preference.EditTextPreference")
       SeekBarPreference =
            classLoader.loadClass("androidx.preference.SeekBarPreference")
       OnSeekBarChangeListener =
            classLoader.loadClass("androidx.preference.SeekBarPreference$1")
       ListPreference =
            classLoader.loadClass("androidx.preference.ListPreference")
       TooltipCompat =
            classLoader.loadClass("androidx.appcompat.widget.TooltipCompat")
        
    }

    override fun hook() {

    }

}