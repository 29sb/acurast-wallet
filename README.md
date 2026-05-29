# Acurast Wallet

基于 Acurast 去中心化计算网络的 Android 钱包应用。

## 功能

- 🔐 钱包创建（BIP39 助记词 + SR25519 密钥对）
- 🔄 钱包恢复（从助记词恢复）
- 💰 账户余额查询（通过 Substrate RPC）
- 📊 区块高度查询
- 🏦 多网络支持（主网/测试网）
- 📱 现代 Material Design 3 UI

## 技术栈

- **语言**: Kotlin 2.1.0
- **UI**: Jetpack Compose + Material Design 3
- **区块链**: Nova Substrate SDK 2.4.0
- **架构**: MVVM + Repository Pattern
- **构建**: Gradle 8.9 + AGP 8.7.3

## SDK 集成

使用 [Nova Substrate SDK](https://github.com/novasamatech/substrate-sdk-android) 进行区块链交互：
- BIP39 助记词生成与恢复
- SR25519 密钥对生成
- SS58 地址编解码
- Substrate RPC 查询

## 构建

```bash
git clone https://github.com/29sb/acurast-wallet.git
cd acurast-wallet
./gradlew assembleDebug
```

APK 位置: `app/build/outputs/apk/debug/app-debug.apk`

## 账户余额解析

账户余额数据格式（Substrate AccountData）:
- free (16 bytes, little-endian)
- reserved (16 bytes, little-endian)
- miscFrozen (16 bytes, little-endian)
- feeFrozen (16 bytes, little-endian)

## TODO

- [ ] 转账功能（需要 runtime metadata 集成）
- [ ] 交易历史查询
- [ ] 多链支持
- [ ] 钱包备份与恢复
- [ ] 生物识别解锁

## 许可证

MIT
