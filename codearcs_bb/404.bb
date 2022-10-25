; ID: 404
; Author: superqix
; Date: 2002-09-03 09:46:27
; Title: Blitz Resource Compiler v0.3
; Description: BASE64 encode a file for "include"ing into your Blitz project for later retrival.

; ******************************************
; * Blltz Resource Compiler v0.3
; ******************************************
; * BASE64 encode a file into BB source to
; * add to a Blitz project for retrival
; * 
; * Perfect for including small graphics or
; * sounds in your Blitz EXE file
; ******************************************
; * Released under the LPGL 9/03/2002 by
; * Michael Wilson wilson(at)no2games.com
; ******************************************

; ******************************************
; * About:
; *
; * These functions will create a new *.bb
; * Blitz source file with a BASE64-like 
; * encoded version of the files added with 
; * ResourceAddFile() and encoded with
; * ResourceEncodeAll().
; * 
; * The unencoding function LoadResource() 
; * is appended to the top of the new
; * *.bb file, ready to be included in your
; * project
; *
; * Since I'm using text encodeing the file
; * will take up 133% the space of the 
; * original, so I'd limit it's use to
; * ????k of resource files, but I don't
; * know what the practical limit is.
; ******************************************

Type ResourceFile
 Field FileName$
End Type

; ******************************************
; * Change EncodeTable$ to encrypt your
; * files, Must be 64 unique characters,
; ******************************************

Global EncodeTable$ = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz{}"
If Len(EncodeTable$) <> 64 Then RuntimeError("Encode Table must be 64 unique characters ("+Len(EncodeTable$)+")")

; ******************************************
; * Function ResourceAddFile(FileName$)
; ******************************************

Function ResourceAddFile(FileName$)
    Resources.ResourceFile = New ResourceFile 
    Resources\FileName$ = FileName$ 
End Function 

; ******************************************
; * Function ResourceEncodeAll(OutputFile$)
; ******************************************

