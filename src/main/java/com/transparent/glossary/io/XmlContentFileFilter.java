/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.io;

import com.transparent.glossary.io.util.XmlFileReader;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents file filter that filters XML files based on a specific content.
 * The content is detected based on the XPath.
 *
 * User: plitvak
 * Date: Aug 3, 2009
 * Time: 11:45:09 AM
 */
public class XmlContentFileFilter extends PatternFileFilter
{
	//=================================== property accessors ===========================================================
	public String getXPath() {
		return xPath;
	}

	public void setXPath( final String xPath ) {
		this.xPath = xPath;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	/**
	 * This sets an expression that describes an expected result of the XPath evaluation against the tested files'
	 * content.
	 * The exprssion consists of a function name and the expected outcome in parentesis.
	 * Currently the following functions are supported:
	 * length
	 * size
	 * value
	 * The length and size are synonims. The operators that are recognized with these functions are != and ==
	 * Here is the example of the length expression: length(!=0).
	 * The value expression has no perators and specified like the following: value(some_text).
	 * Argument of the length/size expression is always treated as operator plus integer value.
	 * Argument of the value expression is always treated as a string.
	 */
	public void setExpectedResult( final String expectedResult ) {
		this.expectedResult = expectedResult;
	}
	//================================== property accessors ============================================================

	/**
	 * Accepts only files that have XML extension and also have specific content at the given XPath.
	 */
	@Override
	public boolean accept( final File pathname )
	{
		boolean res = false;
		// treat patterns as exclude file patterns
		boolean acceptedByExcludePattern = this.getPatterns().size() > 0 && super.accept( pathname );
		if( !acceptedByExcludePattern && pathname.getAbsolutePath().endsWith( "xml" ) )
		{
			try
			{
				XPath xpath	= XPathFactory.newInstance().newXPath();

				res = evaluate( xpath, XmlFileReader.readXml( pathname ) );
			}
			catch( Exception e )
			{
				String msg = "Error when trying to detect conent of the file: " + pathname;
				logger.error( msg, e );
				throw new RuntimeException( e );
			}
		}

		return res;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This method evalueates a given XPath and then checks if the value evaluated by the XPath matches expected
	 * expression.
	 */
	private boolean evaluate( final XPath xpath, final Document doc ) throws XPathExpressionException
	{
		boolean res = false;

		String[] functionData = expectedResult.split( "[\\(|\\)]" );
		assert functionData.length == 2;

		String function	= functionData[0];
		String argument	= functionData[1];
		// check the length/size expression
		if( function.equals( "length" ) || function.equals( "size" ) )
		{
			NodeList nodes = (NodeList)xpath.evaluate( xPath, doc, expressionToNodeTypeMap.get( function ) );
			if( nodes != null )
			{
				if( argument.startsWith( "!=" ) )
				{
					String argData	= argument.substring( 2 );
					res				= nodes.getLength() != Integer.parseInt( argData );
				}
				else if( argument.startsWith( "==" ) )
				{
					String argData	= argument.substring( 2 );
					res				= nodes.getLength() == Integer.parseInt( argData );
				}
			}
		}
		// check value expression
		else if( function.equals( "value" ) )
		{
			String nodeValue	= (String)xpath.evaluate( xPath, doc, expressionToNodeTypeMap.get( function ) );
			String argData		= argument.substring( 2 );
			res					= String.valueOf( nodeValue ).equals( argData );
		}
		else
		{
			String message = "Unrecognized function: " + function;
			logger.error( message );
			throw new RuntimeException( message );
		}

		return res;
	}

	//------------------------------------------------------------------------------------------------------------------
	private String xPath			= null;
	private String expectedResult	= null;

	private Logger logger = Logger.getLogger( this.getClass() );

	private static final Map<String,QName> expressionToNodeTypeMap = new HashMap<String,QName>();
	static
	{
		expressionToNodeTypeMap.put( "length", XPathConstants.NODESET );
		expressionToNodeTypeMap.put( "size", XPathConstants.NODESET );
		expressionToNodeTypeMap.put( "value", XPathConstants.STRING );
	}
	//------------------------------------------------------------------------------------------------------------------
}