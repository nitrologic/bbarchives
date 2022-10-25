; ID: 1945
; Author: Matt Merkulov
; Date: 2007-03-13 12:46:54
; Title: Conway's Game of Life
; Description: Development of rectangular area filled with random cells

;Conway's Game of Life simulation by Matt Merkulov

SeedRnd MilliSecs ()

; Set constants: xres, yres - the sanction of the screen, xrect, yrect-
; - The sizes of the rectangular filled by cells(cages) in the casual order,
; cell - density of cells(cages) inside of this rectangular (%), fsiz - parameter,
; Influencing for the size of a field. The size - (2^fsiz) x (2^fsiz)

Const xres=800, yres=600, xrect=600, yrect=400, cell=30, fsiz=10

; Calculated constants: fsiz0 - width of a field, fsiz1 - border of an index
; Coordinates (X, Y=0.. fsiz1), fsiz2 - the size of the buffer under a field (fsiz0*fsiz),
; fsiz3 - border of the address of a cell in the buffer (X+Y*fsiz0=0.. fsiz3),
; xc, yc - shift of the screen in relation to a field (if the part of a field shows),
; x1, y1, x2, y2 - coordinates of corners of a rectangular filled by cells(cages)

Const fsiz0=1 Shl fsiz, fsiz1=fsiz0-1, fsiz2 = (fsiz0 Shl fsiz), fsiz3=fsiz2-1
Const xc = (fsiz0-xres) Shr 1, yc = (fsiz0-yres) Shr 1
Const x1 = (xres-xrect) Shr 1, x2 = ((xres+xrect) Shr 1)-1
Const y1 = (yres-yrect) Shr 1, y2 = ((yres+yrect) Shr 1)-1

; The variables used both in the program, and in procedure.
Global ib, bnk, dbnk, dend

Graphics xres, yres, 32

; The latent screen buffer
buf=CreateImage (xres, yres)
ib=ImageBuffer (buf)
LockBuffer ib

; Bank under attributes of cells
bnk=CreateBank (fsiz2)
; Bank under coordinates of cells(cages)
dbnk=CreateBank (fsiz2 Shl 2)

; Filling a file of shift of the address of a cell for definition of neighbours
Dim neig (8)
k =-fsiz0-1
For n=0 To 7
 If n=3 Then k =-1
 If n=5 Then k=fsiz0-1
 neig (n) =k
 k=k+1 + (n=3)
Next

; Filling a file of rules
Dim change (24)
change (3)=1
For n=16 To 24
 If n <> 18 And n <> 19 Then change (n) =1
Next

; Border of bank under "interesting" cells
dend =-4
; Filling with cells(cages) of a rectangular
For x=x1 To x2
 For y=y1 To y2
 ; A casual choice: whether to put a cell(cage) in the given place?
 If Rand (0,99) <cell Then putcell x, y
 Next
Next

Repeat

 ; Display of the buffer to the screen
 UnlockBuffer ib
 DrawBlock buf, 0,0

 ; Calculation of quantity(amount) of generations in second, their conclusion and кол-in "interesting"
 ; Cells on the screen
 Color 0,0,0
 Rect 0,0,50,10
 Color 255,255,255
 Text 0,0,1000.0 / (MilliSecs ()-fps)
 Text 0,8, dend Shr 2
 fps=MilliSecs ()

 LockBuffer ib

 ; A cycle on all "interesting" cells
 n=0
 While n <=dend
 ; Definition of the address of a cell and its(her) attributes
 pos=PeekInt (dbnk, n)
 k=PeekByte (bnk, pos)
 ; If it(she) is subject by change (it should be filled or cleared)-
 ; The attribute of change joins
 If change (k And 31) Then PokeByte bnk, pos, k Or 32
 ; If the empty cell has 0 neighbours - it(she) leaves from the list
 ; Also it is cleared of attribute zanesennosti
 If (k And 31) =0 Then
  PokeInt dbnk, n, PeekInt (dbnk, dend)
  PokeByte bnk, pos, 0
  dend=dend-4
 Else
  n=n+4
 End If
 Wend

 n=0
 ; The additional variable to not check newborn cells(cages)
 dend2=dend
 ; The second cycle on all "interesting" cells
 While n <=dend2
 ; Definition of the address of a cell and its(her) attributes
 pos=PeekInt (dbnk, n)
 k=PeekByte (bnk, pos)

 ; Check on attribute of change
 If k And 32
  ; Definition of attribute zapolnennosti
  v = (k And 16) Shr 4
  ; Definition of coordinates of a cell
  x = (pos And fsiz1)-xc
  y = (pos Shr fsiz)-yc
  ; Change of the image of a cell: if it(she) has been filled - to clear (color 0-
  ; Black), differently - to fill (color-1 - white)
  If x>=0 And x<xres And y>=0 And y<yres Then WritePixelFast x, y, v-1, ib
  ; Calculation of an increment of attribute kol-ва neighbours for neighbours of a cell:
  ; If the cell was filled, +1, cleared --1
  v=1-(v Shl 1)
  ; A cycle on all neighbours
  For nn=0 To 7
  ; Definition of the address of the next cell and its(her) attributes
  addr = (neig (nn) +pos) And fsiz3
  p=PeekByte (bnk, addr)
  ; If the attribute kol-ва neighbours at a cell, at which attribute increases
  ; zanesennosti it is equal 0 - we bring it(her) in bank "interesting" and
  ; We establish(install) value of this attribute equal 1
  If p=0 Then
   dend=dend+4
   PokeInt dbnk, dend, addr
   PokeByte bnk, addr, 65
  Else
   ; Differently - only change of attribute kol-ва neighbours
   PokeByte bnk, addr, p+v
  End If
  Next
  ; Change of attribute of filling and attribute of change (48=16+32)
  PokeByte bnk, pos, k Xor 48  
 End If  
 n=n+4
 Wend

 ; If the key "Esc" is pressed - we leave
Until KeyHit (1)

; The function filling a cell with coordinates (x, y) and making
; Additional actions.
Function putcell (x, y)

; Calculation of the address of a cell(cage) (according to shift of the screen otn. Fields)
pos=x+xc + ((y+yc) Shl fsiz)
; Entering in bank of neighbours and cells(cages)
For nn=0 To 8
 ; Definition of the address of a cell (command(team) AND zatsiklivaet a field, keeping
 ; The address within the limits of 0.. fsiz3)
 addr = (neig (nn) +pos) And fsiz3
 p=PeekByte (bnk, addr)
 ; Increase in attribute kol-ва neighbours of a cell on 1. If attribute zapolnennosti
 ; It is equal 0, the cell means is not brought in bank "interesting", therefore we bring.
 If p=0 Then
 ; Each address borrows(occupies) in bank 4 bajta
 dend=dend+4
 PokeInt dbnk, dend, addr
 PokeByte bnk, addr, 65
 Else
 PokeByte bnk, addr, p+1
 End If
Next
; We bring in bank of attributes on the address of a cell attribute "zapolnennosti"
; Minus 1 (as in the previous cycle the attribute " kol-ва has been added
; Neighbours ")
PokeByte bnk, pos, PeekByte (bnk, pos) + 15
; Display of a cell(cage) in the screen buffer
WritePixel x, y,-1, ib
End Function
