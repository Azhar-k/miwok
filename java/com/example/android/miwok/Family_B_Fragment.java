package com.example.android.miwok;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Family_B_Fragment extends Fragment {
    private MediaPlayer mMediaPlayer;

    // Handles audio focus while playing sound file
    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // AUDIOFOCUS_LOSS TRANSIENT means we have lost audio focus for a short amount of time
                // and AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK means we have lost audio focus
                // our app still continues to play song at lower volume but in both cases,
                // we want our app to pause playback and start it from beginning.
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // it means we have gained focused and start playback
                mMediaPlayer.start();
            }else if(focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // it means we have completely lost the focus and we
                // have to stop the playback and free up the playback resources
                releaseMediaPlayer();
            }
        }
    };




    public Family_B_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        //Create and setup link to get audio focus
        mAudioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);

        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("", "coming soon"));
        words.add(new Word("", "coming soon"));
        words.add(new Word("", "coming soon"));
        words.add(new Word("", "coming soon"));
        words.add(new Word("","coming soon"));
        words.add(new Word("", "coming soon"));
        words.add(new Word("", "coming soon"));
        words.add(new Word("", "coming soon"));
        words.add(new Word("", "coming soon"));
        words.add(new Word("", "coming soon"));

        // setting up the array adapter
        WordAdapter itemsAdapter= new WordAdapter(getActivity(), words,  R.color.category_numbers);

        // finding the listView and setting the adapter to it
        ListView listView = (ListView)rootView.findViewById(R.id.word_list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (words.get(position).hasAudio())
                {

                    // relase the media player object if currently exist because we are going to change the song
                    releaseMediaPlayer();

                    // Request audio focus so in order to play the audio file. The app needs to play a
                    // short audio file, so we will request audio focus with a short amount of time
                    // with AUDIOFOCUS_GAIN_TRANSIENT.
                    int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        //we have the audio focus now

                        // creates new media player object
                        mMediaPlayer = MediaPlayer.create(getActivity(), words.get(position).getAudioResourceId());
                        mMediaPlayer.start();

                        /**
                         * set on completion listener on the mediaplayer object
                         * and relase media player object as soon song stops playing*/
                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {

                                // now the sound file has finished player, so free up the media player resources
                                releaseMediaPlayer();
                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getActivity(),"Audio coming soon",Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }
    @Override
    public void onStop() {
        super.onStop();

        //when activity is stooped release media player resources
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

}
