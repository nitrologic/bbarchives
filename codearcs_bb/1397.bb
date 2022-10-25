; ID: 1397
; Author: grindalf
; Date: 2005-06-13 04:43:22
; Title: matrix code
; Description: creates the scroling code from the matrix movies

Graphics 640,480,16,1
SeedRnd MilliSecs()

spd=2

If spd=0 Then lo=8:hi=12:txtspd=1
If spd=1 Then lo=1:hi=2:txtspd=5
If spd=2 Then lo=3:hi=6:txtspd=3
If spd=3 Then lo=6:hi=9:txtspd=2
If spd=4 Then lo=1:hi=9:txtspd=3


speed1#=Rnd(lo,hi):speed2#=Rnd(lo,hi):speed3#=Rnd(lo,hi):speed4#=Rnd(lo,hi)
speed5#=Rnd(lo,hi):speed6#=Rnd(lo,hi):speed7#=Rnd(lo,hi):speed8#=Rnd(lo,hi):speed9#=Rnd(lo,3)
speed10#=Rnd(lo,hi):speed11#=Rnd(lo,hi):speed12#=Rnd(lo,hi):speed13#=Rnd(lo,hi):speed14#=Rnd(lo,hi)
speed15#=Rnd(lo,hi):speed16#=Rnd(lo,hi):speed17#=Rnd(lo,hi):speed18#=Rnd(lo,hi):speed19#=Rnd(lo,hi)
speed20#=Rnd(lo,hi):speed21#=Rnd(lo,hi):speed22#=Rnd(lo,hi):speed23#=Rnd(lo,hi):speed24#=Rnd(lo,hi)
speed25#=Rnd(lo,hi):speed26#=Rnd(lo,hi):speed27#=Rnd(lo,hi):speed28#=Rnd(lo,hi):speed29#=Rnd(lo,hi)
speed30#=Rnd(lo,hi):speed31#=Rnd(lo,hi):speed32#=Rnd(lo,hi):speed33#=Rnd(lo,hi):speed34#=Rnd(lo,hi)
speed35#=Rnd(lo,hi):speed36#=Rnd(lo,hi):speed37#=Rnd(lo,hi):speed38#=Rnd(lo,hi):speed39#=Rnd(lo,hi)
speed40#=Rnd(lo,hi):speed41#=Rnd(lo,hi):speed42#=Rnd(lo,hi):speed43#=Rnd(lo,hi):speed44#=Rnd(lo,hi)
speed45#=Rnd(lo,hi):speed46#=Rnd(lo,hi):speed47#=Rnd(lo,hi):speed48#=Rnd(lo,hi):speed49#=Rnd(lo,hi)
speed50#=Rnd(lo,hi):speed51#=Rnd(lo,hi):speed52#=Rnd(lo,hi):speed53#=Rnd(lo,hi):speed54#=Rnd(lo,hi)
speed55#=Rnd(lo,hi):speed56#=Rnd(lo,hi):speed57#=Rnd(lo,hi):speed58#=Rnd(lo,hi):speed59#=Rnd(lo,hi)
speed60#=Rnd(lo,hi):speed61#=Rnd(lo,hi):speed62#=Rnd(lo,hi):speed63#=Rnd(lo,hi):speed64#=Rnd(lo,hi)



Global txtamount=1921



tmpgreen=150
;----------------------------
Type code
Field codetype
Field y#
Field x
Field green
Field red
Field blue
Field txttyp
Field speed#
Field txttimer
End Type



tmpx=0
tmpy=-10
For tmp =1 To txtamount
symbal.code=New code
symbal\codetype=Rand(1,5)
symbal\y#=tmpy
symbal\x=tmpx
symbal\green=tmpgreen
symbal\txttyp=Rand(1,17)
symbal\txttimer=0
symbal\speed#=Rnd(5,8)
symbal\red=0
symbal\blue=0
tmpgreen=tmpgreen-5
tmpy=tmpy-10

If tmp2=1 Then symbal\red=150:symbal\green=255:symbal\blue=150

