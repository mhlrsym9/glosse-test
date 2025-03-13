package com.transparent.glossary.gui.builder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * Simplifies the creation of panels.
 */
public class JPanelBuilder implements Builder<JPanel>
{
    private static final class BuilderState
    {
        /**
         * Title of border should surround the panel.
         */
        private String theBorderTitle;

        /**
         * The background color of the panel.
         */
        private Color theBackgroundColor;

        /**
         * The desired size of the panel.
         */
        private Dimension theSize;

        /**
         * If true, generate a random background color -- useful in debugging layouts.
         */
        private boolean theRandomBackgroundColorFlag = false;

        /**
         * The layout manager to use.
         */
        private LayoutManager theLayoutManager = new GridBagLayout();

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theBorderTitle = original.theBorderTitle;
            theBackgroundColor = original.theBackgroundColor;
            theSize = original.theSize;
            theRandomBackgroundColorFlag = original.theRandomBackgroundColorFlag;
            theLayoutManager = original.theLayoutManager;
        }
    }

    /**
     * Generates random numbers.
     */
    private final Random theRandomNumberGenerator = new Random();

    /**
     * The current state of the builder.
     */
    private BuilderState theCurrentState = new BuilderState();

    /**
     * The default state of the builder.
     */
    private BuilderState theDefaultState = new BuilderState();

    @Override
    public void reset()
    {
        theCurrentState = new BuilderState( theDefaultState );
    }

    @Override
    public JPanel build()
    {
        final JPanel panel = new JPanel( theCurrentState.theLayoutManager );
        setBorder( panel );
        setBackgroundColor( panel );
        setSize( panel );
        return panel;
    }

    @Override
    public void takeSnapshot()
    {
        theDefaultState = new BuilderState( theCurrentState );
    }

    private void setSize( final JPanel aPanel )
    {
        if ( theCurrentState.theSize != null )
        {
            aPanel.setMinimumSize( theCurrentState.theSize );
            aPanel.setMaximumSize( theCurrentState.theSize );
            aPanel.setPreferredSize( theCurrentState.theSize );
            aPanel.setSize( theCurrentState.theSize );
        }
    }

    private void setBackgroundColor( final JPanel aPanel )
    {
        if ( theCurrentState.theBackgroundColor != null )
        {
            aPanel.setBackground( theCurrentState.theBackgroundColor );
        }

        if ( theCurrentState.theRandomBackgroundColorFlag )
        {
            final Color color = new Color( theRandomNumberGenerator.nextInt( 255 ), theRandomNumberGenerator.nextInt( 255 ), theRandomNumberGenerator.nextInt( 255 ) );
            aPanel.setBackground( color );
        }
    }

    private void setBorder( final JPanel aPanel )
    {
        if ( theCurrentState.theBorderTitle != null )
        {
            aPanel.setBorder( new TitledBorder( theCurrentState.theBorderTitle ) );
        }
    }

    public JPanelBuilder withTitledBorder( final String title )
    {
        theCurrentState.theBorderTitle = title;
        return this;
    }

    public JPanelBuilder withBackgroundColor( final Color color )
    {
        theCurrentState.theBackgroundColor = color;
        return this;
    }

    public JPanelBuilder withBackgroundColor( final int red, final int green, final int blue )
    {
        theCurrentState.theBackgroundColor = new Color( red, green, blue );
        return this;
    }

    public JPanelBuilder withRandomBackgroundColor( final boolean state )
    {
        theCurrentState.theRandomBackgroundColorFlag = state;
        return this;
    }

    public JPanelBuilder withSpecificSize( final int width, final int height )
    {
        theCurrentState.theSize = new Dimension( width, height );
        return this;
    }

    public JPanelBuilder withLayoutManager( final LayoutManager manager )
    {
        theCurrentState.theLayoutManager = manager;
        return this;
    }

}
