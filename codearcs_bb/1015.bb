; ID: 1015
; Author: skn3[ac]
; Date: 2004-05-06 14:00:48
; Title: Draw arrow. Draw triangle functions
; Description: 2 self contained functions. Draw arrow has variable head and line size

Graphics 640,480,0,2
SetBuffer BackBuffer()
Repeat
	Cls
	Color 255,255,255
	DrawArrow(320,240,MouseX(),MouseY(), 640,480, 4,35)
	Flip
Until KeyDown(1)

Function DrawArrow(x1,y1,x2,y2,clipwidth,clipheight,linewidth=3,headsize=9)
	Local tempx#,tempy#,steps,stempx#,stepy#,drawx#,drawy#,drawstep#,tempangle#,headx1#,heady1#,headx2#,heady2#,headx3#,heady3#
	tempx# = Abs(x1 - x2)
	tempy# = Abs(y1 - y2)
	If tempx# = 0 Or tempy# = 0
		If tempx# = 0 And tempy# = 0
			steps = 0
		Else
			If tempx# = 0
				steps  = tempy#
				stepx# = 0
				If y1 < y2
					stepy# = 1
				Else
					stepy# = -1
				End If
			Else
				steps  = tempx#
				If x1 < x2
					stepx# = 1
				Else
					stepx# = -1
				End If
				stepy# = 0
			End If
		End If
	Else
		If tempx# < tempy#
			steps  = tempy#
			If x1 < x2
				stepx# = tempx# / tempy#
			Else
				stepx# = 0 - (tempx# / tempy#)
			End If
			If y1 < y2
				stepy# = 1
			Else
				stepy# = -1
			End If
		Else
			steps  = tempx#
			If x1 < x2
				stepx# = 1
			Else
				stepx# = -1
			End If
			If y1 < y2
				stepy# = tempy# / tempx#
			Else
				stepy# = 0 - (tempy# / tempx#)
			End If
		End If
	End If
	drawx# = x1
	drawy# = y1
	If steps > headsize
		For drawstep# = 0 To steps-headsize Step 1
			Oval drawx#-(linewidth Shr 1),drawy#-(linewidth Shr 1),linewidth,linewidth,1
			drawx# = drawx# + (stepx#)
			drawy# = drawy# + (stepy#)
			If drawx# > clipwidth-(linewidth Shr 1) Exit
			If drawx# < -(linewidth Shr 1) Exit
			If drawy# > clipheight-(linewidth Shr 1) Exit
			If drawy# < -(linewidth Shr 1) Exit
		Next
	End If
	tempangle# = ATan2((y2-y1),(x2-x1))
	headx1# = x2 - (Cos#(tempangle#)*(headsize Shl 1)) + (Cos#(tempangle#-90)*headsize)
	heady1# = y2 - (Sin#(tempangle#)*(headsize Shl 1)) + (Sin#(tempangle#-90)*headsize)
	headx2# = x2 - (Cos#(tempangle#)*(headsize Shl 1)) + (Cos#(tempangle#+90)*headsize)
	heady2# = y2 - (Sin#(tempangle#)*(headsize Shl 1)) + (Sin#(tempangle#+90)*headsize)
	headx3# = x2
	heady3# = y2
	DrawTriangle(headx1#,heady1#,headx2#,heady2#,headx3#,heady3#,clipwidth,clipheight)
End Function

Function DrawTriangle(x1,y1,x2,y2,x3,y3,clipwidth,clipheight)
	Local topx,topy,midx,midy,botx,boty,mstep1#,mstep2#,x#,y#,fromy,toy#,edge1,edge2
	If y1 <= y2
		If y2 <= y3
			topx = x1 : topy = y1 : midx = x2 : midy = y2 : botx = x3 : boty = y3
		Else
			If y1 <= y3
				topx = x1 : topy = y1 : midx = x3 : midy = y3 : botx = x2 : boty = y2
			Else
				topx = x3 : topy = y3 : midx = x1 : midy = y1 : botx = x2 : boty = y2
			End If
		End If
	Else
		If y2 <= y3
			If y1 <= y3
				topx = x2 : topy = y2 : midx = x1 : midy = y1 : botx = x3 : boty = y3
			Else
				topx = x2 : topy = y2 : midx = x3 : midy = y3 : botx = x1 : boty = y1
			End If
		Else
			topx = x3 : topy = y3 : midx = x2 : midy = y2 : botx = x1 : boty = y1
		EndIf
	End If
	If botx-topx=0
		mstep1# = 0
	Else
		If boty-topy=0
			mstep1# = 0
		Else
			mstep1# = Float#(botx-topx)/Float#(boty-topy)
		End If
	End If
	x#    = topx
	fromy = topy
	toy   = boty
	If fromy<0
		x#    = x#+(mstep1#*Abs(toy))
		fromy = 0
	End If
	If toy > clipheight-1
		toy = clipheight-1
	End If
	For y = fromy To toy
		edge1 = x#
		If edge1 > clipwidth
			edge1 = clipwidth
		ElseIf edge1 < 0
			edge1 = 0
		End If
		If y < midy
			If midx-topx=0
				mstep2# = 0
			Else
				If midy-topy=0
					mstep2# = 0
				Else
					mstep2# = Float#(midx-topx)/Float#(midy-topy)
				End If
			End If
			edge2 = topx + (mstep2# * (y-topy))
			If edge2 > clipwidth
				edge2 = clipwidth
			ElseIf edge2 < 0
				edge2 = 0
			End If
		Else
			If botx-midx=0
				mstep2# = 0
			Else
				If boty-midy=0
					mstep2# = 0
				Else
					mstep2# = Float#(botx-midx)/Float#(boty-midy)
				End If
			End If
			edge2 = midx + (mstep2# * (y-midy))
			If edge2 > clipwidth
				edge2 = clipwidth
			ElseIf edge2 < 0
				edge2 = 0
			End If
		End If
		If edge1 <> edge2 Line edge1,y,edge2,y
		x#=x#+mstep1#
	Next
End Function
