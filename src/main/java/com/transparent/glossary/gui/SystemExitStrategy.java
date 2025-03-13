package com.transparent.glossary.gui;

/**
 * Shutdown the system by calling System.exit.
 */
public class SystemExitStrategy implements ExitStrategy
{
    @Override
    public void shutdown()
    {
        System.exit( 0 );
    }
}
