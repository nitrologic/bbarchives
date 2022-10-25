; ID: 1157
; Author: aab
; Date: 2004-09-04 11:34:21
; Title: Writebits/Readbits
; Description: [PLEASE MOVE TO FILE UTILITIES]Write 8 bits at a time to a data file, and read them back again

Function Writebits8(wr8_readingfile,wr8_var1,wr8_var2=0,wr8_var3=0,wr8_var4=0,wr8_var5=0,wr8_var6=0,wr8_var7=0,wr8_var8=0);use this ONCE to write 8 numbers in the range 0 to 1
	wr8_finalwrite=(wr8_var8 Shl 7)+(wr8_var7 Shl 6)+(wr8_var6 Shl 5)+(wr8_var5 Shl 4)+(wr8_var4 Shl 3)+(wr8_var3 Shl 2)+(wr8_var2 Shl 1)+(wr8_var1)
	WriteByte(wr8_readingfile, wr8_finalwrite)
	Return wr8_finalwrite
End Function

Dim ReturnReadbits(7)
Function sReadbits8(srb8_readingfile);Use this to set up the eight variables form the file, then use ReturnReadbits to return each read bit
	srb8_conceptread=ReadByte(srb8_readingfile)
	For srb8_t=0 To 7
		ReturnReadbits(t)=(srb8_conceptread Shr srb8_t) And $1
	Next
	If(Eof(srb8_readingfile))Return(Eof);
	Return srb8_conceptread
End Function


;EG:
fileW=writefile("file.file")
b1=1
b2=0
b3=1
Writebits8(fileW,1,1,1,0,1,b3,b2,b1)
closefile(fileW)

filer=readfile("file.file")
sReadbits8(filer)
b3=ReturnReadbits(2)
b2=ReturnReadbits(1)
b1=ReturnReadbits(0)
closefile(filer)
