; ID: 1407
; Author: Rambo_Bill
; Date: 2005-06-23 17:09:50
; Title: 2D Sprites with Movement and Animation scripting
; Description: Sprites that can be easily scripting to move in different paths. Paths consist of a string describing that path and the animation.

' IMPLEMENTATION of types and support functions
' set some local vars for use
Local RVal:Int=0
Local A:Int=0
' Define Paths for use by SAM
Local Square:String="A 1,1,100,2,100,3,100:L 1:M 150,0,.5,0:M 0,150,0,.5:M 150,0,-.5,0:M 0,150,0,-.5:J 1"
Local Triangle:String="A 1,5,50,4,50,3,50,2,50,1,50:L 1:M 150,150,.5,.5:M 150,0,-.5,0:M 150,150,.5,-.5:J 1"
' setup the graphics
Graphics 800,600,,0 
HideMouse
' Load Anim images
RVal=IMGs_Add("3232.png",32,32)
SeedRnd MilliSecs()

' Make 4 sprites that move in Square path and 4 that move in triangle path
For A=1 To 8
' Create a Bob/Sprite
	RVal=BOB_Add(1,Rnd()*GraphicsWidth(),Rnd()*GraphicsHeight(),Rnd()*20,Rnd()+.1,.Rnd()+.1)
' Now try out SAM
	If A>4 Then
		SAM_calc(Triangle,,RVAL)
	Else
		SAM_calc(Square,,RVAL)
	End If
Next 

' MAIN LOOP
Repeat
	Cls
	SAM_UpdateAll()
	Flip
Until KeyHit (KEY_ESCAPE)

' END OF IMPLEMENTATION

'===============================
' The junk that makes this work
'===============================
'===================================
' IMAGES DEFINED HERE
'===================================
Global IMG:t_IMG[] ' Images
Global IMGCount:Int=-1

' stuff needed to work
Global AIMAGE:tImage[] ' Anim Images
Global AIMAGECount:Int=-1


Type t_IMG
	Field ImagePTR:tImage Ptr ' Pointer to a blitz image type
	Field FrameNum:Int ' Frame Number
	Field Width:Int ' Somewhat redundant, but too damn convenient
	Field Height:Int 	
	Method Draw(X:Int,Y:Int)
		DrawImage(ImagePTR[0],X,Y,FrameNum)
	End Method
End Type
'==============================================
' IMG_ADD 
'----------------------------------------------
' Defines an image and returns the image number
'----------------------------------------------
Function IMG_Add:Int(mImagePTR:tImage Ptr,mFrameNum:Int,mFrameWidth:Int,mFrameHeight:Int)
	Local A:Int
	IMGCount=IMGCount+1
	IMG = IMG[..IMGCount+1]
	IMG[IMGCount]=New t_IMG
	IMG[IMGCount].ImagePTR=mImagePTR
	IMG[IMGCount].FrameNum=mFrameNum

	IMG[IMGCount].Width=mFrameWidth
	IMG[IMGCount].Height=mFrameHeight
End Function

Function IMGs_Add:Int(FileName:String,FrameWidth:Int,FrameHeight:Int)
	Local AnImage:tImage
	Local AnimImage:tImage
	Local iFrame:Int,FramesTotal:Int
	AnImage=LoadImage(FileName)
	FramesTotal = (ImageWidth(AnImage)/FrameWidth) * (ImageHeight(AnImage)/FrameHeight) 
	AIMAGECount = AIMAGECount + 1
	AIMAGE=AIMAGE[..AIMAGECount+1]
'	AIMAGE[AIMAGECount]
	AIMAGE[AIMAGECount]=LoadAnimImage(FileName,FrameWidth,FrameHeight,0,FramesTotal)
	For iFrame=0 To FramesTotal
		IMG_Add(Varptr AIMAGE[0],iFrame,FrameWidth,FrameHeight)
	Next
	Return FramesTotal
End Function
'========================================
' BOBS DEFINED HERE
'========================================

Global BOB:t_BOB[] ' Images
Global BOBCount:Int=-1

