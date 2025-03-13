/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.processor;

import com.transparent.glossary.model.SortObject;

import java.util.Collection;
import java.util.Map;

/**
 * This interface should be implemented by any class that needs to perform some filtering of the sort objects.
 *
 * User: plitvak
 * Date: Aug 3, 2009
 * Time: 2:39:57 PM
 */
public interface ISortObjectsFilter
{
	/**
	 * Filters sort objects. This method will modify the content of a given collection of the sort objects.
	 * A concrete implementation can use optional parameters map to get implementation specific parameters.
	 */
	void process( Collection<SortObject> objectsToProcess, Map<String, Object> parameters );
}
