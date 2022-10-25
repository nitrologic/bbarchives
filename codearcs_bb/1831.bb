; ID: 1831
; Author: markcw
; Date: 2006-10-02 19:06:40
; Title: Planet generating program
; Description: Planet generation in various projections

;Translation of planet.c, planet generating program
;Copyright 1988-2003 Torben AE. Mogensen
;version of October 22 2003

;The program generates planet maps based on recursive spatial subdivision
;of a tetrahedron containing the globe. The output is BMP or PPM.

;The colours may optionally be modified according to latitude to move the
;icecaps lower closer to the poles, with a corresponding change in land
;colours.
;The Mercator map at magnification 1 is scaled to fit the width
;it uses the full height (it could extend infinitely)
;The orthographic projections are scaled so the full view would use the
;full Height. Areas outside the globe are coloured black.
;Stereographic and gnomic projections use the same scale as orthographic
;in the center of the picture, but distorts scale away from the center.

;It is assumed that pixels are square
;I have included procedures to print the maps as bmp or ppm
;bitmaps (portable pixel map) on standard output or specified files.

;I have tried to avoid using machine specific features, so it should
;be easy to port the program to any machine. Beware, though that due
;to different precision on different machines, the same seed numbers
;can yield very different planets.
;The primitive user interface is a result of portability concerns

Graphics 640,480,0,2
SetBuffer BackBuffer()

;these values can be changed to change world characteristics

Global debug = 1 ;show debug info
Global colorscheme = 1 ;specify colour scheme 0..5, default = 0
Global altColors = 0 ;Switch to alternative colour scheme

Global doshade = 0 ;Use 'bumpmap' shading
Global shade_angle# = 0 ;Angle of 'light' in bumpmap shading, eg. 150.0

Global Width = 512 ;Width in pixels, default = 256
Global Height = 256 ;Height in pixels, default = 128

Global view$ = "q" ;Projection: m=mercator, p=peter, q=squarep,
 ;M=Mollweide, S=Sinusoid, s=stereographic, o=orthographic
 ;g=gnomonic, a=azimuth, c=conical, h=heightfield, f=search

Global rseed# = 0.2 ;Seed as number between 0.0..1.0, eg. 0.3
Global M# = 0.19 ;Initial altitude, default = 0.3
Global dd1# = 0.15 ;Weight for altitude difference, default = 0.4
Global dd2# = 0.03 ;Weight for distance, default = 0.03
Global POW# = 0.2 ;Power for distance function, default = 0.47

Global scale# = 1.0 ;Magnification, default = 1.0
Global longi# = 0.0 ;Longitude of centre in degrees, default = 0.0
Global lat# = 0.0 ;Latitude of centre in degrees, default = 0.0
Global vgrid# = 0.0 ;Vertical gridsize in degrees, 0.0 = no grid
Global hgrid# = 0.0 ;Horisontal gridsize in degrees, 0.0 = no grid

Global latic = 0 ;Colour depends on latitude, default = only altitude
Global lighter = 0 ;Use lighter colours (original scheme only)

Global do_outline = 0 ;Trace the edges of land in black on colour map
Global do_bw = 0 ;Produce a black and white outline map

Global filename$ = "planet" ;map image file
Global file_type = 0 ;map file types, 0=BMP/1=PPM

Global searchfile$ = "search.txt" ;map search text file
Global increment# = 0.01 ;seed search increment, eg. 0.00000001

;globals used in calculations
Global BLACK=0
Global WHITE=1
Global BLUE0=2
Global BLUE1, LAND0, LAND1, LAND2, LAND4;
Global GREEN1, BROWN0, GREY0;
Global BACK = WHITE ;BLACK

Global shade;
Global Depth ;depth of subdivisions
Global r1#,r2#,r3#,r4# ;seeds
Global best = 500000

Global cla#,sla#,clo#,slo#;

Global DEG2RAD#=0.0174532918661 ;Pi/180
Global Rad2Deg#=(180/Float(Pi))

;arrays used in calculations
Dim col(256,128)
Dim shades(256,128)
Dim heights(256,128)
Dim rtable(256)
Dim gtable(256)
Dim btable(256)
Dim outx(256*128)
Dim outy(256*128)
Dim cl0(60,30);
Dim weight(30);
Dim colors(9,3)
Dim moll_table#(32)

;set colors array from data
Select colorscheme
 Case 1: Restore atlas_colors
 Case 2: Restore mars_colors
 Case 3: Restore grey_colors
 Case 4: Restore moon_colors
 Case 5: Restore cyan_colors
 Default: Restore colors
End Select
For i=0 To 8 ;copy the 9 base colors
 For j=0 To 2: Read colors(i,j): Next
Next

Restore moll_data
For i=0 To 18 ;copy the 19 moll values
 Read moll_table#(i)
Next

If (view = "h")
 Dim heights(Width,Height) ;heightmap
Else
 Dim col(Width,Height) ;map colors
EndIf
If (doshade)
 Dim shades(Width,Height) ;map shading
EndIf

If (debug)
 Print "> -s "+rseed+" -i "+M+" -a "+dd1+" -d "+dd2+" -p "+POW
 Print "> -w "+Width+" -h "+Height+" -B "+doshade
EndIf

;print error if unknown
Select (view)
 Case "m" ;mercator
 Case "p" ;peter
 Case "q" ;squarep
 Case "M" ;Mollweide
 Case "S" ;Sinusoid
 Case "s" ;stereographic
 Case "o" ;orthographic
 Case "g" ;gnomonic
 Case "a" ;azimuth
 Case "c" ;conical
 Case "h" ;heightfield
 Case "f" ;search
 Default : Print "Unknown projection: "+view : print_error()
End Select

If (view = "c")
 If (lat = 0) view = "m";
 ;Conical approaches mercator when lat -> 0
 If (Abs(lat) >= Pi - 0.000001) view = "s";
 ;Conical approaches stereo when lat -> +/- 90
EndIf

;init calculations
If (longi>180) longi = longi - 360;
longi = longi*DEG2RAD;
lat = lat*DEG2RAD;

sla = Sin(lat): cla = Cos(lat);
slo = Sin(longi): clo = Cos(longi);

Depth = 3*(Int(log_2(scale*Height)))+6;
r1 = rseed;
r1 = rand2(r1,r1);
r2 = rand2(r1,r1);
r3 = rand2(r1,r2);
r4 = rand2(r2,r3);

setcolours() ;set rgb arrays

If (debug) Print "+----+----+----+----+----+"

Select (view)
 Case "m" : mercator() ;Mercator projection
 Case "p" : peter() ;Peters projection (area preserving cylindrical)
 Case "q" : squarep() ;Square projection (equidistant latitudes)
 Case "M" : mollweide() ;Mollweide projection (area preserving)
 Case "S" : sinusoid() ;Sinusoid projection (area preserving)
 Case "s" : stereo() ;Stereographic projection
 Case "o" : orthographic() ;Orthographic projection
 Case "g" : gnomonic() ;Gnomonic projection
 Case "a" : azimuth() ;Area preserving azimuthal projection
 Case "c" : conical() ;Conical projection (conformal)
 Case "h" : heightfield() ;heightfield
 Case "f" ;Search
 file=WriteFile(searchfile)
 While (rseed<1)
  Restore map0 ;set map data
  readmap()
  search(file)
  best = 500000 ;reset
  rseed = rseed + increment ;next seed
  r1 = rseed;
  r1 = rand2(r1,r1);
  r2 = rand2(r1,r1);
  r3 = rand2(r1,r2);
  r4 = rand2(r2,r3);
 Wend
 CloseFile file
End Select

If (do_outline) makeoutline(do_bw) ;outlines

If (doshade) smoothshades() ;bumpmap shading

;add filetype extension
Select (file_type)
Case 1 : filename=filename+".ppm" ;ppm
Default : filename=filename+".bmp" ;bmp
End Select

;save map
Select (file_type)
 Case 1 ;ppm
  If (do_bw)
   printppmBW(filename);
  ElseIf (view <> "h")
   printppm(filename);
  Else
   printheights(filename);
  EndIf
 Default ;bmp
  If (do_bw)
   printbmpBW(filename);
  ElseIf (view <> "h")
   printbmp(filename);
  Else
   printheights(filename);
  EndIf
End Select

;draw map
image=CreateImage(Width,Height)
LockBuffer ImageBuffer(image)
For y=0 To ImageHeight(image)-1
 For x=0 To ImageWidth(image)-1
  If view="h"
   rgb=heights(x,y)
  Else
   r=rtable(col(x,y)) Shl 16
   g=gtable(col(x,y)) Shl 8
   b=btable(col(x,y))
   rgb=r+g+b
   If doshade
    s = (shades(x,y)/3)+85 ;smooth bumpmap
    r = s*rtable(col(x,y))/150;
    If (r>255) r=255;
    g = s*gtable(col(x,y))/150;
    If (g>255) g=255;
    b = s*btable(col(x,y))/150;
    If (b>255) b=255;
    rgb=(r Shl 16)+(g Shl 8)+b
   EndIf
  EndIf
  WritePixelFast x,y,rgb,ImageBuffer(image)
 Next
Next
UnlockBuffer ImageBuffer(image)
SetBuffer BackBuffer()

DrawImage image,50,200

WaitKey
End

;data labels

.colors ;colorscheme 0
Data 0,0,255 ;Dark blue depths
Data 0,128,255 ;Light blue shores
Data 0,255,0 ;Light green lowlands
Data 64,192,16 ;Dark green highlands
Data 64,192,16 ;Dark green Mountains
Data 128,128,32 ;Brown stoney peaks
Data 255,255,255 ;White - peaks
Data 0,0,0 ;Black - Space
Data 0,0,0 ;Black - Lines

.atlas_colors ;colorscheme 1
Data 0,0,192 ;Dark blue depths
Data 0,128,255 ;Light blue shores
Data 0,96,0 ;Dark green Lowlands
Data 0,224,0 ;Light green Highlands
Data 128,176,0 ;Brown mountainsides
Data 128,128,128 ;Grey stoney peaks
Data 255,255,255 ;White - peaks
Data 0,0,0 ;Black - Space
Data 0,0,0 ;Black - Lines

.mars_colors ;colorscheme 2
Data 55,28,75 ;0
Data 136,92,80 ;1
Data 148,100,82 ;2
Data 232,164,90 ;3
Data 248,180,92 ;4
Data 165,128,108 ;5
Data 255,255,255 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

.grey_colors ;colorscheme 3
Data 96,96,96 ;0
Data 152,152,152 ;2
Data 144,144,144 ;1
Data 168,168,168 ;3
Data 160,160,160 ;4
Data 144,144,144 ;5
Data 128,128,128 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

