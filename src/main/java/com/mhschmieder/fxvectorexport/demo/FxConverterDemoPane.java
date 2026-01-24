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

import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * {@code FxConverterDemoPane} is an example of a JavaFX {@link BorderPane}
 * whose contents can be exported to vector graphics.
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
public final class FxConverterDemoPane extends BorderPane {

    //////////////////////////// Constructors ////////////////////////////////

    /**
     * Default constructor.
     *
     * @since 1.0
     */
    public FxConverterDemoPane() {
        // Always call the superclass constructor first!
        super();

        // Avoid constructor failure by wrapping the layout initialization in an
        // exception handler that logs the exception and then returns an object.
        try {
            initPane();
        }
        catch ( final Exception e ) {
            e.printStackTrace();
        }
    }

    /////////////////////// Initialization methods ///////////////////////////

    /**
     * This method follows a naming convention that is popular in Java for
     * encapsulating most of the initialization work of the main constructor,
     * allowing for a simply try-catch-throw bracketing of this call and thus
     * also simplifying any derived class constructor behavior.
     *
     * @since 1.0
     */
    @SuppressWarnings("nls")
    private void initPane() {
        // Make three labels to display in an asymmetric grid, as an example of
        // typical application panel layouts that will need to export to vector
        // graphics via compositing (that is, separate renderings per panel).
        final Label goodbyeLabel = new Label( "Goodbye" );
        goodbyeLabel.setFont( Font.font( "SansSerif", FontWeight.BOLD, 48d ) );
        final Label cruelLabel = new Label( "Cruel" );
        cruelLabel.setFont( Font.font( "SansSerif", FontPosture.ITALIC, 36d ) );
        final Label worldLabel = new Label( "World" );
        worldLabel.setFont( Font.font( "SansSerif", FontPosture.ITALIC, 36d ) );

        // Make three check boxes to display in an asymmetric grid, as an
        // example of typical application panel layouts that will need to export
        // to vector graphics via compositing (that is, several passes in order
        // to accomplish separate renderings per sub-panel or layout region).
        final CheckBox maybeCheckBox = new CheckBox( "Maybe" );
        final CheckBox yesCheckBox = new CheckBox( "Yes" );
        final CheckBox noCheckBox = new CheckBox( "No" );

        // Line up the "Goodbye" label and the "Maybe" check box top-to-bottom
        // in the top pane using a VBox Layout.
        final VBox topPane = new VBox( 10.0d );
        topPane.getChildren().addAll( goodbyeLabel, maybeCheckBox );

        // Line up the "Cruel" label and the "Yes" check box top-to-bottom in
        // the bottom left pane using a VBox Layout.
        final VBox bottomLeftPane = new VBox( 10.0d );
        bottomLeftPane.getChildren().addAll( cruelLabel, yesCheckBox );

        // Line up the "World" label and the "No" check box top-to-bottom in the
        // bottom right pane using a VBox Layout.
        final VBox bottomRightPane = new VBox( 10.0d );
        bottomRightPane.getChildren().addAll( worldLabel, noCheckBox );

        // Line up the bottom left and bottom right panel side-by-side in the
        // bottom panel container using an HBox Layout.
        final HBox bottomPane = new HBox( 10.0d );
        bottomPane.getChildren().addAll( bottomLeftPane, bottomRightPane );

        // Stack the top and bottom panel vertically, so that we thoroughly
        // exemplify a typical application's asymmetric layout.
        setTop( topPane );
        setBottom( bottomPane );

        // Give some insets so things aren't jammed up against the borders.
        setPadding( new Insets( 16d ) );
    }

}
