; ID: 1734
; Author: skidracer
; Date: 2006-06-15 17:53:29
; Title: MaxIDE
; Description: BlitzMax program editor

' maxide.bmx - blitzmax native integrated development environment

' released under the Blitz Shared Source Code License
' (c)2005 Blitz Research

' initial release version by simonarmstrong@blitzbasic.com

' v120d
' added external help option for linux users

' v120c
' add doc modules option to program menu and fixed syncmods so no restart required
' fixed wrong window activation ugliness in TRequester.Hide
' fixed TDebugTree.SetStack synchronization bug

' v120b
' added autobackup code to SaveSource method 
' fixed autoindent bug including tabs to the right of the cursor

' v120
' Ripped out isupdate stuff
' About requester now shows runtime BCC version

' v118
' Added file filter to SaveAs requester
' Edit method of TOpenCode now activates cursor and refreshes status
' TQuickHelp tokenizer now uses faster TToken map 
' ReadSource and WriteSource now use unicode LoadText and SaveText commands

' v112b
' added .doc file type to filters list
' added trycatch for bmxpath not found
' prepends bmx filenames loaded from help with "." to protect original source
' fixed unblanced quotes in wordatpos() stuffing up quick help 
' fixed exception when debugging function pointer values

' v112

' isupgrade - version check on ini file for ranlibdir and quick build disable
' debug step tools now respect cancontinue flag
' tidied up debug stdio buffers and flushing of process pipes
' linux defaults for closedocument-CtrlW nextdoc-AltRight prevdoc-AltLeft 
' end of lines converted to chr(13)+chr(10) for all saved source files 
' ReplaceAll uses locktextarea for speedup
' added print option for helppage
' filtering tab key in opencode for original block in/outdent
' removed vertical tabs from source
' added .bbdoc filetype to filters$
' fixed null href help tree entries (index node)
' new textareafilter code for the Ouput console emulator
' refreshes pointer in splitter on EVENT_MOUSEMOVE

' v111
' fixed open locked file not found at startup crash
' buildandrun continues from debugstop else kills task and rebuilds
' fixed bug on freshly loaded source being dirty
' fixed bug with options cancel
' MacOS specific hotkey bindings updated
' added .m to file FILTERS$
' adjusted GOTO and IDEOptions requesters

' v110
' DebugSource now calls ShowGadget on IDE window to bring debugger to front

' v109
' fixed WordAtPos to include underscore

' v108
' windows release version

' v107
' activate current panel when window receives focus
' fixed multiple lock build file bug
' fixed command highlighting for functions with # and ! return types
' sourcefile already open check now case insensitive
' block indent outdent now ctrl-[ and ctrl-]
' commandline args fixed

' v106b
' new defaults
' fixed replace all garbage
' removed extra endrem highlight

' v106
' added demo version support
' fixed delete / backspace undo redo issues
' added syntaxhighlight disable option
' fixed block indent / outdent highlighting issue
' added flushmem to replaceall loop
' set window title to full path of current document
' fixed double highlight for endrem

' v105c
' code view now includes labels etc. on first line
' textarea activated when panel open
' autohide output on exit now always returns to last active panel
' optimized code change algorithm
' like compiler, rem and endrem must be first code on line
' fixed up linux line spacing, font selection and code tree / create panel crashes
' new linux help system, style setting and popup menu support
 
' v105b
' added tabbing support in dialogs
' fixed pollsystem event debacle
' fixed lineselection out of view
' fixed highlighting for pasting code blocks
' fixed treeview memory leak and speedissues
' uses -x switch from bmk for build and run option
' debugging now opens source when stepping in

' v105
' activating window selects current panel
' fixed closing output window causing crash
' frees gadgets
' dirty flags now clears only if source=file and undobuffer=empty
' tidied up console window madness
' fixed gcc error check hang
' fixed label detection in code view
' fixed debugger crash with local block variable decls
' fixed codenode refresh with saveas

' v104
' fixed debug tree update and debugreset
' fixed incorrect navbar size for maximized window 
' added \ option
' notify gcc compiler errors in output

' v103
' navbar options left/right 
' added copy paste menu for output window
' added font color options for output and navbar windows
' fixed win32 paste from clipboard to use plain ascii
' stdio debugger handler
' save currentpanel, cursorpositions
' replace default "~\" requestfile path with a currentpath
' fixed pipe handling problem in quickhelp

' v102
' specify openfile from commandline 
' hides main window till all files openned
' checks window bounds against desktop size
' added deferred capitalizing cludge
' fixed rem/endrem updating problem
' fixed highlight / redraw noise
' drag&drop file support added
' dropdown menu in textarea added
' run with commandline arguments
' block indent/outdent
' fixed drag&drop text crash
' added escape handler to output window, removed autohide

Strict

Framework brl.maxgui

Import brl.win32maxgui
Import brl.cocoamaxgui
Import brl.fltkmaxgui

Import brl.standardio
Import brl.retro
Import brl.filesystem
Import brl.system
Import pub.freeprocess
Import brl.pngloader
Import brl.timer
Import brl.maxutil

?win32
Import "maxicons.o"
?

Incbin "icons.PNG"

Incbin "syncmodsblurb.txt"

Const IDE_VERSION$="1.20"
Global BCC_VERSION$="{unknown}"	'not valid until codeplay opened

Const EOL$="~n"
Const SPLITWIDTH=4

Const ABOUT$=..
"{bcc_version} - Copyright Blitz Research Ltd~n~n"+..
"Please visit www.blitzbasic.com for all your Blitz related needs!"

Const ABOUTDEMO$=..
"This demo features both the core BlitzMax package and optional MaxGUI module.~n~n"+..
"Please note that the MaxGUI module must be purchased separately."

Const FILTERS$="Code Files:bmx,bbdoc,txt,vp,doc,plist,bb,cpp,c,m,cxx,s,fs,vs,p,fp,glsl,h,hpp,html,htm,css,js,bat;All Files:*"

Const NodeFileTypes$="|bmx|bbdoc|cpp|cxx|c|m|h|s|fs|vs|p|fp|glsl|txt|html|js|css|bb|java|bat|ini|jpg|png|bmp|tga|wav|pl|doc|plist|"

Global MODOPTIONS$[]=["BRL","PUB","AXE"]		',"MAK","GMAN","BIRDIE"]
Global MODINFO$[]=["BlitzMax Core Modules","Public Domain Core Modules","User Contributed Modules"]	'"Mark Sibly's Module Collection","Simon Armstrong's Module Collection","Garrit Grandberg's Module Collection","Dave Bird's Module Collection"]

?MacOS
Const DEFAULTFONT$="Courier"
Const DEFAULTFONTSIZE=12
Const HOMEPAGE$="/doc/index.html"
?
?Win32
Const DEFAULTFONT$="Courier"
Const DEFAULTFONTSIZE=10
Const HOMEPAGE$="/doc/index.html"
?
?Linux
Const DEFAULTFONT$="Lucida"
Const DEFAULTFONTSIZE=12
Const HOMEPAGE$="/doc/index.html"
'Const HOMEPAGE$="/doc/home.html"
?

Const MENUNEW=1
Const MENUOPEN=2
Const MENUCLOSE=3
Const MENUSAVE=4
Const MENUSAVEAS=5
Const MENUSAVEALL=6
Const MENUPRINT=7
Const MENUQUIT=8

Const MENUUNDO=9
Const MENUREDO=10
Const MENUCUT=11
Const MENUCOPY=12
Const MENUPASTE=13
Const MENUSELECTALL=14
Const MENUGOTO=15
Const MENUINDENT=16
Const MENUOUTDENT=17
Const MENUFIND=18
Const MENUFINDNEXT=19
Const MENUREPLACE=20
Const MENUNEXT=21
Const MENUPREV=22
Const MENUBUILD=23
Const MENURUN=24

Const MENUSTEP=25
Const MENUSTEPIN=26
Const MENUSTEPOUT=27
Const MENUSTOP=28

Const MENULOCKBUILD=29
Const MENUUNLOCKBUILD=30
Const MENUBUILDMODULES=31
Const MENUBUILDALLMODULES=32

Const MENUQUICKENABLED=33
Const MENUDEBUGENABLED=34
Const MENUGUIENABLED=35

Const MENUCOMMANDLINE=36
Const MENUSYNCMODS=37
Const MENUIMPORTBB=38
Const MENUFINDINFILES=39
Const MENUPROJECTMANAGER=40
Const MENUSHOWCONSOLE=41
Const MENUOPTIONS=42

Const MENUHOME=43
Const MENUBACK=44
Const MENUFORWARD=45
Const MENUQUICKHELP=46
Const MENUABOUT=47

Const MENUNEWVIEW=48
Const MENUDOCMODS=49

Const MENUTRIGGERDOCMODS=50
Const MENUTRIGGERSYNCDOCS=51

Const MENUCLOSEALL=53

Const MENURECENT=256

Const TAB$=Chr(9)
Const QUOTES$=Chr(34)

Global TEMPCOUNT
Global is_demo
Global codeplay:TCodePlay

Function CheckDemo()
	If Not is_demo Return 1
	Notify "This feature is unavailable in the demo version of BlitzMax"
	Return 0
End Function

codeplay=New TCodePlay
codeplay.Initialize

While codeplay.running
	codeplay.poll
Wend

End

Function Quote$(a$)		'add quotes to arg if spaces found
	Local	p
	If a.length=0 Return
	If a[0]=34 Return a	'already quoted
	p=a.find(" ")
	If p=-1 Return a	'no spaces
	Return Chr(34)+a+Chr(34)		
End Function