.moon_colors ;colorscheme 4
Data 112,112,98 ;0
Data 168,168,147 ;2
Data 160,160,140 ;1
Data 184,184,161 ;3
Data 176,176,154 ;4
Data 160,160,140 ;5
Data 144,144,126 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

.cyan_colors ;colorscheme 5
Data 74,112,150 ;0
Data 126,168,210 ;2
Data 120,160,200 ;1
Data 138,184,230 ;3
Data 132,176,220 ;4
Data 120,160,200 ;5
Data 108,144,180 ;6
Data 0,0,0 ;7
Data 0,0,0 ;8

.moll_data ;mollweide gridlines
Data 0.0, 0.0685055811, 0.1368109534
Data 0.2047150027, 0.2720147303, 0.3385041213
Data 0.4039727534, 0.4682040106, 0.5309726991
Data 0.5920417499, 0.6511575166, 0.7080428038
Data 0.7623860881, 0.8138239166, 0.8619100185
Data 0.9060553621, 0.9453925506, 0.9783738403, 1.0;

.map0 ;map data resembling earth
Data "::;--*;;@O;.;..-:**:;;::"
Data "-*o@O;*;-,;;o*O@@@@@O--;"
Data "..,O@Oo-,.;*--o*@@@O*;,."
Data "...;O-;...;O**oo@@@*;..."
Data "....;*;-.,*@@O*;*;o-...."
Data ".....:oO;,.;O@;.:,---;,."
Data ";;....*@O,..oO-...;;*;;;"
Data ",.....;O:...;*:...,Ooo,,"
Data "......-;...........,,;;,"
Data ".......;................"
Data "oooooooOoooooooooooooooo"

;functions

Function min(x,y)
 If x<y Then Return x Else Return y
End Function

Function max(x,y)
 If x<y Then Return y Else Return x
End Function

Function fmin#(x#,y#)
 If x<y Then Return x Else Return y
End Function

Function fmax#(x#,y#)
 If x<y Then Return y Else Return x
End Function

Function setcolours()

 Local i;
 Local crow;
 Local nocols = 256;

 If (debug)
  For crow = 0 To 8 ;MAXCOL-1
   Write "c"+crow+"="+colors(crow,0)+","+colors(crow,1)+","+colors(crow,2)+" ";
   If Not (crow+1) Mod 3 Print " "
  Next
 EndIf

 If (altColors)

  If (nocols < 8) nocols = 8;

  ;This Color table tries To follow the coloring conventions of
  ;several atlases.
  ;
  ;The First two colors are reserved For black And white
  ;1/4 of the colors are blue For the sea, dark being deep
  ;3/4 of the colors are land, divided as follows:
  ;nearly 1/2 of the colors are greens, with the low being dark
  ;1/8 of the colors shade from green through brown To grey
  ;1/8 of the colors are shades of grey For the highest altitudes
  ;
  ;The minimum Color table is:
  ;0 Black
  ;1 White
  ;2 Blue
  ;3 Dark Green
  ;4 Green
  ;5 Light Green
  ;6 Brown
  ;7 Grey
  ;And doesn't look very good. Somewhere between 24 And 32 colors
  ;is where this scheme starts looking good. 256, of course, is best.
    
  LAND0 = max(nocols / 4, BLUE0 + 1);
  BLUE1 = LAND0 - 1;
  GREY0 = nocols - (nocols / 8);
  GREEN1 = min(LAND0 + (nocols / 2), GREY0 - 2);
  BROWN0 = (GREEN1 + GREY0) / 2;
  LAND1 = nocols - 1;

  rtable(BLACK) = colors(7,0);
  gtable(BLACK) = colors(7,0);
  btable(BLACK) = colors(7,0);
  rtable(WHITE) = colors(6,0);
  gtable(WHITE) = colors(6,1);
  btable(WHITE) = colors(6,2);
  rtable(BLUE0) = colors(0,0);
  gtable(BLUE0) = colors(0,1);
  btable(BLUE0) = colors(0,2);

  For i=BLUE0+1 To BLUE1;
   rtable(i) = (colors(0,0)*(BLUE1-i)+colors(1,0)*(i-BLUE0))/(BLUE1-BLUE0);
   gtable(i) = (colors(0,1)*(BLUE1-i)+colors(1,1)*(i-BLUE0))/(BLUE1-BLUE0);
   btable(i) = (colors(0,2)*(BLUE1-i)+colors(1,2)*(i-BLUE0))/(BLUE1-BLUE0);
  Next
  For i=LAND0 To GREEN1-1
   rtable(i) = (colors(2,0)*(GREEN1-i)+colors(3,0)*(i-LAND0))/(GREEN1-LAND0);
   gtable(i) = (colors(2,1)*(GREEN1-i)+colors(3,1)*(i-LAND0))/(GREEN1-LAND0);
   btable(i) = (colors(2,2)*(GREEN1-i)+colors(3,2)*(i-LAND0))/(GREEN1-LAND0);
  Next
  For i=GREEN1 To BROWN0-1
   rtable(i) = (colors(3,0)*(BROWN0-i)+colors(4,0)*(i-GREEN1))/(BROWN0-GREEN1);
   gtable(i) = (colors(3,1)*(BROWN0-i)+colors(4,1)*(i-GREEN1))/(BROWN0-GREEN1);
   btable(i) = (colors(3,2)*(BROWN0-i)+colors(4,2)*(i-GREEN1))/(BROWN0-GREEN1);
  Next
  For i=BROWN0 To GREY0-1
   rtable(i) = (colors(4,0)*(GREY0-i)+colors(5,0)*(i-BROWN0))/(GREY0-BROWN0);
   gtable(i) = (colors(4,1)*(GREY0-i)+colors(5,1)*(i-BROWN0))/(GREY0-BROWN0);
   btable(i) = (colors(4,2)*(GREY0-i)+colors(5,2)*(i-BROWN0))/(GREY0-BROWN0);
  Next
  For i=GREY0 To nocols-1
   rtable(i) = (colors(5,0)*(nocols-i)+(colors(6,0)+1)*(i-GREY0))/(nocols-GREY0);
   gtable(i) = (colors(5,1)*(nocols-i)+(colors(6,1)+1)*(i-GREY0))/(nocols-GREY0);
   btable(i) = (colors(5,2)*(nocols-i)+(colors(6,2)+1)*(i-GREY0))/(nocols-GREY0);
  Next

 Else

  rtable(BLACK) = 0;
  gtable(BLACK) = 0;
  btable(BLACK) = 0;
  rtable(WHITE) = 255;
  gtable(WHITE) = 255;
  btable(WHITE) = 255;

  Local r, c;
  Local x#;

  While (lighter>0)
   For r = 0 To 7-1
    For c = 0 To 3-1
     x = Sqr(Float(colors(r,c))/256.0);
     colors(r,c) = Int(240.0*x+16);
    Next
   Next
   lighter=lighter-1
  Wend

  BLUE1 = (nocols-4)/2+BLUE0;

  If (BLUE1=BLUE0)
   rtable(BLUE0) = colors(0,0);
   gtable(BLUE0) = colors(0,1);
   btable(BLUE0) = colors(0,2);
  Else
   For i=BLUE0 To BLUE1;
    rtable(i) = (colors(0,0)*(BLUE1-i)+colors(1,0)*(i-BLUE0))/(BLUE1-BLUE0);
    gtable(i) = (colors(0,1)*(BLUE1-i)+colors(1,1)*(i-BLUE0))/(BLUE1-BLUE0);
    btable(i) = (colors(0,2)*(BLUE1-i)+colors(1,2)*(i-BLUE0))/(BLUE1-BLUE0);
   Next
  EndIf
  LAND0 = BLUE1+1: LAND2 = nocols-2: LAND1 = (LAND0+LAND2+1)/2;
  For i=LAND0 To LAND1-1
   rtable(i) = (colors(2,0)*(LAND1-i)+colors(3,0)*(i-LAND0))/(LAND1-LAND0);
   gtable(i) = (colors(2,1)*(LAND1-i)+colors(3,1)*(i-LAND0))/(LAND1-LAND0);
   btable(i) = (colors(2,2)*(LAND1-i)+colors(3,2)*(i-LAND0))/(LAND1-LAND0);
  Next

  If (LAND1=LAND2)
   rtable(LAND1) = colors(4,0);
   gtable(LAND1) = colors(4,1);
   btable(LAND1) = colors(4,2);
  Else
   For i=LAND1 To LAND2;
    rtable(i) = (colors(4,0)*(LAND2-i)+colors(5,0)*(i-LAND1))/(LAND2-LAND1);
    gtable(i) = (colors(4,1)*(LAND2-i)+colors(5,1)*(i-LAND1))/(LAND2-LAND1);
    btable(i) = (colors(4,2)*(LAND2-i)+colors(5,2)*(i-LAND1))/(LAND2-LAND1);
   Next
  EndIf
  LAND4 = nocols-1;
  rtable(LAND4) = colors(6,0);
  gtable(LAND4) = colors(6,1);
  btable(LAND4) = colors(6,2);

 EndIf

End Function

Function makeoutline(do_bw)

 Local i,j,k;

 Dim outx(Width*Height)
 Dim outy(Width*Height)

 k=0;
 For i=1 To Width-2
  For j=1 To Height-2
   If ((col(i,j) >= BLUE0 And col(i,j) <= BLUE1) And (col(i-1,j) >= LAND0 Or col(i+1,j) >= LAND0 Or col(i,j-1) >= LAND0 Or col(i,j+1) >= LAND0 Or col(i-1,j-1) >= LAND0 Or col(i-1,j+1) >= LAND0 Or col(i+1,j-1) >= LAND0 Or col(i+1,j+1) >= LAND0))
    outx(k) = i: outy(k) = j: k=k+1 ;outy(k++)
   EndIf
  Next
 Next

 If (do_bw)
  For i=0 To Width-1
   For j=0 To Height-1
    If (col(i,j) <> BLACK) col(i,j) = WHITE;
    While (k>0) ;(k-- >0)
     col(outx(k),outy(k)) = BLACK
     k=k-1
    Wend
   Next
  Next
 EndIf

End Function

Function smoothshades()

 Local i,j;

 For i=0 To Width-3 ;i<Width-2
  For j=0 To Height-3;
   shades(i,j) = (4*shades(i,j)+2*shades(i,j+1)+2*shades(i+1,j)+shades(i+1,j+2)+4)/9;
  Next
 Next

End Function