Type t_BOB
	Global MPS:Float=60 ' Moves Per Second
	Global Delta:Float
	Global LastDelta:Float
	Global MyTimer:Float
	Field Active:Int=1 ' Are we Active?
	Field X:Float=0 ' Current X Position
	Field Y:Float=0 ' Current Y Position
	Field LastX:Float=0 ' Last X Position
	Field LastY:Float=0 ' Last Y Position
	Field CountX:Float=0 ' Number of X Movements since last reset
	Field CountY:Float=0 ' Number of Y movements since last reset
	Field IMGNum:Int=0 ' Image Number from IMG (IMAGES.BMX)
	Field SX:Float=0 ' X Speed
	Field SY:Float=0 ' Y Speed
	Field WrapAround:Int=1 ' 1 - WrapAround    0 - No Checking
	Field WrapBehaviour:Int=0 ' 0-Go to other side, 1 = Bounce
	Function Width:Int(BobNum)
		Return IMG[BOB[BOBNum].IMGNum].Width
	End Function
	Function Height:Int(BobNum)
		Return IMG[BOB[BOBNum].IMGNum].Height
	End Function
End Type

Function BOB_Add(iCount:Int=1,X:Float=0,Y:Float=0,IMGNum:Int=0,SX:Float=0,SY:Float=0,sSAM:String="")
	Local A:Int
	For A=0 To iCount-1
		BOBCount = BOBCount+1
		BOB = BOB[..BOBCount+1]
		BOB[BOBCount]=New t_BOB
		BOB[BOBCount].X = X
		BOB[BOBCount].Y = Y
		BOB[BOBCount].LastX = X
		BOB[BOBCount].LastY = Y
		BOB[BOBCount].IMGNum = IMGNum
		BOB[BOBCount].SX = SX
		BOB[BOBCount].SY = SY
	Next 
	Return BOBCount
End Function

Function BOB_Update()
	Local A:Int

	If BOBCount=-1 Then Return -1
	If BOB[0].MyTimer>0 Then
		BOB[0].Delta = (MilliSecs()/5)-BOB[0].MyTimer
	Else
		BOB[0].Delta = 0 ' dont move till we have a delta
	End If
	BOB[0].MyTimer = MilliSecs()/5

	If Abs(BOB[0].Delta-BOB[0].LastDelta)>1 Then
		BOB[0].LastDelta=BOB[0].Delta
	Else
 		BOB[0].Delta=BOB[0].LastDelta
	End If

	'Print BOB[0].Delta
	
	For A=0 To BOBCount
		If BOB[A].Active=1 Then
			' Save X , Y
			BOB[A].LastX = BOB[A].X
			BOB[A].LastY = BOB[A].Y
			' Update position
			BOB[A].X = BOB[A].X + (BOB[A].SX * BOB[A].Delta)
			BOB[A].Y = BOB[A].Y + (BOB[A].SY * BOB[A].Delta)
			' counters/Used alot For SAM
			BOB[A].CountX = BOB[A].CountX + (BOB[A].SX * BOB[A].Delta)
			BOB[A].CountY = BOB[A].CountY + (BOB[A].SY * BOB[A].Delta)
			' Zones
			'SetZone(A,BOB_X#(A),BOB_Y#(A),ImageWidth(IB_IMAGE( BOB_I(A) ) ),ImageHeight(IB_IMAGE( BOB_I(A) ) ))
			' If wrap
			If BOB[A].WrapAround=1 Then
				If BOB[A].WrapBehaviour=0 Then
					If BOB[A].X+BOB[A].Width(A)<0 Then BOB[A].X = GraphicsWidth()
					If BOB[A].X>GraphicsWidth() Then BOB[A].X = BOB[A].Width(A)*-1
					If BOB[A].Y+BOB[A].Height(A)<0 Then BOB[A].Y = GraphicsHeight()
					If BOB[A].Y>GraphicsHeight() Then BOB[A].Y = BOB[A].Height(A)*-1
				Else
					If BOB[A].X<0 Then 
						BOB[A].SX = Abs(BOB[A].SX)
					End If
					If BOB[A].X+BOB[A].Width(A)>GraphicsWidth() Then 
						BOB[A].SX = Abs(BOB[A].SX) * -1
					End If
					If BOB[A].Y<0 Then 
						BOB[A].SY = Abs(BOB[A].SY)
					End If
					If BOB[A].Y+BOB[A].Height(A)>GraphicsHeight() Then 
						BOB[A].SY = Abs(BOB[A].SY) * -1
					End If
				End If
				
			End If
		End If
	Next 
End Function