Type TToken
	Field token$
	Field help$
	Field ref$	
	Method Create:TToken(t$,h$,r$)
		token=t
		help=h
		ref=r
		Return Self
	End Method
End Type

Type TQuickHelp
	Field map:TMap=New TMap	'key=lower(token) value=token:TToken
		
	Method AddCommand:TQuickHelp(t$,l$,a$)
		map.Insert Lower(t$),New TToken.Create(t$,l$,a$)
	End Method
	
	Method Token$(cmd$)
		Local t:TToken
		t=TToken(map.ValueForKey(cmd.toLower()))
		If t Return t.token
	End Method
	
	Method Help$(cmd$)
		Local t:TToken
		t=TToken(map.ValueForKey(cmd.toLower()))
		If t Return t.help
	End Method
	
	Method Link$(cmd$)
		Local t:TToken
		t=TToken(map.ValueForKey(cmd.toLower()))
		If t Return t.ref
	End Method
	
	Function LoadCommandsTxt:TQuickHelp(bmxpath$)
		Local	stream:TStream
		Local	qh:TQuickHelp
		Local	l$,c$,p,q
		Local	token$,help$,anchor$
		
		stream=ReadFile(bmxpath+"/doc/bmxmods/commands.txt")
		If stream=Null Return Null
		qh=New TQuickHelp
		While Not Eof(stream)
			l$=ReadLine(stream)
			Local i
			For i=0 Until l.length
				Local c=l[i]
				If c=Asc("_") Continue
				If c>=Asc("0") And c<=Asc("9") Continue
				If c>=Asc("a") And c<=Asc("z") Continue
				If c>=Asc("A") And c<=Asc("Z") Continue
				Exit
			Next
			token$=l[..i]
			help$=""
			anchor$=""
			q=l.find("|")
			If q
				help=l[..q]
				anchor=l[q+1..]
			EndIf			
			qh.AddCommand token,help,anchor
		Wend
		stream.Close
		Return qh
	End Function
End Type

Const TOOLSHOW=1
Const TOOLREFRESH=2
Const TOOLNEW=3
Const TOOLOPEN=4
Const TOOLCLOSE=5
Const TOOLSAVE=6
Const TOOLHELP=7
Const TOOLUNDO=8
Const TOOLREDO=9
Const TOOLCUT=10
Const TOOLCOPY=11
Const TOOLPASTE=12
Const TOOLQUICKSAVE=13
Const TOOLSAVEAS=14
Const TOOLGOTO=15
Const TOOLFIND=16
Const TOOLFINDNEXT=17
Const TOOLREPLACE=18
Const TOOLBUILD=19
Const TOOLRUN=20
Const TOOLLOCK=21
Const TOOLUNLOCK=22
Const TOOLSELECT=23
Const TOOLSELECTALL=24
Const TOOLINDENT=25
Const TOOLOUTDENT=26
Const TOOLACTIVATE=27
Const TOOLNAVIGATE=28
Const TOOLNEWVIEW=29
Const TOOLMENU=30
Const TOOLPRINT=31

Type TTool
	Method Invoke(command,argument:Object=Null)
	End Method
End Type

Type TRequester
	Field	host:TCodePlay
	Field	window:TGadget,ok:TGadget,cancel:TGadget
	Field	centered

	Method initrequester(owner:TCodeplay,label$,w=280,h=128,flags=3)
		host=owner
		window=CreateWindow(label,0,0,w,h,Null,1+64)
		If flags&1 cancel=CreateButton("Cancel",8,ClientHeight(window)-34,80,24,window,BUTTON_CANCEL)
		If flags&2 ok=CreateButton("OK",ClientWidth(window)-88,ClientHeight(window)-34,80,24,window,BUTTON_OK)
	End Method
	
	Method Show()
		Local	x,y,w,h,win:TGadget
		DisableGadget host.window
		If Not centered
			win=host.window		
			w=GadgetWidth(window)
			h=GadgetHeight(window)
			x=GadgetX(win)+(GadgetWidth(win)-w)/2
			y=GadgetY(win)+(GadgetHeight(win)-h)/2
			SetGadgetShape window,x,y,w,h
			centered=True
		EndIf
		ShowGadget window
		ActivateGadget window
		host.SetRequester Self
		PollSystem
	End Method
	
	Method Hide()
		EnableGadget host.window
		HideGadget window
		host.SetRequester Null
'		host.SelectPanel host.currentpanel
	End Method

	Method Poll()
	End Method
End Type

Type TProgressRequester Extends TRequester
	Field	message$,value
	Field	showing
	Field	label:TGadget
	Field	progbar:TGadget
	
	Method Show()	'returns false if cancelled
		showing=True
		Super.Show
	End Method
	
	Method Hide()
		showing=False
		Super.Hide()
	End Method
	
	Method Open(title$)
		SetGadgetText window,title
		Show
	End Method
	
	Method Update(msg$,val)
		If msg$<>message
			message=msg
			If label FreeGadget label
			label=CreateLabel(message,8,8,260,20,window)
		EndIf
		If showing And (val&$fc)<>(value&$fc)	'only update every 4 percent
			UpdateProgBar( progbar,val/100.0 )
			PollSystem
		EndIf
		value=val
	End Method
	
	Function Create:TProgressRequester(host:TCodePlay)
		Local	progress:TProgressRequester
		progress=New TProgressRequester
		progress.initrequester(host,"ProgressRequester",280,128,1)	'1 for cancel button
		progress.progbar=CreateProgBar( 8,32,260,20,progress.window )
		Return progress
	End Function
End Type

Type TPanelRequester Extends TRequester
	Field	tabber:TGadget
	Field	panels:TGadget[]
	Field	index
	
	Method InitPanelRequester(owner:TCodeplay,label$,w=280,h=128)		
		InitRequester owner,label,w,h
		w=ClientWidth(window)
		h=ClientHeight(window)-40
		tabber=CreateTabber(4,4,w-8,h-8,window)
		SetGadgetLayout tabber,1,1,1,1
	End Method

	Method SetPanelIndex(i)
		HideGadget panels[index]
		index=i
		ShowGadget panels[index]
		SelectGadgetItem tabber,index	
	End Method
	
	Method SetPanel(panel:TGadget)
		Local	p:TGadget,i
		For p=EachIn panels
			If p=panel SetPanelIndex i;Exit
			i:+1
		Next
	End Method
	
	Method AddPanel:TGadget(name$)
		Local	panel:TGadget
		panel=CreatePanel(0,0,ClientWidth(tabber),ClientHeight(tabber),tabber)
		SetGadgetLayout panel,1,1,1,1
		HideGadget panel
		AddGadgetItem tabber,name
		panels=panels[..panels.length+1]
		panels[panels.length-1]=panel
		Return panel
	End Method
	
	Method RemovePanel(panel)
	End Method
	
End Type

Type TCmdLineRequester Extends TRequester
	Field	label:TGadget,textfield:TGadget

	Method Poll()
		Local	id,data
		id=EventID()
		data=EventData()
		Select EventSource()
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Hide
				EndIf
			Case ok
				host.SetCommandLine TextFieldText(textfield)
				Hide				
			Case cancel
				SetGadgetText textfield,host.GetCommandLine()
				Hide
		End Select
	End Method
	
	Method Show()
		SetGadgetText textfield,host.GetCommandLine()
		Super.Show()
		ActivateGadget textfield
	End Method

	Function Create:TCmdLineRequester(host:TCodePlay)
		Local	cmd:TCmdLineRequester
		cmd=New TCmdLineRequester
		cmd.initrequester(host,"Program Command Line")
		cmd.label=CreateLabel("Program command line:",8,8,260,20,cmd.window)
		cmd.textfield=CreateTextField(8,32,260,20,cmd.window)
		Return cmd
	End Function
End Type

Type TGotoRequester Extends TRequester
	Field	linenumber:TGadget

	Method Show()
		Super.Show()
		ActivateGadget linenumber
	End Method

	Method Poll()
		Local	line,data
		Select EventSource()
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Hide
				EndIf
			Case ok
				line=Int(TextFieldText(linenumber))
				Hide
				host.activepanel.Invoke TOOLGOTO,String(line)
			Case cancel
				Hide
		End Select
	End Method

	Function Create:TGotoRequester(host:TCodePlay)
		Local	seek:TGotoRequester
		seek=New TGotoRequester
		seek.initrequester(host,"Goto Line",260,128)
		CreateLabel("Line Number:",20,24,114,20,seek.window)
		seek.linenumber=CreateTextField(134,20,100,20,seek.window)
		Return seek
	End Function
End Type

Type TColor
	Field	red,green,blue

	Method Set(rgb)
		red=(rgb Shr 16)&255
		green=(rgb Shr 8)&255
		blue=rgb&255
	End Method

	Method ToString$()
		Return ""+red+","+green+","+blue
	End Method

	Method FromString(s$)
		Local	p,q
		p=s.Find(",")+1
		If Not p Return
		q=s.Find(",",p)+1
		If Not q Return
		red=Int(s[..p-1])
		green=Int(s[p..q-1])
		blue=Int(s[q..])
	End Method

	Method Request()
		If RequestColor(red,green,blue)
			red=RequestedRed()
			green=RequestedGreen()
			blue=RequestedBlue()
			Return True
		EndIf				
	End Method	
End Type

