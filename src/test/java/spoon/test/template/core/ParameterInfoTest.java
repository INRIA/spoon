package spoon.test.template.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.Test;

import spoon.pattern.parameter.ListParameterInfo;
import spoon.pattern.parameter.MapParameterInfo;
import spoon.pattern.parameter.ParameterInfo;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.pattern.parameter.SetParameterInfo;
import spoon.pattern.parameter.UnmodifiableParameterValueProvider;
import spoon.reflect.meta.ContainerKind;

public class ParameterInfoTest {

	@Test
	public void testParameterNames() {
		assertEquals("year", new MapParameterInfo("year").getName());
		assertEquals("year", ((ParameterInfo) new MapParameterInfo("year").setContainerKind(ContainerKind.MAP)).getName());
		assertEquals("year", new MapParameterInfo(new MapParameterInfo("year")).getName());
		assertEquals("year", new MapParameterInfo(new MapParameterInfo("year").setContainerKind(ContainerKind.MAP)).getName());
		assertEquals("year.age", new MapParameterInfo("age", new MapParameterInfo("year")).getName());
		assertEquals("year.age", new MapParameterInfo("age", new MapParameterInfo("year").setContainerKind(ContainerKind.MAP)).getName());

		assertEquals("year", ((ParameterInfo) new MapParameterInfo("year").setContainerKind(ContainerKind.LIST)).getName());
		assertEquals("year", new ListParameterInfo(new MapParameterInfo("year")).getName());
		assertEquals("year", new ListParameterInfo(new MapParameterInfo("year").setContainerKind(ContainerKind.LIST)).getName());
		assertEquals("year[7]", new ListParameterInfo(7, new MapParameterInfo("year")).getName());
		assertEquals("year[7]", new ListParameterInfo(new ListParameterInfo(7, new MapParameterInfo("year"))).getName());
		assertEquals("year[7][2]", new ListParameterInfo(2, new ListParameterInfo(7, new MapParameterInfo("year"))).getName());
		assertEquals("year[7][2].age", new MapParameterInfo("age", new ListParameterInfo(2, new ListParameterInfo(7, new MapParameterInfo("year")))).getName());
		
		assertEquals("year", ((ParameterInfo) new MapParameterInfo("year").setContainerKind(ContainerKind.SET)).getName());
		assertEquals("year", new SetParameterInfo(new MapParameterInfo("year")).getName());
		assertEquals("year", new SetParameterInfo(new MapParameterInfo("year").setContainerKind(ContainerKind.SET)).getName());

	}
	@Test
	public void testSingleValueParameterByNameIntoNullContainer() {
		ParameterInfo namedParam = new MapParameterInfo("year");
		{//adding value into null container, creates a new container with that value
			ParameterValueProvider val = namedParam.addValueAs(null, 2018);
			assertNotNull(val);
			assertEquals(map().put("year", 2018), val.asMap());
		}
	}
	@Test
	public void testSingleValueParameterByNameIntoEmptyContainer() {
		ParameterInfo namedParam = new MapParameterInfo("year");
		{//adding value into empty container, creates a new container with that value
			ParameterValueProvider empty = new UnmodifiableParameterValueProvider();
			ParameterValueProvider val = namedParam.addValueAs(empty, 2018);
			assertNotNull(val);
			assertEquals(map().put("year", 2018), val.asMap());
			//empty is still empty
			assertEquals(map(), empty.asMap());
		}
	}
	@Test
	public void testSingleValueParameterByNameWhenAlreadyExists() {
		ParameterInfo namedParam = new MapParameterInfo("year");
		{//adding value into container, which already contains that value changes nothing and returns origin container
			ParameterValueProvider oldContainer = new UnmodifiableParameterValueProvider().putValueToCopy("year", 2018);
			assertEquals(map().put("year", 2018), oldContainer.asMap());
			//it returned the same container
			assertSame(oldContainer, namedParam.addValueAs(oldContainer, 2018));
			assertEquals(map().put("year", 2018), oldContainer.asMap());
		}
	}
	@Test
	public void testSingleValueParameterByNameWhenDifferentExists() {
		ParameterInfo namedParam = new MapParameterInfo("year");
		{//adding a value into container, which already contains a different value returns null - no match
			ParameterValueProvider oldContainer = new UnmodifiableParameterValueProvider().putValueToCopy("year", 2018);
			assertNull(namedParam.addValueAs(oldContainer, 2111));
			assertNull(namedParam.addValueAs(oldContainer, 0));
			assertNull(namedParam.addValueAs(oldContainer, null));
			//old container is never changed
			assertEquals(map().put("year", 2018), oldContainer.asMap());
		}
	}
	@Test
	public void testOptionalSingleValueParameterByName() {
		ParameterInfo namedParam = new MapParameterInfo("year")
				.setMinOccurences(0);
		{//adding null value into an container with minCount == 0, returns unchanged container.
			//because minCount == 0 means that value is optional
			ParameterValueProvider container = new UnmodifiableParameterValueProvider().putValueToCopy("a", "b");
			assertSame(container, namedParam.addValueAs(container, null));
			assertEquals(map().put("a", "b"), container.asMap());
		}
	}
	@Test
	public void testMandatorySingleValueParameterByName() {
		//adding null value into an container with minCount == 1, returns null -> means NO match, null is not allowed.
		//because minCount == 0 means that value is optional
		ParameterInfo namedParam = new MapParameterInfo("year")
				.setMinOccurences(1);
		{
			ParameterValueProvider container = new UnmodifiableParameterValueProvider().putValueToCopy("a", "b");
			assertNull(namedParam.addValueAs(container, null));
			assertEquals(map().put("a", "b"), container.asMap());
		}
	}
	@Test
	public void testSingleValueParameterByNameConditionalMatcher() {
		ParameterInfo namedParam = new MapParameterInfo("year").setMatchCondition(Integer.class, i -> i > 2000);
		
		//matching value is accepted
		ParameterValueProvider val = namedParam.addValueAs(null, 2018);
		assertNotNull(val);
		assertEquals(map().put("year", 2018), val.asMap());
		//not matching value is not accepted
		assertNull(namedParam.addValueAs(null, 1000));
		assertNull(namedParam.addValueAs(null, "3000"));
		//even matching value is STILL not accepted when there is already a different value
		assertNull(namedParam.addValueAs(new UnmodifiableParameterValueProvider().putValueToCopy("year", 3000), 2018));
	}
	
