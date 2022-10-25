; ID: 1511
; Author: deps
; Date: 2005-10-29 12:28:25
; Title: OSX text to speech
; Description: Make you mac talk

#include <Carbon/Carbon.h>

int speech_init()
{
	long response, mask;
	OSErr err = Gestalt( gestaltSpeechAttr, &response );
	if ( err != noErr )
	{
		return 0;
	}

	mask = 1 << gestaltSpeechMgrPresent;	
	if ( response & mask == 0 )
 	{
		return 0;
	}

	return 1;
}

void speak_string( char *msg )
{
	OSErr err;
	SpeechChannel ch;
	err = NewSpeechChannel( 0, &ch );

	err = SpeakText( ch, msg, strlen(msg) );

	if( err != noErr )
	{
		return;
	}

}

void wait_for_speech()
{
	while( SpeechBusy() )
		;
}
