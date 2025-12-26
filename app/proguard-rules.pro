# jMonkeyEngine ProGuard Rules
-keep class com.jme3.** { *; }
-keep class jme3tools.** { *; }
-dontwarn com.jme3.**

# Dagger ProGuard Rules
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.internal.Binding
-keep class * extends dagger.internal.ModuleAdapter
-keep class * extends dagger.internal.StaticInjection
-dontwarn dagger.**

# Quest/VR specific
-keep class com.oculus.** { *; }
-dontwarn com.oculus.**

