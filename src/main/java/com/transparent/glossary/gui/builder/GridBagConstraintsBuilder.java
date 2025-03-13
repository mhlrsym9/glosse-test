package com.transparent.glossary.gui.builder;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Fluent interface for build constraints for the GridBayLayout.  Uses reasonable defaults and should simplify
 * things.
 */
public class GridBagConstraintsBuilder implements Builder<GridBagConstraints>
{
    public final static double LOW = 0.0;
    public final static double MEDIUM = 0.5;
    public final static double HIGH = 1.0;

    /**
     * The current state of the builder.
     */
    private BuilderState theCurrentState = new BuilderState();

    /**
     * The default state of the builder.
     */
    private BuilderState theDefaultState = new BuilderState();

    private static class BuilderState
    {
        private int theGridX = 0;
        private int theGridY = 0;
        private int theGridWidth = 1;
        private int theGridHeight = 1;
        private double theRowWeight = MEDIUM;
        private double theColumnWeight = MEDIUM;
        private int theAnchor = GridBagConstraints.CENTER;
        private int theFill = GridBagConstraints.NONE;
        private int thePadX = 0;
        private int thePadY = 0;
        private Insets theExternalPadding = new Insets( 0, 0, 0, 0 );

        private BuilderState()
        {
        }

        private BuilderState( final BuilderState original )
        {
            theGridX = original.theGridX;
            theGridY = original.theGridY;
            theGridWidth = original.theGridWidth;
            theGridHeight = original.theGridHeight;
            theRowWeight = original.theRowWeight;
            theColumnWeight = original.theColumnWeight;
            theAnchor = original.theAnchor;
            theFill = original.theFill;
            thePadX = original.thePadX;
            thePadY = original.thePadY;
            theExternalPadding = original.theExternalPadding;
        }
    }

