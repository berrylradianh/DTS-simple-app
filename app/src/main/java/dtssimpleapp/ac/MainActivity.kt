package dtssimpleapp.ac

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import dtssimpleapp.ac.ui.theme.DTSSimpleAPPTheme
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.tokenizer.UnknownFunctionOrVariableException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DTSSimpleAPPTheme {
                MainScreen()
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("DTS Simple APP") },
                            actions = {
                                TopAppBarActions(onActionSelected = { action ->
                                    when (action) {
                                        "input_name" -> navController.navigate("input_name")
                                        "calculator" -> navController.navigate("calculator")
                                        "list_view" -> navController.navigate("list_view")
                                    }
                                })
                            }
                        )
                    }
                ) { innerPadding ->
                    Greeting(
                        name = "",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            composable("input_name") {
                InputNameScreen(onBack = { navController.popBackStack() })
            }
            composable("calculator") {
                CalculatorScreen(onBack = { navController.popBackStack() })
            }
            composable("list_view") {
                ListViewScreen(onBack = { navController.popBackStack() }, showToast = { message ->
                    showToast(message)
                })
            }
        }
    }

    @Composable
    fun TopAppBarActions(onActionSelected: (String) -> Unit) {
        DropdownMenu(onActionSelected)
    }

    @Composable
    fun DropdownMenu(onActionSelected: (String) -> Unit) {
        var expanded by remember { mutableStateOf(false) }

        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    onActionSelected("input_name")
                    expanded = false
                },
                text = { Text("Aplikasi Input Nama") }
            )
            DropdownMenuItem(
                onClick = {
                    onActionSelected("calculator")
                    expanded = false
                },
                text = { Text("Aplikasi Kalkulator") }
            )
            DropdownMenuItem(
                onClick = {
                    onActionSelected("list_view")
                    expanded = false
                },
                text = { Text("Aplikasi List View") }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InputNameScreen(onBack: () -> Unit) {
        var name by remember { mutableStateOf("") }
        var displayedName by remember { mutableStateOf<String?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Aplikasi Input Nama") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Masukkan Nama") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { displayedName = name },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Tampilkan")
                }
                Spacer(modifier = Modifier.height(16.dp))
                displayedName?.let {
                    Text(
                        text = "Nama: $it",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CalculatorScreen(onBack: () -> Unit) {
        var input by remember { mutableStateOf("") }
        var result by remember { mutableStateOf<String?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Aplikasi Kalkulator") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display input and result
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    label = { Text("Input") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = result ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Calculator buttons
                CalculatorButtonGrid(
                    onButtonClick = { button ->
                        when (button) {
                            "=" -> {
                                try {
                                    result = calculateResult(input)
                                    input = ""
                                } catch (e: Exception) {
                                    result = "Error"
                                    input = ""
                                }
                            }
                            "C" -> {
                                input = ""
                                result = null
                            }
                            else -> input += button
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun CalculatorButtonGrid(onButtonClick: (String) -> Unit) {
        val buttons = listOf(
            "1", "2", "3", "+",
            "4", "5", "6", "-",
            "7", "8", "9", "*",
            "0", ".", "C", "/",
            "%", "="
        )

        Column {
            buttons.chunked(4).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { button ->
                        Button(
                            onClick = { onButtonClick(button) },
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(1f)
                        ) {
                            Text(button)
                        }
                    }
                }
            }
        }
    }

    private fun calculateResult(expression: String): String {
        // Replace % with /100 for percentage calculations
        val safeExpression = expression.replace("%", "/100")

        return try {
            val expressionBuilder = ExpressionBuilder(safeExpression).build()
            val result = expressionBuilder.evaluate()
            result.toString()
        } catch (e: IllegalArgumentException) {
            "Error"
        } catch (e: UnknownFunctionOrVariableException) {
            "Error"
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ListViewScreen(onBack: () -> Unit, showToast: (String) -> Unit) {
        val items = listOf(
            "Afghanistan", "Armenia", "Azerbaijan", "Bahrain", "Bangladesh", "Bhutan",
            "Brunei", "Cambodia", "China", "Cyprus", "Georgia", "India", "Indonesia",
            "Iran", "Iraq", "Israel", "Japan", "Jordan", "Kazakhstan", "Kuwait",
            "Kyrgyzstan", "Laos", "Lebanon", "Malaysia", "Maldives", "Mongolia",
            "Myanmar", "Nepal", "North Korea", "Oman", "Pakistan", "Palestine",
            "Philippines", "Qatar", "Russia", "Saudi Arabia", "Singapore", "South Korea",
            "Sri Lanka", "Syria", "Taiwan", "Tajikistan", "Thailand", "Timor-Leste",
            "Turkey", "Turkmenistan", "United Arab Emirates", "Uzbekistan", "Vietnam",
            "Yemen"
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Aplikasi List View") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(items.size) { index ->
                    val item = items[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { showToast(item) },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = item,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Divider(color = Color.Gray)
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "$name",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        DTSSimpleAPPTheme {
            Greeting("")
        }
    }
}
