PLY - Polygon File Format
=========================

Java library to read files in the PLY format.
---------------------------------------------

jPLY is a library to read PLY files in a simple way. It has *complete support
for all features* offered by the PLY format. jPLY is using the
*Apache 2 License* so feel free to use it for open or closed source projects.

Get your bearings:
---------------------------------------------

* [Manual](http://jply.smurn.org/jply/manual.html) A quickstart manual on how jPLY works.

* [JavaDoc](http://jply.smurn.org/jply/apidocs/index.html) jPLY is fully documented, use it!

* [PLY Format](http://jply.smurn.org/jply/plyformat.html) A short description what (and in which structure)
might be stored in PLY files. Read this if you want to know if PLY is the
right format for your needs. It is not a specification of the file format but
a more high-level description.

* [Console Demo](http://jply.smurn.org/consoledemo/index.html) a very small demo application
reading a PLY file and dumping some of the information in it to the console.

* [LWJGL Demo](http://jply.smurn.org/lwjgldemo/index.html) a OpenGL demo showing how to
use jPLY to render a PLY model.

* Something unclear? Just [open a ticket](https://github.com/smurn/jPly/issues).

* And of course feel free to contribute! The source is on [GitHub](https://github.com/smurn/jPly).

What is PLY, and when to use it?
---------------------------------------------

  PLY is a file format designed to store polygon meshes. Other than formats
such as [COLLADA])http://www.collada.org) that can describe complete scenes,
PLY only stores meshes.

  This simplicity makes jPLY attractive where the complexity of COLLADA or VRML
would be troubling. For example when developing demo application or when
testing 3D applications in the early developing stage one needs a simple
and reliable way to load geometry.

  The PLY format is great in such use-cases since it is simple enough that
it can be written by hand if one needs to test a particular geometry. But there
is also a huge collection of PLY files available in the Internet, ranging
from an eight vertex cube to multi-billion triangle scans of Michelangelo
statues. The famous Stanford bunny and the Utah teapot are of course also
available.
