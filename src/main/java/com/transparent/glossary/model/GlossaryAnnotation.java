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
 * 						<translation/>
 * 						<annotations>
 * 							<annotation name=""></annotation>
 * 						</annotations>
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
 * Time: 11:00:15 AM
 */
@XmlRootElement( name="annotation" )
public class GlossaryAnnotation
{
	@XmlValue
	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation( final String annotation ) {
		this.annotation = annotation;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName( final String name ) {
		this.name = name;
	}

	//------------------------------------------------------------------------------------------------------------------
	private String annotation	= null;
	private String name			= null;
	//------------------------------------------------------------------------------------------------------------------
}