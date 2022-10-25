; ID: 3199
; Author: cps
; Date: 2015-04-07 15:34:08
; Title: Rail Baron Pay Chart (Mac)
; Description: Computerised version of the Rail Baron Payment Chart

Import MaxGui.Drivers
SuperStrict
Local Tst1$=""
Local Temp1%=0; Local Temp2%=0; Local Temp3%=0; Local Temp4%=0; Local Temp5%=0
Local ScreenSize%=0
Local GUIFont:TGuiFont


Temp1=DesktopWidth()
If Temp1<640 Then' check to see if screen size allows 640 by 480
	Notify "Sorry screen sizes less than 640 by 480 "+Chr$(13)+"are not supported." 
	End' no support for less than 640 by 480
End If

Local Region:AreaNames = New AreaNames 'store containing area names
Region.initilise() ' loads the ara names


Local W1:TGadget = CreateWindow( "Rail Baron Payout Screen Size", 10,10,500,400,,WINDOW_CENTER | WINDOW_TITLEBAR)
Local W1Pan1:Tgadget=CreatePanel(20,20,460,360,W1)'Select screen size
Local W1Pan1Rad1:TGadget=CreateButton("640 by 480",180,70,100,20, W1Pan1,BUTTON_RADIO)
Local W1Pan1Rad2:TGadget=CreateButton("800 by 600",180,100,100,20, W1Pan1,BUTTON_RADIO)
Local W1Pan1Rad3:TGadget=CreateButton("1024 by 768",180,130,100,20, W1Pan1,BUTTON_RADIO)
Tst1="Please select the screen size you want to use "+Chr$(13)+"then click 'Continue'"
Local W1Pan1Lab1:TGadget=CreateLabel(Tst1,20,10,400,40, W1Pan1,LABEL_CENTER)
Local W1Pan1Lab2:TGadget=CreateLabel("",20,180,400,20, W1Pan1,LABEL_CENTER)
Local W1Pan1But1:TGadget=CreateButton("Continue",170,220,90,25,W1Pan1)

Tst1="You have selected "
If Temp1=640 Then 	
	SetButtonState(W1Pan1Rad1,True); Tst1=Tst1+"640 by 480"; DisableGadget(W1Pan1Rad2); DisableGadget(W1Pan1Rad3)
ElseIf Temp1=800 Then 
	SetButtonState(W1Pan1Rad2,True); Tst1=Tst1+"800 by 600"; DisableGadget(W1Pan1Rad3)	
Else
	SetButtonState(W1Pan1Rad3,True); Tst1=Tst1+"1024 by 768"	
End If
SetGadgetText(W1Pan1Lab2,Tst1)



'------------------------ Start Loop 1-------------------------
Temp1=0
Repeat
	PollEvent
	Select EventID()
  		Case EVENT_WINDOWCLOSE
  		   EndIt()
		Case EVENT_GADGETACTION
			Select EventSource()				
				Case W1Pan1Rad1'640 by 480
					SetGadgetText(W1Pan1Lab2,"You have selected 640 by 480")								
				Case W1Pan1Rad2'800 by 600
					SetGadgetText(W1Pan1Lab2,"You have selected 800 by 600")		
				Case W1Pan1Rad3' 1024 by 768
					SetGadgetText(W1Pan1Lab2,"You have selected 1024 by 768")
				Case W1Pan1But1 ' continue after chosing screen size
					If ButtonState(W1Pan1Rad1)=True Then
						ScreenSize=1
					Else If ButtonState(W1Pan1Rad2)=True Then
						ScreenSize=2
					Else 
						ScreenSize=3
					End If	
			End Select
	End Select
Until ScreenSize<>0
HideGadget(W1Pan1)
HideGadget(W1)

'------------------------ End Loop  1-------------------------
'ScreenSize=1 then 640 by 480 : ScreenSize=2 then 800 by 600 : ScreenSize=3 then 1024 by 768	
If Screensize=1 Then 
	temp1=10; temp2=10;temp3=638;temp4=478
Else If screensize=2 Then
	temp1=10; temp2=10;temp3=798;temp4=598
Else
	temp1=10; temp2=10;temp3=1022;temp4=766
