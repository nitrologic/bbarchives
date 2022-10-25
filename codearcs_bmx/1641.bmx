; ID: 1641
; Author: sswift
; Date: 2006-03-13 01:04:20
; Title: RenderState.Push() + Pop() Methods
; Description: Push and pop the current render state off a stack!  Saves and restores all render parameters.  Scale, rotation, alpha, color, etc!

Type RenderState

	Global RenderStateList:TList = CreateList()
				
	Field Alpha#
	Field Blend
	Field ClsColor_R, ClsColor_G, ClsColor_B
	Field Color_R, Color_G, Color_B
	Field Handle_X#, Handle_Y#
	Field ImageFont:TImageFont
	Field LineWidth#
	Field MaskColor_R, MaskColor_G, MaskColor_B
	Field Origin_X#, Origin_Y#
	Field Rotation#
	Field Scale_X#, Scale_Y#
	Field Viewport_X, Viewport_Y, Viewport_Width, Viewport_Height


	' -------------------------------------------------------------------------------------------------------------------------------------------------------
	' These methods allow you to save and restore the current render settings
	'
	' Each time you call the push method, the current state is placed on the stack.
	' Each time you call the pop method, the last state placed on the stack is restored and removed from the stack.
	' -------------------------------------------------------------------------------------------------------------------------------------------------------

		
		Function Push()

			Local RS:RenderState = New RenderState

			RS.Alpha# = GetAlpha#()
			RS.Blend  = GetBlend()
			GetClsColor(RS.ClsColor_R, RS.ClsColor_G, RS.ClsColor_B)
			GetColor(RS.Color_R, RS.Color_G, RS.Color_B) 
			GetHandle(RS.Handle_X#, RS.Handle_Y#)
			RS.ImageFont = GetImageFont()
			RS.LineWidth# = GetLineWidth#()
			GetMaskColor(RS.MaskColor_R, RS.MaskColor_G, RS.MaskColor_B)
			GetOrigin(RS.Origin_X#, RS.Origin_Y#)
			RS.Rotation# = GetRotation#()
			GetScale(RS.Scale_X#, RS.Scale_Y#)
			GetViewport(RS.Viewport_X, RS.Viewport_Y, RS.Viewport_Width, RS.Viewport_Height)
		
			RenderStateList.AddLast(RS)
		
		End Function		


		Function Pop()
		
			Local RS:RenderState = RenderState(RenderStateList.RemoveLast())	
				
			SetAlpha(RS.Alpha#)
			SetBlend(RS.Blend)
			SetClsColor(RS.ClsColor_R, RS.ClsColor_G, RS.ClsColor_B)
			SetColor(RS.Color_R, RS.Color_G, RS.Color_B) 
			SetHandle(RS.Handle_X#, RS.Handle_Y#)
			SetImageFont(RS.ImageFont)
			SetLineWidth(RS.LineWidth#)
			SetMaskColor(RS.MaskColor_R, RS.MaskColor_G, RS.MaskColor_B)
			SetOrigin(RS.Origin_X#, RS.Origin_Y#)
			SetRotation(RS.Rotation#)
			SetScale(RS.Scale_X#, RS.Scale_Y#)
			SetViewport(RS.Viewport_X, RS.Viewport_Y, RS.Viewport_Width, RS.Viewport_Height)

		End Function


End Type
