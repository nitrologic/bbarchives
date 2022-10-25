; ID: 1742
; Author: b32
; Date: 2006-07-02 20:53:56
; Title: saving midi file
; Description: saves a 64 lines midi file

;--------------------------------
; TRACKER STYLE MIDI FILE OUTPUT
;--------------------------------
;        2006 Bram32bit
;          ver 3.01

;The MIDI file has a fixed size
;and writes only one pattern
;You cannot leave out notes without
;changing the size of the pattern


Dim Channel   (127)
Dim Instrument(127)

;number of tracks
NumTracks = 2

;setup channels/instruments pro track
Channel   (0) =  0    ;standard track
Instrument(0) =  8    ;celesta

Channel   (1) =  9    ;drum track
Instrument(1) =  0    ;unused

;general tempo
BPM = 120

;-------------------------------------------------------------------------------------------------
; 								     calculate tempo
;-------------------------------------------------------------------------------------------------

tt = 60000000 / BPM
t1 = (tt Shr 16)
t2 = (tt Shr 8) Mod 256
t3 = (tt Mod 256)

;opens file
Global MIDI_File = WriteFile("midifile.mid")

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
MIDI_WriteLine Chr$(0) + Chr$(3)

;[2] time base 
MIDI_WriteLine Chr$($01) + Chr$($80)

;-------------------------------------------------------------------------------------------------
;										tempo track
;-------------------------------------------------------------------------------------------------

;[4] track header
MIDI_WriteLine "MTrk"

;[4] track length in bytes = 10 bytes
MIDI_WriteLine Chr$(0) + Chr$(0) + Chr$(0) + Chr$($0A)

;[8] tempo
MIDI_WriteLine Chr$(0) + Chr$(255) + Chr$(81) + Chr$(3) + Chr$(t1) + Chr$(t2) + Chr$(t3) + Chr$(0)

;[3] end of track
MIDI_WriteLine Chr$(255) + Chr$(47) + Chr$(0) 


For CurTrack = 0 To NumTracks - 1

;-------------------------------------------------------------------------------------------------
;									    track CurTrack
;-------------------------------------------------------------------------------------------------
;[4] track header
MIDI_WriteLine "MTrk"

;[4] track length in bytes 
MIDI_WriteLine Chr$(0) + Chr$(0) + Chr$(2) + Chr$(7) 

;[4] Channel: Program Change
MIDI_WriteLine Chr$(0) + Chr$($C0 + Channel(CurTrack)) + Chr$(Instrument(CurTrack)) + Chr$(0)

;-------------------------------------------------------------------------------------------------
;				 						write notes
;-------------------------------------------------------------------------------------------------

		For i = 0 To 63
			note = Rand(128)
			vol  = Rand(128)
			;[4] notes [status] [byte1] [byte2] [delta]
			;     on                           note    volume       time
			Send $90 + Channel(CurTrack): Send note: Send $64: Send $5C
			;    off                           note    volume       time
			Send $80 + Channel(CurTrack): Send note: Send $00: Send $04 * (i < 63)
		Next

;-------------------------------------------------------------------------------------------------
;										end of track
;-------------------------------------------------------------------------------------------------
;[3] end of track
MIDI_WriteLine Chr$(255) + Chr$(47) + Chr$(0) 

Next

;closes file
CloseFile MIDI_File

;-------------------------------------------------------------------------------------------------
;										testing MIDI
;-------------------------------------------------------------------------------------------------

;playback midi file
PlayMusic "midifile.mid"

WaitKey()
End

;-------------------------------------------------------------------------------------------------
;  										 FUNCTIONS
;-------------------------------------------------------------------------------------------------


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
