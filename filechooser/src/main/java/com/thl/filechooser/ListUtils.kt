package com.thl.filechooser

import android.util.Log

/**
 * Description:
 * Data：$ $
 * Author: Allen
 */
object ListUtils {
    fun isSelectAll(fileInfoLIst: MutableList<FileInfo>, chooseType: String):Boolean{
        val fileSize = fileInfoLIst.filter { !it.isFolder }.size
        val fileCheckSize = fileInfoLIst.filter { !it.isFolder }.filter { it.isCheck }.size
        val folderSize = fileInfoLIst.filter { it.isFolder }.size
        val folderCheckSize = fileInfoLIst.filter { it.isFolder }.filter { it.isCheck }.size
        Log.e("草拟吗fileCheckSize", "" + fileCheckSize)
        Log.e("草拟吗fileSize", "" + fileSize)
        Log.e("草拟吗folderSize", "" + folderSize)
        Log.e("草拟吗folderCheckSize", "" + folderCheckSize)
        return when (chooseType) {
            FileInfo.FILE_TYPE_FILE -> {
                fileCheckSize==fileSize
            }
            FileInfo.FILE_TYPE_FOLDER -> {
                folderCheckSize == folderSize
            }
            else -> false
        }
    }

    fun getFileSize(fileInfoLIst: MutableList<FileInfo>):Int{
        return fileInfoLIst.filter { !it.isFolder }.size
    }

    fun getFolderSize(fileInfoLIst: MutableList<FileInfo>):Int{
        return fileInfoLIst.filter { it.isFolder }.size
    }
}