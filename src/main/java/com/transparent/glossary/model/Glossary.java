/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.model;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * The root of the glossary DOM.
 * Represented by the following XML element
 * <pre>
 * {@code
 * <glossary language="">
 * 		<letters/>
 * </glossary>
 * }
 * </pre>
 *
 * User: plitvak
 * Date: Aug 5, 2009
 * Time: 10:34:56 AM
 */
@XmlRootElement( name="glossary" )
public class Glossary
{
	//=================================== property accessors ===========================================================
	@XmlAttribute( name="language" )
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode( final String languageCode ) {
		this.languageCode = languageCode;
	}

	@XmlElementWrapper( name="letters" )
	@XmlElementRef( name="letter" )
	public List<GlossaryLetter> getLetters() {
		return letters;
	}

	public void setLetters( final List<GlossaryLetter> letters ) {
		this.letters = letters;
	}

	@XmlAttribute( name="version" )
	public String getVersion() {
		return version;
	}

	public void setVersion( final String version ) {
		this.version = version;
	}
	//=================================== property accessors ===========================================================

	//------------------------------------------------------------------------------------------------------------------
	private String languageCode				= null;
	private String version					= null;
	private List<GlossaryLetter> letters	= null;
	//------------------------------------------------------------------------------------------------------------------
}