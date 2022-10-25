; ID: 2191
; Author: Nebula
; Date: 2008-01-20 12:14:19
; Title: Xm file reader (b+)
; Description: Read display XM file (mod) commands

; row : (find) 	If FileType(filename$) <> 1 Then filename$ = "neverend.xm" 

;
; XM - Reader
;
;

Const numsongs = 0

Dim Binlookup$(255)

Dim Notes$(127)

Dim pctable$(29,1) ; binary<>hex patterncompression table

Dim XMtags(128)
Dim xmheader(1324)
Dim xmfile(0,999999) ;
Dim xmfilesize(0)
Dim pattern(numsongs,65,65,256,4) 	; 256 patterns max, with 256 tracks each max, and each track with
							; 256 rows max with each 5 tags for instruments and commands

Global Headerid$ = "Extended module: "	;0000h;0               ; 17 char   ID="Extended module: "
Global Modulename$ = "No name"        	;0011h;17              ;20 char   Module name, padded with zeroes
Global ModuleID = 26					;0025h;37              ; 1 char   ID=01Ah
Global Trackername$ = "No name"			;0026h;38              ;20 char   Tracker name
Global Trackerrevision = 0				;003Ah;58              ; 1 word   Tracker revision number, hi-byte is major version
Global Headersize = 0					;003Ch;60              ; 1 dword  Header size
Global Songlength = 0					;0040h;64              ; 1 word   Song length in patterns
Global Restartpos = 0					;0042h;66              ; 1 word   Restart position
Global Numchannels = 0					;0044h;68              ; 1 word   Number of channels
Global Numpatterns = 0					;0046h;70              ; 1 word   Number of patterns (< 256)
Global PatID$ = "PAT"					;                      ;          ="PAT"
Global Numinstruments = 0				;0048h;72              ; 1 word   Number of instruments (<128)
Global Freqtable = 0					;004Ah;74              ; 1 word   Flags :
										;                      ;          0 - Linear frequency table / Amiga freq. table
Global Deftempo = 0						;004Ch;76              ; 1 word   Default tempo
Global DefBPM = 0						;004Eh;78              ; 1 word   Default BPM
										;0050h;80              ;256 byte  Pattern order table
Global filename$ = CommandLine$()


filename$ = validatefilename(filename$)

xmmakepatterncompressiontable()
makebinlookuptable()
makenotes()

loadxmfile(filename$)

;
Graphics 640,480,2
;
If Readxm(filename$) = False Then End
Readpattern(0,0)

x=450:y=0
For i=13 To 127-12
Text x,y,Notes$(i)
y=y+12
If y>460 Then y=0 : x=x+32
Next

x=0:y=0
Text 0+x  ,0  +y,XMgetmodulename$()
Text 0+x  ,20 +y,XMgettrackername$()
Text 0+x  ,40 +y,XMgetsonglength()
Text 0+x  ,60 +y,XMgetrestartpos()
Text 0+x  ,80 +y,XMgetnumchannels()
Text 0+x  ,100+y,XMgetnumpatterns()
Text 0+x  ,120+y,XMgetnuminstruments()
Text 0+x  ,140+y,XMgetdeftempo()
Text 0+x  ,160+y,XMgetdefBpm()
x = 200 : y = 0
Text 0+x  ,0  +y,"XMgetmodulename$()"
Text 0+x  ,20 +y,"XMgettrackername$()"
Text 0+x  ,40 +y,"XMgetsonglength()"
Text 0+x  ,60 +y,"XMgetrestartpos()"
Text 0+x  ,80 +y,"XMgetnumchannels()"
Text 0+x  ,100+y,"XMgetnumpatterns()"
Text 0+x  ,120+y,"XMgetnuminstruments()"
Text 0+x  ,140+y,"XMgetdeftempo()"
Text 0+x  ,160+y,"XMgetdefBpm()"
;


Flip
WaitKey
End


Function Readpattern(xfile,p)


patpos = xmfindpattern(xfile,p)
patsiz = xmgetpatternsize(xfile,p)
patcmp = xmgetpatcompression(xfile,p)
numchan = xmgetnumchannels()

;If Confirm ("Pattern #0 Offset : " + patpos + " Size : " + patsiz + " compressed " + patcmp) Then End


If getinteger(xmReadhexbyte(xfile,patpos)) > getinteger("80") Then
	;If Confirm("Reading Compressed patterns not in yet") Then Return
End If

