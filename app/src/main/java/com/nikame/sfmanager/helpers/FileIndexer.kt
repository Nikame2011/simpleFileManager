package com.nikame.sfmanager.helpers


import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File


class FileIndexer {
    //Механизм взаимодействия должен быть таким:
    // Активити передаёт сюда листенер для отслеживания обновлений индексов.
    // потом активити запрашивает текущие индексы: если есть уже обновлённые, то передаются они.
    // иначе, если есть устаревшие, загруженные из сохранения - передаём их. иначе ничего не передаём
    //

    companion object {

//TODO add class to describe folders in main fragment: class need include list with allowed types files,
// method with search logic and adapters to present items

//        val imageTypesList = arrayListOf("jpeg", "jpg", "png", "gif")
//
//        val audioTypesList = arrayListOf("mp3", "flac", "wav", "wma")
//
//        val videoTypesList = arrayListOf("mp4", "mkv", "mpeg-4", "avi")

        val documentsTypesList = arrayListOf("txt", "pdf")
//        var deferredCounter: Deferred<Array<Int>>? = null

//        var deferredIndexation: Deferred<Array<TypeDescriptor>>? = null
//        var deferredSavedIndexation: Deferred<Array<TypeDescriptor>>? = null

        var hmDeferredIndexation: HashMap<String, Deferred<TypeDescriptor>> = HashMap()

        var hmIndexes: HashMap<String, TypeDescriptor> = HashMap()

//        var counter: Array<Int>? = null

//        var indexes: Array<TypeDescriptor>? = null

//        var savedIndexes: Array<TypeDescriptor>? = null

//        var types: Array<ArrayList<String>>? = null
//
//        var descriptors: Array<TypeDescriptor>? = null

//        lateinit var indexFiles: Array<ArrayList<DirInfo>>

//        /**
//         * быстрый запуск индексатора, для случаев, когда нет необходимости получить его результат сразу же.
//         * Запуск пройдёт в отдельном потоке, о его результате сообщено не будет
//         */
//        fun runIndexationAsync() {
//            CoroutineScope(Dispatchers.Default).launch {
//
//                runIndexation().await()
//            }
//        }
//
//        /**
//         * стандартный запуск индексатора, для случаев, когда необходимо получить результат и работать с ним.
//         * Возвращает Deferred, который будет выполнен, когда индексация запущена. после этого можно спокойно запускать getIndexes
//         */
//        suspend fun runIndexation(): Deferred<Boolean> {
//            //todo maybe need use globalScope?
//            return CoroutineScope(Dispatchers.Default).async {
//                types = arrayOf(audioTypesList, imageTypesList, videoTypesList, documentsTypesList)
////                indexFiles =
////                    Array(types!!.size) { ArrayList() } //todo add initialize from different size with count from added type Lists
////                deferredCounter =//todo remove this
////                    countFilesTarget(
////                        Environment.getExternalStorageDirectory(),
////                        types!!
////                    )
//
//                descriptors = arrayOf(
//                    TypeDescriptor(audioTypesList),
//                    TypeDescriptor(imageTypesList),
//                    TypeDescriptor(videoTypesList),
//                    TypeDescriptor(documentsTypesList)
//                )
//
//                deferredIndexation = runDescriptorIndexation(
//                    Environment.getExternalStorageDirectory(),
//                    descriptors!!
//                )
//                return@async true
//            }
//        }


        /**
         * быстрый запуск индексатора, для случаев, когда нет необходимости получить его результат сразу же.
         * Запуск пройдёт в отдельном потоке, о его результате сообщено не будет
         */
        fun runIndexationNewAsync(appContext: Context) {
            CoroutineScope(Dispatchers.Default).launch {
                runIndexationNew(appContext).await()
            }
        }

        /**
         * стандартный запуск индексатора, для случаев, когда необходимо получить результат и работать с ним.
         * Возвращает Deferred, который будет выполнен, когда индексация запущена. после этого можно спокойно запускать getIndexes
         */
        private fun runIndexationNew(appContext: Context): Deferred<Boolean> {
            //todo maybe need use globalScope?
            return CoroutineScope(Dispatchers.Default).async {


                hmDeferredIndexation.put("Audio", index(appContext, "Audio"))
                hmDeferredIndexation.put("Video", index(appContext, "Video"))
                hmDeferredIndexation.put("Images", index(appContext, "Images"))


                hmDeferredIndexation.put(
                    "Documents", runDescriptorIndexationSingle(
                        Environment.getExternalStorageDirectory(),
                        TypeDescriptor(documentsTypesList)
                    )
                )


//                hmDeferredIndexation.put("Documents", index(appContext, "Documents"))
//                hmDeferredIndexation.put("Audio",)
                return@async true
            }
        }

//        suspend fun getIndexCount(): Array<Int> {
//            if (counter == null) {
//                if (deferredCounter != null) {
//                    counter = deferredCounter!!.await()
//                    //todo add code to save counter in shared preference and load from while app starting - this changes allow visually speed up loading
//                } else {
//                    throw NullPointerException()
//
//                }
//            }
//            return counter as Array<Int>
//        }

//        class Columns(columnId: String){
//           var columnIndex: String = ""
//        }

        fun index(appContext: Context, type: String): Deferred<TypeDescriptor> {
//            if (type.equals("Images")) {
            return CoroutineScope(Dispatchers.Default).async {

                val descriptor = TypeDescriptor()

//                var selection:String?=null
//                var selectionArgs :Array<String>? =null

                val collection: Uri
                if (type.equals("Images")) {
                    collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                } else if (type.equals("Video")) {
                    collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
//                    } else if (type.equals("Downloads")) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL)
//                        } else {
//                            MediaStore.Downloads.EXTERNAL_CONTENT_URI
//                        }
//                    } else if (type.equals("Documents")) {
//                        collection =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
//                        } else {
//                            MediaStore.Files.getContentUri("external")
//                        }
//
//                        selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
//                        selectionArgs = arrayOf(
//                            MediaStore.Files.FileColumns.MEDIA_TYPE_DOCUMENT.toString()
//                        )
                } else {
                    collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                    } else {
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }

                val projection = arrayOf(
                    MediaStore.MediaColumns._ID,
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    MediaStore.MediaColumns.SIZE,
                    MediaStore.MediaColumns.DATA
                )

// Show only Imagess that are at least 5 minutes in duration.
//                val selection = "${MediaStore.Images.Media.DURATION} >= ?"
//                val selectionArgs = arrayOf(
//                    TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
//                )

// Display Imagess in alphabetical order based on their display name.
                val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} ASC"

                val query = appContext.applicationContext?.contentResolver?.query(
                    collection,
                    projection,
                    null,
                    null,
//                    selection,
//                    selectionArgs,
                    sortOrder
                )
                query?.use { cursor ->
                    // Cache column indices.
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                    val nameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
//                    val durationColumn =
//                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)
                    val dataColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)


                    while (cursor.moveToNext()) {
                        // Get values of columns for a given Images.
                        val id = cursor.getLong(idColumn)
                        val name = cursor.getString(nameColumn)
//                        val duration = cursor.getInt(durationColumn)
                        val size = cursor.getInt(sizeColumn)
                        val data = cursor.getString(dataColumn)

                        val contentUri: Uri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
//                        if (contentUri.path!!.contains("content")) {
//
//                        }
                        val file = File(data)
                        //val file = File(data)
                        if (!descriptor.hmDirectories.contains(file.parentFile!!.name)) {
                            descriptor.hmDirectories.put(
                                file.parentFile!!.name,
                                DirInfo(file.parentFile!!, file.parentFile!!.name, file, 0, 0)
                            )
                        }
                        descriptor.hmDirectories.get(file.parentFile!!.name)?.size =
                            descriptor.hmDirectories.get(file.parentFile!!.name)?.size!! + file.length()
                        descriptor.hmDirectories.get(file.parentFile!!.name)?.count =
                            descriptor.hmDirectories.get(file.parentFile!!.name)?.count!! + 1
                        descriptor.hmDirectories.get(file.parentFile!!.name)?.listFiles?.add(
                            FileInfo(name, file, size.toLong(), contentUri)
                        )
                        descriptor.size += file.length()
                        descriptor.count++
                        // Stores column values and the contentUri in a local object
                        // that represents the media file.
//                            Log.e("fileeeeeImages","${contentUri}, ${name},  ${size}, ${data}")//${duration},
//                        ImagesList += Images(contentUri, name, duration, size)
                    }
                }

                return@async descriptor
            }
//            } else if (type.equals("Video")) {
//                return CoroutineScope(Dispatchers.Default).async {
//                    val descriptor = TypeDescriptor()
//
//                    val collection =
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            MediaStore.Images.Media.getContentUri(
//                                MediaStore.VOLUME_EXTERNAL
//                            )
//                        } else {
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                        }
//
//                    val projection = arrayOf(
//                        MediaStore.Images.Media._ID,
//                        MediaStore.Images.Media.DISPLAY_NAME,
//                        MediaStore.Images.Media.SIZE,
//                        MediaStore.Video.Media.DURATION,
//                        MediaStore.Images.Media.DATA
//                    )
//
//// Show only Imagess that are at least 5 minutes in duration.
////                val selection = "${MediaStore.Images.Media.DURATION} >= ?"
////                val selectionArgs = arrayOf(
////                    TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
////                )
//
//// Display Imagess in alphabetical order based on their display name.
//                    val sortOrder = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
//
//                    val query = appContext.applicationContext?.contentResolver?.query(
//                        collection,
//                        projection,
//                        null,
//                        null,
//                        sortOrder
//                    )
//                    query?.use { cursor ->
//                        // Cache column indices.
//                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//                        val nameColumn =
//                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
//                        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
//                        val dataColumn =
//                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                        val durationColumn =
//                            cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
//
//                        while (cursor.moveToNext()) {
//                            // Get values of columns for a given Images.
//                            val id = cursor.getLong(idColumn)
//                            val name = cursor.getString(nameColumn)
//                            val duration = cursor.getInt(durationColumn)
//                            val size = cursor.getInt(sizeColumn)
//                            val data = cursor.getString(dataColumn)
//
//                            val contentUri: Uri = ContentUris.withAppendedId(
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                id
//                            )
//                            val file = File(data)
//                            if (!descriptor.hmDirectories.contains(file.absolutePath)) {
//                                descriptor.hmDirectories.put(
//                                    file.absolutePath,
//                                    DirInfo(file.parentFile, file.absolutePath, null, 0, 0)
//                                )
//                            }
//                            descriptor.hmDirectories.get(file.absolutePath)?.size = file.length()
//                            descriptor.hmDirectories.get(file.absolutePath)?.count =
//                                descriptor.hmDirectories.get(file.absolutePath)?.count!! + 1
//                            descriptor.hmDirectories.get(file.absolutePath)?.listFiles?.add(
//                                FileInfo(name, file, size)
//                            )
//                            descriptor.size += file.length()
//                            descriptor.count++
//                            // Stores column values and the contentUri in a local object
//                            // that represents the media file.
////                            Log.e("fileeeeeImages","${contentUri}, ${name},  ${size}, ${data}")//${duration},
////                        ImagesList += Images(contentUri, name, duration, size)
//                        }
//                    }
//
//                    return@async descriptor
//                }
//            } else {
//                return CoroutineScope(Dispatchers.Default).async {
//                    val descriptor: TypeDescriptor = TypeDescriptor()
//                    return@async descriptor
//                }
//            }
        }

