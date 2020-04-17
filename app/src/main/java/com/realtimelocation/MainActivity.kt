//package com.realtimelocation
//
//import android.Manifest
//import android.app.AppOpsManager
//import android.app.usage.UsageStatsManager
//import android.content.Context
//import android.content.Intent
//import android.content.IntentSender.SendIntentException
//import android.content.pm.PackageManager
//import android.location.Location
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Process
//import android.provider.Settings
//import android.util.Log
//import android.view.View
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import com.google.android.gms.common.ConnectionResult
//import com.google.android.gms.common.api.GoogleApiClient
//import com.google.android.gms.common.api.PendingResult
//import com.google.android.gms.common.api.Status
//import com.google.android.gms.location.*
//import com.google.android.material.snackbar.Snackbar
//import kotlinx.android.synthetic.main.activity_main.*
//
//class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
//    GoogleApiClient.OnConnectionFailedListener, LocationListener {
//
//    private val TAG: String? by lazy { MainActivity::class.java.name }
//
//    private var mFusedLocationClient: FusedLocationProviderClient? = null
//    private var lastLocation: Location? = null
//    private var locationRequest: LocationRequest? = null
//    private var mLocationCallback: LocationCallback? = null
//
//    private var mGoogleApiClient: GoogleApiClient? = null
//    private val REQUEST_PERMISSION_CODE = 14
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        backGroundService = Intent(this@MainActivity, MyBackGroundService::class.java)
//        checkIfAppUsageAccess()
//
//        if (mGoogleApiClient == null)
//            mGoogleApiClient = GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).build()
//        mGoogleApiClient!!.connect()
//
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        locationRequest = LocationRequest.create()
//        locationRequest!!.interval = 5000 //5 seconds
//        locationRequest!!.fastestInterval = 3000 //3 seconds
//        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//
//        mLocationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                Log.i(TAG, "onLocationResult -> ${locationResult}")
//                for (location in locationResult.locations) {
//                    // Update UI with location data
//                    if (location != null && location.latitude > 0 && location.longitude > 0)
//                        txtLocation.setText("Current location -\n ${location.latitude}, ${location?.longitude}")
//                }
//            }
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (!checkPermissions()) {
//            startLocationUpdates()
//            requestPermissions()
//        } else {
//            getLastLocation()
//            startLocationUpdates()
//        }
//    }
//
//    override fun onPause() {
//        stopLocationUpdates()
//        super.onPause()
//    }
//
//    private fun checkPermissions(): Boolean {
//        val permissionState: Int = ActivityCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//        return permissionState == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun startLocationPermissionRequest() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf<String>(Manifest.permission.ACCESS_COARSE_LOCATION),
//            REQUEST_PERMISSION_CODE
//        )
//    }
//
//    private fun requestPermissions() {
//        val shouldProvideRationale =
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        if (shouldProvideRationale) {
//            showSnackBar(R.string.enable_permission_request, R.string.ok,
//                View.OnClickListener { startLocationPermissionRequest() })
//        } else {
//            startLocationPermissionRequest()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String?>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            if (grantResults.isEmpty()) {
//                Log.i(TAG, "User interaction was cancelled.")
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                requestGPSEnable()
//            } else {
//                showSnackBar(R.string.allow_permission, R.string.settings,
//                    View.OnClickListener {
//                        val intent =
//                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                        intent.data = Uri.fromParts("package", packageName, null)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                        startActivity(intent)
//                    })
//            }
//        }
//    }
//
//    private fun requestGPSEnable() {
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this).build()
//            mGoogleApiClient!!.connect()
//            val builder =
//                LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
//
//            builder.setAlwaysShow(true)
//            val result: PendingResult<LocationSettingsResult> =
//                LocationServices.SettingsApi.checkLocationSettings(
//                    mGoogleApiClient,
//                    builder.build()
//                )
//            result.setResultCallback { result ->
//                val status: Status = result.status
//                val state = result.locationSettingsStates
//                when (status.statusCode) {
//                    LocationSettingsStatusCodes.SUCCESS -> {
//                        Log.d(TAG, "LocationSettingsStatusCodes.SUCCESS called")
//                        getLastLocation()
//                        startLocationUpdates()
//                    }
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
//                        try {
//                            status.startResolutionForResult(this@MainActivity, 1000)
//                        } catch (e: SendIntentException) {
//                            e.printStackTrace()
//                        }
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
//                    }
//                }
//            }
//        }
//    }
//
//    private fun getLastLocation() {
//        mFusedLocationClient!!.lastLocation
//            .addOnCompleteListener(this) { task ->
//                Log.i(TAG, "lastLocation - onComplete -> ${task}")
//                if (task.isSuccessful() && task.getResult() != null) {
//                    lastLocation = task.result
//                    txtLocation.setText("Current location -\n ${lastLocation!!.latitude}, ${lastLocation!!.longitude}")
////                    } else {
////                        showSnackBar(R.string.no_location_detected, R.string.ok, View.OnClickListener { })
//                }
//            }
//    }
//
//    private fun stopLocationUpdates() {
//        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
//    }
//
//    private fun startLocationUpdates() {
////        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
////                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            return
////        }
//        mFusedLocationClient!!.requestLocationUpdates(
//            locationRequest,
//            mLocationCallback,
//            null
//        )
//    }
//
//    private fun showSnackBar(
//        mainTextStringId: Int,
//        actionStringId: Int,
//        listener: View.OnClickListener
//    ) {
//        Snackbar.make(
//            txtLocation,
//            getString(mainTextStringId),
//            Snackbar.LENGTH_INDEFINITE
//        )
//            .setAction(getString(actionStringId), listener).show()
//    }
//
//    var backGroundService: Intent? = null
//
//    override fun onDestroy() {
//        super.onDestroy()
//        stopService(backGroundService)
//        if (mGoogleApiClient!!.isConnected) {
//            mGoogleApiClient!!.disconnect()
//        }
//    }
//
//    override fun onConnected(p0: Bundle?) {
//        Log.i(TAG, "onConnected called")
//        getLastLocation()
//        startLocationUpdates()
//    }
//
//    override fun onConnectionSuspended(p0: Int) {
//        Log.i(TAG, "onConnectionSuspended called")
//    }
//
//    override fun onConnectionFailed(p0: ConnectionResult) {
//        Log.i(TAG, "onConnectionFailed called")
//    }
//
//    override fun onLocationChanged(location: Location?) {
//        Log.i(TAG, "onLocationChanged -> ${location}")
//        if (location != null && location.latitude > 0 && location.longitude > 0)
//            txtLocation.setText("Current location -\n ${location.latitude}, ${location?.longitude}")
//    }
//
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    fun checkIfAppUsageAccess() {
//        Log.d(TAG, "checkIfAppUsageAccess called")
//        var granted = false
//        val appOps = this
//            .getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
//        val mode = appOps.checkOpNoThrow(
//            AppOpsManager.OPSTR_GET_USAGE_STATS,
//            Process.myUid(), packageName
//        )
//        granted = if (mode == AppOpsManager.MODE_DEFAULT) {
//            checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
//        } else {
//            mode == AppOpsManager.MODE_ALLOWED
//        }
//        if (Build.VERSION.SDK_INT >= 21 && !granted) {
//            Log.d(TAG, "checkIfAppUsageAccess permission not granted")
//            val mUsageStatsManager =
//                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
//            val time = System.currentTimeMillis()
//            val stats: List<*>? = mUsageStatsManager.queryUsageStats(
//                UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time
//            )
//            if (stats == null || stats.isEmpty()) {
//                val intent = Intent()
//                intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
//                startActivity(intent)
//            }
//        } else {
//            startService(backGroundService)
//            Log.d(TAG, "checkIfAppUsageAccess permission  granted")
//        }
//    }
//
//}