End If
Local W2:TGadget = CreateWindow( "Rail Baron Payout Chart", temp1,temp2,temp3,temp4,,WINDOW_CENTER | WINDOW_TITLEBAR)
If screensize=1 Then temp2=28 Else If screensize=2 Then temp2=34 Else temp2=40
Local W2But1:Tgadget=CreateButton("Help",Temp1,Temp4-(Temp2+Temp1+24),3*Temp2,Temp2,W2)
Temp5=Temp3-(3*Temp2)-Temp1-5
Local W2But2:Tgadget=CreateButton("Exit",Temp5,Temp4-(Temp2+Temp1+24),3*Temp2,Temp2,W2)
Temp5=(Temp3/2)-50
Local W2But3:Tgadget=CreateButton("Mininimise",Temp5,Temp4-(Temp2+Temp1+24),100,Temp2,W2)


If Screensize=1 Then 'earnings panel ----------------
	temp1=187; temp2=20; temp3=257;temp4=390; GUIFont=LoadGuiFont( "Ariel",20)
Else If screensize=2 Then
	temp1=217; temp2=25;temp3=357;temp4=480; GUIFont=LoadGuiFont( "Ariel",28)
Else
	temp1=263; temp2=30;temp3=488;temp4=640; GUIFont=LoadGuiFont( "Ariel",36)
End If	
' use values below for PC font sizes
'GUIFont=LoadGuiFont( "Ariel",16) GUIFont=LoadGuiFont( "Ariel",18) GUIFont=LoadGuiFont( "Ariel",20)

Local W2P3:TGadget=CreatePanel(Temp1,Temp2,Temp3,Temp4,W2)
SetPanelColor(W2P3,250,250,250)
If screensize=1 Then temp5=35 Else If screensize=2 Then temp5=45 Else temp5=55
Local W2P3L1:TGadget=CreateLabel("xx",10,10,Temp3-20,Temp5,W2P3,LABEL_CENTER)
Local W2P3L2:TGadget=CreateLabel("to",(Temp3/2)-30,2*Temp5,60,Temp5,W2P3,LABEL_CENTER)
Local W2P3L3:TGadget=CreateLabel("xx",10,4*Temp5,Temp3-20,Temp5,W2P3,LABEL_CENTER)
Local W2P3L4:TGadget=CreateLabel("pays",(Temp3/2)-30,6*Temp5,60,Temp5,W2P3,LABEL_CENTER)
Local W2P3L5:TGadget=CreateLabel("0.0",10,8*Temp5,Temp3-20,Temp5,W2P3,LABEL_CENTER)
Local W2P3L6:TGadget=CreateLabel("Thousand Dollars.",10,10*Temp5,Temp3-20,Temp5,W2P3,LABEL_CENTER)
SetGadgetFont(W2P3L1,GUIFont); SetGadgetFont(W2P3L3,GUIFont); SetGadgetFont(W2P3L5,GUIFont)
SetGadgetTextColor(W2P3L1,1,220,1); SetGadgetTextColor(W2P3L3,1,1,220)


If Screensize=1 Then 'start panel ---------------------------
	temp1=3; temp2=20;temp3=180;temp4=390; GUIFont=LoadGuiFont( "Ariel",11)
Else If screensize=2 Then
	temp1=3; temp2=25;temp3=210;temp4=480; GUIFont=LoadGuiFont( "Ariel",14)
Else
	temp1=3; temp2=30;temp3=256;temp4=640; GUIFont=LoadGuiFont( "Ariel",18)
End If	
Local W2P1:TGadget=CreatePanel(Temp1,Temp2,Temp3,Temp4,W2,PANEL_GROUP,"Start")

'use values below for PC font sizes
' GUIFont=LoadGuiFont( "Ariel",9.4) GUIFont=LoadGuiFont( "Ariel",12) GUIFont=LoadGuiFont( "Ariel",14)

SetPanelColor(W2P1,100,255,100)
Temp5=(Temp4/2)-(Temp1+26)
Local W2P1Lst1:Tgadget=CreateListBox(Temp1,Temp1,Temp3-(4*Temp1),Temp5,W2P1)
Region.ShowRegions(W2P1Lst1)
Temp2=Temp5+Temp1+3
Local W2P1Lst2:Tgadget=CreateListBox(Temp1,Temp2,Temp3-(4*Temp1),Temp5+30,W2P1)
SelectGadgetItem(W2P1Lst1,0)'set default position of list 1, area names
Region.ShowTowns(0,W2P1Lst2)
SelectGadgetItem(W2P1Lst2,0)'set default position
Region.ShowSelection(0,0,W2P3L1); Region.SetStArea(0); Region.SetStTown(0)
SetGadgetFont(W2P1Lst1,GUIFont); SetGadgetFont(W2P1Lst2,GUIFont)


