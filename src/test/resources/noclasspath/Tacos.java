
public class Tacos {
	public void setStarRatings(java.lang.HashMap<ViolationType, java.lang.Integer> userRatings) {
		userRatings.entrySet().forEach(entryPair -> typeRatingFilters.get(entryPair.getKey()).setCurrentNumberOfStars(entryPair.getValue()));
	}
}