//        suspend fun getSavedIndexes(): Array<TypeDescriptor> {
//            if (savedIndexes == null) {
//                if (deferredSavedIndexation != null) {
//                    savedIndexes = deferredSavedIndexation!!.await()
//                } else {
//                    throw NullPointerException()
//                }
//            }
//            return savedIndexes as Array<TypeDescriptor>
//        }

//        suspend fun getIndexes(): Array<TypeDescriptor> {
//            if (indexes == null) {
//                if (deferredIndexation != null) {
//                    indexes = deferredIndexation!!.await()
//                    //todo add code to save counter in shared preference and load from while app starting - this changes allow visually speed up loading
//                } else {
//                    runIndexation().await()
//                    return getIndexes()
////                    throw NullPointerException()
//
//                }
//            }
//            return indexes as Array<TypeDescriptor>
//        }

        suspend fun getIndexes(appContext: Context, key: String): TypeDescriptor? {
            //todo нужно разделить индексы - хранить их в хешмапе по типу файлов, а не в листе
            // получать их тоже нужно по индексу. деферред тоже нужно получать по типу файлов
            // и по мере получения нужно складывать в хешмап
            if (!hmIndexes.containsKey(key) || hmIndexes.get(key) == null) {
                if (hmDeferredIndexation.get(key) != null) {
                    hmIndexes.put(key, hmDeferredIndexation.get(key)!!.await())
                    //todo add code to save counter in shared preference and load from while app starting - this changes allow visually speed up loading
                } else {
                    runIndexationNew(appContext).await()
                    return getIndexes(appContext, key)
//                    throw NullPointerException()
                }
            }
            return hmIndexes.get(key)
        }

