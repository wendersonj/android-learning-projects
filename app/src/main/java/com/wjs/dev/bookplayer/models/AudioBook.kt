package com.wjs.dev.bookplayer.models

data class AudioBook(
    val book_name: String,
    val author: String,
    val speaker: String,
    val path_to_file: Int,
    val path_to_cover: Int
)