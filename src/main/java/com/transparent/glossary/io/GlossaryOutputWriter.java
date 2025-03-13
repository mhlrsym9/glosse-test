/*
 * Copyright (c) 2008-2009 Transparent Language, Inc.  All rights reserved.
 */
package com.transparent.glossary.io;

import com.transparent.glossary.model.*;
import com.transparent.glossary.sort.CollatorData;
import com.transparent.glossary.sort.CollatorDataHelper;
import com.transparent.glossary.sort.SortType;
import java.io.FileWriter;
import java.io.Writer;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * This class used to create a glossary xml output from the collection of previously sorted sort objects.
 * @see com.transparent.glossary.model.SortObject
 * @see com.transparent.glossary.model.Glossary
 *
 * User: plitvak
 * Date: Aug 5, 2009
 * Time: 10:02:54 AM
 */
public class GlossaryOutputWriter
{
	//===================================== property assessors =========================================================
	public String getGlossaryKeyField() {
		return glossaryKeyField;
	}

	/**
	 * Sets the name of the field that this glossary should treat as a key. The glossary builds its content in the sorted
	 * order of the keys.
	 * The fild name must be one of the sort fields in the sort object and also must be present in the sort object's
	 * collection of fields to include in output. This is usually L1 or L2.
	 * @see com.transparent.glossary.model.SortObject
	 */
	public void setGlossaryKeyField( final String glossaryKeyField ) {
		this.glossaryKeyField = glossaryKeyField;
	}

	public String getGlossaryTranslationField() {
		return glossaryTranslationField;
	}

	/**
	 * Sets the name of the field that will hold glossary entry's translation.
	 * The fild name must be present in the sort object's collection of fields to include in output.
	 * @see com.transparent.glossary.model.SortObject
	 */
	public void setGlossaryTranslationField( final String glossaryTranslationField ) {
		this.glossaryTranslationField = glossaryTranslationField;
	}

	public List<String> getAnnotationFields() {
		return annotationFields;
	}

	/**
	 * Sets the names of the fields that will hold glossary entry's annotations.
	 * The filds names must be present in the sort object's collection of fields to include in output.
	 * @see com.transparent.glossary.model.SortObject
	 */
	public void setAnnotationFields( final List<String> annotationFields ) {
		this.annotationFields = annotationFields;
	}

	public JAXBContext getGlossaryJaxbContext() {
		return glossaryJaxbContext;
	}

	public void setGlossaryJaxbContext( final JAXBContext glossaryJaxbContext ) {
		this.glossaryJaxbContext = glossaryJaxbContext;
	}

	public File getGlossaryFile() {
		return glossaryFile;
	}

	public void setGlossaryFile( final File glossaryFile ) {
		this.glossaryFile = glossaryFile;
	}

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType( final SortType sortType ) {
		this.sortType = sortType;
	}

	public CollatorDataHelper getCollatorHelper() {
		return collatorHelper;
	}

	public void setCollatorHelper( final CollatorDataHelper collatorHelper ) {
		this.collatorHelper = collatorHelper;
	}

	public void setVersionProp( final Properties versionProp ) {
		this.versionProp = versionProp;
	}
	//===================================== property assessors =========================================================

	/**
	 * Creates a glossary output as an XML file according to the settings for this writer.
	 */
	public void writeGlossaryToFile( Collection<SortObject> sortedData )
	{
		try
		{
			logger.info( String.format( "Creating glossary file: [%s]", glossaryFile.getAbsolutePath() ) );

			Glossary glossary = new Glossary();
			// The assumption here is that all objects in the sortedData have same L1 and L2 pairs.
			SortObject sortObject = sortedData.iterator().next();
			glossary.setLanguageCode( getSortLanguageCode( sortObject ) );
			glossary.setVersion( versionProp.getProperty( "version" ) );

			// converts sort objects in to the Glossary DOM
			fillInGlossaryWithContent( glossary, sortedData );

			// writes/marshals glossary to the XML file
			writeGlossaryXmlToFile( glossaryFile, glossary );

            //TODO: not sure if this is going to be a permanent feature or not.
            dumpGlossaryToCsv( glossary );
		}
		catch( Exception e )
		{
			logger.error( e );
			throw new RuntimeException( e );
		}
	}

    private void dumpGlossaryToCsv( final Glossary glossary )
    {
        if ( "ENGLISH".equalsIgnoreCase( glossary.getLanguageCode() ) )
        {
            final List<SortingData> dataSet = createSortedDataSet( glossary );
            writeToCsvFile( dataSet );
        }
    }