tmp2=tmp2+1
If tmp2=31 Then tmpy=-10:tmpx=tmpx+10:tmpgreen=150:tmp2=1:
Next
;---------------------------


SetBuffer BackBuffer()
;===========================================================================================
.loop

Cls
;--------text and symbals--------
tmp=0:tmp2=0
symbal.code=First code
For tmp=1 To txtamount-1
symbal=After symbal
Color symbal\red,symbal\green,symbal\blue
If symbal\txttyp=1 Then Text symbal\x,symbal\y#,"0"
If symbal\txttyp=2 Then Text symbal\x,symbal\y#,"1"
If symbal\txttyp=3 Then Text symbal\x,symbal\y#,"2"
If symbal\txttyp=4 Then Text symbal\x,symbal\y#,"3"
If symbal\txttyp=5 Then Text symbal\x,symbal\y#,"4"
If symbal\txttyp=6 Then Text symbal\x,symbal\y#,"5"
If symbal\txttyp=7 Then Text symbal\x,symbal\y#,"6"
If symbal\txttyp=8 Then Text symbal\x,symbal\y#,"7"
If symbal\txttyp=9 Then Text symbal\x,symbal\y#,"8"
If symbal\txttyp=10 Then Text symbal\x,symbal\y#,"9"
If symbal\txttyp=11 Then Text symbal\x,symbal\y#,"T"
If symbal\txttyp=12 Then Text symbal\x,symbal\y#,"Z"
If symbal\txttyp=13 Then Line symbal\x,symbal\y#+2,symbal\x+7,symbal\y+3:Line symbal\x+1,symbal\y#+5,symbal\x+5,symbal\y+5:Line symbal\x,symbal\y#+9,symbal\x+7,symbal\y+9
If symbal\txttyp=14 Then Line symbal\x+1,symbal\y#+4,symbal\x+7,symbal\y+4:Line symbal\x+6,symbal\y#+2,symbal\x+6,symbal\y+7:Line symbal\x+6,symbal\y#+7,symbal\x+2,symbal\y+9
If symbal\txttyp=15 Then Line symbal\x+3,symbal\y#+2,symbal\x+3,symbal\y+6:Line symbal\x+3,symbal\y#+6,symbal\x+1,symbal\y+9:Line symbal\x+6,symbal\y#+2,symbal\x+8,symbal\y+9
If symbal\txttyp=16 Then Line symbal\x+7,symbal\y#+2,symbal\x+7,symbal\y+6:Line symbal\x+7,symbal\y#+6,symbal\x+3,symbal\y+9:Line symbal\x+4,symbal\y#+2,symbal\x+5,symbal\y+5:Line symbal\x+2,symbal\y#+2,symbal\x+3,symbal\y+5
If symbal\txttyp=17 Then Line symbal\x+5,symbal\y#+2,symbal\x+5,symbal\y+6:Line symbal\x+5,symbal\y#+6,symbal\x+2,symbal\y+9:Line symbal\x+2,symbal\y#+3,symbal\x+8,symbal\y+3:Line symbal\x+8,symbal\y#+3,symbal\x+8,symbal\y+9:Line symbal\x+7,symbal\y#+9,symbal\x+8,symbal\y+9




symbal\txttimer=symbal\txttimer+1
If tmp=1 And symbal\txttimer=txtspd Then symbal\txttyp=Rand(1,17):symbal\txttimer=0
If symbal\txttimer=txtspd Then newtyp=oldtyp:oldtyp=symbal\txttyp:symbal\txttyp=newtyp:symbal\txttimer=0

