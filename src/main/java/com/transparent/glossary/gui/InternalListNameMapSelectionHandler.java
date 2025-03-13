package com.transparent.glossary.gui;

/**
 * Invoked when the user changes his mind about internal list name map support.
 * Created by MDennehy on 4/8/2015.
 */
public interface InternalListNameMapSelectionHandler
{
    /**
     * Invoked when the user changes his mind about internal list name map support.
     * @param state the new state of support.
     */
    void handleInternalListNameMapChange( final boolean state );
}
