package com.techdash.racingnews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class HtmlParser(val adapter: NewsAdapter, private val progressBar: ProgressBar) {
    val f1 = ArrayList<News>()
    suspend fun getSites() {
        coroutineScope {
            launch(Dispatchers.IO) {
                val doc = Jsoup.connect("https://wtf1.com/topics/formula-1").get()
                val links = doc.select("article > a")
                val titles = doc.select("article > header > h1 > a > div")
                for (i in links.indices) {
                    val url = links[i].attr("href")
                    if (url.indexOf("/post/") > -1) {
                        val title = titles[i].toString().replace("&nbsp;","")
                        val news = News(title.substring(6, title.length - 7), "https://www.wtf1.com$url")
                        f1.add(news)
                        launch(Dispatchers.Main) {
                            adapter.addItem(news)
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }

            val sites = listOf("https://autosport.com/all/news",
                "https://motorsport.com/all/news")
            launch(Dispatchers.IO) {
                for ((iter, site) in sites.withIndex()) {
                    val doc = Jsoup.connect(site).get()
                    val links = doc.select(
                        "div > div > div > div > div > div > div > div > div > a"
                    )
                    val seenLinks = TreeSet<String>()
                    for (i in links.indices) {

                        val url = links[i].attr("href")
                        if (url.indexOf("news") > -1 && !seenLinks.contains(url)) {
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
                            val href: String = if (iter == 0) {
                                "https://autosport.com$url"
                            }else {
                                "https://motorsport.com$url"
                            }
                            val news = News(title, href, bmp)
                            if (url.indexOf("f1") > -1)
                                f1.add(news)
                            launch(Dispatchers.Main) {
                                adapter.addItem(news)
                            }
                        }
                        }
                    }
                }

            launch(Dispatchers.IO) {
                val doc = Jsoup.connect("https://wtf1.com/topics/formula-1/page/2").get()
                val links = doc.select("article > a")
                val titles = doc.select("article > header > h1 > a > div")
                for (i in links.indices) {
                    val url = links[i].attr("href")
                    if (url.indexOf("/post/") > -1) {
                        val title = titles[i].toString().replace("&nbsp;","")
                        val news = News(title.substring(6, title.length - 7), "https://www.wtf1.com$url")
                        f1.add(news)
                       launch(Dispatchers.Main) { adapter.addItem(news) }
                    }
                }
            }
        }
    }
}
