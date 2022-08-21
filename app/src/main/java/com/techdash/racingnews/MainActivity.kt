package com.techdash.racingnews

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
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
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: NewsAdapter

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        adapter = NewsAdapter(this)
        recycler.adapter = adapter
    }

    private fun getSites() {
        lifecycleScope.launch(Dispatchers.IO) {
            val doc = Jsoup.connect("https://wtf1.com/topics/formula-1").get()
            val links = doc.select("article > a")
            val titles = doc.select("article > header > h1 > a > div")
            for (i in links.indices) {
                Log.d("MainActivity", "WTF1")
                val url = links[i].attr("href")
                if (url.indexOf("/post/") > -1) {
                    val title = titles[i].toString().replace("&nbsp;","")
                    runOnUiThread {
                        adapter.addItem(
                            News(title.substring(6, title.length - 7), "https://www.wtf1.com$url")
                        )
                        recycler.scrollToPosition(0)
                    }
                }
            }
            runOnUiThread {
                progressBar.visibility = View.GONE
            }
        }

        val sites = listOf("https://autosport.com/all/news",
        "https://motorsport.com/all/news")
        lifecycleScope.launch(Dispatchers.IO) {
            for ((iter, site) in sites.withIndex()) {
                val doc = Jsoup.connect(site).get()
                val links = doc.select(
                    "div > div > div > div > div > div > div > div > div > a"
                )
                val seenLinks = TreeSet<String>()
                for (i in links.indices) {

                    val url = links[i].attr("href")
                    if (url.indexOf("news") > -1 && !seenLinks.contains(url)) {
                        Log.d("MainActivity", "Second")
                        seenLinks.add(url)
                        val title = links[i].attr("title")
                        val src = links[i].select("picture > img").attr("src")
                        var responseCode: Int
                        val conf = Bitmap.Config.ARGB_8888 // see other conf types
                        var bmp = Bitmap.createBitmap(10, 10, conf)
                        try {
                            val url1 = URL(src)
                            val con: HttpURLConnection = url1.openConnection() as HttpURLConnection
                            con.doInput = true
                            con.connect()
                            responseCode = con.responseCode
                            if (responseCode == HttpURLConnection.HTTP_OK) {
                                //download
                                val inputStream = con.inputStream
                                bmp = BitmapFactory.decodeStream(inputStream)
                                inputStream.close()
                            }
                        } catch (ex: Exception) {
                            Log.e("Exception", ex.toString())
                        }
                        runOnUiThread {
                            val href: String = if (iter == 0) {
                                "https://autosport.com$url"
                            }else {
                                "https://motorsport.com$url"
                            }
                            adapter.addItem(
                                News(title, href, bmp)
                            )
                        }
                    }
                }
            }
        }

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