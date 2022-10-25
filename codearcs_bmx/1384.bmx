; ID: 1384
; Author: Perturbatio
; Date: 2005-05-24 11:01:00
; Title: HTML/XML Parser
; Description: Seperates the supplied file into tags, parameters, values and text

Strict
Rem
bbdoc: Pert.HTML
End Rem
Module Pert.html
ModuleInfo "Module: Perturbatio's HTML Mod"
ModuleInfo "Version: 0.01"
ModuleInfo "Author: Kris Kelly (Perturbatio), portions converted from delphi source, author unknown"
ModuleInfo "License: Public Domain"
Import Pub.stdc
Import BRL.FileSystem
Import BRL.LinkedList


Global Entities:String[][] =[["&quot;",  "&#34;"],["&amp;",   "&#38;"],["<",    "&#60;"],[">",    "&#62;"],["&nbsp;",  "&#160;"],["&iexcl;", "&#161;"],["&cent;",  "&#162;"],["&pound;", "&#163;"],["&curren;","&#164;"],["&yen;",   "&#165;"],["&brvbar;","&#166;"],["&sect;",  "&#167;"],["&uml;",   "&#168;"],["&copy;",  "&#169;"],["&ordf;",  "&#170;"],["&laquo;", "&#171;"],["&not;",   "&#172;"],["&shy;",   "&#173;"],["&reg;",   "&#174;"],["&macr;",  "&#175;"],["&deg;",   "&#176;"],["&plusmn;","&#177;"],["&sup2;",  "&#178;"],["&sup3;",  "&#179;"],["&acute;", "&#180;"],["&micro;", "&#181;"],["&para;",  "&#182;"],["&middot;","&#183;"],["&cedil;", "&#184;"],["&sup1;",  "&#185;"],["&ordm;",  "&#186;"],["&raquo;", "&#187;"],["&frac14;","&#188;"],["&frac12;","&#189;"],["&frac34;","&#190;"],["&iquest;","&#191;"],["&Agrave;","&#192;"],["&Aacute;","&#193;"],["&Acirc;", "&#194;"],["&Atilde;","&#195;"],["&Auml;",  "&#196;"],["&Aring;", "&#197;"],["&AElig;", "&#198;"],["&Ccedil;","&#199;"],["&Egrave;","&#200;"],["&Eacute;","&#201;"],["&Ecirc;", "&#202;"],["&Euml;",  "&#203;"],["&Igrave;","&#204;"],["&Iacute;","&#205;"],["&Icirc;", "&#206;"],["&Iuml;",  "&#207;"],["&ETH;",   "&#208;"],["&Ntilde;","&#209;"],["&Ograve;","&#210;"],["&Oacute;","&#211;"],["&Ocirc;", "&#212;"],["&Otilde;","&#213;"],["&Ouml;",  "&#214;"],["&times;", "&#215;"],["&Oslash;","&#216;"],["&Ugrave;","&#217;"],["&Uacute;","&#218;"],["&Ucirc;", "&#219;"],["&Uuml;",  "&#220;"],["&Yacute;","&#221;"],["&THORN;", "&#222;"],["&szlig;", "&#223;"],["&agrave;","&#224;"],["&aacute;","&#225;"],["&acirc;", "&#226;"],["&atilde;","&#227;"],["&auml;",  "&#228;"],["&aring;", "&#229;"],["&aelig;", "&#230;"],["&ccedil;","&#231;"],["&egrave;","&#232;"],["&eacute;","&#233;"],["&ecirc;", "&#234;"],["&euml;",  "&#235;"],["&igrave;","&#236;"],["&iacute;","&#237;"],["&icirc;", "&#238;"],["&iuml;",  "&#239;"],["&eth;",   "&#240;"],["&ntilde;","&#241;"],["&ograve;","&#242;"],["&oacute;","&#243;"],["&ocirc;", "&#244;"],["&otilde;","&#245;"],["&ouml;",  "&#246;"],["&divide;","&#247;"],["&oslash;","&#248;"],["&ugrave;","&#249;"],["&uacute;","&#250;"],["&ucirc;", "&#251;"],["&uuml;",  "&#252;"],["&yacute;","&#253;"],["&thorn;", "&#254;"],["&yuml;",  "&#255;"]];

