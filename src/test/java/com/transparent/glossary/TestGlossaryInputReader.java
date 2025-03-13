/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.io.GlossaryInputDataReader;
import com.transparent.glossary.model.SortObject;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.Collection;

/**
 * User: plitvak
 * Date: Aug 4, 2009
 * Time: 11:46:40 AM
 */
public class TestGlossaryInputReader
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
	public void testB4XReader()
	{
		GlossaryInputDataReader reader = (GlossaryInputDataReader)context.getBean( "b4xReader" );
		reader.setInputFile( new File( "testdata/root/unit/1/italian_english_15_52_useful_words_and_phrases.xml" ) );

		Collection<SortObject> sortObjects = reader.readSortObjects();

		Assert.assertTrue( sortObjects.size() == 17 );
	}

	@Test
	public void testTTKnownReader()
	{
		GlossaryInputDataReader reader = (GlossaryInputDataReader)context.getBean( "ttKnownReader" );
		reader.setInputFile( new File( "testdata/root/unit/IEU10_known_tt.xml" ) );

		Collection<SortObject> sortObjects = reader.readSortObjects();

		Assert.assertTrue( sortObjects.size() == 17 );
	}

	@Test
	public void testTTLearningReader()
	{
		GlossaryInputDataReader reader = (GlossaryInputDataReader)context.getBean( "ttLearningReader" );
		reader.setInputFile( new File( "testdata/root/unit/IEU10_learning_tt.xml" ) );

		Collection<SortObject> sortObjects = reader.readSortObjects();

		Assert.assertTrue( sortObjects.size() == 17 );
	}

	//------------------------------------------------------------------------------------------------------------------
	private static ClassPathXmlApplicationContext context = null;
	//------------------------------------------------------------------------------------------------------------------
}