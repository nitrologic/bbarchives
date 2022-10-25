; ID: 1965
; Author: Matt Merkulov
; Date: 2007-03-15 11:34:21
; Title: "Life" on infinite field
; Description: J. H. Conway's Game of Life on nearly infinite field

;J. H. Conway's Game of Life on nearly infinite field by Matt Merkulov

; The index and simultaneously a cell
Type ptr
 Field nxt.ptr[3]; following indexes in hierarchy
 Field prev.ptr; the previous index in hierarchy
 Field prevpos; an arrangement of the previous index
 Field neig.ptr[7]; addresses of neighbours(for a cell(cage))
 Field x, y, nq; coordinates and quantity(amount) of neighbours
End Type

; Indexes on cells for which change of a condition is possible(probable)
Type chang
 Field p.ptr
End Type

Const loadorg$="locomot.png"
; Const loadorg$="virus.png"
Const xres=800, yres=600

Global cellq, scrx, scry, ib

; Breakdown of a file of indexes on 3 lists
Dim pmark.ptr(3)
For n=0 To 3
 pmark(n)=New ptr
Next

Graphics xres, yres
SetFont LoadFont(" Arial cyr ", 14)

Dim change(24)
For n=0 To 1
 Read m$
 For nn=0 To 8
 change(n*16+nn)=Sgn(Instr(m$, nn))
 Next
Next
Data "3", "0145678"

i=CreateImage(xres, yres)
ib=ImageBuffer(i)

i2=LoadImage(loadorg$)
ib2=ImageBuffer(i2)
xsiz=ImageWidth(i2)
ysiz=ImageHeight(i2)
For x=0 To xsiz-1
 For y=0 To ysiz-1
 If ReadPixel(x, y, ib2) And 255 Then
  ; The first met cell - a starting point for creation of the others
  If cellq=0 Then
  cell.ptr=New ptr
  ptrq=ptrq+1
  cell\x=400
  cell\y=300
  xx=x
  yy=y
  End If
  newborn findcell(cell, x-xx, y-yy)
 End If
 Next
Next
FreeImage i2

Repeat
 
 DrawBlock i, 0,0

 gen=gen+1
 ; The cursor of the mouse moves to the center of the screen(to not cling to edges(territories))
 MoveMouse xres Sar 1, yres Sar 1

 If cellq=0 Then Exit

 ; All the cells subject to changes, pereeshchayutsya in the list ¹2
 For ch.chang=Each chang
 If change(ch\p\nq) Then Insert ch\p After pmark(2)
 Next
 Delete Each chang

 ; We change a condition of all cells(cages) from the list ¹2
 cell=pmark(2)
 Repeat
 cell=After cell
 If cell=pmark(3) Then Exit
 If cell\nq <16 Then
  newborn cell
 Else
  ; For all neighbours - reduction of their counter of neighbours
  For n=0 To 7
  cell2.ptr=cell\neig[n]
  cell2\nq=cell2\nq-1
  ; Entering the neighbour in the list of cells which, probably, will change the condition
  If change(cell2\nq) Then ch.chang=New chang: ch\p=cell2
  Next
  ; Deenergizing a bat zapolnennosti
  cell\nq=cell\nq And 15
  WritePixel scrx+cell\x, scry+cell\y, 0, ib
  cellq=cellq-1
  ; Entering the processed cell in the list of potentially changing cells
  If change(cell\nq) Then ch.chang=New chang: ch\p=cell
 End If
 Forever

 ; Cells from the list ¹2 pass all in the list ¹1(are stabilized)
 Insert pmark(2) Before pmark(3)

 ; Displacement of the cursor of the mouse is defined(determined)
 dx=MouseX()-xres Sar 1
 dy=MouseY()-yres Sar 1
 ; If the cursor was displaced, there is a copying of cells(cages)(all 1-st list)
 If dx <> 0 Or dy <> 0 Then
 scrx=scrx+dx
 scry=scry+dy

 SetBuffer ib
 Cls
 SetBuffer FrontBuffer()

 cell=pmark(1)
 Repeat
  cell=After cell
  If cell=pmark(2) Then Exit
  If cell\nq And 16 Then WritePixel scrx+cell\x, scry+cell\y,-1, ib
 Forever

 End If

 SetBuffer ib
 Color 0,0,0
 Rect 0,0,100,36
 Color 255,255,255
 Text 0,0, "FPS: "+1000.0 /(MilliSecs()-fps)
 Text 0,12, "Cells(Cages): "+cellq
 Text 0,24, "Generation: "+gen
 fps=MilliSecs()
 SetBuffer FrontBuffer()
 
