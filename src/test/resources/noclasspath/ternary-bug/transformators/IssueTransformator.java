package de.uni_bremen.st.quide.persistence.transformators;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.uni_bremen.st.quide.datamodel.transferred_data.IViolation;
import de.uni_bremen.st.quide.datamodel.transferred_data.impl.TOIssue;
import de.uni_bremen.st.quide.persistence.IPersistence;
import de.uni_bremen.st.quide.persistence.data.entities.Filename;
import de.uni_bremen.st.quide.persistence.data.entities.Issue;
import de.uni_bremen.st.quide.persistence.data.entities.IssueLabel;
import de.uni_bremen.st.quide.persistence.data.entities.Version;
import de.uni_bremen.st.quide.persistence.data.relationships.FilenameVersion;
import de.uni_bremen.st.quide.persistence.exceptions.InvalidIDException;
import de.uni_bremen.st.quide.persistence.util.Condition;
import de.uni_bremen.st.quide.persistence.util.Condition.Type;

@Component
public class IssueTransformator {

	@Autowired
	private IPersistence persistence;

	public List<TOIssue> getAllIssues() {
		return getAll(null);
	}

	public List<TOIssue> getIssuesReferencedByVersion(long versionId) throws InvalidIDException {
		Version version = persistence.checkID(Version.class, versionId);

		return getAll(Collections.singletonList(new Condition(Type.REVERSE_IN, "versions", version)));
	}

	public List<TOIssue> getIssuesModifyingFile(long fileId) throws InvalidIDException {
		Filename filename = persistence.checkID(Filename.class, fileId);

		List<Condition> conditions = Collections.singletonList(new Condition("filename", filename));

		return persistence.getAll(FilenameVersion.class, conditions).stream()
				.map(FilenameVersion::getStartVersion)
				.map(Version::getIssues)
				.flatMap(Set::stream)
				.map(this::createIssue)
				.collect(Collectors.toList());
	}

	public List<IViolation> getViolationsFixedByIssue(long issueId) {
		return Collections.emptyList();
	}

	public List<IViolation> getViolationsCreatedByIssue(long issueId) {
		return Collections.emptyList();
	}

	private TOIssue createIssue(Issue issue) {
		final ZonedDateTime closedAt = issue.getClosedAt();

		final Set<String> labels = issue.getLabels().stream()
				.map(IssueLabel::getName)
				.collect(Collectors.toSet());

		final Set<Long> versions = null;

		return new TOIssue(
				issue.getId(),
				issue.getIdentifier(),
				issue.getTitle(),
				issue.getDescription(),
				issue.getUrl(),
				"",

				labels,
				versions,

				Date.from(issue.getCreatedAt().toInstant()),
				closedAt == null ? null : Date.from(issue.getClosedAt().toInstant()),

				issue.getIssueType().toString());
	}

	private List<TOIssue> getAll(List<Condition> conditions) {
		return persistence.getAll(Issue.class, conditions).stream()
				.map(this::createIssue)
				.collect(Collectors.toList());
	}
}
