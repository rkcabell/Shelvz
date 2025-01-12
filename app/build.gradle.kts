plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.shelvz"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shelvz"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }


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
        compose = true
    }
}

dependencies {
    //hilt
    implementation(libs.hilt.navigation)
    implementation(libs.hilt.android)
    implementation(libs.material)
//    implementation(libs.hilt.compiler)
    ksp(libs.hilt.compiler)

    //room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.common)
    ksp(libs.androidx.room.compiler)

    //other
    implementation(libs.bcrypt)
    implementation(libs.androidx.datastore)
    implementation(libs.gson)

    //compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.navigation.compose)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    //core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}