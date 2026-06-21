/*
 * MIT License
 *
 * Copyright (c) 2020, 2026 Mark Schmieder. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is part of the FxConverter Library
 *
 * You should have received a copy of the MIT License along with the
 * FxConverter Library. If not, see <https://opensource.org/licenses/MIT>.
 *
 * Project: https://github.com/mhschmieder/fxconverter
 */
package com.mhschmieder.fxvectorexport.dxf;

import com.mhschmieder.fxdxfimport.DxfShapeGroup;
import com.mhschmieder.fxdxfparser.physics.DxfDistanceUnit;
import com.mhschmieder.fxdxfparser.reader.DxfLoader;
import com.mhschmieder.fxgraphics.paint.ColorUtilities;
import com.mhschmieder.jgraphics.DrawMode;
import com.mhschmieder.jgraphics.shape.AttributedShapeContainer;
import com.mhschmieder.jphysics.measure.DistanceUnit;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import org.jfxconverter.utils.JFXShapeUtilities;

import java.awt.geom.AffineTransform;

/**
 * This is a utility class for dealing with conversions of information in the
 * DXF domain to JavaFX and other standard graphics toolkits for Java.
 */
public final class DxfConverterUtilities {

    /**
     * Returns a {@link DxfShapeGroup} that contains a parsed DXF file structure
     * converted from DXF Entities and Blocks into JavaFX Shapes as Scene Graph
     * Nodes. It presumes that the DXF Loader has already been run and is
     * populated with parsed DXF entities.
     * 
     * @param dxfLoader The DXF Loader that contains the read-in DXF file
     * @return a {@link DxfShapeGroup} that contains a parsed DXF file structure
     */
    public static DxfShapeGroup convertToFxShapes( final DxfLoader dxfLoader ) {
        // Convert the DXF Distance Unit to our supported subset.
        final DxfDistanceUnit dxfDistanceUnit = dxfLoader.getDistanceUnit();
        final DistanceUnit importedGeometryDistanceUnit = DxfConverterUtilities
                .getDistanceUnit( dxfDistanceUnit );

        // Query the Drawing Limits stored with the DXF document.
        final double importedGeometryLimitsMinX = dxfLoader.getLimitsMinX();
        final double importedGeometryLimitsMinY = dxfLoader.getLimitsMinY();
        final double importedGeometryLimitsMaxX = dxfLoader.getLimitsMaxX();
        final double importedGeometryLimitsMaxY = dxfLoader.getLimitsMaxY();

        // Construct a candidate for the loading of newly imported geometry.
        final DxfShapeGroup dxfShapeGroup = new DxfShapeGroup( importedGeometryDistanceUnit,
                                                               importedGeometryLimitsMinX,
                                                               importedGeometryLimitsMinY,
                                                               importedGeometryLimitsMaxX,
                                                               importedGeometryLimitsMaxY );

        try {
            // Use the DXF Loader to convert the DXF geometry into generic
            // JavaFX Shapes, using an enhanced Group as a Shape Container.
            dxfLoader.convertToFxShapes( dxfShapeGroup );
        }
        catch ( final OutOfMemoryError oome ) {
            oome.printStackTrace();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        // Invalidate the DXF Document after each Graphics Import or File Open.
        dxfLoader.invalidateDocument();

        // Return the imported geometry container for downstream clients.
        return dxfShapeGroup;
    }

    /**
     * This method takes an extended Distance Unit set from DXF and converts
     * it to the smaller subset of Distance Units supported within our other
     * libraries and applications. Unsupported units get mapped to "Unitless".
     * <p>
     * TODO: Add an enumeration value for "Unsupported", to treat differently?
     *
     * @param dxfDistanceUnit
     *            The raw Distance Unit from the DXF file
     * @return The smaller subset of Distance Units supported within our
     *         other libraries and applications
     */
    public static DistanceUnit getDistanceUnit( final DxfDistanceUnit dxfDistanceUnit ) {
        DistanceUnit importedGeometryDistanceUnit = DistanceUnit.UNITLESS;
        switch ( dxfDistanceUnit ) {
        case UNITLESS:
            importedGeometryDistanceUnit = DistanceUnit.UNITLESS;
            break;
        case INCHES:
            importedGeometryDistanceUnit = DistanceUnit.INCHES;
            break;
        case FEET:
            importedGeometryDistanceUnit = DistanceUnit.FEET;
            break;
        case MILES:
            // We do not yet support miles, so make unitless.
            break;
        case MILLIMETERS:
            importedGeometryDistanceUnit = DistanceUnit.MILLIMETERS;
            break;
        case CENTIMETERS:
            importedGeometryDistanceUnit = DistanceUnit.CENTIMETERS;
            break;
        case METERS:
            importedGeometryDistanceUnit = DistanceUnit.METERS;
            break;
        case KILOMETERS:
        case MICROINCHES:
        case MILS:
            // We do not yet support statue kilometers, microinches, or mils,
            // as they are uncommon, but should be reviewed.
            break;
        case YARDS:
            importedGeometryDistanceUnit = DistanceUnit.YARDS;
            break;
        case ANGSTROMS:
        case NANOMETERS:
        case MICRONS:
            // NOTE: These units are on a microscopic scale so are irrelevant
            // to architecture and related fields.
            break;
        case DECIMETERS:
        case DECAMETERS:
        case HECTOMETERS:
        case GIGAMETERS:
            // NOTE: These units are less common but should be reviewed.
            break;
        case ASTRONOMICAL_UNITS:
        case LIGHT_YEARS:
        case PARSECS:
            // NOTE: The remaining cases are all unused scientific units.
            break;
        default:
            break;
        }

        return importedGeometryDistanceUnit;
    }

    /**
     * This method converts a full container of JavaFX based Shapes into an
     * equivalent container of AWT based Shapes.
     *
     * @param geometryContainerFx
     *            The original JavaFX Shape container
     * @param scaleTransform
     *            If relevant, a scale factor to apply globally to the full
     *            collection of Shapes
     * @return The converted AWT Shape container
     */
    public static AttributedShapeContainer makeGeometryContainerAwt( final DxfShapeGroup geometryContainerFx,
                                                                     final AffineTransform scaleTransform ) {
        // Make an AWT Geometry Container to fit all of the entities.
        final ObservableList< Node > importedGeometry = geometryContainerFx.getChildren();
        final AttributedShapeContainer geometryContainerAwt =
                                                            new AttributedShapeContainer( importedGeometry
                                                                    .size(), scaleTransform );

        // Iterate to convert and copy the entities, but note that this
        // approach may lose information such as color and line stroke, as
        // that was not preserved in the new JavaFX Geometry Container.
        for ( final Node entity : importedGeometry ) {
            // Use JFXConverter to transcode the JavaFX graphics to AWT.
            final Shape shape = ( Shape ) entity;
            final java.awt.Shape shapeAwt = JFXShapeUtilities.getShape( shape );

            // Do not pre-compensate for Block Insert transforms (when
            // present), due to downstream transform order issues in AWT.
            final AffineTransform transformAwt = JFXShapeUtilities
                    .getTransform( shape );

            // Add this converted shape to the AWT Geometry Container.
            final Color dxfColor = ( Color ) shape.getStroke();
            final java.awt.Color dxfColorAwt = ColorUtilities.getColor( dxfColor );
            final Paint fill = shape.getFill();
            final DrawMode drawMode = ( fill != null ) ? DrawMode.FILL : DrawMode.STROKE;
            geometryContainerAwt.addShape( shapeAwt, dxfColorAwt, drawMode, transformAwt );
        }

        return geometryContainerAwt;
    }

    // NOTE: The constructor is disabled, since this is a static class.
    private DxfConverterUtilities() {}

}// class DxfConverterUtilities
