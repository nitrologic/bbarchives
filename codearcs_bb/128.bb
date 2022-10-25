; ID: 128
; Author: Milky Joe
; Date: 2001-11-06 09:52:16
; Title: Save Screenshot
; Description: Saves a snapshot of the current display

Function SaveScreenshot()

; Version: 1.0
; Author: Leigh Bowers
; Homepage: http://www.curvenet.co.uk

    iFileNumber% = 0
    Repeat
        iFileNumber = iFileNumber + 1
        sFileName$ = "Screenshot" + String$("0", 3 - Len(Str$(iFileNumber))) + iFileNumber + ".bmp"
    Until Not(FileType(sFileName))
    
    SaveBuffer FrontBuffer(), sFileName

End Function
