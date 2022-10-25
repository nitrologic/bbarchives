; ID: 553
; Author: jfk EO-11110
; Date: 2003-01-17 17:18:20
; Title: Auto-format
; Description: Auto-format Blitz Sourcecode

; This Code will Auto-format Blitz Source.

; It will not work with One-Line If-Commands wich don't use "Then"
; "If KeyDown(1) Print a"
; will not be formatted correctly, but
; "If KeyDown(1) Then Print a"
; will work ok.

f$="auto_scr_format.bb" ; this File will be formated
o$="  "; will be used for Offset in Formatting: use Space(s) or Tab

CopyFile f$,f$+".fbk" ; a Backup will be created
Dim ln$(100000) ; max numbr of lines

fi=ReadFile(f$) ; Read File to Array
z=0
While Eof(fi)=0
  ln$(z)=ReadLine(fi)
  z=z+1
Wend
z=z-1
CloseFile fi

Restore frm_plus ; Read Relevant Command Tags, Format-Increment and -Decrement ones
co_pl=0
Repeat
  Read r$
  co_pl=co_pl+1
Until r$=""
co_pl=co_pl-1

Restore frm_minus
co_mi=0
Repeat
  Read r$
  co_mi=co_mi+1
Until r$=""
co_mi=co_mi-1

Dim f_plus$(co_pl)
Dim f_minus$(co_mi)

Restore frm_plus
For i=0 To co_pl-1
  Read f_plus$(i)
Next

Restore frm_minus
For i=0 To co_mi-1
  Read f_minus$(i)
Next


.frm_plus
Data "If","While","For ","Repeat","Function","Type","Else","Select","Case",""


.frm_minus
Data "EndIf","Wend","Until","Forever","End Function","End Type","Else","Next","End Select","Case",""
; You can add some more (note "Else" or "Case" is in both Arrays!)



For i=0 To z ; remove Spaces and Tabs at beginning and End of Lines
  c=1
  While Mid$(ln$(i),c,1)=" " Or Mid$(ln$(i),c,1)=Chr$(9)
    c=c+1
  Wend
  c=c-1
  If c>0
    ln$(i)=Right$(  ln$(i), Len(  ln$(i)  )-c  )
  EndIf
  While Right$(ln(i),1)=" " Or Right$(ln(i),1)=Chr$(9)
    ln$(i)=Left$(ln$(i),Len(ln$(i))-1)
  Wend
Next


;----------------------------------------------------------------------
For i=0 To z
  ez=ez+nez
  nez=0
  For j=0 To co_mi-1
    If Instr(ln$(i),f_minus$(j),1)=1
      ez=ez-1
      Exit
    EndIf
  Next
  For j=0 To co_pl-1
    If Instr(ln$(i),f_plus$(j),1)=1
      nez=1
      ; check special "Then" cases
      wo=Instr(ln$(i),"Then",1) ; is it a single-line if-then command?
      wo2=Instr(ln$(i),";",1)
      If wo<>0 And (wo < Len(ln$(i))-6) And (wo2=0 Or wo2>=(wo+4)); Line finishes after that "Then" without further Commands?
        nez=0
      EndIf
      If wo<>0 And wo2>=(wo+4)
        nez=1
        For j2=wo+4 To wo2
          what$=Mid$(ln$(i),j2,1)
          If what$<>" " And what$<>Chr$(9) And what$<>";"  ; some Commands between "Then" and ";" ?
            nez=0
          EndIf
        Next
      EndIf
      Exit
    EndIf
  Next
  Print String$(o$,ez)+ln$(i)
  ln$(i)=String$(o$,ez)+ln$(i)
Next
;----------------------------------------------------------------------


;finally save the edited Version

wr=WriteFile(f$)
For i=0 To z
  WriteLine(wr,ln$(i))
Next
CloseFile wr
Color 0,255,0
Print "ok, formatted "+f$+" ("+z+" Lines of code.)"
Print "Key"
WaitKey()
End
