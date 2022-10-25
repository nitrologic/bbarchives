; ID: 1683
; Author: tonyg
; Date: 2006-04-21 19:53:21
; Title: Getcaps
; Description: Get device capabilities

Rem
/* D3DDEVICEDESC dwDevCaps flags */
'define D3DDEVCAPS_FLOATTLVERTEX        0x00000001L /* Device accepts floating Point */
 '                                                   /* For post-transform vertex data */
'define D3DDEVCAPS_SORTINCREASINGZ      0x00000002L /* Device needs data sorted For increasing Z */
'define D3DDEVCAPS_SORTDECREASINGZ      0X00000004L /* Device needs data sorted For decreasing Z */
'define D3DDEVCAPS_SORTEXACT            0x00000008L /* Device needs data sorted exactly */

'define D3DDEVCAPS_EXECUTESYSTEMMEMORY  0x00000010L /* Device can use execute buffers from system memory */
'define D3DDEVCAPS_EXECUTEVIDEOMEMORY   0x00000020L /* Device can use execute buffers from video memory */
'define D3DDEVCAPS_TLVERTEXSYSTEMMEMORY 0x00000040L /* Device can use TL buffers from system memory */
'define D3DDEVCAPS_TLVERTEXVIDEOMEMORY  0x00000080L /* Device can use TL buffers from video memory */
'define D3DDEVCAPS_TEXTURESYSTEMMEMORY  0x00000100L /* Device can texture from system memory */
'define D3DDEVCAPS_TEXTUREVIDEOMEMORY   0x00000200L /* Device can texture from device memory */
'If(DIRECT3D_VERSION >= 0x0500)
'define D3DDEVCAPS_DRAWPRIMTLVERTEX     0x00000400L /* Device can draw TLVERTEX primitives */
'define D3DDEVCAPS_CANRENDERAFTERFLIP   0x00000800L /* Device can render without waiting For Flip To complete */
'define D3DDEVCAPS_TEXTURENONLOCALVIDMEM 0x00001000L /* Device can texture from nonlocal video memory */
'EndIf /* DIRECT3D_VERSION >= 0x0500 */
'If(DIRECT3D_VERSION >= 0x0600)
'define D3DDEVCAPS_DRAWPRIMITIVES2         0x00002000L /* Device can support DrawPrimitives2 */
'define D3DDEVCAPS_SEPARATETEXTUREMEMORIES 0x00004000L /* Device is texturing from separate memory pools */
'define D3DDEVCAPS_DRAWPRIMITIVES2EX       0x00008000L /* Device can support Extended DrawPrimitives2 i.e. DX7 compliant driver*/
'EndIf /* DIRECT3D_VERSION >= 0x0600 */
'If(DIRECT3D_VERSION >= 0x0700)
'define D3DDEVCAPS_HWTRANSFORMANDLIGHT     0x00010000L /* Device can support transformation And lighting in hardware And DRAWPRIMITIVES2EX must be also */
'define D3DDEVCAPS_CANBLTSYSTONONLOCAL     0x00020000L /* Device supports a Tex Blt from system memory To non-Local vidmem */
'define D3DDEVCAPS_HWRASTERIZATION         0x00080000L /* Device has HW acceleration For rasterization */
End Rem
    Const D3DDEVCAPS_CANBLTSYSTONONLOCAL = 131072
    Const D3DDEVCAPS_CANRENDERAFTERFLIP = 2048
    Const D3DDEVCAPS_DRAWPRIMTLVERTEX = 1024
    Const D3DDEVCAPS_FLOATTLVERTEX = 1
    Const D3DDEVCAPS_HWRASTERIZATION = 524288
    Const D3DDEVCAPS_HWTRANSFORMANDLIGHT = 65536
    Const D3DDEVCAPS_SEPARATETEXTUREMEMORIES = 16384
    Const D3DDEVCAPS_SORTDECREASINGZ = 4
    Const D3DDEVCAPS_SORTEXACT = 8
    Const D3DDEVCAPS_SORTINCREASINGZ = 2
    Const D3DDEVCAPS_TEXTURENONLOCALVIDMEM = 4096
    Const D3DDEVCAPS_TEXTURESYSTEMMEMORY = 256
    Const D3DDEVCAPS_TEXTUREVIDEOMEMORY = 512
    Const D3DDEVCAPS_TLVERTEXSYSTEMMEMORY = 64
    Const D3DDEVCAPS_TLVERTEXVIDEOMEMORY = 128

