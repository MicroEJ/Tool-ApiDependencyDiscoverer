..
	Copyright 2016-2020 MicroEJ Corp. All rights reserved.
	This library is provided in source code for use, modification and test, subject to license terms.
	Any modification of the source code will break MicroEJ Corp. warranties on the whole library.
	
Overview
========

This project is a tool that lists, for a given Java code, all the
dependencies that are not available in MicroEJ libraries.

Usage
=====

Online
------

#. In your MicroEJ or Eclipse workspace, import this project and drop
   all your JARs into `classpath <classpath/>`__ folder.
#. Right click on **DependencyDiscoverer** project.
#. Select **Run As -> Java Application**.
#. Double click on **DependencyDiscoverer**.
#. A **result.txt** file will be generated at the root of the project.

Offline
-------

#. In your MicroEJ or Eclipse workspace, import this project and drop
   all your JARs into `classpath <classpath/>`__ folder.
#. Get a MicroEJ offline repository (zip archive). For example
   repository from
   `repository.microej.com <https://repository.microej.com/>`__.
#. Unzip MicroEJ repository into
   `againstClasspath <againstClasspath/>`__ folder.
#. Set ``OFFLINE`` field in
   `DependencyDiscovererOptions.java <src/com/microej/tool/dependencydiscoverer/DependencyDiscovererOptions.java>`__
   to ``true``.
#. Right click on **DependencyDiscoverer** project.
#. Select **Run As -> Java Application**.
#. Double click on **DependencyDiscoverer**.
#. The **result.txt** file is available at the root of the project.

Adding additional MicroEJ libraries.
------------------------------------

You may have some additional MicroEJ libraries, to include them, drop
them into `againstClasspath <againstClasspath>`__ folder.

Interpreting the results.
-------------------------

Open the **result.txt** file with a text editor. Each line contains a
missing dependency. If the file is empty, your library is compatible
with MicroEJ libraries!

Each line may be :

-  A **class** described as ``package.of.class.Class``.
-  An **inner class** described as
   ``package.of.class.Class$InnerClassName`` (InnerClassName is a number
   if it is an anonymous class).
-  A **field** described as ``package.of.class.Class.fieldName``.
-  A **constructor** described as
   ``package.of.class.Class.<init>({parameters types see under})V``.
-  A **method** described as
   ``package.of.class.Class.methodName({parameters types see under}){return type}``.

The types may be:

-  **B**: byte
-  **C**: char
-  **D**: double
-  **F**: float
-  **I**: int
-  **J**: long
-  **L{ClassName};**: reference to a {ClassName} instance
-  **S**: short
-  **V**: void
-  **Z**: boolean
-  **[{type}**: array of {type} (type may be an array itself)

Requirements
============

-  MicroEJ Studio 5.0 or later, or MicroEJ SDK 5.0 or later, or Eclipse
   4.7 or later.
-  A JRE 7 or higher.

Dependencies
============

None.

Source
======

N/A

Restrictions
============

None.
