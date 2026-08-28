package com.digitusforum.firewall.subject;

import java.util.ArrayList;
import java.util.List;

import com.digitusforum.firewall.video.VideoVO;

public class SubjectVO {
	private String subjectId;
	private String videoId;
	private String trainingId;
	private String userId;
	private String perfilId;
	private int number;
	private int newNumber;
	private String name;
	private String sinopse;
	private String description;
	private List<VideoVO> videos = new ArrayList<>();

	public String getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(String trainingId) {
		this.trainingId = trainingId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPerfilId() {
		return perfilId;
	}

	public void setPerfilId(String perfilId) {
		this.perfilId = perfilId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNewNumber() {
		return newNumber;
	}

	public void setNewNumber(int newNumber) {
		this.newNumber = newNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSinopse() {
		return sinopse;
	}

	public void setSinopse(String sinopse) {
		this.sinopse = sinopse;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<VideoVO> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoVO> videos) {
		this.videos = videos;
	}

}