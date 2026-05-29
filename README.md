# Acurast Wallet

基于 Acurast 去中心化计算网络的 Android 钱包应用。

## 功能

- ✅ 钱包创建与恢复
- ✅ SR25519 密钥对生成
- ✅ BIP39 助记词支持
- ✅ SS58 地址生成
- ✅ 多网络支持（主网/测试网）
- ✅ 实时余额查询
- ✅ 转账功能
- ✅ 交易历史
- ✅ 钱包管理

## 技术栈

- **语言**: Kotlin 2.1.0
- **UI**: Jetpack Compose + Material Design 3
- **密码学**: Nova Substrate SDK 2.4.0
- **架构**: MVVM + Repository Pattern
- **存储**: SharedPreferences + EncryptedSharedPreferences

## SDK 集成

### Nova Substrate SDK

- **依赖**: `io.github.nova-wallet:substrate-sdk-android:2.4.0`
- **功能**:
  - BIP39 助记词生成
  - SR25519 密钥对生成
  - SS58 地址编码/解码
  - 基础密码学操作

### 种子长度修复

- SubstrateSeedFactory.createSeed() 返回 64 字节种子
- SR25519 密钥生成需要 32 字节种子
- 截取种子前 32 字节：`result.seed.copyOfRange(0, 32)`

## 安装

1. 下载 APK 文件：`acurast-wallet-v5.apk`
2. 在 Android 设备上安装
3. 启动应用并创建钱包

## 使用说明

### 创建钱包

1. 启动应用
2. 点击 "创建钱包"
3. 设置钱包名称和密码
4. 备份助记词（重要！）
5. 完成创建

### 恢复钱包

1. 启动应用
2. 点击 "恢复钱包"
3. 输入助记词
4. 设置新密码
5. 完成恢复

### 转账

1. 进入转账页面
2. 输入收款地址
3. 输入转账金额
4. 确认并发送

## 开发说明

### 环境要求

- Android Studio Hedgehog+
- JDK 21
- Android SDK 34

### 构建命令

```bash
# 调试版本
./gradlew assembleDebug

# 发布版本
./gradlew assembleRelease
```

### 项目结构

```
app/src/main/java/com/acurast/wallet/
├── data/
│   ├── model/          # 数据模型
│   └── repository/     # 数据仓库
├── ui/
│   ├── navigation/     # 导航
│   └── screens/        # 界面
│       ├── create/     # 创建钱包
│       ├── home/       # 主页
│       ├── transfer/   # 转账
│       ├── history/    # 交易历史
│       └── settings/   # 设置
└── utils/              # 工具类
```

## 更新日志

### v1.0.0 (2024-05-30)

- 初始版本发布
- 钱包创建与恢复功能
- Nova Substrate SDK 集成
- SR25519 密钥对生成
- 多网络支持
- 转账与交易历史

## 许可证

MIT License

## 联系方式

- GitHub: [github.com/29sb/acurast-wallet](https://github.com/29sb/acurast-wallet)
