; ID: 1039
; Author: skidracer
; Date: 2004-05-26 21:51:55
; Title: CreateAgent
; Description: example of scripting an activex component

Function ReadObject(x,y,w,h,group)
; read script from data restore point
	script$=""
	While True
		Read s$
		If s$="" Exit
;		s$=Replace$(s$,"'",Chr$(34))
		If script$<>"" script$=script$+Chr$(13)+Chr$(10)
		script$=script$+s$
	Wend
; create a gadget to run our object
	shell=CreateHtmlView(x,y,w,h,group,2)
	While True
		id=WaitEvent()
		If id=$401 And EventSource()=shell And EventID=0 Exit
	Wend
	HtmlViewRun shell,script$
	Return shell
End Function

Function CreateAgent(x,y,w,h,group)
; read script from data
	Restore AgentScript
	Return ReadObject(x,y,w,h,group)
End Function

Function AgentSpeak(agent,message$)
	HtmlViewRun agent,"Merlin.Speak('"+message$+"')"
End Function

.AgentScript
	Data "var AgentControl=new ActiveXObject('Agent.Control')"
	Data "AgentControl.Connected=true"					
	Data "AgentControl.Characters.Load('Merlin')"			;,"http://agent.microsoft.com//agent2//chars//merlin//merlin.acf");
	Data "var Merlin = AgentControl.Characters.Character('Merlin')"
	Data "Merlin.LanguageID=0x0409"
	Data "Merlin.MoveTo(200,200)"
	Data "Merlin.Show()"
	Data "Merlin.Speak('YO!')"
	Data ""
	
win=CreateWindow("Basic Blitz Web Browser",100,100,500,500,0,35)

agent=CreateAgent(0,0,0,0,win)
SetGadgetLayout agent,1,1,1,1

timer=CreateTimer(1.0/6)	;10 secs?

Repeat
	id = WaitEvent()
		If id=$4001 AgentSpeak agent,"The time is "+CurrentTime$()
	If id=$803 Then Exit
Forever

End
