/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.io.XmlContentFileFilter;
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
public class TestXmlContentFileFilter
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
		File file = new File( "testdata/root/unit/1/italian_english_15_52_useful_words_and_phrases.xml" );
		XmlContentFileFilter filter = new XmlContentFileFilter();
		filter.setXPath( "/list/cards/*" );
		filter.setExpectedResult( "length(!=0)" );

		Assert.assertTrue( filter.accept( file ) );
	}

	@Test
	public void testNotAccept()
	{
		File file = new File( "testdata/root/unit/IEU10_known_tt.xml" );
		XmlContentFileFilter filter = new XmlContentFileFilter();
		filter.setXPath( "/list/cards/*" );
		filter.setExpectedResult( "length(!=0)" );

		Assert.assertTrue( !filter.accept( file ) );
	}

	@Test
	public void testNotAccept2()
	{
		File file = new File( "testdata/filters/grammar.xml" );
		XmlContentFileFilter filter = new XmlContentFileFilter();
		filter.setXPath( "/list/cards/*" );
		filter.setExpectedResult( "length(!=0)" );

		Assert.assertTrue( !filter.accept( file ) );
	}

	@Test
	public void testNotAccept3()
	{
		File file = new File( "testdata/filters/grammar.xml" );
		XmlContentFileFilter filter = new XmlContentFileFilter();
		filter.getPatterns().add( ".*grammar\\.xml" );

		Assert.assertTrue( !filter.accept( file ) );
	}

	//------------------------------------------------------------------------------------------------------------------
	private static ClassPathXmlApplicationContext context = null;
	//------------------------------------------------------------------------------------------------------------------
}