'/* D3DPRIMCAPS dwMiscCaps */


    Const D3DPMISCCAPS_CONFORMANT = 8
    Const D3DPMISCCAPS_CULLCCW = 64
    Const D3DPMISCCAPS_CULLCW = 32
    Const D3DPMISCCAPS_CULLNONE = 16
    Const D3DPMISCCAPS_LINEPATTERNREP = 4
    Const D3DPMISCCAPS_MASKPLANES = 1
    Const D3DPMISCCAPS_MASKZ = 2

'/* D3DPRIMCAPS dwRasterCaps */
    Const D3DPRASTERCAPS_ANISOTROPY = 131072 
    Const D3DPRASTERCAPS_ANTIALIASEDGES = 4096 
    Const D3DPRASTERCAPS_ANTIALIASSORTDEPENDENT = 1024 
    Const D3DPRASTERCAPS_DITHER= 1 
    Const D3DPRASTERCAPS_FOGRANGE = 65536 
    Const D3DPRASTERCAPS_FOGTABLE = 256 
    Const D3DPRASTERCAPS_FOGVERTEX = 128 
    Const D3DPRASTERCAPS_MIPMAPLODBIAS = 8192 
    Const D3DPRASTERCAPS_PAT = 8 
    Const D3DPRASTERCAPS_ROP2 = 2 
    Const D3DPRASTERCAPS_STIPPLE = 512 
    Const D3DPRASTERCAPS_SUBPIXEL= 32 
    Const D3DPRASTERCAPS_SUBPIXELX = 64 
    Const D3DPRASTERCAPS_XOR = 4 
    Const D3DPRASTERCAPS_ZBIAS = 16384 
    Const D3DPRASTERCAPS_ZBUFFERLESSHSR = 32768 
    Const D3DPRASTERCAPS_ZTEST = 16 

'/* D3DPRIMCAPS dwZCmpCaps, dwAlphaCmpCaps */
    Const D3DPCMPCAPS_ALWAYS = 128 
    Const D3DPCMPCAPS_EQUAL = 4 
    Const D3DPCMPCAPS_GREATER = 16 
    Const D3DPCMPCAPS_GREATEREQUAL = 64 
    Const D3DPCMPCAPS_LESS = 2 
    Const D3DPCMPCAPS_LESSEQUAL = 8 
    Const D3DPCMPCAPS_NEVER = 1 
    Const D3DPCMPCAPS_NOTEQUAL = 32 

'/* D3DPRIMCAPS dwSourceBlendCaps, dwDestBlendCaps */
    Const D3DPBLENDCAPS_BOTHINVSRCALPHA =4096 
    Const D3DPBLENDCAPS_BOTHSRCALPHA =2048 
    Const D3DPBLENDCAPS_DESTALPHA =64 
    Const D3DPBLENDCAPS_DESTCOLOR =256 
    Const D3DPBLENDCAPS_INVDESTALPHA =128 
    Const D3DPBLENDCAPS_INVDESTCOLOR =512 
    Const D3DPBLENDCAPS_INVSRCALPHA =32 
    Const D3DPBLENDCAPS_INVSRCCOLOR =8 
    Const D3DPBLENDCAPS_ONE =2 
    Const D3DPBLENDCAPS_SRCALPHA =16 
    Const D3DPBLENDCAPS_SRCALPHASAT =1024 
    Const D3DPBLENDCAPS_SRCCOLOR =4 
    Const D3DPBLENDCAPS_ZERO =1 