Function mercator() ;-pm

 Local y#,scale1#,cos2#,theta1#,theta0#;
 Local i,j,k;

 y = Sin(lat*Rad2Deg) ;Sin(lat)
 y = (1.0+y)/(1.0-y);
 y = 0.5*Log(y);
 k = Int(0.5*y*Width*scale/Pi);

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view ;%=mod
  y = Pi*(2.0*(j-k)-Height)/Width/scale;
  y = Exp(2.0*y);
  y = (y-1.0)/(y+1.0);
  scale1 = scale*Width/Height/Sqr(1.0-y*y)/Pi;
  cos2 = Sqr(1.0-y*y);
  Depth = 3*(Int(log_2(scale1*Height)))+3;
  For i = 0 To Width-1
   theta1 = longi-0.5*Pi+Pi*(2.0*i-Width)/Width/scale;
   col(i,j) = planet0(Cos(theta1*Rad2Deg)*cos2,y,-Sin(theta1*Rad2Deg)*cos2);
   ;col(i,j) = planet0(Cos(theta1)*cos2,y,-Sin(theta1)*cos2);
   If (doshade) shades(i,j) = shade;
  Next
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
   theta0=theta0-hgrid
   theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = Sin(DEG2RAD*theta1*Rad2Deg) ;Sin(DEG2RAD*theta1)
    y = (1.0+y)/(1.0-y);
    y = 0.5*Log(y);
    j = Height/2+Int(0.5*y*Width*scale/Pi)+k;
    If (j>=0 And j<Height)
     For i = 0 To Width-1: col(i,j) = BLACK: Next;
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta0 = 0.0
  While theta0>-360.0
   theta0=theta0-vgrid
   theta1 = theta0
   While theta1<360.0
    theta1=theta1+vgrid
    i = Int(0.5*Width*(1.0+scale*(DEG2RAD*theta1-longi)/Pi));
    If (i>=0 And i<Width)
     For j = 0 To Height-1: col(i,j) = BLACK: Next;
    EndIf
   Wend
  Wend
 EndIf

End Function

Function peter() ;-pp

 Local y#,cos2#,theta1#,scale1#,theta0#
 Local k,i,j,water,land

 y = 2.0*Sin(lat*Rad2Deg) ;2.0*Sin(lat)
 k = Int(0.5*y*Width*scale/Pi);
 water = 0: land = 0;

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  y = 0.5*Pi*(2.0*(j-k)-Height)/Width/scale;
  If (Abs(y)>1.0)
   For i = 0 To Width-1
    col(i,j) = BACK;
    If (doshade) shades(i,j) = 255;
   Next
  Else
   cos2 = Sqr(1.0-y*y);
   If (cos2>0.0)
    scale1 = scale*Width/Height/cos2/Pi;
    Depth = 3*(Int(log_2(scale1*Height)))+3;
    For i = 0 To Width-1
     theta1 = longi-0.5*Pi+Pi*(2.0*i-Width)/Width/scale;
     col(i,j) = planet0(Cos(theta1*Rad2Deg)*cos2,y,-Sin(theta1*Rad2Deg)*cos2);
     ;col(i,j) = planet0(Cos(theta1)*cos2,y,-Sin(theta1)*cos2);
     If (doshade) shades(i,j) = shade;
     If (col(i,j) < LAND0) Then water=water+1 Else land=land+1;
    Next
   EndIf
  EndIf
 Next

 If (debug)
  Print " ": Print "water percentage: "+(100*water/(water+land));
 EndIf

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
  theta0=theta0-hgrid
  theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = 2.0*Sin(DEG2RAD*theta1*Rad2Deg) ;2.0*Sin(DEG2RAD*theta1)
    j = Height/2+Int(0.5*y*Width*scale/Pi)+k;
    If (j>=0 And j<Height)
     For i = 0 To Width-1: col(i,j) = BLACK: Next;
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta0 = 0.0
  While theta0>-360.0
   theta0=theta0-vgrid
   theta1 = theta0
   While theta1<360.0
    theta1=theta1+vgrid
    i = Int(0.5*Width*(1.0+scale*(DEG2RAD*theta1-longi)/Pi));
    If (i>=0 And i<Width)
     For j = max(0,Height/2-Int(Width*scale/Pi)+k) To min(Height,Height/2+Int(Width*scale/Pi)+k)-1
      col(i,j) = BLACK;
     Next
    EndIf
   Wend
  Wend
 EndIf

End Function

Function squarep() ;-pq

 Local y#,scale1#,theta1#,cos2#,theta0#;
 Local k,i,j;

 k = Int(lat*Width*scale/Pi);

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  y = (2.0*(j-k)-Height)/Width/scale*Pi;
  If (Abs(y)>0.5*Pi) ;(Abs(y)>=0.5*Pi)
   For i = 0 To Width-1
    col(i,j) = BACK;
    If (doshade) shades(i,j) = 255;
   Next
  Else
   cos2 = Cos(y*Rad2Deg) ;Cos(y)
   If (cos2>0.0)
    scale1 = scale*Width/Height/cos2/Pi;
    Depth = 3*(Int(log_2(scale1*Height)))+3;
    For i = 0 To Width-1
     theta1 = longi-0.5*Pi+Pi*(2.0*i-Width)/Width/scale;
     col(i,j) = planet0(Cos(theta1*Rad2Deg)*cos2,Sin(y*Rad2Deg),-Sin(theta1*Rad2Deg)*cos2);
     ;col(i,j) = planet0(Cos(theta1)*cos2,Sin(y),-Sin(theta1)*cos2);
     If (doshade) shades(i,j) = shade;
    Next
   EndIf
  EndIf
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
  theta0=theta0-hgrid
  theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = DEG2RAD*theta1;
    j = Height/2+Int(0.5*y*Width*scale/Pi)+k;
    If (j>=0 And j<Height)
     For i = 0 To Width-1: col(i,j) = BLACK: Next;
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta0 = 0.0
  While theta0>-360.0
   theta0=theta0-vgrid
   theta1 = theta0
   While theta1<360.0
    theta1=theta1+vgrid
    i = Int(0.5*Width*(1.0+scale*(DEG2RAD*theta1-longi)/Pi));
    If (i>=0 And i<Width)
     For j = max(0,Height/2-Int(0.25*Pi*Width*scale/Pi)+k) To min(Height,Height/2+Int(0.25*Pi*Width*scale/Pi)+k)-1
      col(i,j) = BLACK;
     Next
    EndIf
   Wend
  Wend
 EndIf

End Function

Function mollweide() ;-pM

 Local x#,y#,y1#,zz#,scale1#,cos2#,theta1#,theta2#,theta0#;
 Local i,j,i1=1,k;

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  y1 = 2*(2.0*j-Height)/Width/scale;
  If (Abs(y1)>=1.0)
   For i = 0 To Width-1
    col(i,j) = BACK;
    If (doshade) shades(i,j) = 255;
   Next
  Else
   zz = Sqr(1.0-y1*y1);
   y = 2.0/Pi*(y1*zz+ASin(y1))
   cos2 = Sqr(1.0-y*y);
   If (cos2>0.0)
    scale1 = scale*Width/Height/cos2/Pi;
    Depth = 3*(Int(log_2(scale1*Height)))+3;
    For i = 0 To Width-1
     theta1 = Pi/zz*(2.0*i-Width)/Width/scale;
     If (Abs(theta1)>Pi)
      col(i,j) = BACK;
      If (doshade) shades(i,j) = 255;
     Else
      theta1 = theta1 + longi-0.5*Pi;
      col(i,j) = planet0(Cos(theta1*Rad2Deg)*cos2,y,-Sin(theta1*Rad2Deg)*cos2);
      ;col(i,j) = planet0(Cos(theta1)*cos2,y,-Sin(theta1)*cos2);
      If (doshade) shades(i,j) = shade;
     EndIf
    Next
   EndIf
  EndIf
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
  theta0=theta0-hgrid
  theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    theta2 = Abs(theta1);
    x = Floor(theta2/5.0): y = theta2/5.0-x;
    y = (1.0-y)*moll_table(Int(x))+y*moll_table(Int(x+1));
    If (theta1<0.0) y = -y;
     j = Height/2+Int(0.25*y*Width*scale);
     If (j>=0 And j<Height)
      For i = max(0,Width/2-Int(0.5*Width*scale*Sqr(1.0-y*y))) To min(Width,Width/2+Int(0.5*Width*scale*Sqr(1.0-y*y)))-1
      col(i,j) = BLACK;
      Next
     EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta0 = 0.0
  While theta0>-360.0
   theta0=theta0-vgrid
   theta1 = theta0
   While theta1<360.0
    theta1=theta1+vgrid
     If (DEG2RAD*theta1-longi+0.5*Pi>-Pi And DEG2RAD*theta1-longi+0.5*Pi<=Pi)
      x = 0.5*(DEG2RAD*theta1-longi+0.5*Pi)*Width*scale/Pi;
      j = max(0,Height/2-Int(0.25*Width*scale));
      y = 2*(2.0*j-Height)/Width/scale;
      i = Width/2+x*Sqr(1.0-y*y);
      For j = max(0,Height/2-Int(0.25*Width*scale)) To min(Height,Height/2+Int(0.25*Width*scale))
       y1 = 2*(2.0*j-Height)/Width/scale;
       If (Abs(y1)<=1.0)
        i1 = Width/2+x*Sqr(1.0-y1*y1);
        If (i1>=0 And i1<Width) col(i1,j) = BLACK;
       EndIf
       If (Abs(y)<=1.0)
        If (i<i1)
         For k=i+1 To i1-1 ;for (k=i+1; k<i1; k++)
          If (k>0 And k<Width) col(k,j) = BLACK;
         Next
        ElseIf (i>i1)
         k=i-1
         While k>i1 ;for (k=i-1; k>i1; k--)
          If (k>=0 And k<Width) col(k,j) = BLACK;
          k=k-1
         Wend
        EndIf
       EndIf
       y = y1;
       i = i1;
      Next
     EndIf
    Wend
   Wend
  EndIf

End Function

