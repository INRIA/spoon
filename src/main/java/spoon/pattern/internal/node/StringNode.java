/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.pattern.internal.node;

import spoon.SpoonException;
import spoon.pattern.Quantifier;
import spoon.pattern.internal.DefaultGenerator;
import spoon.pattern.internal.ResultHolder;
import spoon.pattern.internal.parameter.ParameterInfo;
import spoon.support.util.ImmutableMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Delivers single String value, which is created by replacing string markers in constant String template
 * by String value of appropriate parameter.
 */
public class StringNode extends AbstractPrimitiveMatcher {
	private final String stringValueWithMarkers;
	/*
	 * Use LinkedHashMap to assure defined replacement order
	 */
	private final Map<String, ParameterInfo> tobeReplacedSubstrings = new LinkedHashMap<>();
	private ParameterInfo[] params;
	private Pattern regExpPattern;

	public StringNode(String stringValueWithMarkers) {
		this.stringValueWithMarkers = stringValueWithMarkers;
	}

	private String getStringValueWithMarkers() {
		return stringValueWithMarkers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void generateTargets(DefaultGenerator generator, ResultHolder<T> result, ImmutableMap parameters) {
		Class<?> requiredClass = result.getRequiredClass();
		if (requiredClass != null && requiredClass.isAssignableFrom(String.class) == false) {
			throw new SpoonException("StringValueResolver provides only String values. It doesn't support: " + requiredClass);
		}
		/*
		 * initial value of result String. It usually contains some substrings (markers),
		 * which are replaced by values of related parameters
		 */
		String stringValue = getStringValueWithMarkers();
		for (Map.Entry<String, ParameterInfo> requests : tobeReplacedSubstrings.entrySet()) {
			ParameterInfo param = requests.getValue();
			String replaceMarker = requests.getKey();
			ResultHolder.Single<String> ctx = new ResultHolder.Single<>(String.class);
			generator.getValueAs(param, ctx, parameters);
			String substrValue = ctx.getResult() == null ? "" : ctx.getResult();
			stringValue = substituteSubstring(stringValue, replaceMarker, substrValue);
		}
		//convert stringValue from String to type required by result and add it into result
		result.addResult((T) stringValue);
	}

	@Override
	public ImmutableMap matchTarget(Object target, ImmutableMap parameters) {
		if ((target instanceof String) == false) {
			return null;
		}
		String targetString = (String) target;
		java.util.regex.Pattern re = getMatchingPattern();
		Matcher m = re.matcher(targetString);
		if (m.matches() == false) {
			return null;
		}
		ParameterInfo[] params = getMatchingParameterInfos();
		for (int i = 0; i < params.length; i++) {
			String paramValue = m.group(i + 1);
			parameters = params[i].addValueAs(parameters, paramValue);
			if (parameters == null) {
				//two occurrences of the same parameter are matching on different value
				//whole string doesn't matches
				return null;
			}
		}
		return parameters;
	}

	/**
	 * @return The string whose occurrence in target string will be replaced by parameter value
	 */
	public ParameterInfo getParameterInfo(String replaceMarker) {
		return tobeReplacedSubstrings.get(replaceMarker);
	}

	/**
	 * Defines that this Substitution request will replace all occurrences of `replaceMarker` in target string by value of `param`
	 * @param replaceMarker the substring whose occurrences will be substituted
	 * @param param the declaration of to be replaced parameter
	 */
	public void setReplaceMarker(String replaceMarker, ParameterInfo param) {
		tobeReplacedSubstrings.put(replaceMarker, param);
	}

	/**
	 * @return {@link ParameterInfo} to replace marker map
	 */
	public Map<String, ParameterInfo> getReplaceMarkers() {
		return Collections.unmodifiableMap(tobeReplacedSubstrings);
	}

	@Override
	public void forEachParameterInfo(BiConsumer<ParameterInfo, RootNode> consumer) {
		Map<ParameterInfo, Boolean> visitedParams = new IdentityHashMap<>(tobeReplacedSubstrings.size());
		for (ParameterInfo parameterInfo : tobeReplacedSubstrings.values()) {
			//assure that each parameterInfo is called only once
			if (visitedParams.put(parameterInfo, Boolean.TRUE) == null) {
				consumer.accept(parameterInfo, this);
			}
		}
	}

	private ParameterInfo[] getMatchingParameterInfos() {
		getMatchingPattern();
		return params;
	}

	private List<Region> getRegions() {
		List<Region> regions = new ArrayList<>();
		for (Map.Entry<String, ParameterInfo> markers : tobeReplacedSubstrings.entrySet()) {
			addRegionsOf(regions, markers.getValue(), markers.getKey());
		}
		regions.sort((a, b) -> a.from - b.from);
		return regions;
	}

	private synchronized Pattern getMatchingPattern() {
		if (regExpPattern == null) {
			List<Region> regions = getRegions();
			StringBuilder re = new StringBuilder();
			List<ParameterInfo> paramsByRegions = new ArrayList<>();
			int start = 0;
			for (Region region : regions) {
				if (region.from > start) {
					re.append(escapeRegExp(getStringValueWithMarkers().substring(start, region.from)));
				} else if (start > 0) {
					throw new SpoonException("Cannot detect string parts if parameter separators are missing in pattern value: " + getStringValueWithMarkers());
				}
				re.append("(")	//start RE matching group
					.append(".*?")	//match any character, but not greedy
					.append(")");	//end of RE matching group
				paramsByRegions.add(region.param);
				start = region.to;
			}
			if (start < getStringValueWithMarkers().length()) {
				re.append(escapeRegExp(getStringValueWithMarkers().substring(start)));
			}
			regExpPattern = Pattern.compile(re.toString());
			params = paramsByRegions.toArray(new ParameterInfo[0]);
		}
		return regExpPattern;
	}

	/**
	 * Represents a to be replaced region of `getStringValueWithMarkers()`
	 */
	private static class Region {
		ParameterInfo param;
		int from;
		int to;

		Region(ParameterInfo param, int from, int to) {
			this.param = param;
			this.from = from;
			this.to = to;
		}
	}

	private void addRegionsOf(List<Region> regions, ParameterInfo param, String marker) {
		int start = 0;
		while (start < getStringValueWithMarkers().length()) {
			start = getStringValueWithMarkers().indexOf(marker, start);
			if (start < 0) {
				return;
			}
			regions.add(new Region(param, start, start + marker.length()));
			start += marker.length();
		}
	}

	/**
	 * Replaces all occurrences of `tobeReplacedSubstring` in `str` by `substrValue`
	 * @param str to be modified string
	 * @param tobeReplacedSubstring all occurrences of this String will be replaced by `substrValue`
	 * @param substrValue a replacement
	 * @return replaced string
	 */
	private String substituteSubstring(String str, String tobeReplacedSubstring, String substrValue) {
		return str.replaceAll(escapeRegExp(tobeReplacedSubstring), escapeRegReplace(substrValue));
	}

	private String escapeRegExp(String str) {
		return "\\Q" + str + "\\E";
	}

	private String escapeRegReplace(String str) {
		return str.replaceAll("\\$", "\\\\\\$");
	}

	@Override
	public boolean replaceNode(RootNode oldNode, RootNode newNode) {
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		int off = 0;
		for (Region region : getRegions()) {
			if (region.from > off) {
				sb.append(getStringValueWithMarkers().substring(off, region.from));
			}
			sb.append("${").append(region.param.getName()).append("}");
			off = region.to;
		}
		if (getStringValueWithMarkers().length() > off) {
			sb.append(getStringValueWithMarkers().substring(off));
		}
		return sb.toString();
	}

	/**
	 * Applies substring substitution to `targetNode`. Converts old node to {@link StringNode} if needed.
	 * @param targetNode
	 * @param replaceMarker
	 * @param param
	 * @return {@link StringNode} which contains all the data of origin `targetNode` and new replaceMarker request
	 */
	public static StringNode setReplaceMarker(RootNode targetNode, String replaceMarker, ParameterInfo param) {
		StringNode stringNode = null;
		if (targetNode instanceof ConstantNode) {
			ConstantNode constantNode = (ConstantNode) targetNode;
			if (constantNode.getTemplateNode() instanceof String) {
				stringNode = new StringNode((String) constantNode.getTemplateNode());
			}
		} else if (targetNode instanceof StringNode) {
			stringNode = (StringNode) targetNode;
		}
		if (stringNode == null) {
			throw new SpoonException("Cannot add StringNode");
		}
		stringNode.setReplaceMarker(replaceMarker, param);
		return stringNode;
	}

	@Override
	public Quantifier getMatchingStrategy() {
		return Quantifier.POSSESSIVE;
	}

	@Override
	public boolean isTryNextMatch(ImmutableMap parameters) {
		//it always matches only once
		return false;
	}
}
