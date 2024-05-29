package com.example.reservationapp

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.reservationapp.Model.NaverMapApiInterface
import com.example.reservationapp.Model.NaverMapRequest
import com.example.reservationapp.Model.PharmacyMap.NaverMapData
import com.example.reservationapp.Model.PharmacyMap.NaverMapItem
import com.example.reservationapp.databinding.ActivityPharmacymapBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class DayOfWeek(val value: Int) {
    SUNDAY(1),
    MONDAY(2),
    TUESDAY(3),
    WEDNESDAY(4),
    THURSDAY(5),
    FRIDAY(6),
    SATURDAY(7)
}


class PharmacyMapActivity : AppCompatActivity(), OnMapReadyCallback /* Overlay.OnClickListener*/ {
    private lateinit var binding: ActivityPharmacymapBinding
    private lateinit var pharmacy: NaverMapData

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

    private lateinit var naverMapList: NaverMapItem
    private lateinit var naverMapInfo: List<NaverMapData>

    private var mylat: Double = 0.0
    private var mylng: Double = 0.0

    private var m: Int = 6000

    private val markers: MutableList<Marker> = mutableListOf() // 추가된 부분
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private lateinit var pharmacyNameTextView: TextView //약국이름
    private lateinit var operatingTimeTextView: TextView //운영시간
    private lateinit var addressTextView: TextView //약국주소
    private lateinit var button: Button //전화문의 버튼

    private lateinit var lunchTimeTextView: TextView
    private lateinit var monTimeTextView: TextView
    private lateinit var tueTimeTextView: TextView
    private lateinit var wedTimeTextView: TextView
    private lateinit var thuTimeTextView: TextView
    private lateinit var friTimeTextView: TextView
    private lateinit var satTimeTextView: TextView
    private lateinit var sunTimeTextView: TextView
    private lateinit var holTimeTextView: TextView

    private var clickedMarker: Marker? = null


    //
/*
    private val bottomSheetCallback = object: BottomSheetBehavior.BottomSheetCallback() {
        //BottomSheet를 드래그 할때 동작
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when(newState) {
                //접혀져 있을때
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    moveButtonToPeekHeight()
                }
                //펼쳐져 있을때
                BottomSheetBehavior.STATE_EXPANDED -> {
                    moveButtonToBottom()
                }
                //그외
                else -> {
                    moveButtonToBottom()
                }
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            //슬라이드 동작 시 추가 작업
        }
    }

    // 버튼을 214dp 높이에 위치하도록 설정
    private fun moveButtonToPeekHeight() {
        val params = binding.callAskConstraintLayout.layoutParams as ConstraintLayout.LayoutParams
        params.bottomMargin = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
        binding.callAskConstraintLayout.layoutParams = params
    }
    // 버튼을 맨 아래에 위치하도록 설정
    private fun moveButtonToBottom() {
        val params = binding.callAskConstraintLayout.layoutParams as ConstraintLayout.LayoutParams
        params.bottomMargin = 0 // 혹은 원하는 값으로 설정
        binding.callAskConstraintLayout.layoutParams = params
    }
    //dp 변환 함수
    private fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPharmacymapBinding.inflate(layoutInflater)
        setContentView(binding.root) //setContentView(R.layout.activity_pharmacymap)

        //var fm: FragmentManager = supportFragmentManager
        //var mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
        var mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?


        //위치권한 상태 확인
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }

        //지도 프래그먼트 초기화
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.map, mapFragment).commit()
        }
        mapFragment?.getMapAsync(this)

        //현재 위치 정보 제공
        mLocationSource = FusedLocationSource(this, PERMISSION_REQUEST_CODE) //현재 액티비티 기반

        // Bottom Sheet 초기화
        val bottomSheetView = binding.persistentBottomSheet //val bottomSheetView: View = findViewById(R.id.persistent_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView) //해당 뷰에 대한 BottomSheetBehavior 객체를 반환
        bottomSheetBehavior.isHideable = true //숨김 여부
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN //숨겨진 상태로 앱 시작
        //bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback) // Bottom Sheet의 콜백 설정



        //검색창 초기화
        val searchView = binding.searchView
        searchView.setOnQueryTextFocusChangeListener { searchView, hasFocus ->
            if(hasFocus) { //포커스를 가지고 있으면
                val intent = Intent(this, HospitalSearchActivity::class.java)
                startActivity(intent)
                searchView.clearFocus()
            }
        }
        //검색창(이미지) 눌렀을때 이벤트 처리
        searchView.setOnClickListener {
            val intent = Intent(this, HospitalSearchActivity::class.java)
            startActivity(intent)
        }


/*
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean { //검색할때마다 바뀜
                setListenerToEditText()
                if(newText.isNotEmpty()) {
                    searchAndClickMarker(newText)
                }
                return true
            }
        })
*/

