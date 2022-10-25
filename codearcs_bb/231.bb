; ID: 231
; Author: skn3[ac]
; Date: 2002-02-11 09:21:26
; Title: Text box WRAP
; Description: A text box, multi color support, acts as a LIST or text

AppTitle "Text Box function version 2"
Graphics 640,480,32,1
;||  Title: TEXT BOX function Version 2
;||  Written by: Jonahtan Pittock (skn3)
;||   www.skn3.com


;This is a text box function. It     
;Requires no external graphics files, and      
;You can customize its every feature.            
;You can have as many text            
;box's as you want, just               
;remeber memory factor               
;Each text entry is stored as            
;a type instance and can have custom          
;colors. Written by me free for you ......           
;www.acoders.com |-| skn3.acoders.com 

Type scrollbar
	Field ID$
	Field wtext$
	Field mode$                                                
	Field x                          
	Field y                          
	Field width                      
	Field height
	
	Field ent 
	Field top_arrow                  
	Field bottom_arrow 
	                     
	Field font
	Field font_colorR				 
	Field font_colorG				 
	Field font_colorB
	
	Field line_ColorR                
	Field line_ColorG                
	Field line_ColorB  
	              
	Field window_colorR              
	Field window_colorG              
	Field window_colorB  
	            
	Field scroll_bar_colorR          
	Field scroll_bar_colorG          
	Field scroll_bar_colorB  
	        
	Field scroll_button_colorR       
	Field scroll_button_colorG       
	Field scroll_button_colorB    
	
	Field lines_width
	Field lines_height					 
	Field line_index
	Field line_top
	Field lines_Xpointer
	Field lines_Ypointer
	Field lines_start
	Field lines_text$
	Field lines_scan
	
	Field lines_temp$
	Field lines_tempR				 
	Field lines_tempG				 
	Field lines_tempB
	Field lines_color_count
	                                  
	Field lines_flag				 
	Field lines_add_flag			 
	Field lines_next				 			 				 
                 
	Field sbar_bar_y#
	Field sbar_tempy
	Field sbar_height#
	Field sbar_bar_height#
	Field sbar_lines_total
	Field sbar_flag
	Field sbar_move_flag
	Field sbar_move_amount
End Type


;*************************************************************************************************************************************************
;* --->Adding a window																											       *
;* each window must be created in order of how you want them drawn																	 *
;* for example, if you want a chat window ontop of a names list window,													            	 *
;* create the names list window, then the chat window																				 *
;*																																	 *
;* add_window(x,y,width,height,ID,mode,fontsize,font,borderR,borderG,borderB,windowR,windowG,windowB,scrollbarR,scrollbarG,scrollbarB,buttonR,buttonG,buttonB) *
;* 																																	 *
;* The ID parameter is very important, because when you want to re-render it, or add text 											 *
;* you must give the Function the name of the window you are wanting to update, and this is the ID.                                              *
;* IMPORTANT!!!                                                                                                                                  *
;* 	dont call any window "global" as this is used by the function                                                                                *
;*  dont change any of the windows parameters once it has been created! (this will result in unusual text wraps and other strange effects)	 *
;* 																																	 *
;* MODES																															 *
;* "wrap" - this will wrap the text , if a sentance is too long then it will carry it on to the next line							 *
;* "nowrap" - this will write the text regardless of the width of your window, so if the text is too long it will be cut off (NOT WORKING!)				 *
;*************************************************************************************************************************************************
add_window(10,10,500,400,"window","wrap",17,"arial",255,255,255,255,255,255,0,0,0,0,0,0,50,50,50)



