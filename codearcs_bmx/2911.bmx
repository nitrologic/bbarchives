; ID: 2911
; Author: BlitzSupport
; Date: 2012-01-19 15:38:21
; Title: AutoVersion
; Description: Automatic minor-version updater

SuperStrict

' TO USE:

' Create a blank text file called "autoversion.txt" in your
' program's development folder.

' IF PROGRAM FAILS TO BUILD, YOU PROBABLY DIDN'T DO THIS! ^^^

' Call GetVersion () to retrieve version string and update it for next Debug run.

' IN DEBUG MODE ONLY, this will update "autoversion.txt" to
' the NEXT minor version, which will be used next time you run.

' VERSION NUMBER WILL BE ONE LESS THAN "autoversion.txt" FOR RELEASE BUILDS;
' this is expected. Defaults to 0.1 for first Debug run, even if already run
' in Release mode (which will show as 0.1).

' To bump major versions, edit "autoversion.txt" manually. This just
' deals with incremental updates.

' *** You DON'T need to include "autoversion.txt" with your release, as
' it will be included in the binary! ***

' The release version won't create/update an "autoversion.txt" file!

' If it all goes pear-shaped, type a version manually into
' "autoversion.txt"...

' Versions are in the format x.y (y will increase to upper Int range); you'll
' just have to adapt the principle for any other format!

' No serious error-checking, so don't put junk into "autoversion.txt". Valid
' example contents (no more than one line):

' 0.1
' 1.1
' 1.144
' 10.091
' 100.1
' [BLANK FILE]

' -----------------------------------------------------------------------------
' COPY AND PASTE FROM HERE...
' -----------------------------------------------------------------------------

Incbin "autoversion.txt" ' Probably needs to be at top of your code...

Function GetVersion:String ()

	Global AutoVersionSaved:Int = False

	Local version:String = LoadText ("incbin::autoversion.txt")
	Local major:String, minor:String
	
	Local dot:Int = Instr (version, ".")
	
	If dot
		
		major = Left (version, dot - 1)
		minor = Mid (version, dot + 1)
		
		' Hack for Debug Build -> Release Build (Release would
		' use next minor version despite no changes)...
		
		?Not Debug
			minor = Int (minor) - 1
		?
		
	Else
		' Blank/invalid...
		major = "0"
		minor = "1"
	EndIf

	Local autoversion:String = major + "." + minor
	
	?Debug
	
		If Not AutoVersionSaved

			minor = Int (minor) + 1
			
			version = major + "." + minor
			SaveText version, "autoversion.txt"
		
			AutoVersionSaved = True
	
		EndIf
	
	?

	Return autoversion

End Function

' -----------------------------------------------------------------------------
' ... TO HERE.
' -----------------------------------------------------------------------------

Notify "Amazing Apps presents iAyeCapn [Version: " + GetVersion () + "]"

' Uncomment below to confirm that GetVersion only updates version once per run!

'Notify "Amazing Apps presents iAyeCapn [Version: " + GetVersion () + "]"
'Notify "Amazing Apps presents iAyeCapn [Version: " + GetVersion () + "]"
