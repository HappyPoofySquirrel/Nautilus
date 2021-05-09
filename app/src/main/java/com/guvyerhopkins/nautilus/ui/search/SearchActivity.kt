package com.guvyerhopkins.nautilus.ui.search

import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.guvyerhopkins.nautilus.R
import com.guvyerhopkins.nautilus.core.data.AppDatabase
import com.guvyerhopkins.nautilus.core.network.NetworkState
import com.guvyerhopkins.nautilus.ui.detail.CardDetailActivity
import java.util.*

class SearchActivity : AppCompatActivity() {

    /**
     * Todo
     * Handle no internet
     * Write documentation
     * More Unit tests
     * Night mode
     * Update app icon
     * Implement paging from database instead of seperate columns for each page
     * Loading list item for adapter when next pagination call is being made
     */
    private lateinit var searchViewModel: SearchViewModel

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(findViewById(R.id.toolbar))

        progressBar = findViewById(R.id.search_pb)

        searchViewModel =
            ViewModelProvider(
                this,
                SearchViewModelFactory(AppDatabase.getInstance(this).cardsDao())
            ).get(SearchViewModel::class.java)
        val cardAdapter = CardAdapter { card, imageView ->
            val activityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    imageView,
                    getString(R.string.card_search_transition)
                )
            startActivity(
                CardDetailActivity.createIntent(this, card),
                activityOptionsCompat.toBundle()
            )
        }

        findViewById<RecyclerView>(R.id.search_rv).apply {
            adapter = cardAdapter
            addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }
        searchViewModel.cards.observe(this, { cards ->
            cardAdapter.submitList(cards)
        })

        val editText = findViewById<EditText>(R.id.search_et)
        editText.addTextChangedListener {
            searchViewModel.search(it.toString().toLowerCase(Locale.getDefault()))
        }

        searchViewModel.networkState?.observe(this, {
            progressBar.isVisible = it == NetworkState.LOADING
            if (it == NetworkState.ERROR) {
                Toast.makeText(this, getString(R.string.search_error), Toast.LENGTH_LONG).show()
            } else if (it == NetworkState.ZERORESULTS) {
                Toast.makeText(this, getString(R.string.search_no_results), Toast.LENGTH_LONG)
                    .show()

            }
        })
    }
}