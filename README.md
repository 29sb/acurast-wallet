# Acurast Wallet

基于 Acurast 去中心化计算网络的 Android 钱包应用。

## 功能

- 🔐 钱包创建（BIP39 助记词）
- 💰 余额查询
- 📤 转账
- 📋 交易历史
- 🌐 网络切换（Mainnet / Canary）

## 技术栈

- Kotlin 2.1.0
- Jetpack Compose + Material Design 3
- Navigation Compose
- OkHttp + Gson

## 编译

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-arm64
export ANDROID_HOME=/opt/android-sdk
./gradlew assembleDebug
```

## 许可证

MIT
