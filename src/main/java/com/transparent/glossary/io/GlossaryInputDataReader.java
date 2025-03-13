/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.io;

import com.transparent.glossary.io.util.XmlFileReader;
import com.transparent.glossary.model.SortObject;
import com.transparent.glossary.sort.CollatorDataHelper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.*;

/**
 * This class is used to read various content files (b4x, tt, etc) and produce a collection of sortable objects based on
 * the reader's rules.
 * @see com.transparent.glossary.model.SortObject
 * This reader is very flexible and can accomodate a number of XML based input content formats. It uses XPath expressions
 * to read the data.
 *
 * User: plitvak
 * Date: Jul 31, 2009
 * Time: 1:28:24 PM
 */
public class GlossaryInputDataReader
{
	//===================================== property assessors ==========================================================
	public File getInputFile() {
		return inputFile;
	}

	public void setInputFile( final File inputFile ) {
		this.inputFile = inputFile;
	}

	public String getLanguageCodeL1XPath() {
		return languageCodeL1XPath;
	}

	/**
	 * Sets the XPath to query L1 code from the input content
	 */
	public void setLanguageCodeL1XPath( final String languageCodeL1XPath ) {
		this.languageCodeL1XPath = languageCodeL1XPath;
	}

	public String getLanguageCodeL2XPath() {
		return languageCodeL2XPath;
	}

	/**
	 * Sets the XPath to query L2 code from the input content
	 */
	public void setLanguageCodeL2XPath( final String languageCodeL2XPath ) {
		this.languageCodeL2XPath = languageCodeL2XPath;
	}

	public String getDataObjectCollectionXPath() {
		return dataObjectCollectionXPath;
	}

	/**
	 * Sets the XPath to query the collection of the XML elements that will be used to create sort objects
	 */
	public void setDataObjectCollectionXPath( final String dataObjectCollectionXPath ) {
		this.dataObjectCollectionXPath = dataObjectCollectionXPath;
	}

	public List<String> getSortFieldsL1XPaths() {
		return sortFieldsL1XPaths;
	}

	/**
	 * Sets the list of XPath expressions to query sortable content for L1 language. These XPath expressions are
	 * relative to the elements retrieved by the XPath set by setDataObjectCollectionXPath()
	 */
	public void setSortFieldsL1XPaths( final List<String> sortFieldsL1XPaths ) {
		this.sortFieldsL1XPaths = sortFieldsL1XPaths;
	}

	public List<String> getSortFieldsL2XPaths() {
		return sortFieldsL2XPaths;
	}

	/**
	 * Sets the list of XPath expressions to query sortable content for L2 language. These XPath expressions are
	 * relative to the elements retrieved by the XPath set by setDataObjectCollectionXPath()
	 */
	public void setSortFieldsL2XPaths( final List<String> sortFieldsL2XPaths ) {
		this.sortFieldsL2XPaths = sortFieldsL2XPaths;
	}

	public Map<String, String> getFieldsToIncludeMap() {
		return fieldsToIncludeXPaths;
	}

	/**
	 * Sets the list of XPath expressions to query content for the fields that need to be included in the result.
	 */
	public void setFieldsToIncludeMap( final Map<String, String> fieldsToIncludeXPaths ) {
		this.fieldsToIncludeXPaths = fieldsToIncludeXPaths;
	}

	public CollatorDataHelper getCollatorHelper() {
		return collatorHelper;
	}

	public void setCollatorHelper( final CollatorDataHelper collatorHelper ) {
		this.collatorHelper = collatorHelper;
	}

    public void setSideOneSoundUrlXPath( final String sideOneSoundUrlXPath )
    {
        this.sideOneSoundUrlXPath = sideOneSoundUrlXPath;
    }

    public void setSideTwoSoundUrlXPath( final String sideTwoSoundUrlXPath )
    {
        this.sideTwoSoundUrlXPath = sideTwoSoundUrlXPath;
    }

    public void setNameXPath( final String nameXPath )
    {
        this.nameXPath = nameXPath;
    }

	public void setSideTwoPartOfSpeechXPath(final String sideTwoPartOfSpeechXPath)
	{
		this.sideTwoPartOfSpeechXPath = sideTwoPartOfSpeechXPath;
	}

	//===================================== property assessors ==========================================================

