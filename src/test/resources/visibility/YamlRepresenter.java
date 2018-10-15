package visibility;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

public class YamlRepresenter extends Representer {

	public YamlRepresenter() {
	}

	private class RepresentConfigurationSection extends RepresentMap {
		@Override
		public Node representData(Object data) {
			return null;
		}
	}
}
