/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * The glossary DOM element.
 * Represented by the following XML element
 * <pre>
 * {@code
 * <glossary language="">
 * 		<letters>
 * 			<letter name="">
 * 				<words/>
 * 				<phrases>
 * 					<phrase>
 *						<key/>
 * 						<translation language=""></translation>
 * 						<annotations/>
 * 					</phrase>
 * 				</phrases>
 * 			</letter>
 * 		</letters>
 * </glossary>
 * }
 * </pre>
 *
 * User: plitvak
 * Date: Aug 5, 2009
 * Time: 1:53:22 PM
 */
@XmlRootElement( name = "translation" )
public class GlossaryTranslation
{
	@XmlAttribute( name = "language" )
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode( final String languageCode ) {
		this.languageCode = languageCode;
	}

	@XmlValue
	public String getTranslation() {
		return translation;
	}

	public void setTranslation( final String translation ) {
		this.translation = translation;
	}

	//------------------------------------------------------------------------------------------------------------------
	private String translation	= null;
	private String languageCode = null;
	//------------------------------------------------------------------------------------------------------------------
}
