# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep rules here:

# Keep members used by Kotlin reflection
-keepclassmembers class ** {
    @org.jetbrains.annotations.Nullable <fields>;
    @org.jetbrains.annotations.NotNull <fields>;
}

# ===== PERFORMANCE OPTIMIZATION RULES =====

# Keep all data classes and DTOs (required for serialization)
-keep class com.example.groceryapp.domain.model.** { *; }
-keep class com.example.groceryapp.data.dto.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor Client
-keep class io.ktor.** { *; }
-keep class kotlin.reflect.** { *; }
-dontwarn io.ktor.**

# Coil Image Loading
-keep class coil.** { *; }
-dontwarn coil.**

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}

# Keep Repositories
-keep class com.example.groceryapp.data.repository.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# MongoDB Driver (if used in future)
-dontwarn org.bson.**
-dontwarn com.mongodb.**

# Optimization: Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
