; ID: 2604
; Author: Ked
; Date: 2009-11-01 14:46:05
; Title: THeartSystem
; Description: A simple heart system as in Legend of Zelda.

SuperStrict

Const HEARTSYSTEM_ONEPIECE:Int=0
Const HEARTSYSTEM_TWOPIECE:Int=1
Const HEARTSYSTEM_THREEPIECE:Int=2
Const HEARTSYSTEM_FOURPIECE:Int=4

Const HEARTSYSTEM_HALFHEART:Int=HEARTSYSTEM_TWOPIECE
Const HEARTSYSTEM_WHOLEHEART:Int=HEARTSYSTEM_FOURPIECE

Type THeartSystem
	Global _list:TList=New TList
	
	Field x:Float,y:Float
	Field img:TImage
	
	Field totheartcount:Int
	Field totpiececount:Int
	Field curpiececount:Int
	
	Field fullhearts:Int
	Field fullpieces:Int
	
	Field allsc:Float=0.5	'inactive hearts'
	Field cursc:Float=0.5	'active heart'
	
	Field incr:Int=True
	
	Method New()
		_list.addlast Self
	EndMethod
	
	Method Create:THeartSystem(count:Int,x:Float,y:Float,url:Object)
		img=LoadAnimImage(url,48,48,0,5,FILTEREDIMAGE)
		If Not img Return Null
		MidHandleImage img
		
		Self.x=x
		Self.y=y
		
		totheartcount=count
		totpiececount=(count*4)
		curpiececount=totpiececount
		
		fullhearts=totheartcount
		fullpieces=0
		
		Return Self
	EndMethod
	
	Method Subtract(phase:Int)
		Select phase
			Case HEARTSYSTEM_ONEPIECE
				curpiececount:-1
			Case HEARTSYSTEM_TWOPIECE
				curpiececount:-2
			Case HEARTSYSTEM_THREEPIECE
				curpiececount:-3
			Case HEARTSYSTEM_FOURPIECE
				curpiececount:-4
			
			Default
				curpiececount:-0
		EndSelect
		If curpiececount<0
			curpiececount=0
		EndIf
		
		Local numoffullhearts:Int=0
		Local k:Int=0
		Local remaining:Int=0
		Repeat
			If k>=curpiececount
				remaining=k-curpiececount
				numoffullhearts:-1
				Exit
			Else
				numoffullhearts:+1
				k:+4
			EndIf
		Forever
		
		fullhearts=numoffullhearts
		fullpieces=4-remaining
		If fullpieces=4 fullhearts:+1 ; fullpieces=0
	EndMethod
	
	Method Add(phase:Int)
		Select phase
			Case HEARTSYSTEM_ONEPIECE
				curpiececount:+1
			Case HEARTSYSTEM_TWOPIECE
				curpiececount:+2
			Case HEARTSYSTEM_THREEPIECE
				curpiececount:+3
			Case HEARTSYSTEM_FOURPIECE
				curpiececount:+4
			
			Default
				curpiececount:+0
		EndSelect
		If curpiececount>totpiececount
			curpiececount=totpiececount
		EndIf
		
		Local numoffullhearts:Int=0
		Local k:Int=0
		Local remaining:Int=0
		Repeat
			If k>=curpiececount
				remaining=k-curpiececount
				numoffullhearts:-1
				Exit
			Else
				numoffullhearts:+1
				k:+4
			EndIf
		Forever
		
		fullhearts=numoffullhearts
		fullpieces=4-remaining
		If fullpieces=4 fullhearts:+1 ; fullpieces=0
	EndMethod
	
	Method RestoreAll()
		curpiececount=totpiececount
		fullhearts=totheartcount
		fullpieces=0
	EndMethod
	
	Method AddHeart()
		totheartcount:+1
		totpiececount:+4
		curpiececount:+4
		
		Local numoffullhearts:Int=0
		Local k:Int=0
		Local remaining:Int=0
		Repeat
			If k>=curpiececount
				remaining=k-curpiececount
				numoffullhearts:-1
				Exit
			Else
				numoffullhearts:+1
				k:+4
			EndIf
		Forever
		
		fullhearts=numoffullhearts
		fullpieces=4-remaining
		If fullpieces=4 fullhearts:+1 ; fullpieces=0
	EndMethod
	
	Method RemoveHeart()
		totheartcount:-1
		If totheartcount<1 totheartcount=1
		totpiececount:-4
		If totpiececount<4 totpiececount=4
		If curpiececount>=4
			curpiececount:-4
			If curpiececount<0 curpiececount=0
		EndIf
		
		Local numoffullhearts:Int=0
		Local k:Int=0
		Local remaining:Int=0
		Repeat
			If k>=curpiececount
				remaining=k-curpiececount
				numoffullhearts:-1
				Exit
			Else
				numoffullhearts:+1
				k:+4
			EndIf
		Forever
		
		fullhearts=numoffullhearts
		fullpieces=4-remaining
		If fullpieces=4 fullhearts:+1 ; fullpieces=0
	EndMethod
	
	Method Draw()
		If incr=True
			cursc:+0.01
			If cursc>0.75
				cursc=0.75
				incr=False
			EndIf
		Else
			cursc:-0.01
			If cursc<0.5
				cursc=0.5
				incr=True
			EndIf
		EndIf
		
		SetBlend ALPHABLEND
		SetScale allsc,allsc
		SetAlpha 1.0
		SetRotation 0
		SetColor 255,255,255
		
		If fullhearts=totheartcount
			Local i:Int
			For i=0 To fullhearts-2
				DrawImage img,(x+(i*(48*allsc))+(i*5))+((48*allsc)/2),(y+((48*allsc)/2)),0
			Next
			
			SetScale cursc,cursc
			DrawImage img,(x+(i*(48*allsc))+(i*5))+((48*allsc)/2),(y+((48*allsc)/2)),0
			SetScale allsc,allsc
		Else
			Local i:Int
			For i=0 To fullhearts-1
				DrawImage img,(x+(i*(48*allsc))+(i*5))+((48*allsc)/2),(y+((48*allsc)/2)),0
			Next
		
			If fullpieces
				SetScale cursc,cursc
				If fullpieces=1
					DrawImage img,(x+(fullhearts*(48*allsc))+(fullhearts*5)+((48*allsc)/2)),(y+((48*allsc)/2)),3
				ElseIf fullpieces=2
					DrawImage img,(x+(fullhearts*(48*allsc))+(fullhearts*5)+((48*allsc)/2)),(y+((48*allsc)/2)),2
				ElseIf fullpieces=3
					DrawImage img,(x+(fullhearts*(48*allsc))+(fullhearts*5)+((48*allsc)/2)),(y+((48*allsc)/2)),1
				EndIf
				
				SetScale allsc,allsc
				Local i:Int
				For i=fullhearts+1 To totheartcount-1
					DrawImage img,(x+(i*(48*allsc))+(i*5))+((48*allsc)/2),(y+((48*allsc)/2)),4
				Next
			Else
				Local i:Int=fullhearts-1
				
				If i<>-1
					SetScale cursc,cursc
					DrawImage img,(x+(i*(48*allsc))+(i*5)+((48*allsc)/2)),(y+((48*allsc)/2)),0
					SetScale allsc,allsc
				EndIf
					
				For i=fullhearts To totheartcount-1
					DrawImage img,(x+(i*(48*allsc))+(i*5))+((48*allsc)/2),(y+((48*allsc)/2)),4
				Next
			EndIf
		EndIf
	EndMethod
	
	Method Free()
		img=Null
		_list.remove Self
	EndMethod
EndType

Function CreateHeartSystem:THeartSystem(count:Int,x:Float,y:Float,url:Object)
	Return New THeartSystem.Create(count,x,y,url)
EndFunction

Function FreeHeartSystem(system:THeartSystem)
	system.Free()
EndFunction

Graphics 800,600

Global hsystem:THeartSystem=CreateHeartSystem(3,5,3,"zeldaheart.png")

SetClsColor 255,255,255
Repeat
	If AppTerminate() Exit
	If KeyHit(KEY_ESCAPE) Exit
	
	Cls
	
	hsystem.Draw()
	
	If KeyHit(KEY_RIGHT)=True hsystem.Add(HEARTSYSTEM_TWOPIECE)
	If KeyHit(KEY_LEFT)=True hsystem.Subtract(HEARTSYSTEM_TWOPIECE)
	If KeyHit(KEY_UP)=True hsystem.AddHeart()
	If KeyHit(KEY_DOWN)=True hsystem.RemoveHeart()
	
	Flip()
Forever
FreeHeartSystem(hsystem)
End
