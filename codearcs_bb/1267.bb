; ID: 1267
; Author: Arem
; Date: 2005-01-24 17:15:02
; Title: Arem IM
; Description: Open Source IM Program

;Arem IM Server

AppTitle "Arem IM Server"

Global server=CreateTCPServer(3334)
Global instructions$

If server
	Print "Server Initialized."
Else
	End
End If

Type registrar
	Field content$
End Type

Type client
	Field connection,name$,timer,deleted
End Type

While Not KeyDown(1)
	connect=AcceptTCPStream(server)
	
	If connect
		person.client=New client
		person\connection=connect
		person\timer=CreateTimer(1)
		Print "Somebody done hit up the server!"
	End If
	
	For person.client=Each client
		instruction$=""
		
		If ReadAvail(person\connection)>0
			instruction$=ReadLine(person\connection)
		End If
		
		If Left$(instruction$,9)="register,"
			instruction$=Replace$(instruction$,"register,","")
			register$=Left$(instruction$,20)
			instruction$=Replace$(instruction$,register$,"")
			register$=Trim$(register$)
						
			file=OpenFile("C:\server.txt")
			already=0
			
			While Not Eof(file)
				If register$=ReadLine(file)
					already=1
				End If
				
				junk$=ReadLine(file)
			Wend
			
			CloseFile(file)
			
			If already=0
				file=ReadFile("C:\server.txt")
				
				While Not Eof(file)
					r.registrar=New registrar
					r\content$=ReadLine(file)
				Wend
				
				CloseFile(file)
				DeleteFile("C:\server.txt")
				
				file=WriteFile("C:\server.txt")
				
				For r.registrar=Each registrar
					WriteLine(file,r\content$)
					Delete r
				Next
				
				WriteLine(file,register$)
				WriteLine(file,instruction$)
				WriteLine(person\connection,"registered")
				Print register$+" registered!"
			Else
				WriteLine(person\connection,"taken")
			End If
		
			CloseFile(file)
			
		Else If Left$(instruction$,6)="logon,"
			instruction$=Replace$(instruction$,"logon,","")
			
			username$=Left$(instruction$,20)
			instruction$=Replace$(instruction$,username$,"")
			username$=Trim$(username$)
			
			Print username$+" "+instruction$
			file=OpenFile("C:\server.txt")
			already=0
			
			While Not Eof(file)
				name$=ReadLine(file)
				pass$=ReadLine(file)
				
				If name$=username$ And pass$=instruction$
					already=1
				End If
			Wend
			
			CloseFile(file)
			
			If already=1
				person\name$=username$
				WriteLine(person\connection,"your on")
				
				For user.client=Each client
					WriteLine user\connection,"online,"+username$
				Next
				
				Print person\name$+" logged on."
			Else
				WriteLine(person\connection,"not on")
			End If
	
		Else If Left$(instruction$,6)="check,"
			instruction$=Replace$(instruction$,"check,","")
			
			Print "Check "+instruction$+"."
			
			For user.client=Each client
				If user\name$=instruction$
					WriteLine person\connection,"online,"+instruction$
					Print "He was on!"
				End If
			Next
		
		Else If Left$(instruction$,5)="send,"
			instruction$=Replace$(instruction$,"send,","")
			destination$=Left$(instruction$,20)
			instruction$=Replace$(instruction$,destination$,"")
			destination$=Trim$(destination$)
			
			Print person\name$+" said "+Chr$(34)+instruction$+Chr$(34)+" To "+destination$
			
			For user.client=Each client
				If user\name$=destination$
					temp$=LSet$(person\name$,20)
					WriteLine user\connection,"message,"+temp$+instruction$
				End If
			Next
		
		Else If instruction$="ping"
			ResetTimer(person\timer)
		
		Else If instruction$="logoff"
			temp$=person\name$
			
			person\deleted=1
			
			Print temp$+" signed off."
			
			For user.client=Each client
				WriteLine user\connection,"offline,"+temp$
			Next
		End If
		
		If TimerTicks(person\timer)>30
			person\deleted=1
			Print person\name$+" died!"
			For user.client=Each client
				WriteLine user\connection,"offline,"+person\name$
			Next
		End If
		
		If person\deleted=1
			CloseTCPStream person\connection
			Delete person
		End If
	Next		
Wend



; Arem IM Code

Global Win1
Global ListBox1
Global add
Global del
Global addfield
Global premen
Global timer=CreateTimer(1)

Include "signon.bb"

Type window
	Field real,textarea,textfield,messages$
End Type

Type buddy
	Field name$
End Type

Global connection=OpenTCPStream("arem.us",3334)
Global username$

If Not connection
	Notify("Unable to contact server!",1)
	End
Else
	logonwin()
End If

CreateWin1()
budlistcheckeroozi()

