# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools-adtre/proguard.html

# Add any project specific keep rules here:

# Keep members used by Kotlin reflection
-keepclassmembers class ** {
    @org.jetbrains.annotations.Nullable <fields>;
    @org.jetbrains.annotations.NotNull <fields>;
}
