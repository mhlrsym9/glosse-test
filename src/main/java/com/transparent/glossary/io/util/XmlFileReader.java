/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.io.util;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

/**
 * Utility class to simplify reading of the XML content from a file.
 *
 * User: PLitvak
 * Date: Aug 20, 2009
 * Time: 3:48:49 PM
 */
public class XmlFileReader
{
	/**
	 * Reads given file and tries to parse it in to the XML document.
	 */
	static public Document readXml( File file ) throws Exception
	{
		BufferedReader reader	= new BufferedReader( new InputStreamReader( new FileInputStream( file ), "UTF-8" ) );
		StringBuilder buf		= new StringBuilder();
		String str				= null;
		while( (str = reader.readLine()) != null ) {
			buf.append( str );
		}
		reader.close();

		str = buf.toString();
		// hack for illegal characters in creator_application element
		str = str.replaceAll( "<creator_application>(.*)?</creator_application>", "" );
		// clear all potentially unwanted characters at the beginning of the string
		str = str.substring( str.indexOf( "<" ) );
		//--------------------------------------------------------------------------

		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		return builder.parse( new InputSource( new StringReader( str ) ) );
	}

	//------------------------------------------------------------------------------------------------------------------
//	private static final String BOM	= new String( new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF} );
	//------------------------------------------------------------------------------------------------------------------
}
