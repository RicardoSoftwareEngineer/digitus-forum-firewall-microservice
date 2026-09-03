package com.digitusforum.firewall.background;

import java.util.List;

public class BackgroundSaveVO {
	private String id;
	private String backgroundId;
	private String userId;
	private String name;
	private String wallpaperData;
	private String dominantColor;
	private String createdIn;
	private boolean deleted;
	private Boolean backgroundAuto;
	private String pinnedBackgroundId;
	private List<BackgroundSaveVO> backgrounds;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBackgroundId() {
		return backgroundId != null ? backgroundId : id;
	}

	public void setBackgroundId(String backgroundId) {
		this.backgroundId = backgroundId;
		if (this.id == null)
			this.id = backgroundId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWallpaperData() {
		return wallpaperData;
	}

	public void setWallpaperData(String wallpaperData) {
		this.wallpaperData = wallpaperData;
	}

	public String getDominantColor() {
		return dominantColor;
	}

	public void setDominantColor(String dominantColor) {
		this.dominantColor = dominantColor;
	}

	public String getCreatedIn() {
		return createdIn;
	}

	public void setCreatedIn(String createdIn) {
		this.createdIn = createdIn;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getBackgroundAuto() {
		return backgroundAuto;
	}

	public void setBackgroundAuto(Boolean backgroundAuto) {
		this.backgroundAuto = backgroundAuto;
	}

	public String getPinnedBackgroundId() {
		return pinnedBackgroundId;
	}

	public void setPinnedBackgroundId(String pinnedBackgroundId) {
		this.pinnedBackgroundId = pinnedBackgroundId;
	}

	public List<BackgroundSaveVO> getBackgrounds() {
		return backgrounds;
	}

	public void setBackgrounds(List<BackgroundSaveVO> backgrounds) {
		this.backgrounds = backgrounds;
	}

}