	@Test
	public void testListParameterByNameIntoNull() {
		ParameterInfo namedParam = new MapParameterInfo("year").setContainerKind(ContainerKind.LIST);
		{//adding value into null container, creates a new container with List which contains that value
			ParameterValueProvider val = namedParam.addValueAs(null, 2018);
			assertNotNull(val);
			assertEquals(map().put("year", Arrays.asList(2018)), val.asMap());
		}
	}
	@Test
	public void testListParameterByNameIntoEmptyContainer() {
		ParameterInfo namedParam = new MapParameterInfo("year").setContainerKind(ContainerKind.LIST);
		{//adding value into empty container, creates a new container with List which contains that value
			ParameterValueProvider empty = new UnmodifiableParameterValueProvider();
			ParameterValueProvider val = namedParam.addValueAs(empty, 2018);
			assertNotNull(val);
			assertEquals(map().put("year", Arrays.asList(2018)), val.asMap());
			//empty is still empty
			assertEquals(map(), empty.asMap());
		}
	}
	@Test
	public void testListParameterByNameIntoEmptyContainerWithEmptyList() {
		Consumer<ParameterInfo> check = (namedParam) ->
		{//adding value into container, which already contains a empty list, creates a new container with List which contains that value
			ParameterValueProvider empty = new UnmodifiableParameterValueProvider().putValueToCopy("year", Collections.emptyList());
			
			ParameterValueProvider val = namedParam.addValueAs(empty, 2018);
			//adding same value - adds the second value again
			ParameterValueProvider val2 = namedParam.addValueAs(val, 2018);
			//adding null value - adds nothing. Same container is returned
			assertSame(val2, namedParam.addValueAs(val2, null));

			//empty is still empty
			assertEquals(map().put("year", Collections.emptyList()), empty.asMap());
			assertNotNull(val);
			assertEquals(map().put("year", Arrays.asList(2018)), val.asMap());
			assertNotNull(val2);
			assertEquals(map().put("year", Arrays.asList(2018, 2018)), val2.asMap());
		};
		//contract: it behaves like this when container kind is defined as LIST
		check.accept(new MapParameterInfo("year").setContainerKind(ContainerKind.LIST));
		//contract: it behaves like this even when container kind is not defined, so it is automatically detected from the existing parameter value
		check.accept(new MapParameterInfo("year"));
		//contract: it behaves like this when ListAccessor + NamedAccessor is used
		check.accept(new ListParameterInfo(new MapParameterInfo("year")));
		//contract: it behaves like this when ListAccessor + NamedAccessor with defined container is used with 
		check.accept(new ListParameterInfo(new MapParameterInfo("year").setContainerKind(ContainerKind.LIST)));
	}
	
