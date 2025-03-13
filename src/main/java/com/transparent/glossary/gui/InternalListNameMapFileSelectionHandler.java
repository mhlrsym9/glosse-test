package com.transparent.glossary.gui;

import java.io.File;

/**
 * Objects that implement this interface are interested when the user picks a configuration file to use.
 * Created by MDennehy on 4/8/2015.
 */
public interface InternalListNameMapFileSelectionHandler
{
    /**
     * Callback for when the user picks a file.
     * @param selection selected file.
     */
    void handleInternalListNameMapFileSelected( final File selection );
}
