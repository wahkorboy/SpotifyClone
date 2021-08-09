package com.wahkor.spotifyclone.exo

class FirebaseMusicSource {
    private val onReadyListeners= mutableListOf< (Boolean) -> Unit >()
    private var state=State.STATE_CREATED
}

enum class State{
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR,
}