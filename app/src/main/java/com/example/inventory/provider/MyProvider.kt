package com.example.inventory.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import com.example.inventory.data.ItemRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope


class MyProvider : ContentProvider() {

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        const val AUTHORITY = "com.example.inventory"
        val URL ="content://$AUTHORITY/person_info"
        val CONTENT_URI = Uri.parse(URL)
        const val PERSON_TABLE_NAME = "person_info"
        const val ID_PERSON_DATA = 1
        const val ID_PERSON_DATA_ITEM = 2
    }

    init {
        uriMatcher.addURI(AUTHORITY,PERSON_TABLE_NAME,ID_PERSON_DATA);
        uriMatcher.addURI(AUTHORITY,PERSON_TABLE_NAME +"/*", ID_PERSON_DATA_ITEM);
    }

    private lateinit var itemDao: ItemDao
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            ID_PERSON_DATA -> {
                if (context != null) {
                      var id: Long = itemDao.insert2(Item.fromContentValues(values!!)) as Long
                        if (id as Int != 0) {
                        context?.contentResolver?.notifyChange(uri, null)
                        return ContentUris.withAppendedId(uri, id)
                    }
                    throw IllegalArgumentException("Failed")

                }
                throw IllegalArgumentException("Failed")
            }

            ID_PERSON_DATA_ITEM -> {
                throw IllegalArgumentException("Failed")
            }

            else -> {
                throw IllegalArgumentException("Failed")
            }
        }
    }


    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        var cursor: Cursor
        when (uriMatcher.match(uri)) {
            ID_PERSON_DATA -> {
                cursor = itemDao.getallItem()
                if (context != null) {
                    cursor.setNotificationUri(context?.contentResolver, uri)
                    return cursor
                }
                throw IllegalArgumentException("Failed")

            }
            else -> {
                throw IllegalArgumentException("Failed")
            }
        }
    }

    override fun onCreate(): Boolean {
        Log.d("TAG", (context == null).toString())
        context
        val applicationDatabase = ItemRoomDatabase.getDatabase(context!!)
        itemDao = applicationDatabase?.itemDao()!!
        return false
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        when (uriMatcher.match(uri)) {
            ID_PERSON_DATA -> {
                if (context != null) {
                    var count: Int = itemDao.update2(Item.fromContentValues(values!!)) as Int
                    if (count as Int != 0) {
                        context?.contentResolver?.notifyChange(uri, null)
                        return count
                    }
                }
                throw IllegalArgumentException("Unknown URI:$uri")
            }

            ID_PERSON_DATA_ITEM -> {
                throw IllegalArgumentException("UPDATE Failed")
            }

            else -> {
                throw IllegalArgumentException("Failed:$uri")

            }
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        when (uriMatcher.match(uri)) {
            ID_PERSON_DATA -> throw IllegalArgumentException("CANNOT DELETE")
            ID_PERSON_DATA_ITEM -> {
                if (context != null) {
                    var count = itemDao.delete2(ContentUris.parseId(uri))
                    context?.contentResolver?.notifyChange(uri, null)
                    return count
                }
                throw IllegalArgumentException("Unknown URI:$uri")
            }
            else -> throw IllegalArgumentException("Failed")
        }
    }

    override fun getType(uri: Uri): String? {
        return null
    }
}