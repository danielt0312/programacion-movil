plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mltk_poseanimation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mltk_poseanimation"
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

    // Barcode model
    implementation ("com.google.mlkit:barcode-scanning:16.0.3")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-barcode-scanning:16.1.2'

    // Object detection feature with bundled default classifier
    implementation ("com.google.mlkit:object-detection:16.2.1")

    // Object detection feature with custom classifier support
    implementation ("com.google.mlkit:object-detection-custom:16.2.1")

    // Face features
    implementation ("com.google.mlkit:face-detection:16.0.2")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-face-detection:16.1.1'

    // Text features
    implementation ("com.google.android.gms:play-services-mlkit-text-recognition:16.1.1")

    // Image labeling with automl model support
    implementation ("com.google.mlkit:image-labeling-automl:16.2.1")

    // Image labeling
    implementation ("com.google.mlkit:image-labeling:17.0.0")
    // Or comment the dependency above and uncomment the dependency below to
    // use unbundled model that depends on Google Play Services
    // implementation 'com.google.android.gms:play-services-mlkit-image-labeling:16.0.0'

    // Image labeling custom
    implementation ("com.google.mlkit:image-labeling-custom:16.2.1")

    // Pose detection with default models
    implementation ("com.google.mlkit:pose-detection:17.0.0")
    // Pose detection with accurate models
    implementation ("com.google.mlkit:pose-detection-accurate:17.0.0")

    // -------------------------------------------------------

    implementation ("com.google.code.gson:gson:2.8.5")
    //implementation ("com.google.guava:guava:17.0")

    // CameraX
    implementation ("androidx.camera:camera-camera2:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-lifecycle:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-view:1.0.0-SNAPSHOT")
    implementation ("androidx.camera:camera-core:1.5.0-alpha01")

    implementation ("com.google.guava:guava:27.1-android")

    //implementation ("gun0912.ted:tedpermission:2.2.2")
}