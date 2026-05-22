package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.VpsServer
import com.example.ui.components.InlineWebAdBlock
import com.example.ui.theme.*
import com.example.viewmodel.VpsViewModel
import kotlinx.coroutines.delay
import java.text.DecimalFormat

@Composable
fun ServerScreen(
    viewModel: VpsViewModel,
    modifier: Modifier = Modifier
) {
    val servers by viewModel.servers.collectAsState()
    val selectedId by viewModel.selectedServerId.collectAsState()
    val terminalLogs by viewModel.terminalLogs.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    val df2 = remember { DecimalFormat("0.0") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = GoogleBlue,
                contentColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.testTag("add_server_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Server Connection")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Sub-header stats bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Connections Nodes (${servers.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Surface(
                    color = GoogleGreen.copy(alpha = 0.15f),
                    contentColor = GoogleGreen,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GoogleGreen)
                        )
                        Text(
                            text = "${servers.count { it.status == "ONLINE" }} Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ad block
            InlineWebAdBlock()

            Spacer(modifier = Modifier.height(12.dp))

            if (servers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "No connections",
                            tint = TextSecondary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No Connected VPS Nodes",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tap + to bind an SSH host manually or order from the Store.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(servers, key = { it.id }) { server ->
                        val isSelected = selectedId == server.id
                        ServerCard(
                            server = server,
                            isSelected = isSelected,
                            onHeaderClicked = {
                                if (isSelected) viewModel.selectServer(null)
                                else viewModel.selectServer(server.id)
                            },
                            onDeleteClicked = {
                                viewModel.deleteServer(server)
                            },
                            terminalLogs = terminalLogs,
                            onExecuteCommand = { cmd ->
                                viewModel.executeTerminalCommand(cmd)
                            },
                            formatDecimal = { df2.format(it) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        ManualAddDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, ip, user, os, loc ->
                viewModel.addManualServer(name, ip, user, os, loc)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ServerCard(
    server: VpsServer,
    isSelected: Boolean,
    onHeaderClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    terminalLogs: List<Pair<String, Boolean>>,
    onExecuteCommand: (String) -> Unit,
    formatDecimal: (Float) -> String
) {
    val statusColor = when (server.status) {
        "ONLINE" -> GoogleGreen
        "REBOOTING" -> GoogleYellow
        else -> GoogleRed
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isSelected) GoogleBlue else BorderColor, RoundedCornerShape(12.dp))
            .testTag("server_card_${server.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.animateContentSize()) {
            // Header Row
            Row(
                modifier = Modifier
                    .clickable { onHeaderClicked() }
                    .padding(14.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // OS Icon Placeholder
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (server.status == "ONLINE") Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = "OS Icon",
                        tint = statusColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = server.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = server.ipAddress,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(TextSecondary))
                        Text(
                            text = server.location,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    StatusPill(status = server.status, color = statusColor)
                    if (server.status == "ONLINE") {
                        Text(
                            text = "CPU ${formatDecimal(server.cpuUsage)}%",
                            fontSize = 10.sp,
                            color = if (server.cpuUsage > 80) GoogleRed else TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Expanded Telemetry & SSH Area
            if (isSelected) {
                Divider(color = BorderColor, thickness = 1.dp)

                Column(modifier = Modifier.padding(14.dp)) {
                    // System Info Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryItem(
                            label = "vCPU / RAM",
                            value = "${server.vCpu} Cores / ${server.ramGb} GB",
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryItem(
                            label = "OS Distribution",
                            value = server.os,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TelemetryItem(
                            label = "Volume Storage",
                            value = "${server.diskGb} GB NVMe SSD",
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryItem(
                            label = "Traffic (In/Out)",
                            value = "${formatDecimal(server.bandwidthIn.toFloat())} MB / ${formatDecimal(server.bandwidthOut.toFloat())} MB",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (server.status == "ONLINE") {
                        // Interactive Shell / SSH Terminal Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Interactive SSH Terminal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoogleBlue
                            )
                            IconButton(
                                onClick = onDeleteClicked,
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("delete_server_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Disconnect server Node",
                                    tint = GoogleRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Terminal Console Box
                        VirtualTerminal(
                            logs = terminalLogs,
                            onExecuteCommand = onExecuteCommand
                        )
                    } else if (server.status == "REBOOTING") {
                        CircularProgressIndicator(
                            color = GoogleYellow,
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Server node is rebooting daemon. Sockets will reconnect in 4s...",
                            fontSize = 11.sp,
                            color = GoogleYellow,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        // Offline Actions
                        Button(
                            onClick = onDeleteClicked,
                            colors = ButtonDefaults.buttonColors(containerColor = GoogleRed),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Remove Disconnected VPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(GoogleDarkBg, RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@Composable
fun StatusPill(status: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(text = status, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun VirtualTerminal(
    logs: List<Pair<String, Boolean>>,
    onExecuteCommand: (String) -> Unit
) {
    var commandInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF070809))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        // Log scroll
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 120.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            logs.takeLast(6).forEach { log ->
                Text(
                    text = log.first,
                    color = if (log.second) GoogleRed else Color(0xFF00FF66),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Divider(color = Color(0xFF15181D))
        Spacer(modifier = Modifier.height(4.dp))

        // Input Line
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "root@vps:~$ ",
                color = GoogleBlue,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
            BasicTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (commandInput.isNotBlank()) {
                            onExecuteCommand(commandInput)
                            commandInput = ""
                        }
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("terminal_input_field"),
                cursorBrush = SolidColor(Color.White)
            )

            IconButton(
                onClick = {
                    if (commandInput.isNotBlank()) {
                        onExecuteCommand(commandInput)
                        commandInput = ""
                    }
                },
                modifier = Modifier
                    .size(20.dp)
                    .testTag("submit_command")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Execute",
                    tint = GoogleBlue,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, ip: String, user: String, os: String, loc: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var ip by remember { mutableStateOf("") }
    var user by remember { mutableStateOf("") }
    var selectedOs by remember { mutableStateOf("Ubuntu 24.04 LTS") }
    var selectedLoc by remember { mutableStateOf("Germany, Frankfurt") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Connect Manually", color = TextPrimary) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Server Label") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_name_field")
                )

                OutlinedTextField(
                    value = ip,
                    onValueChange = { ip = it },
                    label = { Text("IP Address / Host") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_ip_field")
                )

                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("SSH Username") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_username_field")
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedOs,
                        onValueChange = { selectedOs = it },
                        label = { Text("OS Distro") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_os_field")
                    )

                    OutlinedTextField(
                        value = selectedLoc,
                        onValueChange = { selectedLoc = it },
                        label = { Text("Location") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_location_field")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name, ip, user, selectedOs, selectedLoc) },
                colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                modifier = Modifier.testTag("add_confirm_btn")
            ) {
                Text("Establish connection", color = MaterialTheme.colorScheme.background)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GoogleBlue)
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
}

// Minimalist BasicTextField support block
@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    cursorBrush: androidx.compose.ui.graphics.Brush
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier,
        cursorBrush = cursorBrush
    )
}
