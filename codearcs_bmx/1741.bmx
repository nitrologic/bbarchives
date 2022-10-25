; ID: 1741
; Author: xMicky
; Date: 2006-07-02 18:47:14
; Title: Type for Cubic Splines
; Description: Curves and pathes with cubic splines

' gives you a user defined type at hand for easy generating of curves or pathes with cubic splines
Strict

Graphics 640,480,0,0
SetClsColor 77, 77, 77

Local curKubSplineX:appKubSpline =New appKubSpline
Local curKubSplineY:appKubSpline =New appKubSpline

curKubSplineX.GetDataInt([1, 2, 3, 4], [100, 400, 400, 100])
curKubSplineY.GetDataInt([1, 2, 3, 4], [100, 400, 100, 100])

Local rCurve:Int =Rand(0, 255)
Local gCurve:Int =Rand(0, 255)
Local bCurve:Int =255 -rCurve
Local curvStep:Float =.05

Local tPos:Float =1
Local lastTChange:Int =MilliSecs()
Local setNewCurve:Int =False

Local newDataT:Int[]
Local newDataX:Int[]
Local newDataY:Int[]

Local constSpeed:Int =False
Local pixelPerSecond:Float =200

' Main loop
Repeat
  Cls  
    
  If KeyHit(KEY_SPACE)
		setNewCurve =Not(setNewCurve)
    newDataT =newDataT[..0]
 	  newDataX =newDataX[..0]
 	  newDataY =newDataY[..0]
	EndIf
	
	If setNewCurve Then
	  If MouseHit(1) Then
	    newDataT =newDataT[..newDataT.Length +1]
    	newDataX =newDataX[..newDataT.Length]
    	newDataY =newDataY[..newDataT.Length]
      newDataT[newDataT.Length -1] =newDataT.Length
      newDataX[newDataT.Length -1] =MouseX()
      newDataY[newDataT.Length -1] =MouseY()
	  End If 
	
  	SetColor 200, 200, 0
    For Local curT:Float =1 To newDataT.Length
      DrawOval newDataX[curT -1], newDataY[curT -1], 20, 20
    Next

	  If KeyHit(KEY_RETURN)
	    If newDataT.Length >0 Then
      	newDataT =newDataT[..newDataT.Length +1]
    	  newDataX =newDataX[..newDataT.Length]
    	  newDataY =newDataY[..newDataT.Length]
        newDataT[newDataT.Length -1] =newDataT.Length
        newDataX[newDataT.Length -1] =newDataX[0]
        newDataY[newDataT.Length -1] =newDataY[0]

  	    curKubSplineX.GetDataInt(newDataT, newDataX)
        curKubSplineY.GetDataInt(newDataT, newDataY)

        rCurve =Rand(0, 255)
        gCurve =Rand(0, 255)
        bCurve =255 -rCurve
      End If
      setNewCurve =False
	  End If
	End If 

  ' draw the curve
  SetColor rCurve, gCurve, bCurve
  SetLineWidth(5) 
  Local curT:Float =curKubSplineX.dataX[0]
  While curT <curKubSplineX.dataX[curKubSplineX.dataCount -1]
    DrawLine curKubSplineX.ValueInt(curT), curKubSplineY.ValueInt(curT), curKubSplineX.ValueInt(curT +curvStep), curKubSplineY.ValueInt(curT +curvStep)
    curT :+curvStep
  Wend

  ' draw the given points
  SetColor 0, 200, 0
  For Local curT:Float =curKubSplineX.dataX[0] To curKubSplineX.dataX[curKubSplineX.dataCount -1]
    DrawOval curKubSplineX.ValueInt(curT), curKubSplineY.ValueInt(curT), 20, 20
  Next

  ' draw a point running through the curve
  SetColor 255, 0, 0
  DrawOval curKubSplineX.ValueInt(tPos), curKubSplineY.ValueInt(tPos), 20, 20

  If constSpeed Then
    If KeyHit(KEY_V) Then
      constSpeed =False
    End If
    Local nextTPos:Float
    Local curDist:Float =0
    For nextTPos =tPos To tPos +curKubSplineX.dataX[curKubSplineX.dataCount -1] -.001 Step .001
      curDist :+Sqr((curKubSplineX.Value(nextTPos +.001) -curKubSplineX.Value(nextTPos)) ^2 +(curKubSplineY.Value(nextTPos +.001) -curKubSplineY.Value(nextTPos)) ^2) 
      If curDist =>pixelPerSecond Then
        Exit
      End If 
    Next
    tPos :+(nextTPos -tPos) *(MilliSecs() -lastTChange) /1000 
    lastTChange =MilliSecs()
  Else 
    If KeyHit(KEY_C) Then
      constSpeed =True
      lastTChange =MilliSecs()
    End If
    tPos :+.001 *Float(curKubSplineX.dataCount)
    If tPos >curKubSplineX.dataX[curKubSplineX.dataCount -1] Then
      tPos =curKubSplineX.dataX[0]
    End If
  End If

  SetColor 255, 255, 255
  Local t:String
  If Not(setNewCurve) Then
     t= "Space - Start new curve (set points with left mouse clicks)"
	  DrawText t, 10, 10
	Else  
  	t ="Return -finish New curve"
	  DrawText t, 10, 10
	End If  
	If Not(constSpeed) Then 
  	t ="c -change to constant speed 200 pixel/second"
	  DrawText t, 10, 30
	Else
		t ="v -change to variable speed depending on the points distances"
	  DrawText t, 10, 30
	End If

  Flip

Until KeyHit(KEY_ESCAPE)

