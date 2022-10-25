; ID: 1998
; Author: Matt Merkulov
; Date: 2007-04-21 05:44:33
; Title: Blitz3D commands benchmark
; Description: Several randomly mixed passes of command execution inside cycle

;Blitz3D commands benchmark by Matt Merkulov

;See results in generated "btable.htm"

Type T
 Field i,f#,s$
End Type

Const tq=91, cq=100000, times=10, symq=20
Const dtim#=1000000.0/cq

Dim ns#(tq)
Dim name$(tq)
Dim tms(tq)

Dim im(symq)
Dim fm#(symq)
Dim sm$(symq)
Dim typ.T(cq)

SeedRnd MilliSecs()

For n=1 To tq
 Read name$(n)
Next

For n=1 To times*tq
 Repeat
  num=Rand(1,tq)
 Until tms(num)<times
 tms(num)=tms(num)+1
 ns#(num)=ns#(num)+dtim#*benchmark(name$(num))
Next

n=1
f=WriteFile("btable.htm")
WriteLine f,"<HTML><BODY><TABLE>"
m$="<TR>"
While n<=tq
 If x=900 Or n=2 Then
  x=0
  y=y+12
    WriteLine f,m$
    m$="</TR><TR>"
 End If 
 v#=ns#(n)/times
 m$=m$+"<TD>"+name$(n)+": "+v#+" ns</TD>"
 x=x+300
 n=n+1
Wend
WriteLine f,m$+"</TR></TABLE></BODY></HTML>"
CloseFile f
ExecFile "btable.htm"

Data "template"
Data "A=B","D#=E#","I.T=J.T"
Data "A=B+C","D#=E#+F#","I.T=New T"
Data "A=B-C","D#=E#-F#","Delete I.T"
Data "A=B*C","D#=E#*F#","I.T=Before J.T"
Data "A=B/C","D#=E#/F#","I.T=After J.T"
Data "A=B^P","D#=E#^F#","I.T=First T"
Data "A=Abs(B)","D#=Abs(E#)","I.T=Last T"
Data "A=Sgn(B)","D#=Sgn(E#)","Insert I.T Before J.T"
Data "A=Floor(E#)","D#=Sin(E#)","Insert I.T After J.T"
Data "A=Int(E#)","D#=Cos(E#)","For I.T=Each T"
Data "A=Ceil(E#)","D#=Tan(E#)","Delete Each T"
Data "A=B And C","D#=ASin(R#)","A=Handle(J.T)"
Data "A=B Or C","D#=ACos(R#)","I.T=Object(Handle)"
Data "A=B Xor C","D#=ATan(E#)","L$=M$"
Data "A=B Mod PP","D#=ATan2(E#,F#)","L$=M$+S$"
Data "A=B Shl PP","D#=Sqr(E#)","L$=Left$(M$,SQ)"
Data "A=B Sar PP","D#=Log(G#)","L$=Right$(M$,SQ)"
Data "A=Asc(S$)","D#=Log10(G#)","L$=Chr$(SYM)"
Data "A=Len(M$)","D#=Exp(O#)","L$=Mid$(M$,SPOS,SQ)"
Data "A=Instr(M$,S$)","","L$=String$(S$,SYMQ)"
Data "A=Rand()","D#=Rnd()","L$=Replace$(M$,S$,' ')"
Data "A=INTF(B)","D#=FLOATF#(E#)","L$=STRINGF$(M$)"
Data "A=im(spos)","D#=fm#(spos)","L$=sm$(spos)"
Data "A=J\i","D#=J\f#","L$=J\s$"
Data "PokeByte","PokeFloat","PokeInt"
Data "PeekByte","PeekFloat","PeekInt"
Data "A=D#","D#=S$","S$=A"
Data "D#=A","S$=D#","A=S$"
Data "IF B=C","IF E#=F#","IF I.T=J.T"
Data "IF B>C","IF E#>F#","IF M$=L$"


Function benchmark(name$)

If First t=Null Then
 For n=1 To cq
  I.T=New T
  typ(n)=I
 Next
End If

B=Rand(-2000000000,2000000000)
C=Rand(-2000000000,2000000000)
P=Rand(-20,20)
PP=Rand(1,20)
SYM=Rand(0,255)
E#=Rnd(-2000000000,2000000000)
F#=Rnd(-2000000000,2000000000)
G#=Rnd(2,2000000000)
O#=Rnd(-20,20)
R#=Rnd(-1,1)
I.T=typ(Rand(1,cq))
J.T=typ(Rand(2,cq-1))
If i=Null Or j=Null Then Stop
SPOS=Rand(1,SYMQ)
s$=Chr$(sym)
bnk=CreateBank(symq+4)
For n=1 To SYMQ
 L$=Chr$(Rand(0,255))
 If nn=SPOS Then S$=L$
 M$=M$+L$
