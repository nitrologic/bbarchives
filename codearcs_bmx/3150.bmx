; ID: 3150
; Author: Pineapple
; Date: 2014-10-17 17:04:27
; Title: Lexical scanner
; Description: Tokenizes input strings using regex

'   --+-----------------------------------------------------------------------------------------+--
'     | This code was originally written by Sophie Kirschner (meapineapple@gmail.com) and it is |  
'     | released as public domain. Please do not interpret that as liberty to claim credit that |  
'     | is not yours, or to sell this code when it could otherwise be obtained for free because |  
'     |                    that would be a really shitty thing of you to do.                    |
'   --+-----------------------------------------------------------------------------------------+--

' Updated 13 Feb 2015:
'   ScannerToken now has a match attribute which contains the TRegExMatch object from the match
'   which caused the token to be created.



SuperStrict

Import brl.stream
Import brl.retro
Import brl.linkedlist
Import bah.regex ' Available here: https://code.google.com/p/maxmods/wiki/RegExModule


' Example code

Rem

Import brl.standardio

' Make a new scanner object, that's the thing that does all the work.
Local s:Scanner=New Scanner

' Add some token categories for the Tokenize() method to use the regular expressions to extract tokens from text.
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExBMaxCommentLine),False))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExBMaxCommentBlock),False))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExStringLiteral)))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExIdentifier)))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExNumberUnsigned)))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExParenthese)))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExDelimiter)))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExOperator)))
s.AddCategory(ScannerCategory.Create(SRegEx.Create(ScannerCategory.RegExWhitespace),False))

' Add some branches, they define open and close tokens for the Parse() method to use.
' I'm excluding if/endif and repeat/forever/until because they're context-sensitive and it would be a lot of work
' getting them friendly with the Scanner's very generic parsing algorithm.
s.AddBranch(ScannerBranch.Create(["("],[")"]))
s.AddBranch(ScannerBranch.Create(["["],["]"]))
s.AddBranch(ScannerBranch.Create(["{"],["}"]))
s.AddBranch(ScannerBranch.Create(["while"],["wend","endwhile"]))
s.AddBranch(ScannerBranch.Create(["for"],["next"]))
s.AddBranch(ScannerBranch.Create(["type"],["endtype"]))
s.AddBranch(ScannerBranch.Create(["method"],["endmethod"]))
s.AddBranch(ScannerBranch.Create(["function"],["endfunction"]))

' The tokenizer tokenizes itself!
Local tokenizerpath$="scanner.bmx"
Local stream:TStream=ReadFile(tokenizerpath)

' Iterate through a list of tokens returned by the Process() method and print them and their children to the console.
For Local token:ScannerToken=EachIn s.ProcessStream(stream)
    StandardIOStream.WriteString token.ToBranchedString()
Next

' All done!
CloseFile stream

EndRem





' Categories help the Scanner decide how to tokenize the input text. See its Tokenize() method for more info.
Type ScannerCategory
    ' Some common expressions (don't kill me if I got a ton of these wrong)
    Const RegExNumberSigned$="[+-]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?" ' e.g. "25", "3.14", "-.66", "50.32e+10"
    Const RegExNumberUnsigned$="[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?" ' same as above but doesn't grab preceding +/- characters
    Const RegExStringLiteral$="~q.*?~q" ' e.g. "This is a string literal whoo"
    Const RegExMultilineStringLiteral$="(?s)~q.*?~q" ' e.g. "This is a ~r~n multiline string literal ~r~n whoo"
    Const RegExCommentLine$="//[^~r~n]*" ' e.g. "// this is a comment which ends at a newline ~r~n"
    Const RegExCommentBlock$="(?s)/\*.*?(\*/)" ' e.g. /* this is a comment block thing */
    Const RegExBMaxCommentLine$="'[^~r~n]*" ' BlitzMax line comment, e.g. ' this is a comment
    Const RegExBMaxCommentBlock$="(?s)rem.*?(endrem)" ' BlitzMax block comment (Rem/EndRem)
    Const RegExIdentifier$="[_a-zA-Z]+[\w]*" ' e.g. "abcde", "b4t_m4n", but not "3UPERMAN"
    Const RegExIdentifierIntl$="[_\pL]+[\w\pL]*" ' sam e as above but also allows nonenglish letters
    Const RegExParenthese$="[\[\]\(\)\{\}]" ' e.g. "(", "]", "{"
    Const RegExDelimiter$="[;,]" ' i.e. ";", ","
    Const RegExOperator$="[!@#$%^&*-+=:'.<>/?|`\\~~]+" ' e.g. "%", ">>>", "+="
    Const RegExWhitespace$="\s+" ' matches any whitespace characters
    
    ' The regular expression for what constitutes a token of this category
    Field expression:SRegEx
    ' Tokens of this category are only added to the list that Scanner.Tokenize() returns if they're significant
    Field significant%=True
    
    ' Function to create a new ScannerCategory object
    Function Create:ScannerCategory(expression:SRegEx,significant:Int=True)
        Local cat:ScannerCategory=New ScannerCategory
        cat.expression=expression
        cat.significant=significant
        Return cat
    EndFunction 
