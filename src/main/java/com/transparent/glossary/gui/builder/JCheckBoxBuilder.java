package com.transparent.glossary.gui.builder;

import javax.swing.Action;
import javax.swing.JCheckBox;

/**
 * Simplifies building check boxes.
 */
public class JCheckBoxBuilder implements Builder<JCheckBox>
{
    private static final class BuilderState
    {
        private Action theAction;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theAction = original.theAction;
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
    public JCheckBox build()
    {
        final JCheckBox checkBox;
        if ( theCurrentState.theAction != null )
        {
            checkBox = new JCheckBox( theCurrentState.theAction );
        }
        else
        {
            checkBox = new JCheckBox();
        }
        return checkBox;
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

    public JCheckBoxBuilder withAction( final Action action )
    {
        theCurrentState.theAction = action;
        return this;
    }
}
