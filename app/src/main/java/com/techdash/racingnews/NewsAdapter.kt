package com.techdash.racingnews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NewsAdapter(private val context: Context,
                  private val db: FirebaseFirestore,
                  private val options: FirestorePagingOptions<News>)
  : FirestorePagingAdapter<News, NewsAdapter.ViewHolder>(options) {

  private val layoutInflater : LayoutInflater = LayoutInflater.from(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val row = layoutInflater.inflate(R.layout.news, parent, false)
    return ViewHolder(row)
  }

  /*override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(mNews[position], context, db)
  }

  override fun getItemCount(): Int {
    return mNews.size
  }*/

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val title: TextView
    private val thumbNail: ImageView

    init {
      title = itemView.findViewById(R.id.title)
      thumbNail = itemView.findViewById(R.id.image)
    }

    fun bind(news: News?, context: Context, db: FirebaseFirestore) {
      if (news != null) {
        title.text = news.title

        Glide.with(thumbNail.context)
          .load(news.image).into(thumbNail)

        itemView.setOnClickListener {
          val intent = Intent(context, WebActivity::class.java)
          intent.putExtra("url", news.url)
          context.startActivity(intent)

          db.collection("news").document(news.title)
            .update("opened", FieldValue.increment(1))

          db.collection("f1").document(news.title).get()
            .addOnCompleteListener { doc ->
              if (doc.result.exists()) {
                db.collection("f1").document(news.title)
                  .update("opened", FieldValue.increment(1))
              }
            }
        }
      }
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int, model: News) {
    holder.bind(model, context, db)
  }
}