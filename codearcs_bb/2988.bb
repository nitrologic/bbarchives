; ID: 2988
; Author: Xaymar
; Date: 2012-10-23 11:18:03
; Title: EnumerateDisplays
; Description: Get Information about all Displays avaible to the Computer your Program runs on. (C++ DLL)

;----------------------------------------------------------------
;-- Userlib
;----------------------------------------------------------------
;.lib "Utility_Displays.dll"
;Utility_EnumerateDisplays():"Utility_EnumerateDisplays"
;Utility_GetDisplayCount%():"Utility_GetDisplayCount"
;Utility_GetDisplay(id%, rectangle*):"Utility_GetDisplay"
;----------------------------------------------------------------

;----------------------------------------------------------------
;-- C++ DLL
;----------------------------------------------------------------
; #Include <windows.h>
; 
; struct Display {
;     int left;
;     int top;
;     int right;
;     int bottom;
;     Display* nextDisplay;
;     Display* prevDisplay;
; };
; Display* firstDisplay = NULL;
; Display* lastDisplay = NULL;
; 
; BOOL CALLBACK _EnumerateDisplaysProcedure(HMONITOR hMonitor, HDC hdcMonitor, LPRECT lprcMonitor, LPARAM dwData);
; STDAPIV_(void) Utility_EnumerateDisplays() {
;     /* Clean up the Linked List first. */
;     if (firstDisplay) {
;         Display* displayPointer = firstDisplay;
;         while(displayPointer) {
;             Display* thisDisplay = displayPointer;
;             displayPointer = displayPointer->nextDisplay;
;             delete thisDisplay;
;         }
;         firstDisplay = NULL;
;         lastDisplay = NULL;
;     }
; 
;     EnumDisplayMonitors(NULL, NULL, _EnumerateDisplaysProcedure, 0);
; }
; 
; BOOL CALLBACK _EnumerateDisplaysProcedure(HMONITOR hMonitor, HDC hdcMonitor, LPRECT lprcMonitor, LPARAM dwData) {
;     Display* thisDisplay = new Display;
;     ZeroMemory(thisDisplay,sizeof(thisDisplay));
; 
;     if (!firstDisplay) firstDisplay = thisDisplay;
;     if (!lastDisplay) {
;         lastDisplay = thisDisplay;
;     } else {
;         lastDisplay->nextDisplay = thisDisplay;
;         thisDisplay->prevDisplay = lastDisplay;
;     }
;     thisDisplay->left = lprcMonitor->left;
;     thisDisplay->top = lprcMonitor->top;
;     thisDisplay->right = lprcMonitor->right;
;     thisDisplay->bottom = lprcMonitor->bottom;
;     lastDisplay = thisDisplay;
; 
;     return TRUE;
; }
; 
; STDAPIV_(int) Utility_GetDisplayCount() {
;     int displayCount = 0;
;     Display* displayPointer = firstDisplay;
;     while (displayPointer) {
;         displayCount++;
;         displayPointer = displayPointer->nextDisplay;
;     }
;     return displayCount;
; }
; 
; STDAPIV_(void) Utility_GetDisplay(int displayId, LPRECT display) {
;     int displayCount = 0;
;     Display* displayPointer = firstDisplay;
;     while (displayPointer) {
;         if ((displayCount == displayId) && (display) && (displayPointer)) {
;             display->left = displayPointer->left;
;             display->top = displayPointer->top;
;             display->right = displayPointer->right;
;             display->bottom = displayPointer->bottom;
;         }
;         displayCount++;
;         displayPointer = displayPointer->nextDisplay;
;     }
; }
; 
; BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved) {return TRUE;}
;----------------------------------------------------------------
; Linker Options: -static-libgcc -static-libstdc++
; Linker Libraries: user32
;----------------------------------------------------------------

;----------------------------------------------------------------
;-- Types
;----------------------------------------------------------------
Type Rectangle
	Field X,Y
	Field X2,Y2
End Type
;----------------------------------------------------------------
