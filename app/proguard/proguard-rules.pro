# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk_studio/tools/proguard/proguard-android.txt
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
-keep class com.wt.pinger.proto.** { *; }
-keep class com.wt.pinger.providers.data.** { *; }
-keep class com.wt.pinger.data.api.** { *; }
-keep class com.wt.pinger.data.api.NewsUser  { *; }

-keepclassmembers class com.wt.apkinfo.viewmodel.files.FileListModel { public *; }
