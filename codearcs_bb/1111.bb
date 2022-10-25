; ID: 1111
; Author: Ziltch
; Date: 2004-07-19 18:22:38
; Title: MakeAudio
; Description: Make your own sounds

; Blitz Sound From Memory banks function library
; ADAmor ZILTCH June 2003
;
; version .8  28/06/2003
;         .9  06/04/2004 Speed up by adding WavDataStartPos Param to read/write
;        1.0  20/05/2004 Fixed ReadWavBank Bug

;--------------------------------------------------------------
;  Add to winmm.decls (or create) in userlib directory
;
;.lib "winmm.dll"
;winmm_PlaySound%(lpszName*,hModule%,dwFlags%):"PlaySoundA"
;winmm_StopSound%(lpszName%,hModule%,dwFlags%):"PlaySoundA"

;--------------------------------------------------------------

;NOTE:
;You can load ,play and save all wave files,but only alter PCM type files.
;PCM wav files contain uncompressed sample data so they are easier to deal with.

;ALSO:
;Unfortunately only ONE sample at a time can be played from a sound bank.
;But you can alter sounds as you play them!
;Blitz sounds commands will work at the same time.


Const SND_APPLICATION = $80         ;  look for application specific association
Const SND_ALIAS = $10000            ;  name is a WIN.INI [sounds] entry
Const SND_ALIAS_ID = $110000        ;  name is a WIN.INI [sounds] entry identifier
Const SND_ASYNC = $1                ;  play asynchronously
Const SND_FILENAME = $20000         ;  name is a file name
Const SND_LOOP = $8                 ;  loop the sound until next sndPlaySound
Const SND_MEMORY = $4               ;  lpszSoundName points to a memory file
Const SND_NODEFAULT = $2            ;  silence not default, if sound not found
Const SND_NOSTOP = $10              ;  don;t stop any currently playing sound
Const SND_NOWAIT = $2000            ;  don;t wait if the driver is busy
Const SND_PURGE = $40               ;  purge non-static events for task
Const SND_RESOURCE = $40004         ;  name is a resource name or atom
Const SND_SYNC = $0                 ;  play synchronously (default)
Const ALL_CHANNELS    = 0
Const LEFT_CHANNEL    = 1
Const RIGHT_CHANNEL   = 2
Const EightBitMidpoint = 127         ; 8bit samples range from 0-255
                                    ; use this to move the sample data up.
                                    ; See CreateSinWav function for example
Global WavDataStartPos, WavDataSize

Dim echoData(8)

Type WavTypes
 Field WavTypeNames$,WavTypeID%,WavTypeCompany$
End Type

Function CreateWavBank(NumSamples,SampleRate=44100,BitsPerSample=16,Channels=1)


   BlockSize  = Channels*(BitsPerSample/8)
   DataLength = NumSamples*BlockSize

   ;Make sure DataLength is an even number
   If DataLength/2 <> DataLength/2.0 Then DataLength = DataLength + 1

   FileLength = Datalength + 44
   tbank       = CreateBank(FileLength)

   PokeStr$(tbank,"RIFF",0)
   PokeInt(tbank,4,FileLength-8)
   PokeStr$(tbank,"WAVE",8)
   PokeStr$(tbank,"fmt ",12)
   PokeInt(tbank,16,16)
   PokeShort(tbank,20,1)       ;1=PCM This is raw uncompressed wav file format
   PokeShort(tbank,22,Channels);1=mono,2=stereo
   PokeInt(tbank,24,SampleRate);eg 44100,22050,11025 sound cards only support some freq's.
   PokeInt(tbank,28,BlockSize*SampleRate)
   PokeShort(tbank,32,BlockSize)
   PokeShort(tbank,34,BitsPerSample)
   PokeStr$(tbank,"data",36)
   PokeInt(tbank,40,DataLength)
   WavDataStartPos = 44
   WavDataSize = NumSamples
;
  Return tbank

End Function



