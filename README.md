# Introduction 

The goal of this project, named Rigel in honor of one of the brightest stars, is to write a program to visualize the night sky.

The screenshot below shows what the program should look like when finished.
We see a representation of the sky such that a person on the [EPFL]("https://www.epfl.ch/en/") site on February 17, 2020 at 8:17 p.m. and looking south would see it, assuming the weather is mild.

![A piece of the sky visible from EPFL in the evening of February 17, 2020](resources/rigel.png)

The red line at the bottom of the screen represents the horizon, and the letter S placed below indicates the south. The cursor of the mouse is placed on the star [Rigel]("https://en.wikipedia.org/wiki/Rigel"), of the constellation [Orion]("https://en.wikipedia.org/wiki/Rigel"), which is at this moment at the azimuth 186 ° - almost due south - and 35 ° above the horizon, as indicated at the bottom right.
The blue lines link the stars together forming a number of [asterisms]("https://en.wikipedia.org/wiki/Asterism_(astronomy)"), such as that evoking a vertical bow tie to which Rigel belongs.

# Catalogs

### Stars

For this project, we will use a simplified version of the [HYG catalog]("https://github.com/astronexus/HYG-Database"), which results from the compilation of three famous catalogs: the Hipparcos catalog, the Yale catalog and the Gliese catalog.

### Asterisms

We will use a simplified version of a catalog drawn up by Dominic Ford from the book [The Stars: A New Way To See Them by H. A. Rey]("https://github.com/dcf21/constellation-stick-figures").

The simplified version that we will use is a very simple text file, but which is not in CSV format. Each line of the file, including the first, consists of a list of Hipparcos star numbers belonging to the same asterism, separated by a comma.

# Reference book

The project is based on the fourth edition of the book _Practical Astronomy with your Computer or Spreadsheet by Peter Duffett-Smith and Jonathan Zwart_,
from which all the formulas for astronomical calculation are extracted.

# Project archive

If you want to come up with this project on your own, you can follow the whole stages available on the [archive page]("https://cs108.epfl.ch/archive/20/archive.html") of the website of the [EPFL]("https://epfl.ch") course.