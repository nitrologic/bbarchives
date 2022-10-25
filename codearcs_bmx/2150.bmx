; ID: 2150
; Author: JoshK
; Date: 2007-11-07 11:05:52
; Title: TextArea undo module
; Description: Command set for undo/redo with textarea gadgets

'Rem
Module leadwerks.textareaundo

Import BRL.FileSystem
Import BRL.System
Import BRL.Win32MaxGUI
Import BRL.Retro


'EndRem

Private

Type TUndoContext
	Global list:TList=New TList
	
	Field link:TLink
	Field gadget:TGadget
	Field undostates:TList=New TList
	Field current:TUndoState
	Field change
	Field disabled

	Method New()
		link=list.addlast(Self)
	EndMethod
	
	Method CreateUndoState:TUndostate(force=0)
		If current current.clearafter
		undostate:TUndoState=New TUndoState
		undostate.context=Self
		undostate.link=undostates.addlast(undostate)
		undostate.selpos=TextAreaCursor(gadget)
		undostate.sellen=TextAreaSelLen(gadget)
		undostate.update
		undostate.force=force
		current=undostate
		Return undostate
	EndMethod
		
	Method CanUndo:TUndoState(undostate:TUndoState=Null)
		If undostate=Null undostate=current
		If undostate
			If undostate.link.prevlink()
				prevundostate:TUndoState=TUndoState(undostate.link.prevlink().value())
				If prevundostate.force=-1
					Return TUndoState(prevundostate.link.prevlink().value())
				Else
					Return prevundostate
				EndIf
			EndIf
		EndIf
		Return Null
	EndMethod
	
	Method CanRedo:TUndoState(undostate:TUndoState=Null)
		If undostate=Null undostate=current
		If undostate
			If undostate.link.nextlink()
				nextundostate:TUndoState=TUndoState(undostate.link.nextlink().value())
				If nextundostate.force=1
					Return TUndoState(nextundostate.link.nextlink().value())
				Else
					Return nextundostate
				EndIf
			EndIf
		EndIf
		Return Null
	EndMethod

	Method Undo:Int()
		If current
			If canundo()
				current.undo
				current=canundo()
				Return True
			EndIf
		EndIf
		Return False
	EndMethod

	Method Redo:Int()
		undostate:TUndoState=CanRedo()
		If undostate
			undostate.redo
			current=undostate
			Return True
		EndIf
		Return False
	EndMethod

	Method Flush()
		For undostate:TUndoState=EachIn undostates
			undostate.kill
		Next
		undostates.clear
		current=Null
		createundostate
	EndMethod
	
	Method Kill()
		flush
		undostates=Null
		gadget=Null
		RemoveLink link
		link=Null
	EndMethod

	Function Callback:Object(id:Int,data:Object,context:Object)
		Local event:TEvent=TEvent(data)
		Select event.id
			
			Case EVENT_GADGETSELECT
				For undocontext:TUndoContext=EachIn TUndoContext.list
					If event.source=undocontext.gadget
						If undocontext.current
							If Not undocontext.disabled
								If undocontext.current.text=TextAreaText(undocontext.gadget)
									undocontext.current.selpos=TextAreaCursor(undocontext.gadget)
									undocontext.current.sellen=TextAreaSelLen(undocontext.gadget)
									undocontext.current.removetext=TextAreaText(undocontext.gadget,undocontext.current.selpos,undocontext.current.sellen)
								EndIf
							EndIf
						EndIf
						Exit
					EndIf
				Next
			
			Case EVENT_GADGETACTION
				For undocontext:TUndoContext=EachIn TUndoContext.list
					If event.source=undocontext.gadget
						If Not undocontext.disabled
							If undocontext.current
								If undocontext.current.text=TextAreaText(undocontext.gadget) Return Null
							EndIf
							undostate:TUndoState=undocontext.createundostate()
							event.data=undostate.change
						EndIf
						Exit
					EndIf
				Next
				
		EndSelect
		Return data
	EndFunction
	
EndType

Function CreateUndoContext:TUndocontext(gadget:TGadget)
	undocontext:TUndoContext=New TUndoContext
	undocontext.gadget=gadget
	undocontext.createundostate
	Return undocontext
EndFunction

