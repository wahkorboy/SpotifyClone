package com.wahkor.spotifyclone.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.wahkor.spotifyclone.data.entities.Song
import kotlinx.coroutines.tasks.await

private const val SONG_COLLECTION="songs"
class MusicDatabase {
    private val fireStore=FirebaseFirestore.getInstance()
    private val songCollection=fireStore.collection(SONG_COLLECTION)

    suspend fun getAllSongs():List<Song>{
        return try {
            songCollection.get().await().toObjects(Song::class.java)
        }catch (e:Exception){
            emptyList()
        }
    }

}