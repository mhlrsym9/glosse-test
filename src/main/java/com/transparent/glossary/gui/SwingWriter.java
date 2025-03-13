package com.transparent.glossary.gui;

import java.io.IOException;
import java.io.Writer;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * A custom writer that sends characters to a Swing component.
 */
public class SwingWriter extends Writer
{
    /**
     * The component we want to display messages to.
     */
    private final JTextArea theTextArea;

    public SwingWriter( final JTextArea aTextArea )
    {
        theTextArea = aTextArea;
    }

    @Override
    public void write( final char[] buffer, final int offset, final int length ) throws IOException
    {
        final String string = new String( buffer, offset, length );
        if ( SwingUtilities.isEventDispatchThread() )
        {
            theTextArea.append( string  );
        }
        else
        {
            SwingUtilities.invokeLater( new ThreadFriendlyUpdate( string ) );
        }
    }

    @Override
    public void flush() throws IOException
    {
    }

    @Override
    public void close() throws IOException
    {
    }

    private final class ThreadFriendlyUpdate implements Runnable
    {
        private final String theString;

        private ThreadFriendlyUpdate( final String string )
        {
            theString = string;
        }

        @Override
        public void run()
        {
            theTextArea.append( theString );
        }
    }

}
