; ID: 1958
; Author: Matt Merkulov
; Date: 2007-03-14 11:34:58
; Title: Conway's Life: experiments
; Description: Some interesting additional stuff

;Conway's Life: experiments by Matt Merkulov

SeedRnd MilliSecs ()

Const xres=800, yres=600, fsiz=10, cell=30
; dst - a flag of the detector of stabilization, dstperiod - the greatest distinguished(recognized) period
; Configurations, dstpasses - quantity of checks, rep - a flag of recurrences, bnd - a flag of
; restrictions of a field, visible - display of an organism to the screen, explo - a mode of
; periodic "explosion", xper - frequency of "explosion" in generations, loadorg$ -
; - loading of an organism from a file, search - a mode of search of long-livers (0 - off,
; any positive number - minimal quantity of generations for record in a file)

Const dstperiod=3, dstpasses=5, xper=256, bnd=1, visible=1

Const explo=1, xrect=150, yrect=100
; Const dst=1, search=2000, xrect=7, yrect=7
; Const dst=1, loadorg $ = "locomot.png", xrect=0, yrect=0
; Const dst=1, loadorg $ = "virus.png", xrect=0, yrect=0

Const fsiz0=1 Shl fsiz, fsiz1=fsiz0-1, fsiz2 = (fsiz0 Shl fsiz), fsiz3=fsiz2-1
Const xc = (fsiz0-xres) Shr 1, yc = (fsiz0-yres) Shr 1
Const x1 = (xres-xrect) Shr 1, x2 = ((xres+xrect) Shr 1)-1
Const y1 = (yres-yrect) Shr 1, y2 = ((yres+yrect) Shr 1)-1

Global ib, bnk, dbnk, dend, cellq

Graphics xres, yres, 32
SetFont LoadFont (" Arial cyr ", 14)

buf=CreateImage (xres, yres)
ib=ImageBuffer (buf)

bnk=CreateBank (fsiz2)
dbnk=CreateBank (fsiz2 Shl 2)

Dim neig (8)
k =-fsiz0-1
For n=0 To 7
 If n=3 Then k =-1
 If n=5 Then k=fsiz0-1
 neig (n) =k
 k=k+1 + (n=3)
Next

Dim change (64)
For n=0 To 3
 Read m $
 For nn=0 To 8
 change (n*16+nn) =Sgn (Instr (m $, nn))
 Next
Next
Data "3", "0145678"; key rules
Data "23", "0245678"; rules for "explosion"

; A file for storage of the generated organism
Dim org (xrect, yrect)

If search Then f=WriteFile ("longlife.txt")

