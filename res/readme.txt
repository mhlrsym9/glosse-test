The source code and functional/design documentation for Gloss-E can be found in SVN at:
https://svn.transparent.com/repos/TLShared/Java/Projects/Glossy/trunk

To run the Gloss-E tool you have to make sure that you have JAVA_HOME environment variable
is set and also %JAVA_HOME%/bin is included in PATH environment variable. The easiest way
to determine if you have Java properly installed is to do the following:
* in Windows do, Start->Run and enter cmd.exe into the prompt
* in the resulting window enter in java -version
* if Java is properly installed you should see something similar to this:
  java version "1.6.0_21"
* you must have version 1.6 or better to run Gloss-E
* if you need to install or update Java, visit http://java.com/ and follow the Get Java link

There are two required and six optional command line parameters:

inputFolderRoot	Required full path to the folder where single language content is located.
outputFolder	Required full path to the folder that will hold the tool output.
configFile		The configuration file name including full path. This configuration file will 
				override the original glottal-config.xml.
prefix			Prefix of the name of the glossary file. By default the glossary is used.
suffixL1		Suffix of the L1 glossary file. By default the L1 will be used.
suffixL2		Suffix of the L2 glossary file. By default the L2 will be used.
swType			The type of stop word processing. The default is ALL, which means that for 
				words and phrases stop words will be removed from the sort keys before doing 
				sort. The other available are: 
				WORDS (processes stop word only for vocabulary words); 
				PHRASES (processes stop word only for phrases); 
				NONE (do not process stop words).
marsoc          enables special processing to handle B4X folders that have part of speech information encoded in the name.

Example:

gloss-e.bat -inputFolderRoot x:\content\italian -outputFolder x:\content\italian


There is one more optional parameter called "version", if it is specified then Gloss-E tool
prints help on command line parameters and exits.

IMPORTANT: if you have spaces in your path names, you must surround the path with double-quotes " eg.
gloss-e.bat -marsoc -inputFolderRoot "C:\Documents and Settings\admin\svn\Java\Projects\Glossy\trunk\testdata\MARSOC" -outputFolder "C:\Documents and Settings\admin\svn\Java\Projects\Glossy\trunk\kit\marsoc"

Jay made me do this