EndType

' Branches help the Scanner decide how to organize tokens into a tree. See its Parse() method for more info.
Type ScannerBranch
    ' An array of strings that open this kind of branch or close it, respectively. For example: ["("] and [")"], ["while"] and ["wend","endwhile"]
    Field open$[],close$[]
    ' Are the tokens case-sensitive?
    Field casesensitive%=False
    
    ' Create a new ScannerBranch object
    Function Create:ScannerBranch(open$[],close$[],casesensitive%=False)
        Local n:ScannerBranch=New ScannerBranch
        n.open=open;n.close=close;n.casesensitive=casesensitive
        Return n
    EndFunction
    
    ' These are used by the Scanner's Parse() method
    Method CheckOpen%(str$)
        Return CheckArray(open,str)
    EndMethod
    Method CheckClose%(str$)
        Return CheckArray(close,str)
    EndMethod
    Method CheckArray%(array$[],str$)
        If casesensitive
            For Local i%=0 Until array.length
                If array[i]=str Return i
            Next
        Else
            Local lstr$=Lower(str)
            For Local i%=0 Until array.length
                If Lower(array[i])=lstr Return i
            Next
        EndIf
        Return -1
    EndMethod
EndType

' The bread and butter of what a Scanner returns.
Type ScannerToken
    ' This is the string of the token, pretty simple
    Field text$
    ' This is the regex match that this token was grabbed from
    Field match:TRegExMatch
    ' Also simple, pointer to the category that this token belongs to
    Field category:ScannerCategory
    ' Byte and line position in the string/stream/whatever this token was found in
    Field pos_byte%,pos_line%
    ' Parent and children ScannerTokens for keeping track of the tree structure built by a Scanner's Parse() method
    Field parent:ScannerToken,children:TList
    ' Following token
    Field succ:ScannerToken
    
    ' Creates a new ScanerToken
    Function Create:ScannerToken(text$,cat:ScannerCategory=Null,match:TRegExMatch,pos_byte%=-1,pos_line%=-1)
        Local n:ScannerToken=New ScannerToken
        n.text=text;n.category=cat;n.match=match
        n.pos_byte=pos_byte;n.pos_line=pos_line
        Return n
    EndFunction
        
    ' Various methods for representing the token as a string
    Method ToString$()
        Return "token ~q"+text+"~q on "+PositionToString()
    EndMethod
    Method PositionToString$()
        Local str$
        If pos_line>=0
            str:+"line "+pos_line
            If pos_byte>=0 str:+" "
        EndIf
        If pos_byte>=0 str:+"byte offset "+Hex(pos_byte)
        Return str
    EndMethod
    Method ToBranchedString$(linepadding%=3,prefix$="") ' This one is recursive, neat
        Local str$=padbefore(pos_line,"0",linepadding)+" "+prefix+text+"~n"
        Local subprefix$=prefix+"  "
        If children
            For Local token:ScannerToken=EachIn children
                str:+token.ToBranchedString(linepadding,subprefix)
            Next
        EndIf
        Return str
    EndMethod
    Function padbefore$(s$,char$,length%)
        While Len(s)<length
            s=char+s
        Wend
        Return s
    EndFunction 
EndType

