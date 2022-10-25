; ID: 967
; Author: Klaas
; Date: 2004-03-14 09:16:26
; Title: mp3 Infos (including Audiframes and id3v2)
; Description: analyses a mp3file

;def
Dim mp3_bri$(16)
mp3_bri$(0) = "free"
mp3_bri$(1) = "32"
mp3_bri$(2) = "40"
mp3_bri$(3) = "48"
mp3_bri$(4) = "56"
mp3_bri$(5) = "64"
mp3_bri$(6) = "80"
mp3_bri$(7) = "96"
mp3_bri$(8) = "112"
mp3_bri$(9) = "128"
mp3_bri$(10) = "160"
mp3_bri$(11) = "192"
mp3_bri$(12) = "224"
mp3_bri$(13) = "256"
mp3_bri$(14) = "320"
mp3_bri$(15) = "bad"

Dim mp3_sri$(4)
mp3_sri(0) = "44100"
mp3_sri(1) = "48000"
mp3_sri(2) = "32000"
mp3_sri(3) = "reserved"

Dim mp3_chi$(4)
mp3_chi(0) = "Stereo"
mp3_chi(1) = "Joint Stereo"
mp3_chi(2) = "Dual"
mp3_chi(3) = "Mono"

Dim mp3_emi$(4)
mp3_emi(0) = "None"
mp3_emi(1) = "50/15"
mp3_emi(2) = "reserved"
mp3_emi(3) = "CCIT J.17"

Dim mp3_mvi$(4)
mp3_mvi$(0) = "MPEG Version 2.5 (not an official standard)"
mp3_mvi$(1) = "reserved"
mp3_mvi$(2) = "MPEG Version 2"
mp3_mvi$(3) = "MPEG Version 1"

Dim mp3_mli$(4)
mp3_mli$(0) = "reserved"
mp3_mli$(1) = "Layer III"
mp3_mli$(2) = "Layer II"
mp3_mli$(3) = "Layer I"

Dim mp3_genre$(128)
mp3_genre( 0)= "Blues"
mp3_genre( 1)= "Classic Rock"
mp3_genre( 2)= "Country"
mp3_genre( 3)= "Dance"
mp3_genre( 4)= "Disco"
mp3_genre( 5)= "Funk"
mp3_genre( 6)= "Grunge"
mp3_genre( 7)= "Hip-Hop"
mp3_genre( 8)= "Jazz"
mp3_genre( 9)= "Metal"
mp3_genre(10)= "New Age"
mp3_genre(11)= "Oldies"
mp3_genre(12)= "Other"
mp3_genre(13)= "Pop"
mp3_genre(14)= "R&B"
mp3_genre(15)= "Rap"
mp3_genre(16)= "Reggae"
mp3_genre(17)= "Rock"
mp3_genre(18)= "Techno"
mp3_genre(19)= "Industrial"
mp3_genre(20)= "Alternative"
mp3_genre(21)= "Ska"
mp3_genre(22)= "Death Metal"
mp3_genre(23)= "Pranks"
mp3_genre(24)= "Soundtrack"
mp3_genre(25)= "Euro-Techno"
mp3_genre(26)= "Ambient"
mp3_genre(27)= "Trip-Hop"
mp3_genre(28)= "Vocal"
mp3_genre(29)= "Jazz+Funk"
mp3_genre(30)= "Fusion"
mp3_genre(31)= "Trance"
mp3_genre(32)= "Classical"
mp3_genre(33)= "Instrumental"
mp3_genre(34)= "Acid"
mp3_genre(35)= "House"
mp3_genre(36)= "Game"
mp3_genre(37)= "Sound Clip"
mp3_genre(38)= "Gospel"
mp3_genre(39)= "Noise"
mp3_genre(40)= "AlternRock"
mp3_genre(41)= "Bass"
mp3_genre(42)= "Soul"
mp3_genre(43)= "Punk"
mp3_genre(44)= "Space"
mp3_genre(45)= "Meditative"
mp3_genre(46)= "Instrumental Pop"
mp3_genre(47)= "Instrumental Rock"
mp3_genre(48)= "Ethnic"
mp3_genre(49)= "Gothic"
mp3_genre(50)= "Darkwave"
mp3_genre(51)= "Techno-Industrial"
mp3_genre(52)= "Electronic"
mp3_genre(53)= "Pop-Folk"
mp3_genre(54)= "Eurodance"
mp3_genre(55)= "Dream"
mp3_genre(56)= "Southern Rock"
mp3_genre(57)= "Comedy"
mp3_genre(58)= "Cult"
mp3_genre(59)= "Gangsta"
mp3_genre(60)= "Top 40"
mp3_genre(61)= "Christian Rap"
mp3_genre(62)= "Pop/Funk"
mp3_genre(63)= "Jungle"
mp3_genre(64)= "Native American"
mp3_genre(65)= "Cabaret"
mp3_genre(66)= "New Wave"
mp3_genre(67)= "Psychadelic"
mp3_genre(68)= "Rave"
mp3_genre(69)= "Showtunes"
mp3_genre(70)= "Trailer"
mp3_genre(71)= "Lo-Fi"
mp3_genre(72)= "Tribal"
mp3_genre(73)= "Acid Punk"
mp3_genre(74)= "Acid Jazz"
mp3_genre(75)= "Polka"
mp3_genre(76)= "Retro"
mp3_genre(77)= "Musical"
mp3_genre(78)= "Rock & Roll"
mp3_genre(79)= "Hard Rock"

