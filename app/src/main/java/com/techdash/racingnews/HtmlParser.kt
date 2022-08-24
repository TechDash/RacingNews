package com.techdash.racingnews

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class HtmlParser(val adapter: NewsAdapter, private val progressBar: ProgressBar) {

    val f1 = ArrayList<News>()
    private val wtf1 = ArrayList<News>()
    private val mSport = ArrayList<News>()
    private val gpBlog = ArrayList<News>()

    private suspend fun getSites() {
        coroutineScope {
            launch(Dispatchers.IO) {
                var doc = Jsoup.connect("https://wtf1.com/topics/formula-1").get()
                var links = doc.select("article > a")
                var titles = doc.select("article > header > h1 > a > div")
                for (i in links.indices) {
                    val url = links[i].attr("href")
                    if (url.indexOf("/post/") > -1) {
                        val title = titles[i].toString().replace("&nbsp;","")
                        val news = News(title.substring(6, title.length - 7), "https://www.wtf1.com$url")
                        f1.add(news)
                        wtf1.add(news)
                    }
                }
                doc = Jsoup.connect("https://wtf1.com/topics/formula-1/page/2").get()
                links = doc.select("article > a")
                titles = doc.select("article > header > h1 > a > div")
                for (i in links.indices) {
                    val url = links[i].attr("href")
                    if (url.indexOf("/post/") > -1) {
                        val title = titles[i].toString().replace("&nbsp;","")
                        val news = News(title.substring(6, title.length - 7), "https://www.wtf1.com$url")
                        f1.add(news)
                        wtf1.add(news)
                    }
                }
            }

            launch(Dispatchers.IO) {
                val doc = Jsoup.connect("https://motorsport.com/all/news").get()
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
                                val inputStream = con.inputStream
                                bmp = BitmapFactory.decodeStream(inputStream)
                                inputStream.close()
                            }
                        } catch (ex: Exception) {
                            Log.e("Exception", ex.toString())
                        }
                        val news = News(title, "https://motorsport.com$url", bmp)
                        if (url.indexOf("f1") > -1) {
                            f1.add(news)
                        }
                        mSport.add(news)
                    }
                }
            }

            launch(Dispatchers.IO) {
                val doc = Jsoup.connect("https://gpblog.com/en").get()
                val links = doc.select("main > div > div > div > section > ul > li")
                for (i in links.indices) {
                    val anchor = links[i].select("div > a")
                    val url = anchor.attr("href")
                    if (url.indexOf("news") > -1) {
                        var title = anchor.select("h4").toString()
                        val h4 = title.indexOf("</h4")
                        title = title.substring(13, h4)
                        if (title.indexOf("<span") > -1) {
                            title = title.substring(0, title.indexOf("<span"))
                        }
                        val src = links[i].select("img").attr("src")
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
                                val inputStream = con.inputStream
                                bmp = BitmapFactory.decodeStream(inputStream)
                                inputStream.close()
                            }
                        } catch (ex: Exception) {
                            Log.e("Exception", ex.toString())
                        }
                        bmp = Bitmap.createScaledBitmap(
                            bmp, bmp.width * 2, (bmp.height * 2.2).toInt(), true
                        )
                        val news = News(title, "https://gpblog.com$url", bmp)
                        f1.add(news)
                        gpBlog.add(news)
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
                        wtf1.add(news)
                    }
                }
            }
        }
    }

    suspend fun addSites() {
        coroutineScope {
            getSites()
            for (i in gpBlog.indices) {
                adapter.addItem(gpBlog[i])
                if (i <= wtf1.size - 1) {
                    adapter.addItem(wtf1[i])
                }
                if (i <= mSport.size - 1) {
                    adapter.addItem(mSport[i])
                }
            }
        }
        progressBar.visibility = View.GONE
    }
}