package com.techdash.racingnews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler = findViewById(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)

        getSites()

        val dividerItemDecoration = DividerItemDecoration(
            recycler.context,
            (recycler.layoutManager as LinearLayoutManager).orientation
        )
        recycler.addItemDecoration(dividerItemDecoration)
    }

    private fun getSites() {
        Thread {
            val sites = ArrayList<String>()
            val doc = Jsoup.connect("https://wtf1.com").get()
            val links = doc.select("a[href]")
            for (link in links) {
                val href = link.attr("href")
                if (href.indexOf("/post/") > -1) {
                    //sites.add("https://wtf1.com/$href")
                    val title = link.select("h2").toString()
                    if (title.isNotEmpty())
                        sites.add(title.substring(4, title.length-5))
                }
            }
            this.runOnUiThread {
                recycler.adapter = NewsBlock(this, sites)
            }

        }.start()
    }
}