/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.model.SortObject;
import com.transparent.glossary.processor.GlossaryProcessor;
import com.transparent.glossary.processor.StopWordProcessingType;
import com.transparent.glossary.sort.SortDirection;
import com.transparent.glossary.sort.SortType;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: plitvak
 * Date: Aug 4, 2009
 * Time: 11:46:40 AM
 */
public class TestGlossaryProcessor
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
	public void testDataCollection()
	{
		GlossaryProcessor processor = (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( new File( "testdata/root" ) );

		Collection<SortObject> sortObjects = processor.collectUnsortedGlossaryData();

		Assert.assertTrue( sortObjects.size() == 74 );
	}


	@Test
	public void testWordPhraseCollation()
	{
		GlossaryProcessor processor = (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( new File( "testdata/root" ) );

		Collection<SortObject> sortObjects = processor.collectUnsortedGlossaryData();
		Assert.assertTrue( sortObjects.size() == 74 );

		Collection<SortObject> words	= new ArrayList<SortObject>();
		Collection<SortObject> phrases	= new ArrayList<SortObject>();
		processor.splitDataToWordsAndPhrases(sortObjects, words, phrases);

		Assert.assertTrue( words.size() == 26 );
		Assert.assertTrue( phrases.size() == 48 );
	}

	@Test
	public void testAllStopWordProcessing()
	{
		GlossaryProcessor processor = (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( new File( "testdata/stopwords" ) );
		processor.setStopWordProcessingType( StopWordProcessingType.ALL ); // this is also a default

		Collection<SortObject> sortObjects = processor.collectUnsortedGlossaryData();
		Assert.assertTrue( sortObjects.size() == 2 );

		List<SortObject> words		= new ArrayList<SortObject>();
		List<SortObject> phrases	= new ArrayList<SortObject>();
		processor.splitDataToWordsAndPhrases(sortObjects, words, phrases);

		Assert.assertTrue( words.size() == 1 );
		Assert.assertTrue( phrases.size() == 1 );

		Assert.assertTrue( words.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "stop word" ) );
		Assert.assertTrue( phrases.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "This is  test of  stop words in  phrase" ) );
	}

	@Test
	public void testWordStopWordProcessing()
	{
		GlossaryProcessor processor = (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( new File( "testdata/stopwords" ) );
		processor.setStopWordProcessingType( StopWordProcessingType.WORDS );

		Collection<SortObject> sortObjects = processor.collectUnsortedGlossaryData();
		Assert.assertTrue( sortObjects.size() == 2 );

		List<SortObject> words		= new ArrayList<SortObject>();
		List<SortObject> phrases	= new ArrayList<SortObject>();
		processor.splitDataToWordsAndPhrases(sortObjects, words, phrases);

		Assert.assertTrue( words.size() == 1 );
		Assert.assertTrue( phrases.size() == 1 );

		Assert.assertTrue( words.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "stop word" ) );
		Assert.assertTrue( phrases.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "This is a test of the stop words in the phrase." ) );
	}

	@Test
	public void testPhraseStopWordProcessing()
	{
		GlossaryProcessor processor = (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( new File( "testdata/stopwords" ) );
		processor.setStopWordProcessingType( StopWordProcessingType.PHRASES );

		Collection<SortObject> sortObjects = processor.collectUnsortedGlossaryData();
		Assert.assertTrue( sortObjects.size() == 2 );

		List<SortObject> words		= new ArrayList<SortObject>();
		List<SortObject> phrases	= new ArrayList<SortObject>();
		processor.splitDataToWordsAndPhrases(sortObjects, words, phrases);

		Assert.assertTrue( words.size() == 1 );
		Assert.assertTrue( phrases.size() == 1 );

		Assert.assertTrue( words.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "The stop word" ) );
		Assert.assertTrue( phrases.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "This is  test of  stop words in  phrase" ) );
	}

	@Test
	public void testNoneStopWordProcessing()
	{
		GlossaryProcessor processor = (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( new File( "testdata/stopwords" ) );
		processor.setStopWordProcessingType( StopWordProcessingType.NONE );

		Collection<SortObject> sortObjects = processor.collectUnsortedGlossaryData();
		Assert.assertTrue( sortObjects.size() == 2 );

		List<SortObject> words		= new ArrayList<SortObject>();
		List<SortObject> phrases	= new ArrayList<SortObject>();
		processor.splitDataToWordsAndPhrases(sortObjects, words, phrases);

		Assert.assertTrue( words.size() == 1 );
		Assert.assertTrue( phrases.size() == 1 );

		Assert.assertTrue( words.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "The stop word" ) );
		Assert.assertTrue( phrases.get(0).getSortFieldsL1().get(0).equalsIgnoreCase( "This is a test of the stop words in the phrase." ) );
	}

	@Test
	public void testGlossaryCreation() throws Exception
	{
		GlossaryProcessor processor = (GlossaryProcessor)context.getBean( "glossaryProcessor" );
		processor.setInputRootFolder( new File( "testdata/root" ) );
//		processor.setInputRootFolder( new File( "H:\\Content\\GLPC\\Hungarian" ) );

		List<SortObject> unsortedData = processor.collectUnsortedGlossaryData();

		File glossaryL1File = new File( "testdata/glossary_L1.xml" );
		if( glossaryL1File.exists() ) {
			glossaryL1File.delete();
		}
		File glossaryL2File = new File( "testdata/glossary_L2.xml" );
		if( glossaryL2File.exists() ) {
			glossaryL2File.delete();
		}

		processor.sort( unsortedData, SortType.L1, SortDirection.ASC );
		processor.writeGlossary( glossaryL1File, SortType.L1, unsortedData );

		processor.sort( unsortedData, SortType.L2, SortDirection.ASC );
		processor.writeGlossary( glossaryL2File, SortType.L2, unsortedData );


		//
		validate_testGlossaryCreation( glossaryL1File, glossaryL2File );
	}

	@Ignore
	private void validate_testGlossaryCreation( File glossaryL1File, File glossaryL2File ) throws Exception
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		XPath xpath				= XPathFactory.newInstance().newXPath();

		Document document		= builder.parse( glossaryL1File );
		String testKey			= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/key/text()", document );
		String testTranslation	= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/translation/text()", document );

		Assert.assertTrue( testKey.equals( "appetizer" ) && testTranslation.equals( "l'antipasto" ) );

		document		= builder.parse( glossaryL2File );
		testKey			= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/key/text()", document );
		testTranslation	= xpath.evaluate( "/glossary/letters/letter[1]/words/word[1]/translation/text()", document );

		Assert.assertTrue( testKey.equals( "caldo" ) && testTranslation.equals( "hot" ) );
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	//------------------------------------------------------------------------------------------------------------------
	private static ClassPathXmlApplicationContext context = null;
	//------------------------------------------------------------------------------------------------------------------
}
