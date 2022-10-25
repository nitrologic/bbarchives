; ID: 2365
; Author: Warner
; Date: 2008-12-02 09:39:26
; Title: GifLoad Port for BMax
; Description: Load gif's

' ID: 2207
' Author: markcw
' Date: 2008-01-31 23:18:00
' Title: GIFLoad Module for B3D/B+
' Description: Loads animated gifs

'GIFLoad Module for B3D/B+
'Author: markcw, edited 26 Feb 2008
'LZW decoding based on C code by John Findlay for ImageShop32

Global cl_pCharBuff 'Pointer to next byte in block
Global cl_iPass 'First pass for interlaced images in GIFOutLine
Global cl_iLine 'Offset for addressing the bits in GIFOutLine
Global cl_pBits 'Scanline for bits
Global cl_Pitch 'Bytes are rounded up for image lines
Global cl_CurrCodeSize 'The current code size
Global cl_BitsLeft 'Used in GIFNextCode
Global cl_BytesLeft 'Used in GIFNextCode
Global cl_CurrByte 'Current byte
Global cl_GlobalBpp 'Global bit depth
Global cl_isInterlaced 'Is the image interlaced
Global cl_LocalBpp 'Local bit depth
Global cl_CodeMask[] 'Masks for LZW compression algorithm
Global cl_CharBuff[] 'Current block
Global cl_DIB 'DIB bank handle
Global cl_NextFilePos 'Used to jump to the next block
Global cl_isLocalTable 'Local Color Table Flag
Global cl_bBackIndex 'Background Color Index
Global cl_Disposal 'Disposal Method, 0..3
Global cl_isTransparent 'Transparent Color Flag
Global cl_wLeft 'Image Left Position
Global cl_wTop 'Image Top Position
Global cl_bTransIndex 'Transparent Color Index

Function LoadGif:TImage(filename$,firstframe=1,numframes=0)
 'Creates and returns an image from a gif, top-level function
 'firstframe -> The frame to start drawing from, 1=first frame
 'numframes -> The number of frames to draw, 0=all frames
 'Uses GIFFrames, GIFWidth, GIFHeight and GIFLoad

 ResetGlobals()

 cl_CodeMask=New Int[16]'Null 'Masks for LZW compression algorithm
 cl_CharBuff=New Int[279]'Null 'Current block

 Local graphic:TImage, image:TImage
 Local src:TPixmap,dest:TPixmap
 Local buffer:TBank
 Local frames,width,height,dib,bpp
 Local bits,wWidth,wHeight,pitch,ix,iy,offset
 Local index,pixel,blue,green,red,rgb

 'Set some initial variables
 frames=GIFFrames(filename$) 'Number of frames
 If firstframe>0 Then firstframe=firstframe-1 Else firstframe=0
 If firstframe>frames-1 Then firstframe=frames-1 'Limit firstframe
 If numframes<=0 Then numframes=frames-firstframe 'Limit numframes
 If numframes>frames-firstframe Then numframes=frames-firstframe
 frames=firstframe+numframes 'Limit number of frames
 width=GIFWidth(filename$) 'Screen width
 height=GIFHeight(filename$) 'Screen height
 image=CreateImage(width,height,numframes)
 graphic=CreateImage(width,height,3) '0=previous, 1=this, 2=empty

 For bufferi=0 To frames-1 'Loop through the frames

  dib=GIFLoad(filename$) 'Load the next frame
  If dib=0 'Avoid errors
   'FreeImage graphic 'Free the graphic''hierX
   ResetGlobals()
   Return image 'Return the image
  EndIf
  bpp=PeekShort(dib,14) 'biBitCount
  bits=40+(PeekInt(dib,32)*4) 'biSize+(biClrUsed*4)
  wWidth=PeekInt(dib,4) 'biWidth
  wHeight=PeekInt(dib,8) 'biHeight
  pitch=((wWidth*bpp+31)/32)*4 'DWORD-aligned

  If cl_Disposal=3 'Restore to previous, store before drawing
   src:TPixmap=LockImage(graphic,1) 'Copy this graphic to previous graphic'hierX
   dest:TPixmap=LockImage(graphic,0)'hierX
   PixCopyRect cl_wLeft,cl_wTop,wWidth,wHeight,cl_wLeft,cl_wTop,src,dest'hier
   UnlockImage(graphic, 1)
   UnlockImage(graphic, 0)
  EndIf

  'Draw this graphic from the DIB
  lockbuffer:TPixmap = LockImage(graphic, 1)'LockBuffer ImageBuffer(graphic,1)'hierX
  For iy=0 To wHeight-1
   offset=bits+(pitch*(wHeight-1-iy)) 'Next scanline
   For ix=0 To wWidth-1
    If ix+cl_wLeft<width And iy+cl_wTop<height 'Pixel in bounds
     If bpp=1
      index=PeekByte(dib,offset+(ix Shr 3)) 'Get bit
      pixel=7-(ix Mod 8)
      index=(index & (1 Shl pixel)) Shr pixel
     ElseIf bpp=4
      index=PeekByte(dib,offset+(ix Shr 1)) 'Get nibble
      pixel=(1-(ix Mod 2)) Shl 2
      index=(index & (15 Shl pixel)) Shr pixel
     ElseIf bpp=8
      index=PeekByte(dib,offset+ix) 'Get byte
     EndIf
     If cl_isTransparent And cl_bTransIndex=index 'Transparent pixel
     Else 'Normal pixel
      index=40+(index Shl 2) 'Get palette index
      blue=PeekByte(dib,index)


      green=PeekByte(dib,index+1)
      red=PeekByte(dib,index+2)
      rgb=blue | (green Shl 8) | (red Shl 16)
      If rgb=0 Then 
		rgb=$080808 'Avoid transparent pixels
	  Else
	    rgb = $FF000000 + rgb
      End If
      'WritePixelFast ix+cl_wLeft,iy+cl_wTop,rgb,ImageBuffer(graphic,1)'hierX
	  lockbuffer.WritePixel(ix+cl_wLeft,iy+cl_wTop, rgb)
     EndIf
    EndIf
   Next
  Next
  'UnlockBuffer ImageBuffer(graphic,1)'hierX
  UnlockImage (graphic, 1)
  lockbuffer = Null
  'FreeBank dib 'Free the DIB'hierX

  If bufferi-firstframe>=0 'If this frame is valid
   src:TPixmap=LockImage(graphic,1) 'Copy this graphic to this frame'hierX
   dest:TPixmap=LockImage(image,bufferi-firstframe)'hierX
   PixCopyRect 0,0,width,height,0,0,src,dest'hier
   UnlockImage(graphic, 1)
   UnlockImage(image, bufferi-firstframe)
  EndIf

  'Decide how to dispose of this graphic
  If cl_Disposal<2 '0=Not specified, 1=Do not dispose, both do nothing
  ElseIf cl_Disposal=2 'Restore to background
   src:TPixmap=LockImage(graphic,2) 'Copy empty graphic to this graphic'hierX
   dest:TPixmap=LockImage(graphic,1)'hierX
   PixCopyRect cl_wLeft,cl_wTop,wWidth,wHeight,cl_wLeft,cl_wTop,src,dest'hier
   UnlockImage(graphic, 2)
   UnlockImage(graphic, 1)
  ElseIf cl_Disposal=3 'Restore to previous
   src:TPixmap=LockImage(graphic,0) 'Copy previous graphic to this graphic'hierX
   dest:TPixmap=LockImage(graphic,1)'hierX
   PixCopyRect cl_wLeft,cl_wTop,wWidth,wHeight,cl_wLeft,cl_wTop,src,dest'hier
   UnlockImage(graphic, 1)
   UnlockImage(graphic, 0)
  EndIf

 Next

 'FreeImage graphic 'Free the graphic'hierX
 ResetGlobals()
 Return image 'Return the image

