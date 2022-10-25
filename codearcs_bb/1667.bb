; ID: 1667
; Author: Grey Alien
; Date: 2006-04-14 08:28:41
; Title: Basic Game Ini File
; Description: Basic Game Ini File

;Ini Variables			
	Global IniMusic = 1
	Global IniSound = 1
	Global IniMusicVolume# = 100 ;in percent
	Global IniSoundVolume# = 100 ;in percent
	Global IniFade = 1
	Global IniFlipFalse = 0
	Global Ini16BitGraphics = 0
	Global IniWindowedMode = 1
	Global IniLockKeys = 0
	Global IniGrid = 1 ;always on
	Global IniAlphaBlend = 0 ;always off
	Global IniParticles# = 80
	Global IniInstructionsAtStart = 1	
	Global IniShowPortalSplash = 0
	
; -----------------------------------------------------------------------------
; Read Ini File
; -----------------------------------------------------------------------------
Function IniFileRead(filename$)
	;load the ini file settings into global variables
	ThePath$ = filename + ".ini"
	Local ini = ccLoadFile(ThePath)
	Local l$
	Local flag$
	Local Value$
	
	While Not Eof(ini)
		l$ = ReadLine(ini)			
		flag$ = Upper(ccIniFirstString(l$))
		Value$ = ccIniLastString(l$)
		num% = Int(Value)
	
		Select True
			Case flag$ = "MUSIC"
				IniMusic = num				
			Case flag$ = "SOUND"
				IniSound = num				
			Case flag$ = "MUSIC VOLUME"
				IniMusicVolume = num				
			Case flag$ = "SOUND VOLUME"
				IniSoundVolume = num				
			Case flag$ = "FADE"
				IniFade = num
			Case flag$ = "FLIP FALSE"
				IniFlipFalse = num
			Case flag$ = "16BIT GRAPHICS"
				Ini16BitGraphics = num
			Case flag$ = "WINDOWED MODE"
				IniWindowedMode = num
			Case flag$ = "PARTICLES"
				IniParticles = num	
			Case flag$ = "INSTRUCTIONS AT START"
				IniInstructionsAtStart = num	
			Case flag$ = "SHOW PORTAL SPLASH"
				IniShowPortalSplash = num				
			Default
		End Select		
	Wend
	CloseFile(ini)
End Function

; -----------------------------------------------------------------------------
; Write Ini File
; -----------------------------------------------------------------------------
Function IniFileWrite(filename$)
	;write the ini file settings from global variables
	ThePath$ = filename + ".ini"
	Local ini = WriteFile(ThePath)
	Local l$
	Local flag$
	Local Value$
	
	ccWriteIniNumber(ini, "MUSIC", IniMusic)
	ccWriteIniNumber(ini, "SOUND", IniSound)
	ccWriteIniNumber(ini, "MUSIC VOLUME", IniMusicVolume)
	ccWriteIniNumber(ini, "SOUND VOLUME", IniSoundVolume)
	ccWriteIniNumber(ini, "FADE", IniFade)
	ccWriteIniNumber(ini, "FLIP FALSE", IniFlipFalse)
	ccWriteIniNumber(ini, "16BIT GRAPHICS", Ini16BitGraphics)
	ccWriteIniNumber(ini, "WINDOWED MODE", IniWindowedMode)
	ccWriteIniNumber(ini, "PARTICLES", IniParticles)
	ccWriteIniNumber(ini, "INSTRUCTIONS AT START", IniInstructionsAtStart)
	ccWriteIniNumber(ini, "SHOW PORTAL SPLASH", IniShowPortalSplash)
	CloseFile(ini)
End Function


; -----------------------------------------------------------------------------
; Load a file and show error if not found
; -----------------------------------------------------------------------------
Function ccLoadFile% (ThePath$)
	pointer = ReadFile(ThePath$)
	If Not pointer Then
    	RuntimeError ("Error loading file "+ThePath$)
		End
	Else
    	Return Pointer	
  	EndIf
End Function

; -----------------------------------------------------------------------------
; IniFirst String (return first part of string up to = sign)
; -----------------------------------------------------------------------------
Function ccIniFirstString$(s$)
	;pass in a string, this will only return the first part up to, but not including, the = sign (or end)		
	Return ccFirstStringToSub(s$, "=")
End Function

; -----------------------------------------------------------------------------
; IniLast String (return last part of string from = sign)
; -----------------------------------------------------------------------------
Function ccIniLastString$(s$)
	;pass in a string, this will only return the last part from, but not including, the = sign
	Return ccLastStringToSub(s$, "=")
End Function

; -----------------------------------------------------------------------------
; ccWriteIniNumber
; -----------------------------------------------------------------------------
Function ccWriteIniNumber(ini%, flag$, value%)
	;use this to write flag=number to an ini file
	WriteLine(ini, Upper(flag)+"="+Str(value)) 
End Function

; -----------------------------------------------------------------------------
; ccWriteIniString
; -----------------------------------------------------------------------------
Function ccWriteIniString(ini%, flag$, value$)
	;use this to write flag=string to an ini file
	WriteLine(ini, Upper(flag)+"="+value$) 
End Function

; -----------------------------------------------------------------------------
; First String To Sub (return first part of string up to Substring)
; -----------------------------------------------------------------------------
Function ccFirstStringToSub$(s$, sub$)
	;pass in a string, this will only return the first part up to, but not including, the substring (or end)
	pos% = Instr(s$, sub$)
	;If pos = 0 then then end of the was reached, so return the whole thing.
	If pos = 0 Then
		Return s$
	Else
		Return Mid(s$, 1, pos-1)
	EndIf
End Function

; -----------------------------------------------------------------------------
; Last String To Sub (return last part of string from substring)
; -----------------------------------------------------------------------------
Function ccLastStringToSub$(s$, sub$)
	;pass in a string, this will only return the last part from, but not including, the substring
	pos% = Instr(s$, sub$)
	;If pos = 0 then then end of the was reached, so return nothing
	If pos = 0 Then
		Return ""
	Else
		Return Mid(s$, pos + Len(sub$), Len(s$)-pos)
	EndIf
End Function