        pharmacyNameTextView = binding.pharmacyNameTextView //findViewById(R.id.pharmacy_name_textView)
        operatingTimeTextView = binding.operatingTimeTextView //findViewById(R.id.operating_time_textView)
        addressTextView = binding.addressTextView //findViewById(R.id.address_textView)
        button = binding.button //findViewById(R.id.button)

        lunchTimeTextView = binding.lunchTimeTextView
        monTimeTextView = binding.mondayTimeTextView
        tueTimeTextView = binding.tuesdayTimeTextView
        wedTimeTextView = binding.wednesdayTimeTextView
        thuTimeTextView = binding.thursdayTimeTextView
        friTimeTextView = binding.fridayTimeTextView
        satTimeTextView = binding.saturdayTimeTextView
        sunTimeTextView = binding.sundayTimeTextView
        holTimeTextView = binding.dayOffTimeTextView

/*
       //  필터 버튼 클릭 시 이벤트 설정
        findViewById<ExtendedFloatingActionButton>(R.id.filter_btn).setOnClickListener {
            // 필터 상태 업데이트
            isFilterApplied = !isFilterApplied

            // 필터 상태에 따라 플로팅 버튼 배경색 변경
            val filterBtn = findViewById<ExtendedFloatingActionButton>(R.id.filter_btn)
            if (isFilterApplied) {
                filterBtn.backgroundTintList = ColorStateList.valueOf(Color.rgb(128, 128, 128))
            } else {
                filterBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            }

            // 필터링된 마커 표시/숨기기
            updateMarkers2()
        }
*/
    }

    //엔터 눌렀을때 키보드 내려가게
    private fun setListenerToEditText() {
        binding.searchView.setOnKeyListener { view, keyCode, event ->
            //Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                //키패드 내리기
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
                true
            }

            false
        }
    }

    // 검색어와 일치하는 마커를 찾고 클릭하는 함수
    private fun searchAndClickMarker(query: String) {
        val matchedMarker = markers.find { marker ->
            val pharmacy = marker.tag as? NaverMapData
            pharmacy?.pharmacyname?.contains(query, ignoreCase = true) == true
        }

        matchedMarker?.let {
            // 해당 마커 클릭
            it.performClick()
            // 마커 위치로 카메라 이동
            val cameraUpdate = CameraUpdate.scrollTo(it.position)
            mNaverMap.moveCamera(cameraUpdate)
            // Bottom sheet 표시
            val pharmacy = it.tag as NaverMapData
            showBottomSheet(pharmacy, it)
        } ?: run {
            Toast.makeText(this, "일치하는 약국을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 마커 필터링 함수
    private fun updateMarkers2() {
        for (marker in markers) {
            // 마커의 태그로부터 데이터 가져오기
            val pharmacy = marker.tag as? NaverMapData
            // 필터 상태에 따라 마커 표시/숨기기 결정
            if (pharmacy != null) {
                if (isFilterApplied && isPharmacyOpen(pharmacy)) {
                    // 필터가 적용되고 영업중인 경우 마커 표시
                    showMarker(marker)
                } else if (!isFilterApplied) {
                    // 필터가 해제되면 모든 마커 표시
                    showMarker(marker)
                } else {
                    // 필터가 적용되고 영업중이 아닌 경우 마커 숨기기
                    hideMarker(marker)
                }
            }
        }
    }

    private fun isPharmacyOpen(pharmacy: NaverMapData): Boolean {
        val dayOfWeek = getCurrentDayOfWeek()
        val openHours = getOpenHoursForDay(dayOfWeek, pharmacy)
        return openHours.startsWith("영업중")
    }

    //네이버 지도가 준비되었을때 동작
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
            updateMarkers2()
        }

        gpsTracker = GpsTracker(this@PharmacyMapActivity)
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


        val naverMapApiInterface = NaverMapRequest.getClient().create(NaverMapApiInterface::class.java)

        val call: Call<NaverMapItem> = naverMapApiInterface.getMapData(
        "Ir4%2BpxWC%2F21QisgzfSd91jjuivExqDn7QBqSVIVo1MomAVyWz%2BU%2BO7KFciRTutO8FZ76B82IXzZNSB3c3ajqCg%3D%3D",
            "json",
        1000
        )

        // 통신 요청
        call.enqueue(object : Callback<NaverMapItem> {
            override fun onResponse(call: Call<NaverMapItem>, response: Response<NaverMapItem>) {
                if (response.isSuccessful) {
                   naverMapList = response.body()!!
                   naverMapInfo = naverMapList.response.body.items.itemList

                for (item in naverMapInfo) {

                    val lat: Double = item.mapx
                    val lng: Double = item.mapy

                    val marker = Marker()
                    marker.position = LatLng(lat, lng)
                    marker.icon = OverlayImage.fromResource(R.drawable.pill_pin)
                    marker.width = 90
                    marker.height = 90
                    marker.map = mNaverMap
                    markers.add(marker) // 수정된 부분
                    marker.tag = item // 마커에 데이터 추가

                    // 마커 클릭 리스너 설정
                    marker.setOnClickListener { overlay ->
                        if (overlay is Marker) {
                            val pharmacy = overlay.tag as? NaverMapData
                            pharmacy?.let {
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
                    Toast.makeText(this@PharmacyMapActivity, "요청 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NaverMapItem>, t: Throwable) {
                Toast.makeText(this@PharmacyMapActivity, "네트워크 요청 실패: ${t.message}", Toast.LENGTH_SHORT).show()
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
    private fun showBottomSheet(pharmacy: NaverMapData, clickedMarker: Marker) {
        val dayOfWeek = getCurrentDayOfWeek()
        val openHours = getOpenHoursForDay(dayOfWeek, pharmacy)

        // Bottom sheet에 데이터 표시
        pharmacyNameTextView.text = pharmacy.pharmacyname
        operatingTimeTextView.text = openHours
        addressTextView.text = pharmacy.address


        monTimeTextView.text = getOpenHoursForDay(DayOfWeek.MONDAY, pharmacy)
        tueTimeTextView.text = getOpenHoursForDay(DayOfWeek.THURSDAY, pharmacy)
        wedTimeTextView.text = getOpenHoursForDay(DayOfWeek.WEDNESDAY, pharmacy)
        thuTimeTextView.text = getOpenHoursForDay(DayOfWeek.THURSDAY, pharmacy)
        friTimeTextView.text = getOpenHoursForDay(DayOfWeek.FRIDAY, pharmacy)
        satTimeTextView.text = getOpenHoursForDay(DayOfWeek.SATURDAY, pharmacy)
        sunTimeTextView.text = getOpenHoursForDay(DayOfWeek.SUNDAY, pharmacy)
        //lunchTimeTextView.text = getOpenHoursForDay(DayOfWeek.MONDAY, pharmacy)
        //holTimeTextView.text = getOpenHoursForDay(DayOfWeek.MONDAY, pharmacy)


        // 클릭 시 동작 정의
        button.setOnClickListener {
            val phoneNumber = pharmacy.tel
            if (!phoneNumber.isNullOrEmpty()) {
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:$phoneNumber")
                startActivity(dialIntent)
            } else {
                // 전화번호가 없는 경우 사용자에게 메시지 표시
                Toast.makeText(this, "전화번호가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        // 선택된 마커 크기 크게
        clickedMarker.width = 110
        clickedMarker.height = 110
        // Bottom Sheet 표시
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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
    private fun getOpenHoursForDay(dayOfWeek: DayOfWeek, pharmacy: NaverMapData): String {
        val startTime: String
        val closeTime: String

        when (dayOfWeek) {
            DayOfWeek.SUNDAY -> {
                startTime = pharmacy.sunStartTime ?: ""
                closeTime = pharmacy.sunCloseTime ?: ""
            }
            DayOfWeek.MONDAY -> {
                startTime = pharmacy.monStartTime ?: ""
                closeTime = pharmacy.monCloseTime ?: ""
            }
            DayOfWeek.TUESDAY -> {
                startTime = pharmacy.tueStartTime ?: ""
                closeTime = pharmacy.tueCloseTime ?: ""
            }
            DayOfWeek.WEDNESDAY -> {
                startTime = pharmacy.wedStartTime ?: ""
                closeTime = pharmacy.wedCloseTime ?: ""
            }
            DayOfWeek.THURSDAY -> {
                startTime = pharmacy.thuStartTime ?: ""
                closeTime = pharmacy.thuCloseTime ?: ""
            }
            DayOfWeek.FRIDAY -> {
                startTime = pharmacy.friStartTime ?: ""
                closeTime = pharmacy.friCloseTime ?: ""
            }
            DayOfWeek.SATURDAY -> {
                startTime = pharmacy.satStartTime ?: ""
                closeTime = pharmacy.satCloseTime ?: ""
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
            this@PharmacyMapActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this@PharmacyMapActivity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@PharmacyMapActivity,
                    PERMISSION[0]
                )
            ) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(this@PharmacyMapActivity, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG)
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@PharmacyMapActivity, PERMISSION,
                    PERMISSION_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@PharmacyMapActivity, PERMISSION,
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
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this@PharmacyMapActivity)
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

    //
}