//        fun clearIndex() {//TODO add clear Index and updates in main fragment
//            counter = null
////            runIndexation()
//        }

//        private suspend fun countFilesTarget(
//            rootFile: File,
//            typeList: Array<ArrayList<String>>
//        ): Deferred<Array<Int>> {
//            return CoroutineScope(Dispatchers.Default).async {
//                val count = Array<Int>(typeList.size) { 0 }
//                val size = Array<Long>(typeList.size) { 0 }
//                val previewFile = Array<File?>(typeList.size) { null }
//                val defList = arrayListOf<Deferred<Array<Int>>>()
//                val list = rootFile.listFiles()
//                if (list != null && list.isNotEmpty()) {
//                    for (file in list) {
//                        if (file.isDirectory) {
//                            defList.add(countFilesTarget(file, typeList))
//                        } else {
//                            for (type in typeList) {
//                                if (type.contains(file.extension.lowercase())) {
//                                    count[typeList.indexOf(type)]++
//                                    size[typeList.indexOf(type)] += file.length()
//                                    if (previewFile[typeList.indexOf(type)] == null || file.lastModified() > previewFile[typeList.indexOf(
//                                            type
//                                        )]!!.lastModified()
//                                    ) {//todo remove if conditions and look how it's work
//                                        previewFile[typeList.indexOf(type)] = file
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//                for (i in 0..count.size - 1) {
//                    if (count[i] != 0) {
//                        val thisFolder =
//                            DirInfo(rootFile, rootFile.name, previewFile[i], count[i], size[i])
//                        thisFolder.name += "(${thisFolder.count})"
//                        sortAndAdd(indexFiles, thisFolder, i)
//                    }
//                }
//
//                for (def in defList) {
//                    val countAwait = def.await()
//                    for (i in 0..count.size - 1) {
//                        count[i] += countAwait[i]
//                    }
//                }
//
//                return@async count
//            }
//        }

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

