; ID: 988
; Author: Filax
; Date: 2004-04-05 03:59:00
; Title: Check if an image have good size
; Description: Test if an image have good size

If Proc_GetImageSize("C:\Tmp\Test.bmp",512,512) Then 
; Good
Else
; Bad
endif


Function Proc_GetImageSize(Filename$,SizeX,SizeY)
	If FileType(Filename$)=1 Then 
		Tmp_Image=LoadImage(Filename$)

		If ImageWidth(Tmp_Image)<>SizeX And ImageHeight(Tmp_Image)<>SizeY Then
			FreeImage Tmp_Image
			Return False
		Else
			FreeImage Tmp_Image
			Return True
		EndIf
	Else
		Return False
	EndIf 
End Function
