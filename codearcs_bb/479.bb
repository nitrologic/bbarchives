; ID: 479
; Author: Oldefoxx
; Date: 2002-11-05 00:15:50
; Title: Hard Drive Cleaner
; Description: Allows you to automatically delete certain files and directories

Global swidth=800, sheight=600, v$

Graphics swidth,sheight,16
ClsColor 0,0,128
Cls
AppTitle "HD Cleaner"
Print"  Hard Drive Cleaner, by Donald R. Dardem, November 2002"
Print""
Print"  WARNING! Use this program with Caution!  If used or modified"
Print"  incautiously, it may delete some essential file or directory"
Print"  that is required for proper operation of your system."
Print""
Print"  No warrantee as to suitability or operation is given or implied,"
Print"  so use at your own risk.  Note that some programs may use a zero-"
Print"  length file as a flag of some sort, or there may be an empty"
Print"  directory that is needed at certain other times for the proper"
Print"  operation of some program.  That is the type of issue that would"
Print"  become a problem if the needed file or directory is deleted."
Print"  While this approach is rarely today, some programs have behaved"
Print"  in this fashion in the past, and others may again in the future."
Print""
Print"  Note that when a question is asked, you must enter an answer that"
Print"  includes the letter 'Y' (such as 'y', 'Y', 'yes' or 'okay') if"
Print"  you want to signify 'yes', otherwize the question defaults to"
Print"  'No' when the Enter key is pressed."
Print""
Print""
d$=Input$("Automatically delete zero-byte (empty) files? ")
killnull=Instr(Upper$(d$),"Y")
d$=Input$("Automatically delete .TMP files? ")
killtmp=Instr(Upper$(d$),"Y")
d$=Input$("Automatically delete .BAK files? ")
killbak=Instr(Upper$(d$),"Y")
d$=Input$("Automatically delete Temporary Internet Files? ")
killTIF=Instr(Upper$(d$),"Y")
For a%=Asc("C") To Asc("Z")
  d$=Chr$(a%)+":\"+Chr$(0) 
  chkdrive=1
  While d$>""
    If c$>"" And chkdir=0 Then
      Color 128,255,0
      If Right$(c$,2)<>":\" Then
        Print c$
        e$=Upper$(c$)
        b%=Instr(e$,"\CACHE\")
        If b%>0 And b%+6<Len(e$) And killTIF Then Goto erasedir
        b%=Instr(e$,"\TEMP\")
        If b%>0 And b%+5<Len(e$)And killtmp Then Goto erasedir
        b%=Instr(e$,"\TMP\")
        If b%>0 And b%+4<Len(e$)And killtmp Then Goto erasedir
        b%=Instr(e$,"\TEMPORARY INTERNET FILES\")
        If b% And b%+25<Len(e$) Then
.erasedir
          DeleteDir c$
        Else
.askerase 
          e$=Input$("The above directory is empty, delete it? ")
          If Instr(Upper$(e$),"Y") Then DeleteDir c$
        EndIf
      EndIf
    EndIf
    b%=Instr(d$,Chr$(0))
    If b%=0 Then b%=Len(d$)+1
    c$=Left$(d$,b%-1)
    chkdir=0
    d$=Mid$(d$,b%+1)
    If Right$(c$,1)<>"\" Then c$=c$+"\" 
    b%=ReadDir(c$)    
    While b%>0
      f$=NextFile$(b%)
      If f$="" Then Exit
      If Left$(f$,1)<>"." Then
        e$=c$+f$
        chkdir=chkdir+1
        If chkdrive Then
          chkdrive=0
          numdrives=numdrives+1
        EndIf
        If FileType(e$)=2 Then
          d$=d$+e$+Chr$(0)
          numdirs=numdirs+1
        Else
          Color 255,255,192
          nb=FileSize(e$)
          ue$=Upper$(e$)
          If nb=0 Then
            Color 255,128,0
            nilfiles=nilfiles+1
            If killnull Then DeleteFile e$
          ElseIf Instr(ue$,"\TEMPORARY INTERNET FILES\") Or Instr(ue$,"\CACHE\")Then
            Color 128,128,128
            If killTIF Then DeleteFile e$
          ElseIf Instr(ue$,"\TMP\") Or Instr(ue$,"\TEMP\")Then
            Goto killtmps
          Else     
            Select Upper$(Right$(e$,4))
            Case ".TMP"   ;tabulate temporary files
.killtmps
              Color 192,192,255
              If killtmp Then
                DeleteFile e$
              Else
                tmpfiles=tmpfiles+1
                tmpbyteslo=tmpbyteslo+nb
                While tmpbyteslo>100000000
                  tmpbyteshi=tmpbyteshi+1
                  tmpbyteslo=tmpbyteslo-100000000
                Wend
              EndIf
            Case ".BAK"    ;tabulate backup files
              Color 160,255,160
              If killbak Then
                DeleteFile e$
              Else
                bakfiles=bakfiles+1
                bakbyteslo=bakbyteslo+nb
                While bakbyteslo>100000000
                  bakbyteshi=bakbyteshi+1
                  bakbyteslo=bakbyteslo-100000000
                Wend
              EndIf
            Case ",BB"    ;tabulate BlitzBASIC source files
              bbfiles=bbfiles+1
            Default
            End Select   
          End If
          strnumb(0,nb)
          g$=e$+String$(" ",swidth/8-Len(e$)-Len(v)-6)+v+" bytes"
          Print g$
          numfiles=numfiles+1
          byteslo=byteslo+nb
          While byteslo>100000000
            byteslo=byteslo-100000000
            byteshi=byteshi+1
          Wend
        EndIf
      EndIf
    Wend
  Wend
Next
Print "Number of scanned drives: "+numdrives
Print "Number of scanned directories: "+numdirs
Print "Number of scanned files: "+numfiles
strnumb(byteshi,byteslo)
Print "Total bytes in files: "+v
Print "Number of zero-byte files: "+nilfiles
strnumb(tmpbyteshi,tmpbyteslo)
Print "Number of .TMP files: "+tmpfiles+", Number of bytes: "+v
strnumb(bakbyteshi,bakbyteslo)
Print "Number of .BAK files: "+bakfiles+", Number of bytes: "+v
Function strnumb(hi,lo)
Local z$,w,zz$,x
w=hi
x=lo
While x>100000000
  w=w+1
  x=x-100000000
Wend
zz=w
z=x
z=zz+String$("0",8-Len(z))+z
w=Instr(z,".")
If w=0 Then
  w=Len(z)+1
Else
  x=Instr(z,"e")
  If x Then
    zz=Left$(z,x-1)
    x=Mid$(z,x+1,Len(z)-x)
    z=Left$(z,1)+Mid$(zz,3,Len(zz)-2)
    z=z+String$("0",x-Len(z)+1)
    w=Len(z)+1
Print "w = "+w
  Else 
    While Right$(z,1)="0"Or Right$(z,1)="."
      z=Left$(z,Len(z)-1)
    Wend
  EndIf
EndIf
While w>3
  w=w-3
  z=Left$(z,w-1)+","+Mid$(z,w,Len(z)-w+1)
Wend     
While Left$(z,1)="0" Or Left$(z,1)=","
  z=Mid$(z,2,Len(z)-1)
Wend
If z="" Then z="0" 
v=z
End Function
