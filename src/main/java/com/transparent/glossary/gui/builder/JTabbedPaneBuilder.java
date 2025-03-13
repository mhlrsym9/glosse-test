package com.transparent.glossary.gui.builder;

import java.awt.Component;
import java.util.LinkedList;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

/**
 * Simplifies construction of JTabbedPanes.
 */
public class JTabbedPaneBuilder implements Builder<JTabbedPane>
{
    private static final class BuilderState
    {
        private int theTabOrientation = SwingConstants.TOP;
        private boolean theScrollerFlag = false;
        private final LinkedList<TabInfo> theTabs = new LinkedList<TabInfo>();

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theTabOrientation = original.theTabOrientation;
            theScrollerFlag = original.theScrollerFlag;
        }
    }

    private static final class TabInfo
    {
        private String theTitle;
        private Icon theIcon;
        private Component theComponent;
        private String theTip;
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
    public JTabbedPane build()
    {
        final JTabbedPane tabbedPane = new JTabbedPane( theCurrentState.theTabOrientation );
        while( !theCurrentState.theTabs.isEmpty() )
        {
            final TabInfo info = theCurrentState.theTabs.pop();
            tabbedPane.addTab( info.theTitle, info.theIcon, info.theComponent, info.theTip );
        }
        return tabbedPane;
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

    public JTabbedPaneBuilder withTopTabs()
    {
        theCurrentState.theTabOrientation = SwingConstants.TOP;
        return this;
    }

    public JTabbedPaneBuilder withScroller( final boolean state )
    {
        theCurrentState.theScrollerFlag = state;
        return this;
    }


    public JTabbedPaneBuilder withTab( final String title, final Component component, final String tip )
    {
        final TabInfo info = new TabInfo();
        info.theTitle = title;
        info.theTip = tip;
        if ( theCurrentState.theScrollerFlag )
        {
            info.theComponent = new JScrollPane( component );
        }
        else
        {
            info.theComponent = component;
        }
        theCurrentState.theTabs.addLast( info );
        return this;
    }
}