For i=patpos To (patpos+patsiz)/3

a$ = xmreadhexbyte(xfile,i)
b = xmgetbintablepointer(a$)
c$ = pctable$(b,0)

;Notify a$ : End

;If Left(a$,1) = "1" Then
;pattern(xfile,0,chan,row,0) = xmreadhexbyte(xfile,i+1)
;End If
cnty = 1
If getnextpacked(1,a$) = 1 Then pattern(xfile,0,chan,row,0) = xmreadhexbyte(xfile,i+cnty) : cnty = cnty+1
If getnextpacked(2,a$) = 1 Then pattern(xfile,0,chan,row,1) = xmreadhexbyte(xfile,i+cnty) : cnty = cnty+1
If getnextpacked(3,a$) = 1 Then pattern(xfile,0,chan,row,2) = xmreadhexbyte(xfile,i+cnty) : cnty = cnty+1
If getnextpacked(4,a$) = 1 Then pattern(xfile,0,chan,row,3) = xmreadhexbyte(xfile,i+cnty) : cnty = cnty+1
If getnextpacked(5,a$) = 1 Then pattern(xfile,0,chan,row,4) = xmreadhexbyte(xfile,i+cnty) : cnty = cnty+1

;i = i + countstring(c$,"1")

chan = chan + 1
If chan > numchan Then chan = 0 : row = row + 1




Next

For x = 0 To 4
For y=0 To 32
;Text 0+x*30   ,y*12+200,"--"
Text 0+x*30   ,y*12+200,pattern(xfile,p,0,y,x)
Next
Next

End Function


Function getnextpacked(s,n$) ; Returns 1 or 0 for the next placement of data
For i=0 To 29
	If pctable(i,1) = n$ Then
		Return Int(Mid(pctable(i,0),s,1))
	End If
Next
End Function

Function XMReadHexByte(xmfil,offset)
	;DebugLog offset
	a$ = Right(Hex(xmfile(xmfil,offset)),2)
	Return a$
End Function

Function countstring(a$,ss$)
For i = 1 To Len(a$)
	If Mid(a$,i,1) = ss$ Then aap=aap+1
Next
Return aap
End Function

Function XMgetbintablepointer(a$) ; Return the place in the compression lookup table
	For i=0 To 29
		If a$ = pctable$(i,1) Then Return i
	Next
End Function


Function Readxm(filename$) ; LoadXm Library
	If validatexm(filename$) = False Then Return False
	loadxmheader(filename$)
	;If Readheader(filename$) = False Then Return
	;ReadInstheaders(filename$)
	Return True
End Function

Function validatexm(filename$) ; Load in the first 17 bytes and validate the id
	;
	f = ReadFile(filename$)
		For i=0 To 17-1
		 xmheader(i)= ReadByte(f)
		Next
	CloseFile(f)
	;
	If isxmheader() = True Then Return True Else Return False
	;
End Function

Function loadxmheader(filename$) ; Load the entire header into memory (324 bytes)
	;
	f = ReadFile(filename$)
		For i=0 To 324-1
		 xmheader(i)= ReadByte(f)
		Next
	CloseFile(f)
	;
	;If isxmheader() = True Then Return True Else Return False
	;
End Function

Function Readheader(filename$)

End Function

Function Readinstheaders(filename$)

End Function

Function isxmheader() ; Returns if the header in memory is a valid XM file
blah$ = "Extended module: "
brok = False
For i=0 To Len(blah$)-1
	If Chr(xmheader(i)) = Mid(blah$,i+1,1) Then brok = True	
Next
If brok = False Then Notify "not a xm id tag in this file" : Return False
Return True
End Function

Function loadxmfile(filename$,n = 0)
	If FileType(filename$) <> 1 Then Notify "Error" : End
	f = ReadFile(filename$)
		While Eof(f) = False
			xmfile(n,counter) = ReadByte(f)
			counter = counter + 1
		Wend
	CloseFile(f)
	xmfilesize(n) = counter-1
End Function


Function XMfindpattern(xfile,pt)
If pt > xmgetnumpatterns() Then Notify "findpattern out of bounds" : End
;
st = 336
Repeat
	stp = xmgetbyte(xfile,st,1)	
	st = st + stp - 2
	ps = xmgetbyte(xfile,xmfile(xfile,st),2)
	If cnt = pt Then Return st +2
	st = st + ps
	cnt = cnt + 1
