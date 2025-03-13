package com.transparent.glossary.gui;

/**
 * Objects that implement this interface are interested when the user enables/disables MARSOC support.
 */
public interface MarsocSelectionHandler
{
    /**
     * Invoked when the user changes his mind about MARSOC support.
     * @param state the new state of support.
     */
    void handleMarsocChange( final boolean state );
}
