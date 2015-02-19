package spoon.test.interfaces.testclasses;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface ExtendsDefaultMethodInterface extends InterfaceWithDefaultMethods {
	default public ZonedDateTime getZonedDateTime(String zoneString) {
		try {
			return ZonedDateTime.of(getLocalDateTime(), ZoneId.of(zoneString));
		} catch (DateTimeException e) {
			System.err.println("Invalid zone ID: " + zoneString + "; using the default time zone instead.");
			return ZonedDateTime.of(getLocalDateTime(), ZoneId.systemDefault());
		}
	}
}
