/*
 * Copyright 2014 Kevin Zetterstrom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zetterstrom.android.soundboarder.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.zetterstrom.android.soundboarder.MainActivity;
import com.zetterstrom.android.soundboarder.adapter.SoundAdapter;
import com.zetterstrom.android.soundboarder.dto.Sound;
import com.zetterstrom.android.soundboarder.util.StringParser;
import com.zetterstrom.android.soundboarder.R;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class SoundboardFragment extends Fragment {
    public static final String ARG_BOARD_NUMBER = "soundboard_number";
    private ArrayList<Sound> mSounds = null;

    public SoundboardFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int i = getArguments().getInt(ARG_BOARD_NUMBER);
        String board = getResources().getStringArray(R.array.boards_array)[i];

        getActivity().setTitle(board);
        createSounds(i);

        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist. The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed. Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }
        GridView gridView = (GridView) inflater.inflate(
                R.layout.fragment_board, container, false);
        gridView.setAdapter(new SoundAdapter(getActivity()
                .getApplicationContext(), R.layout.grid_item, getSounds()));

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
                Sound s = (Sound) getSounds().get(position);

                AssetFileDescriptor afd;
                try {
                    afd = getActivity().getAssets().openFd(
                            s.getAssetDescription());
                    ((MainActivity) getActivity()).playSound(afd);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "Error Playing Sound Byte",
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
        gridView.setOnItemLongClickListener(longClickListener);
        return gridView;
    }

    private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
        public boolean onItemLongClick(AdapterView<?> parent, View v,
                final int position, long id) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            CharSequence[] menu = new CharSequence[1];
            menu[0] = "Notification";
            // menu[1] = "Ringtone";

            builder.setTitle("Save As...").setItems(menu,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            switch (which) {
                            case 0:
                                if (saveAsNotification(getSounds()
                                        .get(position))) {
                                    Toast.makeText(getActivity(),
                                            "Saved as Notification",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(),
                                            "Failed to Save Notification",
                                            Toast.LENGTH_SHORT).show();
                                }
                                break;
                            default:
                                break;
                            }
                        }
                    });
            builder.create().show();
            return true;
        }
    };

    public boolean saveAsNotification(Sound sound) {
        byte[] buffer = null;
        InputStream fIn = getActivity().getBaseContext().getResources()
                .openRawResource(sound.getSoundResourceId());
        int size = 0;

        try {
            size = fIn.available();
            buffer = new byte[size];
            fIn.read(buffer);
            fIn.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String path = Environment.getExternalStorageDirectory().getPath()
                + "/media/audio/notifications/";
        String filename = sound.getDescription() + ".ogg";

        boolean exists = (new File(path)).exists();
        if (!exists) {
            new File(path).mkdirs();
        }

        FileOutputStream save;
        try {
            save = new FileOutputStream(path + filename);
            save.write(buffer);
            save.flush();
            save.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        getActivity().sendBroadcast(
                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri
                        .parse("file://" + path + filename)));

        File k = new File(path, filename);

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, k.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, sound.getDescription());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/ogg");
        values.put(MediaStore.Audio.Media.ARTIST, "soundboarder");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        // Insert it into the database
        getActivity().getContentResolver()
                .insert(MediaStore.Audio.Media.getContentUriForPath(k
                        .getAbsolutePath()), values);

        return true;
    }

    public void createSounds(int position) {
        mSounds = new ArrayList<Sound>();
        String assetFolder = "";
        switch (position) {
        case 0:
            assetFolder = "arnold";
            break;
        case 1:
            assetFolder = "ben_stiller_dodgeball";
            break;
        case 2:
            assetFolder = "borat";
            break;
        case 3:
            assetFolder = "charlie_sheen";
            break;
        case 4:
            assetFolder = "family_guy";
            break;
        case 5:
            assetFolder = "kramer";
            break;
        case 6:
            assetFolder = "napolean_dynamite";
            break;
        case 7:
            assetFolder = "samuel_jackson";
            break;
        case 8:
            assetFolder = "shammy";
            break;
        case 9:
            assetFolder = "super_troopers";
            break;
        case 10:
            assetFolder = "tony_soprano";
            break;
        default:
            return;
        }
        Sound s;
        AssetManager am = getActivity().getAssets();
        try {
            String[] allSounds = am.list(assetFolder);
            for (String soundName : allSounds) {
                s = new Sound();
                s.setDescription(StringParser.splitCamelCase(soundName));
                s.setAssetDescription(assetFolder + "/" + soundName);
                mSounds.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Sound> getSounds() {
        return mSounds;
    }

}
