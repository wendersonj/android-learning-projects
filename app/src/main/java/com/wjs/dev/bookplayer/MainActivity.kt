package com.wjs.dev.bookplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.wjs.dev.bookplayer.databinding.ActivityMainBinding
import com.wjs.dev.bookplayer.models.AudioBook
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var runnable: Runnable
    private var handler = Handler()
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val book_list = listOf<AudioBook>(
            AudioBook("colecao", "leandro", "outro moço", R.raw.track_1, R.raw.track_1_cover),
            AudioBook("cute", "", "", R.raw.cute, R.raw.cute_cover),
            AudioBook("creativeminds", "", "", R.raw.creativeminds, R.raw.creativeminds_cover)
        )

        var current_track_pos = 0
        mediaPlayer=create_new_track(
            book_list[0],
            binding.trackImage,
            binding.trackName
        )

        binding.playButton.setOnClickListener {
            play_or_pause_track(mediaPlayer)
        }


        //when change the seekbar, change the song's position

        binding.trackProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, position: Int, changed: Boolean) {

                if (changed) {
                    mediaPlayer?.seekTo(position)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })


        //make the seekbar follow the song progress
        runnable = Runnable {
            binding.trackProgress.progress = mediaPlayer?.currentPosition ?: 0
            handler.postDelayed(runnable, 1000)

            binding.currentProgressTime.text =
                ((mediaPlayer?.currentPosition?.div(1000))?.toDuration(DurationUnit.SECONDS)).toString()
        }
        handler.postDelayed(runnable, 1000)

        mediaPlayer?.setOnCompletionListener {
            binding.playButton.setImageResource(R.drawable.ic_play_arrow)
            binding.trackProgress.progress = 0
            it.seekTo(0)
            //tbm posso forçar a ir para a proxima faixa da lista trocando a midia que esta tocando

        }

        // previous and next track

        binding.nextTrack.setOnClickListener {
            current_track_pos = get_next_track(current_track_pos, book_list.lastIndex)
            mediaPlayer = choose_new_track(
                mediaPlayer,
                book_list,
                current_track_pos,
                binding.trackImage,
                binding.trackName
            )
        }
        binding.previousTrack.setOnClickListener {
            current_track_pos = get_previous_track(current_track_pos, book_list.lastIndex)
            mediaPlayer =
                choose_new_track(
                    mediaPlayer,
                    book_list,
                    current_track_pos,
                    binding.trackImage,
                    binding.trackName
                )

        }

    }

    private fun get_previous_track(currentTrack: Int, last_index: Int): Int {
        if (currentTrack - 1 >= 0) {
            return currentTrack - 1
        } else {
            return last_index
        }
    }

    private fun get_next_track(currentTrack: Int, last_index: Int): Int {
        if (currentTrack + 1 > last_index) {
            return 0
        } else {
            return currentTrack + 1
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }


    private fun choose_new_track(
        media_player: MediaPlayer?,
        book_list: List<AudioBook>,
        current_track_pos: Int,
        track_image: ImageView,
        track_name: TextView
    ): MediaPlayer {
        media_player?.release()
        val currentTrack = book_list[current_track_pos]

        val mediaPlayer = create_new_track(currentTrack, track_image, track_name)

        mediaPlayer.let {
            play_track(mediaPlayer)
        }

        return mediaPlayer
    }

    private fun create_new_track(
        currentTrack: AudioBook,
        track_image: ImageView,
        track_name: TextView
    ): MediaPlayer {
        val mediaPlayer = MediaPlayer.create(this, currentTrack.path_to_file)

        track_image.setImageResource(currentTrack.path_to_cover)
        track_name.text = currentTrack.book_name

        set_parameters_of_new_track(mediaPlayer)
        return mediaPlayer
    }

    private fun set_parameters_of_new_track(mediaPlayer: MediaPlayer?) {
        binding.trackProgress.progress = 0
        if (mediaPlayer != null) {
            binding.trackProgress.max = mediaPlayer.duration
        }
        binding.totalProgressTime.text =
            ((mediaPlayer?.duration?.div(1000))?.toDuration(DurationUnit.SECONDS)).toString()
    }

    private fun play_or_pause_track(mediaPlayer: MediaPlayer?) {
        if (mediaPlayer != null) {
            if (!mediaPlayer.isPlaying) {
                play_track(mediaPlayer)

            } else {
                pause_track(mediaPlayer)
            }
        }
    }

    private fun pause_track(mediaPlayer: MediaPlayer) {
        mediaPlayer.pause()
        binding.playButton.setImageResource(R.drawable.ic_play_arrow)
    }

    private fun play_track(mediaPlayer: MediaPlayer) {
        mediaPlayer.start()
        binding.playButton.setImageResource(R.drawable.ic_pause)
    }


}