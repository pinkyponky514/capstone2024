import com.example.reservationapp.Model.APIService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

public class RetrofitClient {

    private var instance: RetrofitClient? = null
    private var apiService: APIService? = null

    // 사용하고 있는 서버 BASE 주소
    private val baseUrl = "http://10.0.2.2:8080"

    init {
        // 로그를 보기 위한 Interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()

        // Retrofit 설정
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            //.addConverterFactory(MoshiConverterFactory.create())
            .client(client) // 로그 기능 추가
            .build()

        apiService = retrofit.create(APIService::class.java)
    }

    @Synchronized
    fun getInstance(): RetrofitClient {
        if (instance == null) {
            instance = RetrofitClient()
        }
        return instance as RetrofitClient
    }
    fun getRetrofitInterface(): APIService? {
        return apiService
    }

}