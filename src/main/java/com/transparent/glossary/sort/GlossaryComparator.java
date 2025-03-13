/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.sort;

import com.transparent.glossary.model.SortObject;
import org.apache.log4j.Logger;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;

/**
 * This comparator implementation used in the sorting of SortObject instances in the collections.
 * It takes in account multiple L1/L2 sort fields. It would compare only a common number of sort fields.
 * Which means that if two sort objects have different number of sort fields the smaller number is taking as a common
 * denominator.
 * It is assummed that the first field of each sort field collection of each SortObject is a key sort field.
 *
 * User: plitvak
 * Date: Aug 4, 2009
 * Time: 5:15:04 PM
 */
public class GlossaryComparator implements Comparator<SortObject>
{
	//===================================== property assessors ==========================================================
	public CollatorDataHelper getCollatorHelper() {
		return collatorHelper;
	}

	public void setCollatorHelper( final CollatorDataHelper collatorHelper ) {
		this.collatorHelper = collatorHelper;
	}

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection( final SortDirection sortDirection ) {
		this.sortDirection = sortDirection;
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType( final SortType sortType ) {
		this.sortType = sortType;
	}
	//===================================== property assessors ==========================================================

	/**
	 * Compares two SortObject instances.
	 * Uses appropriate collator for comparison.
	 * The assumption is made that both sort objects have the same L1/L2 language codes.
	 * The null objects cannot be compared.
	 */
	@Override
	public int compare( final SortObject o1, final SortObject o2 )
	{
		assert o1 != null && o2 != null;
//		// it is assumed that all sort objects in the collection have the same L1 and L2 languages
//		assert o1.getLanguageCodeL1().equals( o2.getLanguageCodeL1() ): o1.getLanguageCodeL1()+" : "+o2.getLanguageCodeL1();
//		assert o1.getLanguageCodeL2().equals( o2.getLanguageCodeL2() ): o1.getLanguageCodeL2()+" : "+o2.getLanguageCodeL2();
		if( !o1.getLanguageCodeL1().equals( o2.getLanguageCodeL1() ) ) {
			logger.info( String.format( "WARNING: Expected to compare languages with equal language L1 codes but found %s and %s",
										 o1.getLanguageCodeL1(), o2.getLanguageCodeL1() ) );
		}
		if( !o1.getLanguageCodeL2().equals( o2.getLanguageCodeL2() ) ) {
			logger.info( String.format( "WARNING: Expected to compare languages with equal language L2 codes but found %s and %s",
										 o1.getLanguageCodeL2(), o2.getLanguageCodeL2() ) );
		}

		int res						= 1;
		int sortDir					= (sortDirection == SortDirection.ASC? 1: -1);
		String languageCode			= sortType == SortType.L1? o1.getLanguageCodeL1(): o1.getLanguageCodeL2();
		CollatorData collatorData	= collatorHelper.getCollatorData().get( languageCode );

		if( collatorData == null ) {
			throw new RuntimeException( "No collator data found for language: "+languageCode );
		}

		List<String> sortFieldsForObj1 = sortType == SortType.L1? o1.getSortFieldsL1(): o1.getSortFieldsL2();
		List<String> sortFieldsForObj2 = sortType == SortType.L1? o2.getSortFieldsL1(): o2.getSortFieldsL2();

		// TODO: research if this is acceptable, compares only common (index-wise) fields
		int size			= Math.min( sortFieldsForObj1.size(), sortFieldsForObj2.size() );
		Collator collator	= collatorData.getCollator( false );
		for( int i = 0; i < size; i++ )
		{
			if( (res = collator.compare( sortFieldsForObj1.get(i), sortFieldsForObj2.get(i) )) != 0 ) {
				break;
			}
		}

		return sortDir * res;
	}

	//------------------------------------------------------------------------------------------------------------------
	private CollatorDataHelper collatorHelper	= null;
	private SortDirection sortDirection			= SortDirection.ASC;
	private SortType sortType					= SortType.L1;

	final private Logger logger = Logger.getLogger( this.getClass() );
	//------------------------------------------------------------------------------------------------------------------
}