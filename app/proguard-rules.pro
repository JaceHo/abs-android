# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

# Optimizations: If you don't want to optimize, use the
# proguard-android.txt configuration file instead of this one, which
# turns off the optimization flags.  Adding optimization introduces
# certain risks, since for example not all optimizations performed by
# ProGuard works on all versions of Dalvik.  The following flags turn
# off various optimizations known to have issues, but the list may not
# be complete or up to date. (The "arithmetic" optimization can be
# used if you are only targeting Android 2.0 or later.)  Make sure you
# test thoroughly if you go this route.
-optimizations !code/simplification/cast,!code/allocation/*,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# The remainder of this file is identical to the non-optimized version
# of the Proguard configuration file (except that the other file has
# flags to turn off optimization).
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}
# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

#From the collective wisdom of Stack Overflow, I always begin this file with the following entries:

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.billing.IInAppBillingService
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.content.Context {
    public void *(android.view.View);
    public void *(android.view.MenuItem);
}
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/androidSDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.

#finally it's not necessary to obfuscate other code except ours
-keep class !info.futureme.abs.**,!com.github.lzyzsd.jsbridge.** { *; }

#If the documentation mentions nothing about ProGuard, but the library is open source, then
#there is no point to obfuscating its code from a security standpoint anyway.
-libraryjars libs

# The official support library.
-keep class android.** {*;}
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**


#retrofit
# Retrofit 2.X
## https://square.github.io/retrofit/ ##
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-keep class com.squareup.okhttp3.internal.huc.** { *; }

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**


#butter knife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# RxJava
-dontwarn rx.**
-keep class rx.** {*;}

#keep json&greendao reflaction
-keep class info.futureme.abs.example.entity.** {*;}
-keep class info.futureme.abs.entity.** {*;}


#GETUI 如果开发者需要使用proguard进行混淆打包，请在proguard.cfg添加如下代码：
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}

# umeng analytics
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep public class info.futureme.abs.example.R$*{
public static final int *;
}

-dontwarn com.umeng.**
-keep class android.support.v4.** {*;}


#baidu map
-keep class com.baidu.mapapi.** {*;}
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.**{*;}

#easemob
-dontwarn com.easemob.**
-keep class com.easemob.* {*;}
-keep class com.easemob.chat.** {*;}

-keep class org.xmlpull.** {*;}
-keep public class * extends com.umeng.**
-keep class com.umeng.** { *; }
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}

#keep js interfaces
-keep class com.github.lzyzsd.jsbridge.HtmlContentInterceptor {*;}

#keep webview
-keep class * extends android.webkit.WebView
-keep class com.tencent.smtt.sdk.WebView.**{*;}
-keep class com.tencent.** {*;}
-dontwarn com.tencent.**

#keep 3rd lib
-keep class com.qq.** {*;}
-keep class com.google.** {*;}
-keep class com.umeng.** {*;}

#glide
-keep class info.futureme.abs.example.util.MVSGlideModule
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Suppress warnings if you are NOT using IAP:
-dontwarn com.urbanairship.iap.**

# Required if you are using Autopilot
-keep public class * extends com.urbanairship.Autopilot

# Required if you ARE using IAP:
-keep class com.android.vending.billing.**

# Required if you are using the airshipconfig.properties file
-keepclasseswithmembers public class * extends com.urbanairship.Options {
    public *;
}
