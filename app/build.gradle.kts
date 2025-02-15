plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.searchwidget"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.searchwidget"
        minSdk = 29
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // AmbilWarna color picker library
    implementation("com.github.yukuku:ambilwarna:2.0.1")

    // Remove constraintlayout if not used in other parts of the app
    // implementation(libs.constraintlayout)
}