Type TTextStyle
	Field	label:TGadget,panel:TGadget,combo:TGadget
	Field	color:TColor
	Field	flags
	
	Method Set(rgb,bolditalic)
		color.set(rgb)
		flags=bolditalic
	End Method

	Method Format(textarea:TGadget,pos,length)
		FormatTextAreaText textarea,color.red,color.green,color.blue,flags,pos,length
	End Method

	Method ToString$()
		Return ""+color.red+","+color.green+","+color.blue+","+flags
	End Method

	Method FromString(s$)
		Local	p,q,r
		p=s.Find(",")+1;If Not p Return
		q=s.Find(",",p)+1;If Not q Return
		r=s.Find(",",q)+1;If Not r Return
		color.red=Int(s[..p-1])
		color.green=Int(s[p..q-1])
		color.blue=Int(s[q..r-1])
		flags=Int(s[r..])
	End Method

	Method Poll()
		Select EventSource()
			Case panel
				If EventID()=EVENT_MOUSEDOWN
					Return color.Request()
				EndIf
			Case combo
				flags=SelectedGadgetItem(combo)
				Return True
		End Select
	End Method
	
	Method Refresh()
		SetPanelColor panel,color.red,color.green,color.blue
		SelectGadgetItem combo,flags		
	End Method

	Function Create:TTextStyle(name$,xpos,ypos,window:TGadget)
		Local	s:TTextStyle
		s=New TTextStyle
		s.color=New TColor
		s.label=CreateLabel(name,xpos,ypos+4,80,24,window)
		s.panel=CreatePanel(xpos+84,ypos,24,24,window,PANEL_BORDER|PANEL_ACTIVE)
		SetPanelColor s.panel,255,255,0
		s.combo=CreateComboBox(xpos+112,ypos,96,24,window)
		AddGadgetItem s.combo,"Normal"
		AddGadgetItem s.combo,"Bold"
		AddGadgetItem s.combo,"Italic"
		AddGadgetItem s.combo,"Bold+Italic"
		Return s
	End Function
End Type

Type TGadgetStyle
	Field	label:TGadget,fpanel:TGadget,bpanel:TGadget,fbutton:TGadget
	Field	font_name$,font_size
	Field	fg:TColor
	Field	bg:TColor
	Field	font:TGUIFont

	Method Apply(gadget:TGadget)
		SetGadgetFont gadget,font
		SetGadgetColor gadget,bg.red,bg.green,bg.blue,True
		SetGadgetColor gadget,fg.red,fg.green,fg.blue,False
	End Method
	
	Method Set(fg_rgb,bg_rgb,fname$,fsize)
		fg.set(fg_rgb)
		bg.set(bg_rgb)
		font_name=fname
		font_size=fsize
	End Method

	Method ToString$()
		Return font_name+","+font_size+","+fg.ToString()+","+bg.ToString()
	End Method
	
	Function GetArg$(a$ Var)
		Local 	p,r$
		p=a.Find(",")+1
		If p=0 p=Len a$+1
		r$=a$[..p-1]
		a$=a$[p..]
		Return r$	
	End Function
	
	Method FromString(s$)		
		font_name=GetArg(s$)
		font_size=Int(GetArg(s$))
		fg.red=Int(GetArg(s$))
		fg.green=Int(GetArg(s$))
		fg.blue=Int(GetArg(s$))
		bg.red=Int(GetArg(s$))
		bg.green=Int(GetArg(s$))
		bg.blue=Int(GetArg(s$))
	End Method

	Method Poll()
		Local	f:TGUIFont
		Select EventSource()
			Case fpanel
				If EventID()=EVENT_MOUSEDOWN
					Return fg.Request()
				EndIf
			Case bpanel
				If EventID()=EVENT_MOUSEDOWN
					Return bg.Request()
				EndIf
			Case fbutton
				f=RequestFont(font)
				If f
					font_name=FontName(f)
					font_size=FontSize(f)
					Return True
				EndIf				
		End Select
	End Method
	
	Method Refresh()
		font=LoadGuiFont(font_name,font_size)
		SetPanelColor fpanel,fg.red,fg.green,fg.blue
		SetPanelColor bpanel,bg.red,bg.green,bg.blue
		SetGadgetText fbutton,font_name+":"+font_size
	End Method

	Function Create:TGadgetStyle(name$,xpos,ypos,window:TGadget)
		Local	s:TGadgetStyle
		s=New TGadgetStyle
		s.fg=New TColor
		s.bg=New TColor
		s.label=CreateLabel(name,xpos,ypos+4,56,24,window)
		s.fpanel=CreatePanel(xpos+58,ypos,24,24,window,PANEL_BORDER|PANEL_ACTIVE)
		s.bpanel=CreatePanel(xpos+86,ypos,24,24,window,PANEL_BORDER|PANEL_ACTIVE)
		s.fbutton=CreateButton("..",xpos+112,ypos,180,24,window)
		Return s
	End Function
End Type

Const NORMAL=0
Const COMMENT=1
Const QUOTED=2
Const KEYWORD=3

Type TOptionsRequester Extends TPanelRequester
' panels
	Field	optionspanel:TGadget,editorpanel:TGadget,toolpanel:TGadget
' settings
	Field	showtoolbar,restoreopenfiles,autocapitalize,syntaxhighlight,autobackup,autoindent,hideoutput,externalhelp
	Field	tabsize
	Field	editfontname$,editfontsize,editcolor:TColor
	Field	outputfontname$,outputfontsize,outputcolor:TColor
' states
	Field	editfont:TGUIFont
' gadgets
	Field	tabbutton:TGadget
	Field	editpanel:TGadget,editbutton:TGadget
	Field	buttons:TGadget[9]
	Field	styles:TTextStyle[4]
	Field	textarea:TGadget
	Field	outputstyle:TGadgetStyle
	Field	navstyle:TGadgetStyle
	Field	navswapbutton:TGadget,navswap
	Field	dirty
	Field	undo:TBank
	
	Method Snapshot()
		If Not undo undo=CreateBank(8192)
		Local stream:TStream=CreateBankStream(undo)
		write stream
		stream.close
	End Method
	
	Method Restore()
		If Not undo Return
		Local stream:TStream=CreateBankStream(undo)
		Read stream
		stream.close
	End Method

	Method SetDefaults()
		showtoolbar=True
		restoreopenfiles=True
		autocapitalize=True
		syntaxhighlight=True
		autobackup=True
		autoindent=True
		tabsize=4
		editfontname$=DEFAULTFONT
		editfontsize=DEFAULTFONTSIZE
		editcolor.set($01516b)
		styles[NORMAL].set($ffffff,0)
		styles[COMMENT].set($b1e7eb,0)
		styles[QUOTED].set($ff66,0)
		styles[KEYWORD].set($ffff00,0)
		outputstyle.set(0,-1,DEFAULTFONT,DEFAULTFONTSIZE)
		navstyle.set(0,-1,DEFAULTFONT,DEFAULTFONTSIZE)
		navswap=0
		RefreshGadgets
	End Method

	Method Write(stream:TStream)
		Local i
		stream.WriteLine "[Options]"
		stream.WriteLine "showtoolbar="+showtoolbar
		stream.WriteLine "restoreopenfiles="+restoreopenfiles
		stream.WriteLine "autocapitalize="+autocapitalize
		stream.WriteLine "syntaxhighlight="+syntaxhighlight
		stream.WriteLine "autobackup="+autobackup		
		stream.WriteLine "autoindent="+autoindent		
		stream.WriteLine "tabsize="+tabsize
		stream.WriteLine "editfontname="+editfontname		
		stream.WriteLine "editfontsize="+editfontsize		
		stream.WriteLine "editcolor="+editcolor.ToString()		
		stream.WriteLine "normal_style="+styles[0].ToString()		
		stream.WriteLine "comment_style="+styles[1].ToString()		
		stream.WriteLine "quote_style="+styles[2].ToString()		
		stream.WriteLine "keyword_style="+styles[3].ToString()		
		stream.WriteLine "output_style="+outputstyle.ToString()
		stream.WriteLine "nav_style="+navstyle.ToString()
		stream.WriteLine "nav_swap="+navswap
		stream.WriteLine "hide_output="+hideoutput
		stream.WriteLine "external_help="+externalhelp
	End Method

	Method Read(stream:TStream)
		Local	f$,p,a$,b$,t
		While Not stream.Eof()
			f$=stream.ReadLine()
			If f$="" Or (f$[..1]="[" And f$<>"[Options]") Exit
			p=f.find("=")
			a$=f[..p]
			b$=f[p+1..]
			t=Int(b)
			Select a$
				Case "showtoolbar" showtoolbar=t
				Case "restoreopenfiles" restoreopenfiles=t
				Case "autocapitalize" autocapitalize=t
				Case "syntaxhighlight" syntaxhighlight=t
				Case "autobackup" autobackup=t
				Case "autoindent" autoindent=t
				Case "tabsize" tabsize=t		
				Case "editfontname" editfontname=b
				Case "editfontsize" editfontsize=t
				Case "editcolor" editcolor.FromString(b)		
				Case "normal_style" styles[0].FromString(b)
				Case "comment_style" styles[1].FromString(b)
				Case "quote_style" styles[2].FromString(b)
				Case "keyword_style" styles[3].FromString(b)
				Case "output_style" outputstyle.FromString(b)
				Case "nav_style" navstyle.FromString(b)
				Case "nav_swap" navswap=t
				Case "hide_output" hideoutput=t
				Case "external_help" externalhelp=t
			End Select
		Wend		
		RefreshGadgets
	End Method
	
	Method RefreshGadgets()
		Local	i,rgb:TColor
		editfont=LoadGuiFont(editfontname,editfontsize)
		SetButtonState buttons[0],showtoolbar
		SetButtonState buttons[1],restoreopenfiles
		SetButtonState buttons[2],autocapitalize
		SetButtonState buttons[3],syntaxhighlight
		SetButtonState buttons[4],autobackup
		SetButtonState buttons[5],autoindent
		SetButtonState buttons[6],hideoutput
		SetButtonState buttons[7],externalhelp
		SelectGadgetItem tabbutton,Min(Max(tabsize/2-1,0),7)
		SetPanelColor editpanel,editcolor.red,editcolor.green,editcolor.blue
		SetGadgetText editbutton,editfontname+":"+editfontsize
		For i=0 To 3
			styles[i].Refresh
		Next
		LockTextArea textarea
		SetTextAreaColor textarea,editcolor.red,editcolor.green,editcolor.blue,True
		SetGadgetFont textarea,editfont
		styles[0].format(textarea,0,-1)
		styles[1].format(textarea,0,17)
		styles[2].format(textarea,26,8)
		styles[3].format(textarea,36,5)
		UnlockTextArea textarea
		outputstyle.Refresh
		navstyle.Refresh
		SelectGadgetItem navswapbutton,Min(Max(navswap,0),1)
		dirty=True
	End Method

	Method Poll()
		Local	i,font:TGUIFont,refresh
		For i=0 To 3
			refresh=refresh|styles[i].Poll()
		Next
		refresh=refresh|outputstyle.Poll()
		refresh=refresh|navstyle.Poll()
		If EventSource()=buttons[0] showtoolbar=ButtonState(buttons[0]);dirty=True
		If EventSource()=buttons[1] restoreopenfiles=ButtonState(buttons[1])
		If EventSource()=buttons[2] autocapitalize=ButtonState(buttons[2]);dirty=True
		If EventSource()=buttons[3] syntaxhighlight=ButtonState(buttons[3]);dirty=True
		If EventSource()=buttons[4] autobackup=ButtonState(buttons[4])
		If EventSource()=buttons[5] autoindent=ButtonState(buttons[5])
		If EventSource()=buttons[6] hideoutput=ButtonState(buttons[6])
		If EventSource()=buttons[7] externalhelp=ButtonState(buttons[7])
		Select EventSource()
			Case tabber
				SetPanelIndex SelectedGadgetItem(tabber)
			Case ok
				Hide
				If dirty host.RefreshAll
				dirty=False
				SnapShot
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Restore
					dirty=False
					Hide
				EndIf
			Case cancel
				Restore
				dirty=False
				Hide
			Case tabbutton
				tabsize=(SelectedGadgetItem(tabbutton)+1)*2
				refresh=True
			Case editpanel
				If EventID()=EVENT_MOUSEDOWN
					refresh=editcolor.Request()
				EndIf
			Case editbutton
				font=RequestFont(editfont)
				If font
					editfontname=FontName(font)
					editfontsize=FontSize(font)
					refresh=True
				EndIf
			Case navswapbutton
				navswap=SelectedGadgetItem(navswapbutton)
				refresh=True
		End Select
		If refresh RefreshGadgets
	End Method
	
	Method InitOptionsRequester(host:TCodePlay)		
		Local	w:TGadget
