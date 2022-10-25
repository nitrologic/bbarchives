; ID: 2698
; Author: AdamRedwoods
; Date: 2010-04-12 01:43:52
; Title: Quicktime for Blitzmax
; Description: Quicktime module for Blitzmax

''
''Quicktime Module for MSWin & MacOSX
''by AdamRedwoods 2011
''

Module addons.quicktime

?Win32
ModuleInfo "CC_OPTS: -IQuickTimeSDK/CIncludes/"
ModuleInfo "CC_OPTS: -IQuickTimeSDK/CIncludes/CoreFoundation"
ModuleInfo "CC_OPTS: -IQuickTimeSDK/CIncludes/GNUCompatibility"
Import "maxquicktime_glue.cpp"
Import "QuickTimeSDK/Libraries/QTMLClient.lib" 
Import "QuickTimeSDK/Libraries/CVClient.lib"
?MacOS
'ModuleInfo "LD_OPTS: -framework/System/Library/Frameworks/QuickTime.framework"
ModuleInfo "LD_OPTS: -framework QuickTime"
Import "maxquicktime_glue.cpp"
?

Import brl.Pixmap
Import brl.glmax2d
Import brl.standardio
Import brl.GLGraphics


Extern 
	
	Function bmx_CreateQT:Byte Ptr() ''cpp class
    	Function EnterMovies() ' Initialize QuickTime 
    	Function ExitMovies() ' Terminate QuickTime
?Win32
    	Function TerminateQTML() 'terminate QTML
	Function InitializeQTML:Int(i:Int)  ' Initialize QTML
