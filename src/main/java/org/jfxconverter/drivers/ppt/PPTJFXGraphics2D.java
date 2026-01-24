/*
 * Copyright (c) 2016, 2017, 2018 Dassault Aviation.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this
 * list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * The views and conclusions contained in the software and documentation are
 * those
 * of the authors and should not be interpreted as representing official
 * policies,
 * either expressed or implied, of the FreeBSD Project.
 * Please contact Dassault Aviation, 9 Rond-Point des Champs Elysees, 75008
 * Paris,
 * France if you need additional information.
 * Alternatively if you have any questions about this project, you can visit
 * the project website at the project page on http://j661.sourceforge.net
 */
package org.jfxconverter.drivers.ppt;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import org.apache.poi.ddf.EscherProperties;
import org.apache.poi.hslf.usermodel.HSLFAutoShape;
import org.apache.poi.hslf.usermodel.HSLFFreeformShape;
import org.apache.poi.hslf.usermodel.HSLFGroupShape;
import org.apache.poi.hslf.usermodel.HSLFLine;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFPictureShape;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSimpleShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.util.Units;
import org.jfxconverter.utils.ExtendedGraphics2D;
import org.mdiutil.geom.ShapeUtilities;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Stack;

/**
 * A PPTGraphics2D which is also an ExtendedGraphics2D. Compared to a
 * {@link PPTGraphics2D}, the following behavior is
 * supported:
 * <ul>
 * <li>Grouping of Shapes</li>
 * <li>Conversions of Node Shadows</li>
 * </ul>
 *
 * @version 0.21
 */
public class PPTJFXGraphics2D extends PPTGraphics2D implements ExtendedGraphics2D< Node, Effect > {
    private boolean                       supportGroups = false;
    private final Stack< HSLFGroupShape > groups        = new Stack<>();
    private HSLFGroupShape                curGroup      = null;
    private DropShadow                    dshadow       = null;
    private InnerShadow                   ishadow       = null;

    /**
     * Default constructor.
     */
    public PPTJFXGraphics2D() {}

    /**
     * Create a Graphics2D which will paint in a PPT Slide context. It will use
     * a black background, a default white foreground,
     * and a default stroke width of 1 pixel.
     *
     * @param pptSlide
     *            the slide
     * @param imWidth
     *            the slide width
     * @param imHeight
     *            the slide height
     */
    public PPTJFXGraphics2D( final HSLFSlide pptSlide, final float imWidth, final float imHeight ) {
        super( pptSlide, imWidth, imHeight );
    }

    /**
     * Create a Graphics2D which will paint in a PPT Slide context.
     *
     * @param pptSlide
     *            the slide
     * @param imWidth
     *            the slide width
     * @param imHeight
     *            the slide height
     * @param background
     *            the slide background
     * @param foreground
     *            the slide foreground
     */
    public PPTJFXGraphics2D( final HSLFSlide pptSlide,
                             final float imWidth,
                             final float imHeight,
                             final Color background,
                             final Color foreground ) {
        super( pptSlide, imWidth, imHeight, background, foreground );
    }

    /**
     * Create a Graphics2D which will paint in a PPT Slide context. It will use
     * a black background and a default white foreground.
     *
     * @param pptSlide
     *            the slide
     * @param imWidth
     *            the slide width
     * @param imHeight
     *            the slide height
     * @param strokeValue
     *            the stroke width
     */
    public PPTJFXGraphics2D( final HSLFSlide pptSlide,
                             final float imWidth,
                             final float imHeight,
                             final float strokeValue ) {
        super( pptSlide, imWidth, imHeight, strokeValue );
    }

    /**
     * Create a Graphics2D which will paint in a PPT Slide context.
     *
     * @param pptSlide
     *            the slide
     * @param imWidth
     *            the slide width
     * @param imHeight
     *            the slide height
     * @param background
     *            the slide background
     * @param foreground
     *            the slide foreground
     * @param strokeValue
     *            the stroke width
     */
    public PPTJFXGraphics2D( final HSLFSlide pptSlide,
                             final float imWidth,
                             final float imHeight,
                             final Color background,
                             final Color foreground,
                             final float strokeValue ) {
        super( pptSlide, imWidth, imHeight, background, foreground, strokeValue );
    }

