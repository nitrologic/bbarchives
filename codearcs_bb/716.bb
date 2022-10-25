; ID: 716
; Author: Jim Teeuwen
; Date: 2003-06-10 20:38:30
; Title: Using The Microsoft .NET Framework with Blitz
; Description: Howto use .NET DLL's with Blitz3D/BlitzPlus

=== BlitzBasic and Microsoft.NET =====================================

Written by:		Jim Teeuwen / Defiance <JimTeeuwen@hotmail.com>

Table Of Contents

	I.	Preface			: What, Where and Why?
	II.	Requirements		: The goods
	III.	Gettting Started		: Creating the Dll
	IV.	The real Business		: Preparing it for use with Blitz.
	V.	Finalize			: Recompile the Dll and use it


I. Preface

The following Article explains in detail how to use any .NET Assembly
from BlitzPlus/Blitz3D.

This Article focusses on how to use a DLL created with the Microsoft
.NET Framework with Blitz. I have written this article since I personally
believe, the .NET Framework is the best that ever happened to the
programming part of the world, and I found it unbearable that Blitz could
not utilize the potential power it offers in terms of both Game and Application
development.

In this article I am going to assume a few things about you.

	1. You know what the .NET Framework is and how it works.
	2. You know how to use at least 1 .NET enabled programming language
	   like C# (C-sharp), VB.BET, Managed C++, IL, etc.
	3. You know about IL Code, the IL Assembler and Disassembler.
	   You do not have to be a guru, just some knowledge about
	   it's existance will do, I will try to explain as much about it
	   as possible.
	   This is actually an important part, since the actual code exporting
	   is done using IL Code.
	4. You know how Blitz userlibs work.

If either of these items are not met, I suggest you try ad learn about those
before you continue with this article. I will try to explain all the steps
involved as good as I can, so a complete newbie should be able to use it as well,
but lack of knowledge about some of these items might get you into trouble at some stage.


II.	Requirements

Goody, enough with the whining, On to the list of required items for this stuff to work.
Below is a list of things you will need to make all the stuff come together.

	1. Your very own purchased version of either Blitz3D or BlitzPlus.
	   Both have to support UserLibs.

	   You can buy Blitz3D/BlitzPlus from: http://www.blitzbasic.com

	2. The Microsoft .NET Framework Software Development Kit.
	   This is crucial, since you need to write your DLL's with it.
	   Also, this SDK contains the required IL (dis)assembler.

	   You can download the SDK from: http://msdn.microsoft.com/downloads/

	3. A little patience and the will to learn. The process for creating
	   your Blitz DLL's with .NET is not hard, but it will require you to
	   pay attention to some details about the gory CLR internal workings.

If you got all this sorted, we're ready to rock!


III.	Gettting Started

Now that everything is sorted out, we can start to create our first 
managed DLL.

In this article I will create a DLL wich exports 1 Function wich Blitz can Call.
We will call it: SayHello()

As said before in this article, I am asuming you know how to write a normal DLL
using the .NET framework and any language it supports.
From here on I will be using C#, since I consider it the best language ever to
be created, but the sample should not be too hard to convert to your own liking.

Below is the code for my simple dll.

[Code]
// HelloWorldDll.cs
using System;

namespace HelloWorldDll
{
	public class HelloWorldClass
	{
		public static string SayHello(string name)
		{
			return ("Hello " + name);
		}
	}
}
[/code]

Up until now, this should appear pretty straightforward to any of you.
I didnt do anything special. just create a namespace HelloWorldDll.
Create a class called HelloWorldClass. and give it 1 method called SayHello(string name).

Now, all our function does is take a string value as a parameter, combine it with
the string  "Hello " and return the resulting string.

Note that I have made the class PUBLIC. this is NOT required! it does not matter
how you define your class for the final result to work. it's just cos I felt like doing
it this way. 
The same goes for the function declaration. Your function does not have to be
public or static for that matter. just use whatever suits your needs.

