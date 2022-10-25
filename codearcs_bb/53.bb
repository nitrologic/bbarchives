; ID: 53
; Author: Unknown
; Date: 2001-09-25 00:13:15
; Title: Complete Asteroids Clone
; Description: Well... a complete asteroids clone (need I say more?)

; ----------------------------------------------------
; |                                                  |
; |           ASTEROIDS by Jonathan Nguyen           |
; |  aka. world_creator (world_creator@hotmail.com)  |
; |                                                  |
; ----------------------------------------------------

; Start Program
	Graphics 640,480,16
	SetBuffer BackBuffer()

; User Variables

	; Number of starting lives
	lives=3
	
	; Delay until you respawn (in frames)
	respawntime=90
	
	; Time in which shield is active after death (in frames)
	shieldtime=90
	
	; Number of starting asteroids
	astnum=10
	
	; Largest asteroid size
	astmaxs=5
	
	; Maximum number of bullets on screen
	bulnum=100
	
	; Number of hits bullet makes (if more than 1 then it bounces)
	bulhit=1
	
	; Frame delay between bullet firings
	bulmdel=4
	
	; Speed of bullets
	bulspd#=10
	
	; Don't change these
	spknum=bulnum
	shknum=bulnum/2.0
	trlnum=100

; Set Asteroid Arrays
	astnum=astnum-1
	astmax=astnum*2
	For t=1 To astmaxs
		astmax=astmax*2
	Next
	Dim ax#(astmax)
	Dim ay#(astmax)
	Dim aan#(astmax)
	Dim aanvel#(astmax)
	Dim axvel#(astmax)
	Dim ayvel#(astmax)
	Dim asize#(astmax)
	Dim alife#(astmax)
	Dim adist#(astmax,6)

; Create Asteroids
	For t=0 To astnum
		Repeat
			ax#(t)=Rnd(640)
			ay#(t)=Rnd(480)
		Until ax#(t)<320-(astmaxs*8) Or ax#(t)>320+(astmaxs*8) And ay#(t)<240-(astmaxs*8) Or ay#(t)>240+(astmaxs*8)
		aan#(t)=Rnd(360)
		aanvel#(t)=(Rnd(80)-40)/10.0
		axvel#(t)=Rnd(6)-3
		ayvel#(t)=Rnd(6)-3
		asize#(t)=(astmaxs-1)+(Rnd(2)-1)
		alife#(t)=1
		For i=0 To 5
			adist#(t,i)=(asize#(t)*8)+Rnd(16)
		Next
	Next

; Set Bullet Arrays
	bulnum=bulnum-1
	Dim bx#(bulnum)
	Dim by#(bulnum)
	Dim bhit#(bulnum)
	Dim bxvel#(bulnum)
	Dim byvel#(bulnum)
	Dim blife#(bulnum)

; Set Spark Arrays
	spknum=spknum-1
	Dim sx#(spknum)
	Dim sy#(spknum)
	Dim sxvel#(spknum)
	Dim syvel#(spknum)
	Dim slife#(spknum)

; Set Shock Arrays
	shknum=shknum-1
	Dim hx#(shknum)
	Dim hy#(shknum)
	Dim hsca#(shknum)
	Dim hlife#(shknum)

; Set Trail Arrays
	trlnum=trlnum-1
	Dim tx#(trlnum)
	Dim ty#(trlnum)
	Dim txvel#(trlnum)
	Dim tyvel#(trlnum)
	Dim tlife#(trlnum)

; Create Star BG
	starbg=CreateImage(640,480)
	SetBuffer ImageBuffer(starbg)
	For t=0 To 1200
		stc#=128+(Rnd(128)-64)
		Color stc#,stc#,stc#
		Plot Rnd(640),Rnd(480)
	Next
	SetBuffer BackBuffer()
	
; Set Player Variables
	gameOn=1
	x#=320
	y#=240
	an#=0
	xvel#=0
	yvel#=0

; Set Time
	syncTime=CreateTimer(30)

