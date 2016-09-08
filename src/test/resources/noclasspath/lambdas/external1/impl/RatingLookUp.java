package de.uni_bremen.st.quide.persistence.ratings.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.uni_bremen.st.quide.datamodel.transferred_data.IViolation;
import de.uni_bremen.st.quide.datamodel.transferred_data.ViolationType;
import de.uni_bremen.st.quide.datamodel.transferred_data.impl.TOFilename;
import de.uni_bremen.st.quide.datamodel.transferred_data.impl.TOFragment;
import de.uni_bremen.st.quide.persistence.data.RatingEntry;
import de.uni_bremen.st.quide.persistence.ratings.IRatingLookUp;

@Service
public class RatingLookUp implements IRatingLookUp {

	private static final int ERROR_VALUE = -1;

	private static final String byId = "SELECT rating FROM RatingEntry WHERE :type LIKE violationType AND :id = violationId AND userId = :userId";
	private static final String byFiles = "SELECT rating FROM RatingEntry "
			+ "WHERE ("
			+ ":file LIKE (path || '%') OR path IS NULL"
			+ ") AND ("
			+ ":type LIKE violationType OR violationType IS NULL"
			+ ") AND userId = :userId AND violationId IS NULL "
			+ "ORDER BY path DESC, violationType DESC";

	private static final String forTypes =
			"FROM RatingEntry WHERE violationType IS NOT NULL AND violationId IS NULL AND path IS NULL AND userId = :userId";

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = true)
	public void setRating(String userId, final IViolation violation) {
		ViolationType type = violation.getViolationType();
		Long id = violation.getId();

		int rating = byId(userId, type, id);
		if (rating == ERROR_VALUE) {
			rating = byFiles(userId, type, violation.getLocations());
		}
		violation.setRating(rating);
	}

	@Override
	@Transactional
	public Map<String, Integer> getViolationTypeRatings(String userId) {
		Map<String, Integer> map = new HashMap<>();

		entityManager.createQuery(forTypes, RatingEntry.class)
				.setParameter("userId", userId)
				.getResultList()
				.forEach(entry -> map.put(entry.getViolationType(), entry.getRating()));

		Arrays.stream(ViolationType.values())
				.map(type -> type.toString())
				.filter(type -> !map.containsKey(type))
				.forEach(type -> map.put(type, DEFAULT_RATING));

		return map;
	}

	private int byId(String userId, ViolationType type, Long id) {
		List<Integer> result = entityManager.createQuery(byId, Integer.class)
				.setParameter("userId", userId)
				.setParameter("type", type.toString())
				.setParameter("id", id)
				.setMaxResults(1)
				.getResultList();
		return result.isEmpty() ? ERROR_VALUE : result.get(0);
	}

	private int byFiles(String userId, ViolationType type, TOFragment... locations) {
		return Arrays.stream(locations)
				.map(TOFragment::getFile)
				.map(TOFilename::getName)
				.distinct()
				.mapToInt(file -> byFile(userId, type, file))
				.max()
				.orElse(DEFAULT_RATING);
	}

	private int byFile(String userId, ViolationType type, String file) {
		List<Integer> list = entityManager.createQuery(byFiles, Integer.class)
				.setParameter("userId", userId)
				.setParameter("type", type.toString())
				.setParameter("file", file)
				.setMaxResults(1)
				.getResultList();
		return list.isEmpty() ? DEFAULT_RATING : list.get(0);
	}

}
