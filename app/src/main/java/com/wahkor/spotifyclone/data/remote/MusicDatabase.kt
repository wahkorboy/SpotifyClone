package com.wahkor.spotifyclone.data.remote

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.wahkor.spotifyclone.data.entities.Song
import com.wahkor.spotifyclone.utils.Query
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

private const val SONG_COLLECTION="songs"
class MusicDatabase {
   // private val fireStore=FirebaseFirestore.getInstance()
   // private val songCollection=fireStore.collection(SONG_COLLECTION)

    suspend fun getAllSongs():List<Song>{
        while (Query.requestInitialing){
            delay(1000)
        }
        return Query.storageMedia
        /*
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        }catch (e:Exception){
            emptyList()
        }*/
    }

}