'		o=New TOptionsRequester
		InitPanelRequester(host,"Options",320,440)
' init values
		editcolor=New TColor
' init gadgets
		optionspanel=AddPanel("Options")
		editorpanel=AddPanel("Editor")
		toolpanel=AddPanel("Tools")

		w=optionspanel
		buttons[0]=CreateButton("Show ToolBar",10,10,280,24,w,2)
		buttons[1]=CreateButton("Open Files At Startup",10,34,280,24,w,2)
		buttons[2]=CreateButton("Auto Capitalize",10,58,280,24,w,2)
		buttons[3]=CreateButton("Syntax Highlighting",10,82,280,24,w,2)
		buttons[4]=CreateButton("Auto Backup",10,106,280,24,w,2)
		buttons[5]=CreateButton("Auto Indent",10,130,280,24,w,2)
		buttons[6]=CreateButton("Hide Output When Process Complete",10,154,280,24,w,2)
		buttons[7]=CreateButton("Use External Help Browser",10,178,280,24,w,2)

		w=editorpanel
		CreateLabel("Background",4,12+4,80,24,w)
		editpanel=CreatePanel(88,12,24,24,w,PANEL_BORDER|PANEL_ACTIVE)
		editbutton=CreateButton("..",116,12,180,24,w)

		styles=New TTextStyle[4]
		styles[0]=TTextStyle.Create("PlainText:",4,40,w)
		styles[1]=TTextStyle.Create("Remarks:",4,68,w)
		styles[2]=TTextStyle.Create("Strings:",4,96,w)
		styles[3]=TTextStyle.Create("KeyWords:",4,124,w)		
		
		tabbutton=CreateComboBox(20,180,128,24,w)
		For Local i=1 To 8
			AddGadgetItem tabbutton,"TabSize="+(i*2)
		Next
		
		textarea=CreateTextArea(10,220,ClientWidth(w)-20,140,w,TEXTAREA_READONLY)
		SetTextAreaText textarea,"' Example Listing~n~nname$=~qBlitzMax~q~nPrint name$~n"

		w=toolpanel
		outputstyle=TGadgetStyle.Create("Output",4,12,w)
		navstyle=TGadgetStyle.Create("Navbar",4,40,w)

		navswapbutton=CreateComboBox(20,66,128,24,w)
		AddGadgetItem navswapbutton,"Navbar on right"
		AddGadgetItem navswapbutton,"Navbar on left"

		SetDefaults()
		SetPanel optionspanel
		
		SnapShot
	End Method
	
	Function Create:TOptionsRequester(host:TCodePlay)
		Local	o:TOptionsRequester
		o=New TOptionsRequester
		o.InitOptionsRequester host
		Return o
	End Function
	
End Type

Type TFindRequester Extends TRequester
	Field	findterm:TGadget
	
	Method ShowFind(term$="")
		If term SetGadgetText(findterm,term)
		Super.Show()
		ActivateGadget findterm
	End Method

	Method Poll()
		Local	find$,data		
		Select EventSource()
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Hide
				EndIf				
			Case ok
				find=TextFieldText(findterm)
				Hide
				PollSystem
				host.activepanel.Invoke TOOLFINDNEXT,find
			Case cancel
				Hide
		End Select
	End Method

	Function Create:TFindRequester(host:TCodePlay)
		Local	seek:TFindRequester
		seek=New TFindRequester
		seek.initrequester(host,"Find",280,128)
		CreateLabel("Find What",8,16,80,24,seek.window)
		seek.findterm=CreateTextField(88,12,180,20,seek.window)
		Return seek
	End Function
End Type

Type TReplaceRequester Extends TRequester
	Field	findterm:TGadget,replaceterm:TGadget
	Field	findnext:TGadget,replaceit:TGadget,replaceall:TGadget
	
	Method Show()
		Super.Show()
		ActivateGadget findterm
	End Method

	Method Poll()
		Local	find$,Replace$
		Select EventSource()
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Hide
				EndIf
			Case ok
				Hide
			Case cancel
				Hide
			Case findnext
				find=TextFieldText(findterm)
				host.activepanel.Invoke TOOLFINDNEXT,find
			Case replaceit
				Replace=TextFieldText(replaceterm)
				If host.activepanel.Invoke(TOOLREPLACE,Replace)
					host.activepanel.Invoke TOOLFINDNEXT,find
				EndIf				
			Case replaceall
				find=TextFieldText(findterm)
				Replace=TextFieldText(replaceterm)
				host.activepanel.Invoke TOOLREPLACE,find+Chr(0)+Replace
		End Select
	End Method

	Function Create:TReplaceRequester(host:TCodePlay)
		Local x,y
		Local	seek:TReplaceRequester
		seek=New TReplaceRequester
		seek.initrequester(host,"Find and Replace",380,200)
		
		y=14
		CreateLabel( "Find What",8,y+4,88,24,seek.window )
		seek.findterm=CreateTextField( 96,y,168,20,seek.window )

		y:+32		
		CreateLabel( "Replace With",8,y+4,88,24,seek.window )
		seek.replaceterm=CreateTextField( 96,y,168,20,seek.window )

		x=ClientWidth(seek.window)-104
		y=12
		seek.findnext=CreateButton("Find Next",x,y,96,24,seek.window)
		seek.replaceit=CreateButton("Replace",x,y+32,96,24,seek.window)
		seek.replaceall=CreateButton("Replace All",x,y+64,96,24,seek.window)
		Return seek
	End Function
End Type

Type TSyncModsRequester Extends TRequester
	Field	label:TGadget,user:TGadget,password:TGadget,proxy:TGadget
	Field	options:TGadget[]

	Method SyncMods()
		Local	u$,p$,x$,cmd$,i,n
		u$=quote(TextFieldText(user))
		p$=quote(TextFieldText(password))
		x$=TextFieldText(proxy)
		cmd$=quote(host.bmxpath+"/bin/syncmods")
		cmd$:+" -u "+u$+" -p "+p$
		If x cmd$:+" -y "+x
		n=MODOPTIONS.length
		For Local i=0 Until n
			If ButtonState(options[i]) cmd$:+" "+MODOPTIONS[i]
		Next			
		Hide
		host.execute cmd,"Synchronize Modules"',MENUTRIGGERDOCMODS
	End Method
	
	Method Poll()
		Select EventSource()
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Hide
				EndIf
			Case ok
				SyncMods
			Case cancel
				Hide
		End Select
	End Method
	
	Method Show()
		Super.Show()
		ActivateGadget user
	End Method

	Function Create:TSyncModsRequester(host:TCodePlay)
		Local	sync:TSyncModsRequester
		Local m$
		
		m$=LoadString("incbin::syncmodsblurb.txt")
		
		sync=New TSyncModsRequester
		sync.initrequester(host,"Synchronize modules",420,420)

