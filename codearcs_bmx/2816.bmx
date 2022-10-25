; ID: 2816
; Author: JoshK
; Date: 2011-01-21 17:49:51
; Title: CodeArea Gadget
; Description: Text editor with undo/redo and syntax highlighting for BlitzMax, Lua, and C

SuperStrict

Import maxgui.drivers
Import maxgui.proxygadgets

'Codearea style constants
Const CODEAREA_SYNTAXHIGHLIGHTING:Int=1
Const CODEAREA_CANUNDO:Int=2
Const CODEAREA_CORRECTCASE:Int=4
Const CODEAREA_READONLY:Int=8
Const CODEAREA_DEFAULT:Int=CODEAREA_SYNTAXHIGHLIGHTING|CODEAREA_CANUNDO|CODEAREA_CORRECTCASE

'Codearea format constants
Const CODEAREA_PLAIN:Int=0
Const CODEAREA_STRING:Int=1
Const CODEAREA_COMMENT:Int=2
Const CODEAREA_KEYWORD:Int=3
Const CODEAREA_PREPROCESSOR:Int=4

'CodeArea language constants
Const CODEAREA_C:Int=0
Const CODEAREA_LUA:Int=1
Const CODEAREA_BMX:Int=2

Private

Type TAction
	
	Field add:String
	Field addpos:Int
	Field addlen:Int
	Field remove:String
	Field removepos:Int
	Field removelen:Int
	Field beforepos:Int
	Field beforelen:Int
	Field afterpos:Int
	Field afterlen:Int
	
	Method Undo(codearea:TCodeArea)
		Local n:Int
		
		SetTextAreaText codearea,remove,addpos,addlen
		SelectTextAreaText codearea,beforepos,beforelen
		If (CODEAREA_SYNTAXHIGHLIGHTING & codearea.style)
			LockTextArea codearea.textarea
			For n=TextAreaLine(codearea.textarea,beforepos) To TextAreaLine(codearea.textarea,beforepos+beforelen)
				If codearea.FormatLine(n) Exit
			Next
			UnlockTextArea codearea.textarea
		EndIf
		EmitEvent CreateEvent(EVENT_GADGETSELECT,codearea,addpos-remove.length)
	EndMethod
	
	Method Redo(codearea:TCodeArea)
		Local n:Int

		SetTextAreaText codearea,add,removepos,removelen
		SelectTextAreaText codearea,afterpos,afterlen
		If (CODEAREA_SYNTAXHIGHLIGHTING & codearea.style)
			LockTextArea codearea.textarea
			For n=TextAreaLine(codearea.textarea,afterpos) To TextAreaLine(codearea.textarea,afterpos+afterlen)
				If codearea.FormatLine(n) Exit
			Next
			UnlockTextArea codearea.textarea
		EndIf
		EmitEvent CreateEvent(EVENT_GADGETSELECT,codearea,removepos+add.length)
	EndMethod
	
EndType

Type TFormat
	
	Field r:Int
	Field g:Int
	Field b:Int
	Field style:Int
	
	Function Create:TFormat(r:Int,g:Int,b:Int,style:Int)
		Local format:TFormat=New TFormat
		format.r=r
		format.g=g
		format.b=b
		format.style=style
		Return format
	EndFunction
	
EndType

Public

