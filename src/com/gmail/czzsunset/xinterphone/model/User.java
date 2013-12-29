package com.gmail.czzsunset.xinterphone.model;



public class User {
    public enum Sex { 
        MALE , 
        FEMALE  ;
        public static Sex fromString(String x){
            if( x.equals("M")){
                return MALE;
            }else if(x.equals("F")){
                return FEMALE;
            }
            return null;
        }
        public static String toString(Sex sex){
            switch( sex){
                case MALE:
                    return "M";                    
                case FEMALE:
                    return "F";                 
            }
            return null;
        }

    }
    
    public enum Status {   
        NA,             // 未加入群组时的状态
        NORMAL,         // 正常状态  
        LEAVE,          // 主动离开状态
        OUT_OF_RANGE ;   // 不在范围内
        
        public static Status fromInteger(int x){
            switch(x){
                case 0:
                    return NA;
                case 1:
                    return NORMAL;
                case 2:
                    return LEAVE;
                case 3:
                    return OUT_OF_RANGE;
            }
            return null;
        }
     }
    
    public int id; // global Id used in server 
    public byte localId; // localId used in local area in protocol to save data
    public String uuid;
    public String name;
    public String headImgUrl;
    public Sex sex; 
    public Status status;
    


    public User(){      
    }
    
    public User(String uuid){
        this.uuid = uuid;
    }
    
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
        
    
    public void setUuid(String uuid){
        this.uuid = uuid;
    }
    public String getUuid(){
        return this.uuid;
    }
    
    public void setLocalId(byte localId){
        this.localId = localId;
    }
    
    public byte getLocalId(){
        return this.localId;
    }
        

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    
    public String getHeadImgUrl(){
        return this.headImgUrl;
    }
    
    public void setHeadImgUrl(String headImgUrl){
        this.headImgUrl = headImgUrl;
    }

    public Sex getSex(){
        return this.sex;
    }
    public String getSexInString(){
        if( this.sex == Sex.MALE ){
            return "M";
        }else{
            return "F";
        }
    }
    public void setSex(Sex sex){
        this.sex = sex;
    }
    
    public void setStatus(Status status){
        this.status = status;
    }
    
    public Status getStatus(){
        return this.status;
    }
    
}
