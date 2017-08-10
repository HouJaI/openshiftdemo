package com.maxis.bo;



import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "demoUser")
public class user {

	@Id
   private int id;
   public String name;
   public String profession;

   public user(){}
   
   public user(int id, String name, String profession){
      this.id = id;
      this.name = name;
      this.profession = profession;
   }
   
   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }
   public String getName() {
      return name;
   }

      public void setName(String name) {
      this.name = name;
   }
   public String getProfession() {
      return profession;
   }

   public void setProfession(String profession) {
      this.profession = profession;
   }	

   @Override
   public boolean equals(Object object){
      if(object == null){
         return false;
      }else if(!(object instanceof user)){
         return false;
      }else {
         user user = (user)object;
         if(id == user.getId()
            && name.equals(user.getName())
            && profession.equals(user.getProfession())
         ){
            return true;
         }			
      }
      return false;
   }	
}