/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The glossary DOM element.
 * Represented by the following XML element
 * <pre>
 * {@code
 * <glossary language="">
 * 		<letters>
 * 			<letter name="">
 * 				<words>
 * 					<word L1="" L2="">
 * 						<key/>
 * 						<translation/>
 * 						<annotations/>
 * 					</word>
 * 				</words>
 * 				<phrases/>
 * 			</letter>
 * 		</letters>
 * </glossary>
 * }
 * </pre>
 *
 * User: plitvak
 * Date: Aug 5, 2009
 * Time: 10:39:01 AM
 */
@XmlRootElement( name="word" )
public class GlossaryWordEntry extends GlossaryEntryBase
{
}