Function WriteWavBankData(tbank,Offset,WavData,ChannelToWrite=1,WavDataStartPos=0)

  BitsPerSample=GetWavBitsPerSample(tbank)
  Channels=GetWavChannelCount(tbank)
  If WavDataStartPos =0 Then WavDataStartPos=GetWavDataStartPos(tbank)
  blocksize = Channels*BitsPerSample/8
  Offset = (Offset*blocksize) + WavDataStartPos  + ((ChannelToWrite-1)*(BitsPerSample/8))
;debuglog "  Offset = "+(Offset*blocksize)+" + "+WavDataStartPos+" + "  + ((ChannelToWrite-1)*(BitsPerSample))
;stop
  If offset > ( BankSize(tbank)-blocksize) Then
    DebugLog Offset+" Offset to large"
    Return 1
  EndIf

  If BitsPerSample=16 Then

    If WavData > 32767  Then
      WavData =  32767
    Else If WavData < -32767 Then
      WavData = -32767
    End If

    PokeByte tbank,Offset,WavData And $FF
    PokeByte tbank,Offset+1,(WavData And $FF00) Shr 8

  Else If BitsPerSample=8 Then

    If WavData > 255  Then
      WavData =  255
    Else If WavData < 0 Then
      WavData = 0
    End If

    PokeByte tbank,Offset,WavData

  End If

  Return 1

End Function



Function ReadWavBankData(tbank,Offset,ChannelToRead=1,WavDataStartPos=0)

  BitsPerSample=GetWavBitsPerSample(tbank)
  Channels=GetWavChannelCount(tbank)
  If WavDataStartPos = 0 Then WavDataStartPos=GetWavDataStartPos(tbank)
  blocksize = Channels*BitsPerSample/8
  Offset = (Offset*blocksize) + ((ChannelToRead-1)*BitsPerSample/8)+ WavDataStartPos
  Bsize = BankSize(tbank)-blocksize

  If ChannelToRead  > Channels Then
    DebugLog "Channel "+ChannelToRead +" to large. Sample has "+ Channels + " Channels."
    Return
  EndIf

  If offset  > Bsize Then
    DebugLog Offset+" Offset to large. Banksize is "+ Bsize
    Return
  EndIf

  If BitsPerSample=16 Then
      LoByte = PeekByte (tbank,Offset)
      HiByte = PeekByte (tbank,Offset+1)
      sign =   hibyte Shr 7
;      OutData =  LoByte + ((HiByte And 127)Shl 7)
      OutData =  LoByte +( (HiByte And 127)* 256) -(32768*sign)
;      debuglog "HI "+HiByte +" lo "+LoByte +" sign "+ sign  +"   XX "  +outdata
  ElseIf BitsPerSample=8 Then
     OutData = PeekByte (tbank,Offset)
  End If

  Return OutData

End Function



Function PlaySoundBank(TBank,flags=0)
  If tbank <> 0 Then
    flags=flags Or SND_MEMORY Or SND_ASYNC Or SND_NODEFAULT ; These flags set up playing samples from memory
    winmm_PlaySound(TBank,0,flags)
  Else
    Return 1
  End If
End Function


;for playing windows default sounds
Function WinPlaySound(TBank,flags=0)
  If tbank <> 0 Then
    flags=flags Or SND_SYNC; Or SND_NODEFAULT ; These flags set up playing samples from memory
    winmm_PlaySound(TBank,0,flags)
  Else
    Return 1
  End If
End Function



;you must do this to stop a looping sound and to clean up at end of program.
Function StopSoundBank()
    winmm_StopSound(0,0,SND_PURGE Or SND_NODEFAULT)
End Function



;-- These functions get info from the wav header

;-- Not all PCM wav files data starts at byte 44
;   So we need to use this function on some loaded samples if they are to be altered.
Function GetWavDataStartPos(tbank)
   DataStart=36 ; jump over header
   Dummy$=""
   TBanksize = BankSize(tbank)

   Repeat

      Dummy$ = Dummy$ + Chr$(PeekByte(tbank,DataStart))
      Dummy  = Lower(Right$(Dummy,4))
      If Dummy = "data" Then Exit
      DataStart = DataStart +1
      If DataStart >= TBanksize Then
        RuntimeError " Can Not Find Data In Wave File."
        Return
      End If

   Forever

   Return DataStart  +5
