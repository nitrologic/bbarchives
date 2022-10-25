; ID: 1913
; Author: Vic 3 Babes
; Date: 2007-02-03 21:27:51
; Title: Blitz2HTML
; Description: Converts Blitz to HTML for html tutorials, or printing

;Blitz Basic 1.8 / BlitzPlus code to HTML
;Harlequin Software 2006/07

;Originally written for Blitz Basic 1.8 - updated for BlitzPlus 30/01/07

;LEAVE DEBUGGING SWITCH ON - IF YOU GET OFFSET OUT OF RANGE ERROR THEN INCREASE
;HTMLBANKSIZE constant

;I've just upgraded to BlitzPlus, and have just added the new commands to
;the data statements below - if however, I've placed some in the wrong alphabetcial order,
;then they may not appear correctly in the final output.  Since I am new to these commands
;I haven't actually written anything that I can test it with, and although I've tested
;a few of the samples that come with BlitzPlus - I don't know the commands well enough
;to spot the non-coloured ones.  I'm sure if you spot errors in your final output, you
;can put them right easily enough though.  Also, Global, EndGraphics and MouseWait were
;not in the Blitz Command List, which I copied for the data statements - and there may
;be others that I don't know about.  Also - HTML commands in the index have uppercase HTML
;but the actual commands don't - I had to alter the data because it is case-sensitive - there
;may be other commands that are not case-correct in the index that I copied.  Finally - FontDescend
;is described as being added in v1.39 - but isn't recognized as a command by the IDE - but I have
;added it to the list of commands in the data statements anyway.
;
;I DON'T HAVE BLITZ 3D - SO DON'T KNOW THE COMMANDS TO ADD
;
;Although BlitzPlus provides access to file-requesters, I prefer
;to use a constant for the bb and html filenames, because when writing a tutorial, it's
;usually just small bits of code that I want to convert, and I can simply paste them
;into code.bb, save it, and then execute this - a file-requester would take longer.
;Besides - I haven't learnt the new BlitzPlus commands for file-requesters and stuff.
;
AppTitle "Blitz2HTML"
;you might want to change one or more of the following constants
Const HTMLBANKSIZE=102400		;offset out of range - increase size if necessary
Const STYLESHEET$="blitz.css"	;change to blitzprint.css if you want a printable web-page
Const BBNAME$="code.bb"			;name of bb file to convert
Const HTMLNAME$="code.html"		;name of html output file
Const TABSPACES=4				;number of non-breaking spaces to insert to simulate a tab
;###########################################

;don't change the following though
Const QUOTES=34, SEMICOLON=59, TAB=9, CARRIAGERETURN=13 ;ascii values
Const UNDERSCORE=95, LESSTHAN=60, GREATERTHAN=62, SPACE=32
;I'm not dyslexic - I'm big-endian (or is that little-endian?)
Const NBSP1=$73626E26, NBSP2=$3B70				;  - for poking - backwards in hex coz of high-order bytes 'n' stuff
Const ENDSPAN1=$70732F3C, ENDSPAN2=$3E6E61		;</span> - as above
Const BREAK=$3E72623C							;<br> - as above
Const NEWLINE=$0A0D								;13,10 - backwards as above
Const LT=$3B746C26, GT=$3B746726				;html tags to display < and >

Global offset, byte, midword
Global keyword$, comment$, textstring$
Global bbkey$, temptextpointer, datakey$
Global numkeys, currentkey, keycolouron, keyfound
Global textbanksize, file
Global textpointer, htmlpointer
Global keywordlen, commentlen, textstringlen
Global rownumber, nextcomment

;you can shorten keyword, comment and textstring here - but make
;sure you also shorten them in the CSS file as well
keyword$="<span class="+Chr$(34)+"keyword"+Chr$(34)+">"
comment$="<span class="+Chr$(34)+"comment"+Chr$(34)+">"
textstring$="<span class="+Chr$(34)+"textstring"+Chr$(34)+">"
;which is why the lengths are noted below - in case you alter
;the above - as they are poked into the html bank in a loop
;according to their length
keywordlen=Len(keyword$)
commentlen=Len(comment$)
textstringlen=Len(textstring$)

