; ID: 1751
; Author: markcw
; Date: 2006-07-12 03:03:20
; Title: PeekPoke Module for B3D/B+
; Description: Allows direct memory access via Kernel32.dll

;PeekPoke Module for B3D/B+
;Author: markcw, edited 15 Mar 2008
;Allows direct memory access via Kernel32.dll
;Create a file in your userlibs folder called "peekpoke.decls"

;.lib "Kernel32.dll"
;
;;Win32 Decls, Memory Management Functions
;Api_RtlMoveToBank(Destination*,Source,Length):"RtlMoveMemory"
;Api_RtlMoveToMemory(Destination,Source*,Length):"RtlMoveMemory"
;;Api_RtlFillMemory(Destination,Length,Fill):"RtlFillMemory"
;;Api_RtlZeroMemory(Destination,Length):"RtlZeroMemory"
;
;;PeekPoke Module Decls
;PeekB%(address)
;PeekS%(address)
;PeekI%(address)
;PeekF#(address)
;PokeB(address,value)
;PokeS(address,value)
;PokeI(address,value)
;PokeF(address,value#)
;MemoryLog(address,ByteLength,filename$)

;This global has a long name to avoid a conflict
Global Kernel32PeekPokeBank=CreateBank(4) ;Bank for memory access

Function PeekB(address) ;PeekByte
 Api_RtlMoveToBank(Kernel32PeekPokeBank,address,1)
 Return PeekByte(Kernel32PeekPokeBank,0)
End Function

Function PeekS(address) ;PeekShort
 Api_RtlMoveToBank(Kernel32PeekPokeBank,address,2)
 Return PeekShort(Kernel32PeekPokeBank,0)
End Function

Function PeekI(address) ;PeekInteger
 Api_RtlMoveToBank(Kernel32PeekPokeBank,address,4)
 Return PeekInt(Kernel32PeekPokeBank,0)
End Function

Function PeekF#(address) ;PeekFloat
 Api_RtlMoveToBank(Kernel32PeekPokeBank,address,4)
 Return PeekFloat(Kernel32PeekPokeBank,0)
End Function

Function PokeB(address,value) ;PokeByte
 PokeByte Kernel32PeekPokeBank,0,value
 Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,1)
End Function

Function PokeS(address,value) ;PokeShort
 PokeShort Kernel32PeekPokeBank,0,value
 Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,2)
End Function

Function PokeI(address,value) ;PokeInteger
 PokeInt Kernel32PeekPokeBank,0,value
 Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,4)
End Function

Function PokeF(address,value#) ;PokeFloat
 PokeFloat Kernel32PeekPokeBank,0,value#
 Api_RtlMoveToMemory(address,Kernel32PeekPokeBank,4)
End Function

;MemoryLog Functions

Function MemoryLog(address,ByteLength,filename$="memorylog.txt")
 ;Writes a memory log text file, useful if you're searching for offsets
 ;address -> where to start from, ByteLength -> length to copy
 Local file,id,value,count,array$
 file=WriteFile(filename$)
 For id=0 To (ByteLength/4)-1 ;Write the line
  MemoryLogValue(file,address) : WriteByte(file,43) ;Write "address+"
  MemoryLogValue(file,id Shl 2) : WriteByte(file,32) ;Write "offset "
  WriteByte(file,123) : WriteByte(file,32) ;Write "{ "
  value=PeekI(address)
  MemoryLogValue(file,value) : WriteByte(file,32) ;Write "Int "
  WriteByte(file,58) : WriteByte(file,32) ;Write ": "
  For count=0 To 3 Step 2 ;2 shorts
   value=PeekS(address+count)
   MemoryLogValue(file,value) ;Write "Short"
   If count=0 Then WriteByte(file,44) ;Write ","
   If count=2 Then WriteByte(file,32) ;Write " " if last
  Next
  WriteByte(file,58) : WriteByte(file,32) ;Write ": "
  For count=0 To 3 ;4 bytes
   value=PeekB(address+count)
   MemoryLogValue(file,value) ;Write "Byte"
   If count<3 Then WriteByte(file,44) ;Write ","
   If count=3 Then WriteByte(file,32) ;Write " " if last
  Next
  WriteByte(file,58) : WriteByte(file,32) ;Write ": "
  For count=0 To 3 ;4 chars
   value=PeekB(address+count)
   WriteByte(file,value) ;Write "Char"
  Next
  WriteByte(file,32) : WriteByte(file,58) ;Write " :"
  array$=Str(PeekF(address))
  WriteByte(file,32) : MemoryLogString(file,array$) ;Write " Float"
  WriteByte(file,32) : WriteByte(file,125) ;Write " }"
  WriteByte(file,13) : WriteByte(file,10) ;Write "EOL"
  address=address+4 ;Get the next 4 bytes
 Next
 array$="Address  { Int : Short : Byte : Char : Float }"
 MemoryLogString(file,array$) ;Write info string
 WriteByte(file,13) : WriteByte(file,10) ;Write "EOL"
 CloseFile file
End Function

Function MemoryLogValue(file,value)
 ;Writes an ascii byte, short or integer
 Local stringarray$,id,char$
 stringarray$=Str(value)
 For id=1 To Len(stringarray$)
  char$=Mid(stringarray$,id,1)
  WriteByte(file,Asc(char$))
 Next
End Function

Function MemoryLogString(file,stringarray$)
 ;Writes an ascii string
 Local id,char$
 For id=1 To Len(stringarray$)
  char$=Mid(stringarray$,id,1)
  WriteByte(file,Asc(char$))
 Next
End Function
