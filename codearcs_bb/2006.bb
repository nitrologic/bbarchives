; ID: 2006
; Author: Yahfree
; Date: 2007-05-03 15:14:02
; Title: Input "field"
; Description: a basic input field

;-----------------------------------------------------------------------------------------------------
;											   Types
;-----------------------------------------------------------------------------------------------------
	
;Type To hold scancodes
Type ScanCode
	Field code
	Field key$
	Field upkey$
End Type

;-----------------------------------------------------------------------------------------------------
;											InitGetKey()
;-----------------------------------------------------------------------------------------------------
;reads all scancodes into ScanCode type
Function InitGetKey()


	;read scancodes
	tel = 1
	Restore scancodez
	Repeat
		Read scanc
		If scanc = -1 Then Exit
		s.ScanCode = New ScanCode
		s\code = scanc
		s\key$ = Lower$(Mid$("1234567890-=QWERTYUIOP[]ASDFGHJKL;'\ZXCVBNM,./* 789-456+1230.,/", tel, 1))
		s\upkey$ = Mid$("!@#$%^&*()_+QWERTYUIOP{}ASDFGHJKL:" + Chr$(34) + "|ZXCVBNM<>?* 789-456+1230.,/", tel, 1)
		tel = tel + 1
	Forever

End Function

Global oldkdown
;-----------------------------------------------------------------------------------------------------
;											iGetKey()
;-----------------------------------------------------------------------------------------------------
;imitates GetKey() using scancodes
Function iGetKey()

	;backspace
	If KeyDown(14) Then 
		If oldkdown <> 8 Then oldkdown = 8: Return 8
		oldkdown = 8: Return
	End If

	;enter
	If KeyDown(28) Or KeyDown(156) Then 
		If oldkdown <> 13 Then oldkdown = 13: Return 13
		oldkdown = 13: Return
	End If
	
	;check what key is down
	sel.ScanCode = Null
	down = -1
	For s.ScanCode = Each ScanCode
		If KeyDown(s\code) Then down = s\code: sel = s: Exit
	Next
	If down = oldkdown Then Return
	oldkdown = down

	;if no valid key is selected, exit	
	If sel = Null Then Return 0

	;shift for uppercase	
	If KeyDown(42) Or KeyDown(54) Then
		sc$ = sel\upkey$
	Else
		sc$ = sel\key$
	End If

	;return ascii value of key	
	Return Asc(sc$)

End Function


;-------------------------------------------------------------------------------------------------------
;                                          CreateInputBox()
;-------------------------------------------------------------------------------------------------------

Function CreateInputBox$(box_x,box_y,box_w,useable,returnstr$)
	
	Rect box_x, box_y, box_w,20, False

If useable=True
   key=iGetKey()
	
   Rect box_x+3+Len(returnstr$)*9,box_y+2,1,17,1
		
	
  If (key=>48 And key=<57) Or (key>=65 And key<=90) Or (key>=97 And key<=122)
     If(Len(returnstr$)<box_w/10)
        returnstr$ = returnstr$ + Chr(key)
				
				
End If
  End If
	  End If


If useable=True
If KeyHit(14)
   If Len(returnstr$)>0 returnstr$=Left(returnstr$,Len(returnstr$)-1)
End If
If KeyHit(57)
  returnstr$=returnstr$+" "
End If
End If


	Text box_x+3,box_y, returnstr$

	Return(returnstr$)
	
		
End Function



;-------------------------------------------------------------------------------------------------------
                                       ;Data stuff
;-------------------------------------------------------------------------------------------------------
.scancodez
Data 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
Data 12, 13, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25
Data 26, 27, 30, 31, 32, 33, 34, 35, 36, 37, 38
Data 39, 40, 43, 44, 45, 46, 47, 48, 49, 50, 51
Data 52, 53, 55, 57, 71, 72, 73, 74, 75, 76, 77
Data 78, 79, 80, 81, 82, 83, 179, 181, -1



;-----------------------------------------------------------------------
;                                       PROGRAM
;-----------------------------------------------------------------------

;Include "FunctionLibary.bb"

Graphics 400,300,16,2
SetBuffer BackBuffer()

InitGetKey()

mystring$=""
mystring2$=""
mystring3$=""

tf=False
tf2=False
tf3=False

controller=1

While Not KeyHit(1)
Cls

;CreateInputBox(box_x,box_y,box_w,box_h,useable,strng$)
mystring = CreateInputBox(0,0,300,tf,mystring$)
mystring2=CreateInputBox(0,30,300,tf2,mystring2$)
mystring3=CreateInputBox(0,60,300,tf3,mystring3$)


Select controller
Case 1
tf=True
tf2=False
tf3=False
Case 2
tf2=True
tf3=False
tf=False
Case 3
tf3=True
tf=False
tf2=False
End Select


If KeyHit(200)
controller = controller + 1
End If

If controller > 3  controller=1



Text 0,280,"TF: "+tf
Text 60,280,"TF2: "+tf2
Text 120,280,"TF3: "+tf3



Delay 5
Flip
Wend
End
