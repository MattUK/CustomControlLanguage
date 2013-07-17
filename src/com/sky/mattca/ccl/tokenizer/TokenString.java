package com.sky.mattca.ccl.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: 06mcarter
 * Date: 08/11/12
 * Time: 10:44
 */
public class TokenString {

    private List<Token> tokenList;

    public int line = 0;

    public TokenString() {
        tokenList = new ArrayList<>();
    }

    public TokenString createCopy() {
        TokenString newString = new TokenString();
        newString.tokenList = new ArrayList<>(this.tokenList);
        newString.line = this.line;
        return newString;
    }

    public TokenString append(TokenString string) {
        tokenList.addAll(string.toList());
        return this;
    }

    public TokenString addToBeginning(TokenString string) {
        tokenList.addAll(0, string.toList());
        return this;
    }

    public TokenString append(Token token) {
        tokenList.add(token);
        return this;
    }

    public TokenString addToBeginning(Token token) {
        tokenList.add(0, token);
        return this;
    }

    public TokenString trimLeft() {
        if (empty()) return this;

        while (tokenList.size() > 0 && tokenList.get(0).type == Token.TokenType.WHITESPACE) {
            tokenList.remove(0);
        }
        return this;
    }

    public TokenString trimRight() {
        if (empty()) return this;

        while (tokenList.get(tokenList.size() - 1).type == Token.TokenType.WHITESPACE) {
            tokenList.remove(tokenList.size() - 1);
        }
        return this;
    }

    public TokenString trim() {
        if (empty()) return this;

        trimLeft();
        trimRight();
        return this;
    }

    public TokenString removeWhitespace() {
        boolean finished = false;

        if (empty()) {
            return this;
        }

        while (!finished) {
            for (int i = 0; i < tokenList.size(); i++) {
                if (tokenList.get(i).type == Token.TokenType.WHITESPACE) {
                    tokenList.remove(i);
                    continue;
                } else {
                    if (i == tokenList.size() - 1) {
                        finished = true;
                    }
                }
            }
        }

        return this;
    }

    public boolean match(Token.TokenType... args) {
        if (empty()) {
            return false;
        }
        try {
            for (int i = 0; i < args.length; i++) {
                if (tokenList.get(i).type != args[i]) {
                    return false;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    public boolean contentMatch(String... args) {
        for (int i = 0; i < args.length; i++) {
            if (tokenList.get(i).contents.toUpperCase().equals(args[i].toUpperCase()) == false) {
                return false;
            }
        }
        return true;
    }

    public Token consume() {
        return (tokenList.size() > 0) ? tokenList.remove(0) : null;
    }

    public Token peek() {
        return (tokenList.size() > 0) ? tokenList.get(0) : null;
    }

    public Token get(int index) {
        return tokenList.get(index);
    }

    public Token remove(int index) {
        return tokenList.remove(index);
    }

    public Token getFirstExcludingWhitespace() {
        for (int i = 0; i < tokenList.size(); i++) {
            if (tokenList.get(i).type != Token.TokenType.WHITESPACE) {
                return tokenList.get(i);
            }
        }
        return null;
    }

    public boolean empty() {
        return !(tokenList.size() > 0);
    }

    public int size() {
        return tokenList.size();
    }

    public List<Token> toList() {
        return tokenList;
    }


    @Override
    public String toString() {
        return "TokenString{" +
                "tokenList=" + tokenList +
                ", line=" + line +
                '}';
    }
}