End Function

Function GIFDelayTimes(filename$,firstframe=1,numframes=0)
 'Creates and returns a bank with delay times for a gif
 'Bank values are in integers, the number of frames is at 0
 'and the delay time for frame 1 is at 1*4, etc
 'firstframe -> The frame to start reading from, 1=first frame
 'numframes -> The number of frames to read, 0=all frames

 Local file:TStream,count,Sig$,bank,bPacked,isColorTable,blocksize
 Local dbyte,blocklabel,wDelayTime,frames

 file=ReadFile_EN(filename$)
 If file=Null
  RuntimeError "File could not be opened"
 EndIf

 'Header block
 For count=0 To 2
  Sig$=Sig$+Chr(ReadByte(file))
 Next
 If Sig$<>"GIF"
  Return 0 'Not a valid gif
 EndIf
 SeekStream(file,StreamPos(file)+3) 'Skip Version

 bank=CreateBank(4) 'Create the delay times bank

 'Logical Screen Descriptor block
 SeekStream(file,StreamPos(file)+4) 'Skip Screen Width/Height
 bPacked=ReadByte(file) 'bPacked
 SeekStream(file,StreamPos(file)+2) 'Skip BackgroundColor/AspectRatio

 'Global Color Table block
 isColorTable=(bPacked & 128) Shr 7 'Global Color Table Flag, bit 7
 If isColorTable
  blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Table size, bits 0..2
  SeekStream(file,StreamPos(file)+blocksize) 'Skip table if present
 EndIf

 'Parse the blocks
 While Not Eof(file)

  dbyte=ReadByte(file)

  If dbyte=$21 'Extension block (89a)
   blocklabel=ReadByte(file)

   If blocklabel=$01 'Plain Text Extension block
    blocksize=ReadByte(file) 'Should be 12
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$F9 'Graphic Control Extension block
    blocksize=ReadByte(file) 'Should be 4
    SeekStream(file,StreamPos(file)+1) 'Skip bPacked
    wDelayTime=ReadShort(file) '100ths of a second (1/100)
    SeekStream(file,StreamPos(file)+1) 'Skip Transparent Color Index
    blocksize=ReadByte(file) 'Should be 0
    ResizeBank(bank,8+(frames Shl 2))
    PokeInt bank,4+(frames Shl 2),wDelayTime*10 'Convert to millisecs

   ElseIf blocklabel=$FE 'Comment Extension block
    blocksize=ReadByte(file) 'Should be 1..255
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$FF 'Application Extension block
    blocksize=ReadByte(file) 'Should be 11
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   EndIf
  EndIf

  If dbyte=$2C 'Image Descriptor block (87a)
   blocklabel=dbyte
   SeekStream(file,StreamPos(file)+8) 'Skip Image Left/Top/Width/Height
   bPacked=ReadByte(file)
   isColorTable=(bPacked & 128) Shr 7 'Local Color Table Flag, bit 7
   If isColorTable 'Local Color Table block
    blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Table size, bits 0..2
    SeekStream(file,StreamPos(file)+blocksize) 'Skip table if present
   EndIf
   frames=frames+1 'Increment frames
  EndIf

  If dbyte>1 And dbyte<13 'Image Data block (87a)
   blocklabel=dbyte 'LZW bit range is 2..12
   blocksize=ReadByte(file) '1..255
   While blocksize>0 'Skip sub-blocks
    SeekStream(file,StreamPos(file)+blocksize)
    blocksize=ReadByte(file)
   Wend
  EndIf

 Wend

 'Modify the bank according to optional parameters
 If firstframe>0 Then firstframe=firstframe-1 Else firstframe=0
 If firstframe>frames-1 Then firstframe=frames-1 'Limit firstframe
 If numframes<=0 Then numframes=frames-firstframe 'Limit numframes
 If numframes>frames-firstframe Then numframes=frames-firstframe
 For count=1 To numframes 'Move the values one at a time
  PokeInt bank,count*4,PeekInt(bank,(count+firstframe)*4)
 Next
 ResizeBank(bank,4+(numframes*4))
 PokeInt bank,0,numframes 'Store the frames

 CloseFile file 'Close the file
 Return bank 'Return the delay times bank

