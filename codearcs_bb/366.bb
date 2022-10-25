; ID: 366
; Author: Mojokool
; Date: 2002-07-15 17:48:15
; Title: Win32 Access Through Blitz DLL Use Visual C++
; Description: Just gives you tips on one way to access the Win32 though DLL.


HERE IS THE VISUAL C++ CODE 

//////////////////////////////////////////////////////
//HEADER FILE
//////////////////////////////////////////////////////
//INCLUDES
#include <windows.h>						//Include the windows standard files
#include <stdlib.h>
#include "commdlg.h"						//Include for windows common dialog boxes
#include "resource.h"						//Include the resource file



//////////////////////////////////////////////////////
//GLOBALS
//////////////////////////////////////////////////////
int				Event			=NULL;			//Passed the current event to the Blitz Window
int				DialogType		=NULL;			//Flag for "User" made Dialog Procedures;
int				FileDialogType	=NULL;			//Flag to open either a "Save" or "Open" Dialog
char			StringToSend[256];				//File String Name
char			*TypeList		=NULL			//Holds the string that contains the type list
HWND			BBWin			=NULL;			//Handle to the Blitz Window
HHOOK			hhkHook			=NULL;			//Handle to the hook function
HMENU			MainMenu		=NULL;			//Handle to the Main Menu that will be loaded
HMENU			PopUpMenu		=NULL;			//The popup menu for the mouse
HINSTANCE		DllInstance		=NULL;			//Instance to the Dll loaded into memory for the Blitz App
HINSTANCE		hBBInstance		=NULL;			//Instance of the Blitz Basic Window
OPENFILENAME	ofn; 



//////////////////////////////////////////////////////
//EXPORT FUNCTION DECLORATIONS
//////////////////////////////////////////////////////
//Get the desktop measurements
extern "C"{
_declspec(dllexport) void _cdecl DesktopMetrics( char *in,int in_size,long *out,int out_sz );
}

//////////////////////////////////////////////////////
//End the Win32 Blitz GUI
extern "C"{
_declspec(dllexport) void _cdecl EndWin32( const void *in,int in_size,void *out,int out_sz );
}

//////////////////////////////////////////////////////
//Get the current windows event
extern "C"{
_declspec(dllexport) int _cdecl GetEvent( const void *in,int in_size,void *out,int out_sz );
}

/////////////////////////////////////////////////////
//Get the string the File Path string
extern "C"{
_declspec(dllexport) void _cdecl GetString( const void *in,int in_size,char *out,int out_sz );
}

//message box
extern "C"{
_declspec(dllexport) int _cdecl MsgBox( char *in,int in_size,int *out,int out_sz );
}

/////////////////////////////////////////////////////
//Get the string the File Path string
extern "C"{
_declspec(dllexport) void _cdecl SetTypeList( const void *in,int in_size,char *out,int out_sz );
}

//////////////////////////////////////////////////////
//Start the Win32 Blitz GUI
extern "C"{
_declspec(dllexport) HWND _cdecl StartWin32( const void *in,int in_size,void *out,int out_sz );
}

//////////////////////////////////////////////////////
//Dialog Event Proccesor
extern "C"{
LRESULT _declspec(dllexport)_stdcall CALLBACK DlgProc( HWND hDlg,UINT msg,WPARAM wparam,LPARAM lparam );
}

//////////////////////////////////////////////////////
//Windows Message Processor Hook
extern "C"{
LRESULT _declspec(dllexport)_stdcall CALLBACK MsgProc( int ncode,WPARAM wparam,LPARAM lparam );
}





//////////////////////////////////////////////////////
//INTERNAL FUNCTION DECLORATIONS
//////////////////////////////////////////////////////
//Get File Name
void GetFileInfo( void );

//////////////////////////////////////////////////////
//get a character string
char *string( char *in );

//////////////////////////////////////////////////////
//SOURCE FILE
//////////////////////////////////////////////////////
//INCLUDES
#include "GLITCH GAME EDITOR.H"					//Include the Game Editor Header file

 

