plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.irosinfo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.irosinfo"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
    }

}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar","*.aar"))))

    implementation(project(path = ":common"))
    implementation(project(path = ":common:bases"))
    implementation(project(path = ":common:camera"))
    implementation(project(path = ":common:connectivity"))
    implementation(project(path = ":common:extensions"))
    implementation(project(path = ":common:utils"))

    api("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-android-compiler:2.52")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    api("androidx.hilt:hilt-navigation-fragment:1.2.0")
}