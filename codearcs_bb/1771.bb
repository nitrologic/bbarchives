; ID: 1771
; Author: Devils Child
; Date: 2006-08-02 09:50:58
; Title: Sound libary
; Description: Little sound libary for bb

Type Sound
	Field path$, snd, snd3d
End Type
Type Channel
	Field ch
End Type

Function InitSoundLib(path$)
CreateListener(Cam)
End Function

Function FreeSoundLib()
For snd.Sound = Each Sound
	If snd\snd Then FreeSound snd\snd
	If snd\snd3d Then FreeSound snd\snd3d
	Delete snd
Next
End Function

Function CreateSound(path$, ent = 0, volume_times = 1)
For snd.Sound = Each Sound
	If snd\path$ = path$ Then
		For i = 1 To volume_times
			ch.Channel = New Channel
			If ent Then ch\ch = EmitSound(snd\snd3d, ent) Else ch\ch = PlaySound(snd\snd)
		Next
		Return
	EndIf
Next
snd.Sound = New Sound
snd\path$ = path$
snd\snd = LoadSound(path$)
snd\snd3d = Load3DSound(path$)
CreateSound(path$)
End Function

Function UpdateSoundLib()
For ch.Channel = Each Channel
	If ChannelPlaying(ch\ch) = False Then
		Delete ch
	Else
	EndIf
Next
End Function