;*********************************************************************************************
;* --->Adding text to a speciffic window                                                     *
;* To add text to a speciffic window, you need to surply a string,ID, and red,green,and blue *
;* parameters. Each text added can have a seperate color. 									 *
;* The ID must be the nameID of one of the windows   										 *
;*																							 *
;* add_text(string$,windowID$)														 *
;* 																						     *
;* --->Adding text to all windows															 *
;* to add text to all windows with 1 function call, simply replace the windowID 		     *
;* with the word "global"																     *
;*																							 *
;* add_text(string$,"global")															 *
;*																							 *
;* IMPORTANT !!																				 *
;* when you add text, you must then render_window for it to update the totals otherwise		 *
;* scrolling will not work properly	
;To use colors simply add in your string  "[c]RGB TEXT_GOES_HERE[c]														 *
;*********************************************************************************************
add_text("written","global")
add_text("In 2001","global")
add_text("testing testing testing testing testing testing [c]255,0,0 Apocalyptic [c] [c]60,60,255 Coders [c] [c]0,255,0 testing again[c]","global")
add_text("www.acoders.com","global")
add_text("[c]255,0,0 RED[c][c]0,255,0 GREEN[c][c]0,0,255 BLUE[c][c]80,80,80 This is a long test line so i can fully test the test wrap feature, lets hope it works now. Thank you for viewing my text box object, please give feedback and send bug reports to[c][c]0,255,255 skn3_skn3@yahoo.com[c]","global")
add_text("line 1","global")
add_text("line 2","global")
add_text("line 3","global")
add_text("line 4","global")
add_text("[c]255,0,0 RED[c][c]0,255,0 GREEN[c][c]0,0,255 BLUE[c][c]80,80,80 This is a long test line so i can fully test the test wrap feature, lets hope it works now. Thank you for viewing my text box object, please give feedback and send bug reports to[c][c]0,255,255 skn3_skn3@yahoo.com[c]","global")
add_text("line 5","global")
add_text("line 6","global")
add_text("line 7","global")
add_text("line 8","global")
add_text("[c]255,0,0 RED[c][c]0,255,0 GREEN[c][c]0,0,255 BLUE[c][c]80,80,80 This is a long test line so i can fully test the test wrap feature, lets hope it works now. Thank you for viewing my text box object, please give feedback and send bug reports to[c][c]0,255,255 skn3_skn3@yahoo.com[c]","global")
add_text("line 9","global")
add_text("line 10","global")
add_text("line 11","global")
add_text("line 12","global")
add_text("line 13","global")
add_text("line 14","global")
add_text("[c]255,0,0 RED[c][c]0,255,0 GREEN[c][c]0,0,255 BLUE[c][c]80,80,80 This is a long test line so i can fully test the test wrap feature, lets hope it works now. Thank you for viewing my text box object, please give feedback and send bug reports to[c][c]0,255,255 skn3_skn3@yahoo.com[c]","global")
add_text("line 15","global")
add_text("line 16","global")
add_text("line 17","global")
add_text("line 18","global")
add_text("[c]255,0,0 RED[c][c]0,255,0 GREEN[c][c]0,0,255 BLUE[c][c]80,80,80 This is a long test line so i can fully test the test wrap feature, lets hope it works now. Thank you for viewing my text box object, please give feedback and send bug reports to[c][c]0,255,255 skn3_skn3@yahoo.com[c]","global")


;****************************************************************
;* RENDER WINDOW												*
;* Rendering a window will not draw it to screen, instead it 	*
;* renders on the backbuffer into its own image entity,			*
;* And restores the BackBuffer, And which buffer you were using	*
;* before you called it to render. 								*
;* 																*
;* --->Rendering a specific window								*
;* much like the add_text function, you suply the windowID of 	*
;* the window you wish to render								*
;*																*
;* render_window(windowID$)										*
;*																*
;* --->rendering all windows in 1 function call					*
;* again like the add_text function just replace the windowID 	*
;* with "global"												*
;*																*
;* render_window("global")										*
;****************************************************************

;****************************************************************
;* DRAW WINDOW											    	*
;* The draw window function draws the window to your current 	*
;* buffer.                                           			*
;* You must render_window before you draw it otherwise you wont	*
;* see any graphics displayed. 							    	*
;* 																*
;* --->drawing a specific window								*
;* much like the add_text function, you suply the windowID of 	*
;* the window you wish to draw  								*
;*																*
;* draw_window(windowID$)										*
;*																*
;* --->drawing all windows in 1 function call					*
;* again like the add_text function just replace the windowID 	*
;* with "global"												*
;*																*
;* draw_window("global")										*
;****************************************************************
;main loop
While Not KeyDown(1)
	SetBuffer BackBuffer() 
	Cls
	render_window("window")
	draw_window("window")
	Rect MouseX(),MouseY(),20,20
	Flip