The reason I used a parameter and a return value, is because I want to show you that
passing parameters ands getting return values does not pose any problem in Blitz.
You can use any basic Data type like byte, short, long, int, string, uint, ulong etc etc etc.
Yes, you can even use a struct as a parameter. in wich case, you create a Type in your blitz code
wich has exactly the same fields as the struct in your .NET assembly.

Example:

[Code]
// C#
public struct SomeStruct
{
	public int X, Y, Z;
	public string Name;
	public object Obj;
}

;// Blitz
Type SomeType
	Field X, Y, Z
	Field Name$
	Field Obj
End Type
[/Code]

Te above will work if you pass an instance of your SomeType Type to the function as a parameter.
The only limitation is the fact that you cannot have the .NET function have such a struct as a return
value. Just pass the instance as as a parameter and have it filled like that.

Example:
[Code]
// BAD
public SomeStruct DoStuff()
{
	SomeStruct obj	= new SomeStruct();
	obj.X			= 0;
	obj.Y			= 0;
	obj.Name		= "This will never arrive in blitz!";
	obj.Obj		= null;
	return obj;
}

// GOOD
public void SomeStruct(ref SomeStruct obj)  // ref = Pass the SomeStruct instance By Reference (ByRef for VB people)
{
	obj.X			= 0;
	obj.Y			= 0;
	obj.Name		= "This *will* arrive in blitz!";
	obj.Obj		= null;
}
[/Code]

Now that this is sorted, I want to get back to the main issue,
before we loose track of it.

We created our Dll code, and now it's time to compile it.
Just hit Shift-F5 in Visual Studio .NET if you have it, or use the
commandline compiler.

[code]
In the case of C# code, use:
	c:\csc /OUT:HelloWorldDll.dll /target:library HelloWorldDll.cs

In the case of VB.NET code, use:
	c:\vbc /OUT:HelloWorldDll.dll /target:library HelloWorldDll.vb
[code]

If all is ok, you should now have a new file called HelloWorldDll.dll in
your working directory.


IV.	The real Business

Now it's time to get down n dirty. We need to create a Dll wich normally runs
in a managed environment, and have it work with completely unmanaged code.
How, o, how do we fix this??
First of all, Exporting managed code for use in unmanaged assemblies/code is
normally possible through a technology called COM interop. This basicly means
that you create a COM interface for your DLL and have the unmanaged code use this
to interact with your Dll.

...BUT...

You guessed it, Blitz does not support COM! Bummer!
What now? Well, the answer lies in the magical world of IL Code! :)
For the ones of you who never heard of it, a short introduction to IL.

MSIL (MicroSoft Intermediate Language), or IL for short, is basicly the
assembly version of the .NET Framework. Tt behaves like Java Bytecode,
or at least, it performs the same task, in that IL Code is the Final step that
ALL .NET languages like C#, VB.NET, C++ etc get to before being compiled to
native machine code (by the JIT compiler). Meaning that all these languages
Ultimatly compile to Pure IL Code.

It is this IL Code, wich makes sure that all the previously named languages
can be used with eachother, and wich (theoreticly) makes any .NET assenbly Platform
independant. This IL Code is compiled into the resulting Exe or Dll, together with an
extensive description of it's actuall contents, called MetaData.

What we are interested in, is the IL Code itself. As I just explained, this is stored
in the final Exe/Dll, so we need a way to extract it. The answer to this is the nifty
little tool called: IldAsm.exe wich comes with the .NET Framework SDK.
As the name suggests, it DE-compiles .NET Assemblies into IL Code.
That's right, you can decompile any .NET Exe or DLL into pure IL code!
Good or bad? That's a matter of discussion I guess, and many ppl are still wondering whether to
consider it a bug or a feature. Personally I think it's great for learning
purposes and situations such as the one we face in this article, where a regular
language just wont cut it, and we need that extra edge IL code offers.


