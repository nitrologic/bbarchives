; ID: 1416
; Author: Kuron
; Date: 2005-07-08 04:51:34
; Title: Sibly's Syntax Highlighting
; Description: Syntax Highlighting by Mark Sibly

;
;Demo of a simple syntax highlighting editor!
;
;Auto-capitalizes words and sets some funky colors for numbers,
;strings and comments.
;
;Formatting is performed 'per-line', and changes in linecount are
;tracked to determine how many lines to reformat.
;
;Note: the color and formatting logic is a little convoluted in
;the interests of minimizing textarea modifications for SPEED.
;

window=CreateWindow( "Simple syntax highlighting text editor",0,0,640,480 )

textarea=CreateTextArea( 0,0,ClientWidth(window),ClientHeight(window)-96,window )
SetGadgetLayout textarea, 1,1,1,1

font=LoadFont( "lucida",20 )
SetTextAreaFont textarea,font
SetTextAreaColor textarea,0,0,0,True
SetTextAreaColor textarea,255,255,255

ActivateGadget textarea

Global output=CreateTextArea( 0,ClientHeight(window)-96,ClientWidth(window),96,window )
DisableGadget output
SetGadgetLayout output,1,1,0,1

SetTextAreaColor output,0,0,0
SetTextAreaColor output,192,192,192,True

bp$=GetEnv$("blitzpath")
tmp$=bp$+"/tmp/tmp.bb"
bcc$=bp$+"/bin/blitzcc"

Info( "Output window active!" )
Info( "blitzpath="+bp$ )

main_menu=WindowMenu( window )
file_menu=CreateMenu( "File",0,main_menu )
edit_menu=CreateMenu( "Edit",0,main_menu )
prog_menu=CreateMenu( "Program",0,main_menu )
go_menu=CreateMenu( "Go!",301,prog_menu )

UpdateWindowMenu window

HotKeyEvent 63,0,$1001,301;F5=program->go menu item
HotKeyEvent 1,0,$803,0,0,0,0,window;ESC=close window!

n_lines=TextAreaLen( textarea,2 )

While WaitEvent()<>$803

Select EventID()
Case $103;key hit
Notify "Bam!"
Case $1001;menu action
Select EventData()
Case 301
t_file=WriteFile( tmp$ )
If t_file
WriteLine t_file,TextAreaText$( textarea )
CloseFile t_file
proc=CreateProcess( bcc$+" "+tmp$ )
If proc
While Not Eof(proc)
ln$=ReadLine$(proc)
If Len(ln$) Info( ln$ )
Wend
Else
Info( "Error creating blitzcc process",True )
EndIf
Else
Info( "Error writing tmp file",True )
EndIf
End Select
Case $401;gadget action
Select EventSource()
Case textarea
;calc row and column
row=TextAreaCursor( textarea,2 )
col=TextAreaCursor( textarea,1 )-TextAreaChar( textarea,row )
t_lines=0
If EventData()=1;modified?

;work how many lines added/removed
n=TextAreaLen( textarea,2 )
t_lines=n-n_lines
n_lines=n
If t_lines<0 t_lines=0

;lock textareas before big changes
LockTextArea textarea

;format the lines!!!!!
For n=row-t_lines To row

FormatLine( textarea,n )

Next

;unlock textarea
UnlockTextArea textarea

EndIf
SetStatusText window,"row="+(row+1)+" col="+(col+1)+" lines="+n_lines+" chars="+TextAreaLen(textarea)+" formated="+(t_lines+1)
End Select
End Select
Wend

End

Function info( t$,err=False )
If err t$="Error! - "+t$
AddTextAreaText output,t$+Chr$(10)
End Function

;return true for digits
Function IsDigit( i$ )
t=Asc(i$)
Return t>=48 And t<58
End Function

;return true for alphabetic characters
Function IsAlpha( i$ )
t=Asc(i$)
Return (t>=65 And t<65+26) Or (t>=97 And t<97+26)
End Function

Function FormatLine( textarea,n )

;line we're formatting
ln$=TextAreaText( textarea,n,1,2 )

;start-of-line character index
sol=TextAreaChar( textarea,n )

;color and format info
fmt_ch=0:fmt_r=0:fmt_g=0:fmt_b=0:fmt_f=-1

;next char to process
ch=1

While ch<=Len(ln$)

;save char index and get next char
t_ch=ch
i$=Mid$(ln$,ch,1)

;turn off formating flags (bold etc) by default
f=0

If IsDigit(i$)
;Format a numeric value
Repeat
ch=ch+1
If ch>Len(ln$) Exit
i$=Mid$(ln$,ch,1)
Until Not IsDigit(i$)
r=255:g=0:b=192;barf red
Else If IsAlpha(i$)
;Format an identifier
z$=i$
q$=Upper$(i$)
Repeat
ch=ch+1
If ch>Len(ln$) Exit
i$=Mid$(ln$,ch,1)
z$=z$+i$
q$=q$+Lower$(i$)
Until Not IsAlpha(i$)
If q$<>z$
SetTextAreaText textarea,q$,sol+t_ch-1,Len(q$)
EndIf
r=255:g=255:b=0:f=1;yellow, bold
Else If i$=Chr$(34)
;Format a string
Repeat
ch=ch+1
If ch>Len(ln$) Exit
i$=Mid$(ln$,ch,1)
Until i$=Chr$(34)
If i$=Chr$(34) ch=ch+1
r=0:g=255:b=0
Else If i$=";"
;Format a comment
ch=Len(ln$)+1
r=0:g=128:b=255:f=3;blue, bold-italic
Else
;Default formatting
ch=ch+1
r=255:g=255:b=255;white
EndIf

;if format changed, apply previous
If r<>fmt_r Or g<>fmt_g Or b<>fmt_b Or f<>fmt_f
If fmt_ch
FormatTextAreaText textarea,fmt_r,fmt_g,fmt_b,fmt_f,sol+fmt_ch-1,t_ch-fmt_ch
EndIf
fmt_ch=t_ch:fmt_r=r:fmt_g=g:fmt_b=b:fmt_f=f
EndIf

Wend

;format to EOL
If fmt_ch
FormatTextAreaText textarea,fmt_r,fmt_g,fmt_b,fmt_f,sol+fmt_ch-1,ch-fmt_ch
EndIf

End Function
