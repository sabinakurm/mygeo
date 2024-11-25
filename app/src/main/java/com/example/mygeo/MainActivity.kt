package com.example.geolocationapp

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mygeo.LocationState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.mygeo.LocationViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationViewModel: LocationViewModel by viewModels()

        // Регистрация запроса разрешений
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                locationViewModel.fetchLocation(fusedLocationClient)
            } else {
                locationViewModel.setError("Permission denied.")
            }
        }

        setContent {
            GeolocationApp(
                viewModel = locationViewModel,
                requestPermission = {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            )
        }
    }
}

@Composable
fun GeolocationApp(viewModel: LocationViewModel, requestPermission: () -> Unit) {
    val locationState by viewModel.locationState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val state = locationState) {
                is LocationState.Loading -> {
                    CircularProgressIndicator() // Показываем индикатор загрузки
                }
                is LocationState.Success -> {
                    // Показываем координаты
                    Text(text = "Latitude: ${state.latitude}")
                    Text(text = "Longitude: ${state.longitude}")
                }
                is LocationState.Error -> {
                    // Показываем сообщение об ошибке
                    Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = requestPermission) {
                Text("Request Location")
            }
        }
    }
}