Wend



Function add_window(w_x,w_y,w_width,w_height,w_ID$,w_mode$,w_fsize,w_font$,fcolR,fcolG,fcolB,w_lcolR,w_lcolG,w_lcolB,w_wcolR,w_wcolG,w_wcolB,w_sbcolR,w_sbcolG,w_sbcolB,w_bcolR,w_bcolG,w_bcolB)
	obj.scrollbar=New scrollbar
	obj\ID$=w_ID$
	obj\mode$=w_mode$
	obj\x=w_x
	obj\y=w_y
	obj\width=w_width-20
	obj\height=w_height
	obj\font=LoadFont(w_font$,w_fsize,False,False,False)
	SetFont obj\font
	obj\lines_height=obj\height/FontHeight()
	obj\ent=CreateImage(obj\width+20,obj\height)
	obj\line_top=1
	obj\font_colorR=fcolR
	obj\font_colorG=fcolG
	obj\font_colorB=fcolB
	obj\line_colorR=w_lcolR
	obj\line_colorG=w_lcolG
	obj\line_colorB=w_lcolB
	obj\window_colorR=w_wcolR
	obj\window_colorG=w_wcolG
	obj\window_colorB=w_wcolB
	obj\scroll_bar_colorR=w_sbcolR
	obj\scroll_bar_colorG=w_sbcolG
	obj\scroll_bar_colorB=w_sbcolB
	obj\scroll_button_colorR=w_bcolR
	obj\scroll_button_colorG=w_bcolG
	obj\scroll_button_colorB=w_bcolB
	obj\sbar_height#=obj\height-42
	obj\sbar_bar_y#=obj\y+21
	obj\sbar_flag=False
	;create arrows
	Cls
	Color obj\scroll_button_colorR,obj\scroll_button_colorG,obj\scroll_button_colorB
	Rect 0,0,9,9
	Color obj\line_colorR,obj\line_colorG,obj\line_colorB
	For xbot=0 To 8 Step 1
	Line 4,0,xbot,8
	Next
	obj\top_arrow=CreateImage(9,9)
	GrabImage obj\top_arrow,0,0
	Cls
	Color obj\scroll_button_colorR,obj\scroll_button_colorG,obj\scroll_button_colorB
	Rect 0,0,9,9
	Color obj\line_colorR,obj\line_colorG,obj\line_colorB
	For xbot=0 To 8 Step 1
	Line 4,8,xbot,0
	Next
	obj\bottom_arrow=CreateImage(9,9)
	GrabImage obj\bottom_arrow,0,0
	Cls
End Function


Function add_text(word$,which$)
	word$=Replace$ (word$,"[c]",Chr$(2))
	For obj.scrollbar=Each scrollbar
		If which$ = "global" Then
		obj\wtext$=obj\wtext$+word$+Chr$(1)
		Else If which$ <> "global" And obj\ID$=which$ Then
			obj\wtext$=obj\wtext$+word$+Chr$(1)
		End If
	Next	 	
End Function

