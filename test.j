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

ldc 3
istore 0
iload 0
invokestatic Fref.Out(I)V
	return

.end method
