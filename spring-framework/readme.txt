THE SPRING FRAMEWORK, release 1.2.9 (March 2007)
------------------------------------------------
http://www.springframework.org


1. INTRODUCTION

Spring is a layered Java/J2EE application framework, based on code published in "Expert One-on-One J2EE Design
and Development" by Rod Johnson (Wrox, 2002). Spring includes:

* Powerful JavaBeans-based configuration management, applying Inversion-of-Control principles. This makes
wiring up applications quick and easy. No more singletons littered throughout your codebase, no more arbitrary
properties files: one consistent and elegant approach everywhere. This core bean factory can be used in any
environment, from applets to J2EE containers.

* Generic abstraction layer for transaction management, allowing for pluggable transaction managers, and making
it easy to demarcate transactions without dealing with low-level issues. Generic strategies for JTA and a
single JDBC DataSource are included. In contrast to plain JTA or EJB CMT, Spring's transaction support is not
tied to J2EE environments.

* JDBC abstraction layer that offers a meaningful exception hierarchy (no more pulling vendor codes out of
SQLException), simplifies error handling, and greatly reduces the amount of code you'll need to write.
You'll never need to write another finally block to use JDBC again. The JDBC-oriented exceptions comply to
Spring's generic DAO exception hierarchy.

* Integration with JDO, Hibernate, TopLink, Apache OJB, and iBATIS SQL Maps: in terms of resource holders,
DAO implementation support, and transaction strategies. First-class Hibernate and JDO support with many
IoC convenience features, addressing many typical Hibernate/JDO integration issues. All of these comply
to Spring's generic transaction and DAO exception hierarchies.

* AOP functionality, fully integrated into Spring configuration management. You can AOP-enable any object
managed by Spring, adding aspects such as declarative transaction management. With Spring, you can have
declarative transaction management without EJB... even without JTA, if you're using a single database in
Tomcat or another web container without JTA support.

* Flexible MVC web application framework, built on core Spring functionality. This framework is highly
configurable via strategy interfaces, and accommodates multiple view technologies like JSP, Tiles, Velocity,
FreeMarker, iText (for PDF), and POI (for Excel). Note that a Spring middle tier can easily be combined with
a web tier based on any other web MVC framework, like Struts, WebWork, Tapestry, or JSF.

You can use all of Spring's functionality in any J2EE server, and most of it also in non-managed environments.
A central focus of Spring is to allow for reusable business and data access objects that are not tied to
specific J2EE services. Such objects can be reused across J2EE environments (web or EJB), standalone
applications, test environments, etc without any hassle.

Spring has a layered architecture; all its functionality builds on lower levels. So you can e.g. use the
JavaBeans configuration management without using the MVC framework or AOP support. But if you use the web
MVC framework or AOP support, you'll find they build on the configuration framework, so you can apply your
knowledge about it immediately.


2. RELEASE INFO

The Spring Framework requires J2SE 1.3 and J2EE 1.3 (Servlet 2.3, JSP 1.2, JTA 1.0, EJB 2.0). J2SE 1.4 is
required for building the framework; for the full build including Tiger support, J2SE 5.0 is required.
J2EE 1.2 (Servlet 2.2, JSP 1.1) is sufficient when not using Spring's JSP tag libraries or the EJB support.

Integration is provided with Log4J 1.2, CGLIB 2.1, Jakarta Commons Attributes 2.1, JMX 1.0/1.2, JCA 1.0,
Hibernate 2.1/3.0/3.1/3.2, TopLink 9.0.4/10.1.3, JDO 1.0/2.0, Apache OJB 1.0, iBATIS SQL Maps 1.3/2.0/2.1/2.2,
Caucho's Hessian & Burlap 2.1/3.0, JAX-RPC 1.1, Quartz 1.3/1.4/1.5/1.6, EHCache 1.1, JSTL 1.0/1.1, Velocity 1.4,
FreeMarker 2.3, JasperReports 1.0/1.1/1.2, Struts/Tiles 1.1/1.2/1.3, JSF 1.1, Jakarta Commons FileUpload 1.0, etc.

Release contents:
* "src" contains the general Java source files for the framework
* "mock" contains the general Java source files for Spring's mock and test classes
* "test" contains the general Java source files for Spring's test suite
* "tiger/src" contains the JDK-1.5-specific Java source files for the framework
* "tiger/test" contains the JDK-1.5-specific Java source files for Spring's test suite
* "dist" contains the Spring distribution jar files
* "lib" contains all third-party libraries needed for running the samples and/or building the framework
* "docs" contains general documentation and API javadocs
* "samples" contains demo applications

The "lib" directory is just included in the "-with-dependencies" download. Make sure to download this full
distribution ZIP file if you want to run the sample applications and/or build the framework yourself.
Ant build scripts for the framework and the samples are provided. The standard samples can be built with
the included Ant runtime by invoking the corresponding "build.bat" files (see samples subdirectories).

Latest info is available at the public website: http://www.springframework.org
Project info at the SourceForge site: http://sourceforge.net/projects/springframework