If Screensize=1 Then 'destination -------------------------
	temp1=449; temp2=20;temp3=180;temp4=390
Else If screensize=2 Then
	temp1=579; temp2=25;temp3=210;temp4=480
Else
	temp1=757; temp2=30;temp3=256;temp4=640
End If	
Local W2P2:TGadget=CreatePanel(Temp1,Temp2,Temp3,Temp4,W2,PANEL_GROUP,"Destination")
Temp1=3

SetPanelColor(W2P2,170,170,255)
Temp5=(Temp4/2)-(Temp1+26)
Local W2P2Lst1:Tgadget=CreateListBox(Temp1,Temp1,Temp3-(4*Temp1),Temp5,W2P2)
Region.ShowRegions(W2P2Lst1)
Temp2=Temp5+Temp1+3
Local W2P2Lst2:Tgadget=CreateListBox(Temp1,Temp2,Temp3-(4*Temp1),Temp5+30,W2P2)
SelectGadgetItem(W2P2Lst1,0)'set default position of list 1, area names
Region.ShowTowns(0,W2P2Lst2)
SelectGadgetItem(W2P2Lst2,0)'set default position
Region.ShowSelection(0,0,W2P3L3); Region.SetEndArea(0); Region.SetEndTown(0)
SetGadgetFont(W2P2Lst1,GUIFont); SetGadgetFont(W2P2Lst2,GUIFont)
Region.ShowPay(W2P3L5)	


'------------------------ Start of main loop -------------------------
Repeat
	PollEvent
	Select EventID()
  		Case EVENT_WINDOWCLOSE
  		   EndIt()
		Case EVENT_GADGETACTION
			Select EventSource()
				Case W2But3' minimise window
					MinimizeWindow(W2)
			
				Case W2But1'Help
					TsT1="Click on a Start Area : Top Left."+Chr$(13)
					Tst1=Tst1+"Click on a Start Town : Bottom Left."+Chr$(13)
					Tst1=Tst1+"Click on a Destination Area : Top Right."+Chr$(13)
					Tst1=Tst1+"Click on a Destination Town : Bottom Right."+Chr$(13)+Chr$(13)
					TsT1=Tst1+"Start and Destination plus payment shown in the centre."
					Notify(TsT1)

				Case W2But2 'Exit
					EndIt()				
			End Select
			
		Case EVENT_GADGETSELECT
		Select EventSource()
			Case W2P1Lst1' start area
				Temp1=EventData()
				If Temp1<>-1 Then 'something selected
				
					Region.ShowTowns(Temp1,W2P1Lst2)
					SelectGadgetItem(W2P1Lst2,0)'set default position
					Region.ShowSelection(Temp1,0,W2P3L1); Region.SetStArea(Temp1)
					Region.SetStTown(0); Region.ShowPay(W2P3L5)				
					
					
				End If		
			Case W2P1Lst2'start town	
				Temp1=EventData()
				If Temp1<>-1 Then 'something selected
					Region.ShowSelection(Region.GetStArea(),Temp1,W2P3L1)
					Region.SetStTown(Temp1); Region.ShowPay(W2P3L5)	
					
				End If
		
			
			Case W2P2Lst1' destination area
				Temp1=EventData()
				If Temp1<>-1 Then 'something selected						
					Region.ShowTowns(Temp1,W2P2Lst2)
					SelectGadgetItem(W2P2Lst2,0)'set default position
					Region.ShowSelection(Temp1,0,W2P3L3); Region.SetEndArea(Temp1)
					Region.SetEndTown(0); Region.ShowPay(W2P3L5)						
					
				End If	
				
			Case W2P2Lst2'destination town
				Temp1=EventData()
				If Temp1<>-1 Then 'something selected
					Region.ShowSelection(Region.GetEndArea(),Temp1,W2P3L3)
					Region.SetEndTown(Temp1); Region.ShowPay(W2P3L5)	
				
				End If
						
      	End Select

		
			
			
			
	End Select
Forever

'------------------------ End of Main Loop -------------------------	

