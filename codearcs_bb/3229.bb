; ID: 3229
; Author: RemiD
; Date: 2015-11-11 15:25:29
; Title: get available resolutions for fullscreen mode
; Description: an example on how to get the available resolutions for fullscreen mode where the width is superior to the height and height/width >= 0.5 and width/height <=2 and the colors depth is 32bits

Graphics3D(640,480,32,2)

Global ResolutionsCount%
Dim ResolutionPWidth%(100)
Dim ResolutionPHeight%(100)

For i% = 1 To CountGfxModes()
 ;DebugLog("")
 ;DebugLog("GfxModeWidth("+i+") = "+GfxModeWidth(i))
 ;DebugLog("GfxModeHeight("+i+") = "+GfxModeHeight(i))
 ;DebugLog("GfxModeDepth("+i+") = "+GfxModeDepth(i))
 If( GfxModeWidth(i) >= 640 And GfxModeHeight(i) >= 480 And GfxModeDepth(i) = 32 And Float(GfxModeHeight(i))/Float(GfxModeWidth(i)) >= 0.5 And Float(GfxModeWidth(i))/Float(GfxModeHeight(i)) <= 2 And GfxModeWidth(i) > GfxModeHeight(i) )
  ResolutionsCount = ResolutionsCount + 1
  Id% = ResolutionsCount
  ResolutionPWidth(Id) = GfxModeWidth(i)
  ResolutionPHeight(Id) = GfxModeHeight(i)
  Print( "Resolution("+Str(Id)+") = "+Str(ResolutionPWidth(Id))+"x"+Str(ResolutionPHeight(Id)) )
 EndIf
Next

WaitKey()

End()
