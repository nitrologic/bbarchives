; ID: 1686
; Author: Jake L.
; Date: 2006-04-23 17:15:19
; Title: CRC32 checksum calculation
; Description: MrCredo's CRC32 code ported to BMax

superstrict

rem
bbdoc: CRC32 Checksum
endrem
module bt.crc32
moduleinfo "CRC32-Checksum calculation"
moduleinfo "ported to BMax by Jake@bittrap-software.de"
moduleinfo "Original code (BB) written by MrCredo"
moduleinfo "VERSION: 1.1"
moduleinfo "OS: Win32 / MacOS / Linux"
moduleinfo "HISTORY: 1.1 Changed Stream2CRC to buffered version (Credits: JeremytheJJ)"

import brl.stream

private
global table%[256]

local val%
	for local i%=0 to 255
		val=i
		for local j%=0 to 7
			if (val & $1)
				val=(val shr 1) ~ $EDB88320
			else
				val=(val shr 1)
			End If
		Next
		table[i]=val
	Next

	
public

rem
bbdoc:create checksum from String
returns: (Int) CRC32 
endrem	
Function String2CRC32%(s$)
	local crc%=$FFFFFFFF
	for local i%=0 to s.length-1
		crc=(crc shr 8) ~ table[s[i] ~ (crc and $FF)]
	next
	return (crc)
End Function

rem
bbdoc:create checksum from an open stream
returns: (Int) CRC32 
endrem	
Function Stream2CRC32%(stream:TStream,bufferSize:Int=$400000)
	local crc%=$FFFFFFFF
	Local buffPtr:Byte Ptr = MemAlloc(bufferSize)
	
	Repeat
    	Local bytesRead:Int = stream.Read(buffPtr, bufferSize)
        For Local b%=0 Until bytesRead
      		crc = (crc Shr 8) ~ table[buffPtr[b] ~ (crc & $FF)]
    	Next
  	Until stream.Eof()
  
    MemFree(buffPtr)
	return (crc)
End Function

rem
bbdoc:create checksum from url/file
returns: (Int) CRC32 
endrem	
Function URL2CRC32% (url$)
	local st:TStream=OpenStream (url,true,false)
	local crc%=Stream2CRC32(st)
	st.Close()
	return (crc)
End Function