	@Test
	public void testMergeOnDifferentValueTypeContainers() {
		BiConsumer<ParameterInfo, ParameterValueProvider> checker = (parameter, params) -> {
			//contract: the same value can be always applied when it already exists there independent on container type
			assertSame(params, parameter.addValueAs(params, "x"));
			//contract: the different value must be never applied independent on container type
			assertNull(parameter.addValueAs(params, "y"));
			//contract: the different value must be never applied independent on container type
			assertNull(parameter.addValueAs(params, null));
		};
		ParameterValueProvider empty = new UnmodifiableParameterValueProvider();
		checker.accept(new MapParameterInfo("year"), empty.putValueToCopy("year", "x"));
		checker.accept(new ListParameterInfo(0, new MapParameterInfo("year")), empty.putValueToCopy("year", Collections.singletonList("x")));
		checker.accept(new ListParameterInfo(1, new MapParameterInfo("year")), empty.putValueToCopy("year", Arrays.asList("zz","x")));
		checker.accept(new MapParameterInfo("key", new ListParameterInfo(1, new MapParameterInfo("year"))), empty.putValueToCopy("year", Arrays.asList("zz",empty.putValueToCopy("key", "x"))));
		checker.accept(new MapParameterInfo("key", new MapParameterInfo("year")), empty.putValueToCopy("year", empty.putValueToCopy("key", "x")));
	}

	@Test
	public void testAppendIntoList() {
		ParameterInfo parameter = new MapParameterInfo("years").setContainerKind(ContainerKind.LIST);
		ParameterValueProvider params = parameter.addValueAs(null, 1000);
		assertNotNull(params);
		assertEquals(map().put("years", Arrays.asList(1000)), params.asMap());
		
		params = parameter.addValueAs(params, 100);
		assertNotNull(params);
		assertEquals(map().put("years", Arrays.asList(1000, 100)), params.asMap());

		params = parameter.addValueAs(params, "a");
		assertNotNull(params);
		assertEquals(map().put("years", Arrays.asList(1000, 100, "a")), params.asMap());

		params = parameter.addValueAs(params, "a");
		assertNotNull(params);
		assertEquals(map().put("years", Arrays.asList(1000, 100, "a", "a")), params.asMap());
	}
	
	@Test
	public void testSetIntoList() {
		ParameterInfo named = new MapParameterInfo("years");
		ParameterValueProvider params = new ListParameterInfo(2, named).addValueAs(null, 1000);
		assertNotNull(params);
		assertEquals(map().put("years", Arrays.asList(null, null, 1000)), params.asMap());
		
		params = new ListParameterInfo(0, named).addValueAs(params, 10);
		assertNotNull(params);
		assertEquals(map().put("years", Arrays.asList(10, null, 1000)), params.asMap());

		params = new ListParameterInfo(3, named).addValueAs(params, 10000);
		assertNotNull(params);
		assertEquals(map().put("years", Arrays.asList(10, null, 1000, 10000)), params.asMap());
	}
	