Type TCodeArea Extends TProxyGadget
	
	Global dummywindow:TGadget' used to hold dummy textarea
	Global dummytextarea:TGadget' used for paste operations
	Global maxundolevels:Int=0' set to 0 to disable limit
	
	Field textarea:TGadget
	Field selpos:Int
	Field sellen:Int
	Field text:String
	Field undoposition:Int=-1
	Field actions:TAction[]
	Field format:TFormat[6]
	Field needsreformat:Int
	Field locked:Int
	Field keywords:TMap=New TMap
	Field token_comment:String="//"
	Field token_multilinecommentbegin:String="/*"
	Field token_multilinecommentend:String="*/"
	Field token_string:String="~q"
	Field token_string2:String' Lua uses multiple string tokens
	Field token_preprocessor:String="#"
	Field multilinecommentstyle:Int
	Field lineincommentblock:Byte[]
	Field multilinecommentschanged:Int
	
	Method New()
		format[CODEAREA_PLAIN]=TFormat.Create(0,0,0,0)
		format[CODEAREA_STRING]=TFormat.Create(128,0,128,0)
		format[CODEAREA_COMMENT]=TFormat.Create(0,128,0,0)
		format[CODEAREA_PREPROCESSOR]=TFormat.Create(255,0,0,0)
		format[CODEAREA_KEYWORD]=TFormat.Create(0,0,255,0)
	EndMethod
	
	Method Cleanup()
		RemoveHook EmitEventHook,EventHook,Self
	EndMethod
	
	'---------------------------------------------------------------------------------------------
	'Syntax highlighting
	'---------------------------------------------------------------------------------------------	
	Method SetLanguage(language:Int)
		Select language
		
		Case CODEAREA_C
			token_comment="//"
			token_multilinecommentbegin="/*"
			token_multilinecommentend="*/"
			token_string="~q"
			token_string2=""
			token_preprocessor="#"			
			multilinecommentstyle=0
		
		Case CODEAREA_LUA
			token_comment="--"
			token_multilinecommentbegin="--[["
			token_multilinecommentend="]]--"
			token_string="~q"
			token_string2="'"
			token_preprocessor=""
			multilinecommentstyle=0
		
		Case CODEAREA_BMX
			token_comment="'"
			token_multilinecommentbegin="Rem"
			token_multilinecommentend="EndRem"
			token_string="~q"
			token_string2=""
			token_preprocessor="?"
			multilinecommentstyle=1
		
		EndSelect
		
		FormatAll()
	EndMethod
	
	Method LockText()
		locked=True
		textarea.LockText()
	EndMethod
	
	Method UnlockText()
		textarea.UnlockText()
		locked=False
		If needsreformat FormatAll()
	EndMethod
	
	Method SetFont(font:TGUIFont)
		Local n:Int
		
		format[CODEAREA_PLAIN].style=FontStyle(font)
		textarea.SetFont(font)
		FormatAll()
	EndMethod
	
	Method SetTextColor(r:Int,g:Int,b:Int)
		Local n:Int
		
		format[CODEAREA_PLAIN].r=r
		format[CODEAREA_PLAIN].g=g
		format[CODEAREA_PLAIN].b=b
		FormatAll()
	EndMethod
	
	Method AddKeyword(word:String)
		keywords.insert(word.tolower(),word)
	EndMethod
	
	Method ClearKeywords()
		keywords.Clear()
	EndMethod
	
	Method KeywordExists:Int(word:String)
		Local keyword:String
		keyword=String(keywords.valueforkey(word.tolower()))
		If keyword
			If Not (CODEAREA_CORRECTCASE & style)
				If keyword=word
					Return True
				Else
					Return False
				EndIf
			Else
				Return True
			EndIf
		Else
			Return False
		EndIf
	EndMethod
	
	Method SetFormat(element:Int,r:Int,g:Int,b:Int,style:Int)
		format[element]=TFormat.Create(r,g,b,style)
		FormatAll()
	EndMethod
	
	Field dontupdatemultilinecomments:Int
	
	Method FormatAll()
		Print "FORMATALL"
		Local n:Int
		Local multilinecommentbegun:Int
		For n=0 To lineincommentblock.length-1
			lineincommentblock[n]=0
		Next
		
		dontupdatemultilinecomments=True
		If locked
			needsreformat=True
		Else
			LockTextArea textarea
			For n=0 To TextAreaLen(textarea,TEXTAREA_LINES)-1
				If FormatLine(n) Exit
			Next
			UnlockTextArea textarea
			needsreformat=False			
		EndIf
		dontupdatemultilinecomments=False
	EndMethod
	
	Method CharacterInQuotes:Int(s:String,c:Int)
		Local n:Int
		Local char:String
		Local stringopen:Int
		Local string2open:Int
		Local stringopenpos:Int
		Local string2openpos:Int
		
		For n=0 To c
			char=Chr(s[n])
			Select char
			Case token_string
				stringopen=Not stringopen
				stringopenpos=n
			Case token_string2
				If token_string2<>token_string
					string2open=Not string2open
					string2openpos=n
				EndIf
			EndSelect
		Next
		If stringopen
			If s.Find(token_string,stringopenpos+1)>-1 Return True
		EndIf
		If string2open
			If s.Find(token_string2,string2openpos+1)>-1 Return True
		EndIf
	EndMethod
	
	Rem
	Method FormatMultiLineComments()
		Local p0:Int=-1,p1:Int,s:String
		
		s=TextAreaText(textarea)
		Repeat
			p0=s.Find(token_multilinecommentblockbegin,p0+1)
			If p0>-1
				If Not CharacterInQuotes(s,p0)
					p1=s.Find(token_multilinecommentblockend,p0+1)
					If p1>-1
						If Not CharacterInQuotes(s,p0)
							FormatTextAreaText textarea,format[FORMAT_COMMENT].r,format[FORMAT_COMMENT].g,format[FORMAT_COMMENT].b,format[FORMAT_COMMENT].style,p0,p1-p0
						EndIf
					Else
						Exit
					EndIf
				EndIf
			Else
				Exit
			EndIf
		Forever
		
	EndMethod
	EndRem
	
	Method FormatLine:Int(line:Int,multilinecommentbegun:Int=False)
		Local s:String=TextAreaText(textarea,line,1,TEXTAREA_LINES)
		Local pos:Int,p2:Int
		Local l:Int
		Local word:String
		Local c:String
		Local stringopen:Int
		Local n:Int
		Local word2:String
		Local stringreplacement:String
		Local commentbegun:Int
		Local multilinecommentstate:Int
		
		'Print line+", "+TextAreaLen(textarea,TEXTAREA_LINES)
		'If line>TextAreaLen(textarea,TEXTAREA_LINES) Return False
		
		
		If line>lineincommentblock.length-1
			lineincommentblock=lineincommentblock[..line+1]
		EndIf

		multilinecommentstate=lineincommentblock[line]

		
		'If Not multilinecommentbegun
		'	If lineincommentblock[line]>1 multilinecommentbegun=True
		'EndIf
		
		'If previous line is commented, so is this one
		If line>0
			If lineincommentblock[line-1]=1 Or lineincommentblock[line-1]=2
				lineincommentblock[line]=2
			'Else
			'	lineincommentblock[line]=0
			EndIf
		EndIf
		
		If token_string2="" token_string2=token_string
		
		FormatTextAreaText textarea,format[CODEAREA_PLAIN].r,format[CODEAREA_PLAIN].g,format[CODEAREA_PLAIN].b,format[CODEAREA_PLAIN].style,line,1,TEXTAREA_LINES
		
		pos=-1
		
		'Remove multi-line comments

			'Rem
			If lineincommentblock[line]=2
				lineincommentblock[line]=0
				
				If multilinecommentstyle=1
					If (CODEAREA_CORRECTCASE & style)
						p2=s.tolower().Find(token_multilinecommentend.tolower(),pos+1)
					Else
						p2=s.Find(token_multilinecommentend,pos+1)
					EndIf
				Else
					p2=s.Find(token_multilinecommentend,pos+1)
				EndIf
				
				'Print s+", "+token_multilinecommentend
				'If p2>-1 End
				
				'BlitzMax does not allow multi-line comment begin/end tokens in the middle of a string
				If p2>-1
					If multilinecommentstyle=1
						If (CODEAREA_CORRECTCASE & style)
							If s.Trim().tolower()<>token_multilinecommentend.tolower()
								p2=-1
							Else
								If s.Trim()<>token_multilinecommentend SetTextAreaText textarea,token_multilinecommentend,TextAreaChar(textarea,line)+p2,token_multilinecommentend.length
							EndIf
						Else
							If s.Trim()<>token_multilinecommentend p2=-1
						EndIf
					EndIf
				EndIf
				
				If CharacterInQuotes(s,p2) p2=-1
				If p2>-1
					stringreplacement=""
					For n=pos To p2+token_multilinecommentend.length-1
						FormatTextAreaText textarea,format[CODEAREA_COMMENT].r,format[CODEAREA_COMMENT].g,format[CODEAREA_COMMENT].b,format[CODEAREA_COMMENT].style,TextAreaChar(textarea,line)+n,1,TEXTAREA_CHARS
						stringreplacement:+"|"
					Next
					lineincommentblock[line]=3' terminates comment block
					s=s[..pos]+stringreplacement+s[(p2+token_multilinecommentend.length-1)..]
				Else
					FormatTextAreaText textarea,format[CODEAREA_COMMENT].r,format[CODEAREA_COMMENT].g,format[CODEAREA_COMMENT].b,format[CODEAREA_COMMENT].style,line,1,TEXTAREA_LINES
					lineincommentblock[line]=2' inside comment block
					'Print s
					s=""
				EndIf
			EndIf
			'EndRem
			
			If lineincommentblock[line]=1 lineincommentblock[line]=0
			pos=-1
			Repeat
				If multilinecommentstyle=1
					If (CODEAREA_CORRECTCASE & style)
						pos=s.tolower().Find(token_multilinecommentbegin.tolower(),pos+1)
					Else
						pos=s.Find(token_multilinecommentbegin,pos+1)					
					EndIf
				Else
					pos=s.Find(token_multilinecommentbegin,pos+1)	
				EndIf
				
				'BlitzMax does not allow multi-line comment begin/end tokens in the middle of a string
				If pos>-1
					If multilinecommentstyle=1
						If (CODEAREA_CORRECTCASE & style)
							If s.Trim().tolower()<>token_multilinecommentbegin.tolower()
								pos=-1
							Else
								If s.Trim()<>token_multilinecommentbegin SetTextAreaText textarea,token_multilinecommentbegin,TextAreaChar(textarea,line)+pos,token_multilinecommentbegin.length
							EndIf
						Else
							If s.Trim()<>token_multilinecommentbegin pos=-1
						EndIf
					EndIf
				EndIf
				
				If pos>-1
					If Not CharacterInQuotes(s,pos)
						p2=s.Find(token_multilinecommentend,pos+1)
						If CharacterInQuotes(s,p2) p2=-1
						If p2>-1
							stringreplacement=""
							For n=pos To p2+token_multilinecommentend.length-1
								FormatTextAreaText textarea,format[CODEAREA_COMMENT].r,format[CODEAREA_COMMENT].g,format[CODEAREA_COMMENT].b,format[CODEAREA_COMMENT].style,TextAreaChar(textarea,line)+n,1,TEXTAREA_CHARS
								stringreplacement:+"|"
							Next
							s=s[..pos]+stringreplacement+s[(p2+token_multilinecommentend.length-1)..]
							lineincommentblock[line]=0' comment block is contained within this line
						Else
							l=s.length-pos'-token_multilinecommentbegin.length
							s=s[..pos]
							'Print l+", "+s
							'pos=TextAreaChar(textarea,line)+pos
							FormatTextAreaText textarea,format[CODEAREA_COMMENT].r,format[CODEAREA_COMMENT].g,format[CODEAREA_COMMENT].b,format[CODEAREA_COMMENT].style,TextAreaChar(textarea,line)+pos,l,TEXTAREA_CHARS
							lineincommentblock[line]=1' begins comment block
							'commentbegun=True
						EndIf
					EndIf
				Else
					Exit
				EndIf
			Forever
		
		'Remove trailing comments
		pos=-1
		Repeat
			pos=s.Find(token_comment,pos+1)
			If pos>-1
				If Not CharacterInQuotes(s,pos)
					l=s.length-pos+1-token_comment.length
					s=s[..pos]
					FormatTextAreaText textarea,format[CODEAREA_COMMENT].r,format[CODEAREA_COMMENT].g,format[CODEAREA_COMMENT].b,format[CODEAREA_COMMENT].style,TextAreaChar(textarea,line)+pos,l,TEXTAREA_CHARS
					Exit
				EndIf
			Else
				Exit
			EndIf
		Forever
		
		'Format strings
		pos=-1
		pos=s.Find(token_string,pos+1)
		While pos>-1
			p2=s.Find(token_string,pos+1)
			If p2>-1
				FormatTextAreaText textarea,format[CODEAREA_STRING].r,format[CODEAREA_STRING].g,format[CODEAREA_STRING].b,format[CODEAREA_STRING].style,TextAreaChar(textarea,line)+pos,p2-pos+1,TEXTAREA_CHARS
				'pos=s.Find(token_string,p2+1)
				stringreplacement=""
				For n=pos To p2
					stringreplacement:+"|"
				Next
				Local pl:Int=s.length
				s=s[..pos]+stringreplacement+s[(p2+1)..]
				'Notify s.length+", "+pl
			Else
				Exit
			EndIf
			pos=s.Find(token_string,pos+1)
		Wend
		
		'Lua uses two string tokens
		Rem
		If token_string2<>token_string
			pos=s.Find(token_string2)
			While pos>-1
				p2=s.Find(token_string2,pos+1)
				If p2>-1
					FormatTextAreaText textarea,format[CODEAREA_STRING].r,format[CODEAREA_STRING].g,format[CODEAREA_STRING].b,format[CODEAREA_STRING].style,TextAreaChar(textarea,line)+pos,p2-pos+1,TEXTAREA_CHARS
					pos=s.Find(token_string2,p2+1)
					stringreplacement=""
					For n=pos To p2
						stringreplacement:+"|"
					Next
					s=s[..pos]+stringreplacement+s[(p2+1)..]
				Else
					Exit
				EndIf			
			Wend		
		EndIf
		EndRem
				
		'Remove defines
		If token_preprocessor
			pos=s.Trim().Find(token_preprocessor)
			If pos=0
				pos=s.Find(token_preprocessor)
				l=s.length-pos+1-token_preprocessor.length
				s=s[..pos]
				pos=TextAreaChar(textarea,line)+pos
				FormatTextAreaText textarea,format[CODEAREA_PREPROCESSOR].r,format[CODEAREA_PREPROCESSOR].g,format[CODEAREA_PREPROCESSOR].b,format[CODEAREA_PREPROCESSOR].style,pos,l,TEXTAREA_CHARS
			EndIf
		EndIf
		
		'Add this in case this is the last line of the text
		If Right(s,1).Trim()<>"" s=s+"~n"
		
		'Format keywords
		For n=0 To s.length-1
			c=Chr(s[n])
			Select c.Trim()
			Case "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","0","1","2","3","4","5","6","7","8","9","_"
				If Not stringopen
					word:+c
				EndIf
			Case token_string,token_string2
				stringopen=CharacterInQuotes(s,n)
				'word=""
				'stringopen=Not stringopen
			Default
				If Not stringopen
					If word
						If KeywordExists(word)
							If (CODEAREA_CORRECTCASE & style)
								word2=String(keywords.valueforkey(word.tolower()))
								If word<>word2
									'Print word2
									SetTextAreaText textarea,word2,TextAreaChar(textarea,line)+n-word.length,word.length
								EndIf
							EndIf	
							FormatTextAreaText textarea,format[CODEAREA_KEYWORD].r,format[CODEAREA_KEYWORD].r,format[CODEAREA_KEYWORD].b,format[CODEAREA_KEYWORD].style,TextAreaChar(textarea,line)+n-word.length,word.length,TEXTAREA_CHARS	
						EndIf
					EndIf
				EndIf
				word=""
			EndSelect
		Next
		
		If multilinecommentstate<>lineincommentblock[line]
			If Not dontupdatemultilinecomments
				Print "CHANGE"
				FormatAll()
				Return True
			EndIf
		EndIf
		
		Return False
	EndMethod
	
	'---------------------------------------------------------------------------------------------
	'Undo/redo functionality
	'---------------------------------------------------------------------------------------------
	Method ClearUndos()
		If (CODEAREA_CANUNDO & style)
			actions=Null
			undoposition=-1
		EndIf
	EndMethod
	
	Method AddAction(action:TAction)
		If (CODEAREA_CANUNDO & style)
			undoposition:+1
			actions=actions[..undoposition+1]
			actions[undoposition]=action
			If maxundolevels>0
				If undoposition>maxundolevels
					undoposition:-1
					actions=actions[1..]
				EndIf
			EndIf
		EndIf
	EndMethod
	
	Method CanUndo:Int()
		If (CODEAREA_CANUNDO & style)
			If undoposition>-1 Return True		
		EndIf
	EndMethod
	
	Method CanRedo:Int()
		If (CODEAREA_CANUNDO & style)
			If undoposition<=actions.length-2 Return True
		EndIf
	EndMethod
	
	Method Undo()
		If CanUndo()
			actions[undoposition].Undo(Self)
			undoposition:-1
		EndIf
	EndMethod
	
	Method Redo()
		If CanRedo()
			actions[undoposition+1].Redo(Self)		
			undoposition:+1
		EndIf
	EndMethod
		
	'---------------------------------------------------------------------------------------------
	'Event handling
	'---------------------------------------------------------------------------------------------
	Method ProcessEvent:Int(event:TEvent)
		Select event.source
		Case Self,textarea
			Select event.id
			
			Case EVENT_GADGETACTION
				text=TextAreaText(textarea)
				selpos=TextAreaCursor(textarea,TEXTAREA_CHARS)
				sellen=TextAreaSelLen(textarea,TEXTAREA_CHARS)
				
			Case EVENT_GADGETSELECT
				selpos=TextAreaCursor(textarea,TEXTAREA_CHARS)
				sellen=TextAreaSelLen(textarea,TEXTAREA_CHARS)
			
			EndSelect
		EndSelect
		Return True
	EndMethod
	
	Method SetText(text:String)
		Local n:Int
		
		textarea.SetText(text)
		If (CODEAREA_SYNTAXHIGHLIGHTING & style)
			LockTextArea textarea
			For n=0 To TextAreaLen(textarea,TEXTAREA_LINES)
				If FormatLine(n) Exit
			Next
			UnlockTextArea textarea
		EndIf
	EndMethod
	
	Method ReplaceText(pos:Int,count:Int,text:String,units:Int)
		Local n:Int
		
		textarea.ReplaceText(pos,count,text,units)
		If (CODEAREA_SYNTAXHIGHLIGHTING & style)
			LockTextArea textarea
			If units=TEXTAREA_CHARS
				For n=TextAreaLine(textarea,pos) To TextAreaLine(textarea,pos+count)
					If FormatLine(n) Exit
				Next
			Else
				For n=pos To pos+count
					If FormatLine(n) Exit
				Next				
			EndIf
			UnlockTextArea textarea
		EndIf
	EndMethod	
	
	Method Activate(command:Int)
		Local n:Int
		Local clipboardtext:String
		
		Select command
		Case ACTIVATE_PASTE
			SetTextAreaText dummytextarea,""
			GadgetPaste(dummytextarea)
			ActivateGadget textarea
			clipboardtext=TextAreaText(dummytextarea)
			
			Update()
			Local action:TAction
			action=New TAction
			AddAction action
			action.add=clipboardtext
			action.addpos=selpos
			action.addlen=clipboardtext.length
			action.beforepos=selpos
			action.beforelen=sellen
			action.remove=Mid(text,selpos+1,sellen)
			action.removepos=selpos
			action.removelen=sellen
			action.afterpos=selpos+clipboardtext.length
			action.afterlen=0
			'action.afterpos=selpos
			'action.afterlen=clipboardtext.length	
			action.Redo(Self)
			EmitEvent CreateEvent(EVENT_GADGETACTION,Self)
			If (CODEAREA_SYNTAXHIGHLIGHTING & style)
				LockTextArea textarea
				'Notify TextAreaLine(textarea,action.afterpos)+", "+TextAreaLine(textarea,action.afterpos+action.afterlen)
				For n=TextAreaLine(textarea,action.beforepos) To TextAreaLine(textarea,action.beforepos+clipboardtext.length)
					If FormatLine(n) Exit
				Next
				UnlockTextArea textarea
			EndIf
			
		Case ACTIVATE_CUT
			If sellen
				textarea.activate(ACTIVATE_COPY)
				Filter(CreateEvent(EVENT_KEYCHAR,textarea,8))
			EndIf
		Default
			textarea.activate(command)
		EndSelect
	EndMethod
	
	Method Update()
		selpos=TextAreaCursor(textarea,TEXTAREA_CHARS)
		sellen=TextAreaSelLen(textarea,TEXTAREA_CHARS)
		text=TextAreaText(textarea)
	EndMethod
	
	Method Filter:Int(event:TEvent)
		Local action:TAction
		
		Select event.id
		Case EVENT_KEYDOWN
			Select event.data
			Case KEY_UP,KEY_DOWN,KEY_RIGHT,KEY_LEFT
				Return True
			EndSelect
		Case EVENT_KEYCHAR
			If MODIFIER_COMMAND & event.mods Return False
			If MODIFIER_OPTION & event.mods Return False
			Update()
			If event.data=8
				action=New TAction
				AddAction action
				action.add=""
				action.addlen=0
				action.beforepos=selpos
				action.beforelen=sellen
				If sellen=0
					action.addpos=selpos-1
					action.afterpos=selpos-1
					action.afterlen=0
					action.removelen=1
					action.removepos=selpos-1
					action.remove=Mid(text,1+selpos-action.removelen,action.removelen)
				Else
					action.addpos=selpos
					action.afterpos=selpos
					action.afterlen=0
					action.removepos=selpos
					action.removelen=sellen
					action.remove=Mid(text,1+selpos,sellen)
				EndIf
				action.Redo(Self)	
				EmitEvent CreateEvent(EVENT_GADGETACTION,Self)
				'action.Undo(self)' test to make sure this is reversable
			ElseIf event.data>31 And event.data<127 Or event.data=13
				action=New TAction
				AddAction action
				action.add=Chr(event.data)
				action.addpos=selpos
				action.addlen=1
				action.beforepos=selpos
				action.beforelen=sellen
				action.remove=Mid(text,selpos+1,sellen)
				action.removepos=selpos
				action.removelen=sellen
				action.afterpos=selpos+1
				action.afterlen=0
				action.Redo(Self)
				EmitEvent CreateEvent(EVENT_GADGETACTION,Self)
				'action.Undo(self)' test to make sure this is reversable
			EndIf
		EndSelect
		Return False
	EndMethod
	
	Function Callback:Int(event:TEvent,context:Object)
		Local codearea:TCodeArea=New TCodeArea
		codearea=TCodeArea(context)
		If codearea
			Return codearea.Filter(event)
		EndIf
	EndFunction
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent=TEvent(data)
		Local codearea:TCodeArea
		
		If event
			codearea=TCodeArea(context)
			If codearea
				If Not codearea.ProcessEvent(event) Return data'Null
			EndIf
		EndIf
		Return data
	EndFunction
	
	'---------------------------------------------------------------------------------------------
	'Creation
	'---------------------------------------------------------------------------------------------	
	Function Create:TCodeArea(x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=CODEAREA_DEFAULT)
		Local codearea:TCodeArea=New TCodeArea
		Local textareastyle:Int
		
		If Not dummywindow
			dummywindow=CreateWindow("",0,0,400,300,Null,WINDOW_HIDDEN)
			dummytextarea=CreateTextArea(0,0,300,200,dummywindow)
		EndIf
		codearea.style=style
		If (CODEAREA_READONLY & style) textareastyle=TEXTAREA_READONLY
		codearea.textarea=CreateTextArea(x,y,width,height,group,textareastyle)
		SetGadgetFilter codearea.textarea,Callback,codearea
		codearea.SetProxy(codearea.textarea)
		AddHook EmitEventHook,EventHook,codearea
		Return codearea
	EndFunction
	
