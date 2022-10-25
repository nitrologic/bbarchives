; ID: 3162
; Author: GW
; Date: 2014-11-14 03:56:44
; Title: 2D Lightmapping
; Description: Smooth 2D lighting for a dungeon map

SuperStrict
Framework brl.basic
Import brl.glmax2d

Graphics 800,600

Const MW%=41
Const MH%=42
Const LIGHTDIST%= 12
Const TILEW%=10
Global map%[MW,MH]
Global lmap#[MW,MH]
Global lights:TList = New TList


LoadMap

Local t% = MilliSecs()
DoLights()
Print MilliSecs()-t


'---------------------------------------------------------------------------------------------------
While Not KeyHit(key_escape)
	Cls
		Drawmap
	Flip
Wend
'---------------------------------------------------------------------------------------------------
Function DrawMap()
	SetColor 0,255,0
	For Local x% = 0 Until MW
		For Local y% = 0 Until MH
			Select map[x,y]
				Case 0
					SetColor((255/LIGHTDIST)*lmap[x,y], (255/LIGHTDIST)*lmap[x,y], (255/LIGHTDIST)*lmap[x,y])
					DrawRect(x*TILEW,y*TILEW,TILEW,TILEW)
				Case 1	
					SetColor 0,64,0
					DrawRect(x*TILEW,y*TILEW,TILEW,TILEW)
			End Select
		Next
	Next	
	SetColor(255,255,255)
End Function

'---------------------------------------------------------------------------------------------------
Function DoLights()
	Local val%
	Local m%=0
	For Local i% = 0 Until LIGHTDIST-1
		For Local x% = 1 Until MW-1
			For Local y% = 1 Until MH-1
				m=0
				val = lmap[x,y]
				If map[x-1,y] = 0 Then m = Max(lmap[x-1,y],m)
				If map[x+1,y] = 0 Then m = Max(lmap[x+1,y],m)
				If map[x,y-1] = 0 Then m = Max(lmap[x,y-1],m)
				If map[x,y+1] = 0 Then m = Max(lmap[x,y+1],m)
				If m > val+1 Then
					lmap[x,y] = val+1
				EndIf 	
			Next
		Next
	Next
End Function
'---------------------------------------------------------------------------------------------------
Function LoadMap()
	Local s$
	Local x%,y%
	RestoreData map
	For Local i% = 0 Until MH
		ReadData s
		For x = 0 Until MW
			Select s[x..x+1]
				Case " " 
					map[x,y]=0
				Case "0"
					map[x,y]=1
				Case "1"
					map[x,y]=0
					lmap[x,y]= LIGHTDIST
					lights.Addlast([x,y])
			EndSelect
		Next
		y:+1
	Next
	 
End Function
'---------------------------------------------------------------------------------------------------

#map
DefData "0000000000000000000000000000000000000000" 'from the Ryan Burnside gen function
DefData "0            000     0000000000        0"
DefData "0            000     0000000000        0"
DefData "0     1      000     0000000000        0"
DefData "0            000     0000000000    1   0"
DefData "0                                      0"
DefData "0            000     0000   000        0"
DefData "0            000     0000              0"
DefData "0            000     0000   000        0"
DefData "0            000     0000   000        0"
DefData "0            0001    0000   00000 0 0000"
DefData "0            000000 0000000 00000 0 0000"
DefData "0            000000 0000000 00000 0 0000"
DefData "00 000 0 0000000000 000000  000        0"
DefData "00 000 0 000000       000   000        0"
DefData "00 000 0 000000       000   000        0"
DefData "00 00       000       000   000        0"
DefData "00 00       000       000              0"
DefData "00 00       000       000   000        0"
DefData "00 001     1000             0000 0 0 000"
DefData "00 00       000       00    0000 0 0 000"
DefData "00 00        00       00    0000 0 0 000"
DefData "00 00        00       00 0 0000      000"
DefData "00 00        00       00 0 0000    1 000"
DefData "00 001      100000 0000010 0000      000"
DefData "00 00        00000 00000 0 0000      000"
DefData "00 00        00000 00000 0 0000      000"
DefData "00 00        00000 000               000"
DefData "00 000 00000 00000 000     0000     0000"
DefData "00 000 00000 00000 000              0000"
DefData "00 000 00000 00000 000     0000     0000"
DefData "0   00             0000000 00000 0000000"
DefData "0   00             0000000 00000 0000000"
DefData "0 1 00             0000000 00000 0000000"
DefData "0   00             0000000 001      0000"
DefData "0                          00       0000"
DefData "0   00             0000000000       0000"
DefData "0   00                              0000"
DefData "0   000            0000000000       0000"
DefData "0   000            0000000000      10000"
DefData "0000000     1      000000000000000000000"
DefData "0000000000000000000000000000000000000000"
DefData "0000000000000000000000000000000000000000"

Rem
#map
DefData "00000000000"
DefData "0 01  0 0 0"
DefData "0 000 0 0 0"
DefData "0     0  10"
DefData "0     00  0"
DefData "00001  0  0"
DefData "0         0"
DefData "0  0 000  0"
DefData "0  0 0    0"
DefData "0000 0 0000"
DefData "0    0    0"
DefData "0    0  1 0"
DefData "00000000000"
EndRem
