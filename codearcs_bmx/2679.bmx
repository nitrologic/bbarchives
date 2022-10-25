; ID: 2679
; Author: Bobysait
; Date: 2010-03-26 04:10:32
; Title: Identify Events
; Description: Identify events from EventId() with a string

' TMap that register event Ids and there EVENT_XXXX const name + the tip associated in the "blitzmax Help"

Global EventNames:TMap=CreateMap()
Global EventTips:TMap=CreateMap()

' Easy way to register an event
Function MapsInsert_(Key:Int,evname:String,tip:String)
	MapInsert(EventNames,String(Key),evname)
	MapInsert(EventTips,String(Key),tip)
End Function

' register all the standart events
MapsInsert_(  257,"EVENT_APPSUSPEND", "Application suspended" )
MapsInsert_(  258,"EVENT_APPRESUME", "Application resumed" )
MapsInsert_(  259,"EVENT_APPTERMINATE", "Application wants To Terminate" )
MapsInsert_(  513,"EVENT_KEYDOWN", "Key pressed. Event data Contains keycode" )
MapsInsert_(  514,"EVENT_KEYUP", "Key released. Event data Contains keycode" )
MapsInsert_(  515,"EVENT_KEYCHAR", "Key character. Event data contains unicode value" )
MapsInsert_( 1025,"EVENT_MOUSEDOWN", "Mouse button pressed. Event data Contains mouse button code" )
MapsInsert_( 1026,"EVENT_MOUSEUP", "Mouse button released. Event data Contains mouse button code" )
MapsInsert_( 1027,"EVENT_MOUSEMOVE", "Mouse moved. Event X And Y contain mouse coordinates" )
MapsInsert_( 1028,"EVENT_MOUSEWHEEL", "Mouse wheel spun. Event data Contains delta clicks" )
MapsInsert_( 1029,"EVENT_MOUSEENTER", "Mouse entered gadget area" )
MapsInsert_( 1030,"EVENT_MOUSELEAVE", "Mouse Left gadget area" )
MapsInsert_( 2049,"EVENT_TIMERTICK", "Timer ticked. Event source Contains timer Object" )
MapsInsert_( 4097,"EVENT_HOTKEYHIT", "Hot Key hit. Event data And mods Contains hotkey keycode And modifier" )
MapsInsert_(32769,"EVENT_MENUACTION", "Menu has been selected" )
MapsInsert_(16385,"EVENT_WINDOWMOVE", "Window has been moved" )
MapsInsert_(16386,"EVENT_WINDOWSIZE", "Window has been resized" )
MapsInsert_(16387,"EVENT_WINDOWCLOSE", "Window Close icon clicked" )
MapsInsert_(16388,"EVENT_WINDOWACTIVATE", "Window activated" )
MapsInsert_(16389,"EVENT_WINDOWACCEPT", "Drag And Drop operation was attempted" )
MapsInsert_( 8193,"EVENT_GADGETACTION", "Gadget State has been updated" )
MapsInsert_( 8194,"EVENT_GADGETPAINT", "A Canvas Gadget needs To be redrawn" )
MapsInsert_( 8195,"EVENT_GADGETSELECT", "A TreeView Node has been selected" )
MapsInsert_( 8196,"EVENT_GADGETMENU", "User has Right clicked a TreeView Node Or TextArea gadget" )
MapsInsert_( 8197,"EVENT_GADGETOPEN", "A TreeView Node has been expanded" )
MapsInsert_( 8198,"EVENT_GADGETCLOSE", "A TreeView Node has been collapsed" )
MapsInsert_( 8199,"EVENT_GADGETDONE", "An HTMLView has completed loading a page" )


' and 2 function that return explicits events name

' return the name ( the Integer const as a string ) for the id of the event
Function EventName:String(evid:Int)
	Return String(EventNames.ValueForKey(String(evid)))
End Function

' return the description for the id of the event
Function EventHelp:String(evid:Int)
	Return String(EventTips.ValueForKey(String(evid)))
End Function
