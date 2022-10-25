; ID: 704
; Author: Red
; Date: 2003-05-23 02:23:49
; Title: Advance Toolbar
; Description: more options for toolbar

Include "B+ Advance Toolbar.bb"

;------------------------------------------------

WIN=CreateWindow("Wrapable toolbar example",100,100,300,128,0,1+2+8)
SetStatusText(WIN,"HELP: type SPACE to change toolbar mode")

Global TOOLBAR=CreateToolBar("ide_toolbar.bmp",0,0,0,0,WIN)		
	SetToolBarSeparators TOOLBAR,"0,4,7,15"  ;toolbar separators ( one before button 0, one before button 8)
	SetToolBarRows TOOLBAR,0               ;toolbar wrapable mode
		
	;button group
	LockToolBarGroup TOOLBAR,"0,1,2,3"
	LockToolBar  TOOLBAR,3        

	;change button state
	SetToolBarState TOOLBAR,4,True  ;pressed
	SetToolBarState TOOLBAR,5,False ;not pressed	

	;button activation
	EnableToolBar TOOLBAR,6         ;enabled	
	DisableToolBar TOOLBAR,7        ;disabled

	;button tooltip
	For i=0 To CountToolBarButtons(TOOLBAR)-1
		SetToolBarTips TOOLBAR,i,"button "+i
	Next 		

;------------------------------------------------

WIN2=CreateWindow("",500,100,0,0,WIN,1+16+32)
	
TOOLBAR2=CreateToolBar("ide_toolbar.bmp",0,0,0,0,WIN2)	
	LockToolBarGroup TOOLBAR2,"ALL" ;group all
	SetToolBarCols TOOLBAR2,2       ;two columns
	FitToolbarShape TOOLBAR2,2,6

;------------------------------------------------

While Not EventID()=$803
	WaitEvent()
	
	;press SPACE to change the toolbar mode 
	If KeyHit(57)  
		mode=(mode+1) Mod 3
		Select mode
			
			; toolbar wrapable !
			Case 0
				SetToolBarRows TOOLBAR,0
							
			; toolbar has 3 rows		
			Case 1
				SetToolBarRows TOOLBAR,3
								
			; toolbar has 8 rows
			Case 2  
				SetToolBarRows TOOLBAR,14
				
		End Select
	EndIf
Wend