    @Override
    public GridBagConstraints build()
    {
        return new GridBagConstraints( theCurrentState.theGridX,
                                       theCurrentState.theGridY,
                                       theCurrentState.theGridWidth,
                                       theCurrentState.theGridHeight,
                                       theCurrentState.theColumnWeight,
                                       theCurrentState.theRowWeight,
                                       theCurrentState.theAnchor,
                                       theCurrentState.theFill,
                                       theCurrentState.theExternalPadding,
                                       theCurrentState.thePadX,
                                       theCurrentState.thePadY );
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

    /**
     * Maps to gridx and gridy properties, defaults to 0. Specify the row and column at the upper left of the component. The
     * leftmost column has address gridx=0 and the top row has address gridy=0.
     * @param column column the component to should be placed in.
     * @param row row the component to should be placed in.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withLocation( final int column, final int row )
    {
        theCurrentState.theGridY = row;
        theCurrentState.theGridX = column;
        return this;
    }

    /**
     * Maps to gridx property, defaults to 0. Specify the row and column at the upper left of the component. The
     * leftmost column has address gridx=0 and the top row has address gridy=0.
     * @param row row the component to should be placed in.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withRow( final int row )
    {
        theCurrentState.theGridY = row;
        return this;
    }

    /**
     * Maps to gridy property, defaults to 0. Specify the row and column at the upper left of the component. The
     * leftmost column has address gridx=0 and the top row has address gridy=0.
     * @param column column the component to should be placed in.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withColumn( final int column )
    {
        theCurrentState.theGridX = column;
        return this;
    }

    /**
     * Maps to the gridWidth property. Specify the number of columns (for gridwidth) or rows (for gridheight) in the
     * component's display area. These constraints specify the number of cells the component uses, not the number of
     * pixels it uses. The default value is 1.
     * @param span how many columns the component should span.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withColumnSpan( final int span )
    {
        theCurrentState.theGridWidth = span;
        return this;
    }

    /**
     * Maps to the gridHeight property. Specify the number of columns (for gridwidth) or rows (for gridheight) in
     * the component's display area. These constraints specify the number of cells the component uses, not the number
     * of pixels it uses. The default value is 1.
     * @param span how many rows the component should span.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withRowSpan( final int span )
    {
        theCurrentState.theGridHeight = span;
        return this;
    }

    public GridBagConstraintsBuilder withDefaultSpans()
    {
        theCurrentState.theGridHeight = 1;
        theCurrentState.theGridWidth = 1;
        return this;
    }
    /**
     * Maps to the weightx property, defaults to MEDIUM weight. Specifying weights is an art that can have a significant
     * impact on the appearance of the components a GridBagLayout controls. Weights are used to determine how to
     * distribute space among columns (weightx) and among rows (weighty); this is important for specifying resizing
     * behavior. Unless you specify at least one non-zero value for weightx or weighty, all the components clump
     * together in the center of their container. This is because when the weight is 0.0 (the default), the
     * GridBagLayout puts any extra space between its grid of cells and the edges of the container.   Generally weights
     * are specified with 0.0 and 1.0 as the extremes: the numbers in between are used as necessary. Larger numbers
     * indicate that the component's row or column should get more space. For each column, the weight is related to
     * the highest weightx specified for a component within that column, with each multicolumn component's weight
     * being split somehow between the columns the component is in. Similarly, each row's weight is related to the
     * highest weighty specified for a component within that row. Extra space tends to go toward the rightmost column
     * and bottom row.
     * @param weight how heavily to value this component when calculating column widths.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withColumnWeight( final double weight )
    {
        theCurrentState.theColumnWeight = weight;
        return this;
    }

    /**
     * Maps to the weighty property, defaults to MEDIUM weight.  Specifying weights is an art that can have a significant
     * impact on the appearance of the components a GridBagLayout controls. Weights are used to determine how to
     * distribute space among columns (weightx) and among rows (weighty); this is important for specifying resizing
     * behavior. Unless you specify at least one non-zero value for weightx or weighty, all the components clump
     * together in the center of their container. This is because when the weight is 0.0 (the default), the
     * GridBagLayout puts any extra space between its grid of cells and the edges of the container. Generally weights
     * are specified with 0.0 and 1.0 as the extremes: the numbers in between are used as necessary. Larger numbers
     * indicate that the component's row or column should get more space. For each column, the weight is related to the
     * highest weightx specified for a component within that column, with each multicolumn component's weight being
     * split somehow between the columns the component is in. Similarly, each row's weight is related to the highest
     * weighty specified for a component within that row. Extra space tends to go toward the rightmost column and
     * bottom row.
     * @param weight how heavily to value this component when calculating row heights.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withRowWeight( final double weight )
    {
        theCurrentState.theRowWeight = weight;
        return this;
    }

    public GridBagConstraintsBuilder withLowWeights()
    {
        theCurrentState.theRowWeight = LOW;
        theCurrentState.theColumnWeight = LOW;
        return this;
    }

    public GridBagConstraintsBuilder withHighWeights()
    {
        theCurrentState.theRowWeight = HIGH;
        theCurrentState.theColumnWeight = HIGH;
        return this;
    }


    public GridBagConstraintsBuilder withMediumWeights()
    {
        theCurrentState.theRowWeight = MEDIUM;
        theCurrentState.theColumnWeight = MEDIUM;
        return this;
    }

    /**
     * Maps to the weighty property, defaults to MEDIUM weight.  Specifying weights is an art that can have a significant
     * impact on the appearance of the components a GridBagLayout controls. Weights are used to determine how to
     * distribute space among columns (weightx) and among rows (weighty); this is important for specifying resizing
     * behavior. Unless you specify at least one non-zero value for weightx or weighty, all the components clump
     * together in the center of their container. This is because when the weight is 0.0 (the default), the
     * GridBagLayout puts any extra space between its grid of cells and the edges of the container. Generally weights
     * are specified with 0.0 and 1.0 as the extremes: the numbers in between are used as necessary. Larger numbers
     * indicate that the component's row or column should get more space. For each column, the weight is related to the
     * highest weightx specified for a component within that column, with each multicolumn component's weight being
     * split somehow between the columns the component is in. Similarly, each row's weight is related to the highest
     * weighty specified for a component within that row. Extra space tends to go toward the rightmost column and
     * bottom row.
     * @param columnWeight how heavily to value this component when calculating column widths.
     * @param rowWeight how heavily to value this component when calculating row heights.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withWeights( final double columnWeight, final double rowWeight )
    {
        theCurrentState.theRowWeight = rowWeight;
        theCurrentState.theColumnWeight = columnWeight;
        return this;
    }

    /**
     * Maps to the anchor property.  Used when the component is smaller than its display area
     * to determine where (within the area) to place the component. Valid values (defined as GridBagConstraints constants)
     * are CENTER (the default), PAGE_START, PAGE_END, LINE_START, LINE_END, FIRST_LINE_START, FIRST_LINE_END,
     * LAST_LINE_END, and LAST_LINE_START.
     * @param anchor how many rows the component should span.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withAnchor( final int anchor )
    {
        theCurrentState.theAnchor = anchor;
        return this;
    }


    /**
     * Maps to the fill property. Used when the component's display area is larger than the component's requested size
     * to determine whether and how to resize the component. Valid values (defined as GridBagConstraints constants)
     * include NONE (the default), HORIZONTAL (make the component wide enough to fill its display area horizontally,
     * but do not change its height), VERTICAL (make the component tall enough to fill its display area vertically, but
     * do not change its width), and BOTH (make the component fill its display area entirely).
     * @param fill how the component should expand.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withFill( final int fill )
    {
        theCurrentState.theFill = fill;
        return this;
    }

    /**
     * Maps to the ipadx property.  Specifies the internal padding: how much to add to the size of the component. The
     * default value is zero. The width of the component will be at least its minimum width plus ipadx*2 pixels,
     * since the padding applies to both sides of the component. Similarly, the height of the component will be at
     * least its minimum height plus ipady*2 pixels.
     * @param pad how many pixels to pad.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withHorizontalPad( final int pad )
    {
        theCurrentState.thePadX = pad;
        return this;
    }

    /**
     * Maps to the ipady property.  Specifies the internal padding: how much to add to the size of the component. The
     * default value is zero. The width of the component will be at least its minimum width plus ipadx*2 pixels,
     * since the padding applies to both sides of the component. Similarly, the height of the component will be at
     * least its minimum height plus ipady*2 pixels.
     * @param pad how many pixels to pad.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withVerticalPad( final int pad )
    {
        theCurrentState.thePadY = pad;
        return this;
    }

    /**
     * Maps to the insets property. Specifies the external padding of the component -- the minimum amount of space
     * between the component and the edges of its display area. The value is specified as an Insets object. By default,
     * each component has no external padding.
     * @param top how many pixels to pad from the top.
     * @param left how many pixels to pad from the left.
     * @param bottom how many pixels to pad from the bottom.
     * @param right how many pixels to pad from the right.
     * @return this builder.
     */
    public GridBagConstraintsBuilder withExternalPadding( final int top, final int left, final int bottom, final int right )
    {
        theCurrentState.theExternalPadding = new Insets( top, left, bottom, right );
        return this;
    }

    /**
     * Use this to compare two constraints.  It will print out any differences it detects.
     * @param apple one constraint.
     * @param orange another constraint.
     */
    public static void compareConstraints( final GridBagConstraints apple, final GridBagConstraints orange )
    {
        if ( apple.gridx != orange.gridx )
        {
            System.err.println( "apple.gridx = " + apple.gridx + " orange.gridx = " + orange.gridx );
        }

        if ( apple.gridy != orange.gridy )
        {
            System.err.println( "apple.gridy = " + apple.gridy + " orange.gridy = " + orange.gridy );
        }

        if ( apple.gridwidth != orange.gridwidth )
        {
            System.err.println( "apple.gridwidth = " + apple.gridwidth + " orange.gridwidth = " + orange.gridwidth );
        }

        if ( apple.gridheight != orange.gridheight )
        {
            System.err.println( "apple.gridheight = " + apple.gridheight + " orange.gridheight = " + orange.gridheight );
        }

        if ( apple.anchor != orange.anchor )
        {
            System.err.println( "apple.anchor = " + apple.anchor + " orange.anchor = " + orange.anchor );
        }

        if ( apple.fill != orange.fill )
        {
            System.err.println( "apple.fill = " + apple.fill + " orange.fill = " + orange.fill );
        }

        if ( apple.ipadx != orange.ipadx )
        {
            System.err.println( "apple.ipadx = " + apple.ipadx + " orange.ipadx = " + orange.ipadx );
        }

        if ( apple.ipady != orange.ipady )
        {
            System.err.println( "apple.ipady = " + apple.ipady + " orange.ipady = " + orange.ipady );
        }

        if ( !equals( apple.weightx, orange.weightx ) )
        {
            System.err.println( "apple.weightx = " + apple.weightx + " orange.weightx = " + orange.weightx );
        }

        if ( !equals( apple.weighty, orange.weighty ) )
        {
            System.err.println( "apple.weighty = " + apple.weighty + " orange.weighty = " + orange.weighty );
        }

        if ( !apple.insets.equals( orange.insets ) )
        {
            System.err.println( "apple.insets = " + apple.insets + " orange.insets = " + orange.insets );
        }
    }

    /**
     * Compares two doubles, accounting for precision errors.
     * @param a one double.
     * @param b the second double.
     * @return true if they are equal to with the accepted tolerances.
     */
    private static boolean equals( final double a, final double b )
    {
        final double EPSILON = 0.0000001;

        return a == b || Math.abs( a - b ) < EPSILON * Math.max( Math.abs( a ), Math.abs( b ) );
    }
}
