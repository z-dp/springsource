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

package org.springframework.web.servlet.view.jasperreports;

import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Rob Harrop
 */
public class JasperReportsHtmlViewTests extends AbstractJasperReportsViewTests {

	protected AbstractJasperReportsView getViewImplementation() {
		return new JasperReportsHtmlView();
	}

	protected String getDesiredContentType() {
		return "text/html";
	}

	public void testConfigureExporterParametersWithPropertiesFile() {
		GenericApplicationContext applicationContext = new GenericApplicationContext();
		BeanDefinitionReader reader = new PropertiesBeanDefinitionReader(applicationContext);
		reader.loadBeanDefinitions(new ClassPathResource("view.properties", getClass()));

		AbstractJasperReportsView view = (AbstractJasperReportsView) applicationContext.getBean("report");
		view.convertExporterParameters();
		String encoding = (String) view.getConvertedExporterParameters().get(JRHtmlExporterParameter.CHARACTER_ENCODING);
		assertEquals("UTF-8", encoding);
	}

}