To Business.
We will now decompile our dll into IL code, so we can edit it around a bit and then
re-compile it into our Dll.
To do this we open a command prompt and CD to the directory with our Dll in it.
Then type:

	c:\somedir\ildasm /OUT:HelloWorldDll.il HelloWorldDll.dll

This will create 2 new files:
	- HelloWorldDll.il
	- HelloWorldDll.res

We can safely delete the .res file, since it's not needed for our current DLL.
this .res file contains resource information about our DLL. wich is needed if your
dll contains forms and controls and such, but in our case it's just a file waiting
to be deleted.

The file of interest is, offcourse, HelloWorldDll.il
Open it in a text editor and be amazed at the mess you see.
Please dont get put off by the garbled presentation of the code,
since it's really not that bad. First of all, you may want to clean it
up a bit by removing all the residual blank lines and unsightly comments (starting with '// ..')

Note that this is NOT needed!. this code will compile into a dll perfectly, it's just
to make life easier on you when you edit the code.

Below is what our IL Code should look like after cleaning it up.
All I am leaving in there is the relevant parts. eg: the parts we need for our final DLL

[code]
.assembly extern mscorlib
{
  .publickeytoken = (B7 7A 5C 56 19 34 E0 89 )
  .ver 1:0:3300:0
}
.assembly HelloWorld
{
  .hash algorithm 0x00008004
  .ver 0:0:0:0
}
.module HelloWorld.dll
.imagebase 0x00400000
.subsystem 0x00000003
.file alignment 512
.corflags 0x00000001

.namespace HelloWorldDll
{
  .class public auto ansi beforefieldinit HelloWorldClass extends [mscorlib]System.Object{}
}

.namespace HelloWorldDll
{
  .class public auto ansi beforefieldinit HelloWorldClass extends [mscorlib]System.Object
  {
    .method public hidebysig static string SayHello(string name) cil managed
    {
      .maxstack  2
      .locals init (string V_0)
      IL_0000:  ldstr      "Hello "
      IL_0005:  ldarg.0
      IL_0006:  call       string [mscorlib]System.String::Concat(string, string)
      IL_000b:  stloc.0
      IL_000c:  br.s       IL_000e

      IL_000e:  ldloc.0
      IL_000f:  ret
    }

    .method public hidebysig specialname rtspecialname instance void  .ctor() cil managed
    {
      .maxstack  1
      IL_0000:  ldarg.0
      IL_0001:  call       instance void [mscorlib]System.Object::.ctor()
      IL_0006:  ret
    }
  }
}
[/code]


Now isn't that beautifull? :D
As you can see, IL looks like a genuine programming language! :)
In effect, it is a genuine programming language, because you can actually
write your programs straight in IL if you want. It's a pretty straigh forward
language. Easy to understand as well. Not anything at all like MASM32 or TASM or any
of that stuff. IL Code is a Stack Based Assembly language. The big difference with
languages like MASM32 and TASM is that IL does NOT use Registers, but all is
done through the Stack and Heap. But thats all gory details wich you probably
wont need, enless you want to become an IL guru.
In wich case by the way, I would like to recommend to you an excellent book called:

	Inside Microsoft .NET IL Assembler, by Serge Lidin

This book is the best possible read about IL and the Common Language Runtime available.
Serge Lidin is the guy who designed IL and a large part of the CLR itself so he knows what he's talking
about. The book explains a lot of the really gory details about the inner workings of
IL code, the CLR and also Why it works the way it does. VERY interesting read!
Btw. if you ever need to ask Mr. Lidin some questions about IL, I happen to know he frequently
visits the IL Code Forum on www.gotdotnet.com ;)

Anyways... we want to export our code for use in blitz.
So let's get to it.

First we will change the line that says:
.corflags 0x00000001
into:
.corflags 0x00000002

Why? Well, this flag is part of the Common language Runtime Header, wich tells
windows that it's dealing with a genuine .NET assembly and not a regular
windows executable.

