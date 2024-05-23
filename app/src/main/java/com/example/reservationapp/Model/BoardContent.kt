package com.example.reservationapp.Model

import android.widget.EditText
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime


data class BoardPost(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
)
data class BoardContent(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("writer") val writer: String?,
    @SerializedName("boardId") val id: Long,
    @SerializedName("regDate") val regDate: LocalDate,
    @SerializedName("regTime") val regTime: LocalTime

)

data class BoardContentResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: BoardContent
)

data class BoardResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<BoardContent>
)
data class BoardLikeResponse(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: String,
)

data class CommentRequest(
    @SerializedName("content") val content: String
)

data class CommunityCommentRequest(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data :Comment
)

data class CommentsRequest(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<Comment>,
)
data class Comment(
    @SerializedName("content") val content: String,
    @SerializedName("writer") val writer: String,
    @SerializedName("regDate") val regDate: LocalDate,
    @SerializedName("regTime") val regTime:LocalTime
)

data class BoardLikesResponset(
    @SerializedName("success") val success: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<BoardLike>
)
data class BoardLike(
    @SerializedName("boardlikeid")  val boardlikeid:Long,
    @SerializedName("boardid") val boardid: Long,
    @SerializedName("userid") val userid: Long
)