//////////////////////////////////////////////////////
//EXPORT FUNCTION DEFINITIONS
/////////////////////////////////////////////////////
//end the gui system
void DesktopMetrics( char *in,int in_size,long *out,int out_sz )
{
	RECT desktop;
	
	SystemParametersInfo( SPI_GETWORKAREA,NULL,&desktop,NULL );

	long screen_width = desktop.right;//GetSystemMetrics( SM_CXSCREEN );	//get the size of the desktop width
	*out = screen_width;
	out++;
	long screen_height = desktop.bottom;//GetSystemMetrics( SM_CYSCREEN );	//get the size of the desktop height
	*out = screen_height;
}

//////////////////////////////////////////////////////
//End the Win32 Blitz GUI
void EndWin32( const void *in,int in_size,void *out,int out_sz )
{
	SendMessage( BBWin,WM_CLOSE,0,0 );				//End Program
}//End the Win32 GUI for Blitz App function

//////////////////////////////////////////////////////
//Get the current windows event
int GetEvent( const void *in,int in_size,void *out,int out_sz )
{	
	int ReturnEvent = Event;					//The Event to return
	Event = 0;									//Set Event Variable to zero
	return( ReturnEvent );						//Return the current event to the Blitz App
}//End the event function

/////////////////////////////////////////////////////
//Get the string the File Path string
void GetString( const void *in,int in_size,char *out,int out_sz )
{
	//Copy the Buffer containing the File Path to 
	//the blitz basiz bank
	memmove( out,StringToSend,256);
}

/////////////////////////////////////////////////////
//message box
int MsgBox( char *in,int in_size,int *out,int out_sz )
{	
	// get the title string
	long next_string = *((long*)in);			//variable for number of string character to recive from *in bank
	char *title = string( in );					//create the title string
	in += (next_string+4);						//move the *in pointer to the next string
	
	//get the msg string
	next_string = *((long*)in);					//get the number of string characters to recive from the *in bank
	char *msg = string( in );					//create the message string
	in += (next_string+4);						// move the *in pointer to the find out the message box button type

	char button = *in;
	
	switch (button)								//show msg box type dependent on button value
	{	
	//create msg box with normal 'OK' button only
	case 0:
		MessageBox( BBWin,msg,title,MB_OK );
		break;
	case 1:
		MessageBox( BBWin,msg,title,MB_OKCANCEL );
		break;
	case 2:
		MessageBox( BBWin,msg,title,MB_YESNO );
		break;
	case 3:
		MessageBox( BBWin,msg,title,MB_YESNOCANCEL );
		break;
	case 4:
		MessageBox( BBWin,msg,title,MB_RETRYCANCEL );
		break;
	case 5:
		MessageBox( BBWin,msg,title,MB_ABORTRETRYIGNORE );
		break;
	}

	//free allocated memory
	free( title );								//free title memory allocated
	free( msg );								//free msg memory allocated

	return(button);
}	//end function

/////////////////////////////////////////////////////
//Get the string the File Path string
void SetTypeList( const void *in,int in_size,char *out,int out_sz )
{
	//Get the type list
	*TypeList = string( in );
}

//////////////////////////////////////////////////////
//Start the Win32 Blitz GUI
HWND StartWin32( const void *in,int in_size,void *out,int out_sz )
{	
	//Get the size of the Desktop
	long screen_width = GetSystemMetrics( SM_CXSCREEN );			//get the size of the desktop width
	long screen_height = GetSystemMetrics( SM_CYSCREEN );

	//Get the Blitz window and setup a menu for the Window
	BBWin		= FindWindow( NULL,"Blitz Basic Main Window" );	//Find the Bliz App Window
	DllInstance = GetModuleHandle( "GLITCH GAME EDITOR.DLL" );	//Get the handle to the DLL
	MainMenu	= LoadMenu( DllInstance,(LPCSTR)MAIN_MENU );	//Load the menu from the DLL handle
	PopUpMenu	= LoadMenu( DllInstance,(LPCSTR)MAIN_POPUPMENU );
	PopUpMenu	= GetSubMenu( PopUpMenu,0 );
	
	//Draw the loaded menu bar
	SetMenu( BBWin,MainMenu );					//Set the load menu as the main window menu
	DrawMenuBar( BBWin );						//Draw the menu to the window

	//Set the hook function to monitor messages
	hhkHook = SetWindowsHookEx( WH_GETMESSAGE,MsgProc,NULL,GetCurrentThreadId() );

	//Set the regular "App" icon for blitz Window
	SetWindowText( BBWin,"GLITCH - Game Editor" );
	SetClassLong( BBWin,GCL_HICON,(LONG)LoadIcon( NULL,IDI_APPLICATION ));
	SetWindowPos( BBWin,HWND_BOTTOM,0,0,screen_width/2,screen_height/2,SWP_SHOWWINDOW | SWP_NOMOVE );
	SetWindowLong( BBWin,GWL_STYLE, WS_OVERLAPPEDWINDOW | WS_MAXIMIZE |	//set the new style of the BlITZ BASIC App
									WS_SIZEBOX );//WS_VSCROLL | WS_HSCROLL | ;

	ShowWindow( BBWin,SW_SHOWNORMAL );								//show the window
	UpdateWindow( BBWin );											//update the window
	return( BBWin );							//Return the handle of the Blitz Window
}//End the Initialize Function