While Not idroppedicedownyourshirt=1
	instruction$=0
	WaitEvent(0)
	
	If EventID()=$803
		If EventSource()=win1
			WriteLine connection,"logoff"
			CloseTCPStream connection
			End
		End If
		
		For w.window=Each window
			If EventSource()=w\real
				FreeGadget w\real
				Delete w
			End If
		Next
	End If
	
	If EventID()=$1001
		If EventSource()=premenu
			ExecFile(SystemProperty("systemdir")+"notepad.exe "+SystemProperty("appdir")+username$+".budlist.txt")
		End If
	End If
	
	If EventID()=$401
		If EventSource()=del
			If SelectedGadgetItem(listbox1)<CountGadgetItems(listbox1) And SelectedGadgetItem(listbox1)>-1
				temp$=GadgetItemText(listbox1,SelectedGadgetItem(listbox1))
				temp$=Replace$(temp$," (offline)","")
				
				RemoveGadgetItem(listbox1,SelectedGadgetItem(listbox1))
				
				file=ReadFile(username$+".budlist.txt")
				
				While Not Eof(file)
					b.buddy=New buddy
					b\name$=ReadLine(file)
				Wend
				
				CloseFile(file)
				
				DeleteFile(username$+".budlist.txt")
				
				file=WriteFile(username$+".budlist.txt")
				
				For b.buddy=Each buddy
					If Not b\name$=temp$
						WriteLine(file,b\name$)
						Delete b
					End If
				Next
			End If
		End If
		
		If EventSource()=add
			If Len(TextFieldText(addfield))>4 Or TextFieldText(addfield)="Arem"
				already=0
				
				For a=0 To CountGadgetItems(listbox1)-1
					If Replace$(GadgetItemText(listbox1,a)," (offline)","")=TextFieldText(addfield)
						already=1
					End If
				Next
				
				If already=0
					temp$=TextFieldText(addfield)
					AddGadgetItem(listbox1,TextFieldText(addfield)+" (offline)")
					SetGadgetText(addfield,"")
					WriteLine(connection,"check,"+temp$)
					
					file=ReadFile(username$+".budlist.txt")
					
					While Not Eof(file)
						b.buddy=New buddy
						b\name$=ReadLine(file)
					Wend
					
					CloseFile(file)
					DeleteFile(username$+".budlist.txt")
					file=WriteFile(username$+".budlist.txt")
					
					For b.buddy=Each buddy
						WriteLine(file,b\name$)
						Delete b
					Next
					
					WriteLine(file,temp$)
					
					CloseFile(file)
				End If
			End If
		End If		
			
		If EventSource()=listbox1
			If Not Right$(GadgetItemText(listbox1,SelectedGadgetItem(listbox1)),10)=" (offline)"
				already=0
				
				For w.window=Each window
					If GadgetText(w\real)=GadgetItemText(listbox1,SelectedGadgetItem(listbox1))
						already=1
					End If
				Next
				
				If already=0
					w.window=New window
					w\real=CreateWindow(GadgetItemText(listbox1,SelectedGadgetItem(listbox1)),Rand(0,600),Rand(0,400),200,215,Desktop(),3)
					w\textarea=CreateTextArea(5,5,185,155,w\real,1)
					w\textfield=CreateTextField(5,165,185,20,w\real)
					SetGadgetLayout(w\textarea,1,2,1,2)
					SetGadgetLayout(w\textfield,1,2,0,1)
					SetMinWindowSize(w\real,200,215)
				End If
			End If
		End If
		
		For w.window=Each window
			If EventSource()=w\textfield
				If EventData()=13
					If TextFieldText(w\textfield)<>""
						WriteLine(connection,"send,"+LSet$(GadgetText(w\real),20)+TextFieldText(w\textfield))
						AddTextAreaText(w\textarea,username$+": "+TextFieldText(w\textfield))
						AddTextAreaText(w\textarea,Chr$(10))
						w\messages$=TextAreaText(w\textarea)
						SetGadgetText(w\textfield,"")
					End If
				End If
			End If
		Next
	End If
	
	If ReadAvail(connection)>0
		instruction$=ReadLine(connection)
		
		If Left$(instruction$,7)="online,"
			instruction$=Replace$(instruction$,"online,","")

			For a=0 To CountGadgetItems(listbox1)-1
				If Left$(GadgetItemText(listbox1,a),Len(instruction$))=instruction$
					If Right$(GadgetItemText(listbox1,a),10)=" (offline)"
						ModifyGadgetItem(listbox1,a,Replace$(GadgetItemText(listbox1,a)," (offline)",""))
					End If
				End If
			Next
			
		Else If Left$(instruction$,8)="offline,"
			instruction$=Replace$(instruction$,"offline,","")
			For a=0 To CountGadgetItems(listbox1)-1
				If GadgetItemText(listbox1,a)=instruction$
					ModifyGadgetItem(listbox1,a,instruction$+" (offline)")
					a=a+1
				End If
			Next
			
			For w.window=Each window
				If GadgetText(w\real)=instruction$
					AddTextAreaText(w\textarea,instruction$+" logged off.")
				End If
			Next
			
		Else If Left$(instruction$,8)="message,"
			instruction$=Replace$(instruction$,"message,","")
			from$=Left$(instruction$,20)
			instruction$=Replace$(instruction$,from$,"")
			from$=Trim$(from$)
			
			already=0
			
			For w.window=Each window	
				If GadgetText(w\real)=from$					
					AddTextAreaText(w\Textarea,from$+": "+instruction$)
					AddTextAreaText(w\textarea,Chr$(10))
					w\messages$=TextAreaText(w\textarea)
					already=1
				End If
			Next
			
			If already=0
				w.window=New window
				w\real=CreateWindow(from$,Rand(0,600),Rand(0,400),200,215,Desktop(),3)
				w\textarea=CreateTextArea(5,5,185,155,w\real,1)
				w\textfield=CreateTextField(5,165,185,20,w\real)
				SetGadgetLayout(w\textarea,1,2,1,2)
				SetGadgetLayout(w\textfield,1,2,0,1)
				SetMinWindowSize(w\real,200,215)
				AddTextAreaText(w\textarea,from$+": "+instruction$)
				AddTextAreaText(w\textarea,Chr$(10))
				w\messages$=TextAreaText(w\textarea)
			End If
		End If
	End If
	
	For w.window=Each window
		If TextAreaText(w\textarea)<>w\messages$
			SetTextAreaText(w\textarea,w\messages$)
		End If
	Next
	
	If TimerTicks(timer)>5
		WriteLine(connection,"ping")
		ResetTimer(timer)
	End If
	
	Delay(1)
