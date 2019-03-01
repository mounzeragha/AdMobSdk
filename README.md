Add it to your build.gradle with:

allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
and:

dependencies {
    compile 'com.github.jitpack:android-example:{latest version}'
}