End Function


Function GetWavDataLength(tbank)
   Return PeekInt(tbank,GetWavDataStartPos(tbank)-4)
End Function

Function GetWavNumberOfSamples(tbank)
   Return GetWavDataLength(tbank)/GetWavBlocksize(tbank)
End Function


Function GetWavChannelCount(tbank)
   Return PeekShort(tbank,22)     ; Channel  (1=Mono ; 2=Stereo)
End Function

Function GetWavSampleRate(tbank)
   Return PeekInt(tbank,24)     ; Sample freq e.g. 44100KHz
End Function

Function GetWavBlocksize(tbank)
   Return PeekShort(tbank,32)  ; Data Blocksize
End Function

Function GetWavBitsPerSample(tbank)
   Return PeekShort(tbank,34)  ; Bits per sample
End Function

Function GetWavBytesPerSecond(tbank)
   Return PeekShort(tbank,28)  ; Bytes per second
End Function

Function GetWavSeconds#(tbank)  ; Sample length in seconds
   Return  GetWavDataLength(tbank)/Float(GetWavBytesPerSecond(tbank))
End Function

Function GetWavType(tbank)     ; Use this to see if a sample is
   Return PeekShort(tbank,20)  ; type 1 (PCM). Only these can be read/writen to.
End Function                   ; See create echo for example.


Function GetWavTypeName$(tbank,show_company=True) ;Returns Wav type name
   If First WavTypes = Null Then ReadWavTypes()
   WavType = PeekShort(tbank,20)

   For  twtype.WavTypes = Each WavTypes

     If WavType = twtype\WavTypeID% Then
       If show_company Then
         Return  twtype\WavTypeNames$+" by " +twtype\WavTypeCompany$
       Else
         Return  twtype\WavTypeNames$
       End If
     End If

   Next

   Return
End Function


Function ReadWavTypes()
  Restore Wavtypedata

  For count = 1 To 55
   twtype.WavTypes = New WavTypes
   Read twtype\WavTypeNames$,twtype\WavTypeID%,twtype\WavTypeCompany$
  Next

End Function


;-- Use this to change the speed of samples
Function SetWavSampleRate(tbank,freq)
   PokeInt(tbank,24,freq)      ; Sample freq e.g. 41MHz
   Blocksize = GetWavBlocksize(tbank)
   PokeShort(tbank,28,freq*Blocksize)  ; Bytes per second

End Function


Function SetWavDataLength(tbank,NewSize%)
   DataStart = GetWavDataStartPos(tbank)
   PokeInt(tbank,DataStart-4,NewSize)
End Function


Function SetWavNumberOfSamples(tbank,NewSize%)
   DataStart = GetWavDataStartPos(tbank)
   Blocksize = GetWavBlocksize(tbank)
   PokeInt(tbank,DataStart-4,NewSize*Blocksize)
End Function