Type TUndostate
	Field link:TLink
	Field context:TUndoContext
	Field text$
	Field undotext$
	Field redotext$
	Field selpos
	Field sellen
	Field undolen
	Field undopos
	Field change
	Field force
	
	Field removetext$
	Field removeposition
	
	Field addtext$
	Field addposition

	Method Update()
		text$=TextAreaText(context.gadget)
		selpos=TextAreaCursor(context.gadget)
		sellen=TextAreaSelLen(context.gadget)
		
		removetext=TextAreaText(context.gadget,selpos,sellen)
		
		lastundostate:TUndoState=context.canundo(Self)
		If lastundostate
			change=text.length-lastundostate.text.length
			change=change+lastundostate.sellen
		Else
			change=text.length
		EndIf
				
		If change<0
			undopos=selpos
			undolen=-change
			If lastundostate
				undotext=Mid(lastundostate.text,undopos+1,-change)
			EndIf
		Else
			undopos=selpos-change
			undolen=change
			If lastundostate
				undotext=Mid(text,undopos+1,change)
			EndIf
		EndIf

	EndMethod

	Method Undo()
		start=TextAreaCursor(context.gadget)
		prevundostate:TUndoState=context.canundo(Self)
		nextundostate:TUndoState=context.canredo(Self)
		LockTextArea context.gadget
		If change>0
			SetTextAreaText context.gadget,"",undopos,undolen
		Else
			SetTextAreaText context.gadget,undotext,undopos,0
		EndIf
		UnlockTextArea context.gadget
		prevundostate:TUndoState=context.canundo(Self)
		If prevundostate		
			SetTextAreaText context.gadget,prevundostate.removetext,prevundostate.selpos,0
			SelectTextAreaText context.gadget,prevundostate.selpos,prevundostate.sellen
		Else
			SelectTextAreaText context.gadget,0,0
		EndIf
		
		stop=TextAreaCursor(context.gadget)
		size=Abs(start-stop)+TextAreaSelLen(context.gadget)
		context.disabled=True
		EmitEvent CreateEvent(EVENT_GADGETACTION,context.gadget,size)
		context.disabled=False
	EndMethod
	
	Method Redo()
		start=TextAreaCursor(context.gadget)
		prevundostate:TUndoState=context.canundo(Self)
		nextundostate:TUndoState=context.canredo(Self)
		LockTextArea context.gadget
		If change<0
			SetTextAreaText context.gadget,"",undopos,undolen
		Else
			If prevundostate l=prevundostate.sellen
			SetTextAreaText context.gadget,undotext,undopos,l
		EndIf
		UnlockTextArea context.gadget
		SelectTextAreaText context.gadget,selpos,sellen
		
		stop=TextAreaCursor(context.gadget)
		size=Abs(start-stop)+TextAreaSelLen(context.gadget)
		context.disabled=True
		EmitEvent CreateEvent(EVENT_GADGETACTION,context.gadget,size)
		context.disabled=False
	EndMethod
	
	Method ClearAfter()
		Local sublink:TLink
		Local nextsublink:TLink
		sublink=link.nextlink()
		While sublink
			nextsublink=sublink.nextlink()
			TUndoState(sublink.value()).kill
			sublink=nextsublink			
		Wend
	EndMethod
	
	Method Kill()
		If link.prevlink()
			current=TUndostate(link.prevlink().value())
		Else
			current=Null
		EndIf
		RemoveLink link
		link=Null
	EndMethod

EndType

AddHook EmitEventHook,TUndoContext.Callback

Function FindUndoContext:TUndoContext(gadget:TGadget)
	For undocontext:TUndoContext=EachIn TUndoContext.list
		If undocontext.gadget=gadget Return undocontext
	Next
	undocontext=CreateUndoContext(gadget)
	Return undocontext
EndFunction

Public

'Helper Functions
Function TextAreaUndo:Int(gadget:TGadget)
	Return FindUndoContext(gadget).undo()
EndFunction

Function TextAreaRedo:Int(gadget:TGadget)
	Return FindUndoContext(gadget).redo()
EndFunction

Function TextAreaCanUndo:Int(gadget:TGadget)
	Return (FindUndoContext(gadget).canundo()<>Null)
EndFunction

Function TextAreaCanRedo:Int(gadget:TGadget)
	Return (FindUndoContext(gadget).canredo()<>Null)
EndFunction

Function TextAreaFlushUndos:Int(gadget:TGadget)
	FindUndoContext(gadget).flush()
EndFunction

Function TextAreaCreateUndoState()
	FindUndoContext(gadget).createundostate
EndFunction
