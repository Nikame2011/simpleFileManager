package com.nikame.sfmanager

import android.content.ContentResolver
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.OnSingleFlingListener
import com.github.chrisbanes.photoview.PhotoView
import java.io.File


//TODO add savedInstance reaction: after flip screen imeges dropdown to first opened

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

//        if (intent.action != null && intent.action == Intent.ACTION_VIEW) {
//            val str = intent.data.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }
//                .toTypedArray()
            val inputUri = intent.data

            files = ArrayList()
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            val cr: ContentResolver = applicationContext.getContentResolver()
            val metaCursor: Cursor? = cr.query(inputUri!!, projection, null, null, null)
            if (metaCursor != null) {
                try {
                    if (metaCursor.moveToFirst()) {

                        val file = File(metaCursor.getString(0))
                        files.add(file)
                    }
                } finally {
                    metaCursor.close()
                }
            }


//            files= ArrayList()
//            files.add(File())
//            number = 0
//            files = MedDocument(str[str.size - 1], intent.data.toString())
//            val str = intent.data.toString().split("/".toRegex()).dropLastWhile { it.isEmpty() }
//                .toTypedArray()
//            files = intent.data/*MedDocument(str[str.size - 1], intent.data.toString())*/
//        } else {
//            files = intent.getSerializableExtra("CODE") as ArrayList<File>
//            number = intent.getIntExtra("SELECTED", 0)
//        }

        //}

        iv = findViewById(R.id.ivPresent2)
        setImage()
    }

    private val listener: OnSingleFlingListener =
        OnSingleFlingListener { e1, e2, velocityX, velocityY ->
            if (iv.scale == iv.minimumScale) {
                if (velocityX > 0) {
                    number--
                    if (number < 0) {
                        number = files.size - 1
                    }
                    setImage()
                } else if (velocityX < 0) {
                    number++
                    if (number == files.size)
                        number = 0
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