/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.sort;

import java.util.Map;

/**
 * Simple wrapper class to be used with spring.
 * This class actually represents a map of collator data objects keyed by their langages.
 *
 * User: plitvak
 * Date: Jul 31, 2009
 * Time: 3:29:37 PM
 */
public class CollatorDataHelper
{
	//===================================== property assessors ==========================================================
	public Map<String, CollatorData> getCollatorData() {
		return collatorData;
	}

	public void setCollatorData( final Map<String, CollatorData> collatorData ) {
		this.collatorData = collatorData;
	}
	//===================================== property assessors ==========================================================

	//------------------------------------------------------------------------------------------------------------------
	private Map<String,CollatorData> collatorData = null;
	//------------------------------------------------------------------------------------------------------------------
}