Forever
Notify "pattern not found" : End
End Function

Function XMgetpatternsize(xfile,p)
a = xmfindpattern(xfile,p)
Return xmgetbyte(xfile,a-2,2)
End Function

Function XMGetpatcompression(xfile,p)
a = xmfindpattern(xfile,p)
If xmgetbyte(xfile,a-5,2) = 0 Then Return True
;If Confirm (xmgetbyte(xfile,a-5,2)) Then End
Return False
End Function

Function Makenotes()
cnt=0
For i=1 To 127
Select i
Case 1+12*cnt  : Notes$(i) = "C-"+cnt
Case 2+12*cnt  : Notes$(i) = "C#"+cnt
Case 3+12*cnt  : Notes$(i) = "D-"+cnt
Case 4+12*cnt  : Notes$(i) = "D#"+cnt
Case 5+12*cnt  : Notes$(i) = "C-"+cnt
Case 6+12*cnt  : Notes$(i) = "F-"+cnt
Case 7+12*cnt  : Notes$(i) = "F#"+cnt
Case 8+12*cnt  : Notes$(i) = "G-"+cnt
Case 9+12*cnt  : Notes$(i) = "G#"+cnt
Case 10+12*cnt : Notes$(i) = "A-"+cnt
Case 11+12*cnt : Notes$(i) = "A#"+cnt
Case 12+12*cnt : Notes$(i) = "B-"+cnt : cnt=cnt+1
End Select
Next
End Function

Function XMmakepatterncompressiontable() ; Make a table to decode/encode XM patterns
whzzup = 0
; Get the first section
frap = 0
For i=129 To 128+15
	a$ = 0
	For crapola = Len(Right(Str(Bin(i)),5)) To 2 Step -1
	a$ = a$ + Mid(Right(Str(Bin((-i))),5),crapola,1)	
	Next
	; a$ = hex , b$ = bin
	b$ = Right(Str(Hex(i+frap)),2); + " : " + a$
	pctable$(whzzup,0) = a$ ; The binary lookup
	pctable$(whzzup,1) = b$ ; The hex code
	whzzup = whzzup +1
	frap=frap+1
Next
;Get the second section
frap = 130+28 : y = 0
For i=129 To 128+30 Step 2
	a$ = ""
	cdus = Len(Str(Bin(i)))
	c$ = Str(Bin(i))
	d$ = Mid(c$,cdus-4,5)	
	a$ = d$	:d$=""
	For ptuh = Len(a$) To 1 Step -1
	d$ = d$ + Mid(a$,ptuh,1)
	Next
	a$=d$
	; a$ = hex , b$ = bin
	b$ = Right(Str(Hex((frap))),2); + " : " + a$
	pctable$(whzzup,0) = a$ ; The binary lookup
	pctable$(whzzup,1) = b$ ; The hex code
	whzzup = whzzup + 1	
	frap = frap - 2
	Return whzzup - 1
Next

End Function

Function XMgetmodulename$(offset = 17,Length=20)
;e$(offset=17,length = 20);20 char   Module name, padded with zeroes
	Modulename$ = getBytestring(offset,Length-1)
	Return Modulename$
End Function
Function XMGetmoduleID(offset=37,length = 1); 1 char   ID=01Ah
ModuleID = getbyte(offset,Length)
Return ModuleID
End Function
Function XMGettrackername$(offset=38,length = 20);20 char   Tracker name
	Trackername$ = getBytestring(offset,Length-1)
	Return trackername$
End Function
Function XMGetTrackerrevision(offset=58,length = 1); 1 word   Tracker revision number, hi-byte is major version
	Trackerrevision = getbyte(offset,length)
	Return trackerrevision
End Function
Function XMGetHeadersize(offset=60,length = 2); 1 dword  Header size
;Headersize = 
End Function
Function XMGetsonglength(offset=64,length = 1); 1 word   Song length in patterns
Songlength = getbyte(offset,Length)
Return Songlength
End Function
Function XMGetrestartpos(offset=66,length = 1); 1 word   Restart position
Restartpos =getbyte(offset,Length)
Return Restartpos
End Function
Function XMGetnumchannels(offset=68,length = 1); 1 word   Number of channels
Numchannels =getbyte(offset,Length)
Return Numchannels
End Function
Function XMGetnumpatterns(offset=70,length = 1); 1 word   Number of patterns (< 256) ="PAT"
Numpatterns =getbyte(offset,Length)
Return Numpatterns
End Function
Function XMGetNuminstruments(offset=72,length = 1); 1 word   Number of instruments (<128)
Numinstruments =getbyte(offset,Length)
Return Numinstruments
End Function
Function XMGetFreqtable(offset=74,length = 1); 1 word   Flags : 0 - Linear frequency table / Amiga freq. table
Freqtable =getbyte(offset,Length)
Return Freqtable
End Function
Function XMGetdeftempo(offset=76,length = 1); 1 word   Default tempo
Deftempo =getbyte(offset,Length)
Return Deftempo
End Function
Function XMGetDefBPM(offset=78,length = 1) ; 1 word   Default BPM
DefBPM =getbyte(offset,Length)
Return DefBPM
End Function

