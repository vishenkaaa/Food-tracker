# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# =================================================================
# Стандартні правила Android
# =================================================================

# Зберігаємо інформацію про номери рядків для налагодження (дуже корисно!)
-keepattributes SourceFile,LineNumberTable

# =================================================================
# Правила для AndroidX, ViewModel та Compose
# =================================================================

# Зберігаємо класичні ViewModel та LiveData
-keep class androidx.lifecycle.ViewModel {
    public <init>(...);
}
-keep class androidx.lifecycle.LiveData
-keep class androidx.lifecycle.MutableLiveData

# Обов'язкові правила для Fragment та DialogFragment
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.fragment.app.DialogFragment

# Додаткові правила для Navigation Components
-keepnames class * extends androidx.navigation.NavArgs

# =================================================================
# Обов'язкові правила для Dagger Hilt
# =================================================================

# Зберігаємо анотації Hilt, які потрібні для рефлексії
-keepnames @dagger.hilt.android.HiltAndroidApp class *
-keepnames @dagger.hilt.android.AndroidEntryPoint class *
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class *

# Зберігаємо конструктори впровадження
-keep public class * extends androidx.lifecycle.ViewModel {
    @dagger.assisted.AssistedInject <init>(...);
    @javax.inject.Inject <init>(...);
}

# Зберігаємо публічні конструктори, які можуть бути використані Hilt у ваших модулях
-keep public class * {
    @javax.inject.Inject <init>(...);
}

# Зберігаємо всі класи, які були згенеровані Dagger/Hilt
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class * extends dagger.hilt.internal.GeneratedEntryPoint

# =================================================================
# Правила для Firebase Crashlytics
# =================================================================

# Crashlytics потрібно, щоб зберегти імена всіх класів, які ви успадковуєте від Activity, Service та ін.
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# =================================================================
# Правила для kotlinx.serialization (якщо використовується рефлексія)
# =================================================================

# Зберігаємо класи, які використовуються для JSON-серіалізації
-keepnames class * implements kotlinx.serialization.KSerializer {
    public <fields>;
}