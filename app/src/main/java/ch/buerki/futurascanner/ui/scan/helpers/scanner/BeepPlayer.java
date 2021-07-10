package ch.buerki.futurascanner.ui.scan.helpers.scanner;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import ch.buerki.futurascanner.R;

public class BeepPlayer {

    private final MediaPlayer mediaPlayerShort;
    private final MediaPlayer mediaPlayerLong;

    public BeepPlayer(Context context) {
        mediaPlayerShort = MediaPlayer.create(context, R.raw.beep_short);
        mediaPlayerShort.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        );
        mediaPlayerShort.setVolume(1.0f, 1.0f);
        mediaPlayerLong = MediaPlayer.create(context, R.raw.beep_long);
        mediaPlayerLong.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        );
        mediaPlayerLong.setVolume(1.0f, 1.0f);
    }

    public void playShort() {
        mediaPlayerShort.seekTo(0);
        mediaPlayerShort.start();
    }

    public void playLong() {
        mediaPlayerLong.seekTo(0);
        mediaPlayerLong.start();
    }

    public void quit() {
        mediaPlayerShort.stop();
        mediaPlayerShort.release();
    }
}