End Function

Function GIFFrames(filename$,firstframe=1,numframes=0)
 'Returns the number of frames in a gif
 'For this we have to manually count them since gifs
 'don't have aGlobal cl_to store the number of frames
 'firstframe -> The frame to start counting from, 1=first frame
 'numframes -> The number of frames to count, 0=all frames

 Local file:TStream,count,Sig$,bPacked,isColorTable,blocksize
 Local dbyte,blocklabel,frames

 file=ReadFile_EN(filename$)
 If file=Null
  RuntimeError "File could not be opened"
 EndIf

 'Header block
 For count=0 To 2
  Sig$=Sig$+Chr(ReadByte(file))
 Next
 If Sig$<>"GIF"
  Return 0 'Gif header not valid
 EndIf
 SeekStream(file,StreamPos(file)+3) 'Skip Version

 'Logical Screen Descriptor block
 SeekStream(file,StreamPos(file)+4) 'Skip Screen Width/Height
 bPacked=ReadByte(file) 'bPacked
 SeekStream(file,StreamPos(file)+2) 'Skip BackgroundColor/AspectRatio

 'Global Color Table block
 isColorTable=(bPacked & 128) Shr 7 'Global Color Table Flag, bit 7
 If isColorTable
  blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Table size, bits 0..2
  SeekStream(file,StreamPos(file)+blocksize) 'Skip table if present
 EndIf

 'Parse the blocks
 While Not Eof(file)

  dbyte=ReadByte(file)

  If dbyte=$21 'Extension block (89a)
   blocklabel=ReadByte(file)

   If blocklabel=$01 'Plain Text Extension block
    blocksize=ReadByte(file) 'Should be 12
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$F9 'Graphic Control Extension block
    blocksize=ReadByte(file) 'Should be 4
    SeekStream(file,StreamPos(file)+blocksize)
    blocksize=ReadByte(file) 'Should be 0

   ElseIf blocklabel=$FE 'Comment Extension block
    blocksize=ReadByte(file) 'Should be 1..255
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$FF 'Application Extension block
    blocksize=ReadByte(file) 'Should be 11
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   EndIf
  EndIf

  If dbyte=$2C 'Image Descriptor block (87a)
   blocklabel=dbyte
   SeekStream(file,StreamPos(file)+8) 'Skip Image Left/Top/Width/Height
   bPacked=ReadByte(file)
   isColorTable=(bPacked & 128) Shr 7 'Local Color Table Flag, bit 7
   If isColorTable 'Local Color Table block
    blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Table size, bits 0..2
    SeekStream(file,StreamPos(file)+blocksize) 'Skip table if present
   EndIf
   frames=frames+1 'Increment frames
  EndIf

  If dbyte>1 And dbyte<13 'Image Data block (87a)
   blocklabel=dbyte 'LZW bit range is 2..12
   blocksize=ReadByte(file) '1..255
   While blocksize>0 'Skip sub-blocks
    SeekStream(file,StreamPos(file)+blocksize)
    blocksize=ReadByte(file)
   Wend
  EndIf

 Wend

 'Modify the return value according to optional parameters
 If firstframe>0 Then firstframe=firstframe-1 Else firstframe=0
 If firstframe>frames-1 Then firstframe=frames-1 'Limit firstframe
 If numframes<=0 Then numframes=frames-firstframe 'Limit numframes
 If numframes>frames-firstframe Then numframes=frames-firstframe

 CloseFile file 'Close the file
 Return numframes 'Return the number of frames

End Function

