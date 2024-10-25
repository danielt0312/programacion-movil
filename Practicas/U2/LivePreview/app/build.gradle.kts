plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.livepreview"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.livepreview"
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

//    implementation("androidx.core:core-ktx:1.9.0")
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.10.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Object detection feature with custom classifier support
    implementation ("com.google.mlkit:object-detection-custom:17.0.0")
    implementation ("com.google.mlkit:object-detection:17.0.0")

    // Face features
    implementation ("com.google.mlkit:face-detection:16.1.5")

    // Pose detection with default models
    implementation ("com.google.mlkit:pose-detection:18.0.0-beta3")
    // Pose detection with accurate models
    implementation ("com.google.mlkit:pose-detection-accurate:18.0.0-beta3")

    // Face Mesh Detection
    implementation ("com.google.mlkit:face-mesh-detection:16.0.0-beta1")

    // CameraX
    // SE cambiaron las versiones del Github de MLToolKit por las mas actuales
    implementation ("androidx.camera:camera-camera2:1.3.0-alpha04")
    implementation ("androidx.camera:camera-lifecycle:1.3.0-alpha04")
    implementation ("androidx.camera:camera-view:1.3.0-alpha04")

    implementation ("com.google.guava:guava:27.1-android")
}