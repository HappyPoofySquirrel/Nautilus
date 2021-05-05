package com.guvyerhopkins.nautilus.ui.search

import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.guvyerhopkins.nautilus.R
import com.guvyerhopkins.nautilus.network.State
import com.guvyerhopkins.nautilus.ui.detail.ImageDetailActivity

class SearchActivity : AppCompatActivity() {

    /**
     * Todo Bonus
     * handle no results
     * figure out why there is such a delay from the network request completing to showing any images
     * handle no internet
     * support portrait and landscape
     * Understand the memory profile
     * Write documentation using KDoc
     * More Unit tests
     * Night mode
     * Update app icon
     * Loading list item for adapter when next pagination call is being made
     * Shared element transition for image press to detail view
     */
    private lateinit var searchViewModel: SearchViewModel

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(findViewById(R.id.toolbar))

        progressBar = findViewById(R.id.search_pb)

        searchViewModel =
            ViewModelProvider(this, SearchViewModelFactory()).get(SearchViewModel::class.java)
        val adapter = CardAdapter { url, imageView ->
            //keep imageview parameter for use with a shared element transition
            startActivity(ImageDetailActivity.createIntent(this, url))
        }
        val recyclerView = findViewById<RecyclerView>(R.id.search_rv)
        recyclerView.adapter = adapter

        searchViewModel.cards.observe(this, { photos ->
            adapter.submitList(photos)
        })

        val editText = findViewById<EditText>(R.id.search_et)
        editText.addTextChangedListener {
            searchViewModel.search(it.toString())
        }

        searchViewModel.networkState?.observe(this, {
            progressBar.isVisible = it == State.LOADING
        })
    }
}