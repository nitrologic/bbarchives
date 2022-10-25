; ID: 2989
; Author: Xaymar
; Date: 2012-10-23 11:22:59
; Title: CloseHandler
; Description: Catch the WM_CLOSE event before Blitz handles it itself.

;----------------------------------------------------------------
;-- Userlib
;----------------------------------------------------------------
;.lib "User32.dll"
;User32_SetWindowLong%(hwnd%, nIndex%, dwNewLong%):"SetWindowLongA"
;User32_GetWindowLong%(hwnd%, index%):"GetWindowLongA"
;
;.lib "Utility_CloseHandler.dll"
;Utility_InstallCloseHandler(hwnd%):"Utility_InstallCloseHandler"
;Utility_UninstallCloseHandler(hwnd%):"Utility_UninstallCloseHandler"
;Utility_GetCloseCount%(hwnd%):"Utility_GetCloseCount"
;----------------------------------------------------------------

;----------------------------------------------------------------
;-- C++ DLL
;----------------------------------------------------------------
; #Include <windows.h>
; 
; LRESULT CALLBACK _CloseWindowProcedure(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam);
; 
; struct WindowUserData {
;      Int oldWindowProcedure;
;      Int oldUserData;
;      Int closeCount;
; };
; 
; STDAPIV_(void) Utility_InstallCloseHandler(HWND hwnd) {
;     If (hwnd) {
;         WindowUserData* hwndData = New WindowUserData;
;         ZeroMemory(hwndData, sizeof(hwndData));
;         hwndData->oldWindowProcedure = SetWindowLong(hwnd, GWL_WNDPROC, (LONG)&_CloseWindowProcedure);
;         hwndData->oldUserData = SetWindowLong(hwnd, GWL_USERDATA, (LONG)hwndData);
;     }
; }
; 
; STDAPIV_(void) Utility_UninstallCloseHandler(HWND hwnd) {
;     If (hwnd) {
;         WindowUserData* hwndData = (WindowUserData*)GetWindowLong(hwnd, GWL_USERDATA);
;         If (hwndData) {
;             SetWindowLong(hwnd, GWL_USERDATA, hwndData->oldUserData);
;             SetWindowLong(hwnd, GWL_WNDPROC, hwndData->oldWindowProcedure);
;             Delete hwndData;
;         }
;     }
; }
; 
; STDAPIV_(Int) Utility_GetCloseCount(HWND hwnd) {
;     If (hwnd) {
;         WindowUserData* hwndData = (WindowUserData*)GetWindowLong(hwnd, GWL_USERDATA);
;         If (hwndData) {
;             Int toReturn = hwndData->closeCount;
;             hwndData->closeCount = 0;
;             Return toReturn;
;         }
;     }
;     Return 0;
; }
; 
; LRESULT CALLBACK _CloseWindowProcedure(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam) {
;     WindowUserData* hwndData = (WindowUserData*)GetWindowLong(hwnd, GWL_USERDATA);
;     If (hwndData) {
;         switch(uMsg) {
;             Case WM_CLOSE:
;             Case WM_DESTROY:
;                 hwndData->closeCount++;
;                 Return False;
;             Default:
;                 Return CallWindowProc((WNDPROC)hwndData->oldWindowProcedure, hwnd, uMsg, wParam, lParam);
;         }
;     } Else {
;         Return DefWindowProc(hwnd, uMsg, wParam, lParam);
;     }
; }
; 
; BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved) {return TRUE;}
;----------------------------------------------------------------
; Linker Options: -static-libgcc -static-libstdc++
; Linker Libraries: user32
;----------------------------------------------------------------

SetBuffer BackBuffer()

Utility_InstallCloseHandler(SystemProperty("AppHWND"))

Local c
While Not KeyHit(1)
	c = c + Utility_GetCloseCount(SystemProperty("AppHWND"))
	Cls
	
	Text MouseX(),MouseY(),c
	Flip
Wend
Utility_UninstallCloseHandler(SystemProperty("AppHWND"))
