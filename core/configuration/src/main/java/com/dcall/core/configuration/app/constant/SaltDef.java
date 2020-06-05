package com.dcall.core.configuration.app.constant;

//public final class SaltDef {
//    public static final String SALT_USER = "\"!`2~|\\\'\3$%^&*(%_)(#$\''@\\^&*\771'&(&\"$%OZa\"";
//}

public enum SaltDef {
    SALT_USER("\"!`2~|\\\'\3$%^&*(%_)(#$\''@\\^&*\771'&(&\"$%OZa\""),
    SALT_CERT("\"?~3|+\'\66\\#=*/-\\%-w+-49ab~cf@r!&*)__QZ$f\''@\\");

    private String salt;

    SaltDef(final String salt) { this.salt = salt; }

    public String getSalt() { return this.salt; }
}
