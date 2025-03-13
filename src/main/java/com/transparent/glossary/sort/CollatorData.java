/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.sort;

import org.apache.log4j.Logger;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.*;

/**
 * This class holds information about a collator.
 * It associates collator with a language, its Java locale (in one exists for the language) and also collation rules
 * if applicable.
 * Also this class holds an association of a collator with language alphabet and the stop words.
 *
 * User: plitvak
 * Date: Jul 30, 2009
 * Time: 1:51:35 PM
 */
public class CollatorData
{
	/**
	 * Languages that don't have Java locale defined should set their collator data locale to this value.
	 */
	public static final String NO_JAVA_LOCALE = "_NA_";

	//===================================== property assessors ==========================================================
	public void setAlphabet( final String alphabet ) {
		this.alphabet = alphabet;
	}

	public void setLetterGroups (final String letterGroups ) { this.letterGroups = letterGroups; }

	public void setMiscBucketLabel (final String miscBucketLabel) {this.miscBucketLabel = miscBucketLabel; }

	public String getTplLanguageCode() {
		return tplLanguageCode;
	}

	public void setTplLanguageCode( final String tplLanguageCode ) {
		this.tplLanguageCode = tplLanguageCode;
	}

	public String getJavaLocaleCode() {
		return javaLocaleCode;
	}

	public void setJavaLocaleCode( final String javaLocaleCode ) {
		this.javaLocaleCode = javaLocaleCode;
	}

	public String getCollationRules() {
		return collationRules;
	}

	public void setCollationRules( final String collationRules ) {
		this.collationRules = collationRules;
	}

	public String getStopWords() {
		return stopWords;
	}

	public void setStopWords( final String stopWords ) {
		this.stopWords = stopWords;
	}
	//===================================== property assessors ==========================================================

	/**
	 * This method caches the returned collator and will not create it again unless the rebuild is set to true.
	 */
	public Collator getCollator( boolean rebuild )
	{
		Collator res = null;

		if( rebuild || cachedCollator == null ) {
			res = cachedCollator = makeCollator();
		}
		else {
			res = cachedCollator;
		}

		return res;
	}

	/**
	 * This method caches the list of the stop words and will not create it again unless the rebuild is set to true.
	 */
	public List<String> getStopWords( boolean rebuild )
	{
		List<String> res = null;

		if( rebuild || cachedStopWords == null ) {
			res = cachedStopWords = parseStopWordsData();
		}
		else {
			res = cachedStopWords;
		}

		return res;
	}

	/**
	 * This method caches the list of the alphabet letters and will not create it again unless the rebuild is set to true.
	 */
	public List<String> getAlphabet( boolean rebuild )
	{
		List<String> res = null;

		if( rebuild || cachedAlphabet == null ) {
			res = cachedAlphabet = parseAlphabet();
		}
		else {
			res = cachedAlphabet;
		}

		return res;
	}

	public List<String> getLetterGroups(boolean rebuild)
	{
		List<String> res = null;
		if (rebuild || cachedLetterGroups == null) {
			res = cachedLetterGroups = parseLetterGroups();
		}
		else {
			res = cachedLetterGroups;
		}

		return res;
	}

	public String getMiscBucketLabel()
	{
		if (miscBucketLabel == null || miscBucketLabel.isEmpty())
			return "*";
		else
			return miscBucketLabel;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a collator based on the language.
	 * For languages that don't have Java locales the collation rules must be specified.
	 */
	private Collator makeCollator()
	{
		Collator res = null;

		if( javaLocaleCode.equals( NO_JAVA_LOCALE ) )
		{
			assert collationRules != null;

			try {
				res = new RuleBasedCollator( collationRules );
			}
			catch( ParseException e )
			{
				log.error( e );
				throw new RuntimeException( e );
			}
		}
		else
		{
			assert javaLocaleCode != null;

			res = Collator.getInstance( new Locale( javaLocaleCode ) );
		}

		return res;
	}


	private List<String> parseStopWordsData() {
		return stopWords != null? Arrays.asList( stopWords.split( " |\t" ) ): new ArrayList<String>();
	}

	private List<String> parseAlphabet() {
		return alphabet != null? Arrays.asList( alphabet.split( " |\t" ) ): new ArrayList<String>();
	}

	private List<String> parseLetterGroups() {
		List<String> rawLetterGroups = letterGroups != null ? Arrays.asList(letterGroups.split("\\|")) : new ArrayList<String>();
		List<String> collapsedLetterGroups = new ArrayList<String>();
		for (String letterGroup : rawLetterGroups)
		{
			List<String> rawLetters = Arrays.asList(letterGroup.split(" |\t"));
			if (!rawLetters.isEmpty())
			{
				StringBuilder builder = new StringBuilder();
				for(String s : rawLetters) {
					builder.append(s);
				}

				collapsedLetterGroups.add(builder.toString());
			}
		}

		return collapsedLetterGroups;
	}

	//------------------------------------------------------------------------------------------------------------------
	private String tplLanguageCode	= null;
	private String javaLocaleCode	= NO_JAVA_LOCALE;
	private String collationRules	= null;
	private String stopWords		= null;
	private String alphabet			= null;
	private String letterGroups     = null;
	private String miscBucketLabel  = null;

	private Collator cachedCollator			= null;
	private List<String> cachedStopWords	= null;
	private List<String> cachedAlphabet		= null;
	private List<String> cachedLetterGroups = null;

	private final Logger log = Logger.getLogger( this.getClass() );
	//------------------------------------------------------------------------------------------------------------------
}