    private void writeToCsvFile( final List<SortingData> sortedData )
    {
        try
        {
            final File csv = new File( glossaryFile.getAbsolutePath().replace( ".xml", ".csv" ) );
            final FileOutputStream fos = new FileOutputStream( csv );
            final OutputStreamWriter osw = new OutputStreamWriter( fos, "UTF-8" );
            for( final SortingData data : sortedData )
            {
                osw.write( data.theEnglish, 0, data.theEnglish.length() );
                osw.write( ';' );
                osw.write( data.theTranslation, 0, data.theTranslation.length() );
                osw.write( System.getProperty( "line.separator" ), 0, System.getProperty( "line.separator" ).length() );
            }
            osw.flush();
            fos.close();
            osw.close();
        }
        catch( IOException e )
        {
            logger.warn( "Unable to write CSV file.", e );
        }
    }

    private List<SortingData> createSortedDataSet( final Glossary glossary )
    {
        final List<SortingData> sorted = new ArrayList<SortingData>( 1024 );
        final List<GlossaryLetter> letters = glossary.getLetters();
        for( final GlossaryLetter letter : letters )
        {
            final List<GlossaryWordEntry> words = letter.getWords();
            for( final GlossaryWordEntry word : words )
            {
                final GlossaryKey key = word.getKey();
                final GlossaryTranslation translation = word.getTranslation();
                final SortingData data = new SortingData();
                data.theEnglish = key.getKey();
                data.theTranslation = translation.getTranslation();
                sorted.add( data );
            }

            final List<GlossaryPhraseEntry> phrases = letter.getPhrases();
            for( final GlossaryPhraseEntry phrase : phrases )
            {
                final GlossaryKey key = phrase.getKey();
                final GlossaryTranslation translation = phrase.getTranslation();
                final SortingData data = new SortingData();
                data.theEnglish = key.getKey();
                data.theTranslation = translation.getTranslation();
                sorted.add( data );
            }
        }
        Collections.sort( sorted );
        return sorted;
    }

    private static final class SortingData implements Comparable<SortingData>
    {
        String theEnglish;
        String theTranslation;

        @Override
        public int compareTo( final SortingData o )
        {
            return theEnglish.compareTo( o.theEnglish );
        }