//        //todo add deffered to this method and await this when need get indexFiles
//        @Synchronized
//        private fun sortAndAdd(
//            folders: Array<java.util.ArrayList<DirInfo>>,
//            folder: DirInfo,
//            index: Int
//        ) {
//            if (folders[index].size > 0) {
//                for (i in 0..folders[index].size - 1) {
//                    if (folders[index][i].rootFolder.lastModified() < folder.rootFolder.lastModified()) {
//                        folders[index].add(i, folder)
//                        return
//                    }
//                }
//            }
//
//            folders[index].add(folder)
//        }
//
//        fun getMimeType(file: File): String? {
//            var type: String? = null
//            val url = URLEncoder.encode(file.absolutePath, "UTF-8")
//            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
//            if (extension != null) {
//                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
//            }
//            return type
//        }

        private suspend fun runDescriptorIndexationSingle(
            rootFile: File, descriptor: TypeDescriptor
        ): Deferred<TypeDescriptor> {
            return CoroutineScope(Dispatchers.Default).async {

                var count = 0
                var size: Long = 0
//                var previewFile: File? = null
                val currentList = ArrayList<FileInfo>()

                val defList = arrayListOf<Deferred<TypeDescriptor>>()

                val list = rootFile.listFiles()
                if (list != null && list.isNotEmpty()) {
                    for (file in list) {
                        if (file.isDirectory) {
                            defList.add(runDescriptorIndexationSingle(file, descriptor))
                        } else {
                            if (descriptor.extensions.contains(file.extension.lowercase())) {
                                count++
                                size += file.length()
//                                if (previewFile == null || file.lastModified() > previewFile.lastModified()) {//todo remove if conditions and look how it's work
//                                    previewFile = file
//                                }
                                currentList.add(FileInfo(file.name, file, file.length(), null))
                            }
                        }
                    }
                }


                for (def in defList) {
                    def.await()
                    /*for (i in 0..count.size - 1) {
                        count[i] += countAwait[i]
                    }*/
                }

                if (count != 0) {
                    val thisFolder = DirInfo(
                        rootFile,
                        rootFile.name,
                        /* if(currentList.size>0) currentList.get(0).file else*/ null,//previewFile,
                        count,
                        size
                    )

                    thisFolder.listFiles = currentList
                    //sortAndAdd(indexFiles, thisFolder,i)

                    if (descriptor.directories.size > 0) {
                        var isAdded = false
                        for (ii in 0 until descriptor.directories.size) {
                            if (descriptor.directories[ii].lastModified() < thisFolder.rootFolder.lastModified()) {
                                descriptor.directories.add(ii, thisFolder)
                                isAdded = true
                                break
                            }
                        }
                        if (!isAdded)
                            descriptor.directories.add(thisFolder)
                    } else {
                        descriptor.directories.add(thisFolder)
                    }
                    descriptor.count += count
                    descriptor.size += size
                }

                return@async descriptor
            }
        }

