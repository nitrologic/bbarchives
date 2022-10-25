; ID: 1858
; Author: Nicholas
; Date: 2006-11-09 13:55:45
; Title: Mappy Routine
; Description: Conversion of my Mappy plug-in from PureBasic/GSDK/DBPro

Const MAPPYERROR_INVALIDCOORD			=	  1
	Const MAPPYERROR_OK 					=	  0
	Const MAPPYERROR_NOFILENAME				=	  2
	Const MAPPYERROR_FILENOTFOUND			=	  3
	Const MAPPYERROR_FILENOTOPENED			=	  4
	Const MAPPYERROR_INVALIDHEADER 			=	  5
	Const MAPPYERROR_OUTOFMEM 				=	  6
	Const MAPPYERROR_INVALIDLAYER 			=	  7
	Const MAPPYERROR_UNKNOWNVERSION 		=	  8
	Const MAPPYERROR_TILENOTFOUND 			=	  9
	Const MAPPYERROR_LAYERNOTINUSE 			=	 10
	Const MAPPYERROR_MAPNOTLOADED 			=	 11
	Const MAPPYERROR_LAYERINUSE 			=	 12
	Const MAPPYERROR_UNKNOWNHEADER			=	 13
 
	Const FMP05:Byte 						=	  0
	Const FMP10:Byte 						=	  1
	Const FMP10RLE:Byte					=	  2
	
	Const FOREGROUND1						=													  0
	Const FOREGROUND2						=			  1
	Const FOREGROUND3						=	  2
		
	Type RGB
		Field r:Byte
		Field g:Byte
		Field b:Byte
	EndType
	
	Type ANISTR
		Field antype:Byte		' Type of anim, AN_? 
		Field andelay:Byte		' Frames To go before Next frame 
		Field ancount:Byte		' Counter, decs each frame, till 0, Then resets To andelay 
		Field anuser:Byte		' User info
		Field ancuroff:Int		' Points To current offset in list
		Field anstartoff:Int	' Points To start of blkstr offsets list, AFTER ref. blkstr offset */
		Field anendoff:Int		' Points To End of blkstr offsets list
	EndType
		
	Type BLKSTR
		Field bgoff:Int,fgoff:Int
		Field fgoff2:Int,fgoff3:Int
		Field user1:Int,user2:Int
		Field user3:Short,user4:Short
		Field user5:Byte
		Field user6:Byte
		Field user7:Byte
		Field flags:Byte
	EndType
	
	Type TMappy
		Const MAX_LAYERS 					=	8
		Const HEADER_SIZE					=	4
		Const HEADER_AUTHOR$ 				=	"ATHR"
		Const HEADER_MAP$ 					=	"MPHD"
		Const HEADER_PALETTE$ 				=	"CMAP"
		Const HEADER_BLOCKGRFX$ 			=	"BGFX"
		Const HEADER_BODY$ 				=	"BODY"
		Const HEADER_LAYER$ 				=	"LYR"
		Const HEADER_ANIMATION$ 			=	"ANDT"
		Const HEADER_BLOCKDATA$ 			=	"BKDT"
		Const HEADER_EDITOR$ 				=	"EDHD"
		Const HEADER_EPHD$ 				=	"EPHD"
		Const MAPPY_HEADER1$ 				=	"FORM"
		Const MAPPY_HEADER2$ 				=	"FMAP"
		Const AN_END 						=	255			' Animation types, AN_END = End of anims 
		Const AN_NONE 					=	0			' No anim defined 
		Const AN_LOOPF 					=	1		' Loops from start To End, Then jumps To start etc 
		Const AN_LOOPR 					=	2		' As above, but from End To start 
		Const AN_ONCE 					=	3			' Only plays once 
		Const AN_ONCEH 					=	4		' Only plays once, but holds End frame 
		Const AN_PPFF 					=	5			' Ping Pong start-End-start-End-start etc 
		Const AN_PPRR 					=	6			' Ping Pong End-start-End-start-End etc 
		Const AN_PPRF 					=	7			' Used internally by playback 
		Const AN_PPFR 					=	8			' Used internally by playback 
		Const AN_ONCES 					=	9		' * Used internally by playback 
		Const TILE_SIZE					=	2
		
		Field m_dMapType:Byte,m_dMapVersion:Short,m_dLSB:Byte
		Field m_dMapWidth:Short,m_dMapHeight:Short,m_dMapDepth:Byte
		Field m_dBlockWidth:Short,m_dBlockHeight:Short,m_dBlockSize:Short
		Field m_dNumBlockStructs:Short,m_dNumBlockGFX:Short
		Field m_dBlockGapX:Short,m_dBlockGapY:Short,m_dBlockStaggerX:Short,m_dBlockStaggerY:Short
		Field m_dClipMask:Short
		Field m_dTrans8:Byte,m_dTransRed:Byte,m_dTransGreen:Byte,m_dTransBlue:Byte
		Field m_dNumAnimations:Int
		Field m_dExtraByteSize:Int
		Field m_dCpGraphicSize:Int,m_cpGraphics:TBank
		Field m_dAuthorSize:Int
		Field m_dTileSizeInBytes:Int,Mappy_FileSize:Int
		Field m_dAuthor$
		Global palette:RGB[256]
		Global blockStructs:BLKSTR[]
		Global layer:TBank[MAX_LAYERS]
		Global extraBytesLayer:TBank[MAX_LAYERS]
		Global animations:ANISTR[]
		Field animationSeq:TBank
		
		Function create:TMappy()
		Local l:Int
		
			m_dMapType=0
			m_dMapVersion=0
			m_dLSB=0
			m_dMapWidth=0
			m_dMapHeight=0
			m_dMapDepth=0
			m_dBlockWidth=0
			m_dBlockHeight=0
			m_dBlockSize=0
			m_dNumBlockStructs=0
			m_dNumBlockGFX=0
			m_dBlockGapX=0
			m_dBlockGapY=0
			m_dBlockStaggerX=0
			m_dBlockStaggerY=0
			m_dClipMask=0
			m_dTrans8=0
			m_dTransRed=0
			m_dTransGreen=0
			m_dTransBlue=0
			m_dNumAnimations=0
			m_dExtraByteSize=0
			m_dTileSizeInBytes=0
			Mappy_FileSize=0
			
			m_dAuthorSize=0
			m_dAuthor$=""
			versionHigh=0
			versionLow=0
			animations=Null
			animationSeq=Null
			
			For l=0 To 255
				palette[l]=Null
			Next 
			
			For l=0 To MAX_LAYERS-1
				layer[l]=Null
				extraBytesLayer[l]=Null
			Next
			
			blockStructs=Null
			animations=Null
			animationSeq=Null
			Return New TMappy
		EndFunction
		
		Method loadMappyWord:Short(handle:TStream,order:Byte)
		Local value:Short
		Local temp:Byte[2]
		
			temp[0]=ReadByte(handle)
			temp[1]=ReadByte(handle)
			
			If order
				value=(Short(temp[1]) Shl 8)+Short(temp[0])
			Else
				value=(Short(temp[0]) Shl 8)+Short(temp[1])
			EndIf
			
			Return value
		EndMethod 		
		
		Method loadMappyHeader(handle:TStream,header:TBank)
			PokeInt(header,0,ReadInt(handle))
		EndMethod
		
		Method swapByteOrder:Int(ThisLong:Int)
		Local Hword:Int
		
			Hword=((Thislong & 4294901760)/65536) & 65535
			Thislong=((Thislong & 255)*16777216)+((Thislong & 65280)*256)+((Hword & 255)*256)+((Hword & 65280)/256)
			Return Thislong	
		EndMethod
		
		Method getMappySize:Int(handle:TStream,header:TBank)
		Local temp:TBank
		Local loop:Int
		Local value:Int
		
			' Read in the header
			PokeInt(header,0,ReadInt(handle))
					
			' Now we read in the size for this header
			value=swapByteOrder(ReadInt(handle))
			Return value
		EndMethod
		
		Method headerToString$(header:TBank)
		Local loop:Int
		Local text$
		
			text$=""
			For loop=0 To HEADER_SIZE-1
				text$:+Chr$(PeekByte(header,loop))
			Next
			
			Return text$
		EndMethod
		
		Method mappySkipSection(handle:TStream,amount:Int)
			SeekStream(handle,StreamPos(handle)+amount)
		EndMethod
		
		Method processPalette:Int(handle:TStream,ChunkSize:Int)
		Local l:Int
		Local i:Int
			
			i=0
			If m_dMapDepth<=8
				For l=0 To (1 Shl m_dMapDepth)-1
					palette[l]=New RGB
					If palette[l]=Null 
						Return MAPPYERROR_OUYTOFMEM		
					EndIf
					
					palette[l].r=ReadByte(handle)
					palette[l].g=ReadByte(handle)
					palette[l].b=ReadByte(handle)
					i:+3			
				Next
			EndIf
			
			If i<>ChunkSize
				mappySkipSection(handle,ChunkSize-l)
			EndIf
			
			Return MAPPYERROR_OK
		EndMethod
		
		Method MapHighTo8(handle:TStream,ChunkSize:Int)
			mappySkipSection(handle,ChunkSize)
		EndMethod
		
		Method processBlockGraphics:Short(handle:TStream,ChunkSize:Int)
			m_cpGraphics=CreateBank(ChunkSize)
			If m_cpGraphics=Null Then Return MAPPYERROR_OUTOFMEM
			
			m_cpGraphicSize=ChunkSize
			Return MapHighTo8(handle,ChunkSize)
		EndMethod
		
		Method getMapHeader(handle:TStream,ChunkSize:Int)
		Local temp:Short
		Local i:Int
		
			m_dMapVersion=(ReadByte(handle) Shl 8)+ReadByte(handle)
			m_dLSB=ReadByte(handle)
			m_dMapType=ReadByte(handle)
			If m_dMapType<>FMP05 And m_dMapType<>FMP10 And m_dMapType<>FMP10RLE
				Return MAPPYERROR_UNKNOWNVERSION
			EndIf
			
			i=4
				
			m_dMapWidth=loadMappyWord(handle,m_dLSB)
			m_dMapHeight=loadMappyWord(handle,m_dLSB)
			temp=loadMappyWord(handle,m_dLSB)
			temp=loadMappyWord(handle,m_dLSB)
			i:+8
			
			m_dBlockWidth=loadMappyWord(handle,m_dLSB)
			m_dBlockHeight=loadMappyWord(handle,m_dLSB)
			m_dMapDepth=loadMappyWord(handle,m_dLSB)
			m_dBlockSize=loadMappyWord(handle,m_dLSB)
			i:+8
			
			m_dNumBlockStructs=loadMappyWord(handle,m_dLSB)
			m_dNumBlockGFX=loadMappyWord(handle,m_dLSB)
			i:+4
			
			m_dTileSizeInBytes=m_dMapWidth*m_dMapHeight
			Select m_dMapDepth
				Case	15,16
					m_dTileSizeInBytes:*2
				Case	24
					m_dTileSizeInBytes:*3
				Case	32
					m_dTileSizeInBytes:*4
			EndSelect
					
			If ChunkSize>24
				m_dMapTrans8=ReadByte(handle)
				m_dMapTransRed=ReadByte(handle)
				m_dMapTransGreen=ReadByte(handle)
				m_dMapTransBlue=ReadByte(handle)
				i:+4
			Else
				m_dMapTrans8=0
				m_dMapTransRed=255
				m_dMapTransGreen=0
				m_dMapTransBlue=255
			EndIf	
			
			If ChunkSize>28
				m_dBlockGapX=loadMappyWord(handle,m_dLSB)
				i:+2
				m_dBlockGapY=loadMappyWord(handle,m_dLSB)
				i:+2
				m_dBlockStaggerX=loadMappyWord(handle,m_dLSB)
				i:+2
				m_dBlockStaggerY=loadMappyWord(handle,m_dLSB)
				i:+2
			Else
				m_dBlockGapX=m_dBlockWidth
				m_dBlockGapY=m_dBlockHeight
				m_dBlockStaggerX=0
				m_dBlockStaggerY=0
			EndIf
			
			If ChunkSize>36
				m_dClipMask=loadMappyWord(handle,m_dLSB)
			Else
				m_dClipMask=0
			EndIf
	
			Return MAPPYERROR_OK
		EndMethod
		
		Method allocateLayer:Int(layerLevel:Int,ChunkSize:Int,extraBytes:Int)
			layer[layerLevel]=CreateBank(ChunkSize*TILE_SIZE)
			If layer[layerLevel]=Null Then Return MAPPYERROR_OUTOFMEM
			
			If extraBytes>0
				extraBytesLayer[layerLevel]=CreateBank(ChunkSize*extraBytes)
				If extraBytesLayer[layerLevel]=Null Then Return MAPPYERROR_OUTOFMEM
			EndIf
			
			Return MAPPYERROR_OK	
		EndMethod
		
		Method getTileMapLayer:Int(handle:TStream,ChunkSize:Int,layerLevel:Int,extraBytes:Int)
		Local status:Int
		Local lp:Int
		Local data:Short
		Local rleCount:Int
		
			status=allocateLayer(layerLevel,m_dMapWidth*m_dMapHeight,extraBytes)
			If status=MAPPYERROR_OK
				'Select doesn't seem to want to work with Byte variables
				
				If m_dMapType=FMP05 Or m_dMapType=FMP10
					lp=0
					While lp<ChunkSize
						data=ReadShort(handle)
						If m_dMapType=FMP05
							If data & 32768
								data=((65536-data)/16) | 32768
							Else
								data:/m_dBlockSize
							EndIf
						EndIf
						
						PokeShort(layer[layerLevel],lp,data)
						lp:+SizeOf data
					EndWhile
				Else
					While lp<ChunkSize
						rleCount=ReadShort(handle)
						If rleCount>0
							While rleCount>0
								data=ReadShort(handle)
								PokeShort(layer[layerLevel],lp,data)
								lp:+SizeOf data
								relCount:-1
							EndWhile
						Else
							If rleCount>32767
								rel:-32767
								data=ReadShort(handle)
								While rleCount>0
									PokeShort(layer[layerLevel],lp,data)
									lp:+SizeOf data
									relCount:-1
								EndWhile
							EndIf
						EndIf
					EndWhile
				EndIf
			EndIf
			
			Return status
		EndMethod
		
		Method processBlockData:Int(handle:TStream,ChunkSize:Int)
		Local loop:Int
		
			blockStructs=New BLKSTR[m_dNumBlockStructs]
			If blockStructs=Null Then Return MAPPYERROR_OUTOFMEM
			
			For loop=0 To m_dNumBlockStructs-1
				blockStructs[loop]=New BLKSTR
				If blockStructs[loop]=Null Then Return MAPPYERROR_OUTOFMEM
				
				blockStructs[loop].bgoff=ReadInt(handle)
				blockStructs[loop].fgoff=ReadInt(handle)
				blockStructs[loop].fgoff2=ReadInt(handle)
				blockStructs[loop].fgoff3=ReadInt(handle)
				blockStructs[loop].user1=ReadInt(handle)
				blockStructs[loop].user2=ReadInt(handle)
				blockStructs[loop].user3=ReadShort(handle)
				blockStructs[loop].user4=ReadShort(handle)
				blockStructs[loop].user5=ReadByte(handle)
				blockStructs[loop].user6=ReadByte(handle)
				blockstructs[loop].user7=ReadByte(handle)
				blockStructs[loop].flags=ReadByte(handle)
			Next
				
			Return MAPPYERROR_OK	
		EndMethod
		
		Method processAnimationData:Int(tempBuffer:TBank,ChunkSize:Int)
		Local sequenceSize:Int
		Local temp:Int
		Local loop:Int
		Local offset:Int
		Local currentPos:Long
		Local value:Int
		
			'Count backwards to get the number of animations
			m_dNumAnimations=0
			sequenceSize=ChunkSize
			temp=ChunkSize
			
			Repeat
				temp:-SizeOf ANISTR
				sequenceSize:-SizeOf ANISTR
				m_dNumAnimations:+1
			Until PeekByte(tempBuffer,temp)=AN_END
				
			animations=New ANISTR[m_dNumAnimations]
			If animations=Null Then Return MAPPYERROR_OUTOFMEM
			
			sequenceSize:/4
			animationSeq=CreateBank(sequenceSize*SizeOf sequenceSize)
			If animationSeq=Null Then Return MAPPYERROR_OUTOFMEM
				
			offset=temp
			For loop=0 To m_dNumAnimations-1
				animations[loop]=New ANISTR
				If animations[loop]=Null Then Return MAPPYERROR_OUTOFMEM
				
				animations[loop].antype=PeekByte(tempBuffer,offset)
				animations[loop].andelay=PeekByte(tempBuffer,offset+1)
				animations[loop].ancount=PeekByte(tempBuffer,offset+2)
				animations[loop].anuser=PeekByte(tempBuffer,offset+3)
				animations[loop].ancuroff=PeekInt(tempBuffer,offset+4)
				animations[loop].anstartoff=PeekInt(tempBuffer,offset+8)
				animations[loop].anendoff=PeekInt(tempBuffer,offset+12)
				
				If m_dMapType=FMP05
					animations[loop].ancuroff:+ChunkSize
					animations[loop].ancuroff:/4
	
					animations[loop].anstartoff:+ChunkSize
					animations[loop].anstartoff:/4
										
					animations[loop].anendoff:+ChunkSize
					animations[loop].anendoff:/4					
				EndIf
				
				offset:+SizeOf ANISTR
			Next
		
			For loop=0 To sequenceSize-1
				value=PeekInt(tempBuffer,loop*SizeOf sequenceSize)
				If m_dMapType=FMP05
					value:/m_dBlockSize
				EndIf
				
				PokeInt(animationSeq,loop*SizeOf sequenceSize,value)
			Next 
			
			MapInitAnims()
			Return MAPPYERROR_OK
		EndMethod
		
		Method MapInitAnims()
		Local loop:Int
		
			If m_dNumAnimations=0 Then Return
			
			For loop=0 To m_dNumAnimations-1
				Select animations[loop].antype
					Case	AN_PPFR
						animations[loop].antype=AN_PPFF
					Case	AN_ONCES
						animations[loop].antype=AN_ONCE
				EndSelect
				
				If animations[loop].antype=AN_LOOPR Or ..
				   animations[loop].antype=AN_PPRF Or ..
				   animations[loop].antype=AN_PPRR
					If animations[loop].antype=AN_PPRF
						animations[loop].antype=AN_PPRR
					EndIf
					
					animations[loop].ancuroff=animations[loop].anstartoff
					
					If animations[loop].anstartoff<>animations[loop].anendoff
						animations[loop].ancuroff=animations[loop].anendoff-1
					EndIf
				Else
					animations[loop].ancuroff=animations[loop].anstartoff
				EndIf
				
				animations[loop].ancount=animations[loop].andelay
			Next 
		EndMethod
		
		Method processAnimation:Int(handle:TStream,ChunkSize:Int)
		Local tempBuffer:TBank
		Local loop:Int
		
			tempBuffer=CreateBank(ChunkSize)
			If tempBuffer=Null Then Return MAPPYERROR_OUTOFMEM
			
			For loop=0 To ChunkSize-1
				PokeByte(tempBuffer,loop,ReadByte(handle))
			Next
			
			loop=processAnimationData(tempBuffer,ChunkSize)
			
			Return loop
		EndMethod
		
		Method loadMappyFile:Int(fileName$,extraBytes:Int)
		Local handle:TStream
		Local header1:TBank
		Local header2:TBank
		Local ChunkHeader:TBank
		Local head1$
		Local head2$
		Local Chunk$
		Local FilePosition:Int
		Local DecodeFlag:Byte
		Local ChunkSize:Int
		Local result:Int
		Local loop:Int
		Local layer:Int
		
			handle=OpenStream(fileName,True,False)
			If handle=Null Then Return MAPPYERROR_FILENOTFOUND
		
			header1=CreateBank(HEADER_SIZE)
			header2=CreateBank(HEADER_SIZE)
			ChunkHeader=CreateBank(HEADER_SIZE)
			If header1=Null Or header2=Null Or ChunkHeader=Null
				Return MAPPYERROR_OUTOFMEM
			EndIf
			
			m_dExtraByteSize=extraBytes	
			Mappy_FileSize=getMappySize(handle,header1)
			loadMappyHeader(handle,header2)
		
			head1$=headerToString(header1)
			head2$=headerToString(header2)
				
			If head1$=MAPPY_HEADER1$ And head2$=MAPPY_HEADER2$
				FilePosition=12
				Repeat
					DecodeFlag=False
		
					ChunkSize=getMappySize(handle,ChunkHeader)
					FilePosition:+8	
					
					Chunk$=headerToString(ChunkHeader)
					result=MAPPYERROR_OK
					
					Select Chunk$
						Case	HEADER_AUTHOR$
							m_dAuthorSize=ChunkSize
							m_dAuthor$=""
							For loop=1 To ChunkSize
								m_dAuthor$:+Chr$(ReadByte(handle))
							Next
						Case HEADER_MAP$
							getMapHeader(handle,ChunkSize)
						Case HEADER_PALETTE$
							processPalette(handle,ChunkSize)
						Case HEADER_BLOCKGRFX$
							processBlockGraphics(handle,ChunkSize)
						Case HEADER_BODY$
							result=getTileMapLayer(handle,ChunkSize,0,m_dExtraBytesSize)
						Case HEADER_LAYER$
							
						Case HEADER_ANIMATION$
							result=processAnimation(handle,ChunkSize)
						Case HEADER_BLOCKDATA$
							result=processBlockData(handle,ChunkSize)
						Case HEADER_EDITOR$
							mappySkipSection(handle,ChunkSize)
						Case HEADER_EPHD$
							mappySkipSection(handle,ChunkSize)
						Default
							If Left$(Chunk$,Len(HEADER_LAYER$))=HEADER_LAYER$
								layer=Asc(Right$(Chunk$,1))-Asc("0")
								If layer>=0 And layer<MAX_LAYERS
									result=getTileMapLayer(handle,ChunkSize,layer,m_dExtraBytesSize)
								Else
									result=MAPPYERROR_INVALIDLAYER
								EndIf
							Else
								result=MAPPYERROR_UNKNOWNHEADER
							EndIf
					EndSelect
					
					FilePosition:+ChunkSize
				Until FilePosition>=Mappy_FileSize Or result<>MAPPYERROR_OK
			Else
				result=MAPPYERROR_INVALIDHEADER
			EndIf
				
			CloseStream(handle)
			Return result
		EndMethod
		
		Method returnMappyFileSize:Int()
			Return Mappy_FileSize
		EndMethod
		
		Method returnAuthorName$()
			Return m_dAuthor$
		EndMethod
		
		Method returnMappyVersion:Short()
			Return m_dMapVersion
		EndMethod
		
		Method returnMapWidth:Short()
			Return m_dMapWidth
		EndMethod
		
		Method returnMapHeight:Short()
			Return m_dMapHeight
		EndMethod
		
		Method returnMapDepth:Byte()
			Return m_dMapDepth
		EndMethod
		
		Method returnMapType:Byte()
			Return m_dMapType
		EndMethod
		
		Method returnBlockWidth:Short()
			Return m_dBlockWidth
		EndMethod
		
		Method returnBlockHeight:Short()
			Return m_dBlockHeight
		EndMethod
		
		Method returnBlockSize:Short()	
			Return m_dBlockSize
		EndMethod
		
		Method getPositionInLayer:Int(x:Short,y:Short)
			Return (x*TILE_SIZE)+(y*m_dMapWidth*TILE_SIZE)
		EndMethod
		
		Method tileAtPosition:Int(layers:Short,x:Short,y:Short)
			If (layers>=0 And layers<MAX_LAYERS) And (x>=0 And x<m_dMapWidth) And (y>=0 And y<m_dMapHeight)
				If layer[layers]
					Return Int(PeekShort(layer[layers],getPositionInLayer(x,y))			)
				EndIf
			EndIf
			
			Return 0
		EndMethod
		
		Method writeTileAtPosition:Int(layers:Short,x:Short,y:Short,value:Short)
			If (layers>=0 And layers<MAX_LAYERS) And (x>=0 And x<m_dMapWidth) And (y>=0 And y<m_dMapHeight)
				If layer[layers]
					PokeShort(layer[layers],getPositionInLayer(x,y),value)			
					Return True
				EndIf
			EndIf
			
			Return False
		EndMethod
		
		Method returnBlockStructInfo(which:Int,store:Byte Ptr)
			If which>=0 And which<m_dNumBlockStructs
				MemCopy(store,blockStructs[which],SizeOf BLKSTR) 
			EndIf
		EndMethod
		
		Method returnNumberOfAnimations:Int()	
			Return m_dNumAnimations
		EndMethod
		
		Method returnNumberOfBlockStructs:Int()
			Return m_dNumBlockStructs
		EndMethod
		
		Method returnNumberOfBlockGFX:Int()
			Return m_dNumBlockGFX
		EndMethod
		
		Method returnClipMask:Short()
			Return m_dClipMask
		EndMethod
				
		Method returnBackgroundOffset:Int(block:Int)
		Local b:BLKSTR
		
			b=New BLKSTR
			returnBlockStructInfo(block,b)
			Return b.bgoff/(m_dBlockWidth*m_dBlockHeight)
		EndMethod
		
		Method returnForegroundOffset:Int(block:Int,which:Short)
		Local b:BLKSTR
		Local index:Short
		
			b=New BLKSTR
			returnBlockStructInfo(block,b)
			Select which
				Case FOREGROUND1	
						index=b.fgoff
				Case	FOREGROUND2
						index=b.fgoff2
				Case	FOREGROUND3
						index=b.fgoff3
			EndSelect
			
			Return index/(m_dBlockWidth*m_dBlockHeight)
		EndMethod
		
		Method returnCurrentAnimationBlock:Int(block:Int)
		Local a:ANISTR
		Local temp:Int
		
			a=New ANISTR
			block=block & 32767
			If getAnimation(m_dNumAnimations-block,a)
				temp=PeekInt(animationSeq,a.ancuroff*SizeOf temp)
				Return temp
			Else
				Return 0
			EndIf
		EndMethod
		
		Method getAnimation:Short(block:Short,store:Byte Ptr)
			If block>=0 And block<m_dNumAnimations
				MemCopy(store,animations[block],SizeOf ANISTR)
				Return True
			Else
				Return False
			EndIf
		EndMethod
		
		Method addLayer:Int(layers:Int)
			If layers>=0 And layers<MAX_LAYERS
				If layer[layers] Then Return MAPPYERROR_LAYERINUSE
			
				If dMapWidth>0 And m_dMapHeight>0
					Return allocateLayer(layers,m_dMapWidth*m_dMapheight,m_dExtraBytes)
				Else
					Return MAPPYERROR_MAPNOTLOADED
				EndIf
			Else
				Return MAPPYERROR_INVALIDLAYER
			EndIf
		EndMethod
		
		Method deleteLayer:Int(layers:Int)
			If layers>=0 And layers<MAX_LAYERS
				If layer[layers]=Null
					Return MAPPYERROR_LAYERNOTINUSE
				Else
					ResizeBank(layer[layers],0)
					layer[layers]=Null
				EndIf
			Else
				Return MAPPYERROR_INVALIDLAYER
			EndIf
		EndMethod
			
		Method copyLayer:Int(fromLayer:Int,toLayer:Int)
		Local status:Int
		
			If fromlayer>=0 And fromLayer<MAX_LAYERS And toLayer>=0 And toLayer<MAX_LAYERS
				If layer[fromLayer]=Null
					Return MAPPYERROR_LAYERNOTINUSE
				EndIf
				
				If layer[toLayer]=Null
					status=allocateLayer(toLayer,m_dMapWidth*m_dMapHeight,m_dExtraBytes)
					If status<>MAPPERROR_OK
						Return status
					EndIf
				EndIf
				
				CopyBank(	layer[fromLayer],0,layer[toLayer],0,BankSize(layer[fromlayer]))
				Return MAPPYERROR_OK
			Else
				Return MAPPYERROR_INVALIDLAYER
			EndIf
		EndMethod
		
		Method clearLayer:Int(layers:Int)
		Local l:Int
		
			If layers>=0 And layers<MAX_LAYERS
				If layer[layers]=Null Then Return MAPPYERROR_LAYERNOTINUSE
				
				For l=0 To BankSize(layer[layers])-1
					PokeByte(layer[layers],l,0)
				Next
				
				Return MAPPYERROR_OK
			Else
				Return MAPPYERROR_INVALIDLAYER
			EndIf
		EndMethod
		
		Method moveLayer:Int(fromLayers:Int,toLayer:Int)
			If fromlayer>=0 And fromLayer<MAX_LAYERS And toLayer>=0 And toLayer<MAX_LAYERS
				If layer[fromLayer]=Null
					Return MAPPYERROR_LAYERNOTINUSE
				EndIf
				
				If layer[toLayer]=Null
					status=allocateLayer(toLayer,m_dMapWidth*m_dMapHeight,m_dExtraBytes)
					If status<>MAPPERROR_OK
						Return status
					EndIf
				EndIf
				
				CopyBank(	layer[fromLayer],0,layer[toLayer],0,BankSize(layer[fromlayer]))
				Return clearLayer(fromLayer)
			Else
				Return MAPPYERROR_INVALIDLAYER
			EndIf
		EndMethod
		
		Method returnRGB(colour:Byte,store:Byte Ptr)
			MemCopy(store,palette[colour],SizeOf RGB)
		EndMethod
		
		Method updateAnimations()
		Local loop:Int
			
			If m_dNumAnimations=0 Then Return
			
			For loop=0 To m_dNumAnimations-1			
				If animations[loop].antype<>AN_NONE
					animations[loop].ancount:-1
					If animations[loop].ancount & 128
						animations[loop].ancount=animations[loop].andelay
						Select animations[loop].antype
							Case	AN_LOOPF
								If animations[loop].anstartoff<>animations[loop].anendoff
									animations[loop].ancuroff:+1
									If animations[loop].ancuroff=animations[loop].anendoff
										animations[loop].ancuroff=animations[loop].anstartoff
									EndIf
								EndIf
							Case AN_LOOPR
								If animations[loop].anstartoff<>animations[loop].anendoff
									animations[loop].ancuroff:-1
									If animations[loop].ancuroff=animations[loop].anstartoff-1
										animations[loop].ancuroff=animations[loop].anendoff
									EndIf
								EndIf				
							Case AN_ONCE
								If animations[loop].anstartoff<>animations[loop].anendoff
									animations[loop].ancuroff:+1
									If animations[loop].ancuroff=animations[loop].anendoff
										animations[loop].antype=AN_ONCES
										animations[loop].ancuroff=animations[loop].anendoff
									EndIf
								EndIf
							Case	AN_ONCEH
								If animations[loop].anstartoff<>animations[loop].anendoff
									If animations[loop].ancuroff<>animations[loop].anendoff-1
										animations[loop].ancuroff:+1
									EndIf
								EndIf
							Case AN_PPFF
								If animations[loop].anstartoff<>animations[loop].anendoff
									animations[loop].ancuroff:+1
									If animations[loop].ancuroff=animations[loop].anendoff
										animations[loop].ancuroff:-2
										animations[loop].antype=AN_PPFR
										If animations[loop].ancuroff<animations[loop].anstartoff
											animations[loop].ancuroff:+1
										EndIf
									EndIf
								EndIf
							Case AN_PPFR
								If animations[loop].anstartoff<>animations[loop].anendoff
									animations[loop].ancuroff:-1
									If animations[loop].ancuroff=animations[loop].anstartoff-1
										animations[loop].ancuroff:+2
										animations[loop].antype=AN_PPFF
										If animations[loop].ancuroff>animations[loop].anendoff
											animations[loop].ancuroff:-1
										EndIf
									EndIf
								EndIf
							Case AN_PPRR
								If animations[loop].anstartoff<>animations[loop].anendoff
									animations[loop].ancuroff:-1
									If animations[loop].ancuroff=animations[loop].anstartoff-1
										animations[loop].ancuroff:+2
										animations[loop].antype=AN_PPRF
										If animations[loop].ancuroff>animations[loop].anendoff
											animations[loop].ancuroff:-1
										EndIf
									EndIf
								EndIf
							Case AN_PPRF
								If animations[loop].anstartoff<>animations[loop].anendoff
									animations[loop].ancuroff:-1
									If animations[loop].ancuroff=animations[loop].anendoff
										animations[loop].ancuroff:-2
										animations[loop].antype=AN_PPRR
										If animations[loop].ancuroff<animations[loop].anstartoff
											animations[loop].ancuroff:+1
										EndIf
									EndIf
								EndIf
	
						EndSelect
					EndIf
				EndIf
			Next
		EndMethod	
	EndType

