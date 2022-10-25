; ID: 706
; Author: Red
; Date: 2003-05-24 04:42:44
; Title: Advance Status bar
; Description: more options for statusbar

; ===============================================================
; Set field count of statusbar and retrieve the status handle
; ===============================================================

Function SetCurrentWindowStatus%(count)
	Local SB_SIMPLE=$409
	Local SB_SETPARTS=$404
	Local SB_ISSIMPLE=$400+14
	
	If count>256 Then RuntimeError("Status : no more 256 fields !")

	;handle of status	
	Local sb=FindWindowEx(GetActiveWindow(),0,"msctls_statusbar32","")
	
	;disable simple-text mode
	SendMessage(sb,SB_SIMPLE,0,0)
			
	;create Fields
	Local fields=CreateBank(256*4)
	For i=1 To count
		PokeInt fields,4*(i-1),-1
	Next	
	SendMessageBANK(sb,SB_SETPARTS,count,fields)	
	FreeBank fields
	
	Return sb
End Function 

; ================================
; Set width of one field 
; ================================

;Note : width,style,tooltip are some optional parameters

Function SetStatusFieldWidth (sb,index,width=-1)
	Local SB_SETPARTS=$404
	Local SB_GETPARTS=$406
	
	;set width 
	Local fields=CreateBank(256*4)
	Local count=SendMessageBANK(sb,SB_GETPARTS,256,fields)
	
	If index>count-1 Then RuntimeError("The index is out of range")
	
	Local border=0
	If width<>-1
		If index=0 
			border=0	
		Else 
			border=PeekInt(fields,(index-1)*4)
		EndIf 
	EndIf 
	PokeInt fields,index*4,border+width
	SendMessageBANK(sb,SB_SETPARTS,count,fields)	
	FreeBank fields
		
End Function 

; ================================
; Change text of one field
; ================================

;Note : style and align are optional parameters
;Note : style=0 (flat) =1 (low border) =2 (high border)
;Note : align=0 left   =1 (right)      =2 (center)

Function SetStatusFieldText(sb,index,txt$,align=0,style=1)
	Local SB_SETTEXT=$401
    Local SBT_NOBORDERS=$100
	Local SBT_POPOUT=$200
	
	;set text
	Local fstyle
	Select style
		Case 0 
			fstyle = SBT_NOBORDERS
		Case 1 
			fstyle = 0  
		Case 2 
			fstyle = SBT_POPOUT
	End Select 		
	Select align
		Case 1 
			txt$ = Chr(9)+txt$  
		Case 2 
			txt$ = Chr(9)+txt$+Chr(9)
	End Select	
	SendMessageSTRING(sb,SB_SETTEXT,index Or fstyle,txt$)
End Function
