package com.acurast.wallet.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * 主页屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToTransfer: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val repository = remember { WalletRepository() }
    
    // 状态
    var walletState by remember { mutableStateOf<WalletState>(WalletState.NoWallet) }
    var balance by remember { mutableStateOf<AccountBalance?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // 模拟钱包状态（实际应该从本地存储读取）
    LaunchedEffect(Unit) {
        // 模拟已创建的钱包
        val testAddress = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY"
        walletState = WalletState.HasWallet(WalletAccount(
            address = testAddress,
            name = "我的钱包",
            publicKey = ByteArray(32) // 模拟公钥
        ))
        
        // 查询余额
        isLoading = true
        try {
            // 使用 Nova SDK 查询真实余额
            balance = repository.getBalance(testAddress)
        } catch (e: Exception) {
            errorMessage = "查询余额失败: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acurast Wallet") },
                actions = {
                    IconButton(onClick = { /* 刷新余额 */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "刷新")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 钱包状态卡片
            when (val state = walletState) {
                is WalletState.NoWallet -> {
                    // 没有钱包
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "欢迎使用 Acurast Wallet",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "创建或导入钱包开始管理您的 ACU 代币",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = onNavigateToCreate) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("创建钱包")
                            }
                        }
                    }
                }
                is WalletState.HasWallet -> {
                    // 有钱包
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Text(
                                "钱包余额",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (isLoading) {
                                CircularProgressIndicator()
                            } else if (errorMessage != null) {
                                Text(
                                    errorMessage!!,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (balance != null) {
                                Text(
                                    "${balance!!.free / 1_000_000_000} ACU",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "可用余额",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            "${balance!!.reserved / 1_000_000_000} ACU",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            "质押",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Column {
                                        Text(
                                            "${balance!!.miscFrozen / 1_000_000_000} ACU",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            "冻结",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // 地址显示
                            Text(
                                "地址: ${state.account.address.take(8)}...${state.account.address.takeLast(8)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 快速操作按钮
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = onNavigateToTransfer) {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("转账")
                        }
                        OutlinedButton(onClick = { /* 接收 */ }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("接收")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // 最近交易
                    Text(
                        "最近交易",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 模拟交易数据
                    val recentTransactions = listOf(
                        Transaction(
                            hash = "0x1234567890abcdef",
                            from = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY",
                            to = "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty",
                            amount = 1000000000,
                            fee = 1000000,
                            timestamp = System.currentTimeMillis() - 3600000,
                            status = TransactionStatus.CONFIRMED
                        ),
                        Transaction(
                            hash = "0xabcdef1234567890",
                            from = "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty",
                            to = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY",
                            amount = 500000000,
                            fee = 500000,
                            timestamp = System.currentTimeMillis() - 7200000,
                            status = TransactionStatus.CONFIRMED
                        )
                    )
                    
                    LazyColumn {
                        items(recentTransactions) { transaction ->
                            TransactionItem(transaction = transaction)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
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
                if (transaction.from == "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY") 
                    Icons.Default.KeyboardArrowUp 
                else 
                    Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (transaction.from == "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY") 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (transaction.from == "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY") "发送" else "接收",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "${transaction.hash.take(8)}...${transaction.hash.takeLast(8)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${transaction.amount / 1_000_000_000} ACU",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${transaction.timestamp}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
