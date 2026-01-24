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
package com.mhschmieder.fxvectorexport.ppt;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextBox;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.converters.DefaultConverterListener;
import org.jfxconverter.drivers.ppt.PPTGraphics2D;
import org.jfxconverter.drivers.ppt.PPTJFXGraphics2D;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * {@code PptExportUtilities} is a utility class for methods that export a
 * JavaFX Scene Graph {@link Node} to a PPT Document via transcoding to AWT
 * using {@link JFXConverter} as a wrapper for {@link PPTJFXGraphics2D}.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class PptExportUtilities {

    /**
     * The default constructor is disabled, as this is a static utilities class.
     */
    private PptExportUtilities() {}

    /**
     * Creates the PPT Document corresponding to the {@link Node}, and writes it
     * to the provided {@link File}, using default UTF-16 encoding due to the
     * need to handle locale sensitive characters for the PPT Title and content.
     *
     * @param file
     *            The {@link File} destination for writing the PPT content
     * @param node
     *            The {@link Node} to convert to AWT and then to PPT
     * @param title
     *            The {@link String} to use as the PPT Document's title
     * @param useExtendedConversion
     *            true for an extended conversion
     * @return The status of whether this PPT export succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final File file,
                                          final Node node,
                                          final String title,
                                          final boolean useExtendedConversion ) {
        if ( ( file == null ) || ( node == null ) ) {
            return false;
        }

        boolean fileSaved = false;

        // Using a safe try-with-resources clause, chain a BufferedOutputStream
        // to a FileOutputStream using the former output wrapper's default
        // UTF-16 encoding (which also matches PPT's default), for better
        // performance and to guarantee platform-independence of newlines and
        // overall system-neutrality and locale-sensitivity of text data. As
        // Apache POI only supports Output Streams vs. Writers, we must use a
        // FileOutputStream here instead of a FileWriter.
        try ( final FileOutputStream fileOutputStream = new FileOutputStream( file );
                final BufferedOutputStream bufferedOutputStream =
                                                                new BufferedOutputStream( fileOutputStream ) ) {
            // Write the PPT contents indirectly via JFXConverter.
            //
            // As OutputStream implements Closeable, try-with-resources
            // auto-closes and auto-flushes the document, so there is no need to
            // do so here explicitly; though it does no harm.
            fileSaved = createDocument( bufferedOutputStream, node, title, useExtendedConversion );
        }
        catch ( final IOException ioe ) {
            ioe.printStackTrace();
        }

        return fileSaved;
    }

    /**
     * Creates the PPT Document corresponding to the {@link Node}, and writes it
     * to the provided {@link OutputStream}, using Apache POI.
     *
     * @param outputStream
     *            The wrapped {@link OutputStream} for channeling the PDF
     *            content
     * @param node
     *            The {@link Node} to convert to AWT and then to PPT
     * @param title
     *            The {@link String} to use as the PPT Document's title
     * @param useExtendedConversion
     *            true for an extended conversion
     * @return The status of whether this PPT export succeeded or not
     *
     * @since 1.0
     */
    public static boolean createDocument( final OutputStream outputStream,
                                          final Node node,
                                          final String title,
                                          final boolean useExtendedConversion ) {
        boolean fileSaved = false;

        // We need bounds in parent vs. bounds in local, so that all transforms
        // are applied and we don't get anomalies such as vertically clipped or
        // offset output (if for instance the Node corresponds to a Layout Pane
        // doesn't include the Tool Bar or the Menu Bar and thus has a non-zero
        // minX and/or minY, causing an unwanted gap at top of document).
        final Bounds bounds = node.getBoundsInParent();
        final float slideWidth = ( float ) bounds.getWidth();
        final float slideHeight = ( float ) bounds.getHeight();

        try ( final HSLFSlideShow pptSlides = new HSLFSlideShow() ) {
            // Create a blank slide to hold the current Node once transformed.
            final HSLFSlide slide = pptSlides.createSlide();

            // Create the PPT Graphics wrapper using the appropriate form.
            //
            // A local copy is built with this code due to some link time issues
            // that cause run-time class-not-found exceptions on account of some
            // incompatible competing versions of some dependency JAR's.
            final PPTGraphics2D pptGraphics = useExtendedConversion
                ? new PPTJFXGraphics2D( slide, slideWidth, slideHeight, Color.WHITE, Color.BLACK )
                : new PPTGraphics2D( slide, slideWidth, slideHeight, Color.WHITE, Color.BLACK );

            // Write the PPT contents to memory indirectly via JFXConverter.
            final JFXConverter converter = new JFXConverter();
            if ( useExtendedConversion ) {
                ( ( PPTJFXGraphics2D ) pptGraphics ).supportGroups( true );
                converter.setListener( new DefaultConverterListener() );
            }
            converter.convert( pptGraphics, node );

            // Add the PPT Title before writing the contents.
            final HSLFTextBox titleBox = slide.addTitle();
            titleBox.setText( title );

            // Write the PPT Document's main contents from memory to disc.
            pptSlides.write( outputStream );

            fileSaved = true;
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }

        return fileSaved;
    }
}
