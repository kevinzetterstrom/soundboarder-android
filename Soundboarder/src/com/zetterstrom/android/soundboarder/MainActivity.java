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
package com.zetterstrom.android.soundboarder;

import java.io.IOException;

import com.zetterstrom.android.soundboarder.fragments.SoundboardFragment;
import com.zetterstrom.android.soundboarder.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.view.GravityCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;
    private String[] mBoardsTitles;
    private int mPosition = 0;
    private MediaPlayer mMediaPlayer = null;
    public static final String PREFS_FILE = "SoundboarderPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Ultimate Soundboard");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mMediaPlayer = new MediaPlayer();

        mTitle = getTitle();
        mBoardsTitles = getResources().getStringArray(R.array.boards_array);
        mDrawerList = (ListView) findViewById(R.id.main_left_drawer);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mBoardsTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
        mDrawerLayout, /* DrawerLayout object */
        R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
        R.string.drawer_open, /* "open drawer" description */
        R.string.drawer_close /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                ActivityCompat.invalidateOptionsMenu(MainActivity.this); // creates
                                                                         // call
                                                                         // to
                // onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                ActivityCompat.invalidateOptionsMenu(MainActivity.this); // creates
                                                                         // call
                                                                         // to
                // onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("soundBoardPosition");
        } else {
            SharedPreferences settings = getSharedPreferences(PREFS_FILE,
                    MODE_PRIVATE);
            mPosition = settings.getInt("soundBoardPosition", 0);
        }
        selectItem(mPosition);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            selectItem(position);
        }
    }

    public void playSound(AssetFileDescriptor afd) {
        if (mMediaPlayer != null) {
            try {
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(afd.getFileDescriptor(),
                        afd.getStartOffset(), afd.getLength());
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, "Error Playing Sound Byte",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error Playing Sound Byte",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IOException e) {
                Toast.makeText(this, "Error Playing Sound Byte",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Error Playing Sound Byte", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on
        // position
        mPosition = position;
        Fragment fragment;
        fragment = new SoundboardFragment();
        Bundle args = new Bundle();
        args.putInt(SoundboardFragment.ARG_BOARD_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_content_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(mPosition, true);
        setTitle(mBoardsTitles[mPosition]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_about).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
        case R.id.action_about:
            Intent launchHelp = new Intent(getApplicationContext(),
                    AboutActivity.class);
            startActivity(launchHelp);
            break;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt("soundBoardPosition", mPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null)
            mMediaPlayer.release();

        // Store values between instances here
        SharedPreferences preferences = getSharedPreferences(PREFS_FILE,
                MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit(); // Put the values
                                                              // from the UI
        editor.putInt("soundBoardPosition", mPosition);
        // Commit to storage
        editor.commit();
    }
}
