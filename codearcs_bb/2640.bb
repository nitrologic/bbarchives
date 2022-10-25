; ID: 2640
; Author: ShadowTurtle
; Date: 2010-01-17 07:28:26
; Title: Timeline-event based entity tweening ( position, rotate, size, color, alpha )
; Description: see title and long description (open the code!)

; ** Globals/Constants: Tween
	Type tweenLib_tween
		Field time_start
		Field time_length
		
		Field obj
		Field editMode
		Field tweenMode
		
		Field coordX_A#, coordY_A#, coordZ_A#
		Field coordX_B#, coordY_B#, coordZ_B#
		Field isRotation

		Field tweenNext.tweenLib_tween
	End Type
	Global tweenLib_lastCreatedTween.tweenLib_tween
	Global tweenLib_pauseLength

	Const tweenLib_tweenMode_void = 0
	Const tweenLib_tweenMode_linear = 1
	Const tweenLib_tweenMode_abrupt = 2
	Const tweenLib_tweenMode_backEaseIn = 3
	Const tweenLib_tweenMode_backEaseOut = 4
	Const tweenLib_tweenMode_backEaseInOut = 5
	Const tweenLib_tweenMode_elasticEaseIn = 6
	Const tweenLib_tweenMode_elasticEaseOut = 7
	Const tweenLib_tweenMode_elasticEaseInOut = 8
	Const tweenLib_tweenMode_bounceEaseOut = 9
	Const tweenLib_tweenMode_bounceEaseIn = 10
	Const tweenLib_tweenMode_bounceEaseInOut = 11
	Const tweenLib_tweenMode_regularEaseIn = 12
	Const tweenLib_tweenMode_regularEaseOut = 13
	Const tweenLib_tweenMode_regularEaseInOut = 14
	Const tweenLib_tweenMode_strongEaseIn = 15
	Const tweenLib_tweenMode_strongEaseOut = 16
	Const tweenLib_tweenMode_strongEaseInOut = 17

	Const tweenLib_editMode_void = 0
	Const tweenLib_editMode_position = 1
	Const tweenLib_editMode_size = 2, tweenLib_editMode_scale = 2
	Const tweenLib_editMode_angle = 3
	Const tweenLib_editMode_alpha = 4
	Const tweenLib_editMode_color = 5
	
	Const tweenLib_editMode_position2D = 6
	Const tweenLib_editMode_size2D = 7, tweenLib_editMode_scale2D = 7
	
	Const tweenLib_editMode_screenColor = 8
	Const tweenLib_editMode_screenAlpha = 9

	Const tweenLib_editMode_soundVolume = 10
	Const tweenLib_editMode_globalSoundVolume = 11

	Const tweenLib_editMode_alpha2d = 12
	Const tweenLib_editMode_color2d = 13
	
	; ** Tween management functions
	Function tweenLib_init()
		tweenLib_lastCreatedTween = Null
	End Function

	Function tweenLib_update()
		Local tw.tweenLib_tween
		Local interpolationsPoint#

		Local coordX#, coordY#, coordZ#
		
		For tw = Each tweenLib_tween
			;DebugLog app_frameTime_ms + ">" + tw\time_start + "   " + app_frameTime_ms + "<" + (tw\time_start + tw\time_length)
			If app_frameTime_ms > tw\time_start Then
				If app_frameTime_ms > tw\time_start + tw\time_length - 1 And 1 = 0 Then
					interpolationsPoint# = 1.0
				Else
					interpolationsPoint# = (100.0 / Float(tw\time_length) * (app_frameTime_ms - tw\time_start)) / 100.0
					interpolationsPoint# = tweenLib_interpolationTween#(tweenLib_tweenMode_strongEaseInOut, interpolationsPoint#)
					interpolationsPoint# = tweenLib_interpolationTween#(tw\tweenMode, interpolationsPoint#)
					interpolationsPoint# = interpolationsPoint# - (interpolationsPoint# / 100) ;- 0.01
				End If
				
				If tw\editMode = tweenLib_editMode_angle Then
					coordX# = tweenLib_interpolateAngle(tw\coordX_A#, tw\coordX_B#, interpolationsPoint#)
					coordY# = tweenLib_interpolateAngle(tw\coordY_A#, tw\coordY_B#, interpolationsPoint#)
					coordZ# = tweenLib_interpolateAngle(tw\coordZ_A#, tw\coordZ_B#, interpolationsPoint#)
				Else
					coordX# = tw\coordX_A# + ((tw\coordX_B# - tw\coordX_A#) * interpolationsPoint#)
					coordY# = tw\coordY_A# + ((tw\coordY_B# - tw\coordY_A#) * interpolationsPoint#)
					coordZ# = tw\coordZ_A# + ((tw\coordZ_B# - tw\coordZ_A#) * interpolationsPoint#)
				End If

				If app_frameTime_ms > tw\time_start + tw\time_length Then
					If coordX# > tw\coordX_B# Then tw\coordX_B# = tw\coordX_B#
					If coordY# > tw\coordY_B# Then tw\coordY_B# = tw\coordY_B#
					If coordZ# > tw\coordZ_B# Then tw\coordZ_B# = tw\coordZ_B#

					If coordX# < tw\coordX_A# Then tw\coordX_A# = tw\coordX_A#
					If coordY# < tw\coordY_A# Then tw\coordY_A# = tw\coordY_A#
					If coordZ# < tw\coordZ_A# Then tw\coordZ_A# = tw\coordZ_A#
				End If
				
				If tw\editMode = tweenLib_editMode_position Then
					PositionEntity tw\obj, coordX#, coordY#, coordZ#
				ElseIf tw\editMode = tweenLib_editMode_size Then
					ScaleEntity tw\obj, coordX#, coordY#, coordZ#
				ElseIf tw\editMode = tweenLib_editMode_angle Then
					RotateEntity tw\obj, coordX#, coordY#, coordZ#
				ElseIf tw\editMode = tweenLib_editMode_alpha Then
					EntityAlpha tw\obj, coordX#
				ElseIf tw\editMode = tweenLib_editMode_color Then
					EntityColor tw\obj, coordX#, coordY#, coordZ#
				End If
				
				If app_frameTime_ms > tw\time_start + tw\time_length Then Delete tw
			End If
		Next
	End Function
	
	Function tweenLib_uninit()
		tweenLib_stopTweens()
	End Function

	; ** Tween definition functions (like move, rotate etc.)
	Function tweenLib_position.tweenLib_tween(obj, editMode, coordX_A#, coordY_A#, coordZ_A#, coordX_B#, coordY_B#, coordZ_B#, beginTime, runLength, tweenMode)
		Local tw.tweenLib_tween

		tweenLib_pauseLength = 0
		
		tw = New tweenLib_tween

		tw\time_start = app_frameTime_ms + beginTime + tweenLib_pauseLength
		tw\time_length = runLength

		tw\obj = obj
		tw\editMode = editMode
		tw\tweenMode = tweenMode

		tw\coordX_A# = coordX_A#
		tw\coordY_A# = coordY_A#
		tw\coordZ_A# = coordZ_A#
		
		tw\coordX_B# = coordX_B#
		tw\coordY_B# = coordY_B#
		tw\coordZ_B# = coordZ_B#

		tw\isRotation = False

		tw\tweenNext = Null
		
		tweenLib_lastCreatedTween = tw

		tweenLib_pauseLength = 0
		
		Return tw
	End Function

	Function tweenLib_insertTween(coordX_B#, coordY_B#, coordZ_B#, runLength, tweenMode)
		Local tw.tweenLib_tween

		tw = New tweenLib_tween

		tw\time_start = tweenLib_lastCreatedTween\time_start + tweenLib_lastCreatedTween\time_length + tweenLib_pauseLength
		tw\time_length = runLength

		tw\obj = tweenLib_lastCreatedTween\obj
		tw\editMode = tweenLib_lastCreatedTween\editMode
		tw\tweenMode = tweenMode

		tw\coordX_A# = tweenLib_lastCreatedTween\coordX_B#
		tw\coordY_A# = tweenLib_lastCreatedTween\coordY_B#
		tw\coordZ_A# = tweenLib_lastCreatedTween\coordZ_B#

		tw\coordX_B# = coordX_B#
		tw\coordY_B# = coordY_B#
		tw\coordZ_B# = coordZ_B#

		tw\isRotation = tweenLib_lastCreatedTween\isRotation

		tw\tweenNext = Null

		tweenLib_lastCreatedTween\tweenNext = tw
		tweenLib_lastCreatedTween = tw

		tweenLib_pauseLength = 0
	End Function
	
	Function tweenLib_insertTweenPause(runLength)
		tweenLib_pauseLength = runLength
	End Function
	
	Function tweenLib_stopTweens()
		Local foundTween
		Local tw.tweenLib_tween
		
		foundTween = True
		While foundTween = True
			foundTween = False
			For tw.tweenLib_tween = Each tweenLib_tween
				Delete tw
				foundTween = True
			Next
		Wend
	End Function

	; ** Tween functions
	Function tweenLib_interpolationTween#(tweenMode, interpolationsPoint#)
		Local t#, b#, c#, d#, s#, a#, p#, pi_#
		Local tmp#, v#
		
		pi_# = 3.141592653589793 
		
		t# = interpolationsPoint# * 100
		d# = 100
		b# = 1
		c# = 100

		If tweenMode = tweenLib_tweenMode_linear Then
			v# = c#*t#/d#+b#
		ElseIf tweenMode = timeLine_action__tweenMode_abrupt Then
			v# = c#*t#/d#+b#
			If v# > 50 Then
				v# = 100
			Else
				v# = 0
			End If
		ElseIf tweenMode = tweenLib_tweenMode_backEaseIn Then
			s# = 1.70158
			t#=t#/d#
			v# = c#*t#*t#*((s#+1)*t#-s#)+b#
		ElseIf tweenMode = tweenLib_tweenMode_backEaseOut Then
			s# = 1.70158
			tmp# = t#/d#-1
			t# = tmp#
			v# = c#*(tmp#*t#*((s#+1)*t#+s#)+1)+b#
		ElseIf tweenMode = tweenLib_tweenMode_backEaseInOut Then
			s# = 1.70158
			If t#/(d#/2) < 1 Then
				t# = t#/(d#/2)
				s#=s#*(1.525)
				v# = c#/2*(t#*t#*(((s#)+1)*t# - s#)) + b#
			Else
				t# = t#/(d#/2)
				s#=s#*(1.525)
				t#=t#-2
				v# = c#/2*(t#*t#*(((s#)+1)*t# + s#) + 2) + b#
			End If
		ElseIf tweenMode = timeLine_action__tweenMode_elasticEaseIn Then ; DOES NOT WORK CORRECT
			a# = 0
			If t# = 0 Then
				v# = b#
			Else
				If t#/d# <> 1 Then
					t#=t#/d#
					p# = d# * 0.3
					If a# < Abs(c#) Then
						a# = c#
						s# = p# / 4
					Else
						s# = p# / (2 * pi_#) * ASin(c# / a#)
					End If
					
					t# = t# - 1
					tmp# = 2^(10*t#)
					;t# = t# - 1
					
					v# = -(a#*tmp# * Sin( (t#*d#-s#)*(2*pi_#)/p# )) + b#
					v#=v#*t#
				Else
					v# = b# + c#
				End If
			End If
		ElseIf tweenMode = tweenLib_tweenMode_elasticEaseOut Then ; DOES NOT WORK CORRECT
		ElseIf tweenMode = tweenLib_tweenMode_elasticEaseInOut Then ; DOES NOT WORK CORRECT
		ElseIf tweenMode = tweenLib_tweenMode_bounceEaseOut Then
			t#=t#/d#
			If t# < 1 / 2.75 Then
				v# = c# * (7.5625*t#*t#) + b#
			ElseIf t# < 2 / 2.75 Then
				t# = t# - ( 1.5 / 2.75 )
				v# = c# * (7.5625*(t#*t#) + 0.75) + b#
			ElseIf t# < 2.5 / 2.75 Then
				t# = t# - ( 2.25 / 2.75 )
				v# = c# * (7.5625*(t#*t#) + 0.9375) + b#
			Else
				t# = t# - ( 2.625 / 2.75 )
				v# = c# * (7.5625*(t#*t#) + 0.984375) + b#
			End If
		ElseIf tweenMode = tweenLib_tweenMode_bounceEaseIn Then
			t# = d# - t#
			b# = 0
			c# = c#
			d# = d#
			
			t#=t#/d#
			If t# < 1 / 2.75 Then
				v# = c# * (7.5625*t#*t#) + b#
			ElseIf t# < 2 / 2.75 Then
				t# = t# - ( 1.5 / 2.75 )
				v# = c# * (7.5625*(t#*t#) + 0.75) + b#
			ElseIf t# < 2.5 / 2.75 Then
				t# = t# - ( 2.25 / 2.75 )
				v# = c# * (7.5625*(t#*t#) + 0.9375) + b#
			Else
				t# = t# - ( 2.625 / 2.75 )
				v# = c# * (7.5625*(t#*t#) + 0.984375) + b#
			End If
			
			v# = c# - v# + b#
		ElseIf tweenMode = tweenLib_tweenMode_bounceEaseInOut Then
			If t# < d# / 2 Then
				t# = t# * 2
				b# = 0
				c# = c#
				d# = d#
				
				t# = d# - t#
				b# = 0
				c# = c#
				d# = d#
				
				t#=t#/d#
				If t# < 1 / 2.75 Then
					v# = c# * (7.5625*t#*t#) + b#
				ElseIf t# < 2 / 2.75 Then
					t# = t# - ( 1.5 / 2.75 )
					v# = c# * (7.5625*(t#*t#) + 0.75) + b#
				ElseIf t# < 2.5 / 2.75 Then
					t# = t# - ( 2.25 / 2.75 )
					v# = c# * (7.5625*(t#*t#) + 0.9375) + b#
				Else
					t# = t# - ( 2.625 / 2.75 )
					v# = c# * (7.5625*(t#*t#) + 0.984375) + b#
				End If
				
				v# = c# - v# + b#
				v# = v# * 0.5 + b#
			Else
				t# = t# * 2 - d#
				b# = 0
				c# = c#
				d# = d#
				
				t#=t#/d#
				If t# < 1 / 2.75 Then
					v# = c# * (7.5625*t#*t#) + b#
				ElseIf t# < 2 / 2.75 Then
					t# = t# - ( 1.5 / 2.75 )
					v# = c# * (7.5625*(t#*t#) + 0.75) + b#
				ElseIf t# < 2.5 / 2.75 Then
					t# = t# - ( 2.25 / 2.75 )
					v# = c# * (7.5625*(t#*t#) + 0.9375) + b#
				Else
					t# = t# - ( 2.625 / 2.75 )
					v# = c# * (7.5625*(t#*t#) + 0.984375) + b#
				End If
				
				v# = v# * 0.5 + c# * 0.5 + b#
			End If
		ElseIf tweenMode = tweenLib_tweenMode_regularEaseIn Then
			t#=t#/d#
			v#=c#*t#*t#+b#
		ElseIf tweenMode = tweenLib_tweenMode_regularEaseOut Then
			t#=t#/d#
			v#=-c#*t#*(t#-2)+b#
		ElseIf tweenMode = tweenLib_tweenMode_regularEaseInOut Then
			t#=t#/(d#/2)
			If t# < 1 Then
				v# = c#/2*t#*t#+b#
			Else
				tmp#=t#-1
				v# = -c#/2*(tmp#*(tmp#-2)-1)+b#
			End If
		ElseIf tweenMode = tweenLib_tweenMode_strongEaseIn Then
			t# = t# / d#
			v# = c#*t#*t#*t#*t#*t#+b#
		ElseIf tweenMode = tweenLib_tweenMode_strongEaseOut Then
			t# = (t# / d#)-1
			v# = c#*(t#*t#*t#*t#*t#+1)+b#
		ElseIf tweenMode = tweenLib_tweenMode_strongEaseInOut Then
			t#=t#/(d#/2)
			If t# < 1 Then
				v# = c#/2*t#*t#*t#*t#*t#+b#
			Else
				t#=t#-2
				v# = c#/2*(t#*t#*t#*t#*t#+2)+b#
			End If
		End If
		
		Return v# / 100
	End Function

	Function tweenLib_interpolateAngle#(a#,b#,blend#=0.5)
		ix# = Sin(a)
		iy# = Cos(a)
		jx# = Sin(b)
		jy# = Cos(b)
		Return ATan2(ix-(ix-jx)*blend,iy-(iy-jy)*blend)
	End Function
	
	Function tweenLib_makeCorrectAngle#(ang#)
		ang# = ang# - 180
		If ang# > 180 Then
			While ang# > 180
				ang# = ang# - 360
			Wend
		ElseIf ang# < (0-180) Then
			While ang# < (0-180)
				ang# = ang# + 360
			Wend
		End If
		ang# = ang# + 180
		
		
		Return ang#
	End Function
