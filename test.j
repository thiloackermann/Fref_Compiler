.class public Fref
.super java/lang/Object

.method public static out(I)V

	.limit stack 2
	.limit locals 2
	iload 0
	getstatic java/lang/System/out Ljava/io/PrintStream;
	swap
	invokevirtual java/io/PrintStream/println(I)V
	return

.end method
.method public inc(I)I

	.limit stack 20
	.limit locals 20
iload 0
ldc 1
iadd
istore 0
iload 0
ireturn
.end method
.method public static main([Ljava/lang/String;)V

	.limit stack 20
	.limit locals 20
ldc 1
istore 1
lb0:
iload 1
invokestatic Fref.out(I)V
iload 1
invokevirtual Fref.inc(I)I
istore 1
iload 1
ldc 4
isub
iflt lb0
return
.end method
