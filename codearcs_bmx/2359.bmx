; ID: 2359
; Author: TwoCorin517
; Date: 2008-11-11 03:46:38
; Title: Fade In/Out
; Description: Fade In/Out Type

'This BMX file was edited with BLIde ( http://www.blide.org )
Rem
	bbdoc:Undocumented type
End Rem
Const MILLI_PER_FRAME = 50	'Calculated: 1,000 / FPS

Type TFadeImage
	Function Cycle(FadeImg:TFadeImage)	'Use this to go through a cycle, the data will be taken from the FadeImage so whatever you set there is what goes in here.
		Repeat
'			WaitTimer(MainTimer)	'Not necessary, but I suggest that you limit how quickly you want this to go. All the hard code is for 20 FPS. If you want it truly accurate you have to restrict it to 20 FPS or change the values.
			Cls
		
			FadeImg.Update()	'Update the Fading image.
			FadeImg.Draw()		'Draw the Fading image.
			
			Flip
		Until FadeImg.State = 3 Or KeyHit(KEY_ESCAPE) Or MouseHit(1)	'Until its done, escape is pressed, or the left-mouse button has been pressed.
	End Function
	
	Field Img:TImage			'This is the source image
	Field TimeTransition:Int	'Amount of time to get from image is barely visible to fully visible, and vice versa.
	Field TimeInBetween:Int		'Amount of time to stay fully visible
	Field State:Int				'0 = Fade In, 1 = In Between, 2 = Fade Out, 3 = Finished
	Field _TimeLeft:Int			'How much time is left. Not advisable to mess with.
	Field _CurrentAlpha:Float	'How far along it is in Alpha. Not advisable to mess with.
	
	Method Create(NewImg:TImage, NewTimeTransition:Int, NewTimeInBetween:Int)	'Initialize
		Img = NewImg
		TimeTransition = NewTimeTransition
		TimeInBetween = NewTimeInBetween
		State = 0	'Get the state to fade in
		_TimeLeft:Int = TimeTransition	'Get it started
		_CurrentAlpha = 0	'Set the Alpha
	End Method
	
	Method Update()
		Local Degree:Float			'How much to increase, or decrease the alpha
		_TimeLeft:-MILLI_PER_FRAME	'Update the countdown
		If _TimeLeft <= 0			'If the countdown for this stage is finished
			Select State
				Case 0				'If it just finished Fading in
					State = 1		'Set it to remain
					_TimeLeft = TimeInBetween	'Reset the countdown
				Case 1				'If it just finished remaining
					State = 2		'Set it to fade out
					_TimeLeft = TimeTransition	'Reset the countdown
				Case 2				'If it just finished fading out
					State = 3		'Tell it that its done
			End Select
		EndIf
		Select State
			Case 0	'If its fading in
				Degree = 1 / Float(TimeTransition / MILLI_PER_FRAME)	'See how much is needed. See an equal amount gained.
				_CurrentAlpha:+Degree	'Add what's needed
			Case 2
				Degree = 1 / Float(TimeTransition / MILLI_PER_FRAME)	'See how much is needed. See an equal amount lost.
				_CurrentAlpha:-Degree	'Subtract what's needed
		End Select
	End Method
	
	Method Draw()
		Local PrevAlpha:Float = GetAlpha()	'See what the alpha was before tampering
		
		SetAlpha(_CurrentAlpha)	'Set it up for this image
		DrawImage(Img, 0, 0)	'Draw the image
		SetAlpha(PrevAlpha)		'Restore to its former setting
	End Method
End Type
