package org.jfw.apt.orm.core.enums;

import org.jfw.apt.orm.core.ColumnHandler;
import org.jfw.apt.orm.core.defaultImpl.*;

public enum DE {
	invalid_de(AbstractColumnHandler.class, null, false,false,false, false, null, null),

	boolean_de(BooleanHandler.class, "CHAR",false,true,true,true,null,null),
	Boolean_de(WBooleanHandler.class, "CHAR", true,true,true,true,null,null),
	
	byte_de(ByteHandler.class, "BYTE",false,true,true,true,null,null),
	Byte_de(WByteHandler.class,"BYTE",true,true,true,true,null,null),
	
	short_de(ShortHandler.class,"SHORT",false,true,true,true,null,null),
	Short_de(ShortHandler.class,"SHORT",true,true,true,true,null,null),
	
	int_de(IntHandler.class, "INTEGER", false,true,true,true,null,null),
	Int_de(WIntHandler.class,"INTEGER",true,true,true,true,null,null),
	
	long_de(LongHandler.class, "LONG", false,true,true,true,null,null),
	Long_de(WLongHandler.class,"LONG",true,true,true,true,null,null),
	
	float_de(FloatHandler.class, "FLOAT",false,true,true,true,null,null),
	Float_de(WFloatHandler.class, "FLOAT",true,true,true,true,null,null),
	
	double_de(DoubleHandler.class, "DOUBLE",false,true,true,true,null,null),
	Double_de(WDoubleHandler.class,"DOUBLE",true,true,true,true,null,null),
	

	string_de(StringHandler.class, "VARCHAR2(255)",false,true,true,true,null,null),
	String_de(StringHandler.class, "VARCHAR2(255)",true,true,true,true,null,null),
	
	id_10(FixLenStringHandler.class,"CHAR(10)",false,true,false,true,null,null),
	id_32(FixLenStringHandler.class,"CHAR(10)",false,true,false,true,null,null),
	
	date_de(FixLenStringHandler.class,"CHAR(8)",false,true,true,true,null,null),
	Date_de(FixLenStringHandler.class,"CHAR(8)",true,true,true,true,null,null),
	
	time_de(FixLenStringHandler.class,"CHAR(6)",false,true,true,true,null,null),
	Time_de(FixLenStringHandler.class,"CHAR(6)",true,true,true,true,null,null),
	
	dateTime_de(FixLenStringHandler.class,"CHAR(14)",false,true,true,true,null,null),
	DateTime_de(FixLenStringHandler.class,"CHAR(14)",true,true,true,true,null,null),

	createDateTime_de(FixLenStringHandler.class,"CHAR(14)",false,true,false,true, "TO_CHAR('YYYYMMDDHH24MISS',SYSDATE)",null),
	modifyDateTime_de(FixLenStringHandler.class,"CHAR(14)",false,true,true,true, "TO_CHAR('YYYYMMDDHH24MISS',SYSDATE)","TO_CHAR('YYYYMMDDHH24MISS',SYSDATE)"),
	
	bigDecmimal_de(BigDecimalHandler.class, "DECIMAL",false,true,true,true,null,null), 
	BigDecmimal_de(BigDecimalHandler.class, "DECIMAL",true,true,true,true,null,null), 

	blob(BlobHandler.class, "BLOB", false,true,true,true,null,null), 
	Blob(BlobHandler.class, "BLOB", true,true,true,true,null,null), 

	streamBlob(StreamBlobHandler.class, "BLOB",false,true,true,false,null,null),
	StreamBlob(StreamBlobHandler.class, "BLOB",false,true,true,false,null,null),
	
	email_de(StringHandler.class,"VARCHAR(50)",false,true,true,true,null,null),
	email_de2(StringHandler.class,"VARCHAR2(50)",false,true,true,true,null,null),
	
	Email_de(StringHandler.class,"VARCHAR(50)",true,true,true,true,null,null),
	Email_de2(StringHandler.class,"VARCHAR2(50)",true,true,true,true,null,null),
	
	mobilePhone_de(FixLenStringHandler.class,"CHAR(11)",false,true,true,true,null,null),
	MobilePhone_de(FixLenStringHandler.class,"CHAR(11)",true,true,true,true,null,null),
	
	md5_de(FixLenStringHandler.class,"CHAR(32)",false,true,true,true,null,null),
	Md5_de(FixLenStringHandler.class,"CHAR(32)",true,true,true,true,null,null)	
	

	;

	private DE(Class<? extends ColumnHandler> handlerClass, String dbType, boolean nullable, boolean insertable,
			boolean renewable, boolean queryable, String fixSqlValueWithInsert, String fixSqlValueWithUpdate) {
		if(null == handlerClass) throw new IllegalArgumentException();
		this.handlerClass = handlerClass;
		this.dbType = dbType;
		this.nullable = nullable;
		this.fixSqlValueWithInsert = fixSqlValueWithInsert;
		this.fixSqlValueWithUpdate = fixSqlValueWithUpdate;
		this.insertable = insertable;
		this.renewable = renewable;
		this.queryable = queryable;
	}

	private Class<? extends ColumnHandler> handlerClass;
	private String dbType;
	private boolean nullable;
	private String fixSqlValueWithInsert;
	private String fixSqlValueWithUpdate;
	private boolean insertable;
	private boolean renewable;
	private boolean queryable;

	public Class<? extends ColumnHandler> getHandlerClass() {
		return handlerClass;
	}

	public String getDbType() {
		return dbType;
	}

	public boolean isNullable() {
		return nullable;
	}

	public String getFixSqlValueWithInsert() {
		return fixSqlValueWithInsert;
	}

	public String getFixSqlValueWithUpdate() {
		return fixSqlValueWithUpdate;
	}

	public boolean isInsertable() {
		return insertable;
	}

	public boolean isRenewable() {
		return renewable;
	}

	public boolean isQueryable() {
		return queryable;
	}
}