Strict

Import "TMappy.bmx"

Global mappy:TMappy=TMappy.create()
Local result
Local block:BLKSTR=New BLKSTR
Local rgbCol:RGB=New RGB
Global tileGraphics:TImage[81]
Local x:Int,y:Int,px:Int,py:Int
Local one$
Local a$
Local tile:Int
Local mapWidth,mapHeight,image:Short
Local blockWidth,blockHeight

Graphics 1024,768

For x=1 To 80
	one$=String(x)
	a$=Right$("000000"+one$,6)
	Print a$
	'tileGraphics[x]=New TImage
	tileGraphics[x]=LoadImage("MappyGraphics\G"+a$+".PNG")
	If tileGraphics[x]=Null
		Print "File Not Found"
		End
	EndIf
Next

result=mappy.loadMappyFile("test.fmp",0)
Print result
If result<>MAPPYERROR_OK
	WaitKey
	End
EndIf
Print "Map Type:"
Print mappy.returnMapType()
If mappy.returnMapType()=FMP05
	Print "FMP 05"
Else
	If mappy.returnMapType()=FMP10
		Print "FMP 10"
	Else
		If mappy.returnMapType()=FMP10RLE
			Print "FMP 10RLE"
		Else
			Print "Unknown"
		EndIf
	EndIf