Function GIFLoops(filename$)
 'Returns the number of loops in a gif, 0=forever
 'This is the number of times the animation is repeated

 Local file:TStream,count,Sig$,bPacked,isColorTable,blocksize
 Local dbyte,blocklabel,app$,loops,frames

 file=ReadFile_EN(filename$)
 If file=Null
  RuntimeError "File could not be opened"
 EndIf

 'Header block
 For count=0 To 2
  Sig$=Sig$+Chr(ReadByte(file))
 Next
 If Sig$<>"GIF"
  Return 0 'Gif header not valid
 EndIf
 SeekStream(file,StreamPos(file)+3) 'Skip Version

 'Logical Screen Descriptor block
 SeekStream(file,StreamPos(file)+4) 'Skip Screen Width/Height
 bPacked=ReadByte(file) 'bPacked
 SeekStream(file,StreamPos(file)+2) 'Skip BackgroundColor/AspectRatio

 'Global Color Table block
 isColorTable=(bPacked & 128) Shr 7 'Global Color Table Flag, bit 7
 If isColorTable
  blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Table size, bits 0..2
  SeekStream(file,StreamPos(file)+blocksize) 'Skip table if present
 EndIf

 'Parse the blocks
 While Not Eof(file)

  dbyte=ReadByte(file)

  If dbyte=$21 'Extension block (89a)
   blocklabel=ReadByte(file)

   If blocklabel=$01 'Plain Text Extension block
    blocksize=ReadByte(file) 'Should be 12
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$F9 'Graphic Control Extension block
    blocksize=ReadByte(file) 'Should be 4
    SeekStream(file,StreamPos(file)+blocksize)
    blocksize=ReadByte(file) 'Should be 0

   ElseIf blocklabel=$FE 'Comment Extension block
    blocksize=ReadByte(file) 'Should be 1..255
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$FF 'Application Extension block
    blocksize=ReadByte(file) 'Should be 11
    app$=""
    For count=1 To blocksize 'Read the info
     app$=app$+Chr(ReadByte(file))
    Next
    If app$="NETSCAPE2.0" 'Netscape's looping extension
     SeekStream(file,StreamPos(file)+2) 'Skip 2 bytes
     loops=ReadShort(file) 'Number of times to loop, 0=forever
     SeekStream(file,StreamPos(file)-4) 'Return to blocksize
    EndIf
    blocksize=ReadByte(file)
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   EndIf
  EndIf

  If dbyte=$2C 'Image Descriptor block (87a)
   blocklabel=dbyte
   SeekStream(file,StreamPos(file)+8) 'Skip Image Left/Top/Width/Height
   bPacked=ReadByte(file)
   isColorTable=(bPacked & 128) Shr 7 'Local Color Table Flag, bit 7
   If isColorTable 'Local Color Table block
    blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Table size, bits 0..2
    SeekStream(file,StreamPos(file)+blocksize) 'Skip table if present
   EndIf
   frames=frames+1 'Increment frames
  EndIf

  If dbyte>1 And dbyte<13 'Image Data block (87a)
   blocklabel=dbyte 'LZW bit range is 2..12
   blocksize=ReadByte(file) '1..255
   While blocksize>0 'Skip sub-blocks
    SeekStream(file,StreamPos(file)+blocksize)
    blocksize=ReadByte(file)
   Wend
  EndIf

 Wend

 CloseFile file 'Close the file
 Return loops 'Return the number of loops

End Function

Function GIFWidth(filename$)
 'Returns the screen width of a gif
 'This is the actual width of the image

 Local file:TStream,count,Sig$,wScreenWidth,wScreenHeight

 file=ReadFile_EN(filename$)
 If file=Null
  RuntimeError "File could not be opened"
 EndIf

 'Header block
 For count=0 To 2
  Sig$=Sig$+Chr(ReadByte(file))
 Next
 If Sig$<>"GIF"
  Return 0 'Gif header not valid
 EndIf
 SeekStream(file,StreamPos(file)+3) 'Skip Version

 'Logical Screen Descriptor block
 wScreenWidth=ReadShort(file)
 wScreenHeight=ReadShort(file)

 CloseFile file 'Close the file
 Return wScreenWidth 'Return the width

End Function

Function GIFHeight(filename$)
 'Returns the screen height of a gif
 'This is the actual height of the image

 Local file:TStream,count,Sig$,wScreenWidth,wScreenHeight

 file=ReadFile_EN(filename$)
 If file=Null
  RuntimeError "File could not be opened"
 EndIf

 'Header block
 For count=0 To 2
  Sig$=Sig$+Chr(ReadByte(file))
 Next
 If Sig$<>"GIF"
  Return 0 'Gif header not valid
 EndIf
 SeekStream(file,StreamPos(file)+3) 'Skip Version

 'Logical Screen Descriptor block
 wScreenWidth=ReadShort(file)
 wScreenHeight=ReadShort(file)

 CloseFile file 'Close the file
 Return wScreenHeight 'Return the height

End Function