;--An example of how to alter sounds simply
Function CreateEcho(TBank,echotime#,Decay#=.9,Channel=0)
;Channel=0 both
;Channel=1 left
;Channel=2 right

  If GetWavType(tbank) <> 1 Then
    error=1
    DebugLog "File is not PCM type.  Only uncompressed files can be used."
    DebugLog "It is type "+  GetWavTypeName$(tbank)
    Return error
  End If

  DataStartPos   = GetWavDataStartPos(tbank)
  BitsPerSample  = GetWavBitsPerSample(tbank)
  Channels       = GetWavChannelCount(tbank)
  blocksize      = GetWavBlocksize(tbank)
  DataEndPos     = GetWavNumberOfSamples(tbank)
  SampleRate     = GetWavSampleRate(tbank)
  DelayTime#     = SampleRate*Float(echotime)/1000/blocksize
  If BitsPerSample = 8 Then EightBit = EightBitMidpoint

  For counter = DelayTime To  DataEndPos-1

    If channel <> ALL_CHANNELS Then

      ;read sample info
      DataIn = ReadWavBankData(TBank,counter,WavDataStartPos)
      ;read second sample (the echo)
      echoDat = ReadWavBankData(TBank,(counter-delaytime),Channel)*Decay#
      ;write the two samples added together back into wav bank
      WriteWavBankData(TBank,counter,( DataIn + EchoDat-EightBit),Channel)

    Else
      ;this does the same for all channels
      For channelcount = 1 To channels
        DataIn = ReadWavBankData(TBank,counter)
        echoData(channelcount) = ReadWavBankData(TBank,(counter-echotime),Channelcount)
        WriteWavBankData(TBank,counter,( DataIn + EchoData(channelcount)-EightBit),Channelcount)
      Next

    End If
  Next

End Function


Function AddWaveBanks(TBank1,Tbank2,Channel=0,LoopWave2=True)
;Channel=0 both
;Channel=1 left
;Channel=2 right

  If GetWavType(tbank1) <> 1 Then
    error=1
    DebugLog "File is not PCM type.  Only uncompressed files can be used."
    DebugLog "It is type "+  GetWavTypeName$(tbank1)
    Return error
  End If

  If GetWavType(tbank2) <> 1 Then
    error=1
    DebugLog "Second File is not PCM type.  Only uncompressed files can be used."
    DebugLog "It is type "+  GetWavTypeName$(tbank2)
    Return error
  End If

;  DataStartPos1  = GetWavDataStartPos(tbank1)
  BitsPerSample1 = GetWavBitsPerSample(tbank1)
  BitsPerSample2 = GetWavBitsPerSample(tbank2)
  Channels1      = GetWavChannelCount(tbank1)
  Channels2      = GetWavChannelCount(tbank2)
;  blocksize1     = GetWavBlocksize(tbank1)
  WavDataStartPos  = GetWavDataStartPos(tbank1)
  WavDataStartPos2 = GetWavDataStartPos(tbank2)
  DataEndPos1    = GetWavNumberOfSamples(tbank1) -1
  DataEndPos2    = GetWavNumberOfSamples(tbank2) -1
  If BitsPerSample1 = 8 Then
    EightBit = EightBitMidpoint
  Else
    EightBit = 0
  End If

  If BitsPerSample1 <> BitsPerSample2 Then
    error=2
    DebugLog "Samples must be same bits per sample size."
    DebugLog "These are " +BitsPerSample1+"bit and "+BitsPerSample2+"bit."
    Return error
  End If

  If channel > Channels2 Then
    error=3
    DebugLog "You have selected a channel that does not exist."
    DebugLog "You asked for channel " +Channel+" and sample has "+Channels1+" channels."
    Return error
  End If

  For counter = 0 To  DataEndPos1
    counter2 = counter2 + 1
;    debuglog "counter2 "+counter2
    If counter2 >= DataEndPos2 Then
      If loopWave2 Then
        counter2 = 0
      Else
        Return
      End If
    End If

    If channel <> ALL_CHANNELS Then

      ;read sample info
      DataIn1 = ReadWavBankData(TBank1,counter,channel,WavDataStartPos)
      ;read second sample
    ;  DataIn2 = ReadWavBankData(TBank2,counter2,channel,WavDataStartPos2)
      ;write the two samples added together back into wav bank
      DataOut = DataIn1 + DataIn2 - EightBit
      WriteWavBankData(TBank1,counter, DataOut ,Channel,WavDataStartPos)
    Else
      ;this does the same for all channels
      For channelcount = 1 To channels1

        DataIn1 = ReadWavBankData(TBank1,counter,channelcount,WavDataStartPos)

        ;read second sample
        If channelcount <= channels2 Then
          channelcount2 = channelcount
        Else
          channelcount2 = channels2
        End If

        DataIn2 = ReadWavBankData(TBank2,counter2,channelcount2,WavDataStartPos2)

        ;write the two samples added together back into wav bank
        WriteWavBankData(TBank1,counter, DataIn1 + DataIn2 - EightBit ,channelcount,WavDataStartPos)
      Next

    End If
  Next

End Function



Function AddNoise(TBank,Amount#=100,Channel=0)
;Channel=0 both
;Channel=1 left
;Channel=2 right

  If GetWavType(tbank) <> 1 Then
    error=1
    DebugLog "File is not PCM type.  Only uncompressed files can be used."
    DebugLog "It is type "+  GetWavTypeName$(tbank)
    Return error
  End If

  DataStartPos   = GetWavDataStartPos(tbank)
  BitsPerSample  = GetWavBitsPerSample(tbank)
  Channels       = GetWavChannelCount(tbank)
  blocksize      = GetWavBlocksize(tbank)
  DataEndPos     = GetWavNumberOfSamples(tbank)
  amount = amount /2.0

  For counter = 0 To  DataEndPos-1

    If channel <> ALL_CHANNELS Then

      ;read sample info
      DataIn = ReadWavBankData(TBank,counter) + Rnd(-amount,amount)

      ;write the new sample back into wav bank
      WriteWavBankData(TBank,counter,DataIn,Channel)

    Else
      ;this does the same for all channels
      For channelcount = 1 To channels
        DataIn = ReadWavBankData(TBank,counter) + Rnd(-amount,amount)
        WriteWavBankData(TBank,counter,DataIn,Channelcount)
      Next

    End If
  Next

End Function


;--After you create a wav bank you can use this to create a sin wave
Function CreateSinWav(tbank,Freq#=5000,Amp#,Channel=0)
;Channel=0 both
;Channel=1 left
;Channel=2 right

;  DataStartPos   = GetWavDataStartPos(tbank)
  BitsPerSample  = GetWavBitsPerSample(tbank)
  Channels       = GetWavChannelCount(tbank)
  blocksize      = Channels*BitsPerSample/8
  Freq           = Freq /1000 ; millisec Hz
;  DataEndPos     = GetWavNumberOfSamples(tbank)-1
  DataEndPos     = (BankSize(tbank)-44)/blocksize - 4
  If BitsPerSample = 8 Then EightBit = EightBitMidpoint

  For counter=0 To  DataEndPos
    oldval = val
    val = (Sin(counter*Freq)*amp) + EightBit

    If (oldval < 0) And (val => 0) Then LastZeroPoint = counter

    If channel <> 0 Then
      WriteWavBankData(tbank,counter,val,Channel)
    Else

      For channelcount = 1 To channels
        WriteWavBankData(tbank,counter,val,Channelcount)
      Next

    End If
  Next

  If LastZeroPoint > 0 Then SetWavNumberOfSamples(tbank,LastZeroPoint)

End Function

;--After you create a wav bank you can use this to create a square wave
Function CreateSqrWav(tbank,Freq#=5000,Amp#,Channel=1)
;Channel=0 both
;Channel=1 left
;Channel=2 right

;  DataStartPos   = GetWavDataStartPos(tbank)
  BitsPerSample  = GetWavBitsPerSample(tbank)
  Channels       = GetWavChannelCount(tbank)
  blocksize      = Channels*BitsPerSample/8
  Freq           = Freq /1000 ; millisec Hz
  DataEndPos     = GetWavNumberOfSamples(tbank)-1
  If BitsPerSample = 8 Then EightBit = EightBitMidpoint

  For counter=0 To  DataEndPos
    If channel <> 0 Then
      WriteWavBankData(tbank,counter,(Sgn(Sin(counter)*Freq)*amp+ EightBit) ,Channel)
    Else

      For channelcount = 1 To channels
        WriteWavBankData(tbank,counter,(Sgn(Sin(counter)*Freq)*amp),Channelcount)
      Next

    End If
  Next
End Function


Function CreateSinSweep(tbank,StartFreq#=5000,EndFreq#=25000,Amp#,Channel=0)
;Channel=0 both
;Channel=1 left
;Channel=2 right

;  DataStartPos   = GetWavDataStartPos(tbank)
  BitsPerSample  = GetWavBitsPerSample(tbank)
  Channels       = GetWavChannelCount(tbank)
  blocksize      = Channels*BitsPerSample/8
  DataEndPos     = GetWavNumberOfSamples(tbank)-1
  Freq#           = StartFreq /1000 ; millisec Hz
  Freqinc#       = ((EndFreq/1000)-freq)/Float(DataEndPos)/2
  If BitsPerSample = 8 Then EightBit = EightBitMidpoint

  For counter=0 To  DataEndPos

    val=(Sin(counter*Freq)*amp) + EightBit
    freq = freq + Freqinc
;    debuglog freq +" Freqinc# "+Freqinc#
    If channel <> 0 Then
      WriteWavBankData(tbank,counter,val,Channel)
    Else

      For channelcount = 1 To channels
        WriteWavBankData(tbank,counter,val,Channelcount)
      Next

    End If
  Next
End Function




;-- Bank routines

Function PeekStr$(Tbank,Size=64,Offset=0)

  Local NewStr$ = ""
  For count = offset To (offset+size-1)
    newchr = PeekByte(Tbank,count)
    If newchr = 0 Then Exit
    newstr$ = newstr$ + Chr$(newchr)
  Next
  Return Newstr$

End Function



Function PokeStr$(Tbank,IN_Str$,Offset=0)

  size=Len(IN_Str$)
  For count = offset To (offset+size-1)
    PokeByte(Tbank,count,Asc(Mid$(IN_Str$,count-offset+1,1)))
  Next

End Function



; This saves a bank to a file
Function BankToFile(InBank,FileName$)

  outfile=WriteFile(FileName$)

  If outfile = 0 Then
     DebugLog("Can not use file. Maybe "+FileName$+" already inuse.")
     Return 1
  End If

  WriteBytes InBank,outfile,0,BankSize(InBank)

  CloseFile outfile

End Function


;-- This loads Any file into a bank if it is allowed to be opened.
Function BankFromFile(FileName$)

  infile=ReadFile(FileName$)

  If infile = 0 Then
     DebugLog("Can not use file Maybe "+FileName$+" already inuse.")
     Return 1
  End If

  filesiz = FileSize(FileName$)
  OutBank = CreateBank(filesiz)
  ReadBytes outBank,Infile,0,FileSiz

  CloseFile infile

  Return Outbank

End Function

.Wavtypedata
Data  "Unknown",$0000,""
Data  "PCM",$0001,"Microsoft Corporation"
Data  "Adpcm",$0002,"Microsoft Corporation"
Data  "ieee_Float",$0003,"Microsoft Corporation"
Data  "Vselp",$0004,"Compaq Computer Corp."
Data  "Ibm_Cvsd",$0005,"IBM Corporation"
Data  "Alaw",$0006,"Microsoft Corporation"
Data  "Mulaw",$0007,"Microsoft Corporation"
Data  "Dts",$0008,"Microsoft Corporation"
Data  "Oki_Adpcm",$0010,"OKI"
Data  "Dvi_Adpcm",$0011,"Intel Corporation"
Data  "Ima_Adpcm",$0012," Intel Corporation"
Data  "Mediaspace_Adpcm",$0012,"Videologic"
Data  "Sierra_Adpcm",$0013,"Sierra Semiconductor Corp"
Data  "G723_Adpcm",$0014,"Antex Electronics Corporation"
Data  "Digistd",$0015,"DSP Solutions, Inc."
Data  "Digifix",$0016,"DSP Solutions, Inc."
Data  "Dialogic_Oki_Adpcm",$0017,"Dialogic Corporation"
Data  "Mediavision_Adpcm",$0018,"Media Vision, Inc."
Data  "Cu_Codec",$0019,"Hewlett-Packard Company"
Data  "Yamaha_Adpcm",$0020,"Yamaha Corporation of America"
Data  "Sonarc",$0021,"Speech Compression"
Data  "Dspgroup_Truespeech",$0022,"DSP Group, Inc"
Data  "Echosc1",$0023,"Echo Speech Corporation"
Data  "Audiofile_Af36",$0024,"Virtual Music, Inc."
Data  "Aptx",$0025,"Audio Processing Technology"
Data  "Audiofile_Af10",$0026,"Virtual Music, Inc."
Data  "Prosody_1612",$0027,"Aculab plc"
Data  "Lrc",$0028,"Merging Technologies S.A."
Data  "Dolby_Ac2",$0030,"Dolby Laboratories"
Data  "Gsm610",$0031,"Microsoft Corporation"
Data  "Msnaudio",$0032,"Microsoft Corporation"
Data  "Antex_Adpcme",$0033,"Antex Electronics Corporation"
Data  "Control_Res_Vqlpc",$0034,"Control Resources Limited"
Data  "Digireal",$0035,"DSP Solutions, Inc."
Data  "Digiadpcm",$0036,"DSP Solutions, Inc."
Data  "Control_Res_Cr10",$0037,"Control Resources Limited"
Data  "Nms_Vbxadpcm",$0038,"Natural MicroSystems"
Data  "Cs_Imaadpcm",$0039,"Crystal Semiconductor IMA ADPCM"
Data  "Echosc3",$003A,"Echo Speech Corporation"
Data  "Rockwell_Adpcm",$003B,"Rockwell International"
Data  "Rockwell_Digitalk",$003C,"Rockwell International"
Data  "Xebec",$003D,"Xebec Multimedia Solutions Limited"
Data  "G721_Adpcm",$0040,"Antex Electronics Corporation"
Data  "G728_Celp",$0041,"Antex Electronics Corporation"
Data  "Msg723",$0042,"Microsoft Corporation"
Data  "Mpeg",$0050,"Microsoft Corporation"
Data  "Rt24",$0052,"InSoft, Inc."
Data  "Pac",$0053,"InSoft, Inc."
Data  "Mpeglayer3",$0055,"ISO/MPEG Layer3 Format Tag"
Data  "Lucent_G723",$0059,"Lucent Technologies"
Data  "Cirrus",$0060,"Cirrus Logic"
Data  "Espcm",$0061,"ESS Technology"
Data  "Voxware",$0062,"Voxware Inc"
Data  "Canopus_Atrac",$0063,"Canopus, co., Ltd."
Data  "G726_Adpcm",$0064,"APICOM"
Data  "G722_Adpcm",$0065,"APICOM"
Data  "Dsat_Display",$0067,"Microsoft Corporation"
Data  "Voxware_Byte_Aligned",$0069,"Voxware Inc"
Data  "Voxware_Ac8",$0070,"Voxware Inc"
Data  "Voxware_Ac10",$0071,"Voxware Inc"
Data  "Voxware_Ac16",$0072,"Voxware Inc"
Data  "Voxware_Ac20",$0073,"Voxware Inc"
Data  "Voxware_Rt24",$0074,"Voxware Inc"
Data  "Voxware_Rt29",$0075,"Voxware Inc"
Data  "Voxware_Rt29hw",$0076,"Voxware Inc"
Data  "Voxware_Vr12",$0077,"Voxware Inc"
Data  "Voxware_Vr18",$0078,"Voxware Inc"
Data  "Voxware_Tq40",$0079,"Voxware Inc"
Data  "Softsound",$0080,"Softsound, Ltd."
Data  "Voxware_Tq60",$0081,"Voxware Inc"
Data  "Msrt24",$0082,"Microsoft Corporation"
Data  "G729a",$0083,"AT&T Labs, Inc."
Data  "Mvi_Mvi2",$0084,"Motion Pixels"
Data  "Df_G726",$0085,"DataFusion Systems (Pty) (Ltd)"
Data  "Df_Gsm610",$0086,"DataFusion Systems (Pty) (Ltd)"
Data  "Isiaudio",$0088,"Iterated Systems, Inc."
Data  "Onlive",$0089,"OnLive! Technologies, Inc."
Data  "Sbc24",$0091,"Siemens Business Communications Sys"
Data  "Dolby_Ac3_Spdif",$0092,"Sonic Foundry"
Data  "Mediasonic_G723",$0093,"MediaSonic"
Data  "Prosody_8kbps",$0094,"Aculab plc"
Data  "Zyxel_Adpcm",$0097,"ZyXEL Communications, Inc."
Data  "Philips_Lpcbb",$0098,"Philips Speech Processing"
Data  "Packed",$0099,"Studer Professional Audio AG"
Data  "Malden_Phonytalk",$00A0,"Malden Electronics Ltd."
Data  "Rhetorex_Adpcm",$0100,"Rhetorex Inc."
Data  "Irat",$0101,"BeCubed Software Inc."
Data  "Vivo_G723",$0111,"Vivo Software"
Data  "Vivo_Siren",$0112,"Vivo Software"
Data  "Digital_G723",$0123,"Digital Equipment Corporation"
Data  "Sanyo_Ld_Adpcm",$0125,"Sanyo Electric Co., Ltd."
Data  "Siprolab_Aceplnet",$0130,"Sipro Lab Telecom Inc."
Data  "Siprolab_Acelp4800",$0131,"Sipro Lab Telecom Inc."
Data  "Siprolab_Acelp8v3",$0132,"Sipro Lab Telecom Inc."
Data  "Siprolab_G729",$0133,"Sipro Lab Telecom Inc."
Data  "Siprolab_G729a",$0134,"Sipro Lab Telecom Inc."
Data  "Siprolab_Kelvin",$0135,"Sipro Lab Telecom Inc."
Data  "G726adpcm",$0140,"Dictaphone Corporation"
Data  "Qualcomm_Purevoice",$0150,"Qualcomm, Inc."
Data  "Qualcomm_Halfrate",$0151,"Qualcomm, Inc."
Data  "Tubgsm",$0155,"Ring Zero Systems, Inc."
Data  "Msaudio1",$0160,"Microsoft Corporation"
Data  "Creative_Adpcm",$0200,"Creative Labs, Inc"
Data  "Creative_Fastspeech8",$0202,"Creative Labs, Inc"
Data  "Creative_Fastspeech10",$0203,"Creative Labs, Inc"
Data  "Uher_Adpcm",$0210,"UHER informatic GmbH"
Data  "Quarterdeck",$0220,"Quarterdeck Corporation"
Data  "Ilink_Vc",$0230,"I-link Worldwide"
Data  "Raw_Sport",$0240,"Aureal Semiconductor"
Data  "Ipi_Hsx",$0250,"Interactive Products, Inc."
Data  "Ipi_Rpelp",$0251,"Interactive Products, Inc."
Data  "Cs2",$0260,"Consistent Software"
Data  "Sony_Scx",$0270,"Sony Corp."
Data  "Fm_Towns_Snd",$0300,"Fujitsu Corp."
Data  "Btv_Digital",$0400,"Brooktree Corporation"
Data  "Qdesign_Music",$0450,"QDesign Corporation"
Data  "Vme_Vmpcm",$0680,"AT&T Labs, Inc."
Data  "Tpc",$0681,"AT&T Labs, Inc."
Data  "Oligsm",$1000,"Ing C. Olivetti & C., S.p.A."
Data  "Oliadpcm",$1001,"Ing C. Olivetti & C., S.p.A."
Data  "Olicelp",$1002,"Ing C. Olivetti & C., S.p.A."
Data  "Olisbc",$1003,"Ing C. Olivetti & C., S.p.A."
Data  "Oliopr",$1004,"Ing C. Olivetti & C., S.p.A."
Data  "Lh_Codec",$1100,"Lernout & Hauspie"
Data  "Norris",$1400,"Norris Communications, Inc."
Data  "Soundspace_Musicompress",$1500,"AT&T Labs, Inc."
Data  "Dvm",$2000,"FAST Multimedia AG"
