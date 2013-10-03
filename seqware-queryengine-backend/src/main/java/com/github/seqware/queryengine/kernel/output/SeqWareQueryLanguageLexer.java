// $ANTLR 3.4 /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g 2013-10-03 15:34:11

package com.github.seqware.queryengine.kernel.output;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class SeqWareQueryLanguageLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__27=27;
    public static final int AND=4;
    public static final int BRACKET_CLOSE=5;
    public static final int BRACKET_OPEN=6;
    public static final int COMMENT_EOL=7;
    public static final int COMMENT_INLINE=8;
    public static final int EQUALS=9;
    public static final int FLOAT=10;
    public static final int GT=11;
    public static final int GTEQ=12;
    public static final int ID=13;
    public static final int INT=14;
    public static final int LT=15;
    public static final int LTEQ=16;
    public static final int NAMED_CONSTANT=17;
    public static final int NAMED_FUNCTION=18;
    public static final int NAMED_THREE_PARAM_PREDICATE=19;
    public static final int NAMED_TWO_PARAM_PREDICATE=20;
    public static final int NOT=21;
    public static final int NOTEQUALS=22;
    public static final int NULL=23;
    public static final int OR=24;
    public static final int STRING=25;
    public static final int WHITESPACE=26;

    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public SeqWareQueryLanguageLexer() {} 
    public SeqWareQueryLanguageLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public SeqWareQueryLanguageLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "/home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g"; }

    // $ANTLR start "T__27"
    public final void mT__27() throws RecognitionException {
        try {
            int _type = T__27;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:12:7: ( ',' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:12:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__27"

    // $ANTLR start "NAMED_CONSTANT"
    public final void mNAMED_CONSTANT() throws RecognitionException {
        try {
            int _type = NAMED_CONSTANT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:2: ( 'STRAND_UNKNOWN' | 'NOT_STRANDED' | 'NEGATIVE_STRAND' | 'POSITIVE_STRAND' )
            int alt1=4;
            switch ( input.LA(1) ) {
            case 'S':
                {
                alt1=1;
                }
                break;
            case 'N':
                {
                int LA1_2 = input.LA(2);

                if ( (LA1_2=='O') ) {
                    alt1=2;
                }
                else if ( (LA1_2=='E') ) {
                    alt1=3;
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 1, 2, input);

                    throw nvae;

                }
                }
                break;
            case 'P':
                {
                alt1=4;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 1, 0, input);

                throw nvae;

            }

            switch (alt1) {
                case 1 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:4: 'STRAND_UNKNOWN'
                    {
                    match("STRAND_UNKNOWN"); 



                    }
                    break;
                case 2 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:23: 'NOT_STRANDED'
                    {
                    match("NOT_STRANDED"); 



                    }
                    break;
                case 3 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:40: 'NEGATIVE_STRAND'
                    {
                    match("NEGATIVE_STRAND"); 



                    }
                    break;
                case 4 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:80:60: 'POSITIVE_STRAND'
                    {
                    match("POSITIVE_STRAND"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_CONSTANT"

    // $ANTLR start "NAMED_FUNCTION"
    public final void mNAMED_FUNCTION() throws RecognitionException {
        try {
            int _type = NAMED_FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:84:2: ( 'tagValue' | 'fsTagValue' )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='t') ) {
                alt2=1;
            }
            else if ( (LA2_0=='f') ) {
                alt2=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }
            switch (alt2) {
                case 1 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:84:4: 'tagValue'
                    {
                    match("tagValue"); 



                    }
                    break;
                case 2 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:84:17: 'fsTagValue'
                    {
                    match("fsTagValue"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_FUNCTION"

    // $ANTLR start "NAMED_TWO_PARAM_PREDICATE"
    public final void mNAMED_TWO_PARAM_PREDICATE() throws RecognitionException {
        try {
            int _type = NAMED_TWO_PARAM_PREDICATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:88:2: ( 'tagOccurrence' | 'fsTagOccurrence' | 'tagHierarchicalOccurrence' )
            int alt3=3;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='t') ) {
                int LA3_1 = input.LA(2);

                if ( (LA3_1=='a') ) {
                    int LA3_3 = input.LA(3);

                    if ( (LA3_3=='g') ) {
                        int LA3_4 = input.LA(4);

                        if ( (LA3_4=='O') ) {
                            alt3=1;
                        }
                        else if ( (LA3_4=='H') ) {
                            alt3=3;
                        }
                        else {
                            NoViableAltException nvae =
                                new NoViableAltException("", 3, 4, input);

                            throw nvae;

                        }
                    }
                    else {
                        NoViableAltException nvae =
                            new NoViableAltException("", 3, 3, input);

                        throw nvae;

                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 3, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA3_0=='f') ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:88:4: 'tagOccurrence'
                    {
                    match("tagOccurrence"); 



                    }
                    break;
                case 2 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:88:22: 'fsTagOccurrence'
                    {
                    match("fsTagOccurrence"); 



                    }
                    break;
                case 3 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:88:42: 'tagHierarchicalOccurrence'
                    {
                    match("tagHierarchicalOccurrence"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_TWO_PARAM_PREDICATE"

    // $ANTLR start "NAMED_THREE_PARAM_PREDICATE"
    public final void mNAMED_THREE_PARAM_PREDICATE() throws RecognitionException {
        try {
            int _type = NAMED_THREE_PARAM_PREDICATE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:92:2: ( 'tagValuePresence' | 'fsTagValuePresence' )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0=='t') ) {
                alt4=1;
            }
            else if ( (LA4_0=='f') ) {
                alt4=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:92:4: 'tagValuePresence'
                    {
                    match("tagValuePresence"); 



                    }
                    break;
                case 2 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:92:25: 'fsTagValuePresence'
                    {
                    match("fsTagValuePresence"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NAMED_THREE_PARAM_PREDICATE"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:9: ( ( 'null' | 'NULL' ) )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:17: ( 'null' | 'NULL' )
            {
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:17: ( 'null' | 'NULL' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='n') ) {
                alt5=1;
            }
            else if ( (LA5_0=='N') ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:18: 'null'
                    {
                    match("null"); 



                    }
                    break;
                case 2 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:96:27: 'NULL'
                    {
                    match("NULL"); 



                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:100:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )* )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:100:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:100:28: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( ((LA6_0 >= '0' && LA6_0 <= '9')||(LA6_0 >= 'A' && LA6_0 <= 'Z')||LA6_0=='_'||(LA6_0 >= 'a' && LA6_0 <= 'z')) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "INT"
    public final void mINT() throws RecognitionException {
        try {
            int _type = INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:104:2: ( ( '0' .. '9' )+ )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:104:4: ( '0' .. '9' )+
            {
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:104:4: ( '0' .. '9' )+
            int cnt7=0;
            loop7:
            do {
                int alt7=2;
                int LA7_0 = input.LA(1);

                if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
                    alt7=1;
                }


                switch (alt7) {
            	case 1 :
            	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt7 >= 1 ) break loop7;
                        EarlyExitException eee =
                            new EarlyExitException(7, input);
                        throw eee;
                }
                cnt7++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "INT"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:108:6: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* | '.' ( '0' .. '9' )+ )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0 >= '0' && LA11_0 <= '9')) ) {
                alt11=1;
            }
            else if ( (LA11_0=='.') ) {
                alt11=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;

            }
            switch (alt11) {
                case 1 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:108:8: ( '0' .. '9' )+ '.' ( '0' .. '9' )*
                    {
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:108:8: ( '0' .. '9' )+
                    int cnt8=0;
                    loop8:
                    do {
                        int alt8=2;
                        int LA8_0 = input.LA(1);

                        if ( ((LA8_0 >= '0' && LA8_0 <= '9')) ) {
                            alt8=1;
                        }


                        switch (alt8) {
                    	case 1 :
                    	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt8 >= 1 ) break loop8;
                                EarlyExitException eee =
                                    new EarlyExitException(8, input);
                                throw eee;
                        }
                        cnt8++;
                    } while (true);


                    match('.'); 

                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:108:24: ( '0' .. '9' )*
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0 >= '0' && LA9_0 <= '9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:109:8: '.' ( '0' .. '9' )+
                    {
                    match('.'); 

                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:109:12: ( '0' .. '9' )+
                    int cnt10=0;
                    loop10:
                    do {
                        int alt10=2;
                        int LA10_0 = input.LA(1);

                        if ( ((LA10_0 >= '0' && LA10_0 <= '9')) ) {
                            alt10=1;
                        }


                        switch (alt10) {
                    	case 1 :
                    	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt10 >= 1 ) break loop10;
                                EarlyExitException eee =
                                    new EarlyExitException(10, input);
                                throw eee;
                        }
                        cnt10++;
                    } while (true);


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "COMMENT_EOL"
    public final void mCOMMENT_EOL() throws RecognitionException {
        try {
            int _type = COMMENT_EOL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:2: ( '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:4: '//' (~ ( '\\n' | '\\r' ) )* ( '\\r' )? '\\n'
            {
            match("//"); 



            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:9: (~ ( '\\n' | '\\r' ) )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( ((LA12_0 >= '\u0000' && LA12_0 <= '\t')||(LA12_0 >= '\u000B' && LA12_0 <= '\f')||(LA12_0 >= '\u000E' && LA12_0 <= '\uFFFF')) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\f')||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:23: ( '\\r' )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0=='\r') ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:113:23: '\\r'
                    {
                    match('\r'); 

                    }
                    break;

            }


            match('\n'); 

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT_EOL"

    // $ANTLR start "COMMENT_INLINE"
    public final void mCOMMENT_INLINE() throws RecognitionException {
        try {
            int _type = COMMENT_INLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:117:2: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:117:4: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:117:9: ( options {greedy=false; } : . )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0=='*') ) {
                    int LA14_1 = input.LA(2);

                    if ( (LA14_1=='/') ) {
                        alt14=2;
                    }
                    else if ( ((LA14_1 >= '\u0000' && LA14_1 <= '.')||(LA14_1 >= '0' && LA14_1 <= '\uFFFF')) ) {
                        alt14=1;
                    }


                }
                else if ( ((LA14_0 >= '\u0000' && LA14_0 <= ')')||(LA14_0 >= '+' && LA14_0 <= '\uFFFF')) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:117:37: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            match("*/"); 



            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT_INLINE"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:121:5: ( '\"' (~ ( '\\\\' | '\"' ) )* '\"' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:121:8: '\"' (~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 

            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:121:12: (~ ( '\\\\' | '\"' ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( ((LA15_0 >= '\u0000' && LA15_0 <= '!')||(LA15_0 >= '#' && LA15_0 <= '[')||(LA15_0 >= ']' && LA15_0 <= '\uFFFF')) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);


            match('\"'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:125:2: ( '!' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:125:4: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:129:2: ( '||' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:129:4: '||'
            {
            match("||"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:133:2: ( '&&' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:133:4: '&&'
            {
            match("&&"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "EQUALS"
    public final void mEQUALS() throws RecognitionException {
        try {
            int _type = EQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:137:2: ( '==' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:137:4: '=='
            {
            match("=="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQUALS"

    // $ANTLR start "NOTEQUALS"
    public final void mNOTEQUALS() throws RecognitionException {
        try {
            int _type = NOTEQUALS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:141:2: ( '!=' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:141:4: '!='
            {
            match("!="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOTEQUALS"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:145:2: ( '<' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:145:4: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "LTEQ"
    public final void mLTEQ() throws RecognitionException {
        try {
            int _type = LTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:149:2: ( '<=' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:149:4: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LTEQ"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:153:2: ( '>' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:153:4: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "GTEQ"
    public final void mGTEQ() throws RecognitionException {
        try {
            int _type = GTEQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:157:2: ( '>=' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:157:4: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GTEQ"

    // $ANTLR start "BRACKET_OPEN"
    public final void mBRACKET_OPEN() throws RecognitionException {
        try {
            int _type = BRACKET_OPEN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:161:2: ( '(' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:161:4: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BRACKET_OPEN"

    // $ANTLR start "BRACKET_CLOSE"
    public final void mBRACKET_CLOSE() throws RecognitionException {
        try {
            int _type = BRACKET_CLOSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:165:2: ( ')' )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:165:4: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BRACKET_CLOSE"

    // $ANTLR start "WHITESPACE"
    public final void mWHITESPACE() throws RecognitionException {
        try {
            int _type = WHITESPACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:169:2: ( ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+ )
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:169:4: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            {
            // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:169:4: ( '\\t' | ' ' | '\\r' | '\\n' | '\\u000C' )+
            int cnt16=0;
            loop16:
            do {
                int alt16=2;
                int LA16_0 = input.LA(1);

                if ( ((LA16_0 >= '\t' && LA16_0 <= '\n')||(LA16_0 >= '\f' && LA16_0 <= '\r')||LA16_0==' ') ) {
                    alt16=1;
                }


                switch (alt16) {
            	case 1 :
            	    // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:
            	    {
            	    if ( (input.LA(1) >= '\t' && input.LA(1) <= '\n')||(input.LA(1) >= '\f' && input.LA(1) <= '\r')||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt16 >= 1 ) break loop16;
                        EarlyExitException eee =
                            new EarlyExitException(16, input);
                        throw eee;
                }
                cnt16++;
            } while (true);


             _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHITESPACE"

    public void mTokens() throws RecognitionException {
        // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:8: ( T__27 | NAMED_CONSTANT | NAMED_FUNCTION | NAMED_TWO_PARAM_PREDICATE | NAMED_THREE_PARAM_PREDICATE | NULL | ID | INT | FLOAT | COMMENT_EOL | COMMENT_INLINE | STRING | NOT | OR | AND | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | BRACKET_OPEN | BRACKET_CLOSE | WHITESPACE )
        int alt17=24;
        alt17 = dfa17.predict(input);
        switch (alt17) {
            case 1 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:10: T__27
                {
                mT__27(); 


                }
                break;
            case 2 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:16: NAMED_CONSTANT
                {
                mNAMED_CONSTANT(); 


                }
                break;
            case 3 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:31: NAMED_FUNCTION
                {
                mNAMED_FUNCTION(); 


                }
                break;
            case 4 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:46: NAMED_TWO_PARAM_PREDICATE
                {
                mNAMED_TWO_PARAM_PREDICATE(); 


                }
                break;
            case 5 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:72: NAMED_THREE_PARAM_PREDICATE
                {
                mNAMED_THREE_PARAM_PREDICATE(); 


                }
                break;
            case 6 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:100: NULL
                {
                mNULL(); 


                }
                break;
            case 7 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:105: ID
                {
                mID(); 


                }
                break;
            case 8 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:108: INT
                {
                mINT(); 


                }
                break;
            case 9 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:112: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 10 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:118: COMMENT_EOL
                {
                mCOMMENT_EOL(); 


                }
                break;
            case 11 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:130: COMMENT_INLINE
                {
                mCOMMENT_INLINE(); 


                }
                break;
            case 12 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:145: STRING
                {
                mSTRING(); 


                }
                break;
            case 13 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:152: NOT
                {
                mNOT(); 


                }
                break;
            case 14 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:156: OR
                {
                mOR(); 


                }
                break;
            case 15 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:159: AND
                {
                mAND(); 


                }
                break;
            case 16 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:163: EQUALS
                {
                mEQUALS(); 


                }
                break;
            case 17 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:170: NOTEQUALS
                {
                mNOTEQUALS(); 


                }
                break;
            case 18 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:180: LT
                {
                mLT(); 


                }
                break;
            case 19 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:183: LTEQ
                {
                mLTEQ(); 


                }
                break;
            case 20 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:188: GT
                {
                mGT(); 


                }
                break;
            case 21 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:191: GTEQ
                {
                mGTEQ(); 


                }
                break;
            case 22 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:196: BRACKET_OPEN
                {
                mBRACKET_OPEN(); 


                }
                break;
            case 23 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:209: BRACKET_CLOSE
                {
                mBRACKET_CLOSE(); 


                }
                break;
            case 24 :
                // /home/dyuen/queryengine/seqware-queryengine-backend/src/main/java/com/github/seqware/queryengine/kernel/SeqWareQueryLanguage.g:1:223: WHITESPACE
                {
                mWHITESPACE(); 


                }
                break;

        }

    }


    protected DFA17 dfa17 = new DFA17(this);
    static final String DFA17_eotS =
        "\2\uffff\6\10\1\uffff\1\36\3\uffff\1\42\3\uffff\1\44\1\46\3\uffff"+
        "\10\10\11\uffff\13\10\1\74\5\10\1\74\3\10\1\uffff\33\10\1\142\11"+
        "\10\1\uffff\13\10\1\142\13\10\1\u0083\10\10\1\uffff\3\10\1\u008f"+
        "\3\10\1\u0083\3\10\1\uffff\3\10\2\u0083\3\10\1\u008f\1\u009c\2\10"+
        "\1\uffff\3\10\1\u009c\6\10\1\u008f";
    static final String DFA17_eofS =
        "\u00a8\uffff";
    static final String DFA17_minS =
        "\1\11\1\uffff\1\124\1\105\1\117\1\141\1\163\1\165\1\uffff\1\56\1"+
        "\uffff\1\52\1\uffff\1\75\3\uffff\2\75\3\uffff\1\122\1\124\1\107"+
        "\1\114\1\123\1\147\1\124\1\154\11\uffff\1\101\1\137\1\101\1\114"+
        "\1\111\1\110\1\141\1\154\1\116\1\123\1\124\1\60\1\124\1\141\1\143"+
        "\1\151\1\147\1\60\1\104\1\124\1\111\1\uffff\1\111\1\154\1\143\1"+
        "\145\1\117\1\137\1\122\2\126\2\165\1\162\1\141\1\143\1\125\1\101"+
        "\2\105\1\145\1\162\1\141\1\154\1\143\2\116\2\137\1\60\2\162\2\165"+
        "\1\113\1\104\2\123\1\162\1\uffff\1\145\1\143\1\145\1\162\1\116\1"+
        "\105\2\124\1\145\1\156\1\150\1\60\1\162\1\117\1\104\2\122\1\163"+
        "\1\143\1\151\1\162\1\145\1\127\1\60\2\101\2\145\1\143\1\145\1\156"+
        "\1\116\1\uffff\2\116\1\156\1\60\1\141\1\163\1\143\1\60\2\104\1\143"+
        "\1\uffff\1\154\2\145\2\60\1\145\1\117\1\156\2\60\2\143\1\uffff\1"+
        "\143\1\145\1\165\1\60\2\162\1\145\1\156\1\143\1\145\1\60";
    static final String DFA17_maxS =
        "\1\174\1\uffff\1\124\1\125\1\117\1\141\1\163\1\165\1\uffff\1\71"+
        "\1\uffff\1\57\1\uffff\1\75\3\uffff\2\75\3\uffff\1\122\1\124\1\107"+
        "\1\114\1\123\1\147\1\124\1\154\11\uffff\1\101\1\137\1\101\1\114"+
        "\1\111\1\126\1\141\1\154\1\116\1\123\1\124\1\172\1\124\1\141\1\143"+
        "\1\151\1\147\1\172\1\104\1\124\1\111\1\uffff\1\111\1\154\1\143\1"+
        "\145\1\126\1\137\1\122\2\126\2\165\1\162\1\141\1\143\1\125\1\101"+
        "\2\105\1\145\1\162\1\141\1\154\1\143\2\116\2\137\1\172\2\162\2\165"+
        "\1\113\1\104\2\123\1\162\1\uffff\1\145\1\143\1\145\1\162\1\116\1"+
        "\105\2\124\1\145\1\156\1\150\1\172\1\162\1\117\1\104\2\122\1\163"+
        "\1\143\1\151\1\162\1\145\1\127\1\172\2\101\2\145\1\143\1\145\1\156"+
        "\1\116\1\uffff\2\116\1\156\1\172\1\141\1\163\1\143\1\172\2\104\1"+
        "\143\1\uffff\1\154\2\145\2\172\1\145\1\117\1\156\2\172\2\143\1\uffff"+
        "\1\143\1\145\1\165\1\172\2\162\1\145\1\156\1\143\1\145\1\172";
    static final String DFA17_acceptS =
        "\1\uffff\1\1\6\uffff\1\7\1\uffff\1\11\1\uffff\1\14\1\uffff\1\16"+
        "\1\17\1\20\2\uffff\1\26\1\27\1\30\10\uffff\1\10\1\12\1\13\1\21\1"+
        "\15\1\23\1\22\1\25\1\24\25\uffff\1\6\45\uffff\1\3\40\uffff\1\2\13"+
        "\uffff\1\4\14\uffff\1\5\13\uffff";
    static final String DFA17_specialS =
        "\u00a8\uffff}>";
    static final String[] DFA17_transitionS = {
            "\2\25\1\uffff\2\25\22\uffff\1\25\1\15\1\14\3\uffff\1\17\1\uffff"+
            "\1\23\1\24\2\uffff\1\1\1\uffff\1\12\1\13\12\11\2\uffff\1\21"+
            "\1\20\1\22\2\uffff\15\10\1\3\1\10\1\4\2\10\1\2\7\10\4\uffff"+
            "\1\10\1\uffff\5\10\1\6\7\10\1\7\5\10\1\5\6\10\1\uffff\1\16",
            "",
            "\1\26",
            "\1\30\11\uffff\1\27\5\uffff\1\31",
            "\1\32",
            "\1\33",
            "\1\34",
            "\1\35",
            "",
            "\1\12\1\uffff\12\11",
            "",
            "\1\40\4\uffff\1\37",
            "",
            "\1\41",
            "",
            "",
            "",
            "\1\43",
            "\1\45",
            "",
            "",
            "",
            "\1\47",
            "\1\50",
            "\1\51",
            "\1\52",
            "\1\53",
            "\1\54",
            "\1\55",
            "\1\56",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\57",
            "\1\60",
            "\1\61",
            "\1\62",
            "\1\63",
            "\1\66\6\uffff\1\65\6\uffff\1\64",
            "\1\67",
            "\1\70",
            "\1\71",
            "\1\72",
            "\1\73",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\75",
            "\1\76",
            "\1\77",
            "\1\100",
            "\1\101",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\102",
            "\1\103",
            "\1\104",
            "",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\112\6\uffff\1\111",
            "\1\113",
            "\1\114",
            "\1\115",
            "\1\116",
            "\1\117",
            "\1\120",
            "\1\121",
            "\1\122",
            "\1\123",
            "\1\124",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\134",
            "\1\135",
            "\1\136",
            "\1\137",
            "\1\140",
            "\12\10\7\uffff\17\10\1\141\12\10\4\uffff\1\10\1\uffff\32\10",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "\1\151",
            "\1\152",
            "\1\153",
            "",
            "\1\154",
            "\1\155",
            "\1\156",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\162",
            "\1\163",
            "\1\164",
            "\1\165",
            "\1\166",
            "\12\10\7\uffff\17\10\1\167\12\10\4\uffff\1\10\1\uffff\32\10",
            "\1\170",
            "\1\171",
            "\1\172",
            "\1\173",
            "\1\174",
            "\1\175",
            "\1\176",
            "\1\177",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\u0084",
            "\1\u0085",
            "\1\u0086",
            "\1\u0087",
            "\1\u0088",
            "\1\u0089",
            "\1\u008a",
            "\1\u008b",
            "",
            "\1\u008c",
            "\1\u008d",
            "\1\u008e",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\u0090",
            "\1\u0091",
            "\1\u0092",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\u0093",
            "\1\u0094",
            "\1\u0095",
            "",
            "\1\u0096",
            "\1\u0097",
            "\1\u0098",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\u0099",
            "\1\u009a",
            "\1\u009b",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\u009d",
            "\1\u009e",
            "",
            "\1\u009f",
            "\1\u00a0",
            "\1\u00a1",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10",
            "\1\u00a2",
            "\1\u00a3",
            "\1\u00a4",
            "\1\u00a5",
            "\1\u00a6",
            "\1\u00a7",
            "\12\10\7\uffff\32\10\4\uffff\1\10\1\uffff\32\10"
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        public String getDescription() {
            return "1:1: Tokens : ( T__27 | NAMED_CONSTANT | NAMED_FUNCTION | NAMED_TWO_PARAM_PREDICATE | NAMED_THREE_PARAM_PREDICATE | NULL | ID | INT | FLOAT | COMMENT_EOL | COMMENT_INLINE | STRING | NOT | OR | AND | EQUALS | NOTEQUALS | LT | LTEQ | GT | GTEQ | BRACKET_OPEN | BRACKET_CLOSE | WHITESPACE );";
        }
    }
 

}