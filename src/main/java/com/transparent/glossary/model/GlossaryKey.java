/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;
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
 *						<key language=""></key>
 * 						<translation/>
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
 * Time: 1:53:07 PM
 */
@XmlRootElement( name = "key" )
public class GlossaryKey
{
	@XmlAttribute( name = "language" )
	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode( final String languageCode ) {
		this.languageCode = languageCode;
	}

	@XmlValue
	public String getKey() {
		return key;
	}

	public void setKey( final String key ) {
		this.key = key;
	}

	//------------------------------------------------------------------------------------------------------------------
	private String key			= null;
	private String languageCode = null;
	//------------------------------------------------------------------------------------------------------------------
}
