package com.transparent.glossary.gui;

import java.io.File;

/**
 * Objects that implement this interface are interested when the user picks a directory where the glossary files
 * will be written to.
 */
public interface DestinationDirectorySelectionHandler
{
    /**
     * Invoked when the user selects a directory.
     * @param selection the directory that has been selected.
     */
    void handleDestinationDirectorySelected( final File selection );
}
