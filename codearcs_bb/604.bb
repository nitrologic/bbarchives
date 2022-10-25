; ID: 604
; Author: Rob Farley
; Date: 2003-02-28 09:02:45
; Title: Simple Encrypter/Decrypter
; Description: An easy way to scamble/descramble files

; ===================================================================================================
; Simple encrypter/decrypter
; 2003 Mental Illusion
; http://www.mentalillusion.co.uk
; rob@mentalillusion.co.uk
; ===================================================================================================


; I know this isn't high security or anything, but it's enough to make people not bother!


; Usage
;        encrypt(Input file, output file, random seed)
;        decrypt(Input file, output file)


encrypt("test.txt","encrypted.txt",1)

decrypt("encrypted.txt","decrypted.txt")

End


Function encrypt(input_file$,output_file$,seed)
SeedRnd seed
filein = ReadFile(input_file)
fileout = WriteFile(output_file)
WriteString (fileout,seed)
While Not Eof(filein)
	temp$=ReadLine(filein)
	enc$=""
	For n=1 To Len(temp$)
		t=Asc(Mid(temp,n,1))
		t=t+Rand(1,Rand(1,128))
		If t>255 Then t=t-255
		enc=enc+Chr$(t)
		Next
	WriteLine(fileout,enc$)
	Wend
CloseFile (filein)
CloseFile(fileout)
End Function


Function decrypt(input_file$,output_file$)
filein = ReadFile(input_file)
fileout = WriteFile(output_file)
seed=ReadString(filein)
SeedRnd seed
While Not Eof(filein)
	temp$=ReadLine(filein)
	enc$=""
	For n=1 To Len(temp$)
		t=Asc(Mid(temp,n,1))
		t=t-Rand(1,Rand(1,128))
		If t<0 Then t=t+255
		enc=enc+Chr$(t)
		Next
	WriteLine(fileout,enc$)
	Wend
CloseFile (filein)
CloseFile(fileout)
End Function