'/* D3DPRIMCAPS dwShadeCaps */
    Const D3DPSHADECAPS_ALPHAFLATBLEND = 4096
    Const D3DPSHADECAPS_ALPHAFLATSTIPPLED = 8192
    Const D3DPSHADECAPS_ALPHAGOURAUDBLEND = 16384
    Const D3DPSHADECAPS_ALPHAGOURAUDSTIPPLED = 32768
    Const D3DPSHADECAPS_ALPHAPHONGBLEND = 65536
    Const D3DPSHADECAPS_ALPHAPHONGSTIPPLED = 131072
    Const D3DPSHADECAPS_COLORFLATMONO = 1
    Const D3DPSHADECAPS_COLORFLATRGB = 2
    Const D3DPSHADECAPS_COLORGOURAUDMONO = 4
    Const D3DPSHADECAPS_COLORGOURAUDRGB = 8
    Const D3DPSHADECAPS_COLORPHONGMONO = 16
    Const D3DPSHADECAPS_COLORPHONGRGB = 32
    Const D3DPSHADECAPS_FOGFLAT = 262144
    Const D3DPSHADECAPS_FOGGOURAUD = 524288
    Const D3DPSHADECAPS_FOGPHONG = 1048576
    Const D3DPSHADECAPS_SPECULARFLATMONO = 64
    Const D3DPSHADECAPS_SPECULARFLATRGB = 128
    Const D3DPSHADECAPS_SPECULARGOURAUDMONO = 256
    Const D3DPSHADECAPS_SPECULARGOURAUDRGB = 512
    Const D3DPSHADECAPS_SPECULARPHONGMONO = 1024
    Const D3DPSHADECAPS_SPECULARPHONGRGB = 2048

Rem
/* D3DPRIMCAPS dwTextureCaps */
 * Perspective-correct texturing is supported
#define D3DPTEXTURECAPS_PERSPECTIVE     0x00000001L
 * Power-of-2 texture dimensions are required*/
#define D3DPTEXTURECAPS_POW2            0x00000002L
 * Alpha in texture pixels is supported*/
#define D3DPTEXTURECAPS_ALPHA           0x00000004L
 * Color-keyed textures are supported*/
#define D3DPTEXTURECAPS_TRANSPARENCY    0x00000008L
 * obsolete, see D3DPTADDRESSCAPS_BORDER
#define D3DPTEXTURECAPS_BORDER          0x00000010L
 * Only square textures are supported*/
#define D3DPTEXTURECAPS_SQUAREONLY      0x00000020L
 * Texture indices are Not scaled by the texture size prior
 * To interpolation.*/
#define D3DPTEXTURECAPS_TEXREPEATNOTSCALEDBYSIZE 0x00000040L
 * Device can draw alpha from texture palettes*/
#define D3DPTEXTURECAPS_ALPHAPALETTE    0x00000080L
 * Device can use non-POW2 textures If:
 *  1) D3DTEXTURE_ADDRESS is set To Clamp For this texture's stage
 *  2) D3DRS_WRAP(N) is zero For this texture's coordinates
 *  3) mip mapping is Not enabled (use magnification filter only)*/
#define D3DPTEXTURECAPS_NONPOW2CONDITIONAL  0x00000100L
// 0x00000200L unused
 * Device can divide transformed texture coordinates by the
 * COUNTth texture coordinate (can do D3DTTFF_PROJECTED)*/
#define D3DPTEXTURECAPS_PROJECTED  0x00000400L
 * Device can do cubemap textures*/
#define D3DPTEXTURECAPS_CUBEMAP           0x00000800L
#define D3DPTEXTURECAPS_COLORKEYBLEND     0x00001000L
End Rem

    Const D3DPTEXTURECAPS_ALPHA = 4
    Const D3DPTEXTURECAPS_BORDER = 16
    Const D3DPTEXTURECAPS_COLORKEYBLEND = 4096
    Const D3DPTEXTURECAPS_CUBEMAP = 1024
    Const D3DPTEXTURECAPS_NONPOW2CONDITIONAL = 256
    Const D3DPTEXTURECAPS_PERSPECTIVE = 1
    Const D3DPTEXTURECAPS_POW2 = 2
    Const D3DPTEXTURECAPS_SQUAREONLY = 32
    Const D3DPTEXTURECAPS_TRANSPARENCY = 8

