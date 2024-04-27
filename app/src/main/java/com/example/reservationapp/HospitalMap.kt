package com.example.reservationapp


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource


class HospitalMap : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG = "MapActivity"
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var mLocationSource: FusedLocationSource
    private var mNaverMap: NaverMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_map)

        val fm: FragmentManager = supportFragmentManager
        var mapFragment: MapFragment? = fm.findFragmentById(R.id.map_fragment) as MapFragment?

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(
                NaverMapOptions()
                    .camera(
                        CameraPosition(
                            LatLng(37.582570570452226, 127.01024590362488),
                            16.0 // 줌 레벨 설정
                        )
                    )
            )
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit()
        }

        mLocationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        }

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            enableLocation()
        }
    }

    private fun enableLocation() {
        mNaverMap?.let {
            it.locationSource = mLocationSource
            it.locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        mNaverMap = naverMap

        // 위치 소스 및 추적 모드 설정
        enableLocation()

        // 현재 위치를 가져와서 마커로 표시
        mLocationSource.getLastLocation()?.let { location ->
            val currentPosition = LatLng(location.latitude, location.longitude)
            val marker = Marker()
            marker.position = currentPosition
            marker.map = mNaverMap // 마커를 지도에 추가
            marker.icon = OverlayImage.fromResource(R.drawable.image_marker) // 새 마커 이미지 지정
            marker.width = resources.getDimensionPixelSize(R.dimen.marker_width) // 마커의 너비 설정
            marker.height = resources.getDimensionPixelSize(R.dimen.marker_height) // 마커의 높이 설정
        }

        // 지도에 또 다른 마커 표시
        mNaverMap?.let {
            val marker2 = Marker()
            marker2.position = LatLng(37.58, 127.010)
            marker2.map = it
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                enableLocation()
            }
        }
    }
}

