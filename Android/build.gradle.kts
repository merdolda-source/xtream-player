plugins {
    // AGP 8.1.0's JdkImageTransform (which jlinks core-for-system-modules.jar
    // into a synthetic JDK image for javac) fails under JDK 21 - it cannot
    // synthesize a module-info.class for the transform, so
    // compileDebugJavaWithJavac dies resolving the androidJdkImage
    // configuration. This is a known AGP bug fixed in 8.2.1+; bumping here
    // is a build-tooling/JDK-compat fix, not an app source-code change.
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    kotlin("android") version "1.9.0" apply false
    kotlin("jvm") version "1.9.0" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}