        @Override
        public String toString()
        {
            return theEnglish + " = " + theTranslation;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Get the language code this glossary output is sorted on.
	 */
	private String getSortLanguageCode( final SortObject sortObject ) {
		return sortType == SortType.L1? sortObject.getLanguageCodeL1(): sortObject.getLanguageCodeL2();
	}

	/**
	 * Get the language code for this glossary's translations.
	 */
	private String getTranslationLanguageCode( final SortObject sortObject ) {
		return sortType == SortType.L1? sortObject.getLanguageCodeL2(): sortObject.getLanguageCodeL1();
	}

	/**
	 * Converts a collection of sorted sort objects in to the glossary DOM.
	 */
	private void fillInGlossaryWithContent( final Glossary glossary, final Collection<SortObject> sortedData )
	{
		Map<String, Collection<SortObject>> glossaryDataMap		= new HashMap<String, Collection<SortObject>>();
		List<IntermediateLetterData> glossaryLetterEntries      = new ArrayList<IntermediateLetterData>();

		// split all sort objects in to the sub collections mapped by first letter of each primary sort term.
		buildGlossaryMap( sortedData, glossaryDataMap, glossaryLetterEntries );

		// build glossary DOM
		glossary.setLetters( new ArrayList<GlossaryLetter>() );
		for( IntermediateLetterData letterData : glossaryLetterEntries )
		{
			// create glossary letter entry
			GlossaryLetter letter = new GlossaryLetter();
			letter.setLetterName( letterData.getLetter() );
            letter.setIsMiscBucket( letterData.getIsMiscBucket() );
			glossary.getLetters().add( letter );

			// build words and phrases for a letter entry
			letter.setWords( new ArrayList<GlossaryWordEntry>() );
			letter.setPhrases( new ArrayList<GlossaryPhraseEntry>() );
			for( SortObject sortObject : glossaryDataMap.get( letterData.letter ) )
			{
				logger.info( String.format( "Creating glossary entry for: [%s]", sortObject.toString() ) );

				if( sortObject.isPhrase() )
				{
					GlossaryPhraseEntry phrase = new GlossaryPhraseEntry();
					fillInGlossaryEntry( sortObject, phrase );
					letter.getPhrases().add( phrase );
				}
				else
				{
					GlossaryWordEntry word = new GlossaryWordEntry();
					fillInGlossaryEntry( sortObject, word );
					letter.getWords().add( word );
				}
			}
		}
	}

	/**
	 * This method fills in a glossary entry (either a word or a phrase) based on the give search object.
	 */
	private void fillInGlossaryEntry( final SortObject sortObject, final GlossaryEntryBase entry )
	{
		entry.setLanguageCodeL1( sortObject.getLanguageCodeL1() );
		entry.setLanguageCodeL2( sortObject.getLanguageCodeL2() );
        entry.setSideOneSoundFile( sortObject.getSideOneSoundFile() );
        entry.setSideTwoSoundFile( sortObject.getSideTwoSoundFile() );
        entry.setReferencedBy( sortObject.getReferencedBy() );
        entry.setPartOfSpeech( sortObject.getPartOfSpeech() );

		// create glossary key
		GlossaryKey key = new GlossaryKey();
		key.setKey( sortObject.getFieldsToIncludeMap().get( glossaryKeyField ) );
		key.setLanguageCode( getSortLanguageCode( sortObject ) );
		entry.setKey( key );

		// create glossary translation
		GlossaryTranslation translation = new GlossaryTranslation();
		translation.setTranslation( sortObject.getFieldsToIncludeMap().get( glossaryTranslationField ) );
		translation.setLanguageCode( getTranslationLanguageCode( sortObject) );
		entry.setTranslation( translation );

		// build annotations collection
		entry.setAnnotations( new ArrayList<GlossaryAnnotation>() );
		for( String field : sortObject.getFieldsToIncludeMap().keySet() )
		{
			// the field is considered to be the annotation if it is not a key or a translation
			if( !field.equals( glossaryKeyField ) && !field.equals(glossaryTranslationField) )
			{
				// filter out empty annotations
				String annotationText = sortObject.getFieldsToIncludeMap().get( field );
				if( annotationText != null && annotationText.trim().length() > 0  )
				{
					GlossaryAnnotation annotation = new GlossaryAnnotation();
					annotation.setAnnotation( annotationText );
					annotation.setName( field );
					entry.getAnnotations().add( annotation );
				}
			}
		}
	}

	private String alignWithLetterGroups(final String tmpKey, final CollatorData collatorData)
	{
		String finalKey = tmpKey;

		final List<String> letterGroups = collatorData.getLetterGroups(false);
		if (!letterGroups.isEmpty())
		{
			for (String letterGroup : letterGroups)
			{
				if (letterGroup.contains(tmpKey))
				{
					finalKey = letterGroup.substring(0, 1);
					break;
				}
			}
		}

		return finalKey;
	}

    private static final class IntermediateLetterData
    {
        public IntermediateLetterData(String letter, Boolean isMiscBucket) {
            this.letter = letter;
            this.isMiscBucket = isMiscBucket;
        }

        public String getLetter() {
            return letter;
        }

        public void setLetter(String letter) {
            this.letter = letter;
        }

        public Boolean getIsMiscBucket() {
            return isMiscBucket;
        }

        public void setIsMiscBucket(Boolean isMiscBucket) {
            this.isMiscBucket = isMiscBucket;
        }

        private String letter;
        private Boolean isMiscBucket;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IntermediateLetterData that = (IntermediateLetterData) o;

            if (!letter.equals(that.letter)) return false;
            return isMiscBucket.equals(that.isMiscBucket);

        }

        @Override
        public int hashCode() {
            int result = letter.hashCode();
            result = 31 * result + isMiscBucket.hashCode();
            return result;
        }

        public static Comparator<IntermediateLetterData> IntermediateLetterDataComparator
                = new Comparator<IntermediateLetterData>() {
            public int compare(IntermediateLetterData l, IntermediateLetterData r) {
                if (!l.getIsMiscBucket() && r.getIsMiscBucket())
                    return -1;
				else if (l.getIsMiscBucket() && !r.getIsMiscBucket())
					return 1;
                else
                    return l.getLetter().compareTo(r.getLetter());
            }
        };
    }

