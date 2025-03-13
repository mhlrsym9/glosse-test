package com.transparent.glossary.gui.builder;

import java.util.LinkedList;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Simplifies building a menu.
 */
public class JMenuBuilder implements Builder<JMenu>
{
    private static final class BuilderState
    {
        private Action theAction;
        private final LinkedList<JMenuItem> theMenuItems = new LinkedList<JMenuItem>();

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theAction = original.theAction;
        }
    }

    /**
     * Current state of the builder.
     */
    private BuilderState theCurrentState = new BuilderState();

    /**
     * Default state of the builder.
     */
    private BuilderState theDefaultState = new BuilderState();



    @Override
    public JMenu build()
    {
        final JMenu menu;
        if ( theCurrentState.theAction != null )
        {
            menu = new JMenu( theCurrentState.theAction );
        }
        else
        {
            menu = new JMenu();
        }

        while( !theCurrentState.theMenuItems.isEmpty() )
        {
            final JMenuItem menuItem = theCurrentState.theMenuItems.pop();
            menu.add( menuItem );
        }

        return menu;
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

    public JMenuBuilder withAction( final Action action )
    {
        theCurrentState.theAction = action;
        return this;
    }

    public JMenuBuilder withMenuItem( final Action action )
    {
        theCurrentState.theMenuItems.addLast( new JMenuItem( action ) );
        return this;
    }
}