' Lexical scanner object, good for tokenizing string data and also organizing those tokens into a tree
Type Scanner
    ' Contains ScannerCategory objects, which tell Tokenize() how to divide text into tokens
    Field Categories:TList=CreateList()
    ' Contains ScannerBranch objects, which tell Parse() how to organize tokens into tree structures
    Field Branches:TList=CreateList()
    
    Rem
        / Summary /
        The Tokenize() method uses ScannerCategory objects to separate tokens, you 
        can add them using this method. See said method's preceding comments for a 
        more thorough explanation.
        / Arguments /
        cat: The ScannerCategory to add
    EndRem
    Method AddCategory:ScannerCategory(cat:ScannerCategory)
        Categories.addlast cat
        Return cat
    EndMethod
    
    Rem
        / Summary /
        The Parse() method uses ScannerBranch objects to determine the tree 
        structure, you can add them using this method. See said method's preceding 
        comments for a more thorough explanation.
        / Arguments /
        branch: The ScannerBranch to add
    EndRem
    Method AddBranch:ScannerBranch(branch:ScannerBranch)
        Branches.addlast branch
        Return branch
    EndMethod
    
    Rem
        / Summary /
        Runs the input string through both the Tokenize() and Parse() methods and
        returns the result.
        / Arguments /
        data: The input string
        start: The initial position
    EndRem
    Method Process:TList(data$,start%=0)
        Return Parse(Tokenize(data,start))
    EndMethod
    
    Rem
        / Summary /
        Same as Process() but accepts a stream as input.
        / Arguments /
        data: The input stream
    EndRem
    Method ProcessStream:TList(data:TStream)
        Return Parse(TokenizeStream(data))
    EndMethod
    
    Rem
        / Summary /
        This method tokenizes an input string using the regular expressions added
        to the Scanner object using the AddCategory() method. It's done like this:
        Starting at the beginning of the string, the tokenizer checks every
        category for a match starting at the current position in the string. The
        token is considered to be a member of the category finding the lengthiest
        match, or the oldest matching category in case of ties. (i.e. the category
        added first beats out the category added next.) In a case where no token
        can be recognized, tokens are added to the list one character at a time
        with null assigned to their category field.
        The list returned is a linear sequence of ScannerToken objects recording
        the text, category, and position in file by byte and by line for one token
        each.
        / Arguments /
        data: The input string
        start: The initial position
    EndRem
    Method Tokenize:TList(data$,start%=0)
        Local tokens:TList=CreateList()
        Local pos%=start,line%=1
        Repeat
            Local bestcat:ScannerCategory=Null,bestmatch:TRegExMatch,beststr$,bestlength%=0
            For Local cat:ScannerCategory=EachIn Categories
                If cat.expression
                    Local match:TRegExMatch=cat.expression.find(data,pos)
                    If match And match.SubStart()=pos
                        Local str$=match.SubExp()
                        If str.length>bestlength
                            bestcat=cat
                            bestmatch=match
                            beststr=str
                            bestlength=str.length
                        EndIf
                    EndIf
                EndIf
            Next
            Local token:ScannerToken=Null
            If bestcat And bestlength ' token has been neatly categorized
                If bestcat.significant token=ScannerToken.Create(beststr,bestcat,bestmatch,pos,line)
                pos:+bestlength
                line:+CountNewlines(beststr)
            Else ' doesn't fit any category? make it its own token.
                token=ScannerToken.Create(Chr(data[pos]),Null,Null,pos,line)
                pos:+1
                line:+CountNewlines(Chr(data[pos]))
            EndIf
            If token
                Local lasttoken:ScannerToken=ScannerToken(tokens.last())
                If lasttoken Then lasttoken.succ=token
                tokens.addlast token
            EndIf
            If pos>=data.length Exit
        Forever
        Return tokens
    EndMethod
    
    Rem
        / Summary /
        Same as Tokenize() but accepts a stream as input.
        / Arguments /
        data: The input stream
    EndRem
    Method TokenizeStream:TList(data:TStream)
        Return Tokenize(data.ReadString(data.size()))
    EndMethod
    
    Rem
        / Summary /
        to the Scanner object using the AddCategory() method. It's done like this:
        This method takes a sequence of tokens as input, such as the list outputted
        by the Tokenize() method, and reorganizes them into a tree structure
        wherein each ScannerToken can have another token as its parent and any
        number of tokens as children.
        The Scanner's list of ScannerBranch objects, added using AddBranch(),
        decide the structure of the output. A branch's open tokens cause another
        level to be added to the tree while close tokens cause the parser to back
        up a level. For example, the sequence of tokens "abc" "(" "123" ")" "xyz",
        when "(" opens a branch and ")" closes it, will become "abc" "(" "xyz" -
        the tokens following "(" and ending with ")" will become children of the
        "(" token. This is a recursive process and nesting is theoretically
        unbounded.
        Generally, you would use this method before calling a more implementation-
        specific parser so that it wouldn't need to organize the tokens into a tree
        for itself. 
        / Arguments /
        tokens: A list of sequential ScannerToken objects, such as is returned by
        the Tokenize() method
    EndRem
    Method Parse:TList(tokens:TList)
        If Not tokens Return Null
        Local list:TList=CreateList()
        Local entry:ScannerParseStackEntry=ScannerParseStackEntry.Create(Null,list,Null,Null)
        For Local token:ScannerToken=EachIn tokens
            entry.list.addlast token
            token.parent=entry.root
            Local branchopen:ScannerBranch=CheckBranchOpen(token)
            Local branchclose:ScannerBranch=CheckBranchClose(token)
            If branchopen And branchclose ' edge case of you being dumb enough to make the same token both an open and close branch
                ' if the parser ends up in here HOPEFULLY it's just because there's a branch that can start and end with the same token. 
                ' because otherwise the parser just makes some assumptions and runs with them.
                ' in short, if branchopen!=branchclose here then you've probably done something stupid defining the way the code branches
                If branchclose=entry.branchtype ' just ran into one of these; assume it's being closed
                    entry=entry.parent
                Else ' haven't run into one of these in the same tree level; assume it's being opened
                    If Not token.children token.children=CreateList()
                    entry=ScannerParseStackEntry.Create(token,token.children,branchopen,entry)
                EndIf
            ElseIf branchopen ' a branch-open symbol was encountered
                If Not token.children token.children=CreateList()
                entry=ScannerParseStackEntry.Create(token,token.children,branchopen,entry)
            ElseIf branchclose ' a branch-close symbol was encountered
                If branchclose=entry.branchtype ' proper closing token, just the kind that's expected
                    entry=entry.parent
                Else ' ran into some shit like { ) }
                    'Print entry.root.tobranchedstring()
                    Throw ScannerException.Create(ScannerException.ExceptionBadBranchTermination,"Unexpected branch termination",token)
                EndIf
            EndIf
        Next
        If entry.parent Throw ScannerException.Create(ScannerException.ExceptionEOFNoBranchTermination,"EOF without expected branch termination",entry.root)
        Return list
    EndMethod
    
    ' This is used by the Tokenize() method to keep track of line numbers
    Function CountNewlines%(str$)
        Local i%=0,lines%=0
        While i<str.length
            If str[i]=Asc("~n")
                lines:+1
            ElseIf str[i]=Asc("~r")
                If str[i+1]=Asc("~n") i:+1
                lines:+1
            EndIf
            i:+1
        Wend
        Return lines
    EndFunction
    
    ' These are used by the Parse() method to cleanly determine whether a given token opens/closes a branch
    Method CheckBranchOpen:ScannerBranch(token:ScannerToken)
        For Local branch:ScannerBranch=EachIn Branches
            If branch.CheckOpen(token.text)>=0 Return branch
        Next
        Return Null
    EndMethod
    Method CheckBranchClose:ScannerBranch(token:ScannerToken)
        For Local branch:ScannerBranch=EachIn Branches
            If branch.CheckClose(token.text)>=0 Return branch
        Next
        Return Null
    EndMethod