Function BOB_Draw()
	Local A:Int

	If BOBCount=-1 Then Return -1
	For A=0 To BOBCount
		IMG[BOB[A].IMGNum].Draw(BOB[A].X,BOB[A].Y)
	Next

	
End Function


Function BOB_ResetCounts(BobNum:Int)
	BOB[BobNum].CountX=0
	BOB[BobNum].CountY=0
End Function
'================================================
' SAM DEFINED HERE
'================================================
' COMMANDS , Seperator :
' M X,Y,Steps
' Goto (char) - goes To a label (char):
' stuff needed to work
Global SAM:t_Sam[] ' Sam
Global SAMCount:Int=-1

Type t_Sam
	Global BufferSize:Int=1024 ' amount To buffer each time in bytes
	Field Bank:TBank ' Handle to Bank
	Field BankOff:Int=0 ' Offset into Bank
	Field EXE:Int=0 ' Offset of current execution
	Field BOBnum:Int=0 ' Bob number to use
	Field ANIM:Int=0 ' Offset of current animation
	Field ANIMDelay:Int=0 ' countdown till next anim
	Field ANIMDelayType:Byte=0 ' 0 is time (100=1 second)
End Type

Function SAM_add()
	Local A:Int
	SAMCount=SAMCount+1
	SAM = SAM[..SAMCount+1]
	SAM[SAMCount]=New t_SAM
	Return SAMCount
End Function

'=========================================================
' Bank Layout
'=========================================================
' Byte 0 - CMD 
' Command Structure: 
'	1 - [Label] (Byte:1:Int:4) 5 - The Int is the label number, although For now only (0-255) supported
' Part of move command
'	2 - [Set X,Y speed] (Byte:1:Float:4:Float:4) 9
'	3 -	[Reset XY counters] (Byte:1) 1
'   4 - [Wait Counters] (Byte:1:Float:4:Float:4) 9
'   5 - [Jump To Label] (Byte:1:Int 4) 5 - the Int is offset of the label
'   6 - [Go]			(Byte:1:Float:4:Float:4) 9
'   7 - [Image]			(Byte:1:Int:4) 5
'   8 - [ANIM]          [Short:2:Int:4] 6 [short:2] [int:4] 6

'SAM_calc("L 1:M 10,10,1,1:M -10,-10,-1,-1:J 1",0)

' SAM Commands
'==============
' L #             ; Label	- See jump		- followed by number of label (0-255) 
'      ex: L 1
' 
' M PX,PY,SX,SY   ; Move    - moves bobs	- PixelsToMoveX,PixelsToMoveY,#SpeedX,#SpeedY
'      ex: M 100,100,.1,.1
'
' G X,Y			  ; Go		- moves bob directly To spot - X,Y
'
' I #			  ; Image   - Sets Image of bob
'
' J #             ; Jump 	- jump/Goto To a label (see L command)
'
' A REPEAT,Frame,Delay,Frame,Delay,Frame,Delay
'	   ex: J 1
'==================

Function SAM_Update()
	Local A:Int
	If SAMCount=-1 Then Return -1
		
	For A=0 To SAMCount
			SAM_Run(A)
	Next 
End Function

Function SAM_UpdateAll()
	SAM_Update()
	BOB_Update()
	BOB_Draw()
End Function

Function SAM_Run(A:Int)
	Local X:Float,Y:Float,CheckX:Float,CheckY:Float,iNum:Int,T:Int
	Local Z:Int
	' okay execute current command
