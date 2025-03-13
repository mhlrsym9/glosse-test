package com.transparent.glossary.gui;

/**
 * Created by MDennehy on 4/8/2015.
 * Objects that implement this interface are interested when the user specifies an extraction approach.
 */
public interface ExtractionApproachSelectionHandler
{
    /**
     * Invoked when the user chooses the extraction approach she wants to use.
     * @param mode mode to use, may not be null or empty.
     */
    void handleExtractionApproachSelected( final String mode );
}
