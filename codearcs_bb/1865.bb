; ID: 1865
; Author: b32
; Date: 2006-11-18 00:46:30
; Title: music tracker
; Description: tracker style music editor

; ID: 1865
; Author: b32
; Date: 2006-11-18 00:46:30
; Title: music tracker
; Description: tracker style music editor

;-----------------------------------------------------------------------------------------------------
;								   Userlib, extracted from winmm.decls by Ziltch
;-----------------------------------------------------------------------------------------------------

;	.lib "winmm.dll"
;	
;	midiOutGetNumDevs%()
;	midiOutClose%(hMidiOut%)
;	midiOutOpen%(lphMidiOut*,uDeviceID%,dwCallback%,dwInstance%,dwFlags%); nul1*,nul2*,dwFlags%)
;	midiOutShortMsg%(hMidiOut%,dwMsg%)
;	midiOutGetDevCaps%( uDeviceID%, lpCaps*, uSize%):"midiOutGetDevCapsA"
;	

;-----------------------------------------------------------------------------------------------------
;	usage: you need this in a .decls file in folder "c:\program files\blitz\userlibs" to run
;-----------------------------------------------------------------------------------------------------


	;MIDI code by Ziltch
	;tracker/saving routine by bram32bit
	
;-----------------------------------------------------------------------------------------------------
;													Globals
;-----------------------------------------------------------------------------------------------------

	Const	notes$ = "C-C#D-D#E-F-F#G-G#A-A#B-"
	Const 	keyb1$ = "ZSXDCVGBHNJM,L.;/'"
	Const	keyb2$ = "ZSXDCVGBHNJMQ2W3ER5T6Y7UI9O0P[=]\"
	Const	dumpfile$ = "dump002.dat" 
	
	Global	device
	Global	setvolume = 127

	Global	maxnotes = 63
	Global	maxchannels = 19

	ReadHeader(dumpfile$)
	
	Global	fmaxnotes = maxnotes
	Global	fmaxchannels = maxchannels
	Const	indexwidth = 35
	Const	notewidth = 100
	Const 	noteheight = 15
	Const	ofx = 180
	Const 	ofy = 50
	Global	vis_maxnotes = 31
	Global	vis_maxchannels = 3
	Global	vis_ofx = 0
	Global  vis_ofy = 0
	
	If vis_maxnotes > maxnotes Then vis_maxnotes = maxnotes
	If vis_maxchannels > maxchannels Then vis_maxchannels = maxchannels

	Global 	patternwidth = notewidth * 4 + indexwidth
	Global	patternheight = (maxnotes + 1) * noteheight
	
	Global	octave = 3
	Global	curX
	Global 	curY
	Global	recmode = 1
	Global	spd# = 125.0
	Global	btel
	Global 	playing
	Global	starttime
	Global	nowtime
	Global  oldtimenow

	Global MIDI_File
	Dim 	nxt(0)
	
	Dim		instrument(maxchannels)
	Dim		lastnote(maxchannels)
	Dim		instrumentname$(128)
	Dim		mute(maxchannels)

	Dim 	Pattern(maxchannels, maxnotes)
	Dim 	Vol(maxchannels, maxnotes)
	Dim		Sel(maxchannels, maxnotes)

	Dim 	BUF_Pattern(maxchannels, maxnotes)
	Dim 	BUF_Vol(maxchannels, maxnotes)
	Dim 	BUF_Sel(maxchannels, maxnotes)

	Dim		templen(maxnotes + 1)
	
	Dim		button$(10)
		
	ReadData()

;-----------------------------------------------------------------------------------------------------
;												  	Initialize
;-----------------------------------------------------------------------------------------------------

	Graphics 800, 600, 0, 2
	SetBuffer BackBuffer()
		
	font = LoadFont("FixedSys")
	SetFont font
	font2 = LoadFont("Arial", 12)
	
	;if midi device gives trouble, try passing -1 as a parameter
	numdevices = midiOutGetNumDevs()
	Print numdevices
	
	For i = 0 To numdevices	
		device = OpenMidiOut(i)
		If device <> 0 Then Print "Found device " + i + " .. ": tst = 1: Exit
		Print "Not found device " + i
	Next
	If tst <> 1 Then 
		Print "Hmm .. no midi devices were found."
		Print "Please press any key to exit"
		WaitKey()
		End
	End If
	
	Cls
	
	For i = 0 To maxchannels
		SelectInstrument(i, i * 2)
	Next
	
	ReadPattern(dumpfile$)

