; ID: 2569
; Author: JoshK
; Date: 2009-08-25 17:59:01
; Title: CodeArea gadget
; Description: Code editor with undo and formatting

SuperStrict

Import maxgui.maxgui
Import "TextAreaUndo.bmx"
Import "Lexer.bmx"

Type TCodeArea Extends TProxyGadget
	
	Field textarea:TGadget
	Field undocontext:TUndoContext
	Field undostate:TUndoState
	Field lexer:TLexer=New TLexer
	
	Method CleanUp()
		RemoveHook(EmitEventHook,EventHook,Self)
		undocontext.Free()
		Super.CleanUp()
	EndMethod
	
	Method ClearUndos()
		undocontext.Flush()
	EndMethod
	
	Method ReplaceText:Int(pos:Int,length:Int,text$,units:Int)
		Local result:Int
		Local l:Int
		Local p:Int
		Local line1:Int
		Local line2:Int
		result=proxy.ReplaceText(pos,length,text,units)
		l=text.length
		p=pos
		line1=TextAreaLine(textarea,p)
		line2=TextAreaLine(textarea,p+l)
		lexer.FormatText(textarea,line1,1+line2-line1)
		ClearUndos()
		Return result
	End Method
	
	Method AddText:Int(text$)
		Local result:Int
		Local l:Int
		Local p:Int
		Local line1:Int
		Local line2:Int
		l=text.length
		p=TextAreaText(textarea).length
		result=proxy.AddText(text)
		line1=TextAreaLine(textarea,p)
		line2=TextAreaLine(textarea,p+l)
		lexer.FormatText(textarea,line1,1+line2-line1)
		ClearUndos()
		Return result
	End Method
	
	Function Create:TCodeArea(x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=0)
		Local codearea:TCodeArea
		codearea=New TCodearea
		codearea.textarea=CreateTextArea(x,y,width,height,group)		
		codearea.undocontext=TUndoContext.Create(codearea.textarea)
		SetGadgetFilter(codearea.textarea,Filter,codearea)
		codearea.setproxy(codearea.textarea)
		AddHook(EmitEventHook,EventHook,codearea)
		Return codearea
	EndFunction
	
	Method Undo()
		Local start:Int
		Local stop:Int
		Local line1:Int
		Local line2:Int
		
		If undostate
			start=undostate.undopos
			stop=start+undostate.undolen
		EndIf
		undocontext.Undo()
		line1=Min(TextAreaLine(textarea,start),TextAreaCursor(textarea,TEXTAREA_LINES))
		line2=Max(TextAreaLine(textarea,stop),TextAreaCursor(textarea,TEXTAREA_LINES)+TextAreaSelLen(textarea,TEXTAREA_LINES))
		lexer.FormatText(textarea,line1,1+line2-line1)
	EndMethod

	Method Redo()
		Local start:Int
		Local stop:Int
		Local line1:Int
		Local line2:Int
		
		If undostate
			start=undostate.undopos
			stop=start+undostate.undolen
		EndIf		
		undocontext.Redo()
		line1=Min(TextAreaLine(textarea,start),TextAreaCursor(textarea,TEXTAREA_LINES))
		line2=Max(TextAreaLine(textarea,stop),TextAreaCursor(textarea,TEXTAREA_LINES)+TextAreaSelLen(textarea,TEXTAREA_LINES))
		lexer.FormatText(textarea,line1,1+line2-line1)		
	EndMethod
	
	'Handles undo states
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local codearea:TCodeArea=TCodeArea(context)
		Local event:TEvent=TEvent(data)
		Select event.id
			Case EVENT_GADGETSELECT
				If codearea.undocontext.Current
					If Not codearea.undocontext.disabled
						If codearea.undocontext.Current.text=TextAreaText(codearea.textarea)
							codearea.undocontext.Current.selpos=TextAreaCursor(codearea.textarea)
							codearea.undocontext.Current.sellen=TextAreaSelLen(codearea.textarea)
							codearea.undocontext.Current.removetext=TextAreaText(codearea.textarea,codearea.undocontext.Current.selpos,codearea.undocontext.Current.sellen)
						EndIf
					EndIf
				EndIf			
			Case EVENT_GADGETACTION
				If Not codearea.undocontext.disabled
					Local change:Int
					Local start:Int
					Local stop:Int
					Local line1:Int
					Local line2:Int
					Local n:Int
					Local s:String
					change=event.data
					start=TextAreaCursor(codearea.textarea)-1
					If change<0 start=start+change
					stop=start+Abs(change)
					line1=TextAreaLine(codearea.textarea,start)
					line2=TextAreaLine(codearea.textarea,stop)
					codearea.lexer.FormatText codearea.textarea,line1,1+line2-line1
					If codearea.undocontext.Current
						If codearea.undocontext.Current.text=TextAreaText(codearea.textarea) Return Null
					EndIf
					If Not codearea.undocontext.disabled
						codearea.undostate=codearea.undocontext.createundostate()
					EndIf
				EndIf
		EndSelect
		Return data
	EndFunction
	
	'Handles block indent
	Function Filter:Int(event:TEvent,context:Object)
		Local codearea:TCodeArea=TCodeArea(context)
		Local line1:Int
		Local line2:Int
		Local n:Int,s:String
		
		Select event.id
		Case EVENT_KEYDOWN
		Case EVENT_KEYCHAR
			If event.data=KEY_TAB
				If TextAreaSelLen(codearea.textarea)
					'Don't lock the textarea gadget, it will cause errors.
					line1=TextAreaCursor(codearea.textarea,TEXTAREA_LINES)
					line2=line1+TextAreaSelLen(codearea.textarea,TEXTAREA_LINES)
					If event.mods=MODIFIER_SHIFT
						For n=line1 To line2-1
							s$=TextAreaText(codearea.textarea,n,1,TEXTAREA_LINES)
							If s.length>1
								If Left(s,1).Trim()=""
									SetTextAreaText codearea.textarea,"",TextAreaChar(codearea.textarea,n),1
								EndIf
							EndIf
						Next
					Else
						For n=line1 To line2-1
							SetTextAreaText codearea.textarea,"	",n,0,TEXTAREA_LINES
						Next
					EndIf
					Local char1:Int
					Local char2:Int
					char1=TextAreaChar(codearea.textarea,line1)
					char2=TextAreaChar(codearea.textarea,line2)

					Local do:Int=0

					If Right(TextAreaText(codearea.textarea,char1,char2-char1,TEXTAREA_CHARS),1)="~n"
						char2:-1
					EndIf
					SelectTextAreaText(codearea.textarea,char1,char2-char1,TEXTAREA_CHARS)
					
					codearea.undostate=codearea.undocontext.CreateUndoState()
					codearea.undostate.undopos=TextAreaChar(codearea.textarea,line1)
					
					Return 0
				EndIf
			EndIf
		EndSelect
		Return 1
	EndFunction
	
EndType
