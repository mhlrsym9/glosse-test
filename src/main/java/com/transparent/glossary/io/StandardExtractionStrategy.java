package com.transparent.glossary.io;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * Strategy Pattern: implements the version of the logic useful in "normal" Courseware deployment.
 */
public class StandardExtractionStrategy implements ExtractionStrategy
{
    private static final Logger theLogger = Logger.getLogger( StandardExtractionStrategy.class );

    /**
     * Platform-specific separator -- either / or \.
     */
    private static final String theFileSeparator = System.getProperty( "file.separator" );

    /**
     * Used to find the unit portion of the file path.
     */
    private static final Pattern theUnitRegExpPattern = Pattern.compile( determinePlatformSpecificUnitRegularExpression() );

    private static final Pattern theUnitNumberPattern = Pattern.compile( "unit\\d{1,3}" );

    private final String theUnitRoot;

    public StandardExtractionStrategy( final File inputFile )
    {
        theUnitRoot = calculateUnitRoot( inputFile );
    }

    @Override
    public String determineUnitNumber()
    {
        final String unitNumber = extractUnitNumberFromUnitRoot( theUnitRoot );
        return new StringBuilder( "Unit " ).append( unitNumber ).toString().trim();
    }

    @Override
    public String buildSoundPath( final String soundPath )
    {
        return createRelativeUrl( theUnitRoot, soundPath );
    }

    @Override
    public String determinePartOfSpeech()
    {
        return "uncategorized";
    }

    private String calculateUnitRoot( final File file )
    {
        final String substring;
        final String path = file.getPath().trim();
        final Matcher matcher = theUnitRegExpPattern.matcher( path );
        if ( matcher.find() )
        {
            substring = path.substring( matcher.start(), matcher.end() );
        }
        else
        {
            theLogger.warn( "Unable to determine the unit portion of the file path." );
            substring = "";
        }
        return substring.replaceAll( "\\\\", "/" );
    }

    private String extractUnitNumberFromUnitRoot( final String unitRoot )
    {
        final String unitNumber;
        final Matcher matcher = theUnitNumberPattern.matcher( unitRoot );
        if ( matcher.find() )
        {
            // likely something like unit01 or unit1 or unit001
            final String theUnitPortion = unitRoot.substring( matcher.start(), matcher.end() );
            unitNumber = theUnitPortion.replaceAll( "unit", "" );
        }
        else
        {
            theLogger.warn( "Unable to determine the unit number." );
            unitNumber = "00";
        }
        return unitNumber;
    }

    private static String determinePlatformSpecificUnitRegularExpression()
    {
        final String regularExpression;
        if ( "\\".equals( theFileSeparator ) )
        {
            regularExpression = "unit\\d{1,2}\\\\data\\\\\\w{12}\\w+\\\\";
        }
        else
        {
            regularExpression = "unit\\d{1,2}/data/\\w{12}\\w+/";
        }
        return regularExpression;
    }

    private String createRelativeUrl( final String unitRoot, final String soundURL )
    {
        final StringBuilder builder = new StringBuilder();
        if ( null == soundURL || soundURL.isEmpty() )
        {
            theLogger.debug( "Empty sound URL.  Not creating relative URL." );
        }
        else
        {
            builder.append( unitRoot ).append( soundURL );

        }
        return builder.toString();
    }


}
