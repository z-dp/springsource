/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.springframework.jmx.export;

import junit.framework.TestCase;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.NopInterceptor;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jmx.IJmxTestBean;
import org.springframework.jmx.JmxTestBean;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.naming.SelfNaming;
import org.springframework.jmx.support.ObjectNameManager;

import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBeanInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rob Harrop
 */
public class MBeanExporterTests extends TestCase {

	private static final String OBJECT_NAME = "spring:test=jmxMBeanAdaptor";

	public void testWithSuppliedMBeanServer() throws Exception {
		MBeanServer server = MBeanServerFactory.newMBeanServer();
		try {
			MBeanExporter adaptor = new MBeanExporter();
			adaptor.setBeans(getBeanMap());
			adaptor.setServer(server);
			adaptor.afterPropertiesSet();
			assertTrue("The bean was not registered with the MBeanServer",
					beanExists(server, ObjectNameManager.getInstance(OBJECT_NAME)));
		}
		finally {
			server.unregisterMBean(new ObjectName(OBJECT_NAME));
		}
	}

	public void testWithLocatedMBeanServer() throws Exception {
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		try {
			MBeanExporter adaptor = new MBeanExporter();
			adaptor.setBeans(getBeanMap());
			adaptor.afterPropertiesSet();
			assertTrue("The bean was not registered with the MBeanServer",
					beanExists(server, ObjectNameManager.getInstance(OBJECT_NAME)));
			server.unregisterMBean(new ObjectName(OBJECT_NAME));
		}
		finally {
			MBeanServerFactory.releaseMBeanServer(server);
		}
	}

	public void testUserCreatedMBeanRegWithDynamicMBean() throws Exception {
		Map map = new HashMap();
		map.put("spring:name=dynBean", new TestDynamicMBean());

		MBeanServer server = MBeanServerFactory.createMBeanServer();
		try {
			InvokeDetectAssembler asm = new InvokeDetectAssembler();

			MBeanExporter adaptor = new MBeanExporter();
			adaptor.setServer(server);
			adaptor.setBeans(map);
			adaptor.setAssembler(asm);
			adaptor.afterPropertiesSet();

			Object name = server.getAttribute(ObjectNameManager.getInstance("spring:name=dynBean"), "name");
			assertEquals("The name attribute is incorrect", "Rob Harrop", name);
			assertFalse("Assembler should not have been invoked", asm.invoked);
		}
		finally {
			MBeanServerFactory.releaseMBeanServer(server);
		}
	}