Function getinteger$(s$) ; Translates hex into integer	
	For i=0 To 255
		If Binlookup$(i) = s$ Then Return i
	Next	
	Return -1
End Function

Function makebinlookuptable()
	For i=0 To 255
		Binlookup$(i) = Right(Hex(i),2)
	Next
End Function
;
Function getbytestring$(offset,Length)
	a$ = ""
	For i=offset To offset+Length
		a$ = a$ + Chr(xmheader(i))
	Next
	Return a$
End Function
;
Function XMgetbyte(xmfil,offset,Length)
	a$ = ""
	For i=offset To offset + Length-1
		a$ = a$ + Right(Hex(xmfile(xmfil,i)),2)
	Next
	Return Hex2int(a$)
End Function
;
Function getbyte(offset,Length)
	a$ = ""
	For i=offset To offset + Length-1
		a$ = a$ + Right(Hex(xmheader(i)),2)
	Next
	Return Hex2int(a$)
End Function
;
.ProgFilefunctions
Function validatefilename$(filename$)
	If FileType(filename$) <> 1 Then filename$ = "neverend.xm" 
	If FileType(filename$) <> 1 Then Notify "No module found - Quiting" : End
	Return filename$
End Function

Function hex2int(hexRef$)
;   Taken from Colour Space library - V2.01, Nov 2002 
;
;	Author:		Ghost Dancer, Aurora-Soft
;	Website:	www.aurora-soft.co.uk
;	Contact:	colour@aurora-soft.co.uk
;-------------------------------------------------------------------
;Convert hex string to decimal integer
;
;Parameters:
;hexRef$	- hex string to convert (e.g. "$ffffff", or "ffffff")
;
;Return value:
;none
;-------------------------------------------------------------------
;
; Minor adjustement to take into acount the length of the hexstring
; By Nebula.
;
;
;

	If Left(hexRef, 1) = "$" Then hexRef = Right(hexRef, Len(hexRef) - 1)	;remove $ if present
	
	hexRef = Lower$(hexRef)
	hexNum = 0
	
	For n = Len(hexRef) To 1 Step -1
		thisNum = 0
		ascii = Asc(Mid(hexRef, n, 1))
		If ascii >= 48 And ascii <= 57 Then thisNum = ascii - 48
		If ascii >= 97 And ascii <= 122 Then thisNum = ascii - 97 + 10
		If thisNum >= 0 Then
			m = Len(Hexref$) - n + 1; take into account the length of the string--6 - n + 1
			mult = (16 ^ (m-1))
			hexNum = hexNum + (thisNum * mult)
		End If
	Next

	Return hexNum
End Function

