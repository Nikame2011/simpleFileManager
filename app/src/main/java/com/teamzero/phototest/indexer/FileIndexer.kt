package com.teamzero.phototest.indexer

import android.os.Environment
import android.webkit.MimeTypeMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.Serializable
import java.net.URLEncoder
import kotlin.collections.ArrayList

class FileIndexer {

}
//TODO replace all methods in class in "companion object"?

//TODO add class to describe folders in main fragment: class need include list with allowed types files,
// method with search logic and adapters to present items

val imageTypesList = arrayListOf("jpeg", "jpg", "png", "gif")

val audioTypesList = arrayListOf("mp3", "flac", "wav")

val videoTypesList = arrayListOf("mp4", "mkv", "mpeg-4", "avi")

var deferredCounter: Deferred<Array<Int>>? = null

var counter: Array<Int>? = null

var types: Array<ArrayList<String>>? = null

lateinit var indexFiles: Array<ArrayList<DirInfo>>

fun runIndexator() {
    CoroutineScope(Dispatchers.Default).launch {
        types = arrayOf(audioTypesList, imageTypesList, videoTypesList)
        indexFiles= Array(types!!.size){ ArrayList()} //todo add initialize from different size with count from added type Lists
        deferredCounter =
            countFilesTarget(
                Environment.getExternalStorageDirectory(),
                types!!
            )
    }
}

suspend fun getIndexCount(): Array<Int> {
    if (counter == null) {
        if (deferredCounter != null) {
            counter = deferredCounter!!.await()
            //todo add code to save counter in shared preference and load from while app starting - this changes allow visually speed up loading
        } else {
            throw NullPointerException()

        }
    }
    return counter as Array<Int>
}

fun clearIndex(){//TODO add clear Index and updates in main fragment
    counter=null
    runIndexator()
}

private suspend fun countFilesTarget(
    rootFile: File,
    typeList: Array<ArrayList<String>>
): Deferred<Array<Int>> {
    return CoroutineScope(Dispatchers.Default).async {
        val count = Array<Int>(typeList.size) { 0 }
        val previewFile= Array<File?>(typeList.size) { null }
        val defList = arrayListOf<Deferred<Array<Int>>>()
        val list = rootFile.listFiles()
        if (list != null && list.isNotEmpty()) {
            for (file in list) {
                if (file.isDirectory) {
                    defList.add(countFilesTarget(file, typeList))
                } else {
                    for (type in typeList) {
                        if (type.contains(file.extension.lowercase())) {
                            count[typeList.indexOf(type)]++

                            if (previewFile[typeList.indexOf(type)] == null || file.lastModified() > previewFile[typeList.indexOf(type)]!!.lastModified()) {//todo remove if conditions and look how it's work
                                previewFile[typeList.indexOf(type)] = file
                            }
                        }
                    }
                }
            }
        }

        for(i in 0..count.size-1) {
            if (count[i] != 0) {
                val thisFolder = DirInfo(rootFile, rootFile.name, previewFile[i], count[i])
                thisFolder.name += "(${thisFolder.count})"
                sortAndAdd(indexFiles, thisFolder,i)
            }
        }

        for (def in defList) {
            val countAwait = def.await()
            for (i in 0..count.size - 1)
                count[i] += countAwait[i]
        }

        return@async count
    }
}
/*
private fun sortAndAdd(folders: ArrayList<DirInfo>, folder: DirInfo) {
    if (folders.size > 0) {
        for (i in 0..folders.size - 1) {
            if (folders[i].rootFolder.lastModified() < folder.rootFolder.lastModified()) {
                folders.add(i, folder)
                return
            }
        }
    }

    folders.add(folder)
}*/

//todo add deffered to this method and await this when need get indexFiles
@Synchronized
private fun sortAndAdd(folders: Array<java.util.ArrayList<DirInfo>>, folder: DirInfo, index:Int) {
    if (folders[index].size > 0) {
        for (i in 0..folders[index].size - 1) {
            if (folders[index][i].rootFolder.lastModified() < folder.rootFolder.lastModified()) {
                folders[index].add(i, folder)
                return
            }
        }
    }

    folders[index].add(folder)
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

class DirInfo(
    var rootFolder: File,
    var name: String,
    var file: File?,
    var count: Int
): Serializable
