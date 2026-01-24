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
package com.mhschmieder.fxvectorexport;

import javafx.scene.shape.Shape;
import org.jfxconverter.utils.JFXShapeUtilities;

import java.util.List;

/**
 * {@code ConverterUtilities} is a utility class for methods that help convert
 * JavaFX graphics to AWT so that they can be fed to AWT-based toolkits.
 */
public final class ConverterUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private ConverterUtilities() {}

    // Use JFXConverter to transcode a collection of JavaFX Shapes to AWT.
    public static java.awt.Shape getVectorGraphicsAwt( final List< Shape > shapes ) {
        final java.awt.geom.GeneralPath shapesAwt =
                                                  new java.awt.geom.GeneralPath( java.awt.geom.Path2D.WIND_EVEN_ODD );
        for ( final Shape shape : shapes ) {
            // Use JFXConverter to transcode the JavaFX graphics to AWT.
            final java.awt.Shape shapeAwt = JFXShapeUtilities.getShape( shape );
            shapesAwt.append( shapeAwt, false );
        }

        return shapesAwt;
    }

}
