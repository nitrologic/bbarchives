; ID: 1257
; Author: Shagwana
; Date: 2005-01-10 04:58:04
; Title: Texture Popper
; Description: B+ Tool to help convert photos into textures

;
;  Program : Texture popper
;
;  Author : Stephen Greener (aka Shagwana)
;           www.sublimegames.com
;
;  Short description :
;
;  Load in a source image
;  Set the 4 points to the area you wish to grab
;  Save the region from the menu
;  Select the size of the area you wish to export
;  Computer will now convert the contence of the region into a nice rectange of your chossing (bmp)
;___________________________________________________________________________________________________________________________________

Const DEBUG_BUILD=False 

Const TESTFILENAME$=""

Const START_WINDOW_WIDTH=700
Const START_WINDOW_HEIGHT=400

Const START_ROI_INSTEP = 3

Const GRAB_SNAP = 250

;__/ Varibles \_____________________________________________________________________________________________________________________

;Data

Global pSourceImage=0
Global iViewXPos=0,iViewYPos=0
Global QuitProgram=False

;What the user is doing
Global bEditing=False         ;User is doing something at the moment?
Global bDraggingScreen=False  ;User is either dragging the screen or dragging a point  
Global iPointEdit=0

Global iSaveWidth=512
Global iSaveHeight=512

Global iMouseXStart=0
Global iMouseYStart=0


;Gadgets
Global pMainWindow=0
Global pMainWindowMenu=0
Global pPanelView=0
Global pMainViewCanvas=0
Global pHorzSlider=0
Global pVertSlider=0
Global pDisplayTimer=0

Global pFileMenu=0



;Vertex on image
Global bVertexValid[4]
Global iVertexXPos[4]
Global iVertexYPos[4]

For n=0 To 3
  bVertexValid[n]=False   ;Clear all the verts
  Next





;__/ Functions \____________________________________________________________________________________________________________________

Function SetupProgram()  ;Setup the gadgets and other gui objects

  ;Setup the main window
  pMainWindow=CreateWindow("Texture popper",(GraphicsWidth()-START_WINDOW_WIDTH)/2,(GraphicsHeight()-START_WINDOW_HEIGHT)/3,START_WINDOW_WIDTH,START_WINDOW_HEIGHT,0,%101111)   ;Main window
  SetMinWindowSize pMainWindow,200,200
  pMainWindowMenu=WindowMenu(pMainWindow)

  pPanelView=CreatePanel(2,2,GadgetWidth(pMainWindow)-30,GadgetHeight(pMainWindow)-88,pMainWindow)
  SetGadgetLayout pPanelView,1,1,1,1

  pMainViewCanvas=CreateCanvas(0,0,GraphicsWidth(),GraphicsHeight(),pPanelView,0)
  SetGadgetLayout pMainViewCanvas,1,0,1,0    ;Clamp the canvas to the top left corner

  pHorzSlider=CreateSlider(0,GadgetHeight(pMainWindow)-84,START_WINDOW_WIDTH-18,18,pMainWindow,1)
  SetGadgetLayout pHorzSlider,1,1,0,1
  pVertSlider=CreateSlider(GadgetWidth(pMainWindow)-26,0,18,START_WINDOW_HEIGHT-18,pMainWindow,0)
  SetGadgetLayout pVertSlider,0,1,1,1

  pDisplayTimer=CreateTimer(10)

  ;Menu...
  pFileMenu=CreateMenu("File",0,pMainWindowMenu)
  CreateMenu "Load Image",1,pFileMenu
  CreateMenu "",2,pFileMenu
  CreateMenu "Save Region",3,pFileMenu

  UpdateWindowMenu pMainWindow

  End Function 

