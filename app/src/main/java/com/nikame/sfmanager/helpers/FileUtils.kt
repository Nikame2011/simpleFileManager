package com.nikame.sfmanager.helpers

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.nikame.sfmanager.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLEncoder
import java.text.DecimalFormat
import java.util.Locale


class FileUtils {
    companion object {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")

        fun getStringSize(bytesSize: Long): String {
            if (bytesSize <= 0)
                return "0 " + units[0]
            val digitGroups = (Math.log10(bytesSize.toDouble()) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(
                bytesSize / Math.pow(1024.0, digitGroups.toDouble())
            ) + " " + units[digitGroups]
        }
        fun runGlide(context: Context, file: File, view: ImageView) {
            runGlide(context, FileInfo(null,file,file.length(),null),view)
        }

        fun runGlide(context: Context, file: FileInfo, view: ImageView) {
            CoroutineScope(Dispatchers.Main).launch {
                if (file.file.isDirectory) {
                    Glide.with(context).load(R.drawable.folder_empty).into(view)
                } else {
                    val type = getMimeType(context, file.file)//if(file.contentUri!=null) getMimeType(context, file.contentUri!!) else getMimeType(file.file)
                    if (type != null && file.size > 0) {
                        if (type.startsWith("image") || type.startsWith("video")) {
                            Glide.with(context).load(file.file).into(view)
                        } else if (type.startsWith("audio")) {
                            try {
                                val metaRetriver = MediaMetadataRetriever()
                                metaRetriver.setDataSource(file.file.absolutePath)
                                val art = metaRetriver.getEmbeddedPicture()
                                if (art != null) {
                                    Glide.with(context).load(art).into(view)
                                } else {
                                    throw NullPointerException("notHasEmbeddedPicture")
                                }
                            } catch (e: Exception) {
                                Log.e("audio icon error", file.file.absolutePath, e)
                                //Toast.makeText(context, "audio icon error", Toast.LENGTH_LONG).show()
                                Glide.with(context).load(R.drawable.audio_img).into(view)
                            }
                        } else if (type.startsWith("text")) {
                            Glide.with(context).load(R.drawable.text_file).into(view)
                        } else {
                            Glide.with(context).load(R.drawable.file).into(view)
                        }
                    } else {
                        Glide.with(context).load(R.drawable.file).into(view)
                    }
                }
            }
        }

        fun getMimeType(file: File): String? {
            var type: String? = null
            val url = URLEncoder.encode(file.absolutePath, "UTF-8")
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return type
        }

        fun getMimeType(context: Context, file: File): String? {
            var type: String? = null
            val url = FileProvider.getUriForFile(
                context,
                context.getApplicationContext().getPackageName() + ".provider",
                file
            )//URLEncoder.encode(file.absolutePath, "UTF-8")
            val extension = MimeTypeMap.getFileExtensionFromUrl(url.toString())
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return type
        }

        fun getMimeType(context: Context, uri: Uri): String? {
            var mimeType: String? = null
            mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                val cr: ContentResolver = context.applicationContext.getContentResolver()
                cr.getType(uri)

            } else {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                    uri
                        .toString()
                )
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.lowercase(Locale.getDefault())
                )
            }
            return mimeType
        }

        fun tryOpenFile(context: Context, file: FileInfo) {
//val file1=file.parentFile
            try {//todo add multiOpen: if files is media? provider can open to view directory
                val intent = Intent(Intent.ACTION_VIEW, file.contentUri/*Uri.fromFile(file.file)*/)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(
                    file.contentUri
                    /*FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext().getPackageName() + ".provider",
                        file.file
                    )*/, getMimeType(
                        context, FileProvider.getUriForFile(
                            context,
                            context.getApplicationContext().getPackageName() + ".provider",
                            file.file
                        )
                    )//"image/*"
                )
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Не удалось открыть файл!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("fileOpen", file.name, e)
                Toast.makeText(context, "Не удалось открыть файл!", Toast.LENGTH_LONG).show()
            }
        }

        fun tryOpenFile(context: Context, file: File) {
//val file1=file.parentFile
            try {//todo add multiOpen: if files is media? provider can open to view directory
                val intent = Intent(Intent.ACTION_VIEW, Uri.fromFile(file))
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(
                    FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext().getPackageName() + ".provider",
                        file
                    ), getMimeType(
                        context, FileProvider.getUriForFile(
                            context,
                            context.getApplicationContext().getPackageName() + ".provider",
                            file
                        )
                    )//"image/*"
                )
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Не удалось открыть файл!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("fileOpen", file.name, e)
                Toast.makeText(context, "Не удалось открыть файл!", Toast.LENGTH_LONG).show()
            }
        }

        fun tryShareFile(context: Context, file: File) {
//            try {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_STREAM, FileProvider.getUriForFile(
                        context,
                        context.getApplicationContext().getPackageName() + ".provider",
                        file
                    )
                )
                type = getMimeType(file)
            }
            context.startActivity(Intent.createChooser(shareIntent, null))


//                val intent = Intent(Intent.ACTION_SEND, Uri.fromFile(file))
////                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//                if (intent.resolveActivity(context.getPackageManager()) != null) {
//                    context.startActivity(intent)
//                }else {
//                    Toast.makeText(context,"Не удалось открыть файл!", Toast.LENGTH_LONG).show()
//                }
//            }
//            catch (e: Exception) {
//                Log.e("fileOpen", file.name, e)
//                Toast.makeText(context,"Не удалось открыть файл!", Toast.LENGTH_LONG).show()
//            }
        }
    }
}