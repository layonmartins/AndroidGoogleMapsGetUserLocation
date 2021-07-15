package com.layon.myapplication.androidgooglemapsgetuserlocation

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.layon.myapplication.androidgooglemapsgetuserlocation.databinding.ActivityMapsBinding
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Validate permissions
        Permissions.validatePermissions(permissions, this, Companion.REQUEST_PERMISSION_CODE)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener {

            mMap.clear()
            val userLocation = LatLng(it.latitude, it.longitude)
            Log.d("TAG", "getUserLocation: ${it.latitude}, ${it.longitude}")
            // Add a marker and move the camera
            mMap.addMarker(MarkerOptions().position(userLocation).title("My Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

            // Used to convert the adress and latitude and longitude
            val geocoder = Geocoder(applicationContext, Locale.getDefault())

            //Get Latitude and Longitude
            val adressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            if (adressList != null && adressList.size > 0) {
                val adress: Address = adressList[0]
                Log.d("layon.f", "onLocationChanged: ${adress.getAddressLine(0)}")
                binding.myAdress.text = adress.getAddressLine(0)
            }

        }

        getUserLocation()
    }

    private fun getUserLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, 0f,
                locationListener
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        Log.d("layon.f", "onRequestPermissionsResult")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (permissionResult in grantResults) {
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                alertValidatePermission()
            } else if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            }
        }
    }

    private fun alertValidatePermission() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permissions Denied")
        builder.setMessage("To use the app it is necessary to accept the permissions")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, which -> finish() }
        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        const val REQUEST_PERMISSION_CODE = 1
    }
}