This value is, by default, always set to COMIMAGE_FLAGS_ILONLY (0x00000001)
This means that the assembly contains only Pure IL Code. So no Embedded native/unmanaged
code is present in the Exe or Dll.

<THEORY>
COMIMAGE_FLAGS_ILONLY (0x00000001)
The Image Flags contains IL Code Only. with no embedded native unmanaged code, except
the startup stub. Because Comon Language Runtime - aware Operating Systems (such as Windows XP)
ignore the startup stub, for all practicle Purposes the file can be considered Pure-IL.
However using this flag can cause certain IlAsm compiler-specific problems when running under
Windows XP. If This flag is set, WinXP ignores not only the startup stub but also the .reloc
section.
</THEORY>

Now the important part is 'WinXP ignores not only the startup stub but also the .reloc section'
This means that the functions we will export as unmanaged code will not be properly loaded.
So, Fix our problem we will change the default flag to: COMIMAGE_FLAGS_32BITSREQUIRED (0x00000002)

<THEORY>
COMIMAGE_FLAGS_32BITSREQUIRED (0x00000002)
The Image file can be loaded only into a 32-bit process. This flag is set wheb native unmanaged code
is embedded in the PE file or when the .reloc section is not empty.
</THEORY>


Next up: Reserving some space in our final Dll to store the address of our function.
This will be filled at runtime with the actual address of the function, we just need to
reserve the space.

<THEORY>
In order to expose managed methods as unmanaged exports, the ILAsm compiler builds a v-Table,
A v-Table Fixup (VTableFixup) table, and a group of unmanaged export tables, wich include the
Export Address Table, the Name Pointer Table, the Ordinal Table, the Export Name Table and the
Export Directory Table.

The VTableFixup table is an array of VTableFixup Descriptors with each Descriptor carying the RVA
of a v-table entry, the number of slots in the entry, and the binary flags indicating the size
of each slot (32 or 64 bit) and any special features of the entry.

Each slot of a V-table in a managed PE File carries the token of the method the slot represents.
At runtime the V-table fixups are executed, replacing the method tokens with actual method
addresses.

The IlAsm syntax for a v-table fixup is:
	.vtfixup [<num_slots>] <flags> at <data_label>

Note that the square brackets in [<num_slots>] are part of the statement, and do not
mean that <num_slots> is optional!

<num_slots> is an Integer constant indicating the number of v-table slots grouped
into one entry because their flags are identical. This serves no other purpose except to
save space in your Code file. you can use a seperate .vtfixup statement for each method if
you like.

The <flags> statement can consist of any of the following:
-	int32
		Each slot of the vtable entry is 4 bytes wide.
-	int64
		Each slot of the vtable entry is 8 bytes wide.
		(int32 and int64 flags are mutually exclusive)
-	fromunmanaged
		The entry is to be called from the unmanaged code,
		so the marshalling thunk must be created by the runtime.
-	callmostderived
		Currently not used.

The order of appearance of .vtfixup declarations defines the order of the respective
VTableFixup descriptors in the VTableFixup table.
The Vtable entries are defined simply as data entries. Note that the VTable must
be contiguous. I other words, the data definitions fot the vtable entries must immediatly follow
one another.

Example:
[code]
...
.vtfixup [1] int32 fromunmanaged at VT_01
...
.vtfixup [1] int32 at VT_02

...

.data VT_01 = int32(0x0600001A)
.data VT_02 = int32(0x0600001B)
[/code]


The actuall data representing the Method tokens is automaticly generated by the IlAsm
compiler and placed in designated vTable slots. To achieve that, it is necesarry to indicate
wich method is represented by wich Vtable slot.
IlAsm provides the .vtentry directive for this purpose

	.vtentry <entry_number> : <slot_number>

Where <entry_number> and <slot_number> are 1-based integer constants.
The .vtentry directive is placed within the respective method's scope. as shown
in the following code:

[code]
...
.vtfixup [1] int32 fromunmanaged at VT_01
.data VT_01 = int32(0)		// alays use 0, the slot will be filled automaticly by ILASM.exe

