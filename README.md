mp3image
========

java utility program to add images from the web to mp3 files based on file names or input parameters

Addimage - a program to add artwork to mp3 files.

	For example, given an mp3 file, "Nirvana - All Apologies.mp3",
	the default action is then to query the internet for nirvana+all+apologies
	and update the mp3 file's artwork from an image found there.

Parameters:

[-f] <list of mp3 files>
	If other options are specified, use -f to identify the mp3 files.

Optional parms:

-i <number of images to add to each mp3 file>

-t <terms to add to search>
	For example, if your mp3 files are named by song title,
	use this parm to specify artist or to otherwise control the search.

-ut
	Use terms only, that is, do not search by mp3 file titles.
	If this option is specified, -t is required.
 
-a <list of artwork files or urls for artwork>
	If these do not match the number of input files,
	they will be reused as needed.

-v
	Verbose mode.  Display messages about processing details.

-n
	Don't resize images.  The default is to scale to 640x480 preserving aspect ratio.


