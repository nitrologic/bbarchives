; ID: 1818
; Author: markcw
; Date: 2006-09-19 01:58:17
; Title: Read wave and write to bb data file
; Description: For packing wave files in executable

;Read Wave Write Data, on 19/9/06

Graphics 640,480,0,2
SetBuffer BackBuffer()

fileout$="temp.bb"
filein$="yourname.wav" ;wave in

ok=ReadWaveWriteData(filein$,fileout$,12000) ;comprate

chnsnd=PlayMusic(filein$) ;play wave in

;Main loop
While Not KeyHit(1)
 Cls

 Text 0,0,"ok="+ok+" filein="+filein$+" fileout$="+fileout$

 Flip
Wend

;Functions
Function ReadWaveWriteData(filein$,fileout$,comprate)
 ;Read a wave file and write data to bb file
 ;filein$=wave file, fileout$=bb file, comprate=compression rate
 ;From PureBasic CodeArchiv, by Froggerprogger on 4/9/03

 Local rfile,datasize,pcm,channels,samplesps,avgbytesps,blockalign,bps
 Local mssize,wfile,time,side,value,lastms,mstime,x,integer

 If filein$="" Then Return False ;fail code
 rfile=ReadFile(filein$)
 If Not rfile Then Return False ;read fail

 ;RIFF Chunk, Resource Interchange File Format
 If ReadInt(rfile)<>MakeFourCC("R","I","F","F") ;dwChunkID, "RIFF"
  CloseFile rfile : Return False ;not riff file
 EndIf
 datasize=ReadInt(rfile) ;dwChunkSize, filesize-8
 If ReadInt(rfile)<>MakeFourCC("W","A","V","E") ;dwTypeID, "WAVE"
  CloseFile rfile : Return False ;not wave file
 EndIf

 ;Format Chunk, RIFF Subchunk
 If ReadInt(rfile)<>MakeFourCC("f","m","t"," ") ;dwChunkID, "fmt "
  CloseFile rfile : Return False ;not PCM format
 EndIf
 datasize=ReadInt(rfile) ;dwChunkSize, sizeof(PCMWAVEFORMAT)
 ;PCMWAVEFORMAT structure, uncompressed wave data
 pcm=ReadShort(rfile) ;wFormatTag, WAVE_FORMAT_PCM=1
 channels=ReadShort(rfile) ;wChannels, mono=1/stereo=2
 samplesps=ReadInt(rfile) ;dwSamplesPerSec, samplerate in hertz
 avgbytesps=ReadInt(rfile) ;dwAvgBytesPerSec
 blockalign=ReadShort(rfile) ;wBlockAlign, bytes per sample
 bps=ReadShort(rfile) ;wBitsPerSample, PCM=8/16

 ;Data Chunk, RIFF Subchunk
 SeekFile(rfile,20+datasize)
 If ReadInt(rfile)<>MakeFourCC("d","a","t","a") ;dwChunkID, "data"
  CloseFile rfile : Return False ;not PCM data
 EndIf
 datasize=ReadInt(rfile) ;dwChunkSize, sizeof(DataSamples)
 ;calculate compressed size, make sure value not too big
 mssize=(datasize*(comprate/1000))/((samplesps/1000)*blockalign)

 ;Write fileout.bb
 If fileout$="" Then Return False ;fail code
 wfile=WriteFile(fileout$)
 If Not wfile Then Return False ;write fail

 ;write title comment, wave data label and data command
 WriteStringAscii(wfile,";"+fileout$)
 WriteByte wfile,13 : WriteByte wfile,10 ;newline
 WriteByte wfile,13 : WriteByte wfile,10 ;newline
 WriteStringAscii(wfile,"."+Left(filein$,Len(filein$)-4))
 WriteStringAscii(wfile,"_"+Right(filein$,3))
 WriteByte wfile,13 : WriteByte wfile,10 ;newline
 WriteStringAscii(wfile,"Data ")
 WriteValueAscii(wfile,mssize) ;first value is compressed size

 ;Data Samples
 While time<datasize
  ;read value according to bits per sample
  For side=1 To channels
   If bps=8
    value=ReadByte(rfile) ;range 0..255
   Else
    value=ReadShort(rfile) ;range -32767..32767
    value=value/256 ;convert short to byte
    If value<128 Then value=value+128 Else value=value-128
   EndIf
  Next
  ;calculate compressed time interval, make sure value not too big
  lastms=mstime
  mstime=(time*(comprate/1000))/((samplesps/1000)*blockalign)
  If lastms<>mstime ;time reached, write integer
   If x=0 Then integer=integer Or value ;set bytes to integer
   If x=1 Then integer=integer Or (value Shl 8)
   If x=2 Then integer=integer Or (value Shl 16)
   If x=3 Then integer=integer Or (value Shl 24)
   If x=3 ;write integer, make sure not fourcc "end "
    If integer=MakeFourCC("e","n","d"," ") Then integer=integer+1
    WriteByte wfile,Asc(",")
    WriteValueAscii(wfile,integer)
   EndIf
   x=x+1 : If x>3 Then x=0 : integer=0 ;reset
  EndIf
  time=time+blockalign
 Wend
 If x>0 ;last integer not aligned
  WriteByte wfile,Asc(",")
  WriteValueAscii(wfile,integer)
 EndIf
 ;write end fourcc and end comment
 WriteByte wfile,Asc(",")
 WriteValueAscii(wfile,MakeFourCC("e","n","d"," ")) ;avoid out of data
 WriteByte wfile,13 : WriteByte wfile,10 ;newline
 WriteStringAscii(wfile,";end "+Left(filein$,Len(filein$)-4))
 WriteStringAscii(wfile,"_"+Right(filein$,3))

 CloseFile rfile
 CloseFile wfile
 Return True ;success code

End Function

Function MakeFourCC(c0$,c1$,c2$,c3$)

 Return (Asc(c0$)+(Asc(c1$) Shl 8)+(Asc(c2$) Shl 16)+(Asc(c3$) Shl 24))

End Function

Function WriteValueAscii(file,value)
 ;file=file handle, value=byte/short/integer

 Local ascii$,i,char$
 ascii$=Str(value)
 For i=1 To Len(ascii$)
  char$=Mid(ascii$,i,1)
  WriteByte(file,Asc(char$))
 Next

End Function

Function WriteStringAscii(file,ascii$)
 ;file=file handle, ascii$=ascii string

 Local i,char$
 For i=1 To Len(ascii$)
  char$=Mid(ascii$,i,1)
  WriteByte(file,Asc(char$))
 Next

End Function
