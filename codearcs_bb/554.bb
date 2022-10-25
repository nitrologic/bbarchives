; ID: 554
; Author: Todd
; Date: 2003-01-18 17:30:28
; Title: Windows Colors
; Description: Gets all the Windows GUI colors

;WinSettings v1.0

Type iniData
	Field dat$
End Type

Type iniHeader
	Field dat$,dc
	Field xdata.iniData[4096]
End Type

Type iniFile
	Field file$,index,xhead.iniHeader[1024]
	Field fhnd,hnum,dnum,head
End Type

Global iniNum
Dim xini.iniFile(1024)

Global windowsIniFile$
SetWindowsIni("")

Function OpenIni(file$)
	ftype=FileType(file$)
	If ftype=1
		ininum=ininum+1
		xini(ininum)=New iniFile
		xini(ininum)\file$=file$
		xini(ininum)\index=ininum
		xini(ininum)\fhnd=OpenFile(file$)
		FillIni(ininum)
		Return ininum
	Else
		Return False
	EndIf
End Function

Function CloseIni(index)
	For hn=1 To xini(index)\hnum
		For dn=1 To xini(index)\xhead[hn]\dc
			Delete xini(index)\xhead[hn]\xdata[dn]
		Next
		Delete xini(index)\xhead[hn]
	Next
	Delete xini(index)
	iniNum=iniNum-1
End Function

Function FillIni(index)
	If index <= ininum
		SeekFile xini(index)\fhnd,0
		Repeat	
			If Eof(xini(index)\fhnd) Then Exit
			xl$=ReadLine$(xini(index)\fhnd)
			xl$=Trim(xl$)
			If Instr(xl$,";")
				xl$=Left(xl$,Instr(xl$,";")-1)
			EndIf
			If xl$ <> ""
				If Left(xl$,1)="[" And Right(xl$,1)="]"
					xini(index)\hnum=xini(index)\hnum+1
					hnum=xini(index)\hnum
					xini(index)\xhead[hnum]=New iniHeader
					xini(index)\xhead[hnum]\dat$=xl$
					dnum=0
				Else
					xini(index)\dnum=xini(index)\dnum+1
					xini(index)\xhead[hnum]\dc=xini(index)\xhead[hnum]\dc+1
					dnum=dnum+1
					xini(index)\xhead[hnum]\xdata[dnum]=New iniData
					xini(index)\xhead[hnum]\xdata[dnum]\dat$=xl$
				EndIf
			EndIf
		Forever
		hnum=0
		Return True
	Else
		Return False
	EndIf
	CloseFile xini(index)\fhnd
End Function

Function GotoHeader(index,header$)
	If index <= ininum
		If Left$(header$,1)<>"[" Then header$="["+header$
		If Right$(header$,1)<>"]" Then header$=header$+"]"
		hnum=xini(index)\hnum
		For hn=1 To hnum
			If xini(index)\xhead[hn]\dat$=header$
				xini(index)\head=hn
				Return True
			EndIf
		Next
	Else
		Return False
	EndIf
End Function

Function ReadData$(index,off,estr=0)
	If index <= ininum
		head=xini(index)\head
		dat$=xini(index)\xhead[head]\xdata[off+1]\dat$
		Select estr
		Case 0
			dat$=Right(dat$,Len(dat$)-Instr(dat$,"="))
			dat$=Replace$(dat$,Chr$(43),"")
			dat$=Replace$(dat$,"True","1")
			dat$=Replace$(dat$,"False","0")
			Return dat$
		Case 1
			dat$=Replace$(dat$,Chr$(43),"")
			Return dat$
		End Select
	Else
		Return False
	EndIf
End Function

Function SetWindowsIni(file$)
	If FileType(file$)=1
		windowsIniFile$=file$
	Else
		windowsIniFile$=SystemProperty("windowsdir")+"win.ini"
	EndIf
End Function

Function GetRedColor(col)
	Return (col Shr 16) And $ff
End Function

Function GetGreenColor(col)
	Return (col Shr 8) And $ff
End Function

Function GetBlueColor(col)
	Return col And $ff
End Function

Function WindowsScrollbarColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,0))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsBackgroundColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,1))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsActiveTitleColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,2))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsInactiveTitleColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,3))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsMenuColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,4))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsWindowColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,5))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsWindowFrameColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,6))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsMenuTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,7))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsWindowTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,8))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsTitleTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,9))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsActiveBorderColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,10))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsInactiveBorderColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,11))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsAppWorkspaceColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,12))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsHighlightColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,13))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsHighlightTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,14))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsButtonFaceColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,15))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsButtonShadowColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,16))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsGrayTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,17))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsButtonTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,18))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsInactiveTitleTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,19))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsButtonHighlightColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,20))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsButtonDarkShadowColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,21))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsButtonLightColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,22))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsInfoTextColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,23))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsInfoWindowColor()	
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,24))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsButtonAlternateFaceColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,25))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsHotTrackingColor()	
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,26))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsGradientActiveTitleColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,27))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function

Function WindowsGradientInactiveTitleColor()
	winIni=OpenIni(windowsIniFile$)
	GotoHeader(winIni,"colors")
	col$=Trim(ReadData$(winIni,28))
	If Instr(col$," ")
		red=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	If Instr(col$," ")
		grn=Left(col$,Instr(col$," ")-1)
		col$=Right(col$,Len(col$)-Instr(col$," "))
	EndIf
	blu=col$
	CloseIni winIni
	Return ((red Shl 16)+(grn Shl 8)+blu)
End Function
