/*
 * Copyright 2002-2005 the original author or authors.
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

package org.springframework.remoting;

/**
 * RemoteAccessException subclass to be thrown in case of a lookup failure,
 * typically if the lookup happens on demand for each method invocation.
 *
 * @author Juergen Hoeller
 * @since 1.1
 */
public class RemoteLookupFailureException extends RemoteAccessException {

	/**
	 * Constructor for RemoteLookupFailureException.
	 * @param msg the detail message
	 * @param ex root cause from remoting API in use
	 */
	public RemoteLookupFailureException(String msg, Throwable ex) {
		super(msg, ex);
	}

}
