package com.example.musicapplication.model

enum class PlayMode {
    DEFAULT, REPEAT_ALL, REPEAT_ONE, SHUFFLE;

    fun nextPlayMode(): PlayMode {
        return values()[(ordinal + 1) % (values().size)]
    }
}

enum class ActionPlay {
    ACTION_NEXT, ACTION_PREV, ACTION_COMPLETE
}

enum class PlaylistType {
    TOP100_PLAYLIST, RECOMMEND_PLAYLIST, FAVOURITE_PLAYLIST, OFFLINE_PLAYLIST
}