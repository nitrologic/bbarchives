; ID: 1893
; Author: ninjarat
; Date: 2006-12-30 10:30:56
; Title: Config File Save / Load with XML
; Description: Uses Brucey's libxml wrapper to save and load a config file.  Easily adaptable code, and simple function interface for non-OOP guys.

Import BaH.libxml
Import BRL.Retro

'by Bill Whitacre (ninjarat)

Type TSettings
	Field swdth,shght
	Field sndflag
	Field mscflag,mscvol#
	Field sfxflag,sfxvol#
	
	Method setToDefault()
		swdth=640
		shght=480
		sndflag=1
		mscflag=1
		sfxflag=1
		mscvol=1#
		sfxvol=1#
	End Method
	
	Method createFromXML(doc:TxmlDoc)
		Local rootnode:TxmlNode
		Local children:TList=New TList
		rootnode=doc.getRootElement()
		If Lower(rootnode.getName())<>"config" Then
			RuntimeError "Not CONFIG file; failed!"
			Return
		End If
		children=rootnode.getChildren()
		If children.count()<2 Then
			RuntimeError "Not enough DATA NODES; failed!"
			Return
		Else If children.count()>2 Then
			RuntimeError "Too many DATA NODES; failed!"
			Return
		End If
		For j:TxmlNode=EachIn children
			Select j.getName()
			Case "screenmode"
				swdth=j.getAttribute("width").toInt()
				shght=j.getAttribute("height").toInt()
			Case "sound"
				op$=j.getAttribute("enableall")
				If Lower(op)="yes" Then sndflag=1 Else If Lower(op)="no" Then sndflag=0
				Local sndchildren:TList=New TList
				sndchildren=j.getChildren()
				If sndchildren.count()<2 Then
					RuntimeError "Not enough DATA NODES; failed!"
					Return
				Else If sndchildren.count()>2 Then
					RuntimeError "Too many DATA NODES; failed!"
					Return
				End If
				For k:TxmlNode=EachIn sndchildren
					currvol#=k.getAttribute("volume").toFloat()
					mode$=k.getAttribute("enable")
					If Lower(mode)="yes" Then currmode=1 Else If Lower(mode)="no" Then currmode=0
					If sndflag And currmode Then currmode=1 Else currmode=0
					Select k.getName()
					Case "music"
						mscflag=currmode
						mscvol=currvol
					Case "soundfx"
						sfxflag=currmode
						sfxvol=currvol
					End Select
				Next
			Default
				RuntimeError "Unidentifiable DATA NODE; failed!"
			End Select
		Next
		xmlCleanupParser()
	End Method
	
	Method saveToXML()
		Local cfgdoc:TxmlDoc=TxmlDoc.newDoc("1.0")
		
		Local rootnode:TxmlNode=TxmlNode.newNode("config")
		cfgdoc.setRootElement(rootnode)
		
		Local resnode:TxmlNode=rootnode.addChild("screenmode")
		resnode.addAttribute("width",swdth)
		resnode.addAttribute("height",shght)
		
		Local sndnode:TxmlNode=rootnode.addChild("sound")
		If sndflag=1 Then
			sndnode.addAttribute("enableall","yes")
		Else
			sndnode.addAttribute("enableall","no")
		End If
		
		Local musicnode:TxmlNode=sndnode.addChild("music")
		musicnode.addAttribute("volume",1#)
		If mscflag=1 Then
			musicnode.addAttribute("enable","yes")
		Else
			musicnode.addAttribute("enable","no")
		End If
		
		Local sfxnode:TxmlNode=sndnode.addChild("soundfx")
		sfxnode.addAttribute("volume",1#)
		If sfxflag=1 Then
			sfxnode.addAttribute("enable","yes")
		Else
			sfxnode.addAttribute("enable","no")
		End If
		
		cfgdoc.setCompressMode(9)
		cfgdoc.saveFormatFile("config",True)
		
		xmlCleanupParser()
	End Method
	
	Method toString$()
		Return "ScreenResolution: "+swdth+", "+shght+"~nAudioEnabled: "+sndflag+"~nMusic: "+mscflag+"   Volume: "+Int(mscvol*100)+"%~nSound: "+sfxflag+"   Volume: "+Int(sfxvol*100)+"%"
	End Method
End Type

Function saveConfig(settings:TSettings)
	settings.saveToXML()
End Function

Function loadConfig:TSettings(settings:TSettings Var)
	settings.createFromXML(getdoc("config"))
End Function

Function defaultConfig(settings:TSettings Var)
	settings.setToDefault()
End Function

Private
	Function getdoc:TxmlDoc(docname:String)
#tryagain
		Local doc:TxmlDoc = TxmlDoc.parseFile(docname)
		
		If doc = Null Then
			If tryingagain=1 Then Return Null
			Local settings:TSettings=New TSettings
			settings.setToDefault()
			saveConfig(settings)
			tryingagain=1
			Goto tryagain
		End If
	
		Return doc
	End Function
