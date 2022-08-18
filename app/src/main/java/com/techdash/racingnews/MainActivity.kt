package com.techdash.racingnews

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.util.concurrent.CountDownLatch

class MainActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var context: Context
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: NewsBlock

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this


        recycler = findViewById(R.id.recycler)
        progressBar = findViewById(R.id.progressbar_id)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recycler.layoutManager = layoutManager


        getSites()

        val dividerItemDecoration = DividerItemDecoration(
            recycler.context,
            (recycler.layoutManager as LinearLayoutManager).orientation
        )
        recycler.addItemDecoration(dividerItemDecoration)
    }

    private fun getSites() {
        val sites = ArrayList<News>()
        val countDownLatch = CountDownLatch(1)
        lifecycleScope.launch(Dispatchers.IO) {
            val doc = Jsoup.connect("https://wtf1.com/topics/formula-1").get()
            val links = doc.select("article > a")
            val titles = doc.select("article > header > h1 > a > div")
            for (i in links.indices) {
                val url = links[i].attr("href")
                countDownLatch.countDown()
                if (url.indexOf("/post/") > -1) {
                    val title = titles[i].toString().replace("&nbsp;","")
                    sites.add(
                        News(title.substring(6, title.length - 7), "https://www.wtf1.com$url")
                    )
                }
            }
            runOnUiThread {
                adapter = NewsBlock(context, sites)
                recycler.adapter = adapter
                progressBar.visibility = View.GONE
            }
        }

        countDownLatch.await()
        lifecycleScope.launch(Dispatchers.IO) {
            val doc = Jsoup.connect("https://wtf1.com/topics/formula-1/page/2").get()
            val links = doc.select("article > a")
            val titles = doc.select("article > header > h1 > a > div")
            for (i in links.indices) {
                val url = links[i].attr("href")
                if (url.indexOf("/post/") > -1) {
                    val title = titles[i].toString().replace("&nbsp;","")
                    runOnUiThread{
                        adapter.addItem(
                            News(title.substring(6, title.length - 7), "https://www.wtf1.com$url")
                        )
                    }
                }
            }
        }
    }
}