EndType

'---------------------------------------------------------------------------------------------
'Procedural functions
'---------------------------------------------------------------------------------------------
Function CreateCodeArea:TCodeArea(x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=CODEAREA_DEFAULT)
	Return TCodeArea.Create(x,y,width,height,group,style)
EndFunction

Function AddCodeAreaKeyword(codearea:TCodeArea,word:String)
	codearea.AddKeyword(word)
EndFunction

Function SetCodeAreaFormat(codearea:TCodeArea,element:Int,r:Int,g:Int,b:Int,style:Int=0)
	codearea.SetFormat(element,r,g,b,style)
EndFunction

Function CodeAreaClearUndos(codearea:TCodeArea)
	codearea.ClearUndos()
EndFunction

Function CodeAreaCanUndo:Int(codearea:TCodeArea)
	Return codearea.CanUndo()
EndFunction

Function CodeAreaCanRedo:Int(codearea:TCodeArea)
	Return codearea.CanRedo()	
EndFunction

Function CodeAreaUndo(codearea:TCodeArea)
	codearea.Undo()
EndFunction

Function CodeAreaRedo(codearea:TCodeArea)
	codearea.Redo()	
EndFunction

Function SetCodeAreaLanguage(codearea:TCodeArea,language:Int)
	codearea.SetLanguage language
