<!--
	Markdown
	Copyright 2016 IS2T. All rights reserved.
	IS2T PROPRIETARY. Use is subject to license terms.
-->
# Overview
This project is a tool to list all dependencies of a Java code and to discover the missing dependencies of this Java code against MicroEJ OS.

# Usage
## Online
1. In your MicroEJ or Eclipse workspace, import this project and drop all your JARs into [classpath](classpath/) folder.
2. Right click on **DependencyDiscoverer** project.
3. Select **Run As -> Java Application**.
4. Double click on **DependencyDiscoverer**.
3. A **result.txt** file will be generated at the root of the project.

## Offline
1. Drop all your JARs into [classpath](classpath/) folder.
2. Get a MicroEJ offline repository (zip archive). For example repository 4.0 from [developer.microej.com/4.0/ivy](http://developer.microej.com/4.0/ivy).
3. Unzip MicroEJ repository into [againstClasspath](againstClasspath/) folder.
4. Set `OFFLINE` variable in [DependencyDiscoverer.java](src/ej/tools/dependencydiscoverer/DependencyDiscoverer.java) to `true`.
5. Right click on **DependencyDiscoverer** project.
6. Select **Run As -> Java Application**.
7. Double click on **DependencyDiscoverer**.
8. A **result.txt** file will be generated at the root of the project.

## Adding additional MicroEJ libraries.
You may have some additional MicroEJ libraries, to include them, drop them into [againstClasspath](againstClasspath) folder.

## Interpreting the results.
Open the **result.txt** file with a text editor. Each line contains a missing dependency. If the file is empty, your library is compatible with MicroEJ OS!

Each line may be :
  * A **class** described as `package.of.class.Class`.
  * An **inner class** described as `package.of.class.Class$InnerClassName` (InnerClassName is a number if it is an anonymous class).
  * A **field** described as `package.of.class.Class.fieldName`.
  * A **constructor** described as `package.of.class.Class.<init>({parameters types see under})V`.
  * A **method** described as `package.of.class.Class.methodName({parameters types see under}){return type}`.
  
The types may be:
  * **B**: byte
  * **C**: char
  * **D**: double
  * **F**: float
  * **I**: int
  * **J**: long
  * **L{ClassName};**: reference to a {ClassName} instance
  * **S**: short
  * **V**: void
  * **Z**: boolean
  * **[{type}**: array of {type} (type may be an array it self)

## Porting a library to MicroEJ OS
To port your library with missing dependencies to MicroEJ OS, the following steps shall be taken:
1. Contact MicroEJ support, some common libraries are available on demand. 
2. Refactor your libraries to avoid those dependencies.

# Requirements
* MicroEJ Studio 4.0 or later, or MicroEJ SDK4.0 or later, or Eclipse 4.2 or later.
* A JRE 7 or higher.

# Dependencies
This project depends on [DependencyDiscoverer-2.0.4.jar](lib/DependencyDiscoverer-2.0.4.jar).

# Source
N/A

# Restrictions
None.