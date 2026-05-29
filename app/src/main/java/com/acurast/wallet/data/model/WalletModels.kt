package com.acurast.wallet.data.model

/**
 * 钱包账户
 */
data class WalletAccount(
    val address: String,      // SS58 地址
    val name: String,         // 账户名称
    val publicKey: ByteArray, // 公钥
    val encryptedSeed: String? = null // 加密的种子（可选）
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WalletAccount
        return address == other.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}

/**
 * 账户余额
 */
data class AccountBalance(
    val address: String,      // 地址
    val free: Long,           // 可用余额
    val reserved: Long,       // 锁定余额
    val miscFrozen: Long,     // 冻结余额
    val feeFrozen: Long,      // 手续费冻结
    val total: Long = free + reserved // 总余额
)

/**
 * 交易记录
 */
data class Transaction(
    val hash: String,         // 交易哈希
    val from: String,         // 发送方
    val to: String,           // 接收方
    val amount: Long,         // 金额
    val fee: Long,            // 手续费
    val timestamp: Long,      // 时间戳
    val status: TransactionStatus = TransactionStatus.CONFIRMED
)

/**
 * 交易状态
 */
enum class TransactionStatus {
    PENDING, CONFIRMED, FAILED
}

/**
 * 网络类型
 */
enum class NetworkType(val displayName: String, val rpcUrl: String, val ss58Prefix: Int) {
    MAINNET("Mainnet", "wss://archive.mainnet.acurast.com", 42),
    CANARY("Canary", "wss://acurast-canary-rpc.gateway.pinata.cloud", 42)
}

/**
 * 钱包状态
 */
sealed class WalletState {
    object NoWallet : WalletState()
    data class HasWallet(val account: WalletAccount) : WalletState()
}