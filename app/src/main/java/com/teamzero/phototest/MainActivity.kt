package com.teamzero.phototest

import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //findViewById<R.id.rvImages>
    }

    val out: ArrayList<File> = arrayListOf()

    override fun onResume() {
        super.onResume()
        if (out.isEmpty()) {
            val directoryForFileSaving =
                Environment.getExternalStorageDirectory()
            // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)


            out.addAll(searchFiles(directoryForFileSaving))
            /*for (file in list) {
                getMimeType(file)
            }*/
            val outMetrics = DisplayMetrics()
            // val metrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                val display = display
                display?.getRealMetrics(outMetrics)
            } else {
                @Suppress("DEPRECATION")
                val display = windowManager.defaultDisplay
                @Suppress("DEPRECATION")
                display.getMetrics(outMetrics)
            }

            val size: Int = outMetrics.widthPixels / 3
            val rvImages: RecyclerView = findViewById(R.id.rvImages)
            rvImages.layoutManager = GridLayoutManager(this, 3)
            rvImages.adapter = ImageAdapter(this, size, out)
        }
    }

    private fun searchFiles(rootFile: File): ArrayList<out File> {
        val out: ArrayList<File> = arrayListOf()
        val list = rootFile.listFiles()
        if (list != null && list.isNotEmpty()) {
            for (file in list) {
                if (file.isDirectory) {
                    out.addAll(searchFiles(file))
                } else {
                    val type = getMimeType(file) ?: ""
                    if (/*getMimeType(file).equals("image/jpeg")*/type.contains("image") || type.contains(
                            "video"
                        )
                    ) {
                        out.add(file)
                    }
                }
            }
        }
        return out
    }

    private fun getMimeType(file: File): String? {
        var type: String? = null
        val url = URLEncoder.encode(file.absolutePath, "UTF-8")
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }


}