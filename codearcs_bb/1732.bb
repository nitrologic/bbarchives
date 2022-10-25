; ID: 1732
; Author: markcw
; Date: 2006-06-12 12:36:20
; Title: FreeImage Module for B3D/B+
; Description: Module and Decls for FreeImage.dll

.lib " "
;FreeImage Module Decls

;Load and save image functions
FiLoadImage%(filename$)
FiSaveImage%(image, filename$)
FiLoad%(filename$)
FiRead%(dib)
FiSave%(dib, filename$)
FiWrite%(image)
FiUnload(dib)

;Load and save anim image functions
FiLoadAnimImage%(filename$, index, frames)
FiSaveAnimImage%(image, filename$, frames, index)
FiAnimFrames%(filename$, index, frames)
FiOpenAnim%(filename$, opentype, flags)
FiCloseAnim(dib, flags)

;Image manipulation functions
FiRescale%(bitmap, scale, filter)
FiRotateClassic%(dib, angle#)
FiRotateClassicEx%(dib, angle#, bgcolor)
FiAdjustGamma%(dib, gamma#)
FiAdjustBrightness%(dib, brightness#)
FiAdjustContrast%(dib, contrast#)
FiAdjustIntensity%(dib, intensity#)

;Information functions
FiGetWidth%(dib)
FiGetHeight%(dib)
FiGetBPP%(dib)

;Clipboard functions
FiCopyToClipboard%(dib)
FiPasteFromClipboard%()

;Bank functions
FiBankFromFile%(filename$)
FiBankToFile%(bank, filename$, size)
FiLoadFromBank%(bank)

;Zlib functions
FiZlibLoadImage%(filename$, datafile$)
FiZlibPack%(filename$, datafile$)
FiZlibUnpack%(filename$, datafile$)
FiGZipLoadImage%(filename$, datafile$)
FiGZip%(filename$, datafile$)
FiGUnzip%(filename$, datafile$)

;Memory stream functions
FiSaveToMemory%(filename$)
FiLoadFromMemory%(stream)
FiMemoryToFile%(stream, filename$)
FiMemorySize%(stream)
FiCloseMemory(stream)

;FiRotateClassicEx functions
FiRotateAny%(hsrc, angle#, bgcolor)
FiRotate45%(hsrc, angle#, bgcolor)
FiHorizontalSkew%(hsrc, hdst, row, offset, weight, bgcolor)
FiVerticalSkew%(hsrc, hdst, col, offset, weight, bgcolor)
FiRotate90%(hsrc)
FiRotate180%(hsrc)
FiRotate270%(hsrc)

;FloatToDouble functions
FiFloatToDouble%(value#, dpart)
FiDoubleToFloat#(dho, dhi)
FiFloatToInt%(value#)
FiIntToFloat#(value)

.lib "Kernel32.dll"
;Memory Management Functions

FiApiGlobalAlloc%(uFlags,dwBytes):"GlobalAlloc"
FiApiGlobalLock%(hmem):"GlobalLock"
FiApiGlobalUnlock%(hmem):"GlobalUnlock"
FiApiBankToMemory(dst, src*, len):"RtlMoveMemory"
FiApiMemoryToBank(dst*, src, len):"RtlMoveMemory"

.lib "User32.dll"
;Clipboard Functions

FiApiCloseClipboard%():"CloseClipboard"
FiApiEmptyClipboard%():"EmptyClipboard"
FiApiGetClipboardData%(uFormat):"GetClipboardData"
FiApiIsClipboardFormatAvailable%(format):"IsClipboardFormatAvailable"
FiApiOpenClipboard%(hwnd):"OpenClipboard"
FiApiSetClipboardData%(uFormat,hData):"SetClipboardData"

.lib "FreeImage.dll"
;FreeImage 3.10.0 Decls
;http://freeimage.sourceforge.net/

;Init / Error Routines
FreeImage_Initialise(load_local_plugins_only):"_FreeImage_Initialise@4"
FreeImage_DeInitialise():"_FreeImage_DeInitialise@0"

;Version Routines
FreeImage_GetVersion$():"_FreeImage_GetVersion@0"
FreeImage_GetCopyrightMessage$():"_FreeImage_GetCopyrightMessage@0"

;Message Output Functions
FreeImage_SetOutputMessageStdCall(omf):"_FreeImage_SetOutputMessageStdCall@4"
FreeImage_SetOutputMessage(omf):"_FreeImage_SetOutputMessage@4"
FreeImage_OutputMessageProc(fif,fmt$):"FreeImage_OutputMessageProc"

;Allocate / Clone / Unload Routines
FreeImage_Allocate%(width,height,bpp,red_mask,green_mask,blue_mask):"_FreeImage_Allocate@24"
FreeImage_AllocateT%(type,width,height,bpp,red_mask,green_mask,blue_mask):"_FreeImage_AllocateT@28"
FreeImage_Clone%(dib):"_FreeImage_Clone@4"
FreeImage_Unload(dib):"_FreeImage_Unload@4"

;Load / Save Routines
FreeImage_Load%(fif,filename$,flags):"_FreeImage_Load@12"
FreeImage_LoadU%(fif,filename$,flags):"_FreeImage_LoadU@12"
FreeImage_LoadFromHandle%(fif,io*,handle,flags):"_FreeImage_LoadFromHandle@16"
FreeImage_Save%(fif,dib,filename$,flags):"_FreeImage_Save@16"
FreeImage_SaveU%(fif,dib,filename$,flags):"_FreeImage_SaveU@16"
FreeImage_SaveToHandle%(fif,dib,io*,handle,flags):"_FreeImage_SaveToHandle@20"

;Memory I/O Stream Routines
FreeImage_OpenMemory%(data*,size_in_bytes):"_FreeImage_OpenMemory@8"
FreeImage_CloseMemory(stream):"_FreeImage_CloseMemory@4"
FreeImage_LoadFromMemory%(fif,stream,flags):"_FreeImage_LoadFromMemory@12"
FreeImage_SaveToMemory%(fif,dib,stream,flags):"_FreeImage_SaveToMemory@16"
FreeImage_TellMemory%(stream):"_FreeImage_TellMemory@4"
FreeImage_SeekMemory%(stream,offset,origin):"_FreeImage_SeekMemory@12"
FreeImage_AcquireMemory%(stream,data*,size_in_bytes*):"_FreeImage_AcquireMemory@12"
FreeImage_ReadMemory%(buffer,size,count,stream):"_FreeImage_ReadMemory@16"
FreeImage_WriteMemory%(buffer,size,count,stream):"_FreeImage_WriteMemory@16"
FreeImage_LoadMultiBitmapFromMemory%(fif,stream,flags):"_FreeImage_LoadMultiBitmapFromMemory@12"

;Plugin Interface
FreeImage_RegisterLocalPlugin%(proc_address,format$,description$,extension$,regexpr$):"_FreeImage_RegisterLocalPlugin@20"
FreeImage_RegisterExternalPlugin%(path$,format$,description$,extension$,regexpr$):"_FreeImage_RegisterExternalPlugin@20"
FreeImage_GetFIFCount%():"_FreeImage_GetFIFCount@0"
FreeImage_SetPluginEnabled%(fif,enable):"_FreeImage_SetPluginEnabled@8"
FreeImage_IsPluginEnabled%(fif):"_FreeImage_IsPluginEnabled@4"
FreeImage_GetFIFFromFormat%(format$):"_FreeImage_GetFIFFromFormat@4"
FreeImage_GetFIFFromMime%(mime$):"_FreeImage_GetFIFFromMime@4"
FreeImage_GetFormatFromFIF$(fif):"_FreeImage_GetFormatFromFIF@4"
FreeImage_GetFIFExtensionList$(fif):"_FreeImage_GetFIFExtensionList@4"
FreeImage_GetFIFDescription$(fif):"_FreeImage_GetFIFDescription@4"
FreeImage_GetFIFRegExpr$(fif):"_FreeImage_GetFIFRegExpr@4"
FreeImage_GetFIFMimeType$(fif):"_FreeImage_GetFIFMimeType@4"
FreeImage_GetFIFFromFilename%(filename$):"_FreeImage_GetFIFFromFilename@4"
FreeImage_GetFIFFromFilenameU%(filename$):"_FreeImage_GetFIFFromFilenameU@4"
FreeImage_FIFSupportsReading%(fif):"_FreeImage_FIFSupportsReading@4"
FreeImage_FIFSupportsWriting%(fif):"_FreeImage_FIFSupportsWriting@4"
FreeImage_FIFSupportsExportBPP%(fif,bpp):"_FreeImage_FIFSupportsExportBPP@8"
FreeImage_FIFSupportsExportType%(fif,type):"_FreeImage_FIFSupportsExportType@8"
FreeImage_FIFSupportsICCProfiles%(fif):"_FreeImage_FIFSupportsICCProfiles@4"

;Multipaging Interface
FreeImage_OpenMultiBitmap%(fif,filename$,create_new,read_only,keep_cache_in_memory,flags):"_FreeImage_OpenMultiBitmap@24"
FreeImage_CloseMultiBitmap%(bitmap,flags):"_FreeImage_CloseMultiBitmap@8"
FreeImage_GetPageCount%(bitmap):"_FreeImage_GetPageCount@4"
FreeImage_AppendPage(bitmap,data):"_FreeImage_AppendPage@8"
FreeImage_InsertPage(bitmap,page,data):"_FreeImage_InsertPage@12"
FreeImage_DeletePage(bitmap,page):"_FreeImage_DeletePage@8"
FreeImage_LockPage%(bitmap,page):"_FreeImage_LockPage@8"
FreeImage_UnlockPage(bitmap,page,changed):"_FreeImage_UnlockPage@12"
FreeImage_MovePage%(bitmap,target,source):"_FreeImage_MovePage@12"
FreeImage_GetLockedPageNumbers%(bitmap,pages,count*):"_FreeImage_GetLockedPageNumbers@12"

;Filetype Request Routines
FreeImage_GetFileType%(filename$,size):"_FreeImage_GetFileType@8"
FreeImage_GetFileTypeU%(filename$,size):"_FreeImage_GetFileTypeU@8"
FreeImage_GetFileTypeFromHandle%(io*,handle,size):"_FreeImage_GetFileTypeFromHandle@12"
FreeImage_GetFileTypeFromMemory%(stream,size):"_FreeImage_GetFileTypeFromMemory@8"

;Image Type Request Routine
FreeImage_GetImageType%(dib):"_FreeImage_GetImageType@4"

;FreeImage Helper Routines
FreeImage_IsLittleEndian%():"_FreeImage_IsLittleEndian@0"
FreeImage_LookupX11Color%(szColor$,nRed*,nGreen*,nBlue*):"_FreeImage_LookupX11Color@16"
FreeImage_LookupSVGColor%(szColor$,nRed*,nGreen*,nBlue*):"_FreeImage_LookupSVGColor@16"

;Pixel Access Routines
FreeImage_GetBits%(dib):"_FreeImage_GetBits@4"
FreeImage_GetScanLine%(dib,scanline):"_FreeImage_GetScanLine@8"
FreeImage_GetPixelIndex%(dib,x,y,value*):"_FreeImage_GetPixelIndex@16"
FreeImage_GetPixelColor%(dib,x,y,value*):"_FreeImage_GetPixelColor@16"
FreeImage_SetPixelIndex%(dib,x,y,value*):"_FreeImage_SetPixelIndex@16"
FreeImage_SetPixelColor%(dib,x,y,value*):"_FreeImage_SetPixelColor@16"

;DIB Info Routines
FreeImage_GetColorsUsed%(dib):"_FreeImage_GetColorsUsed@4"
FreeImage_GetBPP%(dib):"_FreeImage_GetBPP@4"
FreeImage_GetWidth%(dib):"_FreeImage_GetWidth@4"
FreeImage_GetHeight%(dib):"_FreeImage_GetHeight@4"
FreeImage_GetLine%(dib):"_FreeImage_GetLine@4"
FreeImage_GetPitch%(dib):"_FreeImage_GetPitch@4"
FreeImage_GetDIBSize%(dib):"_FreeImage_GetDIBSize@4"
FreeImage_GetPalette%(dib):"_FreeImage_GetPalette@4"
FreeImage_GetDotsPerMeterX%(dib):"_FreeImage_GetDotsPerMeterX@4"
FreeImage_GetDotsPerMeterY%(dib):"_FreeImage_GetDotsPerMeterY@4"
FreeImage_SetDotsPerMeterX(dib,res):"_FreeImage_SetDotsPerMeterX@8"
FreeImage_SetDotsPerMeterY(dib,res):"_FreeImage_SetDotsPerMeterY@8"
FreeImage_GetInfoHeader%(dib):"_FreeImage_GetInfoHeader@4"
FreeImage_GetInfo%(dib):"_FreeImage_GetInfo@4"
FreeImage_GetColorType%(dib):"_FreeImage_GetColorType@4"
FreeImage_GetRedMask%(dib):"_FreeImage_GetRedMask@4"
FreeImage_GetGreenMask%(dib):"_FreeImage_GetGreenMask@4"
FreeImage_GetBlueMask%(dib):"_FreeImage_GetBlueMask@4"
FreeImage_GetTransparencyCount%(dib):"_FreeImage_GetTransparencyCount@4"
FreeImage_GetTransparencyTable%(dib):"_FreeImage_GetTransparencyTable@4"
FreeImage_SetTransparent(dib,enabled):"_FreeImage_SetTransparent@8"
FreeImage_SetTransparencyTable(dib,table*,count):"_FreeImage_SetTransparencyTable@12"
FreeImage_IsTransparent%(dib):"_FreeImage_IsTransparent@4"
FreeImage_SetTransparentIndex(dib,index):"_FreeImage_SetTransparentIndex@8"
FreeImage_GetTransparentIndex%(dib):"_FreeImage_GetTransparentIndex@4"
FreeImage_HasBackgroundColor%(dib):"_FreeImage_HasBackgroundColor@4"
FreeImage_GetBackgroundColor%(dib,bkcolor*):"_FreeImage_GetBackgroundColor@8"
FreeImage_SetBackgroundColor%(dib,bkcolor*):"_FreeImage_SetBackgroundColor@8"

;ICC Profile Routines
FreeImage_GetICCProfile%(dib):"_FreeImage_GetICCProfile@4"
FreeImage_CreateICCProfile%(dib,data,size):"_FreeImage_CreateICCProfile@12"
FreeImage_DestroyICCProfile%(dib):"_FreeImage_DestroyICCProfile@4"

;Line Conversion Routines
FreeImage_ConvertLine1To4(target*,source*,width_in_pixels):"_FreeImage_ConvertLine1To4@12"
FreeImage_ConvertLine8To4(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine8To4@16"
FreeImage_ConvertLine16To4_555(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To4_555@12"
FreeImage_ConvertLine16To4_565(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To4_565@12"
FreeImage_ConvertLine24To4(target*,source*,width_in_pixels):"_FreeImage_ConvertLine24To4@12"
FreeImage_ConvertLine32To4(target*,source*,width_in_pixels):"_FreeImage_ConvertLine32To4@12"
FreeImage_ConvertLine1To8(target*,source*,width_in_pixels):"_FreeImage_ConvertLine1To8@12"
FreeImage_ConvertLine4To8(target*,source*,width_in_pixels):"_FreeImage_ConvertLine4To8@12"
FreeImage_ConvertLine16To8_555(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To8_555@12"
FreeImage_ConvertLine16To8_565(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To8_565@12"
FreeImage_ConvertLine24To8(target*,source*,width_in_pixels):"_FreeImage_ConvertLine24To8@12"
FreeImage_ConvertLine32To8(target*,source*,width_in_pixels):"_FreeImage_ConvertLine32To8@12"
FreeImage_ConvertLine1To16_555(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine1To16_555@16"
FreeImage_ConvertLine4To16_555(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine4To16_555@16"
FreeImage_ConvertLine8To16_555(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine8To16_555@16"
FreeImage_ConvertLine16_565_To16_555(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16_565_To16_555@12"
FreeImage_ConvertLine24To16_555(target*,source*,width_in_pixels):"_FreeImage_ConvertLine24To16_555@12"
FreeImage_ConvertLine32To16_555(target*,source*,width_in_pixels):"_FreeImage_ConvertLine32To16_555@12"
FreeImage_ConvertLine1To16_565(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine1To16_565@16"
FreeImage_ConvertLine4To16_565(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine4To16_565@16"
FreeImage_ConvertLine8To16_565(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine8To16_565@16"
FreeImage_ConvertLine16_555_To16_565(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16_555_To16_565@12"
FreeImage_ConvertLine24To16_565(target*,source*,width_in_pixels):"_FreeImage_ConvertLine24To16_565@12"
FreeImage_ConvertLine32To16_565(target*,source*,width_in_pixels):"_FreeImage_ConvertLine32To16_565@12"
FreeImage_ConvertLine1To24(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine1To24@16"
FreeImage_ConvertLine4To24(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine4To24@16"
FreeImage_ConvertLine8To24(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine8To24@16"
FreeImage_ConvertLine16To24_555(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To24_555@12"
FreeImage_ConvertLine16To24_565(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To24_565@12"
FreeImage_ConvertLine32To24(target*,source*,width_in_pixels):"_FreeImage_ConvertLine32To24@12"
FreeImage_ConvertLine1To32(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine1To32@16"
FreeImage_ConvertLine4To32(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine4To32@16"
FreeImage_ConvertLine8To32(target*,source*,width_in_pixels,palette*):"_FreeImage_ConvertLine8To32@16"
FreeImage_ConvertLine16To32_555(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To32_555@12"
FreeImage_ConvertLine16To32_565(target*,source*,width_in_pixels):"_FreeImage_ConvertLine16To32_565@12"
FreeImage_ConvertLine24To32(target*,source*,width_in_pixels):"_FreeImage_ConvertLine24To32@12"

;Smart Conversion Routines
FreeImage_ConvertTo4Bits%(dib):"_FreeImage_ConvertTo4Bits@4"
FreeImage_ConvertTo8Bits%(dib):"_FreeImage_ConvertTo8Bits@4"
FreeImage_ConvertToGreyscale%(dib):"_FreeImage_ConvertToGreyscale@4"
FreeImage_ConvertTo16Bits555%(dib):"_FreeImage_ConvertTo16Bits555@4"
FreeImage_ConvertTo16Bits565%(dib):"_FreeImage_ConvertTo16Bits565@4"
FreeImage_ConvertTo24Bits%(dib):"_FreeImage_ConvertTo24Bits@4"
FreeImage_ConvertTo32Bits%(dib):"_FreeImage_ConvertTo32Bits@4"
FreeImage_ColorQuantize%(dib,quantize):"_FreeImage_ColorQuantize@8"
FreeImage_ColorQuantizeEx%(dib,quantize,PaletteSize,ReserveSize,ReservePalette*):"_FreeImage_ColorQuantizeEx@20"
FreeImage_Threshold%(dib,T):"_FreeImage_Threshold@8"
FreeImage_Dither%(dib,algorithm):"_FreeImage_Dither@8"
FreeImage_ConvertFromRawBits%(bits*,width,height,pitch,bpp,red_mask,green_mask,blue_mask,topdown):"_FreeImage_ConvertFromRawBits@36"
FreeImage_ConvertToRawBits(bits*,dib,pitch,bpp,red_mask,green_mask,blue_mask,topdown):"_FreeImage_ConvertToRawBits@32"
FreeImage_ConvertToRGBF%(dib):"_FreeImage_ConvertToRGBF@4"
FreeImage_ConvertToStandardType%(src,scale_linear):"_FreeImage_ConvertToStandardType@8"
FreeImage_ConvertToType%(src,dst_type,scale_linear):"_FreeImage_ConvertToType@12"
 ;tone mapping operators
FreeImage_ToneMapping%(dib,tmo,first_param_dlo,first_param_dhi,second_param_dlo,second_param_dlo):"_FreeImage_ToneMapping@24"
FreeImage_TmoDrago03%(src,gamma_dlo,gamma_dhi,exposure_dlo,exposure_dhi):"_FreeImage_TmoDrago03@20"
FreeImage_TmoReinhard05%(src,intensity_dlo,intensity_dhi,contrast_dlo,contrast_dhi):"_FreeImage_TmoReinhard05@20"
FreeImage_TmoFattal02%(src,color_saturation_dlo,color_saturation_dhi,attenuation_dlo,attenuation_dhi):"_FreeImage_TmoFattal02@20"

;ZLib Interface
FreeImage_ZLibCompress%(target*,target_size,source*,source_size):"_FreeImage_ZLibCompress@16"
FreeImage_ZLibUncompress%(target*,target_size,source*,source_size):"_FreeImage_ZLibUncompress@16"
FreeImage_ZLibGZip%(target*,target_size,source*,source_size):"_FreeImage_ZLibGZip@16"
FreeImage_ZLibGUnzip%(target*,target_size,source*,source_size):"_FreeImage_ZLibGUnzip@16"
FreeImage_ZLibCRC32%(crc,source*,source_size):"_FreeImage_ZLibCRC32@12"

;Metadata Routines
 ;tag creation / destruction
FreeImage_CreateTag%():"_FreeImage_CreateTag@0"
FreeImage_DeleteTag(tag):"_FreeImage_DeleteTag@4"
FreeImage_CloneTag%(tag):"_FreeImage_CloneTag@4"
 ;tag getters and setters
FreeImage_GetTagKey$(tag):"_FreeImage_GetTagKey@4"
FreeImage_GetTagDescription$(tag):"_FreeImage_GetTagDescription@4"
FreeImage_GetTagID%(tag):"_FreeImage_GetTagID@4"
FreeImage_GetTagType%(tag):"_FreeImage_GetTagType@4"
FreeImage_GetTagCount%(tag):"_FreeImage_GetTagCount@4"
FreeImage_GetTagLength%(tag):"_FreeImage_GetTagLength@4"
FreeImage_GetTagValue%(tag):"_FreeImage_GetTagValue@4"
FreeImage_SetTagKey%(tag,key$):"_FreeImage_SetTagKey@8"
FreeImage_SetTagDescription%(tag,description$):"_FreeImage_SetTagDescription@8"
FreeImage_SetTagID%(tag,id):"_FreeImage_SetTagID@8"
FreeImage_SetTagType%(tag,type):"_FreeImage_SetTagType@8"
FreeImage_SetTagCount%(tag,count):"_FreeImage_SetTagCount@8"
FreeImage_SetTagLength%(tag,length):"_FreeImage_SetTagLength@8"
FreeImage_SetTagValue%(tag,value):"_FreeImage_SetTagValue@8"
 ;iterator
FreeImage_FindFirstMetadata%(model,dib,tag):"_FreeImage_FindFirstMetadata@12"
FreeImage_FindNextMetadata%(mdhandle,tag):"_FreeImage_FindNextMetadata@8"
FreeImage_FindCloseMetadata(mdhandle):"_FreeImage_FindCloseMetadata@4"
 ;metadata setter and getter
FreeImage_SetMetadata%(model,dib,key$,tag):"_FreeImage_SetMetadata@16"
FreeImage_GetMetadata%(model,dib,key$,tag):"_FreeImage_GetMetadata@16"
 ;helpers
FreeImage_GetMetadataCount%(model,dib):"_FreeImage_GetMetadataCount@8"
 ;tag to C string conversion
FreeImage_TagToString$(model,tag,Make$):"_FreeImage_TagToString@12"

;Image Manipulation Toolkit
 ;rotation and flipping
FreeImage_RotateClassic%(dib,angle_dlo,angle_dhi):"_FreeImage_RotateClassic@12"
FreeImage_RotateEx%(dib,angle_dlo,angle_dhi,x_shift_dlo,x_shift_dhi,y_shift_dlo,y_shift_dhi,x_origin_dlo,x_origin_dhi,y_origin_dlo,y_origin_dhi,use_mask):"_FreeImage_RotateEx@48"
FreeImage_FlipHorizontal%(dib):"_FreeImage_FlipHorizontal@4"
FreeImage_FlipVertical%(dib):"_FreeImage_FlipVertical@4"
FreeImage_JPEGTransform%(src_file$,dst_file$,operation,perfect):"_FreeImage_JPEGTransform@16"
 ;upsampling / downsampling
FreeImage_Rescale%(dib,dst_width,dst_height,filter):"_FreeImage_Rescale@16"
FreeImage_MakeThumbnail%(dib,max_pixel_size,convert):"_FreeImage_MakeThumbnail@12"
 ;color manipulation routines (point operations)
FreeImage_AdjustCurve%(dib,LUT*,channel):"_FreeImage_AdjustCurve@12"
FreeImage_AdjustGamma%(dib,gamma_dlo,gamma_dhi):"_FreeImage_AdjustGamma@12"
FreeImage_AdjustBrightness%(dib,percentage_dlo,percentage_dhi):"_FreeImage_AdjustBrightness@12"
FreeImage_AdjustContrast%(dib,percentage_dlo,percentage_dhi):"_FreeImage_AdjustContrast@12"
FreeImage_Invert%(dib):"_FreeImage_Invert@4"
FreeImage_GetHistogram%(dib,histo*,channel):"_FreeImage_GetHistogram@12"
FreeImage_GetAdjustColorsLookupTable%(LUT*,brightness_dlo,brightness_dhi,contrast_dlo,contrast_dhi,gamma_dlo,gamma_dhi,invert):"_FreeImage_GetAdjustColorsLookupTable@32"
FreeImage_AdjustColors%(dib,brightness_dlo,brightness_dhi,contrast_dlo,contrast_dhi,gamma_dlo,gamma_dhi,invert):"_FreeImage_AdjustColors@32"
FreeImage_ApplyColorMapping%(dib,srccolors*,dstcolors*,count,ignore_alpha,swap):"_FreeImage_ApplyColorMapping@24"
FreeImage_SwapColors%(dib,color_a*,color_b*,ignore_alpha):"_FreeImage_SwapColors@16"
FreeImage_ApplyPaletteIndexMapping%(dib,srcindices*,dstindices*,count,swap):"_FreeImage_ApplyPaletteIndexMapping@20"
FreeImage_SwapPaletteIndices%(dib,index_a*,index_b*):"_FreeImage_SwapPaletteIndices@12"
 ;channel processing routines
FreeImage_GetChannel%(dib,channel):"_FreeImage_GetChannel@8"
FreeImage_SetChannel%(dib,dib8,channel):"_FreeImage_SetChannel@12"
FreeImage_GetComplexChannel%(src,channel):"_FreeImage_GetComplexChannel@8"
FreeImage_SetComplexChannel%(dst,src,channel):"_FreeImage_SetComplexChannel@12"
 ;copy / paste / composite routines
FreeImage_Copy%(dib,left,top,right,bottom):"_FreeImage_Copy@20"
FreeImage_Paste%(dst,src,left,top,alpha):"_FreeImage_Paste@20"
FreeImage_Composite%(fg,useFileBkg,appBkColor*,bg):"_FreeImage_Composite@16"
FreeImage_JPEGCrop%(src_file$,dst_file$,left,top,right,bottom):"_FreeImage_JPEGCrop@24"
FreeImage_PreMultiplyWithAlpha%(dib):"_FreeImage_PreMultiplyWithAlpha@4"
 ;miscellaneous algorithms
FreeImage_MultigridPoissonSolver%(Laplacian,ncycle):"_FreeImage_MultigridPoissonSolver@8"
