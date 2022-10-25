; ID: 2832
; Author: Luke111
; Date: 2011-03-15 15:52:41
; Title: Image Packer
; Description: Packs Images From A Folder Into A New, Single Image

Print "Please Enter The Folder Of Images To Pack - "
folder$ = Input$()
num$ = Input$("Enter Number Of Images - ")
numr$ = Input$("Enter Number Of Rows - ")
numc$ = Input$("Enter Number Of Cols - ")
If FileType(folder$) <> 2 Then
	RuntimeError "Error: Invalid Input"
EndIf
myDir = ReadDir(folder$)
packed = CreateImage((Int(num$)/Int(numr$))*96,(Int(num$)/Int(numc$))*96)
r% = 0
c% = 0
SetBuffer ImageBuffer(packed)
Repeat
Print "1"
file$ = NextFile$(myDir)
Print "2"
If file$ = "" Then
	Exit
EndIf
If FileType(folder$+"\"+file$) = 0 Then
	Exit
EndIf
If Not file$ = "." Then
	If Not file$ = ".." Then

image = LoadImage(folder$+"\"+file$)
DrawImage image,c,r
FreeImage image
If c+96 >= (Int(num$)/Int(numr$))*96 Then
	c = 0
	r = r + 96
Else
	c = c + 96
EndIf
	EndIf
EndIf
Forever
SaveImage packed,folder$+"\packed.bmp"
CloseDir myDir
FreeImage packed
