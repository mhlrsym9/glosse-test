package com.transparent.glossary.io;

import org.w3c.dom.Node;

/**
Strategy Pattern: we need different data extraction logic based on the content layout being processed.
 */
public interface ExtractionStrategy
{
    /**
     * Figures out what unit the word/phrase is referenced in.
     * @return a string in the format of "Unit nn" where nn is the two-digit unit number.
     */
    String determineUnitNumber();

    /**
     * Figures out what needs to be prefixed to a sound file's limited path so that Courseware can play it.
     * @return a string describing what needs to be pre-pended to the file's path, eg unit01/data.
     * @param soundPath partial path to the sound file.
     */
    String buildSoundPath( final String soundPath );

    /**
     * Figures out the part-of-speech the word/phrases has been labeled with.
     * @return the part of speech label.
     */
    String determinePartOfSpeech();

}
