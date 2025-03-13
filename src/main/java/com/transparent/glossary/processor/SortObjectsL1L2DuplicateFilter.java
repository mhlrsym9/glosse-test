/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.processor;

import com.transparent.glossary.model.SortObject;
import org.apache.log4j.Logger;

import java.text.Normalizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This implementation of the ISortObjectsFilter interface filters sort object duplicates. It will remove one of the
 * duplicates from the collection. There is no guarantee which duplicate will be removed.
 * The duplicates are defined as two sort objects that have the same primary L1+L2 sort fields combinations.
 * It is assumed that fields-to-include collection of a given object will always have primary L1 and L2 sort fields.
 *
 * User: plitvak
 * Date: Aug 3, 2009
 * Time: 2:47:51 PM
 */
public class SortObjectsL1L2DuplicateFilter implements ISortObjectsFilter
{
	/**
	 */
	public void process( final Collection<SortObject> objectsToProcess, final Map<String, Object> parameters )
	{
		logger.info( String.format( "Filtering duplicates for original sort object collection of %d elements", objectsToProcess.size() ) );

		Map<String,SortObject> filterMap = new HashMap<String,SortObject>();
		// the assumption here is that there will always be the fields with names L1 and L2 and they always will be the
		// primary sort fields.
		// The map will naturally keep only one unique combination of L1+L2
		for( SortObject sortObject : objectsToProcess )
		{
			String key = Normalizer.normalize( sortObject.getFieldsToIncludeMap().get( SortObject.L1_FIELD_NAME ), Normalizer.Form.NFD )
						 +
						 Normalizer.normalize( sortObject.getFieldsToIncludeMap().get( SortObject.L2_FIELD_NAME ), Normalizer.Form.NFD );
			logger.info( "Key: "+key );
			filterMap.put( key, sortObject );
		}

		objectsToProcess.clear();
		objectsToProcess.addAll( filterMap.values() );

		logger.info( String.format( "Duplicates filterd (if found) new collection size is %d elements", objectsToProcess.size() ) );
	}

	//------------------------------------------------------------------------------------------------------------------
	private Logger logger = Logger.getLogger( this.getClass() );
	//------------------------------------------------------------------------------------------------------------------
}