//        private suspend fun runDescriptorIndexation(
//            rootFile: File, descriptor: Array<TypeDescriptor>
//        ): Deferred<Array<TypeDescriptor>> {
//            return CoroutineScope(Dispatchers.Default).async {
//
//                val count = Array<Int>(descriptor.size) { 0 }
//                val size = Array<Long>(descriptor.size) { 0 }
//                val previewFile = Array<File?>(descriptor.size) { null }
//
//                val defList = arrayListOf<Deferred<Array<TypeDescriptor>>>()
//
//                val list = rootFile.listFiles()
//                if (list != null && list.isNotEmpty()) {
//                    for (file in list) {
//                        if (file.isDirectory) {
//                            defList.add(runDescriptorIndexation(file, descriptor))
//                        } else {
//                            for (i in descriptor.indices) {
//                                if (descriptor[i].extensions.contains(file.extension.lowercase())) {
//                                    count[i]++
//                                    size[i] += file.length()
//                                    if (previewFile[i] == null || file.lastModified() > previewFile[i]!!.lastModified()) {//todo remove if conditions and look how it's work
//                                        previewFile[i] = file
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//
//
//                for (def in defList) {
//                    def.await()
//                    /*for (i in 0..count.size - 1) {
//                        count[i] += countAwait[i]
//                    }*/
//                }
//
//                for (i in descriptor.indices) {
//                    if (count[i] != 0) {
//                        val thisFolder = DirInfo(
//                            rootFile,
//                            rootFile.name + "(${count[i]})",
//                            previewFile[i],
//                            count[i],
//                            size[i]
//                        )
//                        //sortAndAdd(indexFiles, thisFolder,i)
//
//                        if (descriptor[i].directories.size > 0) {
//                            var isAdded = false
//                            for (ii in 0 until descriptor[i].directories.size) {
//                                if (descriptor[i].directories[ii].lastModified() < thisFolder.rootFolder.lastModified()) {
//                                    descriptor[i].directories.add(ii, thisFolder)
//                                    isAdded = true
//                                    break
//                                }
//                            }
//                            if (!isAdded)
//                                descriptor[i].directories.add(thisFolder)
//                        } else {
//                            descriptor[i].directories.add(thisFolder)
//                        }
//                        descriptor[i].count += count[i]
//                        descriptor[i].size += size[i]
//                    }
//                }
//
//                return@async descriptor
//            }
//        }
    }
}

