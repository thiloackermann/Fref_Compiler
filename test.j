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
.method public static inc(II)I

	.limit stack 2
	.limit locals 2
        iload 0
        iload 1
        iadd
        istore 0
        iload 0
        ireturn
.end method
.method public static main([Ljava/lang/String;)V

	.limit stack 2
	.limit locals 3
        ldc 1
        istore 1
        ldc 1
        istore 2
lb0:
        iload 1
        invokestatic Fref.out(I)V
        iload 1
        iload 2
        invokestatic Fref.inc(II)I
        istore 1
        iload 1
        ldc 4
        isub
        iflt lb0
        return
.end method
