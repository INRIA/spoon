package de.uni_bremen.st.quide.persistence.data.entities;

import java.time.ZonedDateTime;

import de.uni_bremen.st.quide.persistence.data.IssueType;

public class Issue {
	public Long getId() {
		return id;
	}

	public String getIdentifier() {
		return "";
	}

	public String getTitle() {
		return "";
	}

	public String getDescription() {
		return "";
	}

	public String getUrl() {
		return "";
	}

	public ZonedDateTime getCreatedAt() {
		return null;
	}

	public ZonedDateTime getClosedAt() {
		return null;
	}

	public IssueType getIssueType() {
		return null;
	}

}
