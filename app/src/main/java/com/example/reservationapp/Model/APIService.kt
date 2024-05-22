package com.example.reservationapp.Model

import com.example.reservationapp.userMapx
import com.example.reservationapp.userMapy
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    @POST("/jwt-login/user/join") //환자 회원가입
    @Headers("Auth: false") //토큰을 헤더에 안담을 api는 이렇게 표시
    fun postPatientSignUp(@Body user: PatientSignUpInfoRequest): Call<PatientSignupInfoResponse> //환자 정보 받음

    @POST("/jwt-hospital-login/hospital/join") //병원 회원가입
    @Headers("Auth: false")
    fun postHospitalSignUp(@Body user: HospitalSignUpInfoRequest): Call<HospitalSignupInfoResponse> //병원 정보 받음

    @POST("/jwt-login/login") //환자 로그인
    @Headers("Auth: false")
    fun postPatientLogin(@Body user: UserLoginInfoRequest): Call<String> //Token 받음

    @POST("/jwt-hospital-login/login") //병원 로그인
    @Headers("Auth: false")
    fun postHospitalLogin(@Body user: UserLoginInfoRequest): Call<HospitalUserLoginResponse> //Token 받음

    @GET("/hospitals") //병원 검색
    @Headers("Auth: false")
    fun getSearchHospital(
        @Query("query") query: String ?= null,
        @Query("department") className: String ?= null,
        @Query("mapx") mapx: Double = userMapx,
        @Query("mapy") mapy: Double = userMapy
    ): Call<List<SearchHospital>>

    @POST("/hospitals/hospitaldetail") //병원 상세정보 입력 (토큰필요)
    fun postHospitalDetail(@Body hospital: HospitalDetail2): Call<HospitalDetailResponse>

    @GET("/hospitals/findhospital/{hospitalId}") //특정 병원 상세정보 검색
    @Headers("Auth: false")
    fun getHospitalDetail(@Path(value="hospitalId") hospitalId: Long = 0): Call<HospitalSignupInfoResponse>

    @POST("/bookmarks/{hospitalId}") //병원 즐겨찾기 등록 (토큰필요)
    fun postHospitalBookmark(@Path(value="hospitalId") hospitalId: Long = 0): Call<BookmarkResponse>

    @GET("/bookmarks") //나의 즐겨찾기 병원 가져오기 (토큰필요)
    fun getMyHospitalBookmarkList(): Call<MyBookmarkResponse>

    @GET("/bookmarks/all") //모든 유저의 즐겨찾기 가져오기
    @Headers("Auth: false")
    fun getAllHospitalBookmark(@Query(value="page") page: Int = 0): Call<AllBookmarkResponse>

    @POST("/reviews/{hospitalId}") //리뷰작성 (토큰필요)
    fun postReviewWrite(@Path(value="hospitalId") hospitalId: Long = 0, @Body review: ReviewRequest): Call<Long>

    @POST("/reservations") //병원 예약 (토큰필요)
    fun postReservation(@Body reservation: ReservationRequest): Call<ReservationResponse>

    @GET("/bot/chat") //챗봇 - 진료과목 가져오기
    @Headers("Auth: false")
    fun getChatBotAnswer( @Query("prompt") prompt: String?= null): Call<ChatBotResponse>

    @POST("/boards/write") //게시글 작성하기
    fun postBoard(@Body board: BoardContent): Call<BoardResponse>

    @GET("/boards") //전체 게시글 가져오기
    fun getAllBoards():Call<BoardResponse>
}