EndIf

Print "Mappy File Size:"
Print mappy.returnMappyFileSize()
Print "Mappy Author Name:"
Print mappy.returnAuthorName$()
Print "Mappy Version:"
Print mappy.returnMappyVersion()

Print "Mappy Width:"
Print mappy.returnMapWidth()
Print "Mappy Height:"
Print mappy.returnMapHeight()
Print "Map Depth:"
Print mappy.returnMapDepth()
Print "Block Width:"
Print mappy.returnBlockWidth()
Print "Block Height:"
Print mappy.returnBlockHeight()
Print "Block Size:"
Print mappy.returnBlockSize()
Print "Number Of Block Structures:"
Print mappy.returnNumberOfBlockStructs()
Print "Number Of Block GFX:"
Print mappy.returnNumberOfBlockGFX()
Print "Clip Mask:"
Print mappy.returnClipMask()
Print "Tile At Position 0,10,10:"
Print mappy.tileAtPosition(0,10,10)
Print "Tile At Position 0,0,0:"
Print mappy.tileAtPosition(0,0,0)
Print "Tile At Position 5,0,0:"
Print mappy.tileAtPosition(5,0,0)
Print "Write Tile At Position 0,10,10:"
Print mappy.writeTileAtPosition(0,10,10,7)
Print "Reading Tile:"
Print mappy.tileAtPosition(0,10,10)
Print "Getting block info for 1:"
mappy.returnBlockStructInfo(1,block)
Print "Flags:"
Print block.bgoff
Print block.fgoff
Print block.fgoff2
Print block.fgoff3
Print block.user1
Print block.user2
Print block.user3
Print block.flags
Print "Number of animations:"
Print mappy.returnNumberOfAnimations()