	@Test
	public void testAppendIntoSet() {
		ParameterInfo parameter = new MapParameterInfo("years").setContainerKind(ContainerKind.SET);
		ParameterValueProvider params = parameter.addValueAs(null, 1000);
		assertNotNull(params);
		assertEquals(map().put("years", asSet(1000)), params.asMap());
		
		params = parameter.addValueAs(params, 100);
		assertNotNull(params);
		assertEquals(map().put("years", asSet(1000, 100)), params.asMap());

		params = parameter.addValueAs(params, "a");
		assertNotNull(params);
		assertEquals(map().put("years", asSet(1000, 100, "a")), params.asMap());

		assertSame(params, parameter.addValueAs(params, "a"));
		assertNotNull(params);
		assertEquals(map().put("years", asSet(1000, 100, "a")), params.asMap());
	}
	@Test
	public void testMapEntryInParameterByName() {
		BiConsumer<ParameterInfo, ParameterValueProvider> checker = (namedParam, empty) ->
		{//the Map.Entry value is added into property of type Map
			//only Map.Entries can be added
			assertNull(namedParam.addValueAs(empty, "a value"));

			final ParameterValueProvider val = namedParam.addValueAs(empty, entry("year", 2018));
			assertNotNull(val);
			assertEquals(map().put("map", new UnmodifiableParameterValueProvider().putValueToCopy("year", 2018)), val.asMap());

			//adding null entry changes nothing
			assertSame(val, namedParam.addValueAs(val, null));
			//adding same value changes nothing
			assertSame(val, namedParam.addValueAs(val, entry("year", 2018)));
			//adding entry value with same key, but different value - no match
			assertNull(namedParam.addValueAs(val, entry("year", 1111)));
			
			ParameterValueProvider val2 = namedParam.addValueAs(val, entry("age", "best"));
			assertNotNull(val2);
			assertEquals(map().put("map", new UnmodifiableParameterValueProvider()
					.putValueToCopy("year", 2018)
					.putValueToCopy("age", "best")), val2.asMap());
			
			//after all the once returned val is still the same - unmodified
			assertEquals(map().put("map", new UnmodifiableParameterValueProvider().putValueToCopy("year", 2018)), val.asMap());
		};
		checker.accept(new MapParameterInfo("map").setContainerKind(ContainerKind.MAP), new UnmodifiableParameterValueProvider());
		checker.accept(new MapParameterInfo("map").setContainerKind(ContainerKind.MAP), new UnmodifiableParameterValueProvider().putValueToCopy("map", null));
		checker.accept(new MapParameterInfo("map").setContainerKind(ContainerKind.MAP), new UnmodifiableParameterValueProvider().putValueToCopy("map", Collections.emptyMap()));
		//the map container is detected automatically from the type of value
		checker.accept(new MapParameterInfo("map"), new UnmodifiableParameterValueProvider().putValueToCopy("map", Collections.emptyMap()));
		//the map container is detected automatically from the type of value
		checker.accept(new MapParameterInfo("map"), new UnmodifiableParameterValueProvider().putValueToCopy("map", new UnmodifiableParameterValueProvider()));
	}
	@Test
	public void testAddMapIntoParameterByName() {
		BiConsumer<ParameterInfo, ParameterValueProvider> checker = (namedParam, empty) ->
		{//the Map value is added into property of type Map
			ParameterValueProvider val = namedParam.addValueAs(empty, Collections.emptyMap());
			assertEquals(map().put("map", new UnmodifiableParameterValueProvider()), val.asMap());
			val = namedParam.addValueAs(empty, map().put("year", 2018));
			assertEquals(map().put("map", new UnmodifiableParameterValueProvider().putValueToCopy("year", 2018)), val.asMap());
			val = namedParam.addValueAs(empty, map().put("year", 2018).put("age", 1111));
			assertEquals(map().put("map", new UnmodifiableParameterValueProvider()
					.putValueToCopy("year", 2018)
					.putValueToCopy("age", 1111)), val.asMap());

			//adding null entry changes nothing
			assertSame(val, namedParam.addValueAs(val, null));
			//adding same value changes nothing
			assertSame(val, namedParam.addValueAs(val, entry("year", 2018)));
			//adding entry value with same key, but different value - no match
			assertNull(namedParam.addValueAs(val, entry("year", 1111)));
		};
		checker.accept(new MapParameterInfo("map").setContainerKind(ContainerKind.MAP), new UnmodifiableParameterValueProvider());
		checker.accept(new MapParameterInfo("map").setContainerKind(ContainerKind.MAP), new UnmodifiableParameterValueProvider().putValueToCopy("map", null));
		checker.accept(new MapParameterInfo("map").setContainerKind(ContainerKind.MAP), new UnmodifiableParameterValueProvider().putValueToCopy("map", Collections.emptyMap()));
		//the map container is detected automatically from the type of value
		checker.accept(new MapParameterInfo("map"), new UnmodifiableParameterValueProvider().putValueToCopy("map", Collections.emptyMap()));
		//the map container is detected automatically from the type of value
		checker.accept(new MapParameterInfo("map"), new UnmodifiableParameterValueProvider().putValueToCopy("map", new UnmodifiableParameterValueProvider()));
		//the map container is detected automatically from the type of new value
		checker.accept(new MapParameterInfo("map"), null);
	}
	
