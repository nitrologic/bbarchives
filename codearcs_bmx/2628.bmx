; ID: 2628
; Author: BlitzSupport
; Date: 2009-12-13 10:53:45
; Title: Read Win32 executable data section names
; Description: Lists the data sections in a Win32 executable

' -----------------------------------------------------------------------------
' Adapted from PureBasic code by thefool:
' -----------------------------------------------------------------------------
' www.purebasic.fr/english/viewtopic.php?f=5&t=23080
' -----------------------------------------------------------------------------

SuperStrict

' -----------------------------------------------------------------------------
' *** CHANGE TO A WIN32 EXECUTABLE ON YOUR SYSTEM!
' -----------------------------------------------------------------------------

Local executable:String = "C:\BlitzMax\MaxIDE.exe"

' -----------------------------------------------------------------------------
' Constants and structures defined at bottom of code!
' -----------------------------------------------------------------------------

Local exe:TStream = ReadFile (executable)

If exe = Null Then RuntimeError "Fail! Change executable path..."

Local exesize:Int = StreamSize (exe)
Local bank:TBank = CreateBank (exesize)

Local bankstream:TStream = CreateBankStream (bank)
CopyStream exe, bankstream
CloseStream bankstream
CloseFile exe
	
Local bankptr:Byte Ptr = LockBank (bank)

Local dosheader:IMAGE_DOS_HEADER = New IMAGE_DOS_HEADER
MemCopy dosheader, bankptr, SizeOf (IMAGE_DOS_HEADER)

Print ""
Print "Reading structures from " + executable

Print ""
Print "Miscellaneous information:"
Print ""

Print "Magic number for Windows executables: " + ShowBytes (dosheader, 2) +  " (should be MZ)"

Local ntheader:IMAGE_NT_HEADERS = New IMAGE_NT_HEADERS
MemCopy ntheader, bankptr + Int (dosheader.e_lfanew), SizeOf (IMAGE_NT_HEADERS)

If Right (Hex (ntheader.Signature), 4) = "4550"
	Print "Got NT signature"
Else
	Print "File has no NT signature"
EndIf

Select ntheader.Machine
	Case IMAGE_FILE_MACHINE_I386
		Print "Built for x86"
	Case IMAGE_FILE_MACHINE_IA64
		Print "Built for Intel IPF"
	Case IMAGE_FILE_MACHINE_AMD64
		Print "Built for x64"
End Select

Print ""
Print "Sections:"
Print ""

For Local loop:Int = 0 Until ntheader.NumberOfSections
	Print ShowBytes (bankptr + Int (dosheader.e_lfanew) + SizeOf (IMAGE_NT_HEADERS) + SizeOf (IMAGE_SECTION_HEADER) * loop, 8)
Next

UnlockBank bank

End

' -----------------------------------------------------------------------------
' Helper...
' -----------------------------------------------------------------------------

Function ShowBytes:String (mem:Byte Ptr, size:Long)
	
	Local b:Long

	Local bytes:String
	Local output:String
	
	For b = 0 Until size
		bytes = bytes + Chr (mem [b])
	Next

	Return "[" + bytes + "]"
		
End Function

' -----------------------------------------------------------------------------
' Constants...
' -----------------------------------------------------------------------------

Const IMAGE_SIZEOF_SHORT_NAME:Int = 8
Const IMAGE_NUMBEROF_DIRECTORY_ENTRIES:Int = 16

Const IMAGE_FILE_MACHINE_I386:Int = $014c	' x86
Const IMAGE_FILE_MACHINE_IA64:Int = $0200	' Intel IPF
Const IMAGE_FILE_MACHINE_AMD64:Int = $8664	' x64

' -----------------------------------------------------------------------------
' Structures -- lots of hacking and padding to make Blitz-friendly!
' -----------------------------------------------------------------------------

' Not all are used, as references to other structures within are defined directly to make Blitz happy...

