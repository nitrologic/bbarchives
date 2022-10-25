; ID: 1822
; Author: SebHoll
; Date: 2006-09-23 09:13:36
; Title: BlitzMax Event Killer
; Description: Removes any events matching an Event ID from the event queue.

Function KillEvent(pID%,pID2%=0)
		
		Local tmpList:TList = New TList
		
		PollSystem()
		
		tmpList.AddFirst CurrentEvent;PollEvent
		
		While EventID() <> Null
		
			If Not (EventID() = pID Or EventID() = pID2) Then tmpList.AddLast CurrentEvent		
		
			PollEvent()
		
		Wend
		
		For Local a:TEvent = EachIn tmpList
		
			PostEvent(a)
		
		Next
		
		PollEvent()
		
		tmpList.Clear()
		tmpList = Null

EndFunction
