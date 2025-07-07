package com.aranthalion.focusly

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aranthalion.focusly.ui.theme.FocuslyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.aranthalion.focusly.work.ExampleWorker

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var exampleString: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocuslyTheme {
                // Nueva sección: Progreso de Hitos
                val hitos = listOf(
                    "Base del proyecto",
                    "MVVM (ViewModel y LiveData)",
                    "Hilt (Inyección de dependencias)",
                    "Room (Base de datos local)",
                    "Coroutines (Operaciones asíncronas)",
                    "WorkManager (Servicios en segundo plano)",
                    "Timber (Logging avanzado)",
                    "Retrofit (Networking/API)"
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hitos completados:",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(hitos) { hito ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Text(
                                    text = "✅ $hito",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        Toast.makeText(this, exampleString, Toast.LENGTH_LONG).show()
        // Lanzar el Worker de ejemplo
        val workRequest = OneTimeWorkRequestBuilder<ExampleWorker>().build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FocuslyTheme {
        Greeting("Android")
    }
}