EndFunction


'---------------------------------------------------------------------------------------------
'Example
'---------------------------------------------------------------------------------------------


'Create window
Local window:TGadget=CreateWindow("CodeArea Example",0,0,1024,768,Null,WINDOW_DEFAULT|WINDOW_CENTER)

'Create menu
Local root:TGadget=CreateMenu("Edit",0,WindowMenu(window))
Local menu:TGadget[6]
menu[0]=CreateMenu("Undo",0,root,KEY_Z,MODIFIER_COMMAND)
menu[1]=CreateMenu("Redo",1,root,KEY_Z,MODIFIER_COMMAND|MODIFIER_OPTION)
CreateMenu("",0,root)
DisableMenu menu[0]
DisableMenu menu[1]
menu[2]=CreateMenu("Clear Undos",2,root)
CreateMenu("",0,root)
menu[3]=CreateMenu("Cut",3,root,KEY_X,MODIFIER_COMMAND)
menu[4]=CreateMenu("Copy",4,root,KEY_C,MODIFIER_COMMAND)
menu[5]=CreateMenu("Paste",5,root,KEY_V,MODIFIER_COMMAND)
DisableMenu menu[3]
DisableMenu menu[4]
UpdateWindowMenu window

'Create CodeArea gadget
Local codearea:TCodeArea=CreateCodearea(0,0,ClientWidth(window),ClientHeight(window),window)
SetGadgetLayout codearea,1,1,1,1