    /**
     * Copy constructor.
     *
     * @param pptG2d
     *            the Graphics2D used for the PPT conversion
     */
    public PPTJFXGraphics2D( final PPTGraphics2D pptG2d ) {
        super( pptG2d );
    }

    /**
     * Set if grouping is supported. If supported, a
     * {@link org.apache.poi.hslf.usermodel.HSLFGroupShape} will be created
     * when the method {@link #startGroup(java.lang.String, javafx.scene.Node)}
     * is called.
     *
     * @param b
     *            true if grouping is supported
     */
    public void supportGroups( final boolean b ) {
        supportGroups = b;
    }

    /**
     * Return true if grouping is supported.
     *
     * @return true if grouping is supported
     */
    public boolean isSupportingGroups() {
        return supportGroups;
    }

    /**
     * Creates a picture Shape.
     *
     * @param data
     *            the picture data
     * @return the picture Shape
     */
    @Override
    protected HSLFPictureShape doCreatePicture( final HSLFPictureData data ) {
        HSLFPictureShape pict;
        if ( curGroup != null ) {
            pict = new HSLFPictureShape( data, curGroup );
        }
        else {
            pict = new HSLFPictureShape( data );
        }
        return pict;
    }

    /**
     * Creates a Line Shape.
     *
     * @return the Line Shape
     */
    @Override
    protected HSLFLine doCreateLine() {
        HSLFLine linep;
        if ( curGroup != null ) {
            linep = new HSLFLine();
        }
        else {
            linep = new HSLFLine();
        }
        return linep;
    }

    /**
     * Creates an AutoShape.
     *
     * @param type
     *            the AutoShape type
     * @return the AutoShape
     */
    @Override
    protected HSLFAutoShape doCreateAutoShape( final ShapeType type ) {
        HSLFAutoShape recp;
        if ( curGroup != null ) {
            recp = new HSLFAutoShape( type, curGroup );
        }
        else {
            recp = new HSLFAutoShape( type );
        }
        return recp;
    }

    /**
     * Creates a FreeForm Shape.
     *
     * @return the FreeForm Shape
     */
    @Override
    protected HSLFFreeformShape doCreateFreeformShape() {
        HSLFFreeformShape recp;
        if ( curGroup != null ) {
            recp = new HSLFFreeformShape( curGroup );
        }
        else {
            recp = new HSLFFreeformShape();
        }
        return recp;
    }

    /**
     * Creates a TextBox.
     *
     * @return the TextBox
     */
    @Override
    protected HSLFTextBox doCreateTextBox() {
        HSLFTextBox text;
        if ( curGroup != null ) {
            text = new HSLFTextBox( curGroup );
        }
        else {
            text = new HSLFTextBox();
        }
        return text;
    }

    /**
     * Add the Shape to the Slide or the current Group
     *
     * @param shape
     *            the Shape
     */
    @Override
    protected void addShape( final HSLFShape shape ) {
        shape.setSheet( slide );
        if ( curGroup != null ) {
            curGroup.addShape( shape );
        }
        else {
            slide.addShape( shape );
        }
    }

    /**
     * Start a Group.
     *
     * @param name
     *            the group name
     * @param node
     *            the Node
     */
    @Override
    public void startGroup( final String name, final Node node ) {
        if ( supportGroups ) {
            final HSLFGroupShape group = new HSLFGroupShape();
            final Bounds bounds = node.getBoundsInParent();
            Rectangle2D rec = new Rectangle2D.Double( bounds.getMinX(),
                                                      bounds.getMinY(),
                                                      bounds.getWidth(),
                                                      bounds.getHeight() );
            // the Anchor is just necessary to set the bounding box around the
            // anchor. There is no need to handle the
            // coordinate system transformations between the group and the
            // children Shapes thankfully.
            final Shape shape = ShapeUtilities.createTransformedShape( rec, trans );
            rec = shape.getBounds2D();
            group.setAnchor( rec );
            group.setSheet( slide );

            // We support groups of groups
            if ( curGroup != null ) {
                curGroup.addShape( group );
            }
            else {
                slide.addShape( group );
            }
            curGroup = group;
            groups.push( group );
        }
    }