?

	Function bmx_OpenMovieFile:Int( qtclass:Byte Ptr, permission:Int) ''return -1 if error
	Function bmx_CloseMovieFile(qtclass:Byte Ptr)
	Function bmx_NewMovieFromFile:Int( qtclass:Byte Ptr, filename:Byte Ptr)
	Function bmx_NewMovieFromURL:Int( qtclass:Byte Ptr, filename:Byte Ptr)
	Function bmx_NewMovieFromFileRef:Int(qtclass:Byte Ptr, flags:Int)
	Function bmx_GetMovieLoadState:Int(qtclass:Byte Ptr)
	
	'''EXTERN_API( ComponentInstance ) NewMovieController(  Movie         theMovie,  Const Rect *  movieRect,  Long someFlags);
	Function bmx_NewMovieController:Int( qtclass:Byte Ptr, flags:Int=0) '' create new movie controller
	
	Function bmx_SetMovieActive(qtclass:Byte Ptr , boolean:Int)
	Function bmx_StartMovie(qtclass:Byte Ptr )
	Function bmx_MoviesTask( qtclass:Byte Ptr, flags:Int=0)
	Function bmx_UpdateMovie:Int(qtclass:Byte Ptr )
	Function bmx_StopMovie(qtclass:Byte Ptr)
	Function bmx_GoToBeginningOfMovie(qtclass:Byte Ptr )
	Function bmx_IsMovieDone(qtclass:Byte Ptr )
	Function bmx_SetMovieVolume ( qtclass:Byte Ptr, volume:Float) ''volume is signed short: -1.0 to 1.0 ''neg volume is silent
	
	Function bmx_GetMovieNextInterestingTime:Int(qtclass:Byte Ptr, flags:Int)
	Function bmx_SetMovieTimeValue(qtclass:Byte Ptr, newtime:Int)
	
	Function bmx_DisposeMovieController (qtclass:Byte Ptr) '                     // Close movie controller, if any
	Function bmx_DisposeMovie (qtclass:Byte Ptr) ' // Destroy movie Object, If any 
	Function bmx_FSMakeFSSpec:Int(qtclass:Byte Ptr, fullPath:Byte Ptr) ''in windows, pass full pathname
	''' FSMakeFSRefUnicode (   Const FSRef *parentRef, UniCharCount nameLength, Const UniChar *name, TextEncoding textEncodingHint,FSRef *newRef)
	''Function FSMakeFSRefUnicode (x:Int=0, filenamelength:Int, name:String, econdingHintConst:Int=0, newf:FSRef)
	Function bmx_NativePathNameToFSSpec:Int(qtclass:Byte Ptr, filename:Byte Ptr, flag:Int=0)
	
	Function bmx_QTNewGWorldFromPtr:Int(qtclass:Byte Ptr, pixelformat:Int, w:Int, h:Int, buffer:Byte Ptr, rowbytes:Int, GWorldFlags:Int=0)
	Function bmx_NewGWorld:Int(qtclass:Byte Ptr, w:Int, h:Int, flags:Int)
	Function bmx_SetMovieGWorld:Int(qtclass:Byte Ptr)
	Function bmx_SetGWorld(qtclass:Byte Ptr)   ' // Port Or Graphics world To make Current ''gdhandle null if gworld
	Function bmx_DisposeGWorld(qtclass:Byte Ptr) ''; //close gworld
	Function bmx_GetGWorldDevice:Int(qtclass:Byte Ptr)
	Function bmx_LockPixels:Int(qtclass:Byte Ptr) ''return true 1 or false 0
	Function bmx_UnlockPixels(qtclass:Byte Ptr)
		
	''Function CopyCStringToPascal(src:String, dst:String)
	Function  c2pstr:Int(src:Byte Ptr)
	
	Function bmx_SetMoviePlayHints(qtclass:Byte Ptr , hints:Int=0, hint2:Int=0)
	'hintsDontUseVideoOverlaySurface
	
	Function GetPixBaseAddr:Byte Ptr(pmhd:Object )
	Function GetGWorldPixMap:Byte Ptr(gw:Byte Ptr )
	'''EXTERN_API( Boolean ) PixMap32Bit(PixMapHandle pmHandle)
	''Function GetPixRowBytes:int(pmhd:Object) ''NOT IN MSWIN

	Function bmx_GetMovieBox(qtclass:Byte Ptr)
	Function bmx_GetMovieWidth(qtclass:Byte Ptr)
	Function bmx_GetMovieHeight(qtclass:Byte Ptr)
	Function bmx_GetMovieDuration:Int(qtclass:Byte Ptr)
	Function bmx_GetMovieTime:Int(qtclass:Byte Ptr)
	Function bmx_GetMovieTimeScale:Int(qtclass:Byte Ptr)
	Function bmx_GetMovieTrackCount:Int(qtclass:Byte Ptr)
	
	Function bmx_PreRollMovie( qtclass:Byte Ptr,  time:Int)
	
		
EndExtern



Const k1MonochromePixelFormat:Int       = $00000001', /* 1 bit indexed*/
Const  k2IndexedPixelFormat:Int          = $00000002', /* 2 bit indexed*/
Const  k4IndexedPixelFormat:Int          = $00000004', /* 4 bit indexed*/
Const  k8IndexedPixelFormat:Int          = $00000008', /* 8 bit indexed*/
Const  k16BE555PixelFormat:Int           = $00000010', /* 16 bit BE rgb 555 (Mac)*/
Const  k24RGBPixelFormat:Int             = $00000018', /* 24 bit rgb */
Const  k32ARGBPixelFormat:Int            = $00000020', /* 32 bit argb    (Mac)*/
Const  k1IndexedGrayPixelFormat:Int      = $00000021', /* 1 bit indexed gray*/
Const  k2IndexedGrayPixelFormat:Int      = $00000022', /* 2 bit indexed gray*/
Const  k4IndexedGrayPixelFormat:Int      = $00000024', /* 4 bit indexed gray*/
Const  k8IndexedGrayPixelFormat:Int      = $00000028' /* 8 bit indexed gray*/
Const  k32BGRAPixelFormat:Int 		= 1111970369
Const 	keepLocal:Int 	=8

Const NEWMOVIEACTIVE:Int = 1 Shl 0

Const mcTopLeftMovie:Int                = 1 Shl 0 ', /* usually centered */
Const mcScaleMovieToFit:Int              = 1 Shl 1 ', /* usually only scales down */
Const mcWithBadge:Int                    = 1 Shl 2 ', /* give me a badge */
Const mcNotVisible:Int                   = 1 Shl 3 ', /* don't show controller */
Const mcWithFrame :Int  			= 1 Shl 4

Const hintsDontUseVideoOverlaySurface:Int = 1 Shl 16
Const hintsOffscreen:Int                = 1 Shl 12
Const hintsUseScreenBuffer:Int          = 1 Shl 5
Const hintsAllowDynamicResize:Int       = 1 Shl 19

Const kInitializeQTMLUseGDIFlag:Int   = 1 Shl 1

Const deviceIsExternalBuffer:Int        = (1 Shl 3)
Const deviceIsGDISurface:Int = 	64
Const deviceIsIndirect:Int = 	1

''nextInterestingTime flags
Const nextTimeStep:Int =  1 Shl 4 
'nextTimeMediaSample ''not recommended since messes up on mpeg
'nextTimeEdgeOK

Const   kMovieLoadStateError:Int                     = -1
Const   kMovieLoadStateLoading:Int                  = 1000
Const   kMovieLoadStateLoaded:Int                    = 2000
Const   kMovieLoadStatePlayable:Int                  = 10000
Const   kMovieLoadStatePlaythroughOK:Int            = 20000
Const   kMovieLoadStateComplete:Int                  = 100000

Rem

hintsScrubMode                = 1 << 0, /* mask == && (if flags == scrub on, flags != scrub off) */
  hintsLoop                     = 1 << 1,
  hintsDontPurge                = 1 << 2,
  hintsUseScreenBuffer          = 1 << 5,
  hintsAllowInterlace           = 1 << 6,
  hintsUseSoundInterp           = 1 << 7,
  hintsHighQuality              = 1 << 8, /* slooooow */
  hintsPalindrome               = 1 << 9,
  hintsInactive                 = 1 << 11,
  hintsOffscreen                = 1 << 12,
  hintsDontDraw                 = 1 << 13,
  hintsAllowBlacklining         = 1 << 14,
  hintsDontUseVideoOverlaySurface = 1 << 16,
  hintsIgnoreBandwidthRestrictions = 1 << 17,
  hintsPlayingEveryFrame        = 1 << 18,
  hintsAllowDynamicResize       = 1 << 19,
  hintsSingleField              = 1 << 20,
  hintsNoRenderingTimeOut       = 1 << 21,
  hintsFlushVideoInsteadOfDirtying = 1 << 22,
  hintsEnableSubPixelPositioning = 1L << 23,
  hintsRenderingMode            = 1L << 24,
  hintsAllowIdleSleep           = 1L << 25, /* asks media handlers not to call UpdateSystemActivity etc */
  hintsDeinterlaceFields        = 1L << 26
EndRem


Type TQuickTime
	Field isQTInit:Int =0
	Field myQTClass:Byte Ptr = Null
	Field debug:Int
	Field _pixmap:TPixmap
	Field texid:Int
	Field imageframe:TGLImageFrame
	
	Method Init:Int(flags:Int=0)
		If (isQTInit) Then Return True
		If StartQT(flags) <> 0 Then Return False

		If EnterMovies() <> 0
			If debug Then Print "EnterMovies fail"
			EndQT()
			Return False
		EndIf
		myQTClass = bmx_CreateQT()
		isQTInit =1
		If debug Then Print "INIT ok"
		Return True
	EndMethod
	
	Method StartQT:Int(flags:Int =0)
		?Win32
		Local r:Int = InitializeQTML(flags) ''the check for QT on system
		If r Then Print "Quicktime not found."
		Return r
		?MacOS
		Return 0 ''assume QT is installed
		?
		Return 0 ''0= success (error code) in QT

	EndMethod
	
	Method EndQT()
		
		?Win32
		
		TerminateQTML()
		?
		Return
	EndMethod
	
	Method LoadMovieToPixmap:Int(filename:String, pixmap:TPixmap, flags:Int=0, isURL:Int=0)

		Init()
		
		If Not myQTClass Or Not pixmap Then Return False
		Local r:Int
		Local pixw:Int = pixmap.width
		Local pixh:Int = pixmap.height
		r = OpenMovie(filename, 0, isURL)'newMovieActive)
		If r Then Return False
		
		SetupGWorld(pixmap, flags)
		
		Return True
	EndMethod
	
	Method LoadMovieToImage:Int(filename:String, img:TImage, flags:Int=0, isURL:Int=0)

		Init()
		
		If Not myQTClass Or Not img Then Return False
		Local r:Int
		Local pixw:Int = img.width
		Local pixh:Int = img.height
		r = OpenMovie(filename, 0, isURL)'newMovieActive)
		If r Then Return False
		
		If Not img.pixmaps[0] Then img.pixmaps[0] = TPixmap.Create( img.width,img.height,PF_RGB888 )
		If img.pixmaps[0].format <> PF_RGB888 Then img.pixmaps[0] = img.pixmaps[0].convert(PF_RGB888) ;Print "convert to rgb888"
		
		SetupGWorld(img.pixmaps[0], flags)
		
		Return True
	EndMethod
	
	Method SetupGWorld:Int(pixmap:TPixmap, flags:Int=0)
		If Not pixmap Then Return False
		Local r:Int
		
		'r=bmx_NewGWorld(myQTClass, 900, 600, 0)
		r=bmx_QTNewGWorldFromPtr(myQTClass, 24, pixmap.width, pixmap.height,  PixmapPixelPtr(pixmap), pixmap.pitch, deviceIsExternalBuffer)'deviceIsIndirect)
		If debug Then Print "Gworld: "+uinttoint(r)
		
		r = bmx_SetGWorld(myQTClass) ''works better than setmoviegworld in osx
		r = bmx_SetMovieGWorld(myQTClass) ''need both for windows
		If debug Then Print "setGW: "+uinttoint(r)
		
		r = bmx_NewMovieController (myQTClass, mcTopLeftMovie|mcNotVisible|flags)
		If debug Then Print "controller: " +uinttoint(r)
		bmx_SetMoviePlayHints(myQTClass, hintsOffscreen|hintsAllowDynamicResize, hintsOffscreen|hintsAllowDynamicResize )
		If debug Then Print "hints ok"
		r=bmx_SetMovieActive(myQTClass,True)
		If debug Print "setactivem: "+uinttoint(r)
		GotoBeginning()
				
		_pixmap = pixmap
		
		Return True
	EndMethod
	
	''Opening a movie does not necessarily load the entirety
	Method OpenMovie:Int(filename:String, flags:Int =0, isURL:Int = 0)
		Init()
		If Not myQTClass Then Return -1
		
		''determine isURL
		If Not isURL And filename.StartsWith("http:") Then isURL = 1
		
		Local r:Int
		'r = bmx_NativePathNameToFSSpec(myQTClass, filename, 0)
		'If debug Then Print "fsspec: " +uinttoint(r)
		'r= bmx_OpenMovieFile( myQTClass, 1)
		'If debug Then Print "openfile "+uinttoint(r)
		'If(r) Then Return -1
		
		''flags = newMovieActive
		'r=bmx_NewMovieFromFile(myQTClass, flags)
		If isURL Then	r = bmx_NewMovieFromURL(myQTClass, filename) Else r = bmx_NewMovieFromFile(myQTClass, filename)
		
		Local myLoadState:Int= kMovieLoadStateLoading
		Local tick:Int =MilliSecs()
		
		While  Not (myLoadState & kMovieLoadStatePlaythroughOK) 'And tick <> 0
			bmx_GetMovieDuration(myQTClass)
			myLoadState = bmx_GetMovieLoadState(myQTClass)
		Wend
		
		If debug Then Print "newmovie: "+uinttoint(r)
		bmx_CloseMovieFile(myQTClass) ''success close file
		''get width and height and etc
		bmx_GetMovieBox(myQTClass)
		Return 0
	EndMethod
	
	Method IsMovieReady:Int()
		Local k:Int = bmx_GetMovieLoadState(myQTClass)
		If k & kMovieLoadStatePlaythroughOK Then Return True
		Return False
	EndMethod
	
	Method CloseMovie()
		If Not myQTClass Then Return 
		ExitMovies()
		bmx_DisposeMovieController (myQTClass)
		bmx_DisposeGWorld(myQTClass)
		
		EndQT()
		isQtInit =0
		If debug Then Print "CloseMovie ok"
	EndMethod
	
	Method GetMovieWidth:Int()
		Return bmx_GetMovieWidth(myQTClass)
	EndMethod
	Method GetMovieHeight:Int()
		Return bmx_GetMovieHeight(myQTClass)
	EndMethod
	'' in seconds
	Method GetMovieDuration:Float()
		Return bmx_GetMovieDuration(myQTClass)/Float(bmx_GetMovieTimeScale(myQTClass))
	EndMethod
	Method GetMovieTracks:Int()
		Return bmx_GetMovieTrackCount(myQTClass)
	EndMethod
	''in ticks
	Method GetMovieTime:Int()
		Return bmx_GetMovieTime(myQTClass)
	EndMethod
	'' in seconds
	Method GetMovieTimeSecs:Float()
		Return bmx_GetMovieTime(myQTClass)/Float(bmx_GetMovieTimeScale(myQTClass))
	EndMethod
	
	'' volume is between 0.0 and 1.0
	Method SetMovieVolume(vol:Float)
		bmx_SetMovieVolume(myQTClass, vol)
	EndMethod

	''movie time in ticks
	Method SetMovieTime(ms:Int)
		bmx_SetMovieTimeValue(myQTClass, ms )
	EndMethod

	Method StartMovie()
		If Not myQTClass Then Return
		bmx_StartMovie(myQTClass)
		If debug Then Print "startmovie ok"
	EndMethod

	Method StopMovie()
		If Not myQTClass Then Return
		bmx_StopMovie(myQTClass)
		If debug Then Print "stopmovie ok"
	EndMethod

	Method UpdateMovie(update:Int=0)
		'If update Then bmx_UpdateMovie(myQTClass)
		bmx_MoviesTask(myQTClass,0)
		'bmx_UpdateMovie(myQTClass)
	EndMethod
	
	Method IsMovieDone:Int()
		Return bmx_IsMovieDone(myQTClass)
	EndMethod

	Method DrawMovieImage(img:TImage, x:Int, y:Int)
		If Not img Or Not _pixmap Then Return
		
		If Not texid
			imageframe = TGLImageFrame.CreateFromPixmap( _pixmap,0 )
			texid = imageframe.name
			If debug Then Print "draw movie bind"
		EndIf

		img.pixmaps[0]=_pixmap
		img.seqs[0]=0

		If texid
			glBindTexture(GL_TEXTURE_2D, texid)
			glPixelStorei GL_UNPACK_ROW_LENGTH, _pixmap.pitch/BytesPerPixel[_pixmap.format]
			
			glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, _pixmap.width, _pixmap.height, GL_RGB, GL_UNSIGNED_BYTE, _pixmap.pixels)
			glPixelStorei GL_UNPACK_ROW_LENGTH,0
			
			imageframe.Draw( 0,0,_pixmap.width,_pixmap.height, x, y ,0,0,_pixmap.width,_pixmap.height)

		EndIf
			
	EndMethod
	
	Method GotoBeginning()
		bmx_GoToBeginningOfMovie(myQTClass)
	EndMethod

EndType



Function UintToInt:Int(value:Int)
      Const OFFSET_2:Int = 65536
      Const MAXINT_2:Int = 32767

        If Value < 0 Or Value >= OFFSET_2 Then Return value ' Overflow
        If Value <= MAXINT_2 Then
          Return Value
        Else
          Return Value - OFFSET_2
        End If
End Function