Function GIFLoad(filename$)
 'Creates and returns a bank with a DIB for a gif frame
 'Uses GIFOutLine and GIFNextCode, used by GIFLoadImage

 Local Stack[4096+1] 'Stack for storing pixels, bytes
 Local Suffix[4096+1] 'Suffix table, max number of LZW codes, bytes
 Local Prefix[4096+1] 'Prefix linked list, integers
 Local GlobalCols[256+1]
 Local LocalCols[256+1]
 Local file:TStream,count,Sig$,bPacked,isGlobalTable,blocksize
 Local GlobalColors,dbyte,blocklabel,wWidth,wHeight,LocalColors
 Local red,green,blue,frames,LZWCodeSize,BitCount,ncolors,size
 Local dib,pal,TopSlot,ClearCode,EndingCode,NewCodes,Slot,cc
 Local TempOldCode,OldCode,pStack,pBuffer=0,BufCount,Buffer,Code

 file=ReadFile_EN(filename$)
 If file=Null
  RuntimeError "File could not be opened"
 EndIf

 'Header block
 For count=0 To 2
  Sig$=Sig$+Chr(ReadByte(file))
 Next
 If Sig$<>"GIF"
  CloseFile file 'Close the file
  Return 0 'Gif header not valid
 EndIf
 SeekStream(file,StreamPos(file)+3) 'Skip Version, both are supported

 cl_CodeMask[0]=$0000 'LZW Code Masks
 cl_CodeMask[1]=$0001 ; cl_CodeMask[2]=$0003 ; cl_CodeMask[3]=$0007
 cl_CodeMask[4]=$000F ; cl_CodeMask[5]=$001F ; cl_CodeMask[6]=$003F
 cl_CodeMask[7]=$007F ; cl_CodeMask[8]=$00FF ; cl_CodeMask[9]=$01FF
 cl_CodeMask[10]=$03FF ; cl_CodeMask[11]=$07FF ; cl_CodeMask[12]=$0FFF
 cl_CodeMask[13]=$1FFF ; cl_CodeMask[14]=$3FFF ; cl_CodeMask[15]=$7FFF

 'Logical Screen Descriptor block
 SeekStream(file,StreamPos(file)+4) 'Skip Screen Width/Height
 bPacked=ReadByte(file) 'Packed Fields
 cl_bBackIndex=ReadByte(file) 'Background Color Index, ignored
 isGlobalTable=(bPacked & 128) Shr 7 'Global Color Table Flag, bit 7
 blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Global Table size, bits 0..2
 SeekStream(file,StreamPos(file)+1) 'Skip Aspect Ratio

 GlobalColors=blocksize/3 'Number of global colors
 If GlobalColors<=2 'Use number of colors to set bit depth
  cl_GlobalBpp=1
 ElseIf GlobalColors<=16
  cl_GlobalBpp=4
 Else
  cl_GlobalBpp=8
 EndIf

 'Global Color Table block
 If isGlobalTable
  For count=0 To GlobalColors-1 'Store global colors
   red=ReadByte(file)
   green=ReadByte(file)
   blue=ReadByte(file)
   GlobalCols[count]=(red Shl 16) | (green Shl 8) | blue
  Next
 Else
  For count=0 To GlobalColors-1 'Create a 2/16/256 greyscale palette
   red=(255*count)/(GlobalColors-1)
   green=(255*count)/(GlobalColors-1)
   blue=(255*count)/(GlobalColors-1)
   GlobalCols[count]=(red Shl 16) | (green Shl 8) | blue
  Next
 EndIf

 'Return to where we left off reading last call
 If cl_NextFilePos>0 Then SeekStream(file,cl_NextFilePos)

 cl_isTransparent=0 'Make sure these extension variables are zero
 cl_bTransIndex=0
 cl_Disposal=0

 'Parse the blocks
 While Not Eof(file)

  dbyte=ReadByte(file)

  If dbyte=$21 'Extension block (89a)
   blocklabel=ReadByte(file)

   If blocklabel=$01 'Plain Text Extension block
    blocksize=ReadByte(file) 'Should be 12
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$F9 'Graphic Control Extension block
    blocksize=ReadByte(file) 'Should be 4
    bPacked=ReadByte(file) 'Packed Fields
    cl_Disposal=(bPacked & (4+8+16)) Shr 2 'Disposal Method, bits 2..4
    cl_isTransparent=bPacked & 1 'Transparent Color Flag, bit 0
    SeekStream(file,StreamPos(file)+2) 'Skip Delay Time
    cl_bTransIndex=ReadByte(file) 'Transparent Color Index
    blocksize=ReadByte(file) 'Block Terminator, always 0

   ElseIf blocklabel=$FE 'Comment Extension block
    blocksize=ReadByte(file) 'Should be 1..255
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   ElseIf blocklabel=$FF 'Application Extension block
    blocksize=ReadByte(file) 'Should be 11
    While blocksize>0 'Skip sub-blocks
     SeekStream(file,StreamPos(file)+blocksize)
     blocksize=ReadByte(file)
    Wend

   EndIf
  EndIf

  If dbyte=$2C 'Image Descriptor block (87a)
   blocklabel=dbyte
   cl_wLeft=ReadShort(file)
   cl_wTop=ReadShort(file)
   wWidth=ReadShort(file)
   wHeight=ReadShort(file)
   bPacked=ReadByte(file)
   cl_isLocalTable=(bPacked & 128) Shr 7 'Local Color Table Flag, bit 7
   cl_isInterlaced=(bPacked & 64) Shr 6 'Interlace Flag, bit 6
   blocksize=3*(1 Shl ((bPacked & 7)+1)) 'Local Table size, bits 0..2
   LocalColors=blocksize/3 'Number of local colors
   If LocalColors<=2 'Use number of colors to set bit depth
    cl_LocalBpp=1
   ElseIf LocalColors<=16
    cl_LocalBpp=4
   Else
    cl_LocalBpp=8
   EndIf
   If cl_isLocalTable 'Local Color Table block
    For count=0 To LocalColors-1 'Store local colors
     red=ReadByte(file)
     green=ReadByte(file)
     blue=ReadByte(file)
     LocalCols[count]=(red Shl 16) | (green Shl 8) | blue
    Next
   EndIf
   frames=frames+1 'Increment frames
  EndIf

  If dbyte>1 And dbyte<13 'Image Data block (87a)
   LZWCodeSize=dbyte 'LZW bit range is 2..12

   count=StreamPos(file) 'Store this block
   blocksize=ReadByte(file) '1..255
   While blocksize>0 'Skip sub-blocks
    SeekStream(file,StreamPos(file)+blocksize)
    blocksize=ReadByte(file)
   Wend
   cl_NextFilePos=StreamPos(file) 'Store the next block
   SeekStream(file,count) 'Return to this block

   'Set the bit depth and number of colors
   If cl_isLocalTable
    BitCount=cl_LocalBpp
    ncolors=LocalColors
   Else
    BitCount=cl_GlobalBpp
    ncolors=GlobalColors
   EndIf
   If ncolors=0 Then ncolors=1 Shl BitCount 'If no palette

   'Allocate memory for DIB
   cl_Pitch=(((BitCount*wWidth)+31) Shr 5) Shl 2 'Bytes per line
   size=40+(ncolors*4)+(cl_Pitch*wHeight) 'Size of DIB
   dib=CreateBank(size)
   cl_DIB=dib 'cl_DIB used in GIFOutLine

   'Fill in the DIB info header
   PokeInt dib,0,40 'biSize, 40
   PokeInt dib,4,wWidth 'biWidth
   PokeInt dib,8,wHeight 'biHeight
   PokeShort dib,12,1 'biPlanes, 1
   PokeShort dib,14,BitCount 'biBitCount, 1/4/8
   PokeInt dib,16,0 'biCompression, #BI_RGB=0
   PokeInt dib,20,cl_Pitch*wHeight 'biSizeImage
   PokeInt dib,24,0 'biXPelsPerMeter
   PokeInt dib,28,0 'biYPelsPerMeter
   PokeInt dib,32,ncolors 'biClrUsed
   PokeInt dib,36,0 'biClrImportant

   pal=40 'Fill in the DIB palette
   If cl_isLocalTable
    For count=0 To ncolors-1
     PokeByte dib,pal,LocalCols[count] & $0000FF 'Blue
     PokeByte dib,pal+1,(LocalCols[count] & $00FF00) Shr 8 'Green
     PokeByte dib,pal+2,(LocalCols[count] & $FF0000) Shr 16 'Red
     pal=pal+4
    Next
   Else
    For count=0 To ncolors-1
     PokeByte dib,pal,GlobalCols[count] & $0000FF 'Blue
     PokeByte dib,pal+1,(GlobalCols[count] & $00FF00) Shr 8 'Green
     PokeByte dib,pal+2,(GlobalCols[count] & $FF0000) Shr 16 'Red
     pal=pal+4
    Next
   EndIf

   'Init variables for the decoder for reading a new image
   cl_CurrCodeSize=LZWCodeSize+1
   TopSlot=1 Shl cl_CurrCodeSize 'Highest code for current size
   ClearCode=1 Shl LZWCodeSize 'Value for a clear code
   EndingCode=ClearCode+1 'Value for an ending code
   NewCodes=ClearCode+2 'First available code
   Slot=NewCodes 'Last read code
   cl_BitsLeft=0 'Make sure these LZW variables are zero
   cl_BytesLeft=0
   pStack=0 ; pBuffer=0 'Init the stack and decode buffer pointers
   BufCount=wWidth 'Line counter (count for pixel line length)
   cl_iLine=0 ; cl_iPass=0 'Init line offset and interlace pass
   cl_pBits=40+(ncolors*4)+(cl_Pitch*(wHeight-1)) 'Pointer to bits

   'Allocate space for the decode buffer
   Buffer=CreateBank(wWidth+16) '+16 just in case

   'This is the main loop. For each code we get we pass through the
   'linked list of prefix codes, pushing the corresponding "character"
   'for each code onto the stack. When the list reaches a single
   '"character" we push that on the stack too, and then start
   'unstacking each character for output in the correct order.
   'Special handling is included for the clear code, and the whole
   'thing ends when we get an ending code.
   While cc<>EndingCode

    cc=GIFNextCode(file)
    If cc<0 Then Exit 'File error, exit without completing the decode

    'If the code is a clear code, re-initialise all necessary items
    If cc=ClearCode

     cl_CurrCodeSize=LZWCodeSize+1
     Slot=NewCodes
     TopSlot=1 Shl cl_CurrCodeSize

     'Continue reading codes until we get a non-clear code
     '(another unlikely, but possible case...)
     While cc=ClearCode
      cc=GIFNextCode(file)
     Wend

     'If we get an ending code immediately after a clear code
     '(yet another unlikely case), then break out of the loop
     If cc=EndingCode Then Exit 'end loop

     'Finally, if the code is beyond the range of already set codes,
     '(This one had better not happen, I have no idea what will
     'result from this, but I doubt it will look good)
     'then set it to color zero.
     If cc>=Slot Then cc=0
     OldCode=cc
     TempOldCode=OldCode

     'And let us not forget to put the char into the buffer, and if,
     'on the off chance, we were exactly one pixel from the end of
     'the line, we have to send the buffer to the GIFOutLine routine.
     PokeByte Buffer,pBuffer,cc
     pBuffer=pBuffer+1
     BufCount=BufCount-1
     If BufCount=0
      GIFOutLine(Buffer,wWidth,wHeight)
      pBuffer=0
      BufCount=wWidth
     EndIf

    Else

     'In this case, it's not a clear code or an ending code, so it
     'must be a code code. So we can now decode the code into a
     'stack of character codes (Clear as mud, right?).
     Code=cc
     If Code=Slot
      Code=TempOldCode
      Stack[pStack]=OldCode
      pStack=pStack+1
     EndIf

     'Here we scan back along the linked list of prefixes, pushing
     'helpless characters (i.e. suffixes) onto the stack as we do so.
     While Code>=NewCodes
      Stack[pStack]=Suffix[Code]
      pStack=pStack+1
      Code=Prefix[Code]
     Wend

     'Push the last character on the stack, and set up the new
     'prefix and suffix, and if the required slot number is greater
     'than that allowed by the current bit size, increase the bit
     'size. (Note - if we are all full, we *don't* save the new
     'suffix and prefix. I'm not certain if this is correct,
     'it might be more proper to overwrite the last code.
     Stack[pStack]=Code
     pStack=pStack+1
     If Slot<TopSlot
      OldCode=Code
      Suffix[Slot]=OldCode
      Prefix[Slot]=TempOldCode
      Slot=Slot+1
      TempOldCode=cc
     EndIf
     If Slot>=TopSlot
      If cl_CurrCodeSize<12
       TopSlot=TopSlot Shl 1
       cl_CurrCodeSize=cl_CurrCodeSize+1
      EndIf
     EndIf

     'Now that we've pushed the decoded string (in reverse order)
     'onto the stack, lets pop it off and put it into our decode
     'buffer, and when the decode buffer is full, write another line.
     While pStack>0
      pStack=pStack-1
      PokeByte Buffer,pBuffer,Stack[pStack]
      pBuffer=pBuffer+1
      BufCount=BufCount-1
      If BufCount=0
       GIFOutLine(Buffer,wWidth,wHeight)
       pBuffer=0
       BufCount=wWidth
      EndIf
     Wend

    EndIf
   Wend

   'If there are any left, output the bytes
   If BufCount<>wWidth
    GIFOutLine(Buffer,wWidth-BufCount-1,wHeight)
   EndIf

   Exit 'End block parsing loop
  EndIf

 Wend

 CloseFile file 'Close the file
 'FreeBank Buffer 'Free the buffer'hierX
 Return dib 'Return the DIB