'		sync.label=CreateLabel(m$,8,20,380,160,sync.window)
		sync.label=CreateTextArea(8,8,400,160,sync.window,TEXTAREA_READONLY|TEXTAREA_WORDWRAP)
		SetGadgetText sync.label,m$
		
		Local i,n,y

		y=180
		
		CreateLabel("Username",20,y+4,80,24,sync.window)
		sync.user=CreateTextField(108,y,160,20,sync.window)
		y:+30

		CreateLabel("Password",20,y+4,80,24,sync.window)
		sync.password=CreateTextField(108,y,160,20,sync.window,TEXTFIELD_PASSWORD)		
		y:+30

		CreateLabel("Proxy Server",20,y+4,80,24,sync.window)
		sync.proxy=CreateTextField(108,y,160,20,sync.window)		
		y:+30
		
		n=MODOPTIONS.length
		sync.options=New TGadget[n]
		For Local i=0 Until n
			sync.options[i]=CreateButton(MODOPTIONS[i],20,y,60,24,sync.window,BUTTON_CHECKBOX)
			If i<2 
				SetButtonState sync.options[i],True
				DisableGadget sync.options[i]
			EndIf
			CreateLabel MODINFO[i],80,y+4,220,24,sync.window
			y:+26
		Next
				
		Return sync
	End Function
	
	Function GetArg$(a$ Var)
		Local 	p,r$
		p=a.Find(",")+1
		If p=0 p=Len a$+1
		r$=a$[..p-1]
		a$=a$[p..]
		Return r$	
	End Function

	Function Crypt$(a$)
		Local b$,c:Byte,i
		For i=0 Until Len a
			c=a[i]
			If c>63 And c<192 c:~(i*-41)&63
			b:+Chr(c)
		Next
		Return b
	End Function
			
	Method FromString(s$)
		Local a$,i,n	
		SetGadgetText user,GetArg(s$)
		SetGadgetText password,Crypt(GetArg(s$))
		SetGadgetText proxy,GetArg(s$)
		n=MODOPTIONS.length
		While s$
			a$=GetArg(s$)
			For Local i=2 Until n
				If a$=MODOPTIONS[i] SetButtonState options[i],True
			Next
		Wend
	End Method
	
	Method ToString$()
		Local sync$,i,n
		sync=TextFieldText(user)
		sync:+","+Crypt(TextFieldText(password))
		sync:+","+TextFieldText(proxy)
		n=MODOPTIONS.length
		For Local i=2 Until n
			If ButtonState(options[i]) sync:+","+MODOPTIONS[i]
		Next
		Return sync
	End Method
End Type

Type TEventHandler Extends TTool
	Method OnEvent() Abstract
End Type

Type TSplitter Extends TEventHandler
	Const	HORIZONTAL=0
	Const	VERTICAL=1

	Field	axis,gadget0:TGadget,gadget1:TGadget,group:TGadget
	Field	panel:TGadget,pos,flipped,snap,oldpos,oldsize
	
	Method Position(p)
		Move p-pos
	End Method
		
	Method Move(d)
		Local	w,h,p
		pos:+d
		w=ClientWidth(group)
		h=ClientHeight(group)
		If pos<0 pos=0
		If axis=VERTICAL
			If pos>w-SPLITWIDTH pos=w-SPLITWIDTH
			If (d<0)~flipped
				SetGadgetShape gadget0,0,0,pos-SPLITWIDTH,h
				SetGadgetShape panel,pos-SPLITWIDTH,0,SPLITWIDTH*2,h
				SetGadgetShape gadget1,pos+SPLITWIDTH,0,w-(pos+SPLITWIDTH),h
			Else
				SetGadgetShape gadget1,pos+SPLITWIDTH,0,w-(pos+SPLITWIDTH),h
				SetGadgetShape panel,pos-SPLITWIDTH,0,SPLITWIDTH*2,h
				SetGadgetShape gadget0,0,0,pos-SPLITWIDTH,h
			EndIf
		Else
			If pos>h-SPLITWIDTH pos=h-SPLITWIDTH
			If (d<0)~flipped
				SetGadgetShape gadget0,0,0,w,pos-SPLITWIDTH
				SetGadgetShape panel,0,pos-SPLITWIDTH,w,SPLITWIDTH*2
				SetGadgetShape gadget1,0,pos+SPLITWIDTH,w,h-(pos+SPLITWIDTH)
			Else
				SetGadgetShape gadget1,0,pos+SPLITWIDTH,w,h-(pos+SPLITWIDTH)
				SetGadgetShape panel,0,pos-SPLITWIDTH,w,SPLITWIDTH*2
				SetGadgetShape gadget0,0,0,w,pos-SPLITWIDTH
			EndIf
		EndIf
	End Method
	
	Method Resize()
		If axis=VERTICAL
			pos=GadgetX(panel)+SPLITWIDTH
			If pos<0 Or pos>ClientWidth(group) move(0)
		Else
			pos=GadgetY(panel)+SPLITWIDTH
			If pos<0 Or pos>ClientHeight(group) move(0)
		EndIf
	End Method		
	
	Method OnEvent()
		If EventSource()<>panel Return
		Select EventID()
			Case EVENT_MOUSEDOWN
				snap=True
				If axis
					oldpos=EventX()
				Else
					oldpos=EventY()
				EndIf				
			Case EVENT_MOUSEENTER
				If axis
					SetPointer POINTER_SIZEWE
				Else
					SetPointer POINTER_SIZENS
				EndIf
			Case EVENT_MOUSELEAVE
				SetPointer POINTER_DEFAULT
			Case EVENT_MOUSEUP
				snap=False
			Case EVENT_MOUSEMOVE
				If axis
					SetPointer POINTER_SIZEWE
				Else
					SetPointer POINTER_SIZENS
				EndIf	
				If snap
					If axis
						Move EventX()-oldpos
					Else
						Move EventY()-oldpos
					EndIf
				EndIf							
		End Select
	End Method
	
	Method SetFlip(f,init=True)
		Local	t:TGadget
		If f<>flipped
			flipped=f
			t=gadget0;gadget0=gadget1;gadget1=t
			If init 
				If axis=VERTICAL
					pos=ClientWidth(group)-pos-1
				Else
					pos=ClientHeight(group)-pos-1
				EndIf
			EndIf
		EndIf			
		If axis
			If flipped
				SetGadgetLayout gadget0,1,0,1,1
				SetGadgetLayout gadget1,1,1,1,1
				SetGadgetLayout panel,1,0,1,1
			Else
				SetGadgetLayout gadget0,1,1,1,1
				SetGadgetLayout gadget1,0,1,1,1
				SetGadgetLayout panel,0,1,1,1
			EndIf
		Else
			If flipped
				SetGadgetLayout gadget0,1,1,1,0
				SetGadgetLayout gadget1,1,1,1,1
				SetGadgetLayout panel,1,1,1,0
			Else
				SetGadgetLayout gadget0,1,1,1,1
				SetGadgetLayout gadget1,1,1,0,1
				SetGadgetLayout panel,1,1,0,1
			EndIf
		EndIf
		Position pos
	End Method

	Function Create:TSplitter(axis,pos,flipped,gadget0:TGadget,gadget1:TGadget)
		Local	s:TSplitter		
		s=New TSplitter
		s.axis=axis
		s.gadget0=gadget0
		s.gadget1=gadget1
		s.group=GadgetGroup(gadget0)
		s.panel=CreatePanel(0,0,0,0,s.group,PANEL_ACTIVE)'|PANEL_BORDER)
		s.pos=pos
		s.setflip flipped,False
		Return s
	End Function
End Type

Type TToolPanel Extends TEventHandler
	Field	name$,path$
	Field	panel:TGadget
	Field	index
	Field	active
	
	Method Show()
	End Method
End Type

Type TView
	Field	node:TGadget,state
End Type

Type TNode Extends TTool
	Const	HIDESTATE=0
	Const	CLOSEDSTATE=1
	Const	OPENSTATE=2

	Field	name$,sortname$
	Field	parent:TNode
	Field	kids:TList=New TList
	Field	views:TView[]
