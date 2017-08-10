package com.maxis.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.maxis.bo.user;

public class UserDao {
	public List<user> getAllUsers() throws Exception {

		List<user> userList = null;
		try {
			File file = new File("C:\\restTest\\Users.dat");
			File fileXML = new File("C:\\restTest\\Users.xml");
			if (!file.exists()) {
				user user = new user(1, "Mahesh", "Teacher");

				JAXBContext context = JAXBContext.newInstance(user.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

				// Write to File
				m.marshal(user, fileXML);
				
				userList = new ArrayList<user>();
				userList.add(user);
				user = new user(2, "Kean Ho", "Teacher");
				userList.add(user);
				saveUserList(userList);

				// saveUserList(user);
			} else {
				FileInputStream fis = new FileInputStream(file);
				ObjectInputStream ois = new ObjectInputStream(fis);
				userList = (List<user>) ois.readObject();
				ois.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return userList;
	}

	private void saveUserList(List<user> userList) {
		try {
			File file = new File("C:\\restTest\\Users.dat");
			FileOutputStream fos;
			fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(userList);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getUserById(int id) throws Exception{
	      List<user> users = getAllUsers();
	      
	      java.io.StringWriter sw = new StringWriter();
	      JAXBContext jc = JAXBContext.newInstance(user.class);
	      Marshaller marshaller = jc.createMarshaller();
	      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	      
	      
	      for(user user: users){
	         if(user.getId() == id){
	        	 marshaller.marshal(user, sw);
	        	 	        	 
	        	 XmlMapper xmlMapper = new XmlMapper();
	        	 JsonNode node = xmlMapper.readTree(sw.toString().getBytes());        	 
	        	 ObjectMapper jsonMapper = new ObjectMapper();
	        	 
	            return jsonMapper.writeValueAsString(node);
	         }
	      }
	      return null;
	   }
	
}