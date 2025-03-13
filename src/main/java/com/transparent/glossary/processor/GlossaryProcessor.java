/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.processor;

import com.transparent.glossary.io.GlossaryInputDataReader;
import com.transparent.glossary.io.GlossaryOutputWriter;
import com.transparent.glossary.model.SortObject;
import com.transparent.glossary.sort.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * Main glossary processor class.
 * It holds data and methods to read the content data and produce a glossary output.
 *
 * User: plitvak
 * Date: Jul 31, 2009
 * Time: 4:00:28 PM
 */
public class GlossaryProcessor
{
	//===================================== property assessors ==========================================================
	public File getInputRootFolder() {
		return inputRootFolder;
	}

	public void setInputRootFolder( final File inputRootFolder ) {
		this.inputRootFolder = inputRootFolder;
	}

	public List<GlossaryProcessorEntry> getProcessorEntries() {
		return entries;
	}

	public void setProcessorEntries( final List<GlossaryProcessorEntry> entries ) {
		this.entries = entries;
	}

	public List<ISortObjectsFilter> getInputDataFinalFilters() {
		return inputDataFinalFilters;
	}

	public void setInputDataFinalFilters( final List<ISortObjectsFilter> inputDataFinalFilters ) {
		this.inputDataFinalFilters = inputDataFinalFilters;
	}

	public List<String> getPhraseMarkers() {
		return phraseMarkers;
	}

	public void setPhraseMarkers( final List<String> phraseMarkers ) {
		this.phraseMarkers = phraseMarkers;
	}

	public CollatorDataHelper getCollatorHelper() {
		return collatorHelper;
	}

	public void setCollatorHelper( final CollatorDataHelper collatorHelper ) {
		this.collatorHelper = collatorHelper;
	}

	public Map<SortType, GlossaryOutputWriter> getGlossaryOutputWriters() {
		return glossaryOutputWriters;
	}

	public void setGlossaryOutputWriters( final Map<SortType, GlossaryOutputWriter> glossaryOutputWriters ) {
		this.glossaryOutputWriters = glossaryOutputWriters;
	}

	public StopWordProcessingType getStopWordProcessingType() {
		return stopWordProcessingType;
	}

	public void setStopWordProcessingType( final StopWordProcessingType stopWordProcessingType ) {
		this.stopWordProcessingType = stopWordProcessingType;
	}
	//===================================== property assessors ==========================================================

	/**
	 * This method uses registered input readers to collect the sort object data from the various sources.
	 */
	public List<SortObject> collectUnsortedGlossaryData()
	{
		assert inputRootFolder != null;

		List<SortObject> res	= new ArrayList<SortObject>();
		File[] files			= inputRootFolder.listFiles();

		processInput( files, res );

		for( ISortObjectsFilter filter : inputDataFinalFilters ) {
			filter.process( res, null );
		}

		markPhrases( res );

		if( stopWordProcessingType != StopWordProcessingType.NONE ) {
			removeStopWordsFromSortKeys( res );
		}

		return res;
	}

	/**
	 * Convinience method that seprarated word sort objects from phrase ones.
	 * This method doesn't modify the data collection passed to it.
	 */
	public void splitDataToWordsAndPhrases(final Collection<SortObject> data,
                                           final Collection<SortObject> words,
                                           final Collection<SortObject> phrases)
	{
		assert data != null && words != null && phrases != null;

		for( SortObject sortObject : data )
		{
			if( sortObject.isPhrase() ) {
				phrases.add( sortObject );
			}
			else {
				words.add( sortObject );
			}
		}
	}