;-----------------------------------------------------------------------------------------------------
;													Main Loop
;-----------------------------------------------------------------------------------------------------
	
	Repeat
	
		Cls

		nowtime = MilliSecs()

		;enter=play
		If KeyHit(28) Then StartPlay()
	
		;handle playmode
		timenow = (Floor((nowtime - starttime) / spd)) Mod (maxnotes + 1)
		If playing Then
			;check if a new line is played
			If timenow <> oldtimenow Then
				;play all notes
				For chi = 0 To maxchannels
					If pattern(chi, timenow) <> 0 Then PlayNote(chi, pattern(chi, timenow) - 1, vol(chi, timenow))
				Next
			;store played line
			oldtimenow = timenow
			End If
			;draw red play cursor dot
			gnow = timenow - vis_ofy
			If gnow >= 0 Then If gnow <= vis_maxnotes Then
				Color 255, 0, 0
				Oval ofx - noteheight, ofy + noteheight * gnow + noteheight/2, noteheight/2, noteheight/2
			End If
		End If
			
		;read key from pc keyboard		
		keynote = ReadNoteFromKeyboard() 
		;if a key is pressed
		If keynote > 0 Then 
			keynote = keynote + octave * 12
			;preview note
			playnote( curX, keynote - 1, setvolume )
			;store note info
			If recmode Then
				If curX >= 0 Then If curX <= maxchannels
				If curY >= 0 Then If curY <= maxnotes
					pattern(curX, curY) = keynote
					vol(curX, curY) = setvolume
					CurY = CurY + 1
				End If
				End If
			End If
		End If
		
		;F3=cut
		If KeyHit(61) Then CutFrame()
		
		;F4=copy
		If KeyHit(62) Then CopyFrame2()
		
		;F5=paste
		If KeyHit(63) Then PasteFrame(curX, curY)
		
		;TAB		
		If KeyHit(15) Then 
			If recmode Then
				If curX >= 0 Then If curX <= maxchannels
				If curY >= 0 Then If curY <= maxnotes
					pattern(curX, curY) = -1
					vol(curX, curY) = 0
					PlayNote(curX, -1, 0)
					CurY = CurY + 1
				End If
				End If
			End If
		End If
		
		;DEL
		tst = False
		If KeyDown(211) Then
			tst = (nowtime - tms > 350)
		Else
			tms = nowtime
		End If				
		If KeyHit(211) Or tst Then 
			If recmode Then
				If curX >= 0 Then If curX <= maxchannels
				If curY >= 0 Then If curY <= maxnotes
					pattern(curX, curY) = 0
					vol(curX, curY) = 0
					PlayNote(curX, -1, 0)
					CurY = CurY + 1
				End If
				End If
			End If
		End If

		;cursor keys for moving cursor
		p = 0
		If KeyHit(200) Then TestSel: curY = curY - 1
		If KeyHit(208) Then TestSel: curY = curY + 1
		If KeyHit(203) Then TestSel: curX = curX - 1
		If KeyHit(205) Then TestSel: curX = curX + 1
		
		;pgup/pgdn
		If KeyHit(201) Then curY = curY - 16: TestSel(+16)
		If KeyHit(209) Then curY = curY + 16: TestSel(-16)
		If KeyHit(199) Then curY = 0
		If KeyHit(207) Then curY = maxnotes
		
		;limit cursor movement
		If curX < 0 Then curX = 0
		If curY < 0 Then curY = 0
		If curX > maxchannels Then curX = maxchannels
		If curY > maxnotes Then curY = maxnotes
	
		;scroll interface		
		If curX > vis_maxchannels + vis_ofx Then vis_ofx = curX - vis_maxchannels
		If curX < vis_ofx Then vis_ofx = curX
		If curY < vis_ofy Then vis_ofy = curY
		If curY > vis_maxnotes + vis_ofy Then vis_ofy = curY - vis_maxnotes
				
		;draw instrument names
		Color 100, 100, 0
		SetFont font2
		For chi = 0 To vis_maxchannels
			If chi + vis_ofx > 4 Then
				Text ofx + chi * notewidth + indexwidth, ofy - 15, InstrumentName$(instrument(chi + vis_ofx))
			Else
				Text ofx + chi * notewidth + indexwidth, ofy - 15, "Drum" + (chi + vis_ofx)
			End If
			Text ofx + chi * notewidth + indexwidth, ofy + noteheight * (vis_maxnotes + 1), chi + vis_ofx
			
		Next
		
		mx = MouseX()
		my = MouseY()
		mh = MouseHit(1)
				
		;volumebar
		volx = 620
		voly = ofy
		Color 100, 100, 0
		Text volx, voly - 15, "volume"
		Rect volx, voly, 50, 100, 0
		Color 100, 100, 0
		Rect volx, voly + 90 - setvolume * 90 / 127, 50, 10
		If MouseDown(1) Then
			If RectsOverlap(mx, my, 1, 1, volx, voly - 10, 50, 120) Then
				setvolume = ((voly + 90 - MouseY()) * 127 / 90)
				If setvolume < 0 Then setvolume = 0
				If setvolume > 127 Then setvolume = 127
				For i = 0 To maxchannels
				For j = 0 To maxnotes
					If sel(i, j) Then vol(i, j) = setvolume
				Next
				Next				
			End If
		End If
		SetFont font
		
		;buttons
		gui$ = ""
		For y = 0 To btel - 1
			overlap = RectsOverlap(mx, my, 1, 1, ofx - 80, ofy + y * noteheight * 10 / 4, 60, noteheight * 2)
			If mh Then
				If overlap Then
					gui$ = button$(y)
				End If
			End If
			Color 100, 100, 0
			Rect ofx - 80, ofy + y * noteheight * 10 / 4, 60, noteheight * 2, 0
			If overlap Then Color 255, 255, 0 Else Color 100, 100, 0
			Text ofx - 50, ofy + y * noteheight * 10 / 4 + noteheight, button$(y), True, True
		Next	
				
		Select gui$
			
			Case "New"
				
				For i = 0 To maxchannels
				For j = 0 To maxnotes
				pattern(i, j) = 0
				vol(i, j) = 0
				Next
				Next
				setvolume = 127
				
			Case "Load"
			
				SetBuffer FrontBuffer()
				Cls
				Color 255, 255, 255
				Locate 0, 0
				dir = ReadDir(CurrentDir$())
				Repeat
					f$ = Lower$(NextFile$(dir))
					If Right$(f$, 4) = ".pia" Then Print f$
					If f$ = "" Then Exit
				Forever
				f$ = Lower$(iInput$("please enter filename>"))
				If Right$(f$, 4) <> ".pia" Then f$ = f$ + ".pia"
				ReadPattern(CurrentDir$() + f$)
				SetBuffer BackBuffer()
				Cls
				
			Case "Save"
			
				SetBuffer FrontBuffer()
				Cls
				Color 255, 255, 255
				Locate 0, 0
				f$ = Lower$(iInput$("please enter filename>"))
				If Right$(f$, 4) <> ".pia" Then f$ = f$ + ".pia"
				ok = True
				If FileType(f$) = 1 Then 
					Print "file exists!"
					ok = Lower$(iInput$("overwrite? (y/n)")) = "y"
					If ok Then DeleteFile f$
				End If
				If ok Then 
					WritePattern(f$)
					Print "file saved!"
					Print "press any key"
					WaitKey()
				End If
				SetBuffer BackBuffer()
				Cls
				
			Case "Export"
			
				SetBuffer FrontBuffer()
				Cls
				Color 255, 255, 255
				Locate 0, 0
				f$ = Lower$(iInput$("please enter filename>"))
				If Right$(f$, 4) <> ".mid" Then f$ = f$ + ".mid"
				ok = True
				If FileType(f$) = 1 Then 
					Print "file exists!"
					ok = Lower$(iInput$("overwrite? (y/n)")) = "y"
					If ok Then DeleteFile f$
				End If
				If ok Then SaveMidi(f$)
				SetBuffer BackBuffer()
				Cls
				
			Case "Play"
				
				StartPlay()
				
			Case "Stop"
				
				playing = False
				button$(4) = "Play"
				
			Case "Speed"
				
				SetSpeed()
				
			Case "All"			

				For i = 0 To maxchannels
				For j = 0 To maxnotes
					sel(i, j) = 1
				Next
				Next
				
			Case "Track"

				For i = 0 To maxchannels
				For j = 0 To maxnotes
					sel(i, j) = (i = curX)
				Next
				Next
				
			Case "Length"
			
				ChangeLength()
				
			Case "Deselect"
				
				For i = 0 To maxchannels
				For j = 0 To maxnotes
					sel(i, j) = 0
				Next
				Next
				
			Case "Help"
			
				Help()
				
		End Select	
		
		;loop through visible lines			
		For lni = 0 To vis_maxnotes
			;draw index rectangle
			Color 64, 64, 0
			Rect ofx, ofy + lni * noteheight, indexwidth + 1, noteheight + 1, 0
			;draw line index
			col = 90 + ((lni + vis_ofy) Mod 4 = 0) * 45
			Color col, col, 0
			Text ofx + (indexwidth / 2), ofy + lni * noteheight, lni + vis_ofy, True
			;loop through visible channels
			For chi = 0 To vis_maxchannels
				
				;get note data			
				note$ = ConvertNote$(pattern(vis_ofx + chi, vis_ofy + lni) - 1, vol(vis_ofx + chi, vis_ofy + lni))
				;check if this is the selected note
				selected = ((lni + vis_ofy) = curY) And ((chi + vis_ofx) = curX)
				
				If selected Then 
					col = 255
					If note$ = "" Then note$ = "<-->"
				Else
					col = 90 + ((lni + vis_ofy) Mod 4 = 0) * 45
				End If
				;draw pattern grid
				If sel(vis_ofx + chi, vis_ofy + lni) Then
					Color 45, 45, 0
					Rect ofx + indexwidth + (chi * notewidth), ofy + (lni * noteheight), notewidth + 1, noteheight + 1, 1
				Else
					Color 64, 64, 0
					Rect ofx + indexwidth + (chi * notewidth), ofy + (lni * noteheight), notewidth + 1, noteheight + 1, 0
				End If
				Color col, col, 0
				Text ofx + chi * notewidth + indexwidth + (notewidth / 2), ofy + (lni * noteheight), note$, True
			Next
		Next
		
		;SPACE=change recmode
		If KeyHit(57) Then 
			For i = 0 To maxchannels
				PlayNote(i, -1, 0)
			Next
			recmode = Not(recmode)
		End If
				
		If KeyHit(59) Then Help()
		
		;F7/F8
		If KeyHit(65) Then If octave > 0 Then octave = octave - 1
		If KeyHit(66) Then If octave < 12 Then octave = octave + 1
		;F9/F10
		If KeyHit(67) Then SelectInstrument(curX, instrument(curX) - 1)
		If KeyHit(68) Then SelectInstrument(curX, instrument(curX) + 1)					
		
		If MouseDown(1) Or MouseDown(2) Then
			If RectsOverlap(mx, my, 1, 1, ofx + indexwidth, ofy, patternwidth, patternheight) Then
				xx = (mx - ofx - indexwidth) / notewidth + vis_ofx
				yy = (my - ofy) / noteheight + vis_ofy
				sel(xx, yy) = MouseDown(1)
			End If
		End If
		
		;show recmode flag		
		If recmode Then 
			Color 255, 0, 0
			Text GraphicsWidth() / 2, 10, "RECORD", True
		End If
		
		Color 64, 64, 64
		Text 0, 0, "Press F1 for help"
				
		Flip
		Delay 10
					
	Until KeyHit(1)

	WritePattern(dumpfile$)