Type IMAGE_DOS_HEADER

	Field e_magic:Short			' Magic number ($5A4D / "MZ")
	Field e_cblp:Short			' Bytes on last page of file
	Field e_cp:Short				' Pages in file
	Field e_crlc:Short			' Relocations
	Field e_cparhdr:Short			' Size of header in paragraphs
	Field e_minalloc:Short			' Minimum extra paragraphs needed
	Field e_maxalloc:Short			' Maximum extra paragraphs needed
	Field e_ss:Short				' Initial (relative) SS value
	Field e_sp:Short				' Initial SP value
	Field e_csum:Short			' Checksum
	Field e_ip:Short				' Initial IP value
	Field e_cs:Short				' Initial (relative) CS value
	Field e_lfarlc:Short			' File address of relocation table
	Field e_ovno:Short			' Overlay number
	Field e_res:Short				' Reserved words
	
	' Hack!
	
	Field e_res_pad1:Short
	Field e_res_pad2:Int
	
	Field e_oemid:Short			' OEM identifier (For e_oeminfo)
	Field e_oeminfo:Short			' OEM information; e_oemid specific
	Field e_res2:Short			' Reserved words
	
	' Hack!
	
	Field e_res2_pad1:Short		' Reserved words
	Field e_res2_pad2:Int			' Reserved words
	Field e_res2_pad3:Int			' Reserved words
	Field e_res2_pad4:Int			' Reserved words
	Field e_res2_pad5:Int			' Reserved words
	
	Field e_lfanew:Int			' File address of new exe header
	
End Type

Type IMAGE_SECTION_HEADER

	Field Name:Byte ' [IMAGE_SIZEOF_SHORT_NAME]
	
	' Hack!

	Field Name1:Byte
	Field Name2:Byte
	Field Name3:Byte
	Field Name4:Byte
	Field Name5:Byte
	Field Name6:Byte
	Field Name7:Byte
	
	Field PhysicalAddress:Int ' Union with VirtualSize:Int
	Field VirtualAddress:Int
	Field SizeOfRawData:Int
	Field PointerToRawData:Int
	Field PointerToRelocations:Int
	Field PointerToLinenumbers:Int
	Field NumberOfRelocations:Short
	Field NumberOfLinenumbers:Short
	Field Characteristics:Int
	
End Type

Type IMAGE_NT_HEADERS

	Field Signature:Int

'	Field FileHeader:IMAGE_FILE_HEADER

	Field Machine:Short
	Field NumberOfSections:Short
	Field TimeDateStamp:Int
	Field PointerToSymbolTable:Int
	Field NumberOfSymbols:Int
	Field SizeOfOptionalHeader:Short
	Field Characteristics:Short

	' Hack!

'	Field OptionalHeader:IMAGE_OPTIONAL_HEADER

	Field Magic:Short
	Field MajorLinkerVersion:Byte
	Field MinorLinkerVersion:Byte
	Field SizeOfCode:Int
	Field SizeOfInitializedData:Int
	Field SizeOfUninitializedData:Int
	Field AddressOfEntryPoint:Int
	Field BaseOfCode:Int
	Field BaseOfData:Int
	Field ImageBase:Int
	Field SectionAlignment:Int
	Field FileAlignment:Int
	Field MajorOperatingSystemVersion:Short
	Field MinorOperatingSystemVersion:Short
	Field MajorImageVersion:Short
	Field MinorImageVersion:Short
	Field MajorSubsystemVersion:Short
	Field MinorSubsystemVersion:Short
	Field Win32VersionValue:Int
	Field SizeOfImage:Int
	Field SizeOfHeaders:Int
	Field CheckSum:Int
	Field Subsystem:Short
	Field DllCharacteristics:Short
	Field SizeOfStackReserve:Int
	Field SizeOfStackCommit:Int
	Field SizeOfHeapReserve:Int
	Field SizeOfHeapCommit:Int
	Field LoaderFlags:Int
	Field NumberOfRvaAndSizes:Int
	
	' Hack!
	
