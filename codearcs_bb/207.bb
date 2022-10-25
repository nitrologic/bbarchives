; ID: 207
; Author: Darkmaster
; Date: 2002-01-29 06:12:50
; Title: rInput
; Description: This code is like the input$, but the programm don´t stops

Function rInput$(aString$)
value = GetKey()
length = Len(aString$)
If value = 8 Then value = 0 :If length > 0 Then aString$ = Left$(aString,Length-1)
If value = 13 Then Goto ende
If value = 0 Then Goto ende
If value>0 And value<7 Or value>26 And value<32 Or value=9 Then Goto ende
aString$=aString$ + Chr$(value)
.ende
Return aString$
End Function

framebegrnzung = CreateTimer(30)
Repeat
testa$=test$
test$ = rInput$(test$)
If KeyHit(28) Then ende%=1
If X=0 Then R=0
If X > 100 Then R=1
If R=1 Then X=X-1 Else X=X+1
WaitTimer framebegrnzung
Cls:Text X,X, test$
Until ende%=1
End
