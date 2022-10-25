; ID: 1910
; Author: tonyg
; Date: 2007-02-03 00:11:22
; Title: Frame Limiting
; Description: Code to go with Morduun's tutorial

SuperStrict
Type TFramerate
	Global list:tlist=CreateList()
	Field targetfps:Float = 60.0
	Field speedfactor:Float
	Field fps:Float
	Field tickspersecond:Int = 1000
	Field currentticks:Int
	Field framedelay:Int = MilliSecs()
	Function FrameLimitInit:tframerate()
		Local temp:tframerate = New tframerate
		ListAddLast list , temp
    	Return temp
	End Function
	Method settargetfps(target:Float)
		targetfps = target
	End Method
	Method setspeedfactor()
		currentticks = MilliSecs()
		speedfactor = (currentticks - framedelay) / (tickspersecond / targetfps)
		If speedfactor <= 0 speedfactor = 0.0000000001
		fps = targetfps / speedfactor
		framedelay = currentticks
	End Method
	Method drawfps(x:Int , y:Int)
		DrawText Int(fps) , x , y
		DrawText Int(targetfps) , x , y + 20
		DrawText speedfactor,x,y+40
	End Method
	Method printfps()
		Print "FPS : " + FPS + " TargetFPS : " + targetfps
	End Method
	Method pause()
		Global p:Int
		Global oldtargetfps:Float
		If targetfps <> 0 oldtargetfps = targetfps
		If p=0
			settargetfps(0.0) 
			p=1
		Else 
			settargetfps(oldtargetfps)
			p=0
		EndIf
	End Method
End Type
Global main_FR:TFramerate = tframerate.FrameLimitInit()
main_FR.settargetfps(30.0)
Graphics 640,480
Local posx:Float=0.0, posy:Float=GraphicsHeight()/2, speed:Float=4.0
While Not KeyHit(KEY_ESCAPE)
	main_FR.setspeedfactor()
	Cls
	If MouseHit(1) main_fr.settargetfps(main_fr.targetfps + 10.0)
	If MouseHit(2) main_fr.settargetfps(main_fr.targetfps - 10.0)
	If KeyHit(KEY_SPACE) main_fr.pause
	DrawOval posx , posy , 10 , 10
	main_FR.drawfps(0,0)
	posx = (posx + (speed * main_fr.speedfactor) ) Mod 640.0
	Flip 0
Wend