    /**
     * End a Group.
     *
     * @param node
     *            the Node
     */
    @Override
    public void endGroup( final Node node ) {
        if ( supportGroups ) {
            groups.pop();
            if ( groups.isEmpty() ) {
                curGroup = null;
            }
            else {
                curGroup = groups.peek();
            }
        }
    }

    /**
     * Apply an Effect on the next graphics object to be rendered. For the
     * moment, only the following Effects are handled:
     * <ul>
     * <li>{@link javafx.scene.effect.DropShadow}</li>
     * <li>{@link javafx.scene.effect.InnerShadow}</li>
     * </ul>
     * Note that for the moment InnerShadows will not have a correct result.
     *
     * @param node
     *            the Node on which the effect must be applied
     * @param effect
     *            the Effect
     */
    @Override
    public void applyEffect( final Node node, final Effect effect ) {
        dshadow = null;
        ishadow = null;
        if ( effect instanceof DropShadow ) {
            dshadow = ( DropShadow ) effect;
        }
        else if ( effect instanceof InnerShadow ) {
            ishadow = ( InnerShadow ) effect;
        }
    }

    /**
     * Add an additional Shadow to a Shape if the associated Node has a Shadow
     * Effect.
     *
     * @param shape
     *            the Shape
     */
    @Override
    protected void setShapeFillingAdditionalProperties( final HSLFSimpleShape shape ) {
        if ( ( dshadow == null ) && ( ishadow == null ) ) {
            return;
        }

        if ( dshadow != null ) {
            setDropShadow( shape );
        }
        else {
            setInnerShadow( shape );
        }
    }

    /**
     * Add a Drop Shadow to a shape.
     *
     * @param shape
     *            the Shape
     */
    private void setDropShadow( final HSLFSimpleShape shape ) {
        // shadow color
        final javafx.scene.paint.Color jfxColor = dshadow.getColor();
        final int blue = ( int ) ( 255 * jfxColor.getBlue() );
        final int green = ( int ) ( 255 * jfxColor.getGreen() );
        final int red = ( int ) ( 255 * jfxColor.getRed() );
        final int alpha = Units.doubleToFixedPoint( jfxColor.getOpacity() * 0.5d );
        final int rgb = new Color( red, green, blue, 0 ).getRGB();
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__TYPE, 2 ); // 2
                                                                          // for
                                                                          // outer
                                                                          // shadow
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__COLOR, rgb );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__SHADOWOBSURED, 131074 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__OPACITY, alpha );
        // shadow offset
        final int swidth = Units.doubleToFixedPoint( dshadow.getWidth() / 2 );

        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__WEIGHT, swidth );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__OFFSETX, 0 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__OFFSETY, 0 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__SCALEXTOX, 66847 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__SCALEYTOY, 66847 );
    }

    /**
     * Add an Inner Shadow to a shape. Note that for the moment, it will
     * incorrectly show an inner shadow.
     *
     * @param shape
     *            the Shape
     */
    private void setInnerShadow( final HSLFSimpleShape shape ) {
        // shadow color
        final javafx.scene.paint.Color jfxColor = ishadow.getColor();
        final int blue = ( int ) ( 255 * jfxColor.getBlue() );
        final int green = ( int ) ( 255 * jfxColor.getGreen() );
        final int red = ( int ) ( 255 * jfxColor.getRed() );
        final int alpha = Units.doubleToFixedPoint( jfxColor.getOpacity() * 0.5d );
        final int rgb = new Color( red, green, blue, 0 ).getRGB();
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__TYPE, 2 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__COLOR, rgb );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__SHADOWOBSURED, 131074 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__OPACITY, alpha );
        // shadow offset
        final int swidth = Units.doubleToFixedPoint( ishadow.getWidth() / 2 );
        final int scale = Units.doubleToFixedPoint( 0.95 );

        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__WEIGHT, swidth );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__OFFSETX, 0 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__OFFSETY, 0 );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__SCALEXTOX, scale );
        shape.setEscherProperty( EscherProperties.SHADOWSTYLE__SCALEYTOY, scale );
    }
}