End Function

Function GIFOutLine(Buffer,Width,Height)
 'Outputs the pixel color index data to the DIB
 'Buffer -> Memory block that holds the color index value
 'Width -> Length of the line of pixels, Height -> wHeight
 'Gif images are 2, 16 or 256 colors, poking the values into memory
 'requires a different method for each case. If gif is interlaced,
 'that is dealt with here.
 'Used by GIFLoad

 Local bits,bpp,count,pixel,dbyte,bitcount

 bits=cl_pBits-(cl_iLine*cl_Pitch) 'Pointer to bits

 If cl_iLine>=Height Then Return False 'Avoid poking out of range

 If cl_isLocalTable
  bpp=cl_LocalBpp
 Else
  bpp=cl_GlobalBpp
 EndIf

 Select bpp
  Case 1 '1-bit DIB
   count=0
   For pixel=0 To Width-1 Step 8
    dbyte=0
    For bitcount=0 To 7
     If PeekByte(Buffer,bitcount+pixel)
      dbyte=dbyte | (1 Shl (7-bitcount))
     EndIf
    Next
    PokeByte cl_DIB,bits+count,dbyte
    count=count+1
   Next
  Case 4 '4-bit DIB
   count=0
   For pixel=0 To Width-1 Step 2
    dbyte=PeekByte(Buffer,pixel) Shl 4
    dbyte=dbyte | PeekByte(Buffer,pixel+1)
    PokeByte cl_DIB,bits+count,dbyte
    count=count+1
   Next
  Case 8 '8-bit DIB
   For pixel=0 To Width-1
    dbyte=PeekByte(Buffer,pixel)
    PokeByte cl_DIB,bits+pixel,dbyte
   Next
 End Select

 If cl_isInterlaced 'Set iLine for different passes when interlaced
  Select cl_iPass
   Case 0 'Pass 1
    If cl_iLine<Height-8
     cl_iLine=cl_iLine+8
    Else
     cl_iLine=4 ; cl_iPass=cl_iPass+1 'For 2nd pass
    EndIf
   Case 1 'Pass 2
    If cl_iLine<Height-8
     cl_iLine=cl_iLine+8
    Else
     cl_iLine=2 ; cl_iPass=cl_iPass+1 'For 3rd pass
    EndIf
   Case 2 'Pass 3
    If cl_iLine<Height-4
     cl_iLine=cl_iLine+4
    Else
     cl_iLine=1 ; cl_iPass=cl_iPass+1 'For 4th pass
    EndIf
   Case 3 'Pass 4
    If cl_iLine<Height-2
     cl_iLine=cl_iLine+2
    EndIf
  End Select
 Else 'When not interlaced increment iLine
  cl_iLine=cl_iLine+1
 EndIf

