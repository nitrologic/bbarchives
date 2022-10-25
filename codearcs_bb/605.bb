; ID: 605
; Author: Skitchy
; Date: 2003-03-01 09:51:46
; Title: B+ GUI controlling a B3D program
; Description: Use GUIs and 3D together. All done with Blitz

;THE BLITZ3D PART

Graphics3D 800,600,16,2
AppTitle("Skitchy's B+ GUI with B3D example")
;these variables get passed in from the GUI swapfile
Global guimsgtype
Global guistring$
Global guistring2$
Global guifloat#
Global guifloat2#
Global guifloat3#

;make a simple scene
cam=CreateCamera()
cube=CreateCube()
light=CreateLight()
PositionEntity light,-3,0,3
PositionEntity cam,-3,5,-8
PointEntity cam,cube

;main loop begins
While Not KeyHit(1)

;this bit makes the program read the "guiout.swp" once every 10 updates.
readtimer=readtimer+1
If readtimer>10 Then readtimer=1

If readtimer=1
	Repeat
		guiout=OpenFile("guiout.swp")
		If guiout=0 
			DebugLog "unable to open swapfile - probably in use by GUI window - retrying in 1/2 sec - have you run the GUI first"
			Delay 500
		EndIf
	Until guiout<>0

	guimsgtype=ReadInt(guiout)
	guistring$=ReadString(guiout)
	guistring2$=ReadString(guiout)
	guifloat#=ReadFloat(guiout)
	guifloat2#=ReadFloat(guiout)
	guifloat3#=ReadFloat(guiout)

	CloseFile(guiout)
	;if there was an instruction in the file then overwrite it to ensure that the instruction is not repeated
	If guimsgtype<>0 Then clearguioutfile()
	
EndIf

;check what was pressed in the gui and carry out the instructions
If guistring$="changecolor" Then EntityColor(cube,guifloat,guifloat2,guifloat3)

;clear the variables from the gui to ensure instructions are not repeated
guimsgtype=0
guistring=""
guistring2=""
guifloat=0
guifloat2=0
guifloat3=0

;update the scene
TurnEntity cube,1,2,3
UpdateWorld
RenderWorld
Flip
Wend
End


Function clearguioutfile()
Repeat
swapout=WriteFile("guiout.swp")
Until swapout<>0

WriteInt (swapout,0)
WriteString(swapout," ")
WriteString(swapout," ")
WriteFloat(swapout,0)
WriteFloat(swapout,0)
WriteFloat(swapout,0)
WriteFloat(swapout,0)
WriteFloat(swapout,0)
CloseFile swapout
End Function




-----------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------


;THE BLITZPLUS PART

;create a blank output file. this will also stop the last command you did on the last run from being reproduced
guiout(0," "," ")
;the globals that the gui passes out to the 3D window.
Global guistring$
Global guistring2$
Global guifloat#
Global guifloat2#
Global guifloat3#
;create a simple gui
Win1 = CreateWindow("Skitchy B+ GUI in B3D Window Example",250,10,250,178,Desktop(),1)
changecolor = CreateButton("Change the box color",20,20,150,30,Win1,0)
;main gui handler loop
While WaitEvent()<>$803
	If EventID()=$401
		;if the button is pressed
		If EventSource()=changecolor
			;ask for a color
			RequestColor( 128,128,128 )
			r#=RequestedRed()
			g#=RequestedGreen()
			b#=RequestedBlue()
			;write the information to the output file 
			guiout(1,"changecolor"," ",r,g,b)
		EndIf
	EndIf
Wend

;function to write a TINY file that the 3D window will read.
Function guiout(msgtype,string1$,string2$=" ",float1#=0,float2#=0,float3#=0,float4#=0,float5#=0)
swapout=WriteFile("guiout.swp")
WriteInt (swapout,msgtype)
WriteString(swapout,string1$)
WriteString(swapout,string2$)
WriteFloat(swapout,float1#)
WriteFloat(swapout,float2#)
WriteFloat(swapout,float3#)
WriteFloat(swapout,float4#)
WriteFloat(swapout,float5#)
CloseFile swapout
End Function
