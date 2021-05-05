package com.guvyerhopkins.nautilus.ui.search

import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.guvyerhopkins.nautilus.R
import com.guvyerhopkins.nautilus.network.State
import com.guvyerhopkins.nautilus.ui.detail.ImageDetailActivity

class SearchActivity : AppCompatActivity() {

    /**
     * Todo
     * handle no results
     * handle no internet
     * support portrait and landscape
     * Write documentation
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
        val cardAdapter = CardAdapter { card, imageView ->
            //keep imageview parameter for use with a shared element transition
            startActivity(ImageDetailActivity.createIntent(this, card))
        }

        findViewById<RecyclerView>(R.id.search_rv).apply {
            adapter = cardAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
        searchViewModel.cards.observe(this, { photos ->
            cardAdapter.submitList(photos)
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