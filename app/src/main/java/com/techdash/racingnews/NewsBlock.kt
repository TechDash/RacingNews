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

class NewsBlock(private val context: Context, private val news: ArrayList<News>) : Adapter<NewsBlock.ViewHolder>() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val row = layoutInflater.inflate(R.layout.news, parent, false)
        return ViewHolder(row)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = news[position].title
        holder.thumbNail.setImageResource(R.drawable.f1_logo)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("url", news[position].url)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return news.size
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