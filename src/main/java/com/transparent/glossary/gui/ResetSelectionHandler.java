package com.transparent.glossary.gui;

import java.io.File;

/**
 * Objects that implement this interface are interested when the user decides to reset the form to its default state.
 */
public interface ResetSelectionHandler
{
    /**
     * Invoked when the user selects reset from the menu.
     */
    void handleResetSelected();
}