#SAM_RUNNEXT	
	If SAM[A].EXE>=SAM[A].BankOff Then Return 0
	
	Select PeekByte(SAM[A].Bank,SAM[A].EXE)
		Case 1 ' label 
				' 0 (1 2 3 4)
				' we just skip labels when we encounter them
				SAM[A].EXE=SAM[A].EXE+5
				Goto SAM_RUNNEXT
		Case 2 ' set speed
				' 0  (1 2 3 4) (5 6 7 8)
				BOB[SAM[A].BobNum].SX = PeekFloat(SAM[A].Bank,SAM[A].EXE+1)
				BOB[SAM[A].BobNum].SY = PeekFloat(SAM[A].Bank,SAM[A].EXE+5)
				SAM[A].EXE=SAM[A].EXE+9
				Goto SAM_RUNNEXT
		Case 3 '  reset counts
				' 0
				BOB_resetcounts(SAM[A].BOBnum)
				SAM[A].EXE=SAM[A].EXE+1
				Goto SAM_RUNNEXT
		Case 4 ' wait counters
				' T = Meets Tests
				' 0 (1 2 3 4) (5 6 7 8)
				' Check X,Y
				CheckX = PeekFloat(SAM[A].Bank,SAM[A].EXE+1)
				CheckY = PeekFloat(SAM[A].Bank,SAM[A].EXE+5)
				T=0
				If Abs(BOB[SAM[A].BOBnum].countX ) >= CheckX Then
					T=T+1
					If BOB[SAM[A].BobNum].SX<>0 Then 'First time
						BOB[SAM[A].BobNum].SX=0 ' stop X speed
						CheckX = CheckX - Abs(BOB[SAM[A].BOBnum].countX )
						'BOB[SAM[A].BobNum].X = BOB[SAM[A].BobNum].X + CheckX
					End If
					' set X exactly
					' 100 110
					
				End If
				If Abs(BOB[SAM[A].BOBnum].countY ) >= CheckY Then
					T=T+1
					If BOB[SAM[A].BobNum].SY<>0 Then ' First time
						BOB[SAM[A].BobNum].SY=0 ' stop X speed
						CheckY = CheckY - Abs(BOB[SAM[A].BOBnum].countX )
						'BOB[SAM[A].BobNum].Y = BOB[SAM[A].BobNum].Y + CheckY
					End If					
					
				End If
				' If both tests met Then go To Next command				
				If T=2 Then
						SAM[A].EXE=SAM[A].EXE+9
						Goto SAM_RUNNEXT
				End If
		Case 5 ' Jump
				iNum=PeekInt(SAM[A].Bank,SAM[A].EXE+1) ' get offset
				SAM[A].EXE=iNum		
				Goto SAM_RUNNEXT
		Case 6 ' Go
				X=PeekFloat(SAM[A].Bank,SAM[A].EXE+1)
				Y=PeekFloat(SAM[A].Bank,SAM[A].EXE+5)
				BOB[SAM[A].Bobnum].X=X
				BOB[SAM[A].Bobnum].Y=Y				
				SAM[A].EXE=SAM[A].EXE+9
		Case 7 ' Image
				iNum=PeekInt(SAM[A].Bank,SAM[A].EXE+1) ' get Image
				BOB[SAM[A].Bobnum].IMGnum = iNum
				SAM[A].EXE=SAM[A].EXE+5		
		Case 8 ' Anim
'			Field ANIM:Int=0 ' Offset of current animation
	'		Field ANIMDelay:Int=0 ' countdown till next anim	
		' read frame
		' read delay
		' jump spot
			Z = PeekInt(SAM[A].Bank,SAM[A].EXE+1)
			' get first frame and delay
			iNum = Int(PeekShort(SAM[A].Bank,SAM[A].EXE+5))
			SAM[A].AnimDelay= PeekInt(SAM[A].Bank,SAM[A].EXE+7)
			If SAM[A].AnimDelayType=0 Then
				SAM[A].AnimDelay=MilliSecs() + (SAM[A].AnimDelay*10)
			End If
			SAM[A].ANIM = SAM[A].EXE + 11
			' set bob frame
			BOB[SAM[A].Bobnum].IMGnum = iNum
			' jump ahead
			SAM[A].EXE = Z
			
'			iNum = Int(PeekShort(SAM[A].Bank,SAM[A].EXE+1))
'			SAM[A].AnimDelay= PeekInt(SAM[A].Bank,SAM[A].EXE+3)
			' save next offset
'			SAM[A].ANIM=SAM[A].EXE+7
			' set first frame
'			BOB[SAM[A].Bobnum].IMGnum = iNum
			' move exe spot
			' need to store stepover spot, or read shorts till 65330
			' short , int 
			' read until we get a short over 65529
	End Select

	' do any animation
	If Not SAM[A].AnimDelay=0 Then

		If SAM[A].AnimDelayType=0 Then ' 100=1 second

			If SAM[A].AnimDelay<MilliSecs() Then