;############### load bb file ##################

textbanksize=FileSize(BBNAME$)
If Not(textbanksize) Then RuntimeError BBNAME$+Chr$(13)+Chr$(10)+"Does it exist?"

Global textbank=CreateBank(textbanksize)
Global htmlbank=CreateBank(HTMLBANKSIZE)

file=OpenFile(BBNAME$)
If file
	ReadBytes(textbank,file,0,textbanksize)
	CloseFile file
Else
	RuntimeError "Couldn't open"+Chr$(13)+Chr$(10)+BBNAME$
EndIf

;################ convert to html ##############
rownumber=0
textpointer=0
htmlpointer=0
midword=False
Repeat
	byte=PeekByte(textbank,textpointer)
	If (byte>64) And (byte<91) And (Not(midword))	;uppercase - not in middle of a word - is it a keyword?
;following select split into 2 just to speed things up a bit
		If byte<77
			Select byte
				Case 65 Restore a
				Case 66 Restore b
				Case 67 Restore c
				Case 68 Restore d
				Case 69 Restore e
				Case 70 Restore f
				Case 71 Restore g
				Case 72 Restore h
				Case 73 Restore i
				Case 74 Restore j
				Case 75 Restore k
				Case 76 Restore l
				Default Restore none
			End Select
		Else
			Select byte
				Case 77 Restore m
				Case 78 Restore n
				Case 79 Restore o
				Case 80 Restore p
				Case 82 Restore r
				Case 83 Restore s
				Case 84 Restore t
				Case 85 Restore u
				Case 86 Restore v
				Case 87 Restore w
				Case 88 Restore x
				Default Restore none
			End Select
		EndIf
		bbkey$=""
		temptextpointer=textpointer+1
		If temptextpointer<textbanksize
			byte=PeekByte(textbank,temptextpointer)
			While ((byte>64 And byte<91) Or (byte>96 And byte<123) Or (byte=UNDERSCORE)	Or (byte>47 And byte<58)) And (temptextpointer<textbanksize)
				bbkey$=bbkey$+Chr$(byte)
				temptextpointer=temptextpointer+1
				If temptextpointer<textbanksize Then byte=PeekByte(textbank,temptextpointer)
			Wend
			keyfound=False
			Read numkeys	;number of keywords beginning with this letter
			If numkeys		;some letters have no keywords, so don't bother comparing
				currentkey=0
				Repeat
					Read datakey$
					If bbkey$=datakey$
						keyfound=True
					Else
						If bbkey$ < datakey$ Then Exit
					EndIf
					currentkey=currentkey+1
				Until keyfound Or (currentkey=numkeys+1)
			EndIf
			If keyfound
				If Not(keycolouron)	;poke the <span class="keyword"> string to the html bank
					keycolouron=True
					For offset=0 To keywordlen-1
						PokeByte htmlbank,htmlpointer+offset,Asc(Mid$(keyword$,offset+1,1))
					Next
					htmlpointer=htmlpointer+keywordlen
				EndIf
			Else
				If keycolouron Then addendspan()	;pokes </span> to terminate text-colouring
			EndIf
			;copy the word to the html bank - bb keyword or not
			CopyBank textbank,textpointer,htmlbank,htmlpointer,temptextpointer-textpointer
			htmlpointer=htmlpointer+(temptextpointer-textpointer)
			textpointer=temptextpointer
		Else	;last character in text file
			If keycolouron Then addendspan()	;pokes </span> to terminate text-colouring
			PokeByte htmlbank,htmlpointer,byte
			htmlpointer=htmlpointer+1			;total number of bytes to save
			textpointer=textpointer+1
		EndIf
	Else	;not uppercase at start of a word - so wasn't a keyword
		Select byte
			Case QUOTES
				If keycolouron Then addendspan()	;terminate any other text-colour with </span>
				For offset=0 To textstringlen-1		;poke <span class="textstring"> string to htmlbank
					PokeByte htmlbank,htmlpointer+offset,Asc(Mid$(textstring$,offset+1,1))
				Next
				htmlpointer=htmlpointer+textstringlen
				PokeByte htmlbank,htmlpointer,QUOTES
				htmlpointer=htmlpointer+1
				textpointer=textpointer+1
				If textpointer<textbanksize
					Repeat
						byte=PeekByte(textbank,textpointer)
						textpointer=textpointer+1
						If byte<>QUOTES
							If (byte=LESSTHAN) Or (byte=GREATERTHAN)
								If byte=LESSTHAN Then PokeInt htmlbank,htmlpointer,LT Else PokeInt htmlbank,htmlpointer,GT
								htmlpointer=htmlpointer+4
							Else
								PokeByte htmlbank,htmlpointer,byte
								htmlpointer=htmlpointer+1
							EndIf
						EndIf
					Until (byte=QUOTES) Or (textpointer=textbanksize) Or (byte=CARRIAGERETURN)
					If byte<>QUOTES
						RuntimeError "String not terminated with" +Chr$(13)+Chr$(10)+"quotes on row "+rownumber
						;we could ignore this - but it's probably better to inform the user - the Blitz compiler
						;doesn't highlight this error, but prints the string without the last character.
						;maybe I'm the only person that forgets to terminate a string with quotes - usually in
						;data statements
					Else
						PokeByte htmlbank,htmlpointer,QUOTES
						htmlpointer=htmlpointer+1
					EndIf
					addendspan()	;poke </span> to end the string-colouring
				Else
					RuntimeError "Last row not terminated with quotes"
				EndIf
				midword=False
			Case SEMICOLON
				If keycolouron Then addendspan()	;turn off previous text-colouring with </span>
				For offset=0 To commentlen-1	;poke <span class="comment"> to htmlbank
					PokeByte htmlbank,htmlpointer+offset,Asc(Mid$(comment$,offset+1,1))
				Next
				htmlpointer=htmlpointer+commentlen
				Repeat
					nextcomment=False
					PokeByte htmlbank,htmlpointer,SEMICOLON
					htmlpointer=htmlpointer+1
					textpointer=textpointer+1
					If textpointer<textbanksize
						Repeat
							byte=PeekByte(textbank,textpointer)
							textpointer=textpointer+1
							If byte<>CARRIAGERETURN
								If (byte=LESSTHAN) Or (byte=GREATERTHAN)
									If byte=LESSTHAN Then PokeInt htmlbank,htmlpointer,LT Else PokeInt htmlbank,htmlpointer,GT
									htmlpointer=htmlpointer+4
								Else
									If byte=TAB
										add_tab()
										textpointer=textpointer-1	;was increased in add_tab() but increased above as well
									Else
										PokeByte htmlbank,htmlpointer,byte
										htmlpointer=htmlpointer+1
									EndIf
								EndIf
							EndIf
						Until (byte=CARRIAGERETURN) Or (textpointer=textbanksize)
						If (byte=CARRIAGERETURN)
							add_enter()
							;is the next line also a comment and was it bloody tabbed to?
							While PeekByte(textbank,textpointer)=TAB
								add_tab()
							Wend
							If textpointer<>textbanksize
								If PeekByte(textbank,textpointer)=SEMICOLON
									nextcomment=True
								EndIf
							EndIf
						EndIf
					EndIf
				Until (textpointer=textbanksize) Or (nextcomment=False)
				addendspan()
				midword=False
			Case TAB
				add_tab()
				midword=False
			Case CARRIAGERETURN
				add_enter()
				textpointer=textpointer+1
				midword=False
			Case LESSTHAN
				PokeInt htmlbank,htmlpointer,LT
				htmlpointer=htmlpointer+4
				textpointer=textpointer+1
				midword=False
			Case GREATERTHAN
				PokeInt htmlbank,htmlpointer,GT
				htmlpointer=htmlpointer+4
				textpointer=textpointer+1
				midword=False
			Default	;Not an uppercase character at start of a word - so not a keyword, and not any of the above
				If keycolouron
					If (byte<>SPACE) Then addendspan()	;</span>
				EndIf
				;   lowercase a-z                       _                uppercase a-z               numbers
				If ((byte>96) And (byte<123)) Or (byte=UNDERSCORE) Or ((byte>64) And (byte<91))	Or (byte>47 And byte<58)
					midword=True
				Else
					midword=False
				EndIf
				PokeByte htmlbank,htmlpointer,byte
				htmlpointer=htmlpointer+1
				textpointer=textpointer+1
		End Select
	EndIf