'Add some keywords
AddCodeAreaKeyword codearea,"Allegience"
AddCodeAreaKeyword codearea,"Flag"
AddCodeAreaKeyword codearea,"Justice"

'Adjust the syntax highlighting
SetCodeAreaFormat codearea,CODEAREA_KEYWORD,0,0,255,FONT_BOLD
SetCodeAreaFormat codearea,CODEAREA_COMMENT,0,128,0,FONT_ITALIC

'Set the language and add some text

'C/C++
SetCodeAreaLanguage codearea,CODEAREA_C
SetGadgetText codearea,"#define whatever 3~n~nI ~qpledge~q allegience to the flag of the United States of /*America~nAnd to the republic for which it stands~n~qOne nation~q, under God,*/ indivisible~nWith liberty and justice //for all.~n"

'Lua
'SetCodeAreaLanguage codearea,CODEAREA_LUA
'SetGadgetText codearea,"I ~qpledge~q allegience to 'the' flag of the United States of --[[America~nAnd to the republic for which it stands~n~qOne nation~q, under God,]]-- indivisible~nWith liberty and justice --for all.~n"

'BlitzMax
'SetCodeAreaLanguage codearea,CODEAREA_BMX
'SetGadgetText codearea,"?debug~nPrint ~qHello~q~n?~n~nI ~qpledge~q allegience To the flag of the United States of America~nRem~nAnd To the republic For which it stands~n~qOne nation~q, under God, indivisible~nEndRem~nWith liberty and justice 'for all.~n"


