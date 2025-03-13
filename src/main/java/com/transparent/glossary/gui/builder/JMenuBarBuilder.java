package com.transparent.glossary.gui.builder;

import java.util.LinkedList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * Simplifies building a menu bar.
 */
public class JMenuBarBuilder implements Builder<JMenuBar>
{
    private static final class BuilderState
    {
        private final LinkedList<JMenu> theMenus = new LinkedList<JMenu>();

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
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
    public JMenuBar build()
    {
        final JMenuBar menuBar = new JMenuBar();
        while( !theCurrentState.theMenus.isEmpty() )
        {
            final JMenu menu = theCurrentState.theMenus.pop();
            menuBar.add( menu );
        }
        return menuBar;
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

    public JMenuBarBuilder withMenu( final JMenu menu )
    {
        theCurrentState.theMenus.addLast( menu );
        return this;
    }
}