Function ResourceEncodeAll(OutputFile$)
	
	OutFile = WriteFile(OutputFile$)
	If OutFile = 0 Then RuntimeError("Can not make output file.")
	
	WriteLine OutFile,"Type TempResourceFile"
	WriteLine OutFile,"  Field FileName$"
	WriteLine OutFile,"End Type"			
	WriteLine OutFile,""

	WriteLine OutFile,"Function FreeResources()"
	WriteLine OutFile,"  For TempFiles.TempResourceFile = Each TempResourceFile "
	WriteLine OutFile,"  DeleteFile TempFiles\FileName$ "
	WriteLine OutFile,"  Delete TempFiles "
	WriteLine OutFile,"  Next "
	WriteLine OutFile,"End Function "
	WriteLine OutFile,""

	WriteLine OutFile,"Function LoadResource$(Resource$)"
	WriteLine OutFile,"  Select Upper$(Resource$)"
    For Resources.ResourceFile = Each ResourceFile 
		ResourceFile$ = Upper$(ExtractFilename$(Resources\FileName$))
		ResourceName$ = ExtractFilename$(Replace(Resources\FileName$,".","_"))
		WriteLine OutFile,"    Case " + Chr$(34) + ResourceFile$ + Chr$(34) +" Restore "+ResourceName$
    Next 
	WriteLine OutFile,"  End Select"			
	WriteLine OutFile,"  EncodeTable$ = " + Chr$(34) + EncodeTable$ + Chr$(34)
	WriteLine OutFile,"  TempFile$ = Str Int Rnd(11111,99999)"
	WriteLine OutFile,"  TempFile$ = SystemProperty ("+ Chr$(34) +"tempdir" + Chr$(34) +")+" + Chr$(34) +"~bb" + Chr$(34) + "+TempFile$+" + Chr$(34) + ".tmp" + Chr$(34) 
	WriteLine OutFile,"  OutFile = WriteFile(TempFile$)"
	WriteLine OutFile,"   Repeat"
	WriteLine OutFile,"     Read Output$"
	WriteLine OutFile,"     If Output$ = "+ Chr$(34) +"!EOF"+ Chr$(34) +" Then Exit"
    WriteLine OutFile,"     ByteCount% = Len(Output$)"
    WriteLine OutFile,"     For i = 1 To ByteCount% Step 4"
    WriteLine OutFile,"       Word% = ((Instr(EncodeTable$,Mid$(Output$,i,1)) - 1) And 63) Shl 18"
    WriteLine OutFile,"       Word = Word + (((Instr(EncodeTable$,Mid$(Output$,i+1,1)) - 1) And 63) Shl 12)"
    WriteLine OutFile,"       Word = Word + (((Instr(EncodeTable$,Mid$(Output$,i+2,1)) - 1) And 63) Shl 6)"
    WriteLine OutFile,"       Word = Word + ((Instr(EncodeTable$,Mid$(Output$,i+3,1)) - 1) And 63)"
    WriteLine OutFile,"       Byte% = Word Shr 16 And 255"
    WriteLine OutFile,"       Byte2% = Word Shr 8 And 255"
    WriteLine OutFile,"       Byte3% = Word And 255"
    WriteLine OutFile,"       WriteByte OutFile, Byte"
    WriteLine OutFile,"       WriteByte OutFile, Byte2"
    WriteLine OutFile,"       WriteByte OutFile, Byte3"
    WriteLine OutFile,"     Next"
	WriteLine OutFile,"   Forever"
	WriteLine OutFile,"  CloseFile OutFile"
	WriteLine OutFile,"  TempFiles.TempResourceFile = New TempResourceFile"
	WriteLine OutFile,"  TempFiles\FileName$ = TempFile$"
	WriteLine OutFile,"  Return TempFile$"
	WriteLine OutFile,"End Function"
	WriteLine OutFile,""
	
    For Resources.ResourceFile = Each ResourceFile 
		InputFile$ = Resources\FileName$
		ResourceName$ = ExtractFilename$(Replace(Resources\FileName$,".","_"))
		InFile = ReadFile(InputFile$)
		WriteLine OutFile,""
		WriteLine OutFile,"." + ResourceName$
		ByteCount% = 0
		While Not Eof(InFile)
			Byte% = ReadByte(InFile)
			Byte2% = ReadByte(InFile)
			Byte3% = ReadByte(InFile)
			Word% = (Byte Shl 16) Or (Byte2 Shl 8) Or Byte3			
			; BASE 64 Encode
			Output$ = Output$ + Mid$(EncodeTable$,((Word Shr 18) + 1) ,1)
			Output$ = Output$ + Mid$(EncodeTable$,(((Word Shr 12) And 63) + 1) ,1)
			Output$ = Output$ + Mid$(EncodeTable$,(((Word Shr 6) And 63) + 1) ,1)
			Output$ = Output$ + Mid$(EncodeTable$,((Word And 63) + 1) ,1)
			ByteCount = ByteCount + 3 
			If ByteCount >= 64 Then 
				WriteLine OutFile, "Data " + Chr$(34) + Output$ + Chr$(34)
				ByteCount = 0
				Output$ = ""
			End If
		Wend
		If Output$ <> "" Then WriteLine OutFile, "Data " + Chr$(34) + Output$ + Chr$(34)
		WriteLine OutFile, "Data " + Chr$(34) + "!EOF" + Chr$(34)
		Output$ = ""
		CloseFile InFile
    Next 	
	CloseFile OutFile
	
End Function

; ******************************************
; * Function ExtractFilename$(FileName$)
; ******************************************

Function ExtractFilename$(FileName$)

	FileName$="\"+FileName$
	LastBackSlash = 1
	Repeat
		NextBackSlash = Instr(filename$,"\",LastBackSlash)
		If NextBackSlash > 0 Then LastBackSlash = NextBackSlash + 1
	Until NextBackSlash = 0
	tmp$ = Right$(FileName$,Len(FileName$)-LastBackSlash+1)
	Return tmp$

End Function
