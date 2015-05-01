# Simusic

HOW TO RUN:

To run the project 
from the command line, 
go to the dist folder and
type the following:

java -jar "Simusic.jar" 

MAINTENANCE:
Refer to the maintenance.pdf
file in the project folder.

PROJECT INFORMATION:

This is Simusic: an agent-based 
algorithmic composition/ accompaniment 
system developed as part of fulfilling a
BSc Computing Science Honours degree at
University of Aberdeen (1 May 2015).

To distribute this project, 
zip up the dist folder 
(including the lib folder)
and distribute the ZIP file.

IMPORTANT NOTES:
-   The folder ./lib contains ONLY open-source
    packages whose license allows use and copy
    for non-commercial purposes.
-   The folder ./resources contains images from
    the website http://tech-kid.com/ - the authors
	claim that "we do not own the copyright to 
	any of the images on this website they are 
	provided as-is".
	./resources also contains the simusic.policy 
	file used by Java RMI to grant the code
	full RMI access permissions - this is safe
	because in this project all classes are local
	and trusted.
-   The folder ./runtime contains a folder
    /sample_agents used by the automatic agent
	creator tool which extracts all subdirectories
	of a selected folder as new agents. The sub-
	directories should contain the MIDI file 
	repertoire of each agent.
    The agent subdirectories contain MIDI 
	files which are freely available to
	download from many online sources.

Project proposal:
http://minovski.net/algomusic/Honours-Project-Proposal.pdf

Project plan:
http://minovski.net/algomusic/Honours-Project-Plan.pdf

Research in Data Mining (Music Genre Classification):
http://minovski.net/algomusic/CS4040-Music-Genre-Classification.pdf