Until KeyHit(1)
End

; Function of search of a cell in hierarchy on a starting point and displacement
; If the cell does not exist, it(she) is created together with a chain of indexes
Function findcell.ptr(cell.ptr, x, y)
; If displacement zero, result - a starting point
If x=0 And y=0 Then Return cell
; We remember coordinates of a required cell(in case it(she) should be created)
xx=x+cell\x
yy=y+cell\y
pmax=1; the Counter of a level in hierarchy
; The first stage - rise upwards
Repeat
 ; Addition of the new index from above if the top of hierarchy is reached(achieved)
 If cell\prev=Null Then
 p.ptr=New ptr
 Insert p After pmark(0)
 ; The position is defined(determined) by position of a required cell
 pos=(x <0)+(y <0) Shl 1
 p\nxt[pos]=cell
 cell\prev=p
 cell\prevpos=pos
 Else
 ; Differently - transition to higher level in hierarchy
 pos=cell\prevpos
 p.ptr=cell\prev
 End If
 ; Change of coordinates according to moving
 If pos And 1 Then x=x+pmax
 If pos And 2 Then y=y+pmax
 ; Increase of a level
 pmax=pmax Shl 1
 cell=p
 ; An output(exit) if the point is reached(achieved), whence it is possible to go down up to required
Until x>=0 And y>=0 And x <pmax And y <pmax

; The second stage - descent(release)
Repeat
 ; Downturn of a level
 pmax=pmax Shr 1
 ; Definition of a direction
 pos=((x And pmax)=pmax)+((y And pmax)=pmax) Shl 1

 ; Creation of the new index if the branch is absent
 If cell\nxt[pos]=Null Then
 p.ptr=New ptr
 Insert p After pmark(0)
 cell\nxt[pos]=p
 p\prev=cell
 p\prevpos=pos
 ; If we create a cell(cage)(the index of 1-st level) it is moved it(her) to the list ¹1 and
 ; We appropriate(give) the remembered coordinates
 If pmax=1 Then
  Insert p After pmark(1)
  p\x=xx
  p\y=yy
 End If
 End If
 cell=cell\nxt[pos]
 ; If will reach(achieve) a bottom of hierarchy(a level of cells(cages)) - an output(exit)
Until pmax=1
Return cell
End Function

; Function of a birth of a new cell(cage)
Function newborn(cell.ptr)
; Search, storing of neighbours and increase in their counter of quantity(amount) of neighbours
For xx=-1 To 1
 For yy=-1 To 1
 If xx Or yy Then
  cell2.ptr=findcell(cell, xx, yy)
  cell2\nq=cell2\nq+1
  ; Entering the neighbour in the list of cells which, probably, will change the condition
  If change(cell2\nq) Then ch.chang=New chang: ch\p=cell2
  cell\neig[n]=cell2
  n=n+1
 End If
 Next
Next
; Inclusion a bat zapolnennosti
cell\nq=cell\nq Or 16
; Entering the processed cell in the list of potentially changing cells
If change(cell\nq) Then ch.chang=New chang: ch\p=cell
WritePixel cell\x+scrx, cell\y+scry,-1, ib
cellq=cellq+1
End Function
