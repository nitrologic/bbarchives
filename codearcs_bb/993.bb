; ID: 993
; Author: Nilium
; Date: 2004-04-09 19:21:56
; Title: Virtual GL
; Description: A library of functions to mimic the behavour of OpenGL

;VGL Example

Graphics3d 800,600,32,2

;Include "vgl.bb"

vglInit()

;EntityFX vglDisplayMesh,1+2+16

;PART_FIRE = vglLoadTexture("part_fire.tga",1)     ;;;  UNCOMMENT THIS IS YOU'VE GOT A TEXTURE TO USE.

;vglEnable(VGL_CULL_FACE)
vglEnable(VGL_AUTO_NORMAL)
vglDisable(VGL_VERTEX_ALPHA)

UsePushPop = True

Repeat
     vglClear(VGL_COLOR_BUFFER_BIT)

     vglBegin(VGL_TRIANGLE_FAN)
          vglVertex3f(0,0,0)
          vglColor4f(0,1,0,1)
          For X = 0 To 32
               vglVertex3f(Sin(X*11.25),Cos(X*11.25),0)
               vglColor4b(Sin(X*11.25)*255,Cos(X*11.25),X*7.96875,128)
          Next
     vglEnd()

     vglRotatef(Float(Millisecs())/60,.5,1,.25)
     vglTranslatef(0,0,4)

     If Keyhit(57) Then UsePushPop = Not UsePushPop
     If UsePushPop Then vglPushMatrix()
          vglBegin(VGL_TRIANGLES)
               vglVertex3f(-1,-1,0)
               vglColor4f(1,0,0,1)
               vglVertex3f(0,1,0)
               vglColor4f(0,1,0,1)
               vglVertex3f(1,-1,0)
               vglColor4f(0,0,1,1)
          vglEnd()
          vglTranslatef(0,0,4)
          vglRotatef(Float(Millisecs())/60,0,1,0)
          vglTranslatef(0,0,4)
     If UsePushPop Then vglPopMatrix()

     For E.Error = Each Error
          Debuglog E\Enum
     Next
     vglFlip()
Until Keyhit(1)

vglQuit()


;;;;; VirtualGL CODE


;VirtualGL is a GL 'simulator' (a laughable concept)
;basically, this tries to mimic the very most basic level of using OpenGL

;no enumerators should resemble the GL enumerators or variables, if they do then it's purely coincedence.
;oh yeah, and no pushing/popping

;currently can only draw triangles and quads, and there're a few useless banks

Type Surface
     Field Texture
     Field Surface[32]
End Type


Type Error
     Field Enum
End Type


Type DisplayList
     Field Offset
     Field Length
End Type


Const VGL_INVALID_ENUM = $E3FAFF01     ;invalid enumerator error
Const VGL_NULL_TEXTURE = $0038AF72     ;null texture- texture not loaded.

;vglBegin enumerators
Const VGL_TRIANGLES% = $00000001
Const VGL_QUADS% = $00000002
Const VGL_TRIANGLE_STRIP% = $00000003
Const VGL_QUAD_STRIP% = $00000004
Const VGL_TRIANGLE_FAN% = $00000005
Const VGL_POINTS% = $00000006
Const VGL_LINES% = $00000007
Const VGL_LINE_STRIP% = $00000008
Const VGL_LINE_LOOP% = $00000009
Const VGL_POLYGON% = $0000000A

;vglEnd enumerators
Global VGL_DRAW_MODE% = 0

;vglEnable / vglDisable enumerators
Const VGL_CULL_FACE = $6A9F0713
Const VGL_AUTO_NORMAL = $FD9F1713
Const VGL_VERTEX_ALPHA = $BAD213FE

;VirtualGL data banks, or databases, take your pick
Global vglCommonData% = 0
Global vglDisplayListData% = 0
Global vglVertexArrayData% = 0

Global vglDisplayMesh

Global NullTexture.Surface
Global vglCurrentTexture.Surface

Global vglBeginOpen = False
Global vglVertexOffset

Global vglViewport

Global vglRotationYaw#,vglRotationPitch#,vglRotationRoll#
Global vglTranslationX#,vglTranslationY#,vglTranslationZ#

Global vglCullFaces = False
Global vglUpdateNormals = False
Global vglVertexAlpha = False

Dim vglStack(0)   ;to me, this is where it gets interesting.
Global vglStackOffset = 0


Function vglInit()
     vglCommonData = CreateBank(0)
     vglDisplayListData = CreateBank(0)
     vglVertexArrayData = CreateBank(0)
     vglDisplayMesh = CreateMesh()
     EntityFX vglDisplayMesh,1+2
     NullTexture.Surface = New Surface
     NullTexture\Surface[vglStackOffset] = CreateSurface(vglDisplayMesh)
     vglCurrentTexture = NullTexture
     vglViewport = CreateCamera()
     CameraClsMode vglViewport,0,1 ;only clear the z buffer

     Dim vglStack(32)
     vglStack(0) = vglDisplayMesh
