; ID: 705
; Author: Red
; Date: 2003-05-23 06:36:09
; Title: SetListBoxBackground
; Description: change background color of listbox

; ===========================
; Change background color
; ===========================

function SetListBoxBackground(lb,colorRGB%)
	Local LVM_SETBKCOLOR=$1001
	SendMessage(QueryObject(lb,1),LVM_SETBKCOLOR,0,InvertRGB(colorRGB))
End Function

; ===========================
; RGB(blitz) to BGR(win) 
; ===========================

function InvertRGB%(rgb%)
	Local r=(rgb And $0000FF) Shl 16
	Local g=(rgb And $00FF00)
	Local b=(rgb And $FF0000) Shr 16
	
	rgb=r Or g Or b 
	Return rgb
End Function
