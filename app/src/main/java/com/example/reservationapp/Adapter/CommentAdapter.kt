import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservationapp.Model.CommentItem
import com.example.reservationapp.R

class CommentAdapter(private val reviews: MutableList<CommentItem> = mutableListOf()) :
    RecyclerView.Adapter<CommentAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView = itemView.findViewById<TextView>(R.id.contentTextView)
        val writerTextView = itemView.findViewById<TextView>(R.id.writerTextView)
        val timestampTextView = itemView.findViewById<TextView>(R.id.timestampTextView)

        fun bind(review: CommentItem) {
            titleTextView.text = review.title
            writerTextView.text = review.writer
            timestampTextView.text = review.timestamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun addComment(comment: CommentItem) {
        reviews.add(comment)
        notifyDataSetChanged()
    }
}