	/**
	 * Sorts the collection of the sort objects accordint to the give sort type and sort direction.
	 * This method modifies the collection passed in to it.
	 */
	public void sort( final List<SortObject> unsortedData, SortType sortType, SortDirection sortDirection )
	{
		assert unsortedData != null;

		if( unsortedData.size() > 0 )
		{
			logger.info( String.format( "Sorting collection of %d elements", unsortedData.size() ) );

			GlossaryComparator glossaryComparator = new GlossaryComparator();
			glossaryComparator.setCollatorHelper( collatorHelper );
			glossaryComparator.setSortType( sortType );
			glossaryComparator.setSortDirection( sortDirection );

			Collections.sort( unsortedData, glossaryComparator );

			logger.info( String.format( "Finished sorting collection of %d elements", unsortedData.size() ) );
		}
	}

	/**
	 * Creates a glossary file at a given file location based on the given sort type (used to determine content of the
	 * glossary) and sort objects collection.
	 */
	public void writeGlossary( File glossaryFile, SortType sortType, Collection<SortObject> sortedData )
	{
		GlossaryOutputWriter glossaryWriter = glossaryOutputWriters.get( sortType );
		glossaryWriter.setGlossaryFile( glossaryFile );
		glossaryWriter.writeGlossaryToFile( sortedData );
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Processes the set of input files and reads sort object data from those that are accepted by the registered
	 * input data readers.
	 * This method works recursively going down to all subfolders of the given file set.
	 */
	private void processInput( final File[] files, final Collection<SortObject> result )
	{
		List<File> folders	= new ArrayList<File>();
		Map<GlossaryProcessorEntry, Collection<SortObject>> collectedDataForFolder
							= new HashMap<GlossaryProcessorEntry, Collection<SortObject>>();
		for( File f : files )
		{
			// collect folders for future processing
			if( f.isDirectory() ) {
				folders.add( f );
			}
			else
			{
				// for all processor collect sort data
				for( GlossaryProcessorEntry procEntry : entries )
				{
					Collection<SortObject> collectedSortData = loadSortObjects( procEntry, f );
					// for those processors that collect data for more then one file accumulate that data
					if( procEntry.getProcessingRule() == ContentProcessingRule.ALL )
					{
						Collection<SortObject> sortData = collectedDataForFolder.get( procEntry );
						if( sortData == null )
						{
							sortData = new ArrayList<SortObject>();
							collectedDataForFolder.put( procEntry, sortData );
						}

						// record the file for which sort objects were collected we will need it later in processing
						// of the merge if it occurs
						addFileInfoToSortObjects( collectedSortData, f );

						sortData.addAll( collectedSortData );
					}
					// process the data collected by the processors that operate with a single file only
					else
					{
						procEntry.process( collectedSortData, null );
						result.addAll( collectedSortData );
					}
				}
			}
		}

		// process the data collected for multi-file processors
		if( collectedDataForFolder.size() > 0 )
		{
			for( GlossaryProcessorEntry procEntry : collectedDataForFolder.keySet() )
			{
				Collection<SortObject> collectedSortData = collectedDataForFolder.get( procEntry );
				procEntry.process( collectedSortData, null );
				result.addAll( collectedSortData );
			}
		}

		// help the GC, since we use recursion here we need to let GC know it can collect the object
		collectedDataForFolder.clear();
		collectedDataForFolder = null;
		//-------------------------------------------

		// recurse in to the sub-folders
		for( File folder : folders ) {
			processInput( folder.listFiles(), result );
		}
	}

	/**
	 * This method loads the sort object for a given file if accepted by the given processinf entry.
	 */
	private Collection<SortObject> loadSortObjects( final GlossaryProcessorEntry procEntry, final File file )
	{
		Collection<SortObject> res = new ArrayList<SortObject>();

		// get all filters for the processor entry and check the file against all of them
		for( FileFilter filter : procEntry.getInputReadersMap().keySet() )
		{
			if( filter.accept( file ) )
			{
				GlossaryInputDataReader inputReader = procEntry.getInputReadersMap().get( filter );
				inputReader.setInputFile( file );
				res.addAll(  inputReader.readSortObjects() );
			}
		}

		return res;
	}

	/**
	 * This method sets the file as a user data to every sort object of a given collection.
	 */
	private void addFileInfoToSortObjects( final Collection<SortObject> sortObjects, File file )
	{
		for( SortObject so : sortObjects ) {
			so.getUserData().put( "", file.getAbsolutePath() );
		}
	}

	/**
	 * Marks all phrase sort objects as such in a given collection of the sort objects.
	 * The marker of a phrase is the presence of the punctuation signs (phrase markers) anywhere in L1 text.
	 */
	private void markPhrases( Collection<SortObject> sortObjects )
	{
		for( SortObject sortObject : sortObjects )
		{
			final String textL1	= sortObject.getFieldsToIncludeMap().get( SortObject.L1_FIELD_NAME );
            sortObject.setIsPhrase( isPhrase( textL1 ) );
		}
	}

    private boolean isPhrase( final String text )
    {
        boolean isPhrase = false;
        for( String phraseMarker : phraseMarkers )
        {
            isPhrase = text.contains( phraseMarker );
            if( isPhrase )
            {
                break;
            }
        }
        return isPhrase;
    }

    private void removeStopWordsFromSortKeys( Collection<SortObject> sortObjects )
	{
		for( SortObject sortObject : sortObjects )
		{
			//skip entry if necessary
			if( stopWordProcessingType == StopWordProcessingType.WORDS && sortObject.isPhrase()
					||
				stopWordProcessingType == StopWordProcessingType.PHRASES && !sortObject.isPhrase()	) {
				continue;
			}

			for( int i = 0; i < sortObject.getSortFieldsL1().size(); i++ ) {
				sortObject.getSortFieldsL1().set( i, removeStopWords( sortObject.getSortFieldsL1().get( i ), sortObject.getLanguageCodeL1() ) );
			}

			for( int i = 0; i < sortObject.getSortFieldsL2().size(); i++ ) {
				sortObject.getSortFieldsL2().set( i, removeStopWords( sortObject.getSortFieldsL2().get( i ), sortObject.getLanguageCodeL2() ) );
			}
		}
	}

	/**
	 * Removes all stop words from the given string. This method also converts a given string to lowercase according to
	 * the rules of a given language.
	 */
	private String removeStopWords( final String field, final String langCode )
	{
		String res = field.toLowerCase();

		logger.info( String.format( "Removing stop words from: [%s]", res ) );

		CollatorData collatorData = collatorHelper.getCollatorData().get( langCode );
		if( collatorData != null && collatorData.getStopWords( false ).size() > 0 )
		{
			if( !collatorData.getJavaLocaleCode().equals( CollatorData.NO_JAVA_LOCALE ) ) {
				res	= field.toLowerCase( new Locale( collatorData.getJavaLocaleCode() ) );
			}

			for( String word : collatorData.getStopWords( false ) ) {
				res = res.replaceAll( word, "" );
			}
		}

		// apparently the field text was a stop word so we return the field value to keep the standalone word
		if( res.trim().length() == 0 ) {
			res = field;
		}

		logger.info( String.format( "Stop words removed: [%s]", res ) );

		return res.trim();
	}

	//------------------------------------------------------------------------------------------------------------------
	private File inputRootFolder = null;

	private List<String> phraseMarkers						= new ArrayList<String>();
	private List<GlossaryProcessorEntry> entries			= new ArrayList<GlossaryProcessorEntry>();
	private List<ISortObjectsFilter> inputDataFinalFilters	= new ArrayList<ISortObjectsFilter>();
	private Map<SortType,GlossaryOutputWriter> glossaryOutputWriters
															= new HashMap<SortType,GlossaryOutputWriter>();
	private CollatorDataHelper collatorHelper				= null;
	private StopWordProcessingType stopWordProcessingType	= StopWordProcessingType.ALL;

	private Logger logger = Logger.getLogger( this.getClass() );
	//------------------------------------------------------------------------------------------------------------------
}
