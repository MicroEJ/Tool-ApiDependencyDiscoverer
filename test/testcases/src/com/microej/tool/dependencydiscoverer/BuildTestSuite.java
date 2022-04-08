/*
 * Java
 *
 * Copyright 2021-2022 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
package com.microej.tool.dependencydiscoverer;

import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Categories.class)
@ExcludeCategory(IgnoredTest.class)
@SuiteClasses({ Tests.class, TestsXml.class, TestsXmlWriter.class, TestsJsonWriter.class, TestsCLI.class,
	TestsClassfinder.class, TestModuleInfo.class })
public class BuildTestSuite {

}