Function InitSourceImage()    ;Load in a source image

  pSourceImage=CreateImage(800,600)   ;Blank usless image!
  SetStatusText(pMainWindow,"www.sublimegames.com")
  iViewXPos=0
  iViewYPos=0

  ;Make the ROI fit inside the image
  iVertexXPos[0]=(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[0]=(ImageHeight(pSourceImage)/START_ROI_INSTEP)
  iVertexXPos[1]=ImageWidth(pSourceImage)-(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[1]=(ImageHeight(pSourceImage)/START_ROI_INSTEP)
  iVertexXPos[2]=ImageWidth(pSourceImage)-(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[2]=ImageHeight(pSourceImage)-(ImageHeight(pSourceImage)/START_ROI_INSTEP)
  iVertexXPos[3]=(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[3]=ImageHeight(pSourceImage)-(ImageHeight(pSourceImage)/START_ROI_INSTEP)

  DrawDisplay()
  End Function 

Function LoadSourceImage(Filename$)    ;Load in a source image
  If pSourceImage<>0
    FreeImage pSourceImage
    EndIf

  pSourceImage=LoadImage(Filename$)
  SetStatusText(pMainWindow,"Loaded source image : "+Filename$)
  iViewXPos=0
  iViewYPos=0

  ;Make the ROI fit inside the image
  iVertexXPos[0]=(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[0]=(ImageHeight(pSourceImage)/START_ROI_INSTEP)
  iVertexXPos[1]=ImageWidth(pSourceImage)-(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[1]=(ImageHeight(pSourceImage)/START_ROI_INSTEP)
  iVertexXPos[2]=ImageWidth(pSourceImage)-(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[2]=ImageHeight(pSourceImage)-(ImageHeight(pSourceImage)/START_ROI_INSTEP)
  iVertexXPos[3]=(ImageWidth(pSourceImage)/START_ROI_INSTEP)
  iVertexYPos[3]=ImageHeight(pSourceImage)-(ImageHeight(pSourceImage)/START_ROI_INSTEP)

  DrawDisplay()
  End Function 

Function DrawDisplay()                 ;update the display where needed
 SetBuffer CanvasBuffer(pMainViewCanvas)

 If pSourceImage<>0


    If GadgetWidth(pPanelView)>ImageWidth(pSourceImage) Then iViewXPos=0
    If GadgetHeight(pPanelView)>ImageHeight(pSourceImage) Then iViewYPos=0


   ;Ensure the view is valid  
    If 0-iViewXPos>0
      iViewXPos=0    ;Should never happen!
      Else
      If (iViewXPos+GadgetWidth(pPanelView))>ImageWidth(pSourceImage)
        iViewXPos=ImageWidth(pSourceImage)-GadgetWidth(pPanelView)
        EndIf
      EndIf

    If 0-iViewYPos>0
      iViewYPos=0    ;Should never happen!
      Else
      If (iViewYPos+GadgetHeight(pPanelView))>ImageHeight(pSourceImage)
        iViewYPos=ImageHeight(pSourceImage)-GadgetHeight(pPanelView)
        EndIf
      EndIf

    ;Redraw the screen
    ClsColor 0,100,0
    Cls
    DrawBlock pSourceImage,0-iViewXPos,0-iViewYPos

    ;Update the slider bars (just in case of window resize)
    SetSliderRange pHorzSlider,GadgetWidth(pPanelView),ImageWidth(pSourceImage)
    SetSliderValue pHorzSlider,iViewXPos
    SetSliderRange pVertSlider,GadgetHeight(pPanelView),ImageHeight(pSourceImage)
    SetSliderValue pVertSlider,iViewYPos

    OverLayArea()

    EndIf

 FlipCanvas pMainViewCanvas 
 End Function

Function OverLayArea()                 ;Draw the ROI (region of intrest)

  ;Check to see if all 4 verts are on screen, before drawing
  For N=0 To 3
    If iVertexXPos[N]<0 Then iVertexXPos[N]=0
    If iVertexYPos[N]<0 Then iVertexYPos[N]=0
    If iVertexXPos[N]>ImageWidth(pSourceImage) Then iVertexXPos[N]=ImageWidth(pSourceImage)
    If iVertexYPos[N]>ImageHeight(pSourceImage) Then iVertexYPos[N]=ImageHeight(pSourceImage)
    Next


  b=False
  If ((MilliSecs()/140) Mod 4)>2 Then b=True


  ;Draw the lines inbetween
  If b=True
    Color 255,0,255
    Else
    Color 0,255,0
    EndIf
  Line iVertexXPos[0]-iViewXPos,iVertexYPos[0]-iViewYPos,iVertexXPos[1]-iViewXPos,iVertexYPos[1]-iViewYPos
  Line iVertexXPos[1]-iViewXPos,iVertexYPos[1]-iViewYPos,iVertexXPos[2]-iViewXPos,iVertexYPos[2]-iViewYPos
  Line iVertexXPos[2]-iViewXPos,iVertexYPos[2]-iViewYPos,iVertexXPos[3]-iViewXPos,iVertexYPos[3]-iViewYPos
  Line iVertexXPos[3]-iViewXPos,iVertexYPos[3]-iViewYPos,iVertexXPos[0]-iViewXPos,iVertexYPos[0]-iViewYPos


  For N=0 To 3
    iX=iVertexXPos[N]-iViewXPos
    iY=iVertexYPos[N]-iViewYPos

    ;Draw the Point
    If b=True
      Color 255,255,255
      Rect iX-1,iY-1,3,3,False
      Color 0,0,0
      Rect iX-3,iY-3,7,7,False
      Else
      Color 0,0,0
      Rect iX-1,iY-1,3,3,False
      Color 255,255,255
      Rect iX-3,iY-3,7,7,False
      EndIf

    Next

  

  End Function

;__/ Main program \_________________________________________________________________________________________________________________
.MainProgram:


  iSaveWidth=512
  iSaveHeight=512

  SetupProgram()
  If DEBUG_BUILD=True
    LoadSourceImage(TESTFILENAME$)
    Else
    InitSourceImage()
    EndIf

  Repeat

    DrawDisplay()

    Ev=WaitEvent()
    Es=EventSource()

    Select Es

      Case pHorzSlider
      Select Ev
        Case $401
        iViewXPos=SliderValue(pHorzSlider)
        End Select

      Case pVertSlider
      Select Ev
        Case $401
        iViewYPos=SliderValue(pVertSlider)
        End Select


      Case pMainViewCanvas
      DoCanvas(Ev)

      Case pMainWindow
      Select Ev
        Case $803
        ;Window close
        QuitProgram=True
        End Select

      Default
      Select Ev
        Case $1001

        Select EventData()
          Case 1
          ;Load
          fn$=RequestFile$("Load source image")
          If fn$<>""
            LoadSourceImage(fn$)
            EndIf
          Case 3
          ;Save

          ;If sfn$<>""
          SaveRegion()
          ;  EndIf

          End Select


        End Select
      End Select


    Until QuitProgram=True

   End

;Calc distance of a line
Function Calc_Distance(XD%,YD%)
  XDelta%=Abs(XD%)
  YDelta%=Abs(YD%)
  Return Sqr((XDelta*XDelta)+(YDelta*YDelta))
  End Function

;Do the magical work on the canvas here
Function DoCanvas(Ev)


  If bEditing=False
    ;Not doing anything at the moment, free to do something...
    
    Select Ev

      Case $201     ;Mouse down in the canvas
      Select EventData()
        Case 1 ;LMB (drag point)
        ;If over point, drag it

        iCloses=(GRAB_SNAP+1)
        bFound=False

        For N=0 To 3
          dist=Calc_Distance(iVertexXPos[N]-(MouseX(pMainViewCanvas)+iViewXPos),iVertexYPos[N]-(MouseY(pMainViewCanvas)+iViewYPos))
          If dist<iCloses
            iCloses=dist
            bFound=True
            iPointEdit=N
            EndIf
          Next


        If bFound=True
          bEditing=True
          bDraggingScreen=False
          n=MouseXSpeed(pMainViewCanvas)  ;First one contains rubbish!
          n=MouseYSpeed(pMainViewCanvas)
          EndIf

        Case 2 ;RMB (move view)
        ;Move screen!
        bEditing=True
        bDraggingScreen=True

        n=MouseXSpeed(pMainViewCanvas)  ;First one contains rubbish!
        n=MouseYSpeed(pMainViewCanvas)

        End Select
      End Select     


    Else
    ;Working on something at the moment
    If bDraggingScreen=False
      ;Moving the screen

      Select Ev

        Case $203

        Select iPointEdit
          Case 0
          SetStatusText(pMainWindow,"Moving top-left point")
          Case 1
          SetStatusText(pMainWindow,"Moving top-right point")
          Case 2
          SetStatusText(pMainWindow,"Moving bottom-right point")
          Case 3
          SetStatusText(pMainWindow,"Moving bottom-left point")
          End Select

        iVertexXPos[iPointEdit]=iVertexXPos[iPointEdit]+MouseXSpeed(pMainViewCanvas)
        iVertexYPos[iPointEdit]=iVertexYPos[iPointEdit]+MouseYSpeed(pMainViewCanvas)

        Case $202
        Select EventData()
          Case 1 ;RMB (move view)
          ;Move screen!
          bEditing=False
            
          End Select

        End Select

      Else
      ;Moving a point


      Select Ev

        Case $203
        iViewXPos=iViewXPos-MouseXSpeed(pMainViewCanvas)
        iViewYPos=iViewYPos-MouseYSpeed(pMainViewCanvas)

        Case $202
        Select EventData()
          Case 2 ;RMB (move view)
          ;Move screen!
          bEditing=False
            
          End Select

        End Select


      EndIf

    EndIf


  End Function

;Pop up window asking for the size of the region to export to, with proceed button
Function SaveRegion()


  ;Get size of the image to save from the user by putting up a new window
  pToolWin=CreateWindow("Save region",GadgetX(pMainWindow)+40,GadgetY(pMainWindow)+40,300,50,pMainWindow,%010001)

  p1=CreateLabel("Width ",10,7,40,20,pToolWin)
  p2=CreateLabel("Height ",120,7,40,20,pToolWin)

  pWGad=CreateTextField(50,4,60,20,pToolWin)
  pHGad=CreateTextField(160,4,60,20,pToolWin)

  pProceed=CreateButton("Save",228,4,60,20,pToolWin)


  SetGadgetText(pWGad,iSaveWidth)
  SetGadgetText(pHGad,iSaveHeight)

  bDone=False
  bDoSave=False
  Repeat
    Ev=WaitEvent()
    Es=EventSource()

    Select Es
   
      Case pProceed

      ;Ensure numbers are valid
      FlushEvents()

      num=TextFieldText(pWGad)

      If num<10 Then num=10
      iSaveWidth=num
      SetGadgetText(pWGad,iSaveWidth)

      ;Ensure numbers are valid
      num=TextFieldText(pHGad)
      If num<10 Then num=10
      iSaveHeight=num
      SetGadgetText(pHGad,iSaveHeight)





      bDoSave=True
      bDone=True
 
      Case pToolWin
      Select Ev
        Case $803    ;Exit save thing
        bDone=True
        End Select


      Case pMainWindow
      ;Quit on the main window!
      Select Ev
        Case $803
        QuitProgram=True
        bDone=True
        End Select
      End Select



    Until bDone=True

  FreeGadget pProceed
  FreeGadget p1
  FreeGadget p2
  FreeGadget pWGad
  FreeGadget pHGad

  FreeGadget pToolWin

  If bDoSave=True

    If DEBUG_BUILD=True
      sfn$=Int(MilliSecs())+".bmp"
      Else
      sfn$=RequestFile$("Save region to","",True)
      EndIf

    SaveRegionAsBMP(sfn$,iSaveWidth,iSaveHeight)   ;Do the needed magic
    EndIf

  End Function

;Convert the area of intrest into another bitmap and save
Function SaveRegionAsBMP(filename$,iSaveWidth,iSaveHeight)

  pSaveImage=CreateImage(iSaveWidth,iSaveHeight,1,2)
  SetBuffer ImageBuffer(pSaveImage)

  ;Do the funcky magic - wip


  ;Top lines maths (top left to top right)
  fstart_xstep#=Float(iVertexXPos[1]-iVertexXPos[0])/Float(iSaveWidth)   ;How much to step every x
  fstart_ystep#=Float(iVertexYPos[1]-iVertexYPos[0])/Float(iSaveWidth)  ;How much to step every y 

  ;Bottom lines maths (bottom left to bottom right)
  fend_xstep#=Float(iVertexXPos[2]-iVertexXPos[3])/Float(iSaveWidth)   ;How much to step every x
  fend_ystep#=Float(iVertexYPos[2]-iVertexYPos[3])/Float(iSaveWidth)  ;How much to step every y 

  
  top_xcoord#=iVertexXPos[0]
  top_ycoord#=iVertexYPos[0]
  bottom_xcoord#=iVertexXPos[3]
  bottom_ycoord#=iVertexYPos[3]


  For xstep=0 To iSaveWidth-1
    ;Step through each pixel that needs saving in the x

    fdown_xstep#=Float(bottom_xcoord#-top_xcoord#)/Float(iSaveHeight)
    fdown_ystep#=Float(bottom_ycoord#-top_ycoord#)/Float(iSaveHeight)

    fdown_xcoord#=top_xcoord#
    fdown_ycoord#=top_ycoord#

    For ystep=0 To iSaveHeight-1

      ;Pull the pixel we want
      SetBuffer ImageBuffer(pSourceImage)
      GetColor(Int(fdown_xcoord),Int(fdown_ycoord))

      ;Set color works per buffer. not global
      r=ColorRed()
      g=ColorGreen()
      b=ColorBlue()

      SetBuffer ImageBuffer(pSaveImage)

      Color r,g,b
      Plot(xstep,ystep)

      fdown_xcoord#=fdown_xcoord#+fdown_xstep#
      fdown_ycoord#=fdown_ycoord#+fdown_ystep#

      Next

    ;Step to the next point of intrest
    top_xcoord#=top_xcoord#+fstart_xstep#
    top_ycoord#=top_ycoord#+fstart_ystep#
    bottom_xcoord#=bottom_xcoord#+fend_xstep#
    bottom_ycoord#=bottom_ycoord#+fend_ystep#

    Next

  FlushEvents()

  ;Working 
  SetStatusText(pMainWindow,"Save region :"+filename$)
  SaveImage(pSaveImage,filename$)
  FreeImage pSaveImage

  End Function
