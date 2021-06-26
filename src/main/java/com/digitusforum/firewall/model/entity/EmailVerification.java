package com.digitusforum.firewall.model.entity;

import com.digitusforum.firewall.model.repository.EmailVerificationRepository;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Entity
public class EmailVerification {
	@Id
	private String emailVerificationId;
	private String email;
	private String name;
	private String password;
	private Integer readableNumber;
	private Boolean used;
	private ZonedDateTime createdIn;
	private ZonedDateTime usedIn;

	public EmailVerification() {
	}

	/*
	 * public EmailVerification(UserVO user, EmailVerificationRepository
	 * emailVerificationRepository) {
	 * this.setEmailVerificationId(UUID.randomUUID().toString()); this.email =
	 * user.getEmail(); this.name = user.getName(); this.password =
	 * user.getPassword(); this.readableNumber = generateReadableNumber(email,
	 * emailVerificationRepository); this.createdIn = ZonedDateTime.now(); this.used
	 * = false; }
	 */

	private int generateReadableNumber(String userId, EmailVerificationRepository emailVerificationRepository) {
		int readableNumber = ThreadLocalRandom.current().nextInt(1000, 9999);
		while (emailVerificationRepository.findByReadableNumber(readableNumber).isPresent())
			readableNumber = ThreadLocalRandom.current().nextInt(1000, 9999);
		return readableNumber;
	}

	public String getEmailVerificationId() {
		return emailVerificationId;
	}

	public void setEmailVerificationId(String emailVerificationId) {
		this.emailVerificationId = emailVerificationId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getReadableNumber() {
		return readableNumber;
	}

	public void setReadableNumber(Integer readableNumber) {
		this.readableNumber = readableNumber;
	}

	public Boolean getUsed() {
		return used;
	}

	public void setUsed(Boolean used) {
		this.used = used;
	}

	public ZonedDateTime getCreatedIn() {
		return createdIn;
	}

	public void setCreatedIn(ZonedDateTime createdIn) {
		this.createdIn = createdIn;
	}

	public ZonedDateTime getUsedIn() {
		return usedIn;
	}

	public void setUsedIn(ZonedDateTime usedIn) {
		this.usedIn = usedIn;
	}

}
