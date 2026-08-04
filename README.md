# FxVectorExport

The FxVectorExport library is a toolkit for converting JavaFX based graphics and
GUI elements to various vector graphics output formats such as EPS, SVG and PDF,
via intermediary transcoding to AWT using the excellent JFXConverter library by
Hervé Girod, which allows one to connect legacy AWT-based Java libraries to
JavaFX application elements.

ALERT: Please note that there seem to have been some recent changes to the
artifact labeling of several of the legacy Java 8 versions of JFreeOrg's
libraries, but I won't have time to update my POM's and/or API calls in this
library for at least a few days (as of early October 20201, when I discovered
the new discrepancy).

Although the JFXConverter library has its own drivers and extensions for the
supported file formats, I needed to switch to better libraries for each vector
graphics target and to have this code all available in a coordinated release
with my other libraries. It is possible that this library may go away later on
if Hervé likewise switches his source libraries for EPS and SVG (the latter
currently uses Batik, which is very old and hard to maintain integration
compatibility) and adds PDF support (this is my most important output format
overall, and is meant to eventually supplant the need for EPS).

Eclipse and NetBeans related support files are included as they are generic and
are agnostic to the OS or to the user's system details and file system
structure, so it seems helpful to post them in order to accelerate the
integration of this library into a user's normal IDE project workflow and build
cycle.

The Javadocs are 100% compliant and complete, but I am still learning how to
publish those at the hosting site that I think is part of Maven Central, as it
is a bad idea to bloat a GitHub project with such files and to complicate
repository changes (just as with binary files and archices). Hopefully later
tonight!

This projects depends on my GraphicsToolkit and EpsToolkit libraries, as well as
depending on Object Refinery's JFreeSVG and JFreePDF libraries, along with the
JFXConverter library, and is marked as such in the Maven POM file.

Please note that for now my forks must be used for both external dependencies,
as I had to modify the POM in order to specify Java 1.8 vs. Java 11.

This is a placeholder note: this library does not build yet, due to JFXConverter
not being found. It is not yet a GitHub project or available as an artifact at
Maven Central, so I think I will migrate it to Maven myself and bump its rev to
0.24 to avoid confusion. It has a run-time dependency though as well, and that
one isn't open source, so it's another learningt curve for me, to see how to
package pre-built resources that aren't yet using the GitHub + Maven paradigm.

UPDATE: Although I just now found a recent GitHub posting of this library, with
Maven build support, I don't have mnuch time available to work on complex
integrations and I ran into issues so I will see if we can combine forces later
but for now I borrowed the approach I used to resolve Qoppa's jPDFWriter
dependencies, and I think I got it down to three M2 Cache directories that you
need to create and populate weith direct downloads from the SourceForge site for
JFXConverter, using the latest 0.24 build.

https://sourceforge.net/projects/jfxconverter/files/Version%200.24/

As contributed to JFXConverter around the v0.17 through v0.19 release cycle, I
have enough familiarity with how it breaks down that I was able to make
decisions that only took a couple of hours to fully resolve into a successful
build, the final step of which was to disable Javadoc errors in JFXConverter
from blocking builds of my toolkit.

You'll need to make fake M2 caches for three JAR's that are found after
unzipping the binary JAR in the list linked above. One for
org/jfxconverter/jfxconverter/0.24, one for com/mdiutil/mdiutilities-core/0.24,
and one for com/mdiutil/mdiutilities-ui/0.24. Then copy the respective JAR's
one-by one into their matched cache directories, and rename them to match proper
naming conventions for Maven references in the POM file of my prokect:
jfxconverter-0.24.jar, mdiutilities-core-0.24.jar, and mdiutilities-ui-0.24.jar.

I do not yet know if run-time dependencies will be missing, as I don't have time
right now to resol,ve unrelated build issues with an app I wrote that uses this
library. I think the main JFXConverter JAR is a superjar though. If it isn't, it
needs Apache Batik ands a few others, some of them likely already present from
other library dependencies as Apache Commons (for instance) is widely used.
Nevertheless, I had to be careful about versions on stuff like Apache POI due to
API compatibility issues.

The other thing I have not done yet, is ensure that my modified, enhanvced, and
improved drivers (which also cover additional formats) get used vs. the older
ones built into the JFXConverter itself. Although I think those are still
packaged as separate JAR's so that people who don't need PowerPoint support
don't have to pull in PPT stuff.