'------------------------ Start of Functions -------------------------


Function EndIt()'common exit point
	Select Confirm("Are you sure you want to quit?")
		Case 1
			End
		Case 0	
			Return
	End Select
End Function

'------------------------ End of Functions -------------------------

'   ---------------------           Start of Types    ---------------

Type AreaNames Extends TownNames	
	Global Dat$' working directory
	Field Town:TownNames[7]' town names by area
	Field Names$[7]'area names
	Field NumTown%[7]' number of towns in a given area
	Field StArea%=0' start area index
	Field EndArea%'destination area index
	Field StTown%=0' start town index
	Field EndTown%=0' destination town index
	Field Chart$[67,67]' payment chart in thousands of dollars
		
	Method Initilise()' loads area and town names into vars
		Local Tx1%=0; Local Tx2%=0
		Local Myfile:TStream
		
		Dat=CurrentDir()
		Names[0]="NORTHEAST"; Names[1]="SOUTHEAST"; Names[2]="NORTH CENTRAL"
		Names[3]="SOUTH CENTRAL"; Names[4]="PLAINS"; Names[5]="NORTHWEST"
		Names[6]="SOUTHWEST"
		NumTown[0]=9; NumTown[1]=11; NumTown[2]=8; NumTown[3]=11; NumTown[4]=9
		NumTown[5]=9; NumTown[6]=10
		For Tx1=0 To 6
			Town[Tx1]=New TownNames
		Next		
		MyFile=ReadFile(Dat+"/NAMES.DAT")		
		For Tx1=0 To 6
			For Tx2=0 To NumTown[Tx1]-1
				Town[Tx1].TName[Tx2]=ReadLine(MyFile)		
			Next
		Next
		CloseStream MyFile		
		MyFile=ReadFile(Dat+"/PAYS.DAT")		
		For Tx1=0 To 66
			For Tx2=0 To 66
				Chart[Tx1,Tx2]=ReadLine(MyFile)
			Next
		Next
		CloseStream MyFile		
	End Method

	Method ShowPay(Lst:TGadget)'expects label name to show payment in
		Local Tx1%=0 ; Local Tx2%=0; Local Tx3%=0
 		If StArea=0 Then
			Tx2=0
		Else
			For Tx1=0 To StArea-1
				Tx2=Tx2+NumTown[Tx1]
			Next
		End If
		Tx2=Tx2+StTown' tx2= index position of start town for payment chart array		
		If EndArea=0 Then
			Tx3=0
		Else
			For Tx1=0 To EndArea-1
				Tx3=Tx3+NumTown[Tx1]
			Next
		End If
		Tx3=Tx3+EndTown' tx3= index position of end town for payment chart array	
		SetGadgetText(Lst,Chart[Tx2,Tx3])
	End Method
	
	Method ShowRegions(Lst:Tgadget)'expects list name to display in
		Local Tx1%=0
		ClearGadgetItems(Lst)
		For Tx1=0 To 6
			AddGadgetItem(Lst,Names[Tx1])
		Next
	End Method
	
	Method ShowTowns(Tx1%,Lst:TGadget)' tx1 = area index 0 to 6: Lst = list gadget name for output
		Local Tx2%=0
		ClearGadgetItems(Lst)
		For Tx2=0 To NumTown[Tx1]-1
			AddGadgetItem(Lst,Town[Tx1].TName[Tx2])		
		Next		
	End Method
	
	Method ShowSelection(Tx1%,Tx2%,Lab:Tgadget)' tx1=area index, tx2=town index, lab = where to show it
		SetGadgetText(Lab,Town[Tx1].TName[Tx2])	
	End Method

	Method SetStArea(Tx1%)' set start area index
		StArea=Tx1
	End Method
	
	Method GetStArea%()' get start area index
		Local Tx2%=0
		Tx2=StArea
		Return Tx2
	End Method
	
	Method SetEndArea(Tx1%)' set destination area index
		EndArea=Tx1
	End Method
	
	Method GetEndArea%()'get destination index
		Local Tx2%=0
		Tx2=EndArea
		Return Tx2
	End Method
	
	Method SetStTown(Tx1%)' set start town index
		StTown=Tx1
	End Method
	
	Method SetEndTown(Tx1%)' set detination town index
		EndTown=Tx1
	End Method

End Type


Type TownNames
	Field TName$[11]

End Type
