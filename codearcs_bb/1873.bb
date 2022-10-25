; ID: 1873
; Author: kochOn
; Date: 2006-12-08 00:14:04
; Title: aSound.bb
; Description: (advanced) Sound Library

; aSound.bb : Advanced Sound Functions for Blitz
; by kochOn - www.kochonet.com
;-----------------------------------------------------------------------------------------------------
; This library exists cause I think there is a lack in Blitz Sound Functions.
; Those functions mainly load sounds with a single channel assigned and let the
; user defining groups of sounds (facility to stop, pause and resume several sounds
; at a time).

Type tASND
	Field copy%
	Field sound%, channel%, group%
	Field vol#, pan#
	Field pitched%, pitch%
	Field basefreq%, freq%, fscale#
End Type

; aSound_Load%(file$, loop% = False, group% = 0)
;-----------------------------------------------------------------------------------------------------
; Load a sound file in memory
;-----------------------------------------------------------------------------------------------------
; file$ :  all accepted blitz sound files
; loop% :  optional looping mode (defaut = False)
; group% : optional group (default = 0)

Function aSound_Load%(file$, loop% = False, group% = 0)
	Local asnd.tASND = New tASND
	
	asnd\sound = LoadSound(file$)
	If asnd\sound = 0 Then Return False
	If loop = True Then LoopSound(asnd\sound)
	asnd\copy = 0
	asnd\group = group
	asnd\vol = 1
	asnd\pan = 0
	asnd\pitched = False
	asnd\pitch = 0
	asnd\basefreq = False
	asnd\freq = 0
	asnd\fscale = 1
	Return Handle(asnd)
End Function

; aSound_Copy%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Copy a sound previously loaded with aSound_Load
; The copy keep the original behaviors (looping mode, group, volume, panning, pitching, base frequence
; and frequence scaling)
; Use the aSound_Update function to destroy all copies of a deleted sound
; You can copy another copy but each won't work when the parent or the base sound will be deleted
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Copy%(hsnd%)
	Local copy.tASND
	Local asnd.tASND
	Local tmp.tASND
	
	copy = Object.tASND(hsnd)
	If copy = Null Then Return False
	tmp = copy
	While copy\copy <> 0
		copy = Object.tASND(copy\copy)
		If copy = Null Then Return False
	Wend
	asnd = New tASND
	asnd\copy = Handle(copy)
	asnd\sound = 0
	asnd\group = tmp\group
	asnd\vol = tmp\vol
	asnd\pan = tmp\pan
	asnd\pitched = tmp\pitched
	asnd\pitch = tmp\pitch
	asnd\basefreq = tmp\basefreq
	asnd\freq = tmp\freq
	asnd\fscale = tmp\fscale
	Return Handle(asnd)
End Function

