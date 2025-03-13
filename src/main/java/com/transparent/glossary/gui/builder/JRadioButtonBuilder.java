package com.transparent.glossary.gui.builder;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

/**
 * Simplifies the creation of radio buttons.
 */
public class JRadioButtonBuilder implements Builder<JRadioButton>
{
    private static final class BuilderState
    {
        private Action theAction;
        private ButtonGroup theButtonGroup;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theAction = original.theAction;
            theButtonGroup = original.theButtonGroup;
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
    public JRadioButton build()
    {
        final JRadioButton button;
        if ( theCurrentState.theAction != null )
        {
            button = new JRadioButton( theCurrentState.theAction );
        }
        else
        {
            button = new JRadioButton();
        }
        if ( theCurrentState.theButtonGroup != null )
        {
            theCurrentState.theButtonGroup.add( button );
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

    public JRadioButtonBuilder withAction( final Action action )
    {
        theCurrentState.theAction = action;
        return this;
    }

    public JRadioButtonBuilder withButtonGroup( final ButtonGroup group )
    {
        theCurrentState.theButtonGroup = group;
        return this;
    }

}