' activate program	
	Field	target:TTool
	Field	action
	Field	argument:Object

	Method SortKids( ascending=True )
		Local term:TLink=kids._head
		Repeat
			Local link:TLink=kids._head._succ
			Local sorted=True
			Repeat
				Local succ:TLink=link._succ
				If succ=term Exit
				Local cc=TNode(link._value).sortname.Compare( TNode(succ._value).sortname )
				If (cc>0 And ascending) Or (cc<0 And Not ascending)
					Local link_pred:TLink=link._pred
					Local succ_succ:TLink=succ._succ
					link_pred._succ=succ
					succ._succ=link
					succ._pred=link_pred
					link._succ=succ_succ
					link._pred=succ
					succ_succ._pred=link
					sorted=False
				Else
					link=succ
				EndIf
			Forever
			If sorted Return
			term=link
		Forever
	End Method

	Method FindArgument:TNode(arg:Object)
		Local	n:TNode,r:TNode,a$
		If arg.Compare(argument)=0 Return Self
		a$=(String(arg)).ToLower()
		If a And a=(String(argument)).toLower() Return Self
		For n=EachIn kids
			r=n.FindArgument(arg)
			If r Return r
		Next
	End Method
	
	Method Dump(indent$="")
		Local	n:TNode		
		Print indent+name
		indent:+"~t"
		For n=EachIn kids
			n.Dump indent
		Next
	End Method		
	
	Method IsHidden()
		Local	v:TView
		If Not parent Return False
		For v=EachIn parent.views
			If v.state=OPENSTATE Return False
		Next
		Return True
	End Method
	
	Method SetAction(tool:TTool,cmd,arg:Object=Null)
		target=tool
		action=cmd
		argument=arg
	End Method

	Method Hide(v:TView=Null)	'null means hide in all views
		Local	n:TNode
		For n=EachIn kids
			n.hide v
		Next
		If v
			If v.node FreeTreeViewNode v.node;v.node=Null
		Else
			For v=EachIn views
				If v.node FreeTreeViewNode v.node;v.node=Null
			Next
		EndIf
	End Method
	
	Method Detach()
		Hide
		If parent parent.kids.remove Self
		parent=Null
	End Method

	Method FreeKids()
		Local	n:TNode		
		For n=EachIn kids
			n.free
		Next
	End Method
			
	Method Free()
		Local	n:TNode		
		For n=EachIn kids
			n.free
		Next
		Detach		'Hide()
		views=Null
	End Method
	
	Method Invoke(command,arg:Object=Null)
		Select command
		Case TOOLACTIVATE 
			If target Return target.Invoke(action,argument)
		End Select
	End Method
	
	Method Find:TNode(treeviewnode:TGadget,view=0)
		Local	n:TNode,r:TNode
		Local	v:TView
		v=getview(view)
		If v And v.node=treeviewnode Return Self
		For n=EachIn kids
			r=n.Find(treeviewnode,view)
			If r Return r
		Next
	End Method
	
	Method SetNode(treeviewnode:TGadget,view=0)
		Local	v:TView
		v=getview(view)
		v.node=treeviewnode
		open view
	End Method
	
	Method HighLight(view=-1)
		Local	v:TView
		If view=-1
			For view=0 To views.length
				HighLight view
			Next
			Return
		EndIf
		v=GetView(view)
		If v.node SelectTreeViewNode v.node
	End Method
	
	Method Open(view=-1)
		Local	v:TView
		If view=-1
			For view=0 To views.length
				Open view
			Next
			Return
		EndIf
		v=GetView(view)
		If v.state<>OPENSTATE
			v.state=OPENSTATE
			RefreshView view
'			if v.node ExpandTreeViewNode v.node
		EndIf
	End Method
	
	Method Close(view=0)
		Local	v:TView
		v=GetView(view)
		If v.state<>CLOSEDSTATE
			v.state=CLOSEDSTATE
'			if v.node CollapseTreeViewNode v.node
		EndIf
	End Method
	
	Method GetState(view=0)
		Local	v:TView
		v=GetView(view)
		Return v.state
	End Method

	Method GetView:TView(view=0)
		If view>=views.length 
			views=views[..view+1]
			views[view]=New TView
		EndIf
		If views[view]=Null views[view]=New TView
		Return views[view]
	End Method
	
	Method GetIndex()
		Local	node:TNode
		Local	i		
		If parent
			For node=EachIn parent.kids
				If node=Self Return i
				i:+1
			Next
		EndIf
	End Method
	
	Method Refresh()
		Local	i
		For i=0 To views.length-1
			RefreshView i
		Next
	End Method
	
	Method RefreshView(view=0)
		Local	n:TNode,quick
		Local	v:TView,vv:TView
		Local	node
		If parent And parent.getstate(view)=CLOSEDSTATE quick=True		
		v=getview(view)
		If v.node And parent
			ModifyTreeViewNode v.node,name
		Else
			If parent And name
				vv=parent.getview(view)
				If vv.node
'					v.node=AddTreeViewNode(name,vv.node)
					v.node=InsertTreeViewNode(GetIndex(),name,vv.node)
					If v.state=HIDESTATE v.state=CLOSEDSTATE					
					If vv.state=OPENSTATE ExpandTreeViewNode vv.node
					quick=False
				EndIf
			EndIf
		EndIf
		If quick Return
		If Not kids Return
		For n=EachIn kids
			n.RefreshView view
		Next
	End Method

	Method NodeAfter:TNode(node:TNode)
		Local	link:TLink
		If node	link=kids.FindLink(node)
		If link link=link.NextLink()
		If link Return TNode(link.Value())	
	End Method

	Method Sync(snap:TNode)
		Local	snapkid:TNode
		Local	currentkid:TNode
		Local	kid:TNode
		Local	t:TNode
		Local	link:TLink

		If snap.name<>name Return
		If kids.Count() currentkid=TNode(kids.First())
		For snapkid=EachIn snap.kids
' if same name in list 
			kid=currentkid
			While kid
				If kid.name=snapkid.name Exit
				kid=NodeAfter(kid)
			Wend
' then remove entries in front			
			If kid
				While currentkid<>kid
					t=currentkid
					currentkid=NodeAfter(currentkid)			
					t.free()
				Wend
			EndIf
' if same name sync else insert 
			If currentkid And currentkid.name=snapkid.name	'merge values if same name 
				currentkid.Sync snapkid
				currentkid=NodeAfter(currentkid)
			Else
				snapkid.detach
				If currentkid
					link=kids.FindLink(currentkid)
					kids.InsertBeforeLink snapkid,link
				Else
					kids.AddLast snapkid
				EndIf
				snapkid.parent=Self
			EndIf
		Next
' remove any entries at end
		While currentkid
			t=currentkid
			currentkid=NodeAfter(currentkid)			
			t.free()
		Wend
		Refresh()
	End Method

	Method SetName(n$)
		name=n
	End Method
		
	Method AddNode:TNode(name$)
		Local	v:TNode
		v=New TNode
		v.setname name
		Append v
		Return v
	End Method
	
	Method Append(v:TNode)
		v.parent=Self
		kids.AddLast v
	End Method	
	
	Function Create:TNode(name$)
		Local	node:TNode
		node=New TNode
		node.setname name
		Return node
	End Function		
End Type

Type THelpPanel Extends TToolPanel

	Field	host:TCodePlay
	Field	htmlview:TGadget
	Field root:TNode
	Field user:TNode
	Field  lang:TNode
	Field mods:TNode

	Method AddLink:TNode(parent:TNode,name$,href$)
		Local	n:TNode
		n=parent.AddNode(name)
		If href href=RealPath(href)
		n.SetAction(Self,TOOLNAVIGATE,href)
		Return n
	End Method
	
	Method ImportLinks(root:TNode,path$)
		Local	stream:TStream
		Local	c$,l$,p,q,a$,n$,cat
		Local	node:TNode
		
		stream=ReadFile(path)
		If Not stream Return
		path=ExtractDir(path)
		node=root
		While Not Eof(stream)
			c=ReadLine(stream)
			If c="" Continue
			If c.Find("Alphabetic index")<>-1
				node=AddLink(root,"Index","")
				Continue
			EndIf
			l=c.ToLower()
			l=l.Replace(QUOTES$,"'")
			p=l.Find("<a")
			If p=-1 Continue
			a$=""
			q=l.find("href=",p)+1
			If q
				If l[q+4]=39	'"'"
					p=q+5
					q=l.find("'",p)
				Else
					p=q+4
					q=l.find(" ",p)
				EndIf
				If q>p 
					a$=path+"/"+c[p..q]
				EndIf
			EndIf
			p=c.Find(">",q)+1
			If p And c[p]=60 p=c.Find(">",p)+1
			q=c.Find("<",p)
			If q<=p Continue
			n$=c[p..q]
