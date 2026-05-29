package com.acurast.wallet.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import com.acurast.wallet.data.model.*
import com.acurast.wallet.data.repository.WalletRepository
import kotlinx.coroutines.launch
import android.content.Context

/**
 * 设置屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scope = rememberCoroutineScope()
    val repository = remember { WalletRepository() }
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("acurast_wallet", Context.MODE_PRIVATE)
    
    // 状态
    var selectedNetwork by remember { mutableStateOf(NetworkType.MAINNET) }
    var currentAddress by remember { mutableStateOf(prefs.getString("wallet_address", "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY") ?: "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY") }
    var walletName by remember { mutableStateOf(prefs.getString("wallet_name", "我的钱包") ?: "我的钱包") }
    var showNetworkDialog by remember { mutableStateOf(false) }
    var showWalletInfo by remember { mutableStateOf(false) }
    var showMnemonicDialog by remember { mutableStateOf(false) }
    var walletState by remember { mutableStateOf<WalletState>(WalletState.NoWallet) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    // 读取钱包状态
    LaunchedEffect(Unit) {
        val savedAddress = prefs.getString("wallet_address", null)
        if (savedAddress != null) {
            val savedName = prefs.getString("wallet_name", "我的钱包") ?: "我的钱包"
            walletState = WalletState.HasWallet(WalletAccount(
                address = savedAddress,
                name = savedName,
                publicKey = ByteArray(32)
            ))
        }
    }
    
    // 网络信息
    val networks = listOf(
        NetworkType.MAINNET to "Acurast 主网",
        NetworkType.CANARY to "Canary 测试网"
    )
    
    // 助记词对话框
    if (showMnemonicDialog) {
        val mnemonic = prefs.getString("wallet_mnemonic", null)
        AlertDialog(
            onDismissRequest = { showMnemonicDialog = false },
            title = { Text("助记词") },
            text = {
                Column {
                    Text(
                        "请妥善保管您的助记词，这是恢复钱包的唯一方式：",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        mnemonic ?: "助记词未找到",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "请勿将助记词分享给任何人！",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showMnemonicDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 钱包信息
            Text(
                "钱包信息",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showWalletInfo = !showWalletInfo }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            walletName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "${currentAddress.take(8)}...${currentAddress.takeLast(8)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        if (showWalletInfo) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            
            if (showWalletInfo) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "钱包地址",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            currentAddress,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "网络",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            selectedNetwork.displayName,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 安全设置
            Text(
                "安全设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // 导出助记词
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // 从 SharedPreferences 读取助记词
                    val mnemonic = prefs.getString("wallet_mnemonic", null)
                    if (mnemonic != null) {
                        showMnemonicDialog = true
                    } else {
                        errorMessage = "助记词未找到"
                    }
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "导出助记词",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "备份您的钱包助记词",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 切换账户
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    // 清除钱包状态，回到创建/恢复钱包界面
                    val editor = prefs.edit()
                    editor.clear()
                    editor.apply()
                    walletState = WalletState.NoWallet
                    successMessage = "钱包已切换"
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.SwapHoriz,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "切换账户",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "管理多个钱包账户",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 网络设置
            Text(
                "网络设置",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showNetworkDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.NetworkCheck,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "网络",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            selectedNetwork.displayName,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 关于
            Text(
                "关于",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Acurast Wallet",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "版本 1.0.0",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 错误和成功消息
            errorMessage?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            successMessage?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    
    // 网络选择对话框
    if (showNetworkDialog) {
        AlertDialog(
            onDismissRequest = { showNetworkDialog = false },
            title = { Text("选择网络") },
            text = {
                Column {
                    networks.forEach { (network, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    selectedNetwork = network
                                    showNetworkDialog = false
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedNetwork == network,
                                onClick = {
                                    selectedNetwork = network
                                    showNetworkDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNetworkDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
