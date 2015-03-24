package spoon.test.interfaces.testclasses;

import java.time.ZoneId;

public interface RedefinesStaticMethodInterface extends InterfaceWithDefaultMethods {
	public ZoneId getZoneId(String zoneString);
}
