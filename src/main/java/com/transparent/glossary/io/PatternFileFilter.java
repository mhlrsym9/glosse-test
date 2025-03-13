/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class represents file filter that filters files based on a specific set of file name patterns.
 * The patterns are set in a form of the regular expressions.
 * It is an OR based filter, meaning that if just one pattern from the set matches the file name this file is considered
 * to be acceptable by this filter.
 *
 * User: plitvak
 * Date: Aug 3, 2009
 * Time: 11:45:37 AM
 */
public class PatternFileFilter implements FileFilter
{
	//================================== property accessors ============================================================
	/**
	 */
	public List<String> getPatterns() {
		return patterns;
	}

	/**
	 */
	public void setPatterns( final List<String> patterns ) {
		this.patterns = patterns;
	}
	//================================== property accessors ============================================================

	/**
	 */
	public boolean accept( final File pathname )
	{
		boolean res = false;

		String fileName = pathname.getAbsolutePath();
		for( String pattern : patterns )
		{
			if( (res = Pattern.matches( pattern, fileName )) ) {
				break;
			}
		}

		return res;
	}

	//------------------------------------------------------------------------------------------------------------------
	private List<String> patterns = new ArrayList<String>();
	//------------------------------------------------------------------------------------------------------------------
}