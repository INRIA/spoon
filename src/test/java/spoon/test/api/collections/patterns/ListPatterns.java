package spoon.test.api.collections.patterns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtProvidedService;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.template.Local;

public class ListPatterns extends CtElementImpl {

	private List<ItemType> $field$ = CtElementImpl.emptyList();

	@Local
	public static Pattern fieldPattern(Factory factory) {
		return PatternBuilder.create(new PatternBuilderHelper(factory.Class().get(ListPatterns.class)).setTypeMember("$field$").getPatternElements())
		.configurePatternParameters(pb -> {
			pb.parameter("propertyName").byString("$field$");
			pb.parameter("itemType").byType(ItemType.class);
		}).build();
	}

	public List<ItemType> get$UCPropertyName$() {
		if ($unmodifiable$) {
			return Collections.unmodifiableList(this.$field$);
		} else {
			return this.$field$;
		}
	}
	
	@Local
	boolean $unmodifiable$;
	
	@Local
	public static Pattern getListPattern(Factory factory) {
		return PatternBuilder.create(new PatternBuilderHelper(factory.Class().get(ListPatterns.class)).setTypeMember("get$UCPropertyName$").getPatternElements())
		.configurePatternParameters(pb -> {
			pb.parameter("ucPropertyName").bySubstring("$UCPropertyName$");
			pb.parameter("propertyName").byString("$field$");
			pb.parameter("itemType").byType(ItemType.class);
			pb.parameter("annotations").byRole(CtRole.ANNOTATION, new TypeFilter<>(CtMethod.class));
			pb.parameter("isUnmodifiable").byReferenceName("$unmodifiable$").matchInlinedStatements();
		}).build();
	}

	public <T extends ListPatterns> T set$UCPropertyName$s(List<ItemType> $paramName$) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.IMPLEMENTATION_TYPE, this.$field$, new ArrayList<>(this.$field$));
		if ($paramName$ == null || $paramName$.isEmpty()) {
			this.$field$ = CtElementImpl.emptyList();
			return (T) this;
		}

		if (this.$field$ == CtElementImpl.<ItemType>emptyList()) {
			this.$field$ = new ArrayList<>();
		}
		this.$field$.clear();
		for (ItemType usedType : $paramName$) {
			this.add$UCPropertyName$(usedType);
		}

		return (T) this;
	}
	
	@Local
	public static Pattern setListPattern(Factory factory) {
		return PatternBuilder.create(new PatternBuilderHelper(factory.Class().get(ListPatterns.class)).setTypeMember("set$UCPropertyName$s").getPatternElements())
		.configurePatternParameters(pb -> {
			pb.parameter("ucPropertyName").bySubstring("$UCPropertyName$");
			pb.parameter("propertyName").byString("$field$");
			pb.parameter("paramName").byString("$paramName$");
			pb.parameter("itemType").byType(ItemType.class);
			pb.parameter("roleType").byReferenceName("IMPLEMENTATION_TYPE");
			pb.parameter("annotations").byRole(CtRole.ANNOTATION, new TypeFilter<>(CtMethod.class));
			pb.parameter("genericTypeName").byString("T");
		}).build();
	}

	public <T extends ListPatterns> T add$UCPropertyName$(ItemType $paramName$) {
		if ($paramName$ == null) {
			return (T) this;
		}
		if (this.$field$ == CtElementImpl.<ItemType>emptyList()) {
			if ($useArraySize$) {
				this.$field$ = new ArrayList<>($arraySize$);
			} else {
				this.$field$ = new ArrayList<>();
			}
		}

		if ($case1$) { 
			getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.IMPLEMENTATION_TYPE, this.$field$, $paramName$);
			$paramName$.setParent(this);
		} else {
		    $paramName$.setParent(this);
		    getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.IMPLEMENTATION_TYPE, this.$field$, $paramName$);
		}
		this.$field$.add($paramName$);
		return (T) this;
	}
	
	boolean $case1$;
	boolean $useArraySize$;
	int $arraySize$;
	

	@Local
	public static Pattern addItemPattern(Factory factory) {
		return PatternBuilder.create(new PatternBuilderHelper(factory.Class().get(ListPatterns.class)).setTypeMember("add$UCPropertyName$").getPatternElements())
		.configurePatternParameters(pb -> {
			pb.parameter("ucPropertyName").bySubstring("$UCPropertyName$");
			pb.parameter("propertyName").byString("$field$");
			pb.parameter("paramName").byString("$paramName$");
			pb.parameter("itemType").byType(ItemType.class);
			pb.parameter("roleType").byReferenceName("IMPLEMENTATION_TYPE");
			pb.parameter("annotations").byRole(CtRole.ANNOTATION, new TypeFilter<>(CtMethod.class));
			pb.parameter("genericTypeName").byString("T");
			pb.parameter("arraySize").byReferenceName("$arraySize$");
			pb.parameter("case1").byReferenceName("$case1$").matchInlinedStatements();
			pb.parameter("useArraySize").byReferenceName("$useArraySize$").matchInlinedStatements();
			
		}).build();
	}

	@Local
	@Override
	public void accept(CtVisitor visitor) {
		// TODO Auto-generated method stub
		
	}
}

interface ItemType extends CtElement {
}