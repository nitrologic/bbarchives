; ID: 1457
; Author: ozak
; Date: 2005-09-08 04:43:57
; Title: Recursive CRC-32 file builder
; Description: CRC-32 file builder

; Recursive directory list CRC-32 builder by Odin Jensen (www.furi.dk)

; Setup CRC stuff
Dim CRC32_table(255)
CRC32_init()

Global outfile = WriteFile("patchinfo.dat")
Print("CRC-32 list builder by Odin Jensen")
Print("----------------------------------")
Print("")

BuildList("./")

CloseFile(outfile)

Print("")
Print("Done.")

; Build list function
Function BuildList(From_$) 
  Local MyDir, MyFile$ 

  MyDir = ReadDir(From_$) 
  MyFile$ = NextFile$(MyDir) 
  MyFile$ = NextFile$(MyDir) 
  Repeat 
    MyFile$ = NextFile$(MyDir) 
    If MyFile$ = "" Then Exit 

    If FileType(From_$ + "\" + MyFile$) = 2 Then 
      BuildList(From_$ + "\" + MyFile$) 
    Else 
	  temp$ = Replace(From_$ + "\" + MyFile$, "./\", "")

	  If ((Not Right(temp, 13) = "patchinfo.dat") And (Not Right(temp, 12) = "buildcrc.exe"))
	  
		  crc32% = CRC32_FromFile(temp)
	      WriteLine(outfile, temp + "," + Str(Hex(crc32)))
		  Print("> CRC32 for file '" + temp + "' is " + Str(Hex(crc32)))
	  End If

    End If 
  Forever 
  CloseDir(MyDir)  
End Function


; CRC init function
Function CRC32_Init()
  Local i
  Local j
  Local value

  For i=0 To 255
    value=i
    For j=0 To 7
      If (value And $1) Then 
        value=(value Shr 1) Xor $EDB88320
      Else
        value=(value Shr 1)
      EndIf
    Next
    CRC32_table(i)=value
  Next
End Function

; Function to get CRC-32 value from a file
Function CRC32_FromFile(name$)
  Local byte
  Local crc
  Local file

  crc=$FFFFFFFF
  file=ReadFile(name$)
  If file=0 Then Return
  While Not Eof(file)
    byte=ReadByte(file)
    crc=(crc Shr 8) Xor CRC32_table(byte Xor (crc And $FF))
  Wend
  Return ~crc
End Function