#SAM_RepeatFrame
			' get frame and delay
				iNum = Int(PeekShort(SAM[A].Bank,SAM[A].ANIM))
				If iNum<65530 Then
					SAM[A].AnimDelay= MilliSecs() + (PeekInt(SAM[A].Bank,SAM[A].ANIM+2)*10)
					SAM[A].ANIM = SAM[A].ANIM + 6
	'				' set bob frame
					BOB[SAM[A].Bobnum].IMGnum = iNum
				Else
					If iNum=65530 Then
						SAM[A].AnimDelay=0
					Else
						SAM[A].ANIM=PeekInt(SAM[A].Bank,SAM[A].ANIM+2)
						Goto SAM_RepeatFrame		
					End If
				End If ' iNum < 65530 (Signifies end of frames)

			End If ' Anim Delay has run out <1
		End If ' Delay Type 0 (100=1 second)
	End If
End Function

'========================================================
' Okay, typically For movement there are two types..
' 1. Path
'			Follow a predefined path, but we want this To happen at a standard pace. 
'			A. Set Speed
'			
' 2. Constant
' IMPORTANT: Uses Global sARRAY For label calcs

Function SAM_Calc(TXT:String,iChan:Int=-1,iBob:Int)
	Local CMD:String
	Local A:Int,B:Int,C:Int
	Local TMP:String
	Local LBL:String
	Local iNum:Int=0
	' dim Global array
	Local sArray:Int[255] ' label use
	If iChan=-1 Then
		iChan=SAM_add()
	End If
' Set bob
	SAM[iChan].BOBnum = iBOB

' first create bank
	If Not SAM[iChan].Bank=Null Then
			SAM[iChan].Bank=Null
	End If

	SAM[iChan].Bank=CreateBank(SAM[iChan].BufferSize)
	SAM[iChan].BankOff=0	
	SAM[iChan].EXE=0
	A=1
	' Upper Case
	TXT=Upper(TXT)
	' remove all spaces
	TXT=Replace(TXT," ","")
	' get CMD
Repeat
	' get End of cmd
	B=Instr(TXT,":",A)
	If B<1 Then B=Len(TXT)+1
	B = B - A
	' If no more Then Exit
	If B<1 Then Exit
	' get command
	CMD=Mid(TXT,A,B)
	'TMP$=TMP$ + CMD$ + "+"
	
	Select Mid(CMD,1,1)
		Case "I" ' Image
			SAM_CheckBankSize(iChan,5)
			iNum=Int(Mid(CMD,2))
			' place into bank
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff,7 '@
			' 0 
			PokeInt SAM[iChan].Bank,SAM[iChan].BankOff+1,iNum
			' 1 2 3 4
			SAM[iChan].BankOff = SAM[iChan].BankOff + 5
		Case "J" ' JUMP
			SAM_CheckBankSize(iChan,5)
			iNum=Int(Mid(CMD,2))
			' place into bank
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff,5 '@
			' 0 (Place actaul offset instead of label pointer)
			PokeInt SAM[iChan].Bank,SAM[iChan].BankOff+1,sArray[iNum]
			' 1 2 3 4
			SAM[iChan].BankOff = SAM[iChan].BankOff + 5
		Case "L" ' LABEL
			SAM_CheckBankSize(iChan,5)
			' Get label Number into integer
			iNum=Int(Mid(CMD,2))
			' place into bank
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff,1 '@
			' 0
			PokeInt SAM[iChan].Bank,SAM[iChan].BankOff+1,iNum
			' 1 2 3 4
			SAM[iChan].BankOff = SAM[iChan].BankOff + 5
			' SAVE OFFSET For jumps
			sArray[iNum]=SAM[iChan].BankOff
		Case "M" ' MOVE
			SAM_CheckBankSize(iChan,20)
			' first we do a set speed command
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff,2 ' @
			' 0 
			PokeFloat SAM[iChan].Bank,SAM[iChan].BankOff+1,Float(GET_TOKEN(CMD,3))
			' 1 2 3 4
			PokeFloat SAM[iChan].Bank,SAM[iChan].BankOff+5,Float(GET_TOKEN(CMD,4))
			' 5 6 7 8
			' reset counters
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff+9,3 '@
			' 9
			' Wait counters
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff+10,4 '@
			' 10
			PokeFloat SAM[iChan].Bank,SAM[iChan].BankOff+11,Float(Mid(GET_TOKEN(CMD,1),2))
			' 11 12 13 14
			PokeFloat SAM[iChan].Bank,SAM[iChan].BankOff+15,Float(GET_TOKEN(CMD,2))
			' 15 16 17 18
			SAM[iChan].BankOff = SAM[iChan].BankOff + 19
