package spoon.test.interfaces.testclasses;

import java.time.ZonedDateTime;

public interface RedefinesDefaultMethodInterface extends InterfaceWithDefaultMethods {
	public ZonedDateTime getZonedDateTime(String zoneString);
}