; aSound_Replace%(hsnd%, file$, loop% = False)
;-----------------------------------------------------------------------------------------------------
; Replace a previously loaded sound with another sound file (won't work with a copy)
; All modifications made with this sound remains and only the sound file change as for the looping
; mode
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound
; file$ : all accepted blitz sound files
; loop% : optional looping mode for the replacing sound (defaut = False)

Function aSound_Replace%(hsnd%, file$, loop% = False)
	Local asnd.tASND, tmp%
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	If asnd\copy <> 0 Then Return False
	tmp = LoadSound(file$)
	If tmp = 0 Then Return False
	FreeSound(asnd\sound)
	asnd\sound = tmp
	If loop = True Then LoopSound(asnd\sound)
	Return True	
End Function

; aSound_Play%(hsnd%, override% = False)
;-----------------------------------------------------------------------------------------------------
; Play a sound like the original Blitz function but only one at a time. If a sound is already playing
; nothing will happen.
; You can override a playing sound which will stop and replay from the start (no multiple same sounds
; until you make a copy) 
;-----------------------------------------------------------------------------------------------------
; hsnd% :     handle of a previously loaded sound or copy
; override% : restart the sound on its own channel (default = False)

Function aSound_Play%(hsnd%, override% = False)
	Local copy.tASND
	Local asnd.tASND
	Local tmp%
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	If asnd\copy <> 0 Then
		copy = Object.tASND(asnd\copy)
		If copy = Null Then Return False
		tmp = copy\sound
	Else
		tmp = asnd\sound
	EndIf
	If override = True Then
		If ChannelPlaying(asnd\channel) = True Then StopChannel(asnd\channel)
	EndIf
	If ChannelPlaying(asnd\channel) = False Then
		asnd\channel = PlaySound(tmp)
		ChannelVolume(asnd\channel, asnd\vol)
		ChannelPan(asnd\channel, asnd\pan)
		If asnd\pitched = True Then
			ChannelPitch(asnd\channel, asnd\pitch)
		EndIf
	EndIf
	Return True
End Function

; aSound_Volume%(hsnd%, vol#)
;-----------------------------------------------------------------------------------------------------
; Set the volume of a sound (the volume will stay the same until a new aSound_Volume command)
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy
; vol#  : the desired amount of volume (0 = min, 1 = max)

Function aSound_Volume%(hsnd%, vol#)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	asnd\vol = vol
	ChannelVolume(asnd\channel, vol)
	Return True
End Function

; aSound_Pan%(hsnd%, pan#)
;-----------------------------------------------------------------------------------------------------
; Set the panning of a sound (the panning will stay the same until a new aSound_Pan command)
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy
; pan#  : the desired panning level (-1 = left, 0 = middle, 1 = right)

Function aSound_Pan%(hsnd%, pan#)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	asnd\pan = pan
	ChannelPan(asnd\channel, pan)
	Return True
End Function

; aSound_Pitch%(hsnd%, pitch%)
;-----------------------------------------------------------------------------------------------------
; Set the Pitch of a sound (the frequence will stay the same until a new aSound_Pitch command)
;-----------------------------------------------------------------------------------------------------
; hsnd% :  handle of a previously loaded sound or copy
; pitch# : the desired frequence (11025, ..., 22050, ..., 44100, ...)

Function aSound_Pitch%(hsnd%, pitch%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	asnd\pitched = True
	asnd\pitch = pitch
	asnd\fscale = 1
	ChannelPitch(asnd\channel, pitch)
	Return True
End Function

; aSound_Playing%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Useful to know if a sound is playing (playing = True)
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Playing%(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	Return ChannelPlaying(asnd\channel) 
End Function

; aSound_Stop%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Stop a playing sound
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Stop%(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	If ChannelPlaying(asnd\channel) Then StopChannel(asnd\channel)
	Return True 
End Function

; aSound_Pause%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Pause a playing sound (use aSound_Resume to restart the sound from where it has been paused)
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Pause%(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	If ChannelPlaying(asnd\channel) Then PauseChannel(asnd\channel)
	Return True 
End Function

; aSound_Resume%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Resume a previously paused sound
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Resume%(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	ResumeChannel(asnd\channel)
	Return True 
End Function

; aSound_StopGroup(group%)
;-----------------------------------------------------------------------------------------------------
; Same as aSound_Stop but works for a group of sound
;-----------------------------------------------------------------------------------------------------
; group% : group of sounds to be stopped

Function aSound_StopGroup(group%)
	Local asnd.tASND
	
	For asnd = Each tASND
		If asnd\group = group Then
			If ChannelPlaying(asnd\channel) Then
				StopChannel(asnd\channel)
			EndIf
		EndIf
	Next
End Function

; aSound_PauseGroup(group%)
;-----------------------------------------------------------------------------------------------------
; Same as aSound_Pause but works for a group of sound
;-----------------------------------------------------------------------------------------------------
; group% : group of sounds to be paused

Function aSound_PauseGroup(group%)
	Local asnd.tASND
	
	For asnd = Each tASND
		If asnd\group = group Then
			If ChannelPlaying(asnd\channel) Then
				PauseChannel(asnd\channel)
			EndIf
		EndIf
	Next
End Function

; aSound_ResumeGroup(group%)
;-----------------------------------------------------------------------------------------------------
; Same as aSound_Resume but works for a group of sound
;-----------------------------------------------------------------------------------------------------
; group% : group of sounds to be resumed

Function aSound_ResumeGroup(group%)
	Local asnd.tASND
	
	For asnd = Each tASND
		If asnd\group = group Then
			ResumeChannel(asnd\channel)
		EndIf
	Next
End Function

; aSound_StopAll()
;-----------------------------------------------------------------------------------------------------
; Stop all playing sounds
;-----------------------------------------------------------------------------------------------------

Function aSound_StopAll()
	Local asnd.tASND
	
	For asnd = Each tASND
		If ChannelPlaying(asnd\channel) Then
			StopChannel(asnd\channel)
		EndIf
	Next
End Function

; aSound_PauseAll()
;-----------------------------------------------------------------------------------------------------
; Pause all playing sounds
;-----------------------------------------------------------------------------------------------------

Function aSound_PauseAll()
	Local asnd.tASND
	
	For asnd = Each tASND
		If ChannelPlaying(asnd\channel) Then
			PauseChannel(asnd\channel)
		EndIf
	Next
End Function

; aSound_ResumeAll()
;-----------------------------------------------------------------------------------------------------
; Resume all previously paused sounds
;-----------------------------------------------------------------------------------------------------

Function aSound_ResumeAll()
	Local asnd.tASND
	
	For asnd = Each tASND
		ResumeChannel(asnd\channel)
	Next
End Function

; aSound_Free%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Free a sound from the memory
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Free%(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	If asnd\copy = 0 Then FreeSound(asnd\sound)
	Delete asnd
	Return True 
End Function

; aSound_FreeGroup(group%)
;-----------------------------------------------------------------------------------------------------
; Free a group of sounds from the memory
;-----------------------------------------------------------------------------------------------------
; group% : group of sounds to be deleted

Function aSound_FreeGroup(group%)
	Local asnd.tASND
	
	For asnd = Each tASND
		If asnd\group = group Then
			If asnd\copy = 0 Then FreeSound(asnd\sound)
			Delete asnd
		EndIf
	Next
End Function

; aSound_FreeAll(group%)
;-----------------------------------------------------------------------------------------------------
; Free all sounds from the memory
;-----------------------------------------------------------------------------------------------------

Function aSound_FreeAll()
	Local asnd.tASND
	
	For asnd = Each tASND
		If asnd\copy = 0 Then FreeSound(asnd\sound)
		Delete asnd
	Next
End Function

; aSound_BaseFreq%(hsnd%, freq%)
;-----------------------------------------------------------------------------------------------------
; As the Blitz LoadSound Function do not permit to know the base frequence of a sample, this is a
; a useful function to set it by yourself.
; Then you could use The aSound_PitchB command which use a scalling factor instead of a pitch
; frequence
; Note that it's important to know and set the good frequence of a sample to avoid surprises
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy
; freq% : known base frequence of the sample

Function aSound_BaseFreq%(hsnd%, freq%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	asnd\basefreq = True
	asnd\freq = freq
	asnd\pitched = True
	asnd\pitch = Float(freq) * asnd\fscale
	ChannelPitch(asnd\channel, freq)
	Return True 
End Function

; aSound_PitchB%(hsnd%, scale#)
;-----------------------------------------------------------------------------------------------------
; Pitch a sound, which the base frequence has been set, by a scalling factor
; The result is set as the pitch frequence
; Note that using the normal aSound_Pitch command will reset the scalling factor
;-----------------------------------------------------------------------------------------------------
; hsnd% :  handle of a previously loaded sound or copy
; scale# : a scalling factor (2 = base frequence * 2, 0.5 = base frequence / 2)

Function aSound_PitchB%(hsnd%, scale#)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	If asnd\basefreq = False Then Return False
	asnd\fscale = scale
	asnd\pitched = True
	asnd\pitch = Float(asnd\freq) * asnd\fscale
	ChannelPitch(asnd\channel, asnd\pitch)
	Return True
End Function

; aSound_Reset%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Reset all properties of a sound or copy execept for the looping mode
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Reset%(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	If ChannelPlaying(asnd\channel) = True Then StopChannel(asnd\channel)
	asnd\vol = 1
	asnd\pan = 0
	asnd\pitched = False
	asnd\pitch = 0
	asnd\basefreq = False
	asnd\freq = 0
	asnd\fscale = 1
End Function

; aSound_Update()
;-----------------------------------------------------------------------------------------------------
; Necessary to automatically kill copies depending of a sound or copy that has been deleted
; You can use it in a loop or at the end of a program after the base sounds have been deleted
; Forget it if you don't use sound copies
;-----------------------------------------------------------------------------------------------------

Function aSound_Update()
	Local src.tASND
	Local asnd.tASND
	
	For asnd = Each tASND
		If asnd\copy <> 0 Then
			src = Object.tASND(asnd\copy)
			If src = Null Then
				Delete asnd
			EndIf
		EndIf	
	Next
End Function

; aSound_Group%(hsnd%, group%)
;-----------------------------------------------------------------------------------------------------
; Let you set a new or existing group for a sound
;-----------------------------------------------------------------------------------------------------
; hsnd% :  handle of a previously loaded sound or copy
; group% : new or existing group to assign the sound (can be any positive or negative integer value) 

Function aSound_Group%(hsnd%, group%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False
	asnd\group = group
	Return True
End Function

; aSound_GetVolume$(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Return a string containing the sound volume or "Null" if the sound doesn't exist
; The result should be converted into a float value
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_GetVolume$(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return "Null"
	Return Str$(asnd\vol)
End Function

; aSound_GetPan$(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Return a string containing the sound panning level or "Null" if the sound doesn't exist
; The result should be converted into a float value
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_GetPan$(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return "Null"
	Return Str$(asnd\pan)
End Function

; aSound_GetPitch$(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Return a string containing the sound pitching frequence or "Null" if the sound doesn't exist
; The result should be converted into an integer value
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_GetPitch$(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return "Null"
	If asnd\pitched = False Then Return "Undefined"
	Return Str$(asnd\pitch)
End Function

; aSound_GetBaseFreq$(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Return a string containing the sound base frequence, "undefined" or "Null" if the sound doesn't
; exist
; The result should be converted into an integer value
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_GetBaseFreq$(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return "Null"
	If asnd\basefreq = False Then Return "Undefined"
	Return Str$(asnd\freq)
End Function

; aSound_GetPitchB$(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Return a string containing the sound scalling factor or "Null" if the sound doesn't exists
; The result should be converted into a float value
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_GetPitchB$(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return "Null"
	Return Str$(asnd\fscale)
End Function

; aSound_Exists%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Return True if the sound exists False otherwise
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Exists%(hsnd%)
	Local asnd.tASND
	
	asnd = Object.tASND(hsnd)
	If asnd = Null Then Return False Else Return True
End Function

; aSound_Count%()
;-----------------------------------------------------------------------------------------------------
; Return the number of existing sounds after killing unused copies
;----------------------------------------------------------------------------------------------------- 

Function aSound_Count%()
	Local src.tASND
	Local asnd.tASND
	Local nb% = 0
	
	For asnd = Each tASND
		If asnd\copy <> 0 Then
			src = Object.tASND(asnd\copy)
			If src = Null Then
				Delete asnd
			Else
				nb = nb + 1
			EndIf
		Else
			nb = nb + 1
		EndIf	
	Next
	Return nb
End Function

; aSound_Debug%(hsnd%)
;-----------------------------------------------------------------------------------------------------
; Show properties of a sound in the Debug Window (Debug Mode)
;-----------------------------------------------------------------------------------------------------
; hsnd% : handle of a previously loaded sound or copy

Function aSound_Debug%(hsnd%)
	Local src.tASND
	Local asnd.tASND
		
	asnd = Object.tASND(hsnd)
	If asnd = Null Then DebugLog("Null") : Return False
	DebugLog("========================")
	DebugLog("handle:   " + Str$(Handle(asnd)))
	If asnd\copy <> 0 Then
		src = Object.tASND(asnd\copy)
		If src = Null Then
			DebugLog("copy of:  unexisting")
		Else
			DebugLog("copy of:  "+ Str$(asnd\copy))
		EndIf
	EndIf
	DebugLog("------------------------")
	DebugLog("group:    " + Str$(asnd\group))
	DebugLog("volume:   " + Str$(asnd\vol))
	DebugLog("pan:      " + Str$(asnd\pan))
	If asnd\pitched = True Then
		DebugLog("pitch:    " + Str$(asnd\pitch))
	Else
		DebugLog("pitch:    not set")
	EndIf
	DebugLog("------------------------")
	If asnd\basefreq = True Then
		DebugLog("basefreq: " + Str$(asnd\freq))
		DebugLog("fscale:   " + Str$(asnd\fscale))
	Else
		DebugLog("basefreq: not set")
	EndIf
	Return True
End Function