Next

If name$="I.T=New T" Then Delete Each T

k=1
tim=MilliSecs()
Select name$
 Case "template":For n=1 To cq:A=B:Next
 Case "A=B":For n=1 To cq:A=B:A=C:Next
 Case "D#=E#":For n=1 To cq:A=B:D#=E#:Next
 Case "A=E#":For n=1 To cq:A=B:A=E#:Next
 Case "D#=B":For n=1 To cq:A=B:D#=B:Next
 Case "A=B+C":For n=1 To cq:A=B+C:Next
 Case "D#=E#+F#":For n=1 To cq:D#=E#+F#:Next
 Case "A=B-C":For n=1 To cq:A=B-C:Next
 Case "D#=E#-F#":For n=1 To cq:D#=E#-F#:Next
 Case "A=B*C":For n=1 To cq:A=B*C:Next
 Case "D#=E#*F#":For n=1 To cq:D#=E#*F#:Next
 Case "A=B/C":For n=1 To cq:A=B/C:Next
 Case "D#=E#/F#":For n=1 To cq:D#=E#/F#:Next
 Case "A=B^P":For n=1 To cq/10:A=B^P:Next:k=10
 Case "D#=E#^F#":For n=1 To cq/10:D#=E#^F#:Next:k=10
 Case "A=Floor(E#)":For n=1 To cq:A=Floor(E#):Next
 Case "A=Int(E#)":For n=1 To cq:A=Int(E#):Next
 Case "A=Ceil(E#)":For n=1 To cq:A=Ceil(E#):Next
 Case "A=Abs(B)":For n=1 To cq:A=Abs(B):Next
 Case "D#=Abs(E#)":For n=1 To cq:D#=Abs(E#):Next
 Case "A=Sgn(B)":For n=1 To cq:A=Sgn(B):Next
 Case "D#=Sgn(E#)":For n=1 To cq:D#=Sgn(E#):Next
 Case "A=B Mod PP":For n=1 To cq:A=B Mod PP:Next
 Case "A=B And C":For n=1 To cq:A=B And C:Next
 Case "A=B Or C":For n=1 To cq:A=B Or C:Next
 Case "A=B Xor C":For n=1 To cq:A=B Xor C:Next
 Case "A=B Shl PP":For n=1 To cq:A=B Shl PP:Next
 Case "A=B Sar PP":For n=1 To cq:A=B Sar PP:Next
 Case "D#=Sin(E#)":For n=1 To cq:D#=Sin(E#):Next
 Case "D#=Cos(E#)":For n=1 To cq:D#=Cos(E#):Next
 Case "D#=Tan(E#)":For n=1 To cq:D#=Tan(E#):Next
 Case "D#=ASin(R#)":For n=1 To cq:D#=ASin(R#):Next
 Case "D#=ACos(R#)":For n=1 To cq:D#=ACos(R#):Next
 Case "D#=ATan(E#)":For n=1 To cq:D#=ATan(E#):Next
 Case "D#=ATan2(E#,F#)":For n=1 To cq:D#=ATan2(E#,F#):Next
 Case "A=Rand()":For n=1 To cq:A=Rand(0):Next
 Case "D#=Rnd()":For n=1 To cq:D#=Rnd(0.0):Next
 Case "A=INTF(B)":For n=1 To cq:A=INTF(B):Next
 Case "D#=FLOATF#(E#)":For n=1 To cq:D#=FLOATF#(E#):Next
 Case "I.T=J.T":For n=1 To cq:I.T=J.T:Next
 Case "I.T=New T":For n=1 To cq:I.T=New T:Next
 Case "Delete I.T":For n=1 To cq:Delete typ(n):Next
 Case "I.T=Before J.T":For n=1 To cq:I.T=Before J.T:Next
 Case "I.T=After J.T":For n=1 To cq:I.T=After J.T:Next
 Case "I.T=First T":For n=1 To cq:I.T=First T:Next
 Case "I.T=Last T":For n=1 To cq:I.T=Last T:Next
 Case "Insert I.T Before J.T":For n=1 To cq:Insert I.T Before J.T:Next
 Case "Insert I.T After J.T":For n=1 To cq:Insert I.T After J.T:Next
 Case "For I.T=Each T":For I.T=Each T:Next
 Case "A=Handle(J.T)":For n=1 To cq:A=Handle(J.T):Next
 Case "I.T=Object(Handle)":For n=1 To cq:I.T=Object.T(Handle(J.T)):Next
 Case "Delete Each T":Delete Each T
 Case "L$=M$":For n=1 To cq/10:L$=M$:Next:k=10
 Case "L$=M$+S$":For n=1 To cq/10:L$=M$+S$:Next:k=10
 Case "L$=Mid$(M$,SPOS,SQ)":For n=1 To cq/10:L$=Mid$(M$,SPOS,SymQ):Next:k=10
 Case "D#=Sqr(E#)":For n=1 To cq:D#=Sqr(G#):Next
 Case "L$=Left$(M$,SQ)":For n=1 To cq/10:L$=Left$(M$,SymQ):Next:k=10
 Case "D#=Log(G#)":For n=1 To cq:D#=Log(G#):Next
 Case "L$=Right$(M$,SQ)":For n=1 To cq/10:L$=Right$(M$,SymQ):Next:k=10
 Case "A=Asc(S$)":For n=1 To cq:A=Asc(S$):Next
 Case "D#=Log10(G#)":For n=1 To cq:D#=Log10(G#):Next
 Case "L$=String$(S$,SYMQ)":For n=1 To cq/10:L$=String$(S$,SYMQ):Next:k=10
 Case "A=Len(M$)":For n=1 To cq/10:A=Len(M$):Next:k=10
 Case "D#=Exp(O#)":For n=1 To cq:D#=Exp(O#):Next
 Case "L$=Chr$(SYM)":For n=1 To cq/10:L$=Chr$(SYM):Next:k=10
 Case "A=Instr(M$,S$)":For n=1 To cq/10:A=Instr(M$,S$):Next:k=10
 Case "L$=Replace$(M$,S$,' ')":For n=1 To cq/10:L$=Replace$(M$,S$," "):Next:k=10
 Case "L$=STRINGF$(M$)":For n=1 To cq/10:L$=STRINGF$(M$):Next:k=10
 Case "PokeByte":For n=1 To cq:PokeByte(bnk,spos,sym):Next
 Case "PokeInt":For n=1 To cq:PokeInt(bnk,spos,a):Next
 Case "PokeFloat":For n=1 To cq:PokeFloat(bnk,spos,e#):Next
 Case "PeekByte":For n=1 To cq:a=PeekByte(bnk,spos):Next
 Case "PeekInt":For n=1 To cq:a=PeekInt(bnk,spos):Next
 Case "PeekFloat":For n=1 To cq:d#=PeekFloat(bnk,spos):Next
 Case "A=im(spos)":For n=1 To cq:a=im(spos):Next
 Case "D#=fm#(spos)":For n=1 To cq:d#=fm#(spos):Next
 Case "L$=sm$(spos)":For n=1 To cq:l$=sm$(spos):Next
 Case "A=J\i":For n=1 To cq:a=J\i:Next
 Case "D#=J\f#":For n=1 To cq:d#=J\f#:Next
 Case "L$=J\s$":For n=1 To cq:l$=J\s$:Next
 Case "A=D#":For n=1 To cq:A=D#:Next
 Case "D#=S$":For n=1 To cq:D#=S$:Next
 Case "S$=A":For n=1 To cq/10:S$=A:Next:k=10
 Case "D#=A":For n=1 To cq:D#=A:Next:k=10
 Case "S$=D#":For n=1 To cq/10:S$=D#:Next
 Case "A=S$":For n=1 To cq:A=S$:Next
 Case "IF B=C"
  For n=1 To cq
   If B=C Then
   End If
  Next
 Case "IF E#=F#"
  For n=1 To cq
   If E#=F# Then
   End If
  Next
 Case "IF I.T=J.T"
  For n=1 To cq
   If I.T=J.T Then
   End If
  Next
 Case "IF B>C"
  For n=1 To cq
   If B>C Then
   End If
  Next
 Case "IF E#>F#"
  For n=1 To cq
   If E#>F# Then
   End If
  Next
 Case "IF M$=L$"
  For n=1 To cq
   If M$=L$ Then
   End If
  Next
End Select
tim=MilliSecs()-tim

If name$="I.T=New T" Then
 i.t=First t
 For n=1 To cq
  typ(n)=i
  i=After i
 Next
End If

Return tim*k
End Function

Function INTF(N)
Return N
End Function

Function FLOATF#(N#)
Return N#
End Function

Function STRINGF$(S$)
Return S$
End Function
