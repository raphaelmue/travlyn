package org.travlyn.ui.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.travlyn.infrastructure.error.PermissionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

abstract class CurrentLocationListener : LocationListener {

    final override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    final override fun onProviderEnabled(provider: String?) {}
    final override fun onProviderDisabled(provider: String?) {}

}

suspend fun LocationManager.fetchLocation(context: Context): Location =
    withContext(Dispatchers.Main) {
        return@withContext awaitLocation(context)
    }

suspend fun LocationManager.awaitLocation(context: Context): Location =
    suspendCancellableCoroutine { continuation ->
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            continuation.resumeWithException(PermissionException("No permission for location access"))
        }

        requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 5000, 10f, object : CurrentLocationListener() {
                override fun onLocationChanged(location: Location?) {
                    if (location != null && continuation.isActive) {
                        continuation.resume(location)
                    }
                }
            })
    }