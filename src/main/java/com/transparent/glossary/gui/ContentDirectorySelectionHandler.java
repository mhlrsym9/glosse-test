package com.transparent.glossary.gui;

import java.io.File;

/**
 * Objects that implement this interface are interested when the user picks a directory from where the content will
 * be read from.
 */
public interface ContentDirectorySelectionHandler
{
    /**
     * Invoked when the user selects a directory.
     * @param selection the selected directory.
     */
    void handleContentDirectorySelected( final File selection );
}
