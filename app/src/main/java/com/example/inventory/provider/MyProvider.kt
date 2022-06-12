package com.example.inventory.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import com.example.inventory.data.ItemRoomDatabase
import kotlinx.coroutines.*


class MyProvider : ContentProvider() {

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        const val AUTHORITY = "com.example.inventory"
        val URL ="content://$AUTHORITY"
        val BASE_CONTENT_URI = Uri.parse(URL)
        const val PERSON_TABLE_NAME = "person_info"
        const val ID_PERSON_DATA = 1
        const val ID_PERSON_DATA_ITEM = 2
        val CONTENT_URI: Uri? =Uri.withAppendedPath(MyProvider.BASE_CONTENT_URI,MyProvider.PERSON_TABLE_NAME)
        val _ID ="_id"
        val Country ="Country"
        val Capital ="Capital"
    }

    init {
        uriMatcher.addURI(AUTHORITY,PERSON_TABLE_NAME,ID_PERSON_DATA);
        uriMatcher.addURI(AUTHORITY,PERSON_TABLE_NAME +"/*", ID_PERSON_DATA_ITEM);
    }

    private lateinit var itemDao: ItemDao

    override fun onCreate(): Boolean {
        Log.d("TAG", (context == null).toString())
        context
        val applicationDatabase = ItemRoomDatabase.getDatabase(context!!)
        itemDao = applicationDatabase?.itemDao()!!
        return true
    }


    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? =
        context?.let {
            when (uriMatcher.match(uri)) {
                ID_PERSON_DATA -> {
                    val id = itemDao.insert2(Item.fromContentValues(contentValues!!))
                    it.contentResolver.notifyChange(uri, null)
                    return@let ContentUris.withAppendedId(uri, id)
                }
                ID_PERSON_DATA_ITEM -> throw IllegalArgumentException("Invalid Uri, can not insert with ID: $uri")
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        }


    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? =
        context?.let {
            when (uriMatcher.match(uri)) {
                ID_PERSON_DATA -> itemDao.getallItem()
                ID_PERSON_DATA_ITEM -> itemDao.selectById(ContentUris.parseId(uri))
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        }



    override fun update(
        uri: Uri, value: ContentValues?, selection: String?, selectionArgs: Array<out String>?
    ): Int =
        if (context != null) {
            when (uriMatcher.match(uri)) {
                ID_PERSON_DATA -> throw IllegalArgumentException("Invalid Uri, cannot update without id: $uri")
                ID_PERSON_DATA_ITEM -> {
                    val item =Item.fromContentValues(value!!)
                    item.id = ContentUris.parseId(uri).toInt()

                    val count = itemDao.update2(item)
                    context!!.contentResolver.notifyChange(uri, null)
                    count
                }
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        } else {
            0
        }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int =
        if (context != null) {
            when (uriMatcher.match(uri)) {
                ID_PERSON_DATA -> itemDao.deleteAll()// throw IllegalArgumentException("Invalid Uri, cannot delete with uri: $uri")
                ID_PERSON_DATA_ITEM -> {
                    val count = itemDao
                        // ContentUris.parseId(): Convert the last path segment to a long; -1 if the path is empty
                        .deleteById(ContentUris.parseId(uri))
                    context!!.contentResolver.notifyChange(uri, null)
                    count
                }
                else -> throw IllegalArgumentException("Unknown Uri: $uri")
            }
        } else {
            0
        }

    override fun getType(uri: Uri): String =
        when (uriMatcher.match(uri)) {
            ID_PERSON_DATA -> "vnd.android.cursor.dir/$AUTHORITY.person_info"
            ID_PERSON_DATA_ITEM -> "vnd.android.cursor.item/$AUTHORITY.person_info"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
}