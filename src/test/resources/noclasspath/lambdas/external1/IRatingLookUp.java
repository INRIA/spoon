package de.uni_bremen.st.quide.persistence.ratings;

import java.util.Map;

import de.uni_bremen.st.quide.datamodel.transferred_data.IViolation;

public interface IRatingLookUp {

	int DEFAULT_RATING = 5;

	/**
	 * Sets the rating for the given violation with the contents of the database.
	 * <p>
	 * Ratings can be saved for different granularities, one of which is rated filesystems.<br>
	 * So in the case that the rating for a violation referencing many files has to be calculated and
	 * no rating of higher granularity can be found the value to be set will be the maximum of the
	 * ratings of all the files.
	 *
	 * @param userId the user who sent this request
	 * @param violation the violation for which the rating will be set
	 *
	 */
	void setRating(String userId, IViolation violation);

	/**
	 * Returns ratings for ViolationTypes in a map of name of ViolationType -> rating
	 * 
	 * @param userId the user to get the rating for
	 * @return the map
	 */
	Map<String, Integer> getViolationTypeRatings(String userId);

}
