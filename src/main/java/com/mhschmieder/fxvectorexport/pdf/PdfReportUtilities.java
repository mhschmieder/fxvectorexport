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
package com.mhschmieder.fxvectorexport.pdf;

import com.mhschmieder.jgraphics.GraphicsUtilities;
import com.mhschmieder.jgraphics.color.ColorMode;
import com.orsonpdf.PDFDocument;
import com.orsonpdf.PDFGraphics2D;
import com.orsonpdf.PDFHints;
import com.orsonpdf.Page;
import javafx.geometry.Bounds;
import javafx.print.Paper;
import javafx.scene.Node;
import org.jfxconverter.JFXConverter;

import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * {@code pdfreportUtilities} is a utility class for methods that export a
 * JavaFX Scene Graph {@link Node} to a PDF Document via transcoding to AWT
 * using {@link JFXConverter} as a wrapper for {@link PDFGraphics2D}.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class PdfReportUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private PdfReportUtilities() {}

    /**
     * Creates the PDF Document corresponding to the {@link Node}, and writes it
     * to the provided {@link File}, using UTF-16 encoding due to the need to
     * handle locale sensitive characters for the PDF Title and content.
     * <p>
     * This minimally specified entry point defaults to North American Letter
     * as the target page paper size and orientation, for clients that don't
     * want to bother specifying it or querying it either from the user or the
     * current Page Layout. It also presumes that RGB output is wanted, and
     * vectorized text (especially so that rotated text is rotated in PDF).
     *
     * @param file
     *            The {@link File} destination for writing the PDF content
     * @param node
     *            The {@link Node} to convert to AWT and then export to PDF
     * @param title
     *            The {@link String} to use as the PDF Document's title
     * @param author
     *            The {@link String} to use as the PDF Document's author
     * @return The status of whether SVG Document creation succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file,
                                          final Node node,
                                          final String title,
                                          final String author ) {
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
                author,
                pageWidth,
                pageHeight,
                ColorMode.RGB,
                true );
    }

    /**
     * Creates the PDF Document corresponding to the {@link Node}, and writes it
     * to the provided {@link File}, using UTF-16 encoding due to the need to
     * handle locale sensitive characters for the PDF Title and content.
     * <p>
     * Note that the Color Mode isn't used yet, until JFreePDF supports it.
     *
     * @param file
     *            The {@link File} destination for writing the PDF content
     * @param node
     *            The {@link Node} to convert to AWT and then export to PDF
     * @param title
     *            The {@link String} to use as the PDF Document's title
     * @param author
     *            The {@link String} to use as the PDF Document's author
     * @param pageWidth
     *            The target page width, usually in points (1/72 inch)
     * @param pageHeight
     *            The target page height, usually in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with PDF specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether this PDF export succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file,
                                          final Node node,
                                          final String title,
                                          final String author,
                                          final double pageWidth,
                                          final double pageHeight,
                                          final ColorMode colorMode,
                                          final boolean useVectorizedText ) {
        boolean fileSaved = false;

        // Using a safe try-with-resources clause, chain a BufferedOutputStream
        // to a FileOutputStream using the former output wrapper's default
        // UTF-16 encoding (which also matches PDF's default), for better
        // performance and to guarantee platform-independence of newlines and
        // overall system-neutrality and locale-sensitivity of text data. As
        // JFreePDF returns the entire file contents as a byte array, we must
        // use {@link BufferedOutputStream} instead of {@link FileWriter}.
        try ( final FileOutputStream fileOutputStream = new FileOutputStream( file );
                final BufferedOutputStream bufferedOutputStream =
                                                                new BufferedOutputStream( fileOutputStream ) ) {
            // Write the PDF contents indirectly via JFXConverter.
            fileSaved = createDocument( bufferedOutputStream,
                                        node,
                                        title,
                                        author,
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
     * Creates the PDF Document corresponding to the {@link Node}, and writes it
     * to the provided {@link OutputStream} using JFreePDF.
     * <p>
     * Note that the Color Mode isn't used yet, until JFreePDF supports it.
     *
     * @param outputStream
     *            The wrapped {@link OutputStream} for channeling the PDF
     *            content
     * @param node
     *            The {@link Node} to convert to AWT and then export to PDF
     * @param title
     *            The {@link String} to use as the PDF Document's title
     * @param author
     *            The {@link String} to use as the PDF Document's author
     * @param pageWidth
     *            The target page width, usually in points (1/72 inch)
     * @param pageHeight
     *            The target page height, usually in points (1/72 inch)
     * @param colorMode
     *            The {@link ColorMode} to use, compatible with PDF specs
     * @param useVectorizedText
     *            Set to {@code true} if Vectorized Text Mode is desired;
     *            {@code false} otherwise (that is, if text is to be rendered as
     *            strings, sometimes referred to as Basic Text Mode)
     * @return The status of whether this PDF export succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final OutputStream outputStream,
                                          final Node node,
                                          final String title,
                                          final String author,
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

        // Create a new PDF Document.
        final PDFDocument document = new PDFDocument();

        // Write the file title for the PDF Document (null permitted).
        document.setTitle( title );

        // Write the file author to the PDF Document (null permitted).
        document.setAuthor( author );

        // Create a new PDF Page, and add it to the PDF Document.
        final Page page = document
                .createPage( new Rectangle2D.Double( 0.0d, 0.0d, pageWidth, pageHeight ) );

        // Get the Graphics Context wrapper for drawing the PDF content.
        final PDFGraphics2D pdfGraphics = page.getGraphics2D();

        // Vectorize the text, to avoid missing fonts and to allow more
        // flexibility in how to work with text in downstream applications.
        final Object textRenderingHint = useVectorizedText
            ? PDFHints.VALUE_DRAW_STRING_TYPE_VECTOR
            : PDFHints.VALUE_DRAW_STRING_TYPE_STANDARD;
        pdfGraphics.setRenderingHint( PDFHints.KEY_DRAW_STRING_TYPE, textRenderingHint );

        // Calculate and apply a global transform for all of the AWT transcoding
        // from source coordinates to PDF-oriented page coordinates.
        //
        // PDF starts at the bottom left corner, like EPS and PostScript.
        GraphicsUtilities.applySourceToDestinationTransform( pdfGraphics,
                                                             minX,
                                                             minY,
                                                             maxX,
                                                             maxY,
                                                             pageWidth,
                                                             pageHeight );

        try {
            // Write the PDF contents to memory indirectly via JFXConverter.
            final JFXConverter converter = new JFXConverter();
            converter.convert( pdfGraphics, node );

            // Get the full PDF Document as an encoded byte array.
            final byte[] pdfBytes = document.getPDFBytes();

            // Save the PDF Document from memory to disc.
            outputStream.write( pdfBytes );

            // If we got this far without exceptions, the file was saved.
            fileSaved = true;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileSaved;
    }

}// class pdfreportUtilities