	@Test
	public void testAddListIntoParameterByName() {
		BiConsumer<ParameterInfo, ParameterValueProvider> checker = (namedParam, empty) ->
		{//the List value is added into property of type List
			ParameterValueProvider val = namedParam.addValueAs(empty, Collections.emptyList());
			assertEquals(map().put("list", Collections.emptyList()), val.asMap());
			val = namedParam.addValueAs(empty, Arrays.asList(2018));
			assertEquals(map().put("list", Arrays.asList(2018)), val.asMap());
			val = namedParam.addValueAs(empty, Arrays.asList(2018, 1111));
			assertEquals(map().put("list", Arrays.asList(2018, 1111)), val.asMap());

			//adding null entry changes nothing
			assertSame(val, namedParam.addValueAs(val, null));
		};
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.LIST), new UnmodifiableParameterValueProvider());
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.LIST), new UnmodifiableParameterValueProvider().putValueToCopy("list", null));
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.LIST), new UnmodifiableParameterValueProvider().putValueToCopy("list", Collections.emptyList()));
		//Set can be converted to List
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.LIST), new UnmodifiableParameterValueProvider().putValueToCopy("list", Collections.emptySet()));
		//the list container is detected automatically from the type of value
		checker.accept(new MapParameterInfo("list"), new UnmodifiableParameterValueProvider().putValueToCopy("list", Collections.emptyList()));
		//the list container is detected automatically from the type of new value
		checker.accept(new MapParameterInfo("list"), null);
	}
	@Test
	public void testAddSetIntoParameterByName() {
		BiConsumer<ParameterInfo, ParameterValueProvider> checker = (namedParam, empty) ->
		{//the Set value is added into property of type Set
			ParameterValueProvider val = namedParam.addValueAs(empty, Collections.emptySet());
			assertEquals(map().put("list", Collections.emptySet()), val.asMap());
			val = namedParam.addValueAs(empty, asSet(2018));
			assertEquals(map().put("list", asSet(2018)), val.asMap());
			val = namedParam.addValueAs(empty, asSet(2018, 1111));
			assertEquals(map().put("list", asSet(2018, 1111)), val.asMap());

			//adding null entry changes nothing
			assertSame(val, namedParam.addValueAs(val, null));
			//adding same entry changes nothing
			assertSame(val, namedParam.addValueAs(val, 1111));
			//adding Set with same entry changes nothing
			assertSame(val, namedParam.addValueAs(val, asSet(1111)));
			//adding Set with same entry changes nothing
			assertSame(val, namedParam.addValueAs(val, asSet(2018, 1111)));
		};
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.SET), new UnmodifiableParameterValueProvider());
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.SET), new UnmodifiableParameterValueProvider().putValueToCopy("list", null));
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.SET), new UnmodifiableParameterValueProvider().putValueToCopy("list", Collections.emptySet()));
		//The container kind has higher priority, so List will be converted to Set
		checker.accept(new MapParameterInfo("list").setContainerKind(ContainerKind.SET), new UnmodifiableParameterValueProvider().putValueToCopy("list", Collections.emptyList()));
		//the list container is detected automatically from the type of value
		checker.accept(new MapParameterInfo("list"), new UnmodifiableParameterValueProvider().putValueToCopy("list", Collections.emptySet()));
		//the list container is detected automatically from the type of new value
		checker.accept(new MapParameterInfo("list"), null);
	}
	@Test
	public void testFailOnUnpectedContainer() {
		ParameterInfo namedParam = new MapParameterInfo("year").setContainerKind(ContainerKind.LIST);
		try {
			namedParam.addValueAs(new UnmodifiableParameterValueProvider().putValueToCopy("year", "unexpected"), 1);
			fail();
		} catch (Exception e) {
			//OK
		}
	}

	@Test
	public void testSetEmptyMap() {
		ParameterInfo namedParam = new MapParameterInfo("year").setContainerKind(ContainerKind.MAP);
		{//adding empty Map works
			ParameterValueProvider val = namedParam.addValueAs(null, null);
			assertNotNull(val);
			assertEquals(map().put("year", new UnmodifiableParameterValueProvider()), val.asMap());
		}
	}

	private MapBuilder map() {
		return new MapBuilder();
	}
	
	private Map.Entry<String, Object> entry(String key, Object value) {
		return new Map.Entry<String, Object>() {
			@Override
			public Object setValue(Object value) {
				throw new RuntimeException();
			}
			
			@Override
			public Object getValue() {
				return value;
			}
			
			@Override
			public String getKey() {
				return key;
			}
		};
	}
	
	class MapBuilder extends LinkedHashMap<String, Object> {
		public MapBuilder put(String key, Object value) {
			super.put(key, value);
			return this;
		}
	}
	
	private static Set asSet(Object... objects) {
		return new HashSet<>(Arrays.asList(objects));
	}
}

