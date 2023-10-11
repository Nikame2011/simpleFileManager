package com.teamzero.phototest

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.OnSingleFlingListener
import java.io.File
import com.github.chrisbanes.photoview.PhotoView

class FullscreenActivity : AppCompatActivity() {
    private lateinit var files: ArrayList<File>
    private lateinit var iv: PhotoView
    private var number: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        /*if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            files = intent.getParcelableArrayListExtra("CODE", File::class.java) as ArrayList<File>
        }
        else {*/
        files = intent.getSerializableExtra("CODE") as ArrayList<File>
        //}

        number = intent.getIntExtra("SELECTED", 0)
        iv = findViewById(R.id.ivPresent2)
        setImage()
    }

    private val listener: OnSingleFlingListener =
        OnSingleFlingListener { e1, e2, velocityX, velocityY ->
            if (iv.scale == iv.minimumScale) {
                if (velocityX > 0) {
                    number--
                    if (number<0){
                        number=files.size-1
                    }
                    setImage()
                }
                else if (velocityX < 0) {
                    number++
                    if(number==files.size)
                        number=0
                    setImage()
                }
            }
            true
        }

    private fun setImage() {
        val file: File =
            files[number]
        Glide.with(iv).load(file).into(iv)
        iv.setOnSingleFlingListener(listener)
    }
}