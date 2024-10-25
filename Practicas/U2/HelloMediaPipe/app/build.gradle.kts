plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.hellomediapipe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hellomediapipe"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // MediaPipe Hands Solution.
    implementation ("com.google.mediapipe:solution-core:latest.release")
    implementation ("com.google.mediapipe:hands:latest.release")

    implementation ("androidx.exifinterface:exifinterface:1.3.2")
}