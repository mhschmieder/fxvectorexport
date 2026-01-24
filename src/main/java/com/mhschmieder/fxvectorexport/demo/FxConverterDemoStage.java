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
package com.mhschmieder.fxvectorexport.demo;

import com.mhschmieder.fxvectorexport.eps.EpsExportUtilities;
import com.mhschmieder.fxvectorexport.pdf.PdfReportUtilities;
import com.mhschmieder.fxvectorexport.svg.SvgExportUtilities;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * {@code FxConverterDemoStage} is an example of a JavaFX {@link Stage} whose
 * contents can be exported to vector graphics.
 * <p>
 * All that is needed, is a menu system and file chooser, plus a custom dialog,
 * to query the title and File Name. Almost any existing primary stage written
 * in JavaFX will already have this support.
 * <p>
 * This code is provided only as a example for adding this functionality to your
 * own application, and is not meant to serve as a mini-application for printing
 * to vector graphics; nor has it been tested fully. It is the maximal subset of
 * non-proprietary code in the author's commercial application that could be
 * safely extracted for public consumption.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class FxConverterDemoStage extends Stage {

    /**
     * This {@link Pane} mostly just contains the primary layout element.
     */
    private FxConverterDemoPane converterDemoPane;

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * This is the default constructor, and is all that is needed at this time.
     */
    public FxConverterDemoStage() {
        // Always call the superclass constructor first!
        super();

        try {
            initStage();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    /////////////////////// Initialization methods ///////////////////////////

    /**
     * This method follows a naming convention that is popular in Swing for
     * encapsulating most of the initialization work of the main constructor,
     * allowing for a simply try-catch-throw bracketing of this call and thus
     * also simplifying any derived class constructor behavior.
     *
     * @since 1.0
     */
    private void initStage() {
        // Add a quick-and-dirty toolbar just to test the export feature.
        final Button epsExportButton = new Button( "Export to EPS" ); //$NON-NLS-1$
        final Button pdfreportButton = new Button( "Export to PDF" ); //$NON-NLS-1$
        final Button svgExportButton = new Button( "Export to SVG" ); //$NON-NLS-1$
        final ToolBar exportToolBar = new ToolBar();
        final Region spacer1 = new Region();
        final Region spacer2 = new Region();
        HBox.setHgrow( spacer1, Priority.ALWAYS );
        HBox.setHgrow( spacer2, Priority.ALWAYS );
        exportToolBar.getItems()
                .addAll( epsExportButton, spacer1, pdfreportButton, spacer2, svgExportButton );

        // Make the primary layout element, and add it to the content pane.
        converterDemoPane = new FxConverterDemoPane();

        // Use a Border Pane as the root for the Scene, as this makes it easier
        // to lay out traditional Windows with a Menu Bar, Tool Bars, Status
        // Bar, Action Button Bar, and main Content Node (centered).
        final BorderPane root = new BorderPane();

        // Make the Scene as early as possible, as it may involve CSS loading.
        final Scene scene = new Scene( root, 420d, 280d, Color.WHITE );

        // Set a generic frame title, and allow for resizing due to sloppy code.
        setTitle( "FxConverter Demo Application" ); //$NON-NLS-1$
        setResizable( true );

        // We may need to set the tool bar height as well (it may be too tall).
        root.setTop( exportToolBar );
        root.setCenter( converterDemoPane );

        // The main window construction is complete; now we can set the scene.
        setScene( scene );

        // Detect button clicks on the Export EPS Button.
        epsExportButton.setOnAction( value -> {
            // This is a very rudimentary and unsafe example of file chooser
            // usage, until I have time to put together a JavaFX framework
            // library that enforces best practices.
            final FileChooser fileChooser = new FileChooser();
            final File file = fileChooser.showSaveDialog( this );
            if ( file != null ) {
                // Transcode this window to AWT to export to EPS.
                EpsExportUtilities.createDocument( file,
                                                   converterDemoPane,
                                                   "Fake EPS Title", //$NON-NLS-1$
                                                   "Saved from FxConverterDemoApplication" ); //$NON-NLS-1$
            }
        } );

        // Detect button clicks on the Export PDF Button.
        pdfreportButton.setOnAction( value -> {
            // This is a very rudimentary and unsafe example of file chooser
            // usage, until I have time to put together a JavaFX framework
            // library that enforces best practices.
            final FileChooser fileChooser = new FileChooser();
            final File file = fileChooser.showSaveDialog( this );
            if ( file != null ) {
                // Transcode this window to AWT to export to PDF.
                PdfReportUtilities.createDocument( file,
                                                   converterDemoPane,
                                                   "Fake PDF Title", //$NON-NLS-1$
                                                   "Saved from FxConverterDemoApplication" ); //$NON-NLS-1$
            }
        } );

        // Detect button clicks on the Export SVG Button.
        svgExportButton.setOnAction( value -> {
            // This is a very rudimentary and unsafe example of file chooser
            // usage, until I have time to put together a JavaFX framework
            // library that enforces best practices.
            final FileChooser fileChooser = new FileChooser();
            final File file = fileChooser.showSaveDialog( this );
            if ( file != null ) {
                // Transcode this window to AWT to export to SVG.
                SvgExportUtilities.createDocument( file, converterDemoPane, "Fake SVG Title" ); //$NON-NLS-1$
            }
        } );
    }

}
