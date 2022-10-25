; ID: 1533
; Author: Beaker
; Date: 2005-11-16 10:08:07
; Title: [maxgui] Max Browser
; Description: A simple web browser using htmlview

'MAX BROWSE by Beaker 2005

SuperStrict 

Global window:TGadget
Global html:TGadget


'SETUP WINDOW
window=CreateWindow( "Max Browse",0,0,800,600)
SetMinWindowSize window,200,0


'SETUP MENUS
Local filemenu:Tgadget = CreateMenu ("  &File  ",0,WindowMenu(window))
	Local openmenuitem:Tgadget = CreateMenu ("&Open...",1,filemenu)

CreateMenu ("",999,filemenu)
	Local exitmenuitem:Tgadget = CreateMenu ("E&xit",9,filemenu)

Local bookmenu:Tgadget = CreateMenu ("  &Bookmarks  ",0,WindowMenu(window))
	Local addbookmenuitem:Tgadget = CreateMenu ("&Add bookmark",21,bookmenu)
	Local organisebookmenuitem:Tgadget = CreateMenu ("&Organise bookmarks",22,bookmenu)
	DisableMenu organisebookmenuitem
	Local defaultmenuitem:Tgadget = CreateMenu ("Set as &default homepage",23,bookmenu)
	CreateMenu ("",999,bookmenu)

	
Local bookname$, bookURL$, bookfile:TStream
If FileType("bookmark.txt") <> 1	'Then create a bookmark file with Default bookmarks
	bookfile = WriteFile("bookmark.txt")
	RestoreData bookdata
	ReadData bookname$
	ReadData bookURL$
	While bookname$ <> "END"
		WriteLine bookfile,bookname$
		WriteLine bookfile,bookURL$
		ReadData bookname$
		ReadData bookURL
	Wend
	CloseFile bookfile
EndIf

'read the bookmarks into the bookmark menu
Global bookf:Int=500
bookfile = ReadFile("bookmark.txt")
While Not Eof(bookfile)
	bookname$ = ReadLine(bookfile)
	bookURL$ = ReadLine(bookfile)
	If bookURL<>""
		CreateMenu (bookname,bookf,bookmenu)
		bookf = bookf +1
	EndIf
Wend
CloseFile bookfile
CreateMenu ("",999,bookmenu)

Local helpmenu:Tgadget = CreateMenu("  &Help  ",0,WindowMenu(window))
Local aboutmenu:Tgadget = CreateMenu("&About",1000,helpmenu)

UpdateWindowMenu window


If FileType("default.txt") <> 1	'Then create the default.txt file with the Default homepage
	Local deffile:TStream = WriteFile("default.txt")
		WriteLine deffile,"http://www.blitzbasic.com"
	CloseFile deffile
EndIf

Local deffile:TStream = ReadFile("default.txt")
	Global defURL$ = ReadLine (deffile)
CloseFile deffile




'SETUP BUTTONS
Local panel:Tgadget = CreatePanel (0,0,800,40,window,0)
	SetGadgetLayout panel,1,0,1,0
	Local backbutt:TGadget = CreateButton ("Back",5,5,60,30,panel)
	Local forebutt:TGadget = CreateButton ("Forward",70,5,60,30,panel)
	Local refreshbutt:TGadget = CreateButton ("Refresh",135,5,60,30,panel)
	Local homebutt:TGadget = CreateButton ("Home",200,5,60,30,panel)
	Global URLfield:TGadget = CreateTextField (265,10,350,20,panel)
	Local URLgo:TGadget = CreateButton ("GO!",620,10,60,20,panel,BUTTON_OK)

	

'SETUP HTML VIEW
html:Tgadget=CreateHTMLView( 0,45,ClientWidth(window),ClientHeight(window)-50,window, HTMLVIEW_NONAVIGATE )
SetGadgetLayout html,1,1,1,1


Global current$
GoURL(defURL)

Local localURL$,found:Int,url$

'MAIN LOOP
While WaitEvent()
	Print EventData()
	Select EventID()
		Case EVENT_GADGETDONE	'page loaded
			If EventSource() = html
				SetGadgetText URLfield,HtmlViewCurrentURL(html)			
			EndIf
		Case EVENT_MENUACTION	'MENU EVENTS
			Select EventData()
				Case 1	'Open Local File
					DebugLog "OPEN LOCAL"
					localURL$ = RequestFile("Open local file","Web files - *.htm,html,jpg,gif,png:htm,html,jpg,gif,png;All files:*")
					If FileType (localURL) = 1
						GoURL(localURL)
					EndIf
				Case 9	'Close program
					End
				Case 21	'Add bookmark
					bookfile = OpenFile("bookmark.txt")
					found = False
					While Not Eof(bookfile)
						bookname = ReadLine(bookfile)
						bookURL = ReadLine(bookfile)
						If bookURL = current
							Notify "URL already in bookmarks"
							found = True
							Exit
						EndIf
					Wend
					If found = False
						WriteLine bookfile,current
						WriteLine bookfile,current
						CreateMenu (current,bookf,bookmenu)
						bookf = bookf +1
						UpdateWindowMenu window
					EndIf
					CloseFile bookfile
				Case 23	'Set as Default homepage
					deffile = WriteFile("default.txt")
						WriteLine deffile,current$
					CloseFile deffile
				Case 1000	'About
					Notify "Max Browse by Beaker 2005"+Chr(13)+"(bookmarks are in the bookmark.txt file)"
			End Select
			If EventData() >= 500	'Jump To a specific bookmark
				Local f:Int = 0
				bookfile = ReadFile("bookmark.txt")
				While Not Eof(bookfile)
					bookname$ = ReadLine (bookfile)
					bookURL = ReadLine (bookfile)
					If f = EventData()-500 Then
						GoURL(bookURL)
						Exit
					EndIf
					f = f +1
				Wend
			EndIf	
		Case EVENT_GADGETACTION	'BUTTON EVENTS
			Select EventSource()
				Case html	' Catch clicking on URL links
					goURL String(EventExtra())
				Case backbutt
						HtmlViewBack html
				Case forebutt
						HtmlViewForward html
				Case refreshbutt
						GoURL(HtmlViewCurrentURL(html))
				Case homebutt
					deffile = ReadFile("default.txt")
						GoURL(ReadLine (deffile))
					CloseFile deffile
				Case URLgo
					GoURL(TextFieldText(URLfield))					
			End Select
					
		Case EVENT_WINDOWCLOSE	'WINDOW CLOSED EVENT
			Select EventSource()
				Case window
					End
			End Select
				
	End Select
	

Wend
End



Function GoURL(URL$)
	current = URL$
	SetGadgetText URLfield,current
	DebugLog current
	HtmlViewGo html,current
	SetGadgetText window,"Max Browse - "+current
End Function




#bookdata
DefData "Blitz Basic"
	DefData "http://www.blitzbasic.com"
DefData "gile[s] lightmapper"
	DefData "http://www.frecle.net/giles/"
DefData "GUIde GUI editor"
	DefData "http://members.home.nl/wdw/guide/"
DefData "Game Making Tools forum"
	DefData "http://playerfactory.proboards25.com"
DefData "END"
	DefData "END"