; Main Game Loop
	While gameOn=1
	
	; Render
		WaitTimer(syncTime)
		Flip
		Cls
		
	; Display Stars
		DrawImage starbg,0,0
		
	; Controls
		If KeyDown(200)=1
			xvel#=xvel#+(Sin(an#)/4.0)
			yvel#=yvel#+(Cos(an#)/4.0)
			If lives>=0 And respawn=0
				For t=0 To trlnum
					If tlife#(t)=0
						tx#(t)=x#+(Sin(an#)*-6)
						ty#(t)=y#+(Cos(an#)*-6)
						txvel#(t)=xvel#*0.5
						tyvel#(t)=yvel#*0.5
						tlife#(t)=trlnum
						Exit
					EndIf
				Next
			EndIf
		EndIf
		If KeyHit(208)=1
			x#=Rnd(640)
			y#=Rnd(480)
		EndIf
		If KeyDown(203)=1
			an#=an#+7.5
		EndIf
		If KeyDown(205)=1
			an#=an#-7.5
		EndIf
		If KeyDown(1)=1 Or KeyDown(70)=1 Then End
		If KeyDown(57)>0 And buldel#=0 And lives>=0 And respawn=0
			For t=0 To bulnum
				If blife#(t)=0
					temp1#=an#+((Rnd(80)-40)/10.0)
					bx#(t)=x#
					by#(t)=y#
					bhit#(t)=0
					bxvel#(t)=xvel#+(Sin(temp1#)*bulspd#)
					byvel#(t)=yvel#+(Cos(temp1#)*bulspd#)
					blife#(t)=bulnum/bulhit
					buldel#=bulmdel
					Exit
				EndIf
			Next
		EndIf
		If buldel#>0 Then buldel#=buldel#-1
		
	; Restrict Speed
		If xvel#>15 Then xvel#=15
		If yvel#>15 Then yvel#=15
		If xvel#<-15 Then xvel#=-15
		If yvel#<-15 Then yvel#=-15
		
	; Move Ship
		x#=x#+xvel#
		y#=y#+yvel#
		
	; Warp Ship
		If x#<0
			x#=640
		EndIf
		If x#>640
			x#=0
		EndIf
		If y#<0
			y#=480
		EndIf
		If y#>480
			y#=0
		EndIf
		
	; Control and Draw Bullets
		For t=0 To bulnum
			If blife#(t)>0
				bx#(t)=bx#(t)+bxvel#(t)
				by#(t)=by#(t)+byvel#(t)
				If bx#(t)<0 Then bx#(t)=640
				If bx#(t)>640 Then bx#(t)=0
				If by#(t)<0 Then by#(t)=480
				If by#(t)>480 Then by#(t)=0
				blife#(t)=blife#(t)-1
				For i=0 To astmax
					If alife#(i)>0
						If Sqr#(((bx#(t)-ax#(i))^2)+((by#(t)-ay#(i))^2))=<adist#(i,6)
							axvel#(i)=(axvel#(i)+bxvel#(t))/2.0
							ayvel#(i)=(ayvel#(i)+byvel#(t))/2.0
							asize#(i)=asize(i)-1
							For m=0 To 5
								adist#(i,m)=(asize#(i)*8)+Rnd(16)
							Next
							If asize#(i)=<0
								alife#(i)=0
							EndIf
							If asize#(i)>0
								m=-1
								Repeat
									m=m+1
								Until alife#(m)=0 Or m=>astmax
								ax#(m)=ax#(i)
								ay#(m)=ay#(i)
								asize#(m)=asize#(i)
								alife#(m)=1
								axvel#(m)=Rnd(6)-3
								ayvel#(m)=Rnd(6)-3
								For e=0 To 5
									adist#(m,e)=(asize#(m)*8)+Rnd(16)
								Next
							EndIf
							bx#(t)=bx#(t)-bxvel#(t)
							by#(t)=by#(t)-byvel#(t)
							blife#(t)=bulnum/bulhit
							bhit#(t)=bhit#(t)+1
							temp#=ATan(byvel#(t)/bxvel#(t))+(Rnd(180)-90)
							bxvel#(t)=Sin(temp#)*(Sqr#((bxvel#(t)^2)+(byvel#(t)^2))*0.95)
							byvel#(t)=Cos(temp#)*(Sqr#((bxvel#(t)^2)+(byvel#(t)^2))*0.95)
							temp#=ATan(byvel#(t)/bxvel#(t))+(Rnd(180)-90)
							For e=0 To spknum
								If slife#(e)=0
									sx#(e)=bx#(t)
									sy#(e)=by#(t)
									sxvel#(e)=Sin(temp#)*(Sqr#((bxvel#(t)^2)+(byvel#(t)^2))*0.9)
									syvel#(e)=Cos(temp#)*(Sqr#((bxvel#(t)^2)+(byvel#(t)^2))*0.9)
									slife#(e)=50
									Exit
								EndIf
							Next
							temp#=ATan(byvel#(t)/bxvel#(t))+(Rnd(180)-90)
							For e=0 To spknum
								If slife#(e)=0
									sx#(e)=bx#(t)
									sy#(e)=by#(t)
									sxvel#(e)=Sin(temp#)*(Sqr#((bxvel#(t)^2)+(byvel#(t)^2))*0.9)
									syvel#(e)=Cos(temp#)*(Sqr#((bxvel#(t)^2)+(byvel#(t)^2))*0.9)
									slife#(e)=50
									Exit
								EndIf
							Next
							For e=0 To shknum
								If slife#(e)=0
									hx#(e)=bx#(t)
									hy#(e)=by#(t)
									hsca#(e)=0
									hlife#(e)=25
									Exit
								EndIf
							Next
							If bhit#(t)=bulhit Then Exit
						EndIf
					EndIf
				Next
				If bhit#(t)=bulhit Then blife#(t)=0
				Color 0,128,255
				Line bx#(t),by#(t),bx#(t)-(bxvel#(t)/2.0),by#(t)-(byvel#(t)/2.0)
			EndIf
		Next
	
	; Control and Draw Sparks
		For t=0 To spknum
			If slife#(t)>0
				sx#(t)=sx#(t)+sxvel#(t)
				sy#(t)=sy#(t)+syvel#(t)	
				sxvel#(t)=sxvel#(t)*0.9	
				syvel#(t)=syvel#(t)*0.9
				slife#(t)=slife#(t)-1
				Color 255,255,0
				Line sx#(t),sy#(t),sx#(t)-sxvel#(t),sy#(t)-syvel#(t)
			EndIf
		Next
		
	; Control and Draw Shocks
		For t=0 To shknum
			If hlife#(t)>0
				hsca#(t)=hsca#(t)+5
				hlife#(t)=hlife#(t)-1
				Color 255,128,0
				Oval hx#(t)-(hsca#(t)/2.0),hy#(t)-(hsca#(t)/2.0),hsca#(t),hsca#(t),0
			EndIf
		Next
	
	; Control and Draw Trails
		For t=0 To trlnum
			If tlife#(t)>0
				tx#(t)=tx#(t)+txvel#(t)
				ty#(t)=ty#(t)+tyvel#(t)
				txvel#(t)=txvel#(t)*0.9
				tyvel#(t)=tyvel#(t)*0.9
				tlife#(t)=tlife#(t)-1
				Color 255,255,0
				Line tx#(t),ty#(t),tx#(t)-(txvel#(t)*8.0),ty#(t)-(tyvel#(t)*8.0)
			EndIf
		Next
			
	; Control and Draw Asteroids
		astremain=0
		For t=0 To astmax
			If alife#(t)>0
				astremain=astremain+1
				ax#(t)=ax#(t)+axvel#(t)
				ay#(t)=ay#(t)+ayvel#(t)
				If ax#(t)<0 Then ax#(t)=640
				If ax#(t)>640 Then ax#(t)=0
				If ay#(t)<0 Then ay#(t)=480
				If ay#(t)>480 Then ay#(t)=0
				aan#(t)=aan#(t)+aanvel#(t)
				Color 128,128,128
				For i=0 To 4
					Line ax#(t)+(Sin(aan#(t)+(i*60))*adist#(t,i)),ay#(t)+(Cos(aan#(t)+(i*60))*adist#(t,i)),ax#(t)+(Sin(aan#(t)+((i+1)*60))*adist#(t,i+1)),ay#(t)+(Cos(aan#(t)+((i+1)*60))*adist#(t,i+1))
				Next
				Line ax#(t)+(Sin(aan#(t)+(5*60))*adist#(t,5)),ay#(t)+(Cos(aan#(t)+(5*60))*adist#(t,5)),ax#(t)+(Sin(aan#(t)+((0)*60))*adist#(t,0)),ay#(t)+(Cos(aan#(t)+((0)*60))*adist#(t,0))
				temp#=0
				For i=0 To 5
					temp#=temp#+adist#(t,i)
				Next
				adist#(t,6)=temp#/6.0	
			EndIf
		Next
		If astremain=0 Or lives<0
			Color 255,255,255
			Text 320,240,"Game Over, Press Escape to Exit.",1,1
		EndIf
	
	; Death
		If respawn=0 And shield=0
			For t=0 To astmax
				If alife#(t)>0
					If Sqr#(((x#-ax#(t))^2)+((y#-ay#(t))^2))=<adist#(t,6)+8
						lives=lives-1
						respawn=respawntime
						axvel#(t)=(axvel#(t)+xvel#)/2.0
						ayvel#(t)=(ayvel#(t)+yvel#)/2.0
						asize#(t)=asize(t)-1
						For i=0 To 5
							adist#(t,i)=(asize#(t)*8)+Rnd(16)
						Next
						If asize#(t)=<0
							alife#(t)=0
						EndIf
						If asize#(t)>0
							i=-1
							Repeat
								i=i+1
							Until alife#(i)=0 Or i=>astmax
							ax#(i)=ax#(t)
							ay#(i)=ay#(t)
							asize#(i)=asize#(t)
							alife#(i)=1
							axvel#(i)=Rnd(6)-3
							ayvel#(i)=Rnd(6)-3
							For m=0 To 5
								adist#(i,m)=(asize#(i)*8)+Rnd(16)
							Next
						EndIf
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						temp#=Rnd(3600)/10.0
						For i=0 To spknum
							If slife#(i)=0
								sx#(i)=x#
								sy#(i)=y#
								sxvel#(i)=(Sin(temp#)*20)+(xvel#/2.0)
								syvel#(i)=(Cos(temp#)*20)+(yvel#/2.0)
								slife#(i)=50
								Exit
							EndIf
						Next
						For i=0 To shknum
							If hlife#(i)=0
								hx#(i)=x#
								hy#(i)=y#
								hsca#(i)=0
								hlife#(i)=25
								Exit
							EndIf
						Next
						For i=0 To shknum
							If hlife#(i)=0
								hx#(i)=x#
								hy#(i)=y#
								hsca#(i)=100
								hlife#(i)=25
								Exit
							EndIf
						Next
					EndIf
				EndIf
			Next
		EndIf
	
	; Control Respawn
		If respawn>0 And lives>=0
			respawn=respawn-1
			If respawn=0
				x#=320
				y#=240
				xvel#=0
				yvel#=0
				an#=0
				shield=shieldtime
			EndIf
		EndIf
	
	; Control Shield
		If shield>0
			shield=shield-1
			Color 0,128,255
			Oval x#-13,y#-13,26,26,0
		EndIf
	
	; Draw Number of Lives
		Color 128,128,128
		If lives>=0 Then Text 0,444,"Lives: "+Str$(lives)
		If lives<0 Then Text 0,444,"Lives: Dead"
		If lives>0
			For t=0 To lives-1
				Color 255,255,255
				Line (11+(t*22))+(Sin(an#)*10),469+(Cos(an#)*10),(11+(t*22))+(Sin(an#+90)*4),469+(Cos(an#+90)*4)
				Line (11+(t*22))+(Sin(an#)*10),469+(Cos(an#)*10),(11+(t*22))+(Sin(an#-90)*4),469+(Cos(an#-90)*4)
				Line (11+(t*22))+(Sin(an#+90)*4),469+(Cos(an#+90)*4),(11+(t*22))+(Sin(an#)*-6),469+(Cos(an#)*-6)
				Line (11+(t*22))+(Sin(an#-90)*4),469+(Cos(an#-90)*4),(11+(t*22))+(Sin(an#)*-6),469+(Cos(an#)*-6)
			Next
		EndIf
	
	; Draw Ship
		If lives>=0 And respawn=0
			Color 255,255,255
			Line x#+(Sin(an#)*10),y#+(Cos(an#)*10),x#+(Sin(an#+90)*4),y#+(Cos(an#+90)*4)
			Line x#+(Sin(an#)*10),y#+(Cos(an#)*10),x#+(Sin(an#-90)*4),y#+(Cos(an#-90)*4)
			Line x#+(Sin(an#+90)*4),y#+(Cos(an#+90)*4),x#+(Sin(an#)*-6),y#+(Cos(an#)*-6)
			Line x#+(Sin(an#-90)*4),y#+(Cos(an#-90)*4),x#+(Sin(an#)*-6),y#+(Cos(an#)*-6)
		EndIf

; End Main Game Loop
	Wend

; End
	End
