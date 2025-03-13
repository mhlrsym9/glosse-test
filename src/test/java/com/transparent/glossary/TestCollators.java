/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import com.transparent.glossary.sort.CollatorData;
import com.transparent.glossary.sort.CollatorDataHelper;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: plitvak
 * Date: Aug 12, 2009
 * Time: 9:34:17 AM
 */
public class TestCollators
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
	public void testCollators() throws Exception
	{
		OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( "testresult/collator_test_out.txt" ), "UTF-8" );
		CollatorDataHelper helper = (CollatorDataHelper)context.getBean( "collatorData" );
		try
		{
			for( String lang : languagesToTest )
			{
				CollatorData collatorData 		= helper.getCollatorData().get( lang );
				List<String> letters			= collatorData.getAlphabet( true );
				List<String> shuffledLetters	= new ArrayList<String>( letters );

				Collections.shuffle( shuffledLetters );
				Assert.assertTrue( "Failed for language: "+lang, !assertEquals( letters, shuffledLetters ) );

				Collator cl = collatorData.getCollator( true );
				Collections.sort( shuffledLetters, cl );
				boolean assertRes = assertEquals( letters, shuffledLetters );
				if( !assertRes )
				{
					String failMsg		= lang + " - sort fail!";
					String origAbc		= "original:\t"+letters;
					String sortedAbc	= "sorted:\t"+shuffledLetters;
					System.out.println( failMsg );

					writer.write( failMsg );
					writer.write( "\n" );
					writer.write( origAbc+"\n" );
					writer.write( sortedAbc+"\n" );
				}
				else
				{
					System.out.println( lang + " - OK!" );
					writer.write( lang + " - OK!\n" );
				}
				Assert.assertTrue( assertRes );
			}
		}
		finally
		{
			writer.flush();
			writer.close();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static boolean assertEquals( final List<String> letters, final List<String> shuffledLetters )
	{
		boolean res = true;

		for( int i = 0; res && i < letters.size(); i++ ) {
			res = letters.get( i ).equals( shuffledLetters.get( i ) );
		}

		return res;
	}


	//------------------------------------------------------------------------------------------------------------------
	private static ClassPathXmlApplicationContext context	= null;
	private static final List<String> languagesToTest		= new ArrayList<String>();
	static
	{
		languagesToTest.add( "ARABIC");
		languagesToTest.add( "ARABIC_MSA");
		languagesToTest.add( "ARABIC_ALGERIAN" );
		languagesToTest.add( "ARABIC_EGYPTIAN" );
		languagesToTest.add( "ARABIC_LIBYAN" );
		languagesToTest.add( "ARABIC_MOROCCAN" );
		languagesToTest.add( "ARABIC_SUDANESE" );
		languagesToTest.add( "ARABIC_TUNISIAN" );
		languagesToTest.add( "BULGARIAN" );
		languagesToTest.add( "CROATIAN" );
		languagesToTest.add( "DANISH" );
		languagesToTest.add( "DARI" );
		languagesToTest.add( "ENGLISH" );
		languagesToTest.add( "ENGLISH_BRITISH" );
		languagesToTest.add( "FRENCH" );
		languagesToTest.add( "FRENCH_CANADIAN" );
		languagesToTest.add( "HEBREW" );
		languagesToTest.add( "HINDI" );
		languagesToTest.add( "IRISH" );
		languagesToTest.add( "JAPANESE" );
		languagesToTest.add( "JAPANESE_KATAKANA" );
		languagesToTest.add( "JAPANESE_HIRAGANA" );
		languagesToTest.add( "KOREAN" );
		languagesToTest.add( "PORTUGUESE_BRAZILIAN" );
		languagesToTest.add( "PORTUGUESE_EUROPEAN" );
		languagesToTest.add( "RUSSIAN" );
		languagesToTest.add( "SWAHILI" );
		languagesToTest.add( "THAI" );
		languagesToTest.add( "URDU" );
		languagesToTest.add( "DUTCH" );
		languagesToTest.add( "FARSI" );
		languagesToTest.add( "GERMAN" );
		languagesToTest.add( "GREEK" );
		languagesToTest.add( "HUNGARIAN" );
		languagesToTest.add( "INDONESIAN" );
		languagesToTest.add( "ITALIAN" );
		languagesToTest.add( "NORWEGIAN" );
		languagesToTest.add( "POLISH" );
		languagesToTest.add( "ROMANIAN" );
		languagesToTest.add( "SPANISH" );
		languagesToTest.add( "SPANISH_COLOMBIAN" );
		languagesToTest.add( "SWEDISH" );
		languagesToTest.add( "TAGALOG" );
		languagesToTest.add( "TURKISH" );
		languagesToTest.add( "VIETNAMESE" );
	}
	//------------------------------------------------------------------------------------------------------------------
}
