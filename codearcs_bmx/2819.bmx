; ID: 2819
; Author: BlitzSupport
; Date: 2011-01-30 14:55:39
; Title: Read contents of online zip file
; Description: Prints out the contents of an online zip file

' BMX/online port of...

' readzip.bb
' by Mike Carroll, 20040418
' www.iol.ie/~carrollm

' Need to explore the contents of a zip file? You do? Then you're in luck! With 
' Unky Mike's handy Zip File reader, your Blitz Basic program need never again
' suffer from zip-file-contents-ignorance!
'
' Note: this function DOES NOT extract the contents of a zip file; it just tells
' you what's in there.
'
' The routine was written using Unky Mike's MIUAYGA principle ("Making It Up As"
' You Go Along"); it works fine on every zip file I've tried, but it's possible"
' that there are files out there for which it won't work...
'
' The accompanying functions are as follows:
'
' FileDateToStr(fd) & FileTimeToStr(ft)
' Turns the filedate and filetime values into human-readable formats. Filedates
' and filetimes are squashed into two-byte integers in the zip file (in the same
' format as DOS used to use). 
'
' Pad0(i, w)
' Pads a number with leading zeroes. In this instance, the function is used only
' for displaying the extracted dates and times, but please feel free to use it
' anywhere you see fit.
'
' Del$(st$, pos, num)
' Deletes the specified number of characters from the string, starting at the
' specified position. Not used in this case, but I find it really handy so I
' decided to give it away free!
' 
' Ins$(st1$, st2$, pos)
' Inserts st2$ into st1$ at the specified position. Also not used in this case, 
' but it's also really handy and anyway the Del$ function absolutely refused to 
' go without it.
' 

file$ = "http://www.blitzbasic.com/gnet/gnet_v1.zip"

ReadDataZipfile file

End

Function OnlineSeekAhead (s:TStream, offset)
	For bytes = 0 Until offset
		ReadByte s
	Next
End Function

Function ReadDataZipfile(filename$)

' Note: there's not much done in the way of checking: the function returns 1 if the file exists, 0 if it doesn't

	If Lower (Left (filename, 7)) <> "http://" Then Return
	
	filename = "http::" + Right (filename, Len (filename) - 7)
	
	filein:TStream = LittleEndianStream (ReadStream (filename$))

	If filein <> Null Then ' the file exists
		
		Try
		
		While (Not Eof(filein)) 
		
			' read the header signature to determine whether this is a file or a central directory record
			headersig = ReadInt(filein)
			
			If headersig = $04034b50 Then ' this is a file  (0x04034b50) = 67324752
				version = ReadByte(filein) + 256*(ReadByte(filein))
				generalpurpose = ReadByte(filein) + 256*(ReadByte(filein))
				compmethod = ReadByte(filein) + 256*(ReadByte(filein))
				modtime = ReadByte(filein) + (256 * ReadByte(filein))
				moddate = ReadByte(filein) + (256 * ReadByte(filein))
				crc32 = ReadInt(filein)
				compsize = ReadInt(filein)
				uncompsize = ReadInt(filein)
				fnlen = ReadByte(filein) + 256*(ReadByte(filein))
				extrafieldlen = ReadByte(filein) + 256*(ReadByte(filein))
		
				fn$ = ""
				For i = 1 To fnlen
					fn = fn + Chr(ReadByte(filein))
				Next
			
				' skip file data and extra field data
			'	SeekStream filein, StreamPos(filein)+compsize+extrafieldlen
				
				OnlineSeekAhead filein, compsize+extrafieldlen

				If generalpurpose & 8 Then
					' If generalpurpose bit 3 is set, then CRC, compsize and uncompsize are incorrect in the header...
					crc32 = ReadByte(filein)+ReadByte(filein)+ReadByte(filein)+ReadByte(filein)
					compsize = ReadInt(filein)
					uncompsize = ReadInt(filein)
				EndIf

				count = count + 1
				Print count+"   "+fn$+"  "+uncompsize+"  "+compsize+"  "+FileDateToStr(moddate)+"  "+FileTimeToStr(modtime)
			EndIf
		
			If headersig = $02014b50 Then' this is a central directory record  (0x02014b50) = 33639248
				versionmadeby = ReadByte(filein) + 256*(ReadByte(filein))
				version = ReadByte(filein) + 256*(ReadByte(filein))
				generalpurpose = ReadByte(filein) + 256*(ReadByte(filein))
				compmethod = ReadByte(filein) + 256*(ReadByte(filein))
				modtime = ReadByte(filein) + (256 * ReadByte(filein))
				moddate = ReadByte(filein) + (256 * ReadByte(filein))
				crc32 = ReadInt(filein)
				compsize = ReadInt(filein)
				uncompsize = ReadInt(filein)
				fnlen = ReadByte(filein) + 256*(ReadByte(filein))
				extrafieldlen = ReadByte(filein) + 256*(ReadByte(filein))
				commentlen = ReadByte(filein) + 256*(ReadByte(filein))
				disknumberstart = ReadByte(filein) + 256*(ReadByte(filein))
				internalfileattributes = ReadByte(filein) + 256*(ReadByte(filein))
				externalfileattributes = ReadInt(filein)
				relativeoffsetoflocalheader = ReadInt(filein)
					
				' skip file data and extra field data
				'SeekStream filein, StreamPos(filein)+compsize+extrafieldlen
				OnlineSeekAhead filein, compsize+extrafieldlen

				endofcentraldirsignature     = ReadInt(filein) ' (0x06054b50) = 101010256
				numberofthisdisk             = ReadByte(filein) + 256*(ReadByte(filein))
				numberofdiskwithstart		 = ReadByte(filein) + 256*(ReadByte(filein))
				totalentriesincentraldirondisk = ReadByte(filein) + 256*(ReadByte(filein))
				totalentriesincentraldir       = ReadByte(filein) + 256*(ReadByte(filein))
				sizeofthecentraldir   = ReadInt(filein)
				offsetofcentraldir    = ReadInt(filein)
				commentlen        = ReadByte(filein) + 256*(ReadByte(filein))
				comment$ = ""
				For i = 1 To commentlen         
					comment = comment + Chr(ReadByte(filein))
				Next
			EndIf
			
		Wend
		
		Catch ReadFail:Object
			DebugLog "Read error in " + filename
			
		End Try
		
		CloseFile(filein)
		Return 1

	Else ' the file doesn't exist, so return 0
		Return 0
	EndIf
End Function' ReadZipfile



Function FileDateToStr$(fd)
' filedate = ((year - 1980) * 512) + (month * 32) + day
' Returns "yyyy/mm/dd"
	Local year, month, day
	year = (fd / 512)+1980
	month = (fd Mod 512) / 32
	day = (fd Mod 32) 
	Return year+"/"+ Pad0(month, 2) + "/"+ Pad0(day, 2)
End Function' FileDateToStr


Function FileTimeToStr$(ft)
' filetime = (hour * 2048) + (min * 32) + (sec / 2)
' Returns "hh:mm:ss"
	Local hour, fmin, sec
	hour = (ft / 2048)
	fmin = (ft Mod 2048) / 32
	sec = (ft Mod 32) * 2
	Return Pad0(hour, 2)+":"+Pad0(fmin, 2)+":"+Pad0(sec, 2)
End Function' FileTimeToStr


Function Pad0$(i, w)
' Uses leading zeros to pad a number to the required length
	Local tst$
	tst$ = i
	While Len(tst) < w
		tst = "0"+tst
	Wend
	Return tst
End Function' Pad0


Function Del$(st$, pos, num)
' Deletes 'num' characters from the string, starting at the position specified by 'pos'
	Local tst$
	tst$ = Left$(st, pos-1)
	tst2$ = Right$(st, Len(st)-(pos+num-1))
	Return tst$ + tst2$
End Function


Function Ins$(st1$, st2$, pos)
' Inserts st2 into st1 at the position specified by 'pos'
	Local tst$
	tst$ = Left$(st1, pos-1)
	tst2$ = Right$(st1, Len(st1)-(pos-1))
	Return tst+st2+tst2
End Function
