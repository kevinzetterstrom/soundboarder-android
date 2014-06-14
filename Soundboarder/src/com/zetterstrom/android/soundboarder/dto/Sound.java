package com.zetterstrom.android.soundboarder.dto;

public class Sound {
	private String mDescription = "";
	private String mAssetDescription = "";
	private int mSoundResourceId = -1;

	public void setDescription(String description) {
		mDescription = description;
	}

	public String getDescription() {
		return mDescription;
	}

	public void setAssetDescription(String description) {
		mAssetDescription = description;
	}

	public String getAssetDescription() {
		return mAssetDescription;
	}

	public void setSoundResourceId(int id) {
		mSoundResourceId = id;
	}

	public int getSoundResourceId() {
		return mSoundResourceId;
	}

}