;-----------------------------------------------------------------------------------------------------
;												    Finalize
;-----------------------------------------------------------------------------------------------------

	CloseMidiOut device
	
	End

;-----------------------------------------------------------------------------------------------------
;													CloseMidiOut()
;-----------------------------------------------------------------------------------------------------
;close MIDI device
Function CloseMidiOut(MidiOutHandle)

  midiOutClose(MidiOutHandle)

End Function

;-----------------------------------------------------------------------------------------------------
;													OpenMidiOut()
;-----------------------------------------------------------------------------------------------------
;open MIDI device
Function OpenMidiOut(OutDevID=0)

  OutHandleBank		= CreateBank(4)
  ok 				= midiOutOpen(OutHandleBank,OutDevID,0,0, 0)
  MidiOutHandle 	= PeekInt(OutHandleBank,0)
  FreeBank 			OutBankHandle
  Return 			MidiOutHandle

End Function

;-----------------------------------------------------------------------------------------------------
;													SendMidiOut()
;-----------------------------------------------------------------------------------------------------
;send MIDI message
Function SendMidiOut(MidiOutHandle, MidiOutChannel, MidiOutStatus, MidiOutdata1 = 0, MidiOutData2 = 0)

   Return midiOutShortMsg(MidiOutHandle,( (MidiOutdata2 Shl 16) Or (MidiOutdata1 Shl 8 ) Or (MidiOutStatus Shl 4) Or MidiOutChannel))

