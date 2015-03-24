package spoon.test.interfaces.testclasses;

import java.time.ZoneId;

public interface ExtendsStaticMethodInterface extends InterfaceWithDefaultMethods {
	static ZoneId getZoneId(String zoneString) {
		return null;
	}
}