'			print n$
			If l.find("onclick=toggle")<>-1
				node=AddLink(root,n$,a$)
			Else
				AddLink(node,n$,a$)
			EndIf
		Wend
		stream.Close
	End Method
	
	Method SyncDocs()
		mods.FreeKids
		AddLink(mods,"Welcome",host.bmxpath+"/doc/bmxmods/welcome.html")
		ImportLinks mods,host.bmxpath+"/doc/bmxmods/navbar.html"
		mods.Refresh
	End Method

	Method BuildTree()
		root=host.helproot
		user=root.AddNode("UserGuide")
		ImportLinks user,host.bmxpath+"/doc/bmxuser/navbar.html"
		lang=root.AddNode("Language")
		ImportLinks lang,host.bmxpath+"/doc/bmxlang/navbar.html"
		mods=root.AddNode("Modules")
		SyncDocs
		AddLink root,"Samples",host.bmxpath+"/samples/index.html"
		root.refresh
	End Method

	Method Invoke(command,argument:Object=Null)
		Local	href$
		If Not htmlview Return
		Select command
			Case TOOLCUT
				GadgetCut htmlview
			Case TOOLCOPY
				GadgetCopy htmlview
			Case TOOLPASTE
				GadgetPaste htmlview
			Case TOOLSHOW 
				ActivateGadget htmlview	
				host.SetTitle
			Case TOOLNAVIGATE
				href$=String(argument)
				If href Go href
			Case TOOLPRINT
				GadgetPrint htmlview
		End Select
	End Method

	Method OnEvent()
		Local	url$,p,t$
		If EventSource()=htmlview
			Select EventID()			
				Case EVENT_GADGETACTION				'NAVIGATEREQUEST
					url$=String( EventExtra() )
					If url[..5]="http:"
						OpenURL url
					Else
						p=url.findlast(".")
						If p>-1
							t$=url[p..].tolower()
							If t$=".bmx"
								If url.Find( "file://" )=0
									url=url[7..]
									?Win32
									url=url[1..]
									?
								EndIf 
								url=url.replace("%20"," ")
								Local source:TOpenCode=host.OpenSource(url)
								If source source.MakePathTemp
							Else 
								url=url.Replace("\","/")
								url=url.Replace("user/index","user/welcome")
								url=url.Replace("lang/index","lang/welcome")
								url=url.Replace("mods/index","mods/welcome")
								Go url$
							EndIf
						EndIf
					EndIf
			End Select			
		EndIf
	End Method
	
	Method Go(url$,internal=False)
		Local	node:TNode

		If host.options.externalhelp And Not internal
			PollSystem
			OpenURL url
			MinimizeWindow host.window
			PollSystem
			Return
		EndIf

		HtmlViewGo htmlview,url$
		host.SelectPanel Self
		node=host.helproot.FindArgument(RealPath(url))
		If node
			node.Highlight
		Else
'			print "node not found"
		EndIf		
		ActivateGadget htmlview			
	End Method
	
	Method Home()
		Go host.bmxpath+HOMEPAGE,True
	End Method
	
	Method Forward()
		HtmlViewForward htmlview
	End Method

	Method Back()
		HtmlViewBack htmlview
	End Method

	Function Create:THelpPanel(host:TCodePlay)
		Local	p:THelpPanel
		Local	root,style		
		p=New THelpPanel
		p.host=host
		p.name="Help"
		codeplay.addpanel(p)
		style=HTMLVIEW_NONAVIGATE		'HTMLVIEW_NOCONTEXTMENU
		p.htmlview=CreateHTMLView(0,0,ClientWidth(p.panel),ClientHeight(p.panel),p.panel,style)		
		SetGadgetLayout p.htmlview,1,1,1,1
'		p.Home
		p.buildtree
		Return p
	End Function

End Type

Type TSearchRequester Extends TRequester
	Field	findbox:TGadget,typebox:TGadget,pathbox:TGadget,pathbutton:TGadget

	Method Poll()
		Local	id,data,index
		id=EventID()
		data=EventData()
		Select EventSource()
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Hide
				EndIf
			Case ok
				Hide				
			Case cancel
				Hide
		End Select
	End Method

	Function Create:TSearchRequester(host:TCodePlay)
		Local	search:TSearchRequester
		search=New TSearchRequester
		search.initrequester(host,"Find In Files",400,256)

		CreateLabel("Find What",6,18,80,24,search.window)
		search.findbox=CreateTextField(88,14,180,20,search.window)

		CreateLabel("File Types",6,48,80,24,search.window)
		search.typebox=CreateTextField(88,44,180,20,search.window)

		CreateLabel("Search Path",6,78,80,24,search.window)
		search.pathbox=CreateTextField(88,74,180,20,search.window)

		Return search
	End Function
End Type

Type TProjectRequester Extends TRequester
	Field	projects:TProjects
	Field	listbox:TGadget,add:TGadget,remove:TGadget,props:TGadget

	Method Invoke(command,arg:Object=Null)
		Select command
		Case TOOLACTIVATE 
			Refresh
		End Select
	End Method

	Method Poll()
		Local	id,data,index
		id=EventID()
		data=EventData()
		Select EventSource()
			Case window
				If EventID()=EVENT_WINDOWCLOSE
					Hide
				EndIf
			Case add
				projects.AddProject
				Refresh
			Case remove
				projects.RemoveProject SelectedGadgetItem(listbox)
				Refresh
			Case ok
				Hide				
			Case cancel
				Hide
		End Select
	End Method

	Method Refresh()
		Local	node:TNode
		
		ClearGadgetItems listbox
		For node=EachIn projects.kids
			If TFolderNode(node)'node.argument
				AddGadgetItem listbox,node.name
			EndIf
		Next
	End Method
	
	Method Open(projnode:TProjects)
		projects=projnode
		Refresh
		Show
	End Method
	
	Function Create:TProjectRequester(host:TCodePlay)
		Local x,y
		Local	proj:TProjectRequester
		proj=New TProjectRequester
	
		proj.initrequester(host,"Project Manager",400,256)
		proj.listbox=CreateListBox( 8,8,248,184,proj.window )
		
		x=ClientWidth(proj.window)-136
		proj.add=CreateButton("Add Project",x,8,128,24,proj.window)
		proj.remove=CreateButton("Remove Project",x,40,128,24,proj.window)
		proj.props=CreateButton("Properties",x,72,128,24,proj.window)
		DisableGadget proj.props 
		Return proj
	End Function
End Type


Type TFolderNode Extends TNode
	Field	path$
	Field	scanned
	Field	owner:TNode

	Method Open(view=-1)
		If Not scanned
			ScanKids		'Path name,path
		EndIf
		Super.Open view
	End Method

	Method AddFileNode:TNode(file$)
		Local	n:TNode
		Local 	ext$
		
		If NodeFileTypes.find("|"+ExtractExt(file).toLower()+"|")<0 Return
		n=AddNode(StripDir(file))
		n.SetAction(owner,TOOLOPEN,file)
		ext=ExtractExt(file$).ToLower()
		n.sortname=ext+n.name
		Return n
	End Method

	Method AddFolderNode:TNode(path$)
		Local	n:TNode
		n=TFolderNode.Create(path)
		n.sortname=" "+n.name
		Append n
		Return n
	End Method

	Method Scan(o:TNode)
		Local	flist:TList
		Local	dir,f$,p$,t	
		owner=o
		flist=New TList
		dir=ReadDir(path)
		If dir
			Repeat
				f$=NextFile(dir)
				If f$="" Exit
				If f[..1]="." Continue
				p$=path+"/"+f
				Select FileType(p$)
				Case FILETYPE_FILE
					AddFileNode p$
				Case FILETYPE_DIR
					AddFolderNode p$
				End Select				
			Forever
			CloseDir dir
		EndIf
		SortKids
	End Method
	
	Method ScanKids()
		Local	f:TFolderNode		
		If scanned Return
		For f=EachIn kids
			f.Scan owner
		Next
		scanned=True
	End Method

	Function Create:TFolderNode(path$)
		Local	n:TFolderNode
'		print "create foldernode "+path
		n=New TFolderNode
		n.setname StripDir(path)
		n.path=path
		Return n
	End Function
End Type

Type TProjects Extends TNode
	Field	host:TCodePlay
	Field	addproj:TNode
	
	Method RemoveProject(index)
		Local	node:TNode
		If index<0 Or index>=kids.Count() Return
		node=TNode(kids.ValueAtIndex(index))
		If node node.Free
		Refresh
	End Method			
	
	Method AddProject()
		Local	path$,name$,stream:TStream
		path=RequestDir("Select the project folder")
		If Not path Return
		Add path,path
'		addproj.Detach
'		Append TFolderNode.Create(path)		
'		name=StripDir(path)		
'		If Not ScanPath(Self,name,path)	
'			If Confirm("No source files found, create "+name+".bmx?")
'				stream=WriteStream(path+"/"+name+".bmx")
'				If stream CloseStream stream
'				ScanPath(Self,name,path)
'			EndIf
'		EndIf
'		Append addproj
'		Refresh
	End Method

	Method Add(name$,path$)
		Local folder:TFolderNode
		addproj.Detach
		folder=TFolderNode.Create(path)
		folder.Scan Self
		Append folder
		Append addproj
		Refresh
	End Method

	Method Write(stream:TStream)
		Local	project:TFolderNode
		For project=EachIn kids
			stream.WriteLine "proj_node="+project.name+"|"+project.path'node.argument.ToString()
		Next
	End Method

	Method Invoke(command,argument:Object=Null)
		Select command
		Case TOOLNEW
			AddProject
		Case TOOLOPEN
			host.OpenSource String(argument)
		End Select
	End Method
			
	Function CreateProjects:TProjects(host:TCodePlay)
		Local	p:TProjects
		p=New TProjects
		p.SetName("Projects")
		p.host=host
		p.addproj=p.AddNode("Add Project")
		p.addproj.SetAction p,TOOLNEW
		Return p
	End Function
End Type

Type TByteBuffer Extends TStream
	Field	bytes:Byte[]
	Field	readpointer

	Method Read( buf:Byte Ptr,count )
		If count>readpointer count=readpointer
		If count=0 Return
		MemCopy buf,bytes,count
		readpointer:-count
		If readpointer MemMove bytes,Varptr bytes[count],readpointer
		Return count 
	End Method

	Method Write( buf:Byte Ptr,count )
		Local	n,m
		n=readpointer+count
		If n>bytes.length
			m=Max(bytes.length*1.5,n)
			bytes=bytes[..m]
		EndIf
		MemCopy Varptr bytes[readpointer],buf,count
		readpointer=n
		Return count
	End Method	
	
	Method LineAvail()
		Local	i
		For i=0 Until readpointer
			If bytes[i]=10 Return True
		Next
	End Method

	Method FlushBytes:Byte[]()
		Local res:Byte[]=bytes[..readpointer]
		readpointer=0
		Return res
	End Method
End Type

Type TObj
	Field	addr$,sync,refs
End Type

Type TVar Extends TNode

	Field	owner:TDebugTree
	Field	obj:Object

	Method Free()
		If obj owner.RemoveObj obj
		obj=Null
		Super.Free()
	End Method

	Method SetVarName(n$)
		Local	p
		name=n
' if object ref set addr$ field	
		If name.find("$=")=-1 And name.find(")=$")=-1
			p=name.find("=$")
			If p<>-1
				TDebugTree.RemoveObj obj
				obj=TDebugTree.AddObj(name[p+2..])
			EndIf
			p=name.find("=Null")
			If p<>-1
				FreeKids
				TDebugTree.RemoveObj obj
				obj=Null
			EndIf
		EndIf
	End Method
	
	Method AddVar(name$)
		Local	v:TVar
		v=New TVar
		v.setvarname name
		v.owner=owner
		Append v
	End Method
	
	Method SetValue(val:TVar)
		Local	v:TVar,w:TVar,i
' if this is a reference to same object refresh values	
		If obj And obj=val.obj
'			sync=val.sync
			If kids.IsEmpty()
				For v=EachIn val.kids
					AddVar v.name
				Next
			Else
				For v=EachIn val.kids
					If i<kids.count()
						w=TVar(kids.ValueAtIndex(i))
						If w w.SetVarName v.name
					Else
						AddVar v.name
					EndIf
					i:+1
				Next			
			EndIf
			Refresh
		EndIf	
' recurse so all references are updated
		If IsHidden() Return				'parent And parent.state=CLOSEDSTATE Return
		For v=EachIn kids
			v.SetValue val
		Next
	End Method
	
	Method Open(view=0)
		owner.sync:+1
		Super.Open view
		owner.SyncVars
	End Method
	
End Type

Type TScope Extends TVar
	Field	tree:TDebugTree
	Field	file$,line,column

	Method Invoke(command,argument:Object=Null)
		Select command
			Case TOOLACTIVATE
				tree.SelectScope Self,True
		End Select
	End Method
	
	Method SetScope(s:TScope)
		Local	v:TVar
		file=s.file
		line=s.line
		column=s.column
		s.obj=Self
		SetValue s
	End Method
	
	Method SetFile(debugtree:TDebugTree,f$)
		Local	p,q,r
		tree=debugtree
		p=Instr(f,"<")
		q=Instr(f,">")
		r=Instr(f,",")
		If p And q And r
			file=f[..p-1]
			line=Int(f[p..r-1])
			column=Int(f[r..q-1])
		EndIf
		obj=Self
	End Method
End Type

Type TDebugTree Extends TVar
	Global	sync
	Global	objlist:TList
	Field	host:TCodePlay
	Field	instack:TList
	Field	inscope:TScope
	Field	invar:TVar
	Field	infile$
	Field	inexception$
	Field	firststop
	Field	cancontinue
		
	Method Reset()
		host.SetMode host.DEBUGMODE
		SetStack New TList
		objlist=New TList	
		instack=Null
		inscope=Null
		invar=Null
		infile=""
		inexception=""
		sync=0
		firststop=True
		cancontinue=False
	End Method

	Function AddObj:TObj(addr$)
		Local	o:TObj
		For o=EachIn objlist
			If o.addr=addr 
				o.refs:+1
				Return o
			EndIf
		Next
		o=New TObj
		o.addr=addr
		o.refs=1
		objlist.addlast o
		Return o
	End Function

	Function FindObj:TObj(addr$)
		Local	o:TObj
		For o=EachIn objlist
			If o.addr=addr Return o
		Next
	End Function
	
	Function RemoveObj(obj:Object)		':TObj
		Local	o:TObj
		o=TObj(obj)
		If Not o Return
		o.refs:-1
		If o.refs=0 
			objlist.Remove o
		EndIf
	End Function

	Method SyncVars()
		Local	o:TObj
		For o=EachIn objlist
			If o.sync<sync 
				o.sync=sync
				host.output.WritePipe "d"+o.addr
			EndIf
		Next		
	End Method
			
	Method SetStack(list:TList)
		Local	scope:TScope
		Local	openscope:TScope
		Local	s:TScope
		Local	count,i

		count=kids.count()			'root.varlist.count()
		For scope=EachIn list
			If i>=count
				Append scope		'root.Append scope
				s=scope
			Else
				s=TScope(kids.ValueAtIndex(i))
' simon was here				
				If s.name=scope.name
					s.SetScope scope
					scope.Free
				Else
					While kids.count()>i
						s=TScope(kids.ValueAtIndex(i))
						s.free
					Wend
					Append scope
					s=scope
					count=i+1
				EndIf
				
			EndIf
			If firststop
				If host.IsSourceOpen(s.file) openscope=s
			Else
				openscope=s
			EndIf
			i:+1
		Next
		While kids.count()>i
			scope=TScope(kids.ValueAtIndex(i))
			scope.free
		Wend 
		If list.isempty() Return
		If Not openscope openscope=TScope(list.First())
		If openscope SelectScope openscope,True
		Refresh
		firststop=False
	End Method

	Method SelectScope(scope:TScope,open)
		If Not scope Return		
		If scope.file host.DebugSource scope.file,scope.line,scope.column
		scope.Open
'		If open
'			SelectTreeViewNode scope.node
'			scope.open
'		EndIf
	End Method

	Method ProcessError$(line$)
		Local	p
				
		While line[..1]=">" 
			line=line[1..]
		Wend
		If Not line Return

		If line[..2]<>"~~>" Return line

		line=line[2..]

		If invar
			If line="}"
				SetValue invar		'root
				invar.Free
				invar=Null
			Else
'				If Not invar.name
'					invar.name=line
'				Else
					invar.AddVar line
'				EndIf
			EndIf
			Return
		EndIf
		
		If instack 			
			If line="}" 
				sync:+1
				SetStack instack
				instack=Null
				inscope=Null
				SyncVars
				If inexception
					Notify inexception
					inexception=""
				EndIf
				Return
			EndIf
			
			If infile
				If line="Local <local>"
				Else
					inscope=New TScope
'					Print "inscope.line="+line
					inscope.name=line
					inscope.owner=Self
					instack.AddLast inscope
				EndIf
				If inscope inscope.setfile Self,infile
				infile=""
				Return
			EndIf

			If Instr(line,"@")=1 And Instr(line,"<")
				infile=line[1..]
			Else
				If inscope inscope.AddVar line
			EndIf

			Return
		EndIf

		If line[..20]="Unhandled Exception:"
			inexception=line
			host.output.WritePipe "t"
			cancontinue=False
			Return
		EndIf

		If line="StackTrace{" 
			instack=New TList
			Return
		EndIf

		If line="Debug:" Or line="DebugStop:"
			host.output.WritePipe "t"
			cancontinue=True
			Return
		EndIf					
		
		If line[..11]="ObjectDump@"
			p=line.find("{")
			If p=-1 Return line
			line=line[11..p]
			invar=New TVar
'			invar.obj=AddObj(line)
			invar.obj=FindObj(line)	'simon come here
			invar.owner=Self
			Return
		EndIf
		
	End Method

	Function CreateDebugTree:TDebugTree(host:TCodePlay)
		Local	d:TDebugTree
		objlist=New TList
		d=New TDebugTree
		d.owner=d
		d.SetName "Debug"
		d.host=host
		d.Open
		Return d		
	End Function
End Type

Type TNodeView
	Field	owner:TNavBar
	Field	root:TNode
	Field	treeview:TGadget
	Field	index
	
	Method NewView()
		Local	n:TNode,hnode:TGadget
		hnode=SelectedTreeViewNode(treeview)
		n=root.Find(hnode,index)
		If n And n.parent owner.AddView n
	End Method

	Method OnEvent()
		Local	n:TNode
		n=root.Find(TGadget(EventExtra()),index)
		If Not n Return
		Select EventID()
			Case EVENT_GADGETSELECT
				n.invoke(TOOLSELECT)				
			Case EVENT_GADGETACTION
				n.invoke(TOOLACTIVATE)				
			Case EVENT_GADGETMENU
				n.invoke(TOOLMENU,Self)
			Case EVENT_GADGETOPEN
				n.open index
			Case EVENT_GADGETCLOSE
				n.close index
		End Select
	End Method
End Type

Type TNavBar Extends TEventHandler
	Field	host:TCodePlay
	Field	tabber:TGadget
	Field	viewlist:TList=New TList
	Field	selected:TNodeView
	Field	navmenu:TGadget
	
	Method SelectedView()
		If selected Return selected.index
	End Method
		
	Method SelectView(index)
		Local	n:TNodeView
		If index>=viewlist.count() Return
		n=TNodeView(viewlist.ValueAtIndex(index))
		If Not n Print "selectview failed";Return
		If n<>selected
			If selected HideGadget selected.treeview
			selected=n
		EndIf
		ShowGadget n.treeview
		SelectGadgetItem tabber,index
	End Method
	
	Method AddView(node:TNode)
		Local	n:TNodeView
		Local	index,root:TGadget
		For n=EachIn viewlist
			If n.root=node SelectView n.index;Return
		Next
		n=New TNodeView
		n.owner=Self
		n.root=node
		n.treeview=CreateTreeView(0,0,ClientWidth(tabber),ClientHeight(tabber),tabber)
		host.options.navstyle.Apply n.treeview
		SetGadgetLayout n.treeview,1,1,1,1
		HideGadget n.treeview

		viewlist.AddLast n		
		n.index=viewlist.Count()

		AddGadgetItem tabber,node.name
		root=TreeViewRoot(n.treeview)
		node.setnode root,n.index
		SelectView n.index
		Return n.index
	End Method

	Method OnEvent()
		If EventSource()=tabber
			SelectView SelectedGadgetItem(tabber)				
		End If
		If selected And EventSource()=selected.treeview
			selected.OnEvent
		EndIf
	End Method

	Method Refresh()
		Local	view:TNodeView
		For view=EachIn viewlist
			host.options.navstyle.Apply view.treeview
		Next
	End Method

	Method Invoke(command,argument:Object=Null)
		If command=TOOLREFRESH Refresh()		
		If command=TOOLNEWVIEW And selected selected.NewView
	End Method

	Function CreateNavMenu:TGadget()
		Local	edit:TGadget
		edit=CreateMenu("&Nav",0,Null)
		CreateMenu "&New View",MENUNEWVIEW,edit
		Return edit
	End Function

	Function Create:TNavBar(host:TCodePlay)	',root:TNode)
		Local	n:TNavBar
		n=New TNavBar
		n.host=host	
		n.tabber=CreateTabber(0,0,1,1,host.client)
'		n.AddView root
		n.navmenu=CreateNavMenu()
		Return n		
	End Function

End Type