'/* D3DPRIMCAPS dwTextureFilterCaps */

    Const D3DPTFILTERCAPS_LINEAR = 2
    Const D3DPTFILTERCAPS_LINEARMIPLINEAR = 32
    Const D3DPTFILTERCAPS_LINEARMIPNEAREST = 16
    Const D3DPTFILTERCAPS_MAGFAFLATCUBIC = 134217728
    Const D3DPTFILTERCAPS_MAGFANISOTROPIC = 67108864
    Const D3DPTFILTERCAPS_MAGFGAUSSIANCUBIC =  268435456
    Const D3DPTFILTERCAPS_MAGFLINEAR = 16777216
    Const D3DPTFILTERCAPS_MAGFPOINT = 8388608
    Const D3DPTFILTERCAPS_MINFANISOTROPIC = 1024
    Const D3DPTFILTERCAPS_MINFLINEAR = 512
    Const D3DPTFILTERCAPS_MINFPOINT = 256
    Const D3DPTFILTERCAPS_MIPFLINEAR = 131072
    Const D3DPTFILTERCAPS_MIPFPOINT = 65536
    Const D3DPTFILTERCAPS_MIPLINEAR = 8
    Const D3DPTFILTERCAPS_MIPNEAREST = 4
    Const D3DPTFILTERCAPS_NEAREST = 1

'/* D3DPRIMCAPS dwTextureBlendCaps */
'Replaced by TextureOpCaps

'/* D3DPRIMCAPS dwTextureAddressCaps */
    Const D3DPTADDRESSCAPS_BORDER = 8
    Const D3DPTADDRESSCAPS_CLAMP = 4
    Const D3DPTADDRESSCAPS_INDEPENDENTUV = 16
    Const D3DPTADDRESSCAPS_MIRROR = 2
    Const D3DPTADDRESSCAPS_WRAP = 1

'/* D3DDEVICEDESC dwStencilCaps */
    Const D3DSTENCILCAPS_DECR = 128
    Const D3DSTENCILCAPS_DECRSAT = 16
    Const D3DSTENCILCAPS_INCR = 64
    Const D3DSTENCILCAPS_INCRSAT = 8
    Const D3DSTENCILCAPS_INVERT = 32
    Const D3DSTENCILCAPS_KEEP = 1
    Const D3DSTENCILCAPS_REPLACE = 4

'/* D3DDEVICEDESC dwTextureOpCaps */
    Const D3DTEXOPCAPS_ADD = 64
    Const D3DTEXOPCAPS_ADDSIGNED = 128
    Const D3DTEXOPCAPS_ADDSIGNED2X = 256
    Const D3DTEXOPCAPS_ADDSMOOTH = 1024
    Const D3DTEXOPCAPS_BLENDCURRENTALPHA = 32768
    Const D3DTEXOPCAPS_BLENDDIFFUSEALPHA = 2048
    Const D3DTEXOPCAPS_BLENDFACTORALPHA = 8192
    Const D3DTEXOPCAPS_BLENDTEXTUREALPHA = 4096
    Const D3DTEXOPCAPS_BLENDTEXTUREALPHAPM = 16384
    Const D3DTEXOPCAPS_BUMPENVMAP = 2097152
    Const D3DTEXOPCAPS_BUMPENVMAPLUMINANCE = 4194304
    Const D3DTEXOPCAPS_DISABLE = 1
    Const D3DTEXOPCAPS_DOTPRODUCT3 = 8388608
    Const D3DTEXOPCAPS_MODULATE = 8
    Const D3DTEXOPCAPS_MODULATE2X = 16
    Const D3DTEXOPCAPS_MODULATE4X = 32
    Const D3DTEXOPCAPS_MODULATEALPHA_ADDCOLOR = 131072
    Const D3DTEXOPCAPS_MODULATECOLOR_ADDALPHA = 262144
    Const D3DTEXOPCAPS_MODULATEINVALPHA_ADDCOLOR = 524288
    Const D3DTEXOPCAPS_MODULATEINVCOLOR_ADDALPHA = 1048576
    Const D3DTEXOPCAPS_PREMODULATE = 65536
    Const D3DTEXOPCAPS_SELECTARG1 = 2
    Const D3DTEXOPCAPS_SELECTARG2 = 4
    Const D3DTEXOPCAPS_SUBTRACT = 512

