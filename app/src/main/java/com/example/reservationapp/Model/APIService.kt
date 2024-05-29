package com.example.reservationapp.Model

import com.example.reservationapp.userMapx
import com.example.reservationapp.userMapy
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart

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

    //Delete /reviews/{review_id}/{reservation_id} -> 병원 리뷰 삭제 ( user 본인만 가능 )
    @DELETE("/reviews/{reviewId}/{reservationId}")
    fun deleteReview(@Path(value="reviewId") reviewId: Long = 0, @Path(value="reservationId") reservationId: Long = 0): Call<Int>

    @GET("/reviews/{hospitalId}/all") //병원의 모든 리뷰 조회 - 비동기 처리
    @Headers("Auth: false")
    fun getHospitalReviewAll(@Path(value="hospitalId") hospitalId: Long = 0): Call<List<HospitalReviewAllResponse>>

    @GET("/reviews/{hospitalId}/all") //병원의 모든 리뷰 조회 - 동기적 처리
    @Headers("Auth: false")
    suspend fun getSyncHospitalReviewAll(@Path(value="hospitalId") hospitalId: Long = 0)

    @POST("/reservations") //병원 예약 (토큰필요)
    fun postReservation(@Body reservation: ReservationRequest): Call<ReservationResponse>

    @DELETE("/reservations/cancel/{reservationId}") //예약 취소 (토큰필요)
    fun deleteReservation(@Path(value="reservationId") reservationId: Long = 0): Call<DeleteReservationResponse>

    @GET("/bot/chat") //챗봇 - 진료과목 가져오기
    @Headers("Auth: false")
    fun getChatBotAnswer(@Query("prompt") prompt: String?= null): Call<ChatBotResponse>

    @Multipart
    @POST("/boards/write") //커뮤니티 게시글 작성하기
    fun postBoard(@Part image: List<MultipartBody.Part>, @Part("boardDto") board: BoardPost): Call<BoardResponse>

    @GET("/boards") //전체 게시글 가져오기
    @Headers("Auth: false")
    fun getAllBoards():Call<AllBoardResponse>

    @POST("/reservations/hospital/confirm") //예약 확정
    fun postConfirmReservation(@Body reservation: ConfirmReservationRequest):Call<ConfirmReservationResponse>

    @POST("/boardlike/{boardId}") //커뮤니티 게시글 좋아요
    fun postBoardLike(@Path(value="boardId") boardId:Long): Call<BoardLikeResponse>

    @GET("/boards/{boardId}") //개별 게시글 가져오기
    @Headers("Auth: false")
    fun getBoaradContent(@Path(value="boardId") boardId:Long): Call<BoardContentResponse>

    @POST("/comments/write/{boardId}") //게시글 댓글 작성
    fun postComment(@Path(value="boardId") boardId:Long, @Body comment: CommentRequest): Call<CommunityCommentRequest>

    @GET("/comments/{boardId}") //게시글 댓글 불러오기
    @Headers("Auth: false")
    fun getComments(@Path(value="boardId") boardId:Long): Call<CommentsRequest>

    @GET("/boardlike/find/{boardId}") //게시글별 좋아요 가져오기
    @Headers("Auth: false")
    fun getBoardLikes(@Path(value="boardId") boardId:Long): Call<BoardLikesResponset>

    @GET("/boardlike/find/user/{boardId}") //유저의 게시글 좋아요 확인
    fun getUserBoardLike(@Path(value="boardId") boardId:Long): Call<UserBoardLikeResponse>

/*
    @GET("/reservations/check") //유저의 예약 조회 (토큰필요)
    suspend fun getUserReservation(): List<UserReservationResponse> //비동기 처리
*/

    @GET("/reservations/check") //유저의 예약 조회 (토큰필요)
    fun getUserReservation(): Call<List<UserReservationResponse>>

    @GET("/tokencheck") //토큰 유효기간 체크
    @Headers("Auth: false")
    fun getTokenCheck(): Call<String>

    @GET("/api/hospitals") //병원 api테이블에서 모든 데이터 가져오기
    @Headers("Auth: false")
    fun getAllHospitlas():Call<ApiHospitalResponse>

    @GET("/users/search/request") //최근 검색어 가져오기 (토큰필요)
    fun getRecentSearchWordList(): Call<RecentSearchWordResponse>

    @POST("/users/search/save/{keyword}") //최근 검색어 저장 (토큰필요)
    fun postRecentSearchWord(@Path(value="keyword") searchWord:String): Call<RecentSearchWordResponseData>

    @DELETE("/users/search/remove/{keyword}") //최근 검색어 삭제 (토큰필요)
    fun deleteRecentSearchWord(@Path(value="keyword") searchWord:String?): Call<RecentSearchWordResponseData>

    @GET("/hospitals/download/{detailId}") //병원 이미지 가져오기
    @Headers("Auth: false")
    fun getHospitalDetailImage(@Path(value="detailId") detailId: Long = 0): Call<List<String>>
}