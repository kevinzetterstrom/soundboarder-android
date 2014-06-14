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