Rem
/* D3DDEVICEDESC dwFVFCaps flags */

#define D3DFVFCAPS_TEXCOORDCOUNTMASK    0x0000ffffL /* mask For texture coordinate count Field */
#define D3DFVFCAPS_DONOTSTRIPELEMENTS   0x00080000L /* Device prefers that vertex elements Not be stripped */
End Rem
 Const   D3DFVFCAPS_DONOTSTRIPELEMENTS = 524288 
 Const   D3DFVFCAPS_TEXCOORDCOUNTMASK = 65535 




Rem
 * These are the flags in the D3DDEVICEDESC7.dwVertexProcessingCaps Field
 */
/* device can do texgen */
#define D3DVTXPCAPS_TEXGEN              0x00000001L
/* device can do IDirect3DDevice7 colormaterialsource ops */
#define D3DVTXPCAPS_MATERIALSOURCE7     0x00000002L
/* device can do vertex fog */
#define D3DVTXPCAPS_VERTEXFOG           0x00000004L
/* device can do directional lights */
#define D3DVTXPCAPS_DIRECTIONALLIGHTS   0x00000008L
/* device can do positional lights (includes Point And spot) */
#define D3DVTXPCAPS_POSITIONALLIGHTS    0x00000010L
/* device can do Local viewer */
#define D3DVTXPCAPS_LOCALVIEWER         0x00000020L
End Rem
    Const D3DVTXPCAPS_DIRECTIONALLIGHTS = 8
    Const D3DVTXPCAPS_MATERIALSOURCE7 = 2
    Const D3DVTXPCAPS_NONLOCALVIEWER = 32
    Const D3DVTXPCAPS_POSITIONALLIGHTS = 16
    Const D3DVTXPCAPS_TEXGEN = 1
    Const D3DVTXPCAPS_VERTEXFOG = 4

'Not sure how to read the GUID info.
'The first dword contains the interface type listed in DX7 docs.
'For default Bmax this is likely to be IDIRECT3D.
'The rest must mean something but I use them here as 'fillers'.

