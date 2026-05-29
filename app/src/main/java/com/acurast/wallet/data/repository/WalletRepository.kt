package com.acurast.wallet.data.repository

import com.acurast.wallet.data.model.*
import io.novasama.substrate_sdk_android.encrypt.EncryptionType
import io.novasama.substrate_sdk_android.encrypt.mnemonic.Mnemonic
import io.novasama.substrate_sdk_android.encrypt.seed.substrate.SubstrateSeedFactory
import io.novasama.substrate_sdk_android.encrypt.keypair.substrate.SubstrateKeypairFactory
import io.novasama.substrate_sdk_android.ss58.SS58Encoder
import io.novasama.substrate_sdk_android.ss58.SS58Encoder.toAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * 钱包仓库 - 使用 Nova Substrate SDK 处理链上交互
 */
class WalletRepository {
    
    private var currentNetwork = NetworkType.MAINNET
    private val ss58Prefix: Short = 42 // Acurast uses standard Substrate prefix
    
    /**
     * 切换网络
     */
    fun switchNetwork(network: NetworkType) {
        currentNetwork = network
    }
    
    /**
     * 生成新钱包 - 助记词 + 种子 + 密钥对
     */
    fun generateWallet(mnemonicLength: Mnemonic.Length = Mnemonic.Length.TWELVE): WalletCreationResult {
        val result = SubstrateSeedFactory.createSeed(mnemonicLength, null)
        
        // 检查 seed 是否有效
        if (result.seed.isEmpty()) {
            throw Exception("生成的种子无效")
        }
        
        // SR25519 需要 32 字节种子，但 SubstrateSeedFactory 返回 64 字节
        // 截取前 32 字节作为 SR25519 种子
        val seed32 = result.seed.copyOfRange(0, 32)
        
        val keypair = SubstrateKeypairFactory.generate(EncryptionType.SR25519, seed32, emptyList())
        val address = keypair.publicKey.toAddress(ss58Prefix)
        
        return WalletCreationResult(
            mnemonic = result.mnemonic.words,
            publicKey = keypair.publicKey,
            privateKey = keypair.privateKey,
            address = address
        )
    }
    
    /**
     * 从助记词恢复钱包
     */
    fun restoreWallet(mnemonicWords: String): WalletCreationResult {
        val result = SubstrateSeedFactory.deriveSeed(mnemonicWords, null)
        
        // 检查 seed 是否有效
        if (result.seed.isEmpty()) {
            throw Exception("恢复的种子无效")
        }
        
        // SR25519 需要 32 字节种子，但 SubstrateSeedFactory 返回 64 字节
        // 截取前 32 字节作为 SR25519 种子
        val seed32 = result.seed.copyOfRange(0, 32)
        
        val keypair = SubstrateKeypairFactory.generate(EncryptionType.SR25519, seed32, emptyList())
        val address = keypair.publicKey.toAddress(ss58Prefix)
        
        return WalletCreationResult(
            mnemonic = result.mnemonic.words,
            publicKey = keypair.publicKey,
            privateKey = keypair.privateKey,
            address = address
        )
    }
    
    /**
     * 查询账户余额 (通过 HTTP RPC)
     */
    suspend fun getBalance(address: String): AccountBalance = withContext(Dispatchers.IO) {
        try {
            val rpcUrl = currentNetwork.rpcUrl
                .replace("wss://", "https://")
                .replace("ws://", "http://")
            
            // 构造 storage key: System.Account(address)
            val accountId = SS58Encoder.decode(address)
            val storageKey = "0x26aa394eea5630e07c48ae0c9558cef7" +
                    "b99d880ec681799c0cf30e8886371da9" +
                    accountId.joinToString("") { "%02x".format(it) } +
                    "0000000000000000"
            
            val request = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "state_getStorage")
                put("params", listOf(storageKey))
            }
            
            val response = makeHttpRequest("/rpc", request.toString())
            val jsonResponse = JSONObject(response)
            
            if (jsonResponse.has("result")) {
                val hex = jsonResponse.getString("result")
                if (hex != null && hex != "0x" && hex.length > 2) {
                    parseBalance(address, hex)
                } else {
                    AccountBalance(address, 0L, 0L, 0L, 0L)
                }
            } else {
                AccountBalance(address, 0L, 0L, 0L, 0L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            AccountBalance(address, 0L, 0L, 0L, 0L)
        }
    }
    
    /**
     * 获取最新区块高度
     */
    suspend fun getBlockNumber(): Long = withContext(Dispatchers.IO) {
        try {
            val rpcUrl = currentNetwork.rpcUrl
                .replace("wss://", "https://")
                .replace("ws://", "http://")
            
            val request = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "chain_getHeader")
                put("params", emptyList<String>())
            }
            
            val response = makeHttpRequest("/rpc", request.toString())
            val jsonResponse = JSONObject(response)
            
            if (jsonResponse.has("result")) {
                val result = jsonResponse.getJSONObject("result")
                val numberHex = result.getString("number")
                numberHex.removePrefix("0x").toLong(16)
            } else {
                0L
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
    
    /**
     * 发送转账交易 (需要签名)
     */
    suspend fun transfer(
        fromAddress: String,
        toAddress: String,
        amount: Long,
        privateKey: ByteArray
    ): String = withContext(Dispatchers.IO) {
        // TODO: 使用 Nova SDK 构造 extrinsic 并签名
        // 需要 runtime metadata 来编码 call data
        throw NotImplementedError("转账功能需要 runtime metadata 集成")
    }
    
    /**
     * 获取交易历史
     */
    suspend fun getTransactionHistory(address: String): List<Transaction> = withContext(Dispatchers.IO) {
        // TODO: 扫描链上事件获取交易历史
        emptyList()
    }
    
    /**
     * 验证 SS58 地址
     */
    fun isValidAddress(address: String): Boolean {
        return try {
            SS58Encoder.decode(address)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    // ---- 内部方法 ----
    private fun parseBalance(address: String, hex: String): AccountBalance {
        return try {
            val bytes = hex.removePrefix("0x")
                .chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()
            
            // Substrate AccountData: free(16) + reserved(16) + miscFrozen(16) + feeFrozen(16)
            if (bytes.size >= 64) {
                val free = bytes.sliceArray(0..15).toLongLE()
                val reserved = bytes.sliceArray(16..31).toLongLE()
                val miscFrozen = bytes.sliceArray(32..47).toLongLE()
                val feeFrozen = bytes.sliceArray(48..63).toLongLE()
                AccountBalance(address, free, reserved, miscFrozen, feeFrozen)
            } else {
                AccountBalance(address, 0L, 0L, 0L, 0L)
            }
        } catch (e: Exception) {
            AccountBalance(address, 0L, 0L, 0L, 0L)
        }
    }
    
    private fun makeHttpRequest(url: String, body: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true
        connection.connectTimeout = 10000
        connection.readTimeout = 30000
        connection.outputStream.use { os ->
            os.write(body.toByteArray())
        }
        return connection.inputStream.bufferedReader().readText()
    }
    
    private fun ByteArray.toLongLE(): Long {
        var result = 0L
        for (i in indices.reversed()) {
            result = result shl 8 or (this[i].toLong() and 0xFF)
        }
        return result
    }
}

/**
 * 钱包创建结果
 */
data class WalletCreationResult(
    val mnemonic: String,
    val publicKey: ByteArray,
    val privateKey: ByteArray,
    val address: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WalletCreationResult
        return address == other.address
    }
    
    override fun hashCode(): Int = address.hashCode()
}
