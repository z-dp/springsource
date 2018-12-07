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

package org.springframework.mock.web;

import junit.framework.TestCase;

import org.springframework.web.util.WebUtils;

/**
 * @author Juergen Hoeller
 * @since 19.02.2006
 */
public class MockHttpServletResponseTests extends TestCase {

	public void testSetContentTypeWithNoEncoding() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setContentType("test/plain");
		assertEquals("Character encoding should be the default",
				WebUtils.DEFAULT_CHARACTER_ENCODING, response.getCharacterEncoding());
	}

	public void testSetContentTypeWithUTF8() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setContentType("test/plain; charset=UTF-8");
		assertEquals("Character encoding should be 'UTF-8'", "UTF-8", response.getCharacterEncoding());
	}

}
