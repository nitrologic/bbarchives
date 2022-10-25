; ID: 1287
; Author: slenkar
; Date: 2005-02-10 14:33:56
; Title: Use bitmap fonts with user input
; Description: An alternative to the input command

Graphics 800,600
Global player_name$
Global name_chosen=no

While Not name_chosen
Cls
FlushKeys()

While Not player_input
player_input=GetKey()
Text (400,200,"Enter name (max 7 characters)",True,True)
Text (400,300,player_name+"$",True,True)
Flip
Wend

actual_character$=0
actual_character=Chr(player_input)
If player_input<>13 And player_input<>8 And Len(player_name)<7
player_name$=player_name$+actual_character
Else
If player_input=13 And Len (player_name)>0
name_chosen=True
EndIf
If player_input=8
player_name=delete_letter(player_name)
EndIf
EndIf
player_input=0
Wend
Print "your name is: "+player_name+" press a key to start game"
WaitKey
End

Function delete_letter$(St$)

  If Len(St$) = 0 Then Return ""

  Return Left$(St$, Len(St$) - 1)


End Function