class TypeDescriptor() {
    constructor(extensions: java.util.ArrayList<String>) : this() {
        this.extensions = extensions
    }

    var count: Int = 0
    var size: Long = 0
    var directories = arrayListOf<DirInfo>()
    var hmDirectories = HashMap<String, DirInfo>()//String - root url
    lateinit var extensions: ArrayList<String>

    fun getDirectoriies(): ArrayList<DirInfo> {
        if (directories.isEmpty() && hmDirectories.isNotEmpty()) {
            for (key in hmDirectories.keys)
                directories.add(hmDirectories.get(key)!!)
        }
        return directories
    }
}

class DirInfo(
    var rootFolder: File,
    var name: String,
    var file: File?,
    var count: Int,
    var size: Long,
) : Parcelable {
    fun lastModified(): Long = rootFolder.lastModified()
    var listFiles = arrayListOf<FileInfo>()

    constructor(parcel: Parcel) : this(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readSerializable(ClassLoader.getSystemClassLoader(), File::class.java) as File
        } else {
            parcel.readSerializable() as File
        },
        parcel.readString()!!,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readSerializable(ClassLoader.getSystemClassLoader(), File::class.java) as File
        } else {
            parcel.readSerializable() as File
        },
        parcel.readInt(),
        parcel.readLong()
    ) {
        listFiles = ArrayList()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readList(listFiles, ClassLoader.getSystemClassLoader(), FileInfo::class.java)
        } else {
            parcel.readList(listFiles, FileInfo::class.java.classLoader)
        }

//        listFiles= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            parcel.readArrayList(ClassLoader.getSystemClassLoader(), File::class.java) as ArrayList<FileInfo>
//        } else {parcel.readArrayList(FileInfo::class.java.classLoader) as ArrayList<FileInfo>}
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeSerializable(rootFolder)
        parcel.writeString(name)
        parcel.writeSerializable(file)
        parcel.writeInt(count)
        parcel.writeLong(size)
        parcel.writeList(listFiles)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DirInfo> {
        override fun createFromParcel(parcel: Parcel): DirInfo {
            return DirInfo(parcel)
        }

        override fun newArray(size: Int): Array<DirInfo?> {
            return arrayOfNulls(size)
        }
    }
}

class FileInfo(
    var name: String?,
    var file: File,
    var size: Long,
    var contentUri: Uri?
//    var thumbnail:
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readSerializable(ClassLoader.getSystemClassLoader(), File::class.java) as File
        } else {
            parcel.readSerializable() as File
        },
        parcel.readLong(),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            parcel.readParcelable(ClassLoader.getSystemClassLoader(), Uri::class.java)
        } else {
            parcel.readParcelable(Uri::class.java.classLoader)
        }

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeSerializable(file)
        parcel.writeLong(size)
        parcel.writeParcelable(contentUri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileInfo> {
        override fun createFromParcel(parcel: Parcel): FileInfo {
            return FileInfo(parcel)
        }

        override fun newArray(size: Int): Array<FileInfo?> {
            return arrayOfNulls(size)
        }
    }
}