'	Field DataDirectory:IMAGE_DATA_DIRECTORY ' [IMAGE_NUMBEROF_DIRECTORY_ENTRIES]

	Field VirtualAddress1:Int
	Field Size1:Int
	
	Field VirtualAddress2:Int
	Field Size2:Int
	
	Field VirtualAddress3:Int
	Field Size3:Int
	
	Field VirtualAddress4:Int
	Field Size4:Int
	
	Field VirtualAddress5:Int
	Field Size5:Int
	
	Field VirtualAddress6:Int
	Field Size6:Int
	
	Field VirtualAddress7:Int
	Field Size7:Int
	
	Field VirtualAddress8:Int
	Field Size8:Int
	
	Field VirtualAddress9:Int
	Field Size9:Int
	
	Field VirtualAddress10:Int
	Field Size10:Int
	
	Field VirtualAddress11:Int
	Field Size11:Int
	
	Field VirtualAddress12:Int
	Field Size12:Int
	
	Field VirtualAddress13:Int
	Field Size13:Int
	
	Field VirtualAddress14:Int
	Field Size14:Int
	
	Field VirtualAddress15:Int
	Field Size15:Int
	
	Field VirtualAddress16:Int
	Field Size16:Int

End Type

Type IMAGE_DATA_DIRECTORY
	Field VirtualAddress:Int
	Field Size:Int
End Type

Type IMAGE_OPTIONAL_HEADER

	Field Magic:Short
	Field MajorLinkerVersion:Byte
	Field MinorLinkerVersion:Byte
	Field SizeOfCode:Int
	Field SizeOfInitializedData:Int
	Field SizeOfUninitializedData:Int
	Field AddressOfEntryPoint:Int
	Field BaseOfCode:Int
	Field BaseOfData:Int
	Field ImageBase:Int
	Field SectionAlignment:Int
	Field FileAlignment:Int
	Field MajorOperatingSystemVersion:Short
	Field MinorOperatingSystemVersion:Short
	Field MajorImageVersion:Short
	Field MinorImageVersion:Short
	Field MajorSubsystemVersion:Short
	Field MinorSubsystemVersion:Short
	Field Win32VersionValue:Int
	Field SizeOfImage:Int
	Field SizeOfHeaders:Int
	Field CheckSum:Int
	Field Subsystem:Short
	Field DllCharacteristics:Short
	Field SizeOfStackReserve:Int
	Field SizeOfStackCommit:Int
	Field SizeOfHeapReserve:Int
	Field SizeOfHeapCommit:Int
	Field LoaderFlags:Int
	Field NumberOfRvaAndSizes:Int
	
	' Hack!
	
'	Field DataDirectory:IMAGE_DATA_DIRECTORY ' [IMAGE_NUMBEROF_DIRECTORY_ENTRIES]

	Field VirtualAddress1:Int
	Field Size1:Int
	
	Field VirtualAddress2:Int
	Field Size2:Int
	
	Field VirtualAddress3:Int
	Field Size3:Int
	
	Field VirtualAddress4:Int
	Field Size4:Int
	
	Field VirtualAddress5:Int
	Field Size5:Int
	
	Field VirtualAddress6:Int
	Field Size6:Int
	
	Field VirtualAddress7:Int
	Field Size7:Int
	
	Field VirtualAddress8:Int
	Field Size8:Int
	
	Field VirtualAddress9:Int
	Field Size9:Int
	
	Field VirtualAddress10:Int
	Field Size10:Int
	
	Field VirtualAddress11:Int
	Field Size11:Int
	
	Field VirtualAddress12:Int
	Field Size12:Int
	
	Field VirtualAddress13:Int
	Field Size13:Int
	
	Field VirtualAddress14:Int
	Field Size14:Int
	
	Field VirtualAddress15:Int
	Field Size15:Int
	
	Field VirtualAddress16:Int
	Field Size16:Int

End Type

Type IMAGE_FILE_HEADER
	Field Machine:Short
	Field NumberOfSections:Short
	Field TimeDateStamp:Int
	Field PointerToSymbolTable:Int
	Field NumberOfSymbols:Int
	Field SizeOfOptionalHeader:Short
	Field Characteristics:Short
End Type
