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

package org.springframework.web.servlet.handler;

import java.util.Collections;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

/**
 * @author Seth Ladd
 * @author Juergen Hoeller
 */
public class SimpleMappingExceptionResolverTests extends TestCase {

	private SimpleMappingExceptionResolver exceptionResolver;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private Object handler1;
	private Object handler2;
	private Exception genericException;

	protected void setUp() throws Exception {
		exceptionResolver = new SimpleMappingExceptionResolver();
		handler1 = new Object();
		handler2 = new Object();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		request.setMethod("GET");
		genericException = new Exception();
	}

	public void testSetOrder() {
		exceptionResolver.setOrder(2);
		assertEquals(2, exceptionResolver.getOrder());
	}

	public void testDefaultErrorView() {
		exceptionResolver.setDefaultErrorView("default-view");
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals("default-view", mav.getViewName());
		assertEquals(genericException, mav.getModel().get(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE));
	}

	public void testDefaultErrorViewDifferentHandler() {
		exceptionResolver.setDefaultErrorView("default-view");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler2, genericException);
		assertNull(mav);
	}

	public void testNullExceptionAttribute() {
		exceptionResolver.setDefaultErrorView("default-view");
		exceptionResolver.setExceptionAttribute(null);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals("default-view", mav.getViewName());
		assertNull(mav.getModel().get(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE));
	}

	public void testNullExceptionMappings() {
		exceptionResolver.setExceptionMappings(null);
		exceptionResolver.setDefaultErrorView("default-view");
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals("default-view", mav.getViewName());
	}

	public void testNoDefaultStatusCode() {
		exceptionResolver.setDefaultErrorView("default-view");
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
	}

	public void testSetDefaultStatusCode() {
		exceptionResolver.setDefaultErrorView("default-view");
		exceptionResolver.setDefaultStatusCode(HttpServletResponse.SC_BAD_REQUEST);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.getStatus());
	}

	public void testNoDefaultStatusCodeInInclude() {
		exceptionResolver.setDefaultErrorView("default-view");
		exceptionResolver.setDefaultStatusCode(HttpServletResponse.SC_BAD_REQUEST);
		request.setAttribute(UrlPathHelper.INCLUDE_URI_REQUEST_ATTRIBUTE, "some path");
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals(HttpServletResponse.SC_OK, response.getStatus());
	}

	public void testSimpleExceptionMapping() {
		Properties props = new Properties();
		props.setProperty("Exception", "error");
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals("error", mav.getViewName());
	}

	public void testExactExceptionMappingWithHandlerSpecified() {
		Properties props = new Properties();
		props.setProperty("java.lang.Exception", "error");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals("error", mav.getViewName());
	}

	public void testSimpleExceptionMappingWithHandlerSpecifiedButWrongHandler() {
		Properties props = new Properties();
		props.setProperty("Exception", "error");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler2, genericException);
		assertNull(mav);
	}

	public void testMissingExceptionInMapping() {
		Properties props = new Properties();
		props.setProperty("SomeFooThrowable", "error");
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertNull(mav);
	}

	public void testTwoMappings() {
		Properties props = new Properties();
		props.setProperty("java.lang.Exception", "error");
		props.setProperty("AnotherException", "another-error");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals("error", mav.getViewName());
	}

	public void testTwoMappingsOneShortOneLong() {
		Properties props = new Properties();
		props.setProperty("Exception", "error");
		props.setProperty("AnotherException", "another-error");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, genericException);
		assertEquals("error", mav.getViewName());
	}

	public void testTwoMappingsOneShortOneLongThrowOddException() {
		Exception oddException = new SomeOddException();
		Properties props = new Properties();
		props.setProperty("Exception", "error");
		props.setProperty("SomeOddException", "another-error");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, oddException);
		assertEquals("error", mav.getViewName());
	}

	public void testTwoMappingsThrowOddExceptionUseLongExceptionMapping() {
		Exception oddException = new SomeOddException();
		Properties props = new Properties();
		props.setProperty("java.lang.Exception", "error");
		props.setProperty("SomeOddException", "another-error");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, oddException);
		assertEquals("another-error", mav.getViewName());
	}

	public void testThreeMappings() {
		Exception oddException = new AnotherOddException();
		Properties props = new Properties();
		props.setProperty("java.lang.Exception", "error");
		props.setProperty("SomeOddException", "another-error");
		props.setProperty("AnotherOddException", "another-some-error");
		exceptionResolver.setMappedHandlers(Collections.singleton(handler1));
		exceptionResolver.setExceptionMappings(props);
		ModelAndView mav = exceptionResolver.resolveException(request, response, handler1, oddException);
		assertEquals("another-some-error", mav.getViewName());
	}


	private static class SomeOddException extends Exception {

	}


	private static class AnotherOddException extends Exception {

	}

}