Until textpointer=textbanksize
If keycolouron Then addendspan()
;################ write html file ##############
file=WriteFile(HTMLNAME$)
If file
	WriteLine file,"<html>"
	WriteLine file,"<head>"
	WriteLine file,"<title>Converted Code</title>"
	WriteLine file,"<link rel="+Chr$(QUOTES)+"stylesheet"+Chr$(QUOTES)+" type="+Chr$(QUOTES)+"text/css"+Chr$(QUOTES)+" href="+Chr$(QUOTES)+STYLESHEET$+Chr$(QUOTES)+" />"
	WriteLine file,"</head>"
	WriteLine file,"<body>"
	WriteLine file,""
	WriteLine file,"<p class="+Chr$(QUOTES)+"blitz"+Chr$(QUOTES)+">"
	WriteBytes(htmlbank,file,0,htmlpointer)
	WriteLine file,""
	WriteLine file,"</p>"
	WriteLine file,""
	WriteLine file,"</body>"
	WriteLine file,"</html>"
	CloseFile file
	ExecFile HTMLNAME$
Else
	RuntimeError "Couldn't write "+HTMLNAME$
EndIf
End
;###########################################
Function add_tab()
Local tabloop
	For tabloop=1 To TABSPACES
		PokeInt htmlbank,htmlpointer,NBSP1
		PokeShort htmlbank,htmlpointer+4,NBSP2
		htmlpointer=htmlpointer+6
	Next
	textpointer=textpointer+1