Function sinusoid() ;-pS

 Local y#,theta1#,theta2#,cos2#,l1#,i1#,scale1#,theta0#;
 Local k,i,j,l,c;

 k = Int(lat*Width*scale/Pi);

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  y = (2.0*(j-k)-Height)/Width/scale*Pi;
  If (Abs(y)>=0.5*Pi)
   For i = 0 To Width-1
    col(i,j) = BACK;
    If (doshade) shades(i,j) = 255;
   Next
  Else
   cos2 = Cos(y*Rad2Deg) ;Cos(y)
   If (cos2>0.0)
    scale1 = scale*Width/Height/cos2/Pi;
    Depth = 3*(Int(log_2(scale1*Height)))+3;
    For i = 0 To Width-1
     l = i*12/Width;
     l1 = l*Width/12.0;
     i1 = i-l1;
     theta2 = longi-0.5*Pi+Pi*(2.0*l1-Width)/Width/scale;
     theta1 = (Pi*(2.0*i1-Width/12)/Width/scale)/cos2;
     If (Abs(theta1)>Pi/12.0)
      col(i,j) = BACK;
      If (doshade) shades(i,j) = 255;
     Else
      col(i,j) = planet0(Cos((theta1+theta2)*Rad2Deg)*cos2,Sin(y*Rad2Deg),-Sin((theta1+theta2)*Rad2Deg)*cos2);
      ;col(i,j) = planet0(Cos(theta1+theta2)*cos2,Sin(y),-Sin(theta1+theta2)*cos2);
      If (doshade) shades(i,j) = shade;
     EndIf
    Next
   EndIf
  EndIf
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
   theta0=theta0-hgrid
   theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = DEG2RAD*theta1;
    cos2 = Cos(y*Rad2Deg) ;Cos(y)
    j = Height/2+Int(0.5*y*Width*scale/Pi)+k;
    If (j>=0 And j<Height)
     For i = 0 To Width-1
      l = i*12/Width;
      l1 = l*Width/12.0;
      i1 = i-l1;
      theta2 = (Pi*(2.0*i1-Width/12)/Width/scale)/cos2;
      If (Abs(theta2)<=Pi/12.0) col(i,j) = BLACK;
     Next
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta0 = 0.0
  While theta0>-360.0
   theta0=theta0-vgrid
   theta1 = theta0
   While theta1<360.0
    theta1=theta1+vgrid
    i = Int(0.5*Width*(1.0+scale*(DEG2RAD*theta1-longi)/Pi));
    If (i>=0 And i<Width)
     For j = max(0,Height/2-Int(0.25*Pi*Width*scale/Pi)+k) To min(Height,Height/2+Int(0.25*Pi*Width*scale/Pi)+k)-1
      y = (2.0*(j-k)-Height)/Width/scale*Pi;
      cos2 = Cos(y*Rad2Deg) ;Cos(y)
      l = i*12/Width;
      l1 = l*Width/12.0+Width/24.0;
      i1 = i-l1;
      c = l1+i1*cos2;
      If (c>=0 And c<Width) col(c,j) = BLACK;
     Next
    EndIf
   Wend
  Wend
 EndIf

End Function