End Function


Function vglQuit()
     FreeBank vglCommonData
     FreeBank vglDisplayListData
     FreeBank vglVertexArrayData
     FreeEntity vglDisplayMesh
     For S.Surface = Each Surface
          If S\Texture <> 0 Then FreeBrush S\Texture
          Delete S
     Next
End Function


Function vglBegin(VGL_ENUM=0)
     If Not vglCommonData% <> 0 Or vglDisplayListData% <> 0 Or vglVertexArrayData% <> 0 Or vglDisplayMesh <> 0 Then vglError(VGL_NOT_INITIALIZED)
     vglBeginOpen = True
     vglVertexOffset = CountVertices(vglCurrentTexture\Surface[vglStackOffset])
     VGL_DRAW_MODE = VGL_ENUM
End Function


Function vglEnd()
     Select VGL_DRAW_MODE
          Case VGL_TRIANGLES
               N = vglVertexOffset
               While N+2 <= CountVertices(vglCurrentTexture\Surface[vglStackOffset])-1
                    AddTriangle vglCurrentTexture\Surface[vglStackOffset],N,N+1,N+2
                    N = N + 3
               Wend

          Case VGL_QUADS
               N = vglVertexOffset
               While N+3 <= CountVertices(vglCurrentTexture\Surface[vglStackOffset])-1
                    AddTriangle vglCurrentTexture\Surface[vglStackOffset],N,N+1,N+2
                    AddTriangle vglCurrentTexture\Surface[vglStackOffset],N+2,N+3,N
                    N = N + 4
               Wend

          Case VGL_TRIANGLE_STRIP

          Case VGL_QUAD_STRIP

          Case VGL_TRIANGLE_FAN
               N = vglVertexOffset
               While N +2 <= CountVertices(vglCurrentTexture\Surface[vglStackOffset])-1
                    AddTriangle vglCurrentTexture\Surface[vglStackOffset],vglVertexOffset,N+1,N+2
                    N = N + 1
               Wend

          Case VGL_POINTS

          Case VGL_LINES

          Case VGL_LINE_STRIP

          Case VGL_LINE_LOOP

          Case VGL_POLYGON

          Default
               vglError(VGL_INVALID_ENUM)
     End Select

     FreeBank vglCommonData
     vglCommonData = CreateBank()
     VGL_DRAW_MODE = 0
     vglVertexOffset = 0
     vglBeginOpen = False
     vglCurrentTexture = NullTexture
     Return True
End Function


