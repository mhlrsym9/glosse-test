package com.transparent.glossary.gui.builder;

import javax.swing.JTextArea;

/**
 * Simplifies building out text areas.
 */
public class JTextAreaBuilder implements Builder<JTextArea>
{
    private static final class BuilderState
    {
        private int theRowCount;
        private int theColumnCount;
        private boolean theEditableFlag;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theRowCount = original.theRowCount;
            theColumnCount = original.theColumnCount;
            theEditableFlag = original.theEditableFlag;
        }
    }

    /**
     * The current state of the builder.
     */
    private BuilderState theCurrentState = new BuilderState();

    /**
     * The default state of the builder.
     */
    private BuilderState theDefaultState = new BuilderState();

    @Override
    public JTextArea build()
    {
        final JTextArea textArea = new JTextArea( theCurrentState.theColumnCount, theCurrentState.theRowCount );
        textArea.setEditable( theCurrentState.theEditableFlag );
        return textArea;
    }

    @Override
    public void reset()
    {
        theCurrentState = new BuilderState( theDefaultState );
    }

    @Override
    public void takeSnapshot()
    {
        theDefaultState = new BuilderState( theDefaultState );
    }

    public JTextAreaBuilder withEditable( final boolean  state )
    {
        theCurrentState.theEditableFlag = state;
        return this;
    }

    public JTextAreaBuilder withArea( final int columns, final int rows )
    {
        theCurrentState.theColumnCount = columns;
        theCurrentState.theRowCount = rows;
        return this;
    }
}
