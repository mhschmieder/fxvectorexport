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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.Map;

/**
 * {@code FxConverterDemoApplication} is an example of a JavaFX-based
 * application that can export to vector graphics.
 * <p>
 * All that is needed, is a menu system and file chooser, plus a custom dialog,
 * to query the title and File Name. Almost any existing JavaFX application will
 * already have this support.
 * <p>
 * A sample stage is supplied as an example of the minimal code needed when not
 * starting from scratch.
 * <p>
 * This code is provided only as a example for adding this functionality to your
 * own application, and is not meant to serve as a mini-application for
 * converting GUI visuals to vector graphics; nor has it been tested fully. It
 * is the maximal subset of non-proprietary code in the author's commercial
 * application that could be safely extracted for public consumption.
 * <p>
 * Once I have written my boilerplate library for JavaFX GUI's, I will re-do
 * this demo app to take advantage of that library and improve the layout and
 * other aspects as this was a hastily written bare bones demo just to prove the
 * basic concepts of the converter libraries.
 *
 * @version 1.0
 *
 * @author Mark Schmieder
 */
public final class FxConverterDemoApplication extends Application {

    /**
     * It is advised to provide a main() method that at least explicitly calls
     * launch() so that standalone contexts are supported. The launch() method
     * calls the init() method followed by the start() method.
     *
     * @param args
     *            Variable command-line argument list when launched standalone.
     */
    public static void main( final String[] args ) {
        // Forward the command line arguments to the internal launcher.
        launch( args );
    }

    /**
     * The preferred CSS Stylesheet needs to be cached for class-level access.
     */
    private String cssStylesheet;

    //////////////////// Application method overrides ////////////////////////

    /**
     * Initializes the controller class. This method is automatically called
     * after the FXML file (when present) has been loaded.
     */
    @Override
    public void init() {
        // Get the encapsulated application parameters to forward to the session
        // context initialization. It includes JNLP parameters and command-line
        // JVM arguments. The preferred CSS Style Sheet can be passed here too.
        final Parameters applicationParameters = getParameters();
        final Map< String, String > namedArguments = applicationParameters.getNamed();

        // Initialize anything not related to creating scenes or stages.
        initVariables( namedArguments );
    }

    @Override
    public void start( final Stage primaryStage ) throws Exception {
        // Make sure to set the preferred CSS style sheet explicitly, in case a
        // future JavaFX update changes the default and it has a negative impact
        // on application look and feel until GUI coding tweaks are performed.
        setUserAgentStylesheet( cssStylesheet );

        try {
            // Instantiate the primary application window.
            final FxConverterDemoStage converterDemoStage = new FxConverterDemoStage();

            // Now show it, until the session ends.
            Platform.runLater( () -> converterDemoStage.show() );
        }
        catch ( final Throwable throwable ) {
            throwable.printStackTrace();
            System.err.println( "Exiting due to error while starting FxConverterDemoApplication." ); //$NON-NLS-1$
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        // This is a fail-safe in case exiting the JavaFX Application Thread
        // failed to exit the Java Runtime Environment for this Application.
        System.exit( 0 );
    }

    /////////////////////// Initialization methods ///////////////////////////

    /**
     * Initialize variables, excepting any scene or stage building, which must
     * be deferred until inside the start() method or any methods it invokes.
     * <p>
     * The initialization order is designed to minimize the chance that a
     * startup error could result before exception handling is possible.
     *
     * @param namedArguments
     *            The named arguments passed to the application launcher,
     *            including JNLP parameters and command-line JVM arguments
     */
    private void initVariables( final Map< String, String > namedArguments ) {
        // Make sure to set the Modena CSS style sheet explicitly, in case a
        // future JavaFX update changes the default and it has a negative impact
        // on application look and feel until GUI coding tweaks are performed.
        //
        // This also makes this a placeholder for setting Aqua CSS for macOS
        // and/or Metro CSS for Windows, if we choose to later on.
        cssStylesheet = Application.STYLESHEET_MODENA;
    }

}
