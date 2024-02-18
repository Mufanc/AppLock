# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepattributes Signature
-keep class kotlin.coroutines.Continuation

-keep class xyz.mufanc.applock.ui.base.* { *; }
-keep class ** extends xyz.mufanc.applock.ui.base.* { *; }

-keepclassmembers class ** extends androidx.viewbinding.ViewBinding {
    public static ** inflate(...);
}

-keepclassmembers class ** extends androidx.databinding.ViewDataBinding {
    public static ** inflate(...);
}

-keep class kotlin.reflect.jvm.internal.** { *; }
-keep @io.github.libxposed.api.annotations.* class ** { *; }

-keep class xyz.mufanc.applock.core.process.guard.* { *; }
-keep class xyz.mufanc.applock.core.scope.provider.* { *; }
