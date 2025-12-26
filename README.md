# Quest Hello World 🥽

A **Hello World** prototype for the **Meta Quest 3** VR headset using:

- ☕ **Java 21** - Modern Java with latest features
- 🎮 **jMonkeyEngine 3.6** - Open-source 3D game engine with VR support
- 🏗️ **Gradle 8.5** - Build automation with Kotlin DSL
- 💉 **Dagger 2.48** - Compile-time dependency injection

## 📋 Prerequisites

### Required Software

1. **Java 21 JDK**
   ```bash
   # macOS (Homebrew)
   brew install openjdk@21
   
   # Verify installation
   java -version
   ```

2. **Android SDK** (API 34)
   - Install via [Android Studio](https://developer.android.com/studio) or command line
   - Required SDK components:
     - Android SDK Platform 34
     - Android SDK Build-Tools 34.0.0
     - Android NDK (for native libraries)

3. **Meta Quest Developer Account**
   - Sign up at [Meta Quest Developer Hub](https://developer.oculus.com/)
   - Enable Developer Mode on your Quest 3

### Environment Setup

Set the following environment variables:

```bash
# ~/.zshrc or ~/.bashrc
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export ANDROID_HOME=$HOME/Library/Android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
export PATH=$PATH:$ANDROID_HOME/tools
```

## 🚀 Building the Project

### 1. Clone and Build

```bash
# Navigate to project
cd /Users/mark.orr/IdeaProjects/quest

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

### 2. Generate Gradle Wrapper (if needed)

```bash
gradle wrapper --gradle-version 8.5
```

## 📱 Deploying to Meta Quest 3

### Enable Developer Mode

1. Open the **Meta Quest mobile app**
2. Go to **Menu → Devices → Your Quest 3**
3. Select **Developer Mode** and enable it

### Connect Quest 3 via ADB

```bash
# Connect via USB-C cable
adb devices

# You should see your Quest listed
# Accept the "Allow USB debugging" prompt in your headset
```

### Install the APK

```bash
# Install debug build
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Or use Gradle
./gradlew installDebug
```

### Wireless ADB (Optional)

```bash
# Connect USB first, then enable wireless
adb tcpip 5555
adb connect <quest-ip-address>:5555

# Disconnect USB cable and deploy wirelessly
./gradlew installDebug
```

## 🎯 Project Structure

```
quest/
├── app/
│   ├── build.gradle.kts          # App module build config
│   ├── proguard-rules.pro        # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml   # Android manifest with VR intents
│       ├── java/com/quest/helloworld/
│       │   ├── QuestHelloWorldApp.java   # Application class
│       │   ├── QuestVRApplication.java   # jME3 VR application
│       │   ├── MainActivity.java         # Android entry point
│       │   ├── di/
│       │   │   ├── AppComponent.java     # Dagger component
│       │   │   └── AppModule.java        # Dagger module
│       │   └── vr/
│       │       ├── HelloWorldScene.java  # 3D VR scene
│       │       └── VRSceneConfig.java    # Scene configuration
│       └── res/
│           ├── values/           # Colors, strings, themes
│           └── drawable/         # App icon
├── build.gradle.kts              # Root build config
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle settings
└── README.md
```

## 🎨 What You'll See

When you run the app on your Quest 3, you'll experience:

- **"Hello Quest 3!"** - Glowing cyan 3D text floating in space
- **Floating Cube** - An orange cube gently rotating and bobbing
- **Colorful Spheres** - Decorative elements in green, pink, and yellow
- **Dynamic Lighting** - Ambient and directional lights creating depth
- **Animated Effects** - Text pulses, cube rotates continuously

## 🔧 Customization

### Modify the Scene

Edit `HelloWorldScene.java` to:
- Change text content and colors
- Add more 3D objects
- Adjust animation speeds
- Modify lighting

### Update Configuration

Edit `AppModule.java` to change:
- Background color
- Text scale and distance
- Scene name

```java
@Provides
@Singleton
public VRSceneConfig provideVRSceneConfig() {
    return new VRSceneConfig.Builder()
            .setSceneName("My Custom Scene")
            .setBackgroundColor(0.1f, 0.0f, 0.1f, 1.0f)  // Purple
            .setTextScale(2.0f)
            .setTextDistance(4.0f)
            .build();
}
```

## 🐛 Troubleshooting

### Build Issues

**"Could not find com.android.tools.build:gradle"**
```bash
# Ensure you have correct repositories in settings.gradle.kts
# Run with fresh cache
./gradlew clean build --refresh-dependencies
```

**"Unsupported class file major version 65"**
```bash
# Ensure Gradle is using Java 21
./gradlew --version
# Update JAVA_HOME if needed
```

### Deployment Issues

**"No devices found"**
- Ensure Quest 3 is connected and USB debugging is enabled
- Accept the authorization prompt in your headset
- Try: `adb kill-server && adb start-server`

**"App crashes on launch"**
- Check logcat for errors: `adb logcat | grep -i "quest\|jme\|error"`
- Ensure all native libraries are included

## 📚 Resources

- [jMonkeyEngine Documentation](https://wiki.jmonkeyengine.org/)
- [jMonkeyEngine VR Guide](https://wiki.jmonkeyengine.org/docs/3.4/core/vr/vr.html)
- [Dagger Documentation](https://dagger.dev/dev-guide/)
- [Meta Quest Developer Docs](https://developer.oculus.com/documentation/)
- [Android VR Development](https://developer.android.com/ndk/guides/vr)

## 📄 License

MIT License - Feel free to use this as a starting point for your Quest 3 projects!

---

**Happy VR Development! 🎉**

