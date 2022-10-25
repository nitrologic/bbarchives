; ID: 854
; Author: elseano
; Date: 2003-12-13 03:59:45
; Title: Message Box function
; Description: Makes a small message box at the bottom of the screen with name, message, and picture

Function message(charachter$,message$,pic$,r#,g#,b#)

Color r#,g#,b# ;color of the main box
Rect 10,470,600,100,True
Color 155,155,155
Rect 10,470,601,100,False
DrawImage pic$,15,480 ;Image of the character [80x80 pixels]
Color 255,255,255
Text 100,480,charachter$ ;The character's name
Color 220,220,220
Text 100,500,message$ ;What he/she is saying!

End Function
