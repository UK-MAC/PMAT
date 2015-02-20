/* Generated By:JavaCC: Do not edit this line. DerivedDataParserTokenManager.java */
package uk.co.awe.pmat.deriveddata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import static uk.co.awe.pmat.deriveddata.ParserValues.*;

/** Token Manager. */
public class DerivedDataParserTokenManager implements
		DerivedDataParserConstants {

	/** Debug output. */
	public java.io.PrintStream debugStream = System.out;

	/** Set debug output. */
	public void setDebugStream(java.io.PrintStream ds) {
		debugStream = ds;
	}

	private final int jjStopStringLiteralDfa_0(int pos, long active0) {
		switch (pos) {
		default:
			return -1;
		}
	}

	private final int jjStartNfa_0(int pos, long active0) {
		return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
	}

	private int jjStopAtPos(int pos, int kind) {
		jjmatchedKind = kind;
		jjmatchedPos = pos;
		return pos + 1;
	}

	private int jjMoveStringLiteralDfa0_0() {
		switch (curChar) {
		case 13:
			jjmatchedKind = 4;
			return jjMoveStringLiteralDfa1_0(0x20L);
		case 40:
			return jjStopAtPos(0, 10);
		case 41:
			return jjStopAtPos(0, 11);
		case 42:
			return jjStopAtPos(0, 9);
		case 43:
			return jjStopAtPos(0, 6);
		case 44:
			return jjStopAtPos(0, 12);
		case 45:
			return jjStopAtPos(0, 7);
		case 47:
			return jjStopAtPos(0, 8);
		default:
			return jjMoveNfa_0(0, 0);
		}
	}

	private int jjMoveStringLiteralDfa1_0(long active0) {
		try {
			curChar = input_stream.readChar();
		} catch (java.io.IOException e) {
			jjStopStringLiteralDfa_0(0, active0);
			return 1;
		}
		switch (curChar) {
		case 10:
			if ((active0 & 0x20L) != 0L)
				return jjStopAtPos(1, 5);
			break;
		default:
			break;
		}
		return jjStartNfa_0(0, active0);
	}

	private int jjMoveNfa_0(int startState, int curPos) {
		int startsAt = 0;
		jjnewStateCnt = 34;
		int i = 1;
		jjstateSet[0] = startState;
		int kind = 0x7fffffff;
		for (;;) {
			if (++jjround == 0x7fffffff)
				ReInitRounds();
			if (curChar < 64) {
				long l = 1L << curChar;
				do {
					switch (jjstateSet[--i]) {
					case 0:
						if ((0x3ff000000000000L & l) != 0L) {
							if (kind > 13)
								kind = 13;
							jjCheckNAddStates(0, 9);
						} else if (curChar == 46)
							jjCheckNAddTwoStates(7, 11);
						else if (curChar == 34)
							jjCheckNAdd(4);
						else if (curChar == 39)
							jjCheckNAdd(1);
						break;
					case 1:
						if ((0x3ffff0100000000L & l) != 0L)
							jjCheckNAddTwoStates(1, 2);
						break;
					case 2:
						if (curChar == 39 && kind > 18)
							kind = 18;
						break;
					case 3:
						if (curChar == 34)
							jjCheckNAdd(4);
						break;
					case 4:
						if ((0x3ffff0100000000L & l) != 0L)
							jjCheckNAddTwoStates(4, 5);
						break;
					case 5:
						if (curChar == 34 && kind > 22)
							kind = 22;
						break;
					case 6:
						if (curChar == 46)
							jjCheckNAddTwoStates(7, 11);
						break;
					case 7:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 13)
							kind = 13;
						jjCheckNAddTwoStates(7, 8);
						break;
					case 9:
						if (curChar == 45)
							jjCheckNAdd(10);
						break;
					case 10:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 13)
							kind = 13;
						jjCheckNAdd(10);
						break;
					case 11:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 16)
							kind = 16;
						jjCheckNAddTwoStates(11, 12);
						break;
					case 13:
						if (curChar == 45)
							jjCheckNAdd(14);
						break;
					case 14:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 16)
							kind = 16;
						jjCheckNAdd(14);
						break;
					case 16:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjstateSet[jjnewStateCnt++] = 16;
						break;
					case 17:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(17, 18);
						break;
					case 18:
						if (curChar == 46)
							jjstateSet[jjnewStateCnt++] = 19;
						break;
					case 20:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 23)
							kind = 23;
						jjCheckNAddTwoStates(18, 20);
						break;
					case 21:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 13)
							kind = 13;
						jjCheckNAddStates(0, 9);
						break;
					case 22:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 13)
							kind = 13;
						jjCheckNAddTwoStates(22, 23);
						break;
					case 24:
						if (curChar == 45)
							jjCheckNAdd(25);
						break;
					case 25:
						if ((0x3ff000000000000L & l) == 0L)
							break;
						if (kind > 13)
							kind = 13;
						jjCheckNAdd(25);
						break;
					case 26:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(26, 27);
						break;
					case 27:
						if (curChar == 46 && kind > 13)
							kind = 13;
						break;
					case 28:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(28, 29);
						break;
					case 29:
						if (curChar == 46)
							jjCheckNAdd(7);
						break;
					case 30:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(30, 31);
						break;
					case 31:
						if (curChar == 46)
							jjCheckNAdd(11);
						break;
					case 32:
						if ((0x3ff000000000000L & l) != 0L)
							jjCheckNAddTwoStates(32, 33);
						break;
					case 33:
						if (curChar == 46 && kind > 16)
							kind = 16;
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else if (curChar < 128) {
				long l = 1L << (curChar & 077);
				do {
					switch (jjstateSet[--i]) {
					case 0:
						if ((0x7fffffe07fffffeL & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAddStates(10, 12);
						break;
					case 1:
						if ((0x7fffffe87fffffeL & l) != 0L)
							jjAddStates(13, 14);
						break;
					case 4:
						if ((0x7fffffe87fffffeL & l) != 0L)
							jjAddStates(15, 16);
						break;
					case 8:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(17, 18);
						break;
					case 12:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(19, 20);
						break;
					case 16:
						if ((0x7fffffe07fffffeL & l) == 0L)
							break;
						if (kind > 21)
							kind = 21;
						jjCheckNAdd(16);
						break;
					case 17:
						if ((0x7fffffe07fffffeL & l) != 0L)
							jjCheckNAddTwoStates(17, 18);
						break;
					case 19:
					case 20:
						if ((0x7fffffe07fffffeL & l) == 0L)
							break;
						if (kind > 23)
							kind = 23;
						jjCheckNAddTwoStates(18, 20);
						break;
					case 23:
						if ((0x2000000020L & l) != 0L)
							jjAddStates(21, 22);
						break;
					default:
						break;
					}
				} while (i != startsAt);
			} else {
				int hiByte = (curChar >> 8);
				int i1 = hiByte >> 6;
				long l1 = 1L << (hiByte & 077);
				int i2 = (curChar & 0xff) >> 6;
				long l2 = 1L << (curChar & 077);
				do {
					switch (jjstateSet[--i]) {
					default:
						break;
					}
				} while (i != startsAt);
			}
			if (kind != 0x7fffffff) {
				jjmatchedKind = kind;
				jjmatchedPos = curPos;
				kind = 0x7fffffff;
			}
			++curPos;
			if ((i = jjnewStateCnt) == (startsAt = 34 - (jjnewStateCnt = startsAt)))
				return curPos;
			try {
				curChar = input_stream.readChar();
			} catch (java.io.IOException e) {
				return curPos;
			}
		}
	}

	static final int[] jjnextStates = { 22, 23, 26, 27, 28, 29, 30, 31, 32, 33,
			16, 17, 18, 1, 2, 4, 5, 9, 10, 13, 14, 24, 25, };

	/** Token literal values. */
	public static final String[] jjstrLiteralImages = { "", null, null, null,
			null, null, "\53", "\55", "\57", "\52", "\50", "\51", "\54", null,
			null, null, null, null, null, null, null, null, null, null, };

	/** Lexer state names. */
	public static final String[] lexStateNames = { "DEFAULT", };
	static final long[] jjtoToken = { 0xe53fc1L, };
	static final long[] jjtoSkip = { 0x3eL, };
	protected JavaCharStream input_stream;
	private final int[] jjrounds = new int[34];
	private final int[] jjstateSet = new int[68];
	protected char curChar;

	/** Constructor. */
	public DerivedDataParserTokenManager(JavaCharStream stream) {
		if (JavaCharStream.staticFlag)
			throw new Error(
					"ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
		input_stream = stream;
	}

	/** Constructor. */
	public DerivedDataParserTokenManager(JavaCharStream stream, int lexState) {
		this(stream);
		SwitchTo(lexState);
	}

	/** Reinitialise parser. */
	public void ReInit(JavaCharStream stream) {
		jjmatchedPos = jjnewStateCnt = 0;
		curLexState = defaultLexState;
		input_stream = stream;
		ReInitRounds();
	}

	private void ReInitRounds() {
		int i;
		jjround = 0x80000001;
		for (i = 34; i-- > 0;)
			jjrounds[i] = 0x80000000;
	}

	/** Reinitialise parser. */
	public void ReInit(JavaCharStream stream, int lexState) {
		ReInit(stream);
		SwitchTo(lexState);
	}

	/** Switch to specified lex state. */
	public void SwitchTo(int lexState) {
		if (lexState >= 1 || lexState < 0)
			throw new TokenMgrError("Error: Ignoring invalid lexical state : "
					+ lexState + ". State unchanged.",
					TokenMgrError.INVALID_LEXICAL_STATE);
		else
			curLexState = lexState;
	}

	protected Token jjFillToken() {
		final Token t;
		final String curTokenImage;
		final int beginLine;
		final int endLine;
		final int beginColumn;
		final int endColumn;
		String im = jjstrLiteralImages[jjmatchedKind];
		curTokenImage = (im == null) ? input_stream.GetImage() : im;
		beginLine = input_stream.getBeginLine();
		beginColumn = input_stream.getBeginColumn();
		endLine = input_stream.getEndLine();
		endColumn = input_stream.getEndColumn();
		t = Token.newToken(jjmatchedKind, curTokenImage);

		t.beginLine = beginLine;
		t.endLine = endLine;
		t.beginColumn = beginColumn;
		t.endColumn = endColumn;

		return t;
	}

	int curLexState = 0;
	int defaultLexState = 0;
	int jjnewStateCnt;
	int jjround;
	int jjmatchedPos;
	int jjmatchedKind;

	/** Get the next Token. */
	public Token getNextToken() {
		Token matchedToken;
		int curPos = 0;

		EOFLoop: for (;;) {
			try {
				curChar = input_stream.BeginToken();
			} catch (java.io.IOException e) {
				jjmatchedKind = 0;
				matchedToken = jjFillToken();
				return matchedToken;
			}

			try {
				input_stream.backup(0);
				while (curChar <= 32 && (0x100000600L & (1L << curChar)) != 0L)
					curChar = input_stream.BeginToken();
			} catch (java.io.IOException e1) {
				continue EOFLoop;
			}
			jjmatchedKind = 0x7fffffff;
			jjmatchedPos = 0;
			curPos = jjMoveStringLiteralDfa0_0();
			if (jjmatchedKind != 0x7fffffff) {
				if (jjmatchedPos + 1 < curPos)
					input_stream.backup(curPos - jjmatchedPos - 1);
				if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L) {
					matchedToken = jjFillToken();
					return matchedToken;
				} else {
					continue EOFLoop;
				}
			}
			int error_line = input_stream.getEndLine();
			int error_column = input_stream.getEndColumn();
			String error_after = null;
			boolean EOFSeen = false;
			try {
				input_stream.readChar();
				input_stream.backup(1);
			} catch (java.io.IOException e1) {
				EOFSeen = true;
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
				if (curChar == '\n' || curChar == '\r') {
					error_line++;
					error_column = 0;
				} else
					error_column++;
			}
			if (!EOFSeen) {
				input_stream.backup(1);
				error_after = curPos <= 1 ? "" : input_stream.GetImage();
			}
			throw new TokenMgrError(EOFSeen, curLexState, error_line,
					error_column, error_after, curChar,
					TokenMgrError.LEXICAL_ERROR);
		}
	}

	private void jjCheckNAdd(int state) {
		if (jjrounds[state] != jjround) {
			jjstateSet[jjnewStateCnt++] = state;
			jjrounds[state] = jjround;
		}
	}

	private void jjAddStates(int start, int end) {
		do {
			jjstateSet[jjnewStateCnt++] = jjnextStates[start];
		} while (start++ != end);
	}

	private void jjCheckNAddTwoStates(int state1, int state2) {
		jjCheckNAdd(state1);
		jjCheckNAdd(state2);
	}

	private void jjCheckNAddStates(int start, int end) {
		do {
			jjCheckNAdd(jjnextStates[start]);
		} while (start++ != end);
	}

}