	private IntermediateLetterData alignWithAlphabet(final String tmpKey, final CollatorData collatorData)
	{
		IntermediateLetterData retVal = new IntermediateLetterData(tmpKey, false);
		final List<String> alphabet = collatorData.getAlphabet(false);
		Boolean match = false;
		if (!alphabet.isEmpty())
		{
			for (String letter : alphabet)
			{
				if (letter.contains(tmpKey)) {
					match = true;
					break;
				}
			}
		}
		else
			match = true;

		if (!match) {
            retVal.setLetter(collatorData.getMiscBucketLabel());
            retVal.setIsMiscBucket(true);
        }
		return retVal;
	}

	/**
	 * This method splits sorted sort objects in to the map of sub collections of sorted sort objects keyed by the first
	 * letter of a primary search term (usually L1 or L2) in a given sort object. Also to preserve the sorted nature of
	 * the entries the auxiliary list is used to hold the map keys in the original sort order.
	 */
	private void buildGlossaryMap(final Collection<SortObject> sortedData,
								  final Map<String, Collection<SortObject>> glossaryMap,
								  final List<IntermediateLetterData> glossaryLetterEntries) {
		//Extremely bad hotfix to remove Mandarin alphabet for JIRA entry: CAD-731
		Collection<SortObject> MandarinSortObj = new ArrayList<SortObject>();
		for (SortObject sortObject : sortedData) {
			// convert sort key to upper case using appropriate locale if possible
			CollatorData collatorData
					= collatorHelper.getCollatorData().get(getSortLanguageCode(sortObject));
			//Add all mandarin sort objects into one collection
			if ("MANDARIN".equals(collatorData.getTplLanguageCode())) {
				MandarinSortObj.add(sortObject);
			} else {

				// glossary will be keyed by the first letter of the first sort field since this is how entries
				// are sorted anyways
				assert sortObject.getSortFieldsL1().size() > 0 && sortObject.getSortFieldsL2().size() > 0;
				int keyCodePoint = sortType == SortType.L1 ?
						sortObject.getSortFieldsL1().get(0).codePointAt(0) :
						sortObject.getSortFieldsL2().get(0).codePointAt(0);
				String tmpKey = new String(new int[]{keyCodePoint}, 0, 1);
				if (!collatorData.getJavaLocaleCode().equals(CollatorData.NO_JAVA_LOCALE)) {
					tmpKey = tmpKey.toUpperCase(new Locale(collatorData.getJavaLocaleCode()));
				} else {
					tmpKey = tmpKey.toUpperCase();
				}

				tmpKey = alignWithLetterGroups(tmpKey, collatorData);
                IntermediateLetterData letterData = alignWithAlphabet(tmpKey, collatorData);
				// TODO: Add check for letter to Collation Rules. Find which Position the character is in collation rules, then map that to the equivalent Alphabet "Display" character.
				//-----------------------------------------------------------------

                if (glossaryLetterEntries.contains(letterData)) {
                    Collection<SortObject> mappedObjects = glossaryMap.get(letterData.getLetter());
                    mappedObjects.add(sortObject);
                } else {
                    Collection<SortObject> mappedObjects = new ArrayList<SortObject>();
                    glossaryMap.put(letterData.getLetter(), mappedObjects);
                    mappedObjects.add(sortObject);
                    glossaryLetterEntries.add(letterData);
                }
			}
		}
		if (!(MandarinSortObj.isEmpty())) {
			glossaryMap.put(" ", MandarinSortObj);
			glossaryLetterEntries.add(new IntermediateLetterData(" ", false));
		}
		glossaryLetterEntries.sort(IntermediateLetterData.IntermediateLetterDataComparator);
	}

	/**
	 * Writes the glossary DOM  as XML to a given file.
	 */
	private void writeGlossaryXmlToFile( final File glossaryFile, final Glossary glossary )
			throws JAXBException, IOException
	{
		Marshaller marshaller = glossaryJaxbContext.createMarshaller();
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
		marshaller.setProperty( Marshaller.JAXB_ENCODING, "UTF-8" );

		OutputStreamWriter buffer = new OutputStreamWriter( new FileOutputStream( glossaryFile ), "UTF-8" );
		marshaller.marshal( glossary, buffer );
	}

	//------------------------------------------------------------------------------------------------------------------
	private String glossaryKeyField			= null;
	private String glossaryTranslationField = null;
	private List<String> annotationFields	= new ArrayList<String>();
	private JAXBContext glossaryJaxbContext	= null;
	private SortType sortType				= SortType.L1;

	private File glossaryFile				= null;

	private Properties versionProp			= null;

	private CollatorDataHelper collatorHelper = null;

	private Logger logger = Logger.getLogger( this.getClass() );
	//------------------------------------------------------------------------------------------------------------------
}
