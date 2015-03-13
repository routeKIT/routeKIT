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

You can find rendered versions of the documents in the `PDFs` branch.

Attribution
-----------

*routeKIT* uses the following libraries:

* the JDK;
* [METIS](http://glaros.dtc.umn.edu/gkhome/views/metis) to partition a graph when a map is imported – it is assumed that you have a `gpmetis` or a `kmetis` in your PATH;
* [jbzip2](https://code.google.com/p/jbzip2/) ([Github export](https://github.com/routeKIT/jbzip2)) to decompress the map that is downloaded when no map is found (on first program start) – included in this repository (see LICENSE-jbzip2).

The map data comes from the [OpenStreetMap project](www.openstreetmap.org/);
specifically, the initial map that is downloaded on first startup when no existing installation is found is downloaded from [geofabrik.de](http://www.geofabrik.de/).

Commit hashes
-------------

We did a big filter-branch before the open-source release, and therefore some commit hashes that we referenced in commit messages or documents no longer exist.
Here’s a mapping from old to new hashes:

Old hash|New hash
--------|--------
03d263d|2f87f88
0d5e5b2|6a92c4d
1260e09|853a1f3
20f10e4|c9220f3
241109e|b2e5bd4
3035972f6643f6d5b53002d1823e99f5de07d9fb|096342b
3c2b61df374375029a4fe853dce074f05488f9b6|00af429
46b37c9|f6e2dc5
4757eb9|d0b8040
4f9104c|efc6a02
51ed8b1|6ee8726
58eba738bd73e19038805d9a06f970c72cb9240f|4b08529
64f0263|08a6a58
672a169|2c88ab7
680d84bff40fce53b812|d675565
680d84bff40fce53b8126ed23e148572ca364474|d675565
6d239a2ab7402c7a469b48299c42e8d56f547884|5d77ac2
78c5119|6a764fa
811dec1|3dabfc1
85e690f|4cc21db
90d79b9|2b4e911
95a0b11|19b27ed
9ab9064|f12ff82
9fba4a8193d32bd3f267d40314cb4a28063fbb4e|dc6caf9
a1d3577|350a5f4
a747dd6|58d079d
c7dca7d|937948b
d6b1d0b7eecb8a5eba5c22c2e4b7cad71c41b4a4|acf2eb9
f152f6d|06a5c7c
ffb5ec4|5cbb2b6

(The hashes were found using [this script](https://gist.github.com/lucaswerkmeister/10495164).)
