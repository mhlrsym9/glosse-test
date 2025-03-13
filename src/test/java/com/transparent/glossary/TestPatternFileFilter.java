/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.io.PatternFileFilter;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * User: plitvak
 * Date: Aug 4, 2009
 * Time: 11:46:40 AM
 */
public class TestPatternFileFilter
{
	@BeforeClass
	static public void startUp()
	{
		context = new ClassPathXmlApplicationContext( "glottal-config.xml" );
		context.refresh();
	}

	@AfterClass
	static public void shutDown() {
		context.destroy();
	}

	@Test
	public void testAccept()
	{
		File file1 = new File( "testdata/root/unit/IEU10_known_tt.xml" );
		File file2 = new File( "testdata/root/unit/IEU10_learning_tt.xml" );
		PatternFileFilter filter = new PatternFileFilter();
		filter.getPatterns().add( ".*_known_tt\\.xml" );
		filter.getPatterns().add( ".*_learning_tt\\.xml" );

		Assert.assertTrue( filter.accept( file1 ) && filter.accept( file2 ) );
	}

	@Test
	public void testNotAccept()
	{
		File file					= new File( "testdata/root/unit/1/italian_english_15_52_useful_words_and_phrases.xml" );
		PatternFileFilter filter	= new PatternFileFilter();
		filter.getPatterns().add( ".*_known_tt\\.xml" );
		filter.getPatterns().add( ".*_learning_tt\\.xml" );

		Assert.assertTrue( !filter.accept( file ) );
	}

	//------------------------------------------------------------------------------------------------------------------
	private static ClassPathXmlApplicationContext context = null;
	//------------------------------------------------------------------------------------------------------------------
}