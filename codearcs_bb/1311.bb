; ID: 1311
; Author: sswift
; Date: 2005-03-07 07:33:43
; Title: GetSystemColor()
; Description: Uses windows GDI functions to tell you what color different interface elements in windows are.

; -----------------------------------------------------------------------------------------------------------------------------------
; To use these functions, add the following to 'User32.decls' in your userlib folder:
;
; 	.lib "User32.dll"
;	GetSysColor%(Color%):"GetSysColor" 
;
; Example:
;
; 	R = GetSysColorR(COLOR_MENU)
; -----------------------------------------------------------------------------------------------------------------------------------


Const COLOR_SCROLLBAR 				= 0		; Scroll bar gray area.
Const COLOR_BACKGROUND 				= 1		; Desktop.
Const COLOR_DESKTOP					= 1
Const COLOR_ACTIVECAPTION			= 2		; Active window title bar.
											; Specifies the left side color in the color gradient of an active window's title bar
											; if the gradient effect is enabled.  (Except Windows NT and Windows 95)
Const COLOR_INACTIVECAPTION			= 3		; Inactive window caption.
											; Specifies the left side color in the color gradient of an inactive window's
											; title bar if the gradient effect is enabled.
											; Windows NT and Windows 95:  This remark does not apply.
Const COLOR_MENU 					= 4		; Menu background.
Const COLOR_WINDOW 					= 5		; Window background.
Const COLOR_WINDOWFRAME 			= 6		; Window frame.
Const COLOR_MENUTEXT				= 7		; Text in menus.
Const COLOR_WINDOWTEXT				= 8		; Text in windows.
Const COLOR_CAPTIONTEXT 			= 9		; Text in caption, size box, And scroll bar arrow box.
Const COLOR_ACTIVEBORDER			= 10	; Active window border.
Const COLOR_INACTIVEBORDER			= 11	; Inactive window border.
Const COLOR_APPWORKSPACE 			= 12	; Background color of multiple document interface (MDI) applications.
Const COLOR_HIGHLIGHT				= 13	; Item(s) selected in a control.
Const COLOR_HIGHLIGHTTEXT			= 14	; Text of item(s) selected in a control.
Const COLOR_3DFACE					= 15	; Face color For three-dimensional display elements and for dialog box backgrounds.
Const COLOR_BTNFACE					= 15	
Const COLOR_3DSHADOW				= 16	; Shadow color for three-dimensional display elements  
Const COLOR_BTNSHADOW				= 16	; (For edges facing away fromthe light source.)
Const COLOR_GRAYTEXT				= 17	; Grayed (disabled) text. This color is set to 0 if the current display driver
											; does not support a solid gray color.
Const COLOR_BTNTEXT					= 18	; Text on push buttons.
Const COLOR_INACTIVECAPTIONTEXT 	= 19	; Color of text in an inactive caption.
Const COLOR_3DHILIGHT				= 20	; Highlight color For three-dimensional display elements. 
Const COLOR_3DHIGHLIGHT				= 20	; (For edges facing the light source.)
Const COLOR_BTNHILIGHT				= 20
Const COLOR_BTNHIGHLIGHT			= 20
Const COLOR_3DDKSHADOW				= 21 	; Dark shadow for three-dimensional display elements.
Const COLOR_3DLIGHT 				= 22	; Light color for three-dimensional display elements
											; (For edges facing the light source.)
Const COLOR_INFOTEXT 				= 23	; Text color for tooltip controls.
Const COLOR_INFOBK 					= 24	; Background color fFor tooltip controls.
Const COLOR_ALTERNATEBTNFACE		= 25	; I could not find this in the windows documenation.  A few websites that have info
											; on WINE for Linux listed this contant.  I'm not sure if windows uses it though.
Const COLOR_HOTLIGHT				= 26	; Color For a hot-tracked item. Single clicking a hot-tracked item executes the item.
											; Windows NT And Windows 95:  This value is not supported.
Const COLOR_GRADIENTACTIVECAPTION	= 27	; Right side color in the color gradient of an active window's title bar.
											; COLOR_ACTIVECAPTION specifies the left side Color.
											; Use SPI_GETGRADIENTCAPTIONS with the SystemParametersInfo function
											; to determine whether the gradient effect is enabled.
											; Windows NT And Windows 95:  This value is not supported.
Const COLOR_GRADIENTINACTIVECAPTION = 28	; Right side color in the color gradient of an inactive window's title bar.
											; COLOR_INACTIVECAPTION specifies the left side Color.
											; Windows NT And Windows 95:  This value is not supported.
Const COLOR_MENUHILIGHT 			= 29	; The color used to highlight menu items when the menu appears as a flat menu
											; (see SystemParametersInfo). The highlighted menu item is outlined with COLOR_HIGHLIGHT.
											; Windows 2000/NT and Windows Me/98/95:  This value is not supported.
Const COLOR_MENUBAR 				= 30	; The background color for the menu bar when menus appear as flat menus 
											; (see SystemParametersInfo).  However, COLOR_MENU continues to specify
											; the background color of the menu popup.
											; Windows 2000/NT and Windows Me/98/95:  This value is not supported.


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the red portion of the specified system color.
; -----------------------------------------------------------------------------------------------------------------------------------
Function GetSysColorR(SystemColor)
        Return (GetSysColor(SystemColor) And $000000FF) 
End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the green portion of the specified system color.
; -----------------------------------------------------------------------------------------------------------------------------------
Function GetSysColorG(SystemColor)
	Return (GetSysColor(SystemColor) And $0000FF00) Shr 8
End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the blue portion of the specified system color.
; -----------------------------------------------------------------------------------------------------------------------------------
Function GetSysColorB(SystemColor)
	Return (GetSysColor(SystemColor) And $00FF0000) Shr 16 
End Function