Global mp3_FileSize

Type id3v1
	Field key$
	Field Dat$
End Type

Type id3v2
	Field key$
	Field flags
	Field Dat$
	Field size
	Field start
End Type

Type AudioFrame
	Field mpegVersion$
	Field mpegLayer$
	Field CRCprotection
	Field Bitrate
	Field samplerate
	Field padding
	Field PrivateBit
	Field Channel$
	Field ModeExtension_intensity
	Field ModeExtension_MS
	Field copyright
	Field Original
	Field Emphasis$
	
	Field num
	Field framelen
	Field framestart
End Type

Graphics 800,600,32,2
filename$ = "D:\MP3\musik\Muse\Showbiz\01 - Sunburn.mp3"

Print "Anlysing: "+filename
Print "----"
readTagv1(filename)
file = mp3_openFile(filename)
analyseMP3(file)
file = mp3_closeFile(filename)

Print "----"
mp3_PrintAudioFrame(Last AudioFrame)

Print "----"
For t1.id3v1 = Each id3v1
	mp3_printID3v1(t1)
Next

Print "----"
For t2.id3v2 = Each id3v2
	mp3_printID3v2(t2)
Next
WaitKey
End

Function mp3_printID3v1(t.id3v1)
	If t\key = "Genre"
		Print t\key+": "+mp3_genre(Int(t\dat))
	Else
		Print t\key+": "+t\dat
	EndIf
End Function

Function mp3_printID3v2(t.id3v2)
	Print t\key+": "+t\dat
End Function

Function mp3_openFile(filename$)
	If Not FileType(filename) = 1 Then Return False

	file = ReadFile(filename)
	mp3_FileSize = FileSize(filename)
	Return file
End Function

Function mp3_closeFile(filename$)
	If Not FileType(filename) = 1 Then Return False

	file = ReadFile(filename)
	Return file
End Function

Function analyseMP3(file)
	SeekFile(file,0)
	readTagv2(file)

	While Not Eof(file)
		a.audioframe = mp3_readAudioFrame.AudioFrame(file,count)
		If a = Null
			Print "interupt by "+FilePos(file)
			Exit
		Else
			lastvalid.Audioframe = a
		EndIf
		count = count + 1
	Wend
	
	Print "Analysed "+count+" Audioframes"
End Function

Function mp3_readAudioFrame.AudioFrame(file,count)
	
	pos = FilePos(file)
	
	b1 = ReadByte(file)
	b2 = ReadByte(file)
	b3 = ReadByte(file)
	b4 = ReadByte(file)
	
	If b1 <> $ff And (b2 And 224) <> 224
		Print "Frame syncronizer not found"
		SeekFile(file,FilePos(file)-4)
		Return Null
	EndIf
	
	a.AudioFrame = New AudioFrame
	a\framestart = pos
	a\num = count
	
	MPEGversionID = (b2 And 24) Shr 3
	a\mpegVersion = mp3_mvi(MPEGversionID)

	MPEGlayerID = (b2 And 6) Shr 1
	a\mpegLayer = mp3_mli(MPEGlayerID)
	
	a\CRCprotection = (b2 And $1)
	
	BitrateID = (b3 And $f0) Shr 4
	a\BitRate = Int(mp3_bri(BitrateID)) * 1000
	
	SamplerateID = (b3 And $c) Shr 2
	a\SampleRate = Int(mp3_sri(SamplerateID))
	
	a\Padding = (b3 And $2) Shr 1

	a\privateBit = (b3 And 1)

	channelID = (b4 And $C0) Shr 6
	a\channel = mp3_chi(channelID)

	a\ModeExtension_intensity = (b4 And $10) Shr 5
	a\ModeExtension_MS = (b4 And $20) Shr 6
	
	a\copyright = (b4 And $8)

	a\original = (b4 And $4)
	
	emphasisID = (b4 And $3)
	a\emphasis = mp3_emi(emphasisID)
	
	a\FrameLen = Floor((144.0 * Float(a\BitRate) / Float(a\SampleRate) ) + a\Padding)
	
	SeekFile(file,FilePos(file)+a\framelen-4)
	Return a
End Function

