/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary;

import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import com.transparent.glossary.model.SortObject;
import com.transparent.glossary.sort.CollatorDataHelper;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User: plitvak
 * Date: Aug 5, 2009
 * Time: 3:20:12 PM
 */
@Ignore
public class TestDataHelper
{
	public TestDataHelper( ApplicationContext context ) {
		this.context = context;
	}

	public Collection<SortObject> createSortedTestData()
	{
		Collection<SortObject> res	= new ArrayList<SortObject>();
		String language1			= "ENGLISH";
		String language2			= "ITALIAN";

		CollatorDataHelper helper	= (CollatorDataHelper)context.getBean( "collatorData" );
		List<String> alphabetL1		= new ArrayList<String>(helper.getCollatorData().get( language1 ).getAlphabet( false ));
		List<String> alphabetL2		= new ArrayList<String>(helper.getCollatorData().get( language2 ).getAlphabet( false ));

		Random rb = new Random();
		for( int i = 0; i < alphabetL1.size(); i++ )
		{
			SortObject objWord = new SortObject();
			objWord.setLanguageCodeL1( language1 );
			objWord.setLanguageCodeL2( language2 );
			String sL1	= alphabetL1.get(i) + "_english_word_"+i;
			String sL2	= alphabetL2.get( i < alphabetL2.size()? i: alphabetL2.size()-1 ) + "_italian_word_"+i;
			objWord.getSortFieldsL1().add( sL1 );
			objWord.getSortFieldsL2().add( sL2 );
			objWord.getFieldsToIncludeMap().put( "L1", sL1 );
			objWord.getFieldsToIncludeMap().put( "L2", sL2 );
			objWord.getFieldsToIncludeMap().put( "comment", "comment_"+i );

			res.add( objWord );

			if( rb.nextBoolean() )
			{
				SortObject objPhrase = new SortObject();
				objPhrase.setLanguageCodeL1( language1 );
				objPhrase.setLanguageCodeL2( language2 );
				sL1	= alphabetL1.get(i) + "_english_phrase_"+i;
				sL2	= alphabetL2.get( i < alphabetL2.size()? i: alphabetL2.size()-1 ) + "_italian_phrase_"+i;
				objPhrase.getSortFieldsL1().add( sL1 );
				objPhrase.getSortFieldsL2().add( sL2 );
				objPhrase.getFieldsToIncludeMap().put( "L1", sL1 );
				objPhrase.getFieldsToIncludeMap().put( "L2", sL2 );
				objPhrase.getFieldsToIncludeMap().put( "comment", "comment_"+i );
				objPhrase.setIsPhrase( true );

				res.add( objPhrase );
			}
		}

		return res;
	}

	//------------------------------------------------------------------------------------------------------------------
	private ApplicationContext context = null;
	//------------------------------------------------------------------------------------------------------------------
}
