package spoon.test.api.testclasses;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import java.util.HashMap;

public class Bar {
	public AudioFormat doSomething() {
		return new AudioFormat(Encoding.ALAW, (float) 1.0, 8, 2, 1, (float) 1.0, true, new HashMap<String, Object>());
	}
}