Print "Copying Layer:"
Print mappy.moveLayer(0,1)

Print "Getting RGB value:"
mappy.returnRGB(0,rgbCol)
Print "Red:"
Print rgbCol.r
Print "Green:"
Print rgbCol.g
Print "Blue:"
Print rgbCol.b

mapWidth=mappy.returnMapWidth()
mapHeight=mappy.returnMapHeight()
blockWidth=mappy.returnBlockWidth()
blockHeight=mappy.returnBlockHeight()

px=0
py=0

While Not KeyHit(KEY_ESCAPE)
	Cls
	If KeyHit(KEY_UP) And py>0 Then py=py-1
	If KeyHit(KEY_DOWN) And py<mapHeight Then py=py+1
	If KeyHit(KEY_LEFT) And px>0 Then px=px-1
	If KeyHit(KEY_RIGHT) And px<mapWidth Then px=px+1

	displayMap(px,py,100,100,blockWidth,blockHeight)	
	Flip
	
	mappy.updateAnimations()
Wend
WaitKey
End

Function displayMap(x:Int,y:Int,maxX:Int,maxY:Int,blockWidth:Int,blockHeight:Int)
Local lx:Int
Local ly:Int
Local px:Int
Local py:Int
Local tile:Short
Local image:Int

	For ly=0 To maxY
		For lx=0 To maxX
			px=x+lx
			py=y+ly
			tile=mappy.tileAtPosition(1,px,py)
			If tile<>0 
				If tile & (1 Shl 15)
					'Animate
					tile=mappy.returnCurrentAnimationBlock(tile)
				EndIf
				
				image=mappy.returnBackgroundOffset(tile)
				If image>0 And tileGraphics[image] Then DrawImage tileGraphics[image],lx*blockWidth,ly*blockHeight
				image=mappy.returnForegroundOffset(tile,FOREGROUND1)
				If image>0 And tileGraphics[image] Then DrawImage tileGraphics[image],lx*blockWidth,ly*blockHeight
			EndIf
		Next
	Next						
EndFunction
