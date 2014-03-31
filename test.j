.class public Fref
.super java/lang/Object

.method public static Out(I)V

	.limit stack 20
	.limit locals 20
	iload 0
	getstatic java/lang/System/out Ljava/io/PrintStream;
	swap
	invokevirtual java/io/PrintStream/println(I)V
	return

.end method
.method public static main([Ljava/lang/String;)V
	.limit stack 100
	.limit locals 100

ldc 2
istore 1
iload 1
ldc 1
isub
istore 0
iload 0
ldc 1
ldc 2
istore 0
ldc 3
istore 0
iload 0
ldc 2
ldc 0
istore 0
iload 0
ldc 2
imul
istore 0
iload 0
ldc 10
istore 2
iload 2
invokestatic Fref.Out(I)V
	return

.end method
