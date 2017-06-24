package spoon.experimental.modelobs;

import spoon.experimental.modelobs.action.AddAction;
import spoon.experimental.modelobs.action.DeleteAction;
import spoon.experimental.modelobs.action.DeleteAllAction;
import spoon.experimental.modelobs.action.UpdateAction;
import spoon.experimental.modelobs.context.ListContext;
import spoon.experimental.modelobs.context.MapContext;
import spoon.experimental.modelobs.context.ObjectContext;
import spoon.experimental.modelobs.context.SetContext;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This listener will propagate the change to the listener
 */
public class ListeningChangeFactory extends FineModelChangeListener {

		@Override
		public void onObjectUpdate(CtElement currentElement, CtRole role, CtElement newValue, CtElement oldValue) {
			propagateModelChange(new UpdateAction<>(new ObjectContext(currentElement, role), newValue, oldValue));
		}

		@Override
		public void onObjectUpdate(CtElement currentElement, CtRole role, Object newValue, Object oldValue) {
			propagateModelChange(new UpdateAction<>(new ObjectContext(currentElement, role), newValue, oldValue));
		}

		@Override
		public void onObjectDelete(CtElement currentElement, CtRole role, CtElement oldValue) {
			propagateModelChange(new DeleteAction<>(new ObjectContext(currentElement, role), oldValue));
		}

		@Override
		public void onListAdd(CtElement currentElement, CtRole role, List field, CtElement newValue) {
			propagateModelChange(new AddAction<>(new ListContext(currentElement, role, field), newValue));
		}

		@Override
		public void onListAdd(CtElement currentElement, CtRole role, List field, int index, CtElement newValue) {
			propagateModelChange(new AddAction<>(new ListContext(currentElement, role, field, index), newValue));
		}

		@Override
		public void onListDelete(CtElement currentElement, CtRole role, List field, int index, CtElement oldValue) {
			propagateModelChange(new DeleteAction<>(new ListContext(currentElement, role, field, index), oldValue));
		}

		@Override
		public void onListDeleteAll(CtElement currentElement, CtRole role, List field, List oldValue) {
			propagateModelChange(new DeleteAllAction(new ListContext(currentElement, role, field), oldValue));
		}

		@Override
		public <K, V> void onMapAdd(CtElement currentElement, CtRole role, Map<K, V> field, K key, CtElement newValue) {
			propagateModelChange(new AddAction<>(new MapContext<>(currentElement, role, field, key), newValue));
		}

		@Override
		public <K, V> void onMapDeleteAll(CtElement currentElement, CtRole role, Map<K, V> field, Map<K, V> oldValue) {
			propagateModelChange(new DeleteAllAction(new MapContext<>(currentElement, role, field), oldValue));
		}

		@Override
		public void onSetAdd(CtElement currentElement, CtRole role, Set field, CtElement newValue) {
			propagateModelChange(new AddAction<>(new SetContext(currentElement, role, field), newValue));
		}

		@Override
		public void onSetAdd(CtElement currentElement, CtRole role, Set field, ModifierKind newValue) {
			propagateModelChange(new AddAction<>(new SetContext(currentElement, role, field), newValue));
		}

		@Override
		public void onSetDelete(CtElement currentElement, CtRole role, Set field, CtElement oldValue) {
			propagateModelChange(new DeleteAction<>(new SetContext(currentElement, role, field), oldValue));
		}

		@Override
		public void onSetDelete(CtElement currentElement, CtRole role, Set field, ModifierKind oldValue) {
			propagateModelChange(new DeleteAction<>(new SetContext(currentElement, role, field), oldValue));
		}

		@Override
		public void onSetDeleteAll(CtElement currentElement, CtRole role, Set field, Set oldValue) {
			propagateModelChange(new DeleteAllAction(new SetContext(currentElement, role, field), oldValue));
		}
	}