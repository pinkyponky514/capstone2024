package com.example.reservationapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.reservationapp.Custom.CustomToast
import com.example.reservationapp.Model.OpenApiHospital
import com.example.reservationapp.Model.ApiHospitalResponse
import com.example.reservationapp.Model.Hospital
import com.example.reservationapp.Model.HospitalSignupInfoResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HospitalMapActivity : AppCompatActivity(), OnMapReadyCallback/* Overlay.OnClickListener*/ {

    // 필터 상태를 나타내는 변수
    private var isFilterApplied = false

    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSION_REQUEST_CODE: Int = 100

    private var gpsTracker: GpsTracker? = null
    private val PERMISSION: Array<String> = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var mLocationSource: FusedLocationSource
    private lateinit var mNaverMap: NaverMap


    private var mylat: Double = 0.0
    private var mylng: Double = 0.0

    private var m: Int = 6000

    private val markers: MutableList<Marker> = mutableListOf() // 추가된 부분
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var textView4: TextView
    private lateinit var textView5: TextView
    private lateinit var textView: TextView
    private lateinit var button: Button

    private var clickedMarker: Marker? = null

    private var clickHospitalId :Long = 0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hospital_map)


        var fm: FragmentManager = supportFragmentManager
        var mapFragment = fm.findFragmentById(R.id.hospitalmap) as MapFragment?

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            fm.beginTransaction().add(R.id.hospitalmap, mapFragment).commit()
        }
        mapFragment?.getMapAsync(this)

        mLocationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE)

        // Bottom Sheet 초기화
        val bottomSheetView: View = findViewById(R.id.hospital_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // BottomSheetCallback 추가
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if(clickHospitalId != 0.toLong()) { //병원 아이디가 있을 경우
                        // Hospital_DetailPage 액티비티로 이동
                        val intent = Intent(this@HospitalMapActivity, Hospital_DetailPage::class.java)
                        intent.putExtra("hospitalId", clickHospitalId)
                        startActivity(intent)
                    } else {
                        CustomToast(this@HospitalMapActivity, "병원 정보가 없습니다. \n병원 정보를 입력하길 기다리십시오.").show()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 진행 중에 호출됩니다.
            }
        })


        textView4 = findViewById(R.id.hospitaltextView4)
        textView5= findViewById(R.id.hospitaltextView5)
        textView = findViewById(R.id.hospitalAddress)
        button = findViewById(R.id.hospitaltelbutton)

    }

    // 마커 필터링 함수
