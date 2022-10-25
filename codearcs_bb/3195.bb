; ID: 3195
; Author: GfK
; Date: 2015-03-11 06:33:29
; Title: Minimise/Maximise buttons on Mac OSX
; Description: How to enable and use Min/Maximise buttons on OSX

Wrote this code years back and just spent an hour searching for it after revisiting some old code which doesn't seem to work.  So to save myself and anyone else the hassle in future, figured I'd stick it in here.



In MacOS you need to edit brl.mod/glgraphics.mod/glgraphics.macos.m

First, find this code:[code]style=NSTitledWindowMask|NSClosableWindowMask;[/code]...and change it to:[code]style=NSTitledWindowMask|NSClosableWindowMask|NSMiniaturizableWindowMask|NSResizableWindowMask;[/code]
This will add the minimize and zoom buttons to the window.

Next, find this code (starting at line 37)[code]-(BOOL)windowShouldClose:(id)sender{
	bbSystemEmitEvent( BBEVENT_APPTERMINATE,&bbNullObject,0,0,0,0,&bbNullObject );
	return NO;
}[/code]
AFTER this code, add the following:[code]-(void) windowDidMiniaturize:(NSNotification *) note {
	bbSystemEmitEvent( BBEVENT_APPSUSPEND,&bbNullObject,0,0,0,0,&bbNullObject );
}
-(void) windowDidDeminiaturize:(NSNotification *) note {
	bbSystemEmitEvent( BBEVENT_APPRESUME,&bbNullObject,0,0,0,0,&bbNullObject );
}
-(void) windowDidResize:(NSNotification *) note {
	bbSystemEmitEvent( BBEVENT_WINDOWSIZE,&bbNullObject,0,0,0,0,&bbNullObject );
}
[/code]Note, the first two functions there were added by Grable/Grey Alien but since you'll want to minimise your app as well, you might as well add those functions in now.

Once you've done that, save the file and go into MaxIDE and rebuild modules.

Clicking the Zoom button will now generate an EVENT_WINDOWSIZE event which you can trap, and change graphics modes.
