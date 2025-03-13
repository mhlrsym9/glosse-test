package com.transparent.glossary.gui.builder;

import javax.swing.JTextField;

/**
 * Simplify building out text fields.
 */
public final class JTextFieldBuilder implements Builder<JTextField>
{
    /**
     * Holds the default values this builder should be using.
     */
    private BuilderState theDefaultState = new BuilderState();

    /**
     * Holds the current values this builder should be using.
     */
    private BuilderState theCurrentState = new BuilderState();

    @Override
    public JTextField build()
    {
        final JTextField field = new JTextField( theCurrentState.theNumberOfColumns );
        if ( theCurrentState.theTip != null )
        {
            field.setToolTipText( theCurrentState.theTip );
        }
        return field;
    }

    @Override
    public void reset()
    {
        theCurrentState = new BuilderState( theDefaultState );
    }

    @Override
    public void takeSnapshot()
    {
        theDefaultState = new BuilderState( theCurrentState );
    }

    public JTextFieldBuilder withColumnCount( final int columnCount )
    {
        theCurrentState.theNumberOfColumns = columnCount;
        return this;
    }

    public JTextFieldBuilder withTip( final String tip )
    {
        theCurrentState.theTip = tip;
        return this;
    }

    private static final class BuilderState
    {
        private int theNumberOfColumns = 0;
        private String theTip;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theNumberOfColumns = original.theNumberOfColumns;
            theTip = original.theTip;
        }
    }
}
