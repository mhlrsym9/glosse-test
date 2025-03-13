package com.transparent.glossary.gui.builder;

import javax.swing.JEditorPane;

/**
 * Simplifies the creation of editor panes.
 */
public class JEditorPaneBuilder implements Builder<JEditorPane>
{
    private static final class BuilderState
    {
        private String theContentType;
        private String theText;
        private boolean theEditableFlag;

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theContentType = original.theContentType;
            theText = original.theText;
            theEditableFlag = original.theEditableFlag;
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
    public JEditorPane build()
    {
        final JEditorPane pane = new JEditorPane();
        if ( theCurrentState.theContentType != null )
        {
            pane.setContentType( theCurrentState.theContentType );
        }
        if ( theCurrentState.theText != null )
        {
            pane.setText( theCurrentState.theText );
        }
        pane.setEditable( theCurrentState.theEditableFlag );
        return pane;
    }

    @Override
    public void reset()
    {
        theDefaultState = new BuilderState( theCurrentState );
    }

    @Override
    public void takeSnapshot()
    {
        theCurrentState = new BuilderState( theDefaultState );
    }

    public JEditorPaneBuilder withContentType( final String type )
    {
        theCurrentState.theContentType = type;
        return this;
    }

    public JEditorPaneBuilder withText( final String text )
    {
        theCurrentState.theText = text;
        return this;
    }

    public JEditorPaneBuilder withEditing( final boolean state)
    {
        theCurrentState.theEditableFlag = state;
        return this;
    }
}
