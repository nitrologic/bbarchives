; ID: 1811
; Author: Leon Drake
; Date: 2006-09-11 19:37:39
; Title: Convert Textarea to Password Area
; Description: use the password ****** in a textarea

Include "bin\user32.bmx"
'use the user32 source as an include in a sub directory.. you 
'don't have to though you can just import it.
Global windowt:TGadget = CreateWindow("win",200,200,200,300,Desktop(),1)
Global txtarea:Tgadget = CreateTextArea(10,10,100,20,windowt)
'create our window and our textfield
hWnd = QueryGadget(txtarea,1)
SendMessageA(hWnd, EM_SETEDITSTYLE, ES_PASSWORD,0)
SendMessageA(hWnd,EM_SETPASSWORDCHAR,1,0 )
rem
the 1 inside the SendMessageA(hWnd,EM_SETPASSWORDCHAR,1,0 )
command is suppose to represent the character to replace the  characters in the textarea with. I used a 1 guessing.. but it makes a *. i suppose you could play around with it to have it replace the chars with something other than a asterisk
end rem
