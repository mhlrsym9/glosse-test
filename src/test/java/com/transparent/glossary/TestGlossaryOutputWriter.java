/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.io.GlossaryOutputWriter;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * User: plitvak
 * Date: Aug 4, 2009
 * Time: 11:46:40 AM
 */
public class TestGlossaryOutputWriter
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
	public void testL1Writer() throws Exception
	{
		TestDataHelper dataHelper	= new TestDataHelper( context );
		GlossaryOutputWriter writer = (GlossaryOutputWriter)context.getBean( "glossaryL1Writer" );
		File f						= new File( "testdata/glossary_L1.xml" );
		if( f.exists() ) {
			f.delete();
		}
		writer.setGlossaryFile( f );
		writer.writeGlossaryToFile( dataHelper.createSortedTestData() );

		Assert.assertTrue( f.exists() );

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document		= builder.parse( f );
		XPath xpath				= XPathFactory.newInstance().newXPath();

		String testKey			= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/key/text()", document );
		String testTranslation	= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/translation/text()", document );

		Assert.assertTrue( testKey.equals( "A_english_word_0" ) && testTranslation.equals( "A_italian_word_0" ) );
	}

	@Test
	public void testL2Wrtiter() throws Exception
	{
		TestDataHelper dataHelper	= new TestDataHelper( context );
		GlossaryOutputWriter writer = (GlossaryOutputWriter)context.getBean( "glossaryL2Writer" );
		File f						= new File( "testdata/glossary_L2.xml" );
		if( f.exists() ) {
			f.delete();
		}
		writer.setGlossaryFile( f );
		writer.writeGlossaryToFile( dataHelper.createSortedTestData() );

		Assert.assertTrue( f.exists() );

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document		= builder.parse( f );
		XPath xpath				= XPathFactory.newInstance().newXPath();

		String testKey			= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/key/text()", document );
		String testTranslation	= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/translation/text()", document );

		Assert.assertTrue( testKey.equals( "A_italian_word_0" ) && testTranslation.equals( "A_english_word_0" ) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//------------------------------------------------------------------------------------------------------------------
	private static ClassPathXmlApplicationContext context = null;
	//------------------------------------------------------------------------------------------------------------------
}