End Function

Function GIFNextCode(file:TStream)
 'Reads the next code from the data stream
 'Returns the LZW code or error
 'Used by GIFLoad

 Local count,char,ret

 If cl_BitsLeft=0 'Any bits left in byte?

  If cl_BytesLeft<=0 'If not get another block
   cl_pCharBuff=0 'Reset byte pointer
   cl_BytesLeft=ReadByte(file) 'Block size
   If cl_BytesLeft=0 Then Return -2 'Found block terminator
   If Eof(file)<>0 Then Return -1 'Stream error or end of file
   For count=0 To cl_BytesLeft-1
    char=ReadByte(file)
    cl_CharBuff[count]=char 'Fill CharBuff with the new block
   Next
  EndIf

  cl_CurrByte=cl_CharBuff[cl_pCharBuff] 'Get a byte
  cl_pCharBuff=cl_pCharBuff+1 'Increment byte pointer
  cl_BitsLeft=8 'Set bits left in the byte
  cl_BytesLeft=cl_BytesLeft-1 'Decrement BytesLeft counter

 EndIf

 'Shift off any previously used bits
 ret=cl_CurrByte Shr (8-cl_BitsLeft)

 While cl_CurrCodeSize>cl_BitsLeft

  If cl_BytesLeft<=0 'Out of bytes in current block
   cl_pCharBuff=0 'Set byte pointer
   cl_BytesLeft=ReadByte(file) 'Block size
   If cl_BytesLeft=0 Then Return -2 'Found block terminator
   If Eof(file)<>0 Then Return -1 'Stream error or end of file
   For count=0 To cl_BytesLeft-1
    char=ReadByte(file)
    cl_CharBuff[count]=char 'Fill CharBuff with current block
   Next
  EndIf

  cl_CurrByte=cl_CharBuff[cl_pCharBuff] 'Get a byte
  cl_pCharBuff=cl_pCharBuff+1 'Increment byte pointer
  ret=ret | (cl_CurrByte Shl cl_BitsLeft) 'Add remaining bits to ret
  cl_BitsLeft=cl_BitsLeft+8 'Set bit counter
  cl_BytesLeft=cl_BytesLeft-1 'Decrement BytesLeft counter

 Wend

 'Subtract the code size from BitsLeft
 cl_BitsLeft=cl_BitsLeft-cl_CurrCodeSize
 'Mask off the right number of bits
 ret=ret & cl_CodeMask[cl_CurrCodeSize] 
 Return ret

