# WebView JavaScript bridge - must not be obfuscated
-keepattributes JavascriptInterface
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Billing client uses reflection internally
-keep class com.android.billingclient.** { *; }

# Kotlin coroutines service-loader entries
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
