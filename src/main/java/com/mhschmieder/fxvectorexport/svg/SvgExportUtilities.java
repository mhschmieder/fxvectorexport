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
package com.mhschmieder.fxvectorexport.svg;

import com.mhschmieder.jgraphics.GraphicsUtilities;
import com.mhschmieder.jgraphics.color.ColorMode;
import javafx.geometry.Bounds;
import javafx.print.Paper;
import javafx.scene.Node;
import org.apache.commons.math3.util.FastMath;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGHints;
import org.jfxconverter.JFXConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * {@code SvgExportUtilities} is a utility class for methods that export a
 * JavaFX Scene Graph {@link Node} to an SVG Document via transcoding to AWT
 * using {@link JFXConverter} as a wrapper for {@link SVGGraphics2D}.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class SvgExportUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private SvgExportUtilities() {}

    /**
     * Creates the SVG Document corresponding to the {@link Node}, and writes it
     * to the provided {@link File}, using UTF-8 encoding due to the need to
     * handle locale sensitive characters for the SVG Title and content.
     * <p>
     * This minimally specified entry point defaults to North American Letter
     * as the target page paper size and orientation, for clients that don't
     * want to bother specifying it or querying it either from the user or the
     * current Page Layout. It also presumes that RGB output is wanted, and
     * vectorized text (especially so that rotated text is rotated in SVG).
     *
     * @param file
     *            The {@link File} destination for writing the SVG content
     * @param node
     *            The {@link Node} to convert to AWT and then export to SVG
     * @param title
     *            The {@link String} to use as the SVG Document's title
     * @return The status of whether SVG Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file, 
                                          final Node node, 
                                          final String title ) {
        // Use North American Letter as the target page paper size and page
        // orientation. There are no limits on allowed values; units are points
        // (1/72 inch) but it is common to specify Letter Size and then convert.
        final Paper paperTarget = Paper.NA_LETTER;
        final double pageWidth = paperTarget.getWidth();
        final double pageHeight = paperTarget.getHeight();

        return createDocument(
                file,
                node,
                title,
                pageWidth,
                pageHeight,
                ColorMode.RGB,
                true );
    }

    /**
     * Creates the SVG Document corresponding to the {@link Node}, and writes it
     * to the provided {@link File}, using UTF-8 encoding due to the need to
     * handle locale sensitive characters for the SVG Title and content.
     * <p>
     * Although JFreeSVG supports various units, we use points here just to be
     * consistent with the less flexible EpsToolkit and OrsonPDF exporters.
     * <p>
     * Note that the Color Mode isn't used yet, until JFreeSVG supports it.
     *
     * @param file
     *            The {@link File} destination for writing the SVG content
     * @param node
     *            The {@link Node} to convert to AWT and then export to SVG
     * @param title
     *            The {@link String} to use as the SVG Document's title
     * @param pageWidth
     *            The target page width, usually in points (1/72 inch)
     * @param pageHeight
     *            The target page height, usually in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with SVG specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether SVG Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file,
                                          final Node node,
                                          final String title,
                                          final double pageWidth,
                                          final double pageHeight,
                                          final ColorMode colorMode,
                                          final boolean useVectorizedText ) {
        boolean fileSaved = false;

        // Using a safe try-with-resources clause, chain a BufferedWriter to an
        // OutputStreamWriter to a FileOutputStream using UTF-8, for better
        // performance and to guarantee platform-independence of newlines and
        // overall system-neutrality and locale-sensitivity of text data. As SVG
        // supports both UTF-8 and UTF-16 (which reference the same Unicode
        // specs), and as UTF-8 is more efficient and byte-for-byte identical
        // when dealing with documents that only contain characters that can be
        // represented by non-Unicode 7-bit or 8-bit formats (such as US-ASCII),
        // if is preferable to use a {@link OutputStreamWriter} set to UTF-8
        // encoding instead of {@link FileWriter} that can only write to UTF-16.
        try ( final FileOutputStream fileOutputStream = new FileOutputStream( file );
                final OutputStreamWriter outputStreamWriter
                        = new OutputStreamWriter(
                                fileOutputStream, "UTF-8" );
                final BufferedWriter bufferedWriter = new BufferedWriter(
                        outputStreamWriter ) ) {
            // Write the SVG contents indirectly via JFXConverter.
            fileSaved = createDocument( bufferedWriter,
                                        node,
                                        title,
                                        pageWidth,
                                        pageHeight,
                                        colorMode,
                                        useVectorizedText );
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileSaved;
    }

    /**
     * Creates the SVG Document corresponding to the {@link Node}, and writes it
     * to the provided {@link Writer} (usually a Character Stream based
     * {@link OutputStreamWriter} class) using JFreeSVG.
     * <p>
     * Although JFreeSVG supports various units, we use points here just to be
     * consistent with the less flexible EpsToolkit and OrsonPDF exporters.
     * <p>
     * Note that the Color Mode isn't used yet, until JFreeSVG supports it.
     *
     * @param writer
     *            The wrapped {@link Writer} for channeling the SVG content
     * @param node
     *            The {@link Node} to convert to AWT and then export to SVG
     * @param title
     *            The {@link String} to use as the SVG Document's title
     * @param pageWidth
     *            The target page width, usually in points (1/72 inch)
     * @param pageHeight
     *            The target page height, usually in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with SVG specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether SVG Document creation succeeded or not
     *
     * @since 1.0
     */
    @SuppressWarnings("nls")
    public static boolean createDocument( final Writer writer,
                                          final Node node,
                                          final String title,
                                          final double pageWidth,
                                          final double pageHeight,
                                          final ColorMode colorMode,
                                          final boolean useVectorizedText ) {
        boolean fileSaved = false;

        // We need bounds in parent vs. bounds in local, so that all transforms
        // are applied and we don't get anomalies such as vertically clipped or
        // offset output (if for instance the Node corresponds to a Layout Pane
        // doesn't include the Tool Bar or the Menu Bar and thus has a non-zero
        // minX and/or minY, causing an unwanted gap at top of document).
        final Bounds bounds = node.getBoundsInParent();
        final double minX = bounds.getMinX();
        final double minY = bounds.getMinY();
        final double maxX = bounds.getMaxX();
        final double maxY = bounds.getMaxY();

        // Get the Graphics Context wrapper for drawing the SVG content.
        //
        // As JFreeSVG only supports integer precision bounds, we round up for
        // the canvas size, so that nothing gets clipped during higher-precision
        // floating-point transforms of the graphics transcoding.
        final SVGGraphics2D svgGraphics = new SVGGraphics2D( ( int ) FastMath.ceil( pageWidth ),
                                                             ( int ) FastMath.ceil( pageHeight ) );

        // Vectorize the text, to avoid missing fonts and to allow more
        // flexibility in how to work with text in downstream applications.
        final Object textRenderingHint = useVectorizedText
            ? SVGHints.VALUE_DRAW_STRING_TYPE_VECTOR
            : SVGHints.VALUE_DRAW_STRING_TYPE_STANDARD;
        svgGraphics.setRenderingHint( SVGHints.KEY_DRAW_STRING_TYPE, textRenderingHint );

        // Make a default Title if none was provided, or if empty.
        final String svgTitle =
                              ( ( title == null ) || title.isEmpty() ) ? "The SVG Document" : title;
        svgGraphics.setRenderingHint( SVGHints.KEY_ELEMENT_TITLE, svgTitle );

        // Calculate and apply a global transform for all of the AWT transcoding
        // from source coordinates to SVG-oriented page coordinates.
        //
        // SVG starts at the top left corner, unlike EPS, PDF and PostScript.
        // This makes it more like Java2D, and more like screen addressing.
        GraphicsUtilities.applySourceToDestinationTransform( svgGraphics,
                                                             minX,
                                                             minY,
                                                             maxX,
                                                             maxY,
                                                             pageWidth,
                                                             pageHeight );

        try {
            // Write the SVG contents to memory indirectly via JFXConverter.
            final JFXConverter converter = new JFXConverter();
            converter.convert( svgGraphics, node );

            // Get the full SVG Document (header, content, dictionary, etc.).
            final String svgDocument = svgGraphics.getSVGDocument();

            // Save the SVG Document from memory to disc.
            writer.write( svgDocument );

            // If we got this far without exceptions, the file was saved.
            fileSaved = true;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileSaved;
    }
}