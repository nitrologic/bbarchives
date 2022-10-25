; ID: 1805
; Author: John Galt
; Date: 2006-09-03 19:52:56
; Title: Programmatically click the mouse
; Description: Inserts mouse clicks in the input buffer

Global GetLastError:Int() "win32"
Global SendInput(nInputs,pInputs,cbSize) "win32"

kernel32 = LoadLibraryA ("kernel32.dll")
If kernel32
	GetLastError=GetProcAddress(kernel32, "GetLastError")
Else
   Print "Kernel32 dead"; End
EndIf

user32 = LoadLibraryA ("user32.dll")
If user32
	SendInput=GetProcAddress(user32, "SendInput")
Else
   Print "User32 dead"; End
EndIf

Rem
UINT SendInput(          UINT nInputs,
    LPINPUT pInputs,
    Int cbSize
);
EndRem

Type Tbuf_input
	Rem
			typedef struct tagINPUT { 
	  DWORD Type; 
	  union {MOUSEINPUT mi; 
	            KEYBDINPUT ki;
	            HARDWAREINPUT hi;
	           };
	  }Input, *PINPUT;
	
	
		typedef struct tagMOUSEINPUT {
	    	LONG dx;
	    	LONG dy;
	    	DWORD mouseData;
	    	DWORD dwFlags;
	    	DWORD time;
	    	ULONG_PTR dwExtraInfo;
		} MOUSEINPUT, *PMOUSEINPUT;
	
		
	EndRem
	
	Rem
	#define MOUSEEVENTF_MOVE        0x0001 /* mouse move */
	#define MOUSEEVENTF_LEFTDOWN    0x0002 /* Left button down */
	#define MOUSEEVENTF_LEFTUP      0x0004 /* Left button up */
	#define MOUSEEVENTF_RIGHTDOWN   0x0008 /* Right button down */
	#define MOUSEEVENTF_RIGHTUP     0x0010 /* Right button up */
	#define MOUSEEVENTF_MIDDLEDOWN  0x0020 /* middle button down */
	#define MOUSEEVENTF_MIDDLEUP    0x0040 /* middle button up */
	#define MOUSEEVENTF_XDOWN       0x0080 /* x button down */
	#define MOUSEEVENTF_XUP         0x0100 /* x button down */
	#define MOUSEEVENTF_WHEEL       0x0800 /* wheel button rolled */
	#define MOUSEEVENTF_VIRTUALDESK 0x4000 /* map To entire virtual Desktop */
	#define MOUSEEVENTF_ABSOLUTE    0x8000 /* absolute move */
	EndRem
	
	
	
	Field _type=0		
	'mouse bits
	Field dx:Int=0		
	Field dy:Int=0
	Field mousedata=0	'int or dword=16 bits
	Field dwFlags=0
	Field time=0
	Field dwExtraInfo:Long Ptr=Null 
	
	Method input_ptr()
		Return Int(Varptr(_type))
	End Method
End Type

Type Tmouse_controller
	Global buffer_input:Tbuf_input=New Tbuf_input
	Global down_events[]=[$2,$8,$20]
	Global up_events[]=[$4,$10,$40]
	Global input_pointer=buffer_input.input_ptr()
	
	Method button_down(button)
		buffer_input.dwFlags=down_events[button-1]
		sendinput(1,input_pointer,28)
	End Method
	
	Method button_up(button)
		buffer_input.dwFlags=up_events[button-1]
		sendinput(1,input_pointer,28)
	End Method
End Type

Graphics 800,600,0
Global mousecont:Tmouse_controller=New Tmouse_controller

Repeat
	Cls
	
	mousecont.button_down(3)
	
	'e=GetLastError()
	'DrawText e,100,500
	If MouseDown(1) DrawText "Button 1 down",100,100
	If MouseDown(2) DrawText "Button 2 down",100,200
	If MouseDown(3) DrawText "Button 3 down",100,300
		
	Flip()
	
Until KeyHit(KEY_ESCAPE)