'			Print GET_TOKEN$(CMD$,1) +"-"
'			Print GET_TOKEN$(CMD$,2) +"-"
'			Print GET_TOKEN$(CMD$,3) +"-"
'			Print GET_TOKEN$(CMD$,4) +"-"
'			Print "Blarg"
		Case "G" ' Go
			SAM_CheckBankSize(iChan,9)
			' GO
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff,6 '@
			' 0
			PokeFloat SAM[iChan].Bank,SAM[iChan].BankOff+1,Float(Mid(GET_TOKEN(CMD,1),2))
			' 1 2 3 4
			PokeFloat SAM[iChan].Bank,SAM[iChan].BankOff+5,Float(GET_TOKEN(CMD,2))
			' 5 6 7 8
			SAM[iChan].BankOff = SAM[iChan].BankOff + 9
		Case "A" ' Anim
			Local aRepeat:Short=0
			Local iFrame:Int=1
			Local iBankStart:Int
			SAM_CheckBankSize(iChan,7) ' byte+ one anim
			' Anim Define
			PokeByte SAM[iChan].Bank,SAM[iChan].BankOff,8 '@
			' 0
			' Int (jump ahead)
			PokeInt SAM[iChan].Bank,SAM[iChan].BankOff+1,0 ' this will be repoked at end with end position
			' 1 2 3 4 x
			' save whether to repeat
			aRepeat=Short(Mid(GET_TOKEN(CMD,1),2))
			' up offset before loop
			SAM[iChan].BankOff = SAM[iChan].BankOff + 5
			iBankStart = SAM[iChan].BankOff
			' Frame,Delay
			While Len(GET_TOKEN(CMD,2*iFrame))>0
				' check we have space for a short (0-65535)  2 Bytes and a long 8 bytes
				SAM_CheckBankSize(iChan,6)
				' frame
				PokeShort SAM[iChan].Bank,SAM[iChan].BankOff,Short(GET_TOKEN(CMD,2*iFrame))
				' 0 1
				' delay
				PokeInt SAM[iChan].Bank,SAM[iChan].BankOff+2,Int(GET_TOKEN(CMD,2*iFrame+1))
				' 2 3 4 5 
				SAM[iChan].BankOff = SAM[iChan].BankOff + 6
				iFrame = iFrame + 1
			Wend
			' to signify end we use either 65530 (No repeat) or 65531 (Repeat)
			aRepeat=aRepeat+65530
			PokeShort SAM[iChan].Bank,SAM[iChan].BankOff,Short(aRepeat)
			' 0 1
			' and poke int no matter what for start pos
			PokeInt SAM[iChan].Bank,SAM[iChan].BankOff+2,Int(iBankStart)
			' 2 3 4 5
			SAM[iChan].BankOff = SAM[iChan].BankOff + 6
			' reset jump spot at beginning
			PokeInt SAM[iChan].Bank,iBankStart-4,SAM[iChan].BankOff ' repoking
	End Select
	A=A+B+1
Forever
	Return iChan
'	Print TMP$
'	Text 10,10,TMP$
		
End Function

' checks For enough free space, If Not it resizes
Function SAM_CheckBankSize(iBank,iNeeded)
	If iNeeded+SAM[iBank].BankOff>BankSize(SAM[iBank].Bank) Then
		ResizeBank SAM[iBank].Bank,BankSize(SAM[iBank].Bank)+SAM[iBank].BufferSize
	End If
End Function


Function GET_TOKEN:String(TXT:String,iNum:Int,sDelim:String=",")
	Local A:Int,B:Int=0,C:Int
	B=0
	If TXT="" Then
		Return ""
	End If
	If Instr(TXT,",")<1 Then
		Return TXT
	End If
	If iNum=0 Then
		Return Mid(TXT,1,Instr(TXT,sDelim)-1)
	End If
	C=1
	For A=1 To Len(TXT)
		If Mid(TXT,A,1)=sDelim Then 
			B=B+1
			If B=iNum Then
				Return Mid(TXT,C,A - C)
			Else
				C=A+1
			End If
		End If
	Next 
	B=B+1
	If B=iNum Then
		Return Mid(TXT,C,A - C)
	End If
	Return ""
End Function