End
'==================================================================================================================================
Type appKubSpline
  Field dataX:Float[]
  Field dataY:Float[]
  Field dataCount:Int =0
  Field koeffB:Float[]
  Field koeffC:Float[]
  Field koeffD:Float[]
  '------------------------------------------------------------------------------------------------------------
  ' gets data as FLOAT and calculates the cubic splines
  ' if x-, y-arrays size is different, only the smaller count is taken
  ' data must be sorted uprising for x
  Method GetData(x:Float[], y:Float[])

    Local count:Int =Min(x.Length, y.Length)
    dataX =x[..]
    dataX =x[..count]
    dataY =y[..]
    dataY =y[..count]
    koeffB =koeffB[..count]
    koeffC =koeffC[..count]
    koeffD =koeffD[..count]

    Local m:Int =count -2
    Local s:Float
    Local r:Float
    For Local i:Int =0 To m
      koeffD[i] =dataX[i +1] -dataX[i]
      r =(dataY[i +1] -dataY[i]) /koeffD[i]
      koeffC[i] =r -s
      s =r 
    Next    
    s =0
    r =0
    koeffC[0] =0
    koeffC[count -1] =0
    For Local i:Int =1 To m
      koeffC[i] =koeffC[i] +r *koeffC[i -1]
      koeffB[i] =(dataX[i -1] -dataX[i +1]) *2 -r *s
      s =koeffD[i]
      r =s /koeffB[i]
    Next
    For Local i:Int =m To 1 Step -1
      koeffC[i] =(koeffD[i] *koeffC[i +1] -koeffC[i]) /koeffB[i]
    Next
    For Local i:Int =0 To m
      s =koeffD[i]
      r =koeffC[i +1] -koeffC[i]
      koeffD[i] =r /s
      koeffC[i] =koeffC[i] *3
      koeffB[i] =(dataY[i +1] -dataY[i]) /s -(koeffC[i] +r) *s
    Next

    dataCount =count

  End Method
  '------------------------------------------------------------------------------------------------------------
  ' gets data as INT and calculates the cubic splines
  ' if x-, y-arrays size is different, only the smaller count is taken
  ' data must be sorted uprising for x
  Method GetDataInt(x:Int[], y:Int[])

    Local count:Int =Min(x.Length, y.Length)
    
    dataX =dataX[..count]
    For Local z:Int =1 To count
      dataX[z -1] =Float(x[z -1])
    Next
    dataY =dataY[..count]
    For Local z:Int =1 To count
      dataY[z -1] =Float(y[z -1])
    Next
    koeffB =koeffB[..count]
    koeffC =koeffC[..count]
    koeffD =koeffD[..count]

    Local m:Int =count -2
    Local s:Float
    Local r:Float
    For Local i:Int =0 To m
      koeffD[i] =dataX[i +1] -dataX[i]
      r =(dataY[i +1] -dataY[i]) /koeffD[i]
      koeffC[i] =r -s
      s =r 
    Next    
    s =0
    r =0
    koeffC[0] =0
    koeffC[count -1] =0
    For Local i:Int =1 To m
      koeffC[i] =koeffC[i] +r *koeffC[i -1]
      koeffB[i] =(dataX[i -1] -dataX[i +1]) *2 -r *s
      s =koeffD[i]
      r =s /koeffB[i]
    Next
    For Local i:Int =m To 1 Step -1
      koeffC[i] =(koeffD[i] *koeffC[i +1] -koeffC[i]) /koeffB[i]
    Next
    For Local i:Int =0 To m
      s =koeffD[i]
      r =koeffC[i +1] -koeffC[i]
      koeffD[i] =r /s
      koeffC[i] =koeffC[i] *3
      koeffB[i] =(dataY[i +1] -dataY[i]) /s -(koeffC[i] +r) *s
    Next

    dataCount =count

  End Method
  '------------------------------------------------------------------------------------------------------------
  ' returns kubic splines value as FLOAT at given x -position 
   'or always 0 if currently no data is loaded
  Method Value:Float(x:Float)

    If dataCount =0 Then Return 0

    If x <dataX[0] Then 
      Repeat
        x :+dataX[dataCount -1] -dataX[0]
      Until x =>dataX[0]
    ElseIf x >dataX[dataCount -1] Then
      Repeat
        x :-dataX[dataCount -1] -dataX[0]
      Until x <=dataX[dataCount -1]
    End If

    Local q:Float =Sgn(dataX[dataCount -1] -dataX[0])
    Local k:Int =-1
    Local i:Int
    Repeat
      i =k
      k :+1
    Until (q *x <q *dataX[k]) Or k =dataCount -1

    q =x - dataX[i]
    Return ((koeffD[i] *q +koeffC[i]) *q +koeffB[i]) *q +dataY[i]

  End Method
  '------------------------------------------------------------------------------------------------------------
  ' returns kubic splines value as rounded INT at given x -position 
   'or always 0 if currently no data is loaded
  Method ValueInt:Int(x:Float)

    If dataCount =0 Then Return 0

    If x <dataX[0] Then 
      Repeat
        x :+dataX[dataCount -1] -dataX[0]
      Until x =>dataX[0]
    ElseIf x >dataX[dataCount -1] Then
      Repeat
        x :-dataX[dataCount -1] -dataX[0]
      Until x <=dataX[dataCount -1]
    End If

    Local q:Float =Sgn(dataX[dataCount -1] -dataX[0])
    Local k:Int =-1
    Local i:Int
    Repeat
      i =k
      k :+1
    Until (q *x <q *dataX[k]) Or k =dataCount -1

    q =x - dataX[i]
    Local tmpResult:Float =((koeffD[i] *q +koeffC[i]) *q +koeffB[i]) *q +dataY[i]
    If tmpResult -Floor(tmpResult) <=.5 Then
      Return Floor(tmpResult)
    Else
      Return Floor(tmpResult) +1
    End If

  End Method
  '------------------------------------------------------------------------------------------------------------

End Type
'------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
