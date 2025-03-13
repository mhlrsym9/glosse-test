/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.processor;

import com.transparent.glossary.model.SortObject;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This implementation of the ISortObjectsFilter interface filters merges sort objects based on the L1/L2.
 * It assumes that collection contains an equal number of sort object with just L1 and just L2 information specified.
 * This may occure while reader reads a *TT.xml file that has only one language information in it.
 * It is also expected that every sort object instance will have an information about the file it was created from.

 * User: plitvak
 * Date: Aug 3, 2009
 * Time: 2:47:51 PM
 */
public class SortObjectsL1L2FileMergeFilter implements ISortObjectsFilter
{
	//===================================== property assessors ==========================================================
	public String getFilePatternL1() {
		return filePatternL1;
	}

	/**
	 * Sets the reg exp pattern for L1 file name used to create some of the sort objects to be merged.
	 * For example: .*_known_tt\.xml
	 */
	public void setFilePatternL1( final String filePatternL1 ) {
		this.filePatternL1 = filePatternL1;
	}

	public String getFilePatternL2() {
		return filePatternL2;
	}

	/**
	 * Sets the reg exp pattern for L2 file name used to create some of the sort objects to be merged.
	 * For example: .*_learning_tt\.xml
	 */
	public void setFilePatternL2( final String filePatternL2 ) {
		this.filePatternL2 = filePatternL2;
	}

	public String getCommonPartPattern() {
		return commonPartPattern;
	}

	/**
	 * Sets the reg exp pattern for the common part of L1/L2 file name used to create objects to be merged.
	 * The common part needs to be captured by the reg exp group.
	 * For example: .*\\(.*)?_.*_tt\.xml
	 */
	public void setCommonPartPattern( final String commonPartPattern ) {
		// Running on Windows? Leave string as is...
		if (File.separator.equals("\\"))
			this.commonPartPattern = commonPartPattern;
		// Running on Linux/Mac? Translate backslashes into one forward slash...
		else
			this.commonPartPattern = commonPartPattern.replaceAll("\\\\\\\\", File.separator);
	}
	//===================================== property assessors ==========================================================

	/**
	 * Merges sort objects to create ones that have complete L1/L2 information from the collection that has half sort
	 * object with ust L1 and half with just L2 information available.
	 */
	public void process( final Collection<SortObject> objectsToProcess, final Map<String, Object> parameters )
	{
		logger.info( String.format( "Merging objects with just one language for original sort object collection of %d elements",
									objectsToProcess.size() ) );

		Collection<SortObject> result			= new ArrayList<SortObject>( objectsToProcess.size() / 2 );
		Map<String, List<SortObject>> dataL1	= new HashMap<String, List<SortObject>>();
		Map<String, List<SortObject>> dataL2	= new HashMap<String, List<SortObject>>();

		// split sort object in to two maps, one just those that have L1 information and
		// another that has only L2. These maps are keyed by the common part of the correspondent
		// file names that the sort objects were created from.
		splitSortData( objectsToProcess, dataL1, dataL2 );

		// merge the sort objects
		for( String key : dataL1.keySet() )
		{
			List<SortObject> listOfL1 = dataL1.get( key );
			List<SortObject> listOfL2 = dataL2.get( key );

			assert listOfL1 != null && listOfL2 != null && listOfL1.size() == listOfL2.size();

			// the assumption is made that there is always the same number of search objects with non empty L1 and L2
			// and they are different objects as well.
			for( int i = 0; i < listOfL1.size(); i++ )
			{
				SortObject distSortObj		= listOfL1.get(i);
				SortObject sourceSortObj	= listOfL2.get(i);

				assert distSortObj != sourceSortObj;

				// fill in missing data in destination object
				distSortObj.setLanguageCodeL2( sourceSortObj.getLanguageCodeL2() );
				distSortObj.getSortFieldsL2().clear();
				distSortObj.getSortFieldsL2().addAll( sourceSortObj.getSortFieldsL2() );
				for( String name : sourceSortObj.getFieldsToIncludeMap().keySet() ) {
					distSortObj.getFieldsToIncludeMap().put( name, sourceSortObj.getFieldsToIncludeMap().get( name ) );
				}
				//---------------------------------------

				result.add( distSortObj );
			}
		}

		objectsToProcess.clear();
		objectsToProcess.addAll( result );

		logger.info( String.format( "Merged objects with just one language, new sort object collection size is %d elements", 
									objectsToProcess.size() ) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * This methid splits the collection of sort objects in two maps. One that has only objects that contain L1
	 * information and another that contain only L2. Both maps are keyd with the same set of keys which is build upon
	 * a common file name part for files that correspondent sort object were created from.
	 */
	private void splitSortData( final Collection<SortObject> objectsToProcess,
								final Map<String, List<SortObject>> dataL1,
								final Map<String, List<SortObject>> dataL2 )
	{
		Pattern regExpL1		= Pattern.compile( filePatternL1 );
		Pattern regExpL2		= Pattern.compile( filePatternL2 );
		Pattern regExpCommon	= Pattern.compile( commonPartPattern );

		for( SortObject sortObject : objectsToProcess )
		{
			// get the file name that this sort object was created from.
			String fileName	= (String)sortObject.getUserData().get( "" );
			assert fileName != null;

			Matcher matcherL1	= regExpL1.matcher( fileName );
			Matcher matcherL2	= regExpL2.matcher( fileName );
			Matcher commonPart	= regExpCommon.matcher( fileName );

			// there has to be a common part for the file name
			if( !commonPart.matches() || commonPart.groupCount() != 1 )
			{
				String msg = String.format( "File name [%s] doesn't match to regexp [%s]", fileName, commonPartPattern );
				logger.error( msg );
				throw new RuntimeException( msg );
			}

			//
			String fileNameCommonPart = commonPart.group( 1 );

			// if the file name matches L1 file name add to L1 map
			if( matcherL1.matches() )
			{
				List<SortObject> data = dataL1.get( fileNameCommonPart );
				if( data == null )
				{
					data = new ArrayList<SortObject>();
					dataL1.put( fileNameCommonPart, data );
				}
				data.add( sortObject );
			}
			// if the file name matches L2 file name add to L2 map
			else if( matcherL2.matches() )
			{
				List<SortObject> data = dataL2.get( fileNameCommonPart );
				if( data == null )
				{
					data = new ArrayList<SortObject>();
					dataL2.put( fileNameCommonPart, data );
				}
				data.add( sortObject );
			}
			// file name must match either L1 or L2 file name
			else
			{
				String msg = String.format( "File name [%s] doesn't match to any of the regexps [%s] or [%s]",
											fileName, filePatternL1, filePatternL2 );
				logger.error( msg );
				throw new RuntimeException( msg );
			}
		}
	}

	//------------------------------------------------------------------------------------------------------------------
	private String filePatternL1		= null;
	private String filePatternL2		= null;
	private String commonPartPattern    = null;

	private Logger logger = Logger.getLogger( this.getClass() );
	//------------------------------------------------------------------------------------------------------------------
}