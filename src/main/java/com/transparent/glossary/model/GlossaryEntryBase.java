/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Base class for word and phrase glossary DOM element. The both have the same structure.
 * For XML snippet see:
 * @see com.transparent.glossary.model.GlossaryWordEntry
 * @see com.transparent.glossary.model.GlossaryPhraseEntry
 *
 * User: plitvak
 * Date: Aug 5, 2009
 * Time: 1:16:09 PM
 */
@XmlType( propOrder={"key","translation","annotations", "sideOneSoundFile", "sideTwoSoundFile", "referencedBy", "partOfSpeech" } )
abstract public class GlossaryEntryBase
{
	@XmlElement
	public GlossaryKey getKey() {
		return key;
	}

	public void setKey( final GlossaryKey key ) {
		this.key = key;
	}

	@XmlElement
	public GlossaryTranslation getTranslation() {
		return translation;
	}

	public void setTranslation( final GlossaryTranslation translation ) {
		this.translation = translation;
	}

	@XmlElementWrapper( name="annotations" )
	@XmlElementRef( name="annotation" )
	public List<GlossaryAnnotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations( final List<GlossaryAnnotation> annotations ) {
		this.annotations = annotations;
	}

	@XmlAttribute( name = "L1" )
	public String getLanguageCodeL1() {
		return languageCodeL1;
	}

	public void setLanguageCodeL1( final String languageCodeL1 ) {
		this.languageCodeL1 = languageCodeL1;
	}

	@XmlAttribute( name = "L2" )
	public String getLanguageCodeL2() {
		return languageCodeL2;
	}

	public void setLanguageCodeL2( final String languageCodeL2 ) {
		this.languageCodeL2 = languageCodeL2;
	}


    @XmlElement( name = "sideOneSoundfile" )
    public String getSideOneSoundFile()
    {
        return sideOneSoundFile;
    }

    public void setSideOneSoundFile( final String sideOneSoundFile )
    {
        this.sideOneSoundFile = sideOneSoundFile;
    }


    @XmlElement( name = "sideTwoSoundfile" )
    public String getSideTwoSoundFile()
    {
        return sideTwoSoundFile;
    }

    public void setSideTwoSoundFile( final String sideTwoSoundFile )
    {
        this.sideTwoSoundFile = sideTwoSoundFile;
    }

    @XmlElement
    public String getReferencedBy()
    {
        return referencedBy;
    }

    public void setReferencedBy( final String referencedBy )
    {
        this.referencedBy = referencedBy;
    }


    @XmlElement
    public String getPartOfSpeech()
    {
        return partOfSpeech;
    }

    public void setPartOfSpeech( final String partOfSpeech )
    {
        this.partOfSpeech = partOfSpeech;
    }

	//------------------------------------------------------------------------------------------------------------------
	private GlossaryKey key							= null;
	private GlossaryTranslation translation			= null;
	private String languageCodeL1					= null;
	private String languageCodeL2					= null;
    private String sideOneSoundFile = null;
    private String sideTwoSoundFile = null;
    private String referencedBy    					= null;
    private String partOfSpeech    					= null;
	private List<GlossaryAnnotation> annotations	= null;
	//------------------------------------------------------------------------------------------------------------------
}
