package info.futureme.abs.example.conf;

public enum CIdType {
   GETUI(0),
   BAIDU(1);
   
   private Integer type;
   CIdType(int type){
	   this.type=type;
   }
   public Integer getType() {
		return type;
   }
   public CIdType get(int type){
	   for(CIdType c:CIdType.values()){
		   if(c.getType()==type){
			   return c;
		   }
	   }
	   return null;
   }
   
}
