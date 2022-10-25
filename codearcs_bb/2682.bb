; ID: 2682
; Author: Serpent
; Date: 2010-03-30 10:41:24
; Title: Screenshot Functions
; Description: Take a screenshot of the screen - not just the blitz basic program.  Quick PrintScreen type screenshot.

;  WORKING SCREENSHOT FUNCTIONS
;  Author:  James (aka Serpent)
;
;  Grabs top left corner of screen.
;
;  Usage: 
;  Screenshot2DBuffer(DestinationBuffer, Width, Height)
;  Screenshot3DBuffer(DestinationBuffer, Width, Height)
;
;  DestinationBuffer - the buffer that will eventually be written to.  Can be any buffer (FrontBuffer, BackBuffer, ImageBuffer, TextureBuffer)!
;  Return Value - False if a parameter is 0, otherwise True.
;
;  Note: There is a difference between the two functions!  You must use the Screenshot2DBuffer function for '2D' buffers (i.e. ImageBuffers
;        and the FrontBuffer and BackBuffer in a 2D Graphics Mode), and you must use the Screenshot3DBuffer function for '3D' buffers
;        (i.e. TextureBuffers and the FrontBuffer and BackBuffer in a 3D Graphics Mode).  There is a slight difference regarding the structure in
;        memory of these buffers.  Although I do not know what it is I have worked around it.  3D buffers require a power of 2 size and 2D buffers
;        a width divisible by 16.  The functions take care of this - you can use any size for your buffer as long as you use the correct function.
;
;  MAKE SURE THAT YOU GIVE THE FUNCTION BUFFERS THAT ARE UNLOCKED, NOT LOCKED.  The functions lock and unlock buffers as necessary.
;  Although my testing has found that locking a buffer twice then unlocking it twice works alright, it may cause unexpected results.  If you lock
;  the buffer then the functions won't get what they expect.
;
;
;
;  Timing - For 2D buffers on my computer:
;
;  < 40 ms for a 1680 by 1050 screenshot (almost good enough for 30 screenshots per second at this resolution)
;  18-19 ms for an 800 by 600 screenshot
;
;
;
;  Note:  It is probably useful to call the GetSystemMetrics() function (user32.dll) to find the screen width and height.
;
;
;
;  e.g. - copy entire screen to BackBuffer:
;
;  Screenshot BackBuffer(), GetSystemMetrics(0), GetSystemMetrics(1)
;
;
;  .decls file (not needed for screenshot function, but useful - GetSystemMetrics):
;  ---------------------------
;  .lib "user32.dll"
;  GetSystemMetrics%(nIndex%)
;  ---------------------------
;
;
;
;  Note:  Keep Width and Height parameters positive, otherwise you might experience vertical or horizontal flipping of the
;         image - unless you want to do this for some reason.



Function Screenshot2DBuffer(DestinationBuffer,BufferWidth,BufferHeight)
	
	If DestinationBuffer = 0 Or BufferWidth = 0 Or BufferHeight = 0 Then Return False
	
	OriginalBufferWidth = BufferWidth
	BufferWidth = BufferWidth / 16
	If BufferWidth * 16 <> OriginalBufferWidth Then BufferWidth = (BufferWidth + 1) * 16 Else BufferWidth = OriginalBufferWidth
	
	hDC = GetDC(0)
	hMemDC= CreateCompatibleDC(hdc)
	hMemBmp= CreateCompatibleBitmap(hdc,BufferWidth,BufferHeight)
	
	bmi = CreateBank(48)
	PokeByte bmi,0,44
	PokeInt bmi,4,BufferWidth
	PokeInt bmi,8,-BufferHeight
	PokeByte bmi,12,1
	PokeByte bmi,14,32
	
	SelectObject hMemDC, hMemBmp
	BitBlt hMemDC, 0, 0, BufferWidth, BufferHeight, hDC, 0, 0, $00CC0020
	
	If BufferWidth = OriginalBufferWidth Then
	
		LockBuffer DestinationBuffer
		LocBnk = CreateBank(76)
		MoveMemoryObjInt(LocBnk,DestinationBuffer,76)
		Loc = PeekInt(LocBnk,72)
		FreeBank LocBnk
		GetDIBitsInt hDC,hMemBmp,0,BufferHeight,Loc,bmi,0
		UnlockBuffer DestinationBuffer
		
	Else
		
		TemporaryImage = CreateImage(BufferWidth,BufferHeight)
		TemporaryBuffer = ImageBuffer(TemporaryImage)
		LocBnk = CreateBank(76)
		LockBuffer TemporaryBuffer
		MoveMemoryObjInt(LocBnk,TemporaryBuffer,76)
		Loc = PeekInt(LocBnk,72)
		FreeBank LocBnk
		GetDIBitsInt hDC,hMemBmp,0,BufferHeight,Loc,bmi,0
		UnlockBuffer TemporaryBuffer
		CopyRect 0,0,OriginalBufferWidth,BufferHeight,0,0,TemporaryBuffer,DestinationBuffer
		
	EndIf
	
	ReleaseDC 0, hDC
	DeleteDC hMemDC
	DeleteObject hMemBmp
	
	FreeBank bmi
	Return True
	
End Function

Function Screenshot3DBuffer(DestinationBuffer,BufferWidth,BufferHeight)
	
	If DestinationBuffer = 0 Or BufferWidth = 0 Or BufferHeight = 0 Then Return False
	
	OriginalBufferWidth = BufferWidth
	Shifts = -1
	Repeat
		BufferWidth = BufferWidth Shr 1
		Shifts = Shifts + 1
	Until BufferWidth = 0
	BufferWidth = 1 Shl Shifts
	
	If BufferWidth < OriginalBufferWidth Then BufferWidth = BufferWidth Shl 1
	
	hDC = GetDC(0)
	hMemDC= CreateCompatibleDC(hdc)
	hMemBmp= CreateCompatibleBitmap(hdc,BufferWidth,BufferHeight)
	
	bmi = CreateBank(48)
	PokeByte bmi,0,44
	PokeInt bmi,4,BufferWidth
	PokeInt bmi,8,-BufferHeight
	PokeByte bmi,12,1
	PokeByte bmi,14,32
	
	SelectObject hMemDC, hMemBmp
	BitBlt hMemDC, 0, 0, BufferWidth, BufferHeight, hDC, 0, 0, $00CC0020
	
	If BufferWidth = OriginalBufferWidth Then
		
		LockBuffer DestinationBuffer
		LocBnk = CreateBank(76)
		MoveMemoryObjInt(LocBnk,DestinationBuffer,76)
		Loc = PeekInt(LocBnk,72)
		FreeBank LocBnk
		GetDIBitsInt hDC,hMemBmp,0,BufferHeight,Loc,bmi,0
		UnlockBuffer DestinationBuffer
		
	Else
		
		TemporaryImage = CreateImage(BufferWidth,BufferHeight)
		TemporaryBuffer = ImageBuffer(TemporaryImage)
		LocBnk = CreateBank(76)
		LockBuffer TemporaryBuffer
		MoveMemoryObjInt(LocBnk,TemporaryBuffer,76)
		Loc = PeekInt(LocBnk,72)
		FreeBank LocBnk
		GetDIBitsInt hDC,hMemBmp,0,BufferHeight,Loc,bmi,0
		UnlockBuffer TemporaryBuffer
		CopyRect 0,0,OriginalBufferWidth,BufferHeight,0,0,TemporaryBuffer,DestinationBuffer
		
	EndIf
	
	ReleaseDC 0, hDC
	DeleteDC hMemDC
	DeleteObject hMemBmp
	
	FreeBank bmi
	Return True
	
End Function