End Function

;-----------------------------------------------------------------------------------------------------
;													SelectInstrument()
;-----------------------------------------------------------------------------------------------------
;select MIDI instrument
Function SelectInstrument( channel, program )

	If program < 0 Then Return
	If program > 127 Then Return

	SendMidiOut(device, ConvertChannel(channel), $C, program, 4)
	instrument(channel) = program
	
End Function

;-----------------------------------------------------------------------------------------------------
;													ReadNoteFromKeyboard()
;-----------------------------------------------------------------------------------------------------
;convert pc keyboard keypress to MIDI note
Function ReadNoteFromKeyboard()

		w = GetKey()
		note = Instr(keyb1$, Upper$(Chr$(w)))
		If note = 0 Then note = Instr(keyb2$, Upper$(Chr$(w)))

		Return note
		
End Function

Function Two$( num )

	r$ = Hex$(num)
	Return Right$(r$, 2)
	
End Function
	

Function ConvertNote$(index, vol)

	If index = -2 Then Return "====="
	If index = -1 Then Return ""
	
	nti = index Mod 12	
	note$ = Mid$(notes$, nti * 2 + 1, 2) + (index / 12) + " " + Two$(vol)
	
	Return note$ 
	
End Function
;"C#6 127"

Function PlayNote( channel, playnote, vol )
	If channel < 0 Then Return
	If channel > maxchannels Then Return
	;stop previous note
	If lastnote(channel) > -1 Then SendMidiOut(device, ConvertChannel(channel), $8, lastnote(channel), 0)
	;play new note
	If playnote > -1 Then SendMidiOut(device, ConvertChannel(channel), $9, playnote, vol)
	;store this note
	lastnote(channel) = playnote
End Function


;-------------------------------------------------------------------------------------------------
;										instrument data
;-------------------------------------------------------------------------------------------------

