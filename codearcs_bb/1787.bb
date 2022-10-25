; ID: 1787
; Author: RifRaf
; Date: 2006-08-16 22:10:38
; Title: Sprite Candy Cursor
; Description: two functions for sc

;---------------------------------------------------
;Cursor function added by rifraf ( jeff frazier )
;---------------------------------------------------
Function Gui_LoadCursor(CurFile$,Cam,flag=4)
    If CursorHud<>0 Then 
      HUD_FreeImageResources(Cursorimage)
      HUD_ClearLayer(CursorLayer)
      HUD_RemoveLayer(CursorLayer)
      HUD_Remove(CursorHud)
      cursorimage%=0 
      CursorHud%=0  
    EndIf
    If FileType(curfile$)<>1 Then RuntimeError "Gui_LoadCursor : File "+curfile$+" not found."
    TempImage=LoadImage(curfile$)
    CursorWidth=ImageWidth(Tempimage)-ImageWidth(Tempimage)/2
    CursorHeight=ImageHeight(Tempimage)-ImageHeight(Tempimage)/2
    FreeImage tempimage
	CursorHud%       = HUD_Create (Cam)
	CursorResource%  = HUD_LoadImageResource (curfile$,flag)
	CursorLayer%     = HUD_CreateLayer(cursorhud,cursorresource)
	CursorImage%     = HUD_CreateImage (CursorLayer, 0,0)
    HUD_SetObjectOrder (cursorimage, 2500)
    HUD_SetObjectOrigin (cursorimage, -1,-1)

End Function 
;---------------------------------------------------
;Cursor function added by rifraf ( jeff frazier )
;---------------------------------------------------
Function Gui_UpdateCursor()
    If CursorImage=0 Then RuntimeError "Gui_UpdateCursor : Null Cursor Image"
	HUD_PositionObject Cursorimage,MouseX(),MouseY()
End Function