Global CharSet:String[] = [" ","!","~q","#","$","%","&","(",")"..
,"*","+",",","-",".","/","0","1","2","3","4","5","6","7","8"..
,"9",":",";","<","=",">","?","@","A","B","C","D","E","F","G"..
,"H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V"..
,"W","X","Y","Z","[","\","]","^","_","`","a","b","c","d","e"..
,"f","g","h","i","j","k","l","m","n","o","p","q","r","s","t"..
,"u","v","w","x","y","z","{","|","}","","_","_","~q","ƒ"..
,".","?","?","^","%","S","<","O","_"..
,"Z","_","_","","-","-","~~","T","s",">","o"..
,"_","z","Y"," ","¡","¢","£","¤","¥","¦","§","¨","©","ª","«"..
,"¬","­","®","¯","°","±","²","³","´","µ","¶","·","¸","¹","º"..
,"»","¼","½","¾","¿","À","Á","Â","Ã","Ä","Å","Æ","Ç","È","É"..
,"Ê","Ë","Ì","Í","Î","Ï","Ð","Ñ","Ò","Ó","Ô","Õ","Ö","×","Ø"..
,"Ù","Ú","Û","Ü","Ý","Þ","ß","à","á","â","ã","ä","å","æ","ç"..
,"è","é","ê","ë","ì","í","î","ï","ð","ñ","ò","ó","ô","õ","ö"..
,"÷","ø","ù","ú","û","ü","ý","þ","ÿ"]
Rem
bbdoc: THTMLParam type
End Rem
Type THTMLParam

	Field fRaw:String
	Field fKey:String
	Field fValue:String
	
	Rem
	bbdoc: Sets a key passed as a string i.e. "color=~qBlack~q"
	End Rem
	Method SetKey(Key:String)
		fValue = ""
		fRaw = Key
		'DebugStop
		If Key.Find("=")>-1 Then
			fValue = Key
			'fValue = fValue[0..Key.Find("=")]
			fValue = fValue[Key.Find("=")+1..]
			key = Key[0..Key.Find("=")]
			
			If Len(fValue)>1 Then
				If (fValue[0..1] = "~q") And (fValue[Len(fValue)-1..]="~q") Then
					fValue = fValue[1..Len(fValue)-1]
				EndIf
			EndIf
		EndIf
		
		fKey = Key.ToUpper()
	End Method
	Rem
	bbdoc: Creates a new THTMLParam instance
	About: Usage: THTMLParam.Create()
	End Rem
	Function Create:THTMLParam()
		Local tempHTMLParam:THTMLParam = New THTMLParam
		Return tempHTMLParam
	End Function
	Rem
	bbdoc: Destroy function frees the passed type and flushes the memory
	about: pass an instance of the THTMLParam type, no return value<br> usage:THTMLParam.Destroy(HTMLParam)
	End Rem
	Function Destroy(HTMLParam : THTMLParam Var)
		HTMLParam = Null
		
	End Function
End Type

Rem
bbdoc: THTMLtag type
End Rem
Type THTMLTag

	Field fName:String
	Field fRaw:String
	Field Params:TList
	Rem
	bbdoc: Sets the tag name (should not be called directly)
	End Rem
	Method SetName(Name:String)
		Local Tag : String
		Local param : String
		Local HTMLParam : THTMLParam
		Local isQuote : Int
		
		fRaw = Name
		Params.Clear()
		'DebugStop
		
		While (Len(Name)>0) And (Name[0..1] <> " ")
			Tag = Tag + Name[0..1]
			Name = Name[1..]
		Wend

		fName = Tag.ToUpper()
		
		While (Len(Name)>0)
			param = ""
			isQuote = False
			While (Len(Name)>0) And ( Not ((Name[0..1]=" ") And (isQuote=False)))
				If Name[0..1] = "~q" Then IsQuote = Not(IsQuote)
					param = param + Name[0..1]
					Name = Name[1..]
			Wend
				
				If (Len(Name)>0) And (Name[0..1]=" ") Then Name = Name[1..]
				If (param <> "") Then
					HTMLParam = THTMLParam.Create()
					HTMLParam.SetKey(param)
					params.AddLast(HTMLParam)
				EndIf
			
		Wend
	GCCollect()
	End Method
	
	Method GetName()
	End Method
	Rem
	bbdoc: returns the raw HTML code for this tag
	End Rem
	Method GetRaw:String()
		Return fRaw
	End Method
		
	Function Create:THTMLTag()
		Local tempHTMLTag:THTMLTag = New THTMLTag
			tempHTMLTag.Params = New TList
		Return tempHTMLTag
	End Function
	
	Function Destroy(TAG:THTMLTag)
		TAG.Params.Clear()
		TAG = Null
		
	End Function

End Type

Rem
bbdoc: THTMLText type
about: Contains any text blocks within the supplied document
End Rem
Type THTMLText

	Field fLine:String
	Field fRawLine:String

	Method SetLine(Line:String)
		Local j : Int
		Local i : Int
		Local Entity : String
		Local isEntity : Int
		Local EnLen : Int
		Local EnPos : Int
		Local d : Int
		Local c : Int
		
		fRawLine = Line
		
		Line = Line.Replace(Chr(10), " ")
		Line = Line.Replace("  ", " ")
		
		i = 0
		isEntity = False
		EnPos = -1

		While i <= Len(Line)
			If Line[i..i+1] = "&" Then 
				EnPos = 1
				isEntity = True
				Entity = ""
			EndIf
			If isEntity Then Entity = Entity+Line[i..i+1]
			
			If isEntity Then
				If (Line[i..i+1]=";") Or (Line[i..i+1]=" ") Then
					EnLen = Len(Entity)
					
					If (EnLen > 2) And (Entity[1..2] = "#") Then
						Entity = Entity[..EnLen-1] 'remove semicolon
						Entity = Entity[2..] 'remove &#
						If Entity[0..1].ToUpper()="X" Then Entity = "$" + Entity[1..]
						If (Len(Entity)<=3) Then 
							d = Int(entity)
							If d <> Null Then
								Line = Line[0..EnPos]+Line[EnPos+EnLen..]
								StrInsert(CharSet[d], Line, EnPos)
								i = EnPos
							EndIf
						EndIf
					Else
					
						j = 1
					
						While (j<=100)
							If Entity = (Entities[j][1]) Then
								Line = Line[0..EnPos]+Line[EnPos+EnLen..]
								StrInsert(Line, Entities[j][2], EnPos)
								j = 102
							EndIf
							j:+1
						Wend
						
						If j=103 Then 
							i = enPos-1
						Else 
							i = EnPos
						EndIf
						
						
					EndIf
					
				EndIf
				IsEntity=False
			EndIf
			i:+1
		Wend
	fLine=Line;
	
	End Method
	Rem
	bbdoc: returns the raw HTML code for this text portion
	End Rem
	Method GetRaw:String()
		Return fRawLine
	End Method
	Rem
	bbdoc: returns a new THTMLText instance
	End Rem
	Function Create:THTMLText()
		Local tempHTMLText : THTMLText = New THTMLText
		
		Return tempHTMLText
	End Function
	Rem
	bbdoc: Destroy a THTMLText instance
	End Rem
	Function Destroy(HTMLText : THTMLText Var)
		HTMLText = Null
		
	End Function

End Type

Rem
bbdoc: THTMLParser Type
End Rem
Type THTMLParser

	Field Text:String
	Field Tag:String
	Field isTag:Int
	Field parsed:TList
	Field Lines:TList

	Method AddText()
		Local HTMLText:THTMLText
		If Not isTag Then
			If Text <> "" Then
				
				HTMLText = THTMLText.Create()
				HTMLText.SetLine(Text)
				Text = ""
				parsed.AddLast(HTMLText)
				
			EndIf
		EndIf
		
	End Method
	
	Rem
	bbdoc: Pass a filename to load (can specify a url by prefixing with "http::" )
	End Rem
	Method LoadFile(FileName:String)
		Lines.Clear()
		Local HTMLFile:TStream 
		Try
			HTMLFile = ReadStream(FileName)
			While Not Eof(HTMLFile)
				Lines.AddLast(ReadLine(HTMLFile))
			Wend
		Catch a$
			CloseStream(HTMLFile)
			RuntimeError(a$)
		EndTry
		CloseStream(HTMLFile)
		
	End Method
	
	Method AddTag()
		Local HTMLTag:THTMLTag;

		isTag = False
		HTMLTag = THTMLTag.Create()
		
		HTMLTag.SetName(Tag)
		
		Tag = ""
		parsed.AddLast(HTMLTag)
		
	End Method

	
	Function Create:THTMLParser()
		Local tempParser : THTMLParser = New THTMLParser
			'initialize the lists
			tempParser.parsed:TList = New TList
			tempParser.Lines:TList = New TList
		Return tempParser
	End Function
	

	Function Destroy(parser:THTMLParser Var)
		parser.parsed.clear()
		parser.lines.clear()
		parser.parsed = Null
		parser.lines = Null
		parser = Null
		
	End Function

	Rem
	bbdoc: Call execute to parse the file (NOTE: You MUST call LoadFile first)
	End Rem
	Method Execute()
		Local s:String
		Text = ""
		Tag = ""
		isTag =False;
		
		For s = EachIn Lines
			While Len(s) > 0
				If s[0..1] = "<" Then 
					AddText()
					isTag=True
				Else If s[0..1] = ">" Then 
					AddTag()
				Else If isTag Then 
					Tag = Tag + s[0..1]
				Else 
					Text = Text + s[0..1]
			    End If
				
				s=s[1..] 'slice the first character off
				
			Wend
			
				If (Not isTag) And (Text <> "") Then Text = Text + Chr(10)
				
		Next
					
		If (isTag) And (Tag <> "") Then AddTag()
		If (Not isTag) And (Text <> "") Then AddText()
		
	End Method

End Type

Rem
bbdoc: insert inString into SourceStr at the specified index
End Rem
Function StrInsert(SourceStr:String Var, inString:String, Index:Int)
	SourceStr = SourceStr[..Index] + inString + SourceStr[Index..]
End Function


'test
Rem
Print MemAlloced()

Local myParser : THTMLParser = THTMLParser.Create()

myParser.LoadFile("http::www.blitzbasic.com")

myParser.Execute()

For Local a:Object = EachIn myParser.parsed
	If THTMLTag(a) Then
		Print THTMLTag(a).fName
		For Local b:THTMLParam = EachIn THTMLTag(a).params
			Print b.fKey
			Print b.fValue
		Next
	Else
		Print THTMLText(a).fLine
	EndIf
Next

myParser.Destroy(myParser)
FlushMem
Print MemAlloced()
End
EndRem