Repeat
 LockBuffer ib
 dend =-4

 If loadorg $ = "" Then
 For x=x1 To x2
  For y=y1 To y2
  If Rand (0,99) <cell Then putcell x, y: org (x-x1, y-y1) =1 Else org (x-x1, y-y1) =0
  Next
 Next
 Else
 i=LoadImage (loadorg $)
 ii=ImageBuffer (i)
 xsiz=ImageWidth (i)
 ysiz=ImageHeight (i)
 xx = (xres-xsiz) Shr 1
 yy = (yres-ysiz) Shr 1
 For x=0 To xsiz-1
  For y=0 To ysiz-1
  If ReadPixel (x, y, ii) =-1 Then putcell x+xx, y+yy
  Next
 Next
 FreeImage i
 End If

 ; An auxiliary file of the detector of stabilization: the first value - quantity of cells
 ; at the previous stage, the second - the counter of concurrences quantity of cells
 Dim dstcq (dstperiod, 1)

 Repeat

 UnlockBuffer ib

 If visible Then
  ; To exclude blinking, we print the text not on the screen, and in the buffer
  SetBuffer ib
  Color 0,0,0
  Rect 0,0,100,36
  Color 255,255,255
  Text 0,0, " FPS: " +1000.0 / (MilliSecs ()-fps)
  ; A conclusion kol-ва cells(cages) and generations on the screen
  Text 0,12, " Cells(Cages): " +cellq
  Text 0,24, " Generation: " +gen

  SetBuffer FrontBuffer ()
  DrawBlock buf, 0,0
 End If
 
 ; The counter of generations
 gen=gen+1
 ; "Explosion"
 If explo And (gen Mod xper) =0 Then add=32 Else add=0

 ; The detector of stabilization
 If dst Then
  ; A cycle on all periods
  For n=2 To dstperiod
  If gen Mod n=0 Then
   If cellq=dstcq (n, 0) Then
   ; If кол-in cells(cages) coincides with former - the counter of checks increases
   dstcq (n, 1) =dstcq (n, 1) +1
   ; If кол-in checks has reached(achieved) a limit - the organism was stabilized,
   ; The flag of an output(exit) joins
   If dstcq (n, 1) =dstpasses Then ex=1
   Else
   ; If кол-in cells(cages) differs - the counter is nulled
   dstcq (n, 0) =cellq
   dstcq (n, 1) =0
   End If
  End If
  Next
 End If

 ; If a flag of an output(exit) the key of a blank is included or pressed - is nulled it(him) and we leave
 ; From a cycle of development
 If ex Or KeyHit (57) Then ex=0:Exit
 fps=MilliSecs ()

 LockBuffer ib

 n=0
 While n <=dend
  pos=PeekInt (dbnk, n)
  k=PeekByte (bnk, pos)
  If change ((k And 31) +add) Then PokeByte bnk, pos, k Or 32
  If (k And 31) =0 Then
  PokeInt dbnk, n, PeekInt (dbnk, dend)
  PokeByte bnk, pos, 0
  dend=dend-4
  Else
  n=n+4
  End If
 Wend

 n=0
 dend2=dend
 While n <=dend2
  pos=PeekInt (dbnk, n)
  k=PeekByte (bnk, pos)
 
  If k And 32 Then
  If bnd=0 Or (pos> fsiz1 And (pos And fsiz1)> 0) Then
   v = (k And 16) Shr 4
   If visible Then
   x = (pos And fsiz1)-xc
   y = (pos Shr fsiz)-yc
   If x>=0 And x <xres And y>=0 And y <yres Then WritePixelFast x, y, v-1, ib
   End If
   v=1-(v Shl 1)
   ; If the cell was cleared, the counter of cells(cages) decreases on 1 if it was filled-
   ; Increases on 1
   cellq=cellq+v
   For nn=0 To 7
   addr = (neig (nn) +pos) And fsiz3
   p=PeekByte (bnk, addr)
   If p=0 Then
    dend=dend+4
    PokeInt dbnk, dend, addr
    PokeByte bnk, addr, 65
   Else
    PokeByte bnk, addr, p+v
   End If
   Next
   PokeByte bnk, pos, k Xor 48
  End If
  End If
  n=n+4
 Wend

 If KeyHit (1) Then End
 Forever

 ; Record of a long-liver in a file
 If search> 0 And search <=gen Then
 WriteLine f, gen
 For x=0 To xrect-1
  m $ = ""
  For y=0 To yrect-1
  If org (x, y) Then m $ = m $ + "0" Else m $ = m $ + "-"
  Next
  WriteLine f, m $
 Next
 WriteLine f, " "
 End If

 ; Before generation of a new organism the screen and the buffer of attributes is cleared, and also
 ; Counters of generations, cells(cages) and the index of a file "interesting" are nulled
 ; Cells
 SetBuffer ib
 Cls
 SetBuffer FrontBuffer ()
 FreeBank bnk
 bnk=CreateBank (fsiz2)
 gen=0
 cellq=0
Forever

Function putcell (x, y)
pos=x+xc + ((y+yc) Shl fsiz)
For nn=0 To 8
 addr = (neig (nn) +pos) And fsiz3
 p=PeekByte (bnk, addr)
 If p=0 Then
 dend=dend+4
 PokeInt dbnk, dend, addr
 PokeByte bnk, addr, 65
 Else
 PokeByte bnk, addr, p+1
 End If
Next
PokeByte bnk, pos, PeekByte (bnk, pos) + 15
If visible Then WritePixel x, y,-1, ib
; Increase in the counter of cells(cages) at unit
cellq=cellq+1
End Function
