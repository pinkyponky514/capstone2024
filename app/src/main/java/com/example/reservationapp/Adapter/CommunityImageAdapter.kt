// CommunityImageAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.CommunityItem
import com.example.reservationapp.R

class CommunityImageAdapter(
    private val itemList: List<CommunityItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<CommunityImageAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_community, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val writer: TextView = itemView.findViewById(R.id.writer)
        private val likes: TextView = itemView.findViewById(R.id.likes)
        private val reviews: TextView = itemView.findViewById(R.id.reviews)
        private val timestamp: TextView = itemView.findViewById(R.id.timestamp) // Timestamp TextView 추가

        fun bind(item: CommunityItem) {
            imageView.setImageBitmap(item.imageResource)
            title.text = item.title
            writer.text = item.writer
            likes.text = item.likes
            reviews.text = item.reviews
            timestamp.text = item.timestamp // timestamp 값을 TextView에 설정
        }
    }
}
