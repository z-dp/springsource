/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 */
public class CollectionUtilsTests extends TestCase {

	public void testIsEmpty() {
		assertTrue(CollectionUtils.isEmpty((Set) null));
		assertTrue(CollectionUtils.isEmpty((Map) null));
		assertTrue(CollectionUtils.isEmpty(new HashMap()));
		assertTrue(CollectionUtils.isEmpty(new HashSet()));

		List list = new LinkedList();
		list.add(new Object());
		assertFalse(CollectionUtils.isEmpty(list));

		Map map = new HashMap();
		map.put("foo", "bar");
		assertFalse(CollectionUtils.isEmpty(map));
	}

	public void testHasUniqueObject() {
		List list = new LinkedList();
		list.add("myElement");
		list.add("myOtherElement");
		assertFalse(CollectionUtils.hasUniqueObject(list));

		list = new LinkedList();
		list.add("myElement");
		assertTrue(CollectionUtils.hasUniqueObject(list));

		list = new LinkedList();
		list.add("myElement");
		list.add(null);
		assertFalse(CollectionUtils.hasUniqueObject(list));

		list = new LinkedList();
		list.add(null);
		list.add("myElement");
		assertFalse(CollectionUtils.hasUniqueObject(list));

		list = new LinkedList();
		list.add(null);
		list.add(null);
		assertTrue(CollectionUtils.hasUniqueObject(list));

		list = new LinkedList();
		list.add(null);
		assertTrue(CollectionUtils.hasUniqueObject(list));

		list = new LinkedList();
		assertFalse(CollectionUtils.hasUniqueObject(list));
	}

	public void testContainsInstanceWithNullCollection() throws Exception {
		assertFalse("Must return false if supplied Collection argument is null",
				CollectionUtils.containsInstance(null, this));
	}

	public void testContainsInstanceWithInstancesThatAreEqualButDistinct() throws Exception {
		List list = new ArrayList();
		list.add(new Instance("fiona"));
		assertFalse("Must return false if instance is not in the supplied Collection argument",
				CollectionUtils.containsInstance(list, new Instance("fiona")));
	}

	public void testContainsInstanceWithSameInstance() throws Exception {
		List list = new ArrayList();
		list.add(new Instance("apple"));
		Instance instance = new Instance("fiona");
		list.add(instance);
		assertTrue("Must return true if instance is in the supplied Collection argument",
				CollectionUtils.containsInstance(list, instance));
	}

	public void testContainsInstanceWithNullInstance() throws Exception {
		List list = new ArrayList();
		list.add(new Instance("apple"));
		list.add(new Instance("fiona"));
		assertFalse("Must return false if null instance is supplied",
				CollectionUtils.containsInstance(list, null));
	}


	private static final class Instance {

		private final String name;

		public Instance(String name) {
			this.name = name;
		}

		public boolean equals(Object rhs) {
			if (this == rhs) {
				return true;
			}
			if (rhs == null || this.getClass() != rhs.getClass()) {
				return false;
			}
			Instance instance = (Instance) rhs;
			return this.name.equals(instance.name);
		}

		public int hashCode() {
			return this.name.hashCode();
		}
	}

}
