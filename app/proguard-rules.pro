# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/IroniaMac/Library/Android/sdk/tools/proguard/proguard-android.txt
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
-keep class com.sreg.japnq.kwrgon.op.SJK{*;}
-dontwarn com.sreg.japnq.kwrgon.op.SJK

-keep interface com.sreg.japnq.kwrgon.op.EdgeViewCallback{ *; }
-dontwarn com.sreg.japnq.kwrgon.op.EdgeViewCallback

-keep class com.sreg.japnq.kwrgon.nyat.**{*;}
-dontwarn com.sreg.japnq.kwrgon.nyat.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