Function mp3_PrintAudioFrame(a.audioframe)
	Print "Audioframe "+a\num
	Print "MPEG Version: " + a\mpegVersion
	Print "MPEG Layer: " + a\mpegLayer
	If a\CRCprotection
		Print "NOT Protected by CRC"
	Else
		Print "Protected by CRC"
	EndIf
	
	Print "Bitrate: " + a\BitRate + " bps"	
	Print "Samplig rate frequency: " + a\SampleRate+" hz"
	
	If a\Padding
		Print "Frame is padded"
	Else
		Print "Frame is not padded"
	EndIf

	If a\privateBit 
		Print "private bit is set"
	Else
		Print "private bit is not set"
	EndIf

	Print "Channel: " + a\channel+" mode"

	Print "Mode Extension: "
	If a\ModeExtension_intensity
		Print "Intensity Stereo on"
	Else
		Print "Intensity Stereo off"
	EndIf
	If a\ModeExtension_MS
		Print "MS Stereo on"
	Else
		Print "MS Stereo off"
	EndIf

	If a\copyright
		Print "Audio is copyrighted"
	Else
		Print "Audio is not copyrighted"
	EndIf

	If a\original
		Print "Original media"
	Else
		Print "Copy of original media"
	EndIf
	
	Print "Emphasis: " + a\emphasis
	
	Print "Framelength: "+a\FrameLen
End Function

Function readTagv1(filename$)
	If Not FileType(filename) = 1 Then Return False

	file = ReadFile(filename)
	SeekFile(file,FileSize(filename)-128)
	
	For i=0 To 2
		txt$ = txt$ + Chr(ReadByte(file))
	Next
	
	If txt = "TAG"
		Print "ID3 v1 Tag present"
				
		txt = ""
		For i=0 To 29
			songname$ = songname$ + Chr(ReadByte(file))
		Next
		t.id3v1 = New id3v1
		t\key = "Songname"
		t\dat = Trim(songname)
		
		For i=0 To 29
			Artist$ = Artist$ + Chr(ReadByte(file))
		Next
		t.id3v1 = New id3v1
		t\key = "Artist"
		t\dat = Trim(artist)

		For i=0 To 29
			Album$ = Album$ + Chr(ReadByte(file))
		Next
		t.id3v1 = New id3v1
		t\key = "Album"
		t\dat = Trim(album)

		txt = ""
		For i=0 To 3
			year$ = year$ + Chr(ReadByte(file))
		Next
		t.id3v1 = New id3v1
		t\key = "Year"
		t\dat = Trim(year)

		For i=0 To 29
			Comment$ = Comment$ + Chr(ReadByte(file))
		Next
		t.id3v1 = New id3v1
		t\key = "Comment"
		t\dat = Trim(comment)

		For i=0 To 0
			genre = ReadByte(file)
		Next
		t.id3v1 = New id3v1
		t\key = "Genre"
		t\dat = Trim(genre)
		
	EndIf
End Function

Function readTagv2(file)		
	For i=0 To 2
		txt$ = txt$ + Chr(ReadByte(file))
	Next
	
	If txt = "ID3"
		
		;read TAG version
		hiVersion = ReadByte(file)
		lowVersion = ReadByte(file)
		Print "ID3 v2 Tag present (version: "+hiVersion+"/"+lowVersion+")"
		
		;read flags
		flags = ReadByte(file)
		If flags And 128 Then Print "unsynchronisation is applied on all frames"
		If flags And 64 Then Print "the header is followed by an extended header"
		If flags And 32 Then Print "Experimental"
		If flags And 16 Then Print "Footer present"
		
		;read size of tag
		b = ReadInt(file)
		
		size = syncsafeInt(b)

		While tagpos < size
			;read frame TAG
			txt = ""

			b1 = ReadByte(file)
			tagpos = tagpos + 1
			If b1
				t.id3v2 = New id3v2
				t\start = FilePos(file)-1
				
				b2 = ReadByte(file)
				b3 = ReadByte(file)
				b4 = ReadByte(file)
				tagpos = tagpos + 3

				txt$ = Chr(b1) + Chr(b2) + Chr(b3) + Chr(b4)
				t\key = txt
				
				framesize = syncsafeInt(ReadInt(file))
				tagpos = tagpos + 4
				t\size = framesize
				
				flags = ReadShort(file)
				tagpos = tagpos + 2
				t\flags = flags
								
				txt$ = ""
				For i= 1 To framesize
					txt = txt + Chr(ReadByte(file))
					tagpos = tagpos + 1
				Next
				t\dat$ = Trim(txt)
			EndIf
			;WaitKey
		Wend
	Else
		SeekFile(file,0)
	EndIf
End Function

Function syncsafeInt(b)
	
	b1 = b And $ff000000
	b2 = b And $00ff0000
	b3 = b And $0000ff00
	b4 = b And $000000ff
	
	b1 = (b And $ff000000) Shr 24
	b2 = (b And $00ff0000) Shr 8
	b3 = (b And $0000ff00) Shr 8 
	b4 = (b And $000000ff) Shr 24

	b4 = b4 Shr 3
	b3 = b3 Shr 2
	b2 = b2 Shr 1
	
	size = b4 Or b3 Or b2 Or b1
	Return size
End Function