Return
End Function
;###########################################
Function add_enter()
	rownumber=rownumber+1
	PokeInt htmlbank,htmlpointer,BREAK
	PokeShort htmlbank,htmlpointer+4,NEWLINE
	htmlpointer=htmlpointer+6
	textpointer=textpointer+1	;to skip ascii 13 or 10 depending on where called from
Return
End Function
;###########################################
Function addendspan()
	PokeInt htmlbank,htmlpointer,ENDSPAN1
	PokeInt htmlbank,htmlpointer+4,ENDSPAN2
	htmlpointer=htmlpointer+7
	keycolouron=False
Return
End Function
;###########################################

.a
Data 18
Data "bs", "cceptTCPStream", "Cos", "ctivateGadget", "ctivateWindow", "ctiveWindow"
Data "ddGadgetItem", "ddTextAreaText", "ddTreeViewNode", "fter", "nd", "ppTitle"
Data "sc", "Sin", "Tan", "Tan2", "utoMidHandle", "vailVidMem"
.b
Data 5
Data "ackBuffer", "ankSize", "efore", "in", "uttonState"
.c
Data 69
Data "allDLL", "anvasBuffer", "ase", "eil", "hangeDir", "hannelPan", "hannelPitch", "hannelPlaying", "hannelVolume"
Data "heckMenu", "hr", "learGadgetItems", "lientHeight", "lientWidth", "loseDir", "loseFile", "loseMovie", "loseTCPServer"
Data "loseTCPStream", "loseUDPStream", "ls", "lsColor", "ollapseTreeViewNode", "olor", "olorBlue", "olorGreen", "olorRed"
Data "ommandLine", "onfirm", "onst", "opyBank", "opyFile", "opyImage", "opyPixel", "opyPixelFast", "opyRect", "opyStream"
Data "os", "ountGadgetItems", "ountGfxDrivers", "ountGfxModes", "ountHostIPs", "ountTreeViewNodes"
Data "reateBank", "reateButton", "reateCanvas", "reateComboBox", "reateDir", "reateHtmlView", "reateImage", "reateLabel"
Data "reateListBox","reateMenu", "reateNetPlayer", "reatePanel", "reateProgBar", "reateSlider", "reateTabber"
Data "reateTCPServer", "reateTextArea", "reateTextField", "reateTimer", "reateToolBar", "reateTreeView", "reateUDPStream"
Data "reateWindow", "urrentDate", "urrentDir", "urrentTime"
.d
Data 20
Data "ata", "ebugLog", "efault", "elay", "elete", "eleteDir", "eleteFile", "eleteNetPlayer", "esktop", "esktopBuffer"
Data "im", "isableGadget", "isableMenu", "isableToolBarItem", "ottedIP", "rawBlock", "rawBlockRect", "rawImage"
Data "rawImageRect", "rawMovie"
.e
Data 24
Data "ach", "lse", "lse If", "lseIf", "nableGadget", "nableMenu", "nableToolBarItem", "nd", "nd Function", "ndGraphics"
Data "nd If", "nd Select", "nd Type", "ndIf", "of", "ventData", "ventID", "ventSource", "ventX", "ventY", "xecFile", "xit", "xp"
Data "xpandTreeViewNode"
.f
Data 32
Data "alse", "ield", "ilePos", "ileSize", "ileType", "irst", "lip", "lipCanvas", "loat", "loor", "lushEvents", "lushJoy"
Data "lushKeys", "lushMouse", "ontAscend", "ontDescend", "ontHeight", "ontName", "ontSize", "ontStyle", "ontWidth", "or", "orever"
Data "reeBank", "reeFont", "reeGadget", "reeImage", "reeSound", "reeTimer", "reeTreeViewNode", "rontBuffer", "unction"
.g
Data 28
Data "adgetHeight", "adgetItemText", "adgetText", "adgetWidth", "adgetX", "adgetY", "ammaBlue", "ammaGreen", "ammaRed", "etColor"
Data "etEnv", "etJoy", "etKey", "etMouse", "fxDriverName", "fxModeDepth", "fxModeExists", "fxModeHeight", "fxModeWidth"
Data "lobal", "osub", "oto", "rabImage", "raphics", "raphicsBuffer", "raphicsDepth", "raphicsHeight", "raphicsWidth"
.h
Data 14
Data "andleImage", "ex", "ideGadget", "idePointer", "ostIP", "ostNetGame", "otKeyEvent", "tmlViewBack", "tmlViewCurrentURL"
Data "tmlViewEventURL", "tmlViewForward", "tmlViewGo", "tmlViewRun", "tmlViewStatus"
.i
Data 17
Data "f", "mageBuffer", "mageHeight", "mageRectCollide", "mageRectOverlap", "magesCollide", "magesOverlap"
Data "mageWidth", "mageXHandle", "mageYHandle", "nclude", "nput", "nsert", "nsertGadgetItem", "nsertTreeViewNode", "nstr", "nt"
.j
Data 18
Data "oinNetGame", "oyDown", "oyHat", "oyHit", "oyPitch", "oyRoll", "oyType", "oyU", "oyUDir", "oyV"
Data "oyVDir", "oyX", "oyXDir", "oyY", "oyYaw", "oyYDir", "oyZ", "oyZDir"
.k
Data 2
Data "eyDown", "eyHit"
.l
Data 20
Data "ast", "eft", "en", "ine", "oadAnimImage", "oadBuffer", "oadFont", "oadImage", "oadSound", "ocal"
Data "ocate", "ockBuffer", "ockedFormat", "ockedPitch", "ockedPixels", "og", "og10", "oopSound", "ower", "Set"
.m
Data 23
Data "askImage", "aximizeWindow", "enuChecked", "enuEnabled", "id", "idHandle", "illiSecs", "inimizeWindow", "od"
Data "odifyGadgetItem", "ouseDown", "ouseHit", "ouseWait", "ouseX", "ouseXSpeed", "ouseY", "ouseYSpeed", "ouseZ"
Data "ouseZSpeed", "oveMouse", "ovieHeight", "oviePlaying", "ovieWidth"
.n
Data 12
Data "etMsgData", "etMsgFrom", "etMsgTo", "etMsgType", "etPlayerLocal", "etPlayerName", "ew", "ext", "extFile"
Data "ot", "otify", "ull"
.o
Data 6
Data "penFile", "penMovie", "penTCPStream", "r", "rigin", "val"
.p
Data 17
Data "auseChannel", "eekByte", "eekEvent", "eekFloat", "eekInt", "eekShort", "i", "layCDTrack", "layMusic", "laySound"
Data "lot", "okeByte", "okeFloat", "okeInt", "okeShort", "rint", "roceed"
.q
Data 1
Data "ueryObject"
.r
Data 39
Data "and", "ead", "eadAvail", "eadByte", "eadBytes", "eadDir", "eadFile", "eadFloat", "eadInt", "eadLine"
Data "eadPixel", "eadPixelFast", "eadShort", "eadString", "ect", "ectsOverlap", "ecvNetMsg", "ecvUDPMsg", "emoveGadgetItem"
Data "epeat", "eplace", "equestColor", "equestDir", "equestedBlue", "equestedGreen", "equestedRed", "equestFile", "equestFont"
Data "esizeBank", "esizeImage", "estore", "esumeChannel", "eturn", "ight", "nd", "ndSeed", "otateImage", "Set", "untimeError"
.s
Data 58
Data "ar", "aveBuffer", "aveImage", "caleImage", "canLine", "eedRnd", "eekFile", "elect", "electedGadgetItem"
Data "electedTreeViewNode", "electGadgetItem", "electTreeViewNode", "endNetMsg", "endUDPMsg"
Data "etBuffer", "etButtonState", "etEnv", "etFont", "etGadgetFont", "etGadgetIconStrip", "etGadgetLayout", "etGadgetShape"
Data "etGadgetText", "etGamma", "etGfxDriver", "etMenuText", "etMinWindowSize", "etPanelColor", "etPanelImage", "etSliderRange"
Data "etSliderValue", "etStatusText", "etTextAreaColor", "etTextAreaFont", "etTextAreaTabs", "etTextAreaText", "etToolBarTips"
Data "gn", "hl", "howGadget", "howPointer", "hr", "in", "liderValue", "oundPan", "oundPitch", "oundVolume", "qr", "tartNetGame"
Data "tep", "top", "topChannel", "topNetGame", "tr", "tring", "tringHeight", "tringWidth", "ystemProperty"
.t
Data 19
Data "an", "CPStreamIP", "CPStreamPort", "CPTimeouts", "ext", "extAreaText", "extFieldText", "FormFilter", "FormImage"
Data "hen", "ileBlock", "ileImage", "o", "otalVidMem", "reeViewRoot", "reeViewNodeText", "rim", "rue", "ype"
.u
Data 12
Data "DPMsgIP", "DPMsgPort", "DPStreamIP", "DPStreamPort", "DPTimeouts", "ncheckMenu", "nlockBuffer", "ntil", "pdateGamma"
Data "pdateProgBar", "pdateWindowMenu", "pper"
.v
Data 2
Data "iewport", "Wait"
.w
Data 21
Data "aitEvent", "aitJoy", "aitKey", "aitMouse", "aitTimer", "end", "hile", "indowMaximized", "indowMenu", "indowMinimized"
Data "rite", "riteByte", "riteBytes", "riteFile", "riteFloat", "riteInt", "riteLine", "ritePixel", "ritePixelFast"
Data "riteShort", "riteString"
.x
Data 1
Data "or"
.none
Data 0