Data "Acoustic Grand Piano"
Data "Bright Acoustic Piano"
Data "Electric Grand Piano"
Data "Honky-tonk Piano"
Data "Rhodes Piano"
Data "Chorused Piano"
Data "Harpsichord"
Data "Clavinet"
Data "Celesta"
Data "Glockenspiel"
Data "Music box"
Data "Vibraphone"
Data "Marimba"
Data "Xylophone"
Data "Tubular Bells"
Data "Dulcimer"
Data "Hammond Organ"
Data "Percussive Organ"
Data "Rock Organ"
Data "Church Organ"
Data "Reed Organ"
Data "Accordian"
Data "Harmonica"
Data "Tango Accordian"
Data "Acoustic Guitar (nylon)"
Data "Acoustic Guitar (steel)"
Data "Electric Guitar (jazz)"
Data "Electric Guitar (clean)"
Data "Electric Guitar (muted)"
Data "Overdriven Guitar"
Data "Distortion Guitar"
Data "Guitar Harmonics"
Data "Acoustic Bass"
Data "Electric Bass (finger)"
Data "Electric Bass (pick)"
Data "Fretless Bass"
Data "Slap Bass 1"
Data "Slap Bass 2"
Data "Synth Bass 1"
Data "Synth Bass 2"
Data "Violin"
Data "Viola"
Data "Cello"
Data "Contrabass"
Data "Tremolo Strings"
Data "Pizzicato Strings"
Data "Orchestral Harp"
Data "Timpani"
Data "String Ensemble 1"
Data "String Ensemble 2"
Data "Synth Strings 1"
Data "Synth Strings 2"
Data "Choir Aahs"
Data "Voice Oohs"
Data "Synth Voice"
Data "Orchestra Hit"
Data "Trumpet"
Data "Trombone"
Data "Tuba"
Data "Muted Trumpet"
Data "French Horn"
Data "Brass Section"
Data "Synth Brass 1"
Data "Synth Brass 2"
Data "Soprano Sax"
Data "Alto Sax"
Data "Tenor Sax"
Data "Baritone Sax"
Data "Oboe"
Data "English Horn"
Data "Bassoon"
Data "Clarinet"
Data "Piccolo"
Data "Flute"
Data "Recorder"
Data "Pan Flute"
Data "Bottle Blow"
Data "Shakuhachi"
Data "Whistle"
Data "Ocarina"
Data "Lead 1 (square)"
Data "Lead 2 (sawtooth)"
Data "Lead 3 (caliope lead)"
Data "Lead 4 (chiff lead)"
Data "Lead 5 (charang)"
Data "Lead 6 (voice)"
Data "Lead 7 (fifths)"
Data "Lead 8 (brass + lead)"
Data "Pad 1 (new age)"
Data "Pad 2 (warm)"
Data "Pad 3 (polysynth)"
Data "Pad 4 (choir)"
Data "Pad 5 (bowed)"
Data "Pad 6 (metallic)"
Data "Pad 7 (halo)"
Data "Pad 8 (sweep)"
Data "FX 1 (rain)"
Data "FX 2 (soundtrack)"
Data "FX 3 (crystal)"
Data "FX 4 (atmosphere)"
Data "FX 5 (brightness)"
Data "FX 6 (goblins)"
Data "FX 7 (echoes)"
Data "FX 8 (sci-fi)"
Data "Sitar"
Data "Banjo"
Data "Shamisen"
Data "Koto"
Data "Kalimba"
Data "Bagpipe"
Data "Fiddle"
Data "Shanai"
Data "Tinkle Bell"
Data "Agogo"
Data "Steel Drums"
Data "Woodblock"
Data "Taiko Drum"
Data "Melodic Tom"
Data "Synth Drum"
Data "Reverse Cymbal"
Data "Guitar Fret Noise"
Data "Breath Noise"
Data "Seashore"
Data "Bird Tweet"
Data "Telephone Ring"
Data "Helicopter"
Data "Applause"
Data "Gunshot"

Data "New"
Data "Load"
Data "Save"
Data "Export"
Data "Play"
Data "Speed"
;Data "Copy"
;Data "Paste"
;Data "Exit"
Data "All"
Data "Deselect"
Data "Track"
Data "Length"
Data "Help"
Data "STOP"
;-----------------------------------------------------------------------------------------------------
;													ReadData()
;-----------------------------------------------------------------------------------------------------
;read instrument names
Function ReadData()
	Restore
	For i = 0 To 127
		Read instrumentname$(i)
	Next
	btel = 0
	Repeat
		Read b$
		If b$ = "STOP" Then Exit
		button$(btel) = b$
		btel = btel + 1
	Forever
End Function

;-----------------------------------------------------------------------------------------------------
;												 ConvertChannel()
;-----------------------------------------------------------------------------------------------------
;this makes track 0-4 drumtracks, and shifts the rest
Function ConvertChannel(chn)
	
	If chn > 4 Then 
		chn = chn - 4
;		If chn > 8 Then chn = chn + 1
	Else
		chn = 9
	End If
	
	Return chn
	
End Function

;-----------------------------------------------------------------------------------------------------
;													WritePattern()
;-----------------------------------------------------------------------------------------------------
;dump the pattern to disk
Function WritePattern(f$)

	If f$ = "" Then Return

	ff = WriteFile(f$)
	
	If ff = 0 Then Return
	
	WriteInt ff, maxchannels
	WriteInt ff, maxnotes
	
	For j = 0 To maxchannels
		WriteInt ff, instrument(j)
	Next
	
	For i = 0 To maxnotes
	For j = 0 To maxchannels
		WriteInt ff, pattern(j, i)
		WriteInt ff, vol(j, i)
	Next
	Next
	
	WriteInt ff, curX
	WriteInt ff, curY
	
	CloseFile ff
	
