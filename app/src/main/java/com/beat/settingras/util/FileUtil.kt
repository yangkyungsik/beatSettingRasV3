package com.beat.settingras.util

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.beat.settingras.AppLog
import com.beat.settingras.Constant
import java.io.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*


/**
 * 파일 관련 클래스
 */
object FileUtil {
    fun writeFile(filePath:String, fileName:String){

    }


    fun readFile(filePath:String, fileName:String):File?{
        return File(filePath,fileName)
    }

    fun getRootPath():String{
        return if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
            Environment.getExternalStorageDirectory().path
        } else{
            ""
        }
    }

    fun getVideoPath():String{
        return if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
            Environment.getExternalStorageDirectory().path+File.separator+"beateye"+File.separator
        } else{
            ""
        }    }

    fun getLogFolderPath():String{
        return if(Build.VERSION.SDK_INT<Build.VERSION_CODES.O){
            Environment.getExternalStorageDirectory().path+ File.separator+ Constant.LOG_FOLDER +File.separator
        } else{
            ""
        }
    }

    /**
     * 비디오 폴더 내 파일 리스트 출력
     */
    fun getVideoFileOutput():String {
        val arr: List<String> = File(getVideoPath()).list().toList()
        val sortedArr:MutableList<String> = mutableListOf()

        for(x in arr.indices){
            if(arr[x].contains("mp4")){
                sortedArr.add("\""+arr[x]+"\"")
            }
        }

        var result:StringBuilder = StringBuilder()
        result.append(sortedArr.toString())

        return result.toString()
    }

    /**
     * 비디오 폴더 내 파일 리스트 출력
     */
    fun getVideoFileList():List<String> {
        val arr: List<String> = File(getVideoPath()).list().toList()
        val sortedArr:MutableList<String> = mutableListOf()

        for(x in arr.indices){
            if(arr[x].contains("mp4")){
                sortedArr.add(arr[x])
            }
        }
        return sortedArr
    }

    fun isExistVideoFile(filename:String):Boolean{
        return File(getVideoPath()+filename).exists()
    }

    fun UriToFilePath(uri:Uri,resolver: ContentResolver):String?{
        val proj = arrayOf(MediaStore.Images.Media.DATA)

        val cursor: Cursor? =
            resolver.query(uri, proj, null, null, null)

        cursor?.let {
            cursor.moveToNext()
            val path =
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
            val uri = Uri.fromFile(File(path))

            cursor.close()
            return path
        }

        return null

    }
    fun filePathToURI(filePath:String,resolver:ContentResolver):Uri?{
        val cursor: Cursor? = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, "_data = '$filePath'", null, null
        )

        cursor?.let {
            cursor.moveToNext()
            val id: Int = cursor.getInt(cursor.getColumnIndex("_id"))
            return ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toLong())
        }

        return null
    }

    /**
     * 부팅 시 앱 해당 날짜 이외 앱 로그 삭제
     */
    fun deleteAnotherLogFiles(){
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        var folderList = File(FileUtil.getLogFolderPath()).listFiles()

        folderList?.let {
            for(x in it.indices){
                if(!it[x].name.contains(date)){
                    it[x].delete()
                }
            }
        }

    }

    /**
     * 로그 폴더 내 로그 전부 삭제
     */
    fun deleteAllLogFiles(){
        var folderList = File(FileUtil.getLogFolderPath()).listFiles()

        folderList?.let {
            for(x in it.indices){
                it[x].delete()
            }
        }

    }

    /**
     * IP 쓰기
     */

    fun writeIP(){
        AppLog.l()
        try{
            var file:File = File(Environment.getExternalStorageDirectory().path+File.separator+Constant.FILENAME.IP_CODE)
            if(file.exists()){
                file.createNewFile()
            }
            var writer = FileWriter(file.absoluteFile)
            var bufferWriter = BufferedWriter(writer)
            bufferWriter.write(NetworkUtil.getMyIP())
            bufferWriter.close()
        }
        catch (e : Exception){
            AppLog.e("message : ${e.message}")
            e.printStackTrace()
        }
    }

    fun getIp():String?{
        var file:File = File(Environment.getExternalStorageDirectory().path+File.separator+Constant.FILENAME.IP_CODE)
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
            }
            br.close()
        } catch (e: IOException) {
            return null
        }
        return text.toString()
    }

}