End Function

Function PixCopyRect(x, y, w, h, x2, y2, src:TPixmap, dest:TPixmap)

	window:TPixmap = PixmapWindow(src, x, y, w, h)
	dest.Paste(window, x2, y2)

End Function

Function LoadAnimGif:TImage(url:String, ww, hh, st, cnt)

	Local gif:TImage = LoadGif(url)
	Local img:TImage = CreateImage(ww, hh, cnt)	
	
	x = 0
	y = 0
	For i = 0 To st + cnt - 1
		Cls
		DrawImage gif, -x, -y
		If i >= st Then GrabImage img, 0, 0, i - st
		x = x + ww
		If x > ImageWidth(gif)-ww Then x = 0 y = y + hh
	Next
		
	Return img
	
End Function

Function ResetGlobals()

 cl_pCharBuff=0 'Pointer to next byte in block
 cl_iPass=0 'First pass for interlaced images in GIFOutLine
 cl_iLine=0 'Offset for addressing the bits in GIFOutLine
 cl_pBits=0 'Scanline for bits
 cl_Pitch=0 'Bytes are rounded up for image lines
 cl_CurrCodeSize=0 'The current code size
 cl_BitsLeft=0 'Used in GIFNextCode
 cl_BytesLeft=0 'Used in GIFNextCode
 cl_CurrByte=0 'Current byte
 cl_Bpp=0 ' bit depth
 cl_isInterlaced=0 'Is the image interlaced
 cl_LocalBpp=0 'Local bit depth
 cl_DIB=0 'DIB bank handle
 cl_NextFilePos=0 'Used to jump to the next block
 cl_isLocalTable=0 'Local Color Table Flag
 cl_bBackIndex=0 'Background Color Index
 cl_Disposal=0 'Disposal Method, 0..3
 cl_isTransparent=0 'Transparent Color Flag
 cl_wLeft=0 'Image Left Position
 cl_wTop=0 'Image Top Position
 cl_bTransIndex=0 'Transparent Color Index
 cl_CodeMask=Null'Null 'Masks for LZW compression algorithm
 cl_CharBuff=Null'Null 'Current block

End Function

Function ReadFile_EN:TStream(url:Object)

	Return LittleEndianStream(ReadFile(url))
	
End Function

Function LoadImage:TImage(url : Object, flags = -1)

	path$ = String(url)
	If path <> Null Then
		If Lower$(ExtractExt(path$)) = "gif" Then Return LoadGif(path$)
	EndIf
	Return brl.max2d.LoadImage(url, flags)

End Function
