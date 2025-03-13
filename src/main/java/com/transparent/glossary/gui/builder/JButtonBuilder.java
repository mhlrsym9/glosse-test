package com.transparent.glossary.gui.builder;

import javax.swing.Action;
import javax.swing.JButton;

/**
 * Simplifies building buttons.
 */
public class JButtonBuilder implements Builder<JButton>
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
    public JButton build()
    {
        final JButton button;
        if ( theCurrentState.theAction != null )
        {
            button = new JButton( theCurrentState.theAction );
        }
        else
        {
            button = new JButton();
        }
        return button;
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

    public JButtonBuilder withAction( final Action action )
    {
        theCurrentState.theAction = action;
        return this;
    }
}