.XMInfo
;
;--------M-XM--------------------------------
; The first Pattern starts at int offset 336 folowed by a byte that represents the space of
; the patternheader. Add this one up and get the pattern start.
; 336 + 4 is the patternpack type - 0 = packed
; 336 + 7 is the patternbytesize
; 336 + 5 is the Number of rows in the pattern
; Use the patterncompression lookup table to decode the pattern.
;
; Valid intruments range from $01(c-1)1 to $97(b8)int(121)
;
;The .XM files (Extended Module) are multichannel Mod files created by Triton's
;FastTracker ][. They feature up To 32 channels And different effects. FT 2 is
;a shareware program. After the initial .XM header follows the pattern Data,
;After the patterns follow the instruments.
;
;OFFSET              Count Type   Description
;0000h                  17 char   ID="Extended module: "
;0011h                  20 char   Module name, padded with zeroes
;0025h                   1 char   ID=01Ah
;0026h                  20 char   Tracker name
;003Ah                   1 word   Tracker revision number, hi-byte is major version
;003Ch                   1 dword  Header size
;0040h                   1 word   Song length in patterns
;0042h                   1 word   Restart position
;0044h                   1 word   Number of channels
;0046h                   1 word   Number of patterns (< 256)
;                                 ="PAT"
;0048h                   1 word   Number of instruments (<128)
;004Ah                   1 word   Flags :
;                                 0 - Linear frequency table / Amiga freq. table
;004Ch                   1 word   Default tempo
;004Eh                   1 word   Default BPM
;0050h                 256 byte   Pattern order table
;
;--- Pattern header
;The patterns are stored as ordinary Mod patterns, except that Each note is
;stored as 5 bytes:
;
;      ?      1   (byte) Note (0-71, 0 = C-0)
;     +1      1   (byte) Instrument (0-128)
;     +2      1   (byte) Volume column byte (see below)
;     +3      1   (byte) Effect Type
;     +4      1   (byte) Effect parameter
;
;A simle packing scheme is also adopted, so that the patterns do Not become TOO
;large: Since the MSB in the note value is never used, it is used For the
;compression.If the bit is set, Then the other bits are interpreted as follows:
;
;      bit 0 set: Note byte ollows
;          1 set: Instrument byte follows
;          2 set: Volume column byte follows
;          3 set: Effect byte follows
;          4 set: Effect Data byte follows
;
;OFFSET              Count Type   Description
;0000h                   1 dword  Length of pattern block/header ??
;0004h                   1 byte   Pattern pack Type
;0005h                   1 word   Number of rows in pattern (1..256)
;0007h                   1 word   Size of pattern Data
;                                 ="PSZ"
;                    "PSZ" byte   Pattern Data
;
;--- Instrument header
;Each instrument has one Or more sample headers following it.
;OFFSET              Count Type   Description
;0000h                   1 dword  Instrument block/header size
;0004h                  22 char   ASCII Instrument name, 0 padded ?
;001Ah                   1 byte   Instrument Type (always 0)
;001Bh                   1 word   Number of samples in instrument
;001Dh                   1 dword  Sample header size
;0021h                  96 byte   Sample numbers For all notes
;0081h                  48 byte   Points of volume envelope
;00C1h                  48 byte   Points of panning envelope
;0101h                   1 byte   Number of volume points
;0102h                   1 byte   Number of panning points
;0103h                   1 byte   Volume sustain point
;0104h                   1 byte   Volume loop start point
;0105h                   1 byte   Volume loop End point
;0106h                   1 byte   Panning sustain point
;0107h                   1 byte   Panning loop start point
;0108h                   1 byte   Panning loop End point
;0109h                   1 byte   Volume Type, bitmapped
;                                 0 - Volume on
;                                 1 - Sustain on
;                                 2 - Loop on
;010Ah                   1 byte   Panning Type, bitmapped
;                                 0 - Panning on
;                                 1 - Sustain on
;                                 2 - Loop on
;010Bh                   1 byte   Vibrato Type
;010Ch                   1 byte   Vibrato sweep
;010Dh                   1 byte   Vibrato depth
;010Eh                   1 byte   Vibrato rate
;010Fh                   1 word   Volume fadeout
;0111h                   1 word   Reserved
;
;--- Sample headers
;OFFSET              Count Type   Description
;0000h                   1 dword  Sample length
;                                 ="LEN"
;0004h                   1 dword  Sample loop start
;0008h                   1 dword  Sample loop length
;000Ch                   1 byte   Volume
;000Dh                   1 byte   Finetune For sample (-128..+127)
;                                 +-127 is one half tone
;000Eh                   1 byte   Sample Type, bitmapped
;                                 0,1 : Loop Type :
;                                        0 - no loop
;                                        1 - forward loop
;                                        2 - ping-pong loop
;                                        3 - reserved
;                                   4?: sample is 16-bit
;000Fh                   1 byte   Sample pan
;0010h                   1 byte   Relative note number (signed byte)
;                                 (-96..+95), 0 -> C-4 sounds as C-4
;0011h                   1 byte   Reserved
;0012h                  22 char   ASCII name of sample, 0 padded
;0013h               "LEN" byte   Sample Data. The sample Data is stored
;                                 as delta compressed Data like the ProTracker.
;
;EXTENSION:XM,Mod
;OCCURENCES:
;PROGRAMS:
;REFERENCE:
;SEE ALSO:Mod,S3M
;VALIDATION:
;
;
;
;
;
;
