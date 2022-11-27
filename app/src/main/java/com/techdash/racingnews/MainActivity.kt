package com.techdash.racingnews

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

open class MainActivity : AppCompatActivity() {

  private lateinit var recycler: RecyclerView
  private lateinit var progressBar: ProgressBar
  private lateinit var toggle: ActionBarDrawerToggle
  private lateinit var mFirestore: FirebaseFirestore
  private lateinit var mAdapter: NewsAdapter
  private lateinit var layoutManager: LinearLayoutManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    FirebaseFirestore.setLoggingEnabled(true)
    mFirestore = FirebaseFirestore.getInstance()

    initRecyclerView("news")

    val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
    toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
    drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val navView = findViewById<NavigationView>(R.id.navView)
    navView.setNavigationItemSelectedListener {
      when (it.itemId) {
        R.id.f1 -> {
          initRecyclerView("f1")
          drawerLayout.closeDrawers()
          recycler.scrollToPosition(0)
        }
        R.id.home -> {
          initRecyclerView("news")
          drawerLayout.closeDrawers()
          recycler.scrollToPosition(0)
        }
      }
      true
    }
  }

  private fun initRecyclerView(category: String) {
    recycler = findViewById(R.id.recycler)
    progressBar = findViewById(R.id.progressbar_id)

    val config = PagingConfig(20, 10, false)
    val query = if (category != "news") {
      mFirestore.collection("news").orderBy("addedDate", Query.Direction.DESCENDING)
        .whereEqualTo("category", category)
    } else {
      mFirestore.collection("news").orderBy("addedDate", Query.Direction.DESCENDING)
    }

    val options = FirestorePagingOptions.Builder<News>()
      .setLifecycleOwner(this)
      .setQuery(query, config, News::class.java)
      .build()

    mAdapter = NewsAdapter(this, mFirestore, options)

    layoutManager = LinearLayoutManager(this)
    recycler.layoutManager = layoutManager

    val dividerItemDecoration = DividerItemDecoration(
      recycler.context,
      (recycler.layoutManager as LinearLayoutManager).orientation
    )

    recycler.addItemDecoration(dividerItemDecoration)
    recycler.adapter = mAdapter

    progressBar.visibility = View.GONE
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (toggle.onOptionsItemSelected(item))
      return true
    return super.onOptionsItemSelected(item)
  }
}