The Spring Framework is released under the terms of the Apache Software License (see license.txt).
All libraries included in the "-with-dependencies" download are subject to their respective licenses.
This product includes software developed by the Apache Software Foundation (http://www.apache.org).
This product includes software developed by Clinton Begin (http://www.ibatis.com).


3. DISTRIBUTION JAR FILES

The "dist" directory contains the following distinct jar files for use in applications. Both module-specific
jar files and a jar file with all of Spring are provided. The following list specifies the corresponding contents
and third-party dependencies. Libraries in brackets are optional, i.e. just necessary for certain functionality.

* "spring-core" (~125 KB)
- Contents: core utilities
- Dependencies: Commons Logging, (Log4J)

* "spring-beans" (~230 KB)
- Contents: JavaBeans support, bean container
- Dependencies: spring-core, (CGLIB)

* "spring-aop" (~155 KB)
- Contents: AOP framework, source-level metadata support, AOP Alliance interfaces
- Dependencies: spring-core, (spring-beans, CGLIB, Commons Attributes)

* "spring-context" (~110 KB)
- Contents: application context, validation, JNDI, UI context support
- Dependencies: spring-beans, (spring-aop, Velocity, FreeMarker, JasperReports)

* "spring-dao" (~95 KB)
- Contents: DAO support, transaction infrastructure
- Dependencies: spring-core, (spring-beans, spring-aop, spring-context, JTA)

* "spring-jdbc" (~185 KB)
- Contents: JDBC support
- Dependencies: spring-dao, spring-beans

* "spring-support" (~165 KB)
- Contents: JMX support, JCA support, scheduling support, mail support, caching support
- Dependencies: spring-beans, (spring-dao, spring-context, spring-jdbc, JMX, Quartz, JavaMail, EHCache)

* "spring-web" (~140 KB)
- Contents: web application context, multipart resolver, Struts support, JSF support, web utilities
- Dependencies: spring-context, Servlet, (JSP, JSTL, Commons FileUpload, COS, Struts, JSF)

* "spring-webmvc" (~210 KB)
- Contents: framework servlets, web MVC framework, web controllers, web views
- Dependencies: spring-web, (Tiles, iText, POI, Velocity, FreeMarker, JasperReports)

* "spring-remoting" (~140 KB)
- Contents: remoting support, EJB support, JMS support
- Dependencies: spring-aop, spring-beans, (spring-context, spring-web, Hessian, Burlap, JAX-RPC, EJB, JMS)

* "spring-orm" (~180 KB)
- Contents: iBATIS SQL Maps support, Apache OJB support, TopLink support, JDO support
- Dependencies: spring-dao, spring-beans, (spring-aop, spring-web, iBATIS SQL Maps, Apache OJB, TopLink, JDO)

* "spring-hibernate" (~190 KB)
- Contents: Hibernate 2.1 support, Hibernate 3.x support
- Dependencies: spring-dao, spring-beans, (spring-aop, spring-web, Hibernate2, Hibernate3)

* "spring" (~1900 KB)
- Contents: all of the above (note: mocks not included)
- Dependencies: all of the above

* "spring-mock" (~45 KB)
- Contents: JNDI mocks, Servlet API mocks, JUnit support
- Dependencies: spring-core

Note: To use the JSP expression language for arguments of Spring's web MVC tags, JSP 2.0 is required.
Alternatively, the Jakarta implementation of the JSTL (standard.jar) has to be available on the class path.


4. WHERE TO START?

Documentation can be found in the "docs" directory:
* the Spring reference documentation
* the Spring MVC step-by-step tutorial

Documented sample applications and skeletons can be found in "samples":
* countries
* imagedb
* jpetstore
* petclinic
* tiles-example
* webapp-minimal
* webapp-typical

PetClinic features alternative DAO implementations and application configurations for JDBC, Hibernate,
Apache OJB and Oracle TopLink, with HSQLDB and MySQL as target databases. The default PetClinic configuration
is JDBC on HSQL, which also demonstrates Spring's JMX export through exposing the CachingClinic management
interface. To to be able to build and run the Hibernate and OJB versions, the Spring distribution comes with
all required Hibernate jar files; for TopLink, the full jars need to be downloaded (see PetClinic's readme.txt).

The Spring JPetStore is an adapted version of Clinton Begin's JPetStore (available from http://www.ibatis.com).
It leverages Spring's support for the iBATIS SQL Maps to improve the original JPetStore in terms of
internal structure and wiring. On top of a Spring-managed middle tier, it offers two alternative web
tier implementations: one using Spring's web MVC plus JSTL, and one using Struts 1.2 plus JSTL. Furthermore,
it illustrates remoting via 5 different strategies: Hessian, Burlap, HTTP invoker, RMI invoker, and JAX-RPC.

The Image Database sample is a simple one-screen image management web app that illustrates various
Spring-integrated technologies: C3P0 as connection pool, BLOB/CLOB handling with MySQL and Oracle,
Velocity and FreeMarker for web views, scheduling via Quartz and Timer, and mail sending via JavaMail.

