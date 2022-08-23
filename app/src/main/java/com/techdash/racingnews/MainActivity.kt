package com.techdash.racingnews

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.ProgressBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler = findViewById(R.id.recycler)
        progressBar = findViewById(R.id.progressbar_id)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recycler.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(
            recycler.context,
            (recycler.layoutManager as LinearLayoutManager).orientation
        )

        recycler.addItemDecoration(dividerItemDecoration)
        val parser = HtmlParser(NewsAdapter(this), progressBar)

        lifecycleScope.launch(Dispatchers.IO) {
            parser.getSites()
        }

        recycler.adapter = parser.adapter

        val navView = findViewById<NavigationView>(R.id.navView)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.f1 -> {
                    recycler.adapter = NewsAdapter(this, parser.f1)
                    drawerLayout.closeDrawers()
                    recycler.scrollToPosition(0)
                }
                R.id.home -> {
                    recycler.adapter = parser.adapter
                    drawerLayout.closeDrawers()
                    recycler.scrollToPosition(0)
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
}