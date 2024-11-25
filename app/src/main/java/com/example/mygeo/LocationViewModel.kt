package com.example.mygeo


import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class LocationState {
    object Loading : LocationState() // Состояние загрузки
    data class Success(val latitude: Double, val longitude: Double) : LocationState() // Успех
    data class Error(val message: String) : LocationState() // Ошибка
}

class LocationViewModel : ViewModel() {
    private val _locationState = MutableStateFlow<LocationState>(LocationState.Loading)
    val locationState: StateFlow<LocationState> = _locationState

    @SuppressLint("MissingPermission")
    fun fetchLocation(fusedLocationClient: FusedLocationProviderClient) {
        _locationState.value = LocationState.Loading
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    _locationState.value = LocationState.Success(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                } else {
                    _locationState.value = LocationState.Error("Location is null. Try again.")
                }
            }
            .addOnFailureListener {
                _locationState.value = LocationState.Error("Failed to get location: ${it.message}")
            }
    }

    fun setError(message: String) {
        _locationState.value = LocationState.Error(message)
    }
}
