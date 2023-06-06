package io;

public class MessageProtocol {

    // Message Types
    public final static String TYPE = "TYPE";
    public final static String  JOIN = "JOIN";
    public final static String  REJECT = "REJECT";
    public final static String INVALID = "INVALID";
    public final static String  STATE = "STATE";
    public final static String  CANVAS = "CANVAS";
    public final static String REFRESH = "REFRESH";
    public final static String USER_LIST = "USER_LIST";
    public final static String DISCONNECT = "DISCONNECT";
    public final static String SHUT_DOWN = "SHUT_DOWN";

    // Drawing Types
    public final static String RECTANGLE = "RECTANGLE";
    public final static String CIRCLE = "CIRCLE";
    public final static String LINE = "LINE";
    public final static String OVAL = "OVAL";
    public final static String TEXT_BOX = "TEXT_BOX";
    public final static String ERASER = "ERASER";

    // Drawing Information
    public final static String COLOR = "COLOR";
    public final static String TEXT = "TEXT";
    public final static String POS_X = "X";
    public final static String POS_Y = "Y";
    public final static String POS_X1 = "X1";
    public final static String POS_Y1 = "Y1";
    public final static String POS_X2 = "X2";
    public final static String POS_Y2 = "Y2";
    public final static String WIDTH = "WIDTH";
    public final static String HEIGHT = "HEIGHT";
}