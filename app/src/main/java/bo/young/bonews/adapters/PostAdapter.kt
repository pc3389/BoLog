package bo.young.bonews.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bo.young.bonews.R
import bo.young.bonews.activities.PostActivity
import bo.young.bonews.utilities.Constants
import com.amplifyframework.datastore.generated.model.Post
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_post.view.*
import kotlinx.coroutines.*
import java.io.File

class PostAdapter(private val items: ArrayList<Post>, val context: Context, private val profileIdCurrentUser: String) :
        RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.item_post_text_date
        val titleTextView: TextView = view.item_post_text_title
        val commentsTextView: TextView = view.item_post_text_comments
        val imageImageView: ImageView = view.item_post_image_postImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(
                        R.layout.list_item_post,
                        parent,
                        false
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            val date = items[position].date
            holder.dateTextView.text = date
            val title = items[position].title
            holder.titleTextView.text = title
            val comments = items[position].comments
            val numberOfComments = if (comments == null) {
                "0"
            } else {
                items[position].comments.size.toString()
            } + " Comments"
            holder.commentsTextView.text = numberOfComments

            val postId = items[position].id
            val imageNumber = items[position].image
            val image = "$postId-$imageNumber.jpg"
            val filepath = context.cacheDir.toString() + "/$image"
            loadImageFromS3(filepath, holder, position)
            holder.itemView.setOnClickListener {
                val intent = Intent(context, PostActivity::class.java).apply {
                    putExtra(Constants.PROFILE_ID_CURRENTUSER, profileIdCurrentUser)
                    putExtra(Constants.PROFILE_ID, items[position].profile.id)
                    putExtra(Constants.POST_ID, postId)
                }
                context.startActivity(intent)
            }
        }
    }

    private suspend fun loadImageFromS3(filepath: String, holder: ViewHolder, itemNumber: Int) =
            withContext(Dispatchers.Main) {
                val file = File(filepath)
                if (file.exists()) {
                    Glide.with(context)
                            .load(file)
                            .into(holder.imageImageView)
                } else {
                    holder.imageImageView.visibility = View.GONE
                }
            }

    override fun getItemCount(): Int {
        return items.size
    }
}