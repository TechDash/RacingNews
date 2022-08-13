package com.techdash.racingnews

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.Jsoup


class MainActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var dialog: AlertDialog

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)

        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.splash_screen, null))
        builder.setCancelable(false)
        dialog = builder.create()

        dialog.show()
        getSites()

        val dividerItemDecoration = DividerItemDecoration(
            recycler.context,
            (recycler.layoutManager as LinearLayoutManager).orientation
        )
        recycler.addItemDecoration(dividerItemDecoration)
    }

    private fun getSites() {
        Thread {
            val sites = ArrayList<News>()
            val doc = Jsoup.connect("https://wtf1.com/topics/formula-1").get()
            val links = doc.select("article > a")
            val titles = doc.select("article > header > h1 > a > div")
            for (i in links.indices) {
                val url = links[i].attr("href")
                if (url.indexOf("/post/") > -1) {
                    val title = titles[i].toString()
                    sites.add(News(title.substring(6, title.length - 7), url))
                }
            }
            runOnUiThread {
                recycler.adapter = NewsBlock(this, sites)
                dialog.cancel()
            }

        }.start()
    }
}