End Function

;-----------------------------------------------------------------------------------------------------
;													ReadPattern()
;-----------------------------------------------------------------------------------------------------
;read dumped pattern from disk
Function ReadHeader(f$)
		
	If FileType(f$) <> 1 Then Return
	
	ff = ReadFile(f$)
	
	If ff = 0 Then Return
	
	maxchannels = ReadInt(ff)
	maxnotes = ReadInt(ff)
	
	CloseFile ff
	
End Function

;-----------------------------------------------------------------------------------------------------
;													ReadPattern()
;-----------------------------------------------------------------------------------------------------
;read dumped pattern from disk
Function ReadPattern(f$)
		
	If FileType(f$) <> 1 Then Return
	
	ff = ReadFile(f$)
	
	imaxchannels = ReadInt(ff)
	imaxnotes = ReadInt(ff)
	
	If imaxnotes <> maxnotes Then CloseFile ff: Return
	If imaxchannels <> maxchannels Then CloseFile ff: Return

	For j = 0 To maxchannels
		instrument(j) = ReadInt(ff)
		SelectInstrument j, instrument(j)
	Next
	
	For i = 0 To maxnotes
	For j = 0 To maxchannels
		pattern(j, i) = ReadInt(ff)
		vol(j, i) = ReadInt(ff)
	Next
	Next
	
	If Not Eof(ff) Then
		curX = ReadInt(ff)
		curY = ReadInt(ff)
	End If
	
	CloseFile ff
	
End Function

;-----------------------------------------------------------------------------------------------------
;													SaveMidi()
;-----------------------------------------------------------------------------------------------------
;save MIDI file
Function SaveMidi(name$)

	expand = 1 + 3 * (maxnotes < 16)
	notelength = 96 * expand;$60
	
	SetBuffer FrontBuffer()
	Color 255, 255, 255
	Cls
	Locate 0, 0
	
	Print "Saving MIDI file '" + name$ + "' .. "
	
	;set drum instruments
	For i = 0 To 4
		Instrument(i) = 0
	Next
	
	;general tempo
	;BPM# = 15000 / spd
	
	;-------------------------------------------------------------------------------------------------
	; 								     calculate tempo
	;-------------------------------------------------------------------------------------------------
	
	tt = 4000 / expand * spd ;15000/BPM
	t1 = (tt Shr 16)
	t2 = (tt Shr 8) Mod 256
	t3 = (tt Mod 256)
	
	;opens file
	MIDI_File = WriteFile(name$)
	
	If MIDI_File = 0 Then Return
	
	;-------------------------------------------------------------------------------------------------
	;	  								 write MIDI header
	;-------------------------------------------------------------------------------------------------
	
	;[4] standard header
	MIDI_WriteLine "MThd"
	
	;[4] size header = 6 bytes
	MIDI_WriteLine Chr$(0) + Chr$(0) + Chr$(0) + Chr$(6)
	
	;[2] midi type 2
	MIDI_WriteLine Chr$(0) + Chr$(2)
	
	;[2] number of tracks
	MIDI_WriteLine Chr$(0) + Chr$(maxchannels + 1)
	
	;[2] time base 
	MIDI_WriteLine Chr$($01) + Chr$($80)
	
	;-------------------------------------------------------------------------------------------------
	;										tempo track
	;-------------------------------------------------------------------------------------------------
	
	;[4] track header
	MIDI_WriteLine "MTrk"
	
	;[4] track length in bytes = 10 bytes
	MIDI_WriteLine Chr$(0) + Chr$(0) + Chr$(0) + Chr$($0A)
	
	;[8] tempo in microseconds pro quarter note
	MIDI_WriteLine Chr$(0) + Chr$(255) + Chr$(81) + Chr$(3) + Chr$(t1) + Chr$(t2) + Chr$(t3) + Chr$(0)
	
	;[3] end of track
	MIDI_WriteLine Chr$(255) + Chr$(47) + Chr$(0) 
	
	Dim nxt(maxnotes)
	
	For iCurTrack = 0 To maxchannels 
	
	For i = 0 To maxnotes
		If pattern(iCurTrack, i) > 0 Then Exit
		If i Mod 8 = 0 Then 
			pattern(iCurTrack, i) = 3000
			vol(iCurTrack, i) = 01
		End If
	Next
	;If pattern(iCurtrack, 0) = 0 Then pattern(iCurtrack, 0) = 13: vol(iCurTRack, 0) = 1
	
	CurTrack = ConvertChannel(iCurTrack)
	
	;-------------------------------------------------------------------------------------------------
	;									    track CurTrack
	;-------------------------------------------------------------------------------------------------
	
	;[4] track header
	MIDI_WriteLine "MTrk"
	
	For i = 0 To maxnotes
		templen(i) = 0
	Next
	
	t = 0: prev = -1
	For i = 0 To maxnotes
		note = pattern(iCurTrack, i) - 1
		If note > 0 Then 
			t = t + 1
			If prev > -1 Then
				templen(prev) = i - prev
			End If
			prev = i
			If i = maxnotes Then templen(i) = 1
		End If
	Next
	If prev > -1 Then templen(prev) = (maxnotes + 1) - prev
	
	p = 0
	For i = 0 To maxnotes
		If pattern(iCurTrack, i) > 0 Then p = p + writevarlen(templen(i) * notelength, 0)
	Next
	
	;519
	t = t * 7 + p + 7
	t1 = t Shr 8
	t2 = t Mod 256
	
	;[4] track length in bytes 
	MIDI_WriteLine Chr$(0) + Chr$(0) + Chr$(t1) + Chr$(t2)
	
	
	;[4] Channel: Program Change
	MIDI_WriteLine Chr$(0) + Chr$($C0 + CurTrack) + Chr$(Instrument(iCurTrack)) + Chr$(0)
	
	;-------------------------------------------------------------------------------------------------
	;				 						write notes
	;-------------------------------------------------------------------------------------------------
			
			bb = 7
			For i = 0 To maxnotes
				note = pattern(iCurTrack, i) - 1
				vou = vol(iCurTrack, i)
				If note = 2999 Then note = 13: pattern(iCurTrack, i) = 0
				If note > 0 Then
					;[4] notes [status] [byte1] [byte2] [delta]
					;     on                  note    volume       time
					Send $90 + CurTrack: Send note: Send vou: bb = bb + 3 + WriteVarLen(notelength * templen(i))
					;    off                  note    volume       time
					Send $80 + CurTrack: Send note: Send $00: Send $00: bb = bb + 4
				End If
			Next
			
	;-------------------------------------------------------------------------------------------------
	;										end of track
	;-------------------------------------------------------------------------------------------------
	;[3] end of track
	MIDI_WriteLine Chr$(255) + Chr$(47) + Chr$(0) 
	
	Next
	
	;closes file
	CloseFile MIDI_File

	Dim nxt(0)
	
	Print "Done."
	Print "Opening file.."
	;playback midi file
	chn = PlayMusic(name$)
	If chn = 0 Then 
		Print "Hmm, the channel returns an empty handle .. the file is probably not saved"
	Else
		Print "Playing file .. press any key To Exit"
	End If
	FlushKeys()
	WaitKey()
	FlushKeys()
	
	StopChannel chn
	
	SetBuffer BackBuffer()
	Cls
	