Function stereo() ;-ps

 Local x#,y#,ymin#,ymax#,z#,zz#,x1#,y1#,z1#,theta1#,theta2#,theta0#;
 Local i,j;

 ymin = 2.0;
 ymax = -2.0;

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  For i = 0 To Width-1
   x = (2.0*i-Width)/Height/scale;
   y = (2.0*j-Height)/Height/scale;
   z = x*x+y*y;
   zz = 0.25*(4.0+z);
   x = x/zz;
   y = y/zz;
   z = (1.0-0.25*z)/zz;
   x1 = clo*x+slo*sla*y+slo*cla*z;
   y1 = cla*y-sla*z;
   z1 = -slo*x+clo*sla*y+clo*cla*z;
   If (y1 < ymin) ymin = y1;
   If (y1 > ymax) ymax = y1;
   col(i,j) = planet0(x1,y1,z1);
   If (doshade) shades(i,j) = shade;
  Next
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
   theta0=theta0-hgrid
   theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = Sin(DEG2RAD*theta1*Rad2Deg) ;Sin(DEG2RAD*theta1)
    If (ymin <= y And y <= ymax)
     zz = Sqr(1-y*y);
     theta2=-Pi
     While theta2<Pi
      theta2=theta2+0.5/Width/scale
      x = Sin(theta2*Rad2Deg)*zz ;Sin(theta2)*zz
      z = Cos(theta2*Rad2Deg)*zz ;Cos(theta2)*zz
      x1 = clo*x+slo*z;
      y1 = slo*sla*x+cla*y-clo*sla*z;
      z1 = -slo*cla*x+sla*y+clo*cla*z;
      If (Abs(z1)<1.0)
       i = 0.5*(Height*scale*2.0*x1*(1+z1)/(1.0-z1*z1)+Width);
       j = 0.5*(Height*scale*2.0*y1*(1+z1)/(1.0-z1*z1)+Height);
       If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
      EndIf
     Wend
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta2=-Pi
  While theta2<Pi
   theta2=theta2+0.5/Width/scale
   y = Sin(theta2*Rad2Deg) ;Sin(theta2)
   If (ymin <= y And y <= ymax)
    theta1 = 0.0
    While theta1<360.0
     theta1=theta1+vgrid
     x = Sin(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     z = Cos(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     ;x = Sin(DEG2RAD*theta1)*Cos(theta2);
     ;z = Cos(DEG2RAD*theta1)*Cos(theta2);
     x1 = clo*x+slo*z;
     y1 = slo*sla*x+cla*y-clo*sla*z;
     z1 = -slo*cla*x+sla*y+clo*cla*z;
     If (Abs(z1)<1.0)
      i = 0.5*(Height*scale*2.0*x1*(1+z1)/(1-z1*z1)+Width);
      j = 0.5*(Height*scale*2.0*y1*(1+z1)/(1-z1*z1)+Height);
      If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
     EndIf
    Wend
   EndIf
  Wend
 EndIf

End Function

Function orthographic() ;-po

 Local x#,y#,z#,x1#,y1#,z1#,ymin#,ymax#,theta1#,theta2#,zz#,theta0#;
 Local i,j;

 ymin = 2.0;
 ymax = -2.0;

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  For i = 0 To Width-1
   x = (2.0*i-Width)/Height/scale;
   y = (2.0*j-Height)/Height/scale;
   If (x*x+y*y>1.0)
    col(i,j) = BACK;
    If (doshade) shades(i,j) = 255;
   Else
    z = Sqr(1.0-x*x-y*y);
    x1 = clo*x+slo*sla*y+slo*cla*z;
    y1 = cla*y-sla*z;
    z1 = -slo*x+clo*sla*y+clo*cla*z;
    If (y1 < ymin) ymin = y1;
    If (y1 > ymax) ymax = y1;
    col(i,j) = planet0(x1,y1,z1);
    If (doshade) shades(i,j) = shade;
   EndIf
  Next
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
   theta0=theta0-hgrid
   theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = Sin(DEG2RAD*theta1*Rad2Deg) ;Sin(DEG2RAD*theta1)
    If (ymin <= y And y <= ymax)
     zz = Sqr(1-y*y);
     theta2=-Pi
     While theta2<Pi
      theta2=theta2+0.5/Width/scale
      x = Sin(theta2*Rad2Deg)*zz ;Sin(theta2)*zz
      z = Cos(theta2*Rad2Deg)*zz ;Cos(theta2)*zz
      x1 = clo*x+slo*z;
      y1 = slo*sla*x+cla*y-clo*sla*z;
      z1 = -slo*cla*x+sla*y+clo*cla*z;
      If (z1<=0.0)
       i = 0.5*(Height*scale*x1+Width);
       j = 0.5*(Height*scale*y1+Height);
       If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
      EndIf
     Wend
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta2=-Pi
  While theta2<Pi
   theta2=theta2+0.5/Width/scale
   y = Sin(theta2*Rad2Deg) ;Sin(theta2)
   If (ymin <= y And y <= ymax)
    theta1 = 0.0
    While theta1<360.0
     theta1=theta1+vgrid
     x = Sin(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     z = Cos(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     ;x = Sin(DEG2RAD*theta1)*Cos(theta2);
     ;z = Cos(DEG2RAD*theta1)*Cos(theta2);
     x1 = clo*x+slo*z;
     y1 = slo*sla*x+cla*y-clo*sla*z;
     z1 = -slo*cla*x+sla*y+clo*cla*z;
     If (0.0>=z1)
      i = 0.5*(Height*scale*x1+Width);
      j = 0.5*(Height*scale*y1+Height);
      If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
     EndIf
    Wend
   EndIf
  Wend
 EndIf

End Function

Function gnomonic() ;-pg

 Local x#,y#,z#,x1#,y1#,z1#,zz#,theta1#,theta2#,ymin#,ymax#,theta0#;
 Local i,j;

 ymin = 2.0;
 ymax = -2.0;

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  For i = 0 To Width-1
   x = (2.0*i-Width)/Height/scale;
   y = (2.0*j-Height)/Height/scale;
   zz = Sqr(1.0/(1.0+x*x+y*y));
   x = x*zz;
   y = y*zz;
   z = Sqr(1.0-x*x-y*y);
   x1 = clo*x+slo*sla*y+slo*cla*z;
   y1 = cla*y-sla*z;
   z1 = -slo*x+clo*sla*y+clo*cla*z;
   If (y1 < ymin) ymin = y1;
   If (y1 > ymax) ymax = y1;
   col(i,j) = planet0(x1,y1,z1);
   If (doshade) shades(i,j) = shade;
  Next
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
   theta0=theta0-hgrid
   theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = Sin(DEG2RAD*theta1*Rad2Deg) ;Sin(DEG2RAD*theta1)
    If (ymin <= y And y <= ymax)
     zz = Sqr(1-y*y);
     theta2=-Pi
     While theta2<Pi
      theta2=theta2+0.5/Width/scale
      x = Sin(theta2*Rad2Deg)*zz ;Sin(theta2)*zz
      z = Cos(theta2*Rad2Deg)*zz ;Cos(theta2)*zz
      x1 = clo*x-slo*z;
      y1 = slo*sla*x+cla*y+clo*sla*z;
      z1 = slo*cla*x-sla*y+clo*cla*z;
      If (z1<>0.0)
       i = 0.5*(Height*scale*x1/z1+Width);
       j = 0.5*(Height*scale*y1/z1+Height);
       If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
      EndIf
     Wend
    EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta2=-Pi
  While theta2<Pi
   theta2=theta2+0.5/Width/scale
   y = Sin(theta2*Rad2Deg) ;Sin(theta2)
   If (ymin <= y And y <= ymax)
    theta1 = 0.0
    While theta1<360.0
     theta1=theta1+vgrid
     x = Sin(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     z = Cos(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     ;x = Sin(DEG2RAD*theta1)*Cos(theta2);
     ;z = Cos(DEG2RAD*theta1)*Cos(theta2);
     x1 = clo*x-slo*z;
     y1 = slo*sla*x+cla*y+clo*sla*z;
     z1 = slo*cla*x-sla*y+clo*cla*z;
     If (z1<>0.0)
      i = 0.5*(Height*scale*x1/z1+Width);
      j = 0.5*(Height*scale*y1/z1+Height);
      If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
     EndIf
    Wend
   EndIf
  Wend
 EndIf

End Function

Function azimuth() ;-pa

 Local x#,y#,z#,x1#,y1#,z1#,zz#,theta1#,theta2#,ymin#,ymax#,theta0#;
 Local i,j;

 ymin = 2.0;
 ymax = -2.0;

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  For i = 0 To Width-1
   x = (2.0*i-Width)/Height/scale;
   y = (2.0*j-Height)/Height/scale;
   zz = x*x+y*y;
   z = 1.0-0.5*zz;
   If (z<-1.0)
    col(i,j) = BACK;
    If (doshade) shades(i,j) = 255;
   Else
    zz = Sqr(1.0-0.25*zz);
    x = x*zz;
    y = y*zz;
    x1 = clo*x+slo*sla*y+slo*cla*z;
    y1 = cla*y-sla*z;
    z1 = -slo*x+clo*sla*y+clo*cla*z;
    If (y1 < ymin) ymin = y1;
    If (y1 > ymax) ymax = y1;
    col(i,j) = planet0(x1,y1,z1);
    If (doshade) shades(i,j) = shade;
   EndIf
  Next
 Next

 If (hgrid <> 0.0) ;draw horisontal gridlines
  theta0 = 0.0
  While theta0>-90.0
   theta0=theta0-hgrid
   theta1 = theta0
   While theta1<90.0
    theta1=theta1+hgrid
    y = Sin(DEG2RAD*theta1*Rad2Deg) ;Sin(DEG2RAD*theta1)
    If (ymin <= y And y <= ymax)
     zz = Sqr(1-y*y);
     theta2=-Pi
     While theta2<Pi
      theta2=theta2+0.5/Width/scale
       x = Sin(theta2*Rad2Deg)*zz ;Sin(theta2)*zz
       z = Cos(theta2*Rad2Deg)*zz ;Cos(theta2)*zz
       x1 = clo*x-slo*z;
       y1 = slo*sla*x+cla*y+clo*sla*z;
       z1 = slo*cla*x-sla*y+clo*cla*z;
       If (z1<>-1.0)
        i = 0.5*(Height*scale*x1/Sqr(0.5+0.5*z1)+Width);
        j = 0.5*(Height*scale*y1/Sqr(0.5+0.5*z1)+Height);
        If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
       EndIf
      Wend
     EndIf
   Wend
  Wend
 EndIf

 If (vgrid <> 0.0) ;draw vertical gridlines
  theta2=-Pi
  While theta2<Pi
   theta2=theta2+0.5/Width/scale
   y = Sin(theta2*Rad2Deg) ;Sin(theta2)
   If (ymin <= y And y <= ymax)
    theta1 = 0.0
    While theta1<360.0
     theta1=theta1+vgrid
     x = Sin(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     z = Cos(DEG2RAD*theta1*Rad2Deg)*Cos(theta2*Rad2Deg);
     ;x = Sin(DEG2RAD*theta1)*Cos(theta2);
     ;z = Cos(DEG2RAD*theta1)*Cos(theta2);
     x1 = clo*x-slo*z;
     y1 = slo*sla*x+cla*y+clo*sla*z;
     z1 = slo*cla*x-sla*y+clo*cla*z;
     If (z1<>-1.0)
      i = 0.5*(Height*scale*x1/Sqr(0.5+0.5*z1)+Width);
      j = 0.5*(Height*scale*y1/Sqr(0.5+0.5*z1)+Height);
      If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
     EndIf
    Wend
   EndIf
  Wend
 EndIf

End Function

Function conical() ;-pc

 Local k1#,c#,y2#,x#,y#,zz#,x1#,y1#,z1#
 Local theta1#,theta2#,ymin#,ymax#,cos2#,theta0#;
 Local i,j;

 ymin = 2.0;
 ymax = -2.0;

 If (lat>0)

  k1 = 1.0/Sin(lat*Rad2Deg) ;1.0/Sin(lat)
  c = k1*k1;
  y2 = Sqr(c*(1.0-Sin((lat/k1)*Rad2Deg))/(1.0+Sin((lat/k1)*Rad2Deg)));
  ;y2 = Sqr(c*(1.0-Sin(lat/k1))/(1.0+Sin(lat/k1)));

  For j = 0 To Height-1
   If (debug And ((j Mod (Height/25)) = 0)) Write view
   For i = 0 To Width-1
    x = (2.0*i-Width)/Height/scale;
    y = (2.0*j-Height)/Height/scale+y2;
    zz = x*x+y*y;
    If (zz=0.0) Then theta1 = 0.0 Else theta1 = k1*ATan2(x,y);
    If (theta1<-Pi Or theta1>Pi)
     col(i,j) = BACK;
     If (doshade) shades(i,j) = 255;
    Else
     theta1 = theta1 + longi-0.5*Pi ;theta1 is longitude
     theta2 = k1*ASin((zz-c)/(zz+c)) ;theta2 is latitude
     If (theta2 > 0.5*Pi Or theta2 < -0.5*Pi)
      col(i,j) = BACK;
      If (doshade) shades(i,j) = 255;
     Else
      cos2 = Cos(theta2*Rad2Deg) ;Cos(theta2)
      y = Sin(theta2*Rad2Deg) ;Sin(theta2)
      If (y < ymin) ymin = y;
      If (y > ymax) ymax = y;
      col(i,j) = planet0(Cos(theta1*Rad2Deg)*cos2,y,-Sin(theta1*Rad2Deg)*cos2);
      ;col(i,j) = planet0(Cos(theta1)*cos2,y,-Sin(theta1)*cos2);
      If (doshade) shades(i,j) = shade;
     EndIf
    EndIf
   Next
  Next

  If (hgrid <> 0.0) ;draw horisontal gridlines
   theta0 = 0.0
   While theta0>-90.0
    theta0=theta0-hgrid
    theta1 = theta0
    While theta1<90.0
    theta1=theta1+hgrid
     y = Sin(DEG2RAD*theta1*Rad2Deg) ;Sin(DEG2RAD*theta1)
     If (ymin <= y And y <= ymax)
      zz = Sqr(c*(1.0+Sin(DEG2RAD*(theta1/k1)*Rad2Deg))/(1.0-Sin(DEG2RAD*(theta1/k1)*Rad2Deg)));
      ;zz = Sqr(c*(1.0+Sin(DEG2RAD*theta1/k1))/(1.0-Sin(DEG2RAD*theta1/k1)));
      theta2=-Pi+longi
      While theta2<Pi+longi
       theta2=theta2+0.5/Width/scale
       z1 = theta2-longi;
       x1 = zz*Sin((z1/k1)*Rad2Deg) ;zz*Sin(z1/k1)
       y1 = zz*Cos((z1/k1)*Rad2Deg) ;zz*Cos(z1/k1)
       i = 0.5*(Height*scale*x1+Width);
       j = 0.5*(Height*scale*(y1-y2)+Height);
       If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
      Wend
     EndIf
    Wend
   Wend
  EndIf

  If (vgrid <> 0.0) ;draw vertical gridlines
   theta1=-0.5*Pi
   While theta1<0.5*Pi
    theta1=theta1+0.5/Width/scale
    y = Sin(theta1*Rad2Deg) ;Sin(theta1)
    If (ymin <= y And y <= ymax)
     zz = Sqr(c*(1.0+Sin(theta1/k1*Rad2Deg))/(1.0-Sin(theta1/k1*Rad2Deg)));
     ;zz = Sqr(c*(1.0+Sin(theta1/k1))/(1.0-Sin(theta1/k1)));
     theta0 = 0.0
     While theta0>-180.0+longi/DEG2RAD
      theta0=theta0-vgrid
      theta2 = theta0
      While theta2<180.0+longi/DEG2RAD
       theta2=theta2+vgrid
       z1 = DEG2RAD*theta2-longi
       x1 = zz*Sin(z1/k1*Rad2Deg) ;zz*Sin(z1/k1)
       y1 = zz*Cos(z1/k1*Rad2Deg) ;zz*Cos(z1/k1)
       i = 0.5*(Height*scale*x1+Width);
       j = 0.5*(Height*scale*(y1-y2)+Height);
       If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
      Wend
     Wend
    EndIf
   Wend
  EndIf

 Else

  k1 = 1.0/Sin(lat*Rad2Deg) ;1.0/Sin(lat)
  c = k1*k1;
  y2 = Sqr(c*(1.0-Sin(lat/k1*Rad2Deg))/(1.0+Sin(lat/k1*Rad2Deg)));
  ;y2 = Sqr(c*(1.0-Sin(lat/k1))/(1.0+Sin(lat/k1)));

  For j = 0 To Height-1
   If (debug And ((j Mod (Height/25)) = 0)) Write view
   For i = 0 To Width-1
    x = (2.0*i-Width)/Height/scale;
    y = (2.0*j-Height)/Height/scale-y2;
    zz = x*x+y*y;
    If (zz=0.0) Then theta1 = 0.0 Else theta1 = -k1*ATan2(x,-y);
    If (theta1<-Pi Or theta1>Pi)
     col(i,j) = 2;BACK;
     If (doshade) shades(i,j) = 255;
    Else
     theta1 = theta1 + longi-0.5*Pi ;theta1 is longitude
     theta2 = k1*ASin((zz-c)/(zz+c)) ;theta2 is latitude
     If (theta2 > 0.5*Pi Or theta2 < -0.5*Pi)
      col(i,j) = BACK;
      If (doshade) shades(i,j) = 255;
     Else
      cos2 = Cos(theta2*Rad2Deg) ;Cos(theta2)
      y = Sin(theta2*Rad2Deg) ;Sin(theta2)
      If (y < ymin) ymin = y;
      If (y > ymax) ymax = y;
      col(i,j) = planet0(Cos(theta1*Rad2Deg)*cos2,y,-Sin(theta1*Rad2Deg)*cos2);
      ;col(i,j) = planet0(Cos(theta1)*cos2,y,-Sin(theta1)*cos2);
      If (doshade) shades(i,j) = shade;
     EndIf
    EndIf
   Next
  Next

  If (hgrid <> 0.0) ;draw horisontal gridlines
   theta0 = 0.0
   While theta0>-90.0
    theta0=theta0-hgrid
    theta1 = theta0
    While theta1<90.0
     theta1=theta1+hgrid
     y = Sin(DEG2RAD*theta1*Rad2Deg) ;Sin(DEG2RAD*theta1)
     If (ymin <= y And y <= ymax)
      zz = Sqr(c*(1.0+Sin(DEG2RAD*theta1/k1*Rad2Deg))/(1.0-Sin(DEG2RAD*theta1/k1*Rad2Deg)));
      ;zz = Sqr(c*(1.0+Sin(DEG2RAD*theta1/k1))/(1.0-Sin(DEG2RAD*theta1/k1)));
      theta2=-Pi+longi
      While theta2<Pi+longi
       theta2=theta2+0.5/Width/scale
       z1 = theta2-longi;
       x1 = -zz*Sin(z1/k1*Rad2Deg) ;-zz*Sin(z1/k1)
       y1 = -zz*Cos(z1/k1*Rad2Deg) ;-zz*Cos(z1/k1)
       i = 0.5*(Height*scale*x1+Width);
       j = 0.5*(Height*scale*(y1+y2)+Height);
       If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
      Wend
     EndIf
    Wend
   Wend
  EndIf

  If (vgrid <> 0.0) ;draw vertical gridlines
   theta1=-0.5*Pi
   While theta1<0.5*Pi
    theta1=theta1+0.5/Width/scale
    y = Sin(theta1*Rad2Deg) ;Sin(theta1)
    If (ymin <= y And y <= ymax)
     zz = Sqr(c*(1.0+Sin(theta1/k1*Rad2Deg))/(1.0-Sin(theta1/k1*Rad2Deg)));
     ;zz = Sqr(c*(1.0+Sin(theta1/k1))/(1.0-Sin(theta1/k1)));
     theta0 = 0.0
     While theta0>-180.0+longi/DEG2RAD
      theta0=theta0-vgrid
      theta2 = theta0
      While theta2<180.0+longi/DEG2RAD
       theta2=theta2+vgrid
       z1 = DEG2RAD*theta2-longi;
       x1 = -zz*Sin(z1/k1*Rad2Deg) ;-zz*Sin(z1/k1)
       y1 = -zz*Cos(z1/k1*Rad2Deg) ;-zz*Cos(z1/k1)
       i = 0.5*(Height*scale*x1+Width);
       j = 0.5*(Height*scale*(y1+y2)+Height);
       If (i>=0 And i<Width And j>=0 And j<Height) col(i,j) = BLACK;
      Wend
     Wend
    EndIf
   Wend
  EndIf

 EndIf

End Function

Function heightfield()

 Local x#,y#,z#,x1#,y1#,z1#
 Local i,j;

 For j = 0 To Height-1
  If (debug And ((j Mod (Height/25)) = 0)) Write view
  For i = 0 To Width-1
   x = (2.0*i-Width)/Height/scale;
   y = (2.0*j-Height)/Height/scale;
   If (x*x+y*y>1.0)
    heights(i,j) = 0;
   Else
    z = Sqr(1.0-x*x-y*y);
    x1 = clo*x+slo*sla*y+slo*cla*z;
    y1 = cla*y-sla*z;
    z1 = -slo*x+clo*sla*y+clo*cla*z;
    heights(i,j) = 10000000*planet1(x1,y1,z1);
   EndIf
  Next
 Next

End Function

Function readmap()

 Local i,j;
 Local y#;
 Local c$,cline$ ;char

 Width = 47: Height = 21;

 For j = 0 To Height-1
  y = 0.5*7.5*(2.0*j-Height+1);
  y = Cos(DEG2RAD*y*Rad2Deg) ;Cos(DEG2RAD*y)
  weight(j) = Int(100.0*y+0.5);
 Next

 For j = 0 To Height-1 Step 2 ;j+=2
  Read cline ;read map data line
  For i = 0 To Width-1 Step 2 ;i+=2
   c = Mid(cline,1+(i/2),1) ;getchar()
   Select (c)
   Case ".": cl0(i,j) = -8;
   Case ",": cl0(i,j) = -4;
   Case ":": cl0(i,j) = -2;
   Case ";": cl0(i,j) = -1;
   Case "-": cl0(i,j) = 0;
   Case "*": cl0(i,j) = 1;
   Case "o": cl0(i,j) = 2;
   Case "O": cl0(i,j) = 4;
   Case "@": cl0(i,j) = 8;
   Default: Print "Wrong map symbol: "+c
   End Select
   If (i>0) cl0(i-1,j) = (cl0(i,j)+cl0(i-2,j))/2;
  Next
  If (debug) Print "line "+cline
 Next

 For j = 1 To Height-1 Step 2 ;j+=2
  For i = 0 To Width-1
   cl0(i,j) = (cl0(i,j-1)+cl0(i,j+1))/2;
  Next
 Next

End Function

Function search(file)

 Local y#,cos2#,theta1#,scale1#
 Local y2#,cos22#,theta12#;
 Local i,j,k,l,c,c1,c2,c3,errcount,errcount1,ascii$

 For j = 0 To Height-1
  y = 0.5*7.5*(2.0*j-Height+1);
  y = Sin(DEG2RAD*y*Rad2Deg) ;Sin(DEG2RAD*y)
  scale1 = Width/Height/Sqr(1.0-y*y)/Pi;
  cos2 = Sqr(1.0-y*y);
  y2 = 0.5*7.5*(2.0*j-Height+1.5);
  y2 = Sin(DEG2RAD*y2*Rad2Deg) ;Sin(DEG2RAD*y2)
  cos2 = Sqr(1.0-y2*y2);
  Depth = 3*(Int(log_2(scale1*Height)))+6;
  For i = 0 To Width-1
   theta1 = -0.5*Pi+Pi*(2.0*i-Width)/Width;
   theta12 = -0.5*Pi+Pi*(2.0*i+0.5-Width)/Width;
   c = 128+1000*planet1(Cos(theta1*Rad2Deg)*cos2,y,-Sin(theta1*Rad2Deg)*cos2);
   c1 = 128+1000*planet1(Cos(theta12*Rad2Deg)*cos2,y,-Sin(theta12*Rad2Deg)*cos2);
   c2 = 128+1000*planet1(Cos(theta1*Rad2Deg)*cos22,y2,-Sin(theta1*Rad2Deg)*cos22);
   c3 = 128+1000*planet1(Cos(theta12*Rad2Deg)*cos22,y2,-Sin(theta12*Rad2Deg)*cos22);
   ;c = 128+1000*planet1(Cos(theta1)*cos2,y,-Sin(theta1)*cos2);
   ;c1 = 128+1000*planet1(Cos(theta12)*cos2,y,-Sin(theta12)*cos2);
   ;c2 = 128+1000*planet1(Cos(theta1)*cos22,y2,-Sin(theta1)*cos22);
   ;c3 = 128+1000*planet1(Cos(theta12)*cos22,y2,-Sin(theta12)*cos22);
   c = (c+c1+c2+c3)/4.0;
   If (c<0) c = 0;
   If (c>255) c = 255;
   col(i,j) = c;
  Next
 Next

 For k=0 To Width-1
  For l=-20 To 20 Step 2 ;l+=2
   errcount = 0;

   For j = 0 To Height-1
    errcount1 = 0;
    For i = 0 To Width-1
     If (cl0(i,j)<0 And col((i+k) Mod Width,j) > 128-l)
      errcount1=errcount1-cl0(i,j);
     EndIf
     If (cl0(i,j)>0 And col((i+k) Mod Width,j) <= 128-l)
      errcount1=errcount1+cl0(i,j);
     EndIf
    Next
    errcount = errcount + weight(j)*errcount1;
   Next

   If (errcount < best)
    ascii="Errors: "+errcount+", parameters: -s "+rseed+" -l "+((360.0*k)/(Width+1))+" -i "+(M+l/1000.0)
    Print ascii
    best = errcount;
    For j = 0 To Height-1
     If j=0 ;first line of ascii map
      WriteByte file,13: WriteByte file,10 ;newline
      For i=1 To Len(ascii): WriteByte file,Asc(Mid(ascii,i,1)): Next
      WriteByte file,13: WriteByte file,10 ;newline
     EndIf
     For i = 0 To Width-1
      If (col((i+k) Mod Width,j) <= 128-l)
       WriteByte file,Asc(".") ;putchar('.');
      Else
       WriteByte file,Asc("O")
      EndIf
     Next
     WriteByte file,13: WriteByte file,10 ;newline
    Next
   EndIf
  Next
 Next

End Function

Function planet0(x#,y#,z#)
 ;x#,y#,z#

 Local alt#;
 Local colour;

 alt = planet1(x,y,z);

 If (altColors)

  Local snow# = 0.125;
  Local tree# = snow * 0.5;
  Local bare# = (tree + snow) / 2.0;
    
  If (latic)
   snow = snow - (0.13 * (y*y*y*y*y*y));
   bare = bare - (0.12 * (y*y*y*y*y*y));
   tree = tree - (0.11 * (y*y*y*y*y*y));
  EndIf

  If (alt > 0) ;Land
   If (alt > snow) ;Snow: White
    colour = WHITE;
   ElseIf (alt > bare) ;Snow: Grey - White
    colour = GREY0+Int((1+LAND1-GREY0) * (alt-bare)/(snow-bare));
    If (colour > LAND1) colour = LAND1;
   ElseIf (alt > tree) ;Bare: Brown - Grey
    colour = GREEN1+Int((1+GREY0-GREEN1) * (alt-tree)/(bare-tree));
    If (colour > GREY0) colour = GREY0;
   Else ;Green: Green - Brown
    colour = LAND0+Int((1+GREEN1-LAND0) * (alt)/(tree));
    If (colour > GREEN1) colour = GREEN1;
   EndIf
  Else ;Sea
   alt = alt/2;
   If (alt > snow) ;Snow: White
    colour = WHITE;
   ElseIf (alt > bare)
    colour = GREY0+Int((1+LAND1-GREY0) * (alt-bare)/(snow-bare));
    If (colour > LAND1) colour = LAND1;
   Else
    colour = BLUE1+Int((BLUE1-BLUE0+1)*(25*alt));
    If (colour<BLUE0) colour = BLUE0;
   EndIf
  EndIf

 Else ;calculate colour

  If (alt <=0.0) ;if below sea level then
   If (latic And y*y+alt >= 0.98)
    colour = LAND4 ;white if close to poles
   Else
    colour = BLUE1+Int((BLUE1-BLUE0+1)*(10*alt)) ;blue scale otherwise
    If (colour<BLUE0) colour = BLUE0;
   EndIf
  Else
   If (latic) alt = alt + 0.10204*y*y ;altitude adjusted with latitude
   If (alt >= 0.1) ;if high then
    colour = LAND4;
   Else ;else green to brown scale
    colour = LAND0+Int((LAND2-LAND0+1)*(10*alt));
    If (colour>LAND2) colour = LAND2;
   EndIf
  EndIf

 EndIf

 Return (colour);

End Function

Function planet#(a#,b#,c#,d#,as#,bs#,cs#,ds#,ax#,ay#,az#,bx#,by#,bz#,cx#,cy#,cz#,dx#,dy#,dz#,x#,y#,z#,level)
 ;a#,b#,c#,d# = altitudes of the 4 vertices
 ;as#,bs#,cs#,ds# = seeds of the 4 vertices
 ;ax#,ay#,az#,bx#,by#,bz#,cx#,cy#,cz#,dx#,dy#,dz# = vertex coordinates
 ;x#,y#,z# = goal point
 ;level = levels to go

 Local ssa#,ssb#,ssc#,ssd#,ssas#,ssbs#,sscs#,ssds#,ssax#,ssay#,ssaz#
 Local ssbx#,ssby#,ssbz#,sscx#,sscy#,sscz#,ssdx#,ssdy#,ssdz#;

 Local abx#,aby#,abz#,acx#,acy#,acz#,adx#,ady#,adz#;
 Local bcx#,bcy#,bcz#,bdx#,bdy#,bdz#,cdx#,cdy#,cdz#;
 Local lab#,lac#,lad#,lbc#,lbd#,lcd#;
 Local ex#,ey#,ez#,e#,es#,es1#,es2#,es3#;
 Local eax#,eay#,eaz#,epx#,epy#,epz#;
 Local ecx#,ecy#,ecz#,edx#,edy#,edz#;
 Local x1#,y1#,z1#,x2#,y2#,z2#,l1#,tmp#;

 If (level>0)

  If (level=11)
   ssa=a: ssb=b: ssc=c: ssd=d: ssas=as: ssbs=bs: sscs=cs: ssds=ds;
   ssax=ax: ssay=ay: ssaz=az: ssbx=bx: ssby=by: ssbz=bz;
   sscx=cx: sscy=cy: sscz=cz: ssdx=dx: ssdy=dy: ssdz=dz;
  EndIf

  abx = ax-bx: aby = ay-by: abz = az-bz;
  acx = ax-cx: acy = ay-cy: acz = az-cz;
  lab = abx*abx+aby*aby+abz*abz;
  lac = acx*acx+acy*acy+acz*acz;

  If (lab<lac)
   Return (planet(a,c,b,d,as,cs,bs,ds,ax,ay,az,cx,cy,cz,bx,by,bz,dx,dy,dz,x,y,z,level));
  Else
   adx = ax-dx: ady = ay-dy: adz = az-dz;
   lad = adx*adx+ady*ady+adz*adz;
   If (lab<lad)
    Return (planet(a,d,b,c,as,ds,bs,cs,ax,ay,az,dx,dy,dz,bx,by,bz,cx,cy,cz,x,y,z,level));
   Else
    bcx = bx-cx: bcy = by-cy: bcz = bz-cz;
    lbc = bcx*bcx+bcy*bcy+bcz*bcz;
    If (lab<lbc)
     Return (planet(b,c,a,d,bs,cs,as,ds,bx,by,bz,cx,cy,cz,ax,ay,az,dx,dy,dz,x,y,z,level));
    Else
     bdx = bx-dx: bdy = by-dy: bdz = bz-dz;
     lbd = bdx*bdx+bdy*bdy+bdz*bdz;
     If (lab<lbd)
      Return (planet(b,d,a,c,bs,ds,as,cs,bx,by,bz,dx,dy,dz,ax,ay,az,cx,cy,cz,x,y,z,level));
     Else
      cdx = cx-dx: cdy = cy-dy: cdz = cz-dz;
      lcd = cdx*cdx+cdy*cdy+cdz*cdz;
      If (lab<lcd)
       Return (planet(c,d,a,b,cs,ds,as,bs,cx,cy,cz,dx,dy,dz,ax,ay,az,bx,by,bz,x,y,z,level));
      Else
       es = rand2(as,bs);
       es1 = rand2(es,es);
       es2 = 0.5+0.1*rand2(es1,es1);
       es3 = 1.0-es2;
       If (ax=bx) ;very unlikely to ever happen
        ex = 0.5*ax+0.5*bx: ey = 0.5*ay+0.5*by: ez = 0.5*az+0.5*bz;
       ElseIf (ax<bx)
        ex = es2*ax+es3*bx: ey = es2*ay+es3*by: ez = es2*az+es3*bz;
       Else
        ex = es3*ax+es2*bx: ey = es3*ay+es2*by: ez = es3*az+es2*bz;
       EndIf
       If (lab>1.0) lab = lab^0.75 ;pow(lab,0.75);
       e = 0.5*(a+b)+es*dd1*Abs(a-b)+es1*dd2*(lab^POW) ;pow(lab,POW);
       eax = ax-ex: eay = ay-ey: eaz = az-ez;
       epx =  x-ex: epy =  y-ey: epz =  z-ez;
       ecx = cx-ex: ecy = cy-ey: ecz = cz-ez;
       edx = dx-ex: edy = dy-ey: edz = dz-ez;
       If ((eax*ecy*edz+eay*ecz*edx+eaz*ecx*edy-eaz*ecy*edx-eay*ecx*edz-eax*ecz*edy)*(epx*ecy*edz+epy*ecz*edx+epz*ecx*edy-epz*ecy*edx-epy*ecx*edz-epx*ecz*edy)>0.0)
        Return (planet(c,d,a,e,cs,ds,as,es,cx,cy,cz,dx,dy,dz,ax,ay,az,ex,ey,ez,x,y,z,level-1));
       Else
        Return (planet(c,d,b,e,cs,ds,bs,es,cx,cy,cz,dx,dy,dz,bx,by,bz,ex,ey,ez,x,y,z,level-1));
       EndIf
      EndIf
     EndIf
    EndIf
   EndIf 
  EndIf

 Else

  If (doshade)
   x1 = 0.25*(ax+bx+cx+dx);
   x1 = a*(x1-ax)+b*(x1-bx)+c*(x1-cx)+d*(x1-dx);
   y1 = 0.25*(ay+by+cy+dy);
   y1 = a*(y1-ay)+b*(y1-by)+c*(y1-cy)+d*(y1-dy);
   z1 = 0.25*(az+bz+cz+dz);
   z1 = a*(z1-az)+b*(z1-bz)+c*(z1-cz)+d*(z1-dz);
   l1 = Sqr(x1*x1+y1*y1+z1*z1);
   If (l1=0.0) l1 = 1.0;
   tmp = Sqr(1.0-y*y);
   If (tmp<0.0001) tmp = 0.0001;
   x2 = x*x1+y*y1+z*z1;
   y2 = -x*y/tmp*x1+tmp*y1-z*y/tmp*z1;
   z2 = -z/tmp*x1+x/tmp*z1;
   shade = Int((-Sin(Pi*shade_angle/180.0)*y2-Cos(Pi*shade_angle/180.0)*z2)/l1*48.0+128.0);
   If (shade<10) shade = 10;
   If (shade>255) shade = 255;
  EndIf
  Return ((a+b+c+d)/4);

 EndIf

End Function

Function planet1#(x#,y#,z#)
 ;x#,y#,z#

 Local abx#,aby#,abz#,acx#,acy#,acz#,adx#,ady#,adz#,apx#,apy#,apz#;
 Local bax#,bay#,baz#,bcx#,bcy#,bcz#,bdx#,bdy#,bdz#,bpx#,bpy#,bpz#;

 abx = ssbx-ssax: aby = ssby-ssay: abz = ssbz-ssaz;
 acx = sscx-ssax: acy = sscy-ssay: acz = sscz-ssaz;
 adx = ssdx-ssax: ady = ssdy-ssay: adz = ssdz-ssaz;
 apx = x-ssax: apy = y-ssay: apz = z-ssaz;

 If ((adx*aby*acz+ady*abz*acx+adz*abx*acy-adz*aby*acx-ady*abx*acz-adx*abz*acy)*(apx*aby*acz+apy*abz*acx+apz*abx*acy-apz*aby*acx-apy*abx*acz-apx*abz*acy)>0.0);
  ;p is on same side of abc as d
  If ((acx*aby*adz+acy*abz*adx+acz*abx*ady-acz*aby*adx-acy*abx*adz-acx*abz*ady)*(apx*aby*adz+apy*abz*adx+apz*abx*ady-apz*aby*adx-apy*abx*adz-apx*abz*ady)>0.0);
   ;p is on same side of abd as c
   If ((abx*ady*acz+aby*adz*acx+abz*adx*acy-abz*ady*acx-aby*adx*acz-abx*adz*acy)*(apx*ady*acz+apy*adz*acx+apz*adx*acy-apz*ady*acx-apy*adx*acz-apx*adz*acy)>0.0);
    ;p is on same side of acd as b
    bax = -abx: bay = -aby: baz = -abz;
    bcx = sscx-ssbx: bcy = sscy-ssby: bcz = sscz-ssbz;
    bdx = ssdx-ssbx: bdy = ssdy-ssby: bdz = ssdz-ssbz;
    bpx = x-ssbx: bpy = y-ssby: bpz = z-ssbz;
    If ((bax*bcy*bdz+bay*bcz*bdx+baz*bcx*bdy-baz*bcy*bdx-bay*bcx*bdz-bax*bcz*bdy)*(bpx*bcy*bdz+bpy*bcz*bdx+bpz*bcx*bdy-bpz*bcy*bdx-bpy*bcx*bdz-bpx*bcz*bdy)>0.0);
     ;p is on same side of bcd as a
     ;Hence, p is inside tetrahedron
     Return (planet(ssa,ssb,ssc,ssd,ssas,ssbs,sscs,ssds,ssax,ssay,ssaz,ssbx,ssby,ssbz,sscx,sscy,sscz,ssdx,ssdy,ssdz,x,y,z,11));
    EndIf
   EndIf
  EndIf
 EndIf

 ;otherwise
 Return (planet(M,M,M,M,r1,r2,r3,r4,0.0,0.0,3.01,0.0,Sqr(8.0)+0.01*r1*r1,-1.02+0.01*r2*r3,-Sqr(6.0)-0.01*r3*r3,-Sqr(2.0)-0.01*r4*r4,-1.02+0.01*r1*r2,Sqr(6.0)-0.01*r2*r2,-Sqr(2.0)-0.01*r3*r3,-1.02+0.01*r1*r3,x,y,z,Depth));
 ;M,M,M,M = initial altitude is M on all corners of tetrahedron
 ;r1,r2,r3,r4 = same seed set is used in every call
 ;coordinates of vertices
 ;x,y,z = coordinates of point we want colour of
 ;Depth = subdivision depth

End Function

Function rand2#(p#,q#)
 ;p#,q#
 ;random number generator taking two seeds
 ;rand2(p,q) = rand2(q,p) is important

 Local r#;
 r = (p+3.14159265)*(q+3.14159265);
 Return (2.0*(r-Int(r))-1.0);

End Function

Function printppm(filename$)
 ;prints picture in PPM (portable pixel map) format

 Local i,j,c,s,file,ascii$;

 file=WriteFile(filename)
 If Not file Return False ;write fail

 ascii="P6"+Chr(10) ;"P6\n"
 For i=1 To Len(ascii): WriteByte file,Asc(Mid(ascii,i,1)): Next
 ascii="#fractal planet image"+Chr(10) ;"#title\n"
 For i=1 To Len(ascii): WriteByte file,Asc(Mid(ascii,i,1)): Next
 ascii=Str(Width)+" "+Str(Height)+" "+Str(255)+Chr(10) ;"info ncols\n"
 For i=1 To Len(ascii): WriteByte file,Asc(Mid(ascii,i,1)): Next

 If (doshade)
  For j=0 To Height-1
   For i=0 To Width-1
    s = shades(i,j);
    c = s*rtable(col(i,j))/150;
    If (c>255) c=255;
    WriteByte file,c;
    c = s*gtable(col(i,j))/150;
    If (c>255) c=255;
    WriteByte file,c;
    c = s*btable(col(i,j))/150;
    If (c>255) c=255;
    WriteByte file,c;
   Next
  Next
 Else
  For j=0 To Height-1
   For i=0 To Width-1
    WriteByte file,rtable(col(i,j));
    WriteByte file,gtable(col(i,j));
    WriteByte file,btable(col(i,j));
   Next
  Next
 EndIf

 CloseFile file;

End Function

Function printppmBW(filename$)
 ;prints picture in b/w PPM format

 Local i,j,c,file,ascii$;

 file=WriteFile(filename)
 If Not file Return False ;write fail

 ascii="P6"+Chr(10) ;"P6\n"
 For i=1 To Len(ascii): WriteByte file,Asc(Mid(ascii,i,1)): Next
 ascii="#fractal planet image"+Chr(10) ;"#title\n"
 For i=1 To Len(ascii): WriteByte file,Asc(Mid(ascii,i,1)): Next
 ascii=Str(Width)+" "+Str(Height)+" "+Str(2)+Chr(10) ;"info ncols\n"
 For i=1 To Len(ascii): WriteByte file,Asc(Mid(ascii,i,1)): Next

  For j=0 To Height-1
   For i=0 To Width-1
   If (col(i,j) = BLACK) Then c=0 Else c=1;
   WriteByte file,c;
   WriteByte file,c;
   WriteByte file,c;
  Next
 Next

 CloseFile file;

End Function

Function printbmp(filename$)
 ;prints picture in BMP format

 Local i,j,k,c,s,W1,file;

 file=WriteFile(filename)
 If Not file Return False ;write fail

 W1 = (3*Width+3);
 W1 = W1 - W1 Mod 4;
 s = 54+W1*Height ;file size

 WriteShort file,(Asc("M") Shl 8)+Asc("B") ;file type "BM"
 WriteInt file,s ;bfSize
 WriteShort file,0 ;bfReserved1
 WriteShort file,0 ;bfReserved2
 WriteInt file,54 ;bfOffBits, offset to data

 WriteInt file,40 ;size of infoheader
 WriteInt file,Width ;biWidth
 WriteInt file,Height ;biHeight
 WriteShort file,1 ;no. of planes = 1
 WriteShort file,24 ;bpp
 WriteInt file,0 ;no compression
 WriteInt file,0 ;image size (unspecified)
 WriteInt file,32 Shl 8 ;h. pixels/m
 WriteInt file,32 Shl 8 ;v. pixels/m
 WriteInt file,0 ;colours used (unspecified)
 WriteInt file,0 ;important colours (all)

 If (doshade)
  For k=0 To Height-1 ;j--
   j=Height-1-k
   For i=0 To Width-1
    s = shades(i,j);
    c = s*btable(col(i,j))/150;
    If (c>255) c=255;
    WriteByte file,c;
    c = s*gtable(col(i,j))/150;
    If (c>255) c=255;
    WriteByte file,c
    c = s*rtable(col(i,j))/150;
    If (c>255) c=255;
    WriteByte file,c
   Next
   For i=3*Width To W1-1: WriteByte file,0: Next
  Next
 Else
  For k=0 To Height-1
   j=Height-1-k
   For i=0 To Width-1
    WriteByte file,btable(col(i,j));
    WriteByte file,gtable(col(i,j));
    WriteByte file,rtable(col(i,j));
   Next
   For i=3*Width To W1-1: WriteByte file,0: Next
  Next
 EndIf

 CloseFile file;

End Function

Function printbmpBW(filename$)
 ;prints picture in b/w BMP format

 Local i,j,k,c,s,W1;

 file=WriteFile(filename)
 If Not file Return False ;write fail

 W1 = (Width+31);
 W1 = W1 - W1 Mod 32;
 s = 62+(W1*Height)/8 ;file size

 WriteShort file,(Asc("M") Shl 8)+Asc("B") ;file type "BM"
 WriteInt file,s ;bfSize
 WriteShort file,0 ;bfReserved1
 WriteShort file,0 ;bfReserved2
 WriteInt file,62 ;bfOffBits, offset to data

 WriteInt file,40 ;size of infoheader
 WriteInt file,Width ;biWidth
 WriteInt file,Height ;biHeight
 WriteShort file,1 ;no. of planes = 1
 WriteShort file,1 ;bpp
 WriteInt file,0 ;no compression
 WriteInt file,0 ;image size (unspecified)
 WriteInt file,32 Shl 8 ;h. pixels/m
 WriteInt file,32 Shl 8 ;v. pixels/m
 WriteInt file,2 ;colours used (unspecified)
 WriteInt file,2 ;important colours (all)

 WriteInt file,0 ;colour 0 = black
 WriteInt file,$FFFFFFFF ;colour 1 = white

 For k=0 To Height-1 ;j--
  j=Height-1-k
  For i=0 To W1-1 Step 8 ;i+=8
   If (i<Width And col(i,j) <> BLACK) c=128 Else c=0;
   If (i+1<Width And col(i+1,j) <> BLACK) c=c+64;
   If (i+2<Width And col(i+2,j) <> BLACK) c=c+32;
   If (i+3<Width And col(i+3,j) <> BLACK) c=c+16;
   If (i+4<Width And col(i+4,j) <> BLACK) c=c+8;
   If (i+5<Width And col(i+5,j) <> BLACK) c=c+4;
   If (i+6<Width And col(i+6,j) <> BLACK) c=c+2;
   If (i+7<Width And col(i+7,j) <> BLACK) c=c+1;
   WriteByte file,c;
  Next
 Next

 CloseFile file;

End Function

Function printheights(filename$)

 Local i,j,k,c,s,W1,file;

 file=WriteFile(filename)
 If Not file Return False ;write fail

 W1 = (3*Width+3);
 W1 = W1 - W1 Mod 4;
 s = 54+W1*Height ;file size

 WriteShort file,(Asc("M") Shl 8)+Asc("B") ;file type "BM"
 WriteInt file,s ;bfSize
 WriteShort file,0 ;bfReserved1
 WriteShort file,0 ;bfReserved2
 WriteInt file,54 ;bfOffBits, offset to data

 WriteInt file,40 ;size of infoheader
 WriteInt file,Width ;biWidth
 WriteInt file,Height ;biHeight
 WriteShort file,1 ;no. of planes = 1
 WriteShort file,24 ;bpp
 WriteInt file,0 ;no compression
 WriteInt file,0 ;image size (unspecified)
 WriteInt file,32 Shl 8 ;h. pixels/m
 WriteInt file,32 Shl 8 ;v. pixels/m
 WriteInt file,0 ;colours used (unspecified)
 WriteInt file,0 ;important colours (all)

 For j=0 To Height-1
  For i=0 To Width-1
   k=Height-1-j ;fprintf(filename,"%d ",heights[i][j])
   WriteByte file,(heights(i,k) And $0000FF);
   WriteByte file,((heights(i,k) And $00FF00) Shr 8);
   WriteByte file,(heights(i,k) Shr 16);
  Next
  For i=3*Width To W1-1: WriteByte file,0: Next
 Next

 CloseFile file;

End Function

Function log_2#(x#)
 Return (Log(x)/Log(2.0))
End Function

Function print_error()

 Print "Usage: [options]";
 Print " -?               (or any illegal option) Output this text";
 Print " -s seed          Seed as number between 0.0 and 1.0";
 Print " -w width         Width in pixels, default = 640";
 Print " -h height        Height in pixels, default = 480";
 Print " -m magnification Magnification, default = 1.0";
 Print " -o output_file   Output file, default = planet.bmp";
 Print " -l longitude     Longitude of centre in degrees, default = 0.0";
 Print " -L latitude      Latitude of centre in degrees, default = 0.0";
 Print " -g gridsize      Vertical gridsize in degrees, default = 0.0 (no grid)";
 Print " -G gridsize      Horisontal gridsize in degrees, default = 0.0 (no grid)";
 Print " -i init_alt      Initial altitude, default = 0.3";
 Print " -c               Colour depends on latitude, default = only altitude";
 Print " -C               Use lighter colours (original scheme only)";
 Print " -N nocols        Use nocols number of colours, default = 256, minimum = 5";
 Print " -a               Switch to alternative colour scheme";
 Print " -M file          Read colour definitions from file";
 Print " -O               Produce a black and white outline map";
 Print " -E               Trace the edges of land in black on colour map";
 Print " -B               Use 'bumpmap' shading";
 Print " -A angle         Angle of 'light' in bumpmap shading";
 Print " -P               Use PPM file format, default = BMP";
 Print " -b               Reverse background colour, default = black";
 Print " -V number        Distance contribution to variation, default = 0.03";
 Print " -v number        Altitude contribution to variation, default = 0.4";
 Print " -pprojection     Projection:";
 Print "  m = Mercator (default), p = Peters, q = Square, s = Stereographic";
 Print "  o = Orthographic, g = Gnomonic, a = Area preserving azimuthal";
 Print "  c = Conical (conformal), M = Mollweide, S = Sinusoidal";
 Print "  h = Heightfield, f = Find match, see manual";

 ;With the -pf option a map must be given on standard input.
 ;This map is 11 lines of 24 characters. The characters are:
 ; . : very strong preference for water (value=8)
 ; , : strong preference for water (value=4)
 ; : : preference for water (value=2)
 ; ; : weak preference for water (value=1)
 ; - : don't care (value=0)
 ; * : weak preference for land (value=1)
 ; o : preference for land (value=2)
 ; O : strong preference for land (value=4)
 ; @ : very strong preference for land (value=8)
 ;
 ;Each point on the map corresponds to a point on a 15° grid.
 ;
 ;The program tries seeds starting from the specified and
 ;successively outputs the seed (and rotation) of the best
 ;current match, together with a small map of this.
 ;This is all ascii, no bitmap is produced.

End Function
