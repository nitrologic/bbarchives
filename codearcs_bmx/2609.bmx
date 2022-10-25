; ID: 2609
; Author: Nate the Great
; Date: 2009-11-08 15:45:16
; Title: neural network
; Description: Some bmax types to make/simulate neural networks

SuperStrict
SeedRnd(MilliSecs())
Graphics 800,600,0,60

For Local i:Int = 0 To 20
	food.Create(Rnd(800),Rnd(600))
Next

For Local b:Int = 0 To 20
	Local f:fish = fish.Create(Rnd(800),Rnd(600),0,fish.makebrain:neuralnet())
Next

Global generation:Int = 0
Global simstep:Long = 0

Global finalsimstep:Long
Global finalflag:Byte

Global render:Int = True
Global gopast10:Int = True

Global selected:fish

While Not AppTerminate()
	If KeyHit(key_space) Then
		render = 1-render
	EndIf
	If KeyHit(key_f) Then
		gopast10 = 1-gopast10
	EndIf
	If render Then Cls
	simstep = simstep + 1
	
	food.update()
	fish.update()
	
	Local sf:fish
	For sf:fish = EachIn fish.fishlist
		If sf:fish = selected:fish And sf.alive Then
			sf.drawnnet()
		EndIf
	Next
	
	If MouseHit(1)
		selected:fish = Null
		For sf:fish = EachIn fish.fishlist
			Local dx# = MouseX()-sf.x
			Local dy# = MouseY()-sf.y
			Local dist# = Sqr(dx*dx + dy*dy)
			If dist <= 20 Then
				selected:fish = sf:fish
			EndIf
		Next
	EndIf
	
	If KeyHit(key_p) And selected:fish  <> Null Then
		neuralnet.printnnet(selected.brain:neuralnet)
	EndIf
	neuralnet.update()
	
	SetColor 255,255,255
	DrawText "Generation: "+generation,1,1
	DrawText "Simulation Step: "+simstep,1,20
	If gopast10 = True Then
		DrawText "Simulating full generation (press f to change)",1,40
	Else
		DrawText "Only simulating generations to 10 (press f to change)",1,40
	EndIf
	
	If fish.livecount <= 10 And finalflag = False Then
		finalsimstep = simstep-1
		finalflag = True
	EndIf
	If (finalflag And gopast10 = False) Or fish.livecount = 0 Then
		generation :+ 1
		
		finalflag = False
		Print (generation-1)+ "  length: "+simstep
				
		For Local f:fish = EachIn fish.fishlist
			If f.life >= finalsimstep And f.life > 0 Then
				'Print "good "+f.life
				f.alive = True
				f.hunger = 0
				Local ff:fish = fish.Create(Rnd(800),Rnd(600),0,neuralnet.copy(f.brain:neuralnet))
				ff.makemistake()
			ElseIf f.life > 0
				'Print "bad "+f.life
				f.remove()
			EndIf
		Next
		simstep = 0
	EndIf
	
	If render Then Flip
Wend