End Function


;--------------
;MIDI_WRITELINE
;--------------

;sends a string to the file

Function MIDI_WriteLine( t$ )
	For i = 1 To Len( t$ )
		WriteByte MIDI_File, Asc(Mid$(t$, i, 1))
	Next
End Function


;-----
;SEND
;-----

;sends a byte to the file

Function Send( p )
	WriteByte MIDI_File, p
End Function


;-------------------------------------------------------------------------------------------------
;											WriteVarLen()
;-------------------------------------------------------------------------------------------------
;write strange midi formatted number
Function WriteVarLen(value, send = 1)
	
	buffer = value And $7F
	
	Repeat
	
		value = value Shr 7
		If Not value Then Exit
		
		buffer = buffer Shl 8
		buffer = buffer Or (value And $7F) Or $80
		
	Forever
	
	t = 0
	Repeat
	
		If send Then Send (buffer And $FF)
		t = t + 1
		If buffer < $80 Then Exit
		buffer = buffer Shr 8
		
	Forever
	
	Return t
	
End Function
	
;-------------------------------------------------------------------------------------------------
;													TestSel()
;-------------------------------------------------------------------------------------------------
Function TestSel(i = 0)
	;shift+move=select
	If curX < 0 Then Return
	If curY < 0 Then Return
	If curX > maxchannels Then Return
	If curY > maxnotes Then Return
	If KeyDown(42) Then 
		sel(curX, curY) = Not(sel(curX, curY))
		If i <> 0 Then
			For t = 0 To Abs(i)
				tt = t * Sgn(i) + curY
				If tt >= 0 Then If tt <= maxnotes Then
					sel(curX, tt) = Not(sel(curX, tt))
				End If				
			Next
		End If
	End If

End Function

;-------------------------------------------------------------------------------------------------
;													StartPlay()
;-------------------------------------------------------------------------------------------------
Function StartPlay()
			playing = Not(playing)
			If playing Then
				button$(4) = "Stop"
			Else
				button$(4) = "Play"
			End If
			starttime = nowtime
			oldtimenow = -1000
			For i = 0 To maxchannels
				PlayNote(i, -1, 0)
			Next			
End Function

;-------------------------------------------------------------------------------------------------
;													iInput()
;-------------------------------------------------------------------------------------------------
Function iInput$(r$)
	FlushKeys()
	r$ = Input$(r$)
	FlushKeys()
	Return r$
End Function

;-------------------------------------------------------------------------------------------------
;												SetSpeed()
;-------------------------------------------------------------------------------------------------
Function SetSpeed()
			Cls
			Color 255, 255, 255
			Locate 0, 0
			SetBuffer FrontBuffer()
			Print "Current speed is: " + spd + "  (" + 15000 / spd + " BPM)"
			spd = iInput ("new speed>")
			If spd = 0 Then spd = 1
			SetBuffer BackBuffer()