EndType

' This class is used in a Scanner's Parse() method
Type ScannerParseStackEntry
    Field root:ScannerToken,list:TList,parent:ScannerParseStackEntry
    Field branchtype:ScannerBranch
    Function Create:ScannerParseStackEntry(root:ScannerToken,list:TList,branchtype:ScannerBranch,parent:ScannerParseStackEntry)
        Local n:ScannerParseStackEntry=New ScannerParseStackEntry
        n.list=list;n.root=root;n.branchtype=branchtype;n.parent=parent
        Return n
    EndFunction
EndType

' Exception object to be thrown by the parser. The parser uses exceptions as a way to allow implementation-agnostic error handling.
Type ScannerException Extends TBlitzException
    ' Constants identify the different possible sorts of exceptions
    Const ExceptionBadBranchTermination%=1
    Const ExceptionEOFNoBranchTermination%=2
    ' Exception data: a message, an error number, and the token it happened at
    Field info$,num%,token:ScannerToken
    ' Create a new exceptions
    Function Create:ScannerException(num%,info$,token:ScannerToken)
        Local n:ScannerException=New ScannerException
        n.num=num;n.info=info;n.token=token
        Return n
    EndFunction
    ' Make it a string
    Method ToString$()
        Local str$="Error "+num+": "+info
        If token str:+"; encountered at "+token.ToString()
        Return str
    EndMethod
EndType

' Extension of TRegEx prevents useless repetition of the same find operations
Type SRegEx Extends TRegEx
    ' If the conditions of the previous check are identical to current then find will return the last result
    Field lastoption:TRegExOptions=Null,lastpattern$,lastpos%=-1,lastmatch:TRegExMatch
    ' Make a new SRegEx object
    Function Create:SRegEx(searchPattern$,options:TRegExOptions=Null)
        Local this:SRegEx=New SRegEx
        this.searchPattern=searchPattern        
        If options TRegEx.options=options
        Return this
    EndFunction
    ' Do the finding
    Method find:TRegExMatch(target$=Null,start%=-1)
        If compiled And options=lastoption And lastpos>=0 And start<=lastpos And ..
           searchpattern=lastpattern And target=lasttarget
            Return lastmatch
        Else
            lastoption=options
            lastpattern=searchpattern
            lastmatch=Super.find(target,start)
            If lastmatch lastpos=lastmatch.SubStart() Else lastpos=target.length
            Return lastmatch
        EndIf
    EndMethod
EndType