Function vglVertex3f(x#,y#,z#)
     If VGL_DRAW_MODE = 0 Then Return False
     AddVertex(vglCurrentTexture\Surface[vglStackOffset],x,y,z)
End Function


Function vglTexCoord2f(u#,v#)
     If VGL_DRAW_MODE = 0 Or CountVertices(vglCurrentTexture\Surface[vglStackOffset])-vglVertexOffset = 0 Then Return False
     VertexTexCoords vglCurrentTexture\Surface[vglStackOffset],CountVertices(vglCurrentTexture\Surface[vglStackOffset])-1,u,v,0,0
End Function


Function vglNormal3f(nx#,ny#,nz#)
     If VGL_DRAW_MODE = 0 Or CountVertices(vglCurrentTexture\Surface[vglStackOffset])-vglVertexOffset = 0 Then Return False
     VertexNormal vglCurrentTexture\Surface[vglStackOffset],CountVertices(vglCurrentTexture\Surface[vglStackOffset])-1,nx,ny,nz
End Function


Function vglColor4f(r#,g#,b#,a#)
     If VGL_DRAW_MODE = 0 Or CountVertices(vglCurrentTexture\Surface[vglStackOffset])-vglVertexOffset = 0 Then Return False
     VertexColor vglCurrentTexture\Surface[vglStackOffset],CountVertices(vglCurrentTexture\Surface[vglStackOffset])-1,r*255,g*255,b*255,a
End Function


Function vglColor4b(r%,g%,b%,a%)
     If VGL_DRAW_MODE = 0 Or CountVertices(vglCurrentTexture\Surface[vglStackOffset])-vglVertexOffset = 0 Then Return False
     VertexColor vglCurrentTexture\Surface[vglStackOffset],CountVertices(vglCurrentTexture\Surface[vglStackOffset])-1,r,g,b,Float(a)/255
End Function


Function vglFlip()
     EntityFX vglDisplayMesh,1+2+(16*(Not vglCullFaces))+(32*vglVertexAlpha)
     If vglUpdateNormals Then UpdateNormals vglDisplayMesh
     UpdateWorld
     RenderWorld
     Flip False

     For S.Surface = Each Surface
          For N = 0 To 32
               If S\Surface[N] <> 0 Then ClearSurface S\Surface[N],True,True
          Next
     Next
End Function


Function vglError(VGL_ERROR_ENUM)
     E.Error = New Error
     E\Enum = VGL_ERROR_ENUM
End Function


Function vglGetError%()
     E.Error = Last Error
     Error = E\Enum
     Delete E
End Function


Function vglCallList(VGL_LIST.DisplayList)
End Function


Function vglAddVertex()
     ResizeBank vglCommonData,BankSize(vglCommonData)+4*8+2          ;one short, eight floats, in this order: vertex number, x, y, z, normal x, y, z, u, v
     Return BankSize(vglCommonData)-(4*8)-2                           ;it said five previously... old comment.  mistake.
End Function


Function vglLoadTexture(path$,flags=9)
     TEX = LoadBrush(path$,flags)
     If TEX <> 0 Then
          S.Surface = New Surface
          S\Texture = TEX
          S\Surface[0] = CreateSurface(vglDisplayMesh)
          PaintSurface S\Surface[vglStackOffset],S\Texture
          Return Handle(S)
     Endif
     Return 0
End Function


Function vglBindTexture(Tex)     ;unfortunately, i can't simulate the second parameter of glBindTexture 'cause i don't know how to
     Texture.Surface = Object.Surface(Tex)
     If VGL_DRAW_MODE = 0 And Texture <> Null Then
          vglCurrentTexture = Texture.Surface
          If vglCurrentTexture\Surface[vglStackOffset] = 0 Then vglCurrentTexture\Surface[vglStackOffset] = CreateSurface(vglStack(vglStackOffset))
          ;PaintSurface vglCurrentTexture\Surface,vglCurrentTexture\Texture
          Return True
     Endif
     vglError(VGL_NULL_TEXTURE)
     Return False
End Function


Function vglTranslatef(x#,y#,z#)
     vglTranslationX = vglTranslationX + x
     vglTranslationY = vglTranslationY + y
     vglTranslationZ = vglTranslationZ + z
     PositionMesh vglStack(vglStackOffset),x,y,z
End Function


Function vglRotatef(angle#,x#,y#,z#)
     vglRotationPitch# = vglRotationPitch# + angle*x
     vglRotationYaw# = vglRotationYaw# + angle*y
     vglRotationRoll# = vglRotationRoll + angle*z
     RotateMesh vglStack(vglStackOffset),0,0,angle*z
     RotateMesh vglStack(vglStackOffset),angle*x,0,0
     RotateMesh vglStack(vglStackOffset),0,angle*y,0
End Function


Function vglClear(mask)
     Select VGL_ENUM
          Case VGL_COLOR_BUFFER_BIT
               SetBuffer(BackBuffer())
               Cls
          Default
               vglError(VGL_INVALID_ENUM)
     End Select
End Function


Function vglLoadIdentity()
     PositionMesh vglStack(vglStackOffset),-vglTranslationX,-vglTranslationY,-vglTranslationZ
     RotateMesh vglStack(vglStackOffset),-vglRotationPitch,-vglRotationYaw,-vglRotationRoll
     vglTranslationZ = 0 : vglTranslationY = 0 : vglTranslationX = 0
     vglRotationPitch = 0 : vglRotationYaw = 0 : vglRotationRoll = 0
End Function


Function vglEnable(cap)
     Select cap
          Case VGL_AUTO_NORMAL     ;'fraid these are the only really plausible ones to me
               vglUpdateNormals = True

          Case VGL_CULL_FACE
               vglCullFaces = True

          Case VGL_VERTEX_ALPHA
               vglVertexAlpha = True

          Default
               vglError(VGL_INVALID_ENUM)
     End Select
End Function


Function vglDisable(cap)
     Select cap
          Case VGL_AUTO_NORMAL     ;'fraid these are the only really plausible ones to me
               vglUpdateNormals = False

          Case VGL_CULL_FACE
               vglCullFaces = False

          Case VGL_VERTEX_ALPHA
               vglVertexAlpha = False

          Default
               vglError(VGL_INVALID_ENUM)
     End Select
End Function


Function vglPushMatrix() ;a really cheap attempt at push/popping matrices.
     vglStackOffset = vglStackOffset + 1
     If vglStackOffset > 32 Then
          vglQuit()
          RunTimeError "ERROR: vglStackOffset+1 > 32"
     Endif
     vglStack(vglStackOffset) = CreateMesh()
     For S.Surface = Each Surface
          S\Surface[vglStackOffset] = CreateSurface(vglStack(vglStackOffset))
          If S <> NullTexture Then PaintSurface S\Surface[vglStackOffset],S\Texture
     Next
End Function


Function vglPopMatrix()
     If vglStackOffset-1 < 0 Then
          vglQuit()
          RunTimeError "ERROR: vglStackOffset-1 < 0"
     Endif
     AddMesh vglStack(vglStackOffset),vglStack(vglStackOffset-1)
     FreeEntity vglStack(vglStackOffset)
     For S.Surface = Each Surface
          S\Surface[vglStackOffset] = 0
     Next
     vglStackOffset = vglStackOffset - 1
End Function
