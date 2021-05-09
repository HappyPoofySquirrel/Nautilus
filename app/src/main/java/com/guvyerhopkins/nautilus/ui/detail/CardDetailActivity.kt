package com.guvyerhopkins.nautilus.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.guvyerhopkins.nautilus.R
import com.guvyerhopkins.nautilus.core.network.Card
import com.squareup.picasso.Picasso

private const val CARD_KEY = "CARD_KEY"

class ImageDetailActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context, card: Card): Intent {
            return Intent(context, ImageDetailActivity::class.java).apply {
                putExtra(CARD_KEY, card)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val card = intent.extras!!.getParcelable<Card>(CARD_KEY)!!

        card.imageUrl?.let {
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(findViewById<ImageView>(R.id.details_iv))
        }

        findViewById<TextView>(R.id.details_name_tv).text =
            getString(R.string.monster_name, card.name)
        findViewById<TextView>(R.id.details_type_tv).text =
            getString(R.string.monster_type, card.type)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === android.R.id.home) {
            supportFinishAfterTransition()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}