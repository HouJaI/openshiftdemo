package com.maxis.legacyRest;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.maxis.bo.MongoUser;
import com.maxis.bo.user;
import com.maxis.db.SpringMongoConfig;
import com.maxis.db.UserDao;

@RestController
@RequestMapping("/UserService")
public class UserService {
	UserDao userDao = new UserDao();

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public List<user> getUsers() {

		// For Annotation
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		MongoUser user = new MongoUser("mkyong", "password123");

		// save
		mongoOperation.save(user);

		// now user object got the created id.
		System.out.println("1. user : " + user);

		// query to search user
		Query searchUserQuery = new Query(Criteria.where("username").is("mkyong"));

		// find the saved user again.
		MongoUser savedUser = mongoOperation.findOne(searchUserQuery, MongoUser.class);
		System.out.println("2. find - savedUser : " + savedUser.getUsername() + " " + savedUser.getPassword());

		// update password
		mongoOperation.updateFirst(searchUserQuery, Update.update("password", "new password"), MongoUser.class);

		// find the updated user object
		MongoUser updatedUser = mongoOperation.findOne(searchUserQuery, MongoUser.class);

		System.out.println("3. updatedUser : " + updatedUser);

		// List, it should be empty now.
		List<MongoUser> listUser = mongoOperation.findAll(MongoUser.class);
		System.out.println("4. Number of user = " + listUser.size());

		List abc = null;

		try {
			abc = userDao.getAllUsers();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return abc;

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getUserById(@PathVariable int id) {

		String abc = null;

		try {
			abc = userDao.getUserById(id);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return abc;

	}
	
	@RequestMapping(value = "/insertUser", method = RequestMethod.POST)
	public void insertUser (@RequestBody user usr)
	{
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

		// save
		mongoOperation.save(usr);

		((ConfigurableApplicationContext)ctx).close();
	}
	
	
	@RequestMapping(value = "/updateUser", method = RequestMethod.POST)
	public void updateUser (@RequestBody user new_usr)
	{

		
		Query searchUserQuery = new Query(Criteria.where("name").is(new_usr.getName()));


		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
		
		
		// find the saved user again.
		user savedUser = mongoOperation.findOne(searchUserQuery, user.class);
		System.out.println("2. find - savedUser : " + savedUser.getProfession());


		// update password
		mongoOperation.updateFirst(searchUserQuery, Update.update("profession", new_usr.getProfession()), user.class);
		
		
		((ConfigurableApplicationContext)ctx).close();
	}
	

}