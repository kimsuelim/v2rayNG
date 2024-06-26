### Build dependencies

    $ mkdir build
    $ cd build
    $ git clone https://github.com/kimsuelim/AndroidLibXrayLite.git
    $ cd AndroidLibXrayLite
    $ go get github.com/xtls/xray-core@$latest
    $ gomobile init
    $ go mod tidy -v
    $ gomobile bind -v -androidapi 21 -ldflags='-s -w' ./
    $ cp *.aar ../app/libs/

### Exposes localhost to the world for easy testing

    $ npm install -g localtunnel
    $ lt --subdomain giantpanda --port 8080 // url is: https://giantpanda.loca.lt

### Background work
* [Asynchronous background processing](https://developer.android.com/develop/background-work/background-tasks/asynchronous)
* [Kotlin coroutines on Android](https://developer.android.com/kotlin/coroutines)

### Gradle
* [Configure build variants](https://developer.android.com/build/build-variants)
* [Gradle tips and recipes](https://developer.android.com/build/gradle-tips#kts)
* [Gradle: Android Build Variables Done Right](https://rafamatias.medium.com/gradle-android-build-variables-done-right-d0c0e296ee93)
* [Build multiple APKs](https://developer.android.com/build/configure-apk-splits)

### Splash Screen
* [Splash Screen in Android](https://proandroiddev.com/splash-screen-in-android-3bd9552b92a5)
* [Splash screens](https://developer.android.com/develop/ui/views/launch/splash-screen)

### State and Jetpack Compose
[State Hoisting](https://developer.android.com/develop/ui/compose/state?hl=ko#state-hoisting)
[Preview UI with composable previews](https://developer.android.com/develop/ui/compose/tooling/previews)

### Tools
* [localtunnel](https://github.com/localtunnel/localtunnel)
* [IconKitchen](https://icon.kitchen)
* [Create app icons](https://developer.android.com/studio/write/create-app-icons)
