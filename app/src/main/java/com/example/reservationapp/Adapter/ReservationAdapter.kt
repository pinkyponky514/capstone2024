import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.App
import com.example.reservationapp.Model.ConfirmReservationRequest
import com.example.reservationapp.Model.ConfirmReservationResponse
import com.example.reservationapp.Model.ReservationItem
import com.example.reservationapp.R
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservationAdapter : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    private var reservationList: List<ReservationItem> = emptyList()
    private var selectedDate: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val birthDateTextView: TextView = itemView.findViewById(R.id.birthDateTextView)
        val acceptButton: Button = itemView.findViewById(R.id.acceptButton)
        val cancelButton: Button = itemView.findViewById(R.id.cancelButton)
        val buttonLayout: LinearLayout = itemView.findViewById(R.id.buttonLayout)
        val historyStatusTextView: TextView = itemView.findViewById(R.id.status)

        init {


            // LinearLayout 클릭 이벤트 처리
            itemView.setOnClickListener {
                if (buttonLayout.visibility == View.VISIBLE) {
                    // 버튼이 보이면 숨김
                    buttonLayout.visibility = View.GONE
                } else {
                    // 버튼이 숨겨져 있으면 보이도록 함
                    buttonLayout.visibility = View.VISIBLE
                }
            }


            // 예약 승낙 버튼 클릭 리스너 설정
            acceptButton.setOnClickListener {
                // 클릭된 예약 아이템의 위치(position)을 가져올 수 있습니다.
                val position = adapterPosition
                // 클릭된 예약 아이템을 가져옵니다.
                val selectedItem = reservationList[position]
                // 여기에 승낙 버튼이 클릭되었을 때의 작업을 구현합니다.

                val time = selectedItem.time
                val date = selectedItem.reservationDate

                val confirmReservationRequest = ConfirmReservationRequest(date = date, time = time)
                App.apiService.postConfirmReservation(confirmReservationRequest).enqueue(object:
                    Callback<ConfirmReservationResponse> {
                    override fun onResponse(call: Call<ConfirmReservationResponse>, response: Response<ConfirmReservationResponse>) {
                        if (response.isSuccessful) {
                            val response = response.body()!!
                            historyStatusTextView.text = response.status
                            selectedItem.status = response.status

                            // 상태가 업데이트되었음을 알림
                            notifyItemChanged(position)
                        } else {
                            handleErrorResponse(response)
                            Log.d(
                                "FAILURE Response",
                                "Connect SUCESS, Response FAILURE, body: ${
                                    response.body().toString()
                                }"
                            ) //통신 성공, 응답은 실패

                        }
                    }

                    override fun onFailure(call: Call<ConfirmReservationResponse>, t: Throwable) {
                        Log.d("CONNECTION FAILURE: ", t.localizedMessage) //통신 실패
                    }
                })


            }

            // 예약 취소 버튼 클릭 리스너 설정
            cancelButton.setOnClickListener {
                // 클릭된 예약 아이템의 위치(position)을 가져올 수 있습니다.
                val position = adapterPosition
                // 클릭된 예약 아이템을 가져옵니다.
                val selectedItem = reservationList[position]
                // 여기에 취소 버튼이 클릭되었을 때의 작업을 구현합니다.
            }
        }
    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.hospital_reservation_item, parent, false)
        return ReservationViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val currentItem = reservationList[position]
        holder.timeTextView.text = currentItem.time
        holder.nameTextView.text = currentItem.patientName
        holder.birthDateTextView.text = currentItem.birthDate
        holder.historyStatusTextView.text = currentItem.status

        // 예약된 날짜에 따라 배경색 변경
        if (currentItem.reservationDate == selectedDate) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.reservation_color))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }

        // 여기서 status에 따라 배경색 변경
        if (currentItem.status == "예약확정") {
            holder.historyStatusTextView.setBackgroundResource(R.drawable.style_dark_green_radius_5_status_padding)
        } else if (currentItem.status == "예약신청" || currentItem.status == "예약취소") {
            holder.historyStatusTextView.setBackgroundResource(R.drawable.style_gray_radius_5_status_padding)
        } else if (currentItem.status == "진료완료"){
            holder.historyStatusTextView.setBackgroundResource(R.drawable.alarm_hospital_finish)
        }
    }



    override fun getItemCount(): Int {
        return reservationList.size
    }

    fun updateList(newList: List<ReservationItem>) {
        reservationList = newList
        notifyDataSetChanged()
    }

    private fun handleErrorResponse(response: Response<ConfirmReservationResponse>) {
        val errorBody = response.errorBody()?.string()
        Log.d("FAILURE Response", "Response Code: ${response.code()}, Error Body: $errorBody")
        if (errorBody != null) {
            try {
                val jsonObject = JSONObject(errorBody)
                val timestamp = jsonObject.optString("timestamp")
                val status = jsonObject.optInt("status")
                val error = jsonObject.optString("error")
                val message = jsonObject.optString("message")
                val path = jsonObject.optString("path")
                Log.d("Error Details", "Timestamp: $timestamp, Status: $status, Error: $error, Message: $message, Path: $path")
            } catch (e: JSONException) {
                Log.d("JSON Parsing Error", "Error parsing error body JSON: ${e.localizedMessage}")
            }
        }
    }
}
