; ID: 1498
; Author: Hotcakes
; Date: 2005-10-21 02:07:32
; Title: HOT.IFF8SVXloader
; Description: Loads IFF 8SVX sounds through LoadSound()

Strict

Rem
bbdoc: IFF 8SVX loader
End Rem	' bbdoc
Module HOT.IFF8SVXLoader

ModuleInfo "Version: 1.00"
ModuleInfo "Author: Toby Zuijdveld"
ModuleInfo "License: Blitz Shared Source Code"
ModuleInfo "Copyright: Jerry Morrison and Steve Hayes, Electronic Arts (public domain)"
ModuleInfo "Modserver: n/a"

ModuleInfo "History: 1.00 Release"
ModuleInfo "History:     Does not support volume flag"
ModuleInfo "History:     Does not support Fibonacci-Delta compression"
ModuleInfo "History:     Does not support multi-octave samples"

Import BRL.AudioSample
Import BRL.EndianStream

' '#Region Private block
Private

Function ReadTag$( stream:TStream )
	Local tag:Byte[4]
	If stream.ReadBytes( tag,4 )<>4 Return
	Return Chr(tag[0])+Chr(tag[1])+Chr(tag[2])+Chr(tag[3])
End Function

' sCompression: Choice of compression algorithm applied to the samples
Const sCmpNone		= 0		' not compressed
Const sCmpFibDelta	= 1		' Fibonacci-Delta encoding
'#End Region

Public

Type TAudioSampleLoaderIFF8SVX Extends TAudioSampleLoader

	Method LoadAudioSample:TAudioSample( stream:TStream )

		stream=BigEndianStream(stream)

		If ReadTag( stream )<>"FORM" Return

		Local	dud%	= stream.Readint()	' length of file after 8SVX header

		If ReadTag( stream )<>"8SVX" Return
		If ReadTag( stream )<>"VHDR" Return

				dud		= stream.Readint()	' length of Voice8Header chunk
		
		Local	oneShotHiSamples%	= stream.ReadInt()
		Local	repeatHiSamples%	= stream.ReadInt()
		Local	samplesPerHiCycle%	= stream.ReadInt()
		Local	samplesPerSec:Short	= stream.ReadShort()
		Local	ctOctave:Byte		= stream.ReadByte();If ctOctave>1 Then Return
		Local	sCompression:Byte	= stream.ReadByte();If sCompression<>sCmpNone Then Return
		Local	volume:Short		= stream.ReadShort()	' ;If volume<>1 Then Return

		Local	format	= SF_MONO8

		stream.SkipBytes(dud-18)	' handle extended header information
		
		While Not stream.Eof()

			If Readtag$(stream)<>"BODY"
				Local	dud	= stream.Readint()
				stream.SkipBytes(dud)
				Continue
			EndIf

			Local	w_sizebytes		= stream.Readint()
			Local	t:TAudioSample	= TAudioSample.Create(w_sizebytes,samplesPerSec,format)

			stream.ReadBytes t.samples,w_sizebytes

			For dud=0 To w_sizebytes-1
				If t.samples[dud]>127
					t.samples[dud]=383-(t.samples[dud]+1)
				Else
					t.samples[dud]=128-t.samples[dud]
				EndIf	' signed --> unsigned conversion
			Next	' dud=0 To w_sizebytes-1
			
			Return t
			
		Wend	' Not stream.EOF()

	End Method

End Type

AddAudioSampleLoader New TAudioSampleLoaderIFF8SVX
