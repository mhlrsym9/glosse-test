package com.transparent.glossary.gui;

/**
 * Objects that implement this interface are interested when the user specifies a stop words processing mode.
 */
public interface StopWordSelectionHandler
{
    /**
     * Invoked when the user chooses the stop word processing mode she wants to use.
     * @param mode mode to use, may not be null or empty.
     */
    void handleStopWordSelected( final String mode );
}
