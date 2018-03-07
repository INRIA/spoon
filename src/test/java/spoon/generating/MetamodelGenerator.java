package spoon.generating;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import spoon.Metamodel.Type;
import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtScanner;
import spoon.test.metamodel.MetamodelProperty;
import spoon.test.metamodel.MetamodelConcept;
import spoon.test.metamodel.MMTypeKind;
import spoon.test.metamodel.SpoonMetaModel;

public class MetamodelGenerator {

	public static void main(String[] args) {
		SpoonMetaModel mm = new SpoonMetaModel(new File("src/main/java"));
		mm.getFactory().getEnvironment().useTabulations(true);
		StringBuilder sb = new StringBuilder();
		for (MetamodelConcept type : mm.getConcepts()) {
			if (type.getKind()==MMTypeKind.LEAF) {
				sb.append(printType(mm.getFactory(), type));
			}
		}
		System.out.println(sb.toString());
	}
	

	private static String printType(Factory factory, MetamodelConcept type) {
		Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put("typeName", type.getName());
		valuesMap.put("ifaceName", type.getModelInterface().getQualifiedName());
		valuesMap.put("implName", type.getModelClass().getQualifiedName());
		valuesMap.put("fields", printFields(factory, type));
		
		StrSubstitutor strSubst = new StrSubstitutor(valuesMap);
		return strSubst.replace(
				"types.add(new Type(\"${typeName}\", ${ifaceName}.class, ${implName}.class, fm -> fm\n" + 
				"${fields}\n" + 
				"));\n\n");
		
	}

	private static String printFields(Factory factory, MetamodelConcept type) {
		Map<CtRole, MetamodelProperty> allFields = new LinkedHashMap<>(type.getRoleToProperty());
		List<CtRole> rolesByScanner = getRoleScanningOrderOfType(factory, (Class) type.getModelInterface().getActualClass());
		List<String> elementFields = new ArrayList<>();
		for (CtRole ctRole : rolesByScanner) {
			MetamodelProperty field = allFields.remove(ctRole);
			elementFields.add(printField(field));
		}
		//generate remaining primitive fields, sorted by Enum#ordinal of CtRole - just to have a stable order
		List<String> primitiveFields = new ArrayList<>();
		new ArrayList(allFields.keySet()).stream().sorted().forEach(role -> {
			MetamodelProperty field = allFields.remove(role);
			primitiveFields.add(printField(field));
		});
		if (allFields.isEmpty() == false) {
			throw new SpoonException("There remained some fields?");
		}
		StringBuilder sb = new StringBuilder();
		primitiveFields.addAll(elementFields);
		primitiveFields.forEach(s->sb.append(s).append('\n'));
		return sb.toString();
	}
	
	private static String printField(MetamodelProperty field) {
		Map<String, String> valuesMap = new HashMap<>();
		valuesMap.put("role", field.getRole().name());
		valuesMap.put("derived", String.valueOf(field.isDerived()));
		valuesMap.put("unsetable", String.valueOf(field.isUnsettable()));
		
		StrSubstitutor strSubst = new StrSubstitutor(valuesMap);
		return strSubst.replace("\t.field(CtRole.${role}, ${derived}, ${unsetable})");
	}


	private static List<CtRole> getRoleScanningOrderOfType(Factory factory, Class<? extends CtElement> iface) {
		List<CtRole> roles = new ArrayList<>();
		//generate fields in the same order like they are visited in CtScanner
		CtElement ele = factory.Core().create(iface);
		ele.accept(new CtScanner() {
			@Override
			public void scan(CtRole role, CtElement element) {
				roles.add(role);
			}
			@Override
			public void scan(CtRole role, Collection<? extends CtElement> elements) {
				roles.add(role);
			}
			@Override
			public void scan(CtRole role, Map<String, ? extends CtElement> elements) {
				roles.add(role);
			}
		});
		return roles;
	}

/*	
	types.add(new Type("CtClass", CtClassImpl.class, CtClass.class, fm ->
		fm.field(CtRole.NAME, false, false)
	));
*/
}