;speed settings
If tmp>0 And tmp<31 Then speed#=speed1#
If tmp>30 And tmp<61 Then speed#=speed2#
If tmp>60 And tmp<91 Then speed#=speed3#
If tmp>90 And tmp<121 Then speed#=speed4#
If tmp>120 And tmp<151 Then speed#=speed5#
If tmp>150 And tmp<181 Then speed#=speed6#
If tmp>180 And tmp<211 Then speed#=speed7#
If tmp>210 And tmp<241 Then speed#=speed8#
If tmp>240 And tmp<271 Then speed#=speed9#
If tmp>270 And tmp<301 Then speed#=speed1#
If tmp>300 And tmp<331 Then speed#=speed2#
If tmp>330 And tmp<361 Then speed#=speed3#
If tmp>360 And tmp<391 Then speed#=speed4#
If tmp>390 And tmp<421 Then speed#=speed5#
If tmp>420 And tmp<451 Then speed#=speed6#
If tmp>450 And tmp<481 Then speed#=speed7#
If tmp>480 And tmp<511 Then speed#=speed8#
If tmp>510 And tmp<541 Then speed#=speed9#
If tmp>540 And tmp<571 Then speed#=speed10#
If tmp>570 And tmp<601 Then speed#=speed11#
If tmp>630 And tmp<661 Then speed#=speed12#
If tmp>660 And tmp<691 Then speed#=speed13#
If tmp>690 And tmp<721 Then speed#=speed14#
If tmp>720 And tmp<751 Then speed#=speed15#
If tmp>750 And tmp<781 Then speed#=speed16#
If tmp>780 And tmp<811 Then speed#=speed17#
If tmp>810 And tmp<841 Then speed#=speed18#
If tmp>840 And tmp<871 Then speed#=speed19#
If tmp>870 And tmp<901 Then speed#=speed20#
If tmp>900 And tmp<931 Then speed#=speed21#
If tmp>930 And tmp<961 Then speed#=speed22#
If tmp>960 And tmp<991 Then speed#=speed23#
If tmp>990 And tmp<1021 Then speed#=speed24#
If tmp>1020 And tmp<1051 Then speed#=speed25#
If tmp>1050 And tmp<1081 Then speed#=speed26#
If tmp>1110 And tmp<1141 Then speed#=speed27#
If tmp>1140 And tmp<1171 Then speed#=speed28#
If tmp>1170 And tmp<1201 Then speed#=speed29#
If tmp>1230 And tmp<1261 Then speed#=speed30#
If tmp>1260 And tmp<1291 Then speed#=speed31#
If tmp>1290 And tmp<1321 Then speed#=speed32#
If tmp>1320 And tmp<1351 Then speed#=speed33#
If tmp>1350 And tmp<1381 Then speed#=speed34#
If tmp>1380 And tmp<1411 Then speed#=speed35#
If tmp>1410 And tmp<1441 Then speed#=speed36#
If tmp>1440 And tmp<1471 Then speed#=speed37#
If tmp>1470 And tmp<1501 Then speed#=speed38#
If tmp>1500 And tmp<1531 Then speed#=speed39#
If tmp>1530 And tmp<1561 Then speed#=speed40#
If tmp>1560 And tmp<1591 Then speed#=speed41#
If tmp>1590 And tmp<1621 Then speed#=speed42#
If tmp>1620 And tmp<1651 Then speed#=speed43#
If tmp>1650 And tmp<1681 Then speed#=speed44#
If tmp>1710 And tmp<1741 Then speed#=speed45#
If tmp>1740 And tmp<1771 Then speed#=speed46#
If tmp>1770 And tmp<1801 Then speed#=speed47#
If tmp>1800 And tmp<1831 Then speed#=speed48#
If tmp>1830 And tmp<1861 Then speed#=speed49#
If tmp>1890 And tmp<1921 Then speed#=speed50#
If tmp>1920 And tmp<1951 Then speed#=speed51#
If tmp>1950 And tmp<1981 Then speed#=speed51#

If symbal\y#>500 Then symbal\y#=-50
symbal\y#=symbal\y#+speed# 
Next










;exits the program at the touch of a button
Delay 10
key=GetKey()
If key>0 Then End
ms=GetMouse()
If ms>0 Then End
msx=MouseXSpeed()
If msx>0 Then End
msy=MouseYSpeed()
If msy>0 Then End
Flip
Goto loop
;====================================================================