.method public static void Foo()
{
	.vtentry 1 : 1		// entry 1, slot 1
}
[/code]


The ILAsm syntax for actually declaring a method as exported code is quite simple:

	.export [<ordinal>] as <export_name>

Where <ordinal> is an integer constant. The <export_name> provides an alias for the
exported method, so this is what you will use to call the method from your blitz program.
the <export_name> directive us required, even if it is the same as the original method name.
</THEORY>

Well then, now we know all this, we will apply it to our own little Dll.
All changes are marked with '// ### CHANGE ####'

[code]
.assembly extern mscorlib
{
  .publickeytoken = (B7 7A 5C 56 19 34 E0 89 )
  .ver 1:0:3300:0
}
.assembly HelloWorld
{
  .hash algorithm 0x00008004
  .ver 0:0:0:0
}
.module HelloWorld.dll
.imagebase 0x00400000
.subsystem 0x00000003
.file alignment 512

.corflags 0x00000002					// ### CHANGE #### -> Change Image CoreFlag to COMIMAGE_FLAGS_32BITSREQUIRED to fix the potential WinXP pitfall
.vtfixup [1] int32 fromunmanaged at VT_01		// ### CHANGE #### -> Create a VTable entry wich will contain the needed data to identify our function
.data VT_01 = int32(0)					// ### CHANGE #### -> Create a data entry to hold the Virtual Address to our function

.namespace HelloWorldDll
{
  .class public auto ansi beforefieldinit HelloWorldClass extends [mscorlib]System.Object{}
}

.namespace HelloWorldDll
{
  .class public auto ansi beforefieldinit HelloWorldClass extends [mscorlib]System.Object
  {
    .method public hidebysig static string SayHello(string name) cil managed
    {

	.vtentry 1 : 1				// ### CHANGE #### -> Specify wich VTable entry to use for this function
	.export [1] as SayHello			// ### CHANGE #### -> Export the method as unmanaged code with the alias "SayHello"

      .maxstack  2
      .locals init (string V_0)
      IL_0000:  ldstr      "Hello "
      IL_0005:  ldarg.0
      IL_0006:  call       string [mscorlib]System.String::Concat(string, string)
      IL_000b:  stloc.0
      IL_000c:  br.s       IL_000e

      IL_000e:  ldloc.0
      IL_000f:  ret
    }

    .method public hidebysig specialname rtspecialname instance void  .ctor() cil managed
    {
      .maxstack  1
      IL_0000:  ldarg.0
      IL_0001:  call       instance void [mscorlib]System.Object::.ctor()
      IL_0006:  ret
    }
  }
}
[/code]


There ya go! we're done! :)
After all the theoreticle mumbo-jumbo it seemed a daunting task, but as you see,
it requires you add just a few lines of code and your set. Really no big deal.
Time to re-compile our dll and use it in blitz!


V.	Finalize

Save the HelloWorldDll.il file and close your text editor.
Open a commandline and CD to the dir your il file resides in.
Then type:

	c:\somedir\ilasm /OUT:HelloWorldDll.dll HelloWorldDll.il /DLL
	(Do not forget the /DLL switch!)

Congratulations! Now you have a newly compiled HelloWorldDll.dll file wich you can use in blitz!
Your vewy first Blitz-compatible-.NET-assembly! :D
To test it, copy the Dll into the Blitz3D\Userlibs\ directory and create a new textfile called
'HelloWorldDll.decls'

Open it in notepad and add the following:
[code]
.lib "HelloWorldDll.dll"
SayHello$(name$) : "SayHello"
[/code]

Save it and Close it. then open Blitzbasic. create a new sourcefile and type:

[code]
Print( SayHello("Your Name Here") )
[/code]

Hit F5 and be amazed at your work!

Thats it folks! You now have the power to use the entire Microsoft .NET Framework with BlitzPlus and Blitz3D!
Use it! :)

Regards, Jim Teeuwen.
