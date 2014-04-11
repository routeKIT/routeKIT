routeKIT
--------

*routeKIT* is a routing application with special focus on finding the best route possible with respect to your specific vehicle’s data (size, speed) and respecting traffic regulations.
It was developed by Kevin Birke, Felix Dörre, Fabian Hafner, Lucas Werkmeister, Dominic Ziegler and Anastasia Zinkina, students of the Karlsruhe Institute of Technology, over the course of the winter semester 2013/2014, as a study project for the “practice in software development” course (Praxis der Softwareentwicklung, PSE).

In developing the software, we followed the [waterfall model](https://en.wikipedia.org/wiki/Waterfall_model). You can find the documents of the different phases in these folders:

* `pflichtenheft/` (functional specification)
* `entwurf/` (design)
* `implementierung/` (implementation report)
* `qualitätssicherung/` (verification and testing report)

Please understand that we won’t bother to translate them into English :)

External libraries
------------------

*routeKIT* uses the following libraries:

* the JDK;
* [METIS](http://glaros.dtc.umn.edu/gkhome/views/metis) to partition a graph when a map is imported – it is assumed that you have a `gpmetis` or a `kmetis` in your PATH;
* [jbzip2](https://code.google.com/p/jbzip2/) to decompress the map that is downloaded when no map is found (on first program start) – included in this repository (see LICENSE-jbzip2).
