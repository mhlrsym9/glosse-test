/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the main data abstraction used for store sort information about the element of content.
 * It is used to store the content read from various sources, to sort the content and then to build the glossary.
 * This class is essentially just a value object.
 *
 * User: plitvak
 * Date: Jul 31, 2009
 * Time: 1:49:07 PM
 */
public class SortObject
{
	/**
	 * Customary name for the L1 content field used in the Spring configuration
	 */
	public static final String L1_FIELD_NAME = "L1";
	/**
	 * Customary name for the L2 content field used in the Spring configuration
	 */
	public static final String L2_FIELD_NAME = "L2";

	//=================================== property accessors ===========================================================
	public boolean isPhrase() {
		return isPhrase;
	}

	public void setIsPhrase( final boolean phrase ) {
		isPhrase = phrase;
	}

	public String getLanguageCodeL1() {
		return languageCodeL1;
	}

	public void setLanguageCodeL1( final String languageCodeL1 ) {
		this.languageCodeL1 = languageCodeL1;
	}

	public String getLanguageCodeL2() {
		return languageCodeL2;
	}

	public void setLanguageCodeL2( final String languageCodeL2 ) {
		this.languageCodeL2 = languageCodeL2;
	}

	public List<String> getSortFieldsL1() {
		return sortFieldsL1;
	}

	public List<String> getSortFieldsL2() {
		return sortFieldsL2;
	}

	public Map<String, String> getFieldsToIncludeMap() {
		return fieldsToIncludeMap;
	}

	/**
	 * Provides access to store/retrieve any user specific data for a given instance of a SortObject.
	 */
	public Map<String, Object> getUserData() {
		return userData;
	}


    public String getSideOneSoundFile()
    {
        return sideOneSoundFile;
    }

    public void setSideOneSoundFile( final String sideOneSoundFile )
    {
        this.sideOneSoundFile = sideOneSoundFile;
    }


    public String getSideTwoSoundFile()
    {
        return sideTwoSoundFile;
    }

    public void setSideTwoSoundFile( final String sideTwoSoundFile )
    {
        this.sideTwoSoundFile = sideTwoSoundFile;
    }

    public String getReferencedBy()
    {
        return referencedBy;
    }

    public void setReferencedBy( final String referencedBy )
    {
        this.referencedBy = referencedBy;
    }

    public String getPartOfSpeech()
    {
        return partOfSpeech;
    }

    public void setPartOfSpeech( final String partOfSpeech )
    {
        this.partOfSpeech = partOfSpeech;
    }

    //=================================== property accessors ===========================================================

	@Override
	public String toString()
	{
		return new ToStringBuilder( this ).append( languageCodeL1 )
										  .append( languageCodeL2 )
										  .append( sortFieldsL1 )
										  .append( sortFieldsL2 )
										  .append( fieldsToIncludeMap )
                                          .append( sideOneSoundFile )
                                          .append( sideTwoSoundFile )
                                          .append( partOfSpeech )
                                          .append( referencedBy ).toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//------------------------------------------------------------------------------------------------------------------
	private String languageCodeL1		= null;
	private String languageCodeL2		= null;
    private String sideOneSoundFile = null;
    private String sideTwoSoundFile = null;
    private String referencedBy    		= null;
    private String partOfSpeech    		= null;
	private List<String> sortFieldsL1	= new ArrayList<String>();
	private List<String> sortFieldsL2	= new ArrayList<String>();
	private boolean isPhrase			= false;

	private Map<String, String> fieldsToIncludeMap	= new HashMap<String, String>();
	final private Map<String, Object> userData		= new HashMap<String, Object>();
	//------------------------------------------------------------------------------------------------------------------
}
