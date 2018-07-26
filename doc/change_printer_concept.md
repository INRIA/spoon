Structure:
-----------------
* Element has one Position
* Position has one or more Fragments (for example fragments of CtClass are: modifiers, name, extends, implements, body)
* Fragment has startOffset, endOffset and one or more CtRoles (for example CtRoles of CtClass.modifiers are annotation, modifier).
* Fragment with CtRole comment can be everywhere.
* The Fragment has firstChild and nextSibling properties which allows to build a tree of Fragments.
* The Fragments in tree of Fragments are organized by start/endOffset. 
    * Siblings are fragment with increasing not overlapping offsets
    * Children are fragments with start/endOffset in scope of it's parent
* each character of source file belongs to some Fragment

There are these kinds of Fragments

A) MainFragment of a CtElement - represents whole origin source code of a CtElement. It has link to it's CtElement

B) ChildFragment of a CtElement - represents some part of origin source code of a CtElement. It identifies the part of source code of CtElement



ChildFragment has these sub types (by identification of sub type):

A) keyword child fragment - contains a keyword of CtElement sources. For example: "class", "if", "for"

B) primitive child fragment - contains source code of a primitive attribute of CtElement.
	For example source code which represents `name` of element. It knows one CtRole of the primitive attribute

C) single child fragment - contains source code of child CtElement on a CtRole.
	For example type of CtField or super class of a CtClass

D) list child fragment - constains source code of zero one or more child CtElements on a one or more CtRoles.
	It knows one or more CtRoles. It knows prefix, separator and suffix characters.
	For example: 
		type members of CtType 
		or statements of CtBlock 
		or Annotations and modifiers of CtType

We know which element is actually printed. DJPP#enter/exit can be used to detect that.

We need to know which Fragment of element is actually printed. E.g. by listening on TokenWriter events and by listening to DJPP#enter/exit for child elements.

If no attribute of fragment of printed element is modified, then:
* whole fragment can be copied from origin source code
* all DJPP tokens are ignored as long as they are printing source code of that element

If any attribute of printed element is modified, then:
* we need to know all child fragments of that element
* for each child fragment we need to know whether it is modified or not
* the not modified fragments can be printed from origin source code
* the primitive modified fragments has to be printed by DJPP
* the modified fragments of nested CtElements has to be printed using this algorithm recursively
* the actually printed child fragment has to be synchronized with DJPP tokens this way:
    * listening on TokenWriter and searching for primitive child fragment with same token
    * listening of DJPP enter/exit and searching for element child fragment with same role


How to detect that DJPP finished with one SourceFragment and is going to start with next SourceFragment
* the defined TokenWriter method with defined value is called. For example:
  * keyword 'class' ends the modifiers section 
  * separator '{' starts TypeMembers of CtType
* the DJPP#scan with an element whose CtElement#getRoleInParent returns expected CtRole. For example:
  * element with parent CtRole#TYPE is scanned from CtExecutable, when modifiers of method are finished
* the DJPP or ElementPrinterHelper will call a new listener to notify that fragment/role is finished
* the Spoon model will fire an event when getter of a property is called.

The current SourceFragment can listen on events to detect whether fragment ended
The next SourceFragment can listen on events to detect whether fragment started

Handling of comments:
-----------------
Comments can be everywhere, they are like tokens in the token stream.
So the List of SourceFragments, which describe parts of origin source of current element should contain a SourceFragment for each comment too
Note: we should care only about comments dirrectly belonging to current element. We have to ignore the comments which belongs to child elements

Then we can detect which CtComment belongs to which SourceFragment and add/remove/modify it if needed.

Handling of lists
-----------------
If the list itself is not modified (add/removed/moved item), just some existing item is modified,
then we should keep source code of list start/end and all item separators

If the list itself is modified (add/removed/moved item), then we have to keep list member prefixes and separators of not modified items 
	and to predict prefixes/suffixes separators of add/removed/moved items.

IDEAS:
--------

All the SourcePosition elements might be connected into a tree using
SourcePosition.nextSibling
SourcePosition.firstChild
