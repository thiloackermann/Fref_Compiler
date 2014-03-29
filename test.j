.class public Fref
.super java/lang/Object

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
istore 99
jsr Out
	return

Out:
	getstatic java/lang/System/out LJava/io/PrintStream;
	swap
	invokevirtual java/io/PrintStream/println(I)V
	ret 99

.end method