Type TG_D3DDeviceDesc7
	Field dwDevCaps:Int
	Field dwSize_LINE:Int               'Size of structure
    Field dwMiscCaps_LINE:Int           'Miscellaneous capabilities
    Field dwRasterCaps_LINE:Int         'Raster capabilities
    Field dwZCmpCaps_LINE:Int           'Z-comparison capabilities
    Field dwSrcBlendCaps_LINE:Int       'Source-blending capabilities
    Field dwDestBlendCaps_LINE:Int      'Destination-blending capa bilities
    Field dwAlphaCmpCaps_LINE:Int       'Alpha-test-comparison capabilities
    Field dwShadeCaps_LINE:Int          'Shading capabilities
    Field dwTextureCaps_LINE:Int        'Texture capabilities
    Field dwTextureFilterCaps_LINE:Int  'Texture-filtering capabilities
    Field dwTextureBlendCaps_LINE:Int   'Texture-blending capabilities
    Field dwTextureAddressCaps_LINE:Int 'Texture-addressing capabilities
    Field dwStippleWidth_LINE:Int       'Stipple width
    Field dwStippleHeight_LINE:Int      'Stipple height
    Field dwSize_TRI:Int               'Size of structure
    Field dwMiscCaps_TRI:Int           'Miscellaneous capabilities
    Field dwRasterCaps_TRI:Int         'Raster capabilities
    Field dwZCmpCaps_TRI:Int           'Z-comparison capabilities
    Field dwSrcBlendCaps_TRI:Int       'Source-blending capabilities
    Field dwDestBlendCaps_TRI:Int      'Destination-blending capa bilities
    Field dwAlphaCmpCaps_TRI:Int       'Alpha-test-comparison capabilities
    Field dwShadeCaps_TRI:Int          'Shading capabilities
    Field dwTextureCaps_TRI:Int        'Texture capabilities
    Field dwTextureFilterCaps_TRI:Int  'Texture-filtering capabilities
    Field dwTextureBlendCaps_TRI:Int   'Texture-blending capabilities
    Field dwTextureAddressCaps_TRI:Int 'Texture-addressing capabilities
    Field dwStippleWidth_TRI:Int       'Stipple width
    Field dwStippleHeight_TRI:Int      'Stipple height
	Field      dwDeviceRenderBitDepth:Int
	Field      dwDeviceZBufferBitDepth:Int
	Field      dwMinTextureWidth:Int
	Field  	   dwMinTextureHeight:Int
	Field      dwMaxTextureWidth:Int
	Field 	   dwMaxTextureHeight:Int
	Field      dwMaxTextureRepeat:Int
	Field      dwMaxTextureAspectRatio:Int
	Field      dwMaxAnisotropy:Float
	Field      dvGuardBandLeft:Float
	Field      dvGuardBandTop:Float
	Field      dvGuardBandRight:Float
	Field      dvGuardBandBottom:Float
	Field      dvExtentsAdjust:Float
	Field      dwStencilCaps:Int
	Field      dwFVFCaps:Int
	Field      dwTextureOpCaps:Int
	Field      wMaxTextureBlendStages:Short
	Field      wMaxSimultaneousTextures:Short
	Field      dwMaxActiveLights:Int
	Field      dvMaxVertexW:Float
	Field      GUID_Interface_type:Int
	Field      GUID_1:Short
	Field      GUID_2:Short
	Field      GUID_3:Byte
	Field      GUID_4:Byte
	Field      GUID_5:Byte
	Field      GUID_6:Byte
	Field      GUID_7:Byte
	Field      GUID_8:Byte
	Field      GUID_9:Byte
	Field      GUID_10:Byte
	Field      wMaxUserClipPlanes:Short
	Field      wMaxVertexBlendMatrices:Short
	Field      dwVertexProcessingCaps:Int
	Field      dwReserved1:Int
	Field      dwReserved2:Int
	Field      dwReserved3:Int
	Field      dwReserved4:Int
End Type
Graphics 640,480
Local mycaps:TG_D3DDEVICEDESC7 = New TG_D3DDEVICEDESC7
D3D7GraphicsDriver().Direct3DDevice7().getcaps mycaps
Print "MinTextureWidth " + mycaps.dwMinTextureWidth
Print "MinTextureHeight " + mycaps.dwMinTextureHeight
Print "MaxTextureWidth " + mycaps.dwMaxTextureWidth
Print "MaxTextureHeight " + mycaps.dwMaxTextureHeight
Print "MaxTextureBlendStages " + mycaps.wMaxTextureBlendStages
Print "MaxActiveLights " + mycaps.dwMaxActiveLights
If mycaps.dwTextureOpCaps & D3DTEXOPCAPS_MODULATE2X Print "Device can Mod2X"
If mycaps.dwTextureCaps_LINE & D3DPTEXTURECAPS_POW2 Print "Power-of-2 texture dimensions are required"
If mycaps.dwDevCaps & D3DDEVCAPS_HWRASTERIZATION Print "Device has HW acceleration For rasterization"
If mycaps.dwRasterCaps_LINE & D3DPRASTERCAPS_ANTIALIASEDGES 
   Print "The device can antialias lines forming the convex outline of objects."
Else
   Print "The device can NOT antialias lines forming the convex outline of objects."
EndIf
