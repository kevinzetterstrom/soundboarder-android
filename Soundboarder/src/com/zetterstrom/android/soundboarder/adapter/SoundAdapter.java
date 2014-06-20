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
package com.zetterstrom.android.soundboarder.adapter;

import java.util.ArrayList;

import com.zetterstrom.android.soundboarder.dto.Sound;
import com.zetterstrom.android.soundboarder.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SoundAdapter extends ArrayAdapter<Sound> {
    private ArrayList<Sound> mItems;
    private Context mContext = null;

    public SoundAdapter(Context context, int textViewResourceId,
            ArrayList<Sound> items) {
        super(context, textViewResourceId, items);
        this.mItems = items;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.grid_item, null);
        }
        Sound s = mItems.get(position);
        if (s != null) {
            TextView description = (TextView) v.findViewById(R.id.griditemtext);
            if (description != null) {
                description.setText(s.getDescription());
            }
        }
        return v;
    }
}