Function render_window(which$)
	temp_buffer=GraphicsBuffer()
	SetBuffer BackBuffer()
	temp_buffer_image=CreateImage(GraphicsWidth(),GraphicsHeight())
	GrabImage temp_buffer_image,0,0 
	Cls 
	For obj.scrollbar=Each scrollbar
	If obj\ID$=which$ Or which$="global" Then
		;draw window bg 
		Color obj\window_colorR,obj\window_colorG,obj\window_colorB
		Rect obj\x+1,obj\y+1,obj\width-2,obj\height-2,1
		
		;***************
		;* render-text *
		;***************
			SetFont obj\font
			;reset window values for text parse
			obj\lines_start=1
			obj\line_index=1
			obj\lines_flag=0
			obj\lines_scan=1
			obj\lines_Ypointer=1
			obj\sbar_lines_total=0
			;get to point in text to read from
			If obj\line_top = 1 Then 
				obj\line_index=1
				obj\lines_start=1
				obj\lines_scan=0
			End If
			;render
			Repeat
				obj\lines_text$=""
				obj\lines_Xpointer=1
				obj\lines_add_flag=False
				obj\lines_next=False
				Repeat 
					obj\lines_temp$=Mid$(obj\wtext$,obj\lines_start,1)
					;add current character of text to temp buffer if it isn't a hidden character
					If obj\lines_temp$ <> Chr$(2) And obj\lines_temp$ <> Chr$(1) Then
						obj\lines_text$=obj\lines_text$+obj\lines_temp$
						obj\lines_start=obj\lines_start+1
						obj\lines_temp$=""
					EndIf
					;line break picked up
					If obj\lines_temp$ = Chr$(1) Then
						obj\lines_next=True
						obj\lines_add_flag=True
						obj\lines_start=obj\lines_start+1
						obj\lines_next=True
					End If
					;turn off text custom color, if parser reaches a hidden character 
					If obj\lines_flag=2 And obj\lines_temp$=Chr$(2) Then
						obj\lines_start=obj\lines_start+1
						obj\lines_flag=1
						obj\lines_add_flag=True
						obj\lines_temp$=""
					End If
					;turn on color mode and get color from text
					If obj\lines_flag=0 And obj\lines_temp$=Chr$(2) Then
						obj\lines_flag=3
						obj\lines_add_flag=True
						obj\lines_temp$=""
						If Len(obj\lines_text$)>0 Then
							Color obj\font_colorR,obj\font_colorG,obj\font_colorB
							obj\lines_start=obj\lines_start+1
						Else
							obj\lines_start=obj\lines_start+1
						End If
						;get RED
						Repeat
							obj\lines_temp$=Mid$(obj\wtext$,obj\lines_start,1)
							If obj\lines_temp$<>"," Then
								obj\lines_color_count=obj\lines_color_count+1
								obj\lines_start=obj\lines_start+1
							Else
								obj\lines_tempR=Mid$(obj\wtext$,(obj\lines_start-obj\lines_color_count),obj\lines_color_count)
								obj\lines_start=obj\lines_start+1
								obj\lines_color_count=0
							End If
							If obj\lines_color_count=3 Then
								obj\lines_tempR=Mid$(obj\wtext$,(obj\lines_start-obj\lines_color_count),obj\lines_color_count)
								obj\lines_start=obj\lines_start+1
								obj\lines_color_count=0
								obj\lines_temp$=","
							End If								
						Until obj\lines_temp$=","
						;get GREEN
						Repeat 
							obj\lines_temp$=Mid$(obj\wtext$,obj\lines_start,1)
							If obj\lines_temp$<>"," Then
								obj\lines_color_count=obj\lines_color_count+1
								obj\lines_start=obj\lines_start+1
							Else
								obj\lines_tempG=Mid$(obj\wtext$,(obj\lines_start-obj\lines_color_count),obj\lines_color_count)
								obj\lines_start=obj\lines_start+1
								obj\lines_color_count=0
							End If
							If obj\lines_color_count=3 Then
								obj\lines_tempG=Mid$(obj\wtext$,(obj\lines_start-obj\lines_color_count),obj\lines_color_count)
								obj\lines_start=obj\lines_start+1
								obj\lines_color_count=0
								obj\lines_temp$=","
							End If
						Until obj\lines_temp$=","
						;get BLUE
						If Mid$(obj\wtext$,obj\lines_start,3) <10 Then
							obj\lines_tempB=Mid$(obj\wtext$,obj\lines_start,1)
							obj\lines_start=obj\lines_start+1
						Else If Mid$(obj\wtext$,obj\lines_start,3) <100
							obj\lines_tempB=Mid$(obj\wtext$,obj\lines_start,2)
							obj\lines_start=obj\lines_start+2
						Else
							obj\lines_tempB=Mid$(obj\wtext$,obj\lines_start,3)
							obj\lines_start=obj\lines_start+3
						End If
					End If
				;render mode
				If obj\lines_scan=0
					;check if text is wider than width and wrap (only if wrap mode is on)
					If (obj\lines_Xpointer+StringWidth(obj\lines_text$)>=(obj\width-21)) Then
						obj\lines_add_flag=False
						t_len=Len(obj\lines_text$)
							For t_cut=t_len To 1 Step-1
							obj\lines_temp$=Mid$(obj\lines_text$,t_cut,1)
							If obj\lines_temp$=" " Then
								obj\lines_start=obj\lines_start-(Len(obj\lines_text$)-Len(Mid$(obj\lines_text$,1,t_cut)))
								obj\lines_text$=Mid$(obj\lines_text$,1,t_cut)
								obj\lines_add_flag=True
								obj\lines_next=True
								Exit
							End If
							If t_cut=1 Then
								obj\lines_text$=Left$(obj\lines_text$,(Len(obj\lines_text$)-1))
								obj\lines_add_flag=True
								obj\lines_start=obj\lines_start-1
								obj\lines_next=True
								Exit
							End If
							Next  
					End If
					;add current text buffer if color mode is just turned on 
					If obj\lines_add_flag=True And obj\lines_flag=3 Then
						Color obj\font_colorR,obj\font_colorG,obj\font_colorB
						Text (obj\x+3)+obj\lines_Xpointer,(obj\y+3)+((obj\lines_Ypointer-1)*FontHeight()),obj\lines_text$
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
						obj\lines_flag=2
					End If
					;add text in buffer if color mode is continued
					If obj\lines_add_flag=True And obj\lines_flag=2 Then
						Color obj\lines_tempR,obj\lines_tempG,obj\lines_tempB
						;Color 255,255,255
						Text (obj\x+3)+obj\lines_Xpointer,(obj\y+3)+((obj\lines_Ypointer-1)*FontHeight()),obj\lines_text$
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
					End If 
					;add text in buffer if color mode is finished
					If obj\lines_add_flag=True And obj\lines_flag=1 Then
						Color obj\lines_tempR,obj\lines_tempG,obj\lines_tempB
						;Color 255,255,255
						Text (obj\x+3)+obj\lines_Xpointer,(obj\y+3)+((obj\lines_Ypointer-1)*FontHeight()),obj\lines_text$
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
						obj\lines_flag=0
					End If
					;add text in buffer if color mode or finish color is off
					If obj\lines_add_flag=True And obj\lines_flag=0 Then
						Color obj\font_colorR,obj\font_colorG,obj\font_colorB
						Text (obj\x+3)+obj\lines_Xpointer,(obj\y+3)+((obj\lines_Ypointer-1)*FontHeight()),obj\lines_text$
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
					End If
				;scan mode
				Else
					;check if text is wider than width and wrap (only if wrap mode is on)
					If (obj\lines_Xpointer+StringWidth(obj\lines_text$)>=(obj\width-21)) Then
						obj\lines_add_flag=False
						t_len=Len(obj\lines_text$)
							For t_cut=t_len To 1 Step-1
							obj\lines_temp$=Mid$(obj\lines_text$,t_cut,1)
							If obj\lines_temp$=" " Then
								obj\lines_start=obj\lines_start-(Len(obj\lines_text$)-Len(Mid$(obj\lines_text$,1,t_cut)))
								obj\lines_text$=Mid$(obj\lines_text$,1,t_cut)
								obj\lines_add_flag=True
								obj\lines_next=True
								Exit
							End If
							If t_cut=1 Then
								obj\lines_text$=Left$(obj\lines_text$,(Len(obj\lines_text$)-1))
								obj\lines_add_flag=True
								obj\lines_start=obj\lines_start-1
								obj\lines_next=True
								Exit
							End If
							Next  
					End If
					;add current text buffer if color mode is just turned on 
					If obj\lines_add_flag=True And obj\lines_flag=3 Then
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
						obj\lines_flag=2
					End If
					;add text in buffer if color mode is continued
					If obj\lines_add_flag=True And obj\lines_flag=2 Then
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
					End If 
					;add text in buffer if color mode is finished
					If obj\lines_add_flag=True And obj\lines_flag=1 Then
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
						obj\lines_flag=0
					End If
					;add text in buffer if color mode or finish color is off
					If obj\lines_add_flag=True And obj\lines_flag=0 Then
						obj\lines_Xpointer=obj\lines_Xpointer+StringWidth(obj\lines_text$)
						obj\lines_text$=""
						obj\lines_add_flag=False
					End If
				End If

				Until obj\lines_next=True
				If obj\lines_scan=0 Then
					obj\lines_Ypointer=obj\lines_Ypointer+1
				End If
				If obj\lines_scan=1 And obj\line_index=obj\line_top Then
					obj\lines_scan=0
				Else If obj\lines_scan=0 And obj\line_index >= (obj\line_top+obj\lines_height)
					obj\lines_scan=2
				End If
				obj\sbar_lines_total=obj\sbar_lines_total+1
				obj\lines_next=False
				obj\line_index=obj\line_index+1
			Until obj\lines_start >= Len(obj\wtext$)
		;draw scrollbar bg
		Color obj\scroll_bar_colorR,obj\scroll_bar_colorG,obj\scroll_bar_colorB
		Rect obj\x+obj\width,obj\y+1,18,obj\height-2,1
		;draw buttons
		Color obj\scroll_button_colorR,obj\scroll_button_colorG,obj\scroll_button_colorB
		Rect obj\x+obj\width,obj\y+1,18,18,1
		Rect obj\x+obj\width,obj\y+obj\height-19,18,18,1
		
		
		;scrollbars
		If obj\sbar_lines_total > obj\lines_height Then
			obj\sbar_bar_height#=(obj\sbar_height#/obj\sbar_lines_total*obj\lines_height)
			Rect obj\x+obj\width,obj\sbar_bar_y#-5,21,obj\sbar_bar_height#+10
		End If
		;mouse clicked on bar gizmo
		If obj\sbar_flag=False And MouseDown(1) And RectsOverlap(MouseX(),MouseY(),1,1,obj\x+obj\width,obj\sbar_bar_y#-5,21,obj\sbar_bar_height#+10) Then 
			obj\sbar_flag=True
			obj\sbar_tempy=MouseY()
		End If
		If obj\sbar_flag=True And MouseDown(1) Then
			If MouseY() >= obj\sbar_tempy Then
				obj\sbar_move_flag=False
				obj\sbar_move_amount=MouseYSpeed()/(obj\sbar_height#/obj\sbar_lines_total)
			End If
			If MouseY() <= obj\sbar_tempy Then
				obj\sbar_move_flag=True
				obj\sbar_move_amount=(MouseYSpeed()*-1)/(obj\sbar_height#/obj\sbar_lines_total)
			End If
			While obj\sbar_move_amount > 0
				If obj\sbar_move_flag=False And obj\line_top <= (obj\sbar_lines_total-obj\lines_height) Then
					obj\line_top=obj\line_top+1
					obj\sbar_bar_y#=obj\sbar_bar_y#+(obj\sbar_height#/obj\sbar_lines_total)
				End If
				If obj\sbar_move_flag=True And obj\line_top > obj\sbar_move_amount Then
					obj\line_top=obj\line_top-1
					obj\sbar_bar_y#=obj\sbar_bar_y#-(obj\sbar_height#/obj\sbar_lines_total)	
				End If
				obj\sbar_move_amount=obj\sbar_move_amount-1
			Wend
			obj\sbar_move_amount=0
			obj\sbar_tempy=MouseY()
		Else 
			obj\sbar_flag=False
		End If
		
		
		;draw borders
		Color obj\line_colorR,obj\line_colorG,obj\line_colorB
		Rect obj\x,obj\y,obj\width,obj\height,0
		Rect obj\x+obj\width-1,obj\y,21,obj\height,0
		Rect obj\x+obj\width-1,obj\y,21,21,0
		Rect obj\x+obj\width-1,obj\y+obj\height-21,21,21,0
		;draw arrows
		DrawBlock obj\top_arrow,obj\x+obj\width+5,obj\y+6 
		DrawBlock obj\bottom_arrow,obj\x+obj\width+5,obj\y+obj\height-14
		;save window into image
		GrabImage obj\ent,obj\x,obj\y
		Cls
	End If
	Next
	DrawBlock temp_buffer_image,0,0
	FreeImage temp_buffer_image
	SetBuffer temp_buffer
End Function

Function draw_window(which$)
	If which$ = "global" Then
		For obj.scrollbar=Each scrollbar
			DrawBlock obj\ent,obj\x,obj\y
		Next
	Else 
		For obj.scrollbar=Each scrollbar
			If obj\ID$=which$ Then
				DrawBlock obj\ent,obj\x,obj\y
			End If
		Next
	End If
End Function