Wend

Function CreateWin1()
	Win1=CreateWindow("Arem IM",ClientWidth(Desktop())-175,30,150,700,Desktop(),3)
	SetMinWindowSize(win1,151,400)
	ListBox1=CreateListBox(5,5,130,620,Win1,0)
	SetGadgetLayout ListBox1,2,2,2,2
	addfield=CreateTextField(5,625,130,20,win1)
	add=CreateButton("Add Buddy",5,645,65,20,win1)
	del=CreateButton("Del Buddy",75,645,65,20,win1)
	UpdateWindowMenu Win1
End Function

Function budlistcheckeroozi()
	If FileType(username$+".budlist.txt")=1
		file=OpenFile(username$+".budlist.txt")
	
		While Not Eof(file)
			temp$=ReadLine(file)
			WriteLine(connection,"check,"+temp$)
			AddGadgetItem(listbox1,temp$+" (offline)")
		Wend
		
		CloseFile(file)
	Else
		WriteFile(username$+".budlist.txt")
		CloseFile(file)
	End If
End Function


;Sign On Code.  Needs to be in seperate file called singon.bb

Global Win2
Global Label1
Global Label2
Global TextField1
Global TextField2
Global Button1
Global Button2
CreateWin2()

Function logonwin()
	While Not quitfunction=1
		WaitEvent(0)
		
		If TimerTicks(timer)>5
			WriteLine(connection,"ping")
			ResetTimer(timer)
		End If
		
		If EventSource()=button1
			If Len(TextFieldText(textfield1))>4
				If Len(TextFieldText(textfield1))<20
					If TextFieldText(textfield2)<>""
						DisableGadget(win2)
						WriteLine connection,"register,"+LSet$(TextFieldText(textfield1),20)+TextFieldText(textfield2)
						
						signontimer=CreateTimer(1)
						
						While TimerTicks(signontimer)<15 And ReadAvail(connection)>0
							Delay(5)
						Wend
				
						temp$=ReadLine(connection)

						
						If temp$="registered"
							file=WriteFile(TextFieldText(textfield1)+".budlist.txt")
							CloseFile(file)
							Notify "User Succesfully Created!"
						Else
							Notify "User Name Taken!"
						End If
						
						EnableGadget(win2)
					Else
						Notify "Password is Required!",1
					End If
				Else
					Notify "Username Too Long!",1
				End If
			Else
				Notify "User Name Too Short!",1
			End If
		End If
		
		If EventSource()=button2
			If Len(TextFieldText(textfield1))<20
				DisableGadget(win2)
				WriteLine connection,"logon,"+LSet$(TextFieldText(textfield1),20)+TextFieldText(textfield2)

				signontimer=CreateTimer(1)
						
				While TimerTicks(signontimer)<15 And ReadAvail(connection)>0
					Delay(5)
				Wend
				
				temp$=ReadLine(connection)
										
				If temp$="your on"
					username$=TextFieldText(textfield1)			
					register$=Trim$(register$)
					quitfunction=1
				Else
					Notify "Logon Not Successful!"
				End If
				EnableGadget(win2)
			Else
				Notify "Username Too Long!",1
			End If
		End If
		
		If EventID()=$803
			End
		End If
	
	Wend
	
	FreeGadget(win2)
End Function

Function CreateWin2()
	Win2=CreateWindow("Arem IM Signon",(ClientWidth(Desktop())/2)-125,(ClientHeight(Desktop())/2)-60,250,120,Desktop(),1)
	Label1=CreateLabel("Username:",5,6,50,20,Win2,0)
	Label2=CreateLabel("Password:",5,40,50,20,Win2,0)
	TextField1=CreateTextField(60,5,180,20,Win2,0)
	TextField2=CreateTextField(60,35,180,20,Win2,1)
	Button1=CreateButton("Register",5,70,100,20,Win2,0)
	Button2=CreateButton("Logon",140,70,100,20,Win2,0)
End Function