Type food
	Global foodlist:TList = New TList
		
	Field x#
	Field y#
	Field dist#
	
	Function Create:food(x#,y#)
		Local f:food = New food
		f.x = x
		f.y = y
		foodlist.addlast(f:food)
		Return f:food
	End Function
	
	Function update()
		For Local f:food = EachIn foodlist
			If render Then f.draw()
		Next
	End Function
	
	Method draw()
		SetColor(0,255,0)
		DrawOval(x-5,y-5,10,10)
	End Method
	
	Function getfoodsmell:Float(x#,y#)		'returns the amount of smell of food there is at a certain point
		Local smell:Float = 0
		For Local f:food = EachIn foodlist
			Local dx:Float = x-f.x
			Local dy:Float = y-f.y
			Local dist:Float = Sqr(dx*dx + dy*dy)
			If dist < 50 And dist > 15 Then
				smell :+ (50-dist)/50
			ElseIf dist <= 15 Then
				smell :+ 1
			EndIf
		Next
		Return smell#
	End Function
	
	Function getclosefood:food(x#,y#)	'returns the food closest to given point x,y
		Local closest:Float = 10000
		Local closestfood:food
		Local dist:Float
		For Local f:food = EachIn foodlist
			Local dx:Float = x-f.x
			Local dy:Float = y-f.y
			dist:Float = Sqr(dx*dx + dy*dy)
			If dist < closest Then
				closestfood:food = f:food
				closest = dist
			EndIf
		Next
		If closest < 25 Then Return closestfood:food
	End Function
End Type

Type Fish
	Global fishlist:TList = New TList
	Global livecount:Int
	
	Field x#
	Field y#
	Field angle#
	Field anglespd#
	Field hunger#
	Field heartcount:Int
	Field hearton:Byte
	Field brain:neuralnet
	Field alive:Byte
	Field life:Long
	Field gen:Int
	
	Function update()
		livecount = 0
		Local onedied:Int = 0
		Local totcnt:Int
		For Local f:fish = EachIn fishlist
			totcnt = totcnt + 1
			If f.alive Then
				livecount :+ 1
				f.hunger = f.hunger + .001
				f.heartcount = f.heartcount + 1
				If f.heartcount > 20 Then
					f.hearton = 1-f.hearton
					f.heartcount = 0
				EndIf
				Local hrt:neuron = f.brain.findneuron("heart")
				hrt.setenergy(f.hearton)
				Local hng:neuron = f.brain.findneuron("hunger")
				hng.setenergy(f.hunger)
				Local lft:neuron = f.brain.findneuron("left")
				Local rgt:neuron = f.brain.findneuron("right")
				f.anglespd# = f.anglespd - lft.getenergy()*5 + rgt.getenergy()*5	'calculate steering
				f.anglespd = f.anglespd * .9
				If f.anglespd > 4 Then f.anglespd = 4
				If f.anglespd < -4 Then f.anglespd = -4
				f.angle = f.angle + f.anglespd
				Local spd# = (lft.getenergy()*7+rgt.getenergy()*7)
				If spd > 5 Then spd = 5
				If spd < -5 Then spd = -5
				f.x = f.x + Cos(f.angle)*spd
				f.y = f.y + Sin(f.angle)*spd
				If render Then f.draw()
				f.checkfood()
				f.checkenemy()
				f.life = simstep
				If Abs(f.hunger) > 1 And onedied = 0 Then
					f.alive = False
					f.life = simstep
					onedied = 1
				EndIf
				If f.x < -20 Then f.x = 820
				If f.x > 820 Then f.x = -20
				If f.y < -20 Then f.y = 620
				If f.y > 620 Then f.y = -20
			'	neuralnet.printnnet(f.brain:neuralnet)		
			EndIf
		Next
	End Function
	
	Method draw()
		SetColor(100,255,100)
		DrawLine(x,y,x+Cos(angle+30)*30,y+Sin(angle+30)*30)
		DrawLine(x,y,x+Cos(angle-30)*30,y+Sin(angle-30)*30)
		SetColor(0,50,255)
		DrawOval(x-20,y-20,40,40)
		SetColor(255,255,255)
		DrawLine(x,y,x+Cos(angle)*30,y+Sin(angle)*30)
		SetColor ((hunger+1)/2)*255,((-hunger+1)/2)*255,0
		SetBlend alphablend
		SetAlpha .5
		DrawRect x-20,y-35,((-hunger+1)/2)*40,5
		SetAlpha 1
		SetColor 255,255,255
		DrawText gen,x-10,y-10
	End Method
	
	Method checkfood()
		Local x1:Float = x+Cos(angle-30)*30
		Local y1:Float = y+Sin(angle-30)*30
		Local smell1# = food.getfoodsmell(x1,y1)
		Local foodleft:neuron = brain.findneuron("foodlft")
		foodleft.setenergy(smell1)
		Local x2:Float = x+Cos(angle+30)*30
		Local y2:Float = y+Sin(angle+30)*30
		Local smell2# = food.getfoodsmell(x2,y2)
		Local foodright:neuron = brain.findneuron("foodrgt")
		foodright.setenergy(smell2)
		Local f:food = food.getclosefood(x,y)
		If f:food >< Null Then
			f.x = Rnd(800)
			f.y = Rnd(600)
			hunger :- .7
		EndIf
	End Method
	
	Method checkenemy()
		Local smell1#
		Local smell2#
		For Local f:fish = EachIn fishlist
			If f:fish >< Self Then
				Local dx# = (x+Cos(angle-30)*30)-f.x
				Local dy# = (y+Sin(angle-30)*30)-f.y
				Local dist# = Sqr(dx*dx + dy*dy)
				If dist <= 50 Then
					smell1# = smell1# + (50-dist#)/50.0
				EndIf
				dx# = (x+Cos(angle+30)*30)-f.x
				dy# = (y+Sin(angle+30)*30)-f.y
				dist# = Sqr(dx*dx + dy*dy)
				If dist <= 50 Then
					smell2# = smell2# + (50-dist#)/50.0
				EndIf
			EndIf
		Next
		brain.findneuron("enemylft").setenergy(smell1)
		brain.findneuron("enemyrgt").setenergy(smell2)
	End Method
	
	Function Create:fish(x#,y#,angle#,brain:neuralnet)
		Local f:fish = New fish
		f.x = x
		f.y = y
		f.angle = angle
		f.brain = brain
		f.heartcount = Rnd(0,19)
		f.hearton = Rnd(0,1)
		f.alive = True
		f.gen = generation
		fishlist.addlast(f:fish)
		Return f:fish
	End Function
	
	Method drawnnet()
		SetAlpha .4
		SetColor 255,255,255
		DrawRect x+20,y+20,150,150
		SetAlpha 1
		
		SetColor 0,0,(brain.findneuron("heart").getenergy()*255)
		DrawOval x+87,y+92,6,6
		
		SetColor ((-brain.findneuron("hunger").getenergy())*255),0,(brain.findneuron("hunger").getenergy()*255)
		DrawOval x+97,y+92,6,6
		
		SetColor ((-brain.findneuron("n1").getenergy())*255),0,(brain.findneuron("n1").getenergy()*255)
		DrawOval x+92,y+40,6,6
		
		SetColor ((-brain.findneuron("n2").getenergy())*255),0,(brain.findneuron("n2").getenergy()*255)
		DrawOval x+52,y+115,6,6
		
		SetColor ((-brain.findneuron("n3").getenergy())*255),0,(brain.findneuron("n3").getenergy()*255)
		DrawOval x+132,y+115,6,6
		
		SetColor ((-brain.findneuron("c1").getenergy())*255),0,(brain.findneuron("c1").getenergy()*255)
		DrawOval x+92,y+140,6,6
		
		SetColor ((-brain.findneuron("left").getenergy())*255),0,(brain.findneuron("left").getenergy()*255)
		DrawOval x+30,y+70,6,6
		
		SetColor ((-brain.findneuron("right").getenergy())*255),0,(brain.findneuron("right").getenergy()*255)
		DrawOval x+157,y+70,6,6
		
		SetColor ((-brain.findneuron("foodlft").getenergy())*255),0,(brain.findneuron("foodlft").getenergy()*255)
		DrawOval x+30,y+30,6,6
		
		SetColor ((-brain.findneuron("foodrgt").getenergy())*255),0,(brain.findneuron("foodrgt").getenergy()*255)
		DrawOval x+157,y+30,6,6
	End Method
	
	Function MakeBrain:neuralnet()
		Local n:neuralnet = neuralnet.Create()
		Local hrt:neuron = neuron.Create(0,"heart")	'this neuron will "beat" at a steady rate
		Local hngr:neuron = neuron.Create(-1,"hunger")	'this neuron will slowly turn on if a fish has not eaten in a while, negative if a fish is over full
		
		Local n1:neuron = neuron.Create(Rnd(-1,1.0),"n1")	'neurons that do the logic
		Local n2:neuron = neuron.Create(Rnd(-1,1.0),"n2")
		Local n3:neuron = neuron.Create(Rnd(-1,1.0),"n3")
		
		Local nc1:neuron = neuron.Create(Rnd(-1,1.0),"c1")
				
		Local lft:neuron = neuron.Create(-1,"left")		'neurons that control movement
		Local rgt:neuron = neuron.Create(-1,"right")
						
		Local foodlft:neuron = neuron.Create(-1,"foodlft")	'left and right food detectors
		Local foodrgt:neuron = neuron.Create(-1,"foodrgt")
		
		Local enemyrgt:neuron = neuron.Create(-1,"enemyrgt")	'left and right enemy sensors
		Local enemylft:neuron = neuron.Create(-1,"enemylft")
		
		n.addneuron(hrt)
		n.addneuron(hngr)
		n.addneuron(n1)
		n.addneuron(n2)
		n.addneuron(n3)
		n.addneuron(lft)
		n.addneuron(rgt)
		n.addneuron(foodlft)
		n.addneuron(foodrgt)
		n.addneuron(nc1)
		n.addneuron(enemyrgt)
		n.addneuron(enemylft)
		
		
		Local c1:connection = connection.Create(hrt,n1,Rnd(-1,1.0))
		Local c2:connection = connection.Create(hrt,n2,Rnd(-1,1.0))
		Local c3:connection = connection.Create(hrt,n3,Rnd(-1,1.0))

		Local c4:connection = connection.Create(hngr,n1,Rnd(-1,1.0))
		Local c5:connection = connection.Create(hngr,n2,Rnd(-1,1.0))
		Local c6:connection = connection.Create(hngr,n3,Rnd(-1,1.0))


		Local c7:connection = connection.Create(n1,n2,Rnd(-1,1.0))
		Local c8:connection = connection.Create(n2,n1,Rnd(-1,1.0))

		Local c9:connection = connection.Create(n2,n3,Rnd(-1,1.0))
		Local c10:connection = connection.Create(n3,n2,Rnd(-1,1.0))

		Local c11:connection = connection.Create(n1,n3,Rnd(-1,1.0))
		Local c12:connection = connection.Create(n3,n1,Rnd(-1,1.0))
		
		Local c13:connection = connection.Create(foodlft,n1,Rnd(-1,1.0))
		Local c14:connection = connection.Create(foodlft,n2,Rnd(-1,1.0))

		Local c15:connection = connection.Create(foodrgt,n3,Rnd(-1,1.0))
		Local c16:connection = connection.Create(foodrgt,n1,Rnd(-1,1.0))
		
		Local c17:connection = connection.Create(n1,lft,Rnd(-1,1.0))
		Local c18:connection = connection.Create(n2,lft,Rnd(-1,1.0))

		Local c19:connection = connection.Create(n3,rgt,Rnd(-1,1.0))
		Local c20:connection = connection.Create(n1,rgt,Rnd(-1,1.0))
		
		Local c21:connection = connection.Create(n1,nc1,Rnd(-1,1.0))
		Local c22:connection = connection.Create(n2,nc1,Rnd(-1,1.0))
		Local c23:connection = connection.Create(n3,nc1,Rnd(-1,1.0))
		
		Local c24:connection = connection.Create(enemyrgt,n1,Rnd(-1,1.0))
		Local c25:connection = connection.Create(enemyrgt,n3,Rnd(-1,1.0))
		
		Local c26:connection = connection.Create(enemylft,n1,Rnd(-1,1.0))
		Local c27:connection = connection.Create(enemylft,n2,Rnd(-1,1.0))
		
		n.addconnection(c1)
		n.addconnection(c2)
		n.addconnection(c3)
		n.addconnection(c4)
		n.addconnection(c5)
		n.addconnection(c6)
		n.addconnection(c7)
		n.addconnection(c8)
		n.addconnection(c9)
		n.addconnection(c10)
		n.addconnection(c11)
		n.addconnection(c12)
		n.addconnection(c13)
		n.addconnection(c14)
		n.addconnection(c15)
		n.addconnection(c16)
		n.addconnection(c17)
		n.addconnection(c18)
		n.addconnection(c19)
		n.addconnection(c20)
		n.addconnection(c21)
		n.addconnection(c22)
		n.addconnection(c23)
		n.addconnection(c24)
		n.addconnection(c25)
		n.addconnection(c26)
		n.addconnection(c27)

		Return n:neuralnet
	End Function
	
	Method makemistake()
		Local mvar:Int
		For Local n:neuron = EachIn brain.neuronlist
			mvar = Rnd(50)
			If mvar = 10 Then
				n.setthreshold(Rnd(-1,1.0))
				Print "changed threshold"
			EndIf
		Next
		For Local c:connection = EachIn brain.connectionlist
			mvar = Rnd(50)
			If mvar = 10 Then
				c.setweight(Rnd(-1,1.0))
				Print "changed weight"
			EndIf
		Next
		mvar = Rnd(10)
		If mvar = 2 Then
			Local nn:neuron = neuron.Create(Rnd(-1,1),"x")
			Local n1:neuron
			mvar = Rnd(1,brain.neuronlist.count())
			Local cnt:Int = 0
			For Local n:neuron = EachIn brain.neuronlist
				cnt :+ 1
				If cnt = mvar Then n1:neuron = n:neuron
			Next
			Local cc:connection = connection.Create(n1,nn,Rnd(-1,1.0))
			brain.addconnection(cc)
			Local n2:neuron
			mvar = Rnd(1,brain.neuronlist.count())
			cnt:Int = 0
			For Local n:neuron = EachIn brain.neuronlist
				cnt :+ 1
				If cnt = mvar Then n2:neuron = n:neuron
			Next
			cc:connection = connection.Create(nn,n2,Rnd(-1,1.0))
			brain.addconnection(cc)
			brain.addneuron(nn)
			Print "new neuron!"
		EndIf
		mvar = Rnd(10)
		If mvar = 3 Then
			Print "new connection"
			Local n1:neuron
			Local n2:neuron
			mvar = Rnd(1,brain.neuronlist.count())
			Local cnt:Int = 0
			For Local n:neuron = EachIn brain.neuronlist
				cnt :+ 1
				If cnt = mvar Then n1:neuron = n:neuron
			Next
			mvar = Rnd(1,brain.neuronlist.count())
			cnt:Int = 0
			For Local n:neuron = EachIn brain.neuronlist
				cnt :+ 1
				If cnt = mvar Then n2:neuron = n:neuron
			Next
			If n1:neuron >< n2:neuron Then
				Local cc:connection = connection.Create(n1,n2,Rnd(-1,1.0))
				brain.addconnection(cc)
			EndIf
		EndIf
	End Method
	
	Method remove()
		brain.remove()
		fishlist.remove(Self)
	End Method
	
End Type


Type neuralnet
	Global neuralnetlist:TList = New TList
	
	Field neuronlist:TList
	Field connectionlist:TList
	
	Function printnnet(n:neuralnet)
		For Local nn:neuron = EachIn n.neuronlist
			Print "neuron: "+nn.getname()+" Energy: "+nn.getenergy()+" Threshold: "+nn.getthreshold()
		Next
		For Local c:connection = EachIn n.connectionlist
			Print "connection from "+c.getsource().getname()+" to "+c.getdestination().getname()+"  Weight: "+c.getweight()
		Next
	End Function
	
	Function Create:neuralnet()
		Local n:neuralnet = New neuralnet
		n.neuronlist:TList = New TList
		n.connectionlist:TList = New TList
		neuralnetlist.addlast(n:neuralnet)
		Return n:neuralnet
	End Function
	
	Method addneuron(n:neuron)
		neuronlist.addlast(n:neuron)
	End Method
	
	Method addconnection(c:connection)
		connectionlist.addlast(c:connection)
	End Method
	
	Method removeneuron(n:neuron)
		neuronlist.remove(n:neuron)
	End Method
	
	Method removeconnection(c:connection)
		connectionlist.remove(c:connection)
	End Method
	
	Method remove()
		neuralnetlist.remove(Self)
		For Local n:neuron = EachIn neuronlist
			n.remove()
		Next		
		For Local c:connection = EachIn connectionlist
			c.remove()
		Next		
	End Method
	
	Method findneuron:neuron(name$)
		For Local n:neuron = EachIn neuronlist
			If n.name$ = name$
				Return n:neuron
			EndIf
		Next
	End Method
	
	Function update()
		connection.clearenergy()
		connection.doweight()
		neuron.clearenergy()
		connection.dotransfer()
	End Function
	
	Function copy:neuralnet(n:neuralnet)
		Local nn:neuralnet = neuralnet.Create()
		For Local ne:neuron = EachIn n.neuronlist
			Local ne2:neuron = neuron.copy(ne:neuron)
			nn.neuronlist.addlast(ne2:neuron)
		Next
		For Local c:connection = EachIn n.connectionlist
			Local srcn:neuron
			Local destn:neuron
			Local cntgoal:Int = n.getlistposition(c.getsource())
			Local cnt:Int = 0
			For srcn:neuron = EachIn nn.neuronlist
				cnt:+1
				If cnt = cntgoal Then
					Exit
				EndIf
			Next
			cntgoal:Int = n.getlistposition(c.getdestination())
			cnt:Int = 0
			For destn:neuron = EachIn nn.neuronlist
				cnt:+1
				If cnt = cntgoal Then
					Exit
				EndIf
			Next

			Local cc:connection = connection.Create(srcn:neuron,destn:neuron,c.weight#)
			nn.connectionlist.addlast(cc:connection)
		Next
		Return nn:neuralnet
	End Function
	
	Method getlistposition:Int(nn:neuron)
		Local cnt:Int = 0
		For Local n:neuron = EachIn neuronlist
			cnt:+1
			If n:neuron = nn:neuron Then
				Return cnt
			EndIf
		Next
	End Method
End Type


Type neuron
	Global neuronlist:TList = New TList
	
	Field threshold#
	Field energy#
	Field name$
	
	Function Create:neuron(threshold#,name$ = "")
		Local n:neuron = New neuron
		n.threshold = threshold
		n.energy = 0
		n.name$ = name$
		neuronlist.addlast(n:neuron)
		Return n:neuron		
	End Function
	
	Method getthreshold:Float()
		Return threshold
	End Method
	
	Method setthreshold(t#)
		threshold = t
	End Method
	
	Method getenergy:Float()
		Return energy#
	End Method
	
	Method getenergyt:Float()
		If energy => threshold Then Return energy
		Return 0
	End Method
	
	Method setenergy(e#)
		energy = e
	End Method
	
	Method remove()
		neuronlist.remove(Self)
	End Method
	
	Method getname:String()
		Return name$
	End Method
	
	Function clearenergy()							'''''''''''Corrected this function'''''''''''''''''''''
		For Local n:neuron = EachIn neuronlist
			If n.getenergy() => n.getthreshold() Then
				n.setenergy(0)
			Else
				n.setenergy(n.getenergy()*.98)
			EndIf
			
		Next
	End Function
	
	
	Function copy:neuron(n:neuron)
		Local nn:neuron = neuron.Create(n.getthreshold(),n.getname())
		Return nn:neuron
	End Function
	
End Type

Type connection
	Global connectionlist:TList = New TList
	
	Field src:neuron
	Field dest:neuron
	Field weight#
	Field energy#
	
	Function Create:connection(src:neuron,dest:neuron,weight#)
		Local c:connection = New connection
		c.src:neuron = src:neuron
		c.dest:neuron = dest:neuron
		c.weight = weight
		c.energy = 0
		connectionlist.addlast(c:connection)
		Return(c:connection)
	End Function
	
	Method getweight:Float()
		Return weight#
	End Method
	
	Method setweight(w#)
		weight = w
	End Method
	
	Method getenergy:Float()
		Return energy#
	End Method
	
	Method setenergy(e#)
		energy = e
	End Method
	
	Method getsource:neuron()
		Return src:neuron
	End Method
	
	Method setsource(s:neuron)
		src:neuron = s:neuron
	End Method
	
	Method getdestination:neuron()
		Return dest:neuron
	End Method
	
	Method setdestination(d:neuron)
		dest:neuron = d:neuron
	End Method
	
	Method remove()
		connectionlist.remove(Self)
	End Method
	
	Function clearenergy()
		For Local c:connection = EachIn connectionlist
			c.setenergy(0)
		Next
	End Function
	
	Function doweight()
		For Local c:connection = EachIn connectionlist
			c.setenergy(c.getsource().getenergyt()*c.getweight())
		Next
	End Function
	Function dotransfer()
		For Local c:connection = EachIn connectionlist
			c.getdestination().setenergy(c.getdestination().getenergy() + c.getenergy())
		Next
	End Function
	
End Type
