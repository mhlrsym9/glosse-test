/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.processor;

import com.transparent.glossary.io.GlossaryInputDataReader;
import com.transparent.glossary.model.SortObject;

import java.io.FileFilter;
import java.util.*;

/**
 * This class is a value holder for several data elements common to the processing of the content input data.
 *
 * User: plitvak
 * Date: Aug 3, 2009
 * Time: 10:23:13 AM
 */
public class GlossaryProcessorEntry implements ISortObjectsFilter
{
	//===================================== property assessors ==========================================================
	public Map<FileFilter, GlossaryInputDataReader> getInputReadersMap() {
		return inputReaderMap;
	}

	/**
	 * Sets the map of the input content readers keyed by the file filter that this reader correspons to.
	 */
	public void setInputReadersMap( final Map<FileFilter, GlossaryInputDataReader> inputReaderMap ) {
		this.inputReaderMap = inputReaderMap;
	}

	public ContentProcessingRule getProcessingRule() {
		return processingRule;
	}

	public void setProcessingRule( final ContentProcessingRule processingRule ) {
		this.processingRule = processingRule;
	}

	public List<ISortObjectsFilter> getSortObjectsFilters() {
		return sortObjectsFilters;
	}

	/**
	 * Sets the collection of post processing sort object filters.
	 * Some of the examples of such filters can be duplicate filter or L1/L2 merger filter.
	 * @see com.transparent.glossary.processor.SortObjectsL1L2DuplicateFilter
	 * @see com.transparent.glossary.processor.SortObjectsL1L2FileMergeFilter
	 */
	public void setSortObjectsFilters( final List<ISortObjectsFilter> sortObjectsFilters ) {
		this.sortObjectsFilters = sortObjectsFilters;
	}
	//===================================== property assessors ==========================================================

	/**
	 * This method executes all postprocessing filters registered with this entry.
	 */
	public void process( final Collection<SortObject> objectsToProcess, final Map<String, Object> parameters )
	{
		for( ISortObjectsFilter sortObjProc : sortObjectsFilters ) {
			sortObjProc.process( objectsToProcess, parameters );
		}
	}

	//------------------------------------------------------------------------------------------------------------------
	private Map<FileFilter, GlossaryInputDataReader> inputReaderMap
														= new HashMap<FileFilter, GlossaryInputDataReader>();
	private List<ISortObjectsFilter> sortObjectsFilters	= new ArrayList<ISortObjectsFilter>();
	private ContentProcessingRule processingRule		= ContentProcessingRule.SINGLE;
	//------------------------------------------------------------------------------------------------------------------
}