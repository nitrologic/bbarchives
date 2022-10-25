; ID: 779
; Author: Todd
; Date: 2003-08-24 01:20:13
; Title: TextArea Html Formatting
; Description: Format TextArea using Html-style tags

;TextArea Html-Style Tags
;Written by Todd Yandell

Type TxTag
	Field Flags,Val$
	Field Red,Grn,Blu
	Field Pos,Size
End Type

Const TxFormatOnly=0
Const TxSetText=1
Const TxAppendText=2

Global StTags$[16],TagDef$[16],TgClose$="/"
Global TagOpen$="[",TagClose$="]"
Global MaxTags=3,DefTextRed,DefTextGrn,DefTextBlu

StTags[1]="b":TagDef[1]="Bold"
StTags[2]="i":TagDef[2]="Italic"
StTags[3]="c:%%%%%%":TagDef[3]="Color"

Function FormatTextArea(TextArea,StText$,TextMode=0)
	LockTextArea TextArea
	If StText$ <> ""
		Local Buffer$,TagMode,Char$,Ch,CharA$,CharB$,CloseMode
		Local NewTag$,ChTag$,Match,Tag,Pos,TextFlags,NewLine$
		Local TextRed,TextGrn,TextBlu,NewTagMode,NewCol$,StartPos
		Nt.TxTag=New TxTag
		Nt\Pos=0
		If TextMode=2
			Nt\Pos=TextAreaLen(TextArea,1)
			StartPos=Nt\Pos
		EndIf
		For Ch=1 To Len(StText$)
			Char$=Mid(StText$,Ch,1)
			If Char=TagOpen
				If TagMode=False
					TagMode=True
					Buffer=""
				EndIf
			ElseIf Char=TagClose
				If TagMode=True
					TagMode=False
					NewTag=Replace(Buffer,TgClose,"")
					CloseMode=False
					If NewTag <> Buffer
						CloseMode=True
					EndIf
					Match=False
					For Tag=1 To MaxTags
						ChTag=StTags[Tag]
						If Len(ChTag)=Len(NewTag)
							For Pos=1 To Len(ChTag)
								CharA=Mid(ChTag,Pos,1)
								CharB=Mid(NewTag,Pos,1)
								If CharA <> "%"
									If CharA <> CharB
										Exit
									EndIf
								EndIf
							Next
							If Pos=Len(ChTag)+1
								Match=True
								Exit
							Else
								Match=False
							EndIf
						EndIf
					Next
					If Match=True
						Select TagDef[Tag]
						Case "Bold"
							If Not CloseMode
								TextFlags=TextFlags Or 1
							Else
								TextFlags=TextFlags Xor 1
							EndIf
						Case "Italic"
							If Not CloseMode
								TextFlags=TextFlags Or 2
							Else
								TextFlags=TextFlags Xor 2
							EndIf
						Case "Color"
							If Not CloseMode
								If Instr(NewTag,":")
									ClPos=Instr(NewTag,":")
									NewCol=Right(NewTag,Len(NewTag)-ClPos)
									TextRed=Hex2Int(Mid(NewCol,1,2))
									TextGrn=Hex2Int(Mid(NewCol,3,2))
									TextBlu=Hex2Int(Mid(NewCol,5,2))
								EndIf
							Else
								TextRed=DefTextRed
								TextGrn=DefTextGrn
								TextBlu=DefTextBlu
							EndIf
						End Select
						Nt=New TxTag
						Nt\Pos=StartPos+Ch2
						Nt\Flags=TextFlags
						Nt\Red=TextRed
						Nt\Grn=TextGrn
						Nt\Blu=TextBlu
					EndIf
					Buffer=""
				EndIf
			Else
				Buffer=Buffer+Char
				If Not TagMode
					Ch2=Ch2+1
					Nt\Size=Nt\Size+1
					Nt\Val=Buffer
				EndIf
			EndIf
		Next
		For Nt.TxTag=Each TxTag
			If Nt\Val=""
				Delete Nt
			EndIf
		Next
		Select TextMode
		Case 1
			SetTextAreaText TextArea,""
			For Nt.TxTag=Each TxTag
				AddTextAreaText TextArea,Nt\Val
			Next
		Case 2
			For Nt.TxTag=Each TxTag
				AddTextAreaText TextArea,Nt\Val
			Next
		End Select
		For Nt.TxTag=Each TxTag
			FormatTextAreaText(TextArea,Nt\Red,Nt\Grn,Nt\Blu,Nt\Flags,Nt\Pos,Nt\Size)
		Next
		Delete Each TxTag
	EndIf
	UnlockTextArea TextArea
End Function

Function Hex2Int(val$)
	For x=0 To Len(val$)-1
		ch$=Mid(val,x+1,1)
		Select Upper(ch)
		Case 0,1,2,3,4,5,6,7,8,9
			chn=Int(ch)
		Case "A"
			chn=10
		Case "B"
			chn=11
		Case "C"
			chn=12
		Case "D"
			chn=13
		Case "E"
			chn=14
		Case "F"
			chn=15
		Default
			Return -1
		End Select
		vv=vv+(chn*(16^(Len(val$)-(x+1))))
	Next
	Return vv
End Function
