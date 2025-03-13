/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * The glossary DOM element.
 * Represented by the following XML element
 * <pre>
 * {@code
 * <glossary language="">
 * 		<letters>
 * 			<letter name="">
 * 				<words/>
 * 				<phrases/>
 * 			</letter>
 * 		</letters>
 * </glossary>
 * }
 * </pre>
 *
 * User: plitvak
 * Date: Aug 5, 2009
 * Time: 10:51:15 AM
 */
@XmlRootElement( name= "letter" )
@XmlType( propOrder={"words","phrases"} )
public class GlossaryLetter
{
	@XmlAttribute( name="name" )
	public String getLetterName() {
		return letterName;
	}

	public void setLetterName( final String letterName ) {
		this.letterName = letterName;
	}

	@XmlAttribute( name="isMiscBucket")
	public Boolean getIsMiscBucket() { return isMiscBucket; }

	public void setIsMiscBucket(final Boolean isMiscBucket)
	{
		this.isMiscBucket = isMiscBucket;
	}

	@XmlElementWrapper( name="words" )
	@XmlElementRef( name="word" )
	public List<GlossaryWordEntry> getWords() {
		return words;
	}

	public void setWords( final List<GlossaryWordEntry> words ) {
		this.words = words;
	}

	@XmlElementWrapper( name="phrases" )
	@XmlElementRef( name="phrase" )
	public List<GlossaryPhraseEntry> getPhrases() {
		return phrases;
	}

	public void setPhrases( final List<GlossaryPhraseEntry> phrases ) {
		this.phrases = phrases;
	}

	//------------------------------------------------------------------------------------------------------------------
	private String letterName					= null;
	private Boolean isMiscBucket                = false;
	private List<GlossaryWordEntry> words		= null;
	private List<GlossaryPhraseEntry> phrases	= null;
	//------------------------------------------------------------------------------------------------------------------
}