	/**
	 * Reads the input content and produces a collection of the sortable objects.
	 * @see com.transparent.glossary.model.SortObject
	 */
	public Collection<SortObject> readSortObjects()
	{
		Collection<SortObject> res = new ArrayList<SortObject>();

		try
		{
			theStatusLogger.info( String.format( "Reading sort objects from file: %s", inputFile.getAbsolutePath() ) );

			res.addAll( parseSortObjects( XmlFileReader.readXml( inputFile ) ) );
		}
		catch( Exception e )
		{
			String msg = String.format( "Error while reading sort objects from file: %s", inputFile.getAbsolutePath() );
			logger.error( msg, e );
			throw new RuntimeException( e );
		}

		return res;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Parses input data and produces sortable objects. This method operates of off the XPath queries set for the
	 * data retrieval for this reader.
	 */
	private Collection<SortObject> parseSortObjects( final Document document )
	{
		Collection<SortObject> res	= new ArrayList<SortObject>();
		XPath xPath					= XPathFactory.newInstance().newXPath();

		try
		{
			// read language codes from content
			String langCodeL1	= languageCodeL1XPath != null? xPath.evaluate( languageCodeL1XPath, document ).trim().toUpperCase(): null;
			String langCodeL2	= languageCodeL2XPath != null? xPath.evaluate( languageCodeL2XPath, document ).trim().toUpperCase(): null;
			String name = nameXPath != null ? xPath.evaluate(nameXPath, document).trim():"";

			// get and process collection of content objects
			NodeList objectNodes = (NodeList)xPath.evaluate( dataObjectCollectionXPath, document, XPathConstants.NODESET );
			for( int i = 0; i < objectNodes.getLength(); i++ )
			{
                final Node node = objectNodes.item( i );

                final ExtractionStrategy strategy;
				if ( applyInternalRules )
				{
					strategy = new InternalExtractionStrategy(name, xPath, node, sideTwoPartOfSpeechXPath, inputFile);
				}
                else if ( applyMarsocRules )
                {
                    strategy = new MarsocExtractionStrategy( theStatusLogger, inputFile );
                }
                else
                {
                    strategy = new StandardExtractionStrategy( inputFile );
                }

				SortObject obj = new SortObject();
				obj.setLanguageCodeL1( langCodeL1 );
				obj.setLanguageCodeL2( langCodeL2 );


                if ( null != nameXPath )
                {
                    obj.setReferencedBy( strategy.determineUnitNumber() );
                    obj.setPartOfSpeech( strategy.determinePartOfSpeech() );

                    if  ( null != sideOneSoundUrlXPath )
                    {
                        final String sideOneSoundURL = queryForSoundUrl( xPath, node, sideOneSoundUrlXPath );
                        obj.setSideOneSoundFile( strategy.buildSoundPath( sideOneSoundURL ) );
                    }

                    if ( null != sideTwoSoundUrlXPath )
                    {
                        final String sideTwoSoundURL = queryForSoundUrl( xPath, node, sideTwoSoundUrlXPath );
                        obj.setSideTwoSoundFile( strategy.buildSoundPath( sideTwoSoundURL ) );
                    }
                }

                // add sort fields content for L1
				for( String sortXpath : sortFieldsL1XPaths )
				{
					final String dirty = xPath.evaluate( sortXpath, node ).trim();
                    final String clean = stripXhtml( dirty );
					obj.getSortFieldsL1().add( clean );
				}

				// add sort fields content for L2
				for( String sortXpath : sortFieldsL2XPaths )
				{
					final String dirty = xPath.evaluate( sortXpath, node ).trim();
                    final String clean = stripXhtml( dirty );
					obj.getSortFieldsL2().add( clean );
				}

                if ( null != nameXPath )
                {
                    final String rawB4xName = xPath.evaluate( nameXPath, node ).trim();
                    doubleCheckSortFields( obj.getSortFieldsL1(), rawB4xName );
                    doubleCheckSortFields( obj.getSortFieldsL2(), rawB4xName );
                }

				// collect information about fields that needed to be included in the output
				for( String key : fieldsToIncludeXPaths.keySet() )
				{
                    final String dirty = xPath.evaluate( fieldsToIncludeXPaths.get( key ), node ).trim();
                    final String clean = stripXhtml( dirty );
                    obj.getFieldsToIncludeMap().put( key, clean );
				}

				logger.info( String.format( "Processed sort object: %s", obj.toString() ) );

				res.add( obj );
			}
		}
		catch( XPathExpressionException e )
		{
			logger.error( e );
			throw new RuntimeException( e );
		}

		return res;
	}

    private void doubleCheckSortFields( final List<String> fields, final String b4xName )
    {
        for( final String field : fields )
        {
            if ( (null == field) || field.isEmpty() )
            {
                final StringBuilder builder = new StringBuilder( b4xName ).append( " contains a card with an empty phrase!" );
                theStatusLogger.error( builder.toString() );
            }
        }
    }


    /**
     * Strip any XHMTL that might be part of the string.  We expect the XHTML to be wrapped in a CDATA tag.
     * @param dirty string that contains the XHTML to be removed.
     * @return the cleaned string containing only the non-XHTML content.
     */
    private String stripXhtml( final String dirty )
    {
        //from start of line, match the CDATA preamble
        final String OPENING_CDATA_TAG = "\\A<!\\[CDATA\\[";
        //from the end of the line, match the CDATA closing characters
        final String CLOSING_CDATA_TAG = "\\]\\]>\\Z";
        // <(?:[^>"']|"[^"]*"|'[^']*')*> - matches everything between < and >, skipping over > in attribute values
        /*
          <(?:[^>"']|"[^"]*"|'[^']*')*>

          Match the character < literally <
          Match the regular expression below (?:[^>"']|"[^"]*"|'[^']*')*
             Between zero and unlimited times, as many times as possible, giving back as needed (greedy) *
             Match either the regular expression below (attempting the next alternative only if this one fails) [^>"']
                Match a single character NOT present in the list >"' [^>"']
             Or match regular expression number 2 below (attempting the next alternative only if this one fails) "[^"]*"
                Match the character " literally "
                Match any character that is NOT a " [^"]*
                   Between zero and unlimited times, as many times as possible, giving back as needed (greedy) *
                Match the character " literally "
             Or match regular expression number 3 below (the entire group fails if this one fails to match) '[^']*'
                Match the character ' literally '
                Match any character that is NOT a ' [^']*
                   Between zero and unlimited times, as many times as possible, giving back as needed (greedy) *
                Match the character ' literally '
          Match the character > literally >
        */

        final String BETWEEN_BRACKETS = "<(?:[^>\"']|\"[^\"]*\"|'[^']*')*>";
        // match any characters between & and ;
        final String  HTML_ENTITY_NAMES = "\\x26\\w*[\\x26\\x3B]";
        return dirty.replaceAll( OPENING_CDATA_TAG, "" ).replaceAll( CLOSING_CDATA_TAG, "" ).replaceAll( BETWEEN_BRACKETS, "" ).replaceAll( HTML_ENTITY_NAMES, "" );
    }

    private String queryForSoundUrl( final XPath xPath, final Node node, final String xPathExpression ) throws XPathExpressionException
    {
        String soundURL = xPath.evaluate( xPathExpression, node ).trim();
        if ( logger.isDebugEnabled() )
        {
            logger.debug( "soundURL = " + soundURL );
        }
        return soundURL;
    }

    //------------------------------------------------------------------------------------------------------------------
	private File inputFile = null;

	private String languageCodeL1XPath			= null;
	private String languageCodeL2XPath			= null;
	private String dataObjectCollectionXPath	= null;
    private String sideOneSoundUrlXPath = null;
    private String sideTwoSoundUrlXPath = null;
    private String nameXPath = null;
	private String sideTwoPartOfSpeechXPath = null;

	private List<String> sortFieldsL1XPaths				= new ArrayList<String>();
	private List<String> sortFieldsL2XPaths				= new ArrayList<String>();
	private Map<String, String> fieldsToIncludeXPaths	= new HashMap<String, String>();

	private CollatorDataHelper collatorHelper = null;

	private Logger logger = Logger.getLogger( this.getClass() );

    /**
     * Message logger to use for "normal" operating messages.
     */
    private final Logger theStatusLogger = Logger.getLogger( "normal.messages" );

    /**
     * Logger to user for developer messages.
     */
    private final Logger theLogger = Logger.getLogger( GlossaryInputDataReader.class );

    /**
     * If set to true, we need to use the special MARSOC processing logic.
     */
    private boolean applyMarsocRules = false;
	//------------------------------------------------------------------------------------------------------------------

    public void setApplyMarsocRules( final boolean state )
    {
        applyMarsocRules = state;
    }

	private boolean applyInternalRules = false;

	public void setApplyInternalRules(final boolean state) {applyInternalRules = state;}

	private boolean applyInternalListNameMap = false;

	public void setApplyInternalListNameMap(final boolean state) {applyInternalListNameMap = state;}

	private File internalListNameMapFile = null;

	public void setInternalListNameMapFile(final File file) {internalListNameMapFile = file;}
}
