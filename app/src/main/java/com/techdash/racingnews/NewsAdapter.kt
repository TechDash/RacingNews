package com.techdash.racingnews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class NewsAdapter(private val context: Context,
                  private val news: ArrayList<News> = ArrayList()) : Adapter<NewsAdapter.ViewHolder>() {

    private val layoutInflater : LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val row = layoutInflater.inflate(R.layout.news, parent, false)
        return ViewHolder(row)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = news[position].title

        if (news[position].image != null) {
            holder.thumbNail.setImageBitmap(news[position].image)
        }else {
            holder.thumbNail.setImageResource(R.drawable.f1_logo)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("url", news[position].url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return news.size
    }

    fun addItem(newNews: News) {
        news.add(newNews)
        notifyItemInserted(news.size - 1)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val thumbNail: ImageView
        init {
            title = itemView.findViewById(R.id.title)
            thumbNail = itemView.findViewById(R.id.image)
        }
    }
}