//////////////////////////////////////////////////////
//Dialog Event Proccesor
LRESULT CALLBACK DlgProc( HWND hDlg,UINT msg,WPARAM wparam,LPARAM lparam )
{
	//Change settings for the dialog box depending
	//on the type of dialog box
	switch( DialogType )
	{
	case 1:
		{
			//SetDlgItemText( hDlg,IDD_TYPE_LIST,TEXT("Load Entity Type") );
			break;
		}
	case 2:
		{
			break;
		}
	}//End switch

	//Find out what type of Message is being sent
	switch( msg )
	{
	case WM_COMMAND:							//A menu,button, or key is being hit
		{
			switch(wparam)
			{
			case IDOK:							//ok button
				{
					EndDialog( hDlg,true);		//end the dialog
					return( true );				//send message to exit			
					break;
				}
			case IDCANCEL:
				{
					EndDialog( hDlg,true );		//end the dialog session
					return( true );				//send message to exit
					break;
				}
			}
			break;
		}
	}//End switch

	//return that user has not exited the dialog box yet
	return( false );
}//End Processor Function

//////////////////////////////////////////////////////
//Windows Message Processor Hook
LRESULT CALLBACK MsgProc( int ncode,WPARAM wparam,LPARAM lparam )
{
	//Get the Message sent to windows
	MSG msg = *(PMSG)lparam;
	
	//Decide next action based on type of message
	switch( msg.message )
	{
	case WM_CLOSE:
		{
			DestroyMenu( PopUpMenu );
			DestroyMenu( MainMenu );						//Free the main menu from memory
			UnhookWindowsHookEx( hhkHook );					//Unhook the conected hook function
		}
	//If message is about a menu
	case WM_COMMAND:					
		{
			//Check which menu item was selected
			switch( msg.wParam )
			{
			case ID_MAIN_FILE_NEW:								//Something for "File" was selected
				{
					Event = 2;									//Start a New Program
					break;
				}
			case ID_MAIN_FILE_OPEN:								//Open a Saved File
				{
					Event			= 3;
					FileDialogType	= 1;
					ofn.lpstrFilter	= TEXT("GLITCH Game World Section\0*""*.gws");
					ofn.lpstrTitle	= TEXT("Open File");
					GetFileInfo();
					break;
				}
			case ID_MAIN_FILE_SAVE:								//Save a File
				{
					FileDialogType	= 4;
					ofn.lpstrFilter	= TEXT("GLITCH Game World Section\0*""*.gws");
					ofn.lpstrTitle	= TEXT("Save File");
					GetFileInfo();
					break;
				}
			case ID_MAIN_FILE_SAVEAS:							//Save a File As
				{
					FileDialogType	= 5;
					ofn.lpstrFilter	= TEXT("GLITCH Game World Section\0*""*.gws");
					ofn.lpstrTitle	= TEXT("Save File As");
					GetFileInfo();
					break;
				}
			case ID_MAIN_FILE_EXIT:								//Exit the Program
				{
					Event = 1;
					break;
				}
			case ID_HELP_ABOUT:									//Show the About Dialog
				{
					//Open the about dialog box
					DialogBox( DllInstance,(LPCSTR)IDD_ABOUT,BBWin,(DLGPROC)DlgProc );
					break;
				}
			}//End switch
			break;
		}
	case WM_RBUTTONDOWN:
		{
			UINT value = 0;
			//Show the short cut Menu
			value = TrackPopupMenu( PopUpMenu,TPM_LEFTALIGN | TPM_TOPALIGN |
							  TPM_RETURNCMD | TPM_LEFTBUTTON,msg.pt.x,msg.pt.y,0,BBWin,NULL );

			//Update Action based on what Menu item was picked
			switch( value )
			{
			case ID_MAIN_POPUP_EDIT_ENTITY:
				{
					PopUpMenu	= LoadMenu( DllInstance,(LPCSTR)MAIN_POPUPMENU );
					PopUpMenu	= GetSubMenu( PopUpMenu,1 );
					DrawMenuBar( BBWin );
					Event = 100;
					break;
				}
			case ID_MAIN_POPUP_LOAD_ENTITY:
				{
					Event = 102;
					DialogType = 1;
					DialogBox( DllInstance,(LPCSTR)IDD_TYPE_LIST,BBWin,(DLGPROC)DlgProc );
					break;
				}
			case ID_EDITENTITY_TOMAIN:
				{
					PopUpMenu	= LoadMenu( DllInstance,(LPCSTR)MAIN_POPUPMENU );
					PopUpMenu	= GetSubMenu( PopUpMenu,0 );
					Event = 104;
					break;
				}
			}//End switch

			break;
		}
	}//End switch

	//Call the next hook function in the chain
	return( CallNextHookEx( hhkHook,ncode,wparam,lparam ));
}//End the Message Processor Function

//////////////////////////////////////////////////////
//INTERNAL FUNCTION DECLORATIONS
//////////////////////////////////////////////////////
//Get File Name
void GetFileInfo( void )
{
	HINSTANCE BBWinInstance = (HINSTANCE)GetModuleHandle(NULL);

	//TCHAR _szFilter[] = TEXT("All Files\0*.*\0Text Files (*.txt)\0*.TXT\0");
	//TCHAR _szPickMsg[] = TEXT(""); 
	//CHAR szFileName[] = TEXT("");

	ofn.lStructSize		= sizeof(OPENFILENAME); 
	ofn.hwndOwner		= BBWin; 
	ofn.hInstance		= BBWinInstance; 
	ofn.nFilterIndex	= 1L; 
	ofn.lpstrFile		= (LPTSTR)StringToSend;		
	ofn.nMaxFile		= 500;		
	ofn.lpstrInitialDir = NULL;
	ofn.lpstrInitialDir = NULL; 
	ofn.Flags			= OFN_HIDEREADONLY | OFN_FILEMUSTEXIST | 
						  OFN_PATHMUSTEXIST | OFN_EXPLORER;

	if( FileDialogType == 1 )
		GetOpenFileName(&ofn);
	else
		GetSaveFileName(&ofn);
}//End Get File Name Function

/////////////////////////////////////////////////////
//get a string
char *string( char *in )
{
	//get the number of characters in the first string	

	long string_length = *((long*)in);							//get the length of the string
	char *string;												//create a char string
	in += 4;													//move the in pointer over 4bytes to get the string
	
	//allocate memory for the string
	if (( string = ( char *) malloc( string_length )) == NULL)	//allocate memory space for the string return "0" if unsuccessful
	{
		return 0;												//return back to main program with "0" if not enough memory
	}

	//get the string
	memset(string,NULL,string_length);							//set memory aside for the string
	memmove(string,in,string_length-1);							//!!VERY IMPORTANT!! remember that the total number of characters must be decreased by 1
																//copy the string stored at "in" pointer to the "title string" pointer
	//return the created string
	return( string );
}	//end function

/////////////////////////////////////////////////////
HERE IS THE BLITZ CODE
/////////////////////////////////////////////////////


;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

;	INITIALIZATION

;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;INCLUDES
Include "C:\GLITCH\GLITCH GAME EDITOR\GLITCH Blitz Files\GLITCH - Game Initialization.bb"
Include "C:\GLITCH\GLITCH GAME EDITOR\GLITCH Blitz Files\GLITCH - Functions.bb"
;Include "C:\GLITCH\GLITCH GAME EDITOR\GLITCH Blitz Files\GLITCH - Game  Engine BETA.bb"

;-----------------------------------------------------------------------------------------------------------
;GRAPHICS SETUP
;Setup the Editor for Use
AppTitle "Blitz Basic Main Window"										;Setup the app title
Global Win32Dll$ 	= "C:\GLITCH\GLITCH GAME EDITOR\Debug\GLITCH GAME EDITOR";Get the address of the dll

BankStartIn 		= CreateBank(8)										;Bank to go into dll function
BankStartOut 		= CreateBank(8)										;Bank of info from dll function
CallDLL( Win32Dll$,"DesktopMetrics",BankStartIn,BankStartOut )					;Call Dll to get the size of the Desktop
Graphics3D PeekInt( BankStartOut,0 ),PeekInt( BankStartOut,4 ),16,2				;Enable Blitz for 3D drawing and set Res to Desktop Size
SetBuffer BackBuffer()												;Set the backbuffer for drawing

;-----------------------------------------------------------------------------------------------------------
;VARIABLES
;Gloabl
Global gRecivedString$ 	= ""											;File string to hold the file path string

;Local
BBWin 			= CallDLL( Win32Dll$,"StartWin32" )						;Set up the Win32 Gui and get the window
ExitProgram 		= 0												;Flag to determine when to exit
TypeList$ 			= "Ron#Jeff#Test Wall#Test Floor#"
GuiSetTypeList( TypeList$ )

;-----------------------------------------------------------------------------------------------------------
;TYPES
;ViewPort Type
Type GuiViewPort
	Field CameraPivot												;Viewport Camera Pivot
	Field Camera													;Viewport Camera
	Field ViewType													;Viewport Type eg( Perspective, Top, Back...)
	Field Xpos														;Starting Xpos of Viewport
	Field Ypos														;Starting Ypos of Viewport
	Field Width													;Width of Viewport
	Field Height													;Height of Viewport
End Type

;Create array of 4 Viewports
Dim ViewPorts.GuiViewPort(3)											;Create an Array of  4 Viewports

;Create the first Viewport and Set the Parameters
ViewPorts(0) 			= New GuiViewPort								;Create new Viewport type
ViewPorts(0)\CameraPivot 	= CreatePivot()									;Viewport Camera Pivot
ViewPorts(0)\Camera 		= CreateCamera( ViewPorts(0)\CameraPivot )			;Create the Viewport Camera
ViewPorts(0)\ViewType	= 1											;Set Viewport Type to Perspective
ViewPorts(0)\Xpos 		= 0											;Set Starting Xpos of Viewport 
ViewPorts(0)\Ypos		= 0											;Set Starting Ypos of Viewport 
ViewPorts(0)\Width 		= GraphicsWidth()								;Set Starting Width of Viewport 
ViewPorts(0)\Height 		= GraphicsHeight()								;Set Starting Height of Viewport 

;Set the Viewport with Viewport Parameters
CameraViewport ViewPorts(0)\Camera,ViewPorts(0)\Xpos,ViewPorts(0)\Ypos,ViewPorts(0)\Width,ViewPorts(0)\Height
CameraClsColor ViewPorts(0)\Camera,128,128,128							;Set Viewport Cls Color

;Set current viewport
CurrentViewPort 	= ViewPorts(0)\Camera 								;Set the Current Viewport
CurrentViewPortPivot 	= ViewPorts(0)\CameraPivot							;Set the Current Viewport Pivot
PositionEntity CurrentViewPort ,0,8,-50

;-----------------------------------------------------------------------------------------------------------
;SCENERY
;Cube and light
cube		= CreateCube(CurrentViewPortPivot)								;Create a reference cube
PositionEntity cube,EntityX(CurrentViewPortPivot),EntityY(CurrentViewPortPivot),EntityZ(CurrentViewPortPivot)
light 		= CreateLight()												;Create a default light

;Grid
;Create the Grid Sprite and load it's texture from file
GridTop		= LoadSprite( "C:\GLITCH\GLITCH GAME EDITOR\GLITCH Blitz Files\Grid Texture.bmp" )
GridTexture	= LoadTexture( "C:\GLITCH\GLITCH GAME EDITOR\GLITCH Blitz Files\Grid Texture.bmp",2)
ScaleSprite GridTop,100,100											;Scale the Grid Top									
SpriteViewMode GridTop,2												;Set the Grid's orientaion to be indepndant of the camera
RotateEntity GridTop,90,0,0											;Rotate the Grid Top 90 degrees on the x axis so it faces the top
ScaleTexture GridTexture,0.1,0.1											;Scale the Grid Texutue to a smaller size
EntityTexture GridTop,GridTexture										;Texture the Grid Top
EntityFX GridTop,1													;Set the grid to be at full bright
GridBottom 	= CopyEntity( GridTop,GridTop )								;Create a copy of Grid top but a grid for the bottom
RotateEntity GridBottom,180,0,0											;Rotate the new grid so it faces the bottom

;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

;	MAIN LOOP

;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
While ExitProgram = 0												;Continue while user hasn't hit escape
	;-----------------------------------------------------------------------------------------------------------
	Event = CallDLL( Win32Dll$,"GetEvent" )								;Get the current input event
	
	;-----------------------------------------------------------------------------------------------------------
	;Set the mouse for input
	MouseXSpeed()
	MouseYSpeed()
	NewMouseZ = MouseZSpeed()
		
	;-----------------------------------------------------------------------------------------------------------
	;Act upon the current event
	Select Event 
		Case 1													;User Input to Exit
			ExitProgram = 1												;End Program
		Case 2													;Input New
			;Start a new program
			GuiMsgBox("Windows Message Box","Testing Windows Message Box",1)
		Case 3
			;Open a file
			GuiGetString()											;Get the file path for opening a file
			gFileToUse$ = gRecivedString$
			LoadWorldSection( 1,1 )
		Case 4
			;Save file
			GuiGetString()											;Get the file path for saving a file
		Case 5
			;Save file As
			GuiGetString()											;Get the file path for saving a file as
	End Select 
	
	;----------------------------------------------------------------------------------------------------------
		;camera movement
		If  MouseHit(3)
			HidePointer
			While MouseHit(1) = 0 Or MouseHit(3)
				Xpos# 	= EntityX( CurrentViewPortPivot )
				Ypos# 	= EntityY( CurrentViewPortPivot )
				Zpos# 	= EntityZ( CurrentViewPortPivot )
				
				Pitch# 	= EntityPitch( CurrentViewPortPivot  ) + MouseYSpeed()
				Yaw# 	= EntityYaw( CurrentViewPortPivot  ) + MouseXSpeed()
				RotateEntity CurrentViewPortPivot,Pitch#,Yaw#,0

				
				;-----------------------------------------------------------------------------------------------------------
				;If user wants to move the position of the viewport pivot
				If KeyDown( 42 )
					If KeyDown( 200 )=True Then MoveEntity CurrentViewPortPivot,0,0,1
					If KeyDown( 208 )=True Then MoveEntity CurrentViewPortPivot,0,0,-1
				Else
					If KeyDown( 200 )=True Then MoveEntity CurrentViewPortPivot,0,1,0
					If KeyDown( 208 )=True Then MoveEntity CurrentViewPortPivot,0,-1,0
				EndIf 
				If KeyDown( 203 )=True Then MoveEntity CurrentViewPortPivot,-1,0,0
				If KeyDown( 205 )=True Then MoveEntity CurrentViewPortPivot,1,0,0
				
				;-----------------------------------------------------------------------------------------------------------
				;Update the graphics
				UpdateWorld
				RenderWorld
				Flip
				
				;-----------------------------------------------------------------------------------------------------------
				;Stop all camera movement 
				MoveEntity CurrentViewPortPivot,0,0,0
			Wend
			ShowPointer
		ElseIf NewMouseZ > 0 Or KeyHit( 30 ) And EntityDistance( CurrentViewPort, CurrentViewPortPivot ) >= 1
			MoveEntity CurrentViewPort,0,0,NewMouseZ
		ElseIf NewMouseZ < 0 Or KeyHit( 44 )	
			MoveEntity CurrentViewPort,0,0,NewMouseZ
		EndIf 
		
		
				;-----------------------------------------------------------------------------------------------------------
				;SAVE THIS BIT OF CODE FOR WALK AROUND MODE
				;If user wants to move the camera around 
				;If KeyDown( 17 )=True Then MoveEntity CurrentViewPort,0,0,1
				;If KeyDown( 31 )=True Then MoveEntity CurrentViewPort,0,0,-1
				;If KeyDown( 30 )=True Then MoveEntity CurrentViewPort,-1,0,0
				;If KeyDown( 32 )=True Then MoveEntity CurrentViewPort,1,0,0
				;PositionEntity CurrentViewPort,0,8,-50
				;PointEntity CurrentViewPort,CurrentViewPortPivot
	;-----------------------------------------------------------------------------------------------------------
	;Draw Everything to screen
	;TurnEntity cube,0,1,0
	UpdateWorld
	RenderWorld
	Text 0,0, "Current Event: " + Event, False,False 							;Print the current event on screen
	Text 0,10,"File String: " + gRecivedString$,False,False						;Print the selected File path on screen
	Flip
Wend
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

CallDLL( Win32Dll$,"EndWin32" )										;Call the DLL to End the use of the Win32 Library
End																;Exit the program

;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

;	FUNCTIONS

;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
Function GuiParseText( PassedString$, Bank )
	NewTitle$ 		= PassedString$									;create a temp copy of the passed$ to manipulate
	Char$ 		= PassedString$									;create a second temp copy to manipulate
	StringLength	= Len( PassedString$ ) + 5								;get the length of the passed string, and add 5. 4 the byte size of the int we will pass, and 1 for the NULL
	DebugLog "String Length with NULL character: " + ( StringLength - 4 )
	BankLocation	= BankSize( Bank )
	ResizeBank Bank,( BankSize( Bank) + StringLength )						;resize the passed bank to the size of the string 
	PokeInt Bank,BankLocation,( StringLength-4 )							;put an integer in the bank holding the number of characters in the string
	LoopEnd 		= BankLocation + (( StringLength-5 )-1 )					;get the end of the loop that will put the rest of the characters in the bank
	
	;parse the passed text
	For Counter_1 	= BankLocation To LoopEnd
		Char$ 	= Left( NewTitle$,1 )				
		DebugLog "char: " + Char$			
		NewTitle$ 	= Right(NewTitle$,Len( NewTitle$ )-1)
		PokeByte Bank,( Counter_1+4 ),Asc( Char$ )
	Next
End Function
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
Function GuiMsgBox( Title$="", Msg$="", Button=0 )
	BankIn 	= CreateBank()											;create a bank to pass the DLL the needed function
	BankOut 	= CreateBank( 1 )										;create a bank to recive what user pressed
	GuiParseText( Title$,BankIn )										;put the title text in the bank but first parse the text
	GuiParseText( Msg$,BankIn )										;put the message text in the bank but parse it also
	ResizeBank BankIn, ( BankSize(BankIn)+1 )							;resize the bank by one value
	PokeByte BankIn,( BankSize(BankIn)-1 ),Button							;put the type of message box value in the bank
	Result 	= CallDLL( Win32Dll$,"MsgBox",BankIn )						;call the dll to show the message box
	DebugLog "result: " + Result
End Function
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
Function GuiGetString()
	BankIn 	= CreateBank(4)											;Bank to send in the DLL function
	BankOut 	= CreateBank(256)										;Bank with information when we get out of the DLL function
	CallDLL( Win32Dll$,"GetString",BankIn,BankOut )						;Get the current input event
	For counter1 = 0 To 256											;Start counter
		If PeekByte( BankOut, counter1 ) = 0 Then Exit						;If the value in the bank is zero then exit loop
		gRecivedString$ = gRecivedString$ + Chr$( PeekByte( BankOut,counter1 ) )		;Get the file path
	Next
	gRecivedString$ = Trim( gRecivedString$ )								;Get rid of any trailing or leading "spaces" in the string
End Function
;||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
Function GuiSetTypeList( TypeList$ )
	BankIn 	= CreateBank()											;create a bank to pass the DLL the needed function
	BankOut 	= CreateBank( 1 )										;create a bank to recive what user pressed
	GuiParseText( TypeList$,BankIn )									;put the title text in the bank but first parse the text
	ResizeBank BankIn, ( BankSize(BankIn)+1 )							;resize the bank by one value
	Result 	= CallDLL( Win32Dll$,"MsgBox",BankIn )						;call the dll to show the message box
	Return Result
End Function
