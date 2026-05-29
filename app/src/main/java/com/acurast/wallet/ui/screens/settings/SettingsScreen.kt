package com.acurast.wallet.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.acurast.wallet.data.model.*
import com.acurast.wallet.data.repository.WalletRepository
import kotlinx.coroutines.launch

/**
 * 设置屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scope = rememberCoroutineScope()
    val repository = remember { WalletRepository() }
    
    // 状态
    var selectedNetwork by remember { mutableStateOf(NetworkType.MAINNET) }
    var currentAddress by remember { mutableStateOf("5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY") }
    var walletName by remember { mutableStateOf("我的钱包") }
    var showNetworkDialog by remember { mutableStateOf(false) }
    var showWalletInfo by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    // 网络信息
    val networks = listOf(
        NetworkType.MAINNET to "Acurast 主网",
        NetworkType.CANARY to "Canary 测试网"
    )
    
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
                        Icons.Default.Refresh,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "当前网络",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            if (selectedNetwork == NetworkType.MAINNET) "Acurast 主网" else "Canary 测试网",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
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
                            if (showWalletInfo) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                    
                    if (showWalletInfo) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "完整地址",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            currentAddress,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "SS58 前缀: 42",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 操作
            Text(
                "操作",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // 导出助记词
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { /* 导出助记词 */ }
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
                onClick = { /* 切换账户 */ }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
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
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Acurast Wallet v1.0.0",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        "基于 Nova Substrate SDK for Android",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "去中心化钱包，管理您的 ACU 代币",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
                                        .padding(8.dp),
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
                                    Text(
                                        name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showNetworkDialog = false }
                        ) {
                            Text("取消")
                        }
                    }
                )
            }
            
            // 错误消息
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        errorMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // 成功消息
            if (successMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        successMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