'Set the font (we can do this any time)
SetGadgetFont codearea,LoadGuiFont("Courier New",10)

'Set the gadget text and background colors (we can do this at any time)
SetGadgetColor codearea,240,240,240,True
SetGadgetColor codearea,0,0,0,False



While WaitEvent()
	Select EventID()
		
		Case EVENT_GADGETSELECT
			If TextAreaSelLen(codearea)
				EnableMenu menu[3]
				EnableMenu menu[4]
			Else
				DisableMenu menu[3]
				DisableMenu menu[4]
			EndIf
			UpdateWindowMenu window	
			
		Case EVENT_GADGETACTION
			If TextAreaSelLen(codearea)
				EnableMenu menu[3]
				EnableMenu menu[4]
			Else
				DisableMenu menu[3]
				DisableMenu menu[4]
			EndIf
			If codearea.canundo() EnableMenu menu[0] Else DisableMenu menu[0]
			If codearea.canredo() EnableMenu menu[1] Else DisableMenu menu[1]
			UpdateWindowMenu window			
			
		Case EVENT_MENUACTION
			Select EventData()
			Case 1 codearea.redo()
			Case 0 codearea.undo()
			Case 2 codearea.ClearUndos()
			Case 3 GadgetCut(codearea)
			Case 4 GadgetCopy(codearea)
			Case 5 GadgetPaste(codearea)
			EndSelect
			If codearea.canundo() EnableMenu menu[0] Else DisableMenu menu[0]
			If codearea.canredo() EnableMenu menu[1] Else DisableMenu menu[1]
			UpdateWindowMenu window
			
		Case EVENT_WINDOWCLOSE
			End
			
	EndSelect
	'Print CurrentEvent.ToString()
Wend
