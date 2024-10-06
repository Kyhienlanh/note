package com.example.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.compose.ui.theme.ComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTheme {
                NoteApp()
            }
        }
    }
}

@Composable
fun NoteApp() {
    var isDarkTheme by remember { mutableStateOf(false) }
    val notes = remember { mutableStateListOf<Note>() }
    var showDialog by remember { mutableStateOf(false) }
    var nextId by remember { mutableIntStateOf(1) }
    var noteToDelete by remember { mutableStateOf<Note?>(null) }

    // Mở hộp thoại khi nhấn FloatingActionButton
    if (showDialog) {
        NoteDialog(onDismiss = { showDialog = false }) { title, content ->
            if (title.isNotBlank() && content.isNotBlank()) {
                notes.add(Note(id = nextId, title = title, content = content))
                nextId++
            }
            showDialog = false
        }
    }

    // Hộp thoại xác nhận xóa
    noteToDelete?.let { note ->
        AlertDialog(
            onDismissRequest = { noteToDelete = null },
            title = { Text("Xóa ghi chú") },
            text = { Text("Bạn có chắc chắn muốn xóa ghi chú này không?") },
            confirmButton = {
                TextButton(onClick = {
                    notes.remove(note)
                    noteToDelete = null
                }) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { noteToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }

    // Sử dụng ComposeTheme với trạng thái chế độ tối
    ComposeTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Ghi chú", color = Color.White) },
                    actions = {
                        IconButton(onClick = { isDarkTheme = !isDarkTheme }) {
                            Text(if (isDarkTheme) "Sáng" else "Tối", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Text("+", color = Color.White)
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(notes, key = { note -> note.id }) { note ->
                        NoteCard(note) {
                            noteToDelete = note // Lưu ghi chú muốn xóa
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var noteTitle by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nhập ghi chú mới") },
        text = {
            Column {
                TextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Tiêu đề ghi chú") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Nội dung ghi chú") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(noteTitle, noteText)
                noteTitle = ""
                noteText = ""
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@Composable
fun NoteCard(note: Note, onDelete: () -> Unit) {
    var isLongPress by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.changes.any { it.pressed }) {
                            // Bắt đầu đồng hồ đếm 1 giây
                            isLongPress = true
                            coroutineScope.launch {
                                delay(1000) // Đợi 1 giây
                                if (isLongPress) {
                                    onDelete() // Gọi hàm xóa nếu vẫn còn nhấn giữ
                                }
                            }
                        } else {
                            isLongPress = false // Khôi phục trạng thái khi nhả tay
                        }
                    }
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Start
            )
        }
    }
}