End Function

;-------------------------------------------------------------------------------------------------
;												Help
;-------------------------------------------------------------------------------------------------
Function Help()
		Cls
		Color 64, 64, 64
		Locate 0, 0
		Print "Use cursor keys to move around"
		Print
		Print "Q2W3E..etc for playing sounds"
		Print "ZSXDC..etc for lower octave"
		Print
		Print "Change octave using F7/F8"
		Print
		Print "Change (non-drumtrack) instrument: F9/F10"
		Print
		Print "Enter to play"
		Print "Space toggle recmode"
		Print
		Print "Esc to dump file and exit"
		Print
		Print "Del remove note"
		Print "TAB make mark"
		Print
		Print
		Print "F3-Cut"
		Print "F4-Copy"
		Print "F5-Paste"
		Print
		Print "Press any key to continue"
		Flip
		FlushKeys()
		Repeat
			r = GetKey()
		Until r Or MouseHit(1)
		FlushKeys()
End Function

;-------------------------------------------------------------------------------------------------
;												CopyFrame()
;-------------------------------------------------------------------------------------------------
Function CopyFrame()
	fmaxchannels = maxchannels
	fmaxnotes = maxnotes
	
	For i = 0 To maxchannels
	For j = 0 To maxnotes
		BUF_Sel(i, j) = Sel(i, j)
		BUF_Pattern(i, j) = Pattern(i, j)
		BUF_Vol(i, j) = Vol(i, j)
	Next
	Next

End Function

;-------------------------------------------------------------------------------------------------
;												CopyFrame()
;-------------------------------------------------------------------------------------------------
Function CutFrame(del=0)
	
	If Not del Then CopyFrame()	
	For i = 0 To maxchannels
	For j = 0 To maxnotes
		If del Or sel(i, j) Then
			Pattern(i, j) = 0
			Vol(i, j) = 0
			Sel(i, j) = 0
		End If
	Next
	Next

End Function

;-------------------------------------------------------------------------------------------------
;												PasteFrame()
;-------------------------------------------------------------------------------------------------
Function CopyFrame2()
	
	cx = 0 
	cy = 0
	For j = 0 To maxnotes
	For i = 0 To maxchannels
		If sel(i, j) Then
			cx = i
			cy = j
			Exit
		End If
	Next
	If sel(i, j) Then Exit
	Next
	
	For i = 0 To maxchannels
	For j = 0 To maxnotes
		ii = i - cx
		jj = j - cy
		If ii <= maxchannels Then
		If ii >= 0 Then
			If jj <= maxnotes Then
			If jj >= 0 Then
					BUF_Pattern(ii, jj) = Pattern(i, j)
					BUF_Vol(ii, jj) = Vol(i, j)
					BUF_Sel(ii, jj) = Sel(i, j)
			End If
			End If
		End If
		End If
	Next
	Next

End Function


;-------------------------------------------------------------------------------------------------
;												PasteFrame()
;-------------------------------------------------------------------------------------------------
Function PasteFrame(cx, cy, seld = 0)
	
	For i = 0 To fmaxchannels
	For j = 0 To fmaxnotes
		ii = i + cx
		If ii <= maxchannels Then
		If ii >= 0 Then
			jj = j + cy
			If jj <= maxnotes Then
			If jj >= 0 Then
				If seld Or BUF_sel(i, j) Then
					Pattern(ii, jj) = BUF_Pattern(i, j)
					Vol(ii, jj) = BUF_Vol(i, j)
					If seld Then Sel(ii, jj) = Buf_Sel(i, j)
				End If
			End If
			End If
		End If
		End If
	Next
	Next

End Function

;-------------------------------------------------------------------------------------------------
;												ChangeLength()
;-------------------------------------------------------------------------------------------------
Function ChangeLength(newlength = -1)

	If newlength = -1 Then
		Cls
		Color 255, 255, 255
		Locate 0, 0
		SetBuffer FrontBuffer()
		Print "Current length is: " + (maxnotes+1)
		Print "Exporting MIDI is now only supported for 16/32/64"
		newlength = iInput ("new length>")
		SetBuffer BackBuffer()
		If newlength = 0 Then Return
		newlength = Abs(newlength) - 1
	End If
			
	CopyFrame()
	
	maxnotes = newlength

	Dim 	Pattern(maxchannels, maxnotes)
	Dim 	Vol(maxchannels, maxnotes)
	Dim		Sel(maxchannels, maxnotes)
	
	;delete
	CutFrame(1)

	PasteFrame(0, 0, 1)	

	Dim 	BUF_Vol(maxchannels, maxnotes)
	Dim		BUF_Pattern(maxchannels, maxnotes)
	Dim		BUF_Sel(maxchannels, maxnotes)

	vis_maxnotes = 31
	vis_maxchannels = 3
	If vis_maxnotes > maxnotes Then vis_maxnotes = maxnotes
	If vis_maxchannels > maxchannels Then vis_maxchannels = maxchannels
	
	CurX = 0
	CurY = 0

End Function
