/*
 * Copyright 2002-2004 the original author or authors.
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

package org.springframework.aop.framework;

import java.lang.reflect.Method;

import org.springframework.aop.AfterReturningAdvice;


/**
 * Simple before advice example that we can use for counting checks.
 * 
 * @author Rod Johnson
 * @version $Id: CountingAfterReturningAdvice.java,v 1.3 2004/03/19 18:43:17 johnsonr Exp $
 */
public class CountingAfterReturningAdvice extends MethodCounter implements AfterReturningAdvice {
	public void afterReturning(Object o, Method m, Object[] args, Object target) throws Throwable {
		count(m);
	}
}