package com.guvyerhopkins.nautilus.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.guvyerhopkins.nautilus.R
import com.jsibbold.zoomage.ZoomageView

const val IMAGE_URL_KEY = "IMAGE_URL_KEY"

class ImageDetailActivity : AppCompatActivity() {

    companion object {
        fun createIntent(context: Context, url: String): Intent {
            return Intent(context, ImageDetailActivity::class.java).apply {
                putExtra(IMAGE_URL_KEY, url)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_detail)

        val imageView = findViewById<ZoomageView>(R.id.details_iv)
        imageView.load(intent.extras!!.getString(IMAGE_URL_KEY))
    }
}