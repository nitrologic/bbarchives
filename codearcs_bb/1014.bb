; ID: 1014
; Author: Kozmi
; Date: 2004-05-04 13:02:30
; Title: Smart ScreenShot Taker
; Description: Remembers the last screenshot taken with a data file!

Global Int1%	;For writing to our '.egl' datafile

;hit spacebar to save a screenshot
If KeyHit(57) = True Then Screenshot()


Function Screenshot()

	ChangeDir "E:\Blitz3D\Blitz3D Demos\fpstest\egyptian level\ScreenShots"
	
			screenin = ReadFile("screenshot.egl") ; you can change the extension to whatever you want!
			If screenin = 0 Then currentshot = 0:Goto phase2
			currentshot = ReadInt( screenin )
				  CloseFile( screenin )
	
.phase2		iFileNumber% = currentshot
	    Repeat
	        iFileNumber = iFileNumber + 1
	        sFileName$ = "Egyptian_Level" + String$("0", 3 - Len(Str$(iFileNumber))) + iFileNumber + ".bmp"
	    Until Not(FileType(sFileName))

	Int1 = iFileNumber	
		
			screenout = WriteFile("screenshot.egl") ; Same as above!
			WriteInt( screenout, Int1 )
			  	  CloseFile( screenout )
    
    SaveBuffer FrontBuffer(), sFileName

	ChangeDir "E:\Blitz3D\Blitz3D Demos\fpstest\egyptian level" ; Change back to previous directory

End Function
