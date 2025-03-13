/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.io.GlossaryInputDataReader;
import com.transparent.glossary.model.SortObject;
import com.transparent.glossary.processor.GlossaryProcessorEntry;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User: plitvak
 * Date: Aug 4, 2009
 * Time: 11:46:40 AM
 */
public class TestGlossaryProcessorEntry
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
	public void testB4XProcessor()
	{
		GlossaryProcessorEntry b4xProcessorEntry	= (GlossaryProcessorEntry)context.getBean( "b4xGlossaryProcessorEntry" );
		Collection<SortObject> sortObjects			= new ArrayList<SortObject>();
		File file									= new File( "testdata/root/unit/1/italian_english_15_52_useful_words_and_phrases.xml" );

		for( FileFilter filter : b4xProcessorEntry.getInputReadersMap().keySet() )
		{
			if( filter.accept( file ) )
			{
				GlossaryInputDataReader inputReader = b4xProcessorEntry.getInputReadersMap().get( filter );
				inputReader.setInputFile( file );
				sortObjects.addAll(  inputReader.readSortObjects() );
			}
		}

		Assert.assertTrue( sortObjects.size() == 17 );
	}

	@Test
	public void testTTProcessor()
	{
		GlossaryProcessorEntry ttProcessorEntry	= (GlossaryProcessorEntry)context.getBean( "ttGlossaryProcessorEntry" );
		Collection<SortObject> sortObjects		= new ArrayList<SortObject>();
		File[] files							= new File[]{ 	new File( "testdata/root/unit/IEU12_known_tt.xml" ),
																new File( "testdata/root/unit/IEU12_learning_tt.xml" ) };
		for( File f : files )
		{
			for( FileFilter filter : ttProcessorEntry.getInputReadersMap().keySet() )
			{
				if( filter.accept( f ) )
				{
					GlossaryInputDataReader inputReader = ttProcessorEntry.getInputReadersMap().get( filter );
					inputReader.setInputFile( f );
					sortObjects.addAll(  inputReader.readSortObjects() );
				}
			}
		}

		Assert.assertTrue( sortObjects.size() == 40 );
	}

	@Test
	public void testB4XProcessorWithDupFilter()
	{
		GlossaryProcessorEntry b4xProcessorEntry	= (GlossaryProcessorEntry)context.getBean( "b4xGlossaryProcessorEntry" );
		Collection<SortObject> sortObjects			= new ArrayList<SortObject>();
		File file									= new File( "testdata/root/unit/1/italian_english_15_52_useful_words_and_phrases.xml" );

		for( int i = 0; i < 2; i++ )
		{
			for( FileFilter filter : b4xProcessorEntry.getInputReadersMap().keySet() )
			{
				if( filter.accept( file ) )
				{
					GlossaryInputDataReader inputReader = b4xProcessorEntry.getInputReadersMap().get( filter );
					inputReader.setInputFile( file );
					sortObjects.addAll(  inputReader.readSortObjects() );
				}
			}
		}

		b4xProcessorEntry.process( sortObjects, null );

		// word restaurant is a duplicate by itself in the italian_english_15_52_useful_words_and_phrases.xml
		Assert.assertTrue( sortObjects.size() == 16 );
	}

	@Test
	public void testTTProcessorWithMergeAndDupFilter()
	{
		GlossaryProcessorEntry ttProcessorEntry	= (GlossaryProcessorEntry)context.getBean( "ttGlossaryProcessorEntry" );
		Collection<SortObject> sortObjects		= new ArrayList<SortObject>();
		File[] files							= new File[]{ 	new File( "testdata/root/unit/IEU12_known_tt.xml" ),
																new File( "testdata/root/unit/IEU12_learning_tt.xml" ) };
		for( File f : files )
		{
			for( FileFilter filter : ttProcessorEntry.getInputReadersMap().keySet() )
			{
				if( filter.accept( f ) )
				{
					GlossaryInputDataReader inputReader = ttProcessorEntry.getInputReadersMap().get( filter );
					inputReader.setInputFile( f );
					Collection<SortObject> collectedData = inputReader.readSortObjects();
					addFileInfoToSortObjects( collectedData, f );
					sortObjects.addAll( collectedData );
				}
			}
		}

		ttProcessorEntry.process( sortObjects, null );

		Assert.assertTrue( sortObjects.size() == 19 );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void addFileInfoToSortObjects( final Collection<SortObject> sortObjects, File file )
	{
		for( SortObject so : sortObjects ) {
			so.getUserData().put( "", file.getAbsolutePath() );
		}
	}

	//------------------------------------------------------------------------------------------------------------------
	private static ClassPathXmlApplicationContext context = null;
	//------------------------------------------------------------------------------------------------------------------
}