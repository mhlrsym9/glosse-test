package com.transparent.glossary.io;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * Strategy Pattern: implements the version of the logic useful in "normal" Courseware deployment.
 */
public class MarsocExtractionStrategy implements ExtractionStrategy
{
    private static final Logger theLogger = Logger.getLogger( MarsocExtractionStrategy.class );

    /**
     * Regular expression to find the language code portion of something like: PORbr_ENGus_32
     */
    private final String theLanguageCodeRegularExpression = "[A-Za-z]{5}?_[A-Za-z]{5}?_";

    /**
     * Pre-compiled regexp pattern.
     */
    private final Pattern theLanguageCodePattern = Pattern.compile( theLanguageCodeRegularExpression );

    /**
     * Pre-compiled regexp pattern that should find patterns like PORbr_ENGus_01_Adjectives.xml
     */
    private final Pattern theB4xNamePattern = Pattern.compile( "[A-Za-z]{5}?_[A-Za-z]{5}?_\\d{2,}_[A-Za-z_]*\\.xml" );

    private final Logger theStatusLogger;

    private final String theB4XName;

    public MarsocExtractionStrategy( final Logger statusLogger,
                                     final File b4xFile )
    {
        theStatusLogger = statusLogger;
        theB4XName = extractB4XName( b4xFile );
    }

    private String extractB4XName( final File inputFile )
    {
        final String b4xName;
        final String path = inputFile.getPath().trim();
        final Matcher matcher = theB4xNamePattern.matcher( path );
        if ( matcher.find() )
        {
            b4xName = path.substring( matcher.start(), matcher.end() - ".xml".length() );
        }
        else
        {
            theStatusLogger.warn( "The B4X path " + path + " does not contain the pattern of AAAaa_BBBbb_00.xml"  );
            b4xName = "ENGus_ENGus_99";
        }
        return b4xName;
    }

    @Override
    public String determineUnitNumber()
    {
        final String unitNumber = extractUnitNumber( theB4XName );
        return new StringBuilder( "Unit " ).append( unitNumber ).toString().trim();
    }

    @Override
    public String buildSoundPath( final String soundPath )
    {
        final String path;
        if ( null == soundPath || soundPath.isEmpty() )
        {
            path = "";
        }
        else
        {
            //alphabet/glossary/B4X/sound path
            path = new StringBuilder().append( "alphabet/glossary/" ).append( theB4XName ).append( "/" ).append( soundPath ).toString();
        }
        return path ;
    }

    @Override
    public String determinePartOfSpeech()
    {
        return extractPartOfSpeech( theB4XName );
    }

    private String extractPartOfSpeech( final String rawB4xName )
    {
        return stripOffLessonName( rawB4xName ).replace( '_', ' ' );
    }

    private String stripOffLessonName( final String rawB4xName )
    {
        // given PORbr_ENGus_19_Verbs_and_Verbal_Phrases, replace everything up to the 19_ with an empty string,
        // removing the lesson name
        return rawB4xName.replaceAll( "\\A\\w*_\\d\\d_", "" );
    }

    private String extractUnitNumber( final String rawB4xName )
    {
        final String processedB4xName = trimPartOfSpeechEncoding( rawB4xName );
        final Matcher matcher = theLanguageCodePattern.matcher( processedB4xName );
        final String unitNumber;
        if ( matcher.lookingAt() )
        {
            unitNumber = processedB4xName.replaceFirst( theLanguageCodeRegularExpression, "" );
        }
        else
        {
            theStatusLogger.warn( "The B4X name " + rawB4xName + " does not match the pattern of AAAaa_BBBbb_00."  );
            unitNumber = "00";
        }
        return unitNumber;
    }

    private String trimPartOfSpeechEncoding( final String rawB4xName )
    {
        return stripOffPartOfSpeechSuffix( rawB4xName );
    }

    /**
     * MARSOC B4X file names are encoded with the part of speech that the contained word are, eg. ENGus_30_Nouns.
     * This routine removes the final _Nouns portion of the name leaving the actual unit name.
     * @param rawB4xName actual B4X name.
     * @return B4X name minus the part of speech suffix.
     */
    private String stripOffPartOfSpeechSuffix( final String rawB4xName )
    {
        // given PORbr_ENGus_19_Verbs_and_Verbal_Phrases, replace everything after the 19_ with an empty string,
        // removing the part of speech suffix
        return rawB4xName.replaceFirst( "(?<=_\\d\\d).*", "" );
    }
}