//    private fun updateMarkers2() {
//        for (marker in markers) {
//            // 마커의 태그로부터 데이터 가져오기
//            val hospital = marker.tag as? ApiHospital
//            // 필터 상태에 따라 마커 표시/숨기기 결정
//            if (hospital != null) {
//                if (isFilterApplied && isPharmacyOpen(hospital)) {
//                    // 필터가 적용되고 영업중인 경우 마커 표시
//                    showMarker(marker)
//                } else if (!isFilterApplied) {
//                    // 필터가 해제되면 모든 마커 표시
//                    showMarker(marker)
//                } else {
//                    // 필터가 적용되고 영업중이 아닌 경우 마커 숨기기
//                    hideMarker(marker)
//                }
//            }
//        }
//    }

    private fun isHospitalOpen(hospital: Hospital): Boolean {
        val dayOfWeek = getCurrentDayOfWeek()
        val openHours = getOpenHoursForDay(dayOfWeek, hospital)
        return openHours.startsWith("영업중")
    }

    override fun onMapReady(@NonNull naverMap: NaverMap) {
        mNaverMap = naverMap

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        // 맵의 클릭 이벤트를 처리하여 Bottom Sheet를 숨김
        mNaverMap.setOnMapClickListener { _, _ ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

            clickedMarker?.apply {
                // 클릭된 마커 크기 원래대로 복구
                width = 80
                height = 80
            }
            clickedMarker = null
        }

        // 카메라 이동이 멈추면 호출되는 리스너
        mNaverMap.addOnCameraIdleListener {
            updateMarkers()
           // updateMarkers2()
        }

        gpsTracker = GpsTracker(this@HospitalMapActivity)
        var latitude: Double = gpsTracker!!.latitude
        var longitude: Double = gpsTracker!!.longitude

        mylat = gpsTracker!!.latitude
        mylng = gpsTracker!!.longitude

        val cameraUpdate = CameraUpdate.scrollTo(LatLng(mylat, mylng))
        naverMap.moveCamera(cameraUpdate)

        val locationOverlay = mNaverMap.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.position = LatLng(mylat, mylng)
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.uiSettings.isLocationButtonEnabled = true


        App.prefs.token = null

        // 통신 요청
        App.apiService.getAllHospitlas().enqueue(object: Callback<ApiHospitalResponse>{
            override fun onResponse(call: Call<ApiHospitalResponse>, response: Response<ApiHospitalResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    val data = responseBody.data
                    for (hospital in data) {

                        val lat: Double = hospital.mapY
                        val lng: Double = hospital.mapX

                        val marker = Marker()
                        marker.position = LatLng(lat, lng)
                        marker.icon = OverlayImage.fromResource(R.drawable.hospital_6395229)
                        marker.iconTintColor = Color.BLUE
                        marker.width = 80
                        marker.height = 80
                        marker.map = mNaverMap
                        markers.add(marker) // 수정된 부분
                        marker.tag = hospital // 마커에 데이터 추가

                        // 마커 클릭 리스너 설정
                        marker.setOnClickListener { overlay ->
                            if (overlay is Marker) {
                                val hospital = overlay.tag as? OpenApiHospital
                                hospital?.let {
                                    // 클릭된 마커를 추적하고 선택된 마커의 크기 조정
                                    clickedMarker?.apply {
                                        width = 80
                                        height = 80
                                    }
                                    clickedMarker = overlay
                                    showBottomSheet(it, overlay)
                                }
                                true // 마커 클릭 이벤트 소비
                            } else {
                                false
                            }
                        }
                    }

                    //              updateMarkers2()

                } else {
                    Toast.makeText(this@HospitalMapActivity, "요청 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiHospitalResponse>, t: Throwable) {
                Toast.makeText(this@HospitalMapActivity, "네트워크 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.w("error", "${t.message}")
            }
        })

        mNaverMap.setLocationSource(mLocationSource)
        ActivityCompat.requestPermissions(this, PERMISSION, PERMISSION_REQUEST_CODE)

//        mNaverMap.addOnCameraChangeListener { _, _ ->
//            updateMarkers()
//            updateMarkers2()
//        }
    }

    //마커 클릭시 bottomsheet 보여주기
    private fun showBottomSheet(hospital: OpenApiHospital, clickedMarker: Marker) {

        val hospitalId = hospital.hospitalId
        clickHospitalId = hospitalId
        Log.w("HospitalMapActivity", "Bottom Sheet HospitalId : $clickHospitalId")
        if (hospitalId.toInt() != 0){
            // 클릭한 병원의 데이터를 가져오는 Retrofit 통신 요청
            App.apiService.getHospitalDetail(hospitalId).enqueue(object : Callback<HospitalSignupInfoResponse> {
                override fun onResponse(call: Call<HospitalSignupInfoResponse>, response: Response<HospitalSignupInfoResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()!!

                        val openHours = getOpenHoursForDay(getCurrentDayOfWeek(), responseBody.data)
                        // 가져온 데이터를 이용하여 Bottom Sheet에 표시할 정보 설정
                        textView4.text = hospital.hospitalName
                        textView5.text = openHours
                        textView.text = responseBody.data.openApiHospital.address

                        // 선택된 마커 크기 크게
                        clickedMarker.width = 100
                        clickedMarker.height = 100
                        // Bottom Sheet 표시
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    } else {
                        Log.d("FAILURE Response", "Connect SUCCESS, Response FAILURE, body: ${response.body().toString()}")
                    }
                }

                override fun onFailure(call: Call<HospitalSignupInfoResponse>, t: Throwable) {
                    Log.d("CONNECTION FAILURE: ", t.localizedMessage)
                }
            })
        }else{
            textView4.text = hospital.hospitalName
            textView5.text = "운영시간 정보 없음"
            textView.text = hospital.address
            // 선택된 마커 크기 크게
            clickedMarker.width = 100
            clickedMarker.height = 100
            // Bottom Sheet 표시
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        // 클릭 시 동작 정의
        button.setOnClickListener {
            val phoneNumber = hospital.tel
            if (!phoneNumber.isNullOrEmpty()) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")
                startActivity(dialIntent)
            } else {
                // 전화번호가 없는 경우 사용자에게 메시지 표시
                Toast.makeText(this, "전화번호가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // 현재 시스템 날짜와 시간을 기반으로 요일을 가져오는 함수
    private fun getCurrentDayOfWeek(): DayOfWeek {
        val cal = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK)
        return when (day) {
            Calendar.SUNDAY -> DayOfWeek.SUNDAY
            Calendar.MONDAY -> DayOfWeek.MONDAY
            Calendar.TUESDAY -> DayOfWeek.TUESDAY
            Calendar.WEDNESDAY -> DayOfWeek.WEDNESDAY
            Calendar.THURSDAY -> DayOfWeek.THURSDAY
            Calendar.FRIDAY -> DayOfWeek.FRIDAY
            Calendar.SATURDAY -> DayOfWeek.SATURDAY
            else -> throw IllegalArgumentException("Invalid day of week")
        }
    }

    // 요일에 따라 해당하는 진료 시간을 반환하는 함수
    private fun getOpenHoursForDay(dayOfWeek: DayOfWeek, hospital: Hospital): String {
        val startTime: String
        val closeTime: String

        val hospitaldetail = hospital.hospitalDetail

        when (dayOfWeek) {
            DayOfWeek.SUNDAY -> {
                startTime = hospitaldetail.sun_open ?: ""
                closeTime = hospitaldetail.sun_close ?: ""
            }
            DayOfWeek.MONDAY -> {
                startTime = hospitaldetail.mon_open ?: ""
                closeTime = hospitaldetail.mon_close ?: ""
            }
            DayOfWeek.TUESDAY -> {
                startTime = hospitaldetail.tue_open ?: ""
                closeTime = hospitaldetail.tue_open ?: ""
            }
            DayOfWeek.WEDNESDAY -> {
                startTime = hospitaldetail.wed_open ?: ""
                closeTime = hospitaldetail.wed_close ?: ""
            }
            DayOfWeek.THURSDAY -> {
                startTime = hospitaldetail.thu_open ?: ""
                closeTime = hospitaldetail.thu_close ?: ""
            }
            DayOfWeek.FRIDAY -> {
                startTime = hospitaldetail.fri_open ?: ""
                closeTime = hospitaldetail.fri_close ?: ""
            }
            DayOfWeek.SATURDAY -> {
                startTime = hospitaldetail.sat_open ?: ""
                closeTime = hospitaldetail.sat_close ?: ""
            }
        }

        if (startTime.isNullOrEmpty() || closeTime.isNullOrEmpty()) {
            return "전화 문의"
        }

        val sdf = SimpleDateFormat("HHmm", Locale.getDefault())
        val currentTime = sdf.format(Date())

        val startTimeStr = if (startTime.isNotEmpty()) {
            startTime.substring(0, 2) + ":" + startTime.substring(2)
        } else {
            ""
        }

        val closeTimeStr = if (closeTime.isNotEmpty()) {
            closeTime.substring(0, 2) + ":" + closeTime.substring(2)
        } else {
            ""
        }

        val isOpen = isWithinOpenHours(startTime, closeTime, currentTime)
        return if (isOpen) {
            "영업중  $startTimeStr ~ $closeTimeStr"
        } else {
            "영업종료 $startTimeStr ~ $closeTimeStr"
        }
    }

    // 영업중, 영업종료 확인 함수
    private fun isWithinOpenHours(startTime: String, closeTime: String, currentTime: String): Boolean {
        if (startTime.isEmpty() || closeTime.isEmpty()) {
            return false
        }

        val startTimeHour = startTime.substring(0, 2).toInt()
        val startTimeMinute = startTime.substring(2).toInt()
        val closeTimeHour = closeTime.substring(0, 2).toInt()
        val closeTimeMinute = closeTime.substring(2).toInt()
        val currentHour = currentTime.substring(0, 2).toInt()
        val currentMinute = currentTime.substring(2).toInt()

        if (currentHour < startTimeHour || currentHour > closeTimeHour) {
            return false
        }

        if (currentHour == startTimeHour && currentMinute < startTimeMinute) {
            return false
        }

        if (currentHour == closeTimeHour && currentMinute > closeTimeMinute) {
            return false
        }

        return true
    }

    private fun updateMarkers() {
        val mapBounds = mNaverMap.contentBounds

        for (marker in markers) {
            val position = marker.position

            if (mapBounds.contains(position)) {
                showMarker(marker)
            } else {
                hideMarker(marker)
            }
        }
    }

    private fun showMarker(marker: Marker) {
        if (marker.map == null) {
            marker.map = mNaverMap
        }
    }

    private fun hideMarker(marker: Marker) {
        if (marker.map != null) {
            marker.map = null
        }
    }


    private fun checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@HospitalMapActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@HospitalMapActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@HospitalMapActivity,
                    PERMISSION[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(this@HospitalMapActivity, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG)
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@HospitalMapActivity, PERMISSION,
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@HospitalMapActivity, PERMISSION,
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun showDialogForLocationServiceSetting() {
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@HospitalMapActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다.
            위치 설정을 수정하실래요?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }

}
