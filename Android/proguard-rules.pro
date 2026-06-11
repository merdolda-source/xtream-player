# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
-verbose

# Optimization is turned off by default. Dontoptimize changes the
# setting to disabled.
-dontoptimize

# Note that if you want to enable optimization, you cannot just
# include optimization flags here and expect it to work.
# Instead, you will need to explicitly invoke the optimizer.
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*

# Keep a fixed source file attribute and all line number tables to get line
# numbers in stack traces.
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# Keep all a* classes.
-keep class a.** { *; }

# Repackage classes into the 'com.example' package.
-repackageclasses 'com/xtream/player'

# Keep enums from being obfuscated
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep Retrofit annotations
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

# Keep Retrofit classes
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Gson
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.JsonDeserializer
-keep class * implements com.google.gson.JsonSerializer

# Keep OkHttp
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# Keep Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Keep Firebase
-keep class com.google.firebase.** { *; }
-keepattributes *Annotation*

# Keep ExoPlayer
-keep class com.google.android.exoplayer2.** { *; }
-keep interface com.google.android.exoplayer2.** { *; }

# Keep Media3
-keep class androidx.media3.** { *; }
-keep interface androidx.media3.** { *; }

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Keep Kotlin classes
-keep class kotlin.** { *; }
-keep interface kotlin.** { *; }

# Suppress warnings for libraries
-dontwarn **