	public void testAutodetectMBeans() throws Exception {
		XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("autodetectMBeans.xml", getClass()));
		try {
			bf.getBean("exporter");
			MBeanServer server = (MBeanServer) bf.getBean("server");
			ObjectInstance instance = server.getObjectInstance(ObjectNameManager.getInstance("spring:mbean=true"));
			assertNotNull(instance);
		}
		finally {
			bf.destroySingletons();
		}
	}

	public void testAutodetectWithExclude() throws Exception {
		XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("autodetectMBeans.xml", getClass()));
		try {
			bf.getBean("exporter");
			MBeanServer server = (MBeanServer) bf.getBean("server");
			ObjectInstance instance = server.getObjectInstance(ObjectNameManager.getInstance("spring:mbean=true"));
			assertNotNull(instance);

			try {
				server.getObjectInstance(ObjectNameManager.getInstance("spring:mbean=false"));
				fail("MBean with name spring:mbean=false should have been excluded");
			}
			catch (InstanceNotFoundException ex) {
				// success
			}
		}
		finally {
			bf.destroySingletons();
		}
	}

	public void testAutodetectLazyMBeans() throws Exception {
		XmlBeanFactory bf = new XmlBeanFactory(new ClassPathResource("autodetectLazyMBeans.xml", getClass()));
		try {
			bf.getBean("exporter");
			MBeanServer server = (MBeanServer) bf.getBean("server");
			ObjectInstance instance = server.getObjectInstance(ObjectNameManager.getInstance("spring:mbean=true"));
			assertNotNull(instance);
		}
		finally {
			bf.destroySingletons();
		}
	}

	public void testWithMBeanExporterListeners() throws Exception {
		MockMBeanExporterListener listener1 = new MockMBeanExporterListener();
		MockMBeanExporterListener listener2 = new MockMBeanExporterListener();

		MBeanServer server = MBeanServerFactory.newMBeanServer();
		MBeanExporter adaptor = null;
		try {
			adaptor = new MBeanExporter();
			adaptor.setBeans(getBeanMap());
			adaptor.setServer(server);
			adaptor.setListeners(new MBeanExporterListener[]{listener1, listener2});
			adaptor.afterPropertiesSet();
		}
		finally {
			if (adaptor != null) {
				adaptor.destroy();
			}
		}

		assertListener(listener1);
		assertListener(listener2);
	}

	private void assertListener(MockMBeanExporterListener listener) throws MalformedObjectNameException {
		ObjectName desired = ObjectNameManager.getInstance(OBJECT_NAME);
		assertEquals("Incorrect number of registrations", 1, listener.getRegistered().size());
		assertEquals("Incorrect number of unregistrations", 1, listener.getUnregistered().size());
		assertEquals("Incorrect ObjectName in register", desired, listener.getRegistered().get(0));
		assertEquals("Incorrect ObjectName in unregister", desired, listener.getUnregistered().get(0));
	}

	public void testExportJdkProxy() throws Exception {
		JmxTestBean bean = new JmxTestBean();
		bean.setName("Rob Harrop");

		ProxyFactory factory = new ProxyFactory();
		factory.setTarget(bean);
		factory.addAdvice(new NopInterceptor());
		factory.setInterfaces(new Class[]{IJmxTestBean.class});

		IJmxTestBean proxy = (IJmxTestBean) factory.getProxy();
		String name = "bean:proxy=true";

		Map beans = new HashMap();
		beans.put(name, proxy);
		MBeanServer server = MBeanServerFactory.newMBeanServer();

		MBeanExporter exporter = new MBeanExporter();
		exporter.setServer(server);
		exporter.setBeans(beans);
		exporter.registerBeans();

		ObjectName oname = ObjectName.getInstance(name);
		Object nameValue = server.getAttribute(oname, "Name");
		assertEquals("Rob Harrop", nameValue);
	}

	public void testSelfNaming() throws Exception {
		ObjectName objectName = ObjectNameManager.getInstance(OBJECT_NAME);
		SelfNamingTestBean testBean = new SelfNamingTestBean();
		testBean.setObjectName(objectName);

		Map beans = new HashMap();
		beans.put("foo", testBean);


		MBeanServer server = MBeanServerFactory.createMBeanServer();
		try {

			MBeanExporter adaptor = new MBeanExporter();
			adaptor.setServer(server);
			adaptor.setBeans(beans);

			adaptor.afterPropertiesSet();

			ObjectInstance instance = server.getObjectInstance(objectName);
			assertNotNull(instance);
		}
		finally {
			MBeanServerFactory.releaseMBeanServer(server);
		}
	}

	public void testRegisterIgnoreExisting() throws Exception {
		ObjectName objectName = ObjectNameManager.getInstance(OBJECT_NAME);

		MBeanServer server = MBeanServerFactory.createMBeanServer();
		try {

			Person preRegistered = new Person();
			preRegistered.setName("Rob Harrop");

			server.registerMBean(preRegistered, objectName);

			Person springRegistered = new Person();
			springRegistered.setName("Sally Greenwood");

			Map beans = new HashMap();
			beans.put(objectName.toString(), springRegistered);

			MBeanExporter adaptor = new MBeanExporter();
			adaptor.setServer(server);
			adaptor.setBeans(beans);
			adaptor.setRegistrationBehavior(MBeanExporter.REGISTRATION_IGNORE_EXISTING);

			adaptor.afterPropertiesSet();

			ObjectInstance instance = server.getObjectInstance(objectName);
			assertNotNull(instance);

			// should still be the first bean with name Rob Harrop
			assertEquals("Rob Harrop", server.getAttribute(objectName, "Name"));
		}
		finally {
			MBeanServerFactory.releaseMBeanServer(server);
		}
	}

	public void testRegisterReplaceExisting() throws Exception {
		ObjectName objectName = ObjectNameManager.getInstance(OBJECT_NAME);

		MBeanServer server = MBeanServerFactory.createMBeanServer();
		try {

			Person preRegistered = new Person();
			preRegistered.setName("Rob Harrop");

			server.registerMBean(preRegistered, objectName);

			Person springRegistered = new Person();
			springRegistered.setName("Sally Greenwood");

			Map beans = new HashMap();
			beans.put(objectName.toString(), springRegistered);

			MBeanExporter adaptor = new MBeanExporter();
			adaptor.setServer(server);
			adaptor.setBeans(beans);
			adaptor.setRegistrationBehavior(MBeanExporter.REGISTRATION_REPLACE_EXISTING);

			adaptor.afterPropertiesSet();

			ObjectInstance instance = server.getObjectInstance(objectName);
			assertNotNull(instance);

			// should still be the new bean with name Sally Greenwood
			assertEquals("Sally Greenwood", server.getAttribute(objectName, "Name"));
		}
		finally {
			MBeanServerFactory.releaseMBeanServer(server);
		}
	}

	private Map getBeanMap() {
		Map map = new HashMap();
		map.put(OBJECT_NAME, new JmxTestBean());
		return map;
	}

	private boolean beanExists(MBeanServer server, ObjectName objectName) throws Exception {
		ObjectInstance inst = server.getObjectInstance(objectName);
		return (inst != null);
	}


	private static class InvokeDetectAssembler implements MBeanInfoAssembler {

		private boolean invoked = false;

		public ModelMBeanInfo getMBeanInfo(Object managedResource, String beanKey) throws JMException {
			invoked = true;
			return null;
		}
	}


	private static class MockMBeanExporterListener implements MBeanExporterListener {

		private List registered = new ArrayList();

		private List unregistered = new ArrayList();

		public void mbeanRegistered(ObjectName objectName) {
			registered.add(objectName);
		}

		public void mbeanUnregistered(ObjectName objectName) {
			unregistered.add(objectName);
		}

		public List getRegistered() {
			return registered;
		}

		public List getUnregistered() {
			return unregistered;
		}
	}

	private static class SelfNamingTestBean implements SelfNaming {

		private ObjectName objectName;

		public void setObjectName(ObjectName objectName) {
			this.objectName = objectName;
		}

		public ObjectName getObjectName() throws MalformedObjectNameException {
			return this.objectName;
		}
	}

	public static interface PersonMBean {

		String getName();
	}

	public static class Person implements PersonMBean {

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
