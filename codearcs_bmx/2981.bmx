; ID: 2981
; Author: Captain Wicker Soft
; Date: 2012-10-08 21:48:39
; Title: Sprite Scales
; Description: GUI code for my abandoned sprite scaling program.

'This code was created for an abandoned sprite scaling program. this is the gui only. (buttons and whatnot)
'This code was created by Austin Wicker of Captain Wicker Software. 
'You are given full permission to use this code for any purpose whatsoever as long as you remember to credit me for the design and original code.


SuperStrict
Import maxgui.drivers

Local Window:TGadget = CreateWindow( "Sprite Scales - GUI only",400,120,640,480,Null,545 )
Local loadimage1:TGadget = CreateButton( "Load Image 1",32,392,112,24,window,BUTTON_PUSH )
SetGadgetLayout loadimage1,0,0,0,0
Local loadimage2:TGadget = CreateButton( "Load Image 2",256,392,112,24,window,BUTTON_PUSH )
SetGadgetLayout loadimage2,0,0,0,0
Local loadimage3:TGadget = CreateButton( "Load Image 3",496,392,112,24,window,BUTTON_PUSH )
SetGadgetLayout loadimage3,0,0,0,0
Local spriteed1:TGadget = CreateCanvas( 48,216,80,144,window )
SetGadgetLayout spriteed1,0,0,0,0
Local spriteed2:TGadget = CreateCanvas( 272,216,80,144,window )
SetGadgetLayout spriteed2,0,0,0,0
Local spriteed3:TGadget = CreateCanvas( 512,216,80,144,window )
SetGadgetLayout spriteed3,0,0,0,0
Local slideme1:TGadget = CreateSlider( 56,176,64,20,window,1 )
SetGadgetLayout slideme1,0,0,0,0
Local slideme3:TGadget = CreateSlider( 56,144,64,20,window,1 )
SetGadgetLayout slideme3,0,0,0,0
Local x1:TGadget = CreateLabel( "X",40,176,100,20,window,0 )
SetGadgetLayout x1,0,0,0,0
Local y1:TGadget = CreateLabel( "Y",40,144,100,20,window,0 )
SetGadgetLayout y1,0,0,0,0
Local y2:TGadget = CreateLabel( "Y",256,144,100,20,window,0 )
SetGadgetLayout y2,0,0,0,0
Local x2:TGadget = CreateLabel( "X",256,176,100,20,window,0 )
SetGadgetLayout x2,0,0,0,0
Local clicky1:TGadget = CreateSlider( 136,136,16,20,window,9 )
SetGadgetLayout clicky1,0,0,0,0
Local clicky2:TGadget = CreateSlider( 136,176,16,20,window,9 )
SetGadgetLayout clicky2,0,0,0,0
Local clicky3:TGadget = CreateSlider( 352,136,16,20,window,9 )
SetGadgetLayout clicky3,0,0,0,0
Local slideme5:TGadget = CreateSlider( 280,176,64,20,window,1 )
SetGadgetLayout slideme1,0,0,0,0
Local slideme2:TGadget = CreateSlider( 280,144,64,20,window,1 )
SetGadgetLayout slideme2,0,0,0,0
Local clicky4:TGadget = CreateSlider( 352,176,16,20,window,9 )
SetGadgetLayout clicky4,0,0,0,0
Local notes:TGadget = CreateTextArea( 96,24,464,100,window,1 )
SetGadgetLayout notes,0,0,0,0
Local saveimage1:TGadget = CreateButton( "Save Image 1",32,432,112,24,window,BUTTON_PUSH )
SetGadgetLayout saveimage1,0,0,0,0
Local saveimage2:TGadget = CreateButton( "Save Image 2",256,432,112,24,window,BUTTON_PUSH )
SetGadgetLayout saveimage2,0,0,0,0
Local saveimage3:TGadget = CreateButton( "Save Image 3",496,432,112,24,window,BUTTON_PUSH )
SetGadgetLayout saveimage3,0,0,0,0
Local slideme6:TGadget = CreateSlider( 520,176,64,20,window,1 )
SetGadgetLayout slideme3,0,0,0,0
Local x3:TGadget = CreateLabel( "X",496,176,100,20,window,0 )
SetGadgetLayout x3,0,0,0,0
Local y3:TGadget = CreateLabel( "Y",496,144,100,20,window,0 )
SetGadgetLayout y3,0,0,0,0
Local slideme4:TGadget = CreateSlider( 520,144,64,20,window,1 )
SetGadgetLayout slideme4,0,0,0,0
Local clicky5:TGadget = CreateSlider( 592,136,16,20,window,9 )
SetGadgetLayout clicky5,0,0,0,0
Local clicky6:TGadget = CreateSlider( 592,168,16,20,window,9 )
SetGadgetLayout clicky6,0,0,0,0

Repeat
	WaitEvent()
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
	EndSelect
Until Appterminate() Or KeyHit(KEY_ESCAPE)
