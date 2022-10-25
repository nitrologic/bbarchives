; ID: 3224
; Author: Dan
; Date: 2015-09-17 13:39:46
; Title: Play sound on midi (using MidiLib.dll)
; Description: Midi sound function addon

;====================================================================
; Project:     Play sound on midi channels using Midilib.dll
; Version:     1.0
; Author:      Dan
; Email:       -.-
; Copyright:   PD
; Description: Play sound without external media (needs midilib.dll)
;              from http://www.blitzforum.de/forum/viewtopic.php?t=33817
;              A set of 3 functions written for use with the MidiLib.dll
;              MInstrument(n,ch²) sets an music instrument (0-127) for one channel
;              MPlay(note,oktave²,ch²,durr²) Plays a note * oktave²
;                  on a channel² (0-15) with durration² (0-127)
;              Mstop(note,oktave²,ch²,durr²) same as Mplay, 
;                  needs to be called with the previous MPlay variables !
;                  (only for some instruments)
;                  ² = optional parameters 
;====================================================================
; Demo Numpad +- change the instrument nr
;      Numpad enter sets the instrument

Graphics 800,600,32,2
Graphics 800,600,32,3

Global hOut = MidiOutOpen( 0 ); Microsoft GS Wavetable SW Synth is most probably ID zero (needed at the start)
Print "instrument code"+Hex$(MInstrument(10)) ; Sets an instrument to play (needed !)

;Demo starts here
Delay 1000
Mtime=MilliSecs()
Ktime=MilliSecs()
z=0
Repeat

	Color Rand($0,$ff),Rand($0,$ff),Rand($0,$ff)
	Line Rand(1,GraphicsWidth()),Rand(1,GraphicsHeight()),Rand(1,GraphicsWidth()),Rand(1,GraphicsHeight())
	Color $0,$0,$0
	Rect Rand(1,GraphicsWidth()),Rand(1,GraphicsHeight()),Rand(1,30),Rand(1,30)
	Color $0,0,0
	Rect 0,0,620,16
	Color $ff,$ff,$ff
	Text 0,0,MilliSecs()+"  Note="+x+" InstrNr:"+z+".. "+Right$(Hex$(j),6)
	
	If KeyDown(78) And MilliSecs()-Ktime>100 ;(numpad +)
		z=z+1 : If z>$80 Then z=$80
		Ktime=MilliSecs()
	EndIf
	
	If KeyDown(74) And MilliSecs()-Ktime>100 ;(numpad -)
		z=z-1 : If z<=0 Then z=0
		Ktime=MilliSecs()
	EndIf
	
	If KeyDown(156) And MilliSecs()-Ktime>150 ; Enter (numpad)
	
	MInstrument(z)
	Ktime=MilliSecs()
	EndIf
	
	If MilliSecs()-Mtime>Mdelay    ; Automatic play !
	    
	    J=MStop(x,3,0,0)  ;Stops the previous note, needed for some long playing instruments
		Read x
		If x=-1 
			Restore ende
		Else
			Mdelay=MPlay(x) ; plays the next note and sets the delay untill next one is played
		EndIf
		Mtime=MilliSecs()
	EndIf
	Flip
Until KeyDown(1)

.ende
Data 1,2,3,5,5,0,6,5,3,1,0,2,3,3,2,1,0,2,1,2,3,5,5,6,5,3,1,2,3,3,2,2,1,4,4,4,6,6,6,5,5,4,2,1,0,0
;Data 1,2,1,3,1,3,3,4,3,2,4,2,1,2,3,1,2,1,4,3,5,3,1,2,3,1,2,1,4,3,2,1,2,4,5,6,5,6,4,6,3,2,1,2  
Data -1

;Demo ends here
MidiOutClose(hOut)  ; Closes the midi handle ! 
End



Function MInstrument(nr,ch=0)
;Sets an instrument NR for one Channel
;instrument nr from 0 to 127 (128)
	If ch<0
		ch=-ch Mod 16
	Else
		ch=ch Mod 16
	EndIf
	
	If nr<0 
		nr=-nr Mod $80
	Else
		nr=nr Mod $80
	EndIf
	
	Message=(($C0+ch)*$100+(nr))
	MidiOutShortMsg(hOut,Message)
	Return Message ; for debugging purpose 
End Function

Function MPlay(note,oktave=3,ch=0,durr=$60)
	;Plays a note (1-12)*oktave on channel with durration
    ;oktave range 0-9
	;Durration range 128 (0-127)
	oktave=oktave Mod 10
	ch=ch Mod $10  ; 16 channels maximum ! 0-15 - standard ch 0
	If durr<$10 Then durr=$10
	durr=durr Mod $80
    note=note Mod 13
	;Cnote=note Mod 12
    Select note
		Case 1			;C
			n=0 
		Case 2			;D
			n=2
		Case 3			;E
			n=4
		Case 4			;F
			n=5
		Case 5			;G
			n=7
		Case 6			;A
			n=9
		Case 7			;H
		    n=11
		Case 8			;C#
		    n=1
		Case 9			;D#
		    n=3
		Case 10			;F#
		    n=6
		Case 11			;G#
		    n=8
		Case 12			;A#
		    n=10
	End Select
	;Print "Note= "+note+" / "+n+" ... "+Right$(Hex$(((n+(oktave*12)))*$100),4)
	If note>0
		Message=(($90+ch)*$10000)+((n+(oktave*12)-1)*$100)+durr
	;Print Hex$(Message)
		MidiOutShortMsg(hOut,Message)
    Else
	 Message=0
	EndIf 
	Return durr*6     ;returns the durration*6 (as miliseconds to wait, for example)
End Function 

Function MStop(note,oktave=3,ch=0,durr=0)
    ;oktave 0-9
	oktave=oktave Mod 10
	ch=ch Mod $10  ; 16 channels maximum ! 0-15 - standard ch 0
	durr=durr Mod $80
    note=note Mod 13
	;Cnote=note Mod 12
    Select note
		Case 1			;C
			n=0 
		Case 2			;D
			n=2
		Case 3			;E
			n=4
		Case 4			;F
			n=5
		Case 5			;G
			n=7
		Case 6			;A
			n=9
		Case 7			;H
		    n=11
		Case 8			;C#
		    n=1
		Case 9			;D#
		    n=3
		Case 10			;F#
		    n=6
		Case 11			;G#
		    n=8
		Case 12			;A#
		    n=10
	End Select
Message=(($80+ch)*$10000)+((n+(oktave*12)-1)*$100)+durr
MidiOutShortMsg(hOut,Message)
Return Message
End Function
