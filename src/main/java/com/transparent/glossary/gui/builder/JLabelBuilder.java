package com.transparent.glossary.gui.builder;

import javax.swing.JLabel;

/**
 * Simplifies the creation of labels.
 */
public class JLabelBuilder implements Builder<JLabel>
{
    private static final class BuilderState
    {
        private String theText;
        private String theTip;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theText = original.theText;
            theTip = original.theTip;
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
    public JLabel build()
    {
        final JLabel label;
        if ( theCurrentState.theText != null )
        {
            label = new JLabel( theCurrentState.theText );
        }
        else
        {
            label = new JLabel();
        }
        if ( theCurrentState.theTip != null )
        {
            label.setToolTipText( theCurrentState.theTip );
        }
        return label;
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

    public JLabelBuilder withText( final String text )
    {
        theCurrentState.theText = text;
        return this;
    }

    public JLabelBuilder withTip( final String tip )
    {
        theCurrentState.theTip = tip;
        return this;
    }
}
