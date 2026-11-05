
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.classplanner"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.classplanner"
        minSdk = 26
        targetSdk = 35

        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    implementation("androidx.room:room-runtime:2.7.2")
    annotationProcessor("androidx.room:room-compiler:2.7.2")

    implementation("androidx.core:core:1.16.0")
}
