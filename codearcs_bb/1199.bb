; ID: 1199
; Author: Eikon
; Date: 2004-11-15 11:31:36
; Title: PlaySound replacement
; Description: userlib

; Required Userlib
; .lib "winmm.dll"
; sndPlaySound%(lpszSoundName$, uFlags%):"sndPlaySoundA"

; lpszSoundName:
; A string that specifies the sound to play. This parameter can be either 
; an entry in the registry or in WIN.INI that identifies a system sound, or 
; it can be the name of a waveform-audio file. (If the function does not 
; find the entry, the parameter is treated as a filename.) If this parameter 
; is NULL, any currently playing sound is stopped.

; uFlag constants
Const SND_ASYNC = 1
; The sound is played asynchronously and the function returns immediately 
; after beginning the sound. To terminate an asynchronously played sound, 
; call sndPlaySound with lpszSoundName set to NULL.

Const SND_LOOP = 8
; The sound plays repeatedly until sndPlaySound is called again with the 
; lpszSoundName parameter set to NULL. You must also specify the SND_ASYNC 
; flag to loop sounds.

Const SND_MEMORY = 4
; The parameter specified by lpszSoundName points to an image of a waveform
; sound in memory.

Const SND_NODEFAULT = 2
; If the sound cannot be found, the function returns silently without 
; playing the default sound.

Const SND_NOSTOP = 10
; If a sound is currently playing, the function immediately returns FALSE, 
; without playing the requested sound.

Const SND_SYNC = 0
; The sound is played synchronously and the function does not return until 
; the sound ends.

Local file$ = "c:\windows\media\ding.wav" ; WAV Filename
